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
 * @description: 电梯运输系统脚本
 *               处理玩具城电梯上下运行逻辑
 */

/** 时间设置（单位：毫秒） */
/** 电梯出发前的准备时间 (1分钟) */
var beginTime = 60 * 1000;
/** 电梯运行到目的地所需时间 (1分钟) */
var rideTime = 60 * 1000;

/**
 * 初始化函数
 * 修正交通工具旅行时间并重置电梯区域的反应堆状态
 */
function init() {
    beginTime = em.getTransportationTime(beginTime);
    rideTime = em.getTransportationTime(rideTime);

    em.getChannelServer().getMapFactory().getMap(222020100).resetReactors();
    em.getChannelServer().getMapFactory().getMap(222020200).resetReactors();

    scheduleNew();
}

/**
 * 设置新的电梯运行周期
 * 重置电梯状态并安排上行任务
 */
function scheduleNew() {
    em.setProperty("goingUp", "false");
    em.setProperty("goingDown", "true");

    em.getChannelServer().getMapFactory().getMap(222020100).resetReactors();
    em.getChannelServer().getMapFactory().getMap(222020200).setReactorState();
    em.schedule("goingUpNow", beginTime);
}

/**
 * 安排电梯上行任务
 */
function goUp() {
    em.schedule("goingUpNow", beginTime);
}

/**
 * 安排电梯下行任务
 */
function goDown() {
    em.schedule("goingDownNow", beginTime);
}

/**
 * 电梯上行处理
 * 将底层电梯内的玩家传送到电梯运行地图，并设置上行状态
 */
function goingUpNow() {
    em.getChannelServer().getMapFactory().getMap(222020110).warpEveryone(222020111);
    em.setProperty("goingUp", "true");
    em.schedule("isUpNow", rideTime);

    em.getChannelServer().getMapFactory().getMap(222020100).setReactorState();
}

/**
 * 电梯下行处理
 * 将顶层电梯内的玩家传送到电梯运行地图，并设置下行状态
 */
function goingDownNow() {
    em.getChannelServer().getMapFactory().getMap(222020210).warpEveryone(222020211);
    em.setProperty("goingDown", "true");
    em.schedule("isDownNow", rideTime);

    em.getChannelServer().getMapFactory().getMap(222020200).setReactorState();
}

/**
 * 电梯到达顶层处理
 * 将电梯内的玩家传送到顶层区域，并安排下行任务
 */
function isUpNow() {
    em.setProperty("goingDown", "false");
    em.getChannelServer().getMapFactory().getMap(222020200).resetReactors();
    em.getChannelServer().getMapFactory().getMap(222020111).warpEveryone(222020200, 0);

    goDown();
}

/**
 * 电梯到达底层处理
 * 将电梯内的玩家传到底层区域，并安排上行任务
 */
function isDownNow() {
    em.setProperty("goingUp", "false");
    em.getChannelServer().getMapFactory().getMap(222020100).resetReactors();
    em.getChannelServer().getMapFactory().getMap(222020211).warpEveryone(222020100, 4);

    goUp();
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