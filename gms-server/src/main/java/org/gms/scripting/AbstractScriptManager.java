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
 * 抽象脚本管理器
 * 所有脚本管理器的基类，提供GraalJS脚本引擎的创建、脚本文件加载和执行能力
 */
public abstract class AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(AbstractScriptManager.class);
    /** 脚本根目录名 */
    private static final String SCRIPT_DIRECTORY = "scripts";
    /** GraalJS脚本引擎工厂 */
    private final ScriptEngineFactory sef;

    /**
     * Nashorn兼容层polyfill脚本
     * GraalJS不支持Nashorn的importPackage/importClass/load("nashorn:mozilla_compat.js")等全局函数，
     * 此polyfill提供空实现，确保使用这些遗留API的脚本能正常加载。
     * 实际业务代码均使用全限定类名（如java.lang.System），不依赖importPackage的符号导入。
     */
    private static final String NASHORN_COMPAT_SCRIPT = """
            // Nashorn兼容层polyfill（GraalJS不支持这些Nashorn全局函数）
            this.importPackage = function(pkg) { /* no-op: GraalJS中通过Packages.xxx访问 */ };
            this.importClass = function(cls) { /* no-op */ };
            this.load = function(path) {
                if (path === 'nashorn:mozilla_compat.js') {
                    return; // Nashorn兼容脚本已在polyfill中覆盖，无需加载
                }
                throw new Error('load() not supported in GraalJS: ' + path);
            };
            // Nashorn兼容：GraalJS中Java String被转为JS string，无.equals()方法
            // 使用===严格相等避免类型强制转换（用==会导致"0"==0为true等非预期行为）
            if (typeof String.prototype.equals === 'undefined') {
                String.prototype.equals = function(other) {
                    return this === other;
                };
            }
            """;

    /**
     * 构造函数
     * 初始化GraalJS脚本引擎工厂
     */
    protected AbstractScriptManager() {
        sef = new ScriptEngineManager().getEngineByName("graal.js").getFactory();
    }

    /**
     * 获取可调用的脚本引擎（加载指定路径的JS脚本）
     * 优先加载语言文件夹下的脚本，不存在则回退到默认scripts目录
     *
     * @param path 脚本文件相对路径
     * @return 脚本引擎实例，脚本不存在则返回null
     */
    protected ScriptEngine getInvocableScriptEngine(String path) {
        // 读取当前服务端语言配置，用于拼出 scripts-语言 目录名，例如 scripts-zh-CN。
        ServiceProperty serviceProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class);

        // 默认脚本目录始终是 scripts，里面保留英文原版脚本。
        Path scriptPath = Path.of(SCRIPT_DIRECTORY, path);
        // 语言脚本目录只放已本地化的脚本文件，不要求复制完整 scripts 目录。
        Path scriptLangPath = Path.of(SCRIPT_DIRECTORY + "-" + serviceProperty.getLanguage(), path);

        // 兼容从项目根目录启动（如IDEA默认配置）：尝试 gms-server/ 子目录
        Path gmsScriptPath = Path.of("gms-server", SCRIPT_DIRECTORY, path);
        Path gmsScriptLangPath = Path.of("gms-server", SCRIPT_DIRECTORY + "-" + serviceProperty.getLanguage(), path);

        // 按文件级别选择脚本：先找语言文件，找不到再回退到英文原版文件，最后尝试gms-server子目录。
        Path actualPath;
        if (Files.exists(scriptLangPath)) {
            actualPath = scriptLangPath;
        } else if (Files.exists(scriptPath)) {
            actualPath = scriptPath;
        } else if (Files.exists(gmsScriptLangPath)) {
            actualPath = gmsScriptLangPath;
        } else if (Files.exists(gmsScriptPath)) {
            actualPath = gmsScriptPath;
        } else {
            return null;
        }

        // 为本次实际命中的脚本文件创建独立 JS 引擎。
        ScriptEngine engine = sef.getScriptEngine();
        if (!(engine instanceof GraalJSScriptEngine graalScriptEngine)) {
            throw new IllegalStateException(I18nUtil.getExceptionMessage("AbstractScriptManager.getInvocableScriptEngine.exception1"));
        }

        // 开启脚本访问 Java 类的能力，保持现有脚本里的 Java.type 调用可用。
        enableScriptHostAccess(graalScriptEngine);

        // 用 UTF-8 读取并执行脚本；执行失败时返回 null，让调用方按原逻辑处理缺失脚本。
        try (BufferedReader br = Files.newBufferedReader(actualPath, StandardCharsets.UTF_8)) {
            // 先注入Nashorn兼容层，再执行脚本（解决importPackage/load等未定义问题）
            engine.eval(NASHORN_COMPAT_SCRIPT);
            engine.eval(br);
        } catch (final ScriptException | IOException t) {
            log.warn(I18nUtil.getLogMessage("AbstractScriptManager.getInvocableScriptEngine.warn1"), path, t);
            return null;
        }

        return graalScriptEngine;
    }

    /**
     * 获取可调用的脚本引擎（带客户端缓存）
     * 先从客户端缓存获取，缓存未命中则加载并存入缓存
     *
     * @param path 脚本文件相对路径
     * @param c    客户端
     * @return 脚本引擎实例，脚本不存在则返回null
     */
    protected ScriptEngine getInvocableScriptEngine(String path, Client c) {
        // 缓存键统一使用默认脚本前缀加相对路径，避免读取和写入缓存时使用不同 key。
        String scriptKey = SCRIPT_DIRECTORY + "/" + path;
        ScriptEngine engine = c.getScriptEngine(scriptKey);
        if (engine == null) {
            // 客户端当前没有缓存时，再按文件级 i18n 规则加载脚本。
            engine = getInvocableScriptEngine(path);
            c.setScriptEngine(scriptKey, engine);
        }

        return engine;
    }

    /**
     * 允许脚本通过 Java.type() 查找并调用服务端 Java 类。
     */
    private void enableScriptHostAccess(GraalJSScriptEngine engine) {
        // GraalJS 的 host 访问开关需要写入引擎作用域绑定。
        Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);
        bindings.put("polyglot.js.allowHostAccess", true);
        bindings.put("polyglot.js.allowHostClassLookup", true);
    }

    /**
     * 重置脚本上下文
     * 从客户端缓存中移除指定脚本引擎，以便下次重新加载
     *
     * @param path 脚本文件相对路径
     * @param c    客户端
     */
    protected void resetContext(String path, Client c) {
        // 重置时使用同一个缓存 key，确保能清掉上面 setScriptEngine 写入的脚本引擎。
        c.removeScriptEngine(SCRIPT_DIRECTORY + "/" + path);
    }
}
