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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 聊天信使
 * 管理信使成员及消息传递，最多支持3个位置
 */
public final class Messenger {

    /** 信使ID */
    private final int id;
    /** 信使成员列表 */
    private final List<MessengerCharacter> members = new ArrayList<>(3);
    /** 位置占用状态 */
    private final boolean[] pos = new boolean[3];

    /**
     * 构造信使
     *
     * @param id     信使ID
     * @param chrfor 创建者
     */
    public Messenger(int id, MessengerCharacter chrfor) {
        this.id = id;
        for (int i = 0; i < 3; i++) {
            pos[i] = false;
        }
        addMember(chrfor, chrfor.getPosition());
    }

    /**
     * 获取信使ID
     *
     * @return 信使ID
     */
    public int getId() {
        return id;
    }

    /**
     * 获取信使成员列表
     *
     * @return 不可修改的成员列表
     */
    public Collection<MessengerCharacter> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * 添加成员
     *
     * @param member   信使成员
     * @param position 位置
     */
    public void addMember(MessengerCharacter member, int position) {
        members.add(member);
        member.setPosition(position);
        pos[position] = true;
    }

    /**
     * 移除成员
     *
     * @param member 信使成员
     */
    public void removeMember(MessengerCharacter member) {
        int position = member.getPosition();
        pos[position] = false;
        members.remove(member);
    }

    /**
     * 获取最低可用位置
     *
     * @return 最低可用位置索引，-1表示已满
     */
    public int getLowestPosition() {
        for (byte i = 0; i < 3; i++) {
            if (!pos[i]) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据名称获取成员位置
     *
     * @param name 成员名称
     * @return 位置索引，-1表示未找到
     */
    public int getPositionByName(String name) {
        for (MessengerCharacter messengerchar : members) {
            if (messengerchar.getName().equals(name)) {
                return messengerchar.getPosition();
            }
        }
        return -1;
    }
}