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
import org.gms.client.inventory.Pet;
import org.gms.provider.Data;
import org.gms.provider.DataTool;
import org.gms.server.quest.Quest;
import org.gms.server.quest.QuestRequirementType;

/**
 * 最低宠物亲密度需求
 * 检查玩家宠物亲密度是否达到任务要求的最低值
 *
 * @author Tyler (Twdtwd)
 */
public class MinTamenessRequirement extends AbstractQuestRequirement {
    /** 最低亲密度 */
    private int minTameness;


    public MinTamenessRequirement(Quest quest, Data data) {
        super(QuestRequirementType.MIN_PET_TAMENESS);
        processData(data);
    }

    /**
     * 从WZ数据中解析最低亲密度要求
     *
     * @param data WZ数据
     */
    @Override
    public void processData(Data data) {
        minTameness = DataTool.getInt(data);
    }


    @Override
    public boolean check(Character chr, Integer npcid) {
        int curTameness = 0;

        for (Pet pet : chr.getPets()) {
            if (pet == null) {
                continue;
            }

            if (pet.getTameness() > curTameness) {
                curTameness = pet.getTameness();
            }
        }

        return curTameness >= minTameness;
    }
}