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
package org.gms.scripting.npc;

import lombok.Getter;
import org.gms.client.Character;
import org.gms.client.*;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ItemFactory;
import org.gms.client.inventory.Pet;
import org.gms.config.GameConfig;
import org.gms.constants.game.GameConstants;
import org.gms.constants.game.NextLevelType;
import org.gms.constants.id.MapId;
import org.gms.constants.id.NpcId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.constants.string.LanguageConstants;
import org.gms.manager.ServerManager;
import org.gms.model.pojo.NextLevelContext;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.coordinator.matchchecker.MatchCheckerListenerFactory.MatchCheckerType;
import org.gms.net.server.guild.Alliance;
import org.gms.net.server.guild.Guild;
import org.gms.net.server.guild.GuildPackets;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.service.GachaponService;
import org.gms.util.packets.WeddingPackets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.wz.WZFiles;
import org.gms.scripting.AbstractPlayerInteraction;
import org.gms.server.*;
import org.gms.server.SkillbookInformationProvider.SkillBookEntry;
import org.gms.server.events.gm.Event;
import org.gms.server.expeditions.Expedition;
import org.gms.server.expeditions.ExpeditionType;
import org.gms.server.gachapon.Gachapon;
import org.gms.server.gachapon.Gachapon.GachaponItem;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.PlayerNPC;
import org.gms.server.maps.MapManager;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.server.partyquest.AriantColiseum;
import org.gms.server.partyquest.MonsterCarnival;
import org.gms.server.partyquest.Pyramid;
import org.gms.server.partyquest.Pyramid.PyramidMode;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.*;

import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * NPC 对话管理器：由 {@link NPCScriptManager} 在玩家与 NPC（或物品脚本）交互时创建，
 * 供 Rhino/JS 脚本调用，封装多种 NPC 对话 UI、任务、物品、组队、联盟、怪物嘉年华与婚礼等常用能力。
 * <p>
 * 继承 {@link AbstractPlayerInteraction}，可访问当前 {@link org.gms.client.Client} 与 {@link Character}。
 * 带 {@link NextLevelContext} 的 {@code send*Level} / {@code get*Level} 系列可在脚本中声明多级对话的函数名自动路由。
 *
 * @author Matze
 */
public class NPCConversationManager extends AbstractPlayerInteraction {
    /** 本类日志。 */
    private static final Logger log = LoggerFactory.getLogger(NPCConversationManager.class);

    /** 对话绑定的 NPC 模板 ID（WZ 中的 NPC 编号）。 */
    private final int npc;
    /** 地图上该 NPC 实例的对象 OID；未绑实例时可为 -1。 */
    private int npcOid;
    /** 当前执行的脚本名（不含路径），与 NPCScriptManager 加载的脚本对应。 */
    private String scriptName;
    /** 客户端通过数字/文本输入框提交的内容缓存，供 {@link #getText()} 读取。 */
    private String getText;
    /** 是否为「使用物品触发的脚本」对话（与地图点击 NPC 区分）。 */
    private boolean itemScript;
    /** 部分组队 NPC 对话中缓存的对方队伍成员列表。 */
    private List<PartyCharacter> otherParty;
    /** 扭蛋业务服务，供 {@link #doGachapon()} 等使用。 */
    private static final GachaponService gachaponService = ServerManager.getApplicationContext().getBean(GachaponService.class);

    /** 按 NPC ID 缓存的默认台词，减少重复读取 WZ。 */
    private final Map<Integer, String> npcDefaultTalks = new HashMap<>();
    /**
     * 多级对话路由上下文：记录下一步/上一步/分支等脚本函数名，
     * 由 {@link NPCScriptManager} 在玩家点击按钮后根据 {@link org.gms.model.pojo.NextLevelType} 派发。
     */
    @Getter
    private final NextLevelContext nextLevelContext = new NextLevelContext();

    /**
     * 读取并缓存指定 NPC 的默认一句台词（无脚本或脚本未覆盖时使用）。
     *
     * @param npcid NPC 模板 ID
     * @return 默认对话文本，可能为 null
     */
    private String getDefaultTalk(int npcid) {
        String talk = npcDefaultTalks.get(npcid);
        if (talk == null) {
            talk = LifeFactory.getNPCDefaultTalk(npcid);
            npcDefaultTalks.put(npcid, talk);
        }

        return talk;
    }

    /**
     * 由脚本名构造的对话管理器（使用默认 OID -1）。
     *
     * @param c           当前客户端
     * @param npc         NPC 模板 ID
     * @param scriptName  脚本文件名
     */
    public NPCConversationManager(Client c, int npc, String scriptName) {
        this(c, npc, -1, scriptName, false);
    }

    /**
     * 组队等场景使用的构造：仅绑定 NPC 与对方队伍信息，常用于测试或特殊入口。
     *
     * @param c           当前客户端
     * @param npc         NPC 模板 ID
     * @param otherParty  对方队伍成员快照
     * @param test        历史参数，保留兼容
     */
    public NPCConversationManager(Client c, int npc, List<PartyCharacter> otherParty, boolean test) {
        super(c);
        this.c = c;
        this.npc = npc;
        this.otherParty = otherParty;
    }

    /**
     * 完整构造：绑定 NPC、地图对象 OID、脚本名及是否为物品脚本。
     *
     * @param c           当前客户端
     * @param npc         NPC 模板 ID
     * @param oid         地图上 NPC 实例 OID
     * @param scriptName  脚本名
     * @param itemScript  是否物品触发脚本
     */
    public NPCConversationManager(Client c, int npc, int oid, String scriptName, boolean itemScript) {
        super(c);
        this.npc = npc;
        this.npcOid = oid;
        this.scriptName = scriptName;
        this.itemScript = itemScript;
    }

    /** @return 当前 NPC 模板 ID */
    public int getNpc() {
        return npc;
    }

    /** @return 地图上 NPC 实例的对象 ID */
    public int getNpcObjectId() {
        return npcOid;
    }

    /** @return 当前 NPC 脚本文件名 */
    public String getScriptName() {
        return scriptName;
    }

    /** @return 是否为物品脚本对话 */
    public boolean isItemScript() {
        return itemScript;
    }

    /** 清除物品脚本标记（对话结束后恢复为普通 NPC 流程）。 */
    public void resetItemScript() {
        this.itemScript = false;
    }

    /**
     * 结束当前 NPC 对话：清空多级路由、通知 {@link NPCScriptManager} 释放本 CM，并向客户端解锁操作。
     */
    public void dispose() {
        nextLevelContext.clear();
        NPCScriptManager.getInstance().dispose(this);
        getClient().sendPacket(PacketCreator.enableActions());
    }

    /**
     * 发送仅含「下一步」按钮的 NPC 对话。
     *
     * @param text 支持 #b 等颜色码的正文
     */
    public void sendNext(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", (byte) 0));
    }

    /** 发送仅含「上一步」按钮的 NPC 对话。 */
    public void sendPrev(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", (byte) 0));
    }

    /** 发送同时含「上一步 / 下一步」的 NPC 对话。 */
    public void sendNextPrev(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", (byte) 0));
    }

    /** 发送仅含「确定」的 NPC 对话。 */
    public void sendOk(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", (byte) 0));
    }

    /** 发送当前 NPC 在 WZ 中的默认一句台词。 */
    public void sendDefault() {
        sendOk(getDefaultTalk(npc));
    }

