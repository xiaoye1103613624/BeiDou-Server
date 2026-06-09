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

import org.gms.server.life.LifeFactory.BanishInfo;
import org.gms.server.life.LifeFactory.loseItem;
import org.gms.server.life.LifeFactory.selfDestruction;
import org.gms.util.Pair;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 怪物属性数据类
 * 存储怪物的完整属性信息，包括基础属性、战斗属性、元素抗性、技能、动画时间、碰撞框等
 * 使用反射进行深拷贝（copy方法），支持可变属性覆盖
 *
 * @author Frz
 * @author OdinMS Team
 */
public class MonsterStats {
    /** 是否可变属性（用于支持不同等级的同种怪物属性变化） */
    public boolean changeable;
    /** 经验值 */
    public int exp;
    /** 生命值 */
    public int hp;
    /** 魔法值 */
    public int mp;
    /** 等级 */
    public int level;
    /** 物理攻击力 */
    public int PADamage;
    /** 物理防御力 */
    public int PDDamage;
    /** 魔法攻击力 */
    public int MADamage;
    /** 魔法防御力 */
    public int MDDamage;
    /** 掉落物品周期（毫秒） */
    public int dropPeriod;
    /** 战斗点数 */
    public int cp;
    /** 死亡时给予周围玩家的Buff ID，-1表示不给予 */
    public int buffToGive = -1;
    /** 消失时间（毫秒），超过此时间后尸体消失 */
    public int removeAfter;
    /** 命中率 */
    public int acc;
    /** 回避率 */
    public int eva;
    /** 是否BOSS */
    public boolean boss;
    /** 是否不死系 */
    public boolean undead;
    /** 是否自由拾取（所有人可拾取掉落） */
    public boolean ffaLoot;
    /** 是否爆炸奖励（BOSS宝箱等） */
    public boolean isExplosiveReward;
    /** 是否先手攻击 */
    public boolean firstAttack;
    /** 是否MISS后消失 */
    public boolean removeOnMiss;
    /** 怪物名称 */
    public String name;
    /** 动画时间映射（动作名 -> 动画总时长毫秒） */
    public Map<String, Integer> animationTimes = new HashMap<>();
    /** 元素抗性映射（元素类型 -> 抗性效果） */
    public Map<Element, ElementalEffectiveness> resistance = new HashMap<>();
    /** 复活后的怪物ID列表（用于BOSS阶段） */
    public List<Integer> revives = Collections.emptyList();
    /** 血条颜色 */
    public byte tagColor;
    /** 血条背景色 */
    public byte tagBgColor;
    /** 技能集合 */
    public Set<MobSkillId> skills = new HashSet<>();
    /** 冷却伤害（伤害值, 概率） */
    public Pair<Integer, Integer> cool = null;
    /** 放逐信息 */
    public BanishInfo banish = null;
    /** 丢失物品列表（怪物被攻击后可能丢失的物品） */
    public List<loseItem> loseItem = null;
    /** 自毁信息 */
    public selfDestruction selfDestruction = null;
    /** 固定朝向（0=不固定, 4=固定左, 5=固定右） */
    public int fixedStance = 0;
    /** 是否友好怪物 */
    public boolean friendly;
    /** 怪物类型，-1=未知，0=stand（陆地），1=fly（飞天） */
    public int movetype = -1;
    /** 第一帧图片宽度 */
    public int imgwidth = 0;
    /** 第一帧图片高度 */
    public int imgheight = 0;
    /** 碰撞框最小X */
    public int bboxMinX = 0;
    /** 碰撞框最小Y */
    public int bboxMinY = 0;
    /** 碰撞框最大X */
    public int bboxMaxX = 0;
    /** 碰撞框最大Y */
    public int bboxMaxY = 0;
    /** 碰撞框是否有效 */
    public boolean bboxValid = false;

    /**
     * 设置属性可变标志
     *
     * @param change 是否可变
     */
    public void setChange(boolean change) {
        this.changeable = change;
    }

    /**
     * 属性是否可变
     *
     * @return true表示属性可变
     */
    public boolean isChangeable() {
        return changeable;
    }

    /**
     * 获取经验值
     *
     * @return 经验值
     */
    public int getExp() {
        return exp;
    }

    /**
     * 设置经验值
     *
     * @param exp 经验值
     */
    public void setExp(int exp) {
        this.exp = exp;
    }

    /**
     * 获取生命值
     *
     * @return 生命值
     */
    public int getHp() {
        return hp;
    }

    /**
     * 设置生命值
     *
     * @param hp 生命值
     */
    public void setHp(int hp) {
        this.hp = hp;
    }

    /**
     * 获取魔法值
     *
     * @return 魔法值
     */
    public int getMp() {
        return mp;
    }

