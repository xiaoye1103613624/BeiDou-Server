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
package org.gms.client;

import lombok.Getter;
import org.gms.util.I18nUtil;


/**
 * 【枚举】Job（enum），包 {@code org.gms.client}。
 * 定义MapleStory全部职业，包含冒险家、骑士团、战神、龙神等所有转职分支。
 *
 * <p>该枚举涵盖了游戏中所有的职业类型，每种职业都有唯一的ID和对应的显示名称。
 * 职业按照职业大类进行分组，便于职业判断和技能系统的处理。</p>
 */
public enum Job {
    /** 初心者，所有冒险者的起始职业 */
    BEGINNER(0, I18nUtil.getMessage("job.name.0")),

    /** 战士系 */
    WARRIOR(100, I18nUtil.getMessage("job.name.100")),
    /** 战士一转职 - 剑士 */
    FIGHTER(110, I18nUtil.getMessage("job.name.110")),
    /** 战士二转职 - 骑士 */
    CRUSADER(111, I18nUtil.getMessage("job.name.111")),
    /** 战士三转职 - 英雄 */
    HERO(112, I18nUtil.getMessage("job.name.112")),
    /** 战士一转职 - 准骑士 */
    PAGE(120, I18nUtil.getMessage("job.name.120")),
    /** 战士二转职 - 骑士 */
    WHITEKNIGHT(121, I18nUtil.getMessage("job.name.121")),
    /** 战士三转职 - 圣骑士 */
    PALADIN(122, I18nUtil.getMessage("job.name.122")),
    /** 战士一转职 - 枪骑士 */
    SPEARMAN(130, I18nUtil.getMessage("job.name.130")),
    /** 战士二转职 - 龙骑士 */
    DRAGONKNIGHT(131, I18nUtil.getMessage("job.name.131")),
    /** 战士三转职 - 黑骑士 */
    DARKKNIGHT(132, I18nUtil.getMessage("job.name.132")),

    /** 法师系 */
    MAGICIAN(200, I18nUtil.getMessage("job.name.200")),
    /** 法师一转职 - 火毒法师（火系） */
    FP_WIZARD(210, I18nUtil.getMessage("job.name.210")),
    /** 法师二转职 - 火毒师（火系） */
    FP_MAGE(211, I18nUtil.getMessage("job.name.211")),
    /** 法师三转职 - 火毒魔导师（火系） */
    FP_ARCHMAGE(212, I18nUtil.getMessage("job.name.212")),
    /** 法师一转职 - 冰雷法师（雷系） */
    IL_WIZARD(220, I18nUtil.getMessage("job.name.220")),
    /** 法师二转职 - 冰雷师（雷系） */
    IL_MAGE(221, I18nUtil.getMessage("job.name.221")),
    /** 法师三转职 - 冰雷魔导师（雷系） */
    IL_ARCHMAGE(222, I18nUtil.getMessage("job.name.222")),
    /** 法师一转职 - 牧师 */
    CLERIC(230, I18nUtil.getMessage("job.name.230")),
    /** 法师二转职 - 主教 */
    PRIEST(231, I18nUtil.getMessage("job.name.231")),
    /** 法师三转职 - 大主教 */
    BISHOP(232, I18nUtil.getMessage("job.name.232")),

    /** 弓箭手系 */
    BOWMAN(300, I18nUtil.getMessage("job.name.300")),
    /** 弓箭手一转职 - 猎人 */
    HUNTER(310, I18nUtil.getMessage("job.name.310")),
    /** 弓箭手二转职 - 射手 */
    RANGER(311, I18nUtil.getMessage("job.name.311")),
    /** 弓箭手三转职 - 神射手 */
    BOWMASTER(312, I18nUtil.getMessage("job.name.312")),
    /** 弓箭手一转职 - 弩弓手 */
    CROSSBOWMAN(320, I18nUtil.getMessage("job.name.320")),
    /** 弓箭手二转职 - 狙击手 */
    SNIPER(321, I18nUtil.getMessage("job.name.321")),
    /** 弓箭手三转职 - 箭神 */
    MARKSMAN(322, I18nUtil.getMessage("job.name.322")),

