/* LNHXMLDNL - 列娜海峡·迷路的诺拉 */
/* 击杀30只怪物通关，10分钟限时 */

// ═══════════════════ 事件实例化变量 ═══════════════════
var isPq = true;
var minPlayers = 1, maxPlayers = 6;
var minLevel = 30, maxLevel = 255;
var entryMap = 141010400;       // 事件地图（击杀地图）
var exitMap = 141060000;        // 退场地图（准备地图）
var recruitMap = exitMap;       // 玩家必须在此地图上才能开始此事件
var clearMap = exitMap;         // 通关后传送地图

var mapIds = [entryMap];        // 事件涉及的地图ID列表
var prepareMap = exitMap;       // 准备地图

var minMapId = entryMap;
var maxMapId = entryMap;

var eventTime = 10;             // 事件时间（分钟）
var killTarget = 30;            // 通关所需击杀怪物数量
var completeQuestId = 32170;    // 通关时完成的任务ID ← 请确认

const maxLobbies = 1;

// ═══════════════════ 日志 ═══════════════════
function log(msg) {
    java.lang.System.out.println("[LNHXMLDNL] " + msg);
}

function logErr(msg, err) {
    java.lang.System.out.println("[LNHXMLDNL] ERROR: " + msg);
    if (err) {
        java.lang.System.out.println("[LNHXMLDNL] " + err.toString());
        if (err.stack) java.lang.System.out.println("[LNHXMLDNL] " + err.stack);
    }
}

// ═══════════════════ 事件配置 ═══════════════════
function init() {
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
    setEventRequirements();
    log("init() 被调用 — state/leader 已初始化");
}

function getMaxLobbies() {
    return maxLobbies;
}

function setEventRequirements() {
    var reqStr = "";
    reqStr += "\r\n   组队人数: ";
    reqStr += (maxPlayers - minPlayers >= 1) ? (minPlayers + " ~ " + maxPlayers) : minPlayers;
    reqStr += "\r\n   等级要求: ";
    reqStr += (maxLevel - minLevel >= 1) ? (minLevel + " ~ " + maxLevel) : minLevel;
    reqStr += "\r\n   时间限制: " + eventTime + " 分钟";
    reqStr += "\r\n   击杀目标: " + killTarget + " 只怪物";
    em.setProperty("party", reqStr);
}

function getEligibleParty(party) {
    var eligible = [];
    var hasLeader = false;

    if (party != null && party.size() > 0) {
        var partyList = party.toArray();
        for (var i = 0; i < party.size(); i++) {
            var ch = partyList[i];
            if (ch.getMapId() == recruitMap && ch.getLevel() >= minLevel && ch.getLevel() <= maxLevel) {
                if (ch.isLeader()) hasLeader = true;
                eligible.push(ch);
            }
        }
    }

    if (!(hasLeader && eligible.length >= minPlayers && eligible.length <= maxPlayers)) {
        eligible = [];
    }
    return Java.to(eligible, Java.type('org.gms.net.server.world.PartyCharacter[]'));
}

// ═══════════════════ setup() — 创建事件实例 ═══════════════════
function setup(level, lobbyid) {
    log("setup() level=" + level + " lobbyid=" + lobbyid);

    var eim = em.newInstance("LNHXMLDNL");
    log("setup() eim=" + eim);

    em.setProperty("state", 1);
    em.setProperty("leader", "true");

    try {
        // 初始化击杀计数（必须在地图操作之前，防止 killAllMonsters 回调污染）
        em.setProperty("kill", 0);
        log("setup() 击杀计数已重置为0");

        // setInstanceMap 创建全新实例地图，从XML加载怪物刷新点
        var map = eim.setInstanceMap(entryMap);
        log("setup() setInstanceMap(" + entryMap + ") → map=" + map);

        map.resetFully();
        log("setup() resetFully() 完成");

        try {
            map.killAllMonsters(false);
            log("setup() killAllMonsters 完成");
        } catch (e1) {
            logErr("killAllMonsters() 失败，跳过", e1);
        }

        // 重置计数器（可能被 killAllMonsters 回调污染）
        em.setProperty("kill", 0);

        try {
            var mobCount = map.getMonsterCount();
            log("setup() 当前地图怪物数量=" + mobCount);
        } catch (mcErr) {
            logErr("获取怪物数量失败", mcErr);
        }

        eim.startEventTimer(eventTime * 60000);
        log("setup() 计时器已启动, " + eventTime + "分钟");

    } catch (err) {
        logErr("setup() 执行中发生异常", err);
    }

    log("setup() 完成, 返回eim");
    return eim;
}

// ═══════════════════ playerEntry() — 玩家进入 ═══════════════════
function playerEntry(eim, player) {
    log("playerEntry() player=" + player.getName() + " map=" + player.getMapId());

    var map = eim.getMapInstance(entryMap);
    if (map == null) {
        logErr("playerEntry() 地图实例为null! entryMap=" + entryMap);
        return;
    }

    player.changeMap(map, map.getPortal(0));
    log("playerEntry() 完成, mapId=" + player.getMapId());
}

// ═══════════════════ 玩家生命周期 ═══════════════════
function afterSetup(eim) {}

function playerUnregistered(eim, player) {}

function playerExit(eim, player) {
    log("playerExit() player=" + (player ? player.getName() : "null"));
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 0);
}

function playerLeft(eim, player) {
    if (!eim.isEventCleared()) {
        playerExit(eim, player);
    }
}

function changedLeader(eim, leader) {}

function playerDead(eim, player) {}

function playerRevive(eim, player) {
    return false;
}

function playerDisconnected(eim, player) {
    log("playerDisconnected() player=" + player.getName());
    eim.unregisterPlayer(player);

    if (eim.getPlayerCount() <= 0) {
        log("playerDisconnected() 事件无剩余玩家，执行 end()");
        end(eim);
    }
}

