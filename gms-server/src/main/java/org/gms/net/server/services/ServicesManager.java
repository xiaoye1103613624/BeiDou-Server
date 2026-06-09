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
package org.gms.net.server.services;

/**
 * 服务管理器
 * 管理一组游戏服务，统一初始化和销毁
 *
 * @author Ronan
 */
public class ServicesManager {

    /** 服务数组 */
    private Service[] services;

    /**
     * 构造方法，根据服务类型枚举初始化所有服务
     *
     * @param serviceBundle 服务类型包
     */
    public ServicesManager(ServiceType serviceBundle) {
        Enum[] serviceTypes = serviceBundle.enumValues();

        services = new Service[serviceTypes.length];
        for (Enum type : serviceTypes) {
            services[type.ordinal()] = ((ServiceType) type).createService();
        }
    }

    /**
     * 获取指定类型的服务
     *
     * @param s 服务类型
     * @return 服务实例
     */
    public Service getAccess(ServiceType s) {
        return services[s.ordinal()];
    }

    /**
     * 关闭所有服务，释放资源
     */
    public void shutdown() {
        for (Service service : services) {
            service.dispose();
        }
        services = null;
    }

}