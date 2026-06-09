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
 * 抽象动画地图对象
 * 扩展{@link AbstractMapObject}，实现{@link AnimatedMapObject}接口
 * 提供姿态、朝向管理和空闲移动包生成功能
 * 所有有动画表现的地图对象（如怪物、NPC、召唤兽）的基类
 */
public abstract class AbstractAnimatedMapObject extends AbstractMapObject implements AnimatedMapObject {
    /** 空闲移动包长度常量 */
    public static final int IDLE_MOVEMENT_PACKET_LENGTH = 15;
    /** 预生成的空闲移动包模板（用于性能优化） */
    private static final Packet IDLE_MOVEMENT_PACKET = createIdleMovementPacket();

    /** 当前姿态 */
    private int stance;

    @Override
    public int getStance() {
        return stance;
    }

    @Override
    public void setStance(int stance) {
        this.stance = stance;
    }

    @Override
    public boolean isFacingLeft() {
        return Math.abs(stance) % 2 == 1;
    }

    /**
     * 生成当前对象位置和姿态的空闲移动包
     * <p>
     * 拷贝预生成的模板包，替换位置X/Y、姿态等动态字段，避免每次新建包对象
     *
     * @return 空闲移动输入包
     */
    public InPacket getIdleMovement() {
        final byte[] idleMovementBytes = IDLE_MOVEMENT_PACKET.getBytes();
        byte[] movementData = Arrays.copyOf(idleMovementBytes, idleMovementBytes.length);
        int x = getPosition().x;
        int y = getPosition().y;
        movementData[2] = (byte) (x & 0xFF);
        movementData[3] = (byte) (x >> 8 & 0xFF);
        movementData[4] = (byte) (y & 0xFF);
        movementData[5] = (byte) (y >> 8 & 0xFF);
        movementData[12] = (byte) (getStance() & 0xFF);
        return new ByteBufInPacket(Unpooled.wrappedBuffer(movementData));
    }

    /**
     * 创建空闲移动包模板
     * <p>
     * 预生成一个标准格式的空闲移动包，包含1个移动指令，位置/速度/站脚点/姿态均为默认值
     *
     * @return 空闲移动包模板
     */
    private static Packet createIdleMovementPacket() {
        OutPacket p = new ByteBufOutPacket();
        // movement command count
        p.writeByte(1);
        p.writeByte(0);
        // x
        p.writeShort(-1);
        // y
        p.writeShort(-1);
        // xwobble
        p.writeShort(0);
        // ywobble
        p.writeShort(0);
        // fh
        p.writeShort(0);
        // stance
        p.writeByte(-1);
        // duration
        p.writeShort(0);
        return p;
    }
}