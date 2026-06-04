/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
package org.gms.client.creator;

import org.gms.client.Job;
import org.gms.client.Skill;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.config.GameConfig;
import org.gms.util.Pair;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 【类型】CharacterFactoryRecipe（class），包 {@code org.gms.client.creator}。
 *
 * 角色创建配方/配置类，封装新建角色所需的全部参数：职业、等级、出生地图、初始四维属性、
 * 最大 HP/MP、AP/SP、初始装备（上衣/下装/鞋子/武器）、初始技能与背包物品列表。
 *
 * <p>该类作为角色创建工厂的数据载体，定义了不同类型角色的创建配置，
 * 包括基础属性、起始装备、技能和物品等。通过该类可以灵活配置不同职业的创建参数。</p>
 *
 * @author RonanLana
 */
public class CharacterFactoryRecipe {
    /** 角色职业：决定角色的技能树和属性成长 */
    private final Job job;
    /** 角色等级：新创建角色的起始等级 */
    private final int level;
    /** 出生地图ID：角色创建后首次进入的地图 */
    private final int map;
    /** 起始上衣装备ID：角色初始上衣外观 */
    private final int top;
    /** 起始下装装备ID：角色初始下装外观 */
    private final int bottom;
    /** 起始鞋子装备ID：角色初始鞋子外观 */
    private final int shoes;
    /** 起始武器装备ID：角色初始武器外观 */
    private final int weapon;
    /** 力量值：角色力量属性，默认为4 */
    private int str = 4, 
    /** 敏捷值：角色敏捷属性，默认为4 */
    dex = 4, 
    /** 智力值：角色智力属性，默认为4 */
    int_ = 4, 
    /** 运气值：角色运气属性，默认为4 */
    luk = 4;
    /** 最大HP：角色最大生命值，默认为50 */
    private int maxHp = 50, 
    /** 最大MP：角色最大魔法值，默认为5 */
    maxMp = 5;
    /** 可分配AP点数：角色可自由分配的属性点 */
    private int ap = 0, 
    /** 可分配SP点数：角色可自由分配的技能点 */
    sp = 0;
    /** 初始金币：角色创建时拥有的金币数量 */
    private int meso = 0;
    /** 初始技能列表：角色创建时拥有的技能及其等级 */
    private final List<Pair<Skill, Integer>> skills = new LinkedList<>();

    /** 初始物品列表：角色创建时拥有的物品及其库存类型 */
    private final List<Pair<Item, InventoryType>> itemsWithType = new LinkedList<>();
    /** 库存类型位置计数器：用于跟踪各库存类型的物品位置分配 */
    private final Map<InventoryType, AtomicInteger> runningTypePosition = new LinkedHashMap<>();

    /**
     * 构造函数：创建角色创建配方实例。
     * 
     * <p>初始化角色的基本创建参数，包括职业、等级、地图和初始装备。
     * 根据服务器配置决定是否自动分配初始AP点数。</p>
     * 
     * @param job 角色职业
     * @param level 角色等级
     * @param map 出生地图ID
     * @param top 上衣装备ID
     * @param bottom 下装装备ID
     * @param shoes 鞋子装备ID
     * @param weapon 武器装备ID
     */
    public CharacterFactoryRecipe(Job job, int level, int map, int top, int bottom, int shoes, int weapon) {
        this.job = job;
        this.level = level;
        this.map = map;
        this.top = top;
        this.bottom = bottom;
        this.shoes = shoes;
        this.weapon = weapon;

        if (!GameConfig.getServerBoolean("use_starting_ap_4")) {
            if (GameConfig.getServerBoolean("use_auto_assign_starters_ap")) {
                str = 12;
                dex = 5;
            } else {
                ap = 9;
            }
        }
    }

    /**
     * 设置角色力量值。
     * 
     * @param v 力量值
     */
    public void setStr(int v) {
        str = v;
    }

    /**
     * 设置角色敏捷值。
     * 
     * @param v 敏捷值
     */
    public void setDex(int v) {
        dex = v;
    }

    /**
     * 设置角色智力值。
     * 
     * @param v 智力值
     */
    public void setInt(int v) {
        int_ = v;
    }

    /**
     * 设置角色运气值。
     * 
     * @param v 运气值
     */
    public void setLuk(int v) {
        luk = v;
    }

