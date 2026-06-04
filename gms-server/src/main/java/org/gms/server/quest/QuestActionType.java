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
     * 【枚举】QuestActionType，包 {@code org.gms.server.quest}。
     * 任务动作类型枚举，定义任务接取或完成时执行的动作类型。
     *
     * @author Matze
     */
    public enum QuestActionType {
        UNDEFINED(-1),      // 未定义
        EXP(0),             // 经验奖励
        ITEM(1),            // 物品奖励
        NEXTQUEST(2),       // 跳转下一个任务
        MESO(3),            // 金币奖励
        QUEST(4),           // 任务相关
        SKILL(5),           // 技能奖励
        FAME(6),            // 声望奖励
        BUFF(7),            // BUFF奖励
        PETSKILL(8),        // 宠物技能
        YES(9),             // 是
        NO(10),             // 否
        NPC(11),            // NPC相关
        MIN_LEVEL(12),      // 最低等级
        NORMAL_AUTO_START(13), // 普通自动开始
        PETTAMENESS(14),    // 宠物亲密度
        PETSPEED(15),       // 宠物速度
        INFO(16),           // 信息
        ZERO(16);           // 零

        /** 类型编码 */
        final byte type;

        /**
         * 构造函数
         *
         * @param type 类型编码
         */
        QuestActionType(int type) {
            this.type = (byte) type;
        }

        /**
         * 根据WZ文件中的名称获取对应的动作类型
         *
         * @param name WZ文件中的动作名称
         * @return 对应的动作类型，未匹配返回UNDEFINED
         */
        public static QuestActionType getByWZName(String name) {
        switch (name) {
        case "exp":
            return EXP;
        case "money":
            return MESO;
        case "item":
            return ITEM;
        case "skill":
            return SKILL;
        case "nextQuest":
            return NEXTQUEST;
        case "pop":
            return FAME;
        case "buffItemID":
            return BUFF;
        case "petskill":
            return PETSKILL;
        case "no":
            return NO;
        case "yes":
            return YES;
        case "npc":
            return NPC;
        case "lvmin":
            return MIN_LEVEL;
        case "normalAutoStart":
            return NORMAL_AUTO_START;
        case "pettameness":
            return PETTAMENESS;
        case "petspeed":
            return PETSPEED;
        case "info":
            return INFO;
        case "0":
            return ZERO;
        default:
            return UNDEFINED;
        }
    }
}