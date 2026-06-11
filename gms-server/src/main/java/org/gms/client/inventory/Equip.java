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
package org.gms.client.inventory;

import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.gms.constants.game.ExpTable;
import org.gms.constants.inventory.ItemConstants;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 装备实体类
 *
 * <p>继承 {@link Item}，在物品基础属性之上扩展了装备特有的属性系统，包括：</p>
 * <ul>
 *   <li>16 种可强化属性（四维、攻防、命中回避、移速跳跃、金锤子、升级槽）</li>
 *   <li>卷轴强化系统（成功 / 失败 / 诅咒三种结果）</li>
 *   <li>装备等级与经验系统（击杀怪物获得经验，升级时随机提升属性）</li>
 *   <li>金锤子（vicious hammer）系统，允许额外增加升级次数</li>
 * </ul>
 *
 * @author Patrick Huy, Matthias Butz, Jan Christian Meyer
 * @see Item
 */
public class Equip extends Item {
    private static final Logger log = LoggerFactory.getLogger(Equip.class);

    /**
     * 卷轴结果枚举
     */
    public enum ScrollResult {
        /** 失败 */
        FAIL(0),
        /** 成功 */
        SUCCESS(1),
        /** 诅咒（装备消失） */
        CURSE(2);
        private int value = -1;

