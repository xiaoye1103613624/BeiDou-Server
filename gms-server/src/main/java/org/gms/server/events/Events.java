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

package org.gms.server.events;

/**
 * 游戏事件抽象基类
 * 定义游戏事件的基本接口，子类需实现getInfo()返回事件信息
 *
 * @author kevintjuh93
 */
public abstract class Events {
    /**
     * 构造游戏事件
     */
    public Events() {

    }

    /**
     * 获取事件信息
     *
     * @return 事件信息值
     */
    public abstract int getInfo();
}