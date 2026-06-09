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
package org.gms.net.server;

import org.gms.server.StatEffect;

/**
 * 玩家Buff值持有者
 * 记录Buff效果的持续时间和来源效果
 */
public class PlayerBuffValueHolder {
    /** 已使用时间 */
    public int usedTime;
    /** Buff效果对象 */
    public StatEffect effect;

    /**
     * 构造Buff值持有者
     *
     * @param usedTime 已使用时间
     * @param effect   Buff效果对象
     */
    public PlayerBuffValueHolder(int usedTime, StatEffect effect) {
        this.usedTime = usedTime;
        this.effect = effect;
    }
}