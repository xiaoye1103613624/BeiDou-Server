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
 * 任务动作类型枚举
 * 定义任务完成后的奖励类型，如经验、物品、金币、技能、声望等
 * 支持从WZ名称直接映射
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
    /** 下一个任务 */
    NEXTQUEST(2),
    /** 金币奖励 */
    MESO(3),
    /** 任务奖励 */
    QUEST(4),
    /** 技能奖励 */
    SKILL(5),
    /** 声望奖励 */
    FAME(6),
    /** Buff奖励 */
    BUFF(7),
    /** 宠物技能奖励 */
    PETSKILL(8),
    /** 是 */
    YES(9),
    /** 否 */
    NO(10),
    /** NPC */
    NPC(11),
    /** 最低等级 */
    MIN_LEVEL(12),
    /** 普通自动启动 */
    NORMAL_AUTO_START(13),
    /** 宠物亲密度 */
    PETTAMENESS(14),
    /** 宠物速度 */
    PETSPEED(15),
    /** 信息 */
    INFO(16),
    /** 零 */
    ZERO(16);

    final byte type;

    QuestActionType(int type) {
        this.type = (byte) type;
    }

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