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
 * @description: 品克缤远征队战斗脚本
 *               处理品克缤(Pink Bean)远征副本的核心逻辑，包括5波守护者阶段和最终BOSS战
 *               特色机制：队员阵亡累积到5人时远征失败，阵亡4人时品克缤进入强化模式
 * @author: Ronan
 * @event: Pink Bean Battle
 */

/** 是否为组队任务 */
var isPq = true;
/** 最小/最大玩家数 */
var minPlayers = 6, maxPlayers = 30;
/** 最小/最大等级要求 */
var minLevel = 120, maxLevel = 255;
/** 入口地图 */
var entryMap = 270050100;
/** 出口地图 */
var exitMap = 270050300;
/** 招募地图 */
var recruitMap = 270050000;
/** 通关地图 */
var clearMap = 270050300;

/** 最小地图ID范围 */
var minMapId = 270050100;
/** 最大地图ID范围 */
var maxMapId = 270050300;

/** 事件时间限制（分钟） */
var eventTime = 140;

/** 最大同时进行的副本数 */
const maxLobbies = 1;

/** 游戏配置类引用 */
const GameConfig = Java.type('org.gms.config.GameConfig');
/** 每日活跃管理器引用 */
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
function afterSetup(eim) {
    eim.dropMessage(5, "第一波攻击将在15秒后开始，请做好准备。");
    eim.schedule("startWave", 15 * 1000);
}

/**
 * 设置副本实例
 * @param {number} channel - 频道号
 * @returns {object} 事件实例管理器
 */
