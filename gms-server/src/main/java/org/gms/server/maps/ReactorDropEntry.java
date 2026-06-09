/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.server.maps;

/**
 * 反应器掉落条目
 * 记录反应器被破坏时的掉落物品、概率和任务需求
 */
public class ReactorDropEntry {

    /** 掉落物品ID */
    public int itemId;
    /** 掉落概率 */
    public int chance;
    /** 关联任务ID */
    public int questid;
    /** 分配的掉落范围起始值 */
    public int assignedRangeStart;
    /** 分配的掉落范围长度 */
    public int assignedRangeLength;

    /**
     * 构造反应器掉落条目
     *
     * @param itemId  物品ID
     * @param chance  掉落概率
     * @param questId 关联任务ID（0表示无任务需求）
     */
    public ReactorDropEntry(int itemId, int chance, int questId) {
        this.itemId = itemId;
        this.chance = chance;
        this.questid = questId;
    }
}