/**
 * 匠人街挑战副本 Event
 * 开战倒计时 + 通关后清场倒计时；地图实例隔离。
 *
 * 进场前由 NPC 写入 em 属性：
 * cfgMap / cfgMobs / cfgX / cfgY / cfgFight / cfgName / cfgSpecial / cfgReactor / cfgFlag
 */
var exitMap = 910001000;
var clearWaitMin = 2;
var maxLobbies = 50;

var ZAKUM_FLAG_QUEST = 9031002;

function init() {
}

function getMaxLobbies() {
    return maxLobbies;
}

function setup(level, lobbyid) {
    var eim = em.newInstance("ChallengeDungeon" + lobbyid);

    var mapId = parseInt(em.getProperty("cfgMap") || "0");
    var fightMin = parseInt(em.getProperty("cfgFight") || "15");
    if (mapId <= 0) {
        mapId = exitMap;
    }
    if (fightMin <= 0) {
        fightMin = 15;
    }

    eim.setProperty("bossMap", "" + mapId);
    eim.setProperty("mobs", em.getProperty("cfgMobs") || "");
    eim.setProperty("spawnX", em.getProperty("cfgX") || "0");
    eim.setProperty("spawnY", em.getProperty("cfgY") || "0");
    eim.setProperty("bossName", em.getProperty("cfgName") || "挑战Boss");
    eim.setProperty("special", em.getProperty("cfgSpecial") || "");
    eim.setProperty("reactorName", em.getProperty("cfgReactor") || "boss");
    eim.setProperty("flag", em.getProperty("cfgFlag") || "");
    eim.setProperty("cleared", "0");
    eim.setProperty("phase", "fight");
    eim.setProperty("fightMin", "" + fightMin);
    eim.setProperty("lootStartMs", "0");

    var map = eim.getMapInstance(mapId);
    map.killAllMonsters();
    map.resetReactors();

    eim.startEventTimer(fightMin * 60000);
    return eim;
}

function afterSetup(eim) {
}

function playerEntry(eim, player) {
    var mapId = parseInt(eim.getProperty("bossMap"));
    var map = eim.getMapInstance(mapId);
    player.changeMap(map, map.getPortal(0));

    var special = eim.getProperty("special");
    if (special === "reactor") {
        summonChaosZakum(eim, player, map);
    } else {
        spawnConfiguredBosses(eim, map);
    }

    var fightLeft = Math.floor(eim.getTimeLeft() / 60000);
    player.dropMessage(5, "【" + eim.getProperty("bossName") + "】开战！剩余时间约 " + fightLeft + " 分钟。");
}

function spawnConfiguredBosses(eim, map) {
    var mobs = (eim.getProperty("mobs") || "").split(",");
    var x = parseInt(eim.getProperty("spawnX") || "0");
    var y = parseInt(eim.getProperty("spawnY") || "0");
    for (var i = 0; i < mobs.length; i++) {
        var mobId = parseInt(mobs[i]);
        if (!mobId || mobId <= 0) {
            continue;
        }
        if (map.getMonsterById(mobId) != null) {
            continue;
        }
        map.spawnMonsterOnGroundBelow(mobId, x + (i * 120), y);
    }
}

function summonChaosZakum(eim, player, map) {
    var flag = eim.getProperty("flag") || "CHAOS_ZAKUM";
    var Quest = Java.type("org.gms.server.quest.Quest");
    player.getQuestNAdd(Quest.getInstance(ZAKUM_FLAG_QUEST)).setCustomData(flag);

    var reactorName = eim.getProperty("reactorName") || "boss";
    var reactor = map.getReactorByName(reactorName);
    if (reactor == null) {
        reactor = map.getReactorById(2111001);
    }
    if (reactor != null) {
        reactor.hitReactor(player.getClient());
    }

    var x = parseInt(eim.getProperty("spawnX") || "-10");
    var y = parseInt(eim.getProperty("spawnY") || "-204");
    if (map.getMonsterById(8800100) == null && map.getMonsterById(8800102) == null) {
        var LifeFactory = Java.type("org.gms.server.life.LifeFactory");
        var Point = Java.type("java.awt.Point");
        map.spawnFakeMonsterOnGroundBelow(LifeFactory.getMonster(8800100), new Point(x, y));
        for (var i = 8800103; i <= 8800109; i++) {
            map.spawnMonsterOnGroundBelow(i, x, y);
        }
        player.dropMessage(5, "祭坛已强制唤起混沌扎昆。");
        player.getQuestNAdd(Quest.getInstance(ZAKUM_FLAG_QUEST)).setCustomData("");
    }
}

function isTargetCleared(eim, map, killedMob) {
    var special = eim.getProperty("special");
    if (special === "reactor") {
        // 混沌扎昆：打掉本体 8800102 才算通关
        return killedMob != null && killedMob.getId() === 8800102;
    }

    var mobs = (eim.getProperty("mobs") || "").split(",");
    var hasTarget = false;
    for (var i = 0; i < mobs.length; i++) {
        var mobId = parseInt(mobs[i]);
        if (!mobId || mobId <= 0) {
            continue;
        }
        hasTarget = true;
        if (map.getMonsterById(mobId) != null) {
            return false;
        }
    }
    return hasTarget;
}

// EventInstanceManager.monsterKilled(chr,mob) always invokes this; missing → stack spam.
function monsterValue(eim, mobId) {
    return 1;
}

function monsterKilled(mob, eim) {
    try {
        if (eim.getProperty("cleared") === "1") {
            return;
        }
        var map = mob.getMap();
        if (isTargetCleared(eim, map, mob)) {
            clearPQ(eim);
        }
    } catch (err) {
    }
}

function clearPQ(eim) {
    if (eim.getProperty("cleared") === "1") {
        return;
    }
    // phase/lootStart first so a late fight-timer callback will not end() immediately.
    eim.setProperty("cleared", "1");
    eim.setProperty("phase", "loot");
    eim.setProperty("lootStartMs", "" + java.lang.System.currentTimeMillis());
    eim.setEventCleared();
    eim.showClearEffect();

    var bossName = eim.getProperty("bossName") || "Boss";
    var players = eim.getPlayers().iterator();
    while (players.hasNext()) {
        var p = players.next();
        if (p == null) continue;

        var fightMin = parseInt(eim.getProperty("fightMin") || "15");
        var baseMeso = fightMin >= 30 ? 5000000 : (fightMin >= 20 ? 3000000 : 1000000);
        p.gainMeso(baseMeso, true, true);

        var baseExp = fightMin >= 30 ? 500000 : (fightMin >= 20 ? 300000 : 150000);
        p.gainExp(baseExp, true, true);

        p.dropMessage(5, "【" + bossName + "】讨伐成功！获得金币+" + (baseMeso / 10000) + "W 经验+" + (baseExp / 10000) + "W");
    }

    eim.dropMessage(5, "挑战成功！请尽快拾取掉落，" + clearWaitMin + " 分钟后送回匠人街。");
    eim.restartEventTimer(clearWaitMin * 60000);
}

function scheduledTimeout(eim) {
    var phase = eim.getProperty("phase") || "fight";
    if (phase === "loot") {
        // Ignore stale fight-timer callbacks that fire after clear flipped phase to loot.
        var started = parseInt(eim.getProperty("lootStartMs") || "0");
        var minLootMs = clearWaitMin * 60000 - 3000;
        if (started > 0 && (java.lang.System.currentTimeMillis() - started) < minLootMs) {
            return;
        }
        end(eim);
        return;
    }
    if (eim.getProperty("cleared") === "1") {
        return;
    }
    eim.dropMessage(5, "时间到，挑战失败。");
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
    var bossMap = parseInt(eim.getProperty("bossMap"));
    if (mapid !== bossMap) {
        if (eim.isEventTeamLackingNow(true, 1, player)) {
            eim.unregisterPlayer(player);
            end(eim);
        } else {
            eim.unregisterPlayer(player);
        }
    }
}

function changedLeader(eim, leader) {
}

function playerDead(eim, player) {
}

function playerRevive(eim, player) {
    if (eim.isEventTeamLackingNow(true, 1, player)) {
        eim.unregisterPlayer(player);
        end(eim);
    } else {
        eim.unregisterPlayer(player);
    }
}

function playerDisconnected(eim, player) {
    if (eim.isEventTeamLackingNow(true, 1, player)) {
        eim.unregisterPlayer(player);
        end(eim);
    } else {
        eim.unregisterPlayer(player);
    }
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
        var player = iter.next();
        eim.unregisterPlayer(player);
        player.changeMap(exitMap, 0);
    }
    eim.dispose();
}
