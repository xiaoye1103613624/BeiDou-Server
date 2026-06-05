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
 * 【类型】ReactorDropEntry（class），包 {@code org.gms.server.maps}。
 * 
 * <p>反应器掉落条目类，定义反应器被破坏后可能掉落的物品及其概率和相关任务条件。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>存储反应器掉落物品的信息</li>
 *   <li>管理掉落概率和任务条件</li>
 *   <li>支持随机掉落机制</li>
 * </ul>
 * 
 * <p>设计说明：</p>
 * <ul>
 *   <li>此类主要用于反应器系统</li>
 *   <li>通过chance字段控制掉落概率</li>
 *   <li>questid用于关联特定任务</li>
 * </ul>
 * 
 * @author OdinMS (original)
 * @author Xergon (adaptation)
 * @since 2024-07-18
 */
public class ReactorDropEntry {

    /**
     * 构造函数：创建反应器掉落条目实例
     * 
     * @param itemId 物品ID
     * @param chance 掉落概率（数值越小概率越高）
     * @param questId 关联任务ID
     */
    public ReactorDropEntry(int itemId, int chance, int questId) {
        this.itemId = itemId;
        this.chance = chance;
        this.questid = questId;
    }

    /** 掉落物品ID */
    public int itemId;
    /** 掉落概率（数值越小概率越高） */
    public int chance;
    /** 关联任务ID */
    public int questid;
    /** 分配范围起始值（用于随机算法） */
    public int assignedRangeStart;
    /** 分配范围长度（用于随机算法） */
    public int assignedRangeLength;
}