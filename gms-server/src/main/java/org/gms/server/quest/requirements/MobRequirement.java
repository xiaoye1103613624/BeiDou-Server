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
import org.gms.client.QuestStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestRequirementType;

import java.util.HashMap;
import java.util.Map;

/**
 * 怪物击杀需求
 * 检查玩家是否已击杀指定数量的怪物，通过任务进度记录判断
 *
 * @author Tyler (Twdtwd)
 */
public class MobRequirement extends AbstractQuestRequirement {
    private static final Logger log = LoggerFactory.getLogger(MobRequirement.class);
    /** 怪物ID到击杀数量的映射 */
    Map<Integer, Integer> mobs = new HashMap<>();
    /** 关联的任务ID */
    private final int questID;

    public MobRequirement(Quest quest, Data data) {
        super(QuestRequirementType.MOB);
        questID = quest.getId();
        processData(data);
    }

    /**
     * 从WZ数据中解析怪物ID和击杀数量要求
     *
     * @param data WZ数据
     */
    @Override
    public void processData(Data data) {
        for (Data questEntry : data.getChildren()) {
            int mobID = DataTool.getInt(questEntry.getChildByPath("id"));
            int countReq = DataTool.getInt(questEntry.getChildByPath("count"));
            mobs.put(mobID, countReq);
        }
    }


    @Override
    public boolean check(Character chr, Integer npcid) {
        QuestStatus status = chr.getQuest(Quest.getInstance(questID));
        for (Integer mobID : mobs.keySet()) {
            int countReq = mobs.get(mobID);
            int progress;

            try {
                progress = Integer.parseInt(status.getProgress(mobID));
            } catch (NumberFormatException ex) {
                log.warn("Mob: {}, quest: {}, chrId: {}, progress: {}", mobID, questID, chr.getId(), status.getProgress(mobID), ex);
                return false;
            }

            if (progress < countReq) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取指定怪物的击杀数量要求
     *
     * @param mobid 怪物ID
     * @return 击杀数量，未设置则返回0
     */
    public int getRequiredMobCount(int mobid) {
        if (mobs.containsKey(mobid)) {
            return mobs.get(mobid);
        }
        return 0;
    }
}