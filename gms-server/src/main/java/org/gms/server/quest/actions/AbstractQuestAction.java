/*
	This file is part of the MapleSolaxia Maple Story Server

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
package org.gms.server.quest.actions;

import org.gms.client.Character;
import org.gms.provider.Data;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestActionType;

import java.util.ArrayList;
import java.util.List;

/**
     * 【抽象类】AbstractQuestAction，包 {@code org.gms.server.quest.actions}。
     * 任务动作抽象基类，定义任务执行时的动作接口。
     *
     * @author Tyler (Twdtwd)
     */
    public abstract class AbstractQuestAction {
        /** 动作类型 */
        private final QuestActionType type;
        /** 任务ID */
        protected int questID;

        /**
         * 构造函数
         *
         * @param action 动作类型
         * @param quest  所属任务
         */
        public AbstractQuestAction(QuestActionType action, Quest quest) {
            this.type = action;
            this.questID = quest.getId();
        }

        /**
         * 执行动作
         *
         * @param chr          玩家角色
         * @param extSelection 扩展选择（用于多选奖励）
         */
        public abstract void run(Character chr, Integer extSelection);

        /**
         * 处理WZ数据
         *
         * @param data WZ数据
         */
        public abstract void processData(Data data);

        /**
         * 检查动作是否可以执行
         *
         * @param chr          玩家角色
         * @param extSelection 扩展选择
         * @return 默认返回true
         */
        public boolean check(Character chr, Integer extSelection) {
            return true;
        }

        /**
         * 获取动作类型
         *
         * @return 动作类型
         */
        public QuestActionType getType() {
            return type;
        }

        /**
         * 通过5字节编码获取职业列表
         *
         * @param encoded 编码值
         * @return 职业ID列表
         */
        public static List<Integer> getJobBy5ByteEncoding(int encoded) {
        List<Integer> ret = new ArrayList<>();
        if ((encoded & 0x1) != 0) {
            ret.add(0);
        }
        if ((encoded & 0x2) != 0) {
            ret.add(100);
        }
        if ((encoded & 0x4) != 0) {
            ret.add(200);
        }
        if ((encoded & 0x8) != 0) {
            ret.add(300);
        }
        if ((encoded & 0x10) != 0) {
            ret.add(400);
        }
        if ((encoded & 0x20) != 0) {
            ret.add(500);
        }
        if ((encoded & 0x400) != 0) {
            ret.add(1000);
        }
        if ((encoded & 0x800) != 0) {
            ret.add(1100);
        }
        if ((encoded & 0x1000) != 0) {
            ret.add(1200);
        }
        if ((encoded & 0x2000) != 0) {
            ret.add(1300);
        }
        if ((encoded & 0x4000) != 0) {
            ret.add(1400);
        }
        if ((encoded & 0x8000) != 0) {
            ret.add(1500);
        }
        if ((encoded & 0x20000) != 0) {
            ret.add(2001); //im not sure of this one
            ret.add(2200);
        }
        if ((encoded & 0x100000) != 0) {
            ret.add(2000);
            ret.add(2001); //?
        }
        if ((encoded & 0x200000) != 0) {
            ret.add(2100);
        }
        if ((encoded & 0x400000) != 0) {
            ret.add(2001); //?
            ret.add(2200);
        }

        if ((encoded & 0x40000000) != 0) { //i haven't seen any higher than this o.o
            ret.add(3000);
            ret.add(3200);
            ret.add(3300);
            ret.add(3500);
        }
        return ret;
    }

    /**
         * 通过简单编码获取职业列表
         *
         * @param encoded 编码值
         * @return 职业ID列表
         */
        public static List<Integer> getJobBySimpleEncoding(int encoded) {
            List<Integer> ret = new ArrayList<>();
            if ((encoded & 0x1) != 0) {
                ret.add(200);
            }
            if ((encoded & 0x2) != 0) {
                ret.add(300);
            }
            if ((encoded & 0x4) != 0) {
                ret.add(400);
            }
            if ((encoded & 0x8) != 0) {
                ret.add(500);
            }
            return ret;
        }
    }