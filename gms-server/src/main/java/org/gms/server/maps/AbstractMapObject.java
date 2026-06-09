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

import java.awt.*;

/**
 * 抽象地图对象
 * 实现{@link MapObject}接口，提供地图对象的基础属性管理
 * 包括位置、对象ID的管理，是所有地图对象的基类
 */
public abstract class AbstractMapObject implements MapObject {
    /** 地图对象位置 */
    private Point position = new Point();
    /** 地图对象唯一ID */
    private int objectId;

    @Override
    public abstract MapObjectType getType();

    @Override
    public Point getPosition() {
        return new Point(position);
    }

    @Override
    public void setPosition(Point position) {
        this.position.move(position.x, position.y);
    }

    @Override
    public int getObjectId() {
        return objectId;
    }

    @Override
    public void setObjectId(int id) {
        this.objectId = id;
    }

    @Override
    public void nullifyPosition() {
        this.position = null;
    }
}