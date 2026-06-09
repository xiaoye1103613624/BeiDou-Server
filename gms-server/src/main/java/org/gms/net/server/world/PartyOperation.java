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
package org.gms.net.server.world;

/**
 * 组队操作枚举
 * 定义组队的各种操作类型
 * <ul>
 *   <li>JOIN - 加入队伍</li>
 *   <li>LEAVE - 离开队伍</li>
 *   <li>EXPEL - 踢出队伍</li>
 *   <li>DISBAND - 解散队伍</li>
 *   <li>SILENT_UPDATE - 静默更新</li>
 *   <li>LOG_ONOFF - 上线/下线通知</li>
 *   <li>CHANGE_LEADER - 更换队长</li>
 * </ul>
 */
public enum PartyOperation {
    /** 加入队伍 */
    JOIN,
    /** 离开队伍 */
    LEAVE,
    /** 踢出队伍 */
    EXPEL,
    /** 解散队伍 */
    DISBAND,
    /** 静默更新 */
    SILENT_UPDATE,
    /** 上线/下线通知 */
    LOG_ONOFF,
    /** 更换队长 */
    CHANGE_LEADER
}