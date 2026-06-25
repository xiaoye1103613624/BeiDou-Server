/**
 * @description: 渡轮运输系统脚本
 *               处理天空之城(Orbis)与魔法密林(Ellinia)之间的渡轮运输
 *               包含蝙蝠魔入侵事件处理
 */

// 渡轮相关地图变量
/** 天空之城候船室<开往魔法密林> */
var Orbis_btf;
/** 开往天空之城的船 */
var Boat_to_Orbis;
/** 船仓<开往天空之城> */
var Orbis_Boat_Cabin;
/** 天空之城码头<开往魔法密林> */
var Orbis_docked;
/** 魔法密林候船室<开往天空之城> */
var Ellinia_btf;
/** 开往魔法密林的船 */
var Boat_to_Ellinia;
/** 船仓<开往魔法密林> */
var Ellinia_Boat_Cabin;
/** 魔法密林码头 */
var Ellinia_docked;
/** 天空之城售票处 */
var Orbis_Station;

// 时间设置（以毫秒为单位），以下变量会被getTransportationTime()函数改变时间倍率而重新赋值
/** 关闭登船入口的时间 (4分钟) */
var closeTime = 4 * 60 * 1000;
/** 船只启航前的准备时间 (5分钟) */
var beginTime = 5 * 60 * 1000;
/** 到达目的地所需的时间 (10分钟) */
var rideTime = 10 * 60 * 1000;
/** 蝙蝠魔船只接近的时间 (3分钟) */
var invasionStartTime = 3 * 60 * 1000;
/** 蝙蝠魔船只接近的时间延迟 (1分钟) */
var invasionDelayTime = 1 * 60 * 1000;
/** 生成蝙蝠魔的时间延迟 (5秒) */
var invasionDelay = 5 * 1000;

/** 获取封包数据实例 */
const PacketCreator = Java.type('org.gms.util.PacketCreator');

/**
 * 初始化函数
 * 修正交通工具旅行时间和获取地图实例
 */
function init() {
    closeTime = em.getTransportationTime(closeTime);
    beginTime = em.getTransportationTime(beginTime);
    rideTime = em.getTransportationTime(rideTime);
    invasionStartTime = em.getTransportationTime(invasionStartTime);
    invasionDelayTime = em.getTransportationTime(invasionDelayTime);

    // 获取地图实例
    Orbis_btf = em.getChannelServer().getMapFactory().getMap(200000112);
    Ellinia_btf = em.getChannelServer().getMapFactory().getMap(101000301);
    Boat_to_Orbis = em.getChannelServer().getMapFactory().getMap(200090010);
    Boat_to_Ellinia = em.getChannelServer().getMapFactory().getMap(200090000);
    Orbis_Boat_Cabin = em.getChannelServer().getMapFactory().getMap(200090011);
    Ellinia_Boat_Cabin = em.getChannelServer().getMapFactory().getMap(200090001);
    Ellinia_docked = em.getChannelServer().getMapFactory().getMap(101000300);
    Orbis_Station = em.getChannelServer().getMapFactory().getMap(200000100);
    Orbis_docked = em.getChannelServer().getMapFactory().getMap(200000111);

    // 设置码头状态为已停靠
    Ellinia_docked.setDocked(true);
    Orbis_docked.setDocked(true);

    // 安排新的周期性任务
    scheduleNew();
}

/**
 * 设置新的渡轮运行周期
 * 重置停靠状态并安排关闭入口和出发任务
 */
function scheduleNew() {
    em.setProperty("docked", "true");
    em.setProperty("entry", "true");
    em.setProperty("haveBalrog", "false");

    // 安排关闭入口和起飞的时间点
    em.schedule("stopentry", closeTime);
    em.schedule("takeoff", beginTime);
}

/**
 * 关闭登船入口
 * 关闭入口后清除船舱内的对象（例如箱子）
 */
function stopentry() {
    em.setProperty("entry", "false");
    Orbis_Boat_Cabin.clearMapObjects();
    Ellinia_Boat_Cabin.clearMapObjects();
}

/**
 * 渡轮出发处理
 * 将候船室的玩家传送到船上，并广播船只离开的消息
 */
function takeoff() {
    Orbis_btf.warpEveryone(Boat_to_Ellinia.getId());
    Ellinia_btf.warpEveryone(Boat_to_Orbis.getId());
    Ellinia_docked.broadcastShip(false);
    Orbis_docked.broadcastShip(false);

    // 设置码头状态为未停靠
    em.setProperty("docked", "false");

    // 随机决定是否会有蝙蝠魔船只接近（42%概率）
    if (Math.random() < 0.42) {
        em.schedule("approach", invasionStartTime + Math.trunc(Math.random() * invasionDelayTime));
    }

    // 安排到达目的地的时间点
    em.schedule("arrived", rideTime);
}

/**
 * 渡轮到达目的地处理
 * 将船上的玩家传送到对应站点或码头
 */
function arrived() {
    Boat_to_Orbis.warpEveryone(Orbis_Station.getId(), 0);
    Orbis_Boat_Cabin.warpEveryone(Orbis_Station.getId(), 0);
    Boat_to_Ellinia.warpEveryone(Ellinia_docked.getId(), 1);
    Ellinia_Boat_Cabin.warpEveryone(Ellinia_docked.getId(), 1);

    // 播放船只到达的消息并重置蝙蝠魔状态
    Orbis_docked.broadcastShip(true);
    Ellinia_docked.broadcastShip(true);
    Boat_to_Orbis.broadcastEnemyShip(false);
    Boat_to_Ellinia.broadcastEnemyShip(false);
    Boat_to_Orbis.killAllMonsters();
    Boat_to_Ellinia.killAllMonsters();
    em.setProperty("haveBalrog", "false");

    // 安排下一个周期性任务
    scheduleNew();
}

/**
 * 蝙蝠魔船只接近处理
 * 当蝙蝠魔船只接近时，更改背景音乐并安排蝙蝠魔出现
 */
function approach() {
    em.setProperty("haveBalrog", "true");
    Boat_to_Orbis.broadcastEnemyShip(true);
    Boat_to_Ellinia.broadcastEnemyShip(true);

    // 更改背景音乐为海盗主题
    Boat_to_Orbis.broadcastMessage(PacketCreator.musicChange("Bgm04/ArabPirate"));
    Boat_to_Ellinia.broadcastMessage(PacketCreator.musicChange("Bgm04/ArabPirate"));

    // 安排蝙蝠魔出现的时间点
    em.schedule("invasion", invasionDelay);
}

/**
 * 蝙蝠魔入侵处理
 * 在两艘船上生成蝙蝠魔怪物
 */
function invasion() {
    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');

    var map1 = Boat_to_Ellinia;
    var pos1 = new java.awt.Point(-538, 143);
    map1.spawnMonsterOnGroundBelow(LifeFactory.getMonster(8150000), pos1);
    map1.spawnMonsterOnGroundBelow(LifeFactory.getMonster(8150000), pos1);

    var map2 = Boat_to_Orbis;
    var pos2 = new java.awt.Point(339, 148);
    map2.spawnMonsterOnGroundBelow(LifeFactory.getMonster(8150000), pos2);
    map2.spawnMonsterOnGroundBelow(LifeFactory.getMonster(8150000), pos2);
}

/**
 * 取消预定的事件/任务调度
 */
function cancelSchedule() {}


// ---------- 辅助函数 ----------

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