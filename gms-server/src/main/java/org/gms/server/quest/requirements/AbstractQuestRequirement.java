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
package org.gms.server.quest.requirements;

import org.gms.client.Character;
import org.gms.provider.Data;
import org.gms.server.quest.QuestRequirementType;

/**
     * 【抽象类】AbstractQuestRequirement，包 {@code org.gms.server.quest.requirements}。
     * 任务条件抽象基类，定义任务条件检查的接口。
     * 任务系统使用此类作为所有条件的基类。
     *
     * @author Tyler (Twdtwd)
     */
    public abstract class AbstractQuestRequirement {
        /** 条件类型 */
        private final QuestRequirementType type;

        /**
         * 构造函数
         *
         * @param type 条件类型
         */
        public AbstractQuestRequirement(QuestRequirementType type) {
            this.type = type;
        }

        /**
         * 检查玩家是否满足条件
         *
         * @param chr   玩家角色
         * @param npcid NPC ID
         * @return 如果满足条件返回true
         */
        public abstract boolean check(Character chr, Integer npcid);

        /**
         * 处理WZ数据并存储供后续使用
         *
         * @param data 要处理的数据
         */
        public abstract void processData(Data data);

        /**
         * 获取条件类型
         *
         * @return 条件类型
         */
        public QuestRequirementType getType() {
            return type;
        }
    }