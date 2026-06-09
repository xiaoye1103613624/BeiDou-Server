/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
 * 脚本需求
 * 通过JavaScript脚本检测玩家是否满足任务条件，提供最大的灵活性
 *
 * @author Ronan
 */
public class ScriptRequirement extends AbstractQuestRequirement {
    /** 是否需要脚本检测 */
    private boolean reqScript;

    public ScriptRequirement(Quest quest, Data data) {
        super(QuestRequirementType.BUFF);
        processData(data);
    }

    /**
     * 从WZ数据中解析脚本需求字符串，非空表示需要脚本检测
     *
     * @param data WZ数据
     */
    @Override
    public void processData(Data data) {
        reqScript = !DataTool.getString(data, "").isEmpty();
    }

    /**
     * 检查当前是否满足脚本检测条件（始终返回true，实际检查由脚本完成）
     *
     * @param chr   玩家
     * @param npcid NPC ID
     * @return 始终返回true
     */
    @Override
    public boolean check(Character chr, Integer npcid) {
        return true;
    }

    /**
     * 获取是否需要脚本检测
     *
     * @return true表示需要脚本检测
     */
    public boolean get() {
        return reqScript;
    }
}