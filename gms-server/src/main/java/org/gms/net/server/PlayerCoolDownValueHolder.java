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
package org.gms.net.server;

/**
 * 玩家技能冷却值持有者
 * 记录技能冷却的开始时间和持续时长
 */
public class PlayerCoolDownValueHolder {
    /** 技能ID */
    public int skillId;
    /** 冷却开始时间 */
    public long startTime;
    /** 冷却持续时长 */
    public long length;

    /**
     * 构造冷却值持有者
     *
     * @param skillId   技能ID
     * @param startTime 冷却开始时间
     * @param length    冷却持续时长
     */
    public PlayerCoolDownValueHolder(int skillId, long startTime, long length) {
        this.skillId = skillId;
        this.startTime = startTime;
        this.length = length;
    }
}