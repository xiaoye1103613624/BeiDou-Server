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
package org.gms.server.events.gm;

import org.gms.client.Character;
import org.gms.constants.id.MapId;
import org.gms.server.TimerManager;
import org.gms.util.PacketCreator;

import java.util.concurrent.ScheduledFuture;

/**
 * 奥拉活动（Ola Ola）
 * 玩家限时通过迷宫关卡，超时自动传出地图
 * 打开起点传送门后玩家开始计时挑战
 *
 * @author kevintjuh93
 */
public class Ola {
    /** 参与玩家 */
    private final Character chr;
    /** 剩余时间 */
    private long time = 0;
    /** 开始时间戳 */
    private long timeStarted = 0;
    /** 结束定时器 */
    private ScheduledFuture<?> schedule = null;

    /**
     * 构造奥拉活动
     *
     * @param chr 参与玩家
     */
    public Ola(final Character chr) {
        this.chr = chr;
        this.schedule = TimerManager.getInstance().schedule(() -> {
            if (MapId.isOlaOla(chr.getMapId())) {
                chr.changeMap(chr.getMap().getReturnMap());
            }
            resetTimes();
        }, 360000);
    }

    public void startOla() { // TODO: Messages
        chr.getMap().startEvent();
        chr.sendPacket(PacketCreator.getClock(360));
        this.timeStarted = System.currentTimeMillis();
        this.time = 360000;

        chr.getMap().getPortal("join00").setPortalStatus(true);
        chr.sendPacket(PacketCreator.serverNotice(0, "The portal has now opened. Press the up arrow key at the portal to enter."));
    }

    /**
     * 判断计时是否已开始
     *
     * @return true已开始，false未开始
     */
    public boolean isTimerStarted() {
        return time > 0 && timeStarted > 0;
    }

    /**
     * 获取总时间
     *
     * @return 总时间毫秒数
     */
    public long getTime() {
        return time;
    }

    /**
     * 重置计时器
     * 将时间和开始时间置零，取消定时任务
     */
    public void resetTimes() {
        this.time = 0;
        this.timeStarted = 0;
        schedule.cancel(false);
    }

    /**
     * 获取剩余时间
     *
     * @return 剩余毫秒数
     */
    public long getTimeLeft() {
        return time - (System.currentTimeMillis() - timeStarted);
    }
}