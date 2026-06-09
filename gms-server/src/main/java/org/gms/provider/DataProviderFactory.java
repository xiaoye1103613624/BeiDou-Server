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
package org.gms.provider;

import org.gms.provider.wz.WZFiles;
import org.gms.provider.wz.XMLWZFile;

import java.nio.file.Path;

/**
 * 数据提供者工厂类
 * 根据WZ文件枚举创建对应的XML数据提供者
 */
public class DataProviderFactory {
    /**
     * 从WZ文件路径创建数据提供者
     *
     * @param in WZ文件路径
     * @return XMLWZFile数据提供者
     */
    private static DataProvider getWZ(Path in) {
        return new XMLWZFile(in);
    }

    /**
     * 根据WZ文件枚举获取数据提供者
     *
     * @param in WZ文件枚举
     * @return 数据提供者实例
     */
    public static DataProvider getDataProvider(WZFiles in) {
        return getWZ(in.getFile());
    }
}