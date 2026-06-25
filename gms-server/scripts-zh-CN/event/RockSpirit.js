/* 
 * This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc> 
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * @description: 石头精灵战斗脚本
 *               处理天空之城石头精灵战斗任务，玩家需要在限定时间内击败石头精灵
 * @author: OdinMS Team
 * @event: Rock Spirit Battle
 */

/** 入口地图 */
var entryMap;
/** 出口地图 */
var exitMap;
/** 其他战斗地图 */
var otherMap;

/** 最小地图ID范围 */
var minMapId = 103040410;
/** 最大地图ID范围 */
var maxMapId = 103040460;

/** 最小玩家数 */
var minPlayers = 1;
/** 战斗时间限制（分钟） */
var fightTime = 60;
/** 战斗计时器（毫秒） */
var timer = 1000 * 60 * fightTime;

/**
 * 初始化函数
 * 获取地图实例
 */
function init() {
    exitMap = em.getChannelServer().getMapFactory().getMap(103040400);
    entryMap = em.getChannelServer().getMapFactory().getMap(103040410);
    otherMap = em.getChannelServer().getMapFactory().getMap(103040420);
}

/**
 * 设置战斗实例
 * @param {number} level - 难度等级
 * @param {number} lobbyid - 场次ID
 * @returns {object} 事件实例管理器
 */
function setup(level, lobbyid) {
    var eim = em.newInstance("RockSpirit_" + lobbyid);
    eim.setProperty("level", level);
    eim.setProperty("boss", "0");

    respawn(eim);
    eim.startEventTimer(timer);
    return eim;
}

/**
 * 设置完成后的回调函数
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {}

/**
 * 重生怪物
 * @param {object} eim - 事件实例管理器
 */
function respawn(eim) {
    var map = eim.getMapInstance(entryMap.getId());
    var map2 = eim.getMapInstance(otherMap.getId());
    map.allowSummonState(true);
    map2.allowSummonState(true);
    map.instanceMapRespawn();
    map2.instanceMapRespawn();
    eim.schedule("respawn", 10000);
}

/**
 * 玩家进入战斗处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    var amplifierMap = eim.getMapInstance(entryMap.getId());
    player.changeMap(amplifierMap, 1);
    eim.schedule("timeOut", timer);
}

/**
 * 玩家复活处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @returns {boolean} 是否允许复活
 */
function playerRevive(eim, player) {
    player.respawn(eim, exitMap);
    return false;
}

/**
 * 玩家死亡处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDead(eim, player) {}

/**
 * 玩家断开连接处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDisconnected(eim, player) {
    if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
        eim.unregisterPlayer(player);
        end(eim);
    } else {
        eim.unregisterPlayer(player);
    }
}

/**
 * 玩家切换地图处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {number} mapid - 目标地图ID
 */
function changedMap(eim, player, mapid) {
    if (mapid < minMapId || mapid > maxMapId) {
        if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
            eim.unregisterPlayer(player);
            end(eim);
        } else {
            eim.unregisterPlayer(player);
        }
    }
}

/**
 * 获取怪物价值
 * @param {object} eim - 事件实例管理器
 * @param {number} mobId - 怪物ID
 * @returns {number} 怪物价值
 */
function monsterValue(eim, mobId) {
    return -1;
}

/**
 * 结束战斗事件
 * @param {object} eim - 事件实例管理器
 */
function end(eim) {
    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++) {
        playerExit(eim, party.get(i));
    }
    eim.dispose();
}

/**
 * 离开队伍处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function leftParty(eim, player) {}

/**
 * 解散队伍处理
 * @param {object} eim - 事件实例管理器
 */
function disbandParty(eim) {}

/**
 * 玩家注销处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerUnregistered(eim, player) {}

/**
 * 玩家退出战斗处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, exitMap.getPortal(0));
}

/**
 * 取消预定的事件/任务调度
 */
function cancelSchedule() {}

/**
 * 释放资源
 */
function dispose() {}

/**
 * 完成战斗处理
 * @param {object} eim - 事件实例管理器
 */
function clearPQ(eim) {}

/**
 * 怪物被击杀处理
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function monsterKilled(mob, eim) {}

/**
 * 所有怪物死亡处理
 * @param {object} eim - 事件实例管理器
 */
function allMonstersDead(eim) {}

/**
 * 战斗时间超时处理
 * @param {object} eim - 事件实例管理器
 */
function timeOut(eim) {
    end(eim);
}

// ---------- FILLER FUNCTIONS ----------

function scheduledTimeout(eim) {}

function changedLeader(eim, leader) {}