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

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation. You may not use, modify or distribute
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
 * @description: 天空之城与神木村飞行舱运输脚本
 *               处理天空之城(Orbis)与神木村(Leafre)之间的飞行舱运输
 * @author: OdinMS Team
 * @event: Cabin Transportation
 */

/** 天空之城候车室 */
var Orbis_btf;
/** 神木村候车室 */
var Leafre_btf;
/** 开往天空之城的飞行舱 */
var Cabin_to_Orbis;
/** 开往神木村的飞行舱 */
var Cabin_to_Leafre;
/** 天空之城停靠点 */
var Orbis_docked;
/** 神木村停靠点 */
var Leafre_docked;
/** 天空之城车站 */
var Orbis_Station;
/** 神木村车站 */
var Leafre_Station;

/** 入口关闭时间（毫秒） */
var closeTime = 4 * 60 * 1000;
/** 出发准备时间（毫秒） */
var beginTime = 5 * 60 * 1000;
/** 飞行时间（毫秒） */
var rideTime = 5 * 60 * 1000;

/**
 * 初始化函数
 * 设置交通工具时间配置并初始化地图对象
 */
function init() {
    closeTime = em.getTransportationTime(closeTime);
    beginTime = em.getTransportationTime(beginTime);
    rideTime = em.getTransportationTime(rideTime);

    Orbis_btf = em.getChannelServer().getMapFactory().getMap(200000132);
    Leafre_btf = em.getChannelServer().getMapFactory().getMap(240000111);
    Cabin_to_Orbis = em.getChannelServer().getMapFactory().getMap(200090210);
    Cabin_to_Leafre = em.getChannelServer().getMapFactory().getMap(200090200);
    Orbis_docked = em.getChannelServer().getMapFactory().getMap(200000131);
    Leafre_docked = em.getChannelServer().getMapFactory().getMap(240000110);
    Orbis_Station = em.getChannelServer().getMapFactory().getMap(200000100);
    Leafre_Station = em.getChannelServer().getMapFactory().getMap(240000100);

    scheduleNew();
}

/**
 * 安排新的运输周期
 * 设置停靠状态并调度入口关闭和出发任务
 */
function scheduleNew() {
    em.setProperty("docked", "true");
    Orbis_docked.setDocked(true);
    Leafre_docked.setDocked(true);

    em.setProperty("entry", "true");
    em.schedule("stopEntry", closeTime);
    em.schedule("takeoff", beginTime);
}

/**
 * 停止入口进入
 * 设置入口状态为关闭
 */
function stopEntry() {
    em.setProperty("entry", "false");
}

/**
 * 出发处理
 * 将候车室玩家传送到飞行舱，设置飞行状态
 */
function takeoff() {
    Orbis_btf.warpEveryone(Cabin_to_Leafre.getId());
    Leafre_btf.warpEveryone(Cabin_to_Orbis.getId());

    Orbis_docked.broadcastShip(false);
    Leafre_docked.broadcastShip(false);

    em.setProperty("docked", "false");
    Orbis_docked.setDocked(false);
    Leafre_docked.setDocked(false);

    em.schedule("arrived", rideTime);
}

/**
 * 到达处理
 * 将飞行舱内玩家传送到目的地车站，安排下一轮运输
 */
function arrived() {
    Cabin_to_Orbis.warpEveryone(Orbis_Station.getId(), 0);
    Cabin_to_Leafre.warpEveryone(Leafre_Station.getId(), 0);

    Orbis_docked.broadcastShip(true);
    Leafre_docked.broadcastShip(true);

    scheduleNew();
}

/**
 * 取消调度任务
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