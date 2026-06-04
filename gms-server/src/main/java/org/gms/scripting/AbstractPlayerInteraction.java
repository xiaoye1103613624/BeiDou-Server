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
package org.gms.scripting;

import org.gms.client.Character;
import org.gms.client.*;
import org.gms.client.inventory.*;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.config.GameConfig;
import org.gms.constants.game.DelayedQuestUpdate;
import org.gms.constants.game.GameConstants;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.constants.id.NpcId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.constants.string.ExtendType;
import org.gms.dao.entity.ExtendValueDO;
import org.gms.model.pojo.SkillEntry;
import org.gms.net.server.Server;
import org.gms.net.server.guild.Guild;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.scripting.event.EventManager;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.server.ItemInformationProvider;
import org.gms.server.Marriage;
import org.gms.server.expeditions.Expedition;
import org.gms.server.expeditions.ExpeditionBossLog;
import org.gms.server.expeditions.ExpeditionType;
import org.gms.server.life.*;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.server.partyquest.PartyQuest;
import org.gms.server.partyquest.Pyramid;
import org.gms.server.quest.Quest;
import org.gms.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.List;
import java.util.*;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 【类型】AbstractPlayerInteraction（class），包 `org.gms.scripting`。
 *
 * 脚本系统与玩家交互的抽象基类，为 JS 脚本（NPC、任务、传送门、事件、物品等）提供统一的游戏 API。
 * 在脚本中通过 {@code cm} 或 {@code im} 变量访问，封装了以下能力：
 * <ul>
 *   <li>玩家属性：等级、职业、地图、背包、任务</li>
 *   <li>物品操作：给予/移除/检查物品，装备强化</li>
 *   <li>地图操作：传送、刷怪、地图重置</li>
 *   <li>社交系统：组队、公会、远征队、婚礼</li>
 *   <li>UI 交互：对话框、引导、特效、音效</li>
 *   <li>持久化扩展：角色/账号级别的自定义键值存储（extend_value 表）</li>
 * </ul>
 *
 * 子类包括 {@link org.gms.scripting.npc.NPCConversationManager}（NPC 对话）、
 * {@link org.gms.scripting.quest.QuestActionManager}（任务脚本）、
 * {@link org.gms.scripting.event.EventManager}（事件脚本）等。
 */
public class AbstractPlayerInteraction {

    private static final Logger log = LoggerFactory.getLogger(AbstractPlayerInteraction.class);

    /** 当前脚本交互绑定的客户端连接 */
    public Client c;

    public AbstractPlayerInteraction(Client c) {
        this.c = c;
    }

    /** @return 当前绑定的客户端连接 */
    public Client getClient() {
        return c;
    }

    /** @return 玩家角色对象 */
    public Character getPlayer() {
        return c.getPlayer();
    }

    /** @return 玩家角色对象（getPlayer 的别名） */
    public Character getChar() {
        return c.getPlayer();
    }

    /** @return 当前角色的职业 ID */
    public int getJobId() {
        return getPlayer().getJob().getId();
    }

    /** @return 当前角色的职业枚举 */
    public Job getJob() {
        return getPlayer().getJob();
    }

    /** @return 当前角色等级 */
    public int getLevel() {
        return getPlayer().getLevel();
    }

    /** @return 当前所在的地图实例 */
    public MapleMap getMap() {
        return c.getPlayer().getMap();
    }

    /** @return 当前服务器时间的小时数（0-23） */
    public int getHourOfDay() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /** 获取指定地图的市场传送门（自由市场入口）ID */
    public int getMarketPortalId(int mapId) {
        return getMarketPortalId(getWarpMap(mapId));
    }

    private int getMarketPortalId(MapleMap map) {
        return (map.findMarketPortal() != null) ? map.findMarketPortal().getId() : map.getRandomPlayerSpawnpoint().getId();
    }

    // ==================== 传送 ====================

    /** 传送到指定地图的默认传送门 */
    public void warp(int mapid) {
        getPlayer().changeMap(mapid);
    }

    /** 传送到指定地图的指定传送门（按 ID） */
    public void warp(int map, int portal) {
        getPlayer().changeMap(map, portal);
    }

    /** 传送到指定地图的指定传送门（按名称） */
    public void warp(int map, String portal) {
        getPlayer().changeMap(map, portal);
    }

    /** 将当前地图所有玩家传送到目标地图 */
    public void warpMap(int map) {
        getPlayer().getMap().warpEveryone(map);
    }

    /** 传送当前队伍到指定地图的默认传送门 */
    public void warpParty(int id) {
        warpParty(id, 0);
    }

    /** 传送当前队伍到指定地图的指定传送门 */
    public void warpParty(int id, int portalId) {
        int mapid = getMapId();
        warpParty(id, portalId, mapid, mapid);
    }

    /** 传送当前队伍到指定地图的指定传送门（按名称） */
    public void warpParty(int map, String portalName) {

        int mapid = getMapId();
        var warpMap = c.getChannelServer().getMapFactory().getMap(map);

        var portal = warpMap.getPortal(portalName);

        if (portal == null) {
            portal = warpMap.getPortal(0);
        }

        var portalId = portal.getId();

        warpParty(map, portalId, mapid, mapid);

    }

    /** 传送处于指定地图范围之间的队伍成员到目标地图 */
    public void warpParty(int id, int fromMinId, int fromMaxId) {
        warpParty(id, 0, fromMinId, fromMaxId);
    }

    /**
     * 传送队伍中在线且处于指定地图范围内的成员到目标地图。
     * 只传送 mapId 在 [fromMinId, fromMaxId] 区间的队伍成员，用于 PQ 阶段传送。
     */
    public void warpParty(int id, int portalId, int fromMinId, int fromMaxId) {
        for (Character mc : this.getPlayer().getPartyMembersOnline()) {
            if (mc.isLoggedInWorld()) {
                if (mc.getMapId() >= fromMinId && mc.getMapId() <= fromMaxId) {
                    mc.changeMap(id, portalId);
                }
            }
        }
    }

    /** 获取指定地图 ID 对应的地图对象（含传送逻辑） */
    public MapleMap getWarpMap(int map) {
        return getPlayer().getWarpMap(map);
    }

    /** 获取指定地图 ID 对应的地图对象（getWarpMap 的别名） */
    public MapleMap getMap(int map) {
        return getWarpMap(map);
    }

    // ==================== 地图/怪物 ====================

    /** 查询指定地图上的怪物数量 */
    public int countAllMonstersOnMap(int map) {
        return getMap(map).countMonsters();
    }

    /** 查询当前地图上的怪物数量 */
    public int countMonster() {
        return getPlayer().getMap().countMonsters();
    }

    /** 重置指定地图的所有对象（反应堆、NPC 等，不清除怪物） */
    public void resetMapObjects(int mapid) {
        getWarpMap(mapid).resetMapObjects();
    }

    // ==================== 事件 ====================

    /** 获取指定名称的事件管理器 */
    public EventManager getEventManager(String event) {
        return getClient().getEventManager(event);
    }

