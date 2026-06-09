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
 * 椅子移动
 * 玩家坐到椅子上的移动动作，指定椅子站脚点
 */
public class ChairMovement extends AbstractLifeMovement {
    /** 椅子站脚点ID */
    private int fh;

    /**
     * 构造椅子移动
     *
     * @param type     移动类型
     * @param position 椅子位置
     * @param duration 持续时间
     * @param newstate 坐下后状态
     */
    public ChairMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
    }

    /**
     * 获取椅子站脚点ID
     *
     * @return 站脚点ID
     */
    public int getFh() {
        return fh;
    }

    /**
     * 设置椅子站脚点ID
     *
     * @param fh 站脚点ID
     */
    public void setFh(int fh) {
        this.fh = fh;
    }

    @Override
    public void serialize(OutPacket p) {
        p.writeByte(getType());
        p.writePos(getPosition());
        p.writeShort(fh);
        p.writeByte(getNewstate());
        p.writeShort(getDuration());
    }
}