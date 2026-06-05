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
package org.gms.server.maps;

import org.gms.config.GameConfig;
import org.gms.constants.id.MapId;
import org.gms.provider.*;
import org.gms.provider.wz.WZFiles;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.server.life.AbstractLoadedLife;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.life.PlayerNPC;
import org.gms.server.partyquest.GuardianSpawnPoint;
import org.gms.util.DatabaseConnection;
import org.gms.util.NumberTool;
import org.gms.util.StringUtil;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
     * 【工厂/提供者】MapFactory：创建或提供 `maps` 相关运行时对象。
     * 
     * <p>MapFactory 是地图工厂类，负责从 WZ 文件和数据库加载和创建游戏中的地图对象。
     * 它提供了创建地图实例的核心功能，包括加载地图的基本信息、生物（怪物/NPC）、
     * 传送门、立足点、反应堆等地图元素。</p>
     * 
     * <p>主要职责包括：</p>
     * <ul>
     *   <li>从 WZ 文件加载地图数据</li>
     *   <li>创建并初始化地图实例</li>
     *   <li>加载地图上的生物（怪物/NPC）</li>
     *   <li>设置地图属性（边界、限制、名称等）</li>
     *   <li>处理特殊地图类型（如 CPQ、怪物嘉年华等）</li>
     * </ul>
     */
    public class MapFactory {
        /** Map名称数据（从String.wz加载） */
        private static final Data nameData = DataProviderFactory.getDataProvider(WZFiles.STRING).getData("Map.img");
        /** MAP.wz 数据提供器 */
        private static final DataProvider mapSource = DataProviderFactory.getDataProvider(WZFiles.MAP);

    /**
     * 从WZ文件加载地图生物（怪物/NPC）
     * 
     * <p>从WZ文件中解析并加载地图上的生物信息，包括怪物和NPC。
     * 对于CPQ（怪物嘉年华）地图，会根据生物ID分配团队（红队或蓝队）。</p>
     * 
     * <p>生物数据包含以下属性：</p>
     * <ul>
     *   <li>id: 生物唯一标识符</li>
     *   <li>type: 生物类型（'m'代表怪物，'n'代表NPC）</li>
     *   <li>position: 位置坐标（x, y）</li>
     *   <li>bounds: 移动边界（rx0, rx1）</li>
     *   <li>appearance: 外观属性（方向f、立绘点fh、隐藏状态hide）</li>
     *   <li>respawn: 刷新时间（mobTime）</li>
     * </ul>
     *
     * @param map     要加载生物的目标地图
     * @param mapData 地图的WZ数据
     */
    private static void loadLifeFromWz(MapleMap map, Data mapData) {
        for (Data life : mapData.getChildByPath("life")) {
            life.getName();
            String id = DataTool.getString(life.getChildByPath("id"));
            String type = DataTool.getString(life.getChildByPath("type"));
            int team = DataTool.getInt("team", life, -1);
            if (map.isCPQMap2() && type.equals("m")) {
                if ((Integer.parseInt(life.getName()) % 2) == 0) {
                    team = 0;
                } else {
                    team = 1;
                }
            }
            int cy = DataTool.getInt(life.getChildByPath("cy"));
            Data dF = life.getChildByPath("f");
            int f = (dF != null) ? DataTool.getInt(dF) : 0;
            int fh = DataTool.getInt(life.getChildByPath("fh"));
            int rx0 = DataTool.getInt(life.getChildByPath("rx0"));
            int rx1 = DataTool.getInt(life.getChildByPath("rx1"));
            int x = DataTool.getInt(life.getChildByPath("x"));
            int y = DataTool.getInt(life.getChildByPath("y"));
            int hide = DataTool.getInt("hide", life, 0);
            int mobTime = DataTool.getInt("mobTime", life, 0);

            loadLifeRaw(map, Integer.parseInt(id), type, cy, f, fh, rx0, rx1, x, y, hide, mobTime, team);
        }
    }

    /**
     * 从数据库加载地图生物
     * 
     * <p>从数据库的plife表中加载自定义地图生物信息。
     * 这允许服务器管理员添加自定义的怪物和NPC到特定地图。</p>
     * 
     * <p>数据库表plife包含以下字段：</p>
     * <ul>
     *   <li>map: 地图ID</li>
     *   <li>world: 世界ID</li>
     *   <li>life: 生物ID</li>
     *   <li>type: 生物类型（'m'代表怪物，'n'代表NPC）</li>
     *   <li>位置和外观属性（x, y, cy, f, fh, rx0, rx1, hide）</li>
     *   <li>刷新时间（mobtime）和团队（team）</li>
     * </ul>
     *
     * @param map 要加载生物的目标地图
     */
    private static void loadLifeFromDb(MapleMap map) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM plife WHERE map = ? and world = ?")) {
            ps.setInt(1, map.getId());
            ps.setInt(2, map.getWorld());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("life");
                    String type = rs.getString("type");
                    int cy = rs.getInt("cy");
                    int f = rs.getInt("f");
                    int fh = rs.getInt("fh");
                    int rx0 = rs.getInt("rx0");
                    int rx1 = rs.getInt("rx1");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int hide = rs.getInt("hide");
                    int mobTime = rs.getInt("mobtime");
                    int team = rs.getInt("team");

                    loadLifeRaw(map, id, type, cy, f, fh, rx0, rx1, x, y, hide, mobTime, team);
                }
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    /**
     * 加载生物并添加到地图
     * 
     * <p>根据提供的参数创建生物实例并将其添加到地图中。
     * 对于怪物类型，会根据配置设置刷新率和刷新时间，
     * 特别处理BOSS怪物和事件地图的情况。</p>
     * 
     * <p>对于怪物，会根据配置调整其刷新行为：</p>
     * <ul>
     *   <li>普通怪物：根据mob_respawn_rate配置倍增刷新数量</li>
     *   <li>BOSS怪物：使用专门的刷新时间倍率调整刷新间隔</li>
     *   <li>事件地图：忽略刷新率配置，保持原始刷新行为</li>
     * </ul>
     *
     * @param map     要添加生物的地图
     * @param id      生物ID
     * @param type    生物类型（'m'代表怪物，'n'代表NPC）
     * @param cy      cy坐标（垂直位置）
     * @param f       方向（朝向）
     * @param fh      立足点ID
     * @param rx0     左移动边界
     * @param rx1     右移动边界
     * @param x       x坐标（水平位置）
     * @param y       y坐标（垂直位置）
     * @param hide    是否隐藏（1为隐藏，0为显示）
     * @param mobTime 刷新时间（毫秒，-1表示不刷新）
     * @param team    团队ID（主要用于CPQ地图区分队伍）
     */
    private static void loadLifeRaw(MapleMap map, int id, String type, int cy, int f, int fh, int rx0, int rx1, int x, int y, int hide, int mobTime, int team) {
        AbstractLoadedLife myLife = loadLife(id, type, cy, f, fh, rx0, rx1, x, y, hide);
        if (myLife instanceof Monster monster) {
            int mobRespawnRate = GameConfig.getServerInt("mob_respawn_rate");
            float mobTimeRate = GameConfig.getServerFloat("boss_respawn_mob_time_rate");
            mobTimeRate = (mobTimeRate <= 0 || mobTimeRate > 1) ? 1 : mobTimeRate;  //将值限定在0~1之间的范围
            if (mobRespawnRate < 1) {   //如果读入的值小于1，或者怪物为boss，则设定生怪倍率为1
                mobRespawnRate = 1;
            }
            if (monster.isBoss()) {
                mobRespawnRate = 1;
                mobTime = NumberTool.floatToInt(mobTime * mobTimeRate);
            }
            // 如果是事件地图，刷新倍率保持不变
            if (map.getEventInstance() != null) {
                mobRespawnRate = 1;
            }

            for (int i = 0; i < mobRespawnRate; i++) {
                if (mobTime == -1) { //does not respawn, force spawn once
                    map.spawnMonster(monster);
                } else {
                    map.addMonsterSpawn(monster, mobTime, team);
                }
            }

            //should the map be reseted, use allMonsterSpawn list of monsters to spawn them again
            map.addAllMonsterSpawn(monster, mobTime, team);
        } else {
            map.addMapObject(myLife);
        }
    }

    /**
     * 从WZ文件加载地图
     * 
     * <p>从WZ文件系统中加载指定地图的所有数据并创建MapleMap实例。
     * 此方法是地图加载的核心方法，负责解析地图的各种元素，
     * 包括基本信息、传送门、立足点、生物、反应堆等。</p>
     * 
     * <p>主要加载流程包括：</p>
     * <ol>
     *   <li>解析地图基本信息（返回地图、怪物率、限制等）</li>
     *   <li>创建MapleMap实例并设置基本属性</li>
     *   <li>加载传送门信息</li>
     *   <li>设置地图边界和限制</li>
     *   <li>构建立足点树结构</li>
     *   <li>加载地图区域和座位信息</li>
     *   <li>加载生物（怪物/NPC）</li>
     *   <li>加载反应堆</li>
     *   <li>设置地图名称和街道名</li>
     *   <li>配置特殊地图属性（时钟、永久战、城镇、HP减少等）</li>
     * </ol>
     *
     * @param mapid  要加载的地图ID
     * @param world  世界ID
     * @param channel 频道ID
     * @param event  事件实例管理器（可为null，用于事件地图）
     * @return 创建好的MapleMap实例
     */
    public static MapleMap loadMapFromWz(int mapid, int world, int channel, EventInstanceManager event) {
        MapleMap map;

        String mapName = getMapName(mapid);
        Data mapData = mapSource.getData(mapName);
        Data infoData = mapData.getChildByPath("info");

        String link = DataTool.getString(infoData.getChildByPath("link"), "");
        if (!link.equals("")) {
            mapName = getMapName(Integer.parseInt(link));
            mapData = mapSource.getData(mapName);
        }
        float monsterRate = 0;
        Data mobRate = infoData.getChildByPath("mobRate");
        if (mobRate != null) {
            monsterRate = (Float) mobRate.getData();
        }
        map = new MapleMap(mapid, world, channel, DataTool.getInt("returnMap", infoData), monsterRate);
        map.setEventInstance(event);

        String onFirstEnter = DataTool.getString(infoData.getChildByPath("onFirstUserEnter"), String.valueOf(mapid));
        map.setOnFirstUserEnter(onFirstEnter.equals("") ? String.valueOf(mapid) : onFirstEnter);

        String onEnter = DataTool.getString(infoData.getChildByPath("onUserEnter"), String.valueOf(mapid));
        map.setOnUserEnter(onEnter.equals("") ? String.valueOf(mapid) : onEnter);

        map.setFieldLimit(DataTool.getInt(infoData.getChildByPath("fieldLimit"), 0));
        map.setMobInterval((short) DataTool.getInt(infoData.getChildByPath("createMobInterval"), 5000));
        PortalFactory portalFactory = new PortalFactory();
        for (Data portal : mapData.getChildByPath("portal")) {
            map.addPortal(portalFactory.makePortal(DataTool.getInt(portal.getChildByPath("pt")), portal));
        }
        Data timeMob = infoData.getChildByPath("timeMob");
        if (timeMob != null) {
            map.setTimeMob(DataTool.getInt(timeMob.getChildByPath("id")), DataTool.getString(timeMob.getChildByPath("message")));
        }

        int[] bounds = new int[4];
        bounds[0] = DataTool.getInt(infoData.getChildByPath("VRTop"));
        bounds[1] = DataTool.getInt(infoData.getChildByPath("VRBottom"));

        if (bounds[0] == bounds[1]) {    // old-style baked map
            Data minimapData = mapData.getChildByPath("miniMap");
            if (minimapData != null) {
                bounds[0] = DataTool.getInt(minimapData.getChildByPath("centerX")) * -1;
                bounds[1] = DataTool.getInt(minimapData.getChildByPath("centerY")) * -1;
                bounds[2] = DataTool.getInt(minimapData.getChildByPath("height"));
                bounds[3] = DataTool.getInt(minimapData.getChildByPath("width"));

                map.setMapPointBoundings(bounds[0], bounds[1], bounds[2], bounds[3]);
            } else {
                int dist = (1 << 18);
                map.setMapPointBoundings(-dist / 2, -dist / 2, dist, dist);
            }
        } else {
            bounds[2] = DataTool.getInt(infoData.getChildByPath("VRLeft"));
            bounds[3] = DataTool.getInt(infoData.getChildByPath("VRRight"));

            map.setMapLineBoundings(bounds[0], bounds[1], bounds[2], bounds[3]);
        }

        List<Foothold> allFootholds = new LinkedList<>();
        Point lBound = new Point();
        Point uBound = new Point();
        for (Data footRoot : mapData.getChildByPath("foothold")) {
            for (Data footCat : footRoot) {
                for (Data footHold : footCat) {
                    int x1 = DataTool.getInt(footHold.getChildByPath("x1"));
                    int y1 = DataTool.getInt(footHold.getChildByPath("y1"));
                    int x2 = DataTool.getInt(footHold.getChildByPath("x2"));
                    int y2 = DataTool.getInt(footHold.getChildByPath("y2"));
                    Foothold fh = new Foothold(new Point(x1, y1), new Point(x2, y2), Integer.parseInt(footHold.getName()));
                    fh.setPrev(DataTool.getInt(footHold.getChildByPath("prev")));
                    fh.setNext(DataTool.getInt(footHold.getChildByPath("next")));
                    if (fh.getX1() < lBound.x) {
                        lBound.x = fh.getX1();
                    }
                    if (fh.getX2() > uBound.x) {
                        uBound.x = fh.getX2();
                    }
                    if (fh.getY1() < lBound.y) {
                        lBound.y = fh.getY1();
                    }
                    if (fh.getY2() > uBound.y) {
                        uBound.y = fh.getY2();
                    }
                    allFootholds.add(fh);
                }
            }
        }
        FootholdTree fTree = new FootholdTree(lBound, uBound);
        for (Foothold fh : allFootholds) {
            fTree.insert(fh);
        }
        map.setFootholds(fTree);
        if (mapData.getChildByPath("area") != null) {
            for (Data area : mapData.getChildByPath("area")) {
                int x1 = DataTool.getInt(area.getChildByPath("x1"));
                int y1 = DataTool.getInt(area.getChildByPath("y1"));
                int x2 = DataTool.getInt(area.getChildByPath("x2"));
                int y2 = DataTool.getInt(area.getChildByPath("y2"));
                map.addMapleArea(new Rectangle(x1, y1, (x2 - x1), (y2 - y1)));
            }
        }
        if (mapData.getChildByPath("seat") != null) {
            int seats = mapData.getChildByPath("seat").getChildren().size();
            map.setSeats(seats);
        }
        if (event == null) {
            PlayerNPC.addPlayerNPCMapObject(map);
        }

        loadLifeFromWz(map, mapData);
        loadLifeFromDb(map);

        if (map.isCPQMap()) {
            Data mcData = mapData.getChildByPath("monsterCarnival");
            if (mcData != null) {
                map.setDeathCP(DataTool.getIntConvert("deathCP", mcData, 0));
                map.setMaxMobs(DataTool.getIntConvert("mobGenMax", mcData, 20));    // thanks Atoot for noticing CPQ1 bf. 3 and 4 not accepting spawns due to undefined limits, Lame for noticing a need to cap mob spawns even on such undefined limits
                map.setTimeDefault(DataTool.getIntConvert("timeDefault", mcData, 0));
                map.setTimeExpand(DataTool.getIntConvert("timeExpand", mcData, 0));
                map.setMaxReactors(DataTool.getIntConvert("guardianGenMax", mcData, 16));
                Data guardianGenData = mcData.getChildByPath("guardianGenPos");
                for (Data node : guardianGenData.getChildren()) {
                    GuardianSpawnPoint pt = new GuardianSpawnPoint(new Point(DataTool.getIntConvert("x", node), DataTool.getIntConvert("y", node)));
                    pt.setTeam(DataTool.getIntConvert("team", node, -1));
                    pt.setTaken(false);
                    map.addGuardianSpawnPoint(pt);
                }
                if (mcData.getChildByPath("skill") != null) {
                    for (Data area : mcData.getChildByPath("skill")) {
                        map.addSkillId(DataTool.getInt(area));
                    }
                }

                if (mcData.getChildByPath("mob") != null) {
                    for (Data area : mcData.getChildByPath("mob")) {
                        map.addMobSpawn(DataTool.getInt(area.getChildByPath("id")), DataTool.getInt(area.getChildByPath("spendCP")));
                    }
                }
            }

        }

        if (mapData.getChildByPath("reactor") != null) {
            for (Data reactor : mapData.getChildByPath("reactor")) {
                String id = DataTool.getString(reactor.getChildByPath("id"));
                if (id != null) {
                    Reactor newReactor = loadReactor(reactor, id, (byte) DataTool.getInt(reactor.getChildByPath("f"), 0));
                    map.spawnReactor(newReactor);
                }
            }
        }

        map.setMapName(loadPlaceName(mapid));
        map.setStreetName(loadStreetName(mapid));

        map.setClock(mapData.getChildByPath("clock") != null);
        map.setEverlast(DataTool.getIntConvert("everlast", infoData, 0) != 0); // thanks davidlafriniere for noticing value 0 accounting as true
        map.setTown(DataTool.getIntConvert("town", infoData, 0) != 0);
        map.setHPDec(DataTool.getIntConvert("decHP", infoData, 0));
        map.setHPDecProtect(DataTool.getIntConvert("protectItem", infoData, 0));
        map.setForcedReturnMap(DataTool.getInt(infoData.getChildByPath("forcedReturn"), MapId.NONE));
        map.setBoat(mapData.getChildByPath("shipObj") != null);
        map.setTimeLimit(DataTool.getIntConvert("timeLimit", infoData, -1));
        map.setFieldType(DataTool.getIntConvert("fieldType", infoData, 0));
        map.setMobCapacity(DataTool.getIntConvert("fixedMobCapacity", infoData, 500));//Is there a map that contains more than 500 mobs?

        Data recData = infoData.getChildByPath("recovery");
        if (recData != null) {
            map.setRecovery(DataTool.getFloat(recData));
        }

        HashMap<Integer, Integer> backTypes = new HashMap<>();
        try {
            for (Data layer : mapData.getChildByPath("back")) { // yolo
                int layerNum = Integer.parseInt(layer.getName());
                int btype = DataTool.getInt(layer.getChildByPath("type"), 0);

                backTypes.put(layerNum, btype);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // swallow cause I'm cool
        }

        map.setBackgroundTypes(backTypes);
        map.generateMapDropRangeCache();

        return map;
    }

    /**
     * 加载生物实例
     * 
     * <p>根据给定的参数创建并初始化生物实例（怪物或NPC）。
     * 通过LifeFactory获取基础生物对象，然后设置其位置、外观和其他属性。</p>
     * 
     * <p>生物实例包含以下关键属性：</p>
     * <ul>
     *   <li>位置信息：坐标(x,y)和cy值</li>
     *   <li>外观信息：方向(f)、面向方向(FacingDirection)、隐藏状态(hide)</li>
     *   <li>活动范围：左右边界(rx0, rx1)和立足点(fh)</li>
     * </ul>
     *
     * @param id   生物ID（在WZ文件中定义的唯一标识符）
     * @param type 生物类型（'m'代表怪物，'n'代表NPC）
     * @param cy   cy坐标（垂直位置参考值）
     * @param f    方向（0为右，1为左）
     * @param fh   立足点ID（用于确定生物可以站立的位置）
     * @param rx0  左移动边界
     * @param rx1  右移动边界
     * @param x    x坐标（水平位置）
     * @param y    y坐标（垂直位置）
     * @param hide 是否隐藏（1为隐藏，0为显示）
     * @return 初始化后的AbstractLoadedLife实例
     */
    private static AbstractLoadedLife loadLife(int id, String type, int cy, int f, int fh, int rx0, int rx1, int x, int y, int hide) {
        AbstractLoadedLife myLife = LifeFactory.getLife(id, type);
        myLife.setCy(cy);
        myLife.setF(f);
        myLife.setFh(fh);
        myLife.setRx0(rx0);
        myLife.setRx1(rx1);
        myLife.setPosition(new Point(x, y));
        if (hide == 1) {
            myLife.setHide(true);
        }
        return myLife;
    }

    /**
     * 加载反应堆实例
     * 
     * <p>根据WZ数据创建并初始化反应堆对象。
     * 反应堆是地图上的可交互对象，玩家可以通过技能或道具与其互动，
     * 触发预设的动作序列（如开启宝箱、触发剧情等）。</p>
     * 
     * <p>反应堆包含以下关键属性：</p>
     * <ul>
     *   <li>位置：在地图上的坐标(x,y)</li>
     *   <li>外观：面向方向和名称</li>
     *   <li>行为：触发延迟时间和动作序列</li>
     * </ul>
     *
     * @param reactor          反应堆的WZ数据节点
     * @param id               反应堆ID（在WZ文件中定义的唯一标识符）
     * @param FacingDirection  面向方向（控制反应堆的朝向）
     * @return 初始化后的Reactor实例
     */
    private static Reactor loadReactor(Data reactor, String id, final byte FacingDirection) {
        Reactor myReactor = new Reactor(ReactorFactory.getReactor(Integer.parseInt(id)), Integer.parseInt(id));
        int x = DataTool.getInt(reactor.getChildByPath("x"));
        int y = DataTool.getInt(reactor.getChildByPath("y"));
        myReactor.setFacingDirection(FacingDirection);
        myReactor.setPosition(new Point(x, y));
        myReactor.setDelay((int) SECONDS.toMillis(DataTool.getInt(reactor.getChildByPath("reactorTime"))));
        myReactor.setName(DataTool.getString(reactor.getChildByPath("name"), ""));
        myReactor.resetReactorActions(0);
        return myReactor;
    }

    /**
     * 获取地图名称路径
     * 
     * <p>根据地图ID构建对应WZ文件中的地图路径。
     * 地图ID被分为不同的区域（基于亿位数字），每个区域对应WZ文件中的不同子目录。</p>
     * 
     * <p>路径格式为："Map/Map{区域}/{补零后的地图ID}.img"</p>
     * <ul>
     *   <li>区域0: Maple (ID < 100000000)</li>
     *   <li>区域1: Victoria (ID >= 100000000, < Orbis)</li>
     *   <li>区域2: Ossyria (ID >= Orbis, < ELLIN_FOREST)</li>
     *   <li>等等...</li>
     * </ul>
     *
     * @param mapid 地图ID（整数形式）
     * @return 构建的地图文件路径字符串
     */
    private static String getMapName(int mapid) {
        String mapName = StringUtil.getLeftPaddedStr(Integer.toString(mapid), '0', 9);
        StringBuilder builder = new StringBuilder("Map/Map");
        int area = mapid / 100000000;
        builder.append(area);
        builder.append("/");
        builder.append(mapName);
        builder.append(".img");
        mapName = builder.toString();
        return mapName;
    }

    private static String getMapStringName(int mapid) {
        StringBuilder builder = new StringBuilder();
        if (mapid < 100000000) {
            builder.append("maple");
        } else if (mapid >= 100000000 && mapid < MapId.ORBIS) {
            builder.append("victoria");
        } else if (mapid >= MapId.ORBIS && mapid < MapId.ELLIN_FOREST) {
            builder.append("ossyria");
        } else if (mapid >= MapId.ELLIN_FOREST && mapid < 400000000) {
            builder.append("elin");
        } else if (mapid >= MapId.SINGAPORE && mapid < 560000000) {
            builder.append("singapore");
        } else if (mapid >= MapId.NEW_LEAF_CITY && mapid < 620000000) {
            builder.append("MasteriaGL");
        } else if (mapid >= 677000000 && mapid < 677100000) {
            builder.append("Episode1GL");
        } else if (mapid >= 670000000 && mapid < 682000000) {
            if ((mapid >= 674030000 && mapid < 674040000) || (mapid >= 680100000 && mapid < 680200000)) {
                builder.append("etc");
            } else {
                builder.append("weddingGL");
            }
        } else if (mapid >= 682000000 && mapid < 683000000) {
            builder.append("HalloweenGL");
        } else if (mapid >= 683000000 && mapid < 684000000) {
            builder.append("event");
        } else if (mapid >= MapId.MUSHROOM_SHRINE && mapid < 900000000) {
            if ((mapid >= 889100000 && mapid < 889200000)) {
                builder.append("etc");
            } else {
                builder.append("jp");
            }
        } else {
            builder.append("etc");
        }
        builder.append("/").append(mapid);
        return builder.toString();
    }

    /**
     * 加载地图地点名称
     * 
     * <p>从WZ文件的Map.img中获取指定地图的地点名称。
     * 地点名称通常是地图的正式名称（如"金银岛"、"射手村"等）。</p>
     * 
     * <p>如果无法找到或加载地图名称，则返回空字符串。</p>
     *
     * @param mapid 地图ID
     * @return 地图的地点名称，如果不存在则返回空字符串
     */
    public static String loadPlaceName(int mapid) {
        try {
            return DataTool.getString("mapName", nameData.getChildByPath(getMapStringName(mapid)), "");
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 加载地图街道名称
     * 
     * <p>从WZ文件的Map.img中获取指定地图的街道名称。
     * 街道名称通常是地图所属的区域或街道名称（如"冒险家大道"等）。</p>
     * 
     * <p>如果无法找到或加载街道名称，则返回空字符串。</p>
     *
     * @param mapid 地图ID
     * @return 地图的街道名称，如果不存在则返回空字符串
     */
    public static String loadStreetName(int mapid) {
        try {
            return DataTool.getString("streetName", nameData.getChildByPath(getMapStringName(mapid)), "");
        } catch (Exception e) {
            return "";
        }
    }

    public static String getMapIdByLifeId(int lifeId) {
        return resolveDir(mapSource.getRoot(), lifeId);
    }

    private static String resolveDir(DataEntry dataEntry, int lifeId) {
        String mapId = null;
        if (dataEntry instanceof DataFileEntry) {
            mapId = resolveFile(dataEntry, lifeId);
        } else if (dataEntry instanceof DataDirectoryEntry) {
            List<DataFileEntry> fileEntries = ((DataDirectoryEntry) dataEntry).getFiles();
            for (DataFileEntry fileEntry : fileEntries) {
                mapId = resolveFile(fileEntry, lifeId);
                if (mapId != null) {
                    break;
                }
            }
            List<DataDirectoryEntry> subdirectories = ((DataDirectoryEntry) dataEntry).getSubdirectories();
            for (DataDirectoryEntry subdirectory : subdirectories) {
                if (!subdirectory.getName().startsWith("Map")) {
                    continue;
                }
                mapId = resolveDir(subdirectory, lifeId);
                if (mapId != null) {
                    break;
                }
            }
        }
        return mapId;
    }

    private static String resolveFile(DataEntity dataEntry, int lifeId) {
        String mapId = null;
        if (dataEntry instanceof DataFileEntry) {
            StringBuilder pathBuilder = new StringBuilder();
            resolvePath(dataEntry, pathBuilder);
            pathBuilder.append(dataEntry.getName());
            Data data = mapSource.getData(pathBuilder.toString());
            String wzLifeId = resolveFile(data, lifeId);
            if (wzLifeId != null) {
                mapId = dataEntry.getName().substring(0, dataEntry.getName().length() - 4);
            }
        } else if (dataEntry instanceof Data) {
            Data life = ((Data) dataEntry).getChildByPath("life");
            if (life == null) {
                return null;
            }
            List<Data> children = life.getChildren();
            for (Data child : children) {
                String wzLifeId = DataTool.getString("id", child);
                if (wzLifeId != null && Integer.parseInt(wzLifeId) == lifeId) {
                    return wzLifeId;
                }
            }
        }
        return mapId;
    }

    private static void resolvePath(DataEntity dataEntry, StringBuilder pathBuilder) {
        DataEntity parent = dataEntry.getParent();
        if (parent != null && parent != mapSource.getRoot()) {
            pathBuilder.insert(0, parent.getName() + "/");
            resolvePath(parent, pathBuilder);
        }
    }
}