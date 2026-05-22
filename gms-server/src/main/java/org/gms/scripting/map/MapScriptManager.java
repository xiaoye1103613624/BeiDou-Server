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
package org.gms.scripting.map;

import org.gms.client.Character;
import org.gms.client.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.scripting.AbstractScriptManager;

import javax.script.Invocable;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

/**
 * 【类型】MapScriptManager（class），包 {@code org.gms.scripting.map}。
 * 地图脚本管理器，单例模式。当玩家进入某张地图时触发对应脚本（位于 {@code map/*.js}），
 * 脚本通过 {@code MapScriptMethods} 与玩家交互。同一地图脚本加载后缓存，避免重复编译。
 * 首次进入的玩家会记录已执行标记，防止重复触发。
 */
public class MapScriptManager extends AbstractScriptManager {
    private static final Logger log = LoggerFactory.getLogger(MapScriptManager.class);
    /** 单例实例 */
    private static final MapScriptManager instance = new MapScriptManager();

    /** 地图脚本路径 -> 已编译的 JS 可调用接口的缓存 */
    private final Map<String, Invocable> scripts = new HashMap<>();

    public static MapScriptManager getInstance() {
        return instance;
    }

    /** 清空脚本缓存（热重载时调用） */
    public void reloadScripts() {
        scripts.clear();
    }

    /**
     * 执行地图脚本。
     *
     * @param c             客户端连接
     * @param mapScriptPath 地图脚本路径（不含 {@code map/} 前缀与 {@code .js} 后缀）
     * @param firstUser     该玩家是否为此地图的第一个进入者
     * @return true 表示脚本执行成功
     */
    public boolean runMapScript(Client c, String mapScriptPath, boolean firstUser) {
        if (firstUser) {
            Character chr = c.getPlayer();
            int mapid = chr.getMapId();
            if (chr.hasEntered(mapScriptPath, mapid)) {
                return false;
            } else {
                chr.enteredScript(mapScriptPath, mapid);
            }
        }

        Invocable iv = scripts.get(mapScriptPath);
        if (iv != null) {
            try {
                iv.invokeFunction("start", new MapScriptMethods(c));
                return true;
            } catch (final ScriptException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }

        try {
            iv = (Invocable) getInvocableScriptEngine("map/" + mapScriptPath + ".js");
            if (iv == null) {
                return false;
            }

            scripts.put(mapScriptPath, iv);
            iv.invokeFunction("start", new MapScriptMethods(c));
            return true;
        } catch (final Exception e) {
            log.error("Error running map script {}", mapScriptPath, e);
        }

        return false;
    }
}