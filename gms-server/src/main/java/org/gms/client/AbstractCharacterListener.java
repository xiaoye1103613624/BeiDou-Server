/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

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
 * 【类型】AbstractCharacterListener（interface），包 {@code org.gms.client}。
 *
 * 角色/怪物属性变化监听器接口，定义 HP 变化、HP/MP 池更新、属性更新及属性池广播等回调方法，
 * 供 {@link AbstractCharacterObject} 在状态变更时通知上层（如 {@link Character}）。
 *
 * @author Ronan
 */
public interface AbstractCharacterListener {
    void onHpChanged(int oldHp);
    void onHpMpPoolUpdate();
    void onStatUpdate();
    void onAnnounceStatPoolUpdate();
}
