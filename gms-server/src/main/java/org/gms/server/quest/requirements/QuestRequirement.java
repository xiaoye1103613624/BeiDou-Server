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
package org.gms.server.quest.requirements;

import org.gms.client.Character;
import org.gms.client.QuestStatus;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestRequirementType;

import java.util.HashMap;
import java.util.Map;

/**
 * 前置任务需求
 * 检查玩家是否已完成指定任务，任务状态必须匹配要求的完成状态
 *
 * @author Tyler (Twdtwd)
 */
public class QuestRequirement extends AbstractQuestRequirement {
    /** 任务ID -> 完成状态映射 */
    Map<Integer, Integer> quests = new HashMap<>();

    public QuestRequirement(Quest quest, Data data) {
        super(QuestRequirementType.QUEST);
        processData(data);
    }

    /**
     * 从WZ数据中解析前置任务ID和要求的完成状态
     *
     * @param data WZ数据
     */
    @Override
    public void processData(Data data) {
        for (Data questEntry : data.getChildren()) {
            int questID = DataTool.getInt(questEntry.getChildByPath("id"));
            int stateReq = DataTool.getInt(questEntry.getChildByPath("state"));
            quests.put(questID, stateReq);
        }
    }


    @Override
    public boolean check(Character chr, Integer npcid) {
        for (Integer questID : quests.keySet()) {
            int stateReq = quests.get(questID);
            QuestStatus qs = chr.getQuest(Quest.getInstance(questID));

            if (qs == null && QuestStatus.Status.getById(stateReq).equals(QuestStatus.Status.NOT_STARTED)) {
                continue;
            }

            if (qs == null || !qs.getStatus().equals(QuestStatus.Status.getById(stateReq))) {
                return false;
            }

        }
        return true;
    }
}