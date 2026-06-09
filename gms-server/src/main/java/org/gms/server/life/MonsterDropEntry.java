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
package org.gms.server.life;

/**
 * 怪物掉落条目
 * 存储单个物品的掉落配置信息，包括物品ID、掉落概率、数量和任务关联
 *
 * @author LightPepsi
 */
public class MonsterDropEntry {
    /**
     * 构造掉落条目
     *
     * @param itemId 物品ID
     * @param chance 掉落概率
     * @param Minimum 最小掉落数量
     * @param Maximum 最大掉落数量
     * @param questid 关联任务ID（0表示无关联）
     */
    public MonsterDropEntry(int itemId, int chance, int Minimum, int Maximum, short questid) {
        this.itemId = itemId;
        this.chance = chance;
        this.questid = questid;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
    }

    /** 关联任务ID */
    public short questid;
    /** 物品ID */
    public int itemId;
    /** 掉落概率 */
    public int chance;
    /** 最小掉落数量 */
    public int Minimum;
    /** 最大掉落数量 */
    public int Maximum;
}