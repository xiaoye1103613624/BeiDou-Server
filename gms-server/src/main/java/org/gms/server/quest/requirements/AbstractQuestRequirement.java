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
 * 抽象任务需求基类
 * 定义任务条件检查的基本框架，所有具体需求（职业、等级、物品等）继承此类
 * 子类需实现check()和processData()方法
 *
 * @author Tyler (Twdtwd)
 */
public abstract class AbstractQuestRequirement {
    /** 需求类型 */
    private final QuestRequirementType type;

    public AbstractQuestRequirement(QuestRequirementType type) {
        this.type = type;
    }

    /**
     * 检查玩家是否满足该需求
     *
     * @param chr   玩家
     * @param npcid 调用NPC ID
     * @return true表示满足
     */
    public abstract boolean check(Character chr, Integer npcid);

    /**
     * 从WZ数据中解析需求配置
     *
     * @param data WZ数据
     */
    public abstract void processData(Data data);

    public QuestRequirementType getType() {
        return type;
    }
}