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
 * 地图脚本管理器
 * 管理地图JavaScript脚本的加载和执行，处理首次进入地图/在地图内用户触发的脚本
 */
public class MapScriptManager extends AbstractScriptManager {
    /** SLF4J日志实例 */
    private static final Logger log = LoggerFactory.getLogger(MapScriptManager.class);
    /** 单例实例 */
    private static final MapScriptManager instance = new MapScriptManager();

    /** 脚本路径到JS调用接口的缓存映射 */
    private final Map<String, Invocable> scripts = new HashMap<>();

    /**
     * 获取单例实例
     *
     * @return MapScriptManager单例
     */
    public static MapScriptManager getInstance() {
        return instance;
    }

    /**
     * 重新加载所有脚本（清空缓存）
     */
    public void reloadScripts() {
        scripts.clear();
    }

    /**
     * 执行地图脚本
     *
     * @param c             客户端
     * @param mapScriptPath 地图脚本路径
     * @param firstUser     是否是首次进入的用户
     * @return 是否成功执行
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