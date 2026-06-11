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

import org.gms.client.*;
import org.gms.client.Character;
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
import java.util.*;
import java.util.List;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 抽象玩家交互基类
 * 为脚本提供与玩家操作的所有方法，包括物品操作、任务更新、技能学习、NPC对话等
 */
public class AbstractPlayerInteraction {

    /** SLF4J日志实例 */
    private static final Logger log = LoggerFactory.getLogger(AbstractPlayerInteraction.class);

    /** 客户端引用 */
    public Client c;

    /**
     * 构造函数
     *
     * @param c 客户端
     */
    public AbstractPlayerInteraction(Client c) {
        this.c = c;
    }

    /**
     * 获取客户端
     *
     * @return 客户端
     */
    public Client getClient() {
        return c;
    }

    /**
     * 获取当前角色
     *
     * @return 角色对象
     */
    public Character getPlayer() {
        return c.getPlayer();
    }

    /**
     * 获取当前角色（别名，同getPlayer）
     *
     * @return 角色对象
     */
    public Character getChar() {
        return c.getPlayer();
    }

    /**
     * 获取当前角色的职业ID
     *
     * @return 职业ID
     */
    public int getJobId() {
        return getPlayer().getJob().getId();
    }

    /**
     * 获取当前角色的职业
     *
     * @return 职业对象
     */
    public Job getJob() {
        return getPlayer().getJob();
    }

    /**
     * 获取当前角色等级
     *
     * @return 等级
     */
    public int getLevel() {
        return getPlayer().getLevel();
    }

    /**
     * 获取当前角色所在的地图
     *
     * @return 地图对象
     */
    public MapleMap getMap() {
        return c.getPlayer().getMap();
    }

    /**
     * 获取当前系统时间的小时数
     *
     * @return 小时（0-23）
     */
    public int getHourOfDay() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取市场传送门ID
     *
     * @param mapId 地图ID
     * @return 传送门ID
     */
    public int getMarketPortalId(int mapId) {
        return getMarketPortalId(getWarpMap(mapId));
    }

    /**
     * 获取指定地图的市场传送门ID
     * 优先查找市场传送门，若无则返回随机玩家出生点
     *
     * @param map 地图对象
     * @return 传送门ID
     */
    private int getMarketPortalId(MapleMap map) {
        return (map.findMarketPortal() != null) ? map.findMarketPortal().getId() : map.getRandomPlayerSpawnpoint().getId();
    }

    /**
     * 将玩家传送到指定地图
     *
     * @param mapid 地图ID
     */
    public void warp(int mapid) {
        getPlayer().changeMap(mapid);
    }

    /**
     * 将玩家传送到指定地图的指定传送门
     *
     * @param map    地图ID
     * @param portal 传送门ID
     */
    public void warp(int map, int portal) {
        getPlayer().changeMap(map, portal);
    }

    /**
     * 将玩家传送到指定地图的指定传送门
     *
     * @param map    地图ID
     * @param portal 传送门名称
     */
    public void warp(int map, String portal) {
        getPlayer().changeMap(map, portal);
    }

    /**
     * 将当前地图上的所有玩家传送到指定地图
     *
     * @param map 地图ID
     */
    public void warpMap(int map) {
        getPlayer().getMap().warpEveryone(map);
    }

    /**
     * 将队伍所有成员传送到指定地图（默认0号传送门）
     *
     * @param id 地图ID
     */
    public void warpParty(int id) {
        warpParty(id, 0);
    }

    /**
     * 将队伍中在当前地图范围的成员传送到指定地图
     *
     * @param id       目标地图ID
     * @param portalId 传送门ID
     */
    public void warpParty(int id, int portalId) {
        int mapid = getMapId();
        warpParty(id, portalId, mapid, mapid);
    }

    /**
     * 将队伍成员传送到指定地图（通过传送门名称）
     *
     * @param map        目标地图ID
     * @param portalName 传送门名称
     */
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

    /**
     * 将指定地图范围内的队伍成员传送到指定地图
     *
     * @param id        目标地图ID
     * @param fromMinId 来源地图ID下限
     * @param fromMaxId 来源地图ID上限
     */
    public void warpParty(int id, int fromMinId, int fromMaxId) {
        warpParty(id, 0, fromMinId, fromMaxId);
    }

    /**
     * 将指定地图范围内的队伍成员传送到指定地图
     *
     * @param id        目标地图ID
     * @param portalId  传送门ID
     * @param fromMinId 来源地图ID下限
     * @param fromMaxId 来源地图ID上限
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

    /**
     * 获取目标地图对象（经过传送验证）
     *
     * @param map 地图ID
     * @return 地图对象
     */
    public MapleMap getWarpMap(int map) {
        return getPlayer().getWarpMap(map);
    }

    /**
     * 获取地图对象
     *
     * @param map 地图ID
     * @return 地图对象
     */
    public MapleMap getMap(int map) {
        return getWarpMap(map);
    }

    /**
     * 统计指定地图上的怪物数量
     *
     * @param map 地图ID
     * @return 怪物数量
     */
    public int countAllMonstersOnMap(int map) {
        return getMap(map).countMonsters();
    }

    /**
     * 统计当前地图上的怪物数量
     *
     * @return 怪物数量
     */
    public int countMonster() {
        return getPlayer().getMap().countMonsters();
    }

    /**
     * 重置指定地图的对象（反应堆/怪物/物品）
     *
     * @param mapid 地图ID
     */
    public void resetMapObjects(int mapid) {
        getWarpMap(mapid).resetMapObjects();
    }

    /**
     * 获取事件管理器
     *
     * @param event 事件名称
     * @return 事件管理器
     */
    public EventManager getEventManager(String event) {
        return getClient().getEventManager(event);
    }

    /**
     * 获取当前角色的事件实例
     *
     * @return 事件实例管理器
     */
    public EventInstanceManager getEventInstance() {
        return getPlayer().getEventInstance();
    }

