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

import org.gms.util.RequireUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.DataType;
import org.gms.provider.wz.WZFiles;
import org.gms.util.Pair;
import org.gms.util.StringUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 【工厂/提供者】LifeFactory：创建或提供 `life` 相关运行时对象。
 */
public class LifeFactory {
    private static final Logger log = LoggerFactory.getLogger(LifeFactory.class);
    /** MOB.wz 数据提供器 */
    private static final DataProvider data = DataProviderFactory.getDataProvider(WZFiles.MOB);
    /** String.wz 数据提供器 */
    private final static DataProvider stringDataWZ = DataProviderFactory.getDataProvider(WZFiles.STRING);
    /** 怪物字符串数据 */
    private static final Data mobStringData = stringDataWZ.getData("Mob.img");
    /** NPC字符串数据 */
    private static final Data npcStringData = stringDataWZ.getData("Npc.img");
    /** 怪物属性缓存（怪物ID -> MonsterStats） */
    private static final Map<Integer, MonsterStats> monsterStats = new HashMap<>();
    /** 需要显示血条的BOSS怪物ID集合 */
    private static final Set<Integer> hpbarBosses = getHpBarBosses();
    /** NPC名称缓存（NPC ID -> 名称） */
    private static final Map<Integer, String> npcNames = new HashMap<>();

    /**
     * 获取需要显示血条的BOSS怪物ID集合
     *
     * @return BOSS怪物ID集合
     */
    private static Set<Integer> getHpBarBosses() {
        Set<Integer> ret = new HashSet<>();

        DataProvider uiDataWZ = DataProviderFactory.getDataProvider(WZFiles.UI);
        for (Data bossData : uiDataWZ.getData("UIWindow.img").getChildByPath("MobGage/Mob").getChildren()) {
            ret.add(Integer.valueOf(bossData.getName()));
        }

        return ret;
    }

    /**
     * 根据类型获取怪物或NPC实例
     *
     * @param id   实体ID
     * @param type 类型（"n"表示NPC，"m"表示怪物）
     * @return 实体实例
     */
    public static AbstractLoadedLife getLife(int id, String type) {
        if (type.equalsIgnoreCase("n")) {
            return getNPC(id);
        } else if (type.equalsIgnoreCase("m")) {
            return getMonster(id);
        } else {
            log.warn("Unknown Life type: {}", type);
            return null;
        }
    }

    /**
     * 怪物攻击信息持有者（内部类）
     */
    private static class MobAttackInfoHolder {
        /** 攻击位置 */
        protected int attackPos;
        /** MP消耗 */
        protected int mpCon;
        /** 冷却时间 */
        protected int coolTime;
        /** 动画时间 */
        protected int animationTime;

        protected MobAttackInfoHolder(int attackPos, int mpCon, int coolTime, int animationTime) {
            this.attackPos = attackPos;
            this.mpCon = mpCon;
            this.coolTime = coolTime;
            this.animationTime = animationTime;
        }
    }

    /**
     * 设置怪物攻击信息到全局提供者
     *
     * @param mid        怪物ID
     * @param attackInfos 攻击信息列表
     */
    private static void setMonsterAttackInfo(int mid, List<MobAttackInfoHolder> attackInfos) {
        if (!attackInfos.isEmpty()) {
            MonsterInformationProvider mi = MonsterInformationProvider.getInstance();

            for (MobAttackInfoHolder attackInfo : attackInfos) {
                mi.setMobAttackInfo(mid, attackInfo.attackPos, attackInfo.mpCon, attackInfo.coolTime);
                mi.setMobAttackAnimationTime(mid, attackInfo.attackPos, attackInfo.animationTime);
            }
        }
    }

