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
 * @description: 公会任务远征副本脚本
 *               处理沙伦尼亚(Sarenian)公会任务的核心逻辑
 *               特色机制：要求所有队员来自同一公会，全员职业集齐可获得增益效果
 * @author: Ronan
 * @event: Sharenian Guild PQ
 */

/** 是否为组队任务 */
var isPq = true;
/** 最小/最大玩家数 */
var minPlayers = 6, maxPlayers = 30;
/** 最小/最大等级要求 */
var minLevel = 1, maxLevel = 255;
/** 入口地图 */
var entryMap = 990000000;
/** 出口地图 */
var exitMap = 990001100;
/** 招募地图 */
var recruitMap = 101030104;
/** 通关地图 */
var clearMap = 990001000;

/** 最小地图ID范围 */
var minMapId = 990000000;
/** 最大地图ID范围 */
var maxMapId = 990001101;

/** 等待时间（分钟） */
var waitTime = 3;
/** 事件时间限制（分钟） */
var eventTime = 90;
/** 额外时间（分钟） */
var bonusTime = 0.5;

/** 最大同时进行的副本数 */
const maxLobbies = 1;

/** 游戏配置类引用 */
const GameConfig = Java.type('org.gms.config.GameConfig');
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

    reqStr += "\r\n    All members of the same guild";

    reqStr += "\r\n   时间限制: ";
    reqStr += eventTime + " 分钟";

    em.setProperty("party", reqStr);
}

/**
 * 设置事件专属物品
 * @param {object} eim - 事件实例管理器
 */
function setEventExclusives(eim) {
    var itemSet = [1032033, 4001024, 4001025, 4001026, 4001027, 4001028, 4001029, 4001030, 4001031, 4001032, 4001033, 4001034, 4001035, 4001037];
    eim.setExclusiveItems(itemSet);
}

/**
 * 设置事件奖励
 * @param {object} eim - 事件实例管理器
 */
function setEventRewards(eim) {
    var itemSet, itemQty, evLevel, expStages;

    evLevel = 1;
    itemSet = [];
    itemQty = [];
    eim.setEventRewards(evLevel, itemSet, itemQty);

    expStages = [];
    eim.setEventClearStageExp(expStages);
}

/**
 * 从给定队伍中选择符合条件的队员
 * 要求队员在招募地图、等级在范围内且属于同一公会
 * @param {object} party - 队伍对象
 * @returns {Array} 符合条件的队员数组
 */
function getEligibleParty(party) {
    var eligible = [];
    var hasLeader = false;

    var guildId = 0;

    if (party.size() > 0) {
        var partyList = party.toArray();

        for (var i = 0; i < party.size(); i++) {
            var ch = partyList[i];
            if (ch.isLeader()) {
                guildId = ch.getGuildId();
                break;
            }
        }

        for (var i = 0; i < party.size(); i++) {
            var ch = partyList[i];

            if (ch.getMapId() == recruitMap && ch.getLevel() >= minLevel && ch.getLevel() <= maxLevel && ch.getGuildId() == guildId) {
                if (ch.isLeader()) {
                    hasLeader = true;
                }
                eligible.push(ch);
            }
        }
    }

    if (!(hasLeader)) {
        eligible = [];
    }
    return Java.to(eligible, Java.type('org.gms.net.server.world.PartyCharacter[]'));
}

/**
 * 设置副本实例
 * @param {number} level - 难度等级
 * @param {number} lobbyid - 副本ID
 * @returns {object} 事件实例管理器
 */
function setup(level, lobbyid) {
    var eim = em.newInstance("Guild" + lobbyid);
    eim.setProperty("level", level);

    eim.setProperty("guild", 0);
    eim.setProperty("canJoin", 1);
    eim.setProperty("canRevive", 0);

    eim.getInstanceMap(990000000).resetPQ(level);
    eim.getInstanceMap(990000100).resetPQ(level);
    eim.getInstanceMap(990000200).resetPQ(level);
    eim.getInstanceMap(990000300).resetPQ(level);
    eim.getInstanceMap(990000301).resetPQ(level);
    eim.getInstanceMap(990000400).resetPQ(level);
    eim.getInstanceMap(990000401).resetPQ(level);
    eim.getInstanceMap(990000410).resetPQ(level);
    eim.getInstanceMap(990000420).resetPQ(level);
    eim.getInstanceMap(990000430).resetPQ(level);
    eim.getInstanceMap(990000431).resetPQ(level);
    eim.getInstanceMap(990000440).resetPQ(level);
    eim.getInstanceMap(990000500).resetPQ(level);
    eim.getInstanceMap(990000501).resetPQ(level);
    eim.getInstanceMap(990000502).resetPQ(level);
    eim.getInstanceMap(990000600).resetPQ(level);
    eim.getInstanceMap(990000610).resetPQ(level);
    eim.getInstanceMap(990000611).resetPQ(level);
    eim.getInstanceMap(990000620).resetPQ(level);
    eim.getInstanceMap(990000630).resetPQ(level);
    eim.getInstanceMap(990000631).resetPQ(level);
    eim.getInstanceMap(990000640).resetPQ(level);
    eim.getInstanceMap(990000641).resetPQ(level);
    eim.getInstanceMap(990000700).resetPQ(level);
    eim.getInstanceMap(990000800).resetPQ(level);
    eim.getInstanceMap(990000900).resetPQ(level);
    eim.getInstanceMap(990001000).resetPQ(level);
    eim.getInstanceMap(990001100).resetPQ(level);
    eim.getInstanceMap(990001101).resetPQ(level);

    respawnStages(eim);

    var ts = Date.now();
    ts += (60000 * waitTime);
    eim.setProperty("entryTimestamp", "" + ts);

    eim.startEventTimer(waitTime * 60000);

    setEventRewards(eim);
    setEventExclusives(eim);

    return eim;
}

