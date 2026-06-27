importPackage(Packages.client);
importPackage(Packages.client.inventory);
importPackage(Packages.database);

var setupTask;
var 小鱼 = Array(
    Array(4031627, 1),
    Array(4031628, 1),
    Array(4031630, 1),
    Array(4031631, 1)
);

var 全民奖励地图ID = 910000000;
var 全民抵用 = 1000;
var 全民奖励 = Array(
    Array(2022345, 1, "大力药水"),
    Array(5150040, 1, "皇家理发券"),
    Array(5152001, 1, "整形手术卡"),
    Array(4310148, 1, "升星币x1"),
    Array(4310148, 2, "升星币x2"),
    Array(4310148, 3, "升星币x3"),
    Array(2000016, 50, "白色药水x50"),
    Array(2000017, 50, "蓝色药水x50"),
    Array(2000017, 50, "蓝色药水x50"),
    Array(2000016, 50, "白色药水x50"),
    Array(2000015, 50, "橙色药水x50"),
    Array(2000018, 50, "活力药水x50")
);

var 天降宝物 = Array(
    Array(4000000, 1, "奖励"),
    Array(4000001, 1, "奖励"),
    Array(4000002, 1, "奖励"),
    Array(4000003, 1, "奖励")
);

var 简单悬赏需要物品 = [
    [4000000, 100, 4110000, 2],
    [4000001, 100, 4110000, 2],
    [4000002, 100, 4110000, 2],
    [4000003, 100, 4110000, 2],
    [4000004, 100, 4110000, 2],
    [4000005, 100, 4110000, 2],
    [4000006, 100, 4110000, 2]
];
var 困难悬赏需要物品 = [
    [4000000, 100, 4110000, 2],
    [4000001, 100, 4110000, 2],
    [4000002, 100, 4110000, 2],
    [4000003, 100, 4110000, 2],
    [4000004, 100, 4110000, 2],
    [4000005, 100, 4110000, 2],
    [4000006, 100, 4110000, 2]
];

var 怪物攻城地图 = [
    [100000000],
    [101000000],
    [102000000],
    [103000000]
];

function init() {
    em.setProperty("悬赏道具ID", "9999999");
    em.setProperty("悬赏道具数量", "9999999");
    em.setProperty("悬赏奖励ID", "9999999");
    em.setProperty("悬赏奖励数量", "9999999");
    em.setProperty("悬赏开关", "关");
    em.setProperty("悬赏是否完成", "是");
    scheduleNew();
}

function scheduleNew() {
    // 先取消旧任务，避免重复创建
    if (setupTask) {
        setupTask.cancel(false);
    }
    // 1分钟执行一次（100*60*1毫秒）
    setupTask = em.schedule("start", 100 * 60 * 1);
}

function cancelSchedule() {
    if (setupTask) {
        setupTask.cancel(false);
    }
}

function start() {
    // 重新调度下一次执行（保持原有定时逻辑）
    scheduleNew();
    
    var myDate = new Date();
    var year = myDate.getFullYear();
    var month = myDate.getMonth() + 1;
    var day = myDate.getDate();
    var hour = myDate.getHours();
    var Minutes = myDate.getMinutes();
    var weekday = new Array(7);
    weekday[0] = "星期天";
    weekday[1] = "星期一";
    weekday[2] = "星期二";
    weekday[3] = "星期三";
    weekday[4] = "星期四";
    weekday[5] = "星期五";
    weekday[6] = "星期六";

    // 全服公告区域
    if ((hour == 11 && Minutes == 55)) {
        em.broadcastServerMsg(5121010, "[干饭福利]五分钟后自由市场发放全员午饭福利.请赶紧到达!注意查看背包!", true);
    } else if (hour == 17 && Minutes == 55) {
        em.broadcastServerMsg(5121010, "[干饭福利]五分钟后自由市场发放全员晚饭福利.请赶紧到达!注意查看背包!", true);
    } else if (hour == 19 && Minutes == 55) {
        em.broadcastServerMsg(5121010, "[怪物攻城活动]五分钟后射手村/魔法密林/勇士部落/废弃都市会出现大量怪物开始攻城,请用普通攻击!", true);
    } else if (hour == 21 && Minutes == 00) {
        em.broadcastServerMsg(5121010, "[地图刷新]系统将在每晚21:00清理怪物攻城地图的怪物。", true);
        refreshMaps();
    }

    // 仅1频道执行怪物生成逻辑
    if (em.getChannelServer().getId() == 1) {
        // 怪物攻城（20:00）
        if (hour == 20 && Minutes == 0) {
            em.broadcastYellowMsg("[怪物攻城活动]射手村/魔法密林/勇士部落/废弃都市出现大量怪物开始攻城,请用普通攻击!");
            for (var i = 0; i < 2; i++) {
                makeMonster(em, 怪物攻城地图[0][0], 9400710, 5);
                makeMonster(em, 怪物攻城地图[1][0], 9400710, 5);
                makeMonster(em, 怪物攻城地图[2][0], 9400710, 5);
                makeMonster(em, 怪物攻城地图[3][0], 9400710, 5);
            }
        }
    }
}

function rand(a, b) {
    var re = ~~(Math.random() * (b - a)) + a;
    return re;
}

function refreshMaps() {
    for (var i = 0; i < 怪物攻城地图.length; i++) {
        var mapId = 怪物攻城地图[i][0];
        var map = em.getMapFactory().getMap(mapId);
        if (map) {
            map.killAllMonsters(false);
            map.resetFully();
        }
    }
}

function 召唤怪物(em, 地图, 怪物, 血量) {
    var mapid = 地图;
    var map = em.getMapFactory().getMap(mapid);
    var mob = em.getMonster(怪物);
    var modified = em.newMonsterStats();
    modified.setOHp(血量);
    modified.setOMp(mob.getMobMaxMp());
    mob.setOverrideStats(modified);
    map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(-311, -18));
}

function makeMonster(em, 地图, 怪物, 血量) {
    // 增加参数校验，避免空参数导致异常
    if (!em || !地图 || !怪物 || !血量) {
        print("makeMonster参数不全，跳过生成");
        return;
    }
    
    var mapid = 地图;
    var map = em.getMapFactory().getMap(mapid);
    var mob = em.getMonster(怪物);
    var modified = em.newMonsterStats();
    modified.setOHp(血量);
    
    var mapPortals = map.getPortals();
    var mpit = mapPortals.iterator();
    var mp = Math.floor(Math.random() * mapPortals.size());
    var i = 0;
    var portal = null;
    
    // 正确获取随机传送门
    while (mpit.hasNext()) {
        portal = mpit.next();
        if (mp == i) break;
        i++;
    }
    
    // 确保传送门存在
    if (!portal) {
        print("地图" + mapid + "未找到有效传送门，使用默认坐标");
        portal = map.getPortal(0); // 获取第一个传送门
        if (!portal) {
            return; // 无传送门则放弃生成
        }
    }
    
    var point = portal.getPosition();
    try {
        map.spawnMonsterOnGroundBelow(mob, point);
    } catch (e) {
        // 修复：移除无限递归，改为打印错误日志
        print("生成怪物失败：地图" + mapid + "，怪物" + 怪物 + "，错误：" + e);
    }
}