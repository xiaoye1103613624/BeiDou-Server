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
package org.gms.client;

/**
 * 【类型】BuddylistEntry（class），包 `org.gms.client`。
 * <p>好友列表条目数据类，记录单个好友的详细信息</p>
 */
public class BuddylistEntry {
    /** 好友角色名称 */
    private final String name;
    /** 好友分组名称 */
    private String group;
    /** 好友角色ID */
    private final int cid;
    /** 所在频道（-1表示离线） */
    private int channel;
    /** 是否可见（对方是否屏蔽了自己） */
    private boolean visible;

    /**
     * 构造好友条目
     * @param name 好友角色名称
     * @param group 好友分组
     * @param characterId 好友角色ID
     * @param channel 所在频道（离线时为-1）
     * @param visible 是否可见
     */
    public BuddylistEntry(String name, String group, int characterId, int channel, boolean visible) {
        this.name = name;
        this.group = group;
        this.cid = characterId;
        this.channel = channel;
        this.visible = visible;
    }

    /**
     * 获取好友所在频道
     * @return 频道号，离线返回-1
     */
    public int getChannel() {
        return channel;
    }

    /**
     * 设置好友所在频道
     * @param channel 频道号
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * 判断好友是否在线
     * @return true=在线, false=离线
     */
    public boolean isOnline() {
        return channel >= 0;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public int getCharacterId() {
        return cid;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isVisible() {
        return visible;
    }

    /**
     * 修改好友分组
     * @param group 新分组名称
     */
    public void changeGroup(String group) {
        this.group = group;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cid;
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
        final BuddylistEntry other = (BuddylistEntry) obj;
        return cid == other.cid;
    }
}