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
 * 【类型】ReactorScriptManager（class），包 {@code org.gms.scripting.reactor}。
 * 反应堆脚本管理器，单例模式。处理地图上反应堆（Reactor，如可破坏的箱子、矿石等）的交互事件，
 * 包括打击（hit）、触发（act）、接触/离开（touch/untouch）以及掉落物查询。
 * 脚本位于 {@code reactor/<id>.js}，向 JS 引擎注入 "{@code rm}" 变量（ReactorActionManager）。
 *
 * @author Lerk
 */
public class ReactorScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(ReactorScriptManager.class);
    /** 单例实例 */
    private static final ReactorScriptManager instance = new ReactorScriptManager();

    /** 反应堆ID -> 掉落物列表的缓存 */
    private final Map<Integer, List<ReactorDropEntry>> drops = new HashMap<>();

    public static ReactorScriptManager getInstance() {
        return instance;
    }

    /**
     * 反应堆被打击时的回调，执行对应 JS 脚本中的 {@code hit()} 函数（可选实现）。
     *
     * @param c       玩家客户端连接
     * @param reactor 被打击的反应堆
     */
    public void onHit(Client c, Reactor reactor) {
        try {
            Invocable iv = initializeInvocable(c, reactor);
            if (iv == null) {
                return;
            }

            iv.invokeFunction("hit");
        } catch (final NoSuchMethodException e) {
            // hit 方法是可选的，忽略
        } catch (final ScriptException | NullPointerException e) {
            log.error("Error during onHit script for reactor: {}", reactor.getId(), e);
        }
    }

    /**
     * 反应堆被触发（完成）时的回调，执行对应 JS 脚本中的 {@code act()} 函数。
     *
     * @param c       玩家客户端连接
     * @param reactor 被触发的反应堆
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
     * 获取指定反应堆的掉落物列表（从数据库查询并缓存）。
     *
     * @param reactorId 反应堆ID
     * @return 掉落物条目列表
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

    /** 清空掉落物缓存 */
    public void clearDrops() {
        drops.clear();
    }

    /** 反应堆被玩家接触时的回调 */
    public void touch(Client c, Reactor reactor) {
        touching(c, reactor, true);
    }

    /** 反应堆被玩家离开时的回调 */
    public void untouch(Client c, Reactor reactor) {
        touching(c, reactor, false);
    }

    /**
     * 处理反应堆的接触/离开事件。
     *
     * @param c        玩家客户端连接
     * @param reactor  反应堆
     * @param touching true=接触，false=离开
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
     * 初始化反应堆脚本引擎，加载 JS 文件并注入 "{@code rm}" 变量。
     *
     * @param c       玩家客户端连接
     * @param reactor 反应堆
     * @return 可调用的 JS 引擎接口，脚本不存在时返回 null
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