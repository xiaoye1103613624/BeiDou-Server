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
 * 迷你地下城信息枚举
 * 定义所有迷你地下城的入口地图、基础地图ID和副本数量
 * 每个枚举值对应一个迷你地下城区域，玩家可在入口地图进入
 *
 * @author Alan (SharpAceX)
 */
public enum MiniDungeonInfo {

    /** 蘑菇洞穴（角蘑菇、僵尸蘑菇） */
    CAVE_OF_MUSHROOMS(MapId.ANT_TUNNEL_2, MapId.CAVE_OF_MUSHROOMS_BASE, 30),
    /** 石巨人城堡废墟（石巨人、混合石巨人） */
    GOLEM_CASTLE_RUINS(MapId.SLEEPY_DUNGEON_4, MapId.GOLEMS_CASTLE_RUINS_BASE, 34),
    /** 沙暴丘陵（沙鼠） */
    HILL_OF_SANDSTORMS(MapId.SAHEL_2, MapId.HILL_OF_SANDSTORMS_BASE, 30),
    /** 射手村猪农场（猪、缎带猪） */
    HENESYS_PIG_FARM(MapId.RAIN_FOREST_EAST_OF_HENESYS, MapId.HENESYS_PIG_FARM_BASE, 30),
    /** 龙之蓝洞（黑暗龙） */
    DRAKES_BLUE_CAVE(MapId.COLD_CRADLE, MapId.DRAKES_BLUE_CAVE_BASE, 30),
    /** 打鼓兔子的巢穴（打鼓兔子） */
    DRUMMER_BUNNYS_LAIR(MapId.EOS_TOWER_76TH_TO_90TH_FLOOR, MapId.DRUMMER_BUNNYS_LAIR_BASE, 30),
    /** 半人马圆桌（蓝/红/黑半人马） */
    THE_ROUND_TABLE_OF_KENTARUS(MapId.BATTLEFIELD_OF_FIRE_AND_WATER, MapId.ROUND_TABLE_OF_KENTAURUS_BASE, 30),
    /** 恢复的记忆（骷髅龙、骷髅恐龙） */
    THE_RESTORING_MEMORY(MapId.DRAGON_NEST_LEFT_BEHIND, MapId.RESTORING_MEMORY_BASE, 19),
    /** 纽特安全区（小纽特、变形小纽特） */
    NEWT_SECURED_ZONE(MapId.DESTROYED_DRAGON_NEST, MapId.NEWT_SECURED_ZONE_BASE, 19),
    /** 宝藏岛掠夺（船长） */
    PILLAGE_OF_TREASURE_ISLAND(MapId.RED_NOSE_PIRATE_DEN_2, MapId.PILLAGE_OF_TREASURE_ISLAND_BASE, 30),
    /** 致命错误（洛伊德） */
    CRITICAL_ERROR(MapId.LAB_AREA_C1, MapId.CRITICAL_ERROR_BASE, 30),
    /** 再见车站最长旅程（弗罗斯科拉、小丑斯卡利恩） */
    LONGEST_RIDE_ON_BYEBYE_STATION(MapId.FANTASY_THEME_PARK_3, MapId.LONGEST_RIDE_ON_BYEBYE_STATION, 19);

    /** 入口地图ID */
    private final int baseId;
    /** 地下城基础地图ID */
    private final int dungeonId;
    /** 地下城数量（连续地图数） */
    private final int dungeons;

    MiniDungeonInfo(int baseId, int dungeonId, int dungeons) {
        this.baseId = baseId;
        this.dungeonId = dungeonId;
        this.dungeons = dungeons;
    }

    /**
     * 获取入口地图ID
     *
     * @return 入口地图ID
     */
    public int getBase() {
        return baseId;
    }

    /**
     * 获取地下城基础地图ID
     *
     * @return 地下城基础地图ID
     */
    public int getDungeonId() {
        return dungeonId;
    }

    /**
     * 获取地下城数量
     *
     * @return 地下城数量（连续地图数）
     */
    public int getDungeons() {
        return dungeons;
    }

    /**
     * 判断指定地图是否为迷你地下城地图
     * 通过比较地图ID是否在地下城ID范围内
     *
     * @param map 地图ID
     * @return true表示是迷你地下城地图
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
     * 根据地图ID获取对应的迷你地下城信息
     *
     * @param map 地图ID
     * @return 对应的迷你地下城信息，未找到返回null
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