/**
 * 检查队伍是否包含所有职业
 * @param {object} eim - 事件实例管理器
 * @returns {boolean} 是否包含所有职业
 */
function isTeamAllJobs(eim) {
    var eventJobs = eim.getEventPlayersJobs();
    var rangeJobs = parseInt('111110', 2);

    return ((eventJobs & rangeJobs) == rangeJobs);
}

/**
 * 设置完成后的回调
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {
    var leader = em.getChannelServer().getPlayerStorage().getCharacterById(eim.getLeaderId());
    if (leader != null) {
        eim.setProperty("guild", "" + leader.getGuildId());
    }
}

/**
 * 重生阶段怪物
 * @param {object} eim - 事件实例管理器
 */
function respawnStages(eim) {}

/**
 * 玩家进入事件处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    var map = eim.getMapInstance(entryMap);
    player.changeMap(map, map.getPortal(0));
}

/**
 * 定时超时处理
 * @param {object} eim - 事件实例管理器
 */
function scheduledTimeout(eim) {
    if (eim.isEventCleared()) {
        eim.warpEventTeam(990001100);
    } else {
        if (eim.getIntProperty("canJoin") == 1) {
            eim.setProperty("canJoin", 0);

            if (eim.checkEventTeamLacking(true, minPlayers)) {
                end(eim);
            } else {
                eim.startEventTimer(eventTime * 60000);

                if (isTeamAllJobs(eim)) {
                    var rnd = Math.floor(Math.random() * 4);
                    eim.applyEventPlayersItemBuff(2023000 + rnd);
                }
            }
        } else {
            end(eim);
        }
    }
}

/**
 * 玩家注销处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerUnregistered(eim, player) {
    player.cancelEffect(2023000);
    player.cancelEffect(2023001);
    player.cancelEffect(2023002);
    player.cancelEffect(2023003);
}

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
 * 玩家切换地图处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {number} mapid - 目标地图ID
 */
function changedMap(eim, player, mapid) {
    if (mapid < minMapId || mapid > maxMapId) {
        if (eim.isEventTeamLackingNow(true, minPlayers, player) && eim.getIntProperty("canJoin") == 0) {
            eim.unregisterPlayer(player);
            end(eim);
        } else {
            eim.unregisterPlayer(player);
        }
    }
}

/**
 * 切换地图后的回调
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {number} mapid - 目标地图ID
 */
function afterChangedMap(eim, player, mapid) {
    if (mapid == 990000100) {
        var texttt = "So, here is the brief. You guys should be warned that, once out on the fortress outskirts, anyone that would not be equipping the #b#t1032033##k will die instantly due to the deteriorated state of the air around there. That being said, once your team moves out, make sure to #bhit the glowing rocks#k in that region and #bequip the dropped item#k before advancing stages. That will protect you thoroughly from the air sickness. Good luck!";
        player.getAbstractPlayerInteraction().npcTalk(9040000, texttt);
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
function playerDead(eim, player) {
    if (player.getMapId() == 990000900) {
        if (player.getMap().countAlivePlayers() == 0 && player.getMap().countMonsters() > 0) {
            end(eim);
        }
    }
}

/**
 * 玩家复活处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @returns {boolean} 是否允许复活
 */
function playerRevive(eim, player) {
    if (eim.getIntProperty("canRevive") == 0) {
        if (eim.isEventTeamLackingNow(true, minPlayers, player) && eim.getIntProperty("canJoin") == 0) {
            player.respawn(eim, exitMap);
            end(eim);
        } else {
            player.respawn(eim, exitMap);
        }

        return false;
    }

    return true;
}

/**
 * 玩家断开连接处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDisconnected(eim, player) {
    if (eim.isEventTeamLackingNow(true, minPlayers, player) && eim.getIntProperty("canJoin") == 0) {
        eim.unregisterPlayer(player);
        end(eim);
    } else {
        eim.unregisterPlayer(player);
    }
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

    eim.warpEventTeam(clearMap);
    eim.startEventTimer(bonusTime * 60000);
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
 * 取消调度
 */
function cancelSchedule() {}

/**
 * 释放资源
 * @param {object} eim - 事件实例管理器
 */
function dispose(eim) {
    em.schedule("reopenGuildQuest", em.getLobbyDelay() * 1.5 * 1000);
}

/**
 * 重新开放公会任务
 */
function reopenGuildQuest() {
    em.attemptStartGuildInstance();
}