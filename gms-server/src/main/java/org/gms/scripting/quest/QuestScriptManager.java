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
import org.gms.client.QuestStatus;
import org.gms.constants.game.GameConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;
import org.gms.server.quest.Quest;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务脚本管理器
 * 管理任务JavaScript脚本的加载和执行，处理任务开始、进行中和完成动作
 */
public class QuestScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(QuestScriptManager.class);
    private static final QuestScriptManager instance = new QuestScriptManager();

    /** 客户端到任务动作管理器的映射 */
    private final Map<Client, QuestActionManager> qms = new HashMap<>();
    /** 客户端到JS脚本可调用对象的映射 */
    private final Map<Client, Invocable> scripts = new HashMap<>();

    public static QuestScriptManager getInstance() {
        return instance;
    }

    /**
     * 根据任务ID加载对应的JS脚本引擎
     * 如果是勋章任务，回退到通用勋章脚本
     *
     * @param c       客户端
     * @param questid 任务ID
     * @return 脚本引擎，无对应脚本则返回null
     */
    private ScriptEngine getQuestScriptEngine(Client c, short questid) {
        ScriptEngine engine = getInvocableScriptEngine("quest/" + questid + ".js", c);
        if (engine == null && GameConstants.isMedalQuest(questid)) {
            engine = getInvocableScriptEngine("quest/medalQuest.js", c);   // start generic medal quest
        }

        return engine;
    }

    /**
     * 启动任务开始脚本
     *
     * @param c       客户端
     * @param questid 任务ID
     * @param npc     NPC ID
     */
    public void start(Client c, short questid, int npc) {
        Quest quest = Quest.getInstance(questid);
        try {
            QuestActionManager qm = new QuestActionManager(c, questid, npc, true);
            if (qms.containsKey(c)) {
                return;
            }
            if (c.canClickNPC()) {
                qms.put(c, qm);

                /*if (!quest.hasScriptRequirement(false)) {   // lack of scripted quest checks found thanks to Mali, Resinate
                    qm.dispose();
                    return;
                }*/

                ScriptEngine engine = getQuestScriptEngine(c, questid);
                if (engine == null) {
                    log.warn("START Quest {} is uncoded.", questid);
                    qm.dispose();
                    return;
                }

                engine.put("qm", qm);

                Invocable iv = (Invocable) engine;
                scripts.put(c, iv);
                c.setClickedNPC();
                iv.invokeFunction("start", (byte) 1, (byte) 0, 0);
            }
        } catch (final Throwable t) {
            log.error("Error starting quest script: {}", questid, t);
            dispose(c);
        }
    }

    /**
     * 启动任务进行中脚本（处理玩家选择后续步骤）
     *
     * @param c         客户端
     * @param mode      模式
     * @param type      类型
     * @param selection 选项
     */
    public void start(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.setClickedNPC();
                iv.invokeFunction("start", mode, type, selection);
            } catch (final Exception e) {
                log.error("Error starting quest script: {}", getQM(c).getQuest(), e);
                dispose(c);
            }
        }
    }

    /**
     * 启动任务结束脚本
     * 校验任务状态必须是已开始，且NPC必须在地图中或任务支持自动完成
     *
     * @param c       客户端
     * @param questid 任务ID
     * @param npc     NPC ID
     */
    public void end(Client c, short questid, int npc) {
        Quest quest = Quest.getInstance(questid);
        if (!c.getPlayer().getQuest(quest).getStatus().equals(QuestStatus.Status.STARTED) || (!c.getPlayer().getMap().containsNPC(npc) && !quest.isAutoComplete())) {
            dispose(c);
            return;
        }
        try {
            QuestActionManager qm = new QuestActionManager(c, questid, npc, false);
            if (qms.containsKey(c)) {
                return;
            }
            if (c.canClickNPC()) {
                qms.put(c, qm);

                /*if (!quest.hasScriptRequirement(true)) {
                    qm.dispose();
                    return;
                }*/

                ScriptEngine engine = getQuestScriptEngine(c, questid);
                if (engine == null) {
                    log.warn("END Quest {} is uncoded.", questid);
                    qm.dispose();
                    return;
                }

                engine.put("qm", qm);

                Invocable iv = (Invocable) engine;
                scripts.put(c, iv);
                c.setClickedNPC();
                iv.invokeFunction("end", (byte) 1, (byte) 0, 0);
            }
        } catch (final Throwable t) {
            log.error("Error starting quest script: {}", questid, t);
            dispose(c);
        }
    }

    /**
     * 处理任务结束时的玩家后续选择
     *
     * @param c         客户端
     * @param mode      模式
     * @param type      类型
     * @param selection 选项
     */
    public void end(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.setClickedNPC();
                iv.invokeFunction("end", mode, type, selection);
            } catch (final Exception e) {
                log.error("Error ending quest script: {}", getQM(c).getQuest(), e);
                dispose(c);
            }
        }
    }

    /**
     * 唤起任务打开事件，触发raiseOpen脚本函数
     *
     * @param c       客户端
     * @param questid 任务ID
     * @param npc     NPC ID
     */
    public void raiseOpen(Client c, short questid, int npc) {
        try {
            QuestActionManager qm = new QuestActionManager(c, questid, npc, true);
            if (qms.containsKey(c)) {
                return;
            }
            if (c.canClickNPC()) {
                qms.put(c, qm);

                ScriptEngine engine = getQuestScriptEngine(c, questid);
                if (engine == null) {
                    //FilePrinter.printError(FilePrinter.QUEST_UNCODED, "RAISE Quest " + questid + " is uncoded.");
                    qm.dispose();
                    return;
                }

                engine.put("qm", qm);

                Invocable iv = (Invocable) engine;
                scripts.put(c, iv);
                c.setClickedNPC();
                iv.invokeFunction("raiseOpen");
            }
        } catch (final Throwable t) {
            log.error("Error during quest script raiseOpen for quest: {}", questid, t);
            dispose(c);
        }
    }

    /**
     * 清理任务脚本会话
     * 移除客户端关联的QM和脚本，重置NPC冷却时间并清理脚本上下文
     *
     * @param qm 任务动作管理器
     * @param c  客户端
     */
    public void dispose(QuestActionManager qm, Client c) {
        qms.remove(c);
        scripts.remove(c);
        c.getPlayer().setNpcCooldown(System.currentTimeMillis());
        resetContext("quest/" + qm.getQuest() + ".js", c);
        c.getPlayer().flushDelayedUpdateQuests();
    }

    /**
     * 通过客户端清理任务脚本会话
     *
     * @param c 客户端
     */
    public void dispose(Client c) {
        QuestActionManager qm = qms.get(c);
        if (qm != null) {
            dispose(qm, c);
        }
    }

    /**
     * 获取客户端对应的任务动作管理器
     *
     * @param c 客户端
     * @return 任务动作管理器
     */
    public QuestActionManager getQM(Client c) {
        return qms.get(c);
    }

    /**
     * 重新加载所有任务脚本，清除缓存
     */
    public void reloadQuestScripts() {
        scripts.clear();
        qms.clear();
    }

    /**
     * 检查指定任务脚本是否包含某函数
     *
     * @param c            客户端
     * @param questid      任务ID
     * @param npc          NPC ID
     * @param functionName 函数名
     * @return 是否存在
     */
    public boolean checkFunctionExists(Client c, short questid, int npc, String functionName) {
        ScriptEngine engine = getQuestScriptEngine(c, questid);
        if (engine == null) {
            return false;
        }
        try {
            QuestActionManager qm = new QuestActionManager(c, questid, npc, false);
            engine.put("qm", qm);
            String script = "function checkFunction(funcName) { return typeof this[funcName] === 'function'; }";
            engine.eval(script);

            Invocable invocable = (Invocable) engine;
            boolean exists = (Boolean) invocable.invokeFunction("checkFunction", functionName);

            qm.dispose();
            return exists;
        } catch (ScriptException | NoSuchMethodException e) {
            e.printStackTrace();
            dispose(c);
        }
        return false;
    }


}