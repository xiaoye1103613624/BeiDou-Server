/**
 * @description: 任务 q3239 事件脚本
 *               处理单人副本任务，玩家需要在限定时间内完成挑战
 */

/** 入口地图对象 */
var entryMap;
/** 出口地图对象 */
var exitMap;
/** 事件时长（分钟） */
var eventLength = 20;

/**
 * 初始化函数
 * 设置事件状态和地图实例
 */
function init() {
    em.setProperty("noEntry", "false");
    entryMap = em.getChannelServer().getMapFactory().getMap(922000000);
    exitMap = em.getChannelServer().getMapFactory().getMap(922000009);
}

/**
 * 设置事件实例
 * @param {number} level - 玩家等级
 * @param {number} lobbyid - 场次ID
 * @returns {object} 事件实例管理器
 */
function setup(level, lobbyid) {
    var eim = em.newInstance("q3239_" + lobbyid);
    eim.setExclusiveItems([4031092]);
    return eim;
}

/**
 * 玩家进入事件处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    var im = eim.getInstanceMap(entryMap.getId());

    // 重置副本状态
    im.clearDrops();
    im.resetReactors();
    im.shuffleReactors();

    // 启动计时器
    eim.startEventTimer(eventLength * 60 * 1000);

    // 传送玩家并标记事件为进行中
    player.changeMap(entryMap, 0);
    em.setProperty("noEntry", "true");
}

/**
 * 玩家切换地图处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {number} mapid - 目标地图ID
 */
function changedMap(eim, player, mapid) {
    if (mapid != entryMap.getId())
        playerExit(eim, player);
}

/**
 * 玩家退出事件处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerExit(eim, player) {
    end(eim);
}

/**
 * 玩家断开连接处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDisconnected(eim, player) {
    end(eim);
}

/**
 * 事件超时处理
 * @param {object} eim - 事件实例管理器
 */
function scheduledTimeout(eim) {
    end(eim);
}

/**
 * 结束事件处理
 * @param {object} eim - 事件实例管理器
 */
function end(eim) {
    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++) {
        var player = party.get(i);
        eim.unregisterPlayer(player);
        player.changeMap(exitMap);
    }

    eim.dispose();
    em.setProperty("noEntry", "false");
}


// ---------- 预留函数(空实现) ----------

function disbandParty(eim, player) {}
function afterSetup(eim) {}
function playerUnregistered(eim, player) {}
function changedLeader(eim, leader) {}
function leftParty(eim, player) {}
function clearPQ(eim) {}
function dispose() {}
function cancelSchedule() {}
function allMonstersDead(eim) {}
function monsterValue(eim, mobId) {}
function monsterKilled(mob, eim) {}