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
package org.gms.scripting.event.scheduler;

import org.gms.config.GameConfig;
import org.gms.net.server.Server;
import org.gms.server.ThreadManager;
import org.gms.server.TimerManager;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 事件脚本调度器
 * 管理事件脚本的定时任务调度，支持任务的注册、延迟执行和取消
 */
public class EventScriptScheduler {

    /** 调度器是否已释放 */
    private boolean disposed = false;
    /** 空闲计数器，达到阈值后取消调度任务 */
    private int idleProcs = 0;
    /** 注册的延时任务映射（任务 -> 延迟毫秒数） */
    private final Map<Runnable, Long> registeredEntries = new HashMap<>();

    /** 底层定时任务句柄 */
    private ScheduledFuture<?> schedulerTask = null;
    /** 调度器锁，保证线程安全 */
    private final Lock schedulerLock = new ReentrantLock(true);

    /**
     * 基础调度循环
     * 遍历已注册的延时任务，到期后执行并从注册表中移除
     * 空闲计数达到阈值时自动取消调度任务以节省资源
     */
    private void runBaseSchedule() {
        List<Runnable> toRemove;
        Map<Runnable, Long> registeredEntriesCopy;

        schedulerLock.lock();
        try {
            if (registeredEntries.isEmpty()) {
                idleProcs++;

                if (idleProcs >= GameConfig.getServerInt("mob_status_monitor_idle")) {
                    if (schedulerTask != null) {
                        schedulerTask.cancel(false);
                        schedulerTask = null;
                    }
                }

                return;
            }

            idleProcs = 0;
            registeredEntriesCopy = new HashMap<>(registeredEntries);
        } finally {
            schedulerLock.unlock();
        }

        long timeNow = Server.getInstance().getCurrentTime();
        toRemove = new LinkedList<>();
        for (Entry<Runnable, Long> rmd : registeredEntriesCopy.entrySet()) {
            if (rmd.getValue() < timeNow) {
                Runnable r = rmd.getKey();

                // runs the scheduled action
                r.run();
                toRemove.add(r);
            }
        }

        if (!toRemove.isEmpty()) {
            schedulerLock.lock();
            try {
                for (Runnable r : toRemove) {
                    registeredEntries.remove(r);
                }
            } finally {
                schedulerLock.unlock();
            }
        }
    }

    /**
     * 注册延时任务
     *
     * @param scheduledAction 延时执行的任务
     * @param duration        延迟时间（毫秒）
     */
    public void registerEntry(final Runnable scheduledAction, final long duration) {

        ThreadManager.getInstance().newTask(() -> {
            schedulerLock.lock();
            try {
                idleProcs = 0;
                if (schedulerTask == null) {
                    if (disposed) {
                        return;
                    }

                    schedulerTask = TimerManager.getInstance().register(this::runBaseSchedule, GameConfig.getServerLong("mob_status_monitor_proc"), GameConfig.getServerLong("mob_status_monitor_proc"));
                }

                registeredEntries.put(scheduledAction, Server.getInstance().getCurrentTime() + duration);
            } finally {
                schedulerLock.unlock();
            }
        });
    }

    /**
     * 取消已注册的延时任务
     *
     * @param scheduledAction 要取消的任务
     */
    public void cancelEntry(final Runnable scheduledAction) {

        ThreadManager.getInstance().newTask(() -> {
            schedulerLock.lock();
            try {
                registeredEntries.remove(scheduledAction);
            } finally {
                schedulerLock.unlock();
            }
        });
    }

    /**
     * 释放调度器
     * 取消所有调度任务，清空注册表，标记为已释放
     */
    public void dispose() {

        ThreadManager.getInstance().newTask(() -> {
            schedulerLock.lock();
            try {
                if (schedulerTask != null) {
                    schedulerTask.cancel(false);
                    schedulerTask = null;
                }

                registeredEntries.clear();
                disposed = true;
            } finally {
                schedulerLock.unlock();
            }
        });
    }
}