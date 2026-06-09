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
 * 生命体移动片段接口
 * 定义移动片段的基础行为：序列化到数据包和获取位置
 *
 * @author Patrick Huy
 */
public interface LifeMovementFragment {
    /**
     * 序列化移动片段到输出包
     *
     * @param outPacket 输出包
     */
    void serialize(OutPacket outPacket);

    /**
     * 获取移动终点位置
     *
     * @return 终点位置
     */
    Point getPosition();
}