    /** 发送「是 / 否」二选一对话。 */
    public void sendYesNo(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 1, text, "", (byte) 0));
    }

    /** 发送「接受 / 拒绝」类对话（ opcode 0x0C ）。 */
    public void sendAcceptDecline(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0x0C, text, "", (byte) 0));
    }

    /**
     * 发送简单列表选择对话（#L 选项 #l 由脚本拼在 text 中）。
     *
     * @param text 含选项标记的正文
     */
    public void sendSimple(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 4, text, "", (byte) 0));
    }

    /**
     * 发送带说话者头像的「下一步」对话。
     *
     * @param text    正文
     * @param speaker 说话者类型（0/1/8/9 常见为 NPC 等，与客户端表现相关）
     */
    public void sendNext(String text, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0, text, "00 01", speaker));
    }

    /** 带说话者头像的「上一步」对话。 */
    public void sendPrev(String text, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0, text, "01 00", speaker));
    }

    /** 带说话者头像的「上一步 / 下一步」对话。 */
    public void sendNextPrev(String text, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0, text, "01 01", speaker));
    }

    /** 带说话者头像的「确定」对话。 */
    public void sendOk(String text, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0, text, "00 00", speaker));
    }

    /** 带说话者头像的「是 / 否」对话。 */
    public void sendYesNo(String text, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 1, text, "", speaker));
    }

    /** 带说话者头像的「接受 / 拒绝」对话。 */
    public void sendAcceptDecline(String text, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 0x0C, text, "", speaker));
    }

    /** 带说话者头像的简单列表对话。 */
    public void sendSimple(String text, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalk(npc, (byte) 4, text, "", speaker));
    }

    /**
     * 发送美发/整形等样式选择界面；若 {@code styles} 为空则提示并结束对话，避免客户端崩溃。
     *
     * @param text   说明文字
     * @param styles 可选样式 ID 数组
     */
    public void sendStyle(String text, int[] styles) {
        if (styles.length > 0) {
            nextLevelContext.clear();
            getClient().sendPacket(PacketCreator.getNPCTalkStyle(npc, text, styles));
        } else {    // thanks Conrad for noticing empty styles crashing players
            sendOk("Sorry, there are no options of cosmetics available for you here at the moment.");
            dispose();
        }
    }

    /**
     * 弹出数字输入框（带默认与上下限）。
     *
     * @param text 提示语
     * @param def  默认值
     * @param min  最小可输入
     * @param max  最大可输入
     */
    public void sendGetNumber(String text, int def, int min, int max) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalkNum(npc, text, def, min, max));
    }

    /** 弹出单行文本输入框。 */
    public void sendGetText(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalkText(npc, text, ""));
    }

    /**
     * 带说话者头像的数字输入框。
     *
     * @param speaker 说话者类型
     */
    public void sendGetNumber(String text, int def, int min, int max, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalkNum(npc, text, def, min, max, speaker));
    }

    /** 带说话者头像的文本输入框。 */
    public void sendGetText(String text, byte speaker) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getNPCTalkText(npc, text, "", speaker));
    }
    /*
     * 0 = ariant colliseum
     * 1 = Dojo
     * 2 = Carnival 1
     * 3 = Carnival 2
     * 4 = Ghost Ship PQ?
     * 5 = Pyramid PQ
     * 6 = Kerning Subway
     */
    /**
     * 打开次元之镜选单 UI（传送门列表由 {@code text} 中脚本拼好）。
     *
     * @param text 客户端展示的选单正文
     */
    public void sendDimensionalMirror(String text) {
        nextLevelContext.clear();
        getClient().sendPacket(PacketCreator.getDimensionalMirror(text));
    }

    /** 由脚本侧缓存玩家最近一次在输入框中提交的文字。 */
    public void setGetText(String text) {
        this.getText = text;
    }

    /** @return 最近一次输入框提交的文字 */
    public String getText() {
        return this.getText;
    }

    /**
     * 以当前 NPC 为任务 NPC 强制开始任务。
     *
     * @param id 任务 ID
     */
    @Override
    public boolean forceStartQuest(int id) {
        return forceStartQuest(id, npc);
    }

    /**
     * 以当前 NPC 为任务 NPC 强制完成任务。
     *
     * @param id 任务 ID
     */
    @Override
    public boolean forceCompleteQuest(int id) {
        return forceCompleteQuest(id, npc);
    }

    /** @param id 任务 ID（short 重载） */
    @Override
    public boolean startQuest(short id) {
        return startQuest((int) id);
    }

    /** @param id 任务 ID（short 重载） */
    @Override
    public boolean completeQuest(short id) {
        return completeQuest((int) id);
    }

    /** 以当前 NPC 为任务 NPC 开始任务。 */
    @Override
    public boolean startQuest(int id) {
        return startQuest(id, npc);
    }

    /** 以当前 NPC 为任务 NPC 完成任务。 */
    @Override
    public boolean completeQuest(int id) {
        return completeQuest(id, npc);
    }

    /** @return 玩家当前持有金币 */
    public int getMeso() {
        return getPlayer().getMeso();
    }

    /** 为玩家增加或减少金币（负数即扣除）。 */
    public void gainMeso(int gain) {
        getPlayer().gainMeso(gain);
    }

    /** {@link #gainMeso(int)} 的 Double 重载，内部取整。 */
    public void gainMeso(Double gain) {
        getPlayer().gainMeso(gain.intValue());
    }

    /**
     * 为玩家增加经验（带默认显示与组队分享逻辑，与角色内实现一致）。
     *
     * @param gain 经验值
     */
    public void gainExp(int gain) {
        getPlayer().gainExp(gain, true, true);
    }

    /**
     * 在当前地图广播环境特效（如任务光效）。
     *
     * @param effect 特效资源名或路径标识
     */
    @Override
    public void showEffect(String effect) {
        getPlayer().getMap().broadcastMessage(PacketCreator.environmentChange(effect, 3));
    }

    /** 修改角色发型并刷新外观与属性包。 */
    public void setHair(int hair) {
        getPlayer().setHair(hair);
        getPlayer().updateSingleStat(Stat.HAIR, hair);
        getPlayer().equipChanged();
    }

    /** 修改角色脸型并刷新外观与属性包。 */
    public void setFace(int face) {
        getPlayer().setFace(face);
        getPlayer().updateSingleStat(Stat.FACE, face);
        getPlayer().equipChanged();
    }

    /**
     * 修改角色肤色（皮肤色枚举 ID）。
     *
     * @param color 肤色 ID，对应 {@link SkinColor#getById(int)}
     */
    public void setSkin(int color) {
        getPlayer().setSkinColor(SkinColor.getById(color));
        getPlayer().updateSingleStat(Stat.SKIN, color);
        getPlayer().equipChanged();
    }

    /**
     * @param itemid 物品模板 ID
     * @return 背包中该物品总数量
     */
    public int itemQuantity(int itemid) {
        return getPlayer().getInventory(ItemConstants.getInventoryType(itemid)).countById(itemid);
    }

    /** 向客户端打开公会排名展示界面（与当前 NPC 关联）。 */
    public void displayGuildRanks() {
        Guild.displayGuildRanks(getClient(), npc);
    }

    /**
     * 是否允许在当前规则下于指定地图生成玩家 NPC（等级、GM、配置等条件）。
     *
     * @param mapid 地图 ID
     */
    public boolean canSpawnPlayerNpc(int mapid) {
        Character chr = getPlayer();
        return !GameConfig.getServerBoolean("playernpc_auto_deploy") && chr.getLevel() >= chr.getMaxClassLevel() && !chr.isGM() && PlayerNPC.canSpawnPlayerNpc(chr.getName(), mapid);
    }

    /**
     * 在当前地图查找脚本 ID 匹配的玩家 NPC 实例。
     *
     * @param scriptId PlayerNPC 脚本 ID
     * @return 找到的对象或 null
     */
    public PlayerNPC getPlayerNPCByScriptid(int scriptId) {
        for (MapObject pnpcObj : getPlayer().getMap().getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER_NPC))) {
            PlayerNPC pn = (PlayerNPC) pnpcObj;

            if (pn.getScriptId() == scriptId) {
                return pn;
            }
        }

        return null;
    }

    /**
     * @return 玩家所在队伍，可能为 null
     */
    @Override
    public Party getParty() {
        return getPlayer().getParty();
    }

    /**
     * 重置指定地图上所有反应堆（机关）到初始状态。
     *
     * @param mapid 地图 ID
     */
    @Override
    public void resetMap(int mapid) {
        getClient().getChannelServer().getMapFactory().getMap(mapid).resetReactors();
    }

    /**
     * 为玩家当前携带的所有宠物增加亲密度（饱食度参数传 0）。
     *
     * @param tameness 亲密度增量
     */
    public void gainTameness(int tameness) {
        for (Pet pet : getPlayer().getPets()) {
            if (pet != null) {
                pet.gainTamenessFullness(getPlayer(), tameness, 0, 0);
            }
        }
    }

    /** @return 玩家角色名 */
    public String getName() {
        return getPlayer().getName();
    }

    /** @return 玩家性别 */
    public int getGender() {
        return getPlayer().getGender();
    }

    /** 按职业 ID 转职。 */
    public void changeJobById(int a) {
        getPlayer().changeJob(Job.getById(a));
    }

    /** 转职为指定 {@link Job}。 */
    public void changeJob(Job job) {
        getPlayer().changeJob(job);
    }

    /**
     * @param id 职业 ID
     * @return 职业显示名称
     */
    public String getJobName(int id) {
        return GameConstants.getJobName(id);
    }

    /**
     * @param itemId 物品 ID
     * @return 该物品触发的效果（Buff 等），无则来自 ItemInformationProvider
     */
    public StatEffect getItemEffect(int itemId) {
        return ItemInformationProvider.getInstance().getItemEffect(itemId);
    }

    /** 将玩家 AP 重置为可重新分配状态（具体规则在角色实现中）。 */
    public void resetStats() {
        getPlayer().resetStats();
    }

    /**
     * 打开指定商店 ID 的 NPC 商店；若数据库缺表项则回退到默认商店并打日志。
     *
     * @param id 商店 ID
     */
    public void openShopNPC(int id) {
        Shop shop = ShopFactory.getInstance().getShop(id);

        if (shop != null) {
            shop.sendShop(c);
        } else {    // check for missing shopids thanks to resinate
            log.warn("Shop ID: {} is missing from database.", id);
            ShopFactory.getInstance().getShop(11000).sendShop(c);
        }
    }

    /**
     * 遍历 String.wz 中 {@code Skill.img} 子节点，对玩家批量调用 {@link Character#changeSkillLevel}（脚本/GM 用技能批量处理）。
     */
    public void maxMastery() {
        for (Data skill_ : DataProviderFactory.getDataProvider(WZFiles.STRING).getData("Skill.img").getChildren()) {
            try {
                Skill skill = SkillFactory.getSkill(Integer.parseInt(skill_.getName()));
                getPlayer().changeSkillLevel(skill, (byte) 0, skill.getMaxLevel(), -1);
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
                break;
            } catch (NullPointerException npe) {
                npe.printStackTrace();
                continue;
            }
        }
    }

    /** 根据当前 NPC 与玩家执行扭蛋抽取逻辑（委托 {@link GachaponService}）。 */
    public void doGachapon() {
        gachaponService.doGachapon(getPlayer(), npc);
    }

    // public void doGachapon() {
    //     GachaponItem item = Gachapon.getInstance().process(npc);
    //     Item itemGained = gainItem(item.getId(), (short) (item.getId() / 10000 == 200 ? 100 : 1), true, true); // For normal potions, make it give 100.
    //
    //     sendNext("你获得了一个 #b#t" + item.getId() + "##k ！");
    //
    //     int[] maps = {MapId.HENESYS, MapId.ELLINIA, MapId.PERION, MapId.KERNING_CITY, MapId.SLEEPYWOOD, MapId.MUSHROOM_SHRINE,
    //             MapId.SHOWA_SPA_M, MapId.SHOWA_SPA_F, MapId.NEW_LEAF_CITY, MapId.NAUTILUS_HARBOR};
    //     final int mapId = maps[(getNpc() != NpcId.GACHAPON_NAUTILUS && getNpc() != NpcId.GACHAPON_NLC) ?
    //             (getNpc() - NpcId.GACHAPON_HENESYS) : getNpc() == NpcId.GACHAPON_NLC ? 8 : 9];
    //     String map = c.getChannelServer().getMapFactory().getMap(mapId).getMapName();
    //
    //     Gachapon.log(getPlayer(), item.getId(), map);
    //
    //     if (item.getTier() > 0) { //Uncommon and Rare
    //         Server.getInstance().broadcastMessage(c.getWorld(), PacketCreator.gachaponMessage(itemGained, map, getPlayer()));
    //     }
    // }

    /** 将玩家所在联盟容量 +1 并广播联盟信息更新。 */
    public void upgradeAlliance() {
        Alliance alliance = Server.getInstance().getAlliance(c.getPlayer().getGuild().getAllianceId());
        alliance.increaseCapacity(1);

        Server.getInstance().allianceMessage(alliance.getId(), GuildPackets.getGuildAlliances(alliance, c.getWorld()), -1, -1);
        Server.getInstance().allianceMessage(alliance.getId(), GuildPackets.allianceNotice(alliance.getId(), alliance.getNotice()), -1, -1);

        c.sendPacket(GuildPackets.updateAllianceInfo(alliance, c.getWorld()));  // thanks Vcoc for finding an alliance update to leader issue
    }

    /**
     * 解散指定联盟。
     *
     * @param c          发起客户端（部分实现可能忽略）
     * @param allianceId 联盟 ID
     */
    public void disbandAlliance(Client c, int allianceId) {
        Alliance.disbandAlliance(allianceId);
    }

    /**
     * @param name 联盟名称
     * @return 名称是否未被占用且合法
     */
    public boolean canBeUsedAllianceName(String name) {
        return Alliance.canBeUsedAllianceName(name);
    }

    /**
     * 以当前队伍创建新联盟。
     *
     * @param name 联盟名
     * @return 新建联盟对象
     */
    public Alliance createAlliance(String name) {
        return Alliance.createAlliance(getParty(), name);
    }

    /** @return 当前联盟最大成员容量 */
    public int getAllianceCapacity() {
        return Server.getInstance().getAlliance(getPlayer().getGuild().getAllianceId()).getCapacity();
    }

    /** @return 玩家是否已开设个人商店 */
    public boolean hasMerchant() {
        return getPlayer().hasMerchant();
    }

    /**
     * 玩家是否在商人仓库中仍存有未取回物品，或托管金币非零。
     *
     * @return 有托管货物或金币则 true
     */
    public boolean hasMerchantItems() {
        try {
            if (!ItemFactory.MERCHANT.loadItems(getPlayer().getId(), false).isEmpty()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return getPlayer().getMerchantMeso() != 0;
    }

    /** 打开弗雷德（商人取货）仓库界面。 */
    public void showFredrick() {
        c.sendPacket(PacketCreator.getFredrick(getPlayer()));
    }

    /** @return 与玩家同队且处于同一张地图的人数 */
    public int partyMembersInMap() {
        int inMap = 0;
        for (Character char2 : getPlayer().getMap().getCharacters()) {
            if (char2.getParty() == getPlayer().getParty()) {
                inMap++;
            }
        }
        return inMap;
    }

    /** @return 当前频道正在进行的 GM 活动事件实例，可能为 null */
    public Event getEvent() {
        return c.getChannelServer().getEvent();
    }

    /** 若频道存在活动事件，则按事件计数为玩家分配队伍颜色/阵营。 */
    public void divideTeams() {
        if (getEvent() != null) {
            getPlayer().setTeam(getEvent().getLimit() % 2); //muhaha :D
        }
    }

    /**
     * 按角色名从本频道玩家存储中查找在线角色。
     *
     * @param player 角色名
     * @return 在线 {@link Character}，未找到则为 null
     */
    public Character getMapleCharacter(String player) {
        Character target = Server.getInstance().getWorld(c.getWorld()).getChannel(c.getChannel()).getPlayerStorage().getCharacterByName(player);
        return target;
    }

    /**
     * 记录枫叶点数/抽奖类日志（{@code true} 表示获得类操作）。
     *
     * @param prize 奖品或操作描述
     */
    public void logLeaf(String prize) {
        MapleLeafLogger.log(getPlayer(), true, prize);
    }

    /**
     * 创建奈特的金字塔组队任务实例并将队伍传入起始地图序列。
     *
     * @param mode  金字塔模式枚举名，对应 {@link PyramidMode#valueOf(String)}
     * @param party 是否多人（影响基础地图 ID 偏移）
     * @return 成功创建并传送返回 true；若前置地图仍有人占位则 false
     */
    public boolean createPyramid(String mode, boolean party) {//lol
        PyramidMode mod = PyramidMode.valueOf(mode);

        Party partyz = getPlayer().getParty();
        MapManager mapManager = c.getChannelServer().getMapFactory();

        MapleMap map = null;
        int mapid = MapId.NETTS_PYRAMID_SOLO_BASE;
        if (party) {
            mapid += 10000;
        }
        mapid += (mod.getMode() * 1000);

        for (byte b = 0; b < 5; b++) {//They cannot warp to the next map before the timer ends (:
            map = mapManager.getMap(mapid + b);
            if (map.getCharacters().isEmpty()) {
                return false;
            }
        }

        if (!party) {
            // 修复单人组队金字塔空指针的问题
            PartyCharacter single = new PartyCharacter(getPlayer());
            partyz = new Party(-1, single);
            partyz.addMember(single);
        }
        Pyramid py = new Pyramid(partyz, mod, map.getId());
        getPlayer().setPartyQuest(py);
        py.warp(mapid);
        dispose();
        return true;
    }

    /**
     * @param itemid 物品模板 ID
     * @return 物品是否在数据中存在
     */
    public boolean itemExists(int itemid) {
        return ItemInformationProvider.getInstance().getName(itemid) != null;
    }

    /**
     * 解析美发/脸型类物品 ID：若原 ID 不存在则尝试回退到同系基础 ID。
     *
     * @param itemid 传入的物品 ID
     * @return 可用物品 ID，找不到则 -1
     */
    public int getCosmeticItem(int itemid) {
        if (itemExists(itemid)) {
            return itemid;
        }

        int baseid;
        if (itemid < 30000) {
            baseid = (itemid / 1000) * 1000 + (itemid % 100);
        } else {
            baseid = (itemid / 10) * 10;
        }

        return itemid != baseid && itemExists(baseid) ? baseid : -1;
    }

    /**
     * @param itemid 美发或脸型类物品 ID（小于 30000 按脸型处理，否则按发型）
     * @return 当前玩家已装备的脸或发对应的物品 ID
     */
    private int getEquippedCosmeticid(int itemid) {
        if (itemid < 30000) {
            return getPlayer().getFace();
        } else {
            return getPlayer().getHair();
        }
    }

    /**
     * @param itemid 美发/脸型物品 ID
     * @return 是否与当前玩家已装备的脸或发一致
     */
    public boolean isCosmeticEquipped(int itemid) {
        return getEquippedCosmeticid(itemid) == itemid;
    }

    /**
     * @return 是否启用旧版 PQ NPC 对话样式且玩家处于队伍中
     */
    public boolean isUsingOldPqNpcStyle() {
        return GameConfig.getServerBoolean("use_old_gms_styled_pq_npcs") && this.getPlayer().getParty() != null;
    }

    /** @return 当前玩家可使用的熟练度手册列表（转数组供脚本使用） */
    public Object[] getAvailableMasteryBooks() {
        return ItemInformationProvider.getInstance().usableMasteryBooks(this.getPlayer()).toArray();
    }

    /** @return 可用技能书与可传授技能合并后的数组 */
    public Object[] getAvailableSkillBooks() {
        List<Integer> ret = ItemInformationProvider.getInstance().usableSkillBooks(this.getPlayer());
        ret.addAll(SkillbookInformationProvider.getTeachableSkills(this.getPlayer()));

        return ret.toArray();
    }

    /**
     * @param itemId 物品模板 ID
     * @return 会掉落该物品的非玩家怪物名称数组（供脚本展示）
     */
    public Object[] getNamesWhoDropsItem(Integer itemId) {
        return ItemInformationProvider.getInstance().getWhoDrops(itemId).toArray();
    }

    /**
     * 根据技能书在数据中的可获得方式返回一段英文提示（含颜色码），供 NPC 文本拼接。
     *
     * @param itemid 技能书物品 ID
     * @return 描述字符串，不可获得时为空串
     */
    public String getSkillBookInfo(int itemid) {
        SkillBookEntry sbe = SkillbookInformationProvider.getSkillbookAvailability(itemid);
        switch (sbe) {
            case UNAVAILABLE:
                return "";

            case REACTOR:
                return "    Obtainable through #rexploring#k (loot boxes).";

            case SCRIPT:
                return "    Obtainable through #rexploring#k (field interaction).";

            case QUEST_BOOK:
                return "    Obtainable through #rquestline#k (collecting book).";

            case QUEST_REWARD:
                return "    Obtainable through #rquestline#k (quest reward).";

            default:
                return "    Obtainable through #rquestline#k.";
        }
    }

    // (CPQ + WED wishlist) by -- Drago (Dragohe4rt)
    /**
     * 计算怪物嘉年华（CPQ）某房间地图上玩家的平均等级。
     *
     * @param map 地图 ID（通常为 980000100 系列）
     */
    public int cpqCalcAvgLvl(int map) {
        int num = 0;
        int avg = 0;
        for (MapObject mmo : c.getChannelServer().getMapFactory().getMap(map).getAllPlayer()) {
            avg += ((Character) mmo).getLevel();
            num++;
        }
        avg /= num;
        return avg;
    }

    /**
     * 向玩家发送 CPQ（怪物嘉年华第 1 套地图组）可选场地列表；若无可用场地则返回 false。
     *
     * @return 已发送简单列表对话返回 true，否则 false
     */
    public boolean sendCPQMapLists() {
        String msg = LanguageConstants.getMessage(getPlayer(), LanguageConstants.CPQPickRoom);
        int msgLen = msg.length();
        for (int i = 0; i < 6; i++) {
            if (fieldTaken(i)) {
                if (fieldLobbied(i)) {
                    msg += "#b#L" + i + "#Carnival Field " + (i + 1) + " (Level: "  // "Carnival field" GMS-like improvement thanks to Jayd (jaydenseah)
                            + cpqCalcAvgLvl(980000100 + i * 100) + " / "
                            + getPlayerCount(980000100 + i * 100) + "x"
                            + getPlayerCount(980000100 + i * 100) + ")  #l\r\n";
                }
            } else {
                if (i >= 0 && i <= 3) {
                    msg += "#b#L" + i + "#Carnival Field " + (i + 1) + " (2x2) #l\r\n";
                } else {
                    msg += "#b#L" + i + "#Carnival Field " + (i + 1) + " (3x3) #l\r\n";
                }
            }
        }

        if (msg.length() > msgLen) {
            sendSimple(msg);
            return true;
        } else {
            return false;
        }
    }

    /**
     * CPQ 场地 {@code field} 是否已被占用（嘉年华初始化失败或三张对战图任一有人）。
     *
     * @param field 场地索引 0–5
     */
    public boolean fieldTaken(int field) {
        if (!c.getChannelServer().canInitMonsterCarnival(true, field)) {
            return true;
        }
        if (!c.getChannelServer().getMapFactory().getMap(980000100 + field * 100).getAllPlayer().isEmpty()) {
            return true;
        }
        if (!c.getChannelServer().getMapFactory().getMap(980000101 + field * 100).getAllPlayer().isEmpty()) {
            return true;
        }
        return !c.getChannelServer().getMapFactory().getMap(980000102 + field * 100).getAllPlayer().isEmpty();
    }

    /**
     * 该场地大厅地图是否已有玩家（用于列表中展示「进行中」信息）。
     */
    public boolean fieldLobbied(int field) {
        return !c.getChannelServer().getMapFactory().getMap(980000100 + field * 100).getAllPlayer().isEmpty();
    }

    /**
     * 将本队成员传入 CPQ 大厅地图，启动倒计时与超时踢回出口图。
     *
     * @param field 场地索引
     */
    public void cpqLobby(int field) {
        try {
            final MapleMap map, mapExit;
            Channel cs = c.getChannelServer();

            map = cs.getMapFactory().getMap(980000100 + 100 * field);
            mapExit = cs.getMapFactory().getMap(980000000);
            for (PartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                final Character mc = mpc.getPlayer();
                if (mc != null) {
                    mc.setChallenged(false);
                    mc.changeMap(map, map.getPortal(0));
                    mc.sendPacket(PacketCreator.serverNotice(6, LanguageConstants.getMessage(mc, LanguageConstants.CPQEntryLobby)));
                    TimerManager tMan = TimerManager.getInstance();
                    tMan.schedule(() -> mapClock((int) MINUTES.toSeconds(3)), 1500);

                    mc.setCpqTimer(TimerManager.getInstance().schedule(() -> mc.changeMap(mapExit, mapExit.getPortal(0)), MINUTES.toMillis(3)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * @param id 角色 ID
     * @return 本频道在线角色，或 null
     */
    public Character getChrById(int id) {
        return c.getChannelServer().getPlayerStorage().getCharacterById(id);
    }

    /** 清除本队所有成员身上的 CPQ 大厅倒计时任务。 */
    public void cancelCPQLobby() {
        for (PartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
            Character mc = mpc.getPlayer();
            if (mc != null) {
                mc.clearCpqTimer();
            }
        }
    }

    /**
     * 将仍在 CPQ 大厅内的玩家全部传送出口并重置 CP 相关状态。
     *
     * @param lobbyMap 大厅地图实例
     */
    private void warpoutCPQLobby(MapleMap lobbyMap) {
        MapleMap out = lobbyMap.getChannelServer().getMapFactory().getMap((lobbyMap.getId() < 980030000) ? 980000000 : 980030000);
        for (Character mc : lobbyMap.getAllPlayers()) {
            mc.resetCP();
            mc.setTeam(-1);
            mc.setMonsterCarnival(null);
            mc.changeMap(out, out.getPortal(0));
        }
    }

    /**
     * 校验队伍成员等级与是否均在大厅地图内。
     *
     * @return 0 正常；1 有成员不在大厅；2 有成员等级不符合该大厅段
     */
    private int isCPQParty(MapleMap lobby, Party party) {
        int cpqMinLvl, cpqMaxLvl;

        if (lobby.isCPQLobby()) {
            cpqMinLvl = 30;
            cpqMaxLvl = 50;
        } else {
            cpqMinLvl = 51;
            cpqMaxLvl = 70;
        }

        List<PartyCharacter> partyMembers = party.getPartyMembers();
        for (PartyCharacter pchr : partyMembers) {
            if (pchr.getLevel() >= cpqMinLvl && pchr.getLevel() <= cpqMaxLvl) {
                if (lobby.getCharacterById(pchr.getId()) == null) {
                    return 1;  // party member detected out of area
                }
            } else {
                return 2;  // party member doesn't fit requirements
            }
        }

        return 0;
    }

    /**
     * 校验主场队伍与挑战方队伍是否均满足开局条件。
     *
     * @return 0 可开局；正/负非零表示某方不满足（与 {@link #isCPQParty} 编码一致）
     */
    private int canStartCPQ(MapleMap lobby, Party party, Party challenger) {
        int ret = isCPQParty(lobby, party);
        if (ret != 0) {
            return ret;
        }

        ret = isCPQParty(lobby, challenger);
        if (ret != 0) {
            return -ret;
        }

        return 0;
    }

    /**
     * 怪物嘉年华（第一套地图组）：将挑战方拉入大厅，倒计时后创建 {@link MonsterCarnival} 实例。
     *
     * @param challenger 对方队长所在角色
     * @param field      与地图 ID 偏移相关的场地参数
     */
    public void startCPQ(final Character challenger, final int field) {
        try {
            cancelCPQLobby();

            final MapleMap lobbyMap = getPlayer().getMap();
            if (challenger != null) {
                if (challenger.getParty() == null) {
                    throw new RuntimeException("No opponent found!");
                }

                for (PartyCharacter mpc : challenger.getParty().getMembers()) {
                    Character mc = mpc.getPlayer();
                    if (mc != null) {
                        mc.changeMap(lobbyMap, lobbyMap.getPortal(0));
                        TimerManager tMan = TimerManager.getInstance();
                        tMan.schedule(() -> mapClock(10), 1500);
                    }
                }
                for (PartyCharacter mpc : getPlayer().getParty().getMembers()) {
                    Character mc = mpc.getPlayer();
                    if (mc != null) {
                        TimerManager tMan = TimerManager.getInstance();
                        tMan.schedule(() -> mapClock(10), 1500);
                    }
                }
            }
            final int mapid = c.getPlayer().getMapId() + 1;
            TimerManager tMan = TimerManager.getInstance();
            tMan.schedule(() -> {
                try {
                    for (PartyCharacter mpc : getPlayer().getParty().getMembers()) {
                        Character mc = mpc.getPlayer();
                        if (mc != null) {
                            mc.setMonsterCarnival(null);
                        }
                    }
                    for (PartyCharacter mpc : challenger.getParty().getMembers()) {
                        Character mc = mpc.getPlayer();
                        if (mc != null) {
                            mc.setMonsterCarnival(null);
                        }
                    }
                } catch (NullPointerException npe) {
                    warpoutCPQLobby(lobbyMap);
                    return;
                }

                Party lobbyParty = getPlayer().getParty(), challengerParty = challenger.getParty();
                int status = canStartCPQ(lobbyMap, lobbyParty, challengerParty);
                if (status == 0) {
                    new MonsterCarnival(lobbyParty, challengerParty, mapid, true, (field / 100) % 10);
                } else {
                    warpoutCPQLobby(lobbyMap);
                }
            }, 11000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 怪物嘉年华第二套地图组（980031xxx）：逻辑与 {@link #startCPQ} 类似，使用不同 mapid 偏移与 {@code MonsterCarnival} 构造参数。
     */
    public void startCPQ2(final Character challenger, final int field) {
        try {
            cancelCPQLobby();

            final MapleMap lobbyMap = getPlayer().getMap();
            if (challenger != null) {
                if (challenger.getParty() == null) {
                    throw new RuntimeException("No opponent found!");
                }

                for (PartyCharacter mpc : challenger.getParty().getMembers()) {
                    Character mc = mpc.getPlayer();
                    if (mc != null) {
                        mc.changeMap(lobbyMap, lobbyMap.getPortal(0));
                        mapClock(10);
                    }
                }
            }
            final int mapid = c.getPlayer().getMapId() + 100;
            TimerManager tMan = TimerManager.getInstance();
            tMan.schedule(() -> {
                try {
                    for (PartyCharacter mpc : getPlayer().getParty().getMembers()) {
                        Character mc = mpc.getPlayer();
                        if (mc != null) {
                            mc.setMonsterCarnival(null);
                        }
                    }
                    for (PartyCharacter mpc : challenger.getParty().getMembers()) {
                        Character mc = mpc.getPlayer();
                        if (mc != null) {
                            mc.setMonsterCarnival(null);
                        }
                    }
                } catch (NullPointerException npe) {
                    warpoutCPQLobby(lobbyMap);
                    return;
                }

                Party lobbyParty = getPlayer().getParty(), challengerParty = challenger.getParty();
                int status = canStartCPQ(lobbyMap, lobbyParty, challengerParty);
                if (status == 0) {
                    new MonsterCarnival(lobbyParty, challengerParty, mapid, false, (field / 1000) % 10);
                } else {
                    warpoutCPQLobby(lobbyMap);
                }
            }, 10000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * CPQ 第二套地图组的可选场地列表（3 个场），有可选项时发送 {@link #sendSimple}。
     *
     * @return 是否成功发出列表
     */
    public boolean sendCPQMapLists2() {
        String msg = LanguageConstants.getMessage(getPlayer(), LanguageConstants.CPQPickRoom);
        int msgLen = msg.length();
        for (int i = 0; i < 3; i++) {
            if (fieldTaken2(i)) {
                if (fieldLobbied2(i)) {
                    msg += "#b#L" + i + "#Carnival Field " + (i + 1) + " (Level: "  // "Carnival field" GMS-like improvement thanks to Jayd
                            + cpqCalcAvgLvl(980031000 + i * 1000) + " / "
                            + getPlayerCount(980031000 + i * 1000) + "x"
                            + getPlayerCount(980031000 + i * 1000) + ")  #l\r\n";
                }
            } else {
                if (i == 0 || i == 1) {
                    msg += "#b#L" + i + "#Carnival Field " + (i + 1) + " (2x2) #l\r\n";
                } else {
                    msg += "#b#L" + i + "#Carnival Field " + (i + 1) + " (3x3) #l\r\n";
                }
            }
        }

        if (msg.length() > msgLen) {
            sendSimple(msg);
            return true;
        } else {
            return false;
        }
    }

    /** 第二套 CPQ 场地是否被占用（三张对战图检测）。 */
    public boolean fieldTaken2(int field) {
        if (!c.getChannelServer().canInitMonsterCarnival(false, field)) {
            return true;
        }
        if (!c.getChannelServer().getMapFactory().getMap(980031000 + field * 1000).getAllPlayer().isEmpty()) {
            return true;
        }
        if (!c.getChannelServer().getMapFactory().getMap(980031100 + field * 1000).getAllPlayer().isEmpty()) {
            return true;
        }
        return !c.getChannelServer().getMapFactory().getMap(980031200 + field * 1000).getAllPlayer().isEmpty();
    }

    /** 第二套 CPQ 大厅是否已有玩家。 */
    public boolean fieldLobbied2(int field) {
        return !c.getChannelServer().getMapFactory().getMap(980031000 + field * 1000).getAllPlayer().isEmpty();
    }

    /** 将队伍传入第二套 CPQ 大厅并启动倒计时与踢回出口。 */
    public void cpqLobby2(int field) {
        try {
            final MapleMap map, mapExit;
            Channel cs = c.getChannelServer();

            mapExit = cs.getMapFactory().getMap(980030000);
            map = cs.getMapFactory().getMap(980031000 + 1000 * field);
            for (PartyCharacter mpc : c.getPlayer().getParty().getMembers()) {
                final Character mc = mpc.getPlayer();
                if (mc != null) {
                    mc.setChallenged(false);
                    mc.changeMap(map, map.getPortal(0));
                    mc.sendPacket(PacketCreator.serverNotice(6, LanguageConstants.getMessage(mc, LanguageConstants.CPQEntryLobby)));
                    TimerManager tMan = TimerManager.getInstance();
                    tMan.schedule(() -> mapClock((int) MINUTES.toSeconds(3)), 1500);

                    mc.setCpqTimer(TimerManager.getInstance().schedule(() -> mc.changeMap(mapExit, mapExit.getPortal(0)), MINUTES.toMillis(3)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 在当前地图广播倒计时秒数 UI。
     *
     * @param time 秒数
     */
    public void mapClock(int time) {
        getPlayer().getMap().broadcastMessage(PacketCreator.getClock(time));
    }

    /**
     * 向世界匹配协调器发起 CPQ 挑战确认（双方队长 ID）。
     *
     * @param cpqType  {@code cpq1} 或 {@code cpq2} 等类型标识
     * @param leaderid 对方队长角色 ID
     * @return 是否成功创建待确认会话
     */
    private boolean sendCPQChallenge(String cpqType, int leaderid) {
        Set<Integer> cpqLeaders = new HashSet<>();
        cpqLeaders.add(leaderid);
        cpqLeaders.add(getPlayer().getId());

        return c.getWorldServer().getMatchCheckerCoordinator().createMatchConfirmation(MatchCheckerType.CPQ_CHALLENGE, c.getWorld(), getPlayer().getId(), cpqLeaders, cpqType);
    }

    /** 玩家响应 CPQ 挑战弹窗（接受/拒绝）。 */
    public void answerCPQChallenge(boolean accept) {
        c.getWorldServer().getMatchCheckerCoordinator().answerMatchConfirmation(getPlayer().getId(), accept);
    }

    /**
     * 在第二套 CPQ 场地上查找对方队长并发起挑战匹配。
     *
     * @param field 场地索引
     */
    public void challengeParty2(int field) {
        Character leader = null;
        MapleMap map = c.getChannelServer().getMapFactory().getMap(980031000 + 1000 * field);
        for (MapObject mmo : map.getAllPlayer()) {
            Character mc = (Character) mmo;
            if (mc.getParty() == null) {
                sendOk(LanguageConstants.getMessage(mc, LanguageConstants.CPQFindError));
                return;
            }
            if (mc.getParty().getLeader().getId() == mc.getId()) {
                leader = mc;
                break;
            }
        }
        if (leader != null) {
            if (!leader.isChallenged()) {
                if (!sendCPQChallenge("cpq2", leader.getId())) {
                    sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQChallengeRoomAnswer));
                }
            } else {
                sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQChallengeRoomAnswer));
            }
        } else {
            sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQLeaderNotFound));
        }
    }

    /**
     * 在第一套 CPQ 场地上查找对方队长并发起挑战（需对方队伍人数与地图内玩家一致）。
     *
     * @param field 场地索引
     */
    public void challengeParty(int field) {
        Character leader = null;
        MapleMap map = c.getChannelServer().getMapFactory().getMap(980000100 + 100 * field);
        if (map.getAllPlayer().size() != getPlayer().getParty().getMembers().size()) {
            sendOk("An unexpected error regarding the other party has occurred.");
            return;
        }
        for (MapObject mmo : map.getAllPlayer()) {
            Character mc = (Character) mmo;
            if (mc.getParty() == null) {
                sendOk(LanguageConstants.getMessage(mc, LanguageConstants.CPQFindError));
                return;
            }
            if (mc.getParty().getLeader().getId() == mc.getId()) {
                leader = mc;
                break;
            }
        }
        if (leader != null) {
            if (!leader.isChallenged()) {
                if (!sendCPQChallenge("cpq1", leader.getId())) {
                    sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQChallengeRoomAnswer));
                }
            } else {
                sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQChallengeRoomAnswer));
            }
        } else {
            sendOk(LanguageConstants.getMessage(leader, LanguageConstants.CPQLeaderNotFound));
        }
    }

    /**
     * 若竞技场地图为空则创建 {@link AriantColiseum} 实例。
     *
     * @param exped 远征队实例
     * @param mapid 竞技场入口地图 ID（实际竞技场为 {@code mapid + 1}）
     * @return 地图已被占用则 false
     */
    private synchronized boolean setupAriantBattle(Expedition exped, int mapid) {
        MapleMap arenaMap = this.getMap().getChannelServer().getMapFactory().getMap(mapid + 1);
        if (!arenaMap.getAllPlayers().isEmpty()) {
            return false;
        }

        new AriantColiseum(arenaMap, exped);
        return true;
    }

    /**
     * 尝试从当前远征队开启阿里安特竞技场战斗；失败时返回英文提示字符串，成功返回空串。
     *
     * @param expedType 远征类型（决定等级段等）
     * @param mapid     竞技场入口地图 ID
     * @return 空串表示成功；非空为客户端可直接展示的失败原因（英文）
     */
    public String startAriantBattle(ExpeditionType expedType, int mapid) {
        if (!GameConstants.isAriantColiseumLobby(mapid)) {
            return "You cannot start an Ariant tournament from outside the Battle Arena Entrance.";
        }

        Expedition exped = this.getMap().getChannelServer().getExpedition(expedType);
        if (exped == null) {
            return "Please register on an expedition before attempting to start an Ariant tournament.";
        }

        List<Character> players = exped.getActiveMembers();

        int playersSize = players.size();
        if (!(playersSize >= exped.getMinSize() && playersSize <= exped.getMaxSize())) {
            return "Make sure there are between #r" + exped.getMinSize() + " ~ " + exped.getMaxSize() + " players#k in this room to start the battle.";
        }

        MapleMap leaderMap = this.getMap();
        for (Character mc : players) {
            if (mc.getMap() != leaderMap) {
                return "All competing players should be on this area to start the battle.";
            }

            if (mc.getParty() != null) {
                return "All competing players must not be on a party to start the battle.";
            }

            int level = mc.getLevel();
            if (!(level >= expedType.getMinLevel() && level <= expedType.getMaxLevel())) {
                return "There are competing players outside of the acceptable level range in this room. All players must be on #blevel between 20~30#k to start the battle.";
            }
        }

        if (setupAriantBattle(exped, mapid)) {
            return "";
        } else {
            return "Other players are already competing on the Ariant tournament in this room. Please wait a while until the arena becomes available again.";
        }
    }

    /**
     * 向玩家下发婚礼心愿单或礼物列表封包（根据是否为新郎及是否本人查看）。
     *
     * @param groom true 表示新郎侧心愿单
     */
    public void sendMarriageWishlist(boolean groom) {
        Character player = this.getPlayer();
        Marriage marriage = player.getMarriageInstance();
        if (marriage != null) {
            int cid = marriage.getIntProperty(groom ? "groomId" : "brideId");
            Character chr = marriage.getPlayerById(cid);
            if (chr != null) {
                if (chr.getId() == player.getId()) {
                    player.sendPacket(WeddingPackets.onWeddingGiftResult((byte) 0xA, marriage.getWishlistItems(groom), marriage.getGiftItems(player.getClient(), groom)));
                } else {
                    marriage.setIntProperty("wishlistSelection", groom ? 0 : 1);
                    player.sendPacket(WeddingPackets.onWeddingGiftResult((byte) 0x09, marriage.getWishlistItems(groom), marriage.getGiftItems(player.getClient(), groom)));
                }
            }
        }
    }

    /**
     * 将指定礼物列表以婚礼封包形式发给当前玩家。
     *
     * @param gifts 礼物物品列表
     */
    public void sendMarriageGifts(List<Item> gifts) {
        this.getPlayer().sendPacket(WeddingPackets.onWeddingGiftResult((byte) 0xA, Collections.singletonList(""), gifts));
    }

    /**
     * 若婚礼实例中尚未填写对应方心愿单，则打开客户端心愿单编辑 UI。
     *
     * @return 已弹出心愿单界面返回 true；否则 false
     */
    public boolean createMarriageWishlist() {
        Marriage marriage = this.getPlayer().getMarriageInstance();
        if (marriage != null) {
            Boolean groom = marriage.isMarriageGroom(this.getPlayer());
            if (groom != null) {
                String wlKey;
                if (groom) {
                    wlKey = "groomWishlist";
                } else {
                    wlKey = "brideWishlist";
                }

                if (marriage.getProperty(wlKey).contentEquals("")) {
                    getClient().sendPacket(WeddingPackets.sendWishList());
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 只有下一步的对话
     * 对应sendNext
     *
     * @param nextLevel 下一步方法
     * @param text      对话内容
     */
    public void sendNextLevel(String nextLevel, String text) {
        sendNext(text);
        nextLevelContext.setLevelType(NextLevelType.SEND_NEXT);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 只有上一步的对话
     * 对应sendPrev
     *
     * @param lastLevel 上一步方法
     * @param text      对话内容
     */
    public void sendLastLevel(String lastLevel, String text) {
        sendPrev(text);
        nextLevelContext.setLevelType(NextLevelType.SEND_LAST);
        nextLevelContext.setLastLevel(lastLevel);
    }

    /**
     * 有上一步和下一步的对话
     * 对应sendNextPrev
     *
     * @param lastLevel 上一步方法
     * @param nextLevel 下一步方法
     * @param text      对话内容
     */
    public void sendLastNextLevel(String lastLevel, String nextLevel, String text) {
        sendNextPrev(text);
        nextLevelContext.setLevelType(NextLevelType.SEND_LAST_NEXT);
        nextLevelContext.setLastLevel(lastLevel);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 只有ok按钮的对话
     * 对应sendOk
     *
     * @param nextLevel 点击ok的下一步方法
     * @param text      对话内容
     */
    public void sendOkLevel(String nextLevel, String text) {
        sendOk(text);
        nextLevelContext.setLevelType(NextLevelType.SEND_OK);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 多个选项的对话，选择后自动路由到level + selection对应的方法
     * 对应sendSimple
     *
     * @param text 对话内容
     */
    public void sendSelectLevel(String text) {
        sendSelectLevel("", text);
    }

    /**
     * 多个选项的对话，选择后自动路由到level + prefix + selection对应的方法
     * 对应sendSimple
     *
     * @param prefix 方法前缀，如果脚本有多次要选择的地方，可以通过不同的前缀区分
     * @param text   对话内容
     */
    public void sendSelectLevel(String prefix, String text) {
        sendSimple(text);
        nextLevelContext.setLevelType(NextLevelType.SEND_SELECT);
        nextLevelContext.setPrefix(prefix);
    }

    /**
     * 多个选项的对话，选择后路由到指定方法，将玩家的选择传入
     * 对应sendSimple
     *
     * @param nextLevel 方法前缀，如果脚本有多次要选择的地方，可以通过不同的前缀区分
     * @param text   对话内容
     */
    public void sendNextSelectLevel(String nextLevel, String text) {
        sendSimple(text);
        nextLevelContext.setLevelType(NextLevelType.SEND_NEXT_SELECT);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 获取玩家输入数字的对话
     * 对应sendGetNumber
     *
     * @param nextLevel 下一步方法
     * @param text      对话内容
     * @param def       默认值
     * @param min       最小值
     * @param max       最大值
     */
    public void getInputNumberLevel(String nextLevel, String text, int def, int min, int max) {
        sendGetNumber(text, def, min, max);
        nextLevelContext.setLevelType(NextLevelType.GET_INPUT_NUMBER);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 获取玩家输入字符串的对话
     * 对应sendGetText
     *
     * @param nextLevel 下一步方法
     * @param text      对话内容
     */
    public void getInputTextLevel(String nextLevel, String text) {
        sendGetText(text);
        nextLevelContext.setLevelType(NextLevelType.GET_INPUT_TEXT);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 有接受和拒绝的对话
     * 对应sendAcceptDecline
     *
     * @param decLineLevel 拒绝方法
     * @param acceptLevel  接受方法
     * @param text         对话内容
     */
    public void sendAcceptDeclineLevel(String decLineLevel, String acceptLevel, String text) {
        sendAcceptDecline(text);
        nextLevelContext.setLevelType(NextLevelType.SEND_ACCEPT_DECLINE);
        nextLevelContext.setLastLevel(decLineLevel);
        nextLevelContext.setNextLevel(acceptLevel);
    }

    /**
     * 有是和否的对话
     * 对应sendYesNo
     *
     * @param noLevel  否方法
     * @param yesLevel 是方法
     * @param text     对话内容
     */
    public void sendYesNoLevel(String noLevel, String yesLevel, String text) {
        sendYesNo(text);
        nextLevelContext.setLevelType(NextLevelType.SEND_YES_NO);
        nextLevelContext.setLastLevel(noLevel);
        nextLevelContext.setNextLevel(yesLevel);
    }

    /**
     * 只有下一步的对话
     * 对应sendNext
     *
     * @param nextLevel 下一步方法
     * @param text      对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendNextLevel(String nextLevel, String text, byte speaker) {
        sendNext(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.SEND_NEXT);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 只有上一步的对话
     * 对应sendPrev
     *
     * @param lastLevel 上一步方法
     * @param text      对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendLastLevel(String lastLevel, String text, byte speaker) {
        sendPrev(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.SEND_LAST);
        nextLevelContext.setLastLevel(lastLevel);
    }

    /**
     * 有上一步和下一步的对话
     * 对应sendNextPrev
     *
     * @param lastLevel 上一步方法
     * @param nextLevel 下一步方法
     * @param text      对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendLastNextLevel(String lastLevel, String nextLevel, String text, byte speaker) {
        sendNextPrev(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.SEND_LAST_NEXT);
        nextLevelContext.setLastLevel(lastLevel);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 只有ok按钮的对话
     * 对应sendOk
     *
     * @param nextLevel 点击ok的下一步方法
     * @param text      对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendOkLevel(String nextLevel, String text, byte speaker) {
        sendOk(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.SEND_OK);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 多个选项的对话，选择后自动路由到level + selection对应的方法
     * 对应sendSimple
     *
     * @param text 对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendSelectLevel(String text, byte speaker) {
        sendSelectLevel("", text, speaker);
    }

    /**
     * 多个选项的对话，选择后自动路由到level + prefix + selection对应的方法
     * 对应sendSimple
     *
     * @param prefix 方法前缀，如果脚本有多次要选择的地方，可以通过不同的前缀区分
     * @param text   对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendSelectLevel(String prefix, String text, byte speaker) {
        sendSimple(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.SEND_SELECT);
        nextLevelContext.setPrefix(prefix);
    }

    /**
     * 多个选项的对话，选择后路由到指定方法，将玩家的选择传入
     * 对应sendSimple
     *
     * @param nextLevel 方法前缀，如果脚本有多次要选择的地方，可以通过不同的前缀区分
     * @param text   对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendNextSelectLevel(String nextLevel, String text, byte speaker) {
        sendSimple(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.SEND_NEXT_SELECT);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 获取玩家输入数字的对话
     * 对应sendGetNumber
     *
     * @param nextLevel 下一步方法
     * @param text      对话内容
     * @param def       默认值
     * @param min       最小值
     * @param max       最大值
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void getPnpcInputNumberLevel(String nextLevel, String text, int def, int min, int max, byte speaker) {
        sendGetNumber(text, def, min, max,speaker);
        nextLevelContext.setLevelType(NextLevelType.GET_INPUT_NUMBER);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 获取玩家输入字符串的对话
     * 对应sendGetText
     *
     * @param nextLevel 下一步方法
     * @param text      对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void getPnpcInputTextLevel(String nextLevel, String text, byte speaker) {
        sendGetText(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.GET_INPUT_TEXT);
        nextLevelContext.setNextLevel(nextLevel);
    }

    /**
     * 有接受和拒绝的对话
     * 对应sendAcceptDecline
     *
     * @param decLineLevel 拒绝方法
     * @param acceptLevel  接受方法
     * @param text         对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendAcceptDeclineLevel(String decLineLevel, String acceptLevel, String text, byte speaker) {
        sendAcceptDecline(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.SEND_ACCEPT_DECLINE);
        nextLevelContext.setLastLevel(decLineLevel);
        nextLevelContext.setNextLevel(acceptLevel);
    }

    /**
     * 有是和否的对话
     * 对应sendYesNo
     *
     * @param noLevel  否方法
     * @param yesLevel 是方法
     * @param text     对话内容
     * @param speaker   说话者，0,1,8,9 = NPC；2,3 = 玩家；4,5,6,7 = 客户端报38错误；其它数字未测试。
     */
    public void sendYesNoLevel(String noLevel, String yesLevel, String text, byte speaker) {
        sendYesNo(text, speaker);
        nextLevelContext.setLevelType(NextLevelType.SEND_YES_NO);
        nextLevelContext.setLastLevel(noLevel);
        nextLevelContext.setNextLevel(yesLevel);
    }
}