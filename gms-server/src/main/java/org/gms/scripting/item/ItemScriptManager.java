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
import org.gms.scripting.npc.NPCScriptManager;
import org.gms.server.ItemInformationProvider.ScriptedItem;

/**
 * 【类型】ItemScriptManager（class），包 {@code org.gms.scripting.item}。
 * 物品脚本管理器，单例模式。收到物品脚本执行请求后，直接委托给 {@link NPCScriptManager} 处理，
 * 将 {@link ScriptedItem} 作为脚本上下文传入。仅 ID 在 243xxxx 范围的物品会触发此管理器。
 */
public class ItemScriptManager {

    /** 单例实例 */
    private static final ItemScriptManager instance = new ItemScriptManager();

    public static ItemScriptManager getInstance() {
        return instance;
    }

    /**
     * 运行物品脚本，委托 NPCScriptManager 执行。
     *
     * @param c          客户端连接
     * @param scriptItem 带脚本信息的物品实体
     */
    public void runItemScript(Client c, ScriptedItem scriptItem) {
        NPCScriptManager.getInstance().start(c, scriptItem, null);
    }
}