    /**
     * 获取指定类型的背包
     *
     * @param type 背包类型（参考InventoryType枚举：-1=已装备 0=未定义 1=装备 2=消耗 3=装饰 4=其他 5=现金 6=容器）
     * @return 背包对象
     */
    public Inventory getInventory(int type) {
        return getPlayer().getInventory(InventoryType.getByType((byte) type));
    }

    /**
     * 获取指定类型的背包
     *
     * @param type 背包类型枚举
     * @return 背包对象
     */
    public Inventory getInventory(InventoryType type) {
        return getPlayer().getInventory(type);
    }

    /**
     * 检查玩家是否拥有指定物品（至少1个）
     *
     * @param itemid 物品ID
     * @return 是否拥有
     */
    public boolean hasItem(int itemid) {
        return haveItem(itemid, 1);
    }

    /**
     * 检查玩家是否拥有指定数量的物品
     *
     * @param itemid   物品ID
     * @param quantity 数量
     * @return 是否拥有
     */
    public boolean hasItem(int itemid, int quantity) {
        return haveItem(itemid, quantity);
    }

    /**
     * 检查玩家是否拥有指定物品（至少1个）
     *
     * @param itemid 物品ID
     * @return 是否拥有
     */
    public boolean haveItem(int itemid) {
        return haveItem(itemid, 1);
    }

    /**
     * 检查玩家是否拥有指定数量的物品
     *
     * @param itemid   物品ID
     * @param quantity 数量
     * @return 是否拥有
     */
    public boolean haveItem(int itemid, int quantity) {
        return getPlayer().getItemQuantity(itemid, false) >= quantity;
    }

    /**
     * 获取物品数量
     *
     * @param itemid 物品ID
     * @return 物品数量
     */
    public int getItemQuantity(int itemid) {
        return getPlayer().getItemQuantity(itemid, false);
    }

    /**
     * 检查是否拥有指定ID的物品
     *
     * @param itemid 物品ID
     * @return 是否拥有
     */
    public boolean haveItemWithId(int itemid) {
        return haveItemWithId(itemid, false);
    }

    /**
     * 检查是否拥有指定ID的物品（含装备栏）
     *
     * @param itemid        物品ID
     * @param checkEquipped 是否检查已装备
     * @return 是否拥有
     */
    public boolean haveItemWithId(int itemid, boolean checkEquipped) {
        return getPlayer().haveItemWithId(itemid, checkEquipped);
    }

    /**
     * 检查背包是否可以容纳指定物品
     *
     * @param itemid 物品ID
     * @return 是否可以容纳
     */
    public boolean canHold(int itemid) {
        return canHold(itemid, 1);
    }

    /**
     * 检查背包是否可以容纳指定数量的物品
     *
     * @param itemid   物品ID
     * @param quantity 数量
     * @return 是否可以容纳
     */
    public boolean canHold(int itemid, int quantity) {
        return canHoldAll(Collections.singletonList(itemid), Collections.singletonList(quantity), true);
    }

    /**
     * 检查在移除指定物品后能否容纳新物品
     *
     * @param itemid         要添加的物品ID
     * @param quantity       要添加的数量
     * @param removeItemid   要移除的物品ID
     * @param removeQuantity 要移除的数量
     * @return 是否可以容纳
     */
    public boolean canHold(int itemid, int quantity, int removeItemid, int removeQuantity) {
        return canHoldAllAfterRemoving(Collections.singletonList(itemid), Collections.singletonList(quantity), Collections.singletonList(removeItemid), Collections.singletonList(removeQuantity));
    }

    /**
     * 将Object列表转换为Integer列表
     *
     * @param objects Object列表
     * @return Integer列表
     */
    private List<Integer> convertToIntegerList(List<Object> objects) {
        List<Integer> intList = new ArrayList<>();

        for (Object object : objects) {
            intList.add((Integer) object);
        }

        return intList;
    }

    /**
     * 检查背包是否可以容纳多个物品（每种1个）
     *
     * @param itemids 物品ID列表
     * @return 是否可以容纳
     */
    public boolean canHoldAll(List<Object> itemids) {
        List<Object> quantity = new LinkedList<>();

        final int intOne = 1;
        for (int i = 0; i < itemids.size(); i++) {
            quantity.add(intOne);
        }

        return canHoldAll(itemids, quantity);
    }

    /**
     * 检查背包是否可以容纳多个物品
     *
     * @param itemids  物品ID列表
     * @param quantity 数量列表
     * @return 是否可以容纳
     */
    public boolean canHoldAll(List<Object> itemids, List<Object> quantity) {
        return canHoldAll(convertToIntegerList(itemids), convertToIntegerList(quantity), true);
    }

    /**
     * 检查背包是否可以容纳多个物品（内部实现）
     */
    private boolean canHoldAll(List<Integer> itemids, List<Integer> quantity, boolean isInteger) {
        int size = Math.min(itemids.size(), quantity.size());

        List<Pair<Item, InventoryType>> addedItems = new LinkedList<>();
        for (int i = 0; i < size; i++) {
            Item it = new Item(itemids.get(i), (short) 0, quantity.get(i).shortValue());
            addedItems.add(new Pair<>(it, ItemConstants.getInventoryType(itemids.get(i))));
        }

        return Inventory.checkSpots(c.getPlayer(), addedItems);
    }

    private List<Pair<Item, InventoryType>> prepareProofInventoryItems(List<Pair<Integer, Integer>> items) {
        List<Pair<Item, InventoryType>> addedItems = new LinkedList<>();
        for (Pair<Integer, Integer> p : items) {
            Item it = new Item(p.getLeft(), (short) 0, p.getRight().shortValue());
            addedItems.add(new Pair<>(it, InventoryType.CANHOLD));
        }

        return addedItems;
    }

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
     * 检查在移除指定物品后能否容纳新物品列表
     *
     * @param toAddItemids      要添加的物品ID列表
     * @param toAddQuantity     要添加的数量列表
     * @param toRemoveItemids   要移除的物品ID列表
     * @param toRemoveQuantity  要移除的数量列表
     * @return 是否可以容纳
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

