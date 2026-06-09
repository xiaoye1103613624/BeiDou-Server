/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

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
 * 传送移动
 * 瞬间传送到目标位置，无持续时间
 * 继承AbsoluteLifeMovement使用绝对坐标
 */
public class TeleportMovement extends AbsoluteLifeMovement {

    /**
     * 构造传送移动
     *
     * @param type      移动类型
     * @param position  目标位置
     * @param newstate  传送后状态
     */
    public TeleportMovement(int type, Point position, int newstate) {
        super(type, position, 0, newstate);
    }

    /**
     * 序列化传送移动到数据包
     * <p>
     * 与父类相比省略了 fh 和 duration 字段，因为传送是瞬时的
     *
     * @param p 输出包
     */
    @Override
    public void serialize(OutPacket p) {
        p.writeByte(getType());
        p.writePos(getPosition());
        p.writePos(getPixelsPerSecond());
        p.writeByte(getNewstate());
    }
}