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

package org.gms.server.events.gm;

import org.gms.client.Character;
import org.gms.constants.id.MapId;
import org.gms.server.TimerManager;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 打椰子GM活动
 * 两支队伍在限时内击打椰子获取分数，分数高的队伍获胜
 * 支持炸弹击打和掉落机制，由GM发起活动
 *
 * @author kevintjuh93
 */
public class Coconut extends Event {
    /** 活动地图 */
    private MapleMap map = null;
    /** 枫叶队分数 */
    private int MapleScore = 0;
    /** 冒险岛队分数 */
    private int StoryScore = 0;
    /** 炸弹击打计数 */
    private int countBombing = 80;
    /** 掉落椰子计数 */
    private int countFalling = 401;
    /** 停止掉落计数 */
    private int countStopped = 20;
    /** 椰子列表 */
    private final List<Coconuts> coconuts = new LinkedList<>();

    /**
     * 构造打椰子活动
     *
     * @param map 活动地图
     */
    public Coconut(MapleMap map) {
        super(1, 50);
        this.map = map;
    }

    public void startEvent() {
        map.startEvent();
        for (int i = 0; i < 506; i++) {
            coconuts.add(new Coconuts(i));
        }
        map.broadcastMessage(PacketCreator.hitCoconut(true, 0, 0));
        setCoconutsHittable(true);
        map.broadcastMessage(PacketCreator.getClock(300));

        TimerManager.getInstance().schedule(() -> {
            if (map.getId() == MapId.EVENT_COCONUT_HARVEST) {
                if (getMapleScore() == getStoryScore()) {
                    bonusTime();
                } else if (getMapleScore() > getStoryScore()) {
                    for (Character chr : map.getCharacters()) {
                        if (chr.getTeam() == 0) {
                            chr.sendPacket(PacketCreator.showEffect("event/coconut/victory"));
                            chr.sendPacket(PacketCreator.playSound("Coconut/Victory"));
                        } else {
                            chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                            chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                        }
                    }
                    warpOut();
                } else {
                    for (Character chr : map.getCharacters()) {
                        if (chr.getTeam() == 1) {
                            chr.sendPacket(PacketCreator.showEffect("event/coconut/victory"));
                            chr.sendPacket(PacketCreator.playSound("Coconut/Victory"));
                        } else {
                            chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                            chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                        }
                    }
                    warpOut();
                }
            }
        }, 300000);
    }

    public void bonusTime() {
        map.broadcastMessage(PacketCreator.getClock(120));
        TimerManager.getInstance().schedule(() -> {
            if (getMapleScore() == getStoryScore()) {
                for (Character chr : map.getCharacters()) {
                    chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                    chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                }
                warpOut();
            } else if (getMapleScore() > getStoryScore()) {
                for (Character chr : map.getCharacters()) {
                    if (chr.getTeam() == 0) {
                        chr.sendPacket(PacketCreator.showEffect("event/coconut/victory"));
                        chr.sendPacket(PacketCreator.playSound("Coconut/Victory"));
                    } else {
                        chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                        chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                    }
                }
                warpOut();
            } else {
                for (Character chr : map.getCharacters()) {
                    if (chr.getTeam() == 1) {
                        chr.sendPacket(PacketCreator.showEffect("event/coconut/victory"));
                        chr.sendPacket(PacketCreator.playSound("Coconut/Victory"));
                    } else {
                        chr.sendPacket(PacketCreator.showEffect("event/coconut/lose"));
                        chr.sendPacket(PacketCreator.playSound("Coconut/Failed"));
                    }
                }
                warpOut();
            }
        }, 120000);

    }

    /**
     * 传出玩家
     * 根据胜负将不同队伍的玩家传送到对应地图（获胜区或出口）
     */
    public void warpOut() {
        setCoconutsHittable(false);
        TimerManager.getInstance().schedule(() -> {
            List<Character> chars = new ArrayList<>(map.getCharacters());

            for (Character chr : chars) {
                if ((getMapleScore() > getStoryScore() && chr.getTeam() == 0) || (getStoryScore() > getMapleScore() && chr.getTeam() == 1)) {
                    chr.changeMap(MapId.EVENT_WINNER);
                } else {
                    chr.changeMap(MapId.EVENT_EXIT);
                }
            }
            map.setCoconut(null);
        }, 12000);
    }

    /**
     * 获取枫叶队分数
     *
     * @return 枫叶队分数
     */
    public int getMapleScore() {
        return MapleScore;
    }

    /**
     * 获取冒险岛队分数
     *
     * @return 冒险岛队分数
     */
    public int getStoryScore() {
        return StoryScore;
    }

    /**
     * 枫叶队加1分
     */
    public void addMapleScore() {
        this.MapleScore += 1;
    }

    /**
     * 冒险岛队加1分
     */
    public void addStoryScore() {
        this.StoryScore += 1;
    }

    /**
     * 获取炸弹击打次数
     *
     * @return 炸弹击打计数
     */
    public int getBombings() {
        return countBombing;
    }

    /**
     * 减少炸弹击打计数
     */
    public void bombCoconut() {
        countBombing--;
    }

    /**
     * 获取掉落计数
     *
     * @return 掉落椰子计数
     */
    public int getFalling() {
        return countFalling;
    }

    /**
     * 减少掉落计数
     */
    public void fallCoconut() {
        countFalling--;
    }

    /**
     * 获取停止掉落计数
     *
     * @return 停止掉落计数
     */
    public int getStopped() {
        return countStopped;
    }

    /**
     * 减少停止掉落计数
     */
    public void stopCoconut() {
        countStopped--;
    }

    /**
     * 根据ID获取椰子
     *
     * @param id 椰子ID
     * @return 椰子对象
     */
    public Coconuts getCoconut(int id) {
        return coconuts.get(id);
    }

    /**
     * 获取所有椰子列表
     *
     * @return 椰子列表
     */
    public List<Coconuts> getAllCoconuts() {
        return coconuts;
    }

    /**
     * 设置所有椰子是否可被命中
     *
     * @param hittable true可命中，false不可命中
     */
    public void setCoconutsHittable(boolean hittable) {
        for (Coconuts nut : coconuts) {
            nut.setHittable(hittable);
        }
    }
}