    /**
     * 设置角色最大HP值。
     * 
     * @param v 最大HP值
     */
    public void setMaxHp(int v) {
        maxHp = v;
    }

    /**
     * 设置角色最大MP值。
     * 
     * @param v 最大MP值
     */
    public void setMaxMp(int v) {
        maxMp = v;
    }

    /**
     * 设置角色剩余AP点数。
     * 
     * @param v AP点数
     */
    public void setRemainingAp(int v) {
        ap = v;
    }

    /**
     * 设置角色剩余SP点数。
     * 
     * @param v SP点数
     */
    public void setRemainingSp(int v) {
        sp = v;
    }

    /**
     * 设置角色初始金币数量。
     * 
     * @param v 金币数量
     */
    public void setMeso(int v) {
        meso = v;
    }

    /**
     * 添加角色初始技能。
     * 
     * @param skill 技能对象
     * @param level 技能等级
     */
    public void addStartingSkillLevel(Skill skill, int level) {
        skills.add(new Pair<>(skill, level));
    }

    /**
     * 添加角色初始装备。
     * 
     * @param eqpItem 装备物品
     */
    public void addStartingEquipment(Item eqpItem) {
        itemsWithType.add(new Pair<>(eqpItem, InventoryType.EQUIP));
    }

    /**
     * 添加角色初始物品。
     * 
     * <p>为指定库存类型添加物品，并自动分配位置ID。</p>
     * 
     * @param itemid 物品ID
     * @param quantity 物品数量
     * @param itemType 库存类型
     */
    public void addStartingItem(int itemid, int quantity, InventoryType itemType) {
        AtomicInteger p = runningTypePosition.get(itemType);
        if (p == null) {
            p = new AtomicInteger(0);
            runningTypePosition.put(itemType, p);
        }

        itemsWithType.add(new Pair<>(new Item(itemid, (short) p.getAndIncrement(), (short) quantity), itemType));
    }

    /**
     * 获取角色职业。
     * 
     * @return 角色职业
     */
    public Job getJob() {
        return job;
    }

    /**
     * 获取角色等级。
     * 
     * @return 角色等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 获取出生地图ID。
     * 
     * @return 出生地图ID
     */
    public int getMap() {
        return map;
    }

    /**
     * 获取上衣装备ID。
     * 
     * @return 上衣装备ID
     */
    public int getTop() {
        return top;
    }

    /**
     * 获取下装装备ID。
     * 
     * @return 下装装备ID
     */
    public int getBottom() {
        return bottom;
    }

    /**
     * 获取鞋子装备ID。
     * 
     * @return 鞋子装备ID
     */
    public int getShoes() {
        return shoes;
    }

    /**
     * 获取武器装备ID。
     * 
     * @return 武器装备ID
     */
    public int getWeapon() {
        return weapon;
    }

    /**
     * 获取力量值。
     * 
     * @return 力量值
     */
    public int getStr() {
        return str;
    }

    /**
     * 获取敏捷值。
     * 
     * @return 敏捷值
     */
    public int getDex() {
        return dex;
    }

    /**
     * 获取智力值。
     * 
     * @return 智力值
     */
    public int getInt() {
        return int_;
    }

    /**
     * 获取运气值。
     * 
     * @return 运气值
     */
    public int getLuk() {
        return luk;
    }

    /**
     * 获取最大HP值。
     * 
     * @return 最大HP值
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * 获取最大MP值。
     * 
     * @return 最大MP值
     */
    public int getMaxMp() {
        return maxMp;
    }

    /**
     * 获取剩余AP点数。
     * 
     * @return 剩余AP点数
     */
    public int getRemainingAp() {
        return ap;
    }

    /**
     * 获取剩余SP点数。
     * 
     * @return 剩余SP点数
     */
    public int getRemainingSp() {
        return sp;
    }

    /**
     * 获取初始金币数量。
     * 
     * @return 金币数量
     */
    public int getMeso() {
        return meso;
    }

    /**
     * 获取初始技能列表。
     * 
     * @return 技能列表，包含技能对象和等级的配对
     */
    public List<Pair<Skill, Integer>> getStartingSkillLevel() {
        return skills;
    }

    /**
     * 获取初始物品列表。
     * 
     * @return 物品列表，包含物品对象和库存类型的配对
     */
    public List<Pair<Item, InventoryType>> getStartingItems() {
        return itemsWithType;
    }
}