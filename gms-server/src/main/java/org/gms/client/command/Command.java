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
 * 指令 基类
 */
@Data
public abstract class Command {
    /**
     * 命令级别,gm
     */
    protected int rank;
    /**
     * 描述
     */
    protected String description;

    /**
     * 指令逻辑 需要子类实现
     */
    public abstract void execute(Client client, String[] params);

    /**
     * 从指定索引开始，将字符串数组中的元素拼接成一个字符串
     *
     * @param arr 字符串数组
     * @param start 开始拼接的索引
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
