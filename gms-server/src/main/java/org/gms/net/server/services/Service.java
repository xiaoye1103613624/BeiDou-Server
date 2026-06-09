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
 * 游戏服务包装类
 * 通过反射创建服务实例，提供服务获取和销毁能力
 *
 * @author Ronan
 */
public class Service<T extends BaseService> {

    /** 服务类类型 */
    private Class<T> cls;
    /** 服务实例 */
    private BaseService service;

    /**
     * 构造方法，通过反射创建服务实例
     *
     * @param s 服务类
     */
    public Service(Class<T> s) {
        try {
            cls = s;
            service = cls.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取服务实例
     *
     * @return 强类型转换后的服务实例
     */
    public T getService() {
        return cls.cast(service);
    }

    /**
     * 销毁服务，释放资源
     */
    public void dispose() {
        service.dispose();
        service = null;
    }

}