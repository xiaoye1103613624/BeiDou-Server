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

/**
 * @description: 精灵运输系统脚本
 *               处理天空之城(Orbis)与阿里安特(Ariant)之间的精灵传送
 * @author: OdinMS Team
 * @event: Genie Transportation
 */

/** 天空之城候车室 */
var Orbis_btf;
/** 开往天空之城的精灵 */
var Genie_to_Orbis;
/** 天空之城停靠点 */
var Orbis_docked;
/** 阿里安特候车室 */
var Ariant_btf;
/** 开往阿里安特的精灵 */
var Genie_to_Ariant;
/** 阿里安特停靠点 */
var Ariant_docked;
/** 天空之城售票处 */
var Orbis_Station;

/** 时间设置（单位：毫秒） */
/** 关闭入口的时间 (4分钟) */
var closeTime = 4 * 60 * 1000;
/** 出发前的准备时间 (5分钟) */
var beginTime = 5 * 60 * 1000;
/** 到达目的地所需的时间 (5分钟) */
var rideTime = 5 * 60 * 1000;

/**
 * 初始化函数
 * 修正交通工具旅行时间并获取地图实例
 */
function init() {
    closeTime = em.getTransportationTime(closeTime);
    beginTime = em.getTransportationTime(beginTime);
    rideTime = em.getTransportationTime(rideTime);

    Orbis_btf = em.getChannelServer().getMapFactory().getMap(200000152);
    Ariant_btf = em.getChannelServer().getMapFactory().getMap(260000110);
    Genie_to_Orbis = em.getChannelServer().getMapFactory().getMap(200090410);
    Genie_to_Ariant = em.getChannelServer().getMapFactory().getMap(200090400);
    Orbis_docked = em.getChannelServer().getMapFactory().getMap(200000151);
    Ariant_docked = em.getChannelServer().getMapFactory().getMap(260000100);
    Orbis_Station = em.getChannelServer().getMapFactory().getMap(200000100);

    scheduleNew();
}

/**
 * 设置新的精灵运行周期
 * 重置停靠状态并安排关闭入口和出发任务
 */
function scheduleNew() {
    em.setProperty("docked", "true");
    Orbis_docked.setDocked(true);
    Ariant_docked.setDocked(true);

    em.setProperty("entry", "true");
    em.schedule("stopEntry", closeTime);
    em.schedule("takeoff", beginTime);
}

/**
 * 关闭精灵入口
 */
function stopEntry() {
    em.setProperty("entry", "false");
}

/**
 * 精灵出发处理
 * 将候车室的玩家传送到精灵上，并安排到达任务
 */
function takeoff() {
    Orbis_btf.warpEveryone(Genie_to_Ariant.getId());
    Ariant_btf.warpEveryone(Genie_to_Orbis.getId());
    Orbis_docked.broadcastShip(false);
    Ariant_docked.broadcastShip(false);

    em.setProperty("docked", "false");
    Orbis_docked.setDocked(false);
    Ariant_docked.setDocked(false);

    em.schedule("arrived", rideTime);
}

/**
 * 精灵到达目的地处理
 * 将精灵上的玩家传送到对应站点
 */
function arrived() {
    Genie_to_Orbis.warpEveryone(Orbis_Station.getId(), 0);
    Genie_to_Ariant.warpEveryone(Ariant_docked.getId(), 1);
    Orbis_docked.broadcastShip(true);
    Ariant_docked.broadcastShip(true);

    scheduleNew();
}

/**
 * 取消预定的事件/任务调度
 */
function cancelSchedule() {}

// ---------- FILLER FUNCTIONS ----------

function dispose() {}

function setup(eim, leaderid) {}

function monsterValue(eim, mobid) {return 0;}

function disbandParty(eim, player) {}

function playerDisconnected(eim, player) {}

function playerEntry(eim, player) {}

function monsterKilled(mob, eim) {}

function scheduledTimeout(eim) {}

function afterSetup(eim) {}

function changedLeader(eim, leader) {}

function playerExit(eim, player) {}

function leftParty(eim, player) {}

function clearPQ(eim) {}

function allMonstersDead(eim) {}

function playerUnregistered(eim, player) {}