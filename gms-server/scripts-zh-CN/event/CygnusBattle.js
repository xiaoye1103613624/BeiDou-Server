/*
    Cygnus Battle
*/

var isPq = true;
var minPlayers = 2, maxPlayers = 30;
var minLevel = 170, maxLevel = 255;
var entryMap = 271040100;
var exitMap = 271040000;
var recruitMap = 271040000;
var clearMap = 271040000;

var minMapId = 271040100;
var maxMapId = 271040110;

var eventTime = 120;

const maxLobbies = 1;

const GameConfig = Java.type('org.gms.config.GameConfig');
minPlayers = GameConfig.getServerBoolean("use_enable_solo_expeditions") ? 1 : minPlayers;  //如果解除远征队人数限制，则最低人数改为1人
if(GameConfig.getServerBoolean("use_enable_party_level_limit_lift")) {  //如果解除远征队等级限制，则最低170级，最高999级。
    minLevel = 170, maxLevel = 999;
}

function init() {
    setEventRequirements();
}

function getMaxLobbies() {
    return maxLobbies;
}

function setEventRequirements() {
    var reqStr = "";

    reqStr += "\r\n   组队人数: ";
    if (maxPlayers - minPlayers >= 1) {
        reqStr += minPlayers + " ~ " + maxPlayers;
    } else {
        reqStr += minPlayers;
    }

    reqStr += "\r\n   等级要求: ";
    if (maxLevel - minLevel >= 1) {
        reqStr += minLevel + " ~ " + maxLevel;
    } else {
        reqStr += minLevel;
    }

    reqStr += "\r\n   时间限制: ";
    reqStr += eventTime + " 分钟";

    em.setProperty("party", reqStr);
}

function setEventExclusives(eim) {
    eim.setExclusiveItems([]);
}

function setEventRewards(eim) {
    eim.setEventRewards(1, [], []);
    eim.setEventClearStageExp([]);
    eim.setEventClearStageMeso([]);
}

function afterSetup(eim) {}

function setup(channel) {
    var eim = em.newInstance("CygnusBattle" + channel);
    eim.setProperty("canJoin", 1);
    eim.setProperty("defeatedBoss", 0);

    var level = 1;
    var map = eim.getInstanceMap(entryMap);
    eim.getInstanceMap(271040110).resetPQ(level);
    map.resetPQ(level);
    spawnNext(map, 8850000);

    eim.startEventTimer(eventTime * 60000);
    setEventRewards(eim);
    setEventExclusives(eim);

    return eim;
}

function playerEntry(eim, player) {
    eim.dropMessage(5, "[远征队] " + player.getName() + " 已进入副本地图。");

    var map = eim.getMapInstance(entryMap);
    player.changeMap(map, map.getPortal(0));

    // 开启伤害记录
    if(GameConfig.getServerBoolean("damage_ranking")) {
        eim.startDamageRecording();
        player.dropMessage(6, "当前副本已开启伤害统计。");
    }
}

function scheduledTimeout(eim) {
    eim.broadcastDamageRanking();   // 时间结束时通报
    end(eim);
}

function changedMap(eim, player, mapid) {
    if (mapid < minMapId || mapid > maxMapId) {
        partyPlayersCheck(eim, player);
    }
}

function changedLeader(eim, leader) {}

function playerDead(eim, player) {}

function playerRevive(eim, player) {
    partyPlayersCheck(eim, player);
}

function playerDisconnected(eim, player) {
    partyPlayersCheck(eim, player);
}

function leftParty(eim, player) {}

function disbandParty(eim) {}

function monsterValue(eim, mobId) {
    return 1;
}

function playerUnregistered(eim, player) {}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 0);
}

function end(eim) {
    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++) {
        playerExit(eim, party.get(i));
    }
    eim.dispose();
}

function giveRandomEventReward(eim, player) {
    eim.giveEventReward(player);
}

function clearPQ(eim) {
    eim.stopEventTimer();
    eim.setEventCleared();
}

function monsterKilled(mob, eim) {
    var nextMobId = getNextMobId(mob.getId());
    if (nextMobId > 0) {
        spawnNext(mob.getMap(), nextMobId);
        return;
    }

    if (mob.getId() == 8850011) {
        eim.setIntProperty("defeatedBoss", 1);
        eim.showClearEffect(mob.getMap().getId());
        eim.broadcastDamageRanking();
        eim.clearPQ();
        eim.dispatchRaiseQuestMobCount(8850011, entryMap);
    }
}

function getNextMobId(mobId) {
    if (mobId == 8850000) {
        return 8850001;
    } else if (mobId == 8850001) {
        return 8850002;
    } else if (mobId == 8850002) {
        return 8850003;
    } else if (mobId == 8850003) {
        return 8850004;
    } else if (mobId == 8850004) {
        return 8850011;
    }
    return 0;
}

function spawnNext(map, mobId) {
    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    const Point = Java.type('java.awt.Point');
    map.spawnMonsterOnGroundBelow(LifeFactory.getMonster(mobId), new Point(-363, 100));
}

function allMonstersDead(eim) {}

function cancelSchedule() {}

function dispose(eim) {}

/**
 * 检测队伍人数是否满足最低人数要求
 * @param eim - 远征副本实例管理器
 * @param player - 触发事件的玩家对象
 */
function partyPlayersCheck(eim, player) {
    if (eim.isExpeditionTeamLackingNow(true, minPlayers, player)) {
        eim.unregisterPlayer(player);
        eim.dropMessage(5, "[远征队] 队长已退出远征或者队伍人数不足最低要求，无法继续。");
        end(eim);
        return false;
    } else {
        eim.dropMessage(5, "[远征队] " + player.getName() + " 已离开副本。");
        eim.unregisterPlayer(player);
        return true;
    }
}
