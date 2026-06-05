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
package org.gms.server.maps;

import org.gms.constants.id.MapId;

/**
 * 【类型】MiniDungeonInfo，enum，包 {@code org.gms.server.maps}。
 *
 * <p>迷你地下城信息枚举，定义游戏中所有迷你地下城的入口地图、副本起始 ID 和副本数量，提供根据地图 ID 查找对应地下城的方法。</p>
 *
 * <p>主要功能：</p>
 * <ul>
 *   <li>定义游戏中的所有迷你地下城</li>
 *   <li>提供地下城的基础地图ID</li>
 *   <li>提供地下城的副本起始ID</li>
 *   <li>提供地下城的副本数量</li>
 *   <li>提供根据地图ID查找地下城的功能</li>
 * </ul>
 *
 * @author Alan (SharpAceX)
 */
public enum MiniDungeonInfo {

    //http://bbb.hidden-street.net/search_finder/mini%20dungeon

    /** 蘑菇洞穴：入口地图ID、副本起始ID、副本数量 */
    CAVE_OF_MUSHROOMS(MapId.ANT_TUNNEL_2, MapId.CAVE_OF_MUSHROOMS_BASE, 30), // Horny Mushroom, Zombie Mushroom
    /** 哥lem城堡废墟：入口地图ID、副本起始ID、副本数量 */
    GOLEM_CASTLE_RUINS(MapId.SLEEPY_DUNGEON_4, MapId.GOLEMS_CASTLE_RUINS_BASE, 34), // Stone Golem, Mixed Golem
    /** 沙尘暴山丘：入口地图ID、副本起始ID、副本数量 */
    HILL_OF_SANDSTORMS(MapId.SAHEL_2, MapId.HILL_OF_SANDSTORMS_BASE, 30), // Sand Rat
    /** 勇士猪农场：入口地图ID、副本起始ID、副本数量 */
    HENESYS_PIG_FARM(MapId.RAIN_FOREST_EAST_OF_HENESYS, MapId.HENESYS_PIG_FARM_BASE, 30), // Pig, Ribbon Pig
    /** 德雷克的蓝洞：入口地图ID、副本起始ID、副本数量 */
    DRAKES_BLUE_CAVE(MapId.COLD_CRADLE, MapId.DRAKES_BLUE_CAVE_BASE, 30), // Dark Drake
    /** 打鼓兔兔巢穴：入口地图ID、副本起始ID、副本数量 */
    DRUMMER_BUNNYS_LAIR(MapId.EOS_TOWER_76TH_TO_90TH_FLOOR, MapId.DRUMMER_BUNNYS_LAIR_BASE, 30), // Drumming Bunny
    /** 凯塔鲁斯圆桌：入口地图ID、副本起始ID、副本数量 */
    THE_ROUND_TABLE_OF_KENTARUS(MapId.BATTLEFIELD_OF_FIRE_AND_WATER, MapId.ROUND_TABLE_OF_KENTAURUS_BASE, 30), // Blue/Red/Black Kentaurus
    /** 恢复记忆之地：入口地图ID、副本起始ID、副本数量 */
    THE_RESTORING_MEMORY(MapId.DRAGON_NEST_LEFT_BEHIND, MapId.RESTORING_MEMORY_BASE, 19), // Skelegon, Skelosaurus
    /** 新蒂安全区：入口地图ID、副本起始ID、副本数量 */
    NEWT_SECURED_ZONE(MapId.DESTROYED_DRAGON_NEST, MapId.NEWT_SECURED_ZONE_BASE, 19), // Jr. Newtie, Transforming Jr. Newtie
    /** 宝藏岛掠夺：入口地图ID、副本起始ID、副本数量 */
    PILLAGE_OF_TREASURE_ISLAND(MapId.RED_NOSE_PIRATE_DEN_2, MapId.PILLAGE_OF_TREASURE_ISLAND_BASE, 30), // Captain
    /** 严重错误：入口地图ID、副本起始ID、副本数量 */
    CRITICAL_ERROR(MapId.LAB_AREA_C1, MapId.CRITICAL_ERROR_BASE, 30), // Roid
    /** 再见站最长骑行：入口地图ID、副本起始ID、副本数量 */
    LONGEST_RIDE_ON_BYEBYE_STATION(MapId.FANTASY_THEME_PARK_3, MapId.LONGEST_RIDE_ON_BYEBYE_STATION, 19); // Froscola, Jester Scarlion

    /** 基础地图ID */
    private final int baseId;
    /** 地下城ID */
    private final int dungeonId;
    /** 地下城数量 */
    private final int dungeons;

    /**
     * 构造函数：创建迷你地下城信息实例
     * 
     * @param baseId 基础地图ID
     * @param dungeonId 地下城ID
     * @param dungeons 地下城数量
     */
    MiniDungeonInfo(int baseId, int dungeonId, int dungeons) {
        this.baseId = baseId;
        this.dungeonId = dungeonId;
        this.dungeons = dungeons;
    }

    /**
     * 获取基础地图ID
     * 
     * @return 基础地图ID
     */
    public int getBase() {
        return baseId;
    }

    /**
     * 获取地下城ID
     * 
     * @return 地下城ID
     */
    public int getDungeonId() {
        return dungeonId;
    }

    /**
     * 获取地下城数量
     * 
     * @return 地下城数量
     */
    public int getDungeons() {
        return dungeons;
    }

    /**
     * 检查地图是否为地下城地图
     * 
     * @param map 地图ID
     * @return 如果是地下城地图则返回true，否则返回false
     */
    public static boolean isDungeonMap(int map) {
        for (MiniDungeonInfo dungeon : MiniDungeonInfo.values()) {
            if (map >= dungeon.getDungeonId() && map <= dungeon.getDungeonId() + dungeon.getDungeons()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据地图ID获取地下城信息
     * 
     * @param map 地图ID
     * @return 地下城信息，如果找不到则返回null
     */
    public static MiniDungeonInfo getDungeon(int map) {
        for (MiniDungeonInfo dungeon : MiniDungeonInfo.values()) {
            if (map >= dungeon.getDungeonId() && map <= dungeon.getDungeonId() + dungeon.getDungeons()) {
                return dungeon;
            }
        }
        return null;
    }
}