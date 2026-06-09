/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.server.life;

import lombok.Getter;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.InventoryType;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.NpcId;
import org.gms.dao.entity.PlayernpcsDO;
import org.gms.dao.entity.PlayernpcsEquipDO;
import org.gms.manager.ServerManager;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.gms.service.NpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.life.positioner.PlayerNPCPodium;
import org.gms.server.life.positioner.PlayerNPCPositioner;
import org.gms.server.maps.AbstractMapObject;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 玩家NPC
 * 代表由玩家角色创建的NPC，用于名人堂、排行榜等展示功能
 * 继承AbstractMapObject，包含外观、装备、排名等完整信息
 * 使用静态原子变量管理全局排名序号，确保多线程安全
 *
 * @author XoticStory
 * @author Ronan
 */
public class PlayerNPC extends AbstractMapObject {
    private static final Logger log = LoggerFactory.getLogger(PlayerNPC.class);
    /** 可用脚本ID缓存（分支 -> 可用ID列表） */
    private static final Map<Byte, List<Integer>> availablePlayerNpcScriptIds = new HashMap<>();
    /** 全局排名序号计数器 */
    private static final AtomicInteger runningOverallRank = new AtomicInteger();
    /** 各世界排名序号计数器 */
    private static final List<AtomicInteger> runningWorldRank = new ArrayList<>();
    /** 各世界各职业排名序号计数器（世界ID, 职业ID） -> 计数器 */
    private static final Map<Pair<Integer, Integer>, AtomicInteger> runningWorldJobRank = new HashMap<>();
    /** NPC服务 */
    private static final NpcService npcService = ServerManager.getApplicationContext().getBean(NpcService.class);

    /** 装备映射（位置 -> 装备ID） */
    @Getter
    private Map<Short, Integer> equips = new HashMap<>();
    /** 脚本ID */
    @Getter
    private int scriptId;
    /** 脸型ID */
    @Getter
    private int face;
    /** 发型ID */
    @Getter
    private int hair;
    /** 性别 */
    @Getter
    private int gender;
    /** 职业 */
    @Getter
    private int job;
    /** 肤色 */
    @Getter
    private byte skin;
    /** 玩家名称 */
    @Getter
    private String name = "";
    /** 朝向 */
    @Getter
    private int dir;
    /** 站立平台ID */
    @Getter
    private int FH;
    /** 左边界 */
    @Getter
    private int RX0;
    /** 右边界 */
    @Getter
    private int RX1;
    /** Y坐标 */
    @Getter
    private int CY;
    /** 世界排名 */
    private int worldRank;
    /** 全局排名 */
    private int overallRank;
    /** 世界职业排名 */
    private int worldJobRank;
    /** 全局职业排名 */
    private int overallJobRank;

    /**
     * 构造玩家NPC（内存中创建）
     *
     * @param name 玩家名称
     * @param scriptId 脚本ID
     * @param face 脸型ID
     * @param hair 发型ID
     * @param gender 性别
     * @param skin 肤色
     * @param equips 装备映射
     * @param dir 朝向
     * @param FH 站立平台ID
     * @param RX0 左边界
     * @param RX1 右边界
     * @param CX X坐标
     * @param CY Y坐标
     * @param oid 对象ID
     */
    public PlayerNPC(String name, int scriptId, int face, int hair, int gender, byte skin, Map<Short, Integer> equips, int dir, int FH, int RX0, int RX1, int CX, int CY, int oid) {
        this.equips = equips;
        this.scriptId = scriptId;
        this.face = face;
        this.hair = hair;
        this.gender = gender;
        this.skin = skin;
        this.name = name;
        this.dir = dir;
        this.FH = FH;
        this.RX0 = RX0;
        this.RX1 = RX1;
        this.CY = CY;
        this.job = 7777;    // supposed to be developer

        setPosition(new Point(CX, CY));
        setObjectId(oid);
    }

