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

import org.gms.server.movement.LifeMovement;

import java.awt.*;

/**
 * 抽象生命体移动
 * 实现LifeMovement接口，提供公共属性存储：位置、持续时间、新状态、类型
 * 所有具体移动类型继承此类
 */
public abstract class AbstractLifeMovement implements LifeMovement {
    /** 目标位置 */
    private final Point position;
    /** 持续时间（tick） */
    private final int duration;
    /** 移动后新状态 */
    private final int newstate;
    /** 移动类型 */
    private final int type;

    /**
     * 构造抽象生命体移动
     *
     * @param type     移动类型
     * @param position 目标位置
     * @param duration 持续时间（tick）
     * @param newstate 移动后新状态
     */
    public AbstractLifeMovement(int type, Point position, int duration, int newstate) {
        super();
        this.type = type;
        this.position = position;
        this.duration = duration;
        this.newstate = newstate;
    }

    @Override
    public int getType() {
        return this.type;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public int getNewstate() {
        return newstate;
    }

    @Override
    public Point getPosition() {
        return position;
    }
}