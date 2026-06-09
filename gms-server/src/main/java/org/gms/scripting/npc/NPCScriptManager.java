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
package org.gms.scripting.npc;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.constants.game.NextLevelType;
import org.gms.model.pojo.NextLevelContext;
import org.gms.net.server.world.PartyCharacter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;
import org.gms.server.ItemInformationProvider.ScriptedItem;
import org.gms.util.PacketCreator;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NPC脚本管理器（单例）
 * 管理NPC对话脚本的加载、执行和会话生命周期
 */
public class NPCScriptManager extends AbstractScriptManager {
    /** SLF4J日志实例 */
    private static final Logger log = LoggerFactory.getLogger(NPCScriptManager.class);
    /** 单例实例 */
    private static final NPCScriptManager instance = new NPCScriptManager();

    /** 客户端到NPC对话管理器的映射 */
    private final Map<Client, NPCConversationManager> cms = new HashMap<>();
    /** 客户端到JS脚本调用接口的映射 */
    private final Map<Client, Invocable> scripts = new HashMap<>();

    /**
     * 获取单例实例
     *
     * @return NPCScriptManager单例
     */
    public static NPCScriptManager getInstance() {
        return instance;
    }

    /**
     * 检查指定NPC脚本文件是否存在
     *
     * @param c        客户端
     * @param fileName 脚本文件名（不含路径和扩展名）
     * @return true表示脚本可用
     */
    public boolean isNpcScriptAvailable(Client c, String fileName) {
        ScriptEngine engine = null;
        if (fileName != null) {
            engine = getInvocableScriptEngine("npc/" + fileName + ".js", c);
        }

        return engine != null;
    }

    /**
     * 启动NPC脚本（使用默认对象ID）
     *
     * @param c   客户端
     * @param npc NPC ID
     * @param chr 角色
     * @return 是否成功启动
     */
    public boolean start(Client c, int npc, Character chr) {
        return start(c, npc, -1, chr);
    }

    /**
     * 启动NPC脚本（指定对象ID）
     *
     * @param c   客户端
     * @param npc NPC ID
     * @param oid NPC对象ID
     * @param chr 角色
     * @return 是否成功启动
     */
    public boolean start(Client c, int npc, int oid, Character chr) {
        return start(c, npc, oid, null, chr);
    }

    /**
     * 启动NPC脚本（通过脚本文件名）
     *
     * @param c        客户端
     * @param npc      NPC ID
     * @param fileName 脚本文件名
     * @param chr      角色
     * @return 是否成功启动
     */
    public boolean start(Client c, int npc, String fileName, Character chr) {
        return start(c, npc, -1, fileName, chr);
    }

    /**
     * 启动NPC脚本（完整参数）
     *
     * @param c        客户端
     * @param npc      NPC ID
     * @param oid      NPC对象ID
     * @param fileName 脚本文件名
     * @param chr      角色
     * @return 是否成功启动
     */
    public boolean start(Client c, int npc, int oid, String fileName, Character chr) {
        return start(c, npc, oid, fileName, chr, false, "cm");
    }

    /**
     * 启动物品脚本
     *
     * @param c          客户端
     * @param scriptItem 脚本物品对象
     * @param chr        角色
     * @return 是否成功启动
     */
    public boolean start(Client c, ScriptedItem scriptItem, Character chr) {
        return start(c, scriptItem.getNpc(), -1, scriptItem.getScript(), chr, true, "im");
    }

    /**
     * 启动NPC脚本（带队伍成员列表）
     *
     * @param filename 脚本文件名
     * @param c        客户端
     * @param npc      NPC ID
     * @param chrs     队伍成员列表
     */
    public void start(String filename, Client c, int npc, List<PartyCharacter> chrs) {
        try {
            final NPCConversationManager cm = new NPCConversationManager(c, npc, chrs, true);
            cm.dispose();
            if (cms.containsKey(c)) {
                return;
            }
            cms.put(c, cm);
            ScriptEngine engine = getInvocableScriptEngine("npc/" + filename + ".js", c);

            if (engine == null) {
                c.getPlayer().dropMessage(1, "NPC " + npc + " is uncoded.");
                cm.dispose();
                return;
            }
            engine.put("cm", cm);

            Invocable invocable = (Invocable) engine;
            scripts.put(c, invocable);
            try {
                invocable.invokeFunction("start", chrs);
            } catch (final NoSuchMethodException nsme) {
                nsme.printStackTrace();
            }

        } catch (final Exception e) {
            log.error("Error starting NPC script: {}", npc, e);
            dispose(c);
        }
    }

