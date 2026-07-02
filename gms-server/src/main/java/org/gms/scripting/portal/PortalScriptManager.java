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
import java.util.HashMap;
import java.util.Map;

/**
 * 传送门脚本管理器（单例）
 * 负责加载、缓存和执行传送门JS脚本
 */
public class PortalScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(PortalScriptManager.class);
    private static final PortalScriptManager instance = new PortalScriptManager();

    /** 脚本引擎缓存：避免每次传送都创建新GraalJS引擎 */
    private final Map<String, Invocable> scripts = new HashMap<>();

    public static PortalScriptManager getInstance() {
        return instance;
    }

    /**
     * 执行传送门入口脚本
     * 使用invokeFunction直接调用JS函数，避免getInterface严格类型转换导致的undefined→boolean NPE
     */
    public boolean executePortalScript(Portal portal, Client c) {
        try {
            String scriptName = portal.getScriptName();
            if (GameConfig.getServerBoolean("use_debug") && c.getPlayer().isGM()) {
                c.getPlayer().dropMessage("您已建立与传送门脚本: " + scriptName + ".js 的关联。");
            }

            String scriptPath = "portal/" + scriptName + ".js";
            Invocable iv = scripts.get(scriptPath);
            if (iv == null) {
                ScriptEngine engine = getInvocableScriptEngine(scriptPath);
                if (engine instanceof Invocable inv) {
                    iv = inv;
                    scripts.put(scriptPath, iv);
                } else {
                    return false;
                }
            }

            Object result = iv.invokeFunction("enter", new PortalPlayerInteraction(c, portal));
            if (result instanceof Boolean b) {
                return b;
            }
            return true;
        } catch (Exception e) {
            log.warn("Portal script error in: {}", portal.getScriptName(), e);
        }
        return false;
    }

    /**
     * 清除脚本缓存
     */
    public void reloadPortalScripts() {
        scripts.clear();
    }
}