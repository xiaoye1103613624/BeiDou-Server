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

import java.awt.*;

/**
 * 【类型】LifeMovement（interface），包 `org.gms.server.movement`。
 *
 * 移动指令接口，扩展 LifeMovementFragment 并添加移动类型、新状态和持续时间的约定，用于所有角色/生物移动类型的序列化。
 *
 * @author 萧曵
 */
public interface LifeMovement extends LifeMovementFragment {
    Point getPosition();
    int getNewstate();
    int getDuration();
    int getType();
}
