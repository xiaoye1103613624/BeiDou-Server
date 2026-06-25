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
 * @description: 节日组队任务脚本 - 初级
 *               处理21-30级玩家的节日PQ，特色机制为保护雪人并使其进化
 *               玩家需要收集雪之精气喂养雪人，雪人进化到最高级后召唤BOSS Scrooge
 * @author: Ronan
 * @event: Holiday PQ Level 1
 */

/** 是否为组队任务 */
var isPq = true;
/** 最小/最大玩家数 */
var minPlayers = 3, maxPlayers = 6;
/** 最小/最大等级要求 */
var minLevel = 21, maxLevel = 30;
/** 进入地图ID */
var entryMap = 889100001;
/** 退出地图ID */
var exitMap = 889100002;
/** 招募地图ID */
var recruitMap = 889100000;
/** 通关地图ID */
var clearMap = 889100002;

/** 最小地图ID范围 */
var minMapId = 889100001;
/** 最大地图ID范围 */
var maxMapId = 889100001;

/** 事件时间限制（分钟） */
var eventTime = 15;

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
    var itemSet = [4032094, 4032095];
    eim.setExclusiveItems(itemSet);
}

/**
 * 设置事件奖励
 * @param {object} eim - 事件实例管理器
 */
function setEventRewards(eim) {
    var itemSet, itemQty, evLevel, expStages;

    evLevel = 3;
    itemSet = [1302080, 1002033, 2022153, 2022042, 2020006, 2020009, 2020016, 2020024, 4010006, 4010007, 4020004, 4020005, 4003002];
    itemQty = [1, 1, 1, 5, 20, 15, 10, 10, 2, 4, 4, 4, 1];
    eim.setEventRewards(evLevel, itemSet, itemQty);

    evLevel = 2;
    itemSet = [1302080, 1002033, 2012005, 2012006, 2020002, 2020025, 2020026, 4010003, 4010004, 4010005, 4020002, 4020003, 4020007];
    itemQty = [1, 1, 15, 15, 15, 10, 10, 3, 3, 3, 3, 3, 3];
    eim.setEventRewards(evLevel, itemSet, itemQty);

    evLevel = 1;
    itemSet = [1002033, 2012005, 2012006, 2020002, 2022006, 2022002, 4010000, 4010001, 4010002, 4020000, 4020001, 4020006];
    itemQty = [1, 15, 15, 10, 5, 5, 2, 2, 2, 2, 2, 2];
    eim.setEventRewards(evLevel, itemSet, itemQty);

    expStages = [210, 620, 500, 1400, 950, 2200];
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
    var eim = em.newInstance("Holiday1_" + lobbyid);
    eim.setProperty("level", level);
    eim.setProperty("stage", "0");
    eim.setProperty("statusStg1", "-1");
    eim.setProperty("missingDrops", "0");
    eim.setProperty("snowmanLevel", "0");
    eim.setProperty("snowmanStep", "0");
    eim.setProperty("spawnedBoss", "0");

    var mapobj = eim.getInstanceMap(entryMap);
    mapobj.resetPQ(level);
    mapobj.allowSummonState(false);

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
    eim.getInstanceMap(entryMap).instanceMapRespawn();
    eim.schedule("respawnStages", 10 * 1000);
}

/**
 * 雪人自动回血
 * @param {object} eim - 事件实例管理器
 */
function snowmanHeal(eim) {
    var difficulty = eim.getIntProperty("level");
    var snowman = eim.getInstanceMap(entryMap).getMonsterById(9400316 + (5 * difficulty) + 5);

    snowman.heal(200 + 200 * difficulty, 0);
    eim.schedule("snowmanHeal", 10 * 1000);
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
    eim.applyEventPlayersItemBuff(2022436);
}

/**
 * 判断是否为BOSS Scrooge
 * @param {object} mob - 怪物对象
 * @returns {boolean} 是否为Scrooge
 */
function isScrooge(mob) {
    var mobid = mob.getId();
    return mobid >= 9400319 && mobid <= 9400321;
}

