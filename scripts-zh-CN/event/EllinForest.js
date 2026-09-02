/**
 * 艾琳森林 - 北斗冒险岛简化主题副本
 * 功能: 用083方式实现艾琳森林剧情副本
 *  - 过场动画 → NPC对话链条
 *  - 副本实例 → EventInstanceManager
 *  - 怪物波次 → 计时器驱动
 *  - Boss战 → 击杀检测+奖励
 * 入口: 魔法密林 → 艾琳森林入口NPC
 *
 * 原始版本: v083艾琳森林 (Ellin Forest PQ)
 * 简化实现: 保留核心流程, 去除过场/复杂机制
 *
 * @author 萧曵
 * @date 2026-07-30
 */

var ENTRY_MAP = 101000000;  // 入口地图(魔法密林)
var STAGE_MAPS = [           // 副本关卡地图
    300010000,   // 第1关: 森林入口
    300010100,   // 第2关: 毒藤区
    300010200,   // 第3关: 精灵废墟
];
var BOSS_MAP = 300010300;    // Boss关卡
var EXIT_MAP = 101000000;    // 出口(魔法密林)
var EVENT_TIME = 30;         // 30分钟限制

// 关卡怪物配置 (使用新移植的186怪物)
var STAGES = [
    {
        name: "森林入口",
        mobs: [{id: 2400011, count: 5}, {id: 2400100, count: 5}],
        killTarget: 8,
        message: "清除森林入口的怪物!"
    },
    {
        name: "毒藤区",
        mobs: [{id: 2400300, count: 8}],
        killTarget: 6,
        message: "消灭毒藤区的变异植物!"
    },
    {
        name: "精灵废墟",
        mobs: [{id: 2400200, count: 3}, {id: 2400201, count: 5}],
        killTarget: 7,
        message: "清理精灵废墟的入侵者!"
    }
];

var BOSS_ID = 8500005;        // Boss怪物ID
var BOSS_NAME = "森林守护者";

// 奖励配置
var REWARDS = {
    exp: 100000,
    items: [[4000313, 10], [2049100, 3], [2340000, 2]]
};

var currentStage = 0;
var kills = 0;
var stageMobs = [];

function init() {
    em.setProperty("noEntry", "false");
}

function setup(level, lobbyid) {
    var eim = em.newInstance("EllinForest_" + lobbyid);
    eim.setProperty("stage", "0");
    eim.setProperty("kills", "0");
    eim.setProperty("bossKilled", "false");
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getInstanceMap(STAGE_MAPS[0]);
    player.changeMap(STAGE_MAPS[0], 0);
    em.setProperty("noEntry", "true");

    eim.startEventTimer(EVENT_TIME * 60000); // 内部已包含时钟包

    // 开始第1关
    spawnStage(eim, 0);
}

function spawnStage(eim, stageIndex) {
    var map = eim.getInstanceMap(STAGE_MAPS[stageIndex]);
    if (map == null) return;

    var stage = STAGES[stageIndex];
    eim.setProperty("stage", String(stageIndex));
    eim.setProperty("kills", "0");
    stageMobs = [];

    for (var i = 0; i < stage.mobs.length; i++) {
        var mobConfig = stage.mobs[i];
        for (var j = 0; j < mobConfig.count; j++) {
            // GraalJS兼容: 直接使用int ID, 不调Java.type()
            map.spawnMonsterOnGroundBelow(mobConfig.id,
                -200 + Math.floor(Math.random() * 400),
                -42
            );
            stageMobs.push(mobConfig.id);
        }
    }

    var players = eim.getPlayers();
    for (var i = 0; i < players.size(); i++) {
        players.get(i).message("[艾琳森林] " + stage.message + " (" + stage.killTarget + "只)");
    }
}

