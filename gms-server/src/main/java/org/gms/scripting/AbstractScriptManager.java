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
package org.gms.scripting;

import org.gms.client.Client;
import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 【类型】AbstractScriptManager（abstract class），包 `org.gms.scripting`。
 *
 * 脚本管理器抽象基类，封装 GraalVM JS 脚本引擎的加载、执行和缓存逻辑。
 * 所有具体的脚本管理器（NPC、任务、传送门、事件、反应堆等）均继承此类，
 * 通过 {@link #getInvocableScriptEngine(String, Client)} 获取可调用的脚本引擎实例。
 *
 * 脚本加载策略：优先查找语言后缀目录（如 scripts-zh-CN），不存在则回退到 scripts 目录。
 * 每个客户端连接缓存已加载的脚本引擎，避免重复 IO 和编译。
 *
 * @author Matze
 */
public abstract class AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(AbstractScriptManager.class);
    /** GraalVM JS 脚本引擎工厂，用于创建新的引擎实例 */
    private final ScriptEngineFactory sef;

    protected AbstractScriptManager() {
        sef = new ScriptEngineManager().getEngineByName("graal.js").getFactory();
    }

    /**
     * 从指定路径加载 JS 脚本并返回可调用的脚本引擎。
     * 优先加载语言后缀目录（如 scripts-zh-CN），否则回退到 scripts 目录。
     *
     * @param path 脚本相对路径（不含 scripts 前缀和 .js 后缀）
     * @return 已 eval 脚本的引擎实例，加载失败返回 null
     */
    protected ScriptEngine getInvocableScriptEngine(String path) {
        // 优先取语言文件夹，没有则取scripts
        String scriptName = "scripts";
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        String scriptLangName = scriptName + "-" + serviceProperty.getLanguage();

        Path scriptPath = Path.of(scriptName, path);
        Path scriptLangPath = Path.of(scriptLangName, path);

        Path actualPath;
        if (Files.exists(scriptLangPath)) {
            actualPath = scriptLangPath;
        } else if (Files.exists(scriptPath)){
            actualPath = scriptPath;
        } else {
            return null;
        }

        ScriptEngine engine = sef.getScriptEngine();
        if (!(engine instanceof GraalJSScriptEngine graalScriptEngine)) {
            throw new IllegalStateException(I18nUtil.getExceptionMessage("AbstractScriptManager.getInvocableScriptEngine.exception1"));
        }

        enableScriptHostAccess(graalScriptEngine);

        try (BufferedReader br = Files.newBufferedReader(actualPath, StandardCharsets.UTF_8)) {
            engine.eval(br);
        } catch (final ScriptException | IOException t) {
            log.warn(I18nUtil.getLogMessage("AbstractScriptManager.getInvocableScriptEngine.warn1"), path, t);
            return null;
        }

        return graalScriptEngine;
    }

    /**
     * 从客户端缓存获取脚本引擎，缓存未命中时加载并缓存。
     *
     * @param path 脚本相对路径（不含 scripts 前缀和 .js 后缀）
     * @param c    客户端连接（用于引擎缓存）
     * @return 已 eval 脚本的引擎实例，加载失败返回 null
     */
    protected ScriptEngine getInvocableScriptEngine(String path, Client c) {
        ScriptEngine engine = c.getScriptEngine("scripts/" + path);
        if (engine == null) {
            engine = getInvocableScriptEngine(path);
            c.setScriptEngine(path, engine);
        }

        return engine;
    }

    /** 启用 GraalJS 引擎对 Java 主机类的访问权限（允许脚本中使用 Java.type()） */
    private void enableScriptHostAccess(GraalJSScriptEngine engine) {
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("polyglot.js.allowHostAccess", true);
        bindings.put("polyglot.js.allowHostClassLookup", true);
    }

    /** 从客户端缓存中移除指定脚本引擎，用于脚本热重载 */
    protected void resetContext(String path, Client c) {
        c.removeScriptEngine("scripts/" + path);
    }
}
