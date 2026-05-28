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
 * 【类型】NPCScriptManager（class），包 `org.gms.scripting.npc`。
 *
 * NPC 脚本管理器（单例），负责加载和执行 NPC 对话脚本，管理客户端与 {@link NPCConversationManager} 的映射关系。
 * 每个客户端同一时间只能有一个活跃的 NPC 对话，通过 {@code cms} Map 维护。
 *
 * 脚本路由策略（见 {@link #start} 方法）：
 * <ol>
 *   <li>指定了 fileName → 先查 npc/ 目录，再查 BeiDouSpecial/ 目录（物品脚本查 item/）</li>
 *   <li>未指定 fileName → 按 NPC ID 查找 npc/{npcId}.js</li>
 *   <li>都找不到 → 返回 false，客户端显示 "NPC is uncoded"</li>
 * </ol>
 *
 * @author Matze
 */
public class NPCScriptManager extends AbstractScriptManager {
    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(NPCScriptManager.class);
    /** 单例实例 */
    private static final NPCScriptManager instance = new NPCScriptManager();

    /** 客户端 → 对话管理器映射：每个客户端最多一个活跃的 NPC 对话 */
    private final Map<Client, NPCConversationManager> cms = new HashMap<>();
    /** 客户端 → 可调用脚本引擎映射 */
    private final Map<Client, Invocable> scripts = new HashMap<>();

    /**
     * 获取 NPCScriptManager 单例实例
     * @return 单例对象
     */
    public static NPCScriptManager getInstance() {
        return instance;
    }

    /** 检查指定文件名的 NPC 脚本是否存在（用于条件判断） */
    public boolean isNpcScriptAvailable(Client c, String fileName) {
        ScriptEngine engine = null;
        if (fileName != null) {
            engine = getInvocableScriptEngine("npc/" + fileName + ".js", c);
        }

        return engine != null;
    }

    // ---- start() 重载链，最终汇聚到最完整的 start(c, npc, oid, fileName, chr, itemScript, engineName) ----

    /**
     * 启动 NPC 对话脚本（无实例 OID）
     * @param c    客户端连接
     * @param npc  NPC 模板 ID
     * @param chr  交互角色
     * @return true=启动成功
     */
    public boolean start(Client c, int npc, Character chr) {
        return start(c, npc, -1, chr);
    }

    /**
     * 启动 NPC 对话脚本（带实例 OID）
     * @param c    客户端连接
     * @param npc  NPC 模板 ID
     * @param oid  地图上 NPC 实例的 OID
     * @param chr  交互角色
     * @return true=启动成功
     */
    public boolean start(Client c, int npc, int oid, Character chr) {
        return start(c, npc, oid, null, chr);
    }

    /**
     * 启动指定脚本文件的 NPC 对话
     * @param c        客户端连接
     * @param npc      NPC 模板 ID
     * @param fileName 指定的脚本文件名
     * @param chr      交互角色
     * @return true=启动成功
     */
    public boolean start(Client c, int npc, String fileName, Character chr) {
        return start(c, npc, -1, fileName, chr);
    }

    /**
     * 启动 NPC 对话脚本（完整参数，非物品脚本）
     * @param c        客户端连接
     * @param npc      NPC 模板 ID
     * @param oid      地图上 NPC 实例的 OID
     * @param fileName 指定的脚本文件名（null 则按 NPC ID 查找）
     * @param chr      交互角色
     * @return true=启动成功
     */
    public boolean start(Client c, int npc, int oid, String fileName, Character chr) {
        return start(c, npc, oid, fileName, chr, false, "cm");
    }

    /**
     * 通过物品脚本启动 NPC 对话
     * @param c          客户端连接
     * @param scriptItem 物品脚本配置
     * @param chr        交互角色
     * @return true=启动成功
     */
    public boolean start(Client c, ScriptedItem scriptItem, Character chr) {
        return start(c, scriptItem.getNpc(), -1, scriptItem.getScript(), chr, true, "im");
    }

    /**
     * 启动组队 NPC 对话脚本（用于组队任务场景）
     * @param filename 脚本文件名
     * @param c        客户端连接
     * @param npc      NPC 模板 ID
     * @param chrs     队伍成员列表
     */
    public void start(String filename, Client c, int npc, List<PartyCharacter> chrs) {
        try {
            // 创建组队对话管理器
            final NPCConversationManager cm = new NPCConversationManager(c, npc, chrs, true);
            cm.dispose();
            // 检查是否已有活跃对话
            if (cms.containsKey(c)) {
                return;
            }
            cms.put(c, cm);
            // 加载脚本引擎
            ScriptEngine engine = getInvocableScriptEngine("npc/" + filename + ".js", c);

            if (engine == null) {
                c.getPlayer().dropMessage(1, "NPC " + npc + " is uncoded.");
                cm.dispose();
                return;
            }
            // 绑定对话管理器到脚本引擎
            engine.put("cm", cm);

            Invocable invocable = (Invocable) engine;
            scripts.put(c, invocable);
            try {
                // 调用脚本的 start(chrs) 函数
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
     * 核心 start 方法：加载脚本文件、创建对话管理器、绑定引擎变量、调用脚本 start() 函数。
     *
     * @param c          客户端连接
     * @param npc        NPC 模板 ID
     * @param oid        地图上 NPC 实例的 OID（无实例时为 -1）
     * @param fileName   指定的脚本文件名（null 则按 NPC ID 自动查找）
     * @param chr        与 NPC 交互的角色
     * @param itemScript 是否为物品触发的脚本（物品脚本引擎名用 "im"）
     * @param engineName 脚本中访问 CM 的变量名（NPC=cm, 物品=im）
     * @return true=脚本启动成功, false=启动失败
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
     * 执行 NPC 对话动作（玩家选择、输入等交互）
     * @param c         客户端连接
     * @param mode      操作模式（0=取消, 1=确认）
     * @param type      交互类型
     * @param selection 玩家选择的选项索引
     */
    public void action(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.tryacquireClient();  // 获取客户端锁
                c.setClickedNPC();     // 标记点击状态
                iv.invokeFunction("action", mode, type, selection);  // 调用脚本 action 函数
            } catch (Exception t) {
                if (getCM(c) != null) {
                    log.error("Error performing NPC script action for npc: {}", getCM(c).getNpc(), t);
                }
                dispose(c, true);  // 异常时清理并启用玩家动作
            } finally {
                c.releaseClient();  // 释放客户端锁
            }
        }
    }

    /**
     * 执行 NPC 对话层级跳转（根据 NextLevelContext 调用对应脚本函数）
     * @param c         客户端连接
     * @param mode      操作模式（-1=关闭, 0=返回上一层, 1=进入下一层）
     * @param type      交互类型
     * @param selection 玩家选择的选项索引
     */
    public void nextLevel(Client c, byte mode, byte type, int selection) {
        Invocable iv = scripts.get(c);
        if (iv != null) {
            try {
                c.tryacquireClient();  // 获取客户端锁
                c.setClickedNPC();     // 标记点击状态
                NextLevelContext nextLevelContext = c.getCM().getNextLevelContext();
                // 根据对话层级类型分发到对应的脚本函数
                switch (nextLevelContext.getLevelType()) {
                    case NextLevelType.SEND_SELECT -> {
                        if (mode == 0) { dispose(c, true); return; }
                        iv.invokeFunction("level" + nextLevelContext.getPrefix() + selection);
                    }
                    case NextLevelType.GET_INPUT_NUMBER, NextLevelType.SEND_NEXT_SELECT -> {
                        if (mode == 0) { dispose(c, true); return; }
                        iv.invokeFunction("level" + nextLevelContext.getNextLevel(), selection);
                    }
                    case NextLevelType.GET_INPUT_TEXT -> {
                        if (mode == 0) { dispose(c, true); return; }
                        iv.invokeFunction("level" + nextLevelContext.getNextLevel(), c.getCM().getText());
                    }
                    case NextLevelType.SEND_LAST_NEXT, NextLevelType.SEND_NEXT, NextLevelType.SEND_LAST,
                         NextLevelType.SEND_OK, NextLevelType.SEND_ACCEPT_DECLINE, NextLevelType.SEND_YES_NO -> {
                        if (mode == -1) { dispose(c, true); return; }
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
     * 清理 NPC 对话资源（通过对话管理器）
     * @param cm 对话管理器
     */
    public void dispose(NPCConversationManager cm) {
        Client c = cm.getClient();
        c.getPlayer().setCS(false);                          // 取消 CS 状态
        c.getPlayer().setNpcCooldown(System.currentTimeMillis());  // 设置 NPC 冷却时间
        cms.remove(c);                                       // 移除对话管理器映射
        scripts.remove(c);                                   // 移除脚本引擎映射

        // 重置脚本上下文（item 或 npc 目录）
        String scriptFolder = (cm.isItemScript() ? "item" : "npc");
        if (cm.getScriptName() != null) {
            resetContext(scriptFolder + "/" + cm.getScriptName() + ".js", c);
        } else {
            resetContext(scriptFolder + "/" + cm.getNpc() + ".js", c);
        }

        c.getPlayer().flushDelayedUpdateQuests();  // 刷新延迟更新的任务
    }

    /**
     * 清理客户端的 NPC 对话资源（不发送启用动作包）
     * @param c 客户端连接
     */
    public void dispose(Client c) {
        dispose(c, false);
    }

    /**
     * 清理客户端的 NPC 对话资源
     * @param c      客户端连接
     * @param action 是否发送启用动作包
     */
    public void dispose(Client c, boolean action) {
        NPCConversationManager cm = cms.get(c);
        if (cm != null) {
            dispose(cm);
        }
        if (action) {
            c.sendPacket(PacketCreator.enableActions());  // 发送启用动作包，允许玩家继续操作
        }
    }

    /**
     * 获取指定客户端的对话管理器
     * @param c 客户端连接
     * @return NPCConversationManager 实例，无活跃对话时返回 null
     */
    public NPCConversationManager getCM(Client c) {
        return cms.get(c);
    }

}