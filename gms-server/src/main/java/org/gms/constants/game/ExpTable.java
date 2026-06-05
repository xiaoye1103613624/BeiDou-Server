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
package org.gms.constants.game;

/**
 * 【类】ExpTable，包 {@code org.gms.constants.game}。
 * 经验值常量表定义类。
 *
 * <p>该类定义了MapleStory游戏中各种系统所需的经验值数据，包括：</p>
 * <ul>
 *   <li>角色升级经验表（1-200级）</li>
 *   <li>装备强化经验表</li>
 *   <li>宠物亲密度升级经验表</li>
 *   <li>坐骑（骑宠）经验升级表</li>
 * </ul>
 *
 * <p>所有经验表数据均为静态只读，用于游戏中各类经验值计算和判断。</p>
 *
 * @author OdinMS (original)
 */
public final class ExpTable {
    /**
     * 角色升级经验表数组
     * <p>索引表示等级（从0开始），值表示升到下一级所需的累计经验值。
     * 200级以后统一返回20亿经验值。</p>
     */
    private static final int[] exp = {15, 15, 34, 57, 92, 135, 372, 560, 840, 1144, 1242, 1573, 2144, 2800, 3640, 4700, 5893, 7360, 9144, 11120, 13477, 16268, 19320, 22880, 27008, 31477, 36600, 42444, 48720, 55813, 63800, 86784, 98208, 110932, 124432, 139372, 155865, 173280, 192400, 213345, 235372, 259392, 285532, 312928, 342624, 374760, 408336, 445544, 483532, 524160, 567772, 598886, 631704, 666321, 702836, 741351, 781976, 824828, 870028, 917625, 967995, 1021041, 1076994, 1136013, 1198266, 1263930, 1333194, 1406252, 1483314, 1564600, 1650340, 1740778, 1836173, 1936794, 2042930, 2154882, 2272970, 2397528, 2528912, 2667496, 2813674, 2967863, 3130502, 3302053, 3483005, 3673873, 3875201, 4087562, 4311559, 4547832, 4797053, 5059931, 5337215, 5629694, 5938202, 6263614, 6606860, 6968915, 7350811, 7753635, 8178534, 8626718, 9099462, 9598112, 10124088, 10678888, 11264090, 11881362, 12532461, 13219239, 13943653, 14707765, 15513750, 16363902, 17260644, 18206527, 19204245, 20256637, 21366700, 22537594, 23772654, 25075395, 26449526, 27898960, 29427822, 31040466, 32741483, 34535716, 36428273, 38424542, 40530206, 42751262, 45094030, 47565183, 50171755, 52921167, 55821246, 58880250, 62106888, 65510344, 69100311, 72887008, 76881216, 81094306, 85594273, 90225770, 95170142, 100385466, 105886589, 111689174, 117809740, 124265714, 131075474, 138258410, 145834970, 153826726, 162256430, 171148082, 180526997, 190419876, 200854885, 211861732, 223471711, 223471711, 248635353, 262260570, 276632449, 291791906, 307782102, 324648562, 342439302, 361204976, 380999008, 401877754, 423900654, 447130410, 471633156, 497478653, 524740482, 553496261, 583827855, 615821622, 649568646, 685165008, 722712050, 762316670, 804091623, 848155844, 894634784, 943660770, 995373379, 1049919840, 1107455447, 1168144006, 1232158297, 1299680571, 1370903066, 1446028554, 1525246918, 1608855764, 1697021059};

    /**
     * 装备强化经验表数组
     * <p>索引表示强化等级，值表示升到下一级所需的累计经验值。
     * 用于装备强化系统（星之力/潜力等）的经验计算。</p>
     */
    private static final int[] equip = {1, 15, 19, 23, 35, 43, 98, 188, 237, 280, 304, 331, 571, 656, 840, 1060, 1193, 1467, 1784, 1976, 2357, 2791, 3052, 3560, 4128, 4469, 5123, 5844, 6276, 7093, 10000};

    /**
     * 宠物亲密度经验表数组
     * <p>索引表示亲密度等级，值表示升到下一级所需的累计亲密度值。
     * 30级为满级（使用Integer.MAX_VALUE表示不可达）。</p>
     */
    private static final int[] pet = {1, 1, 3, 6, 14, 31, 60, 108, 181, 287, 434, 632, 891, 1224, 1642, 2161, 2793, 3557, 4467, 5542, 6801, 8263, 9950, 11882, 14084, 16578, 19391, 22547, 26074, 30000, 2147483647};

    /**
     * 坐骑（骑宠）经验表数组
     * <p>索引表示坐骑等级，值表示升到下一级所需的累计经验值。
     * 用于骑宠系统的经验计算和等级判断。</p>
     */
    private static final int[] mount = {1, 24, 50, 105, 134, 196, 254, 263, 315, 367, 430, 543, 587, 679, 725, 897, 1146, 1394, 1701, 2247, 2543, 2898, 3156, 3313, 3584, 3923, 4150, 4305, 4550, 5600};

    /**
     * 获取指定等级升级所需的经验值
     * <p>根据当前等级获取升到下一级所需的累计经验值。
     * 200级以后统一返回20亿（Integer.MAX_VALUE附近），表示封顶。</p>
     * @param level 当前等级
     * @return 升到下一级所需的经验值
     */
    public static int getExpNeededForLevel(int level) {
        return level > 200 ? 2000000000 : exp[level];
    }

    /**
     * 获取指定亲密度等级所需的经验值
     * <p>用于宠物亲密度系统，计算宠物升级到指定亲密度等级所需的经验值。</p>
     * @param level 亲密度等级
     * @return 升到下一级所需的亲密度值
     */
    public static int getTamenessNeededForLevel(int level) {
        return pet[level];
    }

    /**
     * 获取指定坐骑等级升级所需的经验值
     * <p>用于骑宠系统，计算坐骑升级到指定等级所需的经验值。</p>
     * @param level 坐骑等级
     * @return 升到下一级所需的经验值
     */
    public static int getMountExpNeededForLevel(int level) {
        return mount[level];
    }

    /**
     * 获取指定装备强化等级所需的经验值
     * <p>用于装备强化系统（如星之力），计算装备强化到指定等级所需的经验值。</p>
     * @param level 装备强化等级
     * @return 升到下一级所需的强化经验值
     */
    public static int getEquipExpNeededForLevel(int level) {
        return equip[level];
    }

    /**
     * 获取坐骑最高等级
     * @return 坐骑可达到的最高等级
     */
    public static int getMountMaxLevel() {
        return mount.length;
    }


}