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
        UNDEFINED(-1),       // 未定义
        JOB(0),              // 职业限制
        ITEM(1),             // 物品需求
        QUEST(2),            // 前置任务
        MIN_LEVEL(3),        // 最低等级
        MAX_LEVEL(4),        // 最高等级
        END_DATE(5),         // 截止日期
        MOB(6),              // 击杀怪物
        NPC(7),              // NPC限制
        FIELD_ENTER(8),      // 进入地图
        INTERVAL(9),         // 重复间隔
        SCRIPT(10),          // 脚本条件
        PET(11),             // 宠物需求
        MIN_PET_TAMENESS(12), // 宠物亲密度最低
        MONSTER_BOOK(13),    // 怪物图鉴
        NORMAL_AUTO_START(14), // 普通自动开始
        INFO_NUMBER(15),     // 信息编号
        INFO_EX(16),         // 扩展信息
        COMPLETED_QUEST(17), // 已完成任务
        START(18),           // 开始
        END(19),             // 结束
        DAY_BY_DAY(20),      // 每日任务
        MESO(21),            // 金币需求
        BUFF(22),            // BUFF需求
        EXCEPT_BUFF(23);     // 排除BUFF

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
        public static QuestRequirementType getByWZName(String name) {
        switch (name) {
        case "job":
            return JOB;
        case "quest":
            return QUEST;
        case "item":
            return ITEM;
        case "lvmin":
            return MIN_LEVEL;
        case "lvmax":
            return MAX_LEVEL;
        case "end":
            return END_DATE;
        case "mob":
            return MOB;
        case "npc":
            return NPC;
        case "fieldEnter":
            return FIELD_ENTER;
        case "interval":
            return INTERVAL;
        case "startscript":
            return SCRIPT;
        case "endscript":
            return SCRIPT;
        case "pet":
            return PET;
        case "pettamenessmin":
            return MIN_PET_TAMENESS;
        case "mbmin":
            return MONSTER_BOOK;
        case "normalAutoStart":
            return NORMAL_AUTO_START;
        case "infoNumber":
            return INFO_NUMBER;
        case "infoex":
            return INFO_EX;
        case "questComplete":
            return COMPLETED_QUEST;
        case "start":
            return START;
	/* case "end":already coded
            return END;*/
        case "daybyday":
            return DAY_BY_DAY;
        case "money":
            return MESO;
        case "buff":
            return BUFF;
        case "exceptbuff":
            return EXCEPT_BUFF;
        default:
            return UNDEFINED;
        }
    }
}