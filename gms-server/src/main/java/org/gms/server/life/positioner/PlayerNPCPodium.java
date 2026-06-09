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
package org.gms.server.life.positioner;

import org.gms.config.GameConfig;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.life.PlayerNPC;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 玩家NPC领奖台位置管理器
 * 负责在名人堂领奖台地图上为玩家NPC分配位置
 * 领奖台有三个平台，每个平台有固定位置布局
 * 注意：领奖台使用getGroundBelow，底层会减去7像素，本系统在计算时加回7以抵消
 *
 * @author RonanLana
 */
public class PlayerNPCPodium {
    private static final Logger log = LoggerFactory.getLogger(PlayerNPCPodium.class);

    /**
     * 获取平台X坐标偏移
     *
     * @param platform 平台编号（0/1/2）
     * @return X坐标偏移
     */
    private static int getPlatformPosX(int platform) {
        return switch (platform) {
            case 0 -> -50;
            case 1 -> -170;
            default -> 70;
        };
    }

    /**
     * 获取平台Y坐标偏移
     *
     * @param platform 平台编号（0/1/2）
     * @return Y坐标偏移
     */
    private static int getPlatformPosY(int platform) {
        if (platform == 0) {
            return -47;
        }
        return 40;
    }

    /**
     * 计算指定排名和步数下的位置
     * 根据排名确定平台，在平台内均匀分布
     *
     * @param rank 排名序号
     * @param step 步数
     * @return 计算出的位置
     */
    private static Point calcNextPos(int rank, int step) {
        int podiumPlatform = rank / step;
        int relativePos = (rank % step) + 1;

        Point pos = new Point(getPlatformPosX(podiumPlatform) + ((100 * relativePos) / (step + 1)), getPlatformPosY(podiumPlatform));
        return pos;
    }

    /**
     * 重新排列已有NPC位置并返回新NPC的位置
     *
     * @param map 地图
     * @param newStep 当前步数
     * @param pnpcs 已有NPC列表
     * @return 新NPC的可用位置
     */
    private static Point rearrangePlayerNpcs(MapleMap map, int newStep, List<PlayerNPC> pnpcs) {
        int i = 0;
        for (PlayerNPC pn : pnpcs) {
            pn.updatePlayerNPCPosition(map, calcNextPos(i, newStep));
            i++;
        }

        return calcNextPos(i, newStep);
    }

    /**
     * 重新组织地图上的所有玩家NPC（领奖台版本）
     * 按脚本ID排序后重新布局，同步更新所有频道
     *
     * @param map 地图
     * @param newStep 当前步数
     * @param mmoList 地图对象列表
     * @return 新NPC的可用位置
     */
    private static Point reorganizePlayerNpcs(MapleMap map, int newStep, List<MapObject> mmoList) {
        if (!mmoList.isEmpty()) {
            if (GameConfig.getServerBoolean("use_debug")) {
                log.info("Re-organizing pnpc map, step {}", newStep);
            }

            List<PlayerNPC> playerNpcs = new ArrayList<>(mmoList.size());
            for (MapObject mmo : mmoList) {
                playerNpcs.add((PlayerNPC) mmo);
            }

            playerNpcs.sort((p1, p2) -> {
                return p1.getScriptId() - p2.getScriptId(); // scriptid as playernpc history
            });

            for (Channel ch : Server.getInstance().getChannelsFromWorld(map.getWorld())) {
                MapleMap m = ch.getMapFactory().getMap(map.getId());

                for (PlayerNPC pn : playerNpcs) {
                    m.removeMapObject(pn);
                    m.broadcastMessage(PacketCreator.removeNPCController(pn.getObjectId()));
                    m.broadcastMessage(PacketCreator.removePlayerNPC(pn.getObjectId()));
                }
            }

            Point ret = rearrangePlayerNpcs(map, newStep, playerNpcs);

            for (Channel ch : Server.getInstance().getChannelsFromWorld(map.getWorld())) {
                MapleMap m = ch.getMapFactory().getMap(map.getId());

                for (PlayerNPC pn : playerNpcs) {
                    m.addPlayerNPCMapObject(pn);
                    m.broadcastMessage(PacketCreator.spawnPlayerNPC(pn));
                    m.broadcastMessage(PacketCreator.getPlayerNPC(pn));
                }
            }

            return ret;
        }

        return null;
    }

    /**
     * 编码领奖台数据
     * 将步数和计数打包为单个整数
     *
     * @param podiumStep 当前步数
     * @param podiumCount 当前NPC计数
     * @return 编码后的数据
     */
    private static int encodePodiumData(int podiumStep, int podiumCount) {
        return (podiumCount * (1 << 5)) + podiumStep;
    }

    /**
     * 获取下一个玩家NPC的领奖台位置
     * 当当前步数下所有位置都满时，增加步数并重新组织布局
     *
     * @param map 地图
     * @param podiumData 编码后的领奖台数据
     * @return 可用位置，无可用位置返回null
     */
    private static Point getNextPlayerNpcPosition(MapleMap map, int podiumData) {
        int podiumStep = podiumData % (1 << 5), podiumCount = (podiumData / (1 << 5));

        if (podiumCount >= 3 * podiumStep) {
            if (podiumStep >= GameConfig.getServerInt("playernpc_area_steps")) {
                return null;
            }

            List<MapObject> mmoList = map.getMapObjectsInRange(new Point(0, 0), Double.POSITIVE_INFINITY, Arrays.asList(MapObjectType.PLAYER_NPC));
            map.getWorldServer().setPlayerNpcMapPodiumData(map.getId(), encodePodiumData(podiumStep + 1, podiumCount + 1));
            return reorganizePlayerNpcs(map, podiumStep + 1, mmoList);
        } else {
            map.getWorldServer().setPlayerNpcMapPodiumData(map.getId(), encodePodiumData(podiumStep, podiumCount + 1));
            return calcNextPos(podiumCount, podiumStep);
        }
    }

    /**
     * 获取下一个玩家NPC的领奖台位置（公开接口）
     * 返回的位置经过getGroundBelow校正
     *
     * @param map 地图
     * @return 可用位置，无可用位置返回null
     */
    public static Point getNextPlayerNpcPosition(MapleMap map) {
        Point pos = getNextPlayerNpcPosition(map, map.getWorldServer().getPlayerNpcMapPodiumData(map.getId()));
        if (pos == null) {
            return null;
        }

        return map.getGroundBelow(pos);
    }
}