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
package org.gms.net.server.guild;

import org.gms.net.packet.Packet;

/**
 * 公会操作响应码枚举
 * 定义公会操作的各种响应类型及对应的数据包
 */
public enum GuildResponse {
    /** 目标不在频道中 */
    NOT_IN_CHANNEL(0x2a),
    /** 已在公会中 */
    ALREADY_IN_GUILD(0x28),
    /** 不在公会中 */
    NOT_IN_GUILD(0x2d),
    /** 未找到邀请 */
    NOT_FOUND_INVITE(0x2e),
    /** 正在管理邀请 */
    MANAGING_INVITE(0x36),
    /** 拒绝邀请 */
    DENIED_INVITE(0x37);

    /** 响应码值 */
    private final int value;

    /**
     * 构造响应码
     *
     * @param val 响应码值
     */
    GuildResponse(int val) {
        value = val;
    }

    /**
     * 获取对应的数据包
     *
     * @param targetName 目标角色名称
     * @return 响应数据包
     */
    public final Packet getPacket(String targetName) {
        if (value >= MANAGING_INVITE.value) {
            return GuildPackets.responseGuildMessage((byte) value, targetName);
        } else {
            return GuildPackets.genericGuildMessage((byte) value);
        }
    }
}