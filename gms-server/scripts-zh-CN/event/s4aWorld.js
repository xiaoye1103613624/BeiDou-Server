/*
 * @description: 第四职业四转技能任务脚本
 *               处理弓箭手四转技能任务（狙击/集中术）的副本逻辑
 */

/** 最小组队人数 */
var minPlayers = 1;

/**
 * 初始化事件
 * 设置任务未开始状态
 */
function init() {
    em.setProperty("started", "false");
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
 * 从给定队伍中选择符合条件的队员
 * 要求队员在指定地图且等级>=120级
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

            if (ch.getMapId() == 105090200 && ch.getLevel() >= 120) {
                if (ch.isLeader()) {
                    hasLeader = true;
                }
                eligible.push(ch);
            }
        }
    }

    if (!(hasLeader && eligible.length >= minPlayers)) {
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
    var eim = em.newInstance("s4aWorld_" + lobbyid);
    eim.setProperty("level", level);

    eim.getInstanceMap(910500000).resetPQ(1);
    respawnStages(eim);
    eim.getMapInstance(910500000).instanceMapForceRespawn();
    eim.startEventTimer(1200000);

    em.setProperty("started", "true");

    return eim;
}

/**
 * 任务设置完成后的回调函数
 * 
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {}

/**
 * 重新生成任务阶段
 * 每15秒重新生成一次怪物
 * 
 * @param {object} eim - 事件实例管理器
 */
function respawnStages(eim) {
    eim.getMapInstance(910500000).instanceMapRespawn();
    eim.schedule("respawnStages", 15 * 1000);
}

/**
 * 玩家进入任务处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(910500000);
    player.changeMap(map, map.getPortal(0));
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
function playerRevive(eim, player) {}

/**
 * 任务超时处理
 * 
 * @param {object} eim - 事件实例管理器
 */
function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 105090200);

    em.setProperty("started", "false");
}

/**
 * 玩家切换地图处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {number} mapid - 目标地图ID
 */
function changedMap(eim, player, mapid) {
    if (mapid != 910500000) {
        eim.unregisterPlayer(player);

        if (eim.disposeIfPlayerBelow(minPlayers, 105090200)) {
            em.setProperty("started", "false");
        }
    }
}

/**
 * 玩家断开连接处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @returns {number} 0
 */
function playerDisconnected(eim, player) {
    return 0;
}

/**
 * 玩家离开队伍处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function leftParty(eim, player) {
    playerExit(eim, player);
}

/**
 * 队伍解散处理
 * 
 * @param {object} eim - 事件实例管理器
 */
function disbandParty(eim) {
    eim.disposeIfPlayerBelow(100, 105090200);

    em.setProperty("started", "false");
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
    var map = eim.getMapFactory().getMap(105090200);
    player.changeMap(map, map.getPortal(0));
}

/**
 * 完成任务处理
 * 
 * @param {object} eim - 事件实例管理器
 */
function clearPQ(eim) {
    eim.disposeIfPlayerBelow(100, 105090200);

    em.setProperty("started", "false");
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
 */
function dispose() {}


// ---------- FILLER FUNCTIONS ----------

/**
 * 队长变更处理
 * 
 * @param {object} eim - 事件实例管理器
 * @param {object} leader - 新队长
 */
function changedLeader(eim, leader) {}