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
package org.gms.server;

import lombok.Getter;
import org.gms.net.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;

/**
 * 定时器管理器
 * 实现{@link TimerManagerMBean}接口，提供统一的定时任务调度功能
 * 使用ScheduledThreadPoolExecutor作为底层调度器，注册JMX MBean用于监控
 * 单例模式，支持任务注册、调度和自动清除
 *
 * @author Ali
 */
public class TimerManager implements TimerManagerMBean {
    private static final Logger log = LoggerFactory.getLogger(TimerManager.class);
    /** 单例实例 */
    @Getter
    private static final TimerManager instance = new TimerManager();

    /** 定时任务调度器 */
    private ScheduledThreadPoolExecutor ses;

    private TimerManager() {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            mBeanServer.registerMBean(this, new ObjectName("server:type=TimerManger"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 启动定时器线程池（4个线程，空闲5分钟后回收）
     */
    public void start() {
        if (ses != null && !ses.isShutdown() && !ses.isTerminated()) {
            return;
        }
        ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(4, new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("TimerManager-Worker-" + threadNumber.getAndIncrement());
                return t;
            }
        });
        // this is a no-no, it actually does nothing..then why the fuck are you doing it?
        stpe.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
        stpe.setRemoveOnCancelPolicy(true);

        stpe.setKeepAliveTime(5, MINUTES);
        stpe.allowCoreThreadTimeOut(true);

        ses = stpe;
    }

    /**
     * 停止定时器线程池
     */
    public void stop() {
        ses.shutdownNow();
    }

    /**
     * 返回清理任务：更新服务器时间并清理已取消任务
     * Yay?
     *
     * @return 可运行的清理任务
     */
    public Runnable purge() {
        return () -> {
            Server.getInstance().forceUpdateCurrentTime();
            ses.purge();
        };
    }

    /**
     * 注册定时任务（带延迟）
     *
     * @param r          任务
     * @param repeatTime 重复间隔（毫秒）
     * @param delay      初始延迟（毫秒）
     * @return ScheduledFuture句柄
     */
    public ScheduledFuture<?> register(Runnable r, long repeatTime, long delay) {
        return ses.scheduleAtFixedRate(new TimerRunner(r), delay, repeatTime, MILLISECONDS);
    }

    /**
     * 注册定时任务（立即开始）
     *
     * @param r          任务
     * @param repeatTime 重复间隔（毫秒）
     * @return ScheduledFuture句柄
     */
    public ScheduledFuture<?> register(Runnable r, long repeatTime) {
        return ses.scheduleAtFixedRate(new TimerRunner(r), 0, repeatTime, MILLISECONDS);
    }

    /**
     * 更新定时任务的执行间隔
     *
     * @param sf         原ScheduledFuture
     * @param r          任务
     * @param repeatTime 新重复间隔（毫秒）
     * @return 新ScheduledFuture句柄
     */
    public ScheduledFuture<?> update(ScheduledFuture<?> sf, Runnable r, long repeatTime) {
       stop(sf);
        return ses.scheduleAtFixedRate(new TimerRunner(r), 0, repeatTime, MILLISECONDS);
    }

    /**
     * 取消定时任务
     *
     * @param sf ScheduledFuture句柄
     */
    public void stop(ScheduledFuture<?> sf) {
        if (sf != null && !sf.isCancelled()) {
            sf.cancel(false);
        }
    }

    /**
     * 安排一次性延迟任务
     *
     * @param r     任务
     * @param delay 延迟（毫秒）
     * @return ScheduledFuture句柄
     */
    public ScheduledFuture<?> schedule(Runnable r, long delay) {
        return ses.schedule(new TimerRunner(r), delay, MILLISECONDS);
    }

    /**
     * 安排一次性定时任务（指定时间戳）
     *
     * @param r         任务
     * @param timestamp 目标时间戳（毫秒）
     * @return ScheduledFuture句柄
     */
    public ScheduledFuture<?> scheduleAtTimestamp(Runnable r, long timestamp) {
        return schedule(r, timestamp - System.currentTimeMillis());
    }

    @Override
    public long getActiveCount() {
        return ses.getActiveCount();
    }

    @Override
    public long getCompletedTaskCount() {
        return ses.getCompletedTaskCount();
    }

    @Override
    public int getQueuedTasks() {
        return ses.getQueue().toArray().length;
    }

    @Override
    public long getTaskCount() {
        return ses.getTaskCount();
    }

    @Override
    public boolean isShutdown() {
        return ses.isShutdown();
    }

    /**
     * 检查是否已终止
     *
     * @return 已终止返回true
     */
    public boolean isTerminated() {
        return ses.isTerminated();
    }

    /**
     * 定时任务执行器包装
     * 捕获所有异常，防止未处理异常导致线程终止
     */
    private static class TimerRunner implements Runnable {
        /** 委托任务 */
        Runnable r;

        /**
         * 构造函数
         *
         * @param r 委托任务
         */
        public TimerRunner(Runnable r) {
            this.r = r;
        }

        @Override
        public void run() {
            try {
                r.run();
            } catch (Throwable t) {
                log.error("Error in scheduled task", t);
            }
        }
    }
}