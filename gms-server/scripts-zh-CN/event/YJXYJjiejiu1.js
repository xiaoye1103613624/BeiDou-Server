/* 妖精学院_解救1 */
// 事件实例化变量
var isPq = true;                             // 是否为PQ类型事件
var minPlayers = 1, maxPlayers = 6;          // 队伍成员数量范围
var minLevel = 30, maxLevel = 255;           // 合格队伍成员的等级范围
var entryMap = 101073010;                    // 事件启动时玩家进入的初始地图
var exitMap = 101073000;                     // 玩家未能完成事件时被传送至此地图（准备地图）
var recruitMap = exitMap;                    // 玩家必须在此地图上才能开始此事件
var clearMap = exitMap;                      // 玩家成功完成事件后被传送至此地图

var mapIds = [101073010];                    // 事件涉及的地图ID列表
var prepareMap = 101073000;                  // 准备地图（退场传送目标）

var minMapId = entryMap;                     // 事件发生在此地图ID区间内
var maxMapId = entryMap;

var eventTime = 10;                          // 事件的最大允许时间，以分钟计
var killTarget = 10;                         // 通关所需击杀怪物数量
var completeQuestId = 32123;                 // 通关时完成的任务ID
// NPC弹出方案在Nashorn沙箱中无法使用，改为 monsterKilled 中直接完成任务 + dropMessage 通知

const maxLobbies = 1;                        // 并发活跃大厅的最大数量

// ==================== 日志工具 ====================

/**
 * 输出日志到服务端控制台，统一前缀方便检索。
 * 在服务端日志中搜索 [妖精学院_解救1] 即可看到完整调用链路。
 */
function log(msg) {
    java.lang.System.out.println("[妖精学院_解救1] " + msg);
}

/**
 * 输出带异常堆栈的日志。
 */
function logErr(msg, err) {
    java.lang.System.out.println("[妖精学院_解救1] ERROR: " + msg);
    if (err) {
        java.lang.System.out.println("[妖精学院_解救1] " + err.toString());
        if (err.stack) {
            java.lang.System.out.println("[妖精学院_解救1] " + err.stack);
        }
    }
}

// ==================== 事件配置 ====================

/**
 * 初始化事件，设置事件要求。
 */
function init() {
    log("init() 被调用");
    setEventRequirements();
    log("init() 完成, party属性=" + em.getProperty("party"));
}

/**
 * 获取最大并发活跃大厅的数量。
 * @returns {number} 最大活跃大厅数量。
 */
function getMaxLobbies() {
    log("getMaxLobbies() 被调用 → 返回 " + maxLobbies);
    return maxLobbies;
}

/**
 * 设置并显示事件的要求信息。
 * 服务端/NPC脚本通过 em.getProperty("party") 读取此字符串校验队伍资格。
 */
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

    reqStr += "\r\n   击杀目标: ";
    reqStr += killTarget + " 只怪物";

    em.setProperty("party", reqStr);
}

/**
 * 从给定的队伍中选择符合资格的团队尝试此事件。
 * 服务端在启动事件前调用此函数校验队伍资格，返回空数组则拒绝入场。
 * @param {Party} party - 队伍对象。
 * @returns {PartyCharacter[]} 符合条件的队伍成员数组。
 */
function getEligibleParty(party) {
    log("getEligibleParty() 被调用, party.size=" + (party ? party.size() : "null"));

    var eligible = [];
    var hasLeader = false;

    if (party != null && party.size() > 0) {
        var partyList = party.toArray();

        for (var i = 0; i < party.size(); i++) {
            var ch = partyList[i];

            var chMapId = ch.getMapId();
            var chLevel = ch.getLevel();
            var chIsLeader = ch.isLeader();

            log("  队员[" + i + "] name=" + ch.getName() +
                " mapId=" + chMapId + "(需要" + recruitMap + ")" +
                " level=" + chLevel + "(需要" + minLevel + "~" + maxLevel + ")" +
                " isLeader=" + chIsLeader);

            if (chMapId == recruitMap && chLevel >= minLevel && chLevel <= maxLevel) {
                if (chIsLeader) {
                    hasLeader = true;
                }
                eligible.push(ch);
            }
        }
    }

    log("getEligibleParty() 校验结果: eligible=" + eligible.length +
        " hasLeader=" + hasLeader +
        " minPlayers=" + minPlayers + " maxPlayers=" + maxPlayers);

    if (!(hasLeader && eligible.length >= minPlayers && eligible.length <= maxPlayers)) {
        log("getEligibleParty() → 返回空数组（不符合要求）");
        eligible = [];
    } else {
        log("getEligibleParty() → 返回 " + eligible.length + " 个合格队员");
    }
    return Java.to(eligible, Java.type('org.gms.net.server.world.PartyCharacter[]'));
}

