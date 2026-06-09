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
package org.gms.scripting.event;

import org.gms.scripting.event.scheduler.EventScriptScheduler;

/**
 * 事件调度Future
 * 封装事件脚本调度器中已调度任务的取消能力
 */
public class EventScheduledFuture {
    /** 关联的执行任务 */
    Runnable r;
    /** 事件脚本调度器引用 */
    EventScriptScheduler ess;

    /**
     * 构造函数
     *
     * @param r   执行任务
     * @param ess 事件脚本调度器
     */
    public EventScheduledFuture(Runnable r, EventScriptScheduler ess) {
        this.r = r;
        this.ess = ess;
    }

    /**
     * 取消已调度的任务
     * 无论传入的boolean值如何，始终实现"运行中不中断"的行为
     *
     * @param dummy 无实际作用（保留接口兼容性）
     */
    public void cancel(boolean dummy) {
        // 无论传入的boolean值如何，始终实现"运行中不中断"的行为
        ess.cancelEntry(r);
    }
}