    /** 获取玩家当前参与的事件实例 */
    public EventInstanceManager getEventInstance() {
        return getPlayer().getEventInstance();
    }

    // ==================== 背包 ====================

    /** 获取指定类型的背包 */
    public Inventory getInventory(int type) {
        return getPlayer().getInventory(InventoryType.getByType((byte) type));
    }

    /** 获取指定类型的背包 */
    public Inventory getInventory(InventoryType type) {
        return getPlayer().getInventory(type);
    }

    /** 检查是否拥有指定物品（至少 1 个） */
    public boolean hasItem(int itemid) {
        return haveItem(itemid, 1);
    }

    /** 检查是否拥有指定数量的物品 */
    public boolean hasItem(int itemid, int quantity) {
        return haveItem(itemid, quantity);
    }

    /** 检查是否拥有指定物品（至少 1 个，haveItem 的别名） */
    public boolean haveItem(int itemid) {
        return haveItem(itemid, 1);
    }

    /** 检查背包中是否有足够数量的指定物品 */
    public boolean haveItem(int itemid, int quantity) {
        return getPlayer().getItemQuantity(itemid, false) >= quantity;
    }

    /** 获取背包中指定物品的数量 */
    public int getItemQuantity(int itemid) {
        return getPlayer().getItemQuantity(itemid, false);
    }

    /** 检查是否拥有指定 ID 的物品（不检查已装备栏） */
    public boolean haveItemWithId(int itemid) {
        return haveItemWithId(itemid, false);
    }

    /** 检查是否拥有指定 ID 的物品，可选是否检查已装备栏 */
    public boolean haveItemWithId(int itemid, boolean checkEquipped) {
        return getPlayer().haveItemWithId(itemid, checkEquipped);
    }

    // ==================== 背包空间检查 ====================

    /** 检查能否容纳 1 个指定物品 */
    public boolean canHold(int itemid) {
        return canHold(itemid, 1);
    }

    /** 检查能否容纳指定数量的物品 */
    public boolean canHold(int itemid, int quantity) {
        return canHoldAll(Collections.singletonList(itemid), Collections.singletonList(quantity), true);
    }

    /** 检查移除 removeItemid 后再添加 itemid 是否有空间 */
    public boolean canHold(int itemid, int quantity, int removeItemid, int removeQuantity) {
        return canHoldAllAfterRemoving(Collections.singletonList(itemid), Collections.singletonList(quantity), Collections.singletonList(removeItemid), Collections.singletonList(removeQuantity));
    }

    /** 将 List&lt;Object&gt; 转换为 List&lt;Integer&gt;（脚本引擎兼容用） */
    private List<Integer> convertToIntegerList(List<Object> objects) {
        List<Integer> intList = new ArrayList<>();

        for (Object object : objects) {
            intList.add((Integer) object);
        }

        return intList;
    }

    /** 检查能否同时容纳多种物品（每种各 1 个） */
    public boolean canHoldAll(List<Object> itemids) {
        List<Object> quantity = new LinkedList<>();

        final int intOne = 1;
        for (int i = 0; i < itemids.size(); i++) {
            quantity.add(intOne);
        }

        return canHoldAll(itemids, quantity);
    }

    /** 检查能否同时容纳多种物品（指定各自数量） */
    public boolean canHoldAll(List<Object> itemids, List<Object> quantity) {
        return canHoldAll(convertToIntegerList(itemids), convertToIntegerList(quantity), true);
    }

    /** 核心背包空间检查：模拟添加所有物品，检查是否有足够槽位 */
    private boolean canHoldAll(List<Integer> itemids, List<Integer> quantity, boolean isInteger) {
        int size = Math.min(itemids.size(), quantity.size());

        List<Pair<Item, InventoryType>> addedItems = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Item it = new Item(itemids.get(i), (short) 0, quantity.get(i).shortValue());
            addedItems.add(new Pair<>(it, ItemConstants.getInventoryType(itemids.get(i))));
        }