    /** 飞侠系 */
    THIEF(400, I18nUtil.getMessage("job.name.400")),
    /** 飞侠一转职 - 刺客 */
    ASSASSIN(410,I18nUtil.getMessage("job.name.410")),
    /** 飞侠二转职 - 隐士 */
    HERMIT(411, I18nUtil.getMessage("job.name.411")),
    /** 飞侠三转职 - 夜行者 */
    NIGHTLORD(412, I18nUtil.getMessage("job.name.412")),
    /** 飞侠一转职 - 独行客 */
    BANDIT(420, I18nUtil.getMessage("job.name.420")),
    /** 飞侠二转职 - 大盗 */
    CHIEFBANDIT(421, I18nUtil.getMessage("job.name.421")),
    /** 飞侠三转职 - 侠盗 */
    SHADOWER(422, I18nUtil.getMessage("job.name.422")),

    /** 海盗系 */
    PIRATE(500, I18nUtil.getMessage("job.name.500")),
    /** 海盗一转职 - 拳手 */
    BRAWLER(510, I18nUtil.getMessage("job.name.510")),
    /** 海盗二转职 - 斗士 */
    MARAUDER(511, I18nUtil.getMessage("job.name.511")),
    /** 海盗三转职 - 冲锋队长 */
    BUCCANEER(512, I18nUtil.getMessage("job.name.512")),
    /** 海盗一转职 - 枪手 */
    GUNSLINGER(520, I18nUtil.getMessage("job.name.520")),
    /** 海盗二转职 - 大亨 */
    OUTLAW(521, I18nUtil.getMessage("job.name.521")),
    /** 海盗三转职 - 航海王 */
    CORSAIR(522, I18nUtil.getMessage("job.name.522")),

    /** 枫叶勇士（特殊职业） */
    MAPLELEAF_BRIGADIER(800, I18nUtil.getMessage("job.name.800")),

    /** 游戏管理员 */
    GM(900, I18nUtil.getMessage("job.name.900")),
    /** 超级游戏管理员 */
    SUPERGM(910, I18nUtil.getMessage("job.name.910")),

    /** 骑士团（新冒险家）- 初心者 */
    NOBLESSE(1000, I18nUtil.getMessage("job.name.1000")),
    /** 骑士团 - 战士一转职 光明骑士 */
    DAWNWARRIOR1(1100, I18nUtil.getMessage("job.name.1100")),
    DAWNWARRIOR2(1110, I18nUtil.getMessage("job.name.1110")),
    DAWNWARRIOR3(1111, I18nUtil.getMessage("job.name.1111")),
    DAWNWARRIOR4(1112, I18nUtil.getMessage("job.name.1112")),
    /** 骑士团 - 法师一转职 烈火骑士 */
    BLAZEWIZARD1(1200, I18nUtil.getMessage("job.name.1200")),
    BLAZEWIZARD2(1210, I18nUtil.getMessage("job.name.1210")),
    BLAZEWIZARD3(1211,I18nUtil.getMessage("job.name.1211")),
    BLAZEWIZARD4(1212,I18nUtil.getMessage("job.name.1212")),
    /** 骑士团 - 弓箭手一转职 疾风骑士 */
    WINDARCHER1(1300,I18nUtil.getMessage("job.name.1300")),
    WINDARCHER2(1310, I18nUtil.getMessage("job.name.1310")),
    WINDARCHER3(1311, I18nUtil.getMessage("job.name.1311")),
    WINDARCHER4(1312, I18nUtil.getMessage("job.name.1312")),
    /** 骑士团 - 飞侠一转职 暗夜行者 */
    NIGHTWALKER1(1400,I18nUtil.getMessage("job.name.1400")),
    NIGHTWALKER2(1410,I18nUtil.getMessage("job.name.1410")),
    NIGHTWALKER3(1411,I18nUtil.getMessage("job.name.1411")),
    NIGHTWALKER4(1412,I18nUtil.getMessage("job.name.1412")),
    /** 骑士团 - 海盗一转职 雷霆骑士 */
    THUNDERBREAKER1(1500,I18nUtil.getMessage("job.name.1500")),
    THUNDERBREAKER2(1510,I18nUtil.getMessage("job.name.1510")),
    THUNDERBREAKER3(1511,I18nUtil.getMessage("job.name.1511")),
    THUNDERBREAKER4(1512,I18nUtil.getMessage("job.name.1512")),