    /**
     * 构造玩家NPC（从数据库加载）
     *
     * @param npcDO 玩家NPC数据库对象
     * @param equipDOList 装备数据库对象列表
     */
    public PlayerNPC(PlayernpcsDO npcDO, List<PlayernpcsEquipDO> equipDOList) {
        CY = Optional.ofNullable(npcDO.getCy()).orElse(0);
        name = Optional.ofNullable(npcDO.getName()).orElse("");
        hair = Optional.ofNullable(npcDO.getHair()).orElse(0);
        face = Optional.ofNullable(npcDO.getFace()).orElse(0);
        skin = Optional.ofNullable(npcDO.getSkin()).map(Integer::byteValue).orElse((byte) 0);
        gender = Optional.ofNullable(npcDO.getGender()).orElse(0);
        dir = Optional.ofNullable(npcDO.getDir()).orElse(0);
        FH = Optional.ofNullable(npcDO.getFh()).orElse(0);
        RX0 = Optional.ofNullable(npcDO.getRx0()).orElse(0);
        RX1 = Optional.ofNullable(npcDO.getRx1()).orElse(0);
        scriptId = Optional.ofNullable(npcDO.getScriptid()).orElse(0);
        worldRank = Optional.ofNullable(npcDO.getWorldrank()).orElse(0);
        overallRank = Optional.ofNullable(npcDO.getOverallrank()).orElse(0);
        worldJobRank = Optional.ofNullable(npcDO.getWorldjobrank()).orElse(0);
        overallJobRank = GameConstants.getOverallJobRankByScriptId(scriptId);
        job = Optional.ofNullable(npcDO.getJob()).orElse(0);
        setPosition(new Point(Optional.ofNullable(npcDO.getX()).orElse(0), CY));
        int id = Optional.ofNullable(npcDO.getId()).orElse(0);
        setObjectId(id);
        equipDOList.forEach(equipDO -> equips.put(Optional.ofNullable(equipDO.getEquippos()).orElse((short) 0), equipDO.getEquipid()));
    }

    /**
     * 从数据库加载所有玩家NPC的排名数据，初始化排名计数器
     * 为每个世界创建排名计数器，统计当前最大排名值，方便下次分配新序号
     *
     * @param worlds 世界数量
     */
    public static void loadRunningRankData(int worlds) {
        List<PlayernpcsDO> playernpcsDOList = npcService.getPlayerNpcDOs(new PlayernpcsDO());
        runningOverallRank.set(playernpcsDOList.size() + 1);

        for (int i = 0; i < worlds; i++) {
            runningWorldRank.add(new AtomicInteger(1));
        }

        playernpcsDOList.forEach(playernpcsDO -> {
            if (playernpcsDO.getWorldrank() > runningWorldRank.get(playernpcsDO.getWorld()).get()) {
                runningWorldRank.get(playernpcsDO.getWorld()).set(playernpcsDO.getWorldrank());
            }
            Pair<Integer, Integer> worldJobPair = new Pair<>(playernpcsDO.getWorld(), playernpcsDO.getJob());
            AtomicInteger worldJobRank = runningWorldJobRank.get(worldJobPair);
            if (worldJobRank == null) {
                worldJobRank = new AtomicInteger(1);
            }
            if (playernpcsDO.getWorldjobrank() > worldJobRank.get()) {
                runningWorldJobRank.put(worldJobPair, worldJobRank);
            }
        });
    }

    /**
     * 获取世界排名
     *
     * @return 世界排名
     */
    public int getWorldRank() {
        return worldRank;
    }

    /**
     * 获取全局排名
     *
     * @return 全局排名
     */
    public int getOverallRank() {
        return overallRank;
    }

    /**
     * 获取世界职业排名
     *
     * @return 世界职业排名
     */
    public int getWorldJobRank() {
        return worldJobRank;
    }

    /**
     * 获取全局职业排名
     *
     * @return 全局职业排名
     */
    public int getOverallJobRank() {
        return overallJobRank;
    }

    /**
     * 获取地图对象类型
     *
     * @return PLAYER_NPC类型
     */
    @Override
    public MapObjectType getType() {
        return MapObjectType.PLAYER_NPC;
    }