function monsterKilled(mob, eim) {
    var stage = parseInt(eim.getProperty("stage"));
    var currentKills = parseInt(eim.getProperty("kills")) + 1;
    eim.setProperty("kills", String(currentKills));

    // 检查是否是Boss
    if (mob.getId() == BOSS_ID) {
        eim.setProperty("bossKilled", "true");
        giveRewards(eim);
        var players = eim.getPlayers();
        for (var i = 0; i < players.size(); i++) {
            players.get(i).message("[艾琳森林] Boss " + BOSS_NAME + " 已被击败! 10秒后传送...");
        }
        eim.startEventTimer(10000);  // 10秒后退出
        return;
    }

    // 关卡进度
    var killTarget = STAGES[stage].killTarget;
    var players = eim.getPlayers();

    if (currentKills >= killTarget) {
        if (stage < STAGES.length - 1) {
            // 下一关
            var nextStage = stage + 1;
            for (var i = 0; i < players.size(); i++) {
                players.get(i).changeMap(STAGE_MAPS[nextStage], 0);
                players.get(i).message("[艾琳森林] 进入 " + STAGES[nextStage].name + "!");
            }
            spawnStage(eim, nextStage);
        } else {
            // 所有关卡完成, 刷Boss
            for (var i = 0; i < players.size(); i++) {
                players.get(i).changeMap(BOSS_MAP, 0);
                players.get(i).message("[艾琳森林] 最终Boss " + BOSS_NAME + " 出现了!");
            }
            var bossMap = eim.getInstanceMap(BOSS_MAP);
            if (bossMap != null) {
                // GraalJS兼容: spawnMonsterOnGroundBelow(int id, x, y)
                bossMap.spawnMonsterOnGroundBelow(BOSS_ID, 0, -42);
            }
        }
    } else {
        // 进度提示
        for (var i = 0; i < players.size(); i++) {
            players.get(i).message("[艾琳森林] 进度: " + currentKills + "/" + killTarget);
        }
    }
}

function giveRewards(eim) {
    var players = eim.getPlayers();
    for (var i = 0; i < players.size(); i++) {
        var p = players.get(i);
        if (p.isAlive()) {
            p.gainExp(REWARDS.exp, true, true);
            for (var j = 0; j < REWARDS.items.length; j++) {
                if (p.canHold(REWARDS.items[j][0])) {
                    p.gainItem(REWARDS.items[j][0], REWARDS.items[j][1]);
                }
            }
            p.message("[艾琳森林] 副本通关! 获得奖励!");
        }
    }
}

function scheduledTimeout(eim) {
    var players = eim.getPlayers();
    var bossKilled = eim.getProperty("bossKilled") == "true";

    for (var i = 0; i < players.size(); i++) {
        players.get(i).changeMap(EXIT_MAP, 0);
        if (!bossKilled) {
            players.get(i).message("[艾琳森林] 副本超时...");
        }
    }
    eim.dispose();
    em.setProperty("noEntry", "false");
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    if (eim.getPlayers().size() == 0) {
        eim.dispose();
        em.setProperty("noEntry", "false");
    }
}

// 模板函数
function playerUnregistered(eim, player) {}
function playerDisconnected(eim, player) { playerExit(eim, player); }
function changedMap(eim, chr, mapid) {
    var stage = parseInt(eim.getProperty("stage"));
    var validMaps = STAGE_MAPS.slice(0, stage + 1).concat([BOSS_MAP]);
    if (validMaps.indexOf(mapid) == -1) {
        playerExit(eim, chr);
    }
}
function clear(eim) {
    var players = eim.getPlayers();
    for (var i = 0; i < players.size(); i++) {
        players.get(i).changeMap(EXIT_MAP, 0);
    }
    eim.dispose();
    em.setProperty("noEntry", "false");
}
function cancelSchedule() {}
function dispose() {}
function monsterValue(eim, mobid) { return 0; }
function disbandParty(eim, player) {}
function afterSetup(eim) {}
function changedLeader(eim, leader) {}
function leftParty(eim, player) {}
function clearPQ(eim) {}
function allMonstersDead(eim) {}