        ScrollResult(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * 属性升级类型枚举
     * 对应装备可强化的16种属性
     */
    public enum StatUpgrade {
        /** 敏捷 */
        incDEX(0),
        /** 力量 */
        incSTR(1),
        /** 智力 */
        incINT(2),
        /** 运气 */
        incLUK(3),
        /** 最大HP */
        incMHP(4),
        /** 最大MP */
        incMMP(5),
        /** 物理攻击力 */
        incPAD(6),
        /** 魔法攻击力 */
        incMAD(7),
        /** 物理防御力 */
        incPDD(8),
        /** 魔法防御力 */
        incMDD(9),
        /** 回避率 */
        incEVA(10),
        /** 命中率 */
        incACC(11),
        /** 移动速度 */
        incSpeed(12),
        /** 跳跃力 */
        incJump(13),
        /** 金锤子 */
        incVicious(14),
        /** 升级槽 */
        incSlot(15);
        private int value = -1;

        StatUpgrade(int value) {
            this.value = value;
        }
    }

    /** 剩余升级次数（卷轴强化槽位） */
    private byte upgradeSlots;
    /** 装备等级（用于穿戴限制） */
    @Getter
    private byte level;
    /** 装备当前成长等级（装备经验升级系统） */
    @Getter
    private byte itemLevel;
    /** 物品标记位（锁定、绑定等标识） */
    private short flag;
    /** 力量属性加成 */
    private short str;
    /** 敏捷属性加成 */
    private short dex;
    /** 智力属性加成 */
    private short _int;
    /** 运气属性加成 */
    private short luk;
    /** 最大HP属性加成 */
    private short hp;
    /** 最大MP属性加成 */
    private short mp;
    /** 物理攻击力加成 */
    private short watk;
    /** 魔法攻击力加成 */
    private short matk;
    /** 物理防御力加成 */
    private short wdef;
    /** 魔法防御力加成 */
    private short mdef;
    /** 命中率加成 */
    private short acc;
    /** 回避率加成 */
    private short avoid;
    /** 手技加成（影响卷轴成功率） */
    private short hands;
    /** 移动速度加成 */
    private short speed;
    /** 跳跃力加成 */
    private short jump;
    /** 金锤子已使用次数（每次使用可增加一次升级机会） */
    private short vicious;
    /** 当前累积的装备经验值 */
    private float itemExp;
    /** 戒指唯一ID，非戒指类装备为 -1 */
    private int ringid = -1;
    /** 强化等级（星之力系统） */
    private short enhanceLevel;
    /** 是否已被角色穿戴 */
    private boolean wear = false;
    /** 标记本轮升级评估中装备是否具备可升级属性 */
    private boolean isUpgradeable;
    /** 是否为元素装备（timeless/reverse 等，具备预设的属性升级路径） */
    private boolean isElemental = false;
    /** 物品信息提供者单例 */
    private static final ItemInformationProvider ii = ItemInformationProvider.getInstance();

    /**
     * 构造装备对象，初始升级槽位为 0
     *
     * @param id       物品ID
     * @param position 背包位置
     */
    public Equip(int id, short position) {
        this(id, position, 0);
    }

    /**
     * 构造装备对象，指定初始升级槽位
     *
     * @param id       物品ID
     * @param position 背包位置
     * @param slots    初始升级次数
     */
    public Equip(int id, short position, int slots) {
        super(id, position, (short) 1);
        this.upgradeSlots = (byte) slots;
        this.itemExp = 0;
        this.itemLevel = 1;

        this.isElemental = (ii.getEquipLevel(id, false) > 1);
    }

    /**
     * 深拷贝当前装备对象
     *
     * @return 新的装备对象，属性值与当前一致
     */
    @Override
    public Item copy() {
        Equip ret = new Equip(getItemId(), getPosition(), getUpgradeSlots());
        ret.str = str;
        ret.dex = dex;
        ret._int = _int;
        ret.luk = luk;
        ret.hp = hp;
        ret.mp = mp;
        ret.matk = matk;
        ret.mdef = mdef;
        ret.watk = watk;
        ret.wdef = wdef;
        ret.acc = acc;
        ret.avoid = avoid;
        ret.hands = hands;
        ret.speed = speed;
        ret.jump = jump;
        ret.flag = flag;
        ret.vicious = vicious;
        ret.enhanceLevel = enhanceLevel;
        ret.upgradeSlots = upgradeSlots;
        ret.itemLevel = itemLevel;
        ret.itemExp = itemExp;
        ret.level = level;
        ret.itemLog = new LinkedList<>(itemLog);
        ret.setOwner(getOwner());
        ret.setQuantity(getQuantity());
        ret.setExpiration(getExpiration());
        ret.setGiftFrom(getGiftFrom());
        return ret;
    }

    /**
     * 获取物品标记位
     *
     * @return 标记位数值
     */
    @Override
    public short getFlag() {
        return flag;
    }

    /**
     * 获取物品类型
     *
     * @return 1 代表装备类型
     */
    @Override
    public byte getItemType() {
        return 1;
    }

    /**
     * 获取剩余升级次数
     *
     * @return 剩余可升级次数
     */
    public byte getUpgradeSlots() {
        return upgradeSlots;
    }

    /**
     * 获取力量属性加成
     *
     * @return 力量加成值
     */
    public short getStr() {
        return str;
    }

    /**
     * 获取敏捷属性加成
     *
     * @return 敏捷加成值
     */
    public short getDex() {
        return dex;
    }

    /**
     * 获取智力属性加成
     *
     * @return 智力加成值
     */
    public short getInt() {
        return _int;
    }

    /**
     * 获取运气属性加成
     *
     * @return 运气加成值
     */
    public short getLuk() {
        return luk;
    }

    /**
     * 获取最大HP属性加成
     *
     * @return HP加成值
     */
    public short getHp() {
        return hp;
    }

    /**
     * 获取最大MP属性加成
     *
     * @return MP加成值
     */
    public short getMp() {
        return mp;
    }

    /**
     * 获取物理攻击力加成
     *
     * @return 物理攻击力加成值
     */
    public short getWatk() {
        return watk;
    }

    /**
     * 获取魔法攻击力加成
     *
     * @return 魔法攻击力加成值
     */
    public short getMatk() {
        return matk;
    }

    /**
     * 获取物理防御力加成
     *
     * @return 物理防御力加成值
     */
    public short getWdef() {
        return wdef;
    }

    /**
     * 获取魔法防御力加成
     *
     * @return 魔法防御力加成值
     */
    public short getMdef() {
        return mdef;
    }

    /**
     * 获取命中率加成
     *
     * @return 命中率加成值
     */
    public short getAcc() {
        return acc;
    }

    /**
     * 获取回避率加成
     *
     * @return 回避率加成值
     */
    public short getAvoid() {
        return avoid;
    }

    /**
     * 获取手技加成
     *
     * @return 手技加成值
     */
    public short getHands() {
        return hands;
    }

    /**
     * 获取移动速度加成
     *
     * @return 移动速度加成值
     */
    public short getSpeed() {
        return speed;
    }

    /**
     * 获取跳跃力加成
     *
     * @return 跳跃力加成值
     */
    public short getJump() {
        return jump;
    }

    /**
     * 获取金锤子已使用次数
     *
     * @return 金锤子使用次数
     */
    public short getVicious() {
        return vicious;
    }

    /**
     * 设置物品标记位
     *
     * @param flag 标记位数值
     */
    @Override
    public void setFlag(short flag) {
        this.flag = flag;
    }

    /**
     * 设置力量属性加成
     *
     * @param str 力量加成值
     */
    public void setStr(short str) {
        this.str = str;
    }

    /**
     * 设置敏捷属性加成
     *
     * @param dex 敏捷加成值
     */
    public void setDex(short dex) {
        this.dex = dex;
    }

    /**
     * 设置智力属性加成
     *
     * @param _int 智力加成值
     */
    public void setInt(short _int) {
        this._int = _int;
    }

    /**
     * 设置运气属性加成
     *
     * @param luk 运气加成值
     */
    public void setLuk(short luk) {
        this.luk = luk;
    }

    /**
     * 设置最大HP属性加成
     *
     * @param hp HP加成值
     */
    public void setHp(short hp) {
        this.hp = hp;
    }

    /**
     * 设置最大MP属性加成
     *
     * @param mp MP加成值
     */
    public void setMp(short mp) {
        this.mp = mp;
    }

    /**
     * 设置物理攻击力加成
     *
     * @param watk 物理攻击力加成值
     */
    public void setWatk(short watk) {
        this.watk = watk;
    }

    /**
     * 设置魔法攻击力加成
     *
     * @param matk 魔法攻击力加成值
     */
    public void setMatk(short matk) {
        this.matk = matk;
    }

    /**
     * 设置物理防御力加成
     *
     * @param wdef 物理防御力加成值
     */
    public void setWdef(short wdef) {
        this.wdef = wdef;
    }

    /**
     * 设置魔法防御力加成
     *
     * @param mdef 魔法防御力加成值
     */
    public void setMdef(short mdef) {
        this.mdef = mdef;
    }

    /**
     * 设置命中率加成
     *
     * @param acc 命中率加成值
     */
    public void setAcc(short acc) {
        this.acc = acc;
    }

    /**
     * 设置回避率加成
     *
     * @param avoid 回避率加成值
     */
    public void setAvoid(short avoid) {
        this.avoid = avoid;
    }

    /**
     * 设置手技加成
     *
     * @param hands 手技加成值
     */
    public void setHands(short hands) {
        this.hands = hands;
    }

    /**
     * 设置移动速度加成
     *
     * @param speed 移动速度加成值
     */
    public void setSpeed(short speed) {
        this.speed = speed;
    }

    /**
     * 设置跳跃力加成
     *
     * @param jump 跳跃力加成值
     */
    public void setJump(short jump) {
        this.jump = jump;
    }

    /**
     * 设置金锤子已使用次数
     *
     * @param vicious 金锤子使用次数
     */
    public void setVicious(short vicious) {
        this.vicious = vicious;
    }

    /**
     * 设置剩余升级次数
     *
     * @param upgradeSlots 剩余升级次数
     */
    public void setUpgradeSlots(byte upgradeSlots) {
        this.upgradeSlots = upgradeSlots;
    }

    /**
     * 获取装备需求等级（穿戴限制等级）
     *
     * @return 需求等级
     */
    public byte getLevel() {
        return level;
    }

    /**
     * 设置装备需求等级
     *
     * @param level 需求等级
     */
    public void setLevel(byte level) {
        this.level = level;
    }

    /**
     * 计算属性升级的修正除数
     * <p>根据配置决定属性点和非属性点（HP/MP等）的升级修正值，
     * 值越小，相同当前属性值下可获得的升级量越大。</p>
     *
     * @param isAttribute true 表示四维属性，false 表示 HP/MP/攻防等
     * @return 修正除数
     */
    private static int getStatModifier(boolean isAttribute) {
        if (GameConfig.getServerBoolean("use_equipment_level_up_power")) {
            if (isAttribute) {
                return 2;
            } else {
                return 4;
            }
        } else {
            if (isAttribute) {
                return 4;
            } else {
                return 16;
            }
        }
    }

    /**
     * 随机生成属性升级量
     * <p>使用二次分布算法，在 [0, min(top, 配置上限)] 范围内生成随机值，
     * 算法优化来自 David A.</p>
     *
     * @param top 理论最大升级量
     * @return 随机生成的升级量
     */
    private static int randomizeStatUpgrade(int top) {
        int limit = Math.min(top, GameConfig.getServerInt("max_equipment_level_up_stat_up"));

        int poolCount = (limit * (limit + 1) / 2) + limit;
        int rnd = Randomizer.rand(0, poolCount);

        int stat = 0;
        if (rnd >= limit) {
            rnd -= limit;
            stat = 1 + (int) Math.floor((-1 + Math.sqrt((8 * rnd) + 1)) / 2);    // optimized randomizeStatUpgrade author: David A.
        }

        return stat;
    }

    /**
     * 判断是否为物理武器
     *
     * @param itemid 物品ID
     * @return true 表示物理武器（物功 >= 魔功），false 表示魔法武器
     */
    private static boolean isPhysicalWeapon(int itemid) {
        Equip eqp = (Equip) ii.getEquipById(itemid);
        return eqp.getWatk() >= eqp.getMatk();
    }

    /**
     * 判断某属性是否不在武器的亲和范围内
     * <p>Vcoc 的设计思路：非亲和方向的攻击力期望收益应降低。</p>
     *
     * @param name 属性升级类型
     * @return true 表示非武器亲和属性，应降低升级期望
     */
    private boolean isNotWeaponAffinity(StatUpgrade name) {

        if (ItemConstants.isWeapon(this.getItemId())) {
            if (name.equals(StatUpgrade.incPAD)) {
                return !isPhysicalWeapon(this.getItemId());
            } else if (name.equals(StatUpgrade.incMAD)) {
                return isPhysicalWeapon(this.getItemId());
            }
        }

        return false;
    }

    /**
     * 计算并添加单个属性的升级量到属性列表
     * <p>根据当前属性值计算理论最大升级量，随机生成实际升级量。</p>
     *
     * @param stats      属性升级列表，结果添加于此
     * @param name       属性类型
     * @param curStat    当前属性值
     * @param isAttribute 是否为四维属性（影响修正系数）
     */
    private void getUnitStatUpgrade(List<Pair<StatUpgrade, Integer>> stats, StatUpgrade name, int curStat, boolean isAttribute) {
        isUpgradeable = true;

        int maxUpgrade = randomizeStatUpgrade((int) (1 + (curStat / (getStatModifier(isAttribute) * (isNotWeaponAffinity(name) ? 2.7 : 1)))));
        if (maxUpgrade == 0) {
            return;
        }

        stats.add(new Pair<>(name, maxUpgrade));
    }

    /**
     * 为单位插槽尝试添加升级属性（默认10%成功率）
     *
     * @param stats 存储升级属性的列表
     * @param name  要尝试升级的属性类型
     */
    private static void getUnitSlotUpgrade(List<Pair<StatUpgrade, Integer>> stats, StatUpgrade name) {
        getUnitSlotUpgrade(stats, name, 0.1);
    }

    /**
     * 为单位插槽尝试添加升级属性（可配置成功率）
     *
     * @param stats  存储升级属性的列表
     * @param name   要尝试升级的属性类型
     * @param chance 成功概率（0.0~1.0）
     */
    private static void getUnitSlotUpgrade(List<Pair<StatUpgrade, Integer>> stats, StatUpgrade name, double chance) {
        // 随机判定是否触发属性升级
        if (Math.random() <= chance) {
            stats.add(new Pair<>(name, 1));
        }
    }

    /**
     * 根据装备等级决定是否增加升级槽或减少金锤子使用次数
     * 由配置项 use_equipment_level_up_slots 和 use_equipment_level_up_vicious 控制
     *
     * @param stats      属性升级列表
     * @param equipLevel 装备需求等级
     */
    private void UpgradeSlotProcessing(List<Pair<StatUpgrade, Integer>> stats, int equipLevel) {
        if (GameConfig.getServerBoolean("use_equipment_level_up_slots")) {
            getUnitSlotUpgrade(stats, StatUpgrade.incSlot);
        }
        if (GameConfig.getServerBoolean("use_equipment_level_up_vicious") && vicious > 0) {
            // 从配置读取等级范围概率表
            double[][] chanceList = {{0, 255, 0.1}};
            String chanceParam = GameConfig.getServerString("use_equipment_level_up_vicious_levelrange_chance");
            if (chanceParam != null) {
                try {
                    chanceList = JSONObject.parseObject(chanceParam, double[][].class);
                } catch (Throwable e) {
                    log.warn("金锤子装备等级范围概率参数解析失败，请检查是否正确");
                }
            }
            for (double[] obj : chanceList) {
                double minLevel = obj[0], maxLevel = obj[1], chance = obj[2];
                if (equipLevel >= minLevel && equipLevel <= maxLevel) {
                    getUnitSlotUpgrade(stats, StatUpgrade.incVicious, chance);
                    break;
                }
            }
        }
    }

    /**
     * 遍历所有大于 0 的属性值，尝试为其生成升级量
     * <p>只有当前属性值 > 0 才会参与升级评估。</p>
     *
     * @param stats 属性升级列表，结果添加于此
     */
    private void improveDefaultStats(List<Pair<StatUpgrade, Integer>> stats) {
        if (dex > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incDEX, dex, true);
        }
        if (str > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incSTR, str, true);
        }
        if (_int > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incINT, _int, true);
        }
        if (luk > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incLUK, luk, true);
        }
        if (hp > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incMHP, hp, false);
        }
        if (mp > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incMMP, mp, false);
        }
        if (watk > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incPAD, watk, false);
        }
        if (matk > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incMAD, matk, false);
        }
        if (wdef > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incPDD, wdef, false);
        }
        if (mdef > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incMDD, mdef, false);
        }
        if (avoid > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incEVA, avoid, false);
        }
        if (acc > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incACC, acc, false);
        }
        if (speed > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incSpeed, speed, false);
        }
        if (jump > 0) {
            getUnitStatUpgrade(stats, StatUpgrade.incJump, jump, false);
        }
    }

    /**
     * 获取装备当前所有大于0的属性值映射
     *
     * @return 属性类型到属性值的映射
     */
    public Map<StatUpgrade, Short> getStats() {
        Map<StatUpgrade, Short> stats = new HashMap<>(5);

        if (dex > 0) {
            stats.put(StatUpgrade.incDEX, dex);
        }
        if (str > 0) {
            stats.put(StatUpgrade.incSTR, str);
        }
        if (_int > 0) {
            stats.put(StatUpgrade.incINT, _int);
        }
        if (luk > 0) {
            stats.put(StatUpgrade.incLUK, luk);
        }
        if (hp > 0) {
            stats.put(StatUpgrade.incMHP, hp);
        }
        if (mp > 0) {
            stats.put(StatUpgrade.incMMP, mp);
        }
        if (watk > 0) {
            stats.put(StatUpgrade.incPAD, watk);
        }
        if (matk > 0) {
            stats.put(StatUpgrade.incMAD, matk);
        }
        if (wdef > 0) {
            stats.put(StatUpgrade.incPDD, wdef);
        }
        if (mdef > 0) {
            stats.put(StatUpgrade.incMDD, mdef);
        }
        if (avoid > 0) {
            stats.put(StatUpgrade.incEVA, avoid);
        }
        if (acc > 0) {
            stats.put(StatUpgrade.incACC, acc);
        }
        if (speed > 0) {
            stats.put(StatUpgrade.incSpeed, speed);
        }
        if (jump > 0) {
            stats.put(StatUpgrade.incJump, jump);
        }

        return stats;
    }

    /**
     * 装备升级时计算增加的属性值，值>0才显示，避免显示负数或者0，避免玩家以为属性被扣除了
     * 优化提示消息，使其更易懂
     * @param stats 属性升级列表，包含属性类型和增加值
     * @return 返回一个 Pair，包含提示消息和两个布尔值（是否增加升级槽、是否减少金锤子）
     */
    public Pair<String, Pair<Boolean, Boolean>> gainStats(List<Pair<StatUpgrade, Integer>> stats) {
        // 标记是否增加升级槽或减少金锤子
        boolean gotSlot = false, gotVicious = false;
        StringBuilder lvupStr = new StringBuilder();
        // 获取配置的属性上限
        int maxStat = GameConfig.getServerInt("max_equipment_stat");

        for (Pair<StatUpgrade, Integer> stat : stats) {
            StatUpgrade type = stat.getLeft();
            int value = stat.getRight();

            switch (type) {
                case incVicious:
                    // 减少金锤子使用次数
                    vicious -= value;
                    gotVicious = true;
                    break;
                case incSlot:
                    // 增加升级槽
                    upgradeSlots += value;
                    gotSlot = true;
                    break;
                default:
                    // 普通属性升级
                    int statUp = handleStatUpgrade(type, value, maxStat);
                    if (statUp > 0) {
                        lvupStr.append(getStatMessage(type, statUp)).append("; ");
                    }
                    break;
            }
        }

        return new Pair<>(lvupStr.toString(), new Pair<>(gotSlot, gotVicious));
    }

    /**
     * 处理普通属性的升级逻辑
     * @param type 属性类型
     * @param value 属性增加值
     * @param maxStat 属性最大值
     * @return 实际增加的属性值
     */
    private int handleStatUpgrade(StatUpgrade type, int value, int maxStat) {
        // 获取当前属性值
        int currentStat = getCurrentStat(type);
        // 计算实际增加值，不超过配置上限
        int statUp = Math.min(value, maxStat - currentStat);
        if (statUp > 0) {
            // 更新属性值
            setCurrentStat(type, currentStat + statUp);
        }
        return statUp;
    }

    /**
     * 获取当前属性值
     * @param type 属性类型
     * @return 当前属性值
     */
    private int getCurrentStat(StatUpgrade type) {
        switch (type) {
            case incDEX: return dex;
            case incSTR: return str;
            case incINT: return _int;
            case incLUK: return luk;
            case incMHP: return hp;
            case incMMP: return mp;
            case incPAD: return watk;
            case incMAD: return matk;
            case incPDD: return wdef;
            case incMDD: return mdef;
            case incEVA: return avoid;
            case incACC: return acc;
            case incSpeed: return speed;
            case incJump: return jump;
            default: return 0;
        }
    }

    /**
     * 设置当前属性值
     * @param type 属性类型
     * @param value 新的属性值
     */
    private void setCurrentStat(StatUpgrade type, int value) {
        switch (type) {
            case incDEX: dex = (short) value; break;
            case incSTR: str = (short) value; break;
            case incINT: _int = (short) value; break;
            case incLUK: luk = (short) value; break;
            case incMHP: hp = (short) value; break;
            case incMMP: mp = (short) value; break;
            case incPAD: watk = (short) value; break;
            case incMAD: matk = (short) value; break;
            case incPDD: wdef = (short) value; break;
            case incMDD: mdef = (short) value; break;
            case incEVA: avoid = (short) value; break;
            case incACC: acc = (short) value; break;
            case incSpeed: speed = (short) value; break;
            case incJump: jump = (short) value; break;
            default: break;
        }
    }

    /**
     * 获取属性提升的提示消息
     * @param type 属性类型
     * @param value 属性增加值
     * @return 提示消息
     */
    private String getStatMessage(StatUpgrade type, int value) {
        // 从枚举名提取属性简称（如 incDEX → DEX）
        String messageKey = "Equip.gainStats." + type.name().substring(3);
        return I18nUtil.getMessage(messageKey) + "+" + value;
    }

    /**
     * 处理装备升级的逻辑，包括属性提升、升级槽增加、金锤子减少等，并通知客户端更新装备状态
     * @param c 触发升级的客户端
     */
    private void gainLevel(Client c) {
        List<Pair<StatUpgrade, Integer>> stats = new LinkedList<>();
        // 获取装备需求等级
        int equipLevel = ii.getEquipLevelReq(getItemId());

        if (isElemental) {
            // 从配置获取元素装备的属性升级列表
            List<Pair<String, Integer>> elementalStats = ii.getItemLevelupStats(getItemId(), itemLevel);
            for (Pair<String, Integer> p : elementalStats) {
                if (p.getRight() > 0) {
                    stats.add(new Pair<>(StatUpgrade.valueOf(p.getLeft()), p.getRight()));
                }
            }
        }

        if (stats.isEmpty()) {
            // 没有预设属性时，使用默认属性生升级列表
            isUpgradeable = false;
            improveDefaultStats(stats);
        }
        // 处理升级槽和金锤子
        UpgradeSlotProcessing(stats, equipLevel);
        if (isUpgradeable && stats.isEmpty()) {
            // 装备仍可升级但属性列表为空时循环生成
            while (stats.isEmpty()) {
                improveDefaultStats(stats);
                UpgradeSlotProcessing(stats, equipLevel);
            }
        }

        // 提升装备等级
        itemLevel++;

        // 构建等级提升提示
        String lvupStr = I18nUtil.getMessage("Equip.gainStats.lvupStr", ii.getName(this.getItemId()), itemLevel) + "; ";

        Pair<String, Pair<Boolean, Boolean>> res = this.gainStats(stats);
        lvupStr += res.getLeft();
        boolean gotSlot = res.getRight().getLeft();
        boolean gotVicious = res.getRight().getRight();

        if (gotVicious) {
            lvupStr += I18nUtil.getMessage("Equip.gainStats.Vicious", "-1") + "; ";
        }

        if (gotSlot) {
            lvupStr += I18nUtil.getMessage("Equip.gainStats.UPGSLOT", "+1") + "; ";
        }

        // 通知客户端更新装备状态
        c.getPlayer().equipChanged();
        c.getPlayer().showHint(I18nUtil.getMessage("Equip.gainStats.showHint", ii.getName(this.getItemId()), itemLevel), 300);
        c.getPlayer().dropMessage(6, lvupStr);

        // 发送升级特效
        c.sendPacket(PacketCreator.showEquipmentLevelUp());
        c.getPlayer().getMap().broadcastPacket(c.getPlayer(), PacketCreator.showForeignEffect(c.getPlayer().getId(), 15));
        c.getPlayer().forceUpdateItem(this);
    }

    /**
     * 获取当前累积的装备经验值（取整）
     *
     * @return 当前经验值
     */
    public int getItemExp() {
        return (int) itemExp;
    }

    /**
     * 根据装备需求等级计算标准化的经验值除数
     * <p>不同等级区间使用不同指数/对数曲线拟合，用于将怪物经验转换为装备经验。</p>
     *
     * @param reqLevel 装备需求等级
     * @return 标准化经验除数
     */
    private static double normalizedMasteryExp(int reqLevel) {

        if (reqLevel < 5) {
            return 42;
        } else if (reqLevel >= 78) {
            return Math.max((10413.648 * Math.exp(reqLevel * 0.03275)), 15);
        } else if (reqLevel >= 38) {
            return Math.max((4985.818 * Math.exp(reqLevel * 0.02007)), 15);
        } else if (reqLevel >= 18) {
            return Math.max((248.219 * Math.exp(reqLevel * 0.11093)), 15);
        } else {
            return Math.max(((1334.564 * Math.log(reqLevel)) - 1731.976), 15);
        }
    }

    /**
     * 装备获得怪物经验后的处理逻辑
     * 由怪物击杀时触发，经验按比例转换为装备经验
     *
     * @param c    客户端对象
     * @param gain 获得的怪物经验值
     */
    public synchronized void gainItemExp(Client c, int gain) {
        // 检查装备是否可升级
        if (!ii.isUpgradeable(this.getItemId())) {
            return;
        }

        // 计算装备最大等级
        int equipMaxLevel = Math.min(30, Math.max(ii.getEquipLevel(this.getItemId(), true), GameConfig.getServerInt("use_equipment_level_up")));
        if (itemLevel >= equipMaxLevel) {
            return;
        }

        // 获取装备需求等级
        int reqLevel = ii.getEquipLevelReq(this.getItemId());

        // 计算经验修正因子：倍率 * (升1级经验 / 标准化经验除数)
        float masteryModifier = (GameConfig.getServerFloat("equip_exp_rate") * ExpTable.getExpNeededForLevel(1)) / (float) normalizedMasteryExp(reqLevel);
        float elementModifier = (isElemental) ? 0.85f : 0.6f;

        // 计算实际获得的装备经验
        float baseExpGain = gain * elementModifier * masteryModifier;

        itemExp += baseExpGain;
        int expNeeded = ExpTable.getEquipExpNeededForLevel(itemLevel);

        if (GameConfig.getServerBoolean("use_debug_show_eqp_exp")) {
            log.info("{} -> EXP Gain: {}, Mastery: {}, Base gain: {}, exp: {} / {}, Kills TNL: {}", ii.getName(getItemId()),
                    gain, masteryModifier, baseExpGain, itemExp, expNeeded, expNeeded / (baseExpGain / c.getPlayer().getExpRate()));
        }

        if (itemExp >= expNeeded) {
            while (itemExp >= expNeeded) {
                itemExp -= expNeeded;
                // 装备升级
                gainLevel(c);

                // 达到最大等级或不允许连续升级时退出
                if (itemLevel >= equipMaxLevel || !GameConfig.getServerBoolean("use_equipment_level_up_continuous")) {
                    itemExp = 0.0f;
                    break;
                }

                // 更新下一级所需经验
                expNeeded = ExpTable.getEquipExpNeededForLevel(itemLevel);
            }
        }

        // 通知客户端更新装备状态
        c.getPlayer().forceUpdateItem(this);
    }

    /**
     * 检查装备是否已达到最大等级
     *
     * @return true=已达最大等级
     */
    private boolean reachedMaxLevel() {
        if (isElemental) {
            if (itemLevel < ItemInformationProvider.getInstance().getEquipLevel(getItemId(), true)) {
                return false;
            }
        }

        return itemLevel >= GameConfig.getServerInt("use_equipment_level_up");
    }

    /**
     * 构建装备悬停时显示的等级和经验信息
     *
     * @param c 客户端对象
     * @return 装备的经验进度信息字符串
     */
    public String showEquipFeatures(Client c) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        if (!ii.isUpgradeable(this.getItemId())) {
            return "";
        }

        String eqpName = ii.getName(getItemId());
        String eqpInfo = reachedMaxLevel() ? " #e#rMAX LEVEL#k#n" : (" EXP: #e#b" + (int) itemExp + "#k#n / " + ExpTable.getEquipExpNeededForLevel(itemLevel));

        return "'" + eqpName + "' -> LV: #e#b" + itemLevel + "#k#n    " + eqpInfo + "\r\n";
    }

    /**
     * 设置装备经验值
     *
     * @param exp 新的经验值
     */
    public void setItemExp(int exp) {
        this.itemExp = exp;
    }

    /**
     * 设置装备成长等级
     *
     * @param level 新的成长等级
     */
    public void setItemLevel(byte level) {
        this.itemLevel = level;
    }

    /**
     * 设置装备数量，装备数量仅允许 0 或 1
     *
     * @param quantity 数量值，必须为 0 或 1
     * @throws RuntimeException 若数量不在 [0,1] 范围内
     */
    @Override
    public void setQuantity(short quantity) {
        if (quantity < 0 || quantity > 1) {
            throw new RuntimeException("Setting the quantity to " + quantity + " on an equip (itemid: " + getItemId() + ")");
        }
        super.setQuantity(quantity);
    }

    /**
     * 设置升级次数
     *
     * @param i 升级次数
     */
    public void setUpgradeSlots(int i) {
        this.upgradeSlots = (byte) i;
    }

    /**
     * 设置金锤子使用次数
     *
     * @param i 金锤子使用次数
     */
    public void setVicious(int i) {
        this.vicious = (short) i;
    }

    /**
     * 获取戒指ID
     *
     * @return 戒指ID，非戒指类装备返回 -1
     */
    public int getRingId() {
        return ringid;
    }

    /**
     * 设置戒指ID
     *
     * @param id 戒指ID
     */
    public void setRingId(int id) {
        this.ringid = id;
    }

    /**
     * 获取星之力强化等级
     *
     * @return 强化等级
     */
    public short getEnhanceLevel() {
        return enhanceLevel;
    }

    /**
     * 设置星之力强化等级
     *
     * @param level 强化等级
     */
    public void setEnhanceLevel(short level) {
        this.enhanceLevel = level;
    }

    /**
     * 判断装备是否已被角色穿戴
     *
     * @return true 表示已穿戴
     */
    public boolean isWearing() {
        return wear;
    }

    /**
     * 设置装备穿戴状态
     *
     * @param yes true 表示已穿戴
     */
    public void wear(boolean yes) {
        wear = yes;
    }

}