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
 * 【类型】MiniDungeon，class，包 {@code org.gms.server.maps}。
 *
 * <p>迷你地下城实例管理类，负责创建、维护和销毁一个限时副本区域。支持玩家注册/注销、超时自动关闭和队长离开时全员传出。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理迷你地下城的生命周期</li>
 *   <li>处理玩家的注册和注销</li>
 *   <li>自动超时关闭地下城</li>
 *   <li>当队长离开时关闭地下城</li>
 *   <li>同步访问保护</li>
 * </ul>
 *
 * @author Ronan
 */
public class MiniDungeon {
    /** 参与地下城的玩家列表 */
    List<Character> players = new ArrayList<>();
    /** 超时任务 */
    ScheduledFuture<?> timeoutTask = null;
    /** 操作锁 */
    private final Lock lock = new ReentrantLock(true);

    /** 基础地图ID */
    int baseMap;
    /** 过期时间 */
    long expireTime;

    /**
     * 构造函数：创建迷你地下城实例
     * 
     * @param base 基础地图ID
     * @param timeLimit 时间限制（秒）
     */
    public MiniDungeon(int base, long timeLimit) {
        baseMap = base;
        expireTime = SECONDS.toMillis(timeLimit);

        timeoutTask = TimerManager.getInstance().schedule(() -> close(), expireTime);

        expireTime += System.currentTimeMillis();
    }

    /**
     * 注册玩家到地下城
     * 
     * @param chr 要注册的玩家
     * @return 如果注册成功则返回true，否则返回false
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
     * 从地下城注销玩家
     * 
     * @param chr 要注销的玩家
     * @return 如果注销成功则返回true，否则返回false
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

        if (chr.isPartyLeader()) {  // thanks Conrad for noticing party is not sent out of the MD as soon as leader leaves it
            close();
        }

        return true;
    }

    /**
     * 关闭地下城
     * 
     * <p>将所有玩家传送回基础地图并销毁地下城实例。</p>
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
     * 销毁地下城
     * 
     * <p>清理所有玩家列表和定时任务。</p>
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