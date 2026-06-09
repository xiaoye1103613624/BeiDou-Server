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
package org.gms.scripting.portal;

import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;
import org.gms.server.maps.Portal;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * 传送门脚本管理器（单例）
 * 管理传送门脚本的加载、缓存和执行
 */
public class PortalScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(PortalScriptManager.class);
    private static final PortalScriptManager instance = new PortalScriptManager();

    /** 脚本路径到PortalScript实例的缓存映射 */
    private final Map<String, PortalScript> scripts = new HashMap<>();

    public static PortalScriptManager getInstance() {
        return instance;
    }

    /**
     * 加载并获取传送门脚本实例（带缓存）
     * 通过JS引擎加载脚本文件，获取PortalScript接口实现，并缓存结果
     *
     * @param scriptName 脚本名称（不含路径和扩展名）
     * @return 传送门脚本实例
     * @throws ScriptException 脚本未实现PortalScript接口时抛出
     */
    private PortalScript getPortalScript(String scriptName) throws ScriptException {
        String scriptPath = "portal/" + scriptName + ".js";
        PortalScript script = scripts.get(scriptPath);
        if (script != null) {
            return script;
        }

        ScriptEngine engine = getInvocableScriptEngine(scriptPath);
        if (!(engine instanceof Invocable iv)) {
            return null;
        }

        script = iv.getInterface(PortalScript.class);
        if (script == null) {
            throw new ScriptException(String.format("Portal script \"%s\" fails to implement the PortalScript interface", scriptName));
        }

        scripts.put(scriptPath, script);
        return script;
    }

    /**
     * 执行传送门入口脚本
     *
     * @param portal 传送门
     * @param c      客户端
     * @return 脚本是否成功执行，成功后玩家才能进入目标地图
     */
    public boolean executePortalScript(Portal portal, Client c) {
        try {
            String strPortalName = portal.getScriptName();
            if (GameConfig.getServerBoolean("use_debug") && c.getPlayer().isGM() )
            {
                c.getPlayer().dropMessage("您已建立与传送门脚本: " + strPortalName + ".js 的关联。");
            }
            PortalScript script = getPortalScript(strPortalName);
            if (script != null) {
                return script.enter(new PortalPlayerInteraction(c, portal));
            }
        } catch (Exception e) {

            log.warn("Portal script error in: {}", portal.getScriptName(), e);
        }
        return false;
    }

    /**
     * 清除所有已缓存的传送门脚本，触发重新加载
     */
    public void reloadPortalScripts() {
        scripts.clear();
    }
}