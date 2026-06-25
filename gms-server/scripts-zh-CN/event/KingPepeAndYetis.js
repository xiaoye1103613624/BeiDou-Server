/**
 * @description: 皮皮国王与雪人战斗脚本
 *               处理马来西亚皮皮国王BOSS战斗任务，玩家需要击败皮皮国王和雪人
 * @author: OdinMS Team
 * @event: King Pepe and Yetis Battle
 */

/** 最小玩家数 */
var minPlayers = 1;
/** 时间限制（分钟） */
var timeLimit = 20;
/** 事件计时器（毫秒） */
var eventTimer = 1000 * 60 * timeLimit;
/** 出口地图 */
var exitMap = 106021400;
/** 事件地图 */
var eventMap = 106021500;

/**
 * 初始化函数
 */
function init() {}

/**
 * 设置事件实例
 * @param {number} difficulty - 难度等级
 * @param {number} lobbyId - 等待室ID
 * @returns {object} 事件实例管理器
 */
function setup(difficulty, lobbyId) {
    var eim = em.newInstance("KingPepe_" + lobbyId);
    eim.getInstanceMap(eventMap).resetFully();
    eim.getInstanceMap(eventMap).allowSummonState(false);

    eim.startEventTimer(eventTimer);
    return eim;
}

/**
 * 设置完成后的回调
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {}

/**
 * 玩家进入事件处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    var yetiMap = eim.getMapInstance(eventMap);
    player.changeMap(yetiMap, yetiMap.getPortal(1));
}

/**
 * 事件超时处理
 * @param {object} eim - 事件实例管理器
 */
function scheduledTimeout(eim) {
    var party = eim.getPlayers();

    for (var i = 0; i < party.size(); i++) {
        playerExit(eim, party.get(i));
    }

    eim.dispose();
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
 * 获取怪物价值
 * @param {object} eim - 事件实例管理器
 * @param {number} mobId - 怪物ID
 * @returns {number} 怪物价值
 */
function monsterValue(eim, mobId) {
    return -1;
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
 * 玩家离开队伍处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function leftParty(eim, player) {}

/**
 * 队伍解散处理
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
 * 玩家退出事件处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 2);
}

/**
 * 玩家切换地图处理
 * @param {object} eim - 事件实例管理器
 * @param {object} chr - 角色对象
 * @param {number} mapid - 地图ID
 */
function changedMap(eim, chr, mapid) {
    if (mapid != eventMap) {
        if (eim.isEventTeamLackingNow(true, minPlayers, chr)) {
            eim.unregisterPlayer(chr);
            end(eim);
        } else {
            eim.unregisterPlayer(chr);
        }
    }
}

/**
 * 取消调度任务
 */
function cancelSchedule() {}

/**
 * 释放资源
 */
function dispose() {}

/**
 * 通关组队任务
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

// ---------- FILLER FUNCTIONS ----------

function changedLeader(eim, leader) {}