/**
 * 设置事件实例。
 * 创建实例地图、重置怪物、初始化击杀计数、启动计时器。
 * @param {number} level - 事件级别。
 * @param {number} lobbyid - 大厅ID。
 * @returns {EventInstanceManager} 事件实例管理器。
 */
function setup(level, lobbyid) {
    log("setup() 被调用, level=" + level + " lobbyid=" + lobbyid);

    var eventName = em.getName() + lobbyid;
    log("setup() 创建实例, eventName=" + eventName);

    var eim = em.newInstance(eventName);
    log("setup() 实例创建成功, eim=" + eim);

    em.setProperty("state", 1);
    em.setProperty("leader", "true");
    log("setup() state已设为1, leader已设为true");

    try {
        // ❗关键：必须在任何地图操作前初始化击杀计数，
        // 否则 killAllMonsters/resetFully 触发的 monsterKilled 回调中 getProperty("kill") 为 undefined
        em.setProperty("kill", 0);
        log("setup() 击杀计数已重置为0（在地图操作之前）");

        // setInstanceMap 创建全新实例地图，从XML加载怪物刷新点
        var map = eim.setInstanceMap(entryMap);
        log("setup() 创建实例地图, entryMap=" + entryMap + " map=" + map);

        log("setup() 准备调用 map.resetFully()...");
        map.resetFully();
        log("setup() map.resetFully() 完成");

        // 清除 resetFully 后残留怪物（此时 kill 计数器已初始化，callback 安全）
        log("setup() 准备调用 map.killAllMonsters()...");
        try {
            map.killAllMonsters();
            log("setup() map.killAllMonsters() 完成");
        } catch (e1) {
            logErr("killAllMonsters() 失败，跳过", e1);
        }

        // 重置计数器（可能被 killAllMonsters 触发的 callback 污染）
        em.setProperty("kill", 0);
        log("setup() 击杀计数已重新清零");

        // 检查地图上怪物数量
        try {
            var mobCount = map.getMonsterCount();
            log("setup() 当前地图怪物数量=" + mobCount);
        } catch (mcErr) {
            logErr("获取怪物数量失败", mcErr);
        }

    } catch (err) {
        logErr("setup() 执行中发生异常", err);
    }

    log("setup() 完成, 返回eim");
    return eim;
}

/**
 * 事件实例初始化完毕且所有玩家分配完成后，在玩家进入之前触发。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function afterSetup(eim) {
    log("afterSetup() 被调用, eim=" + eim);
}

/**
 * 将玩家传送到事件地图。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 */
function playerEntry(eim, player) {
    log("playerEntry() 被调用, player=" + player.getName() + " eim=" + eim);

    var map = eim.getMapInstance(entryMap);
    log("playerEntry() 获取地图实例, map=" + map + " entryMap=" + entryMap);

    if (map == null) {
        logErr("playerEntry() 地图实例为null! entryMap=" + entryMap);
        return;
    }

    var portal = map.getPortal(0);
    log("playerEntry() portal=" + portal);

    player.changeMap(map, portal);
    log("playerEntry() changeMap已调用, 玩家当前mapId=" + player.getMapId());
}

/**
 * 在玩家即将注销前对其进行某些操作。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 */
function playerUnregistered(eim, player) {
    log("playerUnregistered() player=" + (player ? player.getName() : "null"));
}

/**
 * 在解散事件实例前对玩家进行某些操作。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 */
function playerExit(eim, player) {
    log("playerExit() player=" + (player ? player.getName() : "null"));
    eim.unregisterPlayer(player);
    player.changeMap(exitMap, 0);
    log("playerExit() 已传送到 exitMap=" + exitMap);
}

/**
 * 在玩家离开队伍前对其进行某些操作。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 */
function playerLeft(eim, player) {
    if (!eim.isEventCleared()) {
        playerExit(eim, player);
    }
}

/**
 * 当玩家更换地图时根据 mapid 执行的操作。
 * 玩家离开事件地图范围则退场，否则初始化击杀计数。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 * @param {number} mapid - 新的地图ID。
 */
function changedMap(eim, player, mapid) {
    log("changedMap() player=" + player.getName() + " mapid=" + mapid +
        " (范围 " + minMapId + "~" + maxMapId + ")");

    if (mapid < minMapId || mapid > maxMapId) {
        log("changedMap() 玩家超出事件地图范围");

        // 如果事件已通关，说明正在清场，不重复处理
        if (eim.isEventCleared()) {
            log("changedMap() 事件已通关，跳过");
            return;
        }

        eim.unregisterPlayer(player);

        if (eim.getPlayerCount() <= 0) {
            log("changedMap() 事件无剩余玩家，执行 end() 清理");
            end(eim);
        } else {
            log("changedMap() 事件仍有 " + eim.getPlayerCount() + " 个玩家，仅注销当前玩家");
        }
        return;
    }
    initProp("kill", 0);
    log("changedMap() 玩家在事件地图内，初始化击杀计数");
}

