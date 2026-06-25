/**
 * @description: 神鸟运输系统脚本
 *               处理天空之城(Orbis)与武陵(Wulin)之间的神鸟运输
 * @author: OdinMS Team
 * @event: Hak Transportation
 */

/** 返回地图数组 [天空之城, 武陵] */
var returnTo = [200000141, 250000100];
/** 目的地地图数组 [武陵, 天空之城] */
var rideTo = [250000100, 200000141];
/** 神鸟地图数组 [天空之城→武陵, 武陵→天空之城] */
var birdRide = [200090300, 200090310];
/** 当前乘坐的神鸟索引 */
var myRide;
/** 返回地图 */
var returnMap;
/** 出口地图 */
var exitMap;
/** 当前地图 */
var map;
/** 神鸟地图实例 */
var onRide;

/** 飞行时间（单位：毫秒） */
var rideTime = 60 * 1000;

/**
 * 初始化函数
 * 修正交通工具旅行时间
 */
function init() {
    rideTime = em.getTransportationTime(rideTime);
}

/**
 * 设置神鸟运输实例
 * @param {number} level - 玩家等级
 * @param {number} lobbyid - 场次ID
 * @returns {object} 事件实例管理器
 */
function setup(level, lobbyid) {
    var eim = em.newInstance("Hak_" + lobbyid);
    return eim;
}

/**
 * 设置完成后的回调函数
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {}

/**
 * 玩家进入神鸟运输处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerEntry(eim, player) {
    if (player.getMapId() == returnTo[0]) {
        myRide = 0;
    } else {
        myRide = 1;
    }
    exitMap = eim.getEm().getChannelServer().getMapFactory().getMap(rideTo[myRide]);
    returnMap = eim.getMapFactory().getMap(returnTo[myRide]);
    onRide = eim.getMapFactory().getMap(birdRide[myRide]);
    player.changeMap(onRide, onRide.getPortal(0));

    const PacketCreator = Java.type('org.gms.util.PacketCreator');
    player.sendPacket(PacketCreator.getClock(rideTime / 1000));
    eim.schedule("timeOut", rideTime);
}

/**
 * 飞行时间超时处理
 * @param {object} eim - 事件实例管理器
 */
function timeOut(eim) {
    end(eim);
}

/**
 * 玩家注销处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerUnregistered(eim, player) {}

/**
 * 玩家退出神鸟运输处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {boolean} success - 是否成功到达
 */
function playerExit(eim, player, success) {
    eim.unregisterPlayer(player);
    player.changeMap(success ? exitMap.getId() : returnMap.getId(), 0);
}

/**
 * 结束神鸟运输
 * @param {object} eim - 事件实例管理器
 */
function end(eim) {
    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++) {
        playerExit(eim, party.get(i), true);
    }
    eim.dispose();
}

/**
 * 玩家断开连接处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 */
function playerDisconnected(eim, player) {
    playerExit(eim, player, false);
}

/**
 * 取消预定的事件/任务调度
 */
function cancelSchedule() {}

/**
 * 释放资源
 * @param {object} eim - 事件实例管理器
 */
function dispose(eim) {}


// ---------- FILLER FUNCTIONS ----------

function monsterValue(eim, mobid) {return 0;}

function disbandParty(eim, player) {}

function monsterKilled(mob, eim) {}

function scheduledTimeout(eim) {}

function changedLeader(eim, leader) {}

function leftParty(eim, player) {}

function clearPQ(eim) {}

function allMonstersDead(eim) {}