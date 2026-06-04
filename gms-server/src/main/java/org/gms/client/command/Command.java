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

/*
   @Author: Arthur L - Refactored command content into modules
*/
package org.gms.client.command;

import lombok.Data;
import org.gms.client.Client;

/**
 * 【GM/玩家指令】Command：聊天或控制台命令实现基类。
 * <p>所有命令都继承此类，实现 execute 方法处理具体逻辑</p>
 */
@Data
public abstract class Command {

    /** 命令所需GM等级（0=普通玩家可用） */
    protected int rank;
    /** 命令描述（用于帮助信息） */
    protected String description;

    /**
     * 执行命令
     * @param client 客户端
     * @param params 命令参数
     */
    public abstract void execute(Client client, String[] params);

    /**
     * 从指定索引开始拼接字符串数组
     * @param arr 字符串数组
     * @param start 起始索引
     * @return 拼接后的字符串
     */
    protected String joinStringFrom(String[] arr, int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i != arr.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
}