    /**
     * 设置魔法值
     *
     * @param mp 魔法值
     */
    public void setMp(int mp) {
        this.mp = mp;
    }

    /**
     * 获取怪物等级
     *
     * @return 等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置怪物等级
     *
     * @param level 等级
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 获取尸体消失时间
     *
     * @return 消失时间（毫秒）
     */
    public int removeAfter() {
        return removeAfter;
    }

    /**
     * 设置尸体消失时间
     *
     * @param removeAfter 消失时间（毫秒）
     */
    public void setRemoveAfter(int removeAfter) {
        this.removeAfter = removeAfter;
    }

    /**
     * 获取掉落物品周期
     *
     * @return 掉落周期（毫秒）
     */
    public int getDropPeriod() {
        return dropPeriod;
    }

    /**
     * 设置掉落物品周期
     *
     * @param dropPeriod 掉落周期（毫秒）
     */
    public void setDropPeriod(int dropPeriod) {
        this.dropPeriod = dropPeriod;
    }

    /**
     * 设置是否BOSS
     *
     * @param boss 是否BOSS
     */
    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    /**
     * 是否为BOSS
     *
     * @return true表示BOSS
     */
    public boolean isBoss() {
        return boss;
    }

    /**
     * 设置是否自由拾取
     *
     * @param ffaLoot 是否自由拾取
     */
    public void setFfaLoot(boolean ffaLoot) {
        this.ffaLoot = ffaLoot;
    }

    /**
     * 是否自由拾取
     *
     * @return true表示自由拾取
     */
    public boolean isFfaLoot() {
        return ffaLoot;
    }

    /**
     * 设置动画时间
     * 遍历动作的所有帧，累加每帧的delay值
     *
     * @param name 动作名称
     * @param delay 动画总时长（毫秒）
     */
    public void setAnimationTime(String name, int delay) {
        animationTimes.put(name, delay);
    }

    /**
     * 获取指定动作的动画时间
     *
     * @param name 动作名称
     * @return 动画时间（毫秒），不存在返回500
     */
    public int getAnimationTime(String name) {
        Integer ret = animationTimes.get(name);
        if (ret == null) {
            return 500;
        }
        return ret;
    }

    /**
     * 是否可移动
     * 有move或fly动画即视为可移动
     *
     * @return true表示可移动
     */
    public boolean isMobile() {
        return animationTimes.containsKey("move") || animationTimes.containsKey("fly");
    }

    /**
     * 获取复活怪物ID列表
     *
     * @return 复活后的怪物ID列表
     */
    public List<Integer> getRevives() {
        return revives;
    }

    /**
     * 设置复活怪物ID列表
     *
     * @param revives 复活后的怪物ID列表
     */
    public void setRevives(List<Integer> revives) {
        this.revives = revives;
    }

    /**
     * 设置是否不死系
     *
     * @param undead 是否不死系
     */
    public void setUndead(boolean undead) {
        this.undead = undead;
    }

    /**
     * 是否不死系
     *
     * @return true表示不死系
     */
    public boolean isUndead() {
        return undead;
    }

    /**
     * 设置元素抗性
     *
     * @param e 元素类型
     * @param ee 抗性效果
     */
    public void setEffectiveness(Element e, ElementalEffectiveness ee) {
        resistance.put(e, ee);
    }

    /**
     * 获取元素抗性
     * 未配置的元素默认返回NORMAL
     *
     * @param e 元素类型
     * @return 抗性效果
     */
    public ElementalEffectiveness getEffectiveness(Element e) {
        ElementalEffectiveness elementalEffectiveness = resistance.get(e);
        if (elementalEffectiveness == null) {
            return ElementalEffectiveness.NORMAL;
        } else {
            return elementalEffectiveness;
        }
    }

    /**
     * 获取怪物名称
     *
     * @return 怪物名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置怪物名称
     *
     * @param name 怪物名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取血条颜色
     *
     * @return 血条颜色
     */
    public byte getTagColor() {
        return tagColor;
    }

    /**
     * 设置血条颜色
     *
     * @param tagColor 血条颜色
     */
    public void setTagColor(int tagColor) {
        this.tagColor = (byte) tagColor;
    }

    /**
     * 获取血条背景色
     *
     * @return 血条背景色
     */
    public byte getTagBgColor() {
        return tagBgColor;
    }

    /**
     * 设置血条背景色
     *
     * @param tagBgColor 血条背景色
     */
    public void setTagBgColor(int tagBgColor) {
        this.tagBgColor = (byte) tagBgColor;
    }

    /**
     * 设置技能集合
     *
     * @param skills 技能ID集合
     */
    public void setSkills(Set<MobSkillId> skills) {
        this.skills = skills;
    }

    /**
     * 获取技能集合（不可修改）
     *
     * @return 技能集合
     */
    public Set<MobSkillId> getSkills() {
        return Collections.unmodifiableSet(this.skills);
    }

