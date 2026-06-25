/**
 * @author: 萧曵
 * @event: 怪物攻城
 * @description:
 *   固定地图(默认亨利西斯 100000000)的全服限时攻城活动。
 *   持续10波怪物，每20秒刷新一波；每波随机选择当前地图的若干个传送门口，
 *   每个被选中的门口刷出 10只普通怪 / 3只野外(精英)BOSS / 3只高级BOSS(野外boss与高级boss按概率决定是否出现)。
 *   活动开始10分钟后检查地图怪物是否清空：
 *     - 清空 -> 视为攻城成功，开启全服双倍经验+双倍掉率(限时)
 *     - 未清空 -> 视为攻城失败，清空残留怪物
 *   开始/结束均全服广播。
 *
 *   仅在频道1运行，避免多频道重复刷怪导致计算混乱(与原版"怪物攻城"参考实现一致)。
 */

const Point = Java.type('java.awt.Point');
const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
const PacketCreator = Java.type('org.gms.util.PacketCreator');
const Portal = Java.type('org.gms.server.maps.Portal');
const LoggerFactory = Java.type('org.slf4j.LoggerFactory');
const GameConfig = Java.type('org.gms.config.GameConfig');
const DailyActiveManager = Java.type('org.gms.config.DailyActiveManager');

var log = null;
var channel = null;

// ========== 玩法参数(可按需调整) ==========
/** 攻城地图ID */
var siegeMapId = 100000000;
/** 总波数 */
var totalWaves = 10;
/** 每波刷新间隔(毫秒) */
var waveIntervalMs = 20000;
/** 活动开始后多久判定成功/失败(毫秒)，10分钟 */
var judgeDelayMs = 10 * 60 * 1000;
/** 每波随机选取的传送门口数量范围 [min, max] */
var doorsPerWaveMin = 1, doorsPerWaveMax = 3;
/** 每个被选中的门口刷怪数量 */
var normalCountPerDoor = 10;
var eliteCountPerDoor = 3;
var advancedCountPerDoor = 3;
/** 野外(精英)BOSS / 高级BOSS 每个门口是否出现的概率(0~1)，体现"也可能不出" */
var eliteSpawnChance = 0.5;
var advancedSpawnChance = 0.3;
/** 攻城成功后，双倍经验/掉率持续时间(毫秒)，30分钟 */
var rewardBuffDurationMs = 30 * 60 * 1000;
/** 每天固定自动开启攻城的时间点(24小时制，[小时,分钟]) */
var fixedStartTimes = [[12, 0], [19, 0]];

// TODO: 普通怪、高级BOSS池仍需按服务器实际怪物数据填入，禁止凭空猜测，需在游戏/Mob.wz中核实存在且可正常生成
/** 普通怪物池 */
var normalMobPool = [];
/**
 * 野外(精英)BOSS怪物池
 * 来源: mxd.dvg.cn(冒险岛小册子,079怀旧库)"形态:BOSS"筛选第1页数据，已逐个核实在本服 wz-zh-CN/Mob.wz 中存在对应img
 * 红蜗牛王/树妖王/大宇/蘑菇大臣/战甲吹泡泡鱼/歌尔夫x2/谢尔德/仙人玩偶/外星章鱼闪电棒/巨居蟹x2/浮士德/提莫/大王蜈蚣/
 * 书生鬼/陆陆猫/蘑菇王/多尔/朱诺/帕兹/僵尸蘑菇王/帕兹王/蝙蝠怪系列/自动警备系统/黄色鬼怪/蓝色鬼怪/琐巴鬼怪/
 * 肯德熊/九尾狐/妖怪禅师/迪特和罗伊/蝙蝠怪/蝙蝠魔/火焰龙/天鹰/艾利杰/驭狼雪人/吉米拉/大海兽/多多/玄冰独角兽/
 * 雷卡/蓝蘑菇王/小吃店
 */
var eliteMobPool = [
    2220000, 3220000, 3220001, 3300008, 4130103, 4220000, 4220001,
    5090000, 5090001, 5120100, 5220000, 5220001, 5220002, 5220003, 5220004,
    6090003, 6090004, 6130101, 6220000, 6220001, 6300004, 6300005,
    6400004, 6400005, 6400006, 6400007, 6400008, 6400009,
    7090000, 7130400, 7130401, 7130402, 7220000, 7220001, 7220002,
    8090000, 8130100, 8150000, 8180000, 8180001,
    8220000, 8220001, 8220002, 8220003, 8220004, 8220005, 8220006, 8220007, 8220009
];
/** 高级BOSS怪物池 */
var advancedMobPool = [];