/**
 * 如果队伍领袖变更时执行的操作。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} leader - 新的队伍领袖。
 */
function changedLeader(eim, leader) {
    log("changedLeader() newLeader=" + leader.getName());
}

/**
 * 当玩家死亡时触发。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 */
function playerDead(eim, player) {
    log("playerDead() player=" + player.getName());
}

/**
 * 当玩家复活时触发。
 * 返回 false 禁止在本副本内自动复活。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 * @returns {boolean} 是否允许复活。
 */
function playerRevive(eim, player) {
    log("playerRevive() player=" + player.getName() + " → 返回false(禁止复活)");
    return false;
}

/**
 * 当玩家断开连接时触发。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 */
function playerDisconnected(eim, player) {
    log("playerDisconnected() player=" + player.getName());
    eim.unregisterPlayer(player);

    // 断线后检查事件是否为空，为空则清理 state
    if (eim.getPlayerCount() <= 0) {
        log("playerDisconnected() 事件无剩余玩家，执行 end() 清理");
        end(eim);
    } else {
        log("playerDisconnected() 事件仍有 " + eim.getPlayerCount() + " 个玩家");
    }
}

/**
 * 当玩家离开队伍时触发。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 */
function leftParty(eim, player) {
    log("leftParty() player=" + player.getName());
    if (eim.isEventTeamLackingNow(false, minPlayers, player)) {
        end(eim);
    } else {
        playerLeft(eim, player);
    }
}

/**
 * 当队伍解散时触发。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function disbandParty(eim) {
    log("disbandParty() 被调用");
    if (!eim.isEventCleared()) {
        end(eim);
    }
}

/**
 * 计算怪物价值。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {number} mobId - 怪物ID。
 * @returns {number} 怪物的价值。
 */
function monsterValue(eim, mobId) {
    log("monsterValue() mobId=" + mobId);
    return 1;
}

/**
 * 结束事件。
 * 所有玩家退场并清理事件属性。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function end(eim) {
    log("end() 被调用, eim=" + eim);

    // ⚠️ 关键: 用 try-catch 包裹退场逻辑，防止 changedMap 中已 unregister 的玩家
    // 在 playerExit 中再次 unregister 时抛异常，导致 state 无法重置
    try {
        var party = eim.getPlayers();
        log("end() 剩余玩家数=" + party.size());

        for (var i = 0; i < party.size(); i++) {
            try {
                playerExit(eim, party.get(i));
            } catch (exitErr) {
                logErr("end() playerExit 异常(可能已注销): " + party.get(i).getName(), exitErr);
                // 不抛异常继续：player 可能已被 changedMap 注销，直接传送到 exitMap
                try { party.get(i).changeMap(exitMap, 0); } catch (e2) {}
            }
        }
    } catch (mainErr) {
        logErr("end() 退场循环异常", mainErr);
    }

    // 确保即使上面抛异常，state 也被重置
    try {
        em.getProperties().clear();
        em.setProperty("state", 0);
        em.setProperty("leader", "true");
        setEventRequirements();
        log("end() 属性已清理, state=0, party属性已重建");
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

/**
 * 当队伍成功完成事件实例时触发。
 * 清理事件状态，给玩家短暂停留时间后自动退场。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function clearPQ(eim) {
    log("clearPQ() 被调用");
    eim.setEventCleared();
    log("clearPQ() 事件已标记为通关");
}

/**
 * 当敌对怪物死亡时触发。
 * 累计击杀数，达到 killTarget 后完成对应任务并通过 dropMessage 通知玩家。
 * @param {Monster} mob - 死亡的怪物。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function monsterKilled(mob, eim) {
    var mobId = mob.getId();

    // 使用 parseInt 兜底：getNumberProperty 在某些 Nashorn 版本可能不可靠
    var count = parseInt(em.getProperty("kill")) || 0;
    count++;
    em.setProperty("kill", count);

    log("monsterKilled() mobId=" + mobId + " 累计击杀=" + count + "/" + killTarget);

    if (eim.isEventCleared()) {
        return;
    }

    // 击杀数达到目标：完成任务 + 提示 + 触发 clearPQ 清场
    if (count >= killTarget) {
        log("monsterKilled() 达到击杀目标 " + killTarget + "，完成任务");

        var players = eim.getPlayers();
        for (var i = 0; i < players.size(); i++) {
            var p = players.get(i);
            try {
                // 强制完成任务
                p.forceCompleteQuest(completeQuestId);
                log("monsterKilled() " + p.getName() + " 任务 " + completeQuestId + " 完成");
            } catch (questErr) {
                logErr("monsterKilled() 完成任务失败 " + p.getName(), questErr);
            }
            try {
                p.dropMessage(5, "击杀目标达成！任务已完成，请等待自动退场。");
            } catch (msgErr) {
                logErr("monsterKilled() dropMessage失败", msgErr);
            }
        }

        clearPQ(eim);
    }
}

/**
 * 当所有怪物死亡时触发（服务端自动回调，当前副本不依赖此函数）。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function allMonstersDead(eim) {
    log("allMonstersDead() 被调用");
}

/**
 * 当事件超时而未完成时触发。
 * 对所有在线玩家执行超时退场。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function scheduledTimeout(eim) {
    log("scheduledTimeout() 被调用, 事件超时");
    end(eim);
}

/**
 * 道具被拾取时触发。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 * @param {Character} player - 玩家角色。
 * @param {number} itemId - 道具ID。
 */
