/**
 * 试炼之塔 - 北斗冒险岛Boss挑战副本
 * 功能: 从新移植的186版Boss中选择挑战, 击杀获得奖励
 * 入口NPC: 自定义 (可在自由市场放置)
 *
 * 设计思路: 简化高版本主题副本模式 → 083方式
 *  - 过场动画 → NPC对话
 *  - 独立地图 → EventInstanceManager实例
 *  - Boss机制 → 已有MobSkill系统
 *  - 任务链 → JS脚本驱动
 *
 * @author 萧曵
 * @date 2026-07-30
 */

var entryMap = 910000001;  // 试炼场地图 (可使用现有Boss地图或自定义)
var exitMap = 910000000;   // 自由市场
var eventTime = 30;        // 30分钟

// 试炼Boss列表 (从新移植的186版Boss中选择)
var BOSS_TIERS = {
    "初级试炼": {
        bossId: 8500005,   // 简单Boss
        bossName: "初级试炼Boss",
        reqLevel: 50,
        entryFee: 100000,
        rewardExp: 50000,
        rewards: [[4000313, 5], [2049100, 1]]  // [itemId, qty]
    },
    "中级试炼": {
        bossId: 8641010,
        bossName: "中级试炼Boss",
        reqLevel: 100,
        entryFee: 500000,
        rewardExp: 200000,
        rewards: [[4000313, 10], [2049100, 3], [2340000, 1]]
    },
    "高级试炼": {
        bossId: 8880120,
        bossName: "高级试炼Boss",
        reqLevel: 150,
        entryFee: 2000000,
        rewardExp: 800000,
        rewards: [[4000313, 20], [2049100, 5], [2340000, 3], [2049000, 1]]
    },
    "终极试炼": {
        bossId: 8850100,
        bossName: "终极试炼Boss",
        reqLevel: 200,
        entryFee: 10000000,
        rewardExp: 5000000,
        rewards: [[4000313, 50], [2049100, 10], [2340000, 5], [2049000, 3]]
    }
};

var currentSelection = null;

function init() {
    em.setProperty("noEntry", "false");
}

function setup(level, lobbyid) {
    var eim = em.newInstance("TrialTower_" + lobbyid);
    eim.setProperty("bossId", String(BOSS_TIERS[currentSelection].bossId));
    eim.setProperty("bossName", BOSS_TIERS[currentSelection].bossName);
    eim.setProperty("rewards", JSON.stringify(BOSS_TIERS[currentSelection].rewards));
    eim.setProperty("rewardExp", String(BOSS_TIERS[currentSelection].rewardExp));
    eim.setProperty("killed", "false");
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getInstanceMap(entryMap);
    var bossId = parseInt(eim.getProperty("bossId"));

    // 生成Boss
    map.spawnMonsterOnGroundBelow(
        Java.type('org.gms.server.life.LifeFactory').getMonster(bossId),
        new java.awt.Point(0, -42)
    );

    player.changeMap(entryMap, 0);
    em.setProperty("noEntry", "true");

    var PacketCreator = Java.type('org.gms.util.PacketCreator');
    player.sendPacket(PacketCreator.getClock(eventTime * 60));
    eim.startEventTimer(eventTime * 60000);

    // 公告Boss名称
    player.message("试炼开始! 击败 #r" + eim.getProperty("bossName") + "#k!");
}

function monsterKilled(mob, eim) {
    if (mob.getId() == parseInt(eim.getProperty("bossId"))) {
        eim.setProperty("killed", "true");

        // 奖励所有存活玩家
        var players = eim.getPlayers();
        var rewards = JSON.parse(eim.getProperty("rewards"));
        var expReward = parseInt(eim.getProperty("rewardExp"));

        for (var i = 0; i < players.size(); i++) {
            var p = players.get(i);
            if (p.isAlive()) {
                // 发放经验
                p.gainExp(expReward, true, true);
                // 发放道具
                for (var j = 0; j < rewards.length; j++) {
                    if (p.canHold(rewards[j][0])) {
                        p.gainItem(rewards[j][0], rewards[j][1]);
                    }
                }
                p.message("试炼通过! 获得奖励和 " + expReward.toLocaleString() + " 经验值!");
            }
        }

        // 5秒后传送出去
        eim.startEventTimer(5000);
    }
}

function scheduledTimeout(eim) {
    var players = eim.getPlayers();
    for (var i = 0; i < players.size(); i++) {
        var p = players.get(i);
        if (eim.getProperty("killed") == "true") {
            p.changeMap(exitMap, 0);
        } else {
            p.changeMap(exitMap, 0);
            p.message("时间到! 试炼失败...");
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

function playerUnregistered(eim, player) {}
function playerDisconnected(eim, player) {
    playerExit(eim, player);
}
function changedMap(eim, chr, mapid) {
    if (mapid != entryMap) {
        playerExit(eim, chr);
    }
}

// 清理
function clear(eim) {
    var players = eim.getPlayers();
    for (var i = 0; i < players.size(); i++) {
        players.get(i).changeMap(exitMap, 0);
    }
    eim.dispose();
    em.setProperty("noEntry", "false");
}

// 占位函数
function cancelSchedule() {}
function dispose() {}
function monsterValue(eim, mobid) { return 0; }
function disbandParty(eim, player) {}
function afterSetup(eim) {}
function changedLeader(eim, leader) {}
function leftParty(eim, player) {}
function clearPQ(eim) {}
function allMonstersDead(eim) {}