        return Inventory.checkSpots(c.getPlayer(), addedItems);
    }

    /** 构建背包验证用的物品列表 */
    private List<Pair<Item, InventoryType>> prepareProofInventoryItems(List<Pair<Integer, Integer>> items) {
        List<Pair<Item, InventoryType>> addedItems = new LinkedList<>();
        for (Pair<Integer, Integer> p : items) {
            Item it = new Item(p.getLeft(), (short) 0, p.getRight().shortValue());
            addedItems.add(new Pair<>(it, InventoryType.CANHOLD));
        }

        return addedItems;
    }

    /** 按背包类型分类整理待添加的物品列表 */
    private List<List<Pair<Integer, Integer>>> prepareInventoryItemList(List<Integer> itemids, List<Integer> quantity) {
        int size = Math.min(itemids.size(), quantity.size());

        List<List<Pair<Integer, Integer>>> invList = new ArrayList<>(6);
        for (int i = InventoryType.UNDEFINED.getType(); i <= InventoryType.CASH.getType(); i++) {
            invList.add(new LinkedList<>());
        }

        for (int i = 0; i < size; i++) {
            int itemid = itemids.get(i);
            invList.get(ItemConstants.getInventoryType(itemid).getType()).add(new Pair<>(itemid, quantity.get(i)));
        }

        return invList;
    }

    /**
     * 检查在移除部分物品后，能否容纳新物品。
     * 使用 CANHOLD 虚拟背包进行模拟，不实际修改背包数据。
     */
    public boolean canHoldAllAfterRemoving(List<Integer> toAddItemids, List<Integer> toAddQuantity, List<Integer> toRemoveItemids, List<Integer> toRemoveQuantity) {
        List<List<Pair<Integer, Integer>>> toAddItemList = prepareInventoryItemList(toAddItemids, toAddQuantity);
        List<List<Pair<Integer, Integer>>> toRemoveItemList = prepareInventoryItemList(toRemoveItemids, toRemoveQuantity);

        InventoryProof prfInv = (InventoryProof) this.getInventory(InventoryType.CANHOLD);
        prfInv.lockInventory();
        try {
            for (int i = InventoryType.EQUIP.getType(); i < InventoryType.CASH.getType(); i++) {
                List<Pair<Integer, Integer>> toAdd = toAddItemList.get(i);

                if (!toAdd.isEmpty()) {
                    List<Pair<Integer, Integer>> toRemove = toRemoveItemList.get(i);

                    Inventory inv = this.getInventory(i);
                    prfInv.cloneContents(inv);

                    for (Pair<Integer, Integer> p : toRemove) {
                        InventoryManipulator.removeById(c, InventoryType.CANHOLD, p.getLeft(), p.getRight(), false, false);
                    }

                    List<Pair<Item, InventoryType>> addItems = prepareProofInventoryItems(toAdd);

                    boolean canHold = Inventory.checkSpots(c.getPlayer(), addItems, true);
                    if (!canHold) {
                        return false;
                    }
                }
            }
        } finally {
            prfInv.flushContents();
            prfInv.unlockInventory();
        }

        return true;
    }

    // ==================== 任务系统 ====================

    /** 获取任务记录（如果不存在则自动创建） */
    public final QuestStatus getQuestRecord(final int id) {
        return c.getPlayer().getQuestNAdd(Quest.getInstance(id));
    }

    /** 获取任务记录（如果不存在则返回 null，不自动创建） */
    public final QuestStatus getQuestNoRecord(final int id) {
        return c.getPlayer().getQuestNoAdd(Quest.getInstance(id));
    }

    /** 打开 NPC 对话（使用默认脚本或自动查找） */
    public void openNpc(int npcid) {
        openNpc(npcid, null);
    }

    /**
     * 打开 NPC 对话并执行指定脚本。
     * 如果当前已有活跃的对话管理器则跳过，避免重复开启。
     */
    public void openNpc(int npcid, String script) {
        if (c.getCM() != null) {
            return;
        }

        c.removeClickedNPC();
        NPCScriptManager.getInstance().dispose(c);
        NPCScriptManager.getInstance().start(c, npcid, script, null);
    }

    /** 获取任务状态码（0=未开始, 1=进行中, 2=已完成） */
    public int getQuestStatus(int id) {
        return c.getPlayer().getQuest(Quest.getInstance(id)).getStatus().getId();
    }

    private QuestStatus.Status getQuestStat(int id) {
        return c.getPlayer().getQuest(Quest.getInstance(id)).getStatus();
    }

    /** 判断任务是否已完成 */
    public boolean isQuestCompleted(int id) {
        try {
            return getQuestStat(id) == QuestStatus.Status.COMPLETED;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 判断任务是否正在进行中（isQuestStarted 的别名） */
    public boolean isQuestActive(int id) {
        return isQuestStarted(id);
    }

    /** 判断任务是否正在进行中 */
    public boolean isQuestStarted(int id) {
        try {
            return getQuestStat(id) == QuestStatus.Status.STARTED;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** 设置任务进度（字符串） */
    public void setQuestProgress(int id, String progress) {
        setQuestProgress(id, 0, progress);
    }

    /** 设置任务进度（整数转为字符串） */
    public void setQuestProgress(int id, int progress) {
        setQuestProgress(id, 0, "" + progress);
    }

    /** 设置任务进度（指定 infoNumber 的整数值） */
    public void setQuestProgress(int id, int infoNumber, int progress) {
        setQuestProgress(id, infoNumber, "" + progress);
    }

    /** 设置任务指定 infoNumber 的进度值 */
    public void setQuestProgress(int id, int infoNumber, String progress) {
        c.getPlayer().setQuestProgress(id, infoNumber, progress);
    }

    /** 获取任务进度（infoNumber=0） */
    public String getQuestProgress(int id) {
        return getQuestProgress(id, 0);
    }

    /** 获取任务指定 infoNumber 的进度值 */
    public String getQuestProgress(int id, int infoNumber) {
        QuestStatus qs = getPlayer().getQuest(Quest.getInstance(id));

        if (qs.getInfoNumber() == infoNumber && infoNumber > 0) {
            qs = getPlayer().getQuest(Quest.getInstance(infoNumber));
            infoNumber = 0;
        }

        if (qs != null) {
            return qs.getProgress(infoNumber);
        } else {
            return "";
        }
    }

    /** 获取任务进度整数值（infoNumber=0） */
    public int getQuestProgressInt(int id) {
        try {
            return Integer.parseInt(getQuestProgress(id));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /** 获取任务指定 infoNumber 的进度整数值 */
    public int getQuestProgressInt(int id, int infoNumber) {
        try {
            return Integer.parseInt(getQuestProgress(id, infoNumber));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /** 清空任务全部进度 */
    public void resetAllQuestProgress(int id) {
        QuestStatus qs = getPlayer().getQuest(Quest.getInstance(id));
        if (qs != null) {
            qs.resetAllProgress();
            getPlayer().announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
        }
    }

    /** 清空任务指定 infoNumber 的进度 */
    public void resetQuestProgress(int id, int infoNumber) {
        QuestStatus qs = getPlayer().getQuest(Quest.getInstance(id));
        if (qs != null) {
            qs.resetProgress(infoNumber);
            getPlayer().announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
        }
    }

    /** 强制开始任务（默认 NPC 为 Maple Administrator） */
    public boolean forceStartQuest(int id) {
        return forceStartQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

    public boolean forceStartQuest(int id, int npc) {
        return startQuest(id, npc);
    }

    /** 强制完成任务（默认 NPC 为 Maple Administrator） */
    public boolean forceCompleteQuest(int id) {
        return forceCompleteQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

    public boolean forceCompleteQuest(int id, int npc) {
        return completeQuest(id, npc);
    }

    /** 开始任务（short 版本兼容） */
    public boolean startQuest(short id) {
        return startQuest((int) id);
    }

    /** 完成任务（short 版本兼容） */
    public boolean completeQuest(short id) {
        return completeQuest((int) id);
    }

    /** 开始任务（默认 NPC 为 Maple Administrator） */
    public boolean startQuest(int id) {
        return startQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

    /** 完成任务（默认 NPC 为 Maple Administrator） */
    public boolean completeQuest(int id) {
        return completeQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

    public boolean startQuest(short id, int npc) {
        return startQuest((int) id, npc);
    }

    public boolean completeQuest(short id, int npc) {
        return completeQuest((int) id, npc);
    }

    /** 通过指定 NPC 强制开始任务，忽略前置条件检查 */
    public boolean startQuest(int id, int npc) {
        try {
            return Quest.getInstance(id).forceStart(getPlayer(), npc);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /** 通过指定 NPC 强制完成任务，忽略完成条件检查 */
    public boolean completeQuest(int id, int npc) {
        try {
            return Quest.getInstance(id).forceComplete(getPlayer(), npc);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 宠物进化：将指定槽位的宠物替换为进化后的新宠物。
     *
     * @param slot    宠物所在槽位
     * @param afterId 进化后的宠物道具 ID
     * @return 进化后的宠物物品，失败返回 null
     */
    public Item evolvePet(byte slot, int afterId) {
        Pet evolved = null;
        Pet target;

        long period = DAYS.toMillis(90);    //refreshes expiration date: 90 days


        target = getPlayer().getPet(slot);
        if (target == null) {
            getPlayer().message("Pet could not be evolved...");
            return (null);
        }

        Item tmp = gainItem(afterId, (short) 1, false, true, period, target);
            
            /*
            evolved = Pet.loadFromDb(tmp.getItemId(), tmp.getPosition(), tmp.getPetId());
            
            evolved = tmp.getPet();
            if(evolved == null) {
                getPlayer().message("Pet structure non-existent for " + tmp.getItemId() + "...");
                return(null);
            }
            else if(tmp.getPetId() == -1) {
                getPlayer().message("Pet id -1");
                return(null);
            }
            
            getPlayer().addPet(evolved);
            
            getPlayer().getMap().broadcastMessage(c.getPlayer(), PacketCreator.showPet(c.getPlayer(), evolved, false, false), true);
            c.sendPacket(PacketCreator.petStatUpdate(c.getPlayer()));
            c.sendPacket(PacketCreator.enableActions());
            chr.getClient().getWorldServer().registerPetHunger(chr, chr.getPetIndex(evolved));
            */

        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, target.getPosition(), (short) 1, false);

        return evolved;
    }

    // ==================== 物品给予 ====================

    /** 给予指定物品（默认显示获得提示） */
    public void gainItem(int id, short quantity) {
        gainItem(id, quantity, false, true);
    }

    /** 给予指定物品，可选是否显示获得提示 */
    public void gainItem(int id, short quantity, boolean show) {
        gainItem(id, quantity, false, show);
    }

    /** 给予 1 个指定物品，可选是否显示获得提示 */
    public void gainItem(int id, boolean show) {
        gainItem(id, (short) 1, false, show);
    }

    /** 给予 1 个指定物品（默认显示获得提示） */
    public void gainItem(int id) {
        gainItem(id, (short) 1, false, true);
    }

    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage) {
        return gainItem(id, quantity, randomStats, showMessage, -1);
    }

    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage, long expires) {
        return gainItem(id, quantity, randomStats, showMessage, expires, null);
    }

    /**
     * 核心物品给予方法。当 quantity &lt; 0 时改为移除物品。
     *
     * @param id          物品 ID
     * @param quantity    数量（负数为移除）
     * @param randomStats 装备是否随机属性
     * @param showMessage 是否显示获得物品的提示
     * @param expires     过期时间戳（-1=永不过期）
     * @param from        进化来源宠物（宠物进化时传入）
     * @return 创建或移除的 Item 对象
     */
    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage, long expires, Pet from) {
        Item item = null;
        Pet evolved;
        int petId = -1;

        if (quantity >= 0) {
            if (ItemConstants.isPet(id)) {
                petId = Pet.createPet(id);

                if (from != null) {
                    evolved = Pet.loadFromDb(id, (short) 0, petId);

                    Point pos = getPlayer().getPosition();
                    pos.y -= 12;
                    evolved.setPos(pos);
                    evolved.setFh(getPlayer().getMap().getFootholds().findBelow(evolved.getPos()).getId());
                    evolved.setStance(0);
                    evolved.setSummoned(true);

                    evolved.setName(from.getName().compareTo(ItemInformationProvider.getInstance().getName(from.getItemId())) != 0 ? from.getName() : ItemInformationProvider.getInstance().getName(id));
                    evolved.setTameness(from.getTameness());
                    evolved.setFullness(from.getFullness());
                    evolved.setLevel(from.getLevel());
                    evolved.setExpiration(System.currentTimeMillis() + expires);
                    evolved.saveToDb();
                }

                //InventoryManipulator.addById(c, id, (short) 1, null, petId, expires == -1 ? -1 : System.currentTimeMillis() + expires);
            }

            ItemInformationProvider ii = ItemInformationProvider.getInstance();

            if (ItemConstants.getInventoryType(id).equals(InventoryType.EQUIP)) {
                item = ii.getEquipById(id);

                if (item != null) {
                    Equip it = (Equip) item;
                    if (ItemConstants.isAccessory(item.getItemId()) && it.getUpgradeSlots() <= 0) {
                        it.setUpgradeSlots(3);
                    }

                    if (GameConfig.getServerBoolean("use_enhanced_crafting") && c.getPlayer().isUseCS()) {
                        Equip eqp = (Equip) item;
                        if (!(c.getPlayer().isGM() && GameConfig.getServerBoolean("use_perfect_gm_scroll"))) {
                            eqp.setUpgradeSlots((byte) (eqp.getUpgradeSlots() + 1));
                        }
                        item = ItemInformationProvider.getInstance().scrollEquipWithId(item, ItemId.CHAOS_SCROll_60, true, ItemId.CHAOS_SCROll_60, c.getPlayer().isGM());
                    }
                }
            } else {
                item = new Item(id, (short) 0, quantity, petId);
            }

            if (expires >= 0) {
                item.setExpiration(System.currentTimeMillis() + expires);
            }

            if (!InventoryManipulator.checkSpace(c, id, quantity, "")) {
                c.getPlayer().dropMessage(1, "Your inventory is full. Please remove an item from your " + ItemConstants.getInventoryType(id).name() + " inventory.");
                return null;
            }
            if (ItemConstants.getInventoryType(id) == InventoryType.EQUIP) {
                if (randomStats) {
                    InventoryManipulator.addFromDrop(c, ii.randomizeStats((Equip) item), false, petId);
                } else {
                    InventoryManipulator.addFromDrop(c, item, false, petId);
                }
            } else {
                InventoryManipulator.addFromDrop(c, item, false, petId);
            }
        } else {
            InventoryManipulator.removeById(c, ItemConstants.getInventoryType(id), id, -quantity, true, false);
        }
        if (showMessage) {
            c.sendPacket(PacketCreator.getShowItemGain(id, quantity, true));
        }

        return item;
    }

    /** 增减人气值 */
    public void gainFame(int delta) {
        getPlayer().gainFame(delta);
    }

    // ==================== 消息与特效 ====================

    /** 切换当前地图的 BGM */
    public void changeMusic(String songName) {
        getPlayer().getMap().broadcastMessage(PacketCreator.musicChange(songName));
    }

    /** 向当前玩家发送系统通知（顶部 notice） */
    public void playerMessage(int type, String message) {
        c.sendPacket(PacketCreator.serverNotice(type, message));
    }

    /** 向当前玩家发送聊天框消息 */
    public void message(String message) {
        getPlayer().message(message);
    }

    /** 向当前玩家发送掉落到聊天框的消息 */
    public void dropMessage(int type, String message) {
        getPlayer().dropMessage(type, message);
    }

    /** 向当前地图所有玩家发送系统通知 */
    public void mapMessage(int type, String message) {
        getPlayer().getMap().broadcastMessage(PacketCreator.serverNotice(type, message));
    }

    /** 播放地图特效 */
    public void mapEffect(String path) {
        c.sendPacket(PacketCreator.mapEffect(path));
    }

    /** 播放地图音效 */
    public void mapSound(String path) {
        c.sendPacket(PacketCreator.mapSound(path));
    }

    /** 播放 Aran 职业剧情引导动画（根据当前地图自动选择场景） */
    public void displayAranIntro() {
        String intro = switch (c.getPlayer().getMapId()) {
            case MapId.ARAN_TUTO_1 -> "Effect/Direction1.img/aranTutorial/Scene0";
            case MapId.ARAN_TUTO_2 ->
                    "Effect/Direction1.img/aranTutorial/Scene1" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            case MapId.ARAN_TUTO_3 ->
                    "Effect/Direction1.img/aranTutorial/Scene2" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            case MapId.ARAN_TUTO_4 -> "Effect/Direction1.img/aranTutorial/Scene3";
            case MapId.ARAN_POLEARM ->
                    "Effect/Direction1.img/aranTutorial/HandedPoleArm" + (c.getPlayer().getGender() == 0 ? "0" : "1");
            case MapId.ARAN_MAHA -> "Effect/Direction1.img/aranTutorial/Maha";
            default -> "";
        };
        showIntro(intro);
    }

    /** 播放开场引导动画 */
    public void showIntro(String path) {
        c.sendPacket(PacketCreator.showIntro(path));
    }

    /** 播放信息类引导动画 */
    public void showInfo(String path) {
        c.sendPacket(PacketCreator.showInfo(path));
        c.sendPacket(PacketCreator.enableActions());
    }

    // ==================== 公会 ====================

    /** 向公会频道发送消息 */
    public void guildMessage(int type, String message) {
        if (getGuild() != null) {
            getGuild().guildMessage(PacketCreator.serverNotice(type, message));
        }
    }

    /** 获取玩家所在的公会对象 */
    public Guild getGuild() {
        try {
            return Server.getInstance().getGuild(getPlayer().getGuildId(), getPlayer().getWorld(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ==================== 组队 ====================

    /** 获取玩家所在队伍 */
    public Party getParty() {
        return getPlayer().getParty();
    }

    /** 判断是否为队长（isPartyLeader 的别名） */
    public boolean isLeader() {
        return isPartyLeader();
    }

    /** 判断是否为公会会长 */
    public boolean isGuildLeader() {
        return getPlayer().isGuildLeader();
    }

    /** 判断是否为当前队伍的队长 */
    public boolean isPartyLeader() {
        if (getParty() == null) {
            return false;
        }

        return getParty().getLeaderId() == getPlayer().getId();
    }

    /** 判断是否为当前事件实例的队长 */
    public boolean isEventLeader() {
        return getEventInstance() != null && getPlayer().getId() == getEventInstance().getLeaderId();
    }

    /** 给队伍成员发放物品（正向给予，反向移除） */
    public void givePartyItems(int id, short quantity, List<Character> party) {
        for (Character chr : party) {
            Client cl = chr.getClient();
            if (quantity >= 0) {
                InventoryManipulator.addById(cl, id, quantity);
            } else {
                InventoryManipulator.removeById(cl, ItemConstants.getInventoryType(id), id, -quantity, true, false);
            }
            cl.sendPacket(PacketCreator.getShowItemGain(id, quantity, true));
        }
    }

    /** 移除队伍成员的赫里希安 PQ 种子物品 */
    public void removeHPQItems() {
        int[] items = {ItemId.GREEN_PRIMROSE_SEED, ItemId.PURPLE_PRIMROSE_SEED, ItemId.PINK_PRIMROSE_SEED,
                ItemId.BROWN_PRIMROSE_SEED, ItemId.YELLOW_PRIMROSE_SEED, ItemId.BLUE_PRIMROSE_SEED};
        for (int item : items) {
            removePartyItems(item);
        }
    }

    /** 移除队伍所有成员的指定物品全部数量 */
    public void removePartyItems(int id) {
        if (getParty() == null) {
            removeAll(id);
            return;
        }
        for (PartyCharacter mpc : getParty().getMembers()) {
            if (mpc == null || !mpc.isOnline()) {
                continue;
            }

            Character chr = mpc.getPlayer();
            if (chr != null && chr.getClient() != null) {
                removeAll(id, chr.getClient());
            }
        }
    }

    /** 给指定角色加经验（乘以该角色的经验倍率） */
    public void giveCharacterExp(int amount, Character chr) {
        chr.gainExp(NumberTool.floatToInt(amount * chr.getExpRate()), true, true);
    }

    /** 给队伍中指定成员列表加经验 */
    public void givePartyExp(int amount, List<Character> party) {
        for (Character chr : party) {
            giveCharacterExp(amount, chr);
        }
    }

    /** 按 PQ 配置给队伍加经验（仅限同事件实例成员） */
    public void givePartyExp(String PQ) {
        givePartyExp(PQ, true);
    }

    /**
     * 按 PQ 名称和队伍等级计算并分配经验。
     * 4 人及以上队伍有额外加成：4人+10%，5人+20%，6人+30%。
     */
    public void givePartyExp(String PQ, boolean instance) {
        //1 player  =  +0% bonus (100)
        //2 players =  +0% bonus (100)
        //3 players =  +0% bonus (100)
        //4 players = +10% bonus (110)
        //5 players = +20% bonus (120)
        //6 players = +30% bonus (130)
        Party party = getPlayer().getParty();
        int size = party.getMembers().size();

        if (instance) {
            for (PartyCharacter member : party.getMembers()) {
                if (member == null || !member.isOnline()) {
                    size--;
                } else {
                    Character chr = member.getPlayer();
                    if (chr != null && chr.getEventInstance() == null) {
                        size--;
                    }
                }
            }
        }

        int bonus = size < 4 ? 100 : 70 + (size * 10);
        for (PartyCharacter member : party.getMembers()) {
            if (member == null || !member.isOnline()) {
                continue;
            }
            Character player = member.getPlayer();
            if (player == null) {
                continue;
            }
            if (instance && player.getEventInstance() == null) {
                continue; // They aren't in the instance, don't give EXP.
            }
            int base = PartyQuest.getExp(PQ, player.getLevel());
            int exp = base * bonus / 100;
            if (GameConfig.getServerFloat("pq_bonus_exp_rate") > 0) {
                player.gainExp((int) (exp * GameConfig.getServerFloat("pq_bonus_exp_rate")), true, true);
            } else {
                player.gainExp(exp, true, true);
            }
        }
    }

    // ==================== 物品移除 ====================

    /** 从队伍成员中移除指定物品的全部数量 */
    public void removeFromParty(int id, List<Character> party) {
        for (Character chr : party) {
            InventoryType type = ItemConstants.getInventoryType(id);
            Inventory iv = chr.getInventory(type);
            int possesed = iv.countById(id);
            if (possesed > 0) {
                InventoryManipulator.removeById(c, ItemConstants.getInventoryType(id), id, possesed, true, false);
                chr.sendPacket(PacketCreator.getShowItemGain(id, (short) -possesed, true));
            }
        }
    }

    /** 从当前玩家背包移除指定物品的全部数量 */
    public void removeAll(int id) {
        removeAll(id, c);
    }

    /**
     * 从指定客户端的背包中移除该物品的全部数量。
     * 如果是装备类物品，同时检查并移除已装备栏中的物品。
     */
    public void removeAll(int id, Client cl) {
        InventoryType invType = ItemConstants.getInventoryType(id);
        int possessed = cl.getPlayer().getInventory(invType).countById(id);
        if (possessed > 0) {
            InventoryManipulator.removeById(cl, ItemConstants.getInventoryType(id), id, possessed, true, false);
            cl.sendPacket(PacketCreator.getShowItemGain(id, (short) -possessed, true));
        }

        if (invType == InventoryType.EQUIP) {
            if (cl.getPlayer().getInventory(InventoryType.EQUIPPED).countById(id) > 0) {
                InventoryManipulator.removeById(cl, InventoryType.EQUIPPED, id, 1, true, false);
                cl.sendPacket(PacketCreator.getShowItemGain(id, (short) -1, true));
            }
        }
    }

    /** 清空指定背包类型（如消耗、装备、etc）的全部物品 */
    public void removeAllByInventory(int invType) {
        Inventory inv = getInventory(invType);
        for (Item item : new ArrayList<>(inv.list())) {
            InventoryManipulator.removeFromSlot(c, inv.getType(), item.getPosition(), item.getQuantity(), false);
        }
    }

    /** 移除指定背包类型的指定槽位物品 */
    public void removeAllByInventorySlot(int invType, short slot) {
        Inventory inv = getInventory(invType);
        Item item = inv.getItem(slot);
        if (item != null) {
            InventoryManipulator.removeFromSlot(c, inv.getType(), item.getPosition(), item.getQuantity(), false);
        }
    }

    // ==================== 地图/UI ====================

    /** 获取当前所在地图 ID */
    public int getMapId() {
        return c.getPlayer().getMap().getId();
    }

    /** 获取指定地图上的玩家数量 */
    public int getPlayerCount(int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharacters().size();
    }

    /** 显示带尺寸的提示信息 */
    public void showInstruction(String msg, int width, int height) {
        c.sendPacket(PacketCreator.sendHint(msg, width, height));
        c.sendPacket(PacketCreator.enableActions());
    }

    /** 禁用小地图 */
    public void disableMinimap() {
        c.sendPacket(PacketCreator.disableMinimap());
    }

    /** 检查指定地图上所有指定 ID 的反应堆是否都处于指定状态 */
    public boolean isAllReactorState(final int reactorId, final int state) {
        return c.getPlayer().getMap().isAllReactorState(reactorId, state);
    }

    /** 重置地图：清除反应堆、怪物和掉落物 */
    public void resetMap(int mapid) {
        getMap(mapid).resetReactors();
        getMap(mapid).killAllMonsters();
        for (MapObject i : getMap(mapid).getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM))) {
            getMap(mapid).removeMapObject(i);
            getMap(mapid).broadcastMessage(PacketCreator.removeItemFromMap(i.getObjectId(), 0, c.getPlayer().getId()));
        }
    }

    /** 使用物品效果（消耗品 BUFF） */
    public void useItem(int id) {
        ItemInformationProvider.getInstance().getItemEffect(id).applyTo(c.getPlayer());
        c.sendPacket(PacketCreator.getItemMessage(id));
    }

    /** 取消物品效果 */
    public void cancelItem(final int id) {
        getPlayer().cancelEffect(ItemInformationProvider.getInstance().getItemEffect(id), false, -1);
    }

    // ==================== 技能 ====================

    /** 传授技能（不强制覆盖更高等级） */
    public void teachSkill(int skillid, byte level, byte masterLevel, long expiration) {
        teachSkill(skillid, level, masterLevel, expiration, false);
    }

    /**
     * 传授技能给玩家。
     *
     * @param skillid     技能 ID
     * @param level       技能等级
     * @param masterLevel 精通等级
     * @param expiration  过期时间（-1 永不过期）
     * @param force       是否强制覆盖（否则取已有等级和传入等级的最大值）
     */
    public void teachSkill(int skillid, byte level, byte masterLevel, long expiration, boolean force) {
        Skill skill = SkillFactory.getSkill(skillid);
        SkillEntry skillEntry = getPlayer().getSkills().get(skill);
        if (skillEntry != null) {
            if (!force && level > -1) {
                getPlayer().changeSkillLevel(skill, (byte) Math.max(skillEntry.skillLevel, level), Math.max(skillEntry.masterLevel, masterLevel), expiration == -1 ? -1 : Math.max(skillEntry.expiration, expiration));
                return;
            }
        } else if (GameConstants.isAranSkills(skillid)) {
            c.sendPacket(PacketCreator.showInfo("Effect/BasicEff.img/AranGetSkill"));
        }

        getPlayer().changeSkillLevel(skill, level, masterLevel, expiration);
    }

    // ==================== 装备操作 ====================

    /** 卸下指定槽位的装备 */
    public void removeEquipFromSlot(short slot) {
        Item tempItem = c.getPlayer().getInventory(InventoryType.EQUIPPED).getItem(slot);
        InventoryManipulator.removeFromSlot(c, InventoryType.EQUIPPED, slot, tempItem.getQuantity(), false, false);
    }

    /** 直接获得并装备到指定槽位（旧装备会被替换） */
    public void gainAndEquip(int itemid, short slot) {
        final Item old = c.getPlayer().getInventory(InventoryType.EQUIPPED).getItem(slot);
        if (old != null) {
            InventoryManipulator.removeFromSlot(c, InventoryType.EQUIPPED, slot, old.getQuantity(), false, false);
        }
        final Item newItem = ItemInformationProvider.getInstance().getEquipById(itemid);
        newItem.setPosition(slot);
        c.getPlayer().getInventory(InventoryType.EQUIPPED).addItemFromDB(newItem);
        c.sendPacket(PacketCreator.modifyInventory(false, Collections.singletonList(new ModifyInventory(0, newItem))));
    }

    // ==================== 刷怪 / NPC 生成 ====================

    /** 在指定地图和坐标生成一个 NPC */
    public void spawnNpc(int npcId, Point pos, MapleMap map) {
        NPC npc = LifeFactory.getNPC(npcId);
        if (npc != null) {
            npc.setPosition(pos);
            npc.setCy(pos.y);
            npc.setRx0(pos.x + 50);
            npc.setRx1(pos.x - 50);
            npc.setFh(map.getFootholds().findBelow(pos).getId());
            map.addMapObject(npc);
            map.broadcastMessage(PacketCreator.spawnNPC(npc));
        }
    }

    /** 在当前地图指定坐标生成一个怪物 */
    public void spawnMonster(int id, int x, int y) {
        Monster monster = LifeFactory.getMonster(id);
        monster.setPosition(new Point(x, y));
        getPlayer().getMap().spawnMonster(monster);
    }

    /** 从 LifeFactory 获取怪物模板 */
    public Monster getMonsterLifeFactory(int mid) {
        return LifeFactory.getMonster(mid);
    }

    // ==================== 引导/提示 ====================

    /** 显示新手引导箭头 */
    public void spawnGuide() {
        c.sendPacket(PacketCreator.spawnGuide(true));
    }

    /** 隐藏新手引导箭头 */
    public void removeGuide() {
        c.sendPacket(PacketCreator.spawnGuide(false));
    }

    /** 显示指定的新手引导 UI 图片 */
    public void displayGuide(int num) {
        c.sendPacket(PacketCreator.showInfo("UI/tutorial.img/" + num));
    }

    // ==================== 武陵道场 ====================

    /** 武陵道场传送上一层 */
    public void goDojoUp() {
        c.sendPacket(PacketCreator.dojoWarpUp());
    }

    /** 重置当前玩家的道场能量 */
    public void resetDojoEnergy() {
        c.getPlayer().setDojoEnergy(0);
    }

    /** 重置同地图所有队员的道场能量 */
    public void resetPartyDojoEnergy() {
        for (Character pchr : c.getPlayer().getPartyMembersOnSameMap()) {
            pchr.setDojoEnergy(0);
        }
    }

    /** 恢复玩家操作（解除 UI 锁定） */
    public void enableActions() {
        c.sendPacket(PacketCreator.enableActions());
    }

    /** 播放全屏特效 */
    public void showEffect(String effect) {
        c.sendPacket(PacketCreator.showEffect(effect));
    }

    /** 更新道场能量条 UI */
    public void dojoEnergy() {
        c.sendPacket(PacketCreator.getEnergy("energy", getPlayer().getDojoEnergy()));
    }

    /** 显示对话引导消息 */
    public void talkGuide(String message) {
        c.sendPacket(PacketCreator.talkGuide(message));
    }

    /** 显示引导箭头提示 */
    public void guideHint(int hint) {
        c.sendPacket(PacketCreator.guideHint(hint));
    }

    // ==================== 区域/UI 杂项 ====================

    /** 更新角色区域信息（用于任务进度追踪） */
    public void updateAreaInfo(Short area, String info) {
        c.getPlayer().updateAreaInfo(area, info);
        c.sendPacket(PacketCreator.enableActions());
    }

    /** 检查角色是否已记录指定区域信息 */
    public boolean containsAreaInfo(short area, String info) {
        return c.getPlayer().containsAreaInfo(area, info);
    }

    /** 显示称号获得消息 */
    public void earnTitle(String msg) {
        c.sendPacket(PacketCreator.earnTitleMessage(msg));
    }

    /** 显示信息文本（顶部横幅） */
    public void showInfoText(String msg) {
        c.sendPacket(PacketCreator.showInfoText(msg));
    }

    /** 打开指定类型的游戏内 UI */
    public void openUI(byte ui) {
        c.sendPacket(PacketCreator.openUI(ui));
    }

    /** 锁定 UI（禁止操作） */
    public void lockUI() {
        c.sendPacket(PacketCreator.disableUI(true));
        c.sendPacket(PacketCreator.lockUI(true));
    }

    /** 解锁 UI */
    public void unlockUI() {
        c.sendPacket(PacketCreator.disableUI(false));
        c.sendPacket(PacketCreator.lockUI(false));
    }

    /** 播放环境音效 */
    public void playSound(String sound) {
        getPlayer().getMap().broadcastMessage(PacketCreator.environmentChange(sound, 4));
    }

    /** 切换地图环境（如天气、背景等） */
    public void environmentChange(String env, int mode) {
        getPlayer().getMap().broadcastMessage(PacketCreator.environmentChange(env, mode));
    }

    /** 数字格式化为千分位字符串（如 1000000 → "1,000,000"） */
    public String numberWithCommas(int number) {
        return GameConstants.numberWithCommas(number);
    }

    /** 获取当前的组队任务实例（金字塔 PQ） */
    public Pyramid getPyramid() {
        return (Pyramid) getPlayer().getPartyQuest();
    }

    // ==================== 远征队 ====================

    /** 创建远征队（默认不静默，无人数限制） */
    public int createExpedition(ExpeditionType type) {
        return createExpedition(type, false, 0, 0);
    }

    /**
     * 创建远征队。
     *
     * @return 0=成功, 1=已达到每日次数限制, -1=创建失败
     */
    public int createExpedition(ExpeditionType type, boolean silent, int minPlayers, int maxPlayers) {
        Character player = getPlayer();
        Expedition exped = new Expedition(player, type, silent, minPlayers, maxPlayers);

        int channel = player.getMap().getChannelServer().getId();
        if (!ExpeditionBossLog.attemptBoss(player.getId(), channel, exped, false)) {    // thanks Conrad for noticing missing expeditions entry limit
            return 1;
        }

        if (exped.addChannelExpedition(player.getClient().getChannelServer())) {
            return 0;
        } else {
            return -1;
        }
    }

    /** 结束远征队并清理频道资源 */
    public void endExpedition(Expedition exped) {
        exped.dispose(true);
        exped.removeChannelExpedition(getPlayer().getClient().getChannelServer());
    }

    /** 获取当前频道指定类型的远征队 */
    public Expedition getExpedition(ExpeditionType type) {
        return getPlayer().getClient().getChannelServer().getExpedition(type);
    }

    /** 获取远征队成员名字列表（逗号分隔） */
    public String getExpeditionMemberNames(ExpeditionType type) {
        String members = "";
        Expedition exped = getExpedition(type);
        for (String memberName : exped.getMembers().values()) {
            members += "" + memberName + ", ";
        }
        return members;
    }

    /** 判断当前玩家是否为指定远征队的队长 */
    public boolean isLeaderExpedition(ExpeditionType type) {
        Expedition exped = getExpedition(type);
        return exped.isLeader(getPlayer());
    }

    /** 获取剩余监禁时间（毫秒），未监禁返回 0 */
    public long getJailTimeLeft() {
        return getPlayer().getJailExpirationTimeLeft();
    }

    /** 获取玩家的已过期宠物列表 */
    public List<Pet> getDriedPets() {
        List<Pet> list = new LinkedList<>();

        long curTime = System.currentTimeMillis();
        for (Item it : getPlayer().getInventory(InventoryType.CASH).list()) {
            if (ItemConstants.isPet(it.getItemId()) && it.getExpiration() < curTime) {
                Pet pet = it.getPet();
                if (pet != null) {
                    list.add(pet);
                }
            }
        }

        return list;
    }

    /** 获取玩家未领取的婚礼礼物 */
    public List<Item> getUnclaimedMarriageGifts() {
        return Marriage.loadGiftItemsFromDb(this.getClient(), this.getPlayer().getId());
    }

    /** 为当前频道开启一个迷你地下城实例 */
    public boolean startDungeonInstance(int dungeonid) {
        return c.getChannelServer().addMiniDungeon(dungeonid);
    }

    /**
     * 检查角色是否满足一转能力值要求。
     * 如果启用了 use_auto_assign_starters_ap 配置则直接返回 true。
     */
    public boolean canGetFirstJob(int jobType) {
        if (GameConfig.getServerBoolean("use_auto_assign_starters_ap")) {
            return true;
        }

        Character chr = this.getPlayer();

        switch (jobType) {
            case 1:
                return chr.getStr() >= 35;

            case 2:
                return chr.getInt() >= 20;

            case 3:
            case 4:
                return chr.getDex() >= 25;

            case 5:
                return chr.getDex() >= 20;

            default:
                return true;
        }
    }

    /** 获取一转能力值要求文本（如 "力量 35"） */
    public String getFirstJobStatRequirement(int jobType) {
        switch (jobType) {
            case 1:
                return "力量 " + 35;

            case 2:
                return "智力 " + 20;

            case 3:
            case 4:
                return "敏捷 " + 25;

            case 5:
                return "敏捷 " + 20;
        }

        return null;
    }

    /** 发送 NPC 对话（不带选项按钮） */
    public void npcTalk(int npcid, String message) {
        c.sendPacket(PacketCreator.getNPCTalk(npcid, (byte) 0, message, "00 00", (byte) 0));
    }

    /** 获取服务器当前时间（毫秒） */
    public long getCurrentTime() {
        return Server.getInstance().getCurrentTime();
    }

    /**
     * 削弱区域 Boss：对指定怪物施加封印和减少回避率的 debuff，并广播蓝色提示消息。
     * 适用于脚本中需要"降低 Boss 难度"的场景。
     */
    public void weakenAreaBoss(int monsterId, String message) {
        MapleMap map = c.getPlayer().getMap();
        Monster monster = map.getMonsterById(monsterId);
        if (monster == null) {
            return;
        }

        applySealSkill(monster);
        applyReduceAvoid(monster);
        sendBlueNotice(map, message);
    }

    /** 对怪物施加封印技能（禁止使用技能） */
    private void applySealSkill(Monster monster) {
        MobSkill sealSkill = MobSkillFactory.getMobSkillOrThrow(MobSkillType.SEAL_SKILL, 1);
        sealSkill.applyEffect(monster);
    }

    /** 对怪物施加减回避率 debuff */
    private void applyReduceAvoid(Monster monster) {
        MobSkill reduceAvoidSkill = MobSkillFactory.getMobSkillOrThrow(MobSkillType.EVA, 2);
        reduceAvoidSkill.applyEffect(monster);
    }

    private void sendBlueNotice(MapleMap map, String message) {
        map.dropMessage(6, message);
    }

    // ==================== 扩展值持久化 ====================
    // 提供角色/账号级别的键值对存储，数据持久化到 extend_value 表。
    // 支持永久存储和每日/每周自动刷新两种模式，脚本无需关心数据库细节。

    /**
     * 获取角色扩展表某字段的值。
     *
     * @param extendName 扩展字段名
     * @return 扩展字段值，不存在返回 null
     */
    public String getCharacterExtendValue(String extendName) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getId()), ExtendType.CHARACTER_EXTEND.getType(), extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取每日/每周角色扩展表某字段的值。
     *
     * @param extendName 扩展字段名
     * @param isDaily    true=每日刷新, false=每周刷新
     * @return 扩展字段值，不存在返回 null
     */
    public String getCharacterExtendValue(String extendName, boolean isDaily) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getId()),
                isDaily ? ExtendType.CHARACTER_EXTEND_DAILY.getType() : ExtendType.CHARACTER_EXTEND_WEEKLY.getType(),
                extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取账号扩展表某字段的值。
     *
     * @param extendName 扩展字段名
     * @return 扩展字段值，不存在返回 null
     */
    public String getAccountExtendValue(String extendName) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getAccountId()), ExtendType.ACCOUNT_EXTEND.getType(), extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取每日/每周账号扩展表某字段的值。
     *
     * @param extendName 扩展字段名
     * @param isDaily    true=每日刷新, false=每周刷新
     * @return 扩展字段值，不存在返回 null
     */
    public String getAccountExtendValue(String extendName, boolean isDaily) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getAccountId()),
                isDaily ? ExtendType.ACCOUNT_EXTEND_DAILY.getType() : ExtendType.ACCOUNT_EXTEND_WEEKLY.getType(),
                extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 永久保存或更新角色扩展表指定字段的值。
     *
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值（JSON 字符串）
     */
    public void saveOrUpdateCharacterExtendValue(String extendName, String extendValue) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getId()), ExtendType.CHARACTER_EXTEND.getType(), extendName, extendValue);
    }

    /**
     * 保存每日/每周角色扩展表某字段的值。
     *
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值（JSON 字符串）
     * @param isDaily     true=每日刷新, false=每周刷新
     */
    public void saveOrUpdateCharacterExtendValue(String extendName, String extendValue, boolean isDaily) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getId()), isDaily ? ExtendType.CHARACTER_EXTEND_DAILY.getType() : ExtendType.CHARACTER_EXTEND_WEEKLY.getType(),
                extendName, extendValue);
    }

    /**
     * 永久保存或更新账号扩展表指定字段的值。
     *
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值（JSON 字符串）
     */
    public void saveOrUpdateAccountExtendValue(String extendName, String extendValue) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getAccountId()), ExtendType.ACCOUNT_EXTEND.getType(), extendName, extendValue);
    }

    /**
     * 保存每日/每周账号扩展表某字段的值。
     *
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值（JSON 字符串）
     * @param isDaily     true=每日刷新, false=每周刷新
     */
    public void saveOrUpdateAccountExtendValue(String extendName, String extendValue, boolean isDaily) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getAccountId()), isDaily ? ExtendType.ACCOUNT_EXTEND_DAILY.getType() : ExtendType.ACCOUNT_EXTEND_WEEKLY.getType(),
                extendName, extendValue);
    }

    /** 给予装备（检查空间后添加到背包） */
    public void gainEquip(Equip equip) {
        if (!InventoryManipulator.checkSpace(getClient(), equip.getItemId(), 1, equip.getOwner())) {
            message(I18nUtil.getMessage("AbstractPlayerInteraction.gainEquip.message2", InventoryType.EQUIP.getName()));
        }
        InventoryManipulator.addFromDrop(getClient(), equip, false);
    }

    /**
     * 获取当前角色的在线时间。
     *
     * @return 在线时间，单位：分钟
     */
    public int getOnlineTime()
    {
        return getPlayer().getCurrentOnlineTime();
    }

    // ==================== 轮回石碑（Samsara Stone）====================

    /**
     * 召唤轮回石碑NPC并加速当前地图怪物刷新。
     *
     * @param npcId           石碑NPC模板ID
     * @param durationMinutes 持续时间（分钟）
     * @param accelerationRate 重生加速倍率（如 0.3f = 30%原始重生时间，约3.3倍速）
     */
    public void summonSamsaraStone(int npcId, int durationMinutes, float accelerationRate) {
        getPlayer().getMap().spawnSamsaraStoneNpc(getPlayer(), npcId, durationMinutes, accelerationRate);
    }

    /** 移除当前地图的轮回石碑，恢复正常刷怪速度 */
    public void removeSamsaraStone() {
        getPlayer().getMap().removeSamsaraStoneNpc();
    }

    /** 当前地图是否有轮回石碑 */
    public boolean hasSamsaraStone() {
        return getPlayer().getMap().hasSamsaraStone();
    }

    /** 获取当前地图轮回石碑所有者（可能为null） */
    public Character getSamsaraOwner() {
        return getPlayer().getMap().getSamsaraOwner();
    }

    /** 获取轮回石碑过期时间戳（毫秒），无石碑时返回0 */
    public long getSamsaraExpireTime() {
        return getPlayer().getMap().getSamsaraExpireTime();
    }

    // ==================== 轮回石碑 END ====================

}