function leftParty(eim, player) {
    if (eim.isEventTeamLackingNow(false, minPlayers, player)) {
        end(eim);
    } else {
        playerLeft(eim, player);
    }
}

function disbandParty(eim) {
    if (!eim.isEventCleared()) {
        end(eim);
    }
}

// ═══════════════════ changedMap() ═══════════════════
function changedMap(eim, player, mapid) {
    log("changedMap() player=" + player.getName() + " mapid=" + mapid +
        " (范围 " + minMapId + "~" + maxMapId + ")");

    if (mapid < minMapId || mapid > maxMapId) {
        log("changedMap() 玩家超出事件地图范围");

        if (eim.isEventCleared()) {
            log("changedMap() 事件已通关，跳过");
            return;
        }

        eim.unregisterPlayer(player);

        if (eim.getPlayerCount() <= 0) {
            log("changedMap() 事件无剩余玩家，执行 end()");
            end(eim);
        }
        return;
    }
    initProp("kill", 0);
}

// ═══════════════════ 怪物 ═══════════════════
function monsterValue(eim, mobId) {
    return 1;
}

function monsterKilled(mob, eim) {
    var mobId = mob.getId();

    var count = parseInt(em.getProperty("kill")) || 0;
    count++;
    em.setProperty("kill", count);

    log("monsterKilled() mobId=" + mobId + " 累计击杀=" + count + "/" + killTarget);

    if (eim.isEventCleared()) {
        return;
    }

    if (count >= killTarget) {
        log("monsterKilled() 达到击杀目标 " + killTarget + "，通关");

        var players = eim.getPlayers();
        for (var i = 0; i < players.size(); i++) {
            var p = players.get(i);
            try {
                p.forceCompleteQuest(completeQuestId);
                log("monsterKilled() " + p.getName() + " 任务 " + completeQuestId + " 完成");
            } catch (questErr) {
                logErr("monsterKilled() 完成任务失败 " + p.getName(), questErr);
            }
            try { p.dropMessage(5, "击杀目标达成！请等待自动退场。"); } catch(e) {}
        }

        clearPQ(eim);
    }
}

function allMonstersDead(eim) {
    log("allMonstersDead() 被调用");
}

// ═══════════════════ 事件结束 ═══════════════════
function clearPQ(eim) {
    log("clearPQ() 被调用");
    eim.stopEventTimer();
    eim.setEventCleared();
    eim.startEventTimer(2 * 60000);
    log("clearPQ() 2分钟后自动退场");
}

function end(eim) {
    log("end() 被调用, eim=" + eim);

    try {
        var party = eim.getPlayers();
        log("end() 剩余玩家数=" + party.size());

        for (var i = 0; i < party.size(); i++) {
            try {
                playerExit(eim, party.get(i));
            } catch (exitErr) {
                logErr("end() playerExit 异常: " + party.get(i).getName(), exitErr);
                try { party.get(i).changeMap(exitMap, 0); } catch(e) {}
            }
        }
    } catch (mainErr) {
        logErr("end() 退场循环异常", mainErr);
    }

    try {
        em.getProperties().clear();
        em.setProperty("state", 0);
        em.setProperty("leader", "true");
        setEventRequirements();
        log("end() 属性已清理, state=0");
    } catch (propErr) {
        logErr("end() 属性清理异常", propErr);
    }

    try {
        eim.dispose();
        log("end() eim.dispose() 完成");
    } catch (disposeErr) {
        logErr("end() dispose 异常", disposeErr);
    }
}

function scheduledTimeout(eim) {
    log("scheduledTimeout() 被调用, 事件超时");
    end(eim);
}

// ═══════════════════ 杂项 ═══════════════════
function cancelSchedule(eim) {}
function dispose(eim) {}

function onItemPickedUp(eim, player, itemId) {
    log("onItemPickedUp() player=" + (player ? player.getName() : "null") + " itemId=" + itemId);
}

// ═══════════════════ 自定义API兼容层 ═══════════════════
function onPlayerRegistered(eim, player) {
    log(">>> onPlayerRegistered() player=" + player.getName());
    playerEntry(eim, player);
}

function onMapChanged(eim, player, mapid) {
    log(">>> onMapChanged() player=" + player.getName() + " mapid=" + mapid);
    changedMap(eim, player, mapid);
}

function onMonsterKilled(eim, mob) {
    log(">>> onMonsterKilled() mobId=" + mob.getId());
    monsterKilled(mob, eim);
    return 1;
}

function onTimeOut(eim) {
    log(">>> onTimeOut()");
    scheduledTimeout(eim);
}

function onPlayerDisconnected(eim, player) {
    log(">>> onPlayerDisconnected() player=" + player.getName());
    playerDisconnected(eim, player);
}

function onPlayerRevived(eim, player) {
    return playerRevive(eim, player);
}

function onPartyDisbanded(eim) {
    disbandParty(eim);
}

function onPlayerKilled(eim, player) {
    playerDead(eim, player);
}

// ═══════════════════ 工具函数 ═══════════════════
function randomNum(minOrMax, max) {
    switch (arguments.length) {
        case 1:
            return parseInt(Math.random() * minOrMax + 1, 10);
        case 2:
            return parseInt(Math.random() * (max - minOrMax + 1) + minOrMax, 10);
        default:
            return 0;
    }
}

function initProp(name, value) {
    if (em.getProperty(name) == null) {
        em.setProperty(name, value);
    }
}

function initPropArray(name, value, start, end) {
    for (var i = start; i <= end; i++) {
        if (em.getProperty(name + "_" + i) == null) {
            em.setProperty(name + "_" + i, value);
        }
    }
}