    //---- \/ \/ \/ \/ \/ \/ \/  NOT TESTED  \/ \/ \/ \/ \/ \/ \/ \/ \/ ----

    /**
     * 获取任务记录（存在则返回，不存在则创建）
     *
     * @param id 任务ID
     * @return 任务状态
     */
    public final QuestStatus getQuestRecord(final int id) {
        return c.getPlayer().getQuestNAdd(Quest.getInstance(id));
    }

    /**
     * 获取任务记录（不存在返回null）
     *
     * @param id 任务ID
     * @return 任务状态
     */
    public final QuestStatus getQuestNoRecord(final int id) {
        return c.getPlayer().getQuestNoAdd(Quest.getInstance(id));
    }

    //---- /\ /\ /\ /\ /\ /\ /\  NOT TESTED  /\ /\ /\ /\ /\ /\ /\ /\ /\ ----

    /**
     * 打开NPC对话
     *
     * @param npcid NPC ID
     */
    public void openNpc(int npcid) {
        openNpc(npcid, null);
    }

    /**
     * 打开指定脚本的NPC对话
     *
     * @param npcid  NPC ID
     * @param script 脚本文件名
     */
    public void openNpc(int npcid, String script) {
        if (c.getCM() != null) {
            return;
        }

        c.removeClickedNPC();
        NPCScriptManager.getInstance().dispose(c);
        NPCScriptManager.getInstance().start(c, npcid, script, null);
    }

    /**
     * 获取任务状态ID
     *
     * @param id 任务ID
     * @return 状态ID
     */
    public int getQuestStatus(int id) {
        return c.getPlayer().getQuest(Quest.getInstance(id)).getStatus().getId();
    }

    /**
     * 获取任务状态枚举
     *
     * @param id 任务ID
     * @return 任务状态
     */
    private QuestStatus.Status getQuestStat(int id) {
        return c.getPlayer().getQuest(Quest.getInstance(id)).getStatus();
    }

