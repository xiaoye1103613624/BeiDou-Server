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
 * 【类型】EventScriptManager（class），包 {@code org.gms.scripting.event}。
 * 事件脚本管理器，加载 {@code event/*.js} 脚本，向 JS 引擎注入 "{@code em}" 变量（EventManager 实例），
 * 并负责所有事件的初始化、重载与销毁。
 *
 * @author Matze
 */
public class EventScriptManager extends AbstractScriptManager {
    /** SLF4J 日志实例 */
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(EventScriptManager.class);
    /** 注入到 JS 引擎的变量名 */
    private static final String INJECTED_VARIABLE_NAME = "em";
    /** 后备事件（如 "0_EXAMPLE"），当请求的事件不存在时返回 */
    private static EventEntry fallback;
    /** 事件名 -> EventEntry 的映射，线程安全 */
    private final Map<String, EventEntry> events = new ConcurrentHashMap<>();
    /** 管理器是否已激活（至少有一个非后备事件加载成功） */
    private boolean active = false;

    /**
     * 事件条目，封装单个事件的 JS 可调用接口及其 EventManager。
     */
    private static class EventEntry {
        /** 线程安全的 JS 可调用接口 */
        public Invocable iv;
        /** 对应的事件管理器 */
        public EventManager em;

        public EventEntry(Invocable iv, EventManager em) {
            this.iv = iv;
            this.em = em;
        }
    }

    /**
     * 构造函数，加载并初始化所有事件脚本。
     *
     * @param channel 游戏频道上下文
     * @param scripts 事件脚本名称数组
     */
    public EventScriptManager(final Channel channel, String[] scripts) {
        for (String script : scripts) {
            if (!script.isEmpty()) {
                events.put(script, initializeEventEntry(script, channel));
            }
        }

        init();
        fallback = events.remove("0_EXAMPLE");
    }

    /**
     * 获取指定事件名对应的 EventManager。
     *
     * @param event 事件名称
     * @return 对应的事件管理器，若不存在则返回后备事件
     */
    public EventManager getEventManager(String event) {
        EventEntry entry = events.get(event);
        if (entry == null) {
            return fallback.em;
        }
        return entry.em;
    }

    /**
     * 管理器是否已激活。
     *
     * @return true 表示已激活
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 调用每个事件脚本的 {@code init()} 函数，完成事件初始化。
     */
    public final void init() {
        for (EventEntry entry : events.values()) {
            try {
                entry.iv.invokeFunction("init", (Object) null);
            } catch (Exception ex) {
                log.error("Error on script（事件脚本初始化出错）: {}", entry.em.getName(), ex);
            }
        }

        active = events.size() > 1;
    }

    /**
     * 重新加载所有事件脚本（热重载）。
     */
    private void reloadScripts() {
        Set<Entry<String, EventEntry>> eventEntries = new HashSet<>(events.entrySet());
        if (eventEntries.isEmpty()) {
            return;
        }

        Channel channel = eventEntries.iterator().next().getValue().em.getChannelServer();
        for (Entry<String, EventEntry> entry : eventEntries) {
            String script = entry.getKey();
            events.put(script, initializeEventEntry(script, channel));
        }
    }

    /**
     * 加载单个事件脚本并返回对应的 EventEntry。
     *
     * @param script  脚本名称（不含路径前缀和扩展名）
     * @param channel 游戏频道
     * @return 封装了 JS 引擎和 EventManager 的事件条目
     */
    private EventEntry initializeEventEntry(String script, Channel channel) {
        ScriptEngine engine = getInvocableScriptEngine("event/" + script + ".js");
        Invocable iv = SynchronizedInvocable.of((Invocable) engine);
        EventManager eventManager = new EventManager(channel, iv, script);
        engine.put(INJECTED_VARIABLE_NAME, eventManager);
        return new EventEntry(iv, eventManager);
    }

    /**
     * 重载所有事件脚本（取消 -> 重载 -> 初始化）。
     */
    public void reload() {
        cancel();
        reloadScripts();
        init();
    }

    /**
     * 取消所有正在运行的事件。
     */
    public void cancel() {
        active = false;
        for (EventEntry entry : events.values()) {
            entry.em.cancel();
        }
    }

    /**
     * 销毁事件管理器，取消所有事件并清空映射。
     */
    public void dispose() {
        if (events.isEmpty()) {
            return;
        }

        Set<EventEntry> eventEntries = new HashSet<>(events.values());
        events.clear();

        active = false;
        for (EventEntry entry : eventEntries) {
            entry.em.cancel();
        }
    }
}