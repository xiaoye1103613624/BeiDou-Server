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
package org.gms.server;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.Job;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.autoban.AutobanFactory;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.WeaponType;
import org.gms.config.GameConfig;
import org.gms.constants.id.ItemId;
import org.gms.constants.inventory.EquipSlot;
import org.gms.constants.inventory.ItemConstants;
import org.gms.constants.skills.Assassin;
import org.gms.constants.skills.Gunslinger;
import org.gms.constants.skills.NightWalker;
import org.gms.net.server.Server;
import org.gms.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataDirectoryEntry;
import org.gms.provider.DataFileEntry;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.server.MakerItemFactory.MakerItemCreateEntry;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.MonsterInformationProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 物品信息提供者（单例模式）
 * 
 * <p>核心职责：从WZ数据文件和数据库中加载、缓存、查询所有游戏物品信息</p>
 * 
 * <p>主要功能模块：</p>
 * <ul>
 *   <li><strong>物品基础信息</strong>：名称、描述、价格、最大堆叠数</li>
 *   <li><strong>装备属性系统</strong>：攻击力、防御力、属性需求、升级插槽</li>
 *   <li><strong>套装系统</strong>：套装配置加载、属性加成计算</li>
 *   <li><strong>卷轴强化系统</strong>：卷轴应用、混沌卷轴随机属性、成功率计算</li>
 *   <li><strong>物品限制检查</strong>：交易限制、拾取限制、账号绑定</li>
 *   <li><strong>技能书与宠物系统</strong>：技能升级、宠物食物消耗</li>
 * </ul>
 * 
 * <p>数据来源：</p>
 * <ul>
 *   <li>ITEM.wz - 物品基础数据</li>
 *   <li>CHARACTER.wz - 装备属性数据</li>
 *   <li>STRING.wz - 物品名称和描述</li>
 *   <li>ETC.wz - 套装信息、额外配置</li>
 *   <li>数据库 - 怪物卡片映射等动态数据</li>
 * </ul>
 * 
 * <p>缓存机制：所有查询结果均采用懒加载+缓存策略，首次查询后缓存到Map中，后续调用直接返回缓存值</p>
 * 
 * @author Matze
 */
public class ItemInformationProvider {
    private static final Logger log = LoggerFactory.getLogger(ItemInformationProvider.class);
    
    /** 单例实例 - 使用饿汉式单例，确保线程安全 */
    private final static ItemInformationProvider instance = new ItemInformationProvider();

    /**
     * 获取单例实例
     * @return ItemInformationProvider 单例对象
     */
    public static ItemInformationProvider getInstance() {
        return instance;
    }

    // ==================== WZ数据提供者 ====================
    /** 物品基础数据提供者（ITEM.wz） */
    protected DataProvider itemData;
    /** 装备属性数据提供者（CHARACTER.wz） */
    protected DataProvider equipData;
    /** 字符串数据提供者（STRING.wz） */
    protected DataProvider stringData;
    /** 其他配置数据提供者（ETC.wz） */
    protected DataProvider etcData;
    
    // ==================== 字符串数据缓存 ====================
    /** 现金物品名称/描述 */
    protected Data cashStringData;
    /** 消耗品名称/描述 */
    protected Data consumeStringData;
    /** 装备名称/描述 */
    protected Data eqpStringData;
    /** 其他物品名称/描述 */
    protected Data etcStringData;
    /** 可安装物品名称/描述 */
    protected Data insStringData;
    /** 宠物名称/描述 */
    protected Data petStringData;

    // ==================== 物品基础信息缓存 ====================
    /** 最大堆叠数缓存（物品ID -> 最大堆叠数） */
    protected Map<Integer, Short> slotMaxCache = new HashMap<>();
    /** 物品效果缓存（物品ID -> StatEffect） */
    protected Map<Integer, StatEffect> itemEffects = new HashMap<>();
    /** 物品名称和描述缓存（物品ID -> [名称, 描述]） */
    protected Map<Integer, Pair<String, String>> nameDescCache = new HashMap<>();
    /** 物品消息缓存（物品ID -> 消息文本） */
    protected Map<Integer, String> msgCache = new HashMap<>();
    /** 物品价格缓存（物品ID -> 整体价格） */
    protected Map<Integer, Integer> wholePriceCache = new HashMap<>();
    /** 物品单价缓存（物品ID -> 单位价格，用于可充值物品） */
    protected Map<Integer, Double> unitPriceCache = new HashMap<>();
    /** 金币获取缓存（物品ID -> 金币数量） */
    protected Map<Integer, Integer> getMesoCache = new HashMap<>();
    /** 所有物品名称列表缓存 */
    protected List<Pair<Integer, String>> itemNameCache = new ArrayList<>();

    // ==================== 装备属性缓存 ====================
    /** 装备属性缓存（物品ID -> 属性Map） */
    protected Map<Integer, Map<String, Integer>> equipStatsCache = new HashMap<>();
    /** 装备对象缓存（物品ID -> Equip） */
    protected Map<Integer, Equip> equipCache = new HashMap<>();
    /** 装备等级信息缓存 */
    protected Map<Integer, Data> equipLevelInfoCache = new HashMap<>();
    /** 装备等级需求缓存 */
    protected Map<Integer, Integer> equipLevelReqCache = new HashMap<>();
    /** 装备最大等级缓存 */
    protected Map<Integer, Integer> equipMaxLevelCache = new HashMap<>();
    /** 装备槽位缓存 */
    protected Map<Integer, String> equipmentSlotCache = new HashMap<>();
    /** 卷轴需求缓存 */
    protected Map<Integer, List<Integer>> scrollReqsCache = new HashMap<>();
    /** 投射物攻击力缓存 */
    protected Map<Integer, Integer> projectileWatkCache = new HashMap<>();

    // ==================== 物品限制缓存 ====================
    /** 账号绑定限制缓存 */
    protected Map<Integer, Boolean> accountItemRestrictionCache = new HashMap<>();
    /** 掉落限制缓存 */
    protected Map<Integer, Boolean> dropRestrictionCache = new HashMap<>();
    /** 拾取限制缓存 */
    protected Map<Integer, Boolean> pickupRestrictionCache = new HashMap<>();
    /** 不可交易限制缓存 */
    protected Map<Integer, Boolean> untradeableCache = new HashMap<>();
    /** 装备后不可交易限制缓存 */
    protected Map<Integer, Boolean> onEquipUntradeableCache = new HashMap<>();
    /** 任务物品缓存 */
    protected Map<Integer, Boolean> isQuestItemCache = new HashMap<>();
    /** 组队任务物品缓存 */
    protected Map<Integer, Boolean> isPartyQuestItemCache = new HashMap<>();

    // ==================== 物品功能缓存 ====================
    /** 怪物卡片ID映射（卡片ID -> 怪物ID） */
    protected Map<Integer, Integer> monsterBookID = new HashMap<>();
    /** 脚本物品缓存 */
    protected Map<Integer, ScriptedItem> scriptedItemCache = new HashMap<>();
    /** 业力物品缓存（可交易给其他玩家） */
    protected Map<Integer, Boolean> karmaCache = new HashMap<>();
    /** 状态触发物品缓存 */
    protected Map<Integer, Integer> triggerItemCache = new HashMap<>();
    /** 经验值缓存 */
    protected Map<Integer, Integer> expCache = new HashMap<>();
    /** 创建物品缓存 */
    protected Map<Integer, Integer> createItem = new HashMap<>();
    /** 怪物物品缓存 */
    protected Map<Integer, Integer> mobItem = new HashMap<>();
    /** 使用延迟缓存 */
    protected Map<Integer, Integer> useDelay = new HashMap<>();
    /** 怪物HP缓存 */
    protected Map<Integer, Integer> mobHP = new HashMap<>();
    /** 等级缓存 */
    protected Map<Integer, Integer> levelCache = new HashMap<>();
    /** 奖励缓存 */
    protected Map<Integer, Pair<Integer, List<RewardItem>>> rewardCache = new HashMap<>();
    /** 拾取即消耗缓存 */
    protected Map<Integer, Boolean> consumeOnPickupCache = new HashMap<>();
    /** 过期替换缓存 */
    protected Map<Integer, Pair<Integer, String>> replaceOnExpireCache = new HashMap<>();
    /** 鼠标锁定缓存（禁止取消） */
    protected Map<Integer, Boolean> noCancelMouseCache = new HashMap<>();

    // ==================== 合成系统缓存 ====================
    /** 怪物水晶合成缓存 */
    protected Map<Integer, Integer> mobCrystalMakerCache = new HashMap<>();
    /** 属性升级合成缓存 */
    protected Map<Integer, Pair<String, Integer>> statUpgradeMakerCache = new HashMap<>();
    /** 合成物品缓存 */
    protected Map<Integer, MakerItemFactory.MakerItemCreateEntry> makerItemCache = new HashMap<>();
    /** 催化剂缓存 */
    protected Map<Integer, Integer> makerCatalystCache = new HashMap<>();

    // ==================== 技能书与宠物系统缓存 ====================
    /** 技能升级缓存（物品ID -> 属性Map） */
    protected Map<Integer, Map<String, Integer>> skillUpgradeCache = new HashMap<>();
    /** 技能升级信息缓存（物品ID -> Data节点） */
    protected Map<Integer, Data> skillUpgradeInfoCache = new HashMap<>();
    /** 现金宠物食物缓存（物品ID -> [增量, 可食用宠物ID集合]） */
    protected Map<Integer, Pair<Integer, Set<Integer>>> cashPetFoodCache = new HashMap<>();
    /** 任务消耗物品缓存 */
    protected Map<Integer, QuestConsItem> questItemConsCache = new HashMap<>();
    /** 现金物品信息缓存 */
    protected Map<Integer, ItemCashInfo> itemCashInfoCache = new HashMap<>();

    /**
     * 私有构造函数 - 单例模式
     * 初始化WZ数据提供者和字符串数据缓存
     */
    private ItemInformationProvider() {
        loadCardIdData();
        itemData = DataProviderFactory.getDataProvider(WZFiles.ITEM);
        equipData = DataProviderFactory.getDataProvider(WZFiles.CHARACTER);
        stringData = DataProviderFactory.getDataProvider(WZFiles.STRING);
        etcData = DataProviderFactory.getDataProvider(WZFiles.ETC);
        cashStringData = stringData.getData("Cash.img");
        consumeStringData = stringData.getData("Consume.img");
        eqpStringData = stringData.getData("Eqp.img");
        etcStringData = stringData.getData("Etc.img");
        insStringData = stringData.getData("Ins.img");
        petStringData = stringData.getData("Pet.img");

        isQuestItemCache.put(0, false);
        isPartyQuestItemCache.put(0, false);
    }


