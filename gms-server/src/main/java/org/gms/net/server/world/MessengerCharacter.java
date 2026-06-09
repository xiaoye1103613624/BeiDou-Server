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
 * 聊天信使角色
 * 封装聊天信使（messenger）中角色的基本信息
 *
 * @author kevintjuh93
 */
public class MessengerCharacter {
    /** 角色名称 */
    private final String name;
    /** 角色ID */
    private final int id;
    /** 在信使中的位置 */
    private int position;
    /** 所在频道 */
    private final int channel;
    /** 是否在线 */
    private final boolean online;

    /**
     * 构造信使角色
     *
     * @param maplechar 角色对象
     * @param position  在信使中的位置
     */
    public MessengerCharacter(Character maplechar, int position) {
        this.name = maplechar.getName();
        this.channel = maplechar.getClient().getChannel();
        this.id = maplechar.getId();
        this.online = true;
        this.position = position;
    }

    /**
     * 获取角色ID
     *
     * @return 角色ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取所在频道
     *
     * @return 频道号
     */
    public int getChannel() {
        return channel;
    }

    /**
     * 获取角色名称
     *
     * @return 角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 是否在线
     *
     * @return 在线状态
     */
    public boolean isOnline() {
        return online;
    }

    /**
     * 获取在信使中的位置
     *
     * @return 位置索引
     */
    public int getPosition() {
        return position;
    }

    /**
     * 设置在信使中的位置
     *
     * @param position 位置索引
     */
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