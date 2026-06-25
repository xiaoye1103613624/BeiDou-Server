/**
 * 飞机事件脚本
 * 处理天空之城(KC)与江户村(CBD)之间的飞机运输系统
 */

// 地图实例变量
/** 天空之城候机场 */
var KC_bfd;
/** 飞往江户村的飞机 */
var Plane_to_CBD;
/** 江户村停机坪 */
var CBD_docked;
/** 江户村候机场 */
var CBD_bfd;
/** 飞往天空之城的飞机 */
var Plane_to_KC;
/** 天空之城停机坪 */
var KC_docked;

// 时间设置（单位：毫秒）
/** 关闭登机口的时间 (4分钟) */
var closeTime = 4 * 60 * 1000;
/** 飞机起飞前的准备时间 (5分钟) */
var beginTime = 5 * 60 * 1000;
/** 飞行到目的地所需时间 (1分钟) */
var rideTime = 1 * 60 * 1000;

/**
 * 初始化函数
 * 修正交通工具旅行时间并获取地图实例
 */
function init() {
    closeTime = em.getTransportationTime(closeTime);
    beginTime = em.getTransportationTime(beginTime);
    rideTime = em.getTransportationTime(rideTime);

    KC_bfd = em.getChannelServer().getMapFactory().getMap(540010100);
    CBD_bfd = em.getChannelServer().getMapFactory().getMap(540010001);
    Plane_to_CBD = em.getChannelServer().getMapFactory().getMap(540010101);
    Plane_to_KC = em.getChannelServer().getMapFactory().getMap(540010002);
    CBD_docked = em.getChannelServer().getMapFactory().getMap(540010000);
    KC_docked = em.getChannelServer().getMapFactory().getMap(103000000);
    scheduleNew();
}

/**
 * 设置新的飞行周期
 * 重置停靠状态并安排关闭入口和起飞任务
 */
function scheduleNew() {
    em.setProperty("docked", "true");
    em.setProperty("entry", "true");
    em.schedule("stopEntry", closeTime);
    em.schedule("takeoff", beginTime);
}

/**
 * 关闭登机入口
 */
function stopEntry() {
    em.setProperty("entry", "false");
}

/**
 * 飞机起飞处理
 * 将候机场的玩家传送到飞机上，并安排到达任务
 */
function takeoff() {
    em.setProperty("docked", "false");
    KC_bfd.warpEveryone(Plane_to_CBD.getId());
    CBD_bfd.warpEveryone(Plane_to_KC.getId());
    em.schedule("arrived", rideTime);
}

/**
 * 飞机到达目的地处理
 * 将飞机上的玩家传送到对应的停机坪
 */
function arrived() {
    Plane_to_CBD.warpEveryone(CBD_docked.getId(), 0);
    Plane_to_KC.warpEveryone(KC_docked.getId(), 7);
    scheduleNew();
}

/**
 * 取消预定的事件/任务调度
 */
function cancelSchedule() {}

// ---------- 预留函数(空实现) ----------

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