function onItemPickedUp(eim, player, itemId) {
    log("onItemPickedUp() player=" + (player ? player.getName() : "null") + " itemId=" + itemId);
}

/**
 * 结束正在进行的任务调度。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function cancelSchedule(eim) {
    log("cancelSchedule() 被调用");
}

/**
 * 结束事件实例。
 * @param {EventInstanceManager} eim - 事件实例管理器。
 */
function dispose(eim) {
    log("dispose() 被调用, eim=" + eim);
}

// ==================== 自定义API兼容层 ====================
// 北斗GMS083服务端在有 getEligibleParty 时用标准API做队伍校验，
// 但回调可能仍走自定义API路径（onXxx 系列）。
// 以下函数将 onXxx 调用委托给上方标准API实现，确保两条路径都能正确处理。

/**
 * 玩家注册到事件实例后传送进入（自定义API路径）。
 * 委托给 playerEntry，不重复启动计时器（setup 中已启动）。
 */
function onPlayerRegistered(eim, player) {
    log(">>> onPlayerRegistered() 被调用! player=" + (player ? player.getName() : "null"));
    log(">>> 说明服务端走的是自定义API路径，不是标准API的playerEntry");
    playerEntry(eim, player);
}

/**
 * 玩家更换地图（自定义API路径）。
 */
function onMapChanged(eim, player, mapid) {
    log(">>> onMapChanged() 被调用! player=" + player.getName() + " mapid=" + mapid);
    changedMap(eim, player, mapid);
}

/**
 * 怪物被击杀（自定义API路径，参数顺序与标准API相反）。
 */
function onMonsterKilled(eim, mob) {
    log(">>> onMonsterKilled() 被调用! mobId=" + mob.getId());
    monsterKilled(mob, eim);
    return 1;
}

/**
 * 事件超时（自定义API路径）。
 */
function onTimeOut(eim) {
    log(">>> onTimeOut() 被调用!");
    scheduledTimeout(eim);
}

/**
 * 玩家断线（自定义API路径）。
 */
function onPlayerDisconnected(eim, player) {
    log(">>> onPlayerDisconnected() 被调用! player=" + player.getName());
    playerDisconnected(eim, player);
}

/**
 * 玩家复活（自定义API路径）。
 */
function onPlayerRevived(eim, player) {
    log(">>> onPlayerRevived() 被调用! player=" + player.getName());
    return playerRevive(eim, player);
}

/**
 * 队伍解散（自定义API路径）。
 */
function onPartyDisbanded(eim) {
    log(">>> onPartyDisbanded() 被调用!");
    disbandParty(eim);
}

/**
 * 玩家死亡（自定义API路径）。
 */
function onPlayerKilled(eim, player) {
    log(">>> onPlayerKilled() 被调用! player=" + player.getName());
    playerDead(eim, player);
}

// ==================== 工具函数 ====================

/**
 * 为玩家打开NPC对话 — 已废弃。
 * Nashorn沙箱限制下所有打开NPC方案均不可用，改为 monsterKilled 中直接完成任务 + dropMessage 提示。
 */

/**
 * 生成指定范围内的随机整数。
 * @param {number} minOrMax - 最小值（单参数时为最大值）。
 * @param {number} [max] - 最大值。
 * @returns {number} 随机整数。
 */
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

/**
 * 初始化事件属性（仅在未设置时赋值）。
 * @param {string} name - 属性名。
 * @param {string} value - 属性值。
 */
function initProp(name, value) {
    if (em.getProperty(name) == null) {
        em.setProperty(name, value);
    }
}

/**
 * 初始化数组形式的事件属性。
 * @param {string} name - 属性名前缀。
 * @param {string} value - 属性值。
 * @param {number} start - 起始索引。
 * @param {number} end - 结束索引。
 */
function initPropArray(name, value, start, end) {
    for (var i = start; i <= end; i++) {
        if (em.getProperty(name + "_" + i) == null) {
            em.setProperty(name + "_" + i, value);
        }
    }
}