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
 * 
 * <p>可动画地图对象抽象基类，扩展 AbstractMapObject，增加姿态相关功能。
 * 此类为具有动画效果的地图对象提供基础实现，如玩家角色、怪物、NPC等，
 * 包括姿态管理、朝向判断和移动数据包生成等功能。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理对象的姿态值（朝向、动作等）</li>
 *   <li>提供朝向判断功能</li>
 *   <li>生成空闲移动数据包</li>
 *   <li>预创建移动包模板以提高性能</li>
 * </ul>
 */
public abstract class AbstractAnimatedMapObject extends AbstractMapObject implements AnimatedMapObject {
    /** 空闲移动包长度 */
    public static final int IDLE_MOVEMENT_PACKET_LENGTH = 15;
    /** 预创建的空闲移动包模板 */
    private static final Packet IDLE_MOVEMENT_PACKET = createIdleMovementPacket();

    /** 姿态值（朝向、动作等），用于控制对象的外观和行为 */
    private int stance;

    /**
     * 获取姿态值
     * 
     * @return 当前的姿态值
     */
    @Override
    public int getStance() {
        return stance;
    }

    /**
     * 设置姿态值
     * 
     * @param stance 要设置的姿态值
     */
    @Override
    public void setStance(int stance) {
        this.stance = stance;
    }

    /**
     * 判断是否朝左
     * 
     * <p>姿态值为奇数表示朝左，偶数表示朝右。</p>
     * 
     * @return true=朝左, false=朝右
     */
    @Override
    public boolean isFacingLeft() {
        return Math.abs(stance) % 2 == 1;
    }

    /**
     * 获取空闲移动数据包
     * 
     * <p>基于模板包填充当前位置和姿态信息。此方法用于网络通信，
     * 将对象的当前位置和姿态信息打包成可用于传输的数据包。</p>
     * 
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
     * 
     * <p>创建一个预填充的空闲移动数据包作为模板，用于后续生成实际的移动数据包。
     * 使用模板可以避免重复创建相同的包结构，提高性能。</p>
     * 
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
        p.writeShort(0);     // 持续时间
        return p;
    }
}