    /**
     * 发送生成数据包给客户端
     *
     * @param client 客户端
     */
    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnPlayerNPC(this));
        client.sendPacket(PacketCreator.getPlayerNPC(this));
    }

    /**
     * 发送销毁数据包给客户端
     *
     * @param client 客户端
     */
    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removeNPCController(this.getObjectId()));
        client.sendPacket(PacketCreator.removePlayerNPC(this.getObjectId()));
    }

    /**
     * 获取并自增世界职业排名计数器
     *
     * @param world 世界ID
     * @param job 职业ID
     * @return 自增前的计数器值
     */
    private static int getAndIncrementRunningWorldJobRanks(int world, int job) {
        AtomicInteger wjr = runningWorldJobRank.computeIfAbsent(new Pair<>(world, job), k -> new AtomicInteger(1));
        return wjr.getAndIncrement();
    }

    /**
     * 检查指定名称的玩家NPC是否可以在指定地图生成
     * 同一地图不允许同名玩家NPC重复生成
     *
     * @param name 玩家名称
     * @param mapid 地图ID
     * @return true表示可以生成
     */
    public static boolean canSpawnPlayerNpc(String name, int mapid) {
        List<PlayernpcsDO> playerNpcDOs = npcService.getPlayerNpcDOs(PlayernpcsDO.builder().name(name).map(mapid).build());
        return playerNpcDOs.isEmpty();
    }

    /**
     * 更新玩家NPC在地图上的位置，并持久化到数据库
     *
     * @param map 地图
     * @param newPos 新位置
     */
    public void updatePlayerNPCPosition(MapleMap map, Point newPos) {
        setPosition(newPos);
        RX0 = newPos.x + 50;
        RX1 = newPos.x - 50;
        CY = newPos.y;
        FH = map.getFootholds().findBelow(newPos).getId();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE playernpcs SET x = ?, cy = ?, fh = ?, rx0 = ?, rx1 = ? WHERE id = ?")) {
            ps.setInt(1, newPos.x);
            ps.setInt(2, CY);
            ps.setInt(3, FH);
            ps.setInt(4, RX0);
            ps.setInt(5, RX1);
            ps.setInt(6, getObjectId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从数据库拉取指定分支可用的脚本ID缓存到内存
     * 只保留存在于WZ数据中的脚本ID，避免客户端崩溃
     *
     * @param branch 分支（0-25），每个分支分配不同ID范围
     * @param list 结果列表（输出参数）
     */
    private static void fetchAvailableScriptIdsFromDb(byte branch, List<Integer> list) {
        try {
            int branchLen = (branch < 26) ? 100 : 400;
            int branchSid = NpcId.PLAYER_NPC_BASE + (branch * 100);
            int nextBranchSid = branchSid + branchLen;

            List<Integer> availables = new ArrayList<>(20);
            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT scriptid FROM playernpcs WHERE scriptid >= ? AND scriptid < ? ORDER BY scriptid")) {
                ps.setInt(1, branchSid);
                ps.setInt(2, nextBranchSid);

                Set<Integer> usedScriptIds = new HashSet<>();
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        usedScriptIds.add(rs.getInt(1));
                    }
                }

                int j = 0;
                for (int i = branchSid; i < nextBranchSid; i++) {
                    if (!usedScriptIds.contains(i)) {
                        if (PlayerNPCFactory.isExistentScriptid(i)) {  // thanks Ark, Zein, geno, Ariel, JrCl0wn for noticing client crashes due to use of missing scriptids
                            availables.add(i);
                            j++;

                            if (j == 20) {
                                break;
                            }
                        } else {
                            break;  // after this point no more scriptids expected...
                        }
                    }
                }
            }

            for (int i = availables.size() - 1; i >= 0; i--) {
                list.add(availables.get(i));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * 获取下一个可用的脚本ID
     * 从缓存中取出，缓存为空时从数据库拉取
     *
     * @param branch 分支
     * @return 脚本ID，-1表示无可用ID
     */
    private static int getNextScriptId(byte branch) {
        List<Integer> availablesBranch = availablePlayerNpcScriptIds.computeIfAbsent(branch, k -> new ArrayList<>(20));

        if (availablesBranch.isEmpty()) {
            fetchAvailableScriptIdsFromDb(branch, availablesBranch);

            if (availablesBranch.isEmpty()) {
                return -1;
            }
        }

        return availablesBranch.removeLast();
    }

    private static PlayerNPC createPlayerNPCInternal(MapleMap map, Point pos, Character chr) {
        int mapId = map.getId();

        if (!canSpawnPlayerNpc(chr.getName(), mapId)) {
            return null;
        }

        byte branch = GameConstants.getHallOfFameBranch(chr.getJob(), mapId);

        int scriptId = getNextScriptId(branch);
        if (scriptId == -1) {
            return null;
        }

        if (pos == null) {
            if (GameConstants.isPodiumHallOfFameMap(map.getId())) {
                pos = PlayerNPCPodium.getNextPlayerNpcPosition(map);
            } else {
                pos = PlayerNPCPositioner.getNextPlayerNpcPosition(map);
            }

            if (pos == null) {
                return null;
            }
        }

        if (GameConfig.getServerBoolean("use_debug")) {
            log.info("GOT SID {}, POS {}", scriptId, pos);
        }

        int worldId = chr.getWorld();
        int jobId = (chr.getJob().getId() / 100) * 100;

        List<PlayernpcsDO> playerNpcDOs = npcService.getPlayerNpcDOs(PlayernpcsDO.builder().scriptid(scriptId).build());
        if (!playerNpcDOs.isEmpty()) {
            return null;
        }
        PlayernpcsDO playerNpcDO = PlayernpcsDO.builder()
                .name(chr.getName())
                .hair(chr.getHair())
                .face(chr.getFace())
                .skin(chr.getSkinColor().getId())
                .gender(chr.getGender())
                .x(pos.x)
                .cy(pos.y)
                .world(worldId)
                .map(mapId)
                .scriptid(scriptId)
                .dir(1)
                .fh(map.getFootholds().findBelow(pos).getId())
                .rx0(pos.x + 50)
                .rx1(pos.x - 50)
                .worldrank(runningWorldRank.get(worldId).getAndIncrement())
                .overallrank(runningOverallRank.getAndIncrement())
                .worldjobrank(getAndIncrementRunningWorldJobRanks(worldId, jobId))
                .job(jobId)
                .build();
        List<PlayernpcsEquipDO> playerNpcEquipDOS = chr.getInventory(InventoryType.EQUIPPED).list().stream()
                .map(equip -> PlayernpcsEquipDO.builder()
                        .equipid(equip.getItemId())
                        .equippos(equip.getPosition())
                        .build())
                .toList();
        return npcService.createPlayerNPC(playerNpcDO, playerNpcEquipDOS);
    }

    /**
     * 内部方法：从数据库删除玩家NPC
     * 删除NPC及其装备数据，返回受影响的地图ID列表
     *
     * @param map 地图（可为null，null时删除所有地图上的该NPC）
     * @param chr 角色
     * @return 受影响的地图ID列表（第一个元素为世界ID）
     */
    private static List<Integer> removePlayerNPCInternal(MapleMap map, Character chr) {
        Set<Integer> updateMapids = new HashSet<>();

        List<Integer> mapids = new LinkedList<>();
        mapids.add(chr.getWorld());

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT id, map FROM playernpcs WHERE name LIKE ?" + (map != null ? " AND map = ?" : ""))) {
            ps.setString(1, chr.getName());
            if (map != null) {
                ps.setInt(2, map.getId());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    updateMapids.add(rs.getInt("map"));
                    int npcId = rs.getInt("id");

                    try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM playernpcs WHERE id = ?")) {
                        ps2.setInt(1, npcId);
                        ps2.executeUpdate();
                    }

                    try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM playernpcs_equip WHERE npcid = ?")) {
                        ps2.setInt(1, npcId);
                        ps2.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mapids.addAll(updateMapids);

        return mapids;
    }

    /**
     * 处理玩家NPC的创建或删除（同步方法）
     *
     * @param map 地图
     * @param pos 位置
     * @param chr 角色
     * @param create true=创建，false=删除
     * @return Pair（创建的NPC, 受影响的地图ID列表）
     */
    private static synchronized Pair<PlayerNPC, List<Integer>> processPlayerNPCInternal(MapleMap map, Point pos, Character chr, boolean create) {
        if (create) {
            return new Pair<>(createPlayerNPCInternal(map, pos, chr), null);
        } else {
            return new Pair<>(null, removePlayerNPCInternal(map, chr));
        }
    }

    public static boolean spawnPlayerNPC(int mapid, Character chr) {
        return spawnPlayerNPC(mapid, null, chr);
    }

    /**
     * 在指定地图的指定位置生成玩家NPC（公开接口）
     * 在所有频道广播生成消息
     *
     * @param mapid 地图ID
     * @param pos 指定位置（可为null）
     * @param chr 角色
     * @return true表示生成成功
     */
    public static boolean spawnPlayerNPC(int mapid, Point pos, Character chr) {
        if (chr == null) {
            return false;
        }

        PlayerNPC pn = processPlayerNPCInternal(chr.getClient().getChannelServer().getMapFactory().getMap(mapid), pos, chr, true).getLeft();
        if (pn != null) {
            for (Channel channel : Server.getInstance().getChannelsFromWorld(chr.getWorld())) {
                MapleMap m = channel.getMapFactory().getMap(mapid);

                m.addPlayerNPCMapObject(pn);
                m.broadcastMessage(PacketCreator.spawnPlayerNPC(pn));
                m.broadcastMessage(PacketCreator.getPlayerNPC(pn));
            }

            return true;
        } else {
            return false;
        }
    }

    private static PlayerNPC getPlayerNPCFromWorldMap(String name, int world, int map) {
        World wserv = Server.getInstance().getWorld(world);
        for (MapObject pnpcObj : wserv.getChannel(1).getMapFactory().getMap(map).getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER_NPC))) {
            PlayerNPC pn = (PlayerNPC) pnpcObj;

            if (name.contentEquals(pn.getName()) && pn.getScriptId() < NpcId.CUSTOM_DEV) {
                return pn;
            }
        }

        return null;
    }

    public static void removePlayerNPC(Character chr) {
        if (chr == null) {
            return;
        }

        List<Integer> updateMapids = processPlayerNPCInternal(null, null, chr, false).getRight();
        int worldid = updateMapids.removeFirst();

        for (Integer mapid : updateMapids) {
            PlayerNPC pn = getPlayerNPCFromWorldMap(chr.getName(), worldid, mapid);

            if (pn != null) {
                for (Channel channel : Server.getInstance().getChannelsFromWorld(worldid)) {
                    MapleMap m = channel.getMapFactory().getMap(mapid);
                    m.removeMapObject(pn);

                    m.broadcastMessage(PacketCreator.removeNPCController(pn.getObjectId()));
                    m.broadcastMessage(PacketCreator.removePlayerNPC(pn.getObjectId()));
                }
            }
        }
    }

    /**
     * 在世界各地图批量生成玩家NPC
     * 遍历世界所有角色，为每个角色在指定地图生成NPC
     *
     * @param mapid 地图ID
     * @param world 世界ID
     */
    public static void multicastSpawnPlayerNPC(int mapid, int world) {
        World wserv = Server.getInstance().getWorld(world);
        if (wserv == null) {
            return;
        }

        Client c = Client.createMock();
        c.setWorld(world);
        c.setChannel(1);

        for (Character mc : wserv.loadAndGetAllCharactersView()) {
            mc.setClient(c);
            spawnPlayerNPC(mapid, mc);
        }
    }

    public static void removeAllPlayerNPC() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT DISTINCT world, map FROM playernpcs");
             ResultSet rs = ps.executeQuery()) {
            int wsize = Server.getInstance().getWorldsSize();
            while (rs.next()) {
                int world = rs.getInt("world"), map = rs.getInt("map");
                if (world >= wsize) {
                    continue;
                }

                for (Channel channel : Server.getInstance().getChannelsFromWorld(world)) {
                    MapleMap m = channel.getMapFactory().getMap(map);

                    for (MapObject pnpcObj : m.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER_NPC))) {
                        PlayerNPC pn = (PlayerNPC) pnpcObj;
                        m.removeMapObject(pnpcObj);
                        m.broadcastMessage(PacketCreator.removeNPCController(pn.getObjectId()));
                        m.broadcastMessage(PacketCreator.removePlayerNPC(pn.getObjectId()));
                    }
                }
            }

            try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM playernpcs")) {
                ps2.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM playernpcs_equip")) {
                ps2.executeUpdate();
            }

            try (PreparedStatement ps2 = con.prepareStatement("DELETE FROM playernpcs_field")) {
                ps2.executeUpdate();
            }

            for (World w : Server.getInstance().getWorlds()) {
                w.resetPlayerNpcMapData();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在地图上添加当前地图已有的玩家NPC对象
     * 从数据库加载该地图的所有玩家NPC并添加到地图对象列表中
     *
     * @param map 地图
     */
    public static void addPlayerNPCMapObject(MapleMap map) {
        List<PlayerNPC> playerNPCList = npcService.getPlayerNPC(PlayernpcsDO.builder().map(map.getId()).world(map.getWorld()).build());
        playerNPCList.forEach(map::addPlayerNPCMapObject);
    }
}