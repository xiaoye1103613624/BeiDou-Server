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
 * 【类型】DataProviderFactory（class），包 {@code org.gms.provider}。WZ 数据提供器工厂：根据 WZFiles 枚举创建对应的 XMLWZFile
 * 实例作为 DataProvider，供各 Provider 层读取 WZ XML 中的怪物、物品、地图、技能等静态游戏数据。
 */
public class DataProviderFactory {
    private static DataProvider getWZ(Path in) {
        return new XMLWZFile(in);
    }

    public static DataProvider getDataProvider(WZFiles in) {
        return getWZ(in.getFile());
    }
}