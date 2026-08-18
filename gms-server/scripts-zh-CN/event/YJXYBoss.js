/* YJXYJBoss - 妖精学院 Boss 副本事件脚本 */
/* 参考 DummyTest 模式重写 — 使用 getMapInstance + 手动生成Boss */

// ═══════════════════ 事件实例化变量 ═══════════════════
var isPq = true;
var minPlayers = 1, maxPlayers = 6;
var minLevel = 30, maxLevel = 255;
var entryMap = 101073300;       // Boss战地图
var exitMap = 101073200;        // 退场传送目标
var recruitMap = exitMap;       // 玩家必须在此地图上才能开始此事件
var clearMap = 101073300;       // 通关后传送地图（奖励房）
var prepareMap = 101073200;     // 准备地图

var eventTime = 10;             // 事件时间（分钟）
var bossMobId = 3501008;        // Boss怪物ID
var specialJobId = 164;         // 特殊职业
var specialQuestId = 39557;     // 特殊职业前置任务
var completeQuestId = 32125;    // 通关任务ID ← 请确认

// Boss 生成坐标
var bossSpawnX = 4, bossSpawnY = 7;

const maxLobbies = 1;

// ═══════════════════ 日志 ═══════════════════
function log(msg) {
    java.lang.System.out.println("[YJXYJBoss] " + msg);
}

function logErr(msg, err) {
    java.lang.System.out.println("[YJXYJBoss] ERROR: " + msg);
    if (err) {
        java.lang.System.out.println("[YJXYJBoss] " + err.toString());
        if (err.stack) java.lang.System.out.println("[YJXYJBoss] " + err.stack);
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
    reqStr += "\r\n   目标: 击败Boss (不限时)";
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

// ═══════════════════ Boss 生成 ═══════════════════
// 已验证: spawnMonsterOnGroundBelow(int,int,int) 内部也调用 getMonster()，无法绕过 WZ
// 必须修复 Mob/02400570.img.xml info 节点的缺失字段后，所有路径都会自动恢复

function spawnBoss(map, eim) {
    if (!map) { logErr("spawnBoss() map is null"); return; }

    var LifeFactory = Java.type('org.gms.server.life.LifeFactory');
    var Point = Java.type('java.awt.Point');
    var pos = new Point(bossSpawnX, bossSpawnY);

    var mob = LifeFactory.getMonster(bossMobId);
    if (mob != null) {
        // 不覆盖血量/伤害/经验 — 全部走 02400570.img.xml 的 WZ 数据
        map.spawnMonsterOnGroundBelow(mob, pos);
        log("spawnBoss() ★ Boss " + bossMobId + " 已生成 at (" + bossSpawnX + "," + bossSpawnY + ")");
        return;
    }
    logErr("spawnBoss() ★ 无法生成Boss " + bossMobId + " — getMonster 返回 null");
}

// ═══════════════════ setup() — 创建事件实例 ═══════════════════

function setup(level, lobbyid) {
    log("setup() level=" + level + " lobbyid=" + lobbyid);

    var eim = em.newInstance(em.getName() + lobbyid);
    log("setup() eim=" + eim);

    eim.setProperty("level", level);

    // ── 获取 Boss 战地图 ──
    var map = eim.getMapInstance(entryMap);
    log("setup() getMapInstance(" + entryMap + ") → mapId=" + (map ? map.getId() : "null"));

    if (map != null) {
        map.killAllMonsters();
        log("setup() killAllMonsters OK");
    }

    // ── 生成 Boss — 多路径递进 ──
    spawnBoss(map, eim);

    return eim;
}

// ═══════════════════ playerEntry() — 玩家进入 ═══════════════════

function playerEntry(eim, player) {
    log("playerEntry() player=" + player.getName() + " job=" + player.getJob() + " map=" + player.getMapId());

    // 判断是否为特殊职业入口（job==164 且未完成 quest 39557）
    // 由于只有一张地图可用，特殊职业入口仅做日志标记
    var isSpecial = false;
    try {
        var job = player.getJob();
        if (Math.floor(job / 100) == specialJobId) {
            if (!player.isQuestFinished(specialQuestId)) {
                isSpecial = true;
                log("playerEntry() 特殊职业入口（job=" + job + ", quest未完成）");
            }
        }
    } catch (jobErr) {
        logErr("playerEntry() 职业判定失败", jobErr);
    }

    // ── DummyTest 模式：getMapInstance → changeMap ──
    var map = eim.getMapInstance(entryMap);
    player.changeMap(map, map.getPortal(0));
    log("playerEntry() 完成, mapId=" + player.getMapId() + " isSpecial=" + isSpecial);
}

// ═══════════════════ 空地图检测与状态重置 ═══════════════════

function checkEmptyAndCleanup(eim) {
    if (eim.getPlayerCount() <= 0) {
        log("checkEmptyAndCleanup() Boss地图内无玩家，重置事件状态为可进入");
        em.setProperty("state", "0");
        try { eim.dispose(); } catch(e) {}
    }
}

// ═══════════════════ 玩家生命周期 ═══════════════════

function afterSetup(eim) {}

function playerUnregistered(eim, player) {}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 0);
}

