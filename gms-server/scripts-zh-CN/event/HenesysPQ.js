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
 * @description: 射手村组队任务脚本
 *               处理10级以上玩家的组队任务，特色机制为保护玉兔月妙制作年糕
 *               玩家需要保护玉兔不被怪物攻击，玉兔成功捣出年糕后完成任务
 * @author: Ronan
 * @event: Henesys PQ
 */

/** 是否为组队任务 */
var isPq = true;
/** 最小/最大玩家数 */
var minPlayers = 3, maxPlayers = 6;
/** 最小/最大等级要求 */
var minLevel = 10, maxLevel = 255;
/** 进入地图ID */
var entryMap = 910010000;
/** 退出地图ID */
var exitMap = 910010300;
/** 招募地图ID */
var recruitMap = 100000200;
/** 通关地图ID */
var clearMap = 910010100;

/** 最小地图ID范围 */
var minMapId = 910010000;
/** 最大地图ID范围 */
var maxMapId = 910010400;

/** 事件时间限制（分钟） */
var eventTime = 10;

/** 最大等待室数量 */
const maxLobbies = 1;

const GameConfig = Java.type('org.gms.config.GameConfig');
minPlayers = GameConfig.getServerBoolean("use_enable_solo_expeditions") ? 1 : minPlayers;
if(GameConfig.getServerBoolean("use_enable_party_level_limit_lift")) {
    minLevel = 1 , maxLevel = 999;
}

const PacketCreator = Java.type('org.gms.util.PacketCreator');

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
    var itemSet = [4001095, 4001096, 4001097, 4001098, 4001099, 4001100];
    eim.setExclusiveItems(itemSet);
}

/**
 * 设置事件奖励
 * @param {object} eim - 事件实例管理器
 */
function setEventRewards(eim) {
    var itemSet, itemQty, evLevel, expStages;

    expStages = [1600];
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
    var eim = em.newInstance("Henesys" + lobbyid);
    eim.setProperty("level", level);
    eim.setProperty("stage", "0");
    eim.setProperty("bunnyCake", "0");
    eim.setProperty("bunnyDamaged", "0");

    eim.getInstanceMap(910010000).resetPQ(level);
    eim.getInstanceMap(910010000).allowSummonState(false);

    eim.getInstanceMap(910010200).resetPQ(level);

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
function afterSetup(eim) {}

/**
 * 重生阶段怪物
 * @param {object} eim - 事件实例管理器
 */
function respawnStages(eim) {
    eim.getInstanceMap(910010000).instanceMapRespawn();
    eim.getInstanceMap(910010200).instanceMapRespawn();

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
    if (eim.getProperty("1stageclear") != null) {
        var curStage = 910010200, toStage = 910010400;
        eim.warpEventTeam(curStage, toStage);
    } else {
        end(eim);
    }
}

/**
 * 玉兔月妙被击败处理
 * @param {object} eim - 事件实例管理器
 */
function bunnyDefeated(eim) {
    eim.dropMessage(5, "因未能保护好月妙导致其重伤，你已被传送至流放之地！");
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
    if (mapid < minMapId || mapid > maxMapId || mapid == 910010300) {
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
    eim.warpEventTeam(910010100);
}

/**
 * 怪物被击杀处理
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function monsterKilled(mob, eim) {}

/**
 * 友方怪物死亡处理
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function friendlyKilled(mob, eim) {
    if (mob.getId() == 9300061) {
        eim.schedule("bunnyDefeated", 5 * 1000);
    }
}

/**
 * 友方怪物掉落物品处理（玉兔捣年糕）
 * @param {object} eim - 事件实例管理器
 * @param {object} mob - 怪物对象
 */
function friendlyItemDrop(eim, mob) {
    if (mob.getId() == 9300061) {
        var cakes = eim.getIntProperty("bunnyCake") + 1;
        eim.setIntProperty("bunnyCake", cakes);
        mob.getMap().broadcastMessage(PacketCreator.serverNotice(6, `玉兔月妙成功捣出了第 ${cakes} 份年糕！`));
    }
}

/**
 * 友方怪物受到伤害处理
 * @param {object} eim - 事件实例管理器
 * @param {object} mob - 怪物对象
 */
function friendlyDamaged(eim, mob) {
    if (mob.getId() == 9300061) {
        var bunnyDamage = eim.getIntProperty("bunnyDamaged") + 1;
        if (bunnyDamage > 5) {
            mob.getMap().broadcastMessage(PacketCreator.serverNotice(5, "玉兔月妙受到了伤害，请保护好它，这样它才能继续做出美味的年糕！"));
            eim.setIntProperty("bunnyDamaged", 0);
        } else {
            eim.setIntProperty("bunnyDamaged", bunnyDamage);
        }
    }
}

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