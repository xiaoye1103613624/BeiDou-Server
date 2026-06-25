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
 * @description: 废弃都市组队任务脚本
 *               处理21-30级玩家的组队任务，特色机制为通过多个关卡收集物品
 *               玩家需要在限时内完成多个阶段的挑战，收集特定物品推进任务
 * @author: Ronan
 * @event: Kerning PQ
 */

/** 是否为组队任务 */
var isPq = true;
/** 最小/最大玩家数 */
var minPlayers = 3, maxPlayers = 6;
/** 最小/最大等级要求 */
var minLevel = 21, maxLevel = 30;
/** 进入地图ID */
var entryMap = 103000800;
/** 退出地图ID */
var exitMap = 103000890;
/** 招募地图ID */
var recruitMap = 103000000;
/** 通关地图ID */
var clearMap = 103000805;

/** 最小地图ID范围 */
var minMapId = 103000800;
/** 最大地图ID范围 */
var maxMapId = 103000805;

/** 事件时间限制（分钟） */
var eventTime = 30;

/** 最大等待室数量 */
const maxLobbies = 1;

const GameConfig = Java.type('org.gms.config.GameConfig');
minPlayers = GameConfig.getServerBoolean("use_enable_solo_expeditions") ? 1 : minPlayers;
if(GameConfig.getServerBoolean("use_enable_party_level_limit_lift")) {
    minLevel = 1 , maxLevel = 999;
}

/**
 * 初始化事件
 */
function init() {
    setEventRequirements();
}

/**
 * 获取最大等待室数量
 * @returns {number} 最大等待室数量
 */
function getMaxLobbies() {
    return maxLobbies;
}

/**
 * 设置事件要求描述
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
    var itemSet = [4001007, 4001008];
    eim.setExclusiveItems(itemSet);
}

/**
 * 设置事件奖励
 * @param {object} eim - 事件实例管理器
 */
function setEventRewards(eim) {
    var itemSet, itemQty, evLevel, expStages;

    evLevel = 1;
    itemSet = [2040505, 2040514, 2040502, 2040002, 2040602, 2040402, 2040802, 1032009, 1032004, 1032005, 1032006, 1032007, 1032010, 1032002, 1002026, 1002089, 1002090, 2000003, 2000001, 2000002, 2000006, 2022003, 2022000, 2000004, 4003000, 4010000, 4010001, 4010002, 4010003, 4010004, 4010005, 4010006, 4010007, 4020000, 4020001, 4020002, 4020003, 4020004, 4020005, 4020006, 4020007, 4020008];
    itemQty = [1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 80, 80, 80, 50, 5, 15, 15, 30, 15, 15, 15, 15, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 3, 3];
    eim.setEventRewards(evLevel, itemSet, itemQty);

    expStages = [100, 200, 400, 800, 1500];
    eim.setEventClearStageExp(expStages);
}

/**
 * 从给定队伍中选择符合条件的队员
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
 * 设置事件实例
 * @param {number} level - 难度等级
 * @param {number} lobbyid - 等待室ID
 * @returns {object} 事件实例管理器
 */
function setup(level, lobbyid) {
    var eim = em.newInstance("Kerning" + lobbyid);
    eim.setProperty("level", level);

    respawnStages(eim);
    eim.startEventTimer(eventTime * 60000);
    setEventRewards(eim);
    setEventExclusives(eim);
    return eim;
}

/**
 * 设置完成后的回调
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {
    eim.dropAllExclusiveItems();
}

/**
 * 重生阶段怪物
 * @param {object} eim - 事件实例管理器
 */
function respawnStages(eim) {
    eim.getMapInstance(103000800).instanceMapRespawn();
    eim.getMapInstance(103000805).instanceMapRespawn();
    eim.schedule("respawnStages", 15 * 1000);
}

/**
 * 玩家进入事件
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    var map = eim.getMapInstance(entryMap);
    player.changeMap(map, map.getPortal(0));
}

/**
 * 事件超时处理
 * @param {object} eim - 事件实例管理器
 */
function scheduledTimeout(eim) {
    end(eim);
}

/**
 * 玩家注销处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerUnregistered(eim, player) {}

/**
 * 玩家退出事件
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 0);
}

/**
 * 玩家离开事件
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
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {number} mapid - 地图ID
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
 * @param {object} eim - 事件实例管理器
 * @param {object} leader - 队长对象
 */
function changedLeader(eim, leader) {
    var mapid = leader.getMapId();
    if (!eim.isEventCleared() && (mapid < minMapId || mapid > maxMapId)) {
        end(eim);
    }
}

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
 * @param {object} eim - 事件实例管理器
 */
function disbandParty(eim) {
    if (!eim.isEventCleared()) {
        end(eim);
    }
}

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
 * 通关组队任务
 * @param {object} eim - 事件实例管理器
 */
function clearPQ(eim) {
    eim.stopEventTimer();
    eim.setEventCleared();
}

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
 * 取消调度任务
 */
function cancelSchedule() {}

/**
 * 释放资源
 * @param {object} eim - 事件实例管理器
 */
function dispose(eim) {}