/**
 * @description: 废弃都市地铁运输脚本
 *               处理废弃都市内部地铁运输，连接地铁站和废都广场
 * @author: OdinMS Team
 * @event: Kerning Train Transportation
 */

/** 返回地图数组 [地铁站, 废都广场] */
var returnTo = [103000100, 103000310];
/** 目的地地图数组 [废都广场, 地铁站] */
var rideTo = [103000310, 103000100];
/** 地铁运行地图数组 */
var trainRide = [103000301, 103000302];
/** 当前运行方向 */
var myRide;
/** 返回地图 */
var returnMap;
/** 出口地图 */
var exitMap;
/** 地图对象 */
var map;
/** 运行中的地图 */
var onRide;

/** 运行时间（毫秒） */
var rideTime = 10 * 1000;

/**
 * 初始化函数
 * 获取交通工具时间配置
 */
function init() {
    rideTime = em.getTransportationTime(rideTime);
}

/**
 * 设置事件实例
 * @param {number} level - 难度等级
 * @param {number} lobbyid - 等待室ID
 * @returns {object} 事件实例管理器
 */
function setup(level, lobbyid) {
    var eim = em.newInstance("KerningTrain_" + lobbyid);
    return eim;
}

/**
 * 设置完成后的回调
 * @param {object} eim - 事件实例管理器
 */
function afterSetup(eim) {}

/**
 * 玩家进入地铁处理
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
    onRide = eim.getMapFactory().getMap(trainRide[myRide]);
    player.changeMap(onRide, onRide.getPortal(0));

    const PacketCreator = Java.type('org.gms.util.PacketCreator');
    player.sendPacket(PacketCreator.getClock(rideTime / 1000));
    player.sendPacket(PacketCreator.earnTitleMessage("下一站停靠 " + (myRide == 0 ? "废都广场" : "废弃都市") + " 站。请走左侧门。"));
    eim.schedule("timeOut", rideTime);
}

/**
 * 超时处理
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
 * 玩家退出处理
 * @param {object} eim - 事件实例管理器
 * @param {object} player - 玩家对象
 * @param {boolean} success - 是否成功到达
 */
function playerExit(eim, player, success) {
    eim.unregisterPlayer(player);
    player.changeMap(success ? exitMap.getId() : returnMap.getId(), 0);
}

/**
 * 结束事件
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
 * 取消调度任务
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