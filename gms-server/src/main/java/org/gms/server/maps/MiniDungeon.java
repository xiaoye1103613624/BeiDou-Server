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
package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.server.TimerManager;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 迷你副本
 * 管理玩家专属副本实例的创建、进入和退出
 *
 * @author Ronan
 */
public class MiniDungeon {
    List<Character> players = new ArrayList<>();
    ScheduledFuture<?> timeoutTask = null;
    private final Lock lock = new ReentrantLock(true);

    /** 副本入口地图ID */
    int baseMap;
    /** 副本过期时间（毫秒） */
    long expireTime;

    /**
     * 构造迷你副本，设置超时自动关闭
     *
     * @param base      入口地图ID
     * @param timeLimit 时间限制（秒）
     */
    public MiniDungeon(int base, long timeLimit) {
        baseMap = base;
        expireTime = SECONDS.toMillis(timeLimit);

        timeoutTask = TimerManager.getInstance().schedule(() -> close(), expireTime);

        expireTime += System.currentTimeMillis();
    }

    /**
     * 注册玩家进入副本，显示剩余时间倒计时
     *
     * @param chr 玩家
     * @return true表示注册成功
     */
    public boolean registerPlayer(Character chr) {
        int time = (int) ((expireTime - System.currentTimeMillis()) / 1000);
        if (time > 0) {
            chr.sendPacket(PacketCreator.getClock(time));
        }

        lock.lock();
        try {
            if (timeoutTask == null) {
                return false;
            }

            players.add(chr);
        } finally {
            lock.unlock();
        }

        return true;
    }

    /**
     * 注销玩家，若副本无玩家则销毁，若注销的是队长则关闭副本
     *
     * @param chr 玩家
     * @return true表示注销成功
     */
    public boolean unregisterPlayer(Character chr) {
        chr.sendPacket(PacketCreator.removeClock());

        lock.lock();
        try {
            players.remove(chr);

            if (players.isEmpty()) {
                dispose();
                return false;
            }
        } finally {
            lock.unlock();
        }

        if (chr.isPartyLeader()) {
            close();
        }

        return true;
    }

    /**
     * 关闭副本，将所有玩家传送回入口地图
     */
    public void close() {
        lock.lock();
        try {
            List<Character> lchr = new ArrayList<>(players);

            for (Character chr : lchr) {
                chr.changeMap(baseMap);
            }

            dispose();
            timeoutTask = null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 销毁副本，清除玩家列表并取消超时任务
     */
    public void dispose() {
        lock.lock();
        try {
            players.clear();

            if (timeoutTask != null) {
                timeoutTask.cancel(false);
                timeoutTask = null;
            }
        } finally {
            lock.unlock();
        }
    }
}