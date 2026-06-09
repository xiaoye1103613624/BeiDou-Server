package org.gms.server.movement;/*
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

import java.awt.*;

/**
 * 跳下移动
 * 从高处跳下落到下方站脚点，包含下落速度和目标站脚点
 */
public class JumpDownMovement extends AbstractLifeMovement {
    /** 每秒像素速度 */
    private Point pixelsPerSecond;
    /** 目标站脚点ID */
    private int fh;
    /** 原始站脚点ID */
    private int originFh;

    /**
     * 构造跳下移动
     *
     * @param type     移动类型
     * @param position 起跳位置
     * @param duration 下落持续时间
     * @param newstate 落地后状态
     */
    public JumpDownMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
    }

    /**
     * 获取每秒像素下落速度
     *
     * @return 每秒像素速度
     */
    public Point getPixelsPerSecond() {
        return pixelsPerSecond;
    }

    /**
     * 设置每秒像素下落速度
     *
     * @param wobble 每秒像素速度
     */
    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    /**
     * 获取目标站脚点ID
     *
     * @return 站脚点ID
     */
    public int getFh() {
        return fh;
    }

    /**
     * 设置目标站脚点ID
     *
     * @param fh 站脚点ID
     */
    public void setFh(int fh) {
        this.fh = fh;
    }

    /**
     * 获取起跳原始站脚点ID
     *
     * @return 原始站脚点ID
     */
    public int getOriginFh() {
        return originFh;
    }

    /**
     * 设置起跳原始站脚点ID
     *
     * @param fh 原始站脚点ID
     */
    public void setOriginFh(int fh) {
        this.originFh = fh;
    }

    @Override
    public void serialize(OutPacket p) {
        p.writeByte(getType());
        p.writePos(getPosition());
        p.writePos(pixelsPerSecond);
        p.writeShort(fh);
        p.writeShort(originFh);
        p.writeByte(getNewstate());
        p.writeShort(getDuration());
    }
}