/**
 * 怪物被击杀处理
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function monsterKilled(mob, eim) {
    try {
        if (eim.isEventCleared()) {
            return;
        } else if (isScrooge(mob)) {
            eim.giveEventPlayersStageReward(2 * eim.getIntProperty("level"));
            eim.showClearEffect();
            eim.clearPQ();
            return;
        }

        var rnd = Math.random();
        var forceDrop = false;
        if (rnd >= 0.42) {
            var miss = eim.getIntProperty("missingDrops");
            if (miss < 5) {
                eim.setIntProperty("missingDrops", miss + 1);
                return;
            }
            forceDrop = true;
        }

        var mapObj = mob.getMap();
        const Item = Java.type('org.gms.client.inventory.Item');
        var itemObj = new Item((forceDrop || Math.random() < 0.77) ? 4032094 : 4032095, 0, 1);
        var dropper = eim.getPlayers().get(0);

        mapObj.spawnItemDrop(mob, dropper, itemObj, mob.getPosition(), true, false);
        eim.setIntProperty("missingDrops", 0);
    } catch (err) {
    }
}

/**
 * 所有怪物死亡处理
 * @param {object} eim - 事件实例管理器
 */
function allMonstersDead(eim) {}

/**
 * 友方怪物死亡处理（雪人被击杀）
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function friendlyKilled(mob, eim) {
    eim.setIntProperty("snowmanStep", 0);
    var snowmanLevel = eim.getIntProperty("snowmanLevel");

    if (snowmanLevel <= 1) {
        end(eim);
    } else {
        eim.setIntProperty("snowmanLevel", snowmanLevel - 1);
    }
}

/**
 * 雪人进化
 * @param {object} eim - 事件实例管理器
 * @param {number} curLevel - 当前雪人等级
 */
function snowmanEvolve(eim, curLevel) {
    var mapobj = eim.getInstanceMap(entryMap);
    var difficulty = eim.getIntProperty("level");
    var snowman = mapobj.getMonsterById(9400317 + (5 * difficulty) + (curLevel - 1));

    eim.setIntProperty("snowmanLevel", curLevel + 2);
    mapobj.killMonster(snowman, null, false, 2);

    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    const Point = Java.type('java.awt.Point');
    var snowman = LifeFactory.getMonster(9400317 + (5 * difficulty) + curLevel);
    mapobj.spawnMonsterOnGroundBelow(snowman, new Point(-180, 15));

    if (curLevel >= 4) {
        mapobj.allowSummonState(false);
        mapobj.killAllMonstersNotFriendly();
        mapobj.setReactorState();

        eim.giveEventPlayersStageReward(2 * difficulty - 1);
        eim.showClearEffect();
    }
}

/**
 * 雪人进食（真雪之精气）
 * @param {object} eim - 事件实例管理器
 */
function snowmanSnack(eim) {
    if (eim.getIntProperty("snowmanLevel") >= 5) {
        return;
    }

    var step = eim.getIntProperty("snowmanStep");
    var snowmanLevel = eim.getIntProperty("snowmanLevel");

    if (step >= 2 + (eim.getIntProperty("level") * snowmanLevel)) {
        step = 0;
        snowmanEvolve(eim, snowmanLevel);
    } else {
        var mapobj = eim.getInstanceMap(entryMap);
        var difficulty = eim.getIntProperty("level");
        var snowman = mapobj.getMonsterById(9400316 + (5 * difficulty) + snowmanLevel);

        snowman.heal(200 + (200 * snowmanLevel), 0);
        step += 1;
    }

    eim.setIntProperty("snowmanStep", step);
}

/**
 * 雪人进食假雪之精气
 * @param {object} eim - 事件实例管理器
 */
function snowmanSnackFake(eim) {
    if (eim.getIntProperty("snowmanLevel") >= 5) {
        return;
    }

    var step = eim.getIntProperty("snowmanStep");
    if (step > 0) {
        eim.setIntProperty("snowmanStep", step - 1);
    }

    eim.dropMessage(5, "雪人吸收了假的雪之精气!");
}

/**
 * 取消调度任务
 */
function cancelSchedule() {}

/**
 * 释放资源
 * @param {object} eim - 事件实例管理器
 */
function dispose(eim) {}