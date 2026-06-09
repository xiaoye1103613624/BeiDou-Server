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
package org.gms.scripting.item;

import org.gms.client.Client;
import org.gms.scripting.AbstractScriptManager;
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.server.ItemInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 物品脚本管理器
 * 负责加载和执行物品使用脚本，通过NPCScriptManager启动对应的JS脚本
 */
public class ItemScriptManager extends AbstractScriptManager {
    /** SLF4J日志实例 */
    private static final Logger log = LoggerFactory.getLogger(ItemScriptManager.class);
    /** 单例实例 */
    private static final ItemScriptManager instance = new ItemScriptManager();

    /**
     * 获取单例实例
     *
     * @return ItemScriptManager单例
     */
    public static ItemScriptManager getInstance() {
        return instance;
    }

    /**
     * 执行物品脚本
     *
     * @param c          客户端
     * @param itemScript 脚本物品对象
     */
    public void runItemScript(Client c, ItemInformationProvider.ScriptedItem itemScript) {
        NPCScriptManager.getInstance().start(c, itemScript, null);
        // for verified items, keep old id
    }
}