function playerLeft(eim, player) {
    if (!eim.isEventCleared()) {
        playerExit(eim, player);
        checkEmptyAndCleanup(eim);
    }
}

function changedLeader(eim, leader) {}

function playerDead(eim, player) {}

function playerRevive(eim, player) {
    return false;
}

function playerDisconnected(eim, player) {
    eim.unregisterPlayer(player);
    checkEmptyAndCleanup(eim);
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
    log("changedMap() player=" + player.getName() + " mapid=" + mapid);

    // 仅允许事件地图（Boss战/准备/奖励房）
    if (mapid != entryMap && mapid != prepareMap && mapid != clearMap) {
        log("changedMap() 玩家超出事件地图范围 → 移除");
        eim.unregisterPlayer(player);

        if (eim.isEventTeamLackingNow(true, minPlayers, player)) {
            end(eim);
        } else {
            checkEmptyAndCleanup(eim);
        }
    }
}

// ═══════════════════ 怪物 ═══════════════════

function monsterValue(eim, mobId) {
    return 1;
}

function monsterKilled(mob, eim) {
    try {
        if (eim.isEventCleared()) return;

        var mobId = mob.getId();
        log("monsterKilled() mobId=" + mobId + " bossMobId=" + bossMobId);

        if (mobId != bossMobId) {
            log("monsterKilled() 非Boss怪物，跳过");
            return;
        }

        // Boss 被击败
        log("monsterKilled() ★ Boss " + bossMobId + " 已被击败!");

        // 广播特效
        var players = eim.getPlayers();
        for (var i = 0; i < players.size(); i++) {
            var p = players.get(i);
            try { p.fieldEffect_ScreenMsg("UI/UIWindowPL.img/HiddenCatch/StageImg/clear"); } catch(e) {}
            try { p.fieldEffect_PlayFieldSound("Party1/Clear"); } catch(e) {}
        }

        // 完成任务 + 传送奖励房
        for (var j = 0; j < players.size(); j++) {
            var pl = players.get(j);
            try {
                pl.forceCompleteQuest(completeQuestId);
                log("monsterKilled() " + pl.getName() + " 任务 " + completeQuestId + " 完成");
            } catch (questErr) {
                logErr("monsterKilled() forceCompleteQuest 失败", questErr);
            }
            try { pl.dropMessage(5, "Boss已被击败！即将传送至奖励房间..."); } catch(e) {}
        }

        // 标记通关
        eim.setEventCleared();
        log("monsterKilled() 已标记通关，3秒后传送奖励房");

        // 延迟3秒后传送至奖励房（使用 java.util.Timer 替代 scheduleWarpTask）
        var Timer = Java.type('java.util.Timer');
        var TimerTask = Java.type('java.util.TimerTask');
        var capturedEim = eim;
        var WarpTask = Java.extend(TimerTask, {
            run: function() {
                try {
                    var allPlayers = capturedEim.getPlayers();
                    for (var k = 0; k < allPlayers.size(); k++) {
                        try { allPlayers.get(k).changeMap(clearMap, 0); } catch(e) {}
                    }
                    log("monsterKilled() 全部玩家已传送至奖励房, state重置");
                    em.setProperty("state", "0");
                    try { capturedEim.dispose(); } catch(e) {}
                } catch(e) {}
            }
        });
        new Timer().schedule(new WarpTask(), 2000);
    } catch (err) {
        logErr("monsterKilled() 异常", err);
    }
}

function allMonstersDead(eim) {
    log("allMonstersDead()");
    clearPQ(eim);
}

// ═══════════════════ 事件结束 ═══════════════════

function clearPQ(eim) {
    eim.setEventCleared();
    log("clearPQ() 已标记通关");
}

function end(eim) {
    log("end() 被调用");

    var party = eim.getPlayers();
    log("end() 剩余玩家=" + party.size());

    for (var i = 0; i < party.size(); i++) {
        try {
            playerExit(eim, party.get(i));
        } catch (exitErr) {
            try { party.get(i).changeMap(exitMap, 0); } catch(e) {}
        }
    }

    // 重置事件状态为可正常进入
    em.setProperty("state", "0");
    try { eim.dispose(); } catch(e) {}
    log("end() 完成 — state 已重置为 0");
}

function scheduledTimeout(eim) {
    log("scheduledTimeout()");
    end(eim);
}

// ═══════════════════ 杂项 ═══════════════════

function cancelSchedule(eim) {}
function dispose(eim) {}

// ═══════════════════ 自定义API兼容层 ═══════════════════

function onPlayerRegistered(eim, player) {
    playerEntry(eim, player);
}

function onMapChanged(eim, player, mapid) {
    changedMap(eim, player, mapid);
}

function onMonsterKilled(eim, mob) {
    monsterKilled(mob, eim);
    return 1;
}

function onTimeOut(eim) {
    scheduledTimeout(eim);
}

function onPlayerDisconnected(eim, player) {
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