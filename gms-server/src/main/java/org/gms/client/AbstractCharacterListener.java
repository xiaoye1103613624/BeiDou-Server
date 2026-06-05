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
    /**
     * 当角色生命值发生变化时调用
     * <p>
     * 此方法在角色的HP（生命值）发生改变时被触发，用于处理与生命值变化相关的逻辑，
     * 如血量变化动作、状态更新等。
     * </p>
     *
     * @param oldHp 变化前的生命值
     */
    void onHpChanged(int oldHp);
    
    /**
     * 当角色HP/MP池需要更新时调用
     * <p>
     * 此方法在角色的HP/MP（生命值/魔法值）池需要重新计算时被触发，
     * 用于重新计算本地角色的统计数据，并确保HP/MP值不超过最大限制。
     * </p>
     */
    void onHpMpPoolUpdate();
    
    /**
     * 当角色属性需要更新时调用
     * <p>
     * 此方法在角色的统计数据需要更新时被触发，通常会重新计算本地角色的属性统计信息。
     * </p>
     */
    void onStatUpdate();
    
    /**
     * 当需要广播属性池更新时调用
     * <p>
     * 此方法用于向客户端发送玩家状态更新数据包，将角色的属性变更通知给客户端，
     * 使客户端能够显示最新的角色状态。
     * </p>
     */
    void onAnnounceStatPoolUpdate();
}