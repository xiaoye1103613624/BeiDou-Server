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
 * 已完成任务数量需求
 * 检查玩家已完成的任务总数是否达到要求的最低数量
 *
 * @author Tyler (Twdtwd)
 */
public class CompletedQuestRequirement extends AbstractQuestRequirement {
    /** 要求的已完成任务数量 */
    private int reqQuest;


    public CompletedQuestRequirement(Quest quest, Data data) {
        super(QuestRequirementType.COMPLETED_QUEST);
        processData(data);
    }

    @Override
    public void processData(Data data) {
        reqQuest = DataTool.getInt(data);
    }


    @Override
    public boolean check(Character chr, Integer npcid) {
        return chr.getCompletedQuests().size() >= reqQuest;
    }
}