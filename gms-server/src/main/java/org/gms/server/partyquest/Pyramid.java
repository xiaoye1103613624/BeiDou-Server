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

package org.gms.server.partyquest;

import org.gms.client.Character;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.net.server.world.Party;
import org.gms.server.ItemInformationProvider;
import org.gms.server.TimerManager;
import org.gms.util.PacketCreator;

import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * 金字塔组队任务
 * 玩家在金字塔中击杀怪物，通过连击积累能量，获取经验和奖励
 * 支持4种难度模式：简单、普通、困难、地狱
 *
 * @author kevintjuh93
 */
public class Pyramid extends PartyQuest {
    /**
     * 金字塔难度模式枚举
     */
    public enum PyramidMode {
        EASY(0), NORMAL(1), HARD(2), HELL(3);
        int mode;

        PyramidMode(int mode) {
            this.mode = mode;
        }

        public int getMode() {
            return mode;
        }
    }

    /** 击杀数 */
    int kill = 0;
    /** 失误数 */
    int miss = 0;
    /** 冷却数 */
    int cool = 0;
    /** 经验值 */
    int exp = 0;
    /** 当前地图 */
    int map;
    /** 倒计时 */
    int count;
    /** 冷却增加量 */
    byte coolAdd = 5;
    /** 失误扣除量 */
    byte missSub = 4;
    /** 衰减量 */
    byte decrease = 1;
    /** 能量槽 */
    short gauge;
    /** 等级 */
    byte rank;
    /** 技能 */
    byte skill = 0;
    /** 当前阶段 */
    byte stage = 0;
    /** Buff计数 */
    byte buffcount = 0;
    /** 难度模式 */
    PyramidMode mode;

    /** 定时器 */
    ScheduledFuture<?> timer = null;
    ScheduledFuture<?> gaugeSchedule = null;

    /**
     * 构造金字塔任务
     *
     * @param party 队伍
     * @param mode  难度模式
     * @param mapid 起始地图ID
     */
    public Pyramid(Party party, PyramidMode mode, int mapid) {
        super(party);
        this.mode = mode;
        this.map = mapid;

        byte plus = (byte) mode.getMode();
        coolAdd += plus;
        missSub += plus;
        switch (plus) {
            case 0:
                decrease = 1;
            case 1:
            case 2:
                decrease = 2;
            case 3:
                decrease = 3;
        }
    }

    /**
     * 启动能量槽定时衰减任务
     */
    public void startGaugeSchedule() {
        if (gaugeSchedule == null) {
            gauge = 100;
            count = 0;
            gaugeSchedule = TimerManager.getInstance().register(() -> {
                gauge -= decrease;
                if (gauge <= 0) {
                    warp(MapId.NETTS_PYRAMID);
                }

            }, 1000);
        }
    }

    /**
     * 击杀怪物，增加击杀计数和能量槽
     */
    public void kill() {
        kill++;
        if (gauge < 100) {
            count++;
        }
        gauge++;
        broadcastInfo("hit", kill);
        if (gauge >= 100) {
            gauge = 100;
        }
        checkBuffs();
    }

    /**
     * 冷却操作，增加冷却计数和能量槽
     */
    public void cool() {
        cool++;
        int plus = coolAdd;
        if ((gauge + coolAdd) > 100) {
            plus -= ((gauge + coolAdd) - 100);
        }
        gauge += plus;
        count += plus;
        if (gauge >= 100) {
            gauge = 100;
        }
        broadcastInfo("cool", cool);
        checkBuffs();

    }

    /**
     * 失误操作，扣除能量槽
     */
    public void miss() {
        miss++;
        count -= missSub;
        gauge -= missSub;
        broadcastInfo("miss", miss);
    }

    /**
     * 启动阶段定时器，广播当前状态并启动能量槽
     *
     * @return 当前阶段的时间限制（秒）
     */
    public int timer() {
        int value;
        if (stage > 0) {
            value = 180;
        } else {
            value = 120;
        }

        timer = TimerManager.getInstance().schedule(() -> {
            stage++;
            // Should work :D
            warp(map + (stage * 100));
        }, SECONDS.toMillis(value));
        broadcastInfo("party", getParticipants().size() > 1 ? 1 : 0);
        broadcastInfo("hit", kill);
        broadcastInfo("miss", miss);
        broadcastInfo("cool", cool);
        broadcastInfo("skill", skill);
        broadcastInfo("laststage", stage);
        startGaugeSchedule();
        return value;
    }

