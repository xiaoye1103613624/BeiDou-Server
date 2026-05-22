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
package org.gms.client;

/**
 * 【类型】DiseaseValueHolder（class），包 {@code org.gms.client}。
 *
 * 异常状态（Disease）数值容器，记录状态效果的开始时间与持续时长，
 * 用于 Buff/Debuff 的计时与过期判断。
 *
 * @author anybody can do this
 */
public class DiseaseValueHolder {
    public long startTime, length;

    public DiseaseValueHolder(long start, long length) {
        this.startTime = start;
        this.length = length;
    }
}
