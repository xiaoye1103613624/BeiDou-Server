/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
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
package org.gms.net.server.services.type;

import org.gms.net.server.services.BaseService;
import org.gms.net.server.services.Service;
import org.gms.net.server.services.ServiceType;
import org.gms.net.server.services.task.channel.EventService;
import org.gms.net.server.services.task.channel.MobAnimationService;
import org.gms.net.server.services.task.channel.MobClearSkillService;
import org.gms.net.server.services.task.channel.MobMistService;
import org.gms.net.server.services.task.channel.MobStatusService;
import org.gms.net.server.services.task.channel.OverallService;

/**
 * 频道服务枚举
 * 定义频道级别的服务类型及其对应的服务实现类
 *
 * @author Ronan
 */
public enum ChannelServices implements ServiceType {

    /** 怪物状态服务 */
    MOB_STATUS(MobStatusService.class),
    /** 怪物动画服务 */
    MOB_ANIMATION(MobAnimationService.class),
    /** 怪物清技能服务 */
    MOB_CLEAR_SKILL(MobClearSkillService.class),
    /** 怪物迷雾服务 */
    MOB_MIST(MobMistService.class),
    /** 事件服务 */
    EVENT(EventService.class),
    /** 综合服务 */
    OVERALL(OverallService.class);

    /** 服务实现类 */
    private final Class<? extends BaseService> s;

    /**
     * 构造方法
     *
     * @param service 服务实现类
     */
    ChannelServices(Class<? extends BaseService> service) {
        s = service;
    }

    /**
     * 创建服务实例
     *
     * @return 服务包装实例
     */
    @Override
    public Service createService() {
        return new Service(s);
    }

    /**
     * 获取所有枚举值
     *
     * @return 枚举值数组
     */
    @Override
    public ChannelServices[] enumValues() {
        return ChannelServices.values();
    }

}