    /**
     * 检查任务是否已完成
     *
     * @param id 任务ID
     * @return 是否已完成
     */
    public boolean isQuestCompleted(int id) {
        try {
            return getQuestStat(id) == QuestStatus.Status.COMPLETED;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检查任务是否正在进行中
     *
     * @param id 任务ID
     * @return 是否正在进行
     */
    public boolean isQuestActive(int id) {
        return isQuestStarted(id);
    }

    /**
     * 检查任务是否已开始
     *
     * @param id 任务ID
     * @return 是否已开始
     */
    public boolean isQuestStarted(int id) {
        try {
            return getQuestStat(id) == QuestStatus.Status.STARTED;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 设置任务进度（字符串）
     *
     * @param id       任务ID
     * @param progress 进度值
     */
    public void setQuestProgress(int id, String progress) {
        setQuestProgress(id, 0, progress);
    }

    /**
     * 设置任务进度（整型）
     *
     * @param id       任务ID
     * @param progress 进度值
     */
    public void setQuestProgress(int id, int progress) {
        setQuestProgress(id, 0, "" + progress);
    }

    /**
     * 设置任务进度（指定信息编号）
     *
     * @param id         任务ID
     * @param infoNumber 信息编号
     * @param progress   进度值
     */
    public void setQuestProgress(int id, int infoNumber, int progress) {
        setQuestProgress(id, infoNumber, "" + progress);
    }

    /**
     * 设置任务进度（指定信息编号和字符串值）
     *
     * @param id         任务ID
     * @param infoNumber 信息编号
     * @param progress   进度值
     */
    public void setQuestProgress(int id, int infoNumber, String progress) {
        c.getPlayer().setQuestProgress(id, infoNumber, progress);
    }

    /**
     * 获取任务进度（infoNumber=0）
     *
     * @param id 任务ID
     * @return 进度值
     */
    public String getQuestProgress(int id) {
        return getQuestProgress(id, 0);
    }

    /**
     * 获取任务进度（指定信息编号）
     *
     * @param id         任务ID
     * @param infoNumber 信息编号
     * @return 进度值
     */
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

    /**
     * 获取任务进度（整型，infoNumber=0）
     *
     * @param id 任务ID
     * @return 进度整数值
     */
    public int getQuestProgressInt(int id) {
        try {
            return Integer.parseInt(getQuestProgress(id));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * 获取任务进度（整型，指定信息编号）
     *
     * @param id         任务ID
     * @param infoNumber 信息编号
     * @return 进度整数值
     */
    public int getQuestProgressInt(int id, int infoNumber) {
        try {
            return Integer.parseInt(getQuestProgress(id, infoNumber));
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * 重置所有任务进度
     *
     * @param id 任务ID
     */
    public void resetAllQuestProgress(int id) {
        QuestStatus qs = getPlayer().getQuest(Quest.getInstance(id));
        if (qs != null) {
            qs.resetAllProgress();
            getPlayer().announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
        }
    }

    /**
     * 重置指定信息的任务进度
     *
     * @param id         任务ID
     * @param infoNumber 信息编号
     */
    public void resetQuestProgress(int id, int infoNumber) {
        QuestStatus qs = getPlayer().getQuest(Quest.getInstance(id));
        if (qs != null) {
            qs.resetProgress(infoNumber);
            getPlayer().announceUpdateQuest(DelayedQuestUpdate.UPDATE, qs, false);
        }
    }

    /**
     * 强制开始任务（通过GM管理员NPC）
     */
    public boolean forceStartQuest(int id) {
        return forceStartQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

    /**
     * 强制开始任务（通过指定NPC）
     */
    public boolean forceStartQuest(int id, int npc) {
        return startQuest(id, npc);
    }

    /**
     * 强制完成任务（通过GM管理员NPC）
     */
    public boolean forceCompleteQuest(int id) {
        return forceCompleteQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

    /**
     * 强制完成任务（通过指定NPC）
     */
    public boolean forceCompleteQuest(int id, int npc) {
        return completeQuest(id, npc);
    }

    /**
     * 开始任务（short ID别名）
     */
    public boolean startQuest(short id) {
        return startQuest((int) id);
    }

    /**
     * 完成任务（short ID别名）
     */
    public boolean completeQuest(short id) {
        return completeQuest((int) id);
    }

    /**
     * 开始任务（通过GM管理员NPC）
     */
    public boolean startQuest(int id) {
        return startQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

    /**
     * 完成任务（通过GM管理员NPC）
     */
    public boolean completeQuest(int id) {
        return completeQuest(id, NpcId.MAPLE_ADMINISTRATOR);
    }

    /**
     * 开始任务（short ID，指定NPC）
     */
    public boolean startQuest(short id, int npc) {
        return startQuest((int) id, npc);
    }

    /**
     * 完成任务（short ID，指定NPC）
     */
    public boolean completeQuest(short id, int npc) {
        return completeQuest((int) id, npc);
    }

    /**
     * 开始任务（指定NPC）
     *
     * @param id  任务ID
     * @param npc NPC ID
     * @return 是否成功
     */
    public boolean startQuest(int id, int npc) {
        try {
            return Quest.getInstance(id).forceStart(getPlayer(), npc);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 完成任务（指定NPC）
     *
     * @param id  任务ID
     * @param npc NPC ID
     * @return 是否成功
     */
    public boolean completeQuest(int id, int npc) {
        try {
            return Quest.getInstance(id).forceComplete(getPlayer(), npc);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * 进化宠物
     * 将指定槽位的宠物替换为新ID的宠物
     *
     * @param slot    宠物槽位
     * @param afterId 进化后的宠物ID
     * @return 进化后的物品（宠物），失败返回null
     */
    public Item evolvePet(byte slot, int afterId) {
        Pet evolved = null;
        Pet target;

        // refreshes expiration date: 90 days
        long period = DAYS.toMillis(90);


        target = getPlayer().getPet(slot);
        if (target == null) {
            getPlayer().message("Pet could not be evolved...");
            return (null);
        }

        Item tmp = gainItem(afterId, (short) 1, false, true, period, target);
            
            // evolved = Pet.loadFromDb(tmp.getItemId(), tmp.getPosition(), tmp.getPetId());
            //
            // evolved = tmp.getPet();
            // if(evolved == null) {
            //     getPlayer().message("Pet structure non-existent for " + tmp.getItemId() + "...");
            //     return(null);
            // }
            // else if(tmp.getPetId() == -1) {
            //     getPlayer().message("Pet id -1");
            //     return(null);
            // }
            //
            // getPlayer().addPet(evolved);
            //
            // getPlayer().getMap().broadcastMessage(c.getPlayer(), PacketCreator.showPet(c.getPlayer(), evolved, false, false), true);
            // c.sendPacket(PacketCreator.petStatUpdate(c.getPlayer()));
            // c.sendPacket(PacketCreator.enableActions());
            // chr.getClient().getWorldServer().registerPetHunger(chr, chr.getPetIndex(evolved));

        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, target.getPosition(), (short) 1, false);

        return evolved;
    }

    /**
     * 给予玩家物品（默认数量1，随机属性关闭，显示消息）
     *
     * @param id       物品ID
     * @param quantity 数量
     */
    public void gainItem(int id, short quantity) {
        gainItem(id, quantity, false, true);
    }

    /**
     * 给予玩家物品
     *
     * @param id       物品ID
     * @param quantity 数量
     * @param show     是否显示消息
     */
    public void gainItem(int id, short quantity, boolean show) {
        // this will fk randomStats equip :P
        gainItem(id, quantity, false, show);
    }

    /**
     * 给予玩家物品（默认数量1，随机属性关闭）
     *
     * @param id   物品ID
     * @param show 是否显示消息
     */
    public void gainItem(int id, boolean show) {
        gainItem(id, (short) 1, false, show);
    }

    /**
     * 给予玩家物品（默认数量1，随机属性关闭，显示消息）
     *
     * @param id 物品ID
     */
    public void gainItem(int id) {
        gainItem(id, (short) 1, false, true);
    }

    /**
     * 给予玩家物品（默认不设过期时间）
     */
    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage) {
        return gainItem(id, quantity, randomStats, showMessage, -1);
    }

    /**
     * 给予玩家物品（设置过期时间，无宠物来源）
     */
    public Item gainItem(int id, short quantity, boolean randomStats, boolean showMessage, long expires) {
        return gainItem(id, quantity, randomStats, showMessage, expires, null);
    }

    /**
     * 给予玩家物品（核心方法）
     * 处理装备随机属性、宠物创建、背包检查和物品发放
     *
     * @param id          物品ID
     * @param quantity    数量
     * @param randomStats 是否随机属性
     * @param showMessage 是否显示消息
     * @param expires     过期时间（毫秒），-1=永不过期
     * @param from        宠物来源（用于宠物进化）
     * @return 创建的物品对象
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
                c.getPlayer().dropMessage(1, "您的背包已满，请从" + ItemConstants.getInventoryType(id).name() + "栏移除一件物品。");
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

    /**
     * 增加/减少玩家名誉值
     *
     * @param delta 变化量
     */
    public void gainFame(int delta) {
        getPlayer().gainFame(delta);
    }

    /**
     * 更改地图背景音乐
     *
     * @param songName 音乐文件名
     */
    public void changeMusic(String songName) {
        getPlayer().getMap().broadcastMessage(PacketCreator.musicChange(songName));
    }

    /**
     * 向玩家发送服务器通知
     *
     * @param type    消息类型
     * @param message 消息内容
     */
    public void playerMessage(int type, String message) {
        c.sendPacket(PacketCreator.serverNotice(type, message));
    }

    /**
     * 向玩家发送蓝色消息
     *
     * @param message 消息内容
     */
    public void message(String message) {
        getPlayer().message(message);
    }

    /**
     * 向玩家发送掉落消息
     *
     * @param type    消息类型
     * @param message 消息内容
     */
    public void dropMessage(int type, String message) {
        getPlayer().dropMessage(type, message);
    }

    /**
     * 向当前地图广播服务器通知
     *
     * @param type    消息类型
     * @param message 消息内容
     */
    public void mapMessage(int type, String message) {
        getPlayer().getMap().broadcastMessage(PacketCreator.serverNotice(type, message));
    }

    /**
     * 播放地图特效
     *
     * @param path 特效路径
     */
    public void mapEffect(String path) {
        c.sendPacket(PacketCreator.mapEffect(path));
    }

    /**
     * 播放地图音效
     *
     * @param path 音效路径
     */
    public void mapSound(String path) {
        c.sendPacket(PacketCreator.mapSound(path));
    }

    /**
     * 显示战神（Aran）开场剧情
     */
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

    /**
     * 显示开场动画
     *
     * @param path 动画路径
     */
    public void showIntro(String path) {
        c.sendPacket(PacketCreator.showIntro(path));
    }

    /**
     * 显示信息窗口
     *
     * @param path 信息路径
     */
    public void showInfo(String path) {
        c.sendPacket(PacketCreator.showInfo(path));
        c.sendPacket(PacketCreator.enableActions());
    }

    /**
     * 向公会发送消息
     *
     * @param type    消息类型
     * @param message 消息内容
     */
    public void guildMessage(int type, String message) {
        if (getGuild() != null) {
            getGuild().guildMessage(PacketCreator.serverNotice(type, message));
        }
    }

    /**
     * 获取当前角色所属公会
     *
     * @return 公会对象
     */
    public Guild getGuild() {
        try {
            return Server.getInstance().getGuild(getPlayer().getGuildId(), getPlayer().getWorld(), null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前角色所在队伍
     *
     * @return 队伍对象
     */
    public Party getParty() {
        return getPlayer().getParty();
    }

    /**
     * 是否队长（别名）
     *
     * @return true表示是队长
     */
    public boolean isLeader() {
        return isPartyLeader();
    }

    /**
     * 是否为公会会长
     *
     * @return true表示是会长
     */
    public boolean isGuildLeader() {
        return getPlayer().isGuildLeader();
    }

    /**
     * 是否为队伍队长
     *
     * @return true表示是队长
     */
    public boolean isPartyLeader() {
        if (getParty() == null) {
            return false;
        }

        return getParty().getLeaderId() == getPlayer().getId();
    }

    /**
     * 是否为事件队伍队长
     *
     * @return true表示是事件队长
     */
    public boolean isEventLeader() {
        return getEventInstance() != null && getPlayer().getId() == getEventInstance().getLeaderId();
    }

    /**
     * 向队伍成员发放物品
     *
     * @param id       物品ID
     * @param quantity 数量
     * @param party    队伍成员列表
     */
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

    /**
     * 移除HPQ相关物品
     */
    public void removeHPQItems() {
        int[] items = {ItemId.GREEN_PRIMROSE_SEED, ItemId.PURPLE_PRIMROSE_SEED, ItemId.PINK_PRIMROSE_SEED,
                ItemId.BROWN_PRIMROSE_SEED, ItemId.YELLOW_PRIMROSE_SEED, ItemId.BLUE_PRIMROSE_SEED};
        for (int item : items) {
            removePartyItems(item);
        }
    }

    /**
     * 从队伍成员背包中移除指定物品
     *
     * @param id 物品ID
     */
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

    /**
     * 给予指定角色经验值
     *
     * @param amount 经验值
     * @param chr    角色
     */
    public void giveCharacterExp(int amount, Character chr) {
        chr.gainExp(NumberTool.floatToInt(amount * chr.getExpRate()), true, true);
    }

    /**
     * 向队伍成员发放经验值
     *
     * @param amount 经验值
     * @param party  队伍成员列表
     */
    public void givePartyExp(int amount, List<Character> party) {
        for (Character chr : party) {
            giveCharacterExp(amount, chr);
        }
    }

    /**
     * 根据组队任务（PQ）发放经验值给队伍
     *
     * @param PQ 组队任务名称
     */
    public void givePartyExp(String PQ) {
        givePartyExp(PQ, true);
    }

    /**
     * 根据组队任务（PQ）发放经验值
     * 队伍人数越多，经验奖励加成越高（4人+10%, 5人+20%, 6人+30%）
     *
     * @param PQ       组队任务名称
     * @param instance 是否仅计算事件实例中的玩家
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

    /**
     * 从队伍成员背包中移除指定物品
     *
     * @param id    物品ID
     * @param party 队伍成员列表
     */
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

    /**
     * 从当前玩家背包中移除指定物品（全部）
     *
     * @param id 物品ID
     */
    public void removeAll(int id) {
        removeAll(id, c);
    }

    /**
     * 从指定客户端玩家背包中移除指定物品（全部）
     *
     * @param id 物品ID
     * @param cl 客户端
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

    /**
     * 清空指定背包栏中的所有物品
     *
     * @param invType 背包类型
     */
    public void removeAllByInventory(int invType) {
        Inventory inv = getInventory(invType);
        for (Item item : new ArrayList<>(inv.list())) {
            InventoryManipulator.removeFromSlot(c, inv.getType(), item.getPosition(), item.getQuantity(), false);
        }
    }

    /**
     * 从指定背包栏槽位移除物品
     *
     * @param invType 背包类型
     * @param slot    槽位
     */
    public void removeAllByInventorySlot(int invType, short slot) {
        Inventory inv = getInventory(invType);
        Item item = inv.getItem(slot);
        if (item != null) {
            InventoryManipulator.removeFromSlot(c, inv.getType(), item.getPosition(), item.getQuantity(), false);
        }
    }

    /**
     * 获取当前地图ID
     *
     * @return 地图ID
     */
    public int getMapId() {
        return c.getPlayer().getMap().getId();
    }

    /**
     * 获取指定地图上的玩家数量
     *
     * @param mapid 地图ID
     * @return 玩家数量
     */
    public int getPlayerCount(int mapid) {
        return c.getChannelServer().getMapFactory().getMap(mapid).getCharacters().size();
    }

    /**
     * 显示提示信息
     *
     * @param msg    提示消息
     * @param width  宽度
     * @param height 高度
     */
    public void showInstruction(String msg, int width, int height) {
        c.sendPacket(PacketCreator.sendHint(msg, width, height));
        c.sendPacket(PacketCreator.enableActions());
    }

    /**
     * 禁用小地图显示
     */
    public void disableMinimap() {
        c.sendPacket(PacketCreator.disableMinimap());
    }

    /**
     * 检查所有指定反应堆是否处于指定状态
     *
     * @param reactorId 反应堆ID
     * @param state     目标状态
     * @return 是否全部符合
     */
    public boolean isAllReactorState(final int reactorId, final int state) {
        return c.getPlayer().getMap().isAllReactorState(reactorId, state);
    }

    /**
     * 重置地图（重置反应堆、清除怪物和掉落物）
     *
     * @param mapid 地图ID
     */
    public void resetMap(int mapid) {
        getMap(mapid).resetReactors();
        getMap(mapid).killAllMonsters();
        for (MapObject i : getMap(mapid).getMapObjectsInRange(c.getPlayer().getPosition(), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.ITEM))) {
            getMap(mapid).removeMapObject(i);
            getMap(mapid).broadcastMessage(PacketCreator.removeItemFromMap(i.getObjectId(), 0, c.getPlayer().getId()));
        }
    }

    /**
     * 使用物品效果
     *
     * @param id 物品ID
     */
    public void useItem(int id) {
        ItemInformationProvider.getInstance().getItemEffect(id).applyTo(c.getPlayer());
        // Useful shet :3
        c.sendPacket(PacketCreator.getItemMessage(id));
    }

    /**
     * 取消物品效果
     *
     * @param id 物品ID
     */
    public void cancelItem(final int id) {
        getPlayer().cancelEffect(ItemInformationProvider.getInstance().getItemEffect(id), false, -1);
    }

    /**
     * 教授技能 永久
     *
     * @param skillid     技能ID
     * @param level       技能等级
     * @param masterLevel 掌握等级
     */
    public void teachSkill(int skillid, byte level, byte masterLevel) {
        teachSkill(skillid, level, masterLevel, -1, false);
    }

    /**
     * 教授技能
     *
     * @param skillid     技能ID
     * @param level       技能等级
     * @param masterLevel 掌握等级
     * @param expiration  过期时间（毫秒）
     */
    public void teachSkill(int skillid, byte level, byte masterLevel, long expiration) {
        teachSkill(skillid, level, masterLevel, expiration, false);
    }

    /**
     * 教授技能（可强制覆盖）
     *
     * @param skillid     技能ID
     * @param level       技能等级
     * @param masterLevel 掌握等级
     * @param expiration  过期时间（毫秒）
     * @param force       是否强制覆盖现有等级
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

    /**
     * 从已装备栏指定槽位移除装备
     *
     * @param slot 槽位
     */
    public void removeEquipFromSlot(short slot) {
        Item tempItem = c.getPlayer().getInventory(InventoryType.EQUIPPED).getItem(slot);
        InventoryManipulator.removeFromSlot(c, InventoryType.EQUIPPED, slot, tempItem.getQuantity(), false, false);
    }

    /**
     * 获得装备并直接装备到指定槽位
     *
     * @param itemid 装备ID
     * @param slot   装备槽位
     */
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

    /**
     * 在地图上生成NPC
     *
     * @param npcId NPC ID
     * @param pos   位置坐标
     * @param map   目标地图
     */
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

    /**
     * 在当前地图生成怪物
     *
     * @param id 怪物ID
     * @param x  X坐标
     * @param y  Y坐标
     */
    public void spawnMonster(int id, int x, int y) {
        Monster monster = LifeFactory.getMonster(id);
        monster.setPosition(new Point(x, y));
        getPlayer().getMap().spawnMonster(monster);
    }

    /**
     * 通过ID获取怪物实例
     *
     * @param mid 怪物ID
     * @return 怪物对象
     */
    public Monster getMonsterLifeFactory(int mid) {
        return LifeFactory.getMonster(mid);
    }

    /**
     * 显示新手引导精灵
     */
    public void spawnGuide() {
        c.sendPacket(PacketCreator.spawnGuide(true));
    }

    /**
     * 隐藏新手引导精灵
     */
    public void removeGuide() {
        c.sendPacket(PacketCreator.spawnGuide(false));
    }

    /**
     * 显示指定编号的教程引导
     *
     * @param num 教程编号
     */
    public void displayGuide(int num) {
        c.sendPacket(PacketCreator.showInfo("UI/tutorial.img/" + num));
    }

    /**
     * 道场向上传送
     */
    public void goDojoUp() {
        c.sendPacket(PacketCreator.dojoWarpUp());
    }

    /**
     * 重置道场能量
     */
    public void resetDojoEnergy() {
        c.getPlayer().setDojoEnergy(0);
    }

    /**
     * 重置队伍所有成员的道场能量
     */
    public void resetPartyDojoEnergy() {
        for (Character pchr : c.getPlayer().getPartyMembersOnSameMap()) {
            pchr.setDojoEnergy(0);
        }
    }

    /**
     * 启用玩家操作（发送enableActions包）
     */
    public void enableActions() {
        c.sendPacket(PacketCreator.enableActions());
    }

    /**
     * 显示特效
     *
     * @param effect 特效名称
     */
    public void showEffect(String effect) {
        c.sendPacket(PacketCreator.showEffect(effect));
    }

    /**
     * 更新道场能量显示
     */
    public void dojoEnergy() {
        c.sendPacket(PacketCreator.getEnergy("energy", getPlayer().getDojoEnergy()));
    }

    /**
     * 显示对话引导消息
     *
     * @param message 消息内容
     */
    public void talkGuide(String message) {
        c.sendPacket(PacketCreator.talkGuide(message));
    }

    /**
     * 显示引导提示
     *
     * @param hint 提示编号
     */
    public void guideHint(int hint) {
        c.sendPacket(PacketCreator.guideHint(hint));
    }

    /**
     * 更新区域信息
     *
     * @param area 区域ID
     * @param info 信息内容
     */
    public void updateAreaInfo(Short area, String info) {
        c.getPlayer().updateAreaInfo(area, info);
        // idk, nexon does the same :P
        c.sendPacket(PacketCreator.enableActions());
    }

    /**
     * 检查区域信息是否包含指定内容
     *
     * @param area 区域ID
     * @param info 信息内容
     * @return 是否包含
     */
    public boolean containsAreaInfo(short area, String info) {
        return c.getPlayer().containsAreaInfo(area, info);
    }

    /**
     * 显示称号获得消息
     *
     * @param msg 称号消息
     */
    public void earnTitle(String msg) {
        c.sendPacket(PacketCreator.earnTitleMessage(msg));
    }

    /**
     * 显示信息文本
     *
     * @param msg 信息文本
     */
    public void showInfoText(String msg) {
        c.sendPacket(PacketCreator.showInfoText(msg));
    }

    /**
     * 打开UI面板
     *
     * @param ui UI类型
     */
    public void openUI(byte ui) {
        c.sendPacket(PacketCreator.openUI(ui));
    }

    /**
     * 锁定UI操作
     */
    public void lockUI() {
        c.sendPacket(PacketCreator.disableUI(true));
        c.sendPacket(PacketCreator.lockUI(true));
    }

    /**
     * 解锁UI操作
     */
    public void unlockUI() {
        c.sendPacket(PacketCreator.disableUI(false));
        c.sendPacket(PacketCreator.lockUI(false));
    }

    /**
     * 播放音效
     *
     * @param sound 音效名称
     */
    public void playSound(String sound) {
        getPlayer().getMap().broadcastMessage(PacketCreator.environmentChange(sound, 4));
    }

    /**
     * 更改地图环境效果
     *
     * @param env  环境效果名称
     * @param mode 模式
     */
    public void environmentChange(String env, int mode) {
        getPlayer().getMap().broadcastMessage(PacketCreator.environmentChange(env, mode));
    }

    /**
     * 数字添加千位分隔符
     *
     * @param number 数字
     * @return 格式化后的字符串
     */
    public String numberWithCommas(int number) {
        return GameConstants.numberWithCommas(number);
    }

    /**
     * 获取当前金字塔组队任务
     *
     * @return 金字塔对象
     */
    public Pyramid getPyramid() {
        return (Pyramid) getPlayer().getPartyQuest();
    }

    /**
     * 创建远征队
     *
     * @param type 远征队类型
     * @return 0=成功, 1=次数限制, -1=失败
     */
    public int createExpedition(ExpeditionType type) {
        return createExpedition(type, false, 0, 0);
    }

    /**
     * 创建远征队
     *
     * @param type       远征队类型
     * @param silent     是否静默
     * @param minPlayers 最少人数
     * @param maxPlayers 最多人数
     * @return 0=成功, 1=次数限制, -1=失败
     */
    public int createExpedition(ExpeditionType type, boolean silent, int minPlayers, int maxPlayers) {
        Character player = getPlayer();
        Expedition exped = new Expedition(player, type, silent, minPlayers, maxPlayers);

        int channel = player.getMap().getChannelServer().getId();
        // thanks Conrad for noticing missing expeditions entry limit
        if (!ExpeditionBossLog.attemptBoss(player.getId(), channel, exped, false)) {
            return 1;
        }

        if (exped.addChannelExpedition(player.getClient().getChannelServer())) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * 结束远征队
     *
     * @param exped 远征队对象
     */
    public void endExpedition(Expedition exped) {
        exped.dispose(true);
        exped.removeChannelExpedition(getPlayer().getClient().getChannelServer());
    }

    /**
     * 获取远征队
     *
     * @param type 远征队类型
     * @return 远征队对象
     */
    public Expedition getExpedition(ExpeditionType type) {
        return getPlayer().getClient().getChannelServer().getExpedition(type);
    }

    /**
     * 获取远征队成员名称列表
     *
     * @param type 远征队类型
     * @return 成员名称字符串
     */
    public String getExpeditionMemberNames(ExpeditionType type) {
        String members = "";
        Expedition exped = getExpedition(type);
        for (String memberName : exped.getMembers().values()) {
            members += "" + memberName + ", ";
        }
        return members;
    }

    /**
     * 检查玩家是否为指定远征队队长
     *
     * @param type 远征队类型
     * @return 是否为队长
     */
    public boolean isLeaderExpedition(ExpeditionType type) {
        Expedition exped = getExpedition(type);
        return exped.isLeader(getPlayer());
    }

    /**
     * 获取监禁剩余时间
     *
     * @return 剩余毫秒数
     */
    public long getJailTimeLeft() {
        return getPlayer().getJailExpirationTimeLeft();
    }

    /**
     * 获取已过期的宠物列表
     *
     * @return 过期宠物列表
     */
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

    /**
     * 获取未领取的结婚礼物
     *
     * @return 礼物物品列表
     */
    public List<Item> getUnclaimedMarriageGifts() {
        return Marriage.loadGiftItemsFromDb(this.getClient(), this.getPlayer().getId());
    }

    /**
     * 开启副本实例
     *
     * @param dungeonid 副本ID
     * @return 是否成功
     */
    public boolean startDungeonInstance(int dungeonid) {
        return c.getChannelServer().addMiniDungeon(dungeonid);
    }

    /**
     * 检查是否满足一转属性要求
     *
     * @param jobType 职业类型（1=战士,2=法师,3/4=弓箭手,5=飞侠）
     * @return 是否满足
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

    /**
     * 获取一转属性的需求描述
     *
     * @param jobType 职业类型
     * @return 需求描述
     */
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

    /**
     * 发送NPC对话
     *
     * @param npcid   NPC ID
     * @param message 对话消息
     */
    public void npcTalk(int npcid, String message) {
        c.sendPacket(PacketCreator.getNPCTalk(npcid, (byte) 0, message, "00 00", (byte) 0));
    }

    /**
     * 获取当前服务器时间
     *
     * @return 当前时间戳
     */
    public long getCurrentTime() {
        return Server.getInstance().getCurrentTime();
    }

    /**
     * 削弱区域Boss
     * 对指定Boss施加封印和降低闪避效果，并发送提示消息
     *
     * @param monsterId 怪物ID
     * @param message   提示消息
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

    /**
     * 对怪物施加封印技能
     */
    private void applySealSkill(Monster monster) {
        MobSkill sealSkill = MobSkillFactory.getMobSkillOrThrow(MobSkillType.SEAL_SKILL, 1);
        sealSkill.applyEffect(monster);
    }

    /**
     * 对怪物施加降低闪避技能
     */
    private void applyReduceAvoid(Monster monster) {
        MobSkill reduceAvoidSkill = MobSkillFactory.getMobSkillOrThrow(MobSkillType.EVA, 2);
        reduceAvoidSkill.applyEffect(monster);
    }

    /**
     * 向地图发送蓝色通知消息
     *
     * @param map     地图
     * @param message 消息内容
     */
    private void sendBlueNotice(MapleMap map, String message) {
        map.dropMessage(6, message);
    }

    /////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取角色扩展表某字段的值
     *
     * @param extendName 扩展字段名
     * @return 扩展字段值
     */
    public String getCharacterExtendValue(String extendName) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getId()), ExtendType.CHARACTER_EXTEND.getType(), extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取每日/每周角色扩展表某字段的值
     *
     * @param extendName 扩展字段名
     * @param isDaily    是否是每日，否则为每周
     * @return 扩展字段值
     */
    public String getCharacterExtendValue(String extendName, boolean isDaily) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getId()),
                isDaily ? ExtendType.CHARACTER_EXTEND_DAILY.getType() : ExtendType.CHARACTER_EXTEND_WEEKLY.getType(),
                extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取账号扩展表某字段的值
     *
     * @param extendName 扩展字段名
     * @return 扩展字段值
     */
    public String getAccountExtendValue(String extendName) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getAccountId()), ExtendType.ACCOUNT_EXTEND.getType(), extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    /**
     * 获取每日/每周账号扩展表某字段的值
     *
     * @param extendName 扩展字段名
     * @param isDaily    是否是每日，否则为每周
     * @return 扩展字段值
     */
    public String getAccountExtendValue(String extendName, boolean isDaily) {
        ExtendValueDO extendValueDO = ExtendUtil.getExtendValue(String.valueOf(getPlayer().getAccountId()),
                isDaily ? ExtendType.ACCOUNT_EXTEND_DAILY.getType() : ExtendType.ACCOUNT_EXTEND_WEEKLY.getType(),
                extendName);
        return extendValueDO == null ? null : extendValueDO.getExtendValue();
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 永久保存或者更新角色扩展表指定的值
     *
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值
     */
    public void saveOrUpdateCharacterExtendValue(String extendName, String extendValue) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getId()), ExtendType.CHARACTER_EXTEND.getType(), extendName, extendValue);
    }

    /**
     * 保存每日/每周角色扩展表某字段的值
     *
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值
     * @param isDaily     是否为每日刷新，否则为周刷新
     */
    public void saveOrUpdateCharacterExtendValue(String extendName, String extendValue, boolean isDaily) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getId()), isDaily ? ExtendType.CHARACTER_EXTEND_DAILY.getType() : ExtendType.CHARACTER_EXTEND_WEEKLY.getType(),
                extendName, extendValue);
    }

    /**
     * 永久保存或者更新账号扩展表指定的值
     *
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值
     */
    public void saveOrUpdateAccountExtendValue(String extendName, String extendValue) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getAccountId()), ExtendType.ACCOUNT_EXTEND.getType(), extendName, extendValue);
    }

    /**
     * 保存每日/每周账号扩展表某字段的值
     *
     * @param extendName  扩展字段名
     * @param extendValue 扩展字段值
     * @param isDaily     是否为每日刷新，否则为周刷新
     */
    public void saveOrUpdateAccountExtendValue(String extendName, String extendValue, boolean isDaily) {
        ExtendUtil.saveOrUpdateExtendValue(String.valueOf(getPlayer().getAccountId()), isDaily ? ExtendType.ACCOUNT_EXTEND_DAILY.getType() : ExtendType.ACCOUNT_EXTEND_WEEKLY.getType(),
                extendName, extendValue);
    }

    /**
     * 给予玩家装备物品
     *
     * @param equip 装备对象
     */
    public void gainEquip(Equip equip) {
        if (!InventoryManipulator.checkSpace(getClient(), equip.getItemId(), 1, equip.getOwner())) {
            message(I18nUtil.getMessage("AbstractPlayerInteraction.gainEquip.message2", InventoryType.EQUIP.getName()));
        }
        InventoryManipulator.addFromDrop(getClient(), equip, false);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取账户在线时间
     *
     * @return 返回当前账户角色在线时间，单位分钟
     */
    public int getOnlineTime() {
        return getPlayer().getCurrentOnlineTime();
    }

}