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

/**
 * @description: 阿兰二转坐骑任务脚本
 *               处理阿兰(Aran)职业二转坐骑任务 - 斯卡杜尔的坐骑试炼
 * @author: Ronan
 * @event: Aran 2nd Mount Quest
 */

/** 任务入口地图ID */
var entryMap = 921110000;
/** 任务出口地图ID */
var exitMap = 211050000;

/** 任务地图ID范围 - 最小值 */
var minMapId = 921110000;
/** 任务地图ID范围 - 最大值 */
var maxMapId = 921110000;

/** 任务时限（分钟） */
var eventTime = 3;

/** 最大同时进行任务的场次数量 */
const maxLobbies = 7;

/**
 * 获取最大任务场次数量
 * @returns {number} 最大场次数量
 */
function getMaxLobbies() {
    return maxLobbies;
}

/**
 * 初始化事件
 * 设置允许玩家进入任务状态
 */
function init() {
    em.setProperty("noEntry", "false");
}

/**
 * 设置任务实例
 * @param {number} level - 玩家等级
 * @param {number} lobbyid - 场次ID
 * @returns {object} 事件实例管理器
 */
function setup(level, lobbyid) {
    var eim = em.newInstance("Aran_2ndmount_" + lobbyid);
    eim.setProperty("level", level);
    eim.setProperty("boss", "0");

    return eim;
}

/**
 * 重新生成任务阶段
 * @param {object} eim - 事件实例管理器
 */
function respawnStages(eim) {}

/**
 * 玩家进入任务处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    var mapObj = eim.getInstanceMap(entryMap);

    mapObj.resetPQ(1);
    mapObj.instanceMapForceRespawn();
    respawnStages(eim);

    player.changeMap(entryMap, 2);
    em.setProperty("noEntry", "true");

    const PacketCreator = Java.type('org.gms.util.PacketCreator');
    player.sendPacket(PacketCreator.getClock(eventTime * 60));
    eim.startEventTimer(eventTime * 60000);
}

/**
 * 玩家取消注册处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerUnregistered(eim, player) {}

/**
 * 玩家退出任务处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    eim.dispose();
    em.setProperty("noEntry", "false");
}

/**
 * 任务超时处理
 * @param {object} eim - 事件实例管理器
 */
function scheduledTimeout(eim) {
    var player = eim.getPlayers().get(0);
    playerExit(eim, player);
    player.changeMap(exitMap);
}

/**
 * 玩家断开连接处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDisconnected(eim, player) {
    playerExit(eim, player);
}

/**
 * 玩家切换地图处理
 * @param {object} eim - 事件实例管理器
 * @param {object} chr - 角色对象
 * @param {number} mapid - 目标地图ID
 */
function changedMap(eim, chr, mapid) {
    if (mapid < minMapId || mapid > maxMapId) {
        playerExit(eim, chr);
    }
}

/**
 * 完成任务处理
 * @param {object} eim - 事件实例管理器
 */
function clearPQ(eim) {
    eim.stopEventTimer();
    eim.setEventCleared();

    var player = eim.getPlayers().get(0);
    eim.unregisterPlayer(player);
    player.changeMap(exitMap);

    eim.dispose();
    em.setProperty("noEntry", "false");
}

/**
 * 怪物被击杀处理
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function monsterKilled(mob, eim) {}

/**
 * 获取怪物价值
 * @param {object} eim - 事件实例管理器
 * @param {number} mobId - 怪物ID
 * @returns {number} 怪物价值
 */
function monsterValue(eim, mobId) {
    return 1;
}

/**
 * 友方NPC被击杀处理
 * 当友方NPC被击杀时结束任务
 * @param {object} mob - 怪物对象（此处指友方NPC）
 * @param {object} eim - 事件实例管理器
 */
function friendlyKilled(mob, eim) {
    if (em.getProperty("noEntry") != "false") {
        var player = eim.getPlayers().get(0);
        playerExit(eim, player);
        player.changeMap(exitMap);
    }
}

/**
 * 所有怪物被击杀处理
 * @param {object} eim - 事件实例管理器
 */
function allMonstersDead(eim) {}

/**
 * 取消计划任务
 */
function cancelSchedule() {}

/**
 * 释放资源
 */
function dispose() {}


// ---------- FILLER FUNCTIONS ----------

function disbandParty(eim, player) {}

function afterSetup(eim) {}

function changedLeader(eim, leader) {}

function leftParty(eim, player) {}