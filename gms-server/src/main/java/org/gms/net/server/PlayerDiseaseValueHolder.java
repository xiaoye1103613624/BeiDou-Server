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
package org.gms.net.server;

import org.gms.client.Disease;

/**
 * 玩家异常状态值持有者
 * 记录异常状态的类型、开始时间和持续时长
 */
public class PlayerDiseaseValueHolder {

    /** 异常状态开始时间 */
    public long startTime;
    /** 异常状态持续时长 */
    public long length;
    /** 异常状态类型 */
    public Disease disease;

    /**
     * 构造异常状态值持有者
     *
     * @param disease   异常状态类型
     * @param startTime 异常状态开始时间
     * @param length    异常状态持续时长
     */
    public PlayerDiseaseValueHolder(final Disease disease, final long startTime, final long length) {
        this.disease = disease;
        this.startTime = startTime;
        this.length = length;
    }
}