    /**
     * 传送所有参与者到指定地图
     *
     * @param mapid 目标地图ID
     */
    public void warp(int mapid) {
        for (Character chr : getParticipants()) {
            chr.changeMap(mapid, 0);
        }
        if (stage > -1) {
            gaugeSchedule.cancel(false);
            gaugeSchedule = null;
            timer.cancel(false);
            timer = null;
        } else {
            stage = 0;
        }
    }

    /**
     * 向所有参与者广播指定信息和数值
     *
     * @param info   信息类型
     * @param amount 数值
     */
    public void broadcastInfo(String info, int amount) {
        for (Character chr : getParticipants()) {
            chr.sendPacket(PacketCreator.getEnergy("massacre_" + info, amount));
            chr.sendPacket(PacketCreator.pyramidGauge(count));
        }
    }

    /**
     * 使用技能，消耗一次技能使用次数
     *
     * @return true表示技能使用成功
     */
    public boolean useSkill() {
        if (skill < 1) {
            return false;
        }

        skill--;
        broadcastInfo("skill", skill);
        return true;
    }

    /**
     * 检查是否达到Buff触发阈值
     * 根据（击杀+冷却）总数分段给玩家施加祝福效果和技能
     */
    public void checkBuffs() {
        int total = (kill + cool);
        if (buffcount == 0 && total >= 250) {
            buffcount++;
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            for (Character chr : getParticipants()) {
                ii.getItemEffect(ItemId.PHARAOHS_BLESSING_1).applyTo(chr);
            }

        } else if (buffcount == 1 && total >= 500) {
            buffcount++;
            skill++;
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            for (Character chr : getParticipants()) {
                chr.sendPacket(PacketCreator.getEnergy("massacre_skill", skill));
                ii.getItemEffect(ItemId.PHARAOHS_BLESSING_2).applyTo(chr);
            }
        } else if (buffcount == 2 && total >= 1000) {
            buffcount++;
            skill++;
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            for (Character chr : getParticipants()) {
                chr.sendPacket(PacketCreator.getEnergy("massacre_skill", skill));
                ii.getItemEffect(ItemId.PHARAOHS_BLESSING_3).applyTo(chr);
            }
        } else if (buffcount == 3 && total >= 1500) {
            skill++;
            broadcastInfo("skill", skill);
        } else if (buffcount == 4 && total >= 2000) {
            buffcount++;
            skill++;
            ItemInformationProvider ii = ItemInformationProvider.getInstance();
            for (Character chr : getParticipants()) {
                chr.sendPacket(PacketCreator.getEnergy("massacre_skill", skill));
                ii.getItemEffect(ItemId.PHARAOHS_BLESSING_4).applyTo(chr);
            }
        } else if (buffcount == 5 && total >= 2500) {
            skill++;
            broadcastInfo("skill", skill);
        } else if (buffcount == 6 && total >= 3000) {
            skill++;
            broadcastInfo("skill", skill);
        }
    }

    /**
     * 根据击杀数和阶段计算得分并发放经验
     *
     * @param chr 玩家
     */
    public void sendScore(Character chr) {
        if (exp == 0) {
            int totalkills = (kill + cool);
            if (stage == 5) {
                if (totalkills >= 3000) {
                    rank = 0;
                } else if (totalkills >= 2000) {
                    rank = 1;
                } else if (totalkills >= 1500) {
                    rank = 2;
                } else if (totalkills >= 500) {
                    rank = 3;
                } else {
                    rank = 4;
                }
            } else {
                if (totalkills >= 2000) {
                    rank = 3;
                } else {
                    rank = 4;
                }
            }

            if (rank == 0) {
                exp = (60500 + (5500 * mode.getMode()));
            } else if (rank == 1) {
                exp = (55000 + (5000 * mode.getMode()));
            } else if (rank == 2) {
                exp = (46750 + (4250 * mode.getMode()));
            } else if (rank == 3) {
                exp = (22000 + (2000 * mode.getMode()));
            }

            exp += ((kill * 2) + (cool * 10));
        }
        chr.sendPacket(PacketCreator.pyramidScore(rank, exp));
        chr.gainExp(exp, true, true);
    }
}