    /**
     * 获取技能数量
     *
     * @return 技能数量
     */
    public int getNoSkills() {
        return this.skills.size();
    }

    /**
     * 判断是否拥有指定技能
     *
     * @param skillId 技能ID
     * @param level 技能等级
     * @return true表示拥有该技能
     */
    public boolean hasSkill(int skillId, int level) {
        for (MobSkillId skill : skills) {
            if (skill.type().getId() == skillId && skill.level() == level) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置是否先手攻击
     *
     * @param firstAttack 是否先手攻击
     */
    public void setFirstAttack(boolean firstAttack) {
        this.firstAttack = firstAttack;
    }

    /**
     * 是否先手攻击
     *
     * @return true表示先手攻击
     */
    public boolean isFirstAttack() {
        return firstAttack;
    }

    /**
     * 设置死亡时给予的Buff ID
     *
     * @param buff Buff ID
     */
    public void setBuffToGive(int buff) {
        this.buffToGive = buff;
    }

    /**
     * 获取死亡时给予的Buff ID
     *
     * @return Buff ID，-1表示不给予
     */
    public int getBuffToGive() {
        return buffToGive;
    }

    /**
     * 移除元素抗性
     *
     * @param e 元素类型
     */
    void removeEffectiveness(Element e) {
        resistance.remove(e);
    }

    /**
     * 获取放逐信息
     *
     * @return 放逐信息，null表示无
     */
    public BanishInfo getBanishInfo() {
        return banish;
    }

    /**
     * 设置放逐信息
     *
     * @param banish 放逐信息
     */
    public void setBanishInfo(BanishInfo banish) {
        this.banish = banish;
    }

    /**
     * 获取物理攻击力
     *
     * @return 物理攻击力
     */
    public int getPADamage() {
        return PADamage;
    }

    /**
     * 设置物理攻击力
     *
     * @param PADamage 物理攻击力
     */
    public void setPADamage(int PADamage) {
        this.PADamage = PADamage;
    }

    /**
     * 获取战斗点数
     *
     * @return 战斗点数
     */
    public int getCP() {
        return cp;
    }

    /**
     * 设置战斗点数
     *
     * @param cp 战斗点数
     */
    public void setCP(int cp) {
        this.cp = cp;
    }

    /**
     * 获取丢失物品列表
     *
     * @return 丢失物品列表，null表示无
     */
    public List<loseItem> loseItem() {
        return loseItem;
    }

    /**
     * 添加丢失物品
     *
     * @param li 丢失物品信息
     */
    public void addLoseItem(loseItem li) {
        if (loseItem == null) {
            loseItem = new LinkedList<>();
        }
        loseItem.add(li);
    }

    /**
     * 获取自毁信息
     *
     * @return 自毁信息，null表示无
     */
    public selfDestruction selfDestruction() {
        return selfDestruction;
    }

    /**
     * 设置自毁信息
     *
     * @param sd 自毁信息
     */
    public void setSelfDestruction(selfDestruction sd) {
        this.selfDestruction = sd;
    }

    /**
     * 设置是否爆炸奖励
     *
     * @param isExplosiveReward 是否爆炸奖励
     */
    public void setExplosiveReward(boolean isExplosiveReward) {
        this.isExplosiveReward = isExplosiveReward;
    }

    /**
     * 是否爆炸奖励
     *
     * @return true表示爆炸奖励
     */
    public boolean isExplosiveReward() {
        return isExplosiveReward;
    }

    /**
     * 设置是否MISS后消失
     *
     * @param removeOnMiss 是否MISS后消失
     */
    public void setRemoveOnMiss(boolean removeOnMiss) {
        this.removeOnMiss = removeOnMiss;
    }

    /**
     * 是否MISS后消失
     *
     * @return true表示MISS后消失
     */
    public boolean removeOnMiss() {
        return removeOnMiss;
    }

    /**
     * 设置冷却伤害
     *
     * @param cool 冷却伤害（伤害值, 概率）
     */
    public void setCool(Pair<Integer, Integer> cool) {
        this.cool = cool;
    }

    /**
     * 获取冷却伤害
     *
     * @return 冷却伤害（伤害值, 概率）
     */
    public Pair<Integer, Integer> getCool() {
        return cool;
    }

    /**
     * 获取物理防御力
     *
     * @return 物理防御力
     */
    public int getPDDamage() {
        return PDDamage;
    }

    /**
     * 获取魔法攻击力
     *
     * @return 魔法攻击力
     */
    public int getMADamage() {
        return MADamage;
    }

    /**
     * 获取魔法防御力
     *
     * @return 魔法防御力
     */
    public int getMDDamage() {
        return MDDamage;
    }

    /**
     * 是否友好怪物
     *
     * @return true表示友好怪物
     */
    public boolean isFriendly() {
        return friendly;
    }

    /**
     * 设置是否友好怪物
     *
     * @param value 是否友好
     */
    public void setFriendly(boolean value) {
        this.friendly = value;
    }

    /**
     * 设置物理防御力
     *
     * @param PDDamage 物理防御力
     */
    public void setPDDamage(int PDDamage) {
        this.PDDamage = PDDamage;
    }

    /**
     * 设置魔法攻击力
     *
     * @param MADamage 魔法攻击力
     */
    public void setMADamage(int MADamage) {
        this.MADamage = MADamage;
    }

    /**
     * 设置魔法防御力
     *
     * @param MDDamage 魔法防御力
     */
    public void setMDDamage(int MDDamage) {
        this.MDDamage = MDDamage;
    }

    /**
     * 获取固定朝向
     *
     * @return 固定朝向（0=不固定, 4=固定左, 5=固定右）
     */
    public int getFixedStance() {
        return this.fixedStance;
    }

    /**
     * 设置固定朝向
     *
     * @param stance 固定朝向
     */
    public void setFixedStance(int stance) {
        this.fixedStance = stance;
    }

    /**
     * 怪物类型，-1=未知，0=stand（陆地怪物），1=fly（飞天怪物）
     * @return
     */
    public int getMovetype() {
        return movetype;
    }
    /**
     * 怪物类型，-1=未知，0=stand（陆地怪物），1=fly（飞天怪物）
     * @return
     */
    public void setMovetype(int movetype) {
        this.movetype = movetype;
    }

    /**
     * 设置第一帧图片的宽度
     * @param imgwidth
     */
    public void setImgwidth(int imgwidth) {
        this.imgwidth = imgwidth;
    }

    /**
     * 设置第一帧图片的高度
     * @param imgheight
     */
    public void setImgheight(int imgheight) {
        this.imgheight = imgheight;
    }

    /**
     * 取第一帧图片的宽度
     * @return
     */
    public int getImgwidth() {
        return this.imgwidth;
    }

    /**
     * 取第一帧图片的高度
     * @return
     */
    public int getImgheight() {
        return this.imgheight;
    }

    /**
     * 设置怪物碰撞框（相对 origin 的 lt/rb）
     */
    public void setBbox(int minX, int minY, int maxX, int maxY) {
        this.bboxMinX = minX;
        this.bboxMinY = minY;
        this.bboxMaxX = maxX;
        this.bboxMaxY = maxY;
        this.bboxValid = true;
    }

    /**
     * 是否已计算碰撞框
     */
    public boolean hasBbox() {
        return bboxValid;
    }

    /**
     * 碰撞框相对 lt.x
     */
    public int getBboxMinX() {
        return bboxMinX;
    }

    /**
     * 碰撞框相对 lt.y
     */
    public int getBboxMinY() {
        return bboxMinY;
    }

    /**
     * 碰撞框相对 rb.x
     */
    public int getBboxMaxX() {
        return bboxMaxX;
    }

    /**
     * 碰撞框相对 rb.y
     */
    public int getBboxMaxY() {
        return bboxMaxY;
    }

    /**
     * 碰撞框宽度（相对值）
     */
    public int getBboxWidth() {
        if (bboxValid) {
            return Math.max(0, bboxMaxX - bboxMinX);
        }
        return imgwidth;
    }

    /**
     * 碰撞框高度（相对值）
     */
    public int getBboxHeight() {
        if (bboxValid) {
            return Math.max(0, bboxMaxY - bboxMinY);
        }
        return imgheight;
    }

    /**
     * 大体型判定：用于决定是否启用碰撞框距离检测
     */
    public boolean isLargeSize() {
        int width = getBboxWidth();
        int height = getBboxHeight();
        if (width <= 0 || height <= 0) {
            return false;
        }
        // 宽高或面积满足阈值即可视为大体型
        return width >= 160 || height >= 160 || width * height >= 25000;
    }

    public MonsterStats copy() {
        MonsterStats copy = new MonsterStats();
        try {
            FieldCopyUtil.setFields(this, copy);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                Thread.sleep(10000);
            } catch (Exception ex) {

            }

        }

        return copy;
    }

    /**
     * 反射字段拷贝工具类
     * 使用Java反射机制将源对象的所有字段值复制到目标对象的同名字段
     * 注意：此方式不处理深拷贝，仅做浅拷贝引用传递
     */
    private static class FieldCopyUtil {
        private static void setFields(Object from, Object to) {
            Field[] fields = from.getClass().getDeclaredFields();
            for (Field field : fields) {
                try {
                    Field fieldFrom = from.getClass().getDeclaredField(field.getName());
                    Object value = fieldFrom.get(from);
                    to.getClass().getDeclaredField(field.getName()).set(to, value);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}