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
        /** 未定义 */
        UNDEFINED(-1),
        /** 经验奖励 */
        EXP(0),
        /** 物品奖励 */
        ITEM(1),
        /** 跳转下一个任务 */
        NEXTQUEST(2),
        /** 金币奖励 */
        MESO(3),
        /** 任务相关 */
        QUEST(4),
        /** 技能奖励 */
        SKILL(5),
        /** 声望奖励 */
        FAME(6),
        /** BUFF奖励 */
        BUFF(7),
        /** 宠物技能 */
        PETSKILL(8),
        /** 是 */
        YES(9),
        /** 否 */
        NO(10),
        /** NPC相关 */
        NPC(11),
        /** 最低等级 */
        MIN_LEVEL(12),
        /** 普通自动开始 */
        NORMAL_AUTO_START(13),
        /** 宠物亲密度 */
        PETTAMENESS(14),
        /** 宠物速度 */
        PETSPEED(15),
        /** 信息 */
        INFO(16),
        /** 零 */
        ZERO(16);

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
         * <p>该方法将WZ资源文件中定义的动作名称映射到对应的枚举值，
         * 用于任务奖励和动作的解析处理。</p>
         *
         * @param name WZ文件中的动作名称
         * @return 对应的动作类型，未匹配返回UNDEFINED
         */
        public static QuestActionType getByWZName(String name) {
        // 将WZ文件中的动作名称映射到对应的枚举值
        switch (name) {
        // 【经验奖励】任务完成后给予的经验值奖励
        case "exp":
            return EXP;
        // 【金币奖励】任务完成后给予的金币奖励
        case "money":
            return MESO;
        // 【物品奖励】任务完成后给予的物品奖励
        case "item":
            return ITEM;
        // 【技能奖励】任务完成后给予的技能学习机会
        case "skill":
            return SKILL;
        // 【跳转任务】任务完成后自动接取下一个任务
        case "nextQuest":
            return NEXTQUEST;
        // 【声望奖励】任务完成后给予的知名度/人气值奖励
        case "pop":
            return FAME;
        // 【BUFF奖励】任务完成后给予的增益状态（如药水效果）
        case "buffItemID":
            return BUFF;
        // 【宠物技能】任务完成后解锁的宠物技能
        case "petskill":
            return PETSKILL;
        // 【否定选项】任务对话中的"否"选项
        case "no":
            return NO;
        // 【肯定选项】任务对话中的"是"选项
        case "yes":
            return YES;
        // 【NPC交互】任务中涉及的NPC对话或操作
        case "npc":
            return NPC;
        // 【最低等级限制】任务接取的最低等级要求
        case "lvmin":
            return MIN_LEVEL;
        // 【普通自动开始】任务设置为自动开始模式
        case "normalAutoStart":
            return NORMAL_AUTO_START;
        // 【宠物亲密度提升】任务完成后宠物亲密度增加
        case "pettameness":
            return PETTAMENESS;
        // 【宠物速度提升】任务完成后宠物移动速度提升
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