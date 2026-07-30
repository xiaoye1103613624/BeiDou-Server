/**
 * 蘑菇城堡 - 北斗冒险岛简化主题副本
 * 功能: 守护城堡, 抵御怪物波次进攻
 * 模式: 单人或组队, 固定波次, 每5波1个Boss
 *
 * 简化思路: 高版本蘑菇城堡有复杂剧情线+多NPC交互
 * 083简化为 → 塔防式波次副本, 保留核心乐趣
 *
 * @author 萧曵
 * @date 2026-07-30
 */

var DEFENSE_MAP = 100000000;    // 防守地图
var EXIT_MAP = 100000000;       // 退出地图
var EVENT_TIME = 20;            // 20分钟

// 波次配置 [mobId, count, isBoss]
var WAVES = [
    { name: "第1波", mobs: [[2400011, 3]], boss: false },
    { name: "第2波", mobs: [[2400100, 5]], boss: false },
    { name: "第3波", mobs: [[2400200, 5], [2400201, 3]], boss: false },
    { name: "第4波", mobs: [[2400300, 8]], boss: false },
    { name: "第5波-Boss", mobs: [[8880120, 1]], boss: true },
    { name: "第6波", mobs: [[2400040, 8]], boss: false },
    { name: "第7波", mobs: [[2400048, 5], [2400300, 5]], boss: false },
    { name: "第8波", mobs: [[2400313, 10]], boss: false },
    { name: "第9波", mobs: [[2400330, 8]], boss: false },
    { name: "第10波-最终Boss", mobs: [[8850100, 1]], boss: true }
];

var REWARD_PER_WAVE = 10000;  // 每波经验
var BOSS_REWARD_EXP = 100000;

var currentWave = 0;
var waveKills = 0;
var waveTotalMobs = 0;

function init() {
    em.setProperty("noEntry", "false");
}

function setup(level, lobbyid) {
    var eim = em.newInstance("MushCastle_" + lobbyid);
    eim.setProperty("wave", "0");
    eim.setProperty("kills", "0");
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getInstanceMap(DEFENSE_MAP);
    player.changeMap(DEFENSE_MAP, 0);
    em.setProperty("noEntry", "true");

    eim.startEventTimer(EVENT_TIME * 60000); // 内部已包含时钟包

    player.message("[蘑菇城堡] 城堡防御战开始! 准备迎接怪物进攻!");
    startNextWave(eim);
}

function startNextWave(eim) {
    if (currentWave >= WAVES.length) {
        // 所有波次完成!
        var players = eim.getPlayers();
        for (var i = 0; i < players.size(); i++) {
            players.get(i).message("[蘑菇城堡] 全部波次击退! 城堡守住了!");
            players.get(i).gainExp(500000, true, true);
            players.get(i).gainItem(4000313, 30);
        }
        eim.startEventTimer(5000);
        return;
    }

    var wave = WAVES[currentWave];
    waveKills = 0;
    waveTotalMobs = 0;
    eim.setProperty("kills", "0");

    var map = eim.getInstanceMap(DEFENSE_MAP);
    if (map == null) return;

    for (var i = 0; i < wave.mobs.length; i++) {
        var mc = wave.mobs[i];
        for (var j = 0; j < mc[1]; j++) {
            // GraalJS兼容: spawnMonsterOnGroundBelow(int id, x, y) 自带创建
            map.spawnMonsterOnGroundBelow(mc[0],
                -150 + Math.floor(Math.random() * 300),
                -42
            );
            waveTotalMobs++;
        }
    }

    var players = eim.getPlayers();
    for (var i = 0; i < players.size(); i++) {
        players.get(i).message("[蘑菇城堡] " + wave.name + " 来了! " + (wave.boss ? "Boss!" : "准备战斗!"));
    }
}

function monsterKilled(mob, eim) {
    waveKills++;
    eim.setProperty("kills", String(waveKills));

    var wave = WAVES[currentWave];
    if (waveKills >= waveTotalMobs) {
        // 当前波次完成
        var players = eim.getPlayers();
        var expReward = wave.boss ? BOSS_REWARD_EXP : REWARD_PER_WAVE;

        for (var i = 0; i < players.size(); i++) {
            var p = players.get(i);
            if (p.isAlive()) {
                p.gainExp(expReward, true, true);
                p.message("[蘑菇城堡] " + wave.name + " 击退! +" + expReward.toLocaleString() + "经验");
            }
        }

        currentWave++;
        startNextWave(eim);
    }
}

function scheduledTimeout(eim) {
    var players = eim.getPlayers();
    for (var i = 0; i < players.size(); i++) {
        players.get(i).changeMap(EXIT_MAP, 0);
        players.get(i).message("[蘑菇城堡] 时间到! 城堡失守...");
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

function playerUnregistered(eim, player) {}
function playerDisconnected(eim, player) { playerExit(eim, player); }
function changedMap(eim, chr, mapid) {
    if (mapid != DEFENSE_MAP) { playerExit(eim, chr); }
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