    /**
     * 私有核心启动方法
     *
     * @param c          客户端
     * @param npc        NPC ID
     * @param oid        NPC对象ID
     * @param fileName   脚本文件名
     * @param chr        角色
     * @param itemScript 是否物品脚本
     * @param engineName 引擎变量名（在脚本上下文的名称）
     * @return 是否成功启动
     */
    private boolean start(Client c, int npc, int oid, String fileName, Character chr, boolean itemScript, String engineName) {
        try {
            final NPCConversationManager cm = new NPCConversationManager(c, npc, oid, fileName, itemScript);
            if (cms.containsKey(c)) {
                dispose(c);
            }
            if (c.canClickNPC()) {
                cms.put(c, cm);
                ScriptEngine engine = null;
                if (!itemScript) {
                    if (fileName != null) {
                        engine = getInvocableScriptEngine("npc/" + fileName + ".js", c);
                        if (engine == null) {
                            engine = getInvocableScriptEngine("BeiDouSpecial/" + fileName + ".js", c);
                        }
                    }
                } else {
                    if (fileName != null) {     // thanks MiLin for drafting NPC-based item scripts
                        engine = getInvocableScriptEngine("item/" + fileName + ".js", c);
                    }
                }
                if (engine == null) {
                    engine = getInvocableScriptEngine("npc/" + npc + ".js", c);
                    cm.resetItemScript();
                }

                if (engine == null) {
                    dispose(c);
                    return false;
                }
                engine.put(engineName, cm);

                Invocable iv = (Invocable) engine;
                scripts.put(c, iv);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start");
                } catch (final NoSuchMethodException nsme) {
                    try {
                        iv.invokeFunction("start", chr);
                    } catch (final NoSuchMethodException nsma) {
                        nsma.printStackTrace();
                    }
                }
            } else {
                c.sendPacket(PacketCreator.enableActions());
            }
            return true;
        } catch (Exception e) {
            log.error("Error starting NPC script: {}", npc, e);
            dispose(c, true);

            return false;
        }
    }

    /**
     * 处理玩家NPC对话动作（普通模式）
     *
     * @param c         客户端
     * @param mode      动作模式（yes/no/select）
     * @param type      动作类型
     * @param selection 选择项索引
     */
    public void action(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.tryacquireClient();
                c.setClickedNPC();
                iv.invokeFunction("action", mode, type, selection);
            } catch (Exception t) {
                if (getCM(c) != null) {
                    log.error("Error performing NPC script action for npc: {}", getCM(c).getNpc(), t);
                }
                dispose(c, true);
            } finally {
                c.releaseClient();
            }
        }
    }

    /**
     * 处理玩家NPC对话动作（多层级Level模式）
     * 根据nextLevelContext中记录的类型，路由到对应的方法
     *
     * @param c         客户端
     * @param mode      动作模式
     * @param type      动作类型
     * @param selection 选择项索引
     */
    public void nextLevel(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.tryacquireClient();
                c.setClickedNPC();
                NextLevelContext nextLevelContext = c.getCM().getNextLevelContext();
                switch (nextLevelContext.getLevelType()) {
                    case NextLevelType.SEND_SELECT -> {
                        if (mode == 0) {
                            dispose(c, true);
                            return;
                        }
                        iv.invokeFunction("level" + nextLevelContext.getPrefix() + selection);
                    }
                    case NextLevelType.GET_INPUT_NUMBER, NextLevelType.SEND_NEXT_SELECT -> {
                        if (mode == 0) {
                            dispose(c, true);
                            return;
                        }
                        iv.invokeFunction("level" + nextLevelContext.getNextLevel(), selection);
                    }
                    case NextLevelType.GET_INPUT_TEXT -> {
                        if (mode == 0) {
                            dispose(c, true);
                            return;
                        }
                        iv.invokeFunction("level" + nextLevelContext.getNextLevel(), c.getCM().getText());
                    }
                    case NextLevelType.SEND_LAST_NEXT, NextLevelType.SEND_NEXT, NextLevelType.SEND_LAST,
                         NextLevelType.SEND_OK, NextLevelType.SEND_ACCEPT_DECLINE, NextLevelType.SEND_YES_NO -> {
                        if (mode == -1) {
                            dispose(c, true);
                            return;
                        }
                        if (mode == 0) {
                            iv.invokeFunction("level" + nextLevelContext.getLastLevel());
                        } else {
                            iv.invokeFunction("level" + nextLevelContext.getNextLevel());
                        }
                    }
                    default -> {
                        log.error("Unsupported level type: {}", nextLevelContext.getLevelType());
                        dispose(c, true);
                    }
                }
            } catch (Exception t) {
                if (getCM(c) != null) {
                    log.error("Error performing NPC script action for npc: {}", getCM(c).getNpc(), t);
                }
                dispose(c, true);
            } finally {
                c.releaseClient();
            }
        }
    }

    /**
     * 释放指定对话管理器的会话
     * 清除客户端状态、移除缓存、重置脚本上下文
     *
     * @param cm NPC对话管理器
     */
    public void dispose(NPCConversationManager cm) {
        Client c = cm.getClient();
        c.getPlayer().setCS(false);
        c.getPlayer().setNpcCooldown(System.currentTimeMillis());
        cms.remove(c);
        scripts.remove(c);

        String scriptFolder = (cm.isItemScript() ? "item" : "npc");
        if (cm.getScriptName() != null) {
            resetContext(scriptFolder + "/" + cm.getScriptName() + ".js", c);
        } else {
            resetContext(scriptFolder + "/" + cm.getNpc() + ".js", c);
        }

        c.getPlayer().flushDelayedUpdateQuests();
    }

    /**
     * 释放客户端的NPC对话会话
     *
     * @param c 客户端
     */
    public void dispose(Client c) {
        dispose(c, false);
    }

    /**
     * 释放客户端的NPC对话会话
     *
     * @param c      客户端
     * @param action 是否重新启用玩家操作（发送enableActions包）
     */
    public void dispose(Client c, boolean action) {
        NPCConversationManager cm = cms.get(c);
        if (cm != null) {
            dispose(cm);
        }
        if (action) {
            c.sendPacket(PacketCreator.enableActions());
        }
    }

    /**
     * 获取客户端的NPC对话管理器
     *
     * @param c 客户端
     * @return NPC对话管理器，不存在返回null
     */
    public NPCConversationManager getCM(Client c) {
        return cms.get(c);
    }

}