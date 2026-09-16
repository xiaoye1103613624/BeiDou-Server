/**
 * 世界 Boss：扎昆之王
 * 定时开启入口；玩家经 NPC startInstance 进入；伤害排名发奖。
 */
var PacketCreator = Java.type("org.gms.util.PacketCreator");
var LifeFactory = Java.type("org.gms.server.life.LifeFactory");
var Point = Java.type("java.awt.Point");
var GameConfig = Java.type("org.gms.config.GameConfig");

var exitMap = 910001000;
var bossMapId = 280030001;
var bossMobId = 8800102;
var fightMinutes = 60;
var clearWaitMin = 2;
var maxLobbies = 30;

function init() {
    em.setProperty("bossName", "扎昆之王");
    em.setProperty("bossMap", "" + bossMapId);
    em.setProperty("bossMobId", "" + bossMobId);
    em.setProperty("fightMinutes", "" + fightMinutes);
    em.setProperty("portalOpen", "false");

    em.schedule("scheduleOpen", 21600000);
}

function getMaxLobbies() {
    return maxLobbies;
}

function scheduleOpen() {
    em.getChannelServer().broadcastPacket(
        PacketCreator.serverNotice(6, "[世界Boss] 扎昆之王将在1分钟后刷新！前往匠人街世界Boss入口！")
    );
    em.schedule("openPortal", 60000);
    em.schedule("scheduleOpen", 21660000);
}

function openPortal() {
    em.setProperty("portalOpen", "true");
    em.getChannelServer().broadcastPacket(
        PacketCreator.serverNotice(6, "[世界Boss] 扎昆之王已降临！通过匠人街NPC进入讨伐！")
    );
    em.schedule("closePortal", 3600000);
}

function closePortal() {
    em.setProperty("portalOpen", "false");
}

function setup(level, lobbyid) {
    var eim = em.newInstance("WorldBoss" + lobbyid);

    eim.setProperty("bossMap", "" + bossMapId);
    eim.setProperty("bossMobId", "" + bossMobId);
    eim.setProperty("cleared", "0");
    eim.setProperty("phase", "fight");
    eim.setProperty("lootStartMs", "0");

    var map = eim.getMapInstance(bossMapId);
    map.killAllMonsters();
    map.resetReactors();

    var mob = LifeFactory.getMonster(bossMobId);
    map.spawnMonsterOnGroundBelow(mob, new Point(-10, -204));
    eim.registerMonster(mob);

    if (GameConfig.getServerBoolean("damage_ranking")) {
        eim.startDamageRecording();
    } else {
        eim.forceStartDamageRecording();
    }

    eim.startEventTimer(fightMinutes * 60000);
    return eim;
}

function afterSetup(eim) {
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(bossMapId);
    player.changeMap(map, map.getPortal(0));
    player.dropMessage(5, "【世界Boss】已进入扎昆之王讨伐！造成伤害越高排名越靠前。");
}

function monsterValue(eim, mobId) {
    return 1;
}

function monsterKilled(mob, eim) {
    if (eim.getProperty("cleared") === "1") {
        return;
    }
    if (mob.getId() !== bossMobId) {
        return;
    }
    onBossCleared(eim);
}

function onBossCleared(eim) {
    eim.setProperty("cleared", "1");
    eim.setProperty("phase", "loot");
    eim.setProperty("lootStartMs", "" + java.lang.System.currentTimeMillis());
    eim.stopEventTimer();
    eim.setEventCleared();
    eim.showClearEffect();

    var ranking = eim.getDamageRankingList();
    var players = eim.getPlayers().iterator();
    while (players.hasNext()) {
        var player = players.next();
        if (player == null) {
            continue;
        }
        var rank = 99;
        var contribution = 0.0;
        for (var i = 0; i < ranking.size(); i++) {
            var row = ranking.get(i);
            if (row.get("characterId") === player.getId()) {
                rank = row.get("rank");
                contribution = row.get("contribution");
                break;
            }
        }
        giveReward(player, rank, contribution);
    }

    eim.broadcastDamageRanking();
    em.setProperty("portalOpen", "false");
    em.getChannelServer().broadcastPacket(
        PacketCreator.serverNotice(6, "[世界Boss] 扎昆之王已被击败！6小时后刷新。")
    );
    eim.dropMessage(5, "讨伐成功！奖励已发放，" + clearWaitMin + " 分钟后送回匠人街。");
    eim.restartEventTimer(clearWaitMin * 60000);
}

function giveReward(player, rank, contribution) {
    if (player == null) {
        return;
    }

    var mesoReward = 1000000;
    var mapleReward = 500;

    if (rank === 1) {
        mesoReward = 50000000;
        mapleReward = 10000;
        player.gainItem(4000313, 10);
        player.gainItem(4021017, 3);
        player.dropMessage(6, "世界Boss第一名！奖励已发放。");
    } else if (rank <= 3) {
        mesoReward = 20000000;
        mapleReward = 5000;
        player.gainItem(4000313, 5);
        player.gainItem(4021017, 1);
        player.dropMessage(6, "世界Boss排名前3！");
    } else if (rank <= 10) {
        mesoReward = 10000000;
        mapleReward = 2000;
        player.gainItem(4000313, 3);
        player.dropMessage(6, "世界Boss排名前10！");
    } else if (contribution >= 0.01) {
        mesoReward = 3000000;
        mapleReward = 800;
        player.gainItem(4000313, 5);
        player.dropMessage(6, "获得世界Boss参与奖励。");
    } else {
        player.dropMessage(6, "获得世界Boss参与奖励。");
    }

    player.gainMeso(mesoReward, true, true);
    try {
        player.getCashShop().gainCash(2, mapleReward);
    } catch (e) {
    }

    var pct = Math.round(contribution * 100);
    player.dropMessage(5, "[世界Boss] 排名#" + rank + " 贡献" + pct + "% 金币+" + mesoReward + " 抵用+" + mapleReward);
}

function scheduledTimeout(eim) {
    var phase = eim.getProperty("phase") || "fight";
    if (phase === "loot") {
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
    eim.dropMessage(5, "时间到，讨伐失败。");
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
    if (mapid !== bossMapId) {
        eim.unregisterPlayer(player);
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
        end(eim);
    } else {
        playerExit(eim, player);
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
