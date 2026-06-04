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
package org.gms.server.maps;

import io.netty.buffer.Unpooled;
import org.gms.net.packet.ByteBufInPacket;
import org.gms.net.packet.ByteBufOutPacket;
import org.gms.net.packet.InPacket;
import org.gms.net.packet.OutPacket;
import org.gms.net.packet.Packet;

import java.util.Arrays;

/**
 * 【类型】AbstractAnimatedMapObject（class），包 `org.gms.server.maps`。
 * <p>可动画地图对象抽象基类，扩展 AbstractMapObject，增加姿态相关功能</p>
 */
public abstract class AbstractAnimatedMapObject extends AbstractMapObject implements AnimatedMapObject {
    /** 空闲移动包长度 */
    public static final int IDLE_MOVEMENT_PACKET_LENGTH = 15;
    /** 预创建的空闲移动包模板 */
    private static final Packet IDLE_MOVEMENT_PACKET = createIdleMovementPacket();

    /** 姿态值（朝向、动作等） */
    private int stance;

    @Override
    public int getStance() {
        return stance;
    }

    @Override
    public void setStance(int stance) {
        this.stance = stance;
    }

    /**
     * 判断是否朝左
     * <p>姿态值为奇数表示朝左，偶数表示朝右</p>
     * @return true=朝左, false=朝右
     */
    @Override
    public boolean isFacingLeft() {
        return Math.abs(stance) % 2 == 1;
    }

    /**
     * 获取空闲移动数据包
     * <p>基于模板包填充当前位置和姿态信息</p>
     * @return 空闲移动 InPacket
     */
    public InPacket getIdleMovement() {
        final byte[] idleMovementBytes = IDLE_MOVEMENT_PACKET.getBytes();
        byte[] movementData = Arrays.copyOf(idleMovementBytes, idleMovementBytes.length);
        // 直接操作字节数组比创建新的PacketWriter更高效
        int x = getPosition().x;
        int y = getPosition().y;
        movementData[2] = (byte) (x & 0xFF);     // x 低位
        movementData[3] = (byte) (x >> 8 & 0xFF); // x 高位
        movementData[4] = (byte) (y & 0xFF);     // y 低位
        movementData[5] = (byte) (y >> 8 & 0xFF); // y 高位
        movementData[12] = (byte) (getStance() & 0xFF); // 姿态
        return new ByteBufInPacket(Unpooled.wrappedBuffer(movementData));
    }

    /**
     * 创建空闲移动包模板
     * @return 预填充的空闲移动包
     */
    private static Packet createIdleMovementPacket() {
        OutPacket p = new ByteBufOutPacket();
        p.writeByte(1);      // 移动命令数量
        p.writeByte(0);
        p.writeShort(-1);    // x（占位）
        p.writeShort(-1);    // y（占位）
        p.writeShort(0);     // x摆动
        p.writeShort(0);     // y摆动
        p.writeShort(0);     // 立足点
        p.writeByte(-1);     // 姿态（占位）
        p.writeShort(0);     // 持续时间
        return p;
    }
}