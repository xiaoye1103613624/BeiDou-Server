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
import org.gms.server.movement.AbstractLifeMovement;

import java.awt.*;

/**
 * 绝对位置移动
 * 移动到地图绝对坐标，用于怪物AI路径点移动
 * 包含每秒像素速度和站脚点ID
 */
public class AbsoluteLifeMovement extends AbstractLifeMovement {
    /** 每秒像素速度 */
    private Point pixelsPerSecond;
    /** 目标站脚点ID */
    private int fh;

    /**
     * 构造绝对位置移动对象
     *
     * @param type     移动类型
     * @param position 目标绝对坐标
     * @param duration 移动持续时间
     * @param newstate 移动后的新状态
     */
    public AbsoluteLifeMovement(int type, Point position, int duration, int newstate) {
        super(type, position, duration, newstate);
    }

    /**
     * 获取每秒像素移动速度
     *
     * @return 每秒像素速度
     */
    public Point getPixelsPerSecond() {
        return pixelsPerSecond;
    }

    /**
     * 设置每秒像素移动速度
     *
     * @param wobble 每秒像素速度
     */
    public void setPixelsPerSecond(Point wobble) {
        this.pixelsPerSecond = wobble;
    }

    /**
     * 获取目标站脚点ID（fh 为 foothold 缩写）
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

    @Override
    public void serialize(OutPacket p) {
        p.writeByte(getType());
        p.writePos(getPosition());
        p.writePos(pixelsPerSecond);
        p.writeShort(fh);
        p.writeByte(getNewstate());
        p.writeShort(getDuration());
    }
}