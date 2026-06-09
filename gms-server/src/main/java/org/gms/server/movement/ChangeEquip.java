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
package org.gms.server.movement;

import org.gms.net.packet.OutPacket;
import java.awt.*;

/**
 * 换装备动作
 * 表示生命体换装备的动作，不改变位置只改变外观
 * 实现LifeMovementFragment接口
 */
public class ChangeEquip implements LifeMovementFragment {
    /** 武器 unequip 标识 */
    private final int wui;

    /**
     * 构造换装备动作
     *
     * @param wui 武器 unequip 标识（0=装备武器，1=卸下武器）
     */
    public ChangeEquip(int wui) {
        this.wui = wui;
    }

    /**
     * 序列化换装备动作到数据包
     * <p>
     * 写入固定类型 10（表示换装备动作），后跟武器状态
     *
     * @param p 输出包
     */
    @Override
    public void serialize(OutPacket p) {
        p.writeByte(10);
        p.writeByte(wui);
    }

    /**
     * 换装备不改变位置，返回原点
     *
     * @return (0, 0)
     */
    @Override
    public Point getPosition() {
        return new Point(0, 0);
    }
}