    /**
     * 获取所有物品的ID和名称列表
     * 首次调用时从WZ文件加载并缓存到itemNameCache，后续调用直接返回缓存
     *
     * @return 所有物品的[物品ID, 物品名称]列表
     */
    public List<Pair<Integer, String>> getAllItems() {
        if (!itemNameCache.isEmpty()) {
            return itemNameCache;
        }
        long startTime = System.currentTimeMillis();
        log.info("[物品查询] 首次加载全部物品名称缓存...");

        List<Pair<Integer, String>> itemPairs = new ArrayList<>();
        Data itemsData;

        itemsData = stringData.getData("Cash.img");
        for (Data itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), DataTool.getString("name", itemFolder, "NO-NAME")));
        }

        itemsData = stringData.getData("Consume.img");
        for (Data itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), DataTool.getString("name", itemFolder, "NO-NAME")));
        }

        itemsData = stringData.getData("Eqp.img").getChildByPath("Eqp");
        for (Data eqpType : itemsData.getChildren()) {
            for (Data itemFolder : eqpType.getChildren()) {
                itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), DataTool.getString("name", itemFolder, "NO-NAME")));
            }
        }

        itemsData = stringData.getData("Etc.img").getChildByPath("Etc");
        for (Data itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), DataTool.getString("name", itemFolder, "NO-NAME")));
        }

        itemsData = stringData.getData("Ins.img");
        for (Data itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), DataTool.getString("name", itemFolder, "NO-NAME")));
        }

        itemsData = stringData.getData("Pet.img");
        for (Data itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), DataTool.getString("name", itemFolder, "NO-NAME")));
        }

        // 缓存到itemNameCache，避免每次查询都重新加载WZ数据
        itemNameCache = itemPairs;

        long elapsed = System.currentTimeMillis() - startTime;
        log.info("[物品查询] 全部物品名称缓存加载完成，总数={}，耗时={}ms", itemPairs.size(), elapsed);

        return itemPairs;
    }

    /**
     * 获取所有其他物品(Etc)的ID和名称列表
     * 
     * @return 物品ID和名称的Pair列表
     */
    public List<Pair<Integer, String>> getAllEtcItems() {
        List<Pair<Integer, String>> itemPairs = new ArrayList<>();
        Data itemsData;

        itemsData = stringData.getData("Etc.img").getChildByPath("Etc");
        for (Data itemFolder : itemsData.getChildren()) {
            itemPairs.add(new Pair<>(Integer.parseInt(itemFolder.getName()), DataTool.getString("name", itemFolder, "NO-NAME")));
        }
        return itemPairs;
    }

    /**
     * 根据物品ID获取对应的字符串数据节点（名称、描述等）
     * 根据物品ID范围判断物品类型，返回对应的字符串数据缓存
     * 
     * @param itemId 物品ID
     * @return 字符串数据节点，不存在返回null
     */
    private Data getStringData(int itemId) {
        String cat = "null";
        Data theData;
        if (itemId >= 5010000) {
            theData = cashStringData;
        } else if (itemId >= 2000000 && itemId < 3000000) {
            theData = consumeStringData;
        } else if ((itemId >= 1010000 && itemId < 1040000) || (itemId >= 1122000 && itemId < 1123000) || (itemId >= 1132000 && itemId < 1133000) || (itemId >= 1142000 && itemId < 1143000)) {
            theData = eqpStringData;
            cat = "Eqp/Accessory";
        } else if (itemId >= 1000000 && itemId < 1010000) {
            theData = eqpStringData;
            cat = "Eqp/Cap";
        } else if (itemId >= 1102000 && itemId < 1103000) {
            theData = eqpStringData;
            cat = "Eqp/Cape";
        } else if (itemId >= 1040000 && itemId < 1050000) {
            theData = eqpStringData;
            cat = "Eqp/Coat";
        } else if (ItemConstants.isFace(itemId)) {
            theData = eqpStringData;
            cat = "Eqp/Face";
        } else if (itemId >= 1080000 && itemId < 1090000) {
            theData = eqpStringData;
            cat = "Eqp/Glove";
        } else if (ItemConstants.isHair(itemId)) {
            theData = eqpStringData;
            cat = "Eqp/Hair";
        } else if (itemId >= 1050000 && itemId < 1060000) {
            theData = eqpStringData;
            cat = "Eqp/Longcoat";
        } else if (itemId >= 1060000 && itemId < 1070000) {
            theData = eqpStringData;
            cat = "Eqp/Pants";
        } else if (itemId >= 1802000 && itemId < 1842000) {
            theData = eqpStringData;
            cat = "Eqp/PetEquip";
        } else if (itemId >= 1112000 && itemId < 1120000) {
            theData = eqpStringData;
            cat = "Eqp/Ring";
        } else if (itemId >= 1092000 && itemId < 1100000) {
            theData = eqpStringData;
            cat = "Eqp/Shield";
        } else if (itemId >= 1070000 && itemId < 1080000) {
            theData = eqpStringData;
            cat = "Eqp/Shoes";
        } else if (itemId >= 1900000 && itemId < 2000000) {
            theData = eqpStringData;
            cat = "Eqp/Taming";
        } else if (itemId >= 1200000 && itemId < 1800000) {
            // 1200000~1299999 为高版本副武器/次要武器，与主武器共用 Eqp/Weapon 分类
            theData = eqpStringData;
            cat = "Eqp/Weapon";
        } else if (itemId >= 4000000 && itemId < 5000000) {
            theData = etcStringData;
            cat = "Etc";
        } else if (itemId >= 3000000 && itemId < 4000000) {
            theData = insStringData;
        } else if (ItemConstants.isPet(itemId)) {
            theData = petStringData;
        } else {
            return null;
        }
        if (cat.equalsIgnoreCase("null")) {
            return theData.getChildByPath(String.valueOf(itemId));
        } else {
            return theData.getChildByPath(cat + "/" + itemId);
        }
    }

    /**
     * 检查物品是否禁止鼠标取消操作
     * 
     * @param itemId 物品ID
     * @return true表示禁止取消鼠标操作
     */
    public boolean noCancelMouse(int itemId) {
        if (noCancelMouseCache.containsKey(itemId)) {
            return noCancelMouseCache.get(itemId);
        }

        Data item = getItemData(itemId);
        if (item == null) {
            noCancelMouseCache.put(itemId, false);
            return false;
        }

        boolean blockMouse = DataTool.getIntConvert("info/noCancelMouse", item, 0) == 1;
        noCancelMouseCache.put(itemId, blockMouse);
        return blockMouse;
    }

    /**
     * 根据物品ID获取物品数据节点
     * 先从ITEM.wz查找，找不到则从CHARACTER.wz查找
     * 
     * @param itemId 物品ID
     * @return 物品数据节点，不存在返回null
     */
    private Data getItemData(int itemId) {
        Data ret = null;
        String idStr = "0" + itemId;
        DataDirectoryEntry root = itemData.getRoot();
        for (DataDirectoryEntry topDir : root.getSubdirectories()) {
            for (DataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr.substring(0, 4) + ".img")) {
                    ret = itemData.getData(topDir.getName() + "/" + iFile.getName());
                    if (ret == null) {
                        return null;
                    }
                    ret = ret.getChildByPath(idStr);
                    return ret;
                } else if (iFile.getName().equals(idStr.substring(1) + ".img")) {
                    return itemData.getData(topDir.getName() + "/" + iFile.getName());
                }
            }
        }
        root = equipData.getRoot();
        for (DataDirectoryEntry topDir : root.getSubdirectories()) {
            for (DataFileEntry iFile : topDir.getFiles()) {
                if (iFile.getName().equals(idStr + ".img")) {
                    return equipData.getData(topDir.getName() + "/" + iFile.getName());
                }
            }
        }
        return ret;
    }

    /**
     * 获取指定ID范围内存在的物品ID列表
     * 
     * @param minId 最小物品ID
     * @param maxId 最大物品ID
     * @param ignoreCashItem 是否忽略现金物品
     * @return 物品ID列表
     */
    public List<Integer> getItemIdsInRange(int minId, int maxId, boolean ignoreCashItem) {
        List<Integer> list = new ArrayList<>();

        if (ignoreCashItem) {
            for (int i = minId; i <= maxId; i++) {
                if (getItemData(i) != null && !isCash(i)) {
                    list.add(i);
                }
            }
        } else {
            for (int i = minId; i <= maxId; i++) {
                if (getItemData(i) != null) {
                    list.add(i);
                }
            }
        }

        return list;
    }

    /**
     * 根据玩家技能获取额外的物品堆叠上限加成
     * 飞侠职业的飞镖精通/神枪手的枪械精通技能可以增加弹药堆叠上限
     * 
     * @param c 客户端对象
     * @param itemId 物品ID
     * @return 额外堆叠上限加成
     */
    private static short getExtraSlotMaxFromPlayer(Client c, int itemId) {
        short ret = 0;

        // thanks GMChuck for detecting player sensitive data being cached into getSlotMax
        if (ItemConstants.isThrowingStar(itemId)) {
            if (c.getPlayer().getJob().isA(Job.NIGHTWALKER1)) {
                ret += c.getPlayer().getSkillLevel(SkillFactory.getSkill(NightWalker.CLAW_MASTERY)) * 10;
            } else {
                ret += c.getPlayer().getSkillLevel(SkillFactory.getSkill(Assassin.CLAW_MASTERY)) * 10;
            }
        } else if (ItemConstants.isBullet(itemId)) {
            ret += c.getPlayer().getSkillLevel(SkillFactory.getSkill(Gunslinger.GUN_MASTERY)) * 10;
        }

        return ret;
    }

    /**
     * 获取物品最大堆叠数（包含玩家技能加成）
     * 
     * @param c 客户端对象，用于计算技能加成
     * @param itemId 物品ID
     * @return 最大堆叠数
     */
    public short getSlotMax(Client c, int itemId) {
        // 优先从缓存获取
        Short slotMax = slotMaxCache.get(itemId);
        if (slotMax != null) {
            return (short) (slotMax + getExtraSlotMaxFromPlayer(c, itemId));
        }
        
        short ret = 0;
        Data item = getItemData(itemId);
        if (item != null) {
            // 获取物品配置的堆叠上限
            Data smEntry = item.getChildByPath("info/slotMax");
            // 获取服务器配置的全局堆叠上限
            short itemSlotMax = GameConfig.getServerShort("item_slot_max");
            InventoryType inventoryType = ItemConstants.getInventoryType(itemId);
            
            // 处理堆叠上限的优先级逻辑
            if (smEntry == null) {
                // 物品未配置堆叠上限时的默认值
                if (inventoryType.getType() == InventoryType.EQUIP.getType()) {
                    // 装备类物品默认堆叠1
                    ret = 1;
                } else if (inventoryType.canChangeSlotMax() && itemSlotMax > 0) {
                    // 可修改堆叠上限的物品类型，使用服务器配置
                    ret = itemSlotMax;
                } else {
                    // 其他物品默认堆叠100
                    ret = 100;
                }
            } else {
                // 物品有配置堆叠上限时，优先使用服务器配置（如果允许修改）
                ret = inventoryType.canChangeSlotMax() && itemSlotMax > 0 ? itemSlotMax : (short) DataTool.getInt(smEntry);
            }
        }

        // 缓存结果供后续使用
        slotMaxCache.put(itemId, ret);
        // 返回基础堆叠数 + 玩家技能加成
        return (short) (ret + getExtraSlotMaxFromPlayer(c, itemId));
    }

    /**
     * 获取物品基础最大堆叠数（不包含玩家技能等额外加成）
     * 适用于无需Client场景（如管理后台查询）
     *
     * @param itemId 物品ID
     * @return 基础最大堆叠数
     */
    public short getSlotMax(int itemId) {
        Short slotMax = slotMaxCache.get(itemId);
        if (slotMax != null) {
            return slotMax;
        }
        short ret = 0;
        Data item = getItemData(itemId);
        if (item != null) {
            Data smEntry = item.getChildByPath("info/slotMax");
            short itemSlotMax = GameConfig.getServerShort("item_slot_max");
            InventoryType inventoryType = ItemConstants.getInventoryType(itemId);
            if (smEntry == null) {
                if (inventoryType.getType() == InventoryType.EQUIP.getType()) {
                    ret = 1;
                } else if (inventoryType.canChangeSlotMax() && itemSlotMax > 0) {
                    ret = itemSlotMax;
                } else {
                    ret = 100;
                }
            } else {
                ret = inventoryType.canChangeSlotMax() && itemSlotMax > 0 ? itemSlotMax : (short) DataTool.getInt(smEntry);
            }
        }
        slotMaxCache.put(itemId, ret);
        return ret;
    }

    public int getMeso(int itemId) {
        if (getMesoCache.containsKey(itemId)) {
            return getMesoCache.get(itemId);
        }
        Data item = getItemData(itemId);
        if (item == null) {
            return -1;
        }
        int pEntry;
        Data pData = item.getChildByPath("info/meso");
        if (pData == null) {
            return -1;
        }
        pEntry = DataTool.getInt(pData);
        getMesoCache.put(itemId, pEntry);
        return pEntry;
    }

    /**
     * 将单位价格四舍五入到指定精度
     * 使用二进制小数逼近算法，最多保留max位二进制小数精度
     * 
     * @param unitPrice 原始单位价格
     * @param max 最大二进制小数位数
     * @return 四舍五入后的价格
     */
    private static double getRoundedUnitPrice(double unitPrice, int max) {
        // 分离整数部分和小数部分
        double intPart = Math.floor(unitPrice);
        double fractPart = unitPrice - intPart;
        
        // 小数部分为0，直接返回整数部分
        if (fractPart == 0.0) {
            return intPart;
        }

        // fractMask: 累积的小数部分
        // lastFract: 上一次的精度值
        // curFract: 当前精度值，从1/2开始，每次减半
        double fractMask = 0.0;
        double lastFract, curFract = 1.0;
        int i = 1;

        // 二进制小数逼近循环
        // 从1/2开始，依次尝试1/4, 1/8, 1/16...直到达到最大精度
        do {
            lastFract = curFract;
            curFract /= 2;  // 精度减半

            if (fractPart == curFract) {
                // 恰好匹配当前精度，直接退出
                break;
            } else if (fractPart > curFract) {
                // 当前精度可以被包含，累加到结果中
                fractMask += curFract;
                fractPart -= curFract;
            }

            i++;
        } while (i <= max);

        // 如果超过最大精度，使用最后一次的精度值
        if (i > max) {
            lastFract = curFract;
            curFract = 0.0;
        }

        // 根据剩余小数部分距离哪个精度更近，决定舍入方向
        if (Math.abs(fractPart - curFract) < Math.abs(fractPart - lastFract)) {
            return intPart + fractMask + curFract;
        } else {
            return intPart + fractMask + lastFract;
        }
    }

    /**
     * 获取物品价格数据（整体价格和单位价格）
     * 
     * @param itemId 物品ID
     * @return Pair(整体价格, 单位价格)
     */
    private Pair<Integer, Double> getItemPriceData(int itemId) {
        // 获取物品数据
        Data item = getItemData(itemId);
        if (item == null) {
            // 物品不存在，缓存无效值并返回
            wholePriceCache.put(itemId, -1);
            unitPriceCache.put(itemId, 0.0);
            return new Pair<>(-1, 0.0);
        }

        // 获取整体价格（非可充值物品使用此价格）
        int pEntry = -1;
        Data pData = item.getChildByPath("info/price");
        if (pData != null) {
            pEntry = DataTool.getInt(pData);
        }

        // 获取单位价格（可充值物品使用此价格，如弹药）
        double fEntry = 0.0f;
        pData = item.getChildByPath("info/unitPrice");
        if (pData != null) {
            try {
                // 尝试以double类型读取并四舍五入到5位二进制精度
                fEntry = getRoundedUnitPrice(DataTool.getDouble(pData), 5);
            } catch (Exception e) {
                // 读取失败时降级为int类型
                fEntry = DataTool.getInt(pData);
            }
        }

        // 缓存结果供后续使用
        wholePriceCache.put(itemId, pEntry);
        unitPriceCache.put(itemId, fEntry);
        return new Pair<>(pEntry, fEntry);
    }

    public int getWholePrice(int itemId) {
        if (wholePriceCache.containsKey(itemId)) {
            return wholePriceCache.get(itemId);
        }

        return getItemPriceData(itemId).getLeft();
    }

    public double getUnitPrice(int itemId) {
        if (unitPriceCache.containsKey(itemId)) {
            return unitPriceCache.get(itemId);
        }

        return getItemPriceData(itemId).getRight();
    }

    /**
     * 计算指定数量物品的总价格
     * 
     * @param itemId 物品ID
     * @param quantity 数量
     * @return 总价格，物品不存在返回-1
     */
    public int getPrice(int itemId, int quantity) {
        // 获取物品整体价格
        int retPrice = getWholePrice(itemId);
        if (retPrice == -1) {
            // 物品不存在，返回无效值
            return -1;
        }

        // 根据物品类型计算总价
        if (!ItemConstants.isRechargeable(itemId)) {
            // 非可充值物品：总价 = 单价 * 数量
            retPrice *= quantity;
        } else {
            // 可充值物品（如弹药）：总价 = 基础价格 + 单位价格 * 数量（向上取整）
            retPrice += Math.ceil(quantity * getUnitPrice(itemId));
        }

        return retPrice;
    }

    /**
     * 获取物品过期后的替换信息
     * 
     * @param itemId 物品ID
     * @return Pair(替换物品ID, 提示消息)
     */
    public Pair<Integer, String> getReplaceOnExpire(int itemId) {   // thanks to GabrielSin
        // 优先从缓存获取
        if (replaceOnExpireCache.containsKey(itemId)) {
            return replaceOnExpireCache.get(itemId);
        }

        // 获取物品数据
        Data data = getItemData(itemId);
        // 读取替换物品ID（0表示无替换）
        int itemReplacement = DataTool.getInt("info/replace/itemid", data, 0);
        // 读取替换提示消息
        String msg = DataTool.getString("info/replace/msg", data, "");

        // 创建结果并缓存
        Pair<Integer, String> ret = new Pair<>(itemReplacement, msg);
        replaceOnExpireCache.put(itemId, ret);

        return ret;
    }

    /**
     * 获取装备的槽位信息
     * 
     * @param itemId 物品ID
     * @return 槽位字符串，物品不存在返回null
     */
    protected String getEquipmentSlot(int itemId) {
        // 优先从缓存获取
        if (equipmentSlotCache.containsKey(itemId)) {
            return equipmentSlotCache.get(itemId);
        }

        String ret = "";

        // 获取物品数据
        Data item = getItemData(itemId);
        if (item == null) {
            return null;
        }

        // 获取物品info节点
        Data info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }

        // 读取槽位信息
        ret = DataTool.getString("islot", info, "");

        // 缓存结果
        equipmentSlotCache.put(itemId, ret);

        return ret;
    }

    /**
     * 获取装备属性统计信息
     * 返回的Map包含属性加成、需求等级、职业需求、升级插槽数等信息
     * 
     * @param itemId 物品ID
     * @return 属性Map，物品不存在返回null
     */
    public Map<String, Integer> getEquipStats(int itemId) {
        // 优先从缓存获取
        if (equipStatsCache.containsKey(itemId)) {
            return equipStatsCache.get(itemId);
        }
        
        // 使用LinkedHashMap保持插入顺序
        Map<String, Integer> ret = new LinkedHashMap<>();
        
        // 获取物品数据
        Data item = getItemData(itemId);
        if (item == null) {
            return null;
        }
        
        // 获取物品info节点
        Data info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        
        // 遍历所有子节点，提取属性加成（以"inc"开头的字段）
        // incXXX 表示属性增加值，如 incSTR=力量加成
        for (Data data : info.getChildren()) {
            if (data.getName().startsWith("inc")) {
                // 去掉"inc"前缀，存储属性名和值
                ret.put(data.getName().substring(3), DataTool.getIntConvert(data));
            }
            /*else if (data.getName().startsWith("req"))
             ret.put(data.getName(), DataTool.getInt(data.getName(), info, 0));*/
        }
        
        // 添加需求属性
        ret.put("reqJob", DataTool.getInt("reqJob", info, 0));      // 职业需求
        ret.put("reqLevel", DataTool.getInt("reqLevel", info, 0));  // 等级需求
        ret.put("reqDEX", DataTool.getInt("reqDEX", info, 0));      // 敏捷需求
        ret.put("reqSTR", DataTool.getInt("reqSTR", info, 0));      // 力量需求
        ret.put("reqINT", DataTool.getInt("reqINT", info, 0));      // 智力需求
        ret.put("reqLUK", DataTool.getInt("reqLUK", info, 0));      // 运气需求
        ret.put("reqPOP", DataTool.getInt("reqPOP", info, 0));      // 人气需求
        
        // 添加装备特性
        ret.put("cash", DataTool.getInt("cash", info, 0));          // 是否现金物品
        ret.put("tuc", DataTool.getInt("tuc", info, 0));            // 总升级次数（Total Upgrade Count）
        ret.put("cursed", DataTool.getInt("cursed", info, 0));      // 诅咒概率（卷轴失败时装备消失概率）
        ret.put("success", DataTool.getInt("success", info, 0));    // 成功率（百分比）
        ret.put("fs", DataTool.getInt("fs", info, 0));              // 尖刺属性（攻击时有概率伤害攻击者）
        ret.put("setItemID", DataTool.getInt("setItemID", info, 0));// 所属套装ID

        // 缓存结果供后续使用
        equipStatsCache.put(itemId, ret);
        return ret;
    }

    /**
     * 获取装备所属的套装ID
     * 读取装备info节点下的setItemID字段，0表示该装备不属于任何套装
     *
     * @param itemId 装备物品ID
     * @return 套装ID，无套装返回0
     */
    public int getSetItemID(int itemId) {
        Map<String, Integer> stats = getEquipStats(itemId);
        if (stats == null) {
            return 0;
        }
        Integer setId = stats.get("setItemID");
        return setId == null ? 0 : setId;
    }

    /** 套装信息缓存（套装ID -> 套装配置），首次调用getSetItem时延迟加载 */
    protected final Map<Integer, StructSetItem> setItemCache = new HashMap<>();
    /** 套装数据是否已从SetItemInfo.img加载完毕 */
    protected boolean setItemsLoaded = false;

    /**
     * 根据套装ID获取套装配置（含每档位的属性加成）
     * 数据来源于Etc.wz/SetItemInfo.img，首次调用时一次性解析全部套装并缓存
     *
     * @param setItemId 套装ID
     * @return 套装配置，不存在返回null
     */
    public StructSetItem getSetItem(int setItemId) {
        if (!setItemsLoaded) {
            loadSetItems();
        }
        return setItemCache.get(setItemId);
    }

    /**
     * 解析Etc.wz/SetItemInfo.img，加载全部套装属性配置到缓存
     * <p>
     * 套装系统核心初始化方法，负责从游戏数据文件中读取套装配置信息，包括：
     * - 套装ID和完成套装所需物品数量
     * - 套装包含的所有物品ID列表
     * - 各等级(tier)的套装属性加成效果
     * <p>
     * 执行流程：
     * 1. 标记套装加载状态为true
     * 2. 读取SetItemInfo.img数据文件
     * 3. 遍历每个套装节点，解析套装基本信息
     * 4. 解析套装包含的物品ID列表
     * 5. 解析各等级的套装效果属性
     * 6. 将套装配置存入setItemCache缓存
     */
    private void loadSetItems() {
        // 标记套装配置已开始加载（防止重复加载）
        setItemsLoaded = true;

        // 从Etc.wz读取套装信息数据文件
        Data setItemInfo = etcData.getData("SetItemInfo.img");
        if (setItemInfo == null) {
            log.warn("[套装系统] 未找到SetItemInfo.img，套装属性加成功能不可用");
            return;
        }

        // 遍历所有套装配置节点
        for (Data setData : setItemInfo.getChildren()) {
            int setId;
            try {
                // 将节点名称解析为套装ID（节点名应为数字字符串）
                setId = Integer.parseInt(setData.getName());
            } catch (NumberFormatException e) {
                // 非数字命名的节点跳过
                continue;
            }

            // 创建套装配置对象
            StructSetItem set = new StructSetItem();
            set.setItemID = setId;
            // 读取完成套装所需的物品数量
            set.completeCount = DataTool.getInt("completeCount", setData, 0);

            // 解析套装包含的物品ID列表
            Data itemIdDir = setData.getChildByPath("ItemID");
            if (itemIdDir != null) {
                for (Data idEntry : itemIdDir.getChildren()) {
                    set.itemIDs.add(DataTool.getIntConvert(idEntry));
                }
            }

            // 解析套装效果（按等级划分）
            Data effectDir = setData.getChildByPath("Effect");
            if (effectDir != null) {
                for (Data tierData : effectDir.getChildren()) {
                    int tier;
                    try {
                        // 将节点名称解析为等级（如2件套、3件套）
                        tier = Integer.parseInt(tierData.getName());
                    } catch (NumberFormatException e) {
                        continue;
                    }

                    // 创建等级效果对象
                    StructSetItem.SetItem se = new StructSetItem.SetItem();
                    // 遍历该等级下的所有属性
                    for (Data stat : tierData.getChildren()) {
                        String name = stat.getName();
                        switch (name) {
                            case "incPDD" -> se.incPDD = DataTool.getIntConvert(stat);      // 物理防御
                            case "incMDD" -> se.incMDD = DataTool.getIntConvert(stat);      // 魔法防御
                            case "incSTR" -> se.incSTR = DataTool.getIntConvert(stat);      // 力量
                            case "incDEX" -> se.incDEX = DataTool.getIntConvert(stat);      // 敏捷
                            case "incINT" -> se.incINT = DataTool.getIntConvert(stat);      // 智力
                            case "incLUK" -> se.incLUK = DataTool.getIntConvert(stat);      // 运气
                            case "incACC" -> se.incACC = DataTool.getIntConvert(stat);      // 命中
                            case "incPAD" -> se.incPAD = DataTool.getIntConvert(stat);      // 物理攻击
                            case "incMAD" -> se.incMAD = DataTool.getIntConvert(stat);      // 魔法攻击
                            case "incEVA" -> se.incEVA = DataTool.getIntConvert(stat);      // 闪避
                            case "incSpeed" -> se.incSpeed = DataTool.getIntConvert(stat);  // 速度
                            case "incMHP" -> se.incMHP = DataTool.getIntConvert(stat);      // 最大HP
                            case "incMMP" -> se.incMMP = DataTool.getIntConvert(stat);      // 最大MP
                            case "incMHPr" -> se.incMHPr = DataTool.getIntConvert(stat);    // HP恢复
                            case "incMMPr" -> se.incMMPr = DataTool.getIntConvert(stat);    // MP恢复
                            case "incAllStat" -> se.incAllStat = DataTool.getIntConvert(stat); // 全属性
                            case "Option" -> {
                                // 解析额外技能选项（最多支持2个）
                                Data opt1 = stat.getChildByPath("1");
                                if (opt1 != null) {
                                    se.option1 = DataTool.getInt("option", opt1, 0);
                                    se.option1Level = DataTool.getInt("level", opt1, 0);
                                }
                                Data opt2 = stat.getChildByPath("2");
                                if (opt2 != null) {
                                    se.option2 = DataTool.getInt("option", opt2, 0);
                                    se.option2Level = DataTool.getInt("level", opt2, 0);
                                }
                            }
                            default -> {
                                // 未知属性忽略
                            }
                        }
                    }
                    // 将该等级效果加入套装
                    set.items.put(tier, se);
                }
            }

            // 将套装配置存入缓存
            setItemCache.put(setId, set);
        }

        // 输出加载日志
        log.info("[套装系统] 已加载套装属性配置 {} 套", setItemCache.size());
    }

    public Integer getEquipLevelReq(int itemId) {
        if (equipLevelReqCache.containsKey(itemId)) {
            return equipLevelReqCache.get(itemId);
        }

        int ret = 0;
        Data item = getItemData(itemId);
        if (item != null) {
            Data info = item.getChildByPath("info");
            if (info != null) {
                ret = DataTool.getInt("reqLevel", info, 0);
            }
        }

        equipLevelReqCache.put(itemId, ret);
        return ret;
    }

    public List<Integer> getScrollReqs(int itemId) {
        if (scrollReqsCache.containsKey(itemId)) {
            return scrollReqsCache.get(itemId);
        }

        List<Integer> ret = new ArrayList<>();
        Data data = getItemData(itemId);
        data = data.getChildByPath("req");
        if (data != null) {
            for (Data req : data.getChildren()) {
                ret.add(DataTool.getInt(req));
            }
        }

        scrollReqsCache.put(itemId, ret);
        return ret;
    }

    public WeaponType getWeaponType(int itemId) {
        int cat = (itemId / 10000) % 100;
        WeaponType[] type = {WeaponType.SWORD1H, WeaponType.GENERAL1H_SWING, WeaponType.GENERAL1H_SWING, WeaponType.DAGGER_OTHER, WeaponType.NOT_A_WEAPON, WeaponType.NOT_A_WEAPON, WeaponType.NOT_A_WEAPON, WeaponType.WAND, WeaponType.STAFF, WeaponType.NOT_A_WEAPON, WeaponType.SWORD2H, WeaponType.GENERAL2H_SWING, WeaponType.GENERAL2H_SWING, WeaponType.SPEAR_STAB, WeaponType.POLE_ARM_SWING, WeaponType.BOW, WeaponType.CROSSBOW, WeaponType.CLAW, WeaponType.KNUCKLE, WeaponType.GUN};
        if (cat < 30 || cat > 49) {
            return WeaponType.NOT_A_WEAPON;
        }
        return type[cat - 30];
    }

    private static double testYourLuck(double prop, int dices) {   // revamped testYourLuck author: David A.
        return Math.pow(1.0 - prop, dices);
    }

    public static boolean rollSuccessChance(double propPercent) {
        return Math.random() >= testYourLuck(propPercent / 100.0, GameConfig.getServerInt("scroll_chance_rolls"));
    }

    private static short getMaximumShortMaxIfOverflow(int value1, int value2) {
        return (short) Math.min(Short.MAX_VALUE, Math.max(value1, value2));
    }

    private static short getShortMaxIfOverflow(int value) {
        return (short) Math.min(Short.MAX_VALUE, value);
    }

    private static short chscrollRandomizedStat(int range) {
        return (short) Randomizer.rand(-range, range);
    }

    public void scrollOptionEquipWithChaos(Equip nEquip, int range, boolean option) {
        // option: watk, matk, wdef, mdef, spd, jump, hp, mp
        //   stat: dex, luk, str, int, avoid, acc

        if (!option) {
            if (nEquip.getStr() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setStr(getMaximumShortMaxIfOverflow(nEquip.getStr(), (nEquip.getStr() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setStr(getMaximumShortMaxIfOverflow(0, (nEquip.getStr() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getDex() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setDex(getMaximumShortMaxIfOverflow(nEquip.getDex(), (nEquip.getDex() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setDex(getMaximumShortMaxIfOverflow(0, (nEquip.getDex() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getInt() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setInt(getMaximumShortMaxIfOverflow(nEquip.getInt(), (nEquip.getInt() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setInt(getMaximumShortMaxIfOverflow(0, (nEquip.getInt() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getLuk() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setLuk(getMaximumShortMaxIfOverflow(nEquip.getLuk(), (nEquip.getLuk() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setLuk(getMaximumShortMaxIfOverflow(0, (nEquip.getLuk() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getAcc() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setAcc(getMaximumShortMaxIfOverflow(nEquip.getAcc(), (nEquip.getAcc() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setAcc(getMaximumShortMaxIfOverflow(0, (nEquip.getAcc() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getAvoid() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setAvoid(getMaximumShortMaxIfOverflow(nEquip.getAvoid(), (nEquip.getAvoid() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setAvoid(getMaximumShortMaxIfOverflow(0, (nEquip.getAvoid() + chscrollRandomizedStat(range))));
                }
            }
        } else {
            if (nEquip.getWatk() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setWatk(getMaximumShortMaxIfOverflow(nEquip.getWatk(), (nEquip.getWatk() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setWatk(getMaximumShortMaxIfOverflow(0, (nEquip.getWatk() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getWdef() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setWdef(getMaximumShortMaxIfOverflow(nEquip.getWdef(), (nEquip.getWdef() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setWdef(getMaximumShortMaxIfOverflow(0, (nEquip.getWdef() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getMatk() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setMatk(getMaximumShortMaxIfOverflow(nEquip.getMatk(), (nEquip.getMatk() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setMatk(getMaximumShortMaxIfOverflow(0, (nEquip.getMatk() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getMdef() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setMdef(getMaximumShortMaxIfOverflow(nEquip.getMdef(), (nEquip.getMdef() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setMdef(getMaximumShortMaxIfOverflow(0, (nEquip.getMdef() + chscrollRandomizedStat(range))));
                }
            }

            if (nEquip.getSpeed() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setSpeed(getMaximumShortMaxIfOverflow(nEquip.getSpeed(), (nEquip.getSpeed() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setSpeed(getMaximumShortMaxIfOverflow(0, (nEquip.getSpeed() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getJump() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setJump(getMaximumShortMaxIfOverflow(nEquip.getJump(), (nEquip.getJump() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setJump(getMaximumShortMaxIfOverflow(0, (nEquip.getJump() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getHp() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setHp(getMaximumShortMaxIfOverflow(nEquip.getHp(), (nEquip.getHp() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setHp(getMaximumShortMaxIfOverflow(0, (nEquip.getHp() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getMp() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setMp(getMaximumShortMaxIfOverflow(nEquip.getMp(), (nEquip.getMp() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setMp(getMaximumShortMaxIfOverflow(0, (nEquip.getMp() + chscrollRandomizedStat(range))));
                }
            }
        }
    }

    private void scrollEquipWithChaos(Equip nEquip, int range) {
        if (GameConfig.getServerInt("chaos_scroll_stat_rate") > 0) {
            int temp;
            short curStr, curDex, curInt, curLuk, curWatk, curWdef, curMatk, curMdef, curAcc, curAvoid, curSpeed, curJump, curHp, curMp;

            if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                curStr = nEquip.getStr();
                curDex = nEquip.getDex();
                curInt = nEquip.getInt();
                curLuk = nEquip.getLuk();
                curWatk = nEquip.getWatk();
                curWdef = nEquip.getWdef();
                curMatk = nEquip.getMatk();
                curMdef = nEquip.getMdef();
                curAcc = nEquip.getAcc();
                curAvoid = nEquip.getAvoid();
                curSpeed = nEquip.getSpeed();
                curJump = nEquip.getJump();
                curHp = nEquip.getHp();
                curMp = nEquip.getMp();
            } else {
                curStr = Short.MIN_VALUE;
                curDex = Short.MIN_VALUE;
                curInt = Short.MIN_VALUE;
                curLuk = Short.MIN_VALUE;
                curWatk = Short.MIN_VALUE;
                curWdef = Short.MIN_VALUE;
                curMatk = Short.MIN_VALUE;
                curMdef = Short.MIN_VALUE;
                curAcc = Short.MIN_VALUE;
                curAvoid = Short.MIN_VALUE;
                curSpeed = Short.MIN_VALUE;
                curJump = Short.MIN_VALUE;
                curHp = Short.MIN_VALUE;
                curMp = Short.MIN_VALUE;
            }

            for (int i = 0; i < GameConfig.getServerInt("chaos_scroll_stat_rate"); i++) {
                if (nEquip.getStr() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curStr + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getStr() + chscrollRandomizedStat(range);
                    }

                    curStr = getMaximumShortMaxIfOverflow(temp, curStr);
                }

                if (nEquip.getDex() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curDex + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getDex() + chscrollRandomizedStat(range);
                    }

                    curDex = getMaximumShortMaxIfOverflow(temp, curDex);
                }

                if (nEquip.getInt() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curInt + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getInt() + chscrollRandomizedStat(range);
                    }

                    curInt = getMaximumShortMaxIfOverflow(temp, curInt);
                }

                if (nEquip.getLuk() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curLuk + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getLuk() + chscrollRandomizedStat(range);
                    }

                    curLuk = getMaximumShortMaxIfOverflow(temp, curLuk);
                }

                if (nEquip.getWatk() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curWatk + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getWatk() + chscrollRandomizedStat(range);
                    }

                    curWatk = getMaximumShortMaxIfOverflow(temp, curWatk);
                }

                if (nEquip.getWdef() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curWdef + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getWdef() + chscrollRandomizedStat(range);
                    }

                    curWdef = getMaximumShortMaxIfOverflow(temp, curWdef);
                }

                if (nEquip.getMatk() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curMatk + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getMatk() + chscrollRandomizedStat(range);
                    }

                    curMatk = getMaximumShortMaxIfOverflow(temp, curMatk);
                }

                if (nEquip.getMdef() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curMdef + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getMdef() + chscrollRandomizedStat(range);
                    }

                    curMdef = getMaximumShortMaxIfOverflow(temp, curMdef);
                }

                if (nEquip.getAcc() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curAcc + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getAcc() + chscrollRandomizedStat(range);
                    }

                    curAcc = getMaximumShortMaxIfOverflow(temp, curAcc);
                }

                if (nEquip.getAvoid() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curAvoid + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getAvoid() + chscrollRandomizedStat(range);
                    }

                    curAvoid = getMaximumShortMaxIfOverflow(temp, curAvoid);
                }

                if (nEquip.getSpeed() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curSpeed + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getSpeed() + chscrollRandomizedStat(range);
                    }

                    curSpeed = getMaximumShortMaxIfOverflow(temp, curSpeed);
                }

                if (nEquip.getJump() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curJump + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getJump() + chscrollRandomizedStat(range);
                    }

                    curJump = getMaximumShortMaxIfOverflow(temp, curJump);
                }

                if (nEquip.getHp() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curHp + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getHp() + chscrollRandomizedStat(range);
                    }

                    curHp = getMaximumShortMaxIfOverflow(temp, curHp);
                }

                if (nEquip.getMp() > 0) {
                    if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                        temp = curMp + chscrollRandomizedStat(range);
                    } else {
                        temp = nEquip.getMp() + chscrollRandomizedStat(range);
                    }

                    curMp = getMaximumShortMaxIfOverflow(temp, curMp);
                }
            }

            nEquip.setStr((short) Math.max(0, curStr));
            nEquip.setDex((short) Math.max(0, curDex));
            nEquip.setInt((short) Math.max(0, curInt));
            nEquip.setLuk((short) Math.max(0, curLuk));
            nEquip.setWatk((short) Math.max(0, curWatk));
            nEquip.setWdef((short) Math.max(0, curWdef));
            nEquip.setMatk((short) Math.max(0, curMatk));
            nEquip.setMdef((short) Math.max(0, curMdef));
            nEquip.setAcc((short) Math.max(0, curAcc));
            nEquip.setAvoid((short) Math.max(0, curAvoid));
            nEquip.setSpeed((short) Math.max(0, curSpeed));
            nEquip.setJump((short) Math.max(0, curJump));
            nEquip.setHp((short) Math.max(0, curHp));
            nEquip.setMp((short) Math.max(0, curMp));
        } else {
            if (nEquip.getStr() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setStr(getMaximumShortMaxIfOverflow(nEquip.getStr(), (nEquip.getStr() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setStr(getMaximumShortMaxIfOverflow(0, (nEquip.getStr() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getDex() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setDex(getMaximumShortMaxIfOverflow(nEquip.getDex(), (nEquip.getDex() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setDex(getMaximumShortMaxIfOverflow(0, (nEquip.getDex() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getInt() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setInt(getMaximumShortMaxIfOverflow(nEquip.getInt(), (nEquip.getInt() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setInt(getMaximumShortMaxIfOverflow(0, (nEquip.getInt() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getLuk() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setLuk(getMaximumShortMaxIfOverflow(nEquip.getLuk(), (nEquip.getLuk() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setLuk(getMaximumShortMaxIfOverflow(0, (nEquip.getLuk() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getWatk() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setWatk(getMaximumShortMaxIfOverflow(nEquip.getWatk(), (nEquip.getWatk() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setWatk(getMaximumShortMaxIfOverflow(0, (nEquip.getWatk() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getWdef() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setWdef(getMaximumShortMaxIfOverflow(nEquip.getWdef(), (nEquip.getWdef() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setWdef(getMaximumShortMaxIfOverflow(0, (nEquip.getWdef() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getMatk() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setMatk(getMaximumShortMaxIfOverflow(nEquip.getMatk(), (nEquip.getMatk() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setMatk(getMaximumShortMaxIfOverflow(0, (nEquip.getMatk() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getMdef() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setMdef(getMaximumShortMaxIfOverflow(nEquip.getMdef(), (nEquip.getMdef() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setMdef(getMaximumShortMaxIfOverflow(0, (nEquip.getMdef() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getAcc() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setAcc(getMaximumShortMaxIfOverflow(nEquip.getAcc(), (nEquip.getAcc() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setAcc(getMaximumShortMaxIfOverflow(0, (nEquip.getAcc() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getAvoid() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setAvoid(getMaximumShortMaxIfOverflow(nEquip.getAvoid(), (nEquip.getAvoid() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setAvoid(getMaximumShortMaxIfOverflow(0, (nEquip.getAvoid() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getSpeed() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setSpeed(getMaximumShortMaxIfOverflow(nEquip.getSpeed(), (nEquip.getSpeed() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setSpeed(getMaximumShortMaxIfOverflow(0, (nEquip.getSpeed() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getJump() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setJump(getMaximumShortMaxIfOverflow(nEquip.getJump(), (nEquip.getJump() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setJump(getMaximumShortMaxIfOverflow(0, (nEquip.getJump() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getHp() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setHp(getMaximumShortMaxIfOverflow(nEquip.getHp(), (nEquip.getHp() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setHp(getMaximumShortMaxIfOverflow(0, (nEquip.getHp() + chscrollRandomizedStat(range))));
                }
            }
            if (nEquip.getMp() > 0) {
                if (GameConfig.getServerBoolean("use_enhanced_chaos_scroll")) {
                    nEquip.setMp(getMaximumShortMaxIfOverflow(nEquip.getMp(), (nEquip.getMp() + chscrollRandomizedStat(range))));
                } else {
                    nEquip.setMp(getMaximumShortMaxIfOverflow(0, (nEquip.getMp() + chscrollRandomizedStat(range))));
                }
            }
        }
    }

    /*
        Issue with clean slate found thanks to Masterrulax
        Vicious added in the clean slate check thanks to Crypter (CrypterDEV)
    */
    public boolean canUseCleanSlate(Equip equip) {
        Map<String, Integer> eqStats = getEquipStats(equip.getItemId());
        if (eqStats == null || eqStats.get("tuc") == 0) {
            return false;
        }
        int totalUpgradeCount = eqStats.get("tuc");
        int freeUpgradeCount = equip.getUpgradeSlots();
        int viciousCount = equip.getVicious();
        int appliedScrollCount = equip.getLevel();
        return freeUpgradeCount + appliedScrollCount < totalUpgradeCount + viciousCount;
    }

    public Item scrollEquipWithId(Item equip, int scrollId, boolean usingWhiteScroll, int vegaItemId, boolean isGM) {
        // 检查是否是游戏管理员且配置中启用了完美GM卷轴功能
        boolean assertGM = (isGM && GameConfig.getServerBoolean("use_perfect_gm_scroll"));

        if (equip instanceof Equip nEquip) { // 检查装备是否为 Equip 类型
            // 获取卷轴的相关统计数据（如成功率、诅咒率等）
            Map<String, Integer> stats = this.getEquipStats(scrollId);

            // 检查装备是否有升级插槽或是否是清洁卷轴，或者当前玩家是GM
            if (((nEquip.getUpgradeSlots() > 0 || ItemConstants.isCleanSlate(scrollId))) || assertGM) {
                // 获取卷轴的成功概率
                double prop = (double) stats.get("success");

                // 根据不同的 VEGA 魔法卷轴调整成功概率
                switch (vegaItemId) {
                    case ItemId.VEGAS_SPELL_10:
                        if (prop == 10.0f) {
                            prop = 30.0f;
                        }
                        break;
                    case ItemId.VEGAS_SPELL_60:
                        if (prop == 60.0f) {
                            prop = 90.0f;
                        }
                        break;
                    case ItemId.CHAOS_SCROll_60:
                        prop = 100.0f;
                        break;
                }

                // 判断是否成功应用卷轴效果（根据成功率和GM状态）
                if (assertGM || rollSuccessChance(prop)) {
                    // 获取装备的标志位
                    short flag = nEquip.getFlag();

                    // 根据卷轴ID应用不同的效果
                    switch (scrollId) {
                        case ItemId.SPIKES_SCROLL:
                            flag |= ItemConstants.SPIKES; // 设置刺击标志位
                            nEquip.setFlag((byte) flag);
                            break;
                        case ItemId.COLD_PROTECTION_SCROLl:
                            // 设置寒冷保护标志位
                            flag |= ItemConstants.COLD;
                            nEquip.setFlag((byte) flag);
                            break;
                        case ItemId.CLEAN_SLATE_1:
                        case ItemId.CLEAN_SLATE_3:
                        case ItemId.CLEAN_SLATE_5:
                        case ItemId.CLEAN_SLATE_20:
                            if (canUseCleanSlate(nEquip)) {
                                nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() + 1)); // 增加升级插槽数量
                            }
                            break;
                        case ItemId.CHAOS_SCROll_60:
                        case ItemId.LIAR_TREE_SAP:
                        case ItemId.MAPLE_SYRUP:
                            // 使用混沌卷轴增加随机属性
                            scrollEquipWithChaos(nEquip, GameConfig.getServerInt("chaos_scroll_stat_range"));
                            break;

                        default:
                            // 默认情况下提高装备属性
                            improveEquipStats(nEquip, stats);
                            break;
                    }

                    // 如果不是清洁卷轴，则处理升级插槽和等级
                    if (!ItemConstants.isCleanSlate(scrollId)) {
                        if (!assertGM && !ItemConstants.isModifierScroll(scrollId)) {   // 处理修饰卷轴不消耗插槽的问题
                            // 减少一个升级插槽
                            nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1));
                        }
                        nEquip.setLevel((byte) (nEquip.getLevel() + 1)); // 提升装备等级
                    }
                } else {
                    // 卷轴使用失败的情况
                    if (!GameConfig.getServerBoolean("use_perfect_scrolling") && !usingWhiteScroll && !ItemConstants.isCleanSlate(scrollId) && !assertGM && !ItemConstants.isModifierScroll(scrollId)) {
                        nEquip.setUpgradeSlots((byte) (nEquip.getUpgradeSlots() - 1)); // 减少一个升级插槽
                    }
                    if (Randomizer.nextInt(100) < stats.get("cursed")) {
                        return null; // 卷轴诅咒装备，返回 null 表示装备被摧毁
                    }
                }
            }
        }
        // 返回处理后的装备
        return equip;
    }

    /**
     * 解析卷轴属性中的随机区间字段（incXXXMin / incXXXMax 配对），为每个区间字段随机生成一次具体加成值，
     * 并累加到对应的基础属性键上（如 STRMin/STRMax -> STR）。
     * 不修改传入的原始 stats（该 map 来自 {@link #equipStatsCache}，会被多次卷轴使用共享，不能直接改写）。
     */
    private static Map<String, Integer> resolveRandomRangeStats(Map<String, Integer> stats) {
        Map<String, Integer> resolved = new HashMap<>(stats);
        for (String key : stats.keySet()) {
            if (key.endsWith("Min")) {
                String base = key.substring(0, key.length() - 3);
                String maxKey = base + "Max";
                if (stats.containsKey(maxKey)) {
                    int min = stats.get(key);
                    int max = stats.get(maxKey);
                    int rolled = min <= max ? Randomizer.rand(min, max) : min;
                    resolved.merge(base, rolled, Integer::sum);
                }
            }
        }
        return resolved;
    }

    public static void improveEquipStats(Equip nEquip, Map<String, Integer> stats) {
        for (Entry<String, Integer> stat : resolveRandomRangeStats(stats).entrySet()) {
            switch (stat.getKey()) {
                case "STR":
                    nEquip.setStr(getShortMaxIfOverflow(nEquip.getStr() + stat.getValue().intValue()));
                    break;
                case "DEX":
                    nEquip.setDex(getShortMaxIfOverflow(nEquip.getDex() + stat.getValue().intValue()));
                    break;
                case "INT":
                    nEquip.setInt(getShortMaxIfOverflow(nEquip.getInt() + stat.getValue().intValue()));
                    break;
                case "LUK":
                    nEquip.setLuk(getShortMaxIfOverflow(nEquip.getLuk() + stat.getValue().intValue()));
                    break;
                case "PAD":
                    nEquip.setWatk(getShortMaxIfOverflow(nEquip.getWatk() + stat.getValue().intValue()));
                    break;
                case "PDD":
                    nEquip.setWdef(getShortMaxIfOverflow(nEquip.getWdef() + stat.getValue().intValue()));
                    break;
                case "MAD":
                    nEquip.setMatk(getShortMaxIfOverflow(nEquip.getMatk() + stat.getValue().intValue()));
                    break;
                case "MDD":
                    nEquip.setMdef(getShortMaxIfOverflow(nEquip.getMdef() + stat.getValue().intValue()));
                    break;
                case "ACC":
                    nEquip.setAcc(getShortMaxIfOverflow(nEquip.getAcc() + stat.getValue().intValue()));
                    break;
                case "EVA":
                    nEquip.setAvoid(getShortMaxIfOverflow(nEquip.getAvoid() + stat.getValue().intValue()));
                    break;
                case "Speed":
                    nEquip.setSpeed(getShortMaxIfOverflow(nEquip.getSpeed() + stat.getValue().intValue()));
                    break;
                case "Jump":
                    nEquip.setJump(getShortMaxIfOverflow(nEquip.getJump() + stat.getValue().intValue()));
                    break;
                case "MHP":
                    nEquip.setHp(getShortMaxIfOverflow(nEquip.getHp() + stat.getValue().intValue()));
                    break;
                case "MMP":
                    nEquip.setMp(getShortMaxIfOverflow(nEquip.getMp() + stat.getValue().intValue()));
                    break;
                case "afterImage":
                    break;
            }
        }
    }

    /**
     * 根据装备ID获取装备对象（简化版，不带戒指ID）
     * 
     * @param equipId 装备ID
     * @return 装备对象
     */
    public Item getEquipById(int equipId) {
        return getEquipById(equipId, -1);
    }

    /**
     * 根据装备ID获取装备对象
     * 
     * @param equipId 装备ID
     * @param ringId 戒指ID（用于区分双戒指，非戒指装备传-1）
     * @return 装备对象副本
     */
    private Item getEquipById(int equipId, int ringId) {
        // 创建新的装备对象
        Equip nEquip = new Equip(equipId, (byte) 0, ringId);
        nEquip.setQuantity((short) 1);
        
        // 获取装备属性统计信息
        Map<String, Integer> stats = this.getEquipStats(equipId);
        if (stats != null) {
            // 遍历属性Map，设置装备属性
            for (Entry<String, Integer> stat : stats.entrySet()) {
                switch (stat.getKey()) {
                    case "STR" -> nEquip.setStr((short) stat.getValue().intValue());      // 力量
                    case "DEX" -> nEquip.setDex((short) stat.getValue().intValue());      // 敏捷
                    case "INT" -> nEquip.setInt((short) stat.getValue().intValue());      // 智力
                    case "LUK" -> nEquip.setLuk((short) stat.getValue().intValue());      // 运气
                    case "PAD" -> nEquip.setWatk((short) stat.getValue().intValue());     // 物理攻击
                    case "PDD" -> nEquip.setWdef((short) stat.getValue().intValue());     // 物理防御
                    case "MAD" -> nEquip.setMatk((short) stat.getValue().intValue());     // 魔法攻击
                    case "MDD" -> nEquip.setMdef((short) stat.getValue().intValue());     // 魔法防御
                    case "ACC" -> nEquip.setAcc((short) stat.getValue().intValue());      // 命中
                    case "EVA" -> nEquip.setAvoid((short) stat.getValue().intValue());    // 闪避
                    case "Speed" -> nEquip.setSpeed((short) stat.getValue().intValue());  // 速度
                    case "Jump" -> nEquip.setJump((short) stat.getValue().intValue());    // 跳跃
                    case "MHP" -> nEquip.setHp((short) stat.getValue().intValue());       // 最大HP
                    case "MMP" -> nEquip.setMp((short) stat.getValue().intValue());       // 最大MP
                    case "tuc" -> nEquip.setUpgradeSlots((byte) stat.getValue().intValue()); // 升级插槽数
                }
            }
            
            // 设置不可交易标志
            if (isUntradeableRestricted(equipId)) {
                short flag = nEquip.getFlag();
                flag |= ItemConstants.UNTRADEABLE;
                nEquip.setFlag(flag);
            }
            
            // 设置尖刺属性标志
            if (stats.get("fs") > 0) {
                short flag = nEquip.getFlag();
                flag |= ItemConstants.SPIKES;
                nEquip.setFlag(flag);
                equipCache.put(equipId, nEquip);
            }
        }
        
        // 返回装备对象的副本，避免外部修改影响缓存
        return nEquip.copy();
    }

    /**
     * 随机化装备属性值（基础版本）
     * 在原始值的一定范围内随机波动
     * 
     * @param defaultValue 原始属性值
     * @param maxRange 最大随机范围
     * @return 随机化后的属性值
     */
    private static short getRandStat(short defaultValue, int maxRange) {
        // 如果原始值为0，直接返回0（避免无意义的随机）
        if (defaultValue == 0) {
            return 0;
        }
        
        // 计算实际随机范围：取原始值的10%和maxRange中的较小值
        int lMaxRange = (int) Math.min(Math.ceil(defaultValue * 0.1), maxRange);
        
        // 在 [defaultValue - lMaxRange, defaultValue + lMaxRange] 范围内随机
        // 公式：(defaultValue - lMaxRange) + [0, 2*lMaxRange]
        return (short) ((defaultValue - lMaxRange) + Math.floor(Randomizer.nextDouble() * (lMaxRange * 2 + 1)));
    }

    /**
     * 随机化装备所有属性（用于装备掉落时的属性波动）
     * 主属性和攻击属性最大波动5点，防御和HP/MP最大波动10点
     * 
     * @param equip 装备对象
     * @return 随机化后的装备对象
     */
    public Equip randomizeStats(Equip equip) {
        equip.setStr(getRandStat(equip.getStr(), 5));
        equip.setDex(getRandStat(equip.getDex(), 5));
        equip.setInt(getRandStat(equip.getInt(), 5));
        equip.setLuk(getRandStat(equip.getLuk(), 5));
        equip.setMatk(getRandStat(equip.getMatk(), 5));
        equip.setWatk(getRandStat(equip.getWatk(), 5));
        equip.setAcc(getRandStat(equip.getAcc(), 5));
        equip.setAvoid(getRandStat(equip.getAvoid(), 5));
        equip.setJump(getRandStat(equip.getJump(), 5));
        equip.setSpeed(getRandStat(equip.getSpeed(), 5));
        equip.setWdef(getRandStat(equip.getWdef(), 10));
        equip.setMdef(getRandStat(equip.getMdef(), 10));
        equip.setHp(getRandStat(equip.getHp(), 10));
        equip.setMp(getRandStat(equip.getMp(), 10));
        return equip;
    }

    /**
     * 随机化升级后的装备属性值（仅增加，不减少）
     * 用于卷轴升级成功后的属性随机增量
     * 
     * @param defaultValue 当前属性值
     * @param maxRange 最大增量范围
     * @return 升级后的属性值
     */
    private static short getRandUpgradedStat(short defaultValue, int maxRange) {
        // 如果当前值为0，直接返回0
        if (defaultValue == 0) {
            return 0;
        }
        
        // 在 [defaultValue, defaultValue + maxRange] 范围内随机（只增不减）
        return (short) (defaultValue + Math.floor(Randomizer.nextDouble() * (maxRange + 1)));
    }

    /**
     * 随机化装备升级后的属性（用于卷轴升级成功后的属性增量）
     * 主属性和攻击属性最大增量2点，防御和HP/MP最大增量5点
     * 
     * @param equip 装备对象
     * @return 升级后的装备对象
     */
    public Equip randomizeUpgradeStats(Equip equip) {
        equip.setStr(getRandUpgradedStat(equip.getStr(), 2));
        equip.setDex(getRandUpgradedStat(equip.getDex(), 2));
        equip.setInt(getRandUpgradedStat(equip.getInt(), 2));
        equip.setLuk(getRandUpgradedStat(equip.getLuk(), 2));
        equip.setMatk(getRandUpgradedStat(equip.getMatk(), 2));
        equip.setWatk(getRandUpgradedStat(equip.getWatk(), 2));
        equip.setAcc(getRandUpgradedStat(equip.getAcc(), 2));
        equip.setAvoid(getRandUpgradedStat(equip.getAvoid(), 2));
        equip.setJump(getRandUpgradedStat(equip.getJump(), 2));
        equip.setWdef(getRandUpgradedStat(equip.getWdef(), 5));
        equip.setMdef(getRandUpgradedStat(equip.getMdef(), 5));
        equip.setHp(getRandUpgradedStat(equip.getHp(), 5));
        equip.setMp(getRandUpgradedStat(equip.getMp(), 5));
        return equip;
    }

    /**
     * 获取物品的效果（如药水效果、技能效果等）
     * 
     * @param itemId 物品ID
     * @return 物品效果对象，不存在返回null
     */
    public StatEffect getItemEffect(int itemId) {
        // 优先从缓存获取
        StatEffect ret = itemEffects.get(itemId);
        if (ret == null) {
            // 获取物品数据
            Data item = getItemData(itemId);
            if (item == null) {
                return null;
            }
            
            // 优先读取specEx（扩展效果），不存在则读取spec（基础效果）
            Data spec = item.getChildByPath("specEx");
            if (spec == null) {
                spec = item.getChildByPath("spec");
            }
            
            // 解析效果数据并缓存
            ret = StatEffect.loadItemEffectFromData(spec, itemId);
            itemEffects.put(itemId, ret);
        }
        return ret;
    }

    /**
     * 获取物品召唤怪物的数据
     * 返回二维数组，每行包含[怪物ID, 召唤概率]
     * 
     * @param itemId 物品ID
     * @return 召唤怪物数据数组 [怪物ID, 概率]
     */
    public int[][] getSummonMobs(int itemId) {
        // 获取物品数据
        Data data = getItemData(itemId);
        
        // 获取怪物列表大小
        int theInt = data.getChildByPath("mob").getChildren().size();
        
        // 创建结果数组 [怪物ID, 召唤概率]
        int[][] mobs2spawn = new int[theInt][2];
        for (int x = 0; x < theInt; x++) {
            mobs2spawn[x][0] = DataTool.getIntConvert("mob/" + x + "/id", data);    // 怪物ID
            mobs2spawn[x][1] = DataTool.getIntConvert("mob/" + x + "/prob", data);  // 召唤概率
        }
        return mobs2spawn;
    }

    /**
     * 获取投射物的物理攻击力
     * 
     * @param itemId 物品ID
     * @return 物理攻击力
     */
    public int getWatkForProjectile(int itemId) {
        // 优先从缓存获取
        Integer atk = projectileWatkCache.get(itemId);
        if (atk != null) {
            return atk.intValue();
        }
        
        // 获取物品数据，读取info/incPAD（物理攻击力加成）
        Data data = getItemData(itemId);
        atk = Integer.valueOf(DataTool.getInt("info/incPAD", data, 0));
        
        // 缓存结果
        projectileWatkCache.put(itemId, atk);
        return atk.intValue();
    }

    /**
     * 获取物品名称
     * 
     * @param itemId 物品ID
     * @return 物品名称，不存在返回null
     */
    public String getName(int itemId) {
        Pair<String, String> nameDesc = getNameDesc(itemId);
        return null == nameDesc ? null : nameDesc.left;
    }

    /**
     * 获取物品名称和描述
     * 
     * @param itemId 物品ID
     * @return Pair对象，left为名称，right为描述，不存在返回null
     */
    public Pair<String, String> getNameDesc(int itemId) {
        // 优先从缓存获取
        if (nameDescCache.containsKey(itemId)) {
            return nameDescCache.get(itemId);
        }
        
        // 获取字符串数据
        Data strings = getStringData(itemId);
        if (strings == null) {
            return null;
        }
        
        // 读取名称和描述
        String name = DataTool.getString("name", strings, null);
        String desc = DataTool.getString("desc", strings, null);
        
        // 如果名称为空，返回null
        if (name == null) {
            return null;
        }
        Pair<String, String> ret = new Pair<>(name, desc);
        nameDescCache.put(itemId, ret);
        return ret;
    }

    public String getMsg(int itemId) {
        if (msgCache.containsKey(itemId)) {
            return msgCache.get(itemId);
        }
        Data strings = getStringData(itemId);
        if (strings == null) {
            return null;
        }
        String ret = DataTool.getString("msg", strings, null);
        msgCache.put(itemId, ret);
        return ret;
    }

    public boolean isUntradeableRestricted(int itemId) {
        if (untradeableCache.containsKey(itemId)) {
            return untradeableCache.get(itemId);
        }

        // 怪物卡片(2380000~2389999)强制设为可交易
        if (ItemId.isMonsterCard(itemId)) {
            untradeableCache.put(itemId, false);
            return false;
        }

        boolean bRestricted = false;
        if (itemId != 0) {
            Data data = getItemData(itemId);
            if (data != null) {
                bRestricted = DataTool.getIntConvert("info/tradeBlock", data, 0) == 1;
            }
        }

        untradeableCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public boolean isAccountRestricted(int itemId) {
        if (accountItemRestrictionCache.containsKey(itemId)) {
            return accountItemRestrictionCache.get(itemId);
        }

        boolean bRestricted = false;
        if (itemId != 0) {
            Data data = getItemData(itemId);
            if (data != null) {
                bRestricted = DataTool.getIntConvert("info/accountSharable", data, 0) == 1;
            }
        }

        accountItemRestrictionCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public boolean isLootRestricted(int itemId) {
        if (dropRestrictionCache.containsKey(itemId)) {
            return dropRestrictionCache.get(itemId);
        }

        // 怪物卡片(2380000~2389999)强制设为可交易
        if (ItemId.isMonsterCard(itemId)) {
            dropRestrictionCache.put(itemId, false);
            return false;
        }

        boolean bRestricted = false;
        if (itemId != 0) {
            Data data = getItemData(itemId);
            if (data != null) {
                bRestricted = DataTool.getIntConvert("info/tradeBlock", data, 0) == 1;
                if (!bRestricted) {
                    bRestricted = isAccountRestricted(itemId);
                }
            }
        }

        dropRestrictionCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public boolean isDropRestricted(int itemId) {
        return isLootRestricted(itemId) || isQuestItem(itemId);
    }

    public boolean isPickupRestricted(int itemId) {
        if (pickupRestrictionCache.containsKey(itemId)) {
            return pickupRestrictionCache.get(itemId);
        }

        // 怪物卡片(2380000~2389999)允许拾取多张，忽略 info/only 固有道具限制
        if (ItemId.isMonsterCard(itemId)) {
            pickupRestrictionCache.put(itemId, false);
            return false;
        }

        boolean bRestricted = false;
        if (itemId != 0) {
            Data data = getItemData(itemId);
            if (data != null) {
                bRestricted = DataTool.getIntConvert("info/only", data, 0) == 1;
            }
        }

        pickupRestrictionCache.put(itemId, bRestricted);
        return bRestricted;
    }

    private Pair<Map<String, Integer>, Data> getSkillStatsInternal(int itemId) {
        Map<String, Integer> ret = skillUpgradeCache.get(itemId);
        Data retSkill = skillUpgradeInfoCache.get(itemId);

        if (ret != null) {
            return new Pair<>(ret, retSkill);
        }

        retSkill = null;
        ret = new LinkedHashMap<>();
        Data item = getItemData(itemId);
        if (item != null) {
            Data info = item.getChildByPath("info");
            if (info != null) {
                for (Data data : info.getChildren()) {
                    if (data.getName().startsWith("inc")) {
                        ret.put(data.getName().substring(3), DataTool.getIntConvert(data));
                    }
                }
                ret.put("masterLevel", DataTool.getInt("masterLevel", info, 0));
                ret.put("reqSkillLevel", DataTool.getInt("reqSkillLevel", info, 0));
                ret.put("success", DataTool.getInt("success", info, 0));

                retSkill = info.getChildByPath("skill");
            }
        }

        skillUpgradeCache.put(itemId, ret);
        skillUpgradeInfoCache.put(itemId, retSkill);
        return new Pair<>(ret, retSkill);
    }

    public Map<String, Integer> getSkillStats(int itemId, double playerJob) {
        Pair<Map<String, Integer>, Data> retData = getSkillStatsInternal(itemId);
        if (retData.getLeft().isEmpty()) {
            return null;
        }

        Map<String, Integer> ret = new LinkedHashMap<>(retData.getLeft());
        Data skill = retData.getRight();
        int curskill;
        for (int i = 0; i < skill.getChildren().size(); i++) {
            curskill = DataTool.getInt(Integer.toString(i), skill, 0);
            if (curskill == 0) {
                break;
            }
            if (curskill / 10000 == playerJob) {
                ret.put("skillid", curskill);
                break;
            }
        }
        if (ret.get("skillid") == null) {
            ret.put("skillid", 0);
        }
        return ret;
    }

    public Pair<Integer, Boolean> canPetConsume(Integer petId, Integer itemId) {
        Pair<Integer, Set<Integer>> foodData = cashPetFoodCache.get(itemId);

        if (foodData == null) {
            Set<Integer> pets = new HashSet<>(4);
            int inc = 1;

            Data data = getItemData(itemId);
            if (data != null) {
                Data specData = data.getChildByPath("spec");
                for (Data specItem : specData.getChildren()) {
                    String itemName = specItem.getName();

                    try {
                        Integer.parseInt(itemName); // check if it's a petid node

                        Integer petid = DataTool.getInt(specItem, 0);
                        pets.add(petid);
                    } catch (NumberFormatException npe) {
                        if (itemName.contentEquals("inc")) {
                            inc = DataTool.getInt(specItem, 1);
                        }
                    }
                }
            }

            foodData = new Pair<>(inc, pets);
            cashPetFoodCache.put(itemId, foodData);
        }

        return new Pair<>(foodData.getLeft(), foodData.getRight().contains(petId));
    }

    public boolean isQuestItem(int itemId) {
        if (isQuestItemCache.containsKey(itemId)) {
            return isQuestItemCache.get(itemId);
        }
        Data data = getItemData(itemId);
        boolean questItem = (data != null && DataTool.getIntConvert("info/quest", data, 0) == 1);
        isQuestItemCache.put(itemId, questItem);
        return questItem;
    }

    public boolean isPartyQuestItem(int itemId) {
        if (isPartyQuestItemCache.containsKey(itemId)) {
            return isPartyQuestItemCache.get(itemId);
        }
        Data data = getItemData(itemId);
        boolean partyquestItem = (data != null && DataTool.getIntConvert("info/pquest", data, 0) == 1);
        isPartyQuestItemCache.put(itemId, partyquestItem);
        return partyquestItem;
    }

    private void loadCardIdData() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT cardid, mobid FROM monstercarddata");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                monsterBookID.put(rs.getInt(1), rs.getInt(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getCardMobId(int id) {
        return monsterBookID.get(id);
    }

    public boolean isUntradeableOnEquip(int itemId) {
        if (onEquipUntradeableCache.containsKey(itemId)) {
            return onEquipUntradeableCache.get(itemId);
        }
        boolean untradeableOnEquip = DataTool.getIntConvert("info/equipTradeBlock", getItemData(itemId), 0) > 0;
        onEquipUntradeableCache.put(itemId, untradeableOnEquip);
        return untradeableOnEquip;
    }

    public ScriptedItem getScriptedItemInfo(int itemId) {
        if (scriptedItemCache.containsKey(itemId)) {
            return scriptedItemCache.get(itemId);
        }
        if ((itemId / 10000) != 243) {
            return null;
        }
        Data itemInfo = getItemData(itemId);
        ScriptedItem script = new ScriptedItem(DataTool.getInt("spec/npc", itemInfo, 0),
                DataTool.getString("spec/script", itemInfo, ""),
                DataTool.getInt("spec/runOnPickup", itemInfo, 0) == 1);
        scriptedItemCache.put(itemId, script);
        return scriptedItemCache.get(itemId);
    }

    public boolean isKarmaAble(int itemId) {
        if (karmaCache.containsKey(itemId)) {
            return karmaCache.get(itemId);
        }
        boolean bRestricted = DataTool.getIntConvert("info/tradeAvailable", getItemData(itemId), 0) > 0;
        karmaCache.put(itemId, bRestricted);
        return bRestricted;
    }

    public int getStateChangeItem(int itemId) {
        if (triggerItemCache.containsKey(itemId)) {
            return triggerItemCache.get(itemId);
        } else {
            int triggerItem = DataTool.getIntConvert("info/stateChangeItem", getItemData(itemId), 0);
            triggerItemCache.put(itemId, triggerItem);
            return triggerItem;
        }
    }

    public int getCreateItem(int itemId) {
        if (createItem.containsKey(itemId)) {
            return createItem.get(itemId);
        } else {
            int itemFrom = DataTool.getIntConvert("info/create", getItemData(itemId), 0);
            createItem.put(itemId, itemFrom);
            return itemFrom;
        }
    }

    public int getMobItem(int itemId) {
        if (mobItem.containsKey(itemId)) {
            return mobItem.get(itemId);
        } else {
            int mobItemCatch = DataTool.getIntConvert("info/mob", getItemData(itemId), 0);
            mobItem.put(itemId, mobItemCatch);
            return mobItemCatch;
        }
    }

    /**
     * 获取物品使用延迟时间（毫秒）
     * 
     * @param itemId 物品ID
     * @return 使用延迟时间
     */
    public int getUseDelay(int itemId) {
        // 优先从缓存获取
        if (useDelay.containsKey(itemId)) {
            return useDelay.get(itemId);
        }
        
        // 从物品数据读取使用延迟
        int mobUseDelay = DataTool.getIntConvert("info/useDelay", getItemData(itemId), 0);
        
        // 缓存结果
        useDelay.put(itemId, mobUseDelay);
        return mobUseDelay;
    }

    /**
     * 获取召唤怪物的HP值
     * 
     * @param itemId 物品ID
     * @return 怪物HP值
     */
    public int getMobHP(int itemId) {
        // 优先从缓存获取
        if (mobHP.containsKey(itemId)) {
            return mobHP.get(itemId);
        }
        
        // 从物品数据读取怪物HP
        int mobHPItem = DataTool.getIntConvert("info/mobHP", getItemData(itemId), 0);
        
        // 缓存结果
        mobHP.put(itemId, mobHPItem);
        return mobHPItem;
    }

    /**
     * 获取物品使用后获得的经验值
     * 
     * @param itemId 物品ID
     * @return 经验值
     */
    public int getExpById(int itemId) {
        // 优先从缓存获取
        if (expCache.containsKey(itemId)) {
            return expCache.get(itemId);
        }
        
        // 从物品数据读取经验值
        int exp = DataTool.getIntConvert("spec/exp", getItemData(itemId), 0);
        
        // 缓存结果
        expCache.put(itemId, exp);
        return exp;
    }

    /**
     * 获取物品的最大等级（用于技能书等物品）
     * 
     * @param itemId 物品ID
     * @return 最大等级，默认256
     */
    public int getMaxLevelById(int itemId) {
        // 优先从缓存获取
        if (levelCache.containsKey(itemId)) {
            return levelCache.get(itemId);
        }
        
        // 从物品数据读取最大等级，默认256
        int level = DataTool.getIntConvert("info/maxLevel", getItemData(itemId), 256);
        
        // 缓存结果
        levelCache.put(itemId, level);
        return level;
    }

    /**
     * 获取物品打开后的奖励列表（如宝箱、礼包等）
     * 
     * @param itemId 物品ID
     * @return Pair对象，left为总概率，right为奖励列表
     */
    public Pair<Integer, List<RewardItem>> getItemReward(int itemId) {
        // 优先从缓存获取
        if (rewardCache.containsKey(itemId)) {
            return rewardCache.get(itemId);
        }
        
        int totalprob = 0;
        List<RewardItem> rewards = new ArrayList<>();
        
        // 遍历奖励节点，解析每个奖励项
        for (Data child : getItemData(itemId).getChildByPath("reward").getChildren()) {
            RewardItem reward = new RewardItem();
            reward.itemid = DataTool.getInt("item", child, 0);         // 奖励物品ID
            reward.prob = (byte) DataTool.getInt("prob", child, 0);    // 获得概率
            reward.quantity = (short) DataTool.getInt("count", child, 0); // 数量
            reward.effect = DataTool.getString("Effect", child, "");   // 特效名称
            reward.worldmsg = DataTool.getString("worldMsg", child, null); // 世界公告消息
            reward.period = DataTool.getInt("period", child, -1);      // 有效期（-1为永久）

            // 累加总概率
            totalprob += reward.prob;

            rewards.add(reward);
        }
        
        // 缓存结果
        Pair<Integer, List<RewardItem>> hmm = new Pair<>(totalprob, rewards);
        rewardCache.put(itemId, hmm);
        return hmm;
    }

    /**
     * 判断物品是否拾取即消耗
     * 
     * @param itemId 物品ID
     * @return true表示拾取即消耗
     */
    public boolean isConsumeOnPickup(int itemId) {
        // 优先从缓存获取
        if (consumeOnPickupCache.containsKey(itemId)) {
            return consumeOnPickupCache.get(itemId);
        }
        
        // 获取物品数据
        Data data = getItemData(itemId);
        
        // 检查spec或specEx节点中的consumeOnPickup字段
        boolean consume = DataTool.getIntConvert("spec/consumeOnPickup", data, 0) == 1 
                       || DataTool.getIntConvert("specEx/consumeOnPickup", data, 0) == 1;
        
        // 缓存结果
        consumeOnPickupCache.put(itemId, consume);
        return consume;
    }

    /**
     * 判断武器是否为双手武器
     * 
     * @param itemId 物品ID
     * @return true表示双手武器
     */
    public final boolean isTwoHanded(int itemId) {
        switch (getWeaponType(itemId)) {
            // 双手武器类型列表
            case GENERAL2H_SWING:   // 通用双手挥砍武器
            case BOW:              // 弓
            case CLAW:             // 拳套
            case CROSSBOW:         // 弩
            case POLE_ARM_SWING:   // 长杖挥砍
            case SPEAR_STAB:       // 矛刺击
            case SWORD2H:          // 双手剑
            case GUN:              // 枪
            case KNUCKLE:          // 指节套
                return true;
            default:
                return false;
        }
    }

    /**
     * 判断物品是否为现金物品
     * 
     * @param itemId 物品ID
     * @return true表示现金物品
     */
    public boolean isCash(int itemId) {
        // 通过物品ID判断类型：物品ID / 1000000 = 类型
        int itemType = itemId / 1000000;
        
        // 类型5（消耗品）直接判定为现金物品
        if (itemType == 5) {
            return true;
        }
        
        // 非装备类型（类型1）直接返回false
        if (itemType != 1) {
            return false;
        }

        // 装备类型需要检查cash属性
        Map<String, Integer> eqpStats = getEquipStats(itemId);
        return eqpStats != null && eqpStats.get("cash") == 1;
    }

    /**
     * 判断装备是否可升级（有升级插槽或有可提升的属性）
     * 
     * @param itemId 物品ID
     * @return true表示可升级
     */
    public boolean isUpgradeable(int itemId) {
        // 获取装备对象
        Item it = this.getEquipById(itemId);
        Equip eq = (Equip) it;

        // 有升级插槽或任意属性大于0的装备都可升级
        return (eq.getUpgradeSlots() > 0 || eq.getStr() > 0 || eq.getDex() > 0 || eq.getInt() > 0 || eq.getLuk() > 0 ||
                eq.getWatk() > 0 || eq.getMatk() > 0 || eq.getWdef() > 0 || eq.getMdef() > 0 || eq.getAcc() > 0 ||
                eq.getAvoid() > 0 || eq.getSpeed() > 0 || eq.getJump() > 0 || eq.getHp() > 0 || eq.getMp() > 0);
    }

    /**
     * 判断物品是否不可交易（受服务器配置限制）
     * 
     * @param itemId 物品ID
     * @return true表示不可交易
     */
    public boolean isUnmerchable(int itemId) {
        // 服务器配置强制现金物品不可交易
        if (GameConfig.getServerBoolean("use_enforce_unmerchable_cash") && isCash(itemId)) {
            return true;
        }

        // 服务器配置强制宠物不可交易
        return GameConfig.getServerBoolean("use_enforce_unmerchable_pet") && ItemConstants.isPet(itemId);
    }

    /**
     * 过滤角色可以穿戴的装备
     * 
     * @param chr 角色对象
     * @param items 装备列表
     * @return 可穿戴的装备列表
     */
    public Collection<Item> canWearEquipment(Character chr, Collection<Item> items) {
        // 获取已装备物品栏
        Inventory inv = chr.getInventory(InventoryType.EQUIPPED);
        
        // 如果已装备栏被锁定，直接返回原列表
        if (inv.checked()) {
            return items;
        }
        
        Collection<Item> itemz = new LinkedList<>();
        
        // GM角色可以穿戴所有装备
        if (chr.getJob() == Job.SUPERGM || chr.getJob() == Job.GM) {
            for (Item item : items) {
                Equip equip = (Equip) item;
                equip.wear(true);
                itemz.add(item);
            }
            return itemz;
        }
        // 是否拥有五周年纪念印章（相关逻辑已移除）
        boolean highfivestamp = false;
        
        // 获取角色基础属性和人气值
        int tdex = chr.getDex(), tstr = chr.getStr(), tint = chr.getInt(), tluk = chr.getLuk(), fame = chr.getFame();
        
        // 非GM角色需要累加已装备装备的属性加成
        if (chr.getJob() != Job.SUPERGM || chr.getJob() != Job.GM) {
            for (Item item : inv.list()) {
                Equip equip = (Equip) item;
                tdex += equip.getDex();
                tstr += equip.getStr();
                tluk += equip.getLuk();
                tint += equip.getInt();
            }
        }
        
        // 遍历待检查的装备列表
        for (Item item : items) {
            Equip equip = (Equip) item;
            
            // 获取装备等级需求
            int reqLevel = getEquipLevelReq(equip.getItemId());
            
            // 五周年印章可降低5级需求（最低为0）
            if (highfivestamp) {
                reqLevel -= 5;
                if (reqLevel < 0) {
                    reqLevel = 0;
                }
            }
            
            // 物品数据缺失（如高版本物品未在当前WZ覆盖范围内）时无法判断需求，跳过该装备
            Map<String, Integer> eqStats = getEquipStats(equip.getItemId());
            if (eqStats == null) {
                continue;
            }

            // 依次检查各项需求
            if (reqLevel > chr.getLevel()) {
                continue;  // 等级不足
            } else if (eqStats.get("reqDEX") > tdex) {
                continue;  // 敏捷不足
            } else if (eqStats.get("reqSTR") > tstr) {
                continue;  // 力量不足
            } else if (eqStats.get("reqLUK") > tluk) {
                continue;  // 运气不足
            } else if (eqStats.get("reqINT") > tint) {
                continue;  // 智力不足
            }

            // 检查人气需求（如有）
            int reqPOP = eqStats.get("reqPOP");
            if (reqPOP > 0 && reqPOP > fame) {
                continue;  // 人气不足
            }
            
            // 所有需求满足，标记为可穿戴并加入结果列表
            equip.wear(true);
            itemz.add(equip);
        }
        
        // 标记已装备栏为已检查状态
        inv.checked(true);
        return itemz;
    }

    /**
     * 检查角色是否可以穿戴指定装备到目标槽位
     * 
     * @param chr 角色对象
     * @param equip 装备对象
     * @param dst 目标槽位
     * @return true表示可以穿戴
     */
    public boolean canWearEquipment(Character chr, Equip equip, int dst) {
        int id = equip.getItemId();

        // 结婚戒指特殊处理：刚结婚的玩家不能在当前地图装备（防止双人效果重复导致掉线）
        if (ItemId.isWeddingRing(id) && chr.hasJustMarried()) {
            chr.dropMessage(5, "The Wedding Ring cannot be equipped on this map.");
            return false;
        }

        // 获取装备槽位信息
        String islot = getEquipmentSlot(id);
        
        // WZ中找不到该装备数据时，拒绝穿装并记录警告
        if (islot == null) {
            log.warn("Chr {} tried to equip unknown item {} (no WZ data)", chr.getName(), id);
            return false;
        }
        
        // 检查装备是否允许放入目标槽位
        if (!EquipSlot.getFromTextSlot(islot).isAllowed(dst, isCash(id))) {
            equip.wear(false);
            String itemName = ItemInformationProvider.getInstance().getName(equip.getItemId());
            // 广播警告给GM
            Server.getInstance().broadcastGMMessage(chr.getWorld(), 
                PacketCreator.sendYellowTip("[Warning]: " + chr.getName() + " tried to equip " + itemName + " into slot " + dst + "."));
            // 触发反作弊警报
            AutobanFactory.PACKET_EDIT.alert(chr, chr.getName() + " tried to forcibly equip an item.");
            log.warn("Chr {} tried to equip {} into slot {}", chr.getName(), itemName, dst);
            return false;
        }

        // GM角色可以穿戴所有装备
        if (chr.getJob() == Job.SUPERGM || chr.getJob() == Job.GM) {
            equip.wear(true);
            return true;
        }

        // 是否拥有五周年纪念印章（相关逻辑已移除）
        boolean highfivestamp = false;

        // 获取装备等级需求（五周年印章可降低5级需求）
        int reqLevel = getEquipLevelReq(equip.getItemId());
        if (highfivestamp) {
            reqLevel -= 5;
        }

        // 物品数据缺失（如高版本物品未在当前WZ覆盖范围内）时无法判断需求，直接拒绝穿戴
        Map<String, Integer> eqStats = getEquipStats(equip.getItemId());
        if (eqStats == null) {
            equip.wear(false);
            return false;
        }

        // 检查各项需求是否满足（使用计数器i记录不满足的项数）
        int i = 0;
        if (reqLevel > chr.getLevel()) {
            i++;  // 等级不足
        } else if (eqStats.get("reqDEX") > chr.getTotalDex()) {
            i++;  // 敏捷不足
        } else if (eqStats.get("reqSTR") > chr.getTotalStr()) {
            i++;  // 力量不足
        } else if (eqStats.get("reqLUK") > chr.getTotalLuk()) {
            i++;  // 运气不足
        } else if (eqStats.get("reqINT") > chr.getTotalInt()) {
            i++;  // 智力不足
        }

        // 检查人气需求（如有）
        int reqPOP = eqStats.get("reqPOP");
        if (reqPOP > 0 && reqPOP > chr.getFame()) {
            i++;  // 人气不足
        }

        // 如果有任何需求不满足，拒绝穿戴
        if (i > 0) {
            equip.wear(false);
            return false;
        }
        
        // 所有需求满足，允许穿戴
        equip.wear(true);
        return true;
    }

    public ArrayList<Pair<Integer, String>> getItemDataByName(String name) {
        ArrayList<Pair<Integer, String>> ret = new ArrayList<>();
        for (Pair<Integer, String> itemPair : ItemInformationProvider.getInstance().getAllItems()) {
            if (itemPair.getRight().toLowerCase().contains(name.toLowerCase()) && getItemData(itemPair.left) != null) {
                ret.add(itemPair);
            }
        }
        return ret;
    }

    private Data getEquipLevelInfo(int itemId) {
        Data equipLevelData = equipLevelInfoCache.get(itemId);
        if (equipLevelData == null) {
            if (equipLevelInfoCache.containsKey(itemId)) {
                return null;
            }

            Data iData = getItemData(itemId);
            if (iData != null) {
                Data data = iData.getChildByPath("info/level");
                if (data != null) {
                    equipLevelData = data.getChildByPath("info");
                }
            }

            equipLevelInfoCache.put(itemId, equipLevelData);
        }

        return equipLevelData;
    }

    public int getEquipLevel(int itemId, boolean getMaxLevel) {
        Integer eqLevel = equipMaxLevelCache.get(itemId);
        if (eqLevel == null) {
            eqLevel = 1;    // greater than 1 means that it was supposed to levelup on GMS

            Data data = getEquipLevelInfo(itemId);
            if (data != null) {
                if (getMaxLevel) {
                    int curLevel = 1;

                    while (true) {
                        Data data2 = data.getChildByPath(Integer.toString(curLevel));
                        if (data2 == null || data2.getChildren().size() <= 1) {
                            eqLevel = curLevel;
                            equipMaxLevelCache.put(itemId, eqLevel);
                            break;
                        }

                        curLevel++;
                    }
                } else {
                    Data data2 = data.getChildByPath("1");
                    if (data2 != null && data2.getChildren().size() > 1) {
                        eqLevel = 2;
                    }
                }
            }
        }

        return eqLevel;
    }

    public List<Pair<String, Integer>> getItemLevelupStats(int itemId, int level) {
        List<Pair<String, Integer>> list = new LinkedList<>();
        Data data = getEquipLevelInfo(itemId);
        if (data != null) {
            Data data2 = data.getChildByPath(Integer.toString(level));
            if (data2 != null) {
                for (Data da : data2.getChildren()) {
                    if (Math.random() < 0.9) {
                        if (da.getName().startsWith("incDEXMin")) {
                            list.add(new Pair<>("incDEX", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incDEXMax")))));
                        } else if (da.getName().startsWith("incSTRMin")) {
                            list.add(new Pair<>("incSTR", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incSTRMax")))));
                        } else if (da.getName().startsWith("incINTMin")) {
                            list.add(new Pair<>("incINT", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incINTMax")))));
                        } else if (da.getName().startsWith("incLUKMin")) {
                            list.add(new Pair<>("incLUK", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incLUKMax")))));
                        } else if (da.getName().startsWith("incMHPMin")) {
                            list.add(new Pair<>("incMHP", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incMHPMax")))));
                        } else if (da.getName().startsWith("incMMPMin")) {
                            list.add(new Pair<>("incMMP", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incMMPMax")))));
                        } else if (da.getName().startsWith("incPADMin")) {
                            list.add(new Pair<>("incPAD", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incPADMax")))));
                        } else if (da.getName().startsWith("incMADMin")) {
                            list.add(new Pair<>("incMAD", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incMADMax")))));
                        } else if (da.getName().startsWith("incPDDMin")) {
                            list.add(new Pair<>("incPDD", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incPDDMax")))));
                        } else if (da.getName().startsWith("incMDDMin")) {
                            list.add(new Pair<>("incMDD", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incMDDMax")))));
                        } else if (da.getName().startsWith("incACCMin")) {
                            list.add(new Pair<>("incACC", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incACCMax")))));
                        } else if (da.getName().startsWith("incEVAMin")) {
                            list.add(new Pair<>("incEVA", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incEVAMax")))));
                        } else if (da.getName().startsWith("incSpeedMin")) {
                            list.add(new Pair<>("incSpeed", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incSpeedMax")))));
                        } else if (da.getName().startsWith("incJumpMin")) {
                            list.add(new Pair<>("incJump", Randomizer.rand(DataTool.getInt(da), DataTool.getInt(data2.getChildByPath("incJumpMax")))));
                        }
                    }
                }
            }
        }

        return list;
    }

    private static int getCrystalForLevel(int level) {
        int range = (level - 1) / 10;

        if (range < 5) {
            return ItemId.BASIC_MONSTER_CRYSTAL_1;
        } else if (range > 11) {
            return ItemId.ADVANCED_MONSTER_CRYSTAL_3;
        } else {
            return switch (range) {
                case 5 -> ItemId.BASIC_MONSTER_CRYSTAL_2;
                case 6 -> ItemId.BASIC_MONSTER_CRYSTAL_3;
                case 7 -> ItemId.INTERMEDIATE_MONSTER_CRYSTAL_1;
                case 8 -> ItemId.INTERMEDIATE_MONSTER_CRYSTAL_2;
                case 9 -> ItemId.INTERMEDIATE_MONSTER_CRYSTAL_3;
                case 10 -> ItemId.ADVANCED_MONSTER_CRYSTAL_1;
                default -> ItemId.ADVANCED_MONSTER_CRYSTAL_2;
            };
        }
    }

    public Pair<String, Integer> getMakerReagentStatUpgrade(int itemId) {
        try {
            Pair<String, Integer> statUpgd = statUpgradeMakerCache.get(itemId);
            if (statUpgd != null) {
                return statUpgd;
            } else if (statUpgradeMakerCache.containsKey(itemId)) {
                return null;
            }

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT stat, value FROM makerreagentdata WHERE itemid = ?")) {
                ps.setInt(1, itemId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String statType = rs.getString("stat");
                        int statGain = rs.getInt("value");

                        statUpgd = new Pair<>(statType, statGain);
                    }
                }
            }

            statUpgradeMakerCache.put(itemId, statUpgd);
            return statUpgd;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getMakerCrystalFromLeftover(Integer leftoverId) {
        try {
            Integer itemid = mobCrystalMakerCache.get(leftoverId);
            if (itemid != null) {
                return itemid;
            }

            itemid = -1;

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement("SELECT dropperid FROM drop_data WHERE itemid = ? ORDER BY dropperid;")) {
                ps.setInt(1, leftoverId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int dropperid = rs.getInt("dropperid");
                        itemid = getCrystalForLevel(LifeFactory.getMonsterLevel(dropperid));
                    }
                }
            }

            mobCrystalMakerCache.put(leftoverId, itemid);
            return itemid;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public MakerItemCreateEntry getMakerItemEntry(int toCreate) {
        MakerItemCreateEntry makerEntry;

        if ((makerEntry = makerItemCache.get(toCreate)) != null) {
            return new MakerItemCreateEntry(makerEntry);
        } else {
            try (Connection con = DatabaseConnection.getConnection()) {
                int reqLevel = -1;
                int reqMakerLevel = -1;
                int cost = -1;
                int toGive = -1;
                try (PreparedStatement ps = con.prepareStatement("SELECT req_level, req_maker_level, req_meso, quantity FROM makercreatedata WHERE itemid = ?")) {
                    ps.setInt(1, toCreate);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            reqLevel = rs.getInt("req_level");
                            reqMakerLevel = rs.getInt("req_maker_level");
                            cost = rs.getInt("req_meso");
                            toGive = rs.getInt("quantity");
                        }
                    }
                }

                makerEntry = new MakerItemCreateEntry(cost, reqLevel, reqMakerLevel);
                makerEntry.addGainItem(toCreate, toGive);

                try (PreparedStatement ps = con.prepareStatement("SELECT req_item, count FROM makerrecipedata WHERE itemid = ?")) {
                    ps.setInt(1, toCreate);

                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            makerEntry.addReqItem(rs.getInt("req_item"), rs.getInt("count"));
                        }
                    }
                }
                makerItemCache.put(toCreate, new MakerItemCreateEntry(makerEntry));
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                makerEntry = null;
            }
        }

        return makerEntry;
    }

    public int getMakerCrystalFromEquip(Integer equipId) {
        try {
            return getCrystalForLevel(getEquipLevelReq(equipId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public int getMakerStimulantFromEquip(Integer equipId) {
        try {
            return getCrystalForLevel(getEquipLevelReq(equipId));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<Pair<Integer, Integer>> getMakerDisassembledItems(Integer itemId) {
        List<Pair<Integer, Integer>> items = new LinkedList<>();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT req_item, count FROM makerrecipedata WHERE itemid = ? AND req_item >= 4260000 AND req_item < 4270000")) {
            ps.setInt(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Pair<>(rs.getInt("req_item"), rs.getInt("count") / 2));   // return to the player half of the crystals needed
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return items;
    }

    public int getMakerDisassembledFee(Integer itemId) {
        int fee = -1;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT req_meso FROM makercreatedata WHERE itemid = ?")) {
            ps.setInt(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {   // cost is 13.6363~ % of the original value, trim by 1000.
                    float val = (float) (rs.getInt("req_meso") * 0.13636363636364);
                    fee = (int) (val / 1000);
                    fee *= 1000;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fee;
    }

    public int getMakerStimulant(int itemId) {  // thanks to Arnah
        Integer itemid = makerCatalystCache.get(itemId);
        if (itemid != null) {
            return itemid;
        }

        itemid = -1;
        for (Data md : etcData.getData("ItemMake.img").getChildren()) {
            Data me = md.getChildByPath(StringUtil.getLeftPaddedStr(Integer.toString(itemId), '0', 8));

            if (me != null) {
                itemid = DataTool.getInt(me.getChildByPath("catalyst"), -1);
                break;
            }
        }

        makerCatalystCache.put(itemId, itemid);
        return itemid;
    }

    public Set<String> getWhoDrops(Integer itemId) {
        Set<String> list = new HashSet<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT dropperid FROM drop_data WHERE itemid = ? LIMIT 50")) {
            ps.setInt(1, itemId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String resultName = MonsterInformationProvider.getInstance().getMobNameFromId(rs.getInt("dropperid"));
                    if (!resultName.isEmpty()) {
                        list.add(resultName);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    private boolean canUseSkillBook(Character player, Integer skillBookId) {
        Map<String, Integer> skilldata = getSkillStats(skillBookId, player.getJob().getId());
        if (skilldata == null || skilldata.get("skillid") == 0) {
            return false;
        }

        Skill skill2 = SkillFactory.getSkill(skilldata.get("skillid"));
        return (skilldata.get("skillid") != 0 && ((player.getSkillLevel(skill2) >= skilldata.get("reqSkillLevel") || skilldata.get("reqSkillLevel") == 0) && player.getMasterLevel(skill2) < skilldata.get("masterLevel")));
    }

    public List<Integer> usableMasteryBooks(Character player) {
        List<Integer> masterybook = new LinkedList<>();
        for (Integer i = 2290000; i <= 2290139; i++) {
            if (canUseSkillBook(player, i)) {
                masterybook.add(i);
            }
        }

        return masterybook;
    }

    public List<Integer> usableSkillBooks(Character player) {
        List<Integer> skillbook = new LinkedList<>();
        for (Integer i = 2280000; i <= 2280019; i++) {
            if (canUseSkillBook(player, i)) {
                skillbook.add(i);
            }
        }

        return skillbook;
    }

    public final QuestConsItem getQuestConsumablesInfo(final int itemId) {
        if (questItemConsCache.containsKey(itemId)) {
            return questItemConsCache.get(itemId);
        }
        Data data = getItemData(itemId);
        QuestConsItem qcItem = null;

        Data infoData = data.getChildByPath("info");
        if (infoData.getChildByPath("uiData") != null) {
            qcItem = new QuestConsItem();
            qcItem.exp = DataTool.getInt("exp", infoData);
            qcItem.grade = DataTool.getInt("grade", infoData);
            qcItem.questid = DataTool.getInt("questId", infoData);
            qcItem.items = new HashMap<>(2);

            Map<Integer, Integer> cItems = qcItem.items;
            Data ciData = infoData.getChildByPath("consumeItem");
            if (ciData != null) {
                for (Data ciItem : ciData.getChildren()) {
                    int itemid = DataTool.getInt("0", ciItem);
                    int qty = DataTool.getInt("1", ciItem);

                    cItems.put(itemid, qty);
                }
            }
        }

        questItemConsCache.put(itemId, qcItem);
        return qcItem;
    }

    public final ItemCashInfo getItemCashInfo(int itemId) {
        if (itemCashInfoCache.containsKey(itemId)) {
            return itemCashInfoCache.get(itemId);
        }
        Data item = getItemData(itemId);
        if (item == null) {
            return null;
        }
        Data info = item.getChildByPath("info");
        if (info == null) {
            return null;
        }
        ItemCashInfo ret = new ItemCashInfo();
        ret.addTime = DataTool.getInt("addTime", info, 0);
        ret.maxDays = DataTool.getInt("maxDays", info, 0);
        itemCashInfoCache.put(itemId, ret);
        return ret;
    }

    public class ScriptedItem {

        private final boolean runOnPickup;
        private final int npc;
        private final String script;

        public ScriptedItem(int npc, String script, boolean rop) {
            this.npc = npc;
            this.script = script;
            this.runOnPickup = rop;
        }

        public int getNpc() {
            return npc;
        }

        public String getScript() {
            return script;
        }

        public boolean runOnPickup() {
            return runOnPickup;
        }
    }

    public static final class RewardItem {

        public int itemid, period;
        public short prob, quantity;
        public String effect, worldmsg;
    }

    public static final class QuestConsItem {

        public int questid, exp, grade;
        public Map<Integer, Integer> items;

        public Integer getItemRequirement(int itemid) {
            return items.get(itemid);
        }

    }

    public static final class ItemCashInfo {

        public int maxDays;
        public long addTime;

    }

    /**
     * 根据物品名称模糊搜索物品ID和名称
     * 遍历所有物品缓存进行名称匹配，并记录搜索耗时到服务端日志，便于排查性能问题
     *
     * @param search 搜索关键词（支持模糊匹配）
     * @return 匹配的物品列表 [物品ID, 物品名称]
     */
    public static ArrayList<Pair<Integer, String>> getItemsIDsFromName(String search) {
        long startTime = System.currentTimeMillis();
        ArrayList<Pair<Integer, String>> retItems = new ArrayList<>();
        List<Pair<Integer, String>> allItems = getInstance().getAllItems();
        long loadTime = System.currentTimeMillis();

        String lowerSearch = search.toLowerCase();
        for (Pair<Integer, String> itemPair : allItems) {
            if (itemPair.getRight().toLowerCase().contains(lowerSearch)) {
                retItems.add(itemPair);
            }
        }
        long endTime = System.currentTimeMillis();

        log.info("[物品查询] 关键词=\"{}\"，总物品数={}，匹配结果={}，加载耗时={}ms，搜索耗时={}ms，总耗时={}ms",
                search, allItems.size(), retItems.size(),
                loadTime - startTime, endTime - loadTime, endTime - startTime);

        // 分页后每页仅8条，即使数百结果也不会溢出；此处仅做极端情况记录
        if (retItems.size() > 500) {
            log.warn("[物品查询] 关键词=\"{}\" 匹配结果过多({}条)，建议缩小搜索范围", search, retItems.size());
        }

        return retItems;
    }
}