/*
    LNHXBOSS - 列娜海峡 Boss 副本事件
    基于 Cygnus Battle 模板改写
*/

var isPq = true;
var minPlayers = 1, maxPlayers = 1;
var minLevel = 30, maxLevel = 255;
var entryMap = 141050300;
var exitMap = 141050200;
var recruitMap = 141050200;
var clearMap = 141050200;

var minMapId = 141050300;
var maxMapId = 141050300;

var eventTime = 10;

const maxLobbies = 1;

var bossMobId = 3502008;
var completeQuestId = 32126;

function log(msg) {
    java.lang.System.out.println("[LNHXBOSS] " + msg);
}

function init() {
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
    setEventRequirements();
    log("init() 完成");
}

function getMaxLobbies() {
    return maxLobbies;
}

function setEventRequirements() {
    var reqStr = "";

    reqStr += "\r\n   组队人数: ";
    reqStr += minPlayers;

    reqStr += "\r\n   等级要求: ";
    reqStr += minLevel + " ~ " + maxLevel;

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
    log("setup() channel=" + channel);

    var eim = em.newInstance("LNHXBOSS" + channel);
    eim.setProperty("canJoin", 1);
    eim.setProperty("defeatedBoss", 0);

    em.setProperty("state", 1);

    var level = 1;
    var map = eim.getInstanceMap(entryMap);
    map.resetPQ(level);

    spawnBoss(map);

    eim.startEventTimer(eventTime * 60000);
    setEventRewards(eim);
    setEventExclusives(eim);

    log("setup() 完成, Boss已生成, 计时器已启动");
    return eim;
}

function spawnBoss(map) {
    const LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    const Point = Java.type('java.awt.Point');
    var mob = LifeFactory.getMonster(bossMobId);
    if (mob != null) {
        map.spawnMonsterOnGroundBelow(mob, new Point(0, 100));
        log("spawnBoss() Boss " + bossMobId + " 已生成");
    } else {
        log("spawnBoss() ★ 无法生成Boss " + bossMobId + " — getMonster 返回 null");
    }
}

function playerEntry(eim, player) {
    log("playerEntry() player=" + player.getName());
    eim.dropMessage(5, "[列娜海峡] " + player.getName() + " 已进入Boss副本。");

    var map = eim.getMapInstance(entryMap);
    player.changeMap(map, map.getPortal(0));
}

function scheduledTimeout(eim) {
    log("scheduledTimeout()");
    end(eim);
}

function changedMap(eim, player, mapid) {
    log("changedMap() player=" + player.getName() + " mapid=" + mapid);
    if (mapid < minMapId || mapid > maxMapId) {
        playerExit(eim, player);
        end(eim);
    }
}

function changedLeader(eim, leader) {}

function playerDead(eim, player) {}

function playerRevive(eim, player) {
    return false;
}

function playerDisconnected(eim, player) {
    log("playerDisconnected() player=" + player.getName());
    playerExit(eim, player);
    end(eim);
    return 0;
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
    log("end()");

    var party = eim.getPlayers();
    for (var i = 0; i < party.size(); i++) {
        try {
            playerExit(eim, party.get(i));
        } catch (e) {
            try { party.get(i).changeMap(exitMap, 0); } catch (e2) {}
        }
    }

    em.setProperty("state", "0");
    eim.dispose();
    log("end() 完成, state已重置为0");
}

function giveRandomEventReward(eim, player) {
    eim.giveEventReward(player);
}

function clearPQ(eim) {
    log("clearPQ()");
    eim.stopEventTimer();
    eim.setEventCleared();
}

function monsterKilled(mob, eim) {
    log("monsterKilled() mobId=" + mob.getId() + " bossMobId=" + bossMobId);

    if (mob.getId() == bossMobId) {
        eim.setIntProperty("defeatedBoss", 1);
        eim.showClearEffect(mob.getMap().getId());

        var players = eim.getPlayers();
        for (var i = 0; i < players.size(); i++) {
            var p = players.get(i);
            try {
                p.forceCompleteQuest(completeQuestId);
                log("monsterKilled() " + p.getName() + " 任务 " + completeQuestId + " 完成");
            } catch (e) {
                log("monsterKilled() forceCompleteQuest 失败: " + e);
            }
            try {
                p.dropMessage(5, "Boss已被击败！");
            } catch (e) {}
        }

        eim.clearPQ();
        log("monsterKilled() Boss已击败, 事件通关");
    }
}

function allMonstersDead(eim) {}

function cancelSchedule() {}

function dispose(eim) {
    log("dispose()");
}