    /** 传说英雄（新手二转） */
    LEGEND(2000, I18nUtil.getMessage("job.name.2000")),
    /** 龙神 */
    EVAN(2001, I18nUtil.getMessage("job.name.2001")),
    /** 战神一转职 */
    ARAN1(2100, I18nUtil.getMessage("job.name.2100")),
    ARAN2(2110, I18nUtil.getMessage("job.name.2110")),
    ARAN3(2111, I18nUtil.getMessage("job.name.2111")),
    ARAN4(2112, I18nUtil.getMessage("job.name.2112")),

    /** 龙神转职（龙魔导师） */
    EVAN1(2200,I18nUtil.getMessage("job.name.2200")),
    EVAN2(2210, I18nUtil.getMessage("job.name.2210")),
    EVAN3(2211, I18nUtil.getMessage("job.name.2211")),
    EVAN4(2212, I18nUtil.getMessage("job.name.2212")),
    EVAN5(2213, I18nUtil.getMessage("job.name.2213")),
    EVAN6(2214, I18nUtil.getMessage("job.name.2214")),
    EVAN7(2215, I18nUtil.getMessage("job.name.2215")),
    EVAN8(2216, I18nUtil.getMessage("job.name.2216")),
    EVAN9(2217, I18nUtil.getMessage("job.name.2217")),
    EVAN10(2218, I18nUtil.getMessage("job.name.2218"));

    /** 职业编码ID */
    @Getter
    private final int id;
    /** 职业显示名称（i18n国际化） */
    @Getter
    private final String name;

    /** 最大职业ID，用于职业数量限制校验 */
    final static int maxId = 22;    // maxId = (EVAN / 100);