function setup(channel) {
    var eim = em.newInstance("PinkBean" + channel);
    eim.setProperty("canJoin", 1);
    eim.setProperty("defeatedBoss", 0);
    eim.setProperty("fallenPlayers", 0);

    eim.setProperty("stage", 1);
    eim.setProperty("channel", channel);

    var level = 1;
    eim.getInstanceMap(270050100).resetPQ(level);
    eim.getInstanceMap(270050200).resetPQ(level);
    eim.getInstanceMap(270050300).resetPQ(level);

    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    const Point = Java.type('java.awt.Point');
    var mob = LifeFactory.getMonster(8820000);
    mob.disableDrops();
    eim.getInstanceMap(270050100).spawnMonsterOnGroundBelow(mob, new Point(0, -42));

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
        if (eim.isExpeditionTeamLackingNow(true, minPlayers, player)) {
            eim.unregisterPlayer(player);
            eim.dropMessage(5, "[远征队] 队长已退出远征队或队伍人数不足最低要求，无法继续。");
            end(eim);
        } else {
            eim.dropMessage(5, "[远征队] " + player.getName() + " 已离开远征队。");
            eim.unregisterPlayer(player);
        }
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
 * 累积阵亡人数，达到5人时远征失败
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDead(eim, player) {
    var count = eim.getIntProperty("fallenPlayers");
    count = count + 1;

    eim.setIntProperty("fallenPlayers", count);

    if (count == 5) {
        eim.dropMessage(5, "[远征队] 太多队员阵亡，品克缤现在被视为不可战胜，远征结束。");
        end(eim);
    } else if (count == 4) {
        eim.dropMessage(5, "[远征队] 品克缤变得比以往更强大，大家进入背水一战模式！");
    } else if (count == 3) {
        eim.dropMessage(5, "[远征队] 伤亡人数开始失控，请小心战斗。");
    }
}

/**
 * 玩家复活处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @returns {boolean} 是否允许复活
 */
function playerRevive(eim, player) {
    return true;
}

/**
 * 怪物复活处理
 * @param {object} eim - 事件实例管理器
 * @param {object} mob - 怪物对象
 */
function monsterRevive(eim, mob) {
    if (isPinkBean(mob)) {
        mob.enableDrops();
    }
}

/**
 * 玩家断开连接处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDisconnected(eim, player) {
    if (eim.isExpeditionTeamLackingNow(true, minPlayers, player)) {
        eim.unregisterPlayer(player);
        eim.dropMessage(5, "[远征队] 队长已退出远征队或队伍人数不足最低要求，无法继续。");
        end(eim);
    } else {
        eim.dropMessage(5, "[远征队] " + player.getName() + " 已离开远征队。");
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
 * 判断是否为品克缤BOSS
 * @param {object} mob - 怪物对象
 * @returns {boolean} 是否为品克缤
 */
function isPinkBean(mob) {
    var mobid = mob.getId();
    return (mobid == 8820001);
}

/**
 * 判断是否为小BOSS
 * @param {object} mob - 怪物对象
 * @returns {boolean} 是否为小BOSS
 */
function isJrBoss(mob) {
    var mobid = mob.getId();
    return (mobid >= 8820002 && mobid <= 8820006);
}

/**
 * 检查地图中是否还有小BOSS
 * @param {object} map - 地图对象
 * @returns {boolean} 是否没有小BOSS
 */
function noJrBossesLeft(map) {
    return map.countMonster(8820002, 8820006) == 0;
}

/**
 * 生成小BOSS
 * @param {object} mobObj - 怪物对象
 * @param {boolean} gotKilled - 是否被击杀
 */
function spawnJrBoss(mobObj, gotKilled) {
    if (gotKilled) {
        spawnid = mobObj.getId() + 17;
    } else {
        mobObj.getMap().killMonster(mobObj.getId());
        spawnid = mobObj.getId() - 17;
    }

    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    var mob = LifeFactory.getMonster(spawnid);
    mobObj.getMap().spawnMonsterOnGroundBelow(mob, mobObj.getPosition());
}

/**
 * 怪物被击杀处理
 * @param {object} mob - 怪物对象
 * @param {object} eim - 事件实例管理器
 */
function monsterKilled(mob, eim) {
    if (isPinkBean(mob)) {
        eim.setIntProperty("defeatedBoss", 1);
        eim.showClearEffect(mob.getMap().getId());
        mob.getMap().killAllMonsters();
        eim.clearPQ();

        var ch = eim.getIntProperty("channel");
        mob.getMap().broadcastPinkBeanVictory(ch);

        // 每日活跃-通关副本：品克缤通关时，给在场全部远征队成员各记1次进度
        addPqClearProgress(eim);
    } else if (isJrBoss(mob)) {
        if (noJrBossesLeft(mob.getMap())) {
            var stage = eim.getIntProperty("stage");

            if (stage == 5) {
                var iid = 4001193;
                const Item = Java.type('org.gms.client.inventory.Item');
                var itemObj = new Item(iid, 0, 1);
                var mapObj = eim.getMapFactory().getMap(270050100);
                var reactObj = mapObj.getReactorById(2708000);
                var dropper = eim.getPlayers().get(0);
                mapObj.spawnItemDrop(dropper, dropper, itemObj, reactObj.getPosition(), true, true);

                eim.dropMessage(6, "随着最后的守护者倒下，品克缤失去了无敌状态。真正的战斗现在开始！");
            } else {
                stage++;
                eim.setIntProperty("stage", stage);

                eim.dropMessage(5, "下一波攻击将在15秒后开始，请做好准备。");
                eim.schedule("startWave", 15 * 1000);
            }
        }
    }
}

/**
 * 每日活跃-通关副本：给当前远征队全部在场成员累计1次pq_clear进度
 * @param {object} eim - 事件实例管理器
 */
function addPqClearProgress(eim) {
    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++) {
        DailyActiveManager.addProgress(party.get(i).getId(), "pq_clear", 1);
    }
}

/**
 * 开始新一波攻击
 * @param {object} eim - 事件实例管理器
 */
function startWave(eim) {
    var mapObj = eim.getMapInstance(270050100);
    var stage = eim.getProperty("stage");

    for (var i = 1; i <= stage; i++) {
        spawnJrBoss(mapObj.getMonsterById(8820019 + (i % 5)), false);
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