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
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestRequirementType;

/**
 * 最高等级需求
 * 检查玩家等级是否不超过任务要求的最高等级
 *
 * @author Tyler (Twdtwd)
 */
public class MaxLevelRequirement extends AbstractQuestRequirement {
    /** 最高等级 */
    private int maxLevel = 0;


    public MaxLevelRequirement(Quest quest, Data data) {
        super(QuestRequirementType.MAX_LEVEL);
        processData(data);
    }

    /**
     * 从WZ数据中解析最高等级限制
     *
     * @param data WZ数据
     */
    @Override
    public void processData(Data data) {
        maxLevel = DataTool.getInt(data);
    }


    @Override
    public boolean check(Character chr, Integer npcid) {
        return maxLevel >= chr.getLevel();
    }
}