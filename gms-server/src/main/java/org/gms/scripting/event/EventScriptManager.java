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
package org.gms.scripting.event;

import org.gms.net.server.channel.Channel;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;
import org.gms.scripting.SynchronizedInvocable;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件脚本管理器，负责加载、初始化和执行 JavaScript 事件脚本
 * @author Matze
 */
public class EventScriptManager extends AbstractScriptManager {
    /** SLF4J 日志实例 */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EventScriptManager.class);
    /** 注入到 JS 引擎的变量名 */
    private static final String INJECTED_VARIABLE_NAME = "em";
    /** 后备事件（如默认事件 0_EXAMPLE） */
    private static EventEntry fallback;
    /** 存储事件名与事件实体的映射，使用ConcurrentHashMap保证线程安全 */
    private final Map<String, EventEntry> events = new ConcurrentHashMap<>();
    /** 管理器是否激活 */
    private boolean active = false;

    /**
     * 事件实体类，封装 JS 调用接口和事件管理器
     */
    private static class EventEntry {
        /** 可调用的 JS 引擎接口（线程安全包装） */
        public Invocable iv;
        /** 事件管理器 */
        public EventManager em;

        /**
         * 构造函数
         *
         * @param iv 可调用的 JS 引擎接口
         * @param em 事件管理器
         */
        public EventEntry(Invocable iv, EventManager em) {
            this.iv = iv;
            this.em = em;
        }
    }

    /**
     * 构造函数，加载并初始化所有事件脚本
     * @param channel 游戏频道（上下文）
     * @param scripts 事件脚本名称数组
     */
    public EventScriptManager(final Channel channel, String[] scripts) {
        for (String script : scripts) {
            if (!script.isEmpty()) {
                // 加载每个事件脚本并创建对应的EventEntry
                events.put(script, initializeEventEntry(script, channel));
            }
        }

        // 初始化所有事件（调用每个脚本的init函数）
        init();
        // 移除示例事件作为后备
        fallback = events.remove("0_EXAMPLE");
    }

    /**
     * 获取指定事件的事件管理器
     * @param event 事件名称
     * @return 对应的事件管理器，若不存在则返回后备事件
     */
    public EventManager getEventManager(String event) {
        EventEntry entry = events.get(event);
        if (entry == null) {
            // 未找到时返回后备事件
            return fallback.em;
        }
        return entry.em;
    }

    /**
     * 检查事件管理器是否激活
     * @return true 表示已激活
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 初始化所有事件脚本，调用其 JS 中的 init() 函数
     */
    public final void init() {
        for (EventEntry entry : events.values()) {
            try {
                // 调用每个JS脚本的init函数进行初始化
                entry.iv.invokeFunction("init", (Object) null);
            } catch (Exception ex) {
                log.error("Error on script（事件脚本初始化出错）: {}", entry.em.getName(), ex);
            }
        }

        // 事件数大于1时标记为激活状态（单个示例事件不算激活）
        active = events.size() > 1;
    }

    /**
     * 重新加载所有事件脚本
     */
    private void reloadScripts() {
        // 复制当前事件集合，避免遍历时修改
        Set<Entry<String, EventEntry>> eventEntries = new HashSet<>(events.entrySet());
        if (eventEntries.isEmpty()) {
            return;
        }

        // 从任意事件获取频道上下文（所有事件共用同一频道）
        Channel channel = eventEntries.iterator().next().getValue().em.getChannelServer();
        for (Entry<String, EventEntry> entry : eventEntries) {
            String script = entry.getKey();
            // 重新加载每个脚本文件并替换旧实体
            events.put(script, initializeEventEntry(script, channel));
        }
    }

    /**
     * 初始化单个事件脚本的入口
     * @param script 脚本名称
     * @param channel 游戏频道
     * @return 事件实体（包含 JS 引擎和事件管理器）
     */
    private EventEntry initializeEventEntry(String script, Channel channel) {
        // 加载JS脚本并包装为线程安全的调用接口
        ScriptEngine engine = getInvocableScriptEngine("event/" + script + ".js");
        Invocable iv = SynchronizedInvocable.of((Invocable) engine);
        // 创建事件管理器并注入到JS引擎的全局变量"em"中
        EventManager eventManager = new EventManager(channel, iv, script);
        engine.put(INJECTED_VARIABLE_NAME, eventManager);
        return new EventEntry(iv, eventManager);
    }

    /**
     * 重新加载所有事件脚本（外部调用入口）
     */
    public void reload() {
        // 先取消所有运行中的事件
        cancel();
        // 重新加载脚本文件
        reloadScripts();
        // 重新初始化所有事件
        init();
    }

    /**
     * 取消所有事件执行
     */
    public void cancel() {
        // 标记为非激活状态
        active = false;
        for (EventEntry entry : events.values()) {
            // 调用每个事件的取消方法清理资源
            entry.em.cancel();
        }
    }

    /**
     * 销毁事件管理器，清理资源
     */
    public void dispose() {
        if (events.isEmpty()) {
            return;
        }

        // 复制后立即清空原映射，防止并发修改
        Set<EventEntry> eventEntries = new HashSet<>(events.values());
        events.clear();

        // 标记非激活并取消所有事件
        active = false;
        for (EventEntry entry : eventEntries) {
            entry.em.cancel();
        }
    }
}