// ========== 运行时状态 ==========
var siegeActive = false;
var currentWave = 0;
var doorPoints = [];
var waveTask = null;
var judgeTask = null;
var revertTask = null;
var restartTask = null;

function init() {
    channel = em.getChannelServer().getId();
    log = LoggerFactory.getLogger(em.getName());

    if (channel == 1) {
        scheduleAutoRestart();
    }
}

function cancelSchedule() {
    cancelTask(waveTask);
    cancelTask(judgeTask);
    cancelTask(revertTask);
    cancelTask(restartTask);
}

function cancelTask(task) {
    if (task != null) {
        task.cancel(true);
    }
}

/** 计算距离下一个固定开启时间点(fixedStartTimes中最近的一个)还有多少毫秒 */
function getDelayToNextFixedTime() {
    const Calendar = Java.type('java.util.Calendar');
    var now = Calendar.getInstance();
    var nowMs = now.getTimeInMillis();

    var nearestMs = -1;
    for (var i = 0; i < fixedStartTimes.length; i++) {
        var hour = fixedStartTimes[i][0];
        var minute = fixedStartTimes[i][1];

        var candidate = Calendar.getInstance();
        candidate.set(Calendar.HOUR_OF_DAY, hour);
        candidate.set(Calendar.MINUTE, minute);
        candidate.set(Calendar.SECOND, 0);
        candidate.set(Calendar.MILLISECOND, 0);

        if (candidate.getTimeInMillis() <= nowMs) {
            candidate.add(Calendar.DATE, 1); // 今天该时间点已过，排到明天
        }

        var candidateMs = candidate.getTimeInMillis();
        if (nearestMs == -1 || candidateMs < nearestMs) {
            nearestMs = candidateMs;
        }
    }

    return nearestMs - nowMs;
}

/** 安排下一次固定时间点的自动开启攻城 */
function scheduleAutoRestart() {
    restartTask = em.schedule("autoStart", getDelayToNextFixedTime());
}

/** 自动开启入口(到达固定时间点触发)：仅当当前没有进行中的攻城时才开启，随后排定下一个固定时间点 */
function autoStart() {
    if (!siegeActive) {
        startSiege();
    }
    scheduleAutoRestart();
}

/** 收集地图上的传送门(门口)坐标，仅取通往其他地图的传送门(MAP_PORTAL) */
function collectDoorPoints(map) {
    var points = [];
    for (var i = 0; i < 64; i++) {
        var portal = map.getPortal(i);
        if (portal == null) {
            break;
        }
        if (portal.getType() == Portal.MAP_PORTAL) {
            points.push(portal.getPosition());
        }
    }
    return points;
}

function randomInt(maxExclusive) {
    return Math.floor(Math.random() * maxExclusive);
}

/** 按概率与数量在指定坐标刷怪 */
function spawnMobBatch(map, point, mobPool, count) {
    if (mobPool.length == 0 || count <= 0) {
        return;
    }
    for (var i = 0; i < count; i++) {
        var mobId = mobPool[randomInt(mobPool.length)];
        var mob = LifeFactory.getMonster(mobId);
        if (mob != null) {
            map.spawnMonsterOnGroundBelow(mob, point);
        }
    }
}

/** 开启一次攻城活动 */
function startSiege() {
    var map = em.getChannelServer().getMapFactory().getMap(siegeMapId);
    doorPoints = collectDoorPoints(map);
    if (doorPoints.length == 0) {
        log.warn("[怪物攻城] 地图({})未找到可用传送门口，活动取消", siegeMapId);
        return;
    }

    siegeActive = true;
    currentWave = 0;

    broadcast("[怪物攻城] 怪物正在向 " + map.getMapName() + " 发起攻城！请前往支援，10分钟内清空所有怪物即可触发全服双倍经验与双倍掉率！");

    spawnWave();
    judgeTask = em.schedule("judgeResult", judgeDelayMs);
}

