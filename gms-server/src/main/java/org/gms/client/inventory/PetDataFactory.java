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
package org.gms.client.inventory;

import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;

import java.util.HashMap;
import java.util.Map;

/**
 * 宠物数据工厂
 * 从WZ文件加载宠物数据：命令技能和饥饿度，以HashMap缓存
 * 提供宠物命令技能和饥饿度查询
 *
 * @author Danny (Leifde)
 */
public class PetDataFactory {
    /** WZ数据源 */
    private static final DataProvider dataRoot = DataProviderFactory.getDataProvider(WZFiles.ITEM);
    /** 宠物命令缓存（宠物ID_技能ID -> PetCommand） */
    private static final Map<String, PetCommand> petCommands = new HashMap<>();
    /** 宠物饥饿度缓存（宠物ID -> 饥饿度） */
    private static final Map<Integer, Integer> petHunger = new HashMap<>();

    /**
     * 获取宠物命令技能（双重检查锁DCL）
     * 从WZ文件Pet/{id}.img/interact/{skillId}节点读取触发概率(prob)和亲密度增量(inc)
     *
     * @param petId   宠物ID
     * @param skillId 技能ID
     * @return 宠物命令对象
     */
    public static PetCommand getPetCommand(int petId, int skillId) {
        PetCommand ret = petCommands.get(petId + "" + skillId);
        if (ret != null) {
            return ret;
        }
        synchronized (petCommands) {
            ret = petCommands.get(petId + "" + skillId);
            if (ret == null) {
                Data skillData = dataRoot.getData("Pet/" + petId + ".img");
                int prob = 0;
                int inc = 0;
                if (skillData != null) {
                    prob = DataTool.getInt("interact/" + skillId + "/prob", skillData, 0);
                    inc = DataTool.getInt("interact/" + skillId + "/inc", skillData, 0);
                }
                ret = new PetCommand(petId, skillId, prob, inc);
                petCommands.put(petId + "" + skillId, ret);
            }
            return ret;
        }
    }

    /**
     * 获取宠物饥饿度（双重检查锁DCL）
     * 从WZ文件Pet/{id}.img/info/hungry节点读取
     *
     * @param petId 宠物ID
     * @return 饥饿度数值
     */
    public static int getHunger(int petId) {
        Integer ret = petHunger.get(petId);
        if (ret != null) {
            return ret;
        }
        synchronized (petHunger) {
            ret = petHunger.get(petId);
            if (ret == null) {
                ret = DataTool.getInt(dataRoot.getData("Pet/" + petId + ".img").getChildByPath("info/hungry"), 1);
            }
            return ret;
        }
    }
}