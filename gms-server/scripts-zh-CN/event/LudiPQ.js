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
 * @author: Ronan
 * @event: Ludibrium PQ
 * @description: 玩具城组队任务脚本
 *               玩家组队完成一系列关卡挑战，获取丰富奖励
 */

/** 是否为组队任务 */
var isPq = true;
/** 最小组队人数 */
var minPlayers = 5;
/** 最大组队人数 */
var maxPlayers = 6;
/** 最低等级要求 */
var minLevel = 35;
/** 最高等级要求 */
var maxLevel = 50;
/** 任务入口地图ID */
var entryMap = 922010100;
/** 任务出口地图ID */
var exitMap = 922010000;
/** 组队招募地图ID */
var recruitMap = 221024500;
/** 任务完成地图ID */
var clearMap = 922011000;

/** 任务地图ID范围 - 最小值 */
var minMapId = 922010100;
/** 任务地图ID范围 - 最大值 */
var maxMapId = 922011100;

/** 任务时限（分钟） */
var eventTime = 45;

/** 最大同时进行任务的场次数量 */
const maxLobbies = 1;

/** 游戏配置类引用 */
const GameConfig = Java.type('org.gms.config.GameConfig');

/**
 * 根据服务器配置动态调整组队要求
 * 如果解除远征队人数限制，则最低人数改为1人
 * 如果解除远征队等级限制，则最低1级，最高999级
 */
minPlayers = GameConfig.getServerBoolean("use_enable_solo_expeditions") ? 1 : minPlayers;
if (GameConfig.getServerBoolean("use_enable_party_level_limit_lift")) {
    minLevel = 1;
    maxLevel = 999;
}

/**
 * 初始化事件
 * 设置事件要求信息
 */
function init() {
    setEventRequirements();
}

/**
 * 获取最大任务场次数量
 * 
 * @returns {number} 最大场次数量
 */
function getMaxLobbies() {
    return maxLobbies;
}

/**
 * 设置事件要求信息
 * 将组队人数、等级要求、时间限制等信息设置到属性中
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
 * 
 * @param {object} eim - 事件实例管理器
 */
function setEventExclusives(eim) {
    var itemSet = [4001022, 4001023];
    eim.setExclusiveItems(itemSet);
}

/**
 * 设置事件奖励
 * 
 * @param {object} eim - 事件实例管理器
 */
function setEventRewards(eim) {
    var itemSet, itemQty, evLevel, expStages;

    evLevel = 1;
    itemSet = [2040602, 2040802, 2040002, 2040402, 2040505, 2040502, 2040601, 2044501, 2044701, 2044601, 2041019, 2041016, 2041022, 2041013, 2041007, 2043301, 2040301, 2040801, 2040001, 2040004, 2040504, 2040501, 2040513, 2043101, 2044201, 2044401, 2040701, 2044301, 2043801, 2040401, 2043701, 2040803, 2000003, 2000002, 2000004, 2000006, 2000005, 2022000, 2001001, 2001002, 2022003, 2001000, 2020014, 2020015, 4003000, 1102003, 1102004, 1102000, 1102002, 1102001, 1102011, 1102012, 1102013, 1102014, 1032011, 1032012, 1032013, 1032002, 1032008, 1032011, 2070011, 4010003, 4010000, 4010006, 4010002, 4010005, 4010004, 4010001, 4020001, 4020002, 4020008, 4020007, 4020003, 4020000, 4020004, 4020005, 4020006];
    itemQty = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 85, 85, 10, 60, 2, 20, 15, 15, 20, 15, 10, 5, 35, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 10, 10, 6, 10, 10, 10, 10, 10, 10, 4, 4, 10, 10, 10, 10, 10];
    eim.setEventRewards(evLevel, itemSet, itemQty);

    expStages = [210, 2520, 2940, 3360, 3770, 0, 4620, 5040, 5950];
    eim.setEventClearStageExp(expStages);
}

/**
 * 从给定队伍中选择符合条件的队员
 * 
 * @param {object} party - 队伍对象
 * @returns {Array} 符合条件的队员数组
 */
function getEligibleParty(party) {
    var eligible = [];
    var hasLeader = false;

    if (party.size() > 0) {
        var partyList = party.toArray();

        for (var i = 0; i < party.size(); i++) {
            var ch = partyList[i];

            if (ch.getMapId() == recruitMap && ch.getLevel() >= minLevel && ch.getLevel() <= maxLevel) {
                if (ch.isLeader()) {
                    hasLeader = true;
                }
                eligible.push(ch);
            }
        }
    }

    if (!(hasLeader && eligible.length >= minPlayers && eligible.length <= maxPlayers)) {
        eligible = [];
    }
    return Java.to(eligible, Java.type('org.gms.net.server.world.PartyCharacter[]'));
}

/**
 * 设置任务实例
 * 
 * @param {number} level - 玩家等级
 * @param {number} lobbyid - 场次ID
 * @returns {object} 事件实例管理器
 */
