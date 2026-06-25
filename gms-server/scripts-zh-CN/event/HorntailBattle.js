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
 * @description: 扎昆远征队战斗脚本
 *               处理扎昆(Horntail)远征副本的核心逻辑，包括头部击杀和主体战斗
 *               特色机制：需要先击杀两个头部才能攻击主体
 * @author: Ronan
 * @event: Horntail Battle
 */

/** 是否为组队任务 */
var isPq = true;
/** 最小/最大玩家数 */
var minPlayers = 6, maxPlayers = 30;
/** 最小/最大等级要求 */
var minLevel = 100, maxLevel = 255;
/** 入口地图 */
var entryMap = 240060000;
/** 出口地图 */
var exitMap = 240050600;
/** 招募地图 */
var recruitMap = 240050400;
/** 通关地图 */
var clearMap = 240050600;

/** 最小地图ID范围 */
var minMapId = 240060000;
/** 最大地图ID范围 */
var maxMapId = 240060200;

/** 事件时间限制（分钟） */
var eventTime = 120;

/** 最大同时进行的副本数 */
const maxLobbies = 1;

/** 游戏配置类引用 */
const GameConfig = Java.type('org.gms.config.GameConfig');
const DailyActiveManager = Java.type('org.gms.config.DailyActiveManager');
/** 根据配置动态调整最小玩家数 */
minPlayers = GameConfig.getServerBoolean("use_enable_solo_expeditions") ? 1 : minPlayers;
/** 根据配置动态调整等级限制 */
if(GameConfig.getServerBoolean("use_enable_party_level_limit_lift")) {
    minLevel = 1 , maxLevel = 999;
}

/**
 * 初始化事件要求
 */
function init() {
    setEventRequirements();
}

/**
 * 获取最大副本数
 * @returns {number} 最大同时进行的副本数
 */
function getMaxLobbies() {
    return maxLobbies;
}

/**
 * 设置事件要求信息
 * 将组队人数、等级要求、时间限制等信息保存到属性中供玩家查看
 */
function setEventRequirements() {
    var reqStr = "";

    reqStr += "\r\n   组队人数: ";
    if (maxPlayers - minPlayers >= 1) {
        reqStr += minPlayers + " ~ " + maxPlayers;
    } else {
        reqStr += minPlayers;
    }

    reqStr += "\r\n   等级要求: ";
    if (maxLevel - minLevel >= 1) {
        reqStr += minLevel + " ~ " + maxLevel;
    } else {
        reqStr += minLevel;
    }

    reqStr += "\r\n   时间限制: ";
    reqStr += eventTime + " 分钟";

    em.setProperty("party", reqStr);
}

/**
 * 设置事件专属物品
 * @param {object} eim - 事件实例管理器
 */
function setEventExclusives(eim) {
    var itemSet = [];
    eim.setExclusiveItems(itemSet);
}

/**
 * 设置事件奖励
 * @param {object} eim - 事件实例管理器
 */
function setEventRewards(eim) {
    var itemSet, itemQty, evLevel, expStages, mesoStages;

    evLevel = 1;
    itemSet = [];
    itemQty = [];
    eim.setEventRewards(evLevel, itemSet, itemQty);

    expStages = [];
    eim.setEventClearStageExp(expStages);

    mesoStages = [];
    eim.setEventClearStageMeso(mesoStages);
}

/**
 * 设置完成后的回调
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {}

/**
 * 设置副本实例
 * @param {number} channel - 频道号
 * @returns {object} 事件实例管理器
 */
function setup(channel) {
    var eim = em.newInstance("Horntail" + channel);
    eim.setProperty("canJoin", 1);
    eim.setProperty("defeatedBoss", 0);
    eim.setProperty("defeatedHead", 0);

    var level = 1;
    eim.getInstanceMap(240060000).resetPQ(level);
    eim.getInstanceMap(240060100).resetPQ(level);
    eim.getInstanceMap(240060200).resetPQ(level);

    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    const Point = Java.type('java.awt.Point');
    var map, mob;
    map = eim.getInstanceMap(240060000);
    mob = LifeFactory.getMonster(8810000);
    map.spawnMonsterOnGroundBelow(mob, new Point(960, 120));

    map = eim.getInstanceMap(240060100);
    mob = LifeFactory.getMonster(8810001);
    map.spawnMonsterOnGroundBelow(mob, new Point(-420, 120));

    eim.startEventTimer(eventTime * 60000);
    setEventRewards(eim);
    setEventExclusives(eim);

    return eim;
}

