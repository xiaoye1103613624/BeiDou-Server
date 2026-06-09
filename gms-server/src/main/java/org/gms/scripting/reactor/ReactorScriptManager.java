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
package org.gms.scripting.reactor;

import org.gms.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;
import org.gms.server.maps.Reactor;
import org.gms.server.maps.ReactorDropEntry;
import org.gms.util.DatabaseConnection;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 反应堆脚本管理器（单例）
 * 管理反应堆（Reactor）脚本的加载、执行和掉落配置
 */
public class ReactorScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(ReactorScriptManager.class);
    private static final ReactorScriptManager instance = new ReactorScriptManager();

    /** 反应堆ID到掉落条目列表的映射缓存 */
    private final Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

    public static ReactorScriptManager getInstance() {
        return instance;
    }

    /**
     * 触发反应堆击打脚本hit函数
     *
     * @param c       客户端
     * @param reactor 反应堆
     */
    public void onHit(Client c, Reactor reactor) {
        try {
            Invocable iv = initializeInvocable(c, reactor);
            if (iv == null) {
                return;
            }

            iv.invokeFunction("hit");
        } catch (final NoSuchMethodException e) {
            //do nothing, hit is OPTIONAL
        } catch (final ScriptException | NullPointerException e) {
            log.error("Error during onHit script for reactor: {}", reactor.getId(), e);
        }
    }

    /**
     * 触发反应堆动作脚本act函数
     *
     * @param c       客户端
     * @param reactor 反应堆
     */
    public void act(Client c, Reactor reactor) {
        try {
            Invocable iv = initializeInvocable(c, reactor);
            if (iv == null) {
                return;
            }

            iv.invokeFunction("act");
        } catch (final ScriptException | NoSuchMethodException | NullPointerException e) {
            log.error("Error during act script for reactor: {}", reactor.getId(), e);
        }
    }

    /**
     * 获取反应堆掉落配置列表（带缓存）
     * 首次查询时从数据库加载，后续命中缓存
     *
     * @param reactorId 反应堆ID
     * @return 掉落条目列表
     */
    public List<ReactorDropEntry> getDrops(int reactorId) {
        List<ReactorDropEntry> ret = drops.get(reactorId);
        if (ret == null) {
            ret = new LinkedList<>();
            try (Connection con = DatabaseConnection.getConnection()) {
                try (PreparedStatement ps = con.prepareStatement("SELECT itemid, chance, questid FROM reactordrops WHERE reactorid = ? AND chance >= 0")) {
                    ps.setInt(1, reactorId);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            ret.add(new ReactorDropEntry(rs.getInt("itemid"), rs.getInt("chance"), rs.getInt("questid")));
                        }
                    }
                }
            } catch (Throwable e) {
                log.error("Error getting drops for reactor: {}", reactorId);
            }
            drops.put(reactorId, ret);
        }
        return ret;
    }

    /**
     * 清除掉落配置缓存
     */
    public void clearDrops() {
        drops.clear();
    }

    /**
     * 触发反应堆触碰脚本touch函数
     *
     * @param c       客户端
     * @param reactor 反应堆
     */
    public void touch(Client c, Reactor reactor) {
        touching(c, reactor, true);
    }

    /**
     * 触发反应堆离开脚本untouch函数
     *
     * @param c       客户端
     * @param reactor 反应堆
     */
    public void untouch(Client c, Reactor reactor) {
        touching(c, reactor, false);
    }

    /**
     * 执行触碰/离开脚本
     *
     * @param c        客户端
     * @param reactor  反应堆
     * @param touching true=触碰, false=离开
     */
    private void touching(Client c, Reactor reactor, boolean touching) {
        final String functionName = touching ? "touch" : "untouch";
        try {
            Invocable iv = initializeInvocable(c, reactor);
            if (iv == null) {
                return;
            }

            iv.invokeFunction(functionName);
        } catch (final ScriptException | NoSuchMethodException | NullPointerException e) {
            log.error("Error during {} script for reactor: {}", functionName, reactor.getId(), e);
        }
    }

    /**
     * 初始化脚本可调用对象
     * 加载反应堆JS脚本，创建ReactantActionManager并注入到JS引擎上下文
     *
     * @param c       客户端
     * @param reactor 反应堆
     * @return 可调用对象，脚本不存在则返回null
     */
    private Invocable initializeInvocable(Client c, Reactor reactor) {
        ScriptEngine engine = getInvocableScriptEngine("reactor/" + reactor.getId() + ".js", c);
        if (engine == null) {
            return null;
        }

        Invocable iv = (Invocable) engine;
        ReactorActionManager rm = new ReactorActionManager(c, reactor, iv);
        engine.put("rm", rm);

        return iv;
    }
}