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

import com.oracle.truffle.js.scriptengine.GraalJSScriptEngine;
import org.gms.client.Client;
import org.gms.manager.ServerManager;
import org.gms.property.ServiceProperty;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.script.*;

/**
 * 脚本管理器 抽象类
 * @author Matze
 */
public abstract class AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(AbstractScriptManager.class);
    // 脚本引擎工厂对象，用于创建脚本引擎实例
    private final ScriptEngineFactory scriptEngineFactory;

    protected AbstractScriptManager() {
        scriptEngineFactory = new ScriptEngineManager().getEngineByName("graal.js").getFactory();
    }

    /**
     * 构建脚本引擎
     *
     * @param path 脚本文件路径
     * @return 可调用的脚本引擎对象
     */
    protected ScriptEngine getInvocableScriptEngine(String path) {
        // 优先取语言文件夹，没有则取scripts
        String scriptName = "scripts";
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);
        // 获取语言文件夹名称，例如 "scripts-zh_CN" 或者 "scripts-en_US"
        String scriptLangName = scriptName + "-" + serviceProperty.getLanguage();
        // 获取脚本文件路径
        Path scriptPath = Path.of(scriptName, path);
        // 获取语言文件夹下的脚本文件路径
        Path scriptLangPath = Path.of(scriptLangName, path);

        Path actualPath;
        if (Files.exists(scriptLangPath)) {
            actualPath = scriptLangPath;
        } else if (Files.exists(scriptPath)){
            actualPath = scriptPath;
        } else {
            return null;
        }
        // 初始化脚本引擎对象
        ScriptEngine engine = scriptEngineFactory.getScriptEngine();
        if (!(engine instanceof GraalJSScriptEngine graalScriptEngine)) {
            // 如果脚本引擎不是GraalJSScriptEngine，则抛出异常
            throw new IllegalStateException(I18nUtil.getExceptionMessage("AbstractScriptManager.getInvocableScriptEngine.exception1"));
        }

        enableScriptHostAccess(graalScriptEngine);

        try (BufferedReader br = Files.newBufferedReader(actualPath, StandardCharsets.UTF_8)) {
            // 加载脚本文件
            engine.eval(br);
        } catch (final ScriptException | IOException t) {
            // 脚本存在语法错误
            log.warn(I18nUtil.getLogMessage("AbstractScriptManager.getInvocableScriptEngine.warn1"), path, t);
            return null;
        }

        return graalScriptEngine;
    }

    /**
     * 根据指定路径 构建可用的脚本引擎。
     *
     * @param path 脚本文件的路径，相对于 "scripts/" 目录
     * @param c    客户端对象，用于缓存脚本引擎
     * @return 可调用的脚本引擎
     */
    protected ScriptEngine getInvocableScriptEngine(String path, Client c) {
        ScriptEngine engine = c.getScriptEngine("scripts/" + path);
        if (engine == null) {
            engine = getInvocableScriptEngine(path);
            c.setScriptEngine(path, engine);
        }

        return engine;
    }

    /**
     * 允许在脚本 直接对宿主java程序访问 和 宿主类加载、查找；
     * Allow usage of "Java.type()" in script to look up host class
     */
    private void enableScriptHostAccess(GraalJSScriptEngine engine) {
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        // 允许访问宿主（Java）环境
        bindings.put("polyglot.js.allowHostAccess", true);
        // 允许查找任何Java类
        bindings.put("polyglot.js.allowHostClassLookup", true);
    }

    protected void resetContext(String path, Client c) {
        c.removeScriptEngine("scripts/" + path);
    }
}
