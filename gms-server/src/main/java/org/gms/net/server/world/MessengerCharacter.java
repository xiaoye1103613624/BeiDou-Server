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

import org.gms.client.Character;

/**
 * Messenger（信使）系统中的角色信息封装类。
 * 用于跨频道传递信使成员的状态信息，支持最多3人同时在线聊天。
 */
public class MessengerCharacter {
    /** 角色名称 */
    private final String name;
    /** 角色ID */
    private final int id;
    /** 在信使中的位置（0-2，最多3个位置） */
    private int position;
    /** 所在频道 */
    private final int channel;
    /** 在线状态 */
    private final boolean online;

    public MessengerCharacter(Character maplechar, int position) {
        this.name = maplechar.getName();
        this.channel = maplechar.getClient().getChannel();
        this.id = maplechar.getId();
        this.online = true;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public int getChannel() {
        return channel;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return online;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MessengerCharacter other = (MessengerCharacter) obj;
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }
}