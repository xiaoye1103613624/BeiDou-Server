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
 * 
 * <p>好友列表条目数据类，记录单个好友的详细信息，包括角色名称、分组、ID、
 * 在线状态和可见性等属性。用于管理玩家好友列表中的单个条目。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>存储好友基本信息（名称、ID、分组）</li>
 *   <li>跟踪好友在线状态（频道信息）</li>
 *   <li>管理好友可见性设置</li>
 * </ul>
 * 
 * <p>设计特点：</p>
 * <ul>
 *   <li>不可变字段：角色名称和ID一旦设定不可更改</li>
 *   <li>状态跟踪：实时跟踪好友的频道和在线状态</li>
 *   <li>可见性管理：支持好友间的可见性设置</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public class BuddylistEntry {
    /** 好友角色名称，一旦设定不可更改 */
    private final String name;
    /** 好友分组名称，可用于组织好友列表 */
    private String group;
    /** 好友角色ID，一旦设定不可更改 */
    private final int cid;
    /** 所在频道（-1表示离线），用于跟踪好友在线状态 */
    private int channel;
    /** 是否可见（对方是否屏蔽了自己），用于好友隐私控制 */
    private boolean visible;

    /**
     * 构造函数：创建好友条目实例
     * 
     * <p>初始化好友条目的各项属性，包括名称、分组、ID、频道和可见性。</p>
     * 
     * @param name 好友角色名称
     * @param group 好友分组名称
     * @param characterId 好友角色ID
     * @param channel 好友所在频道（离线时为-1）
     * @param visible 好友是否可见（对方是否屏蔽了自己）
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
     * 
     * <p>返回好友当前所在的频道号，如果好友离线则返回-1。</p>
     * 
     * @return 频道号，离线返回-1
     */
    public int getChannel() {
        return channel;
    }

    /**
     * 设置好友所在频道
     * 
     * <p>更新好友当前所在的频道号，用于跟踪好友的在线状态变化。</p>
     * 
     * @param channel 新的频道号
     */
    public void setChannel(int channel) {
        this.channel = channel;
    }

    /**
     * 判断好友是否在线
     * 
     * <p>检查好友当前是否在线，通过判断频道号是否大于等于0来确定。</p>
     * 
     * @return 如果好友在线则返回true，否则返回false
     */
    public boolean isOnline() {
        return channel >= 0;
    }

    /**
     * 获取好友角色名称
     * 
     * @return 好友角色名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取好友分组名称
     * 
     * @return 好友分组名称
     */
    public String getGroup() {
        return group;
    }

    /**
     * 获取好友角色ID
     * 
     * @return 好友角色ID
     */
    public int getCharacterId() {
        return cid;
    }

    /**
     * 设置好友可见性状态
     * 
     * <p>更新好友的可见性状态，用于控制好友间的隐私设置。</p>
     * 
     * @param visible 新的可见性状态
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * 检查好友是否可见
     * 
     * <p>返回好友当前的可见性状态，用于判断是否能够看到该好友的信息。</p>
     * 
     * @return 如果好友可见则返回true，否则返回false
     */
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