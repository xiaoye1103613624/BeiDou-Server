package org.gms.server.movement;
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

import org.gms.net.packet.OutPacket;
import org.gms.server.movement.AbstractLifeMovement;

import java.awt.*;

/**
 * 相对位置移动
 * 相对于当前位置的移动，用于小范围路径调整
 */
public class RelativeLifeMovement extends AbstractLifeMovement {
    /**
     * 构造相对位置移动
     *
     * @param type     移动类型
     * @param position 相对偏移位置
     * @param duration 持续时间
     * @param newstate 移动后状态
     */
    public RelativeLifeMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
    }

    /**
     * 序列化相对移动到数据包
     * <p>
     * 只包含类型、位置、新状态和持续时间，无需额外字段
     *
     * @param p 输出包
     */
    @Override
    public void serialize(OutPacket p) {
        p.writeByte(getType());
        p.writePos(getPosition());
        p.writeByte(getNewstate());
        p.writeShort(getDuration());
    }
}