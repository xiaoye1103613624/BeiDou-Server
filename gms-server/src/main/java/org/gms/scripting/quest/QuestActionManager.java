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
package org.gms.scripting.quest;

import org.gms.client.Client;
import org.gms.scripting.npc.NPCConversationManager;
import org.gms.server.ItemInformationProvider;
import org.gms.server.quest.Quest;
import org.gms.server.quest.actions.ExpAction;
import org.gms.server.quest.actions.MesoAction;

/**
 * 任务动作管理器
 * 处理任务脚本中的玩家动作，继承NPCConversationManager以支持对话和物品操作
 */
public class QuestActionManager extends NPCConversationManager {
    /** 标记此脚本是任务开始(true)还是结束(false) */
    private final boolean start;
    /** 任务ID */
    private final int quest;

    public QuestActionManager(Client c, int quest, int npc, boolean start) {
        super(c, npc, null);
        this.quest = quest;
        this.start = start;
    }

    /**
     * 获取当前任务ID
     *
     * @return 任务ID
     */
    public int getQuest() {
        return quest;
    }

    /**
     * 判断是否为任务开始节点
     *
     * @return true表示任务开始，false表示任务完成
     */
    public boolean isStart() {
        return start;
    }

    @Override
    public void dispose() {
        QuestScriptManager.getInstance().dispose(this, getClient());
    }

    /**
     * 强制开始当前任务
     *
     * @return 是否成功
     */
    public boolean forceStartQuest() {
        return forceStartQuest(quest);
    }

    /**
     * 强制完成当前任务
     *
     * @return 是否成功
     */
    public boolean forceCompleteQuest() {
        return forceCompleteQuest(quest);
    }

    // For compatibility with some older scripts...
    public void startQuest() {
        forceStartQuest();
    }

    // For compatibility with some older scripts...
    public void completeQuest() {
        forceCompleteQuest();
    }

    @Override
    public void gainExp(int gain) {
        ExpAction.runAction(getPlayer(), gain);
    }

    @Override
    public void gainMeso(int gain) {
        MesoAction.runAction(getPlayer(), gain);
    }

    /**
     * 获取勋章名称，仅用于勋章任务（ID 299XX）
     *
     * @return 勋章名称
     */
    public String getMedalName() {
        Quest q = Quest.getInstance(quest);
        return ItemInformationProvider.getInstance().getName(q.getMedalRequirement());
    }
}