/**
 * 玩家进入事件处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    eim.dropMessage(5, "[远征队] " + player.getName() + " 已进入地图。");
    var map = eim.getMapInstance(entryMap);
    player.changeMap(map, map.getPortal(0));
}

/**
 * 定时超时处理
 * @param {object} eim - 事件实例管理器
 */
function scheduledTimeout(eim) {
    end(eim);
}

/**
 * 玩家切换地图处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {number} mapid - 目标地图ID
 */
function changedMap(eim, player, mapid) {
    if (mapid < minMapId || mapid > maxMapId) {
        partyPlayersCheck(eim, player);
    }
}

/**
 * 队长变更处理
 * @param {object} eim - 事件实例管理器
 * @param {object} leader - 新队长对象
 */
function changedLeader(eim, leader) {}

/**
 * 玩家死亡处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDead(eim, player) {}

/**
 * 玩家复活处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @returns {boolean} 是否允许复活
 */
function playerRevive(eim, player) {
    partyPlayersCheck(eim, player);
}

/**
 * 玩家断开连接处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDisconnected(eim, player) {
    partyPlayersCheck(eim, player);
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
 * 获取怪物价值
 * @param {object} eim - 事件实例管理器
 * @param {number} mobId - 怪物ID
 * @returns {number} 怪物价值
 */
function monsterValue(eim, mobId) {
    return 1;
}

/**
 * 玩家注销处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerUnregistered(eim, player) {}

/**
 * 玩家退出处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 0);
}

/**
 * 结束事件
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
 * 给予随机事件奖励
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function giveRandomEventReward(eim, player) {
    eim.giveEventReward(player);
}

/**
 * 完成副本处理
 * @param {object} eim - 事件实例管理器
 */
function clearPQ(eim) {
    eim.stopEventTimer();
    eim.setEventCleared();
}

/**
 * 判断是否为扎昆头部
 * @param {object} mob - 怪物对象
 * @returns {boolean} 是否为扎昆头部
 */
function isHorntailHead(mob) {
    var mobid = mob.getId();
    return (mobid == 8810000 || mobid == 8810001);
}

/**
 * 判断是否为扎昆主体
 * @param {object} mob - 怪物对象
 * @returns {boolean} 是否为扎昆主体
 */
function isHorntail(mob) {
    var mobid = mob.getId();
    return (mobid == 8810018);
}

/**
 * 怪物被击杀处理
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function monsterKilled(mob, eim) {
    if (isHorntail(mob)) {
        eim.setIntProperty("defeatedBoss", 1);
        eim.showClearEffect(mob.getMap().getId());
        eim.clearPQ();

        eim.dispatchRaiseQuestMobCount(8810018, 240060200);
        mob.getMap().broadcastHorntailVictory();

        // 每日活跃-通关副本：黑龙(扎昆主体)通关时，给在场全部远征队成员各记1次进度
        addPqClearProgress(eim);
    } else if (isHorntailHead(mob)) {
        var killed = eim.getIntProperty("defeatedHead");
        eim.setIntProperty("defeatedHead", killed + 1);
        eim.showClearEffect(mob.getMap().getId());
    }
}

/** 每日活跃-通关副本：给当前远征队全部在场成员累计1次pq_clear进度 */
function addPqClearProgress(eim) {
    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++) {
        DailyActiveManager.addProgress(party.get(i).getId(), "pq_clear", 1);
    }
}

/**
 * 所有怪物死亡处理
 * @param {object} eim - 事件实例管理器
 */
function allMonstersDead(eim) {}

/**
 * 取消调度
 */
function cancelSchedule() {}

/**
 * 释放资源
 * @param {object} eim - 事件实例管理器
 */
function dispose(eim) {}

/**
 * 检测队伍人数是否满足最低人数要求
 * @param {object} eim - 远征副本实例管理器
 * @param {object} player - 触发事件的玩家对象
 * @returns {boolean} 是否满足要求
 */
function partyPlayersCheck(eim, player) {
    if (eim.isExpeditionTeamLackingNow(true, minPlayers, player)) {
        eim.unregisterPlayer(player);
        eim.dropMessage(5, "[远征队] 队长已退出远征或者队伍人数不足最低要求，无法继续。");
        end(eim);
        return false;
    } else {
        eim.dropMessage(5, "[远征队] " + player.getName() + " 已离开副本。");
        eim.unregisterPlayer(player);
        return true;
    }
}