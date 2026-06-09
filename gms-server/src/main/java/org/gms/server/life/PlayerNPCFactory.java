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
package org.gms.server.life;

import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.wz.WZFiles;

/**
 * 玩家NPC工厂
 * 负责验证脚本ID是否存在于WZ数据中，防止因引用不存在的脚本ID导致客户端崩溃
 *
 * @author RonanLana
 */
public class PlayerNPCFactory {
    /** NPC数据提供者 */
    private static final DataProvider npcData = DataProviderFactory.getDataProvider(WZFiles.NPC);

    /**
     * 检查脚本ID是否存在于WZ数据中
     * 使用synchronized确保线程安全的数据访问
     *
     * @param scriptid 脚本ID
     * @return true表示WZ中存在对应的NPC数据
     */
    public synchronized static boolean isExistentScriptid(int scriptid) {
        return npcData.getData(scriptid + ".img") != null;
    }
}