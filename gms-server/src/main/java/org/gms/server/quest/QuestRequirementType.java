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
package org.gms.server.quest;

/**
     * 【枚举】QuestRequirementType，包 {@code org.gms.server.quest}。
     * 任务条件类型枚举，定义接取或完成任务所需满足的条件类型。
     *
     * <p>该枚举包含了游戏中任务系统可能涉及的所有前置条件类型，
     * 用于控制任务的接取、完成条件，如职业限制、等级要求、物品需求等。</p>
     *
     * @author Matze
     */
    public enum QuestRequirementType {
        /** 未定义 */
        UNDEFINED(-1),
        /** 职业限制 */
        JOB(0),
        /** 物品需求 */
        ITEM(1),
        /** 前置任务 */
        QUEST(2),
        /** 最低等级 */
        MIN_LEVEL(3),
        /** 最高等级 */
        MAX_LEVEL(4),
        /** 截止日期 */
        END_DATE(5),
        /** 击杀怪物 */
        MOB(6),
        /** NPC限制 */
        NPC(7),
        /** 进入地图 */
        FIELD_ENTER(8),
        /** 重复间隔 */
        INTERVAL(9),
        /** 脚本条件 */
        SCRIPT(10),
        /** 宠物需求 */
        PET(11),
        /** 宠物亲密度最低 */
        MIN_PET_TAMENESS(12),
        /** 怪物图鉴 */
        MONSTER_BOOK(13),
        /** 普通自动开始 */
        NORMAL_AUTO_START(14),
        /** 信息编号 */
        INFO_NUMBER(15),
        /** 扩展信息 */
        INFO_EX(16),
        /** 已完成任务 */
        COMPLETED_QUEST(17),
        /** 开始 */
        START(18),
        /** 结束 */
        END(19),
        /** 每日任务 */
        DAY_BY_DAY(20),
        /** 金币需求 */
        MESO(21),
        /** BUFF需求 */
        BUFF(22),
        /** 排除BUFF */
        EXCEPT_BUFF(23);

        /** 类型编码：用于标识任务条件类型的字节值 */
        final byte type;

        /**
         * 构造函数：初始化任务条件类型。
         *
         * @param type 类型编码
         */
        QuestRequirementType(int type) {
            this.type = (byte) type;
        }

        /**
         * 获取类型编码。
         *
         * @return 类型编码
         */
        public byte getType() {
            return type;
        }

        /**
         * 根据WZ文件中的名称获取对应的条件类型。
         *
         * <p>WZ文件是MapleStory游戏资源文件，该方法用于将WZ文件中定义的
         * 条件名称映射到对应的枚举值，便于任务配置的加载和解析。</p>
         *
         * @param name WZ文件中的条件名称
         * @return 对应的条件类型，未匹配返回UNDEFINED
         */
        /**
         * 根据WZ文件中的名称获取对应的条件类型。
         *
         * <p>WZ文件是MapleStory游戏资源文件，该方法用于将WZ文件中定义的
         * 条件名称映射到对应的枚举值，便于任务配置的加载和解析。</p>
         *
         * @param name WZ文件中的条件名称
         * @return 对应的条件类型，未匹配返回UNDEFINED
         */
        public static QuestRequirementType getByWZName(String name) {
        // 将WZ文件中的条件名称映射到对应的枚举值
        switch (name) {
        // 【职业限制】要求玩家必须达到特定职业才能接取/完成任务
        case "job":
            return JOB;
        // 【前置任务】要求玩家必须先完成指定任务才能接取/完成当前任务
        case "quest":
            return QUEST;
        // 【物品需求】要求玩家背包中必须拥有指定物品
        case "item":
            return ITEM;
        // 【最低等级】要求玩家等级必须达到指定值以上
        case "lvmin":
            return MIN_LEVEL;
        // 【最高等级】要求玩家等级不能超过指定值
        case "lvmax":
            return MAX_LEVEL;
        // 【截止日期】任务必须在指定日期前完成
        case "end":
            return END_DATE;
        // 【击杀怪物】要求玩家必须击杀指定数量的怪物
        case "mob":
            return MOB;
        // 【NPC交互】要求玩家必须与指定NPC对话
        case "npc":
            return NPC;
        // 【进入地图】要求玩家必须进入指定地图
        case "fieldEnter":
            return FIELD_ENTER;
        // 【重复间隔】限制任务的重复接取间隔时间
        case "interval":
            return INTERVAL;
        // 【开始脚本】任务开始时触发的脚本条件
        case "startscript":
            return SCRIPT;
        // 【结束脚本】任务完成时触发的脚本条件
        case "endscript":
            return SCRIPT;
        // 【宠物需求】要求玩家必须拥有宠物
        case "pet":
            return PET;
        // 【宠物亲密度最低】要求宠物亲密度必须达到指定值
        case "pettamenessmin":
            return MIN_PET_TAMENESS;
        // 【怪物图鉴】要求玩家怪物图鉴收集达到指定数量
        case "mbmin":
            return MONSTER_BOOK;
        // 【普通自动开始】任务设置为自动开始
        case "normalAutoStart":
            return NORMAL_AUTO_START;
        // 【信息编号】存储或读取任务的特定编号信息
        case "infoNumber":
            return INFO_NUMBER;
        // 【扩展信息】存储或读取任务的扩展配置信息
        case "infoex":
            return INFO_EX;
        // 【已完成任务】要求玩家必须已完成指定任务
        case "questComplete":
            return COMPLETED_QUEST;
        // 【开始条件】标记任务开始的条件类型
        case "start":
            return START;
        // 【结束条件】已在上方"end" case中处理
	/* case "end":already coded
            return END;*/
        // 【每日任务】每天重置的任务
        case "daybyday":
            return DAY_BY_DAY;
        // 【金币需求】要求玩家拥有指定数量的金币
        case "money":
            return MESO;
        // 【BUFF需求】要求玩家身上必须存在指定BUFF状态
        case "buff":
            return BUFF;
        // 【排除BUFF】要求玩家身上不能存在指定BUFF状态
        case "exceptbuff":
            return EXCEPT_BUFF;
        // 【未匹配】如果WZ中的名称不在上述列表中，返回未定义类型
        default:
            return UNDEFINED;
        }
    }
}