    /**
     * 构造函数，初始化职业ID和名称
     * @param id 职业编码ID
     * @param name 职业显示名称（支持i18n）
     */
    Job(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * 获取最大职业ID
     * @return 最大职业ID值
     */
    public static int getMax() {
        return maxId;
    }

    /**
     * 根据职业ID获取对应的Job枚举
     * @param id 职业编码ID
     * @return 对应的Job枚举，未找到则返回BEGINNER
     */
    public static Job getById(int id) {
        // 遍历所有职业枚举值，查找匹配的ID
        for (Job l : Job.values()) {
            if (l.getId() == id) {
                return l;
            }
        }
        // 未找到匹配的职业，返回初心者
        return BEGINNER;
    }

    /**
     * 根据5字节编码获取对应的职业
     * <p>该方法用于处理某些特殊场景下的职业编码映射，
     * 例如装备需求、任务条件等。</p>
     * @param encoded 编码值
     * @return 对应的职业枚举
     */
    public static Job getBy5ByteEncoding(int encoded) {
        return switch (encoded) {
            // 【战士编码】值2表示战士职业
            case 2 -> WARRIOR;
            // 【法师编码】值4表示法师职业
            case 4 -> MAGICIAN;
            // 【弓箭手编码】值8表示弓箭手职业
            case 8 -> BOWMAN;
            // 【飞侠编码】值16表示飞侠职业
            case 16 -> THIEF;
            // 【海盗编码】值32表示海盗职业
            case 32 -> PIRATE;
            // 【骑士团初心者编码】值1024表示骑士团初心者
            case 1024 -> NOBLESSE;
            // 【光明骑士编码】值2048表示骑士团战士一转职
            case 2048 -> DAWNWARRIOR1;
            // 【烈火骑士编码】值4096表示骑士团法师一转职
            case 4096 -> BLAZEWIZARD1;
            // 【疾风骑士编码】值8192表示骑士团弓箭手一转职
            case 8192 -> WINDARCHER1;
            // 【暗夜行者编码】值16384表示骑士团飞侠一转职
            case 16384 -> NIGHTWALKER1;
            // 【雷霆骑士编码】值32768表示骑士团海盗一转职
            case 32768 -> THUNDERBREAKER1;
            // 【默认情况】无法识别的编码返回初心者
            default -> BEGINNER;
        };
    }

    /**
     * 判断当前职业是否属于指定职业的分支
     * <p>用于判断角色是否已经转职为指定职业或更高一阶的职业。
     * 例如：CRUSADER.isA(WARRIOR) 返回true，因为骑士是战士的进阶职业。</p>
     * @param basejob 基准职业
     * @return true 如果当前职业是基准职业的分支（包括基准职业本身）
     */
    public boolean isA(Job basejob) {  // thanks Steve (kaito1410) for pointing out an improvement here
        // 计算基准职业的分支值（去掉个位数字）
        int basebranch = basejob.getId() / 10;
        // 判断逻辑：当前职业ID除以10等于基准分支值且ID大于等于基准职业ID
        // 或者基准分支是个位数（基准职业本身是一转职业），且当前职业的百位数与基准职业相同
        return (getId() / 10 == basebranch && getId() >= basejob.getId()) || (basebranch % 10 == 0 && getId() / 100 == basejob.getId() / 100);
    }

    /**
     * 获取职业大类编号
     * <p>职业大类编号用于区分不同系的职业：
     * 0-初心者，1-战士，2-法师，3-弓箭手，4-飞侠，5-海盗等。</p>
     * @return 职业大类编号
     */
    public int getJobNiche() {
        // 通过除以100取模得到职业大类编号
        return (id / 100) % 10;

        // 职业大类编号对照表：
        // case 0: BEGINNER;
        // case 1: WARRIOR;
        // case 2: MAGICIAN;
        // case 3: BOWMAN;
        // case 4: THIEF;
        // case 5: PIRATE;
    }

    /**
     * 获取职业的战斗风格类型
     * <p>该方法用于确定角色适用的武器类型和战斗方式，
     * 例如战士类使用近战武器，法师类使用魔法等。</p>
     * @param jobid 职业ID
     * @param opt 战斗选项标识（用于区分同系职业的不同分支）
     * @return 对应的战斗风格职业
     */
    public static Job getJobStyleInternal(int jobid, byte opt) {
        // 计算职业大类编号（百位数）
        int jobtype = jobid / 100;

        // 根据职业大类编号判断战斗风格
        // 【战士风格】包括普通战士、骑士团战士、战神
        if (jobtype == WARRIOR.getId() / 100 || jobtype == DAWNWARRIOR1.getId() / 100 || jobtype == ARAN1.getId() / 100) {
            return WARRIOR;
        }
        // 【法师风格】包括普通法师、骑士团法师、龙神法师
        else if (jobtype == MAGICIAN.getId() / 100 || jobtype == BLAZEWIZARD1.getId() / 100 || jobtype == EVAN1.getId() / 100) {
            return MAGICIAN;
        }
        // 【弓箭手风格】包括普通弓箭手、骑士团弓箭手
        else if (jobtype == BOWMAN.getId() / 100 || jobtype == WINDARCHER1.getId() / 100) {
            // 进一步区分弩弓手和弓箭手
            // 通过判断是否是弩弓手转职分支（id/10等于弩弓手的id/10）
            if (jobid / 10 == CROSSBOWMAN.getId() / 10) {
                return CROSSBOWMAN;
            } else {
                return BOWMAN;
            }
        }
        // 【飞侠风格】包括普通飞侠、骑士团飞侠
        else if (jobtype == THIEF.getId() / 100 || jobtype == NIGHTWALKER1.getId() / 100) {
            return THIEF;
        }
        // 【海盗风格】包括普通海盗、骑士团海盗
        else if (jobtype == PIRATE.getId() / 100 || jobtype == THUNDERBREAKER1.getId() / 100) {
            // 根据战斗选项区分拳手和枪手
            // opt为0x80表示拳手分支
            if (opt == (byte) 0x80) {
                return BRAWLER;
            } else {
                return GUNSLINGER;
            }
        }

        // 【默认情况】无法识别的职业返回初心者
        return BEGINNER;
    }
}