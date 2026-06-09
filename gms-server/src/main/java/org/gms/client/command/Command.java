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
package org.gms.client.command;

import org.gms.client.Client;
import lombok.Getter;
import lombok.Setter;

/**
 * 命令抽象基类
 * 所有GM命令和玩家命令的基类
 * 子类需实现execute()方法执行具体命令逻辑
 *
 * @Author: Arthur L - Refactored command content into modules
 */
@Getter
@Setter
public abstract class Command {

    /** 命令所需GM等级 */
    protected int rank;
    /** 命令描述 */
    protected String description;

    /**
     * 执行命令
     *
     * @param client 客户端会话
     * @param params 命令参数数组
     */
    public abstract void execute(Client client, String[] params);

    /**
     * 将字符串数组从指定位置开始拼接为单个字符串
     *
     * @param arr   字符串数组
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