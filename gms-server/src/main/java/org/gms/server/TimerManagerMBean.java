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

/**
 * 【类型】TimerManagerMBean（interface），包 `org.gms.server`。
 *
 * 定时任务管理器 JMX Bean 接口，暴露任务调度的运行状态指标（活跃任务数、已完成任务数、队列深度等），供 JMX 监控工具查询。
 *
 * @author 萧曵
 */
public interface TimerManagerMBean {
    boolean isTerminated();
    boolean isShutdown();
    long getCompletedTaskCount();
    long getActiveCount();
    long getTaskCount();
    int getQueuedTasks();
}
