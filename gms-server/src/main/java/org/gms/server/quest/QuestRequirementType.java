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
 * 任务需求类型枚举
 * 定义任务的各种前置条件类型，如等级、职业、物品、怪物、Buff等
 *
 * @author Matze
 */
public enum QuestRequirementType {
    /** 未定义 */
    UNDEFINED(-1),
    /** 职业要求 */
    JOB(0),
    /** 物品要求 */
    ITEM(1),
    /** 前置任务要求 */
    QUEST(2),
    /** 最低等级要求 */
    MIN_LEVEL(3),
    /** 最高等级要求 */
    MAX_LEVEL(4),
    /** 结束日期要求 */
    END_DATE(5),
    /** 击杀怪物要求 */
    MOB(6),
    /** NPC要求 */
    NPC(7),
    /** 进入区域要求 */
    FIELD_ENTER(8),
    /** 间隔时间要求 */
    INTERVAL(9),
    /** 脚本要求 */
    SCRIPT(10),
    /** 宠物要求 */
    PET(11),
    /** 最低宠物亲密度要求 */
    MIN_PET_TAMENESS(12),
    /** 怪物手册要求 */
    MONSTER_BOOK(13),
    /** 普通自动启动 */
    NORMAL_AUTO_START(14),
    /** 信息数字要求 */
    INFO_NUMBER(15),
    /** 信息扩展要求 */
    INFO_EX(16),
    /** 已完成任务要求 */
    COMPLETED_QUEST(17),
    /** 开始 */
    START(18),
    /** 结束 */
    END(19),
    /** 每日 */
    DAY_BY_DAY(20),
    /** 金币要求 */
    MESO(21),
    /** Buff要求 */
    BUFF(22),
    /** 排除Buff要求 */
    EXCEPT_BUFF(23);

    final byte type;

    QuestRequirementType(int type) {
        this.type = (byte) type;
    }

    public byte getType() {
        return type;
    }

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
        // case "end": already coded
        //     return END;
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