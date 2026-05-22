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
 * 【类型】MonsterGlobalDropEntry，class，包 {@code org.gms.server.life}。
 *
 * 怪物全局掉落条目数据类，描述所有怪物在指定大陆范围内可共享的掉落物品、概率、数量范围和关联的任务 ID。
 *
 * @author LightPepsi
 */
public class MonsterGlobalDropEntry {
    public MonsterGlobalDropEntry(int itemId, int chance, int continent, int Minimum, int Maximum, short questid) {
        this.itemId = itemId;
        this.chance = chance;
        this.questid = questid;
        this.continentid = continent;
        this.Minimum = Minimum;
        this.Maximum = Maximum;
    }

    public int itemId, chance, Minimum, Maximum, continentid;
    public short questid;
}