    /**
     * 从WZ文件解析怪物属性
     *
     * @param mid 怪物ID
     * @return 怪物属性和攻击信息列表
     */
    private static Pair<MonsterStats, List<MobAttackInfoHolder>> getMonsterStats(int mid) {
        Data monsterData = data.getData(StringUtil.getLeftPaddedStr(mid + ".img", '0', 11));
        if (monsterData == null) {
            return null;
        }
        Data monsterInfoData = monsterData.getChildByPath("info");

        List<MobAttackInfoHolder> attackInfos = new LinkedList<>();
        MonsterStats stats = new MonsterStats();

        int linkMid = DataTool.getIntConvert("link", monsterInfoData, 0);
        if (linkMid != 0) {
            Pair<MonsterStats, List<MobAttackInfoHolder>> linkStats = getMonsterStats(linkMid);
            if (linkStats == null) {
                return null;
            }

            // thanks resinate for noticing non-propagable infos such as revives getting retrieved
            attackInfos.addAll(linkStats.getRight());
        }

        stats.setHp(DataTool.getIntConvert("maxHP", monsterInfoData));
        stats.setFriendly(DataTool.getIntConvert("damagedByMob", monsterInfoData, stats.isFriendly() ? 1 : 0) == 1);
        stats.setPADamage(DataTool.getIntConvert("PADamage", monsterInfoData));
        stats.setPDDamage(DataTool.getIntConvert("PDDamage", monsterInfoData));
        stats.setMADamage(DataTool.getIntConvert("MADamage", monsterInfoData));
        stats.setMDDamage(DataTool.getIntConvert("MDDamage", monsterInfoData));
        stats.setMp(DataTool.getIntConvert("maxMP", monsterInfoData, stats.getMp()));
        stats.setExp(DataTool.getIntConvert("exp", monsterInfoData, stats.getExp()));
        stats.setLevel(DataTool.getIntConvert("level", monsterInfoData));
        stats.setRemoveAfter(DataTool.getIntConvert("removeAfter", monsterInfoData, stats.removeAfter()));
        stats.setBoss(DataTool.getIntConvert("boss", monsterInfoData, stats.isBoss() ? 1 : 0) > 0);
        stats.setExplosiveReward(DataTool.getIntConvert("explosiveReward", monsterInfoData, stats.isExplosiveReward() ? 1 : 0) > 0);
        stats.setFfaLoot(DataTool.getIntConvert("publicReward", monsterInfoData, stats.isFfaLoot() ? 1 : 0) > 0);
        stats.setUndead(DataTool.getIntConvert("undead", monsterInfoData, stats.isUndead() ? 1 : 0) > 0);
        stats.setName(DataTool.getString(mid + "/name", mobStringData, "MISSINGNO"));
        stats.setBuffToGive(DataTool.getIntConvert("buff", monsterInfoData, stats.getBuffToGive()));
        stats.setCP(DataTool.getIntConvert("getCP", monsterInfoData, stats.getCP()));
        stats.setRemoveOnMiss(DataTool.getIntConvert("removeOnMiss", monsterInfoData, stats.removeOnMiss() ? 1 : 0) > 0);

        Data special = monsterInfoData.getChildByPath("coolDamage");
        if (special != null) {
            int coolDmg = DataTool.getIntConvert("coolDamage", monsterInfoData);
            int coolProb = DataTool.getIntConvert("coolDamageProb", monsterInfoData, 0);
            stats.setCool(new Pair<>(coolDmg, coolProb));
        }
        special = monsterInfoData.getChildByPath("loseItem");
        if (special != null) {
            for (Data liData : special.getChildren()) {
                stats.addLoseItem(new loseItem(DataTool.getInt(liData.getChildByPath("id")), (byte) DataTool.getInt(liData.getChildByPath("prop")), (byte) DataTool.getInt(liData.getChildByPath("x"))));
            }
        }
        special = monsterInfoData.getChildByPath("selfDestruction");
        if (special != null) {
            stats.setSelfDestruction(new selfDestruction((byte) DataTool.getInt(special.getChildByPath("action")), DataTool.getIntConvert("removeAfter", special, -1), DataTool.getIntConvert("hp", special, -1)));
        }
        Data firstAttackData = monsterInfoData.getChildByPath("firstAttack");
        int firstAttack = 0;
        if (firstAttackData != null) {
            if (firstAttackData.getType() == DataType.FLOAT) {
                firstAttack = Math.round(DataTool.getFloat(firstAttackData));
            } else {
                firstAttack = DataTool.getInt(firstAttackData);
            }
        }
        stats.setFirstAttack(firstAttack > 0);
        stats.setDropPeriod(DataTool.getIntConvert("dropItemPeriod", monsterInfoData, stats.getDropPeriod() / 10000) * 10000);

        // thanks yuxaij, Riizade, Z1peR, Anesthetic for noticing some bosses crashing players due to missing requirements
        boolean hpbarBoss = stats.isBoss() && hpbarBosses.contains(mid);
        stats.setTagColor(hpbarBoss ? DataTool.getIntConvert("hpTagColor", monsterInfoData, 0) : 0);
        stats.setTagBgColor(hpbarBoss ? DataTool.getIntConvert("hpTagBgcolor", monsterInfoData, 0) : 0);

        for (Data idata : monsterData) {
            if (!idata.getName().equals("info")) {
                int delay = 0;
                for (Data pic : idata.getChildren()) {
                    delay += DataTool.getIntConvert("delay", pic, 0);
                }
                stats.setAnimationTime(idata.getName(), delay);
            }
        }
        Data reviveInfo = monsterInfoData.getChildByPath("revive");
        if (reviveInfo != null) {
            List<Integer> revives = new LinkedList<>();
            for (Data data_ : reviveInfo) {
                revives.add(DataTool.getInt(data_));
            }
            stats.setRevives(revives);
        }
        decodeElementalString(stats, DataTool.getString("elemAttr", monsterInfoData, ""));

        MonsterInformationProvider mi = MonsterInformationProvider.getInstance();
        Data monsterSkillInfoData = monsterInfoData.getChildByPath("skill");
        if (monsterSkillInfoData != null) {
            int i = 0;
            Set<MobSkillId> skills = new HashSet<>();
            while (monsterSkillInfoData.getChildByPath(Integer.toString(i)) != null) {
                int skillId = DataTool.getInt(i + "/skill", monsterSkillInfoData, 0);
                int skillLv = DataTool.getInt(i + "/level", monsterSkillInfoData, 0);
                MobSkillType type = MobSkillType.from(skillId).orElseThrow();
                skills.add(new MobSkillId(type, skillLv));

                Data monsterSkillData = monsterData.getChildByPath("skill" + (i + 1));
                if (monsterSkillData != null) {
                    int animationTime = 0;
                    for (Data effectEntry : monsterSkillData.getChildren()) {
                        animationTime += DataTool.getIntConvert("delay", effectEntry, 0);
                    }

                    MobSkill skill = MobSkillFactory.getMobSkillOrThrow(type, skillLv);
                    mi.setMobSkillAnimationTime(skill, animationTime);
                }

                i++;
            }
            stats.setSkills(skills);
        }

        int i = 0;
        Data monsterAttackData;
        while ((monsterAttackData = monsterData.getChildByPath("attack" + (i + 1))) != null) {
            int animationTime = 0;
            for (Data effectEntry : monsterAttackData.getChildren()) {
                animationTime += DataTool.getIntConvert("delay", effectEntry, 0);
            }

            int mpCon = DataTool.getIntConvert("info/conMP", monsterAttackData, 0);
            int coolTime = DataTool.getIntConvert("info/attackAfter", monsterAttackData, 0);
            attackInfos.add(new MobAttackInfoHolder(i, mpCon, coolTime, animationTime));
            i++;
        }

        Data banishData = monsterInfoData.getChildByPath("ban");
        if (banishData != null) {
            stats.setBanishInfo(new BanishInfo(DataTool.getString("banMsg", banishData), DataTool.getInt("banMap/0/field", banishData, -1), DataTool.getString("banMap/0/portal", banishData, "sp")));
        }

        int noFlip = DataTool.getInt("noFlip", monsterInfoData, 0);
        if (noFlip > 0) {
            Point origin = DataTool.getPoint("stand/0/origin", monsterData, null);
            if (origin != null) {
                stats.setFixedStance(origin.getX() < 1 ? 5 : 4);    // fixed left/right
            }
        }
        Data fly = monsterData.getChildByPath("fly/0");
        Data stand = monsterData.getChildByPath("stand/0");
        if (fly != null) {
            stats.setMovetype(1);   //设定怪物类型为：fly
            fly = fly.getType() == DataType.UOL ? fly.getChildByPath((String) fly.getData()) : fly;  //呼叫转移...
            if (fly != null) {
                stats.setImgwidth(DataTool.getAttributeValueInt(fly,"width",-1));
                stats.setImgheight(DataTool.getAttributeValueInt(fly,"height",-1));
            }
        } else if (stand != null) {
            stats.setMovetype(0);   //设定怪物类型为：stand
            stand = stand.getType() == DataType.UOL ? stand.getChildByPath((String) stand.getData()) : stand;  //呼叫转移...
            if (stand != null) {
                stats.setImgwidth(DataTool.getAttributeValueInt(stand,"width",-1));
                stats.setImgheight(DataTool.getAttributeValueInt(stand,"height",-1));
            }

        }
        return new Pair<>(stats, attackInfos);
    }

    /**
     * 获取怪物实例（带缓存）
     *
     * @param mid 怪物ID
     * @return 怪物实例
     */
    public static Monster getMonster(int mid) {
        try {
            MonsterStats stats = monsterStats.get(mid);
            if (stats == null) {
                Pair<MonsterStats, List<MobAttackInfoHolder>> mobStats = getMonsterStats(mid);
                if (mobStats == null) {
                    log.warn("Could not find mob data for id {}", mid);
                    return null;
                }
                stats = mobStats.getLeft();
                setMonsterAttackInfo(mid, mobStats.getRight());

                monsterStats.put(mid, stats);
            }
            return new Monster(mid, stats);
        } catch (NullPointerException npe) {
            log.error("[SEVERE] MOB {} failed to load.", mid, npe);
            return null;
        }
    }

    /**
     * 获取怪物等级
     *
     * @param mid 怪物ID
     * @return 怪物等级，-1表示未找到
     */
    public static int getMonsterLevel(int mid) {
        try {
            MonsterStats stats = monsterStats.get(mid);
            if (stats == null) {
                Data monsterData = data.getData(StringUtil.getLeftPaddedStr(mid + ".img", '0', 11));
                if (monsterData == null) {
                    return -1;
                }
                Data monsterInfoData = monsterData.getChildByPath("info");
                return DataTool.getIntConvert("level", monsterInfoData);
            } else {
                return stats.getLevel();
            }
        } catch (NullPointerException npe) {
            log.error("[SEVERE] MOB {} failed to load.", mid, npe);
        }

        return -1;
    }

    /**
     * 解析元素属性字符串
     *
     * @param stats     怪物属性
     * @param elemAttr  元素属性字符串
     */
    private static void decodeElementalString(MonsterStats stats, String elemAttr) {
        for (int i = 0; i < elemAttr.length(); i += 2) {
            stats.setEffectiveness(Element.getFromChar(elemAttr.charAt(i)), ElementalEffectiveness.getByNumber(Integer.parseInt(String.valueOf(elemAttr.charAt(i + 1)))));
        }
    }

    /**
     * 获取NPC实例（带缓存）
     *
     * @param nid NPC ID
     * @return NPC实例
     */
    public static NPC getNPC(int nid) {
        String name = npcNames.get(nid);
        if (RequireUtil.isEmpty(name)) {
            name = DataTool.getString(nid + "/name", npcStringData, "MISSINGNO");
            npcNames.put(nid, name);
        }
        return new NPC(nid, new NPCStats(name));
    }

    /**
     * 获取NPC名称
     *
     * @param nid NPC ID
     * @return NPC名称
     */
    public static String getNPCName(int nid) {
        return getNPC(nid).getName();
    }

    /**
     * 获取NPC默认对话
     *
     * @param nid NPC ID
     * @return 默认对话文本
     */
    public static String getNPCDefaultTalk(int nid) {
        return DataTool.getString(nid + "/d0", npcStringData, "(...)");
    }

    /**
     * 驱逐信息类（怪物将玩家传送走的信息）
     */
    public static class BanishInfo {
        /** 传送目标地图 */
        private final int map;
        /** 传送目标传送门 */
        private final String portal;
        /** 驱逐消息 */
        private final String msg;

        public BanishInfo(String msg, int map, String portal) {
            this.msg = msg;
            this.map = map;
            this.portal = portal;
        }

        public int getMap() {
            return map;
        }

        public String getPortal() {
            return portal;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 掉落物品类（怪物被攻击时掉落的物品）
     */
    public static class loseItem {
        /** 物品ID */
        private final int id;
        /** 掉落概率 */
        private final byte chance;
        /** X坐标偏移 */
        private final byte x;

        public loseItem(int id, byte chance, byte x) {
            this.id = id;
            this.chance = chance;
            this.x = x;
        }

        public int getId() {
            return id;
        }

        public byte getChance() {
            return chance;
        }

        public byte getX() {
            return x;
        }
    }

    /**
     * 自毁信息类（怪物自爆相关信息）
     */
    public static class selfDestruction {
        /** 自毁动作类型 */
        private final byte action;
        /** 自毁后移除延迟 */
        private final int removeAfter;
        /** 触发自毁的HP阈值 */
        private final int hp;

        public selfDestruction(byte action, int removeAfter, int hp) {
            this.action = action;
            this.removeAfter = removeAfter;
            this.hp = hp;
        }

        public int getHp() {
            return hp;
        }

        public byte getAction() {
            return action;
        }

        public int removeAfter() {
            return removeAfter;
        }
    }
}