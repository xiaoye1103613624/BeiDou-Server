/**
 * 任务提醒 — 侧边栏第 11 槽 / 北斗助手入口
 * 替代场上 NPC 头顶 QuestIcon：列出进行中、可交付、当前地图可接任务。
 *
 * @author 萧曵
 */
var ICON = "#fUI/UIWindow.img/QuestIcon/0/0#";
var BOOK = "#fUI/UIWindow.img/QuestIcon/2/0#";
var PROG = "#fUI/UIWindow.img/QuestIcon/1/0#";

var status = -1;
/** @type {Array<{kind:string, questId:number, npcId:number, title:string}>} */
var entries = [];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }
    status++;
    if (status === 0) {
        buildEntries();
        showMenu();
    } else if (status === 1) {
        handleSelect(selection);
    } else {
        cm.dispose();
    }
}

function buildEntries() {
    entries = [];
    var player = cm.getPlayer();
    var Quest = Java.type("org.gms.server.quest.Quest");
    var MapObjectType = Java.type("org.gms.server.maps.MapObjectType");
    var Arrays = Java.type("java.util.Arrays");
    var Point = Java.type("java.awt.Point");

    // 进行中 / 可交付
    var started = player.getStartedQuests();
    for (var i = 0; i < started.size(); i++) {
        var qs = started.get(i);
        var q = qs.getQuest();
        var qid = q.getId();
        var title = safeName(q);
        var npcEnd = q.getNpcRequirement(true);
        if (npcEnd < 0) {
            npcEnd = qs.getNpc();
        }
        if (q.canComplete(player, null)) {
            entries.push({
                kind: "complete",
                questId: qid,
                npcId: npcEnd > 0 ? npcEnd : 0,
                title: title
            });
        } else {
            entries.push({
                kind: "progress",
                questId: qid,
                npcId: npcEnd > 0 ? npcEnd : 0,
                title: title
            });
        }
    }

    // 当前地图可接（NPC 头顶灯泡语义）
    var map = player.getMap();
    if (map == null) {
        return;
    }
    var npcObjs = map.getMapObjectsInRange(
        new Point(0, 0),
        java.lang.Double.POSITIVE_INFINITY,
        Arrays.asList(MapObjectType.NPC)
    );
    var seenQuest = {};
    for (var n = 0; n < entries.length; n++) {
        seenQuest[entries[n].questId] = true;
    }

    var allQuests = Quest.getLoadedQuests().toArray();
    for (var ni = 0; ni < npcObjs.size(); ni++) {
        var npc = npcObjs.get(ni);
        var npcId = npc.getId();
        for (var qi = 0; qi < allQuests.length; qi++) {
            var aq = allQuests[qi];
            var aqId = aq.getId();
            if (seenQuest[aqId]) {
                continue;
            }
            if (aq.getNpcRequirement(false) !== npcId) {
                continue;
            }
            if (!aq.canStart(player, npcId)) {
                continue;
            }
            seenQuest[aqId] = true;
            entries.push({
                kind: "available",
                questId: aqId,
                npcId: npcId,
                title: safeName(aq)
            });
        }
    }
}

function safeName(q) {
    try {
        var n = q.getName();
        if (n != null && String(n).length > 0) {
            return String(n);
        }
    } catch (e) {
    }
    return "任务#" + q.getId();
}

function showMenu() {
    var text = "";
    text += "\t★━━\t#e #r任务提醒#k#n \t━━★\r\n";
    text += ICON + " #d场上头顶灯泡已迁至侧边栏，由此查看可接/进行中/可交任务#k\r\n\r\n";

    if (entries.length === 0) {
        text += "#b当前没有可显示的任务。#k\r\n";
        text += "可接任务请到世界地图查看标记，或靠近相关 NPC。\r\n\r\n";
        text += "#L0##r关闭#k#l\r\n";
        cm.sendSimple(text);
        return;
    }

    // 上限避免超长对话框
    var max = Math.min(entries.length, 40);
    for (var i = 0; i < max; i++) {
        var e = entries[i];
        var prefix = ICON;
        var tag = "#b可接#k";
        if (e.kind === "complete") {
            prefix = BOOK;
            tag = "#r可交#k";
        } else if (e.kind === "progress") {
            prefix = PROG;
            tag = "#d进行中#k";
        }
        var npcHint = e.npcId > 0 ? (" NPC:" + e.npcId) : "";
        text += "#L" + (i + 1) + "#" + prefix + " " + tag + " #b" + e.title + "#k #d[" + e.questId + "]" + npcHint + "#k#l\r\n";
    }
    if (entries.length > max) {
        text += "\r\n#d…另有 " + (entries.length - max) + " 条未显示#k\r\n";
    }
    text += "\r\n#L0##r关闭#k#l\r\n";
    cm.sendSimple(text);
}

function handleSelect(selection) {
    if (selection === 0 || selection < 1 || selection > entries.length) {
        cm.dispose();
        return;
    }
    var e = entries[selection - 1];
    if (e.npcId > 0) {
        cm.dispose();
        cm.openNpc(e.npcId);
        return;
    }
    cm.sendOk("#b" + e.title + "#k\r\n任务ID: " + e.questId + "\r\n未绑定 NPC，请打开任务日志（快捷键 Q）查看详情。");
    cm.dispose();
}
