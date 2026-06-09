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
package org.gms.net.server.task;

import org.gms.client.processor.npc.DueyProcessor;
import org.gms.client.processor.npc.FredrickProcessor;

/**
 * 快递与弗雷德里克定时任务
 * 定期检查弗雷德里克（Fredrick）和快递（Duey）的到期包裹，执行过期清理
 *
 * @author Ronan
 */
public class DueyFredrickTask implements Runnable {
    /** 弗雷德里克处理器 */
    private final FredrickProcessor fredrickProcessor;

    /**
     * 构造快递与弗雷德里克定时任务
     *
     * @param fredrickProcessor 弗雷德里克处理器
     */
    public DueyFredrickTask(FredrickProcessor fredrickProcessor) {
        this.fredrickProcessor = fredrickProcessor;
    }

    @Override
    public void run() {
        fredrickProcessor.runFredrickSchedule();
        DueyProcessor.runDueyExpireSchedule();
    }
}