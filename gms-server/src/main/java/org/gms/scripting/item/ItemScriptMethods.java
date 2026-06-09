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
import org.gms.scripting.AbstractPlayerInteraction;

/**
 * 物品脚本方法
 * 为物品脚本提供可调用的玩家操作方法，继承自AbstractPlayerInteraction
 */
public class ItemScriptMethods extends AbstractPlayerInteraction {
    /**
     * 构造函数
     *
     * @param c 客户端
     */
    public ItemScriptMethods(Client c) {
        super(c);
    }
}