function setup(level, lobbyid) {
    var eim = em.newInstance("Ludi" + lobbyid);
    eim.setProperty("level", level);

    eim.setProperty("statusStg1", -1);
    eim.setProperty("statusStg2", -1);
    eim.setProperty("statusStg3", -1);
    eim.setProperty("statusStg4", -1);
    eim.setProperty("statusStg5", -1);
    eim.setProperty("statusStg6", -1);
    eim.setProperty("statusStg7", -1);
    eim.setProperty("statusStg8", -1);
    eim.setProperty("statusStg9", -1);

    eim.getInstanceMap(922010100).resetPQ(level);
    eim.getInstanceMap(922010200).resetPQ(level);
    eim.getInstanceMap(922010201).resetPQ(level);
    eim.getInstanceMap(922010300).resetPQ(level);
    eim.getInstanceMap(922010400).resetPQ(level);
    eim.getInstanceMap(922010401).resetPQ(level);
    eim.getInstanceMap(922010402).resetPQ(level);
    eim.getInstanceMap(922010403).resetPQ(level);
    eim.getInstanceMap(922010404).resetPQ(level);
    eim.getInstanceMap(922010405).resetPQ(level);
    eim.getInstanceMap(922010500).resetPQ(level);
    eim.getInstanceMap(922010500).resetPQ(level);
    eim.getInstanceMap(922010501).resetPQ(level);
    eim.getInstanceMap(922010502).resetPQ(level);
    eim.getInstanceMap(922010503).resetPQ(level);
    eim.getInstanceMap(922010504).resetPQ(level);
    eim.getInstanceMap(922010505).resetPQ(level);
    eim.getInstanceMap(922010506).resetPQ(level);
    eim.getInstanceMap(922010600).resetPQ(level);
    eim.getInstanceMap(922010700).resetPQ(level);
    eim.getInstanceMap(922010800).resetPQ(level);
    eim.getInstanceMap(922010900).resetPQ(level);
    eim.getInstanceMap(922011000).resetPQ(level);
    eim.getInstanceMap(922011100).resetPQ(level);

    respawnStages(eim);
    eim.startEventTimer(eventTime * 60000);
    setEventRewards(eim);
    setEventExclusives(eim);
    return eim;
}

/**
 * 任务设置完成后的回调函数
 * 
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {
    eim.dropAllExclusiveItems();
}

/**
 * 重新生成任务阶段
 * 
 * @param {object} eim - 事件实例管理器
 */
function respawnStages(eim) {}

/**
 * 玩家进入任务处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    var map = eim.getMapInstance(entryMap);
    player.changeMap(map, map.getPortal(0));
}

/**
 * 任务超时处理
 * 
 * @param {object} eim - 事件实例管理器
 */
function scheduledTimeout(eim) {
    if (eim.getProperty("9stageclear") != null) {
        var curStage = 922011000, toStage = 922011100;
        eim.warpEventTeam(curStage, toStage);
    } else {
        end(eim);
    }
}

/**
 * 玩家取消注册处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerUnregistered(eim, player) {}

/**
 * 玩家退出任务处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 0);
}

/**
 * 玩家离开队伍处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerLeft(eim, player) {
    if (!eim.isEventCleared()) {
        playerExit(eim, player);
    }
}

/**
 * 玩家切换地图处理
 * 
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
 * 队长变更处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} leader - 新队长
 */
function changedLeader(eim, leader) {
    var mapid = leader.getMapId();
    if (!eim.isEventCleared() && (mapid < minMapId || mapid > maxMapId)) {
        end(eim);
    }
}

/**
 * 玩家死亡处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDead(eim, player) {}

/**
 * 玩家复活处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerRevive(eim, player) {
    if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
        eim.unregisterPlayer(player);
        end(eim);
    } else {
        eim.unregisterPlayer(player);
    }
}

/**
 * 玩家断开连接处理
 * 
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
 * 玩家离开队伍处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function leftParty(eim, player) {
    if (eim.isEventTeamLackingNow(false, minPlayers, player)) {
        end(eim);
    } else {
        playerLeft(eim, player);
    }
}

/**
 * 队伍解散处理
 * 
 * @param {object} eim - 事件实例管理器
 */
function disbandParty(eim) {
    if (!eim.isEventCleared()) {
        end(eim);
    }
}

/**
 * 获取怪物价值
 * 
 * @param {object} eim - 事件实例管理器
 * @param {number} mobId - 怪物ID
 * @returns {number} 怪物价值
 */
function monsterValue(eim, mobId) {
    return 1;
}

/**
 * 结束任务处理
 * 
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
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function giveRandomEventReward(eim, player) {
    eim.giveEventReward(player);
}

/**
 * 完成任务处理
 * 
 * @param {object} eim - 事件实例管理器
 */
function clearPQ(eim) {
    eim.stopEventTimer();
    eim.setEventCleared();

    eim.startEventTimer(1 * 60000);
    eim.warpEventTeam(922011000);
}

/**
 * 怪物被击杀处理
 * 
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function monsterKilled(mob, eim) {}

/**
 * 所有怪物被击杀处理
 * 
 * @param {object} eim - 事件实例管理器
 */
function allMonstersDead(eim) {}

/**
 * 取消计划任务
 */
function cancelSchedule() {}

/**
 * 释放资源
 * 
 * @param {object} eim - 事件实例管理器
 */
function dispose(eim) {}