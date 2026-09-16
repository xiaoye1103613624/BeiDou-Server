/**
 * 北斗守城战 — 复用 GPQ 战场 990000500，三波刷怪，守住城门 HP。
 */
var exitMap = 910001000;
var battleMap = 990000500;
var fightMinutes = 30;
var clearWaitMin = 2;
var maxLobbies = 10;
var gateHpMax = 100;

var WAVES = [
    {name: "第1波", mobs: [[9400600, 5, 200, 180]]},
    {name: "第2波", mobs: [[9400601, 4, 200, 180], [9400602, 3, 400, 180]]},
    {name: "第3波", mobs: [[9400603, 2, 200, 180], [9400604, 1, 600, 180]]}
];

function init() {
}

function getMaxLobbies() {
    return maxLobbies;
}

function setup(level, lobbyid) {
    var eim = em.newInstance("CastleDefense" + lobbyid);
    eim.setProperty("bossMap", "" + battleMap);
    eim.setProperty("cleared", "0");
    eim.setProperty("phase", "fight");
    eim.setProperty("wave", "0");
    eim.setProperty("gateHp", "" + gateHpMax);
    eim.setProperty("lootStartMs", "0");

    var map = eim.getMapInstance(battleMap);
    map.killAllMonsters();
    map.resetReactors();

    spawnWave(eim, 0);
    eim.startEventTimer(fightMinutes * 60000);
    return eim;
}

function afterSetup(eim) {
}

function spawnWave(eim, waveIndex) {
    if (waveIndex >= WAVES.length) {
        onVictory(eim);
        return;
    }
    var wave = WAVES[waveIndex];
    eim.setProperty("wave", "" + waveIndex);
    eim.setProperty("waveTotal", "" + WAVES.length);
    eim.setProperty("wavePending", "" + countWaveMobs(wave));

    var map = eim.getMapInstance(battleMap);
    for (var i = 0; i < wave.mobs.length; i++) {
        var spec = wave.mobs[i];
        var mobId = spec[0];
        var qty = spec[1];
        var x = spec[2];
        var y = spec[3];
        for (var j = 0; j < qty; j++) {
            map.spawnMonsterOnGroundBelow(mobId, x + j * 80, y);
        }
    }
    eim.dropMessage(5, "【守城战】" + wave.name + " 来袭！守住城门（HP " + eim.getProperty("gateHp") + "/" + gateHpMax + "）");
}

function countWaveMobs(wave) {
    var n = 0;
    for (var i = 0; i < wave.mobs.length; i++) {
        n += wave.mobs[i][1];
    }
    return n;
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(battleMap);
    player.changeMap(map, map.getPortal(0));
    player.dropMessage(5, "【守城战】已加入防守！击败全部怪物守住城门。");
}

function monsterValue(eim, mobId) {
    return 1;
}

function monsterKilled(mob, eim) {
    if (eim.getProperty("cleared") === "1") {
        return;
    }
    var pending = parseInt(eim.getProperty("wavePending") || "0") - 1;
    eim.setProperty("wavePending", "" + pending);
    if (pending <= 0) {
        var nextWave = parseInt(eim.getProperty("wave") || "0") + 1;
        if (nextWave >= WAVES.length) {
            onVictory(eim);
        } else {
            spawnWave(eim, nextWave);
        }
    }
}

function onVictory(eim) {
    eim.setProperty("cleared", "1");
    eim.setProperty("phase", "loot");
    eim.setProperty("lootStartMs", "" + java.lang.System.currentTimeMillis());
    eim.stopEventTimer();
    eim.setEventCleared();
    eim.showClearEffect();

    var iter = eim.getPlayers().iterator();
    while (iter.hasNext()) {
        var p = iter.next();
        if (p == null) {
            continue;
        }
        p.gainMeso(2000000, true, true);
        p.gainItem(4002000, 5);
        p.gainItem(4002001, 5);
        p.gainItem(4032171, 2);
        p.dropMessage(6, "【守城战】防守成功！奖励已发放。");
    }
    eim.dropMessage(5, "守城成功！" + clearWaitMin + " 分钟后送回匠人街。");
    eim.restartEventTimer(clearWaitMin * 60000);
}

function scheduledTimeout(eim) {
    var phase = eim.getProperty("phase") || "fight";
    if (phase === "loot") {
        end(eim);
        return;
    }
    if (eim.getProperty("cleared") === "1") {
        return;
    }
    eim.dropMessage(5, "守城失败：时间到。");
    end(eim);
}

function playerUnregistered(eim, player) {
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 0);
}

function playerLeft(eim, player) {
    if (!eim.isEventCleared()) {
        playerExit(eim, player);
    }
}

function changedMap(eim, player, mapid) {
    if (mapid !== battleMap) {
        eim.unregisterPlayer(player);
    }
}

function changedLeader(eim, leader) {
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
    eim.unregisterPlayer(player);
}

function playerDisconnected(eim, player) {
    playerExit(eim, player);
}

function leftParty(eim, player) {
    playerExit(eim, player);
}

function disbandParty(eim) {
    if (!eim.isEventCleared()) {
        end(eim);
    }
}

function allMonstersDead(eim) {
}

function cancelSchedule(eim) {
}

function dispose(eim) {
}

function end(eim) {
    var iter = eim.getPlayers().iterator();
    while (iter.hasNext()) {
        var p = iter.next();
        eim.unregisterPlayer(p);
        p.changeMap(exitMap, 0);
    }
    eim.dispose();
}
