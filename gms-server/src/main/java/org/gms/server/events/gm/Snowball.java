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

import java.util.LinkedList;
import java.util.List;

/**
 * 打雪仗活动
 * 两队玩家通过击打雪人推进雪球距离，先将雪球推到对方基地的队伍获胜
 * 支持命中次数和雪人生命值管理
 *
 * @author kevintjuh93
 */
public class Snowball {
    /** 活动地图 */
    private final MapleMap map;
    /** 雪球位置 */
    private int position = 0;
    /** 命中次数 */
    private int hits = 3;
    /** 雪人生命值 */
    private int snowmanhp = 1000;
    /** 是否可命中 */
    private boolean hittable = false;
    /** 队伍编号 */
    private final int team;
    /** 是否获胜 */
    private boolean winner = false;
    /** 参与玩家列表 */
    List<Character> characters = new LinkedList<>();

    /**
     * 构造打雪仗活动
     *
     * @param team 队伍编号
     * @param map  活动地图
     */
    public Snowball(int team, MapleMap map) {
        this.map = map;
        this.team = team;

        for (Character chr : map.getCharacters()) {
            if (chr.getTeam() == team) {
                characters.add(chr);
            }
        }
    }

    /**
     * 开始打雪仗活动
     * 初始化雪球面板，启动10分钟倒计时
     * 时间结束后比较两队的雪球位置决定胜负
     */
    public void startEvent() {
        if (hittable == true) {
            return;
        }

        for (Character chr : characters) {
            if (chr != null) {
                chr.sendPacket(PacketCreator.rollSnowBall(false, 1, map.getSnowball(0), map.getSnowball(1)));
                chr.sendPacket(PacketCreator.getClock(600));
            }
        }
        hittable = true;
        TimerManager.getInstance().schedule(() -> {
            if (map.getSnowball(team).getPosition() > map.getSnowball(team == 0 ? 1 : 0).getPosition()) {
                for (Character chr : characters) {
                    if (chr != null) {
                        chr.sendPacket(PacketCreator.rollSnowBall(false, 3, map.getSnowball(0), map.getSnowball(0)));
                    }
                }
                winner = true;
            } else if (map.getSnowball(team == 0 ? 1 : 0).getPosition() > map.getSnowball(team).getPosition()) {
                for (Character chr : characters) {
                    if (chr != null) {
                        chr.sendPacket(PacketCreator.rollSnowBall(false, 4, map.getSnowball(0), map.getSnowball(0)));
                    }
                }
                winner = true;
            } //Else
            warpOut();
        }, 600000);

    }

    /**
     * 判断是否可命中
     *
     * @return true可命中，false不可命中
     */
    public boolean isHittable() {
        return hittable;
    }

    /**
     * 设置是否可命中
     *
     * @param hit true可命中，false不可命中
     */
    public void setHittable(boolean hit) {
        this.hittable = hit;
    }

    /**
     * 获取雪球位置
     *
     * @return 雪球位置
     */
    public int getPosition() {
        return position;
    }

    /**
     * 获取雪人生命值
     *
     * @return 雪人HP
     */
    public int getSnowmanHP() {
        return snowmanhp;
    }

    /**
     * 设置雪人生命值
     *
     * @param hp 新HP值
     */
    public void setSnowmanHP(int hp) {
        this.snowmanhp = hp;
    }

    /**
     * 击打雪球
     * 根据伤害类型和数值更新雪球位置或雪人生命值
     * 雪球推进时广播位置变化，特定位置触发对方队伍消息
     *
     * @param what   击打类型（0/1为普通击打，2或以上为其他）
     * @param damage 伤害值
     */
    public void hit(int what, int damage) {
        if (what < 2) {
            if (damage > 0) {
                this.hits--;
            } else {
                if (this.snowmanhp - damage < 0) {
                    this.snowmanhp = 0;

                    TimerManager.getInstance().schedule(() -> {
                        setSnowmanHP(7500);
                        message(5);
                    }, 10000);
                } else {
                    this.snowmanhp -= damage;
                }
                map.broadcastMessage(PacketCreator.rollSnowBall(false, 1, map.getSnowball(0), map.getSnowball(1)));
            }
        }

        if (this.hits == 0) {
            this.position += 1;
            switch (this.position) {
            case 45:
                map.getSnowball(team == 0 ? 1 : 0).message(1);
                break;
            case 290:
                map.getSnowball(team == 0 ? 1 : 0).message(2);
                break;
            case 560:
                map.getSnowball(team == 0 ? 1 : 0).message(3);
                break;
            }

            this.hits = 3;
            map.broadcastMessage(PacketCreator.rollSnowBall(false, 0, map.getSnowball(0), map.getSnowball(1)));
            map.broadcastMessage(PacketCreator.rollSnowBall(false, 1, map.getSnowball(0), map.getSnowball(1)));
        }
        map.broadcastMessage(PacketCreator.hitSnowBall(what, damage));
    }

    /**
     * 向本队玩家发送雪球消息
     *
     * @param message 消息序号
     */
    public void message(int message) {
        for (Character chr : characters) {
            if (chr != null) {
                chr.sendPacket(PacketCreator.snowballMessage(team, message));
            }
        }
    }

    /**
     * 传出玩家
     * 获胜队伍传送到胜利地图，失败队伍传送到出口
     */
    public void warpOut() {
        TimerManager.getInstance().schedule(() -> {
            if (winner) {
                map.warpOutByTeam(team, MapId.EVENT_WINNER);
            } else {
                map.warpOutByTeam(team, MapId.EVENT_EXIT);
            }

            map.setSnowball(team, null);
        }, 10000);
    }
}