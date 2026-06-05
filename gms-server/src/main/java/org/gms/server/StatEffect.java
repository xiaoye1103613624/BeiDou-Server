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

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.client.Disease;
import org.gms.client.Job;
import org.gms.client.Mount;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.client.status.MonsterStatus;
import org.gms.client.status.MonsterStatusEffect;
import org.gms.config.GameConfig;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.constants.inventory.ItemConstants;
import org.gms.constants.skills.Aran;
import org.gms.constants.skills.Assassin;
import org.gms.constants.skills.Bandit;
import org.gms.constants.skills.Beginner;
import org.gms.constants.skills.Bishop;
import org.gms.constants.skills.BlazeWizard;
import org.gms.constants.skills.Bowmaster;
import org.gms.constants.skills.Brawler;
import org.gms.constants.skills.Buccaneer;
import org.gms.constants.skills.ChiefBandit;
import org.gms.constants.skills.Cleric;
import org.gms.constants.skills.Corsair;
import org.gms.constants.skills.Crossbowman;
import org.gms.constants.skills.Crusader;
import org.gms.constants.skills.DarkKnight;
import org.gms.constants.skills.DawnWarrior;
import org.gms.constants.skills.DragonKnight;
import org.gms.constants.skills.Evan;
import org.gms.constants.skills.FPArchMage;
import org.gms.constants.skills.FPMage;
import org.gms.constants.skills.FPWizard;
import org.gms.constants.skills.Fighter;
import org.gms.constants.skills.GM;
import org.gms.constants.skills.Gunslinger;
import org.gms.constants.skills.Hermit;
import org.gms.constants.skills.Hero;
import org.gms.constants.skills.Hunter;
import org.gms.constants.skills.ILArchMage;
import org.gms.constants.skills.ILMage;
import org.gms.constants.skills.ILWizard;
import org.gms.constants.skills.Legend;
import org.gms.constants.skills.Magician;
import org.gms.constants.skills.Marauder;
import org.gms.constants.skills.Marksman;
import org.gms.constants.skills.NightLord;
import org.gms.constants.skills.NightWalker;
import org.gms.constants.skills.Noblesse;
import org.gms.constants.skills.Outlaw;
import org.gms.constants.skills.Page;
import org.gms.constants.skills.Paladin;
import org.gms.constants.skills.Pirate;
import org.gms.constants.skills.Priest;
import org.gms.constants.skills.Ranger;
import org.gms.constants.skills.Rogue;
import org.gms.constants.skills.Shadower;
import org.gms.constants.skills.Sniper;
import org.gms.constants.skills.Spearman;
import org.gms.constants.skills.SuperGM;
import org.gms.constants.skills.ThunderBreaker;
import org.gms.constants.skills.WhiteKnight;
import org.gms.constants.skills.WindArcher;
import org.gms.net.packet.Packet;
import org.gms.net.server.Server;
import org.gms.net.server.world.Party;
import org.gms.net.server.world.PartyCharacter;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.life.MobSkill;
import org.gms.server.life.MobSkillFactory;
import org.gms.server.life.MobSkillType;
import org.gms.server.life.Monster;
import org.gms.server.maps.Door;
import org.gms.server.maps.FieldLimit;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Mist;
import org.gms.server.maps.Portal;
import org.gms.server.maps.Summon;
import org.gms.server.maps.SummonMovementType;
import org.gms.server.partyquest.CarnivalFactory;
import org.gms.server.partyquest.CarnivalFactory.MCSkill;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 【类型】StatEffect（class），包 `org.gms.server`。
 *
 * 属性效果类，描述物品/技能使用后对角色或怪物产生的数值变化。
 * 包含 BUFF/DEBUFF 的所有属性（攻击、防御、速度、HP/MP 恢复、异常状态等），
 * 以及技能/物品附带的特殊效果（传送、骑宠、召唤、驱散等）。
 *
 * 由 {@link ItemInformationProvider#getItemEffect} 和 {@link SkillFactory} 从 WZ 解析创建，
 * 通过 {@link #applyTo(Character)} 或 {@link #applyTo(Character, Point)} 作用到目标。
 *
 * @author Matze
 * @author Frz
 * @author Ronan
 */
public class StatEffect {
    /** 物理攻击力提升值 */
    private short watk, matk, wdef, mdef, acc, avoid, speed, jump;
    /** 魔法攻击力提升值 */
    private short hp, mp;
    /** HP恢复比例 */
    private double hpR, mpR;
    /** MP恢复比例 */
    private short mhpRRate, mmpRRate, mobSkill, mobSkillLevel;
    /** HP恢复值 */
    private byte mhpR, mmpR;
    /** MP消耗值 */
    private short mpCon, hpCon;
    /** 持续时间（毫秒） */
    private int duration, target, barrier, mob;
    /** 是否为持续效果 */
    private boolean overTime, repeatEffect;
    /** 技能/物品ID */
    private int sourceid,expbuff;
    /** 传送目标地图ID（-1表示不传送） */
    private int moveTo;
    /** CP点数（怪物嘉年华） */
    private int cp, nuffSkill;
    /** 可治愈的负面状态列表 */
    private List<Disease> cureDebuffs;
    /** 是否为技能而非物品 */
    private boolean skill;
    /** 增益状态列表 */
    private List<Pair<BuffStat, Integer>> statups;
    /** 怪物状态效果映射 */
    private Map<MonsterStatus, Integer> monsterStatus;
    /** 效果参数X值 */
    private int x, y, mobCount, moneyCon, cooldown, morphId = 0, ghost, fatigue, berserk, booster;
    /** 触发概率（0.0-1.0） */
    private double prop;
    /** 消耗物品ID */
    private int itemCon, itemConNo;
    /** 伤害倍率 */
    private int damage, attackCount, fixdamage;
    /** 左上角坐标（用于矩形范围） */
    private Point lt, rb;
    /** 子弹数量限制 */
    private short bulletCount, bulletConsume;
    /** 地图保护类型 */
    private byte mapProtection;
    /** 卡片效果统计信息 */
    private CardItemupStats cardStats;

    private static class CardItemupStats {
        protected int itemCode, prob;
        protected boolean party;
        private final List<Pair<Integer, Integer>> areas;

        private CardItemupStats(int code, int prob, List<Pair<Integer, Integer>> areas, boolean inParty) {
            this.itemCode = code;
            this.prob = prob;
            this.areas = areas;
            this.party = inParty;
        }

        private boolean isInArea(int mapid) {
            if (this.areas == null) {
                return true;
            }

            for (Pair<Integer, Integer> a : this.areas) {
                if (mapid >= a.left && mapid <= a.right) {
                    return true;
                }
            }

            return false;
        }
    }

    private boolean isEffectActive(int mapid, boolean partyHunting) {
        if (cardStats == null) {
            return true;
        }

        if (!cardStats.isInArea(mapid)) {
            return false;
        }

        return !cardStats.party || partyHunting;
    }

    public boolean isActive(Character applyto) {
        return isEffectActive(applyto.getMapId(), applyto.getPartyMembersOnSameMap().size() > 1);
    }

    public int getCardRate(int mapid, int itemid) {
        if (cardStats != null) {
            if (cardStats.itemCode == Integer.MAX_VALUE) {
                return cardStats.prob;
            } else if (cardStats.itemCode < 1000) {
                if (itemid / 10000 == cardStats.itemCode) {
                    return cardStats.prob;
                }
            } else {
                if (itemid == cardStats.itemCode) {
                    return cardStats.prob;
                }
            }
        }

        return 0;
    }

    public static StatEffect loadSkillEffectFromData(Data source, int skillid, boolean overtime) {
        return loadFromData(source, skillid, true, overtime);
    }

    public static StatEffect loadItemEffectFromData(Data source, int itemid) {
        return loadFromData(source, itemid, false, false);
    }

    private static void addBuffStatPairToListIfNotZero(List<Pair<BuffStat, Integer>> list, BuffStat buffstat, Integer val) {
        if (val != 0) {
            list.add(new Pair<>(buffstat, val));
        }
    }

    private static byte mapProtection(int sourceid) {
        if (sourceid == ItemId.RED_BEAN_PORRIDGE || sourceid == ItemId.SOFT_WHITE_BUN) {
            return 1;   //elnath cold
        } else if (sourceid == ItemId.AIR_BUBBLE) {
            return 2;   //aqua road underwater
        } else {
            return 0;
        }
    }

    /**
     * 从数据源加载技能或物品效果
     * @param source 数据源
     * @param sourceid 效果源ID
     * @param skill 是否为技能效果
     * @param overTime 是否为持续效果
     * @return 加载的效果对象
     */
    private static StatEffect loadFromData(Data source, int sourceid, boolean skill, boolean overTime) {
        StatEffect ret = new StatEffect();
        ret.duration = DataTool.getIntConvert("time", source, -1);
        ret.hp = (short) DataTool.getInt("hp", source, 0);
        ret.hpR = DataTool.getInt("hpR", source, 0) / 100.0;
        ret.mp = (short) DataTool.getInt("mp", source, 0);
        ret.mpR = DataTool.getInt("mpR", source, 0) / 100.0;
        ret.mpCon = (short) DataTool.getInt("mpCon", source, 0);
        ret.hpCon = (short) DataTool.getInt("hpCon", source, 0);
        int iprop = DataTool.getInt("prop", source, 100);
        ret.prop = iprop / 100.0;

        ret.cp = DataTool.getInt("cp", source, 0);
        List<Disease> cure = new ArrayList<>(5);
        if (DataTool.getInt("poison", source, 0) > 0) {
            cure.add(Disease.POISON);
        }
        if (DataTool.getInt("seal", source, 0) > 0) {
            cure.add(Disease.SEAL);
        }
        if (DataTool.getInt("darkness", source, 0) > 0) {
            cure.add(Disease.DARKNESS);
        }
        if (DataTool.getInt("weakness", source, 0) > 0) {
            cure.add(Disease.WEAKEN);
            cure.add(Disease.SLOW);
        }
        if (DataTool.getInt("curse", source, 0) > 0) {
            cure.add(Disease.CURSE);
        }
        ret.cureDebuffs = cure;
        ret.nuffSkill = DataTool.getInt("nuffSkill", source, 0);

        ret.mobCount = DataTool.getInt("mobCount", source, 1);
        ret.cooldown = DataTool.getInt("cooltime", source, 0);
        ret.morphId = DataTool.getInt("morph", source, 0);
        ret.ghost = DataTool.getInt("ghost", source, 0);
        ret.fatigue = DataTool.getInt("incFatigue", source, 0);
        ret.repeatEffect = DataTool.getInt("repeatEffect", source, 0) > 0;

        Data mdd = source.getChildByPath("0");
        if (mdd != null && mdd.getChildren().size() > 0) {
            ret.mobSkill = (short) DataTool.getInt("mobSkill", mdd, 0);
            ret.mobSkillLevel = (short) DataTool.getInt("level", mdd, 0);
            ret.target = DataTool.getInt("target", mdd, 0);
        } else {
            ret.mobSkill = 0;
            ret.mobSkillLevel = 0;
            ret.target = 0;
        }

        Data mdds = source.getChildByPath("mob");
        if (mdds != null) {
            if (mdds.getChildren() != null && mdds.getChildren().size() > 0) {
                ret.mob = DataTool.getInt("mob", mdds, 0);
            }
        }
        ret.sourceid = sourceid;
        ret.skill = skill;
        if (!ret.skill && ret.duration > -1) {
            ret.overTime = true;
        } else {
            ret.duration *= 1000; // items have their times stored in ms, of course
            ret.overTime = overTime;
        }

        ArrayList<Pair<BuffStat, Integer>> statups = new ArrayList<>();
        ret.watk = (short) DataTool.getInt("pad", source, 0);
        ret.wdef = (short) DataTool.getInt("pdd", source, 0);
        ret.matk = (short) DataTool.getInt("mad", source, 0);
        ret.mdef = (short) DataTool.getInt("mdd", source, 0);
        ret.acc = (short) DataTool.getIntConvert("acc", source, 0);
        ret.avoid = (short) DataTool.getInt("eva", source, 0);
        ret.expbuff = DataTool.getInt("expBuff", source, 0);
        ret.speed = (short) DataTool.getInt("speed", source, 0);
        ret.jump = (short) DataTool.getInt("jump", source, 0);

        ret.barrier = DataTool.getInt("barrier", source, 0);
        addBuffStatPairToListIfNotZero(statups, BuffStat.AURA, ret.barrier);

        ret.mapProtection = mapProtection(sourceid);
        addBuffStatPairToListIfNotZero(statups, BuffStat.MAP_PROTECTION, (int) ret.mapProtection);

        if (ret.overTime && ret.getSummonMovementType() == null) {
            if (!skill) {
                if (ItemId.isPyramidBuff(sourceid)) {
                    ret.berserk = DataTool.getInt("berserk", source, 0);
                    ret.booster = DataTool.getInt("booster", source, 0);

                    addBuffStatPairToListIfNotZero(statups, BuffStat.BERSERK, ret.berserk);
                    addBuffStatPairToListIfNotZero(statups, BuffStat.BOOSTER, ret.booster);

                } else if (ItemId.isDojoBuff(sourceid) || isHpMpRecovery(sourceid)) {
                    ret.mhpR = (byte) DataTool.getInt("mhpR", source, 0);
                    ret.mhpRRate = (short) (DataTool.getInt("mhpRRate", source, 0) * 100);
                    ret.mmpR = (byte) DataTool.getInt("mmpR", source, 0);
                    ret.mmpRRate = (short) (DataTool.getInt("mmpRRate", source, 0) * 100);

                    addBuffStatPairToListIfNotZero(statups, BuffStat.HPREC, (int) ret.mhpR);
                    addBuffStatPairToListIfNotZero(statups, BuffStat.MPREC, (int) ret.mmpR);

                } else if (ItemId.isRateCoupon(sourceid)) {
                    // 根据经验倍率等级设置对应的经验加成状态
                    switch (DataTool.getInt("expR", source, 0)) {
                        case 1:
                            // 经验倍率等级1：添加1级经验加成券状态
                            addBuffStatPairToListIfNotZero(statups, BuffStat.COUPON_EXP1, 1);
                            break;

                        case 2:
                            // 经验倍率等级2：添加2级经验加成券状态
                            addBuffStatPairToListIfNotZero(statups, BuffStat.COUPON_EXP2, 1);
                            break;

                        case 3:
                            // 经验倍率等级3：添加3级经验加成券状态
                            addBuffStatPairToListIfNotZero(statups, BuffStat.COUPON_EXP3, 1);
                            break;

                        case 4:
                            // 经验倍率等级4：添加4级经验加成券状态
                            addBuffStatPairToListIfNotZero(statups, BuffStat.COUPON_EXP4, 1);
                            break;
                    }

                    // 根据掉宝倍率等级设置对应的掉宝加成状态
                    switch (DataTool.getInt("drpR", source, 0)) {
                        case 1:
                            // 掉宝倍率等级1：添加1级掉宝加成券状态
                            addBuffStatPairToListIfNotZero(statups, BuffStat.COUPON_DRP1, 1);
                            break;

                        case 2:
                            // 掉宝倍率等级2：添加2级掉宝加成券状态
                            addBuffStatPairToListIfNotZero(statups, BuffStat.COUPON_DRP2, 1);
                            break;

                        case 3:
                            // 掉宝倍率等级3：添加3级掉宝加成券状态
                            addBuffStatPairToListIfNotZero(statups, BuffStat.COUPON_DRP3, 1);
                            break;
                    }
                } else if (ItemId.isMonsterCard(sourceid)) {
                    int prob = 0, itemupCode = Integer.MAX_VALUE;
                    List<Pair<Integer, Integer>> areas = null;
                    boolean inParty = false;

                    Data con = source.getChildByPath("con");
                    if (con != null) {
                        areas = new ArrayList<>(3);

                        for (Data conData : con.getChildren()) {
                            int type = DataTool.getInt("type", conData, -1);

                            if (type == 0) {
                                int startMap = DataTool.getInt("sMap", conData, 0);
                                int endMap = DataTool.getInt("eMap", conData, 0);

                                areas.add(new Pair<>(startMap, endMap));
                            } else if (type == 2) {
                                inParty = true;
                            }
                        }

                        if (areas.isEmpty()) {
                            areas = null;
                        }
                    }

                    if (DataTool.getInt("mesoupbyitem", source, 0) != 0) {
                        addBuffStatPairToListIfNotZero(statups, BuffStat.MESO_UP_BY_ITEM, 4);
                        prob = DataTool.getInt("prob", source, 1);
                    }

                    int itemupType = DataTool.getInt("itemupbyitem", source, 0);
                    if (itemupType != 0) {
                        addBuffStatPairToListIfNotZero(statups, BuffStat.ITEM_UP_BY_ITEM, 4);
                        prob = DataTool.getInt("prob", source, 1);

                        switch (itemupType) {
                            case 2:
                                itemupCode = DataTool.getInt("itemCode", source, 1);
                                break;

                            case 3:
                                itemupCode = DataTool.getInt("itemRange", source, 1);    // 3 digits
                                break;
                        }
                    }

                    if (DataTool.getInt("respectPimmune", source, 0) != 0) {
                        addBuffStatPairToListIfNotZero(statups, BuffStat.RESPECT_PIMMUNE, 4);
                    }

                    if (DataTool.getInt("respectMimmune", source, 0) != 0) {
                        addBuffStatPairToListIfNotZero(statups, BuffStat.RESPECT_MIMMUNE, 4);
                    }

                    if (DataTool.getString("defenseAtt", source, null) != null) {
                        addBuffStatPairToListIfNotZero(statups, BuffStat.DEFENSE_ATT, 4);
                    }

                    if (DataTool.getString("defenseState", source, null) != null) {
                        addBuffStatPairToListIfNotZero(statups, BuffStat.DEFENSE_STATE, 4);
                    }

                    int thaw = DataTool.getInt("thaw", source, 0);
                    if (thaw != 0) {
                        addBuffStatPairToListIfNotZero(statups, BuffStat.MAP_PROTECTION, thaw > 0 ? 1 : 2);
                    }

                    ret.cardStats = new CardItemupStats(itemupCode, prob, areas, inParty);
                } else if (ItemId.isExpIncrease(sourceid)) {
                    addBuffStatPairToListIfNotZero(statups, BuffStat.EXP_INCREASE, DataTool.getInt("expinc", source, 0));
                }
            } else {
                if (isMapChair(sourceid)) {
                    addBuffStatPairToListIfNotZero(statups, BuffStat.MAP_CHAIR, 1);
                } else if ((sourceid == Beginner.NIMBLE_FEET || sourceid == Noblesse.NIMBLE_FEET || sourceid == Evan.NIMBLE_FEET || sourceid == Legend.AGILE_BODY) && GameConfig.getServerBoolean("use_ultra_nimble_feet")) {
                    ret.jump = (short) (ret.speed * 4);
                    ret.speed *= 15;
                }
            }

            addBuffStatPairToListIfNotZero(statups, BuffStat.WATK, (int) ret.watk);
            addBuffStatPairToListIfNotZero(statups, BuffStat.WDEF, (int) ret.wdef);
            addBuffStatPairToListIfNotZero(statups, BuffStat.MATK, (int) ret.matk);
            addBuffStatPairToListIfNotZero(statups, BuffStat.MDEF, (int) ret.mdef);
            addBuffStatPairToListIfNotZero(statups, BuffStat.ACC, (int) ret.acc);
            addBuffStatPairToListIfNotZero(statups, BuffStat.AVOID, (int) ret.avoid);
            addBuffStatPairToListIfNotZero(statups, BuffStat.SPEED, (int) ret.speed);
            addBuffStatPairToListIfNotZero(statups, BuffStat.JUMP, (int) ret.jump);
            addBuffStatPairToListIfNotZero(statups, BuffStat.EXP_BUFF, Integer.valueOf(ret.expbuff));
        }

        Data ltd = source.getChildByPath("lt");
        if (ltd != null) {
            ret.lt = (Point) ltd.getData();
            ret.rb = (Point) source.getChildByPath("rb").getData();

            if (GameConfig.getServerBoolean("use_max_range_echo_of_hero") && (sourceid == Beginner.ECHO_OF_HERO || sourceid == Noblesse.ECHO_OF_HERO || sourceid == Legend.ECHO_OF_HERO || sourceid == Evan.ECHO_OF_HERO)) {
                ret.lt = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
                ret.rb = new Point(Integer.MAX_VALUE, Integer.MAX_VALUE);
            }
        }

        int x = DataTool.getInt("x", source, 0);

        if ((sourceid == Beginner.RECOVERY || sourceid == Noblesse.RECOVERY || sourceid == Evan.RECOVERY || sourceid == Legend.RECOVERY) && GameConfig.getServerBoolean("use_ultra_recovery") == true) {
            x *= 10;
        }
        ret.x = x;
        ret.y = DataTool.getInt("y", source, 0);

        ret.damage = DataTool.getIntConvert("damage", source, 100);
        ret.fixdamage = DataTool.getIntConvert("fixdamage", source, -1);
        ret.attackCount = DataTool.getIntConvert("attackCount", source, 1);
        ret.bulletCount = (short) DataTool.getIntConvert("bulletCount", source, 1);
        ret.bulletConsume = (short) DataTool.getIntConvert("bulletConsume", source, 0);
        ret.moneyCon = DataTool.getIntConvert("moneyCon", source, 0);
        ret.itemCon = DataTool.getInt("itemCon", source, 0);
        ret.itemConNo = DataTool.getInt("itemConNo", source, 0);
        ret.moveTo = DataTool.getInt("moveTo", source, -1);
        Map<MonsterStatus, Integer> monsterStatus = new EnumMap<>(MonsterStatus.class);
        if (skill) {
            // 根据技能ID设置对应的增益状态
            switch (sourceid) {
                // ========== 初心者/新手技能 ==========
                // 恢复技能：战士、魔法师、飞侠、林之灵、冒险家通用
                case Beginner.RECOVERY:
                case Noblesse.RECOVERY:
                case Legend.RECOVERY:
                case Evan.RECOVERY:
                    statups.add(new Pair<>(BuffStat.RECOVERY, x));
                    break;
                // 英雄回响：全职业经验加成效果
                case Beginner.ECHO_OF_HERO:
                case Noblesse.ECHO_OF_HERO:
                case Legend.ECHO_OF_HERO:
                case Evan.ECHO_OF_HERO:
                    statups.add(new Pair<>(BuffStat.ECHO_OF_HERO, ret.x));
                    break;
                // 骑乘技能：各种坐骑和飞船
                case Beginner.MONSTER_RIDER:
                case Noblesse.MONSTER_RIDER:
                case Legend.MONSTER_RIDER:
                case Corsair.BATTLE_SHIP:
                case Beginner.SPACESHIP:
                case Noblesse.SPACESHIP:
                case Beginner.YETI_MOUNT1:
                case Beginner.YETI_MOUNT2:
                case Noblesse.YETI_MOUNT1:
                case Noblesse.YETI_MOUNT2:
                case Legend.YETI_MOUNT1:
                case Legend.YETI_MOUNT2:
                case Beginner.WITCH_BROOMSTICK:
                case Noblesse.WITCH_BROOMSTICK:
                case Legend.WITCH_BROOMSTICK:
                case Beginner.BALROG_MOUNT:
                case Noblesse.BALROG_MOUNT:
                case Legend.BALROG_MOUNT:
                    statups.add(new Pair<>(BuffStat.MONSTER_RIDING, sourceid));
                    break;
                // 神圣之躯：无敌状态
                case Beginner.INVINCIBLE_BARRIER:
                case Noblesse.INVINCIBLE_BARRIER:
                case Legend.INVICIBLE_BARRIER:
                case Evan.INVINCIBLE_BARRIER:
                    statups.add(new Pair<>(BuffStat.DIVINE_BODY, 1));
                    break;
                // ========== 战士技能 ==========
                // 能量护盾：反弹部分伤害
                case Fighter.POWER_GUARD:
                case Page.POWER_GUARD:
                    statups.add(new Pair<>(BuffStat.POWERGUARD, x));
                    break;
                // 超越强化：HP和MP上限提升
                case Spearman.HYPER_BODY:
                case GM.HYPER_BODY:
                case SuperGM.HYPER_BODY:
                    statups.add(new Pair<>(BuffStat.HYPERBODYHP, x));
                    statups.add(new Pair<>(BuffStat.HYPERBODYMP, ret.y));
                    break;
                // 组合攻击：连击计数器
                case Crusader.COMBO:
                case DawnWarrior.COMBO:
                    statups.add(new Pair<>(BuffStat.COMBO, 1));
                    break;
                // 元素冲击：火/冰/雷/圣属性攻击强化
                case WhiteKnight.BW_FIRE_CHARGE:
                case WhiteKnight.BW_ICE_CHARGE:
                case WhiteKnight.BW_LIT_CHARGE:
                case WhiteKnight.SWORD_FIRE_CHARGE:
                case WhiteKnight.SWORD_ICE_CHARGE:
                case WhiteKnight.SWORD_LIT_CHARGE:
                case Paladin.BW_HOLY_CHARGE:
                case Paladin.SWORD_HOLY_CHARGE:
                case DawnWarrior.SOUL_CHARGE:
                case ThunderBreaker.LIGHTNING_CHARGE:
                    statups.add(new Pair<>(BuffStat.WK_CHARGE, x));
                    break;
                // 龙之血脉：攻击力提升BUFF
                case DragonKnight.DRAGON_BLOOD:
                    statups.add(new Pair<>(BuffStat.DRAGONBLOOD, ret.x));
                    break;
                // 稳如泰山：免疫击退效果
                case Hero.STANCE:
                case Paladin.STANCE:
                case DarkKnight.STANCE:
                case Aran.FREEZE_STANDING:
                    statups.add(new Pair<>(BuffStat.STANCE, iprop));
                    break;
                // 最终攻击：普攻触发额外伤害
                case DawnWarrior.FINAL_ATTACK:
                case WindArcher.FINAL_ATTACK:
                    statups.add(new Pair<>(BuffStat.FINALATTACK, x));
                    break;
                // ========== 魔法师技能 ==========
                // 魔法盾：用MP抵消部分伤害
                case Magician.MAGIC_GUARD:
                case BlazeWizard.MAGIC_GUARD:
                case Evan.MAGIC_GUARD:
                    statups.add(new Pair<>(BuffStat.MAGIC_GUARD, x));
                    break;
                // 圣甲术：物理伤害免疫
                case Cleric.INVINCIBLE:
                    statups.add(new Pair<>(BuffStat.INVINCIBLE, x));
                    break;
                // 神圣符号：组队经验加成
                case Priest.HOLY_SYMBOL:
                case SuperGM.HOLY_SYMBOL:
                    statups.add(new Pair<>(BuffStat.HOLY_SYMBOL, x));
                    break;
                // 无限魔力：魔法消耗减少
                case FPArchMage.INFINITY:
                case ILArchMage.INFINITY:
                case Bishop.INFINITY:
                    statups.add(new Pair<>(BuffStat.INFINITY, x));
                    break;
                // 魔法反射：反弹魔法伤害
                case FPArchMage.MANA_REFLECTION:
                case ILArchMage.MANA_REFLECTION:
                case Bishop.MANA_REFLECTION:
                    statups.add(new Pair<>(BuffStat.MANA_REFLECTION, 1));
                    break;
                // 神圣护盾：神圣伤害减免
                case Bishop.HOLY_SHIELD:
                    statups.add(new Pair<>(BuffStat.HOLY_SHIELD, x));
                    break;
                // 元素重置：解除异常状态
                case BlazeWizard.ELEMENTAL_RESET:
                case Evan.ELEMENTAL_RESET:
                    statups.add(new Pair<>(BuffStat.ELEMENTAL_RESET, x));
                    break;
                // 魔法护盾：MP抵消伤害
                case Evan.MAGIC_SHIELD:
                    statups.add(new Pair<>(BuffStat.MAGIC_SHIELD, x));
                    break;
                // 魔法抵抗：受到魔法伤害减少
                case Evan.MAGIC_RESISTANCE:
                    statups.add(new Pair<>(BuffStat.MAGIC_RESISTANCE, x));
                    break;
                // 缓速术：移动速度降低
                case Evan.SLOW:
                    statups.add(new Pair<>(BuffStat.SLOW, x));
                    // BOWMAN
                // 神秘门：创建传送门
                case Priest.MYSTIC_DOOR:
                // 灵魂箭：弓/弩发射强化
                case Hunter.SOUL_ARROW:
                case Crossbowman.SOUL_ARROW:
                case WindArcher.SOUL_ARROW:
                    statups.add(new Pair<>(BuffStat.SOULARROW, x));
                    break;
                // 傀儡：召唤怪物仇恨吸引
                case Ranger.PUPPET:
                case Sniper.PUPPET:
                case WindArcher.PUPPET:
                case Outlaw.OCTOPUS:
                case Corsair.WRATH_OF_THE_OCTOPI:
                    statups.add(new Pair<>(BuffStat.PUPPET, 1));
                    break;
                // 集中：命中率/回避率提升
                case Bowmaster.CONCENTRATE:
                    statups.add(new Pair<>(BuffStat.CONCENTRATE, x));
                    break;
                // 断筋：降低怪物移动速度
                case Bowmaster.HAMSTRING:
                    statups.add(new Pair<>(BuffStat.HAMSTRING, x));
                    monsterStatus.put(MonsterStatus.SPEED, x);
                    break;
                // 箭雨：降低怪物命中率
                case Marksman.BLIND:
                    statups.add(new Pair<>(BuffStat.BLIND, x));
                    monsterStatus.put(MonsterStatus.ACC, x);
                    break;
                // 锐利眼：暴击率/暴击伤害提升
                case Bowmaster.SHARP_EYES:
                case Marksman.SHARP_EYES:
                    statups.add(new Pair<>(BuffStat.SHARP_EYES, ret.x << 8 | ret.y));
                    break;
                // 风灵移动：提升移动速度
                case WindArcher.WIND_WALK:
                    statups.add(new Pair<>(BuffStat.WIND_WALK, x));
                    //break;    thanks Vcoc for noticing WW not showing for other players when changing maps
                // 暗影：隐身移动
                case Rogue.DARK_SIGHT:
                case NightWalker.DARK_SIGHT:
                    statups.add(new Pair<>(BuffStat.DARKSIGHT, x));
                    break;
                // 金币加成：打怪获得更多金币
                case Hermit.MESO_UP:
                    statups.add(new Pair<>(BuffStat.MESOUP, x));
                    break;
                // 影子搭档：分身后攻击
                case Hermit.SHADOW_PARTNER:
                case NightWalker.SHADOW_PARTNER:
                    statups.add(new Pair<>(BuffStat.SHADOWPARTNER, x));
                    break;
                // 金币护盾：用金币抵消部分伤害
                case ChiefBandit.MESO_GUARD:
                    statups.add(new Pair<>(BuffStat.MESOGUARD, x));
                    break;
                // 偷窃：打怪额外获得金币
                case ChiefBandit.PICKPOCKET:
                    statups.add(new Pair<>(BuffStat.PICKPOCKET, x));
                    break;
                // 暗影星：飞镖强化攻击
                case NightLord.SHADOW_STARS:
                    statups.add(new Pair<>(BuffStat.SHADOW_CLAW, 0));
                    break;
                // PIRATE
                // 突进：快速移动
                case Pirate.DASH:
                case ThunderBreaker.DASH:
                case Beginner.SPACE_DASH:
                case Noblesse.SPACE_DASH:
                    statups.add(new Pair<>(BuffStat.DASH2, ret.x));
                    statups.add(new Pair<>(BuffStat.DASH, ret.y));
                    break;
                // 速度激发：提升队友移动/攻击速度
                case Corsair.SPEED_INFUSION:
                case Buccaneer.SPEED_INFUSION:
                case ThunderBreaker.SPEED_INFUSION:
                    statups.add(new Pair<>(BuffStat.SPEED_INFUSION, x));
                    break;
                // 追踪目标：召唤物优先攻击特定目标
                case Outlaw.HOMING_BEACON:
                case Corsair.BULLSEYE:
                    statups.add(new Pair<>(BuffStat.HOMING_BEACON, x));
                    break;
                // 火花：电属性攻击强化
                case ThunderBreaker.SPARK:
                    statups.add(new Pair<>(BuffStat.SPARK, x));
                    break;
                // ========== 战士/弓手/飞侠/海盗通用 ==========
                // 武器加速：提升攻击速度
                case Aran.POLEARM_BOOSTER:
                case Fighter.AXE_BOOSTER:
                case Fighter.SWORD_BOOSTER:
                case Page.BW_BOOSTER:
                case Page.SWORD_BOOSTER:
                case Spearman.POLEARM_BOOSTER:
                case Spearman.SPEAR_BOOSTER:
                case Hunter.BOW_BOOSTER:
                case Crossbowman.CROSSBOW_BOOSTER:
                case Assassin.CLAW_BOOSTER:
                case Bandit.DAGGER_BOOSTER:
                case FPMage.SPELL_BOOSTER:
                case ILMage.SPELL_BOOSTER:
                case Brawler.KNUCKLER_BOOSTER:
                case Gunslinger.GUN_BOOSTER:
                case DawnWarrior.SWORD_BOOSTER:
                case BlazeWizard.SPELL_BOOSTER:
                case WindArcher.BOW_BOOSTER:
                case NightWalker.CLAW_BOOSTER:
                case ThunderBreaker.KNUCKLER_BOOSTER:
                case Evan.MAGIC_BOOSTER:
                case Beginner.POWER_EXPLOSION:
                case Noblesse.POWER_EXPLOSION:
                case Legend.POWER_EXPLOSION:
                    statups.add(new Pair<>(BuffStat.BOOSTER, x));
                    break;
                // 枫叶之刃：全属性提升
                case Hero.MAPLE_WARRIOR:
                case Paladin.MAPLE_WARRIOR:
                case DarkKnight.MAPLE_WARRIOR:
                case FPArchMage.MAPLE_WARRIOR:
                case ILArchMage.MAPLE_WARRIOR:
                case Bishop.MAPLE_WARRIOR:
                case Bowmaster.MAPLE_WARRIOR:
                case Marksman.MAPLE_WARRIOR:
                case NightLord.MAPLE_WARRIOR:
                case Shadower.MAPLE_WARRIOR:
                case Corsair.MAPLE_WARRIOR:
                case Buccaneer.MAPLE_WARRIOR:
                case Aran.MAPLE_WARRIOR:
                case Evan.MAPLE_WARRIOR:
                    statups.add(new Pair<>(BuffStat.MAPLE_WARRIOR, ret.x));
                    break;
                // ========== 召唤技能 ==========
                // 银鹰/金鹰召唤：附带眩晕效果
                case Ranger.SILVER_HAWK:
                case Sniper.GOLDEN_EAGLE:
                    statups.add(new Pair<>(BuffStat.SUMMON, 1));
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                // 火焰放射/寒冰急冻：附带冰冻效果
                case FPArchMage.ELQUINES:
                case Marksman.FROST_PREY:
                    statups.add(new Pair<>(BuffStat.SUMMON, 1));
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    break;
                // 各种召唤兽：龙/凤凰/冰火蜥蜴/黑暗灵气等
                case Priest.SUMMON_DRAGON:
                case Bowmaster.PHOENIX:
                case ILArchMage.IFRIT:
                case Bishop.BAHAMUT:
                case DarkKnight.BEHOLDER:
                case Outlaw.GAVIOTA:
                case DawnWarrior.SOUL:
                case BlazeWizard.FLAME:
                case WindArcher.STORM:
                case NightWalker.DARKNESS:
                case ThunderBreaker.LIGHTNING:
                case BlazeWizard.IFRIT:
                    statups.add(new Pair<>(BuffStat.SUMMON, 1));
                    break;
                // ----------------------------- 怪物状态效果 ---------------------------------- //
                // 护甲破碎：封印怪物技能
                case Crusader.ARMOR_CRASH:
                case DragonKnight.POWER_CRASH:
                case WhiteKnight.MAGIC_CRASH:
                    monsterStatus.put(MonsterStatus.SEAL_SKILL, 1);
                    break;
                // 病毒扩散：降低怪物攻击/防御
                case Rogue.DISORDER:
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                // 催眠：使怪物无法移动
                case Corsair.HYPNOTIZE:
                    monsterStatus.put(MonsterStatus.INERTMOB, 1);
                    break;
                // 忍者伏击：造成持续伤害
                case NightLord.NINJA_AMBUSH:
                case Shadower.NINJA_AMBUSH:
                    monsterStatus.put(MonsterStatus.NINJA_AMBUSH, ret.damage);
                    break;
                // 威胁：降低怪物攻击/防御
                case Page.THREATEN:
                    monsterStatus.put(MonsterStatus.WATK, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.y);
                    break;
                // 龙咆哮：造成伤害并眩晕
                case DragonKnight.DRAGON_ROAR:
                    ret.hpR = -x / 100.0;
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                // 昏迷/冲锋打击：造成额外伤害
                case Crusader.AXE_COMA:
                case Crusader.SWORD_COMA:
                case Crusader.SHOUT:
                case WhiteKnight.CHARGE_BLOW:
                case Hunter.ARROW_BOMB:
                case ChiefBandit.ASSAULTER:
                case Shadower.BOOMERANG_STEP:
                case Brawler.BACK_SPIN_BLOW:
                case Brawler.DOUBLE_UPPERCUT:
                case Buccaneer.DEMOLITION:
                case Buccaneer.SNATCH:
                case Buccaneer.BARRAGE:
                case Gunslinger.BLANK_SHOT:
                case DawnWarrior.COMA:
                case ThunderBreaker.BARRAGE:
                case Aran.ROLLING_SPIN:
                case Evan.FIRE_BREATH:
                case Evan.BLAZE:
                    monsterStatus.put(MonsterStatus.STUN, 1);
                    break;
                case NightLord.TAUNT:
                case Shadower.TAUNT:
                    monsterStatus.put(MonsterStatus.SHOWDOWN, ret.x);
                    monsterStatus.put(MonsterStatus.MDEF, ret.x);
                    monsterStatus.put(MonsterStatus.WDEF, ret.x);
                    break;
                case ILWizard.COLD_BEAM:
                case ILMage.ICE_STRIKE:
                case ILArchMage.BLIZZARD:
                case ILMage.ELEMENT_COMPOSITION:
                case Sniper.BLIZZARD:
                case Outlaw.ICE_SPLITTER:
                case FPArchMage.PARALYZE:
                case Aran.COMBO_TEMPEST:
                case Evan.ICE_BREATH:
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    ret.duration *= 2; // freezing skills are a little strange
                    break;
                case FPWizard.SLOW:
                case ILWizard.SLOW:
                case BlazeWizard.SLOW:
                    monsterStatus.put(MonsterStatus.SPEED, ret.x);
                    break;
                case FPWizard.POISON_BREATH:
                case FPMage.ELEMENT_COMPOSITION:
                    monsterStatus.put(MonsterStatus.POISON, 1);
                    break;
                case Priest.DOOM:
                    monsterStatus.put(MonsterStatus.DOOM, 1);
                    break;
                case ILMage.SEAL:
                case FPMage.SEAL:
                case BlazeWizard.SEAL:
                    monsterStatus.put(MonsterStatus.SEAL, 1);
                    break;
                case Hermit.SHADOW_WEB: // shadow web
                case NightWalker.SHADOW_WEB:
                    monsterStatus.put(MonsterStatus.SHADOW_WEB, 1);
                    break;
                case FPArchMage.FIRE_DEMON:
                case ILArchMage.ICE_DEMON:
                    monsterStatus.put(MonsterStatus.POISON, 1);
                    monsterStatus.put(MonsterStatus.FREEZE, 1);
                    break;
                case Evan.PHANTOM_IMPRINT:
                    monsterStatus.put(MonsterStatus.PHANTOM_IMPRINT, x);
                    //ARAN
                case Aran.COMBO_ABILITY:
                    statups.add(new Pair<>(BuffStat.ARAN_COMBO, 100));
                    break;
                case Aran.COMBO_BARRIER:
                    statups.add(new Pair<>(BuffStat.COMBO_BARRIER, ret.x));
                    break;
                case Aran.COMBO_DRAIN:
                    statups.add(new Pair<>(BuffStat.COMBO_DRAIN, ret.x));
                    break;
                case Aran.SMART_KNOCKBACK:
                    statups.add(new Pair<>(BuffStat.SMART_KNOCKBACK, ret.x));
                    break;
                case Aran.BODY_PRESSURE:
                    statups.add(new Pair<>(BuffStat.BODY_PRESSURE, ret.x));
                    break;
                case Aran.SNOW_CHARGE:
                    statups.add(new Pair<>(BuffStat.WK_CHARGE, ret.duration));
                    break;
                default:
                    break;
            }
        }
        if (ret.isMorph()) {
            statups.add(new Pair<>(BuffStat.MORPH, ret.getMorph()));
        }
        if (ret.ghost > 0 && !skill) {
            statups.add(new Pair<>(BuffStat.GHOST_MORPH, ret.ghost));
        }
        ret.monsterStatus = monsterStatus;
        statups.trimToSize();
        ret.statups = statups;
        return ret;
    }

    /**
     * @param applyto
     * @param obj
     * @param attack  damage done by the skill
     */
    /**
     * 应用被动效果，如MP吸收等
     * @param applyto 效果应用目标角色
     * @param obj 相关对象（通常是怪物）
     * @param attack 攻击力
     */
    public void applyPassive(Character applyto, MapObject obj, int attack) {
        if (makeChanceResult()) {
            switch (sourceid) { // MP eater
                case FPWizard.MP_EATER:
                case ILWizard.MP_EATER:
                case Cleric.MP_EATER:
                    if (obj == null || obj.getType() != MapObjectType.MONSTER) {
                        return;
                    }
                    Monster mob = (Monster) obj; // x is absorb percentage
                    if (!mob.isBoss()) {
                        int absorbMp = Math.min((int) (mob.getMaxMp() * (getX() / 100.0)), mob.getMp());
                        if (absorbMp > 0) {
                            mob.setMp(mob.getMp() - absorbMp);
                            applyto.addMP(absorbMp);
                            applyto.sendPacket(PacketCreator.showOwnBuffEffect(sourceid, 1));
                            applyto.getMap().broadcastMessage(applyto, PacketCreator.showBuffEffect(applyto.getId(), sourceid, 1), false);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 应用英雄回响效果，对地图上的所有玩家应用效果
     * @param applyfrom 施放技能的角色
     * @return 应用效果的结果
     */
    public boolean applyEchoOfHero(Character applyfrom) {
        Map<Integer, Character> mapPlayers = applyfrom.getMap().getMapPlayers();
        mapPlayers.remove(applyfrom.getId());

        boolean hwResult = applyTo(applyfrom);
        for (Character chr : mapPlayers.values()) {    // Echo of Hero not buffing players in the map detected thanks to Masterrulax
            applyTo(applyfrom, chr, false, null, false, 1);
        }

        return hwResult;
    }

    public boolean applyTo(Character chr) {
        return applyTo(chr, chr, true, null, false, 1);
    }

    public boolean applyTo(Character chr, boolean useMaxRange) {
        return applyTo(chr, chr, true, null, useMaxRange, 1);
    }

    public boolean applyTo(Character chr, Point pos) {
        return applyTo(chr, chr, true, pos, false, 1);
    }

    // primary: the player caster of the buff
    private boolean applyTo(Character applyfrom, Character applyto, boolean primary, Point pos, boolean useMaxRange, int affectedPlayers) {
        // 特殊处理：如果是GM隐藏技能，则切换目标的隐藏状态
        if (skill && (sourceid == GM.HIDE || sourceid == SuperGM.HIDE)) {
            applyto.toggleHide(false);
            return true;
        }

        // 如果是主要施法者且为治疗技能，则应用增益效果
        if (primary && isHeal()) {
            affectedPlayers = applyBuff(applyfrom, useMaxRange);
        }

        // 计算HP和MP变化值
        int hpchange = calcHPChange(applyfrom, primary, affectedPlayers);
        int mpchange = calcMPChange(applyfrom, primary);
        // 处理主要施法者的情况
        if (primary) {
            // 检查并消耗所需物品
            if (itemConNo != 0) {
                if (!applyto.getAbstractPlayerInteraction().hasItem(itemCon, itemConNo)) {
                    applyto.sendPacket(PacketCreator.enableActions());
                    return false;
                }
                // 从角色库存中移除消耗品
                InventoryManipulator.removeById(applyto.getClient(), ItemConstants.getInventoryType(itemCon), itemCon, itemConNo, false, true);
            }
        } else {
            // 处理非主要施法者的情况，如果是复活技能则特殊处理
            if (isResurrection()) {
                hpchange = applyto.getCurrentMaxHp();
                applyto.broadcastStance(applyto.isFacingLeft() ? 5 : 4);
            }
        }

        // 根据技能类型处理特殊效果
        if (isDispel() && makeChanceResult()) {
            // 驱散负面状态
            applyto.dispelDebuffs();
        } else if (isCureAllAbnormalStatus()) {
            // 清除所有异常状态
            applyto.purgeDebuffs();
        } else if (isComboReset()) {
            // 重置连击数
            applyto.setCombo((short) 0);
        }
        /*if (applyfrom.getMp() < getMpCon()) {
         AutobanFactory.MPCON.addPoint(applyfrom.getAutobanManager(), "mpCon hack for skill:" + sourceid + "; Player MP: " + applyto.getMp() + " MP Needed: " + getMpCon());
         } */

        // 应用HP和MP变化
        if (!applyto.applyHpMpChange(hpCon, hpchange, mpchange)) {
            applyto.sendPacket(PacketCreator.enableActions());
            return false;
        }

        // 处理传送效果
        if (moveTo != -1) {
            if (moveTo != applyto.getMapId()) {
                MapleMap target;
                Portal pt;

                if (moveTo == MapId.NONE) {
                    // 处理反禁传效果
                    if (sourceid != ItemId.ANTI_BANISH_SCROLL) {
                        target = applyto.getMap().getReturnMap();
                        pt = target.getRandomPlayerSpawnpoint();
                    } else {
                        // 检查是否可以恢复上次被禁传的位置
                        if (!applyto.canRecoverLastBanish()) {
                            return false;
                        }

                        Pair<Integer, Integer> lastBanishInfo = applyto.getLastBanishData();
                        target = applyto.getWarpMap(lastBanishInfo.getLeft());
                        pt = target.getPortal(lastBanishInfo.getRight());
                    }
                } else {
                    // 获取目标地图和传送点
                    target = applyto.getClient().getWorldServer().getChannel(applyto.getClient().getChannel()).getMapFactory().getMap(moveTo);
                    int targetid = target.getId() / 10000000;
                    // 检查地图类型是否允许传送
                    if (targetid != 60 && applyto.getMapId() / 10000000 != 61 && targetid != applyto.getMapId() / 10000000 && targetid != 21 && targetid != 20 && targetid != 12 && (applyto.getMapId() / 10000000 != 10 && applyto.getMapId() / 10000000 != 12)) {
                        return false;
                    }

                    pt = target.getRandomPlayerSpawnpoint();
                }

                // 执行地图切换
                applyto.changeMap(target, pt);
            } else {
                return false;
            }
        }
        // 处理影爪技能：消耗飞镖
        if (isShadowClaw()) {
            short projectileConsume = this.getBulletConsume();  // noticed by shavit

            Inventory use = applyto.getInventory(InventoryType.USE);
            use.lockInventory();
            try {
                Item projectile = null;
                // 遍历使用栏物品，查找符合条件的飞镖
                for (int i = 1; i <= use.getSlotLimit(); i++) { // impose order...
                    Item item = use.getItem((short) i);
                    if (item != null) {
                        // 检查是否为飞镖类型且数量足够
                        if (ItemConstants.isThrowingStar(item.getItemId()) && item.getQuantity() >= projectileConsume) {
                            projectile = item;
                            break;
                        }
                    }
                }
                // 如果找不到足够的飞镖则返回失败
                if (projectile == null) {
                    return false;
                } else {
                    // 从库存中移除使用的飞镖
                    InventoryManipulator.removeFromSlot(applyto.getClient(), InventoryType.USE, projectile.getPosition(), projectileConsume, false, true);
                }
            } finally {
                use.unlockInventory();
            }
        }
        // 获取召唤物移动类型
        SummonMovementType summonMovementType = getSummonMovementType();
        // 如果是持续效果、 Cygnus FA 或有召唤物，则应用效果
        if (overTime || isCygnusFA() || summonMovementType != null) {
            if (summonMovementType != null && pos != null) {
                // 根据召唤物移动类型取消相应的增益状态
                if (summonMovementType.getValue() == SummonMovementType.STATIONARY.getValue()) {
                    applyto.cancelBuffStats(BuffStat.PUPPET);
                } else {
                    applyto.cancelBuffStats(BuffStat.SUMMON);
                }

                applyto.sendPacket(PacketCreator.enableActions());
            }

            // 应用增益效果
            applyBuffEffect(applyfrom, applyto, primary);
        }

        // 处理主要施法者的额外效果
        if (primary) {
            if (overTime) {
                applyBuff(applyfrom, useMaxRange);
            }

            if (isMonsterBuff()) {
                applyMonsterBuff(applyfrom);
            }
        }

        // 处理疲劳度变化
        if (this.getFatigue() != 0) {
            applyto.getMapleMount().setTiredness(applyto.getMapleMount().getTiredness() + this.getFatigue());
        }

        // 如果有召唤物移动类型且位置有效，则创建召唤物
        if (summonMovementType != null && pos != null) {
            final Summon tosummon = new Summon(applyfrom, sourceid, pos, summonMovementType);
            applyfrom.getMap().spawnSummon(tosummon);
            applyfrom.addSummon(sourceid, tosummon);
            tosummon.addHP(x);
            if (isBeholder()) {
                tosummon.addHP(1);
            }
        }
        // 处理魔法门技能
        if (isMagicDoor() && !FieldLimit.DOOR.check(applyto.getMap().getFieldLimit())) { // Magic Door
            int y = applyto.getFh();
            if (y == 0) {
                y = applyto.getMap().getGroundBelow(applyto.getPosition()).y;    // thanks Lame for pointing out unusual cases of doors sending players on ground below
            }
            Point doorPosition = new Point(applyto.getPosition().x, y);
            Door door = new Door(applyto, doorPosition);

            if (door.getOwnerId() >= 0) {
                applyto.applyPartyDoor(door, false);

                door.getTarget().spawnDoor(door.getAreaDoor());
                door.getTown().spawnDoor(door.getTownDoor());
            } else {
                InventoryManipulator.addFromDrop(applyto.getClient(), new Item(ItemId.MAGIC_ROCK, (short) 0, (short) 1), false);

                if (door.getOwnerId() == -3) {
                    applyto.dropMessage(5, "Mystic Door cannot be cast far from a spawn point. Nearest one is at " + door.getDoorStatus().getRight() + "pts " + door.getDoorStatus().getLeft());
                } else if (door.getOwnerId() == -2) {
                    applyto.dropMessage(5, "Mystic Door cannot be cast on a slope, try elsewhere.");
                } else {
                    applyto.dropMessage(5, "There are no door portals available for the town at this moment. Try again later.");
                }

                applyto.cancelBuffStats(BuffStat.SOULARROW);  // cancel door buff
            }
        } else if (isMist()) {
            // 创建迷雾效果
            Rectangle bounds = calculateBoundingBox(sourceid == NightWalker.POISON_BOMB ? pos : applyfrom.getPosition(), applyfrom.isFacingLeft());
            Mist mist = new Mist(bounds, applyfrom, this);
            applyfrom.getMap().spawnMist(mist, getDuration(), mist.isPoisonMist(), false, mist.isRecoveryMist());
        } else if (isTimeLeap()) {
            // 时间跳跃技能：移除除了时间跳跃外的所有冷却
            applyto.removeAllCooldownsExcept(Buccaneer.TIME_LEAP, true);
        } else if (cp != 0 && applyto.getMonsterCarnival() != null) {
            // 怪物嘉年华：获得CP点数
            applyto.gainCP(cp);
        } else if (nuffSkill != 0 && applyto.getParty() != null && applyto.getMap().isCPQMap()) { // added by Drago (Dragohe4rt)
            final MCSkill skill = CarnivalFactory.getInstance().getSkill(nuffSkill);
            if (skill != null) {
                final Disease dis = skill.getDisease();
                Party opposition = applyfrom.getParty().getEnemy();
                if (skill.targetsAll()) {
                    // 对所有敌人应用负面效果
                    for (PartyCharacter enemyChrs : opposition.getPartyMembers()) {
                        Character chrApp = enemyChrs.getPlayer();
                        if (chrApp != null && chrApp.getMap().isCPQMap()) {
                            if (dis == null) {
                                chrApp.dispel();
                            } else {
                                MobSkill mobSkill = MobSkillFactory.getMobSkillOrThrow(dis.getMobSkillType(), skill.level());
                                chrApp.giveDebuff(dis, mobSkill);
                            }
                        }
                    }
                } else {
                    // 随机选择一个敌人应用负面效果
                    int amount = opposition.getMembers().size();
                    int randd = (int) Math.floor(Math.random() * amount);
                    Character chrApp = applyfrom.getMap().getCharacterById(opposition.getMemberByPos(randd).getId());
                    if (chrApp != null && chrApp.getMap().isCPQMap()) {
                        if (dis == null) {
                            chrApp.dispel();
                        } else {
                            MobSkill mobSkill = MobSkillFactory.getMobSkillOrThrow(dis.getMobSkillType(), skill.level());
                            chrApp.giveDebuff(dis, mobSkill);
                        }
                    }
                }
            }
        } else if (cureDebuffs.size() > 0) { // added by Drago (Dragohe4rt)
            // 治愈指定的负面状态
            for (final Disease debuff : cureDebuffs) {
                applyfrom.dispelDebuff(debuff);
            }
        } else if (mobSkill > 0 && mobSkillLevel > 0) {
            // 应用怪物技能效果
            MobSkillType mobSkillType = MobSkillType.from(mobSkill).orElseThrow();
            MobSkill ms = MobSkillFactory.getMobSkillOrThrow(mobSkillType, mobSkillLevel);
            Disease dis = Disease.getBySkill(mobSkillType);

            if (target > 0) {
                // 对地图上的其他玩家应用负面效果
                for (Character chr : applyto.getMap().getAllPlayers()) {
                    if (chr.getId() != applyto.getId()) {
                        chr.giveDebuff(dis, ms);
                    }
                }
            } else {
                // 对目标应用负面效果
                applyto.giveDebuff(dis, ms);
            }
        }
        // 返回成功标志
        return true;
    }

    private int applyBuff(Character applyfrom, boolean useMaxRange) {
        int affectedc = 1;

        if (isPartyBuff() && (applyfrom.getParty() != null || isGmBuff())) {
            Rectangle bounds = (!useMaxRange) ? calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft()) : new Rectangle(Integer.MIN_VALUE / 2, Integer.MIN_VALUE / 2, Integer.MAX_VALUE, Integer.MAX_VALUE);
            List<MapObject> affecteds = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapObjectType.PLAYER));
            List<Character> affectedp = new ArrayList<>(affecteds.size());
            for (MapObject affectedmo : affecteds) {
                Character affected = (Character) affectedmo;
                if (affected != applyfrom && (isGmBuff() || applyfrom.getParty().equals(affected.getParty()))) {
                    if (!isResurrection()) {
                        if (affected.isAlive()) {
                            affectedp.add(affected);
                        }
                    } else {
                        if (!affected.isAlive()) {
                            affectedp.add(affected);
                        }
                    }
                }
            }

            affectedc += affectedp.size();   // used for heal
            for (Character affected : affectedp) {
                applyTo(applyfrom, affected, false, null, useMaxRange, affectedc);
                affected.sendPacket(PacketCreator.showOwnBuffEffect(sourceid, 2));
                affected.getMap().broadcastMessage(affected, PacketCreator.showBuffEffect(affected.getId(), sourceid, 2), false);
            }
        }

        return affectedc;
    }

    private void applyMonsterBuff(Character applyfrom) {
        Rectangle bounds = calculateBoundingBox(applyfrom.getPosition(), applyfrom.isFacingLeft());
        List<MapObject> affected = applyfrom.getMap().getMapObjectsInRect(bounds, Arrays.asList(MapObjectType.MONSTER));
        Skill skill_ = SkillFactory.getSkill(sourceid);
        int i = 0;
        for (MapObject mo : affected) {
            Monster monster = (Monster) mo;
            if (isDispel()) {
                monster.debuffMob(skill_.getId());
            } else if (isSeal() && monster.isBoss()) {  // thanks IxianMace for noticing seal working on bosses
                // do nothing
            } else {
                if (makeChanceResult()) {
                    monster.applyStatus(applyfrom, new MonsterStatusEffect(getMonsterStati(), skill_, null, false), isPoison(), getDuration());
                    if (isCrash()) {
                        monster.debuffMob(skill_.getId());
                    }
                }
            }
            i++;
            if (i >= mobCount) {
                break;
            }
        }
    }

    private Rectangle calculateBoundingBox(Point posFrom, boolean facingLeft) {
        Point mylt;
        Point myrb;
        if (facingLeft) {
            mylt = new Point(lt.x + posFrom.x, lt.y + posFrom.y);
            myrb = new Point(rb.x + posFrom.x, rb.y + posFrom.y);
        } else {
            myrb = new Point(-lt.x + posFrom.x, rb.y + posFrom.y);  // thanks Conrad, April for noticing a disturbance in AoE skill behavior after a hitched refactor here
            mylt = new Point(-rb.x + posFrom.x, lt.y + posFrom.y);
        }
        Rectangle bounds = new Rectangle(mylt.x, mylt.y, myrb.x - mylt.x, myrb.y - mylt.y);
        return bounds;
    }

    public int getBuffLocalDuration() {
        return !GameConfig.getServerBoolean("use_buff_everlasting") ? duration : Integer.MAX_VALUE;
    }

    /**
     * 静默应用增益效果，不发送任何提示消息
     * @param chr 应用效果的角色
     * @param localStartTime 效果开始时间
     */
    public void silentApplyBuff(Character chr, long localStartTime) {
        int localDuration = getBuffLocalDuration();
        localDuration = alchemistModifyVal(chr, localDuration, false);
        //CancelEffectAction cancelAction = new CancelEffectAction(chr, this, starttime);
        //ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, ((starttime + localDuration) - Server.getInstance().getCurrentTime()));

        chr.registerEffect(this, localStartTime, localStartTime + localDuration, true);
        SummonMovementType summonMovementType = getSummonMovementType();
        if (summonMovementType != null) {
            final Summon tosummon = new Summon(chr, sourceid, chr.getPosition(), summonMovementType);
            if (!tosummon.isStationary()) {
                chr.addSummon(sourceid, tosummon);
                tosummon.addHP(x);
            }
        }
        if (sourceid == Corsair.BATTLE_SHIP) {
            chr.announceBattleshipHp();
        }
    }

    /**
     * 应用连击增益效果
     * @param applyto 应用效果的角色
     * @param combo 连击数
     */
    public final void applyComboBuff(final Character applyto, int combo) {
        final List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.ARAN_COMBO, combo));
        applyto.sendPacket(PacketCreator.giveBuff(sourceid, 99999, stat));

        final long starttime = Server.getInstance().getCurrentTime();
//	final CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
//	final ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, ((starttime + 99999) - Server.getInstance().getCurrentTime()));
        applyto.registerEffect(this, starttime, Long.MAX_VALUE, false);
    }

    /**
     * 应用追踪信标增益效果
     * @param applyto 应用效果的角色
     * @param objectid 对象ID
     */
    public final void applyBeaconBuff(final Character applyto, int objectid) { // thanks Thora & Hyun for reporting an issue with homing beacon autoflagging mobs when changing maps
        final List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.HOMING_BEACON, objectid));
        applyto.sendPacket(PacketCreator.giveBuff(1, sourceid, stat));

        final long starttime = Server.getInstance().getCurrentTime();
        applyto.registerEffect(this, starttime, Long.MAX_VALUE, false);
    }

    /**
     * 更新增益效果显示
     * @param target 目标角色
     * @param activeStats 激活的增益状态列表
     * @param starttime 开始时间
     */
    public void updateBuffEffect(Character target, List<Pair<BuffStat, Integer>> activeStats, long starttime) {
        int localDuration = getBuffLocalDuration();
        localDuration = alchemistModifyVal(target, localDuration, false);

        long leftDuration = (starttime + localDuration) - Server.getInstance().getCurrentTime();
        if (leftDuration > 0) {
            if (isDash() || isInfusion()) {
                target.sendPacket(PacketCreator.givePirateBuff(activeStats, (skill ? sourceid : -sourceid), (int) leftDuration));
            } else {
                target.sendPacket(PacketCreator.giveBuff((skill ? sourceid : -sourceid), (int) leftDuration, activeStats));
            }
        }
    }

    private void applyBuffEffect(Character applyfrom, Character applyto, boolean primary) {
        if (!isMonsterRiding() && !isCouponBuff() && !isMysticDoor() && !isHyperBody() && !isCombo()) {     // last mystic door already dispelled if it has been used before.
            applyto.cancelEffect(this, true, -1);
        }

        List<Pair<BuffStat, Integer>> localstatups = statups;
        int localDuration = getBuffLocalDuration();
        int localsourceid = sourceid;
        int seconds = localDuration / 1000;
        Mount givemount = null;
        if (isMonsterRiding()) {
            int ridingMountId = 0;
            Item mount = applyfrom.getInventory(InventoryType.EQUIPPED).getItem((short) -18);
            if (mount != null) {
                ridingMountId = mount.getItemId();
            }

            if (sourceid == Corsair.BATTLE_SHIP) {
                ridingMountId = ItemId.BATTLESHIP;
            } else if (sourceid == Beginner.SPACESHIP || sourceid == Noblesse.SPACESHIP) {
                ridingMountId = 1932000 + applyto.getSkillLevel(sourceid);
            } else if (sourceid == Beginner.YETI_MOUNT1 || sourceid == Noblesse.YETI_MOUNT1 || sourceid == Legend.YETI_MOUNT1) {
                ridingMountId = 1932003;
            } else if (sourceid == Beginner.YETI_MOUNT2 || sourceid == Noblesse.YETI_MOUNT2 || sourceid == Legend.YETI_MOUNT2) {
                ridingMountId = 1932004;
            } else if (sourceid == Beginner.WITCH_BROOMSTICK || sourceid == Noblesse.WITCH_BROOMSTICK || sourceid == Legend.WITCH_BROOMSTICK) {
                ridingMountId = 1932005;
            } else if (sourceid == Beginner.BALROG_MOUNT || sourceid == Noblesse.BALROG_MOUNT || sourceid == Legend.BALROG_MOUNT) {
                ridingMountId = 1932010;
            }

            // thanks inhyuk for noticing some skill mounts not acting properly for other players when changing maps
            givemount = applyto.mount(ridingMountId, sourceid);
            applyto.getClient().getWorldServer().registerMountHunger(applyto);

            localDuration = sourceid;
            localsourceid = ridingMountId;
            localstatups = Collections.singletonList(new Pair<>(BuffStat.MONSTER_RIDING, 0));
        } else if (isSkillMorph()) {
            for (int i = 0; i < localstatups.size(); i++) {
                if (localstatups.get(i).getLeft().equals(BuffStat.MORPH)) {
                    localstatups.set(i, new Pair<>(BuffStat.MORPH, getMorph(applyto)));
                    break;
                }
            }
        }
        if (primary) {
            localDuration = alchemistModifyVal(applyfrom, localDuration, false);
            applyto.getMap().broadcastMessage(applyto, PacketCreator.showBuffEffect(applyto.getId(), sourceid, 1, (byte) 3), false);
        }
        if (localstatups.size() > 0) {
            Packet buff = null;
            Packet mbuff = null;
            if (this.isActive(applyto)) {
                buff = PacketCreator.giveBuff((skill ? sourceid : -sourceid), localDuration, localstatups);
            }
            if (isDash()) {
                buff = PacketCreator.givePirateBuff(statups, sourceid, seconds);
                mbuff = PacketCreator.giveForeignPirateBuff(applyto.getId(), sourceid, seconds, localstatups);
            } else if (isWkCharge()) {
                mbuff = PacketCreator.giveForeignWKChargeEffect(applyto.getId(), sourceid, localstatups);
            } else if (isInfusion()) {
                buff = PacketCreator.givePirateBuff(localstatups, sourceid, seconds);
                mbuff = PacketCreator.giveForeignPirateBuff(applyto.getId(), sourceid, seconds, localstatups);
            } else if (isDs()) {
                List<Pair<BuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(BuffStat.DARKSIGHT, 0));
                mbuff = PacketCreator.giveForeignBuff(applyto.getId(), dsstat);
            } else if (isWw()) {
                List<Pair<BuffStat, Integer>> dsstat = Collections.singletonList(new Pair<>(BuffStat.WIND_WALK, 0));
                mbuff = PacketCreator.giveForeignBuff(applyto.getId(), dsstat);
            } else if (isCombo()) {
                Integer comboCount = applyto.getBuffedValue(BuffStat.COMBO);
                if (comboCount == null) {
                    comboCount = 0;
                }

                List<Pair<BuffStat, Integer>> cbstat = Collections.singletonList(new Pair<>(BuffStat.COMBO, comboCount));
                buff = PacketCreator.giveBuff((skill ? sourceid : -sourceid), localDuration, cbstat);
                mbuff = PacketCreator.giveForeignBuff(applyto.getId(), cbstat);
            } else if (isMonsterRiding()) {
                if (sourceid == Corsair.BATTLE_SHIP) {//hp
                    if (applyto.getBattleshipHp() <= 0) {
                        applyto.resetBattleshipHp();
                    }

                    localstatups = statups;
                }
                buff = PacketCreator.giveBuff(localsourceid, localDuration, localstatups);
                mbuff = PacketCreator.showMonsterRiding(applyto.getId(), givemount);
                localDuration = duration;
            } else if (isShadowPartner()) {
                List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.SHADOWPARTNER, 0));
                mbuff = PacketCreator.giveForeignBuff(applyto.getId(), stat);
            } else if (isSoulArrow()) {
                List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.SOULARROW, 0));
                mbuff = PacketCreator.giveForeignBuff(applyto.getId(), stat);
            } else if (isEnrage()) {
                applyto.handleOrbconsume();
            } else if (isMorph()) {
                List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.MORPH, getMorph(applyto)));
                mbuff = PacketCreator.giveForeignBuff(applyto.getId(), stat);
            } else if (isAriantShield()) {
                List<Pair<BuffStat, Integer>> stat = Collections.singletonList(new Pair<>(BuffStat.AURA, 1));
                mbuff = PacketCreator.giveForeignBuff(applyto.getId(), stat);
            }

            if (buff != null) {
                //Thanks flav for such a simple release! :)
                //Thanks Conrad, Atoot for noticing summons not using buff icon

                applyto.sendPacket(buff);
            }

            long starttime = Server.getInstance().getCurrentTime();
            //CancelEffectAction cancelAction = new CancelEffectAction(applyto, this, starttime);
            //ScheduledFuture<?> schedule = TimerManager.getInstance().schedule(cancelAction, localDuration);
            applyto.registerEffect(this, starttime, starttime + localDuration, false);
            if (mbuff != null) {
                applyto.getMap().broadcastMessage(applyto, mbuff, false);
            }
            if (sourceid == Corsair.BATTLE_SHIP) {
                applyto.announceBattleshipHp();
            }
        }
    }

    private int calcHPChange(Character applyfrom, boolean primary, int affectedPlayers) {
        int hpchange = 0;
        if (hp != 0) {
            if (!skill) {
                if (primary) {
                    hpchange += alchemistModifyVal(applyfrom, hp, true);
                } else {
                    hpchange += hp;
                }
                if (applyfrom.hasDisease(Disease.ZOMBIFY)) {
                    hpchange /= 2;
                }
            } else { // assumption: this is heal
                float hpHeal = (applyfrom.getCurrentMaxHp() * (float) hp / (100.0f * affectedPlayers));
                hpchange += hpHeal;
                if (applyfrom.hasDisease(Disease.ZOMBIFY)) {
                    hpchange = -hpchange;
                    hpCon = 0;
                }
            }
        }
        if (hpR != 0) {
            hpchange += (int) (applyfrom.getCurrentMaxHp() * hpR) / (applyfrom.hasDisease(Disease.ZOMBIFY) ? 2 : 1);
        }
        if (primary) {
            if (hpCon != 0) {
                hpchange -= hpCon;
            }
        }
        if (isChakra()) {
            hpchange += makeHealHP(getY() / 100.0, applyfrom.getTotalLuk(), 2.3, 3.5);
        } else if (sourceid == SuperGM.HEAL_PLUS_DISPEL) {
            hpchange += applyfrom.getCurrentMaxHp();
        }

        return hpchange;
    }

    private int makeHealHP(double rate, double stat, double lowerfactor, double upperfactor) {
        return (int) ((Math.random() * ((int) (stat * upperfactor * rate) - (int) (stat * lowerfactor * rate) + 1)) + (int) (stat * lowerfactor * rate));
    }

    private int calcMPChange(Character applyfrom, boolean primary) {
        int mpchange = 0;
        if (mp != 0) {
            if (primary) {
                mpchange += alchemistModifyVal(applyfrom, mp, true);
            } else {
                mpchange += mp;
            }
        }
        if (mpR != 0) {
            mpchange += (int) (applyfrom.getCurrentMaxMp() * mpR);
        }
        if (primary) {
            if (mpCon != 0) {
                double mod = 1.0;
                boolean isAFpMage = applyfrom.getJob().isA(Job.FP_MAGE);
                boolean isCygnus = applyfrom.getJob().isA(Job.BLAZEWIZARD2);
                boolean isEvan = applyfrom.getJob().isA(Job.EVAN7);
                if (isAFpMage || isCygnus || isEvan || applyfrom.getJob().isA(Job.IL_MAGE)) {
                    Skill amp = isAFpMage ? SkillFactory.getSkill(FPMage.ELEMENT_AMPLIFICATION) : (isCygnus ? SkillFactory.getSkill(BlazeWizard.ELEMENT_AMPLIFICATION) : (isEvan ? SkillFactory.getSkill(Evan.MAGIC_AMPLIFICATION) : SkillFactory.getSkill(ILMage.ELEMENT_AMPLIFICATION)));
                    int ampLevel = applyfrom.getSkillLevel(amp);
                    if (ampLevel > 0) {
                        mod = amp.getEffect(ampLevel).getX() / 100.0;
                    }
                }
                mpchange -= mpCon * mod;
                if (applyfrom.getBuffedValue(BuffStat.INFINITY) != null) {
                    mpchange = 0;
                } else if (applyfrom.getBuffedValue(BuffStat.CONCENTRATE) != null) {
                    mpchange -= (int) (mpchange * (applyfrom.getBuffedValue(BuffStat.CONCENTRATE).doubleValue() / 100));
                }
            }
        }
        if (sourceid == SuperGM.HEAL_PLUS_DISPEL) {
            mpchange += applyfrom.getCurrentMaxMp();
        }

        return mpchange;
    }

    private int alchemistModifyVal(Character chr, int val, boolean withX) {
        if (!skill && (chr.getJob().isA(Job.HERMIT) || chr.getJob().isA(Job.NIGHTWALKER3))) {
            StatEffect alchemistEffect = getAlchemistEffect(chr);
            if (alchemistEffect != null) {
                return (int) (val * ((withX ? alchemistEffect.getX() : alchemistEffect.getY()) / 100.0));
            }
        }
        return val;
    }

    private StatEffect getAlchemistEffect(Character chr) {
        int id = Hermit.ALCHEMIST;
        if (chr.isCygnus()) {
            id = NightWalker.ALCHEMIST;
        }
        int alchemistLevel = chr.getSkillLevel(SkillFactory.getSkill(id));
        return alchemistLevel == 0 ? null : SkillFactory.getSkill(id).getEffect(alchemistLevel);
    }

    private boolean isGmBuff() {
        switch (sourceid) {
            case Beginner.ECHO_OF_HERO:
            case Noblesse.ECHO_OF_HERO:
            case Legend.ECHO_OF_HERO:
            case Evan.ECHO_OF_HERO:
            case SuperGM.HEAL_PLUS_DISPEL:
            case SuperGM.HASTE:
            case SuperGM.HOLY_SYMBOL:
            case SuperGM.BLESS:
            case SuperGM.RESURRECTION:
            case SuperGM.HYPER_BODY:
                return true;
            default:
                return false;
        }
    }

    private boolean isMonsterBuff() {
        if (!skill) {
            return false;
        }
        switch (sourceid) {
            case Page.THREATEN:
            case FPWizard.SLOW:
            case ILWizard.SLOW:
            case FPMage.SEAL:
            case ILMage.SEAL:
            case Priest.DOOM:
            case Hermit.SHADOW_WEB:
            case NightLord.NINJA_AMBUSH:
            case Shadower.NINJA_AMBUSH:
            case BlazeWizard.SLOW:
            case BlazeWizard.SEAL:
            case NightWalker.SHADOW_WEB:
            case Crusader.ARMOR_CRASH:
            case DragonKnight.POWER_CRASH:
            case WhiteKnight.MAGIC_CRASH:
            case Priest.DISPEL:
            case SuperGM.HEAL_PLUS_DISPEL:
                return true;
        }
        return false;
    }

    private boolean isPartyBuff() {
        if (lt == null || rb == null) {
            return false;
        }
        // wk charges have lt and rb set but are neither player nor monster buffs
        return (sourceid < 1211003 || sourceid > 1211008) && sourceid != Paladin.SWORD_HOLY_CHARGE && sourceid != Paladin.BW_HOLY_CHARGE && sourceid != DawnWarrior.SOUL_CHARGE;
    }

    private boolean isHeal() {
        return sourceid == Cleric.HEAL || sourceid == SuperGM.HEAL_PLUS_DISPEL;
    }

    private boolean isResurrection() {
        return sourceid == Bishop.RESURRECTION || sourceid == GM.RESURRECTION || sourceid == SuperGM.RESURRECTION;
    }

    private boolean isTimeLeap() {
        return sourceid == Buccaneer.TIME_LEAP;
    }

    /**
     * 判断是否为龙血技能效果
     * @return true 如果是龙血技能效果，false 否则
     */
    /**
     * 判断是否为龙血技能效果
     * @return true 如果是龙血技能效果，false 否则
     */
    public boolean isDragonBlood() {
        return skill && sourceid == DragonKnight.DRAGON_BLOOD;
    }

    /**
     * 判断是否为狂暴技能效果
     * @return true 如果是狂暴技能效果，false 否则
     */
    public boolean isBerserk() {
        return skill && sourceid == DarkKnight.BERSERK;
    }

    /**
     * 判断是否为恢复技能效果
     * @return true 如果是恢复技能效果，false 否则
     */
    public boolean isRecovery() {
        return sourceid == Beginner.RECOVERY || sourceid == Noblesse.RECOVERY || sourceid == Legend.RECOVERY || sourceid == Evan.RECOVERY;
    }

    /**
     * 判断是否为地图椅子效果
     * @return true 如果是地图椅子效果，false 否则
     */
    public boolean isMapChair() {
        return sourceid == Beginner.MAP_CHAIR || sourceid == Noblesse.MAP_CHAIR || sourceid == Legend.MAP_CHAIR;
    }

    public static boolean isMapChair(int sourceid) {
        return sourceid == Beginner.MAP_CHAIR || sourceid == Noblesse.MAP_CHAIR || sourceid == Legend.MAP_CHAIR;
    }

    public static boolean isHpMpRecovery(int sourceid) {
        return sourceid == ItemId.RUSSELLONS_PILLS || sourceid == ItemId.SORCERERS_POTION;
    }

    public static boolean isAriantShield(int sourceid) {
        return sourceid == ItemId.ARPQ_SHIELD;
    }

    private boolean isDs() {
        return skill && (sourceid == Rogue.DARK_SIGHT || sourceid == NightWalker.DARK_SIGHT);
    }

    private boolean isWw() {
        return skill && (sourceid == WindArcher.WIND_WALK);
    }

    private boolean isCombo() {
        return skill && (sourceid == Crusader.COMBO || sourceid == DawnWarrior.COMBO);
    }

    private boolean isEnrage() {
        return skill && sourceid == Hero.ENRAGE;
    }

    /**
     * 判断是否为召唤兽（Beholder）效果
     * @return true 如果是召唤兽效果，false 否则
     */
    public boolean isBeholder() {
        return skill && sourceid == DarkKnight.BEHOLDER;
    }

    private boolean isShadowPartner() {
        return skill && (sourceid == Hermit.SHADOW_PARTNER || sourceid == NightWalker.SHADOW_PARTNER);
    }

    private boolean isChakra() {
        return skill && sourceid == ChiefBandit.CHAKRA;
    }

    private boolean isCouponBuff() {
        return ItemId.isRateCoupon(sourceid);
    }

    private boolean isAriantShield() {
        int itemid = sourceid;
        return isAriantShield(itemid);
    }

    private boolean isMysticDoor() {
        return skill && sourceid == Priest.MYSTIC_DOOR;
    }

    /**
     * 判断是否为骑宠效果
     * @return true 如果是骑宠效果，false 否则
     */
    public boolean isMonsterRiding() {
        return skill && (sourceid % 10000000 == 1004 || sourceid == Corsair.BATTLE_SHIP || sourceid == Beginner.SPACESHIP || sourceid == Noblesse.SPACESHIP
                || sourceid == Beginner.YETI_MOUNT1 || sourceid == Beginner.YETI_MOUNT2 || sourceid == Beginner.WITCH_BROOMSTICK || sourceid == Beginner.BALROG_MOUNT
                || sourceid == Noblesse.YETI_MOUNT1 || sourceid == Noblesse.YETI_MOUNT2 || sourceid == Noblesse.WITCH_BROOMSTICK || sourceid == Noblesse.BALROG_MOUNT
                || sourceid == Legend.YETI_MOUNT1 || sourceid == Legend.YETI_MOUNT2 || sourceid == Legend.WITCH_BROOMSTICK || sourceid == Legend.BALROG_MOUNT);
    }

    /**
     * 判断是否为魔法门效果
     * @return true 如果是魔法门效果，false 否则
     */
    public boolean isMagicDoor() {
        return skill && sourceid == Priest.MYSTIC_DOOR;
    }

    /**
     * 判断是否为中毒效果
     * @return true 如果是中毒效果，false 否则
     */
    public boolean isPoison() {
        return skill && (sourceid == FPMage.POISON_MIST || sourceid == FPWizard.POISON_BREATH || sourceid == FPMage.ELEMENT_COMPOSITION || sourceid == NightWalker.POISON_BOMB || sourceid == BlazeWizard.FLAME_GEAR);
    }

    /**
     * 判断是否为变形效果
     * @return true 如果是变形效果，false 否则
     */
    public boolean isMorph() {
        return morphId > 0;
    }

    /**
     * 判断是否为无攻击变形效果
     * @return true 如果是无攻击变形效果，false 否则
     */
    public boolean isMorphWithoutAttack() {
        return morphId > 0 && morphId < 100; // Every morph item I have found has been under 100, pirate skill transforms start at 1000.
    }

    private boolean isMist() {
        return skill && (sourceid == FPMage.POISON_MIST || sourceid == Shadower.SMOKE_SCREEN || sourceid == BlazeWizard.FLAME_GEAR || sourceid == NightWalker.POISON_BOMB || sourceid == Evan.RECOVERY_AURA);
    }

    private boolean isSoulArrow() {
        return skill && (sourceid == Hunter.SOUL_ARROW || sourceid == Crossbowman.SOUL_ARROW || sourceid == WindArcher.SOUL_ARROW);
    }

    private boolean isShadowClaw() {
        return skill && sourceid == NightLord.SHADOW_STARS;
    }

    private boolean isCrash() {
        return skill && (sourceid == DragonKnight.POWER_CRASH || sourceid == Crusader.ARMOR_CRASH || sourceid == WhiteKnight.MAGIC_CRASH);
    }

    private boolean isSeal() {
        return skill && (sourceid == ILMage.SEAL || sourceid == FPMage.SEAL || sourceid == BlazeWizard.SEAL);
    }

    private boolean isDispel() {
        return skill && (sourceid == Priest.DISPEL || sourceid == SuperGM.HEAL_PLUS_DISPEL);
    }

    private boolean isCureAllAbnormalStatus() {
        if (skill) {
            return isHerosWill(sourceid);
        } else {
            return sourceid == ItemId.WHITE_ELIXIR;
        }
    }

    /**
     * 判断技能是否为英雄意志技能
     * @param skillid 技能ID
     * @return true 如果是英雄意志技能，false 否则
     */
    public static boolean isHerosWill(int skillid) {
        switch (skillid) {
            case Hero.HEROS_WILL:
            case Paladin.HEROS_WILL:
            case DarkKnight.HEROS_WILL:
            case FPArchMage.HEROS_WILL:
            case ILArchMage.HEROS_WILL:
            case Bishop.HEROS_WILL:
            case Bowmaster.HEROS_WILL:
            case Marksman.HEROS_WILL:
            case NightLord.HEROS_WILL:
            case Shadower.HEROS_WILL:
            case Buccaneer.PIRATES_RAGE:
            case Aran.HEROS_WILL:
                return true;

            default:
                return false;
        }
    }

    private boolean isWkCharge() {
        if (!skill) {
            return false;
        }

        for (Pair<BuffStat, Integer> p : statups) {
            if (p.getLeft().equals(BuffStat.WK_CHARGE)) {
                return true;
            }
        }

        return false;
    }

    private boolean isDash() {
        return skill && (sourceid == Pirate.DASH || sourceid == ThunderBreaker.DASH || sourceid == Beginner.SPACE_DASH || sourceid == Noblesse.SPACE_DASH);
    }

    private boolean isSkillMorph() {
        return skill && (sourceid == Buccaneer.SUPER_TRANSFORMATION || sourceid == Marauder.TRANSFORMATION || sourceid == WindArcher.EAGLE_EYE || sourceid == ThunderBreaker.TRANSFORMATION);
    }

    private boolean isInfusion() {
        return skill && (sourceid == Buccaneer.SPEED_INFUSION || sourceid == Corsair.SPEED_INFUSION || sourceid == ThunderBreaker.SPEED_INFUSION);
    }

    private boolean isCygnusFA() {
        return skill && (sourceid == DawnWarrior.FINAL_ATTACK || sourceid == WindArcher.FINAL_ATTACK);
    }

    private boolean isHyperBody() {
        return skill && (sourceid == Spearman.HYPER_BODY || sourceid == GM.HYPER_BODY || sourceid == SuperGM.HYPER_BODY);
    }

    private boolean isComboReset() {
        return sourceid == Aran.COMBO_BARRIER || sourceid == Aran.COMBO_DRAIN;
    }

    private int getFatigue() {
        return fatigue;
    }

    private int getMorph() {
        return morphId;
    }

    private int getMorph(Character chr) {
        if (morphId == 1000 || morphId == 1001 || morphId == 1003) { // morph skill
            return chr.getGender() == 0 ? morphId : morphId + 100;
        }
        return morphId;
    }

    private SummonMovementType getSummonMovementType() {
        if (!skill) {
            return null;
        }
        switch (sourceid) {
            case Ranger.PUPPET:
            case Sniper.PUPPET:
            case WindArcher.PUPPET:
            case Outlaw.OCTOPUS:
            case Corsair.WRATH_OF_THE_OCTOPI:
                return SummonMovementType.STATIONARY;
            case Ranger.SILVER_HAWK:
            case Sniper.GOLDEN_EAGLE:
            case Priest.SUMMON_DRAGON:
            case Marksman.FROST_PREY:
            case Bowmaster.PHOENIX:
            case Outlaw.GAVIOTA:
                return SummonMovementType.CIRCLE_FOLLOW;
            case DarkKnight.BEHOLDER:
            case FPArchMage.ELQUINES:
            case ILArchMage.IFRIT:
            case Bishop.BAHAMUT:
            case DawnWarrior.SOUL:
            case BlazeWizard.FLAME:
            case BlazeWizard.IFRIT:
            case WindArcher.STORM:
            case NightWalker.DARKNESS:
            case ThunderBreaker.LIGHTNING:
                return SummonMovementType.FOLLOW;
        }
        return null;
    }

    /**
     * 判断此效果是否为技能效果
     * @return true 如果是技能效果，false 如果是物品效果
     */
    public boolean isSkill() {
        return skill;
    }
    
    /**
     * 获取效果源ID（技能ID或物品ID）
     * @return 效果源ID
     */
    public int getSourceId() {
        return sourceid;
    }
    
    /**
     * 设置效果源ID
     * @param id 新的效果源ID
     */
    public void setSourceId(int id) {
        sourceid = id;
    }
    
    /**
     * 获取增益效果源ID（如果是技能则返回正数，如果是物品则返回负数）
     * @return 增益效果源ID
     */
    public int getBuffSourceId() {
        return skill ? sourceid : -sourceid;
    }

    /**
     * 根据概率判断是否触发效果
     * @return true 如果效果触发，false 如果未触发
     */
    public boolean makeChanceResult() {
        return prop == 1.0 || Math.random() < prop;
    }

    /**
     * 判断是否为持续效果
     * @return true 如果是持续效果，false 否则
     */
    public boolean isOverTime() {
        return overTime;
    }

    /*
     private static class CancelEffectAction implements Runnable {

     private StatEffect effect;
     private WeakReference<Character> target;
     private long startTime;

     public CancelEffectAction(Character target, StatEffect effect, long startTime) {
     this.effect = effect;
     this.target = new WeakReference<>(target);
     this.startTime = startTime;
     }

     @Override
     public void run() {
     Character realTarget = target.get();
     if (realTarget != null) {
     realTarget.cancelEffect(effect, false, startTime);
     }
     }
     }
     */
    /**
     * 获取HP增益值
     * @return HP增益值
     */
    public short getHp() {
        return hp;
    }

    /**
     * 获取MP增益值
     * @return MP增益值
     */
    public short getMp() {
        return mp;
    }

    /**
     * 获取HP恢复比率
     * @return HP恢复比率
     */
    public double getHpRate() {
        return hpR;
    }

    /**
     * 获取MP恢复比率
     * @return MP恢复比率
     */
    public double getMpRate() {
        return mpR;
    }

    /**
     * 获取HP恢复值
     * @return HP恢复值
     */
    public byte getHpR() {
        return mhpR;
    }

    /**
     * 获取MP恢复值
     * @return MP恢复值
     */
    public byte getMpR() {
        return mmpR;
    }

    /**
     * 获取HP恢复速率
     * @return HP恢复速率
     */
    public short getHpRRate() {
        return mhpRRate;
    }

    /**
     * 获取MP恢复速率
     * @return MP恢复速率
     */
    public short getMpRRate() {
        return mmpRRate;
    }

    /**
     * 获取HP消耗值
     * @return HP消耗值
     */
    public short getHpCon() {
        return hpCon;
    }

    /**
     * 获取MP消耗值
     * @return MP消耗值
     */
    public short getMpCon() {
        return mpCon;
    }

    /**
     * 获取魔法攻击力增益值
     * @return 魔法攻击力增益值
     */
    public short getMatk() {
        return matk;
    }

    /**
     * 获取物理攻击力增益值
     * @return 物理攻击力增益值
     */
    public short getWatk() {
        return watk;
    }

    /**
     * 获取效果持续时间
     * @return 效果持续时间（毫秒）
     */
    public int getDuration() {
        return duration;
    }

    /**
     * 获取增益状态列表
     * @return 增益状态列表
     */
    public List<Pair<BuffStat, Integer>> getStatups() {
        return statups;
    }

    /**
     * 判断两个效果是否来自同一个源
     * @param effect 要比较的效果
     * @return true 如果两个效果来自同一个源，false 否则
     */
    public boolean sameSource(StatEffect effect) {
        return this.sourceid == effect.sourceid && this.skill == effect.skill;
    }

    /**
     * 获取效果参数X值
     * @return 效果参数X值
     */
    public int getX() {
        return x;
    }

    /**
     * 获取效果参数Y值
     * @return 效果参数Y值
     */
    public int getY() {
        return y;
    }

    /**
     * 获取伤害百分比
     * @return 伤害百分比
     */
    public int getDamage() {
        return damage;
    }

    /**
     * 获取攻击次数
     * @return 攻击次数
     */
    public int getAttackCount() {
        return attackCount;
    }

    /**
     * 获取可攻击怪物数量
     * @return 可攻击怪物数量
     */
    public int getMobCount() {
        return mobCount;
    }

    /**
     * 获取固定伤害值
     * @return 固定伤害值
     */
    public int getFixDamage() {
        return fixdamage;
    }

    /**
     * 获取子弹数量限制
     * @return 子弹数量限制
     */
    public short getBulletCount() {
        return bulletCount;
    }

    /**
     * 获取子弹消耗数量
     * @return 子弹消耗数量
     */
    public short getBulletConsume() {
        return bulletConsume;
    }

    /**
     * 获取金钱消耗
     * @return 金钱消耗
     */
    public int getMoneyCon() {
        return moneyCon;
    }

    /**
     * 获取冷却时间
     * @return 冷却时间（毫秒）
     */
    public int getCooldown() {
        return cooldown;
    }

    /**
     * 获取怪物状态效果映射
     * @return 怪物状态效果映射
     */
    public Map<MonsterStatus, Integer> getMonsterStati() {
        return monsterStatus;
    }
}