/** 刷新一波怪物 */
function spawnWave() {
    if (!siegeActive) {
        return;
    }

    currentWave++;
    var map = em.getChannelServer().getMapFactory().getMap(siegeMapId);

    var doorCount = doorsPerWaveMin + randomInt(doorsPerWaveMax - doorsPerWaveMin + 1);
    doorCount = Math.min(doorCount, doorPoints.length);

    var shuffled = doorPoints.slice().sort(function () { return Math.random() - 0.5; });
    for (var i = 0; i < doorCount; i++) {
        var point = shuffled[i];

        spawnMobBatch(map, point, normalMobPool, normalCountPerDoor);
        if (Math.random() < eliteSpawnChance) {
            spawnMobBatch(map, point, eliteMobPool, eliteCountPerDoor);
        }
        if (Math.random() < advancedSpawnChance) {
            spawnMobBatch(map, point, advancedMobPool, advancedCountPerDoor);
        }
    }

    log.info("[怪物攻城] 第 {} / {} 波怪物已刷新，本波出现在 {} 个门口", currentWave, totalWaves, doorCount);

    if (currentWave < totalWaves) {
        waveTask = em.schedule("spawnWave", waveIntervalMs);
    }
}

/** 判定攻城结果(开始后judgeDelayMs触发) */
function judgeResult() {
    if (!siegeActive) {
        return;
    }

    var map = em.getChannelServer().getMapFactory().getMap(siegeMapId);
    var success = map.getAllMonsters().isEmpty();

    if (success) {
        broadcast("[怪物攻城] 玩家们成功清空了攻城怪物！GM团队已开启紧急奖励：未来 " + (rewardBuffDurationMs / 60000) + " 分钟内全服双倍经验、双倍掉率！");
        applyRewardBuff();

        // 每日活跃-守护城镇：攻城判定成功时，给仍在攻城地图内的玩家各记1次"参与守城"进度
        // (此活动怪物直接刷在地图上、未走EventInstanceManager管理，没有逐人击杀统计，故采用"成功时刻仍在场"作为参与判定的近似方案)
        var present = map.getAllPlayers();
        for (var p = 0; p < present.size(); p++) {
            DailyActiveManager.addProgress(present.get(p).getId(), "city_defense", 1);
        }
    } else {
        map.killAllMonsters();
        broadcast("[怪物攻城] 很遗憾，未能在限定时间内清空攻城怪物，本次攻城以失败告终。残留怪物已清除。");
    }

    siegeActive = false;
    currentWave = 0;
}

/** 应用成功奖励：全服双倍经验/掉率，限时后自动恢复 */
function applyRewardBuff() {
    var world = em.getWorldServer();
    var worldId = em.getChannelServer().getWorld();

    // World没有暴露getExpRate()/getDropRate()读取接口，统一从GameConfig持久化配置读取当前基准倍率
    var currentExpRate = GameConfig.getWorldFloat(worldId, "exp_rate");
    var currentDropRate = GameConfig.getWorldFloat(worldId, "drop_rate");
    em.setProperty("originalExpRate", String(currentExpRate));
    em.setProperty("originalDropRate", String(currentDropRate));

    world.setExpRate(currentExpRate * 2);
    world.setDropRate(currentDropRate * 2);

    revertTask = em.schedule("revertRewardBuff", rewardBuffDurationMs);
}

/** 奖励到期，恢复原倍率 */
function revertRewardBuff() {
    var world = em.getWorldServer();

    var savedExpRate = em.getProperty("originalExpRate");
    var savedDropRate = em.getProperty("originalDropRate");

    if (savedExpRate != null && savedExpRate.length() > 0) {
        world.setExpRate(Number(savedExpRate));
    }
    if (savedDropRate != null && savedDropRate.length() > 0) {
        world.setDropRate(Number(savedDropRate));
    }

    broadcast("[怪物攻城] 攻城奖励的双倍经验、双倍掉率已结束，倍率恢复正常。");
}

/** 全服广播 */
function broadcast(msg) {
    em.getWorldServer().broadcastPacket(PacketCreator.serverNotice(6, msg));
}

// ---------- 预留函数(空实现) ----------

function dispose() {}

function setup(eim, leaderid) {}

function monsterValue(eim, mobid) { return 0; }

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
