/**
 * @description: 火车运输系统脚本
 *               处理天空之城(Orbis)与玩具城(Ludibrium)之间的火车运输
 */

/** 天空之城候车室 */
var Orbis_btf;
/** 开往天空之城的火车 */
var Train_to_Orbis;
/** 天空之城火车站台 */
var Orbis_docked;
/** 玩具城候车室 */
var Ludibrium_btf;
/** 开往玩具城的火车 */
var Train_to_Ludibrium;
/** 玩具城火车站台 */
var Ludibrium_docked;
/** 天空之城售票处 */
var Orbis_Station;
/** 玩具城售票处 */
var Ludibrium_Station;

/** 时间设置（单位：毫秒） */
/** 关闭入口的时间 (4分钟) */
var closeTime = 4 * 60 * 1000;
/** 火车出发前的准备时间 (5分钟) */
var beginTime = 5 * 60 * 1000;
/** 到达目的地所需的时间 (5分钟) */
var rideTime = 5 * 60 * 1000;

/**
 * 初始化函数
 * 修正交通工具旅行时间并获取地图实例
 */
function init() {
    closeTime = em.getTransportationTime(closeTime);
    beginTime = em.getTransportationTime(beginTime);
    rideTime = em.getTransportationTime(rideTime);

    Orbis_btf = em.getChannelServer().getMapFactory().getMap(200000122);
    Ludibrium_btf = em.getChannelServer().getMapFactory().getMap(220000111);
    Train_to_Orbis = em.getChannelServer().getMapFactory().getMap(200090110);
    Train_to_Ludibrium = em.getChannelServer().getMapFactory().getMap(200090100);
    Orbis_docked = em.getChannelServer().getMapFactory().getMap(200000121);
    Ludibrium_docked = em.getChannelServer().getMapFactory().getMap(220000110);
    Orbis_Station = em.getChannelServer().getMapFactory().getMap(200000100);
    Ludibrium_Station = em.getChannelServer().getMapFactory().getMap(220000100);

    scheduleNew();
}

/**
 * 设置新的火车运行周期
 * 重置停靠状态并安排关闭入口和出发任务
 */
function scheduleNew() {
    em.setProperty("docked", "true");
    Orbis_docked.setDocked(true);
    Ludibrium_docked.setDocked(true);

    em.setProperty("entry", "true");
    em.schedule("stopEntry", closeTime);
    em.schedule("takeoff", beginTime);
}

/**
 * 关闭火车站入口
 */
function stopEntry() {
    em.setProperty("entry", "false");
}

/**
 * 火车出发处理
 * 将候车室的玩家传送到火车上，并安排到达任务
 */
function takeoff() {
    Orbis_btf.warpEveryone(Train_to_Ludibrium.getId());
    Ludibrium_btf.warpEveryone(Train_to_Orbis.getId());
    Orbis_docked.broadcastShip(false);
    Ludibrium_docked.broadcastShip(false);

    em.setProperty("docked", "false");
    Orbis_docked.setDocked(false);
    Ludibrium_docked.setDocked(false);

    em.schedule("arrived", rideTime);
}

/**
 * 火车到达目的地处理
 * 将火车上的玩家传送到对应的售票处
 */
function arrived() {
    Train_to_Orbis.warpEveryone(Orbis_Station.getId(), 0);
    Train_to_Ludibrium.warpEveryone(Ludibrium_Station.getId(), 0);
    Orbis_docked.broadcastShip(true);
    Ludibrium_docked.broadcastShip(true);
    scheduleNew();
}

/**
 * 取消预定的事件/任务调度
 */
function cancelSchedule() {}


// ---------- FILLER FUNCTIONS ----------

function dispose() {}

function setup(eim, leaderid) {}

function monsterValue(eim, mobid) {return 0;}

function disbandParty(eim, player) {}

function playerDisconnected(eim, player) {}

function playerEntry(eim, player) {}

function monsterKilled(mob, eim) {}

function scheduledTimeout(eim) {}

function afterSetup(eim) {}

function changedLeader(eim, leader) {}

function playerExit(eim, player) {}

function leftParty(eim, player) {}

function clearPQ(eim) {}

function allMonstersDead(eim) {}

function playerUnregistered(eim, player) {}