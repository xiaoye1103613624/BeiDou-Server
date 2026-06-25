/**
 * 地铁事件脚本
 * 处理天空之城(KC)与新叶城(NLC)之间的地铁运输系统
 */

// 地图实例变量
/** 天空之城地铁站候车室 */
var KC_Waiting;
/** 开往天空之城的地铁 */
var Subway_to_KC;
/** 天空之城地铁站 */
var KC_docked;
/** 新叶城地铁站候车室 */
var NLC_Waiting;
/** 开往新叶城的地铁 */
var Subway_to_NLC;
/** 新叶城地铁站 */
var NLC_docked;

// 时间设置（单位：毫秒）
/** 关闭入口的时间 (50秒) */
var closeTime = 50 * 1000;
/** 地铁出发前的准备时间 (1分钟) */
var beginTime = 1 * 60 * 1000;
/** 到达目的地所需的时间 (4分钟) */
var rideTime = 4 * 60 * 1000;

/**
 * 初始化函数
 * 修正交通工具旅行时间并获取地图实例
 */
function init() {
    closeTime = em.getTransportationTime(closeTime);
    beginTime = em.getTransportationTime(beginTime);
    rideTime = em.getTransportationTime(rideTime);

    KC_Waiting = em.getChannelServer().getMapFactory().getMap(600010004);
    NLC_Waiting = em.getChannelServer().getMapFactory().getMap(600010002);
    Subway_to_KC = em.getChannelServer().getMapFactory().getMap(600010003);
    Subway_to_NLC = em.getChannelServer().getMapFactory().getMap(600010005);
    KC_docked = em.getChannelServer().getMapFactory().getMap(103000100);
    NLC_docked = em.getChannelServer().getMapFactory().getMap(600010001);
    scheduleNew();
}

/**
 * 设置新的地铁运行周期
 * 重置停靠状态并安排关闭入口和出发任务
 */
function scheduleNew() {
    em.setProperty("docked", "true");
    em.setProperty("entry", "true");
    em.schedule("stopEntry", closeTime);
    em.schedule("takeoff", beginTime);
}

/**
 * 关闭地铁站入口
 */
function stopEntry() {
    em.setProperty("entry", "false");
}

/**
 * 地铁出发处理
 * 将候车室的玩家传送到地铁上，并安排到达任务
 */
function takeoff() {
    em.setProperty("docked", "false");
    KC_Waiting.warpEveryone(Subway_to_NLC.getId());
    NLC_Waiting.warpEveryone(Subway_to_KC.getId());
    em.schedule("arrived", rideTime);
}

/**
 * 地铁到达目的地处理
 * 将地铁上的玩家传送到对应的地铁站
 */
function arrived() {
    Subway_to_KC.warpEveryone(KC_docked.getId(), 0);
    Subway_to_NLC.warpEveryone(NLC_docked.getId(), 0);
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