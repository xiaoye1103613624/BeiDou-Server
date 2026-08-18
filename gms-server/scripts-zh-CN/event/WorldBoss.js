// 世界Boss事件：扎昆之王
// 定时开启(每6小时)，全服玩家共同讨伐，贡献排名奖励

var PacketCreator = Java.type('org.gms.util.PacketCreator');
var LifeFactory = Java.type('org.gms.server.life.LifeFactory');
var Point = Java.type('java.awt.Point');

function init() {
    em.setProperty("bossName", "扎昆之王");
    em.setProperty("bossMap", "280030001");
    em.setProperty("bossMobId", "8800102");
    em.setProperty("fightMinutes", "60");
    em.setProperty("portalOpen", "false");

    // 启动后每6小时发预告，1分钟后开启
    em.schedule("scheduleOpen", 21600000); // 6小时
}

function scheduleOpen() {
    em.getChannelServer().broadcastPacket(
        PacketCreator.serverNotice(6, "[世界Boss] 扎昆之王将在1分钟后刷新！前往匠人街世界Boss入口！")
    );
    em.schedule("openPortal", 60000); // 1分钟后开启
    em.schedule("scheduleOpen", 21660000); // 6小时+1分钟后再次预告
}

function openPortal() {
    em.setProperty("portalOpen", "true");
    em.getChannelServer().broadcastPacket(
        PacketCreator.serverNotice(6, "[世界Boss] ⚔ 扎昆之王已降临！通过匠人街NPC进入讨伐！")
    );
    em.schedule("closePortal", 3600000); // 1小时后关闭入口
}

function closePortal() {
    em.setProperty("portalOpen", "false");
}

function setup(eim, leaderId) {
    var mapId = parseInt(em.getProperty("bossMap"));
    var map = em.getMapFactory().getMap(mapId);
    map.killAllMonsters();
    map.resetReactors();

    eim.setProperty("bossMap", "" + mapId);
    eim.setProperty("participants", ""); // 格式: id:name:damage|id:name:damage
    eim.setProperty("cleared", "0");

    // 召唤Boss
    var mob = LifeFactory.getMonster(8800102);
    map.spawnMonsterOnGroundBelow(mob, new Point(-10, -204));
    eim.registerMonster(mob);

    // 设置事件定时器
    var fightMin = parseInt(em.getProperty("fightMinutes") || "60");
    eim.startEventTimer(fightMin * 60000);
    return eim;
}

function playerEntry(eim, player) {
    var mapId = parseInt(eim.getProperty("bossMap"));
    var map = eim.getMapInstance(mapId);
    player.changeMap(map, map.getPortal(0));

    // 记录参与者
    var parts = eim.getProperty("participants") || "";
    if (parts.length > 0) parts += "|";
    parts += player.getId() + ":" + player.getName() + ":0";
    eim.setProperty("participants", parts);

    player.dropMessage(5, "【世界Boss】已进入扎昆之王讨伐！造成伤害越高排名越靠前。");
}

function addParticipantDamage(eim, playerId, damage) {
    var parts = (eim.getProperty("participants") || "").split("\\|");
    var newParts = [];
    for (var i = 0; i < parts.length; i++) {
        var seg = parts[i].split(":");
        if (seg.length >= 3 && seg[0] === ("" + playerId)) {
            var oldDmg = parseInt(seg[2]) || 0;
            seg[2] = "" + (oldDmg + damage);
        }
        newParts.push(seg.join(":"));
    }
    eim.setProperty("participants", newParts.join("|"));
}

function monsterValue(eim, mobId) {
    return 1;
}

function monsterKilled(mob, eim) {
    if (eim.getProperty("cleared") === "1") return;
    eim.setProperty("cleared", "1");
    eim.stopEventTimer();
    eim.setEventCleared();
    eim.showClearEffect();

    // 解析参与者排名
    var partsStr = eim.getProperty("participants") || "";
    var parts = partsStr.split("\\|");
    var entries = [];
    for (var i = 0; i < parts.length; i++) {
        var seg = parts[i].split(":");
        if (seg.length >= 3 && seg[0].length > 0) {
            entries.push({
                id: seg[0],
                name: seg[1],
                damage: parseInt(seg[2]) || 0
            });
        }
    }
    // 按伤害排序
    entries.sort(function(a, b) { return b.damage - a.damage; });

    var totalDamage = 0;
    for (var i = 0; i < entries.length; i++) totalDamage += entries[i].damage;

    // 遍历地图内玩家发放奖励
    var mapId = parseInt(eim.getProperty("bossMap"));
    var map = eim.getMapInstance(mapId);
    var players = map.getAllPlayers();
    for (var i = 0; i < players.size(); i++) {
        var player = players.get(i);
        var rank = 99;
        var contribution = 0;
        for (var j = 0; j < entries.length; j++) {
            if (entries[j].id === ("" + player.getId())) {
                rank = j + 1;
                if (totalDamage > 0) contribution = entries[j].damage / totalDamage;
                break;
            }
        }
        giveReward(player, rank, contribution);
    }

    // 全服公告
    em.getChannelServer().broadcastPacket(
        PacketCreator.serverNotice(6, "[世界Boss] 🎉 扎昆之王已被击败！6小时后刷新。")
    );

    em.setProperty("portalOpen", "false");
    eim.dropMessage(5, "讨伐成功！奖励已发放，" + 2 + " 分钟后送回匠人街。");

    // 2分钟后传送所有玩家回城
    eim.startEventTimer(120000);
}

function giveReward(player, rank, contribution) {
    if (player == null) return;

    var mesoReward = 1000000;
    var mapleReward = 500;
    var itemId = 0;
    var itemQty = 0;

    if (rank === 1) {
        mesoReward = 50000000; mapleReward = 10000;
        player.gainItem(4000314, 10);
        player.gainItem(4021017, 3);
        player.dropMessage(6, "🏆 世界Boss第一名！奖励已发放。");
    } else if (rank <= 3) {
        mesoReward = 20000000; mapleReward = 5000;
        player.gainItem(4000314, 5);
        player.gainItem(4021017, 1);
        player.dropMessage(6, "🥈 世界Boss排名前3！");
    } else if (rank <= 10) {
        mesoReward = 10000000; mapleReward = 2000;
        player.gainItem(4000314, 3);
        player.dropMessage(6, "🥉 世界Boss排名前10！");
    } else if (contribution >= 0.01) {
        mesoReward = 3000000; mapleReward = 800;
        player.gainItem(4000313, 5);
    } else {
        player.dropMessage(6, "获得世界Boss参与奖励。");
    }

    player.gainMeso(mesoReward, true, true);
    try {
        player.getCashShop().gainCash(1, mapleReward);
    } catch (e) {}

    var pct = Math.round(contribution * 100);
    player.dropMessage(5, "[世界Boss] 排名#" + rank + " 贡献" + pct + "% 金币+" + mesoReward + " 抵用+" + mapleReward);
}

// ==================== 标准事件回调 ====================

function afterSetup(eim) {}

function scheduledTimeout(eim) {
    if (eim.getProperty("cleared") === "1") {
        end(eim);
        return;
    }
    eim.dropMessage(5, "时间到，讨伐失败。");
    end(eim);
}

function playerUnregistered(eim, player) {}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(910001000, 0);
}

function playerLeft(eim, player) {
    if (!eim.isEventCleared()) {
        playerExit(eim, player);
    }
}

function changedMap(eim, player, mapid) {
    var bossMap = parseInt(eim.getProperty("bossMap"));
    if (mapid !== bossMap) {
        eim.unregisterPlayer(player);
    }
}

function changedLeader(eim, leader) {}

function playerDead(eim, player) {}

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

function allMonstersDead(eim) {}

function cancelSchedule() {}

function dispose(eim) {}

function end(eim) {
    var mapId = parseInt(eim.getProperty("bossMap"));
    try {
        var map = eim.getMapInstance(mapId);
        var players = map.getAllPlayers();
        for (var i = 0; i < players.size(); i++) {
            var p = players.get(i);
            eim.unregisterPlayer(p);
            p.changeMap(910001000, 0);
        }
    } catch (e2) {}
    eim.dispose();
}
