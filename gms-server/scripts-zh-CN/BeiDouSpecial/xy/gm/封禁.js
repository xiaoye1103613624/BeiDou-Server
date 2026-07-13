/**
 * @description GM工具：封禁/解封玩家
 * 功能：GM搜索在线玩家姓名，选择封禁时长或解封操作
 * 入口：9900001.js case 903
 */

var Server = Java.type("org.gms.net.server.Server");
var DatabaseConnection = Java.type("org.gms.util.DatabaseConnection");
var PacketCreator = Java.type("org.gms.util.PacketCreator");

// 封禁时长选项：[标签, 秒数（0=永久）]
var BAN_DURATIONS = [
    { label: "1小时",   seconds: 3600 },
    { label: "24小时",  seconds: 86400 },
    { label: "7天",     seconds: 604800 },
    { label: "30天",    seconds: 2592000 },
    { label: "永久封禁", seconds: 0 }
];

var status = -1;
var targetName = "";
var targetPlayer = null;  // 在线玩家对象（可能为null）
var pendingAction = "";   // "ban" or "unban"
var pendingDuration = -1;

function start() {
    status = -1;
    targetName = "";
    targetPlayer = null;
    pendingAction = "";
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    // GM权限检查（需要高级GM）
    if (cm.getPlayer().getGMLevel() < 2) {
        cm.sendOk("你的GM等级不足，无法使用封禁功能！");
        cm.dispose();
        return;
    }

    if (status === 0) {
        cm.sendGetText("请输入要封禁/解封的玩家名称：");
    } else if (status === 1) {
        targetName = cm.getText().trim();
        if (!targetName || targetName === "") {
            cm.sendOk("#r玩家名称不能为空！#k");
            cm.dispose();
            return;
        }
        showPlayerInfo();
    } else if (status === 2) {
        handleActionSelection(selection);
    } else if (status === 3) {
        handleDurationSelection(selection);
    } else if (status === 4) {
        if (type === 1) {
            executeAction();
        } else {
            cm.sendOk("已取消操作。");
            cm.dispose();
        }
    }
}

function showPlayerInfo() {
    // 查找在线玩家
    targetPlayer = findOnlinePlayer(targetName);

    var text = "#eGM封禁管理#n\r\n\r\n";
    text += "目标玩家：#b" + targetName + "#k\r\n";

    if (targetPlayer != null) {
        text += "状态：#r在线#k  等级：#r" + targetPlayer.getLevel() + "#k\r\n";
        text += "所在地图：#b" + targetPlayer.getMap().getMapName() + "#k\r\n\r\n";
    } else {
        text += "状态：#d离线（或玩家不存在）#k\r\n\r\n";
    }

    text += "#L0##r封禁玩家#k#l\r\n";
    text += "#L1##b解封玩家#k#l\r\n";
    text += "#L9#取消#l\r\n";
    cm.sendSimple(text);
}

function handleActionSelection(selection) {
    if (selection === 9) {
        cm.dispose();
        return;
    }

    if (selection === 0) {
        pendingAction = "ban";
        // 显示封禁时长选项
        var text = "#e选择封禁时长#n\r\n\r\n";
        text += "目标：#b" + targetName + "#k\r\n\r\n";
        for (var i = 0; i < BAN_DURATIONS.length; i++) {
            text += "#L" + i + "#" + BAN_DURATIONS[i].label + "#l\r\n";
        }
        text += "#L9#取消#l\r\n";
        cm.sendSimple(text);
    } else if (selection === 1) {
        pendingAction = "unban";
        cm.sendYesNo("确认解封玩家 #b" + targetName + "#k？");
        status = 3;  // 跳过duration选择直接到确认
    }
}

function handleDurationSelection(selection) {
    if (selection === 9) {
        cm.dispose();
        return;
    }
    if (selection < 0 || selection >= BAN_DURATIONS.length) {
        cm.dispose();
        return;
    }

    pendingDuration = selection;
    var dur = BAN_DURATIONS[selection];
    cm.sendYesNo("确认封禁 #b" + targetName + "#k " + dur.label + "？\r\n#r此操作不可撤销（如需解封请使用解封功能）！#k");
}

function executeAction() {
    if (pendingAction === "ban") {
        doBan();
    } else if (pendingAction === "unban") {
        doUnban();
    } else {
        cm.dispose();
    }
}

function doBan() {
    if (pendingDuration < 0 || pendingDuration >= BAN_DURATIONS.length) {
        cm.sendOk("数据异常。");
        cm.dispose();
        return;
    }

    var dur = BAN_DURATIONS[pendingDuration];
    var banMsg = "封禁 " + dur.label;

    try {
        // 如果玩家在线，直接踢出
        if (targetPlayer != null) {
            targetPlayer.getClient().disconnect(false, false);
        }

        // 数据库封禁（永久或临时）
        var conn = DatabaseConnection.getConnection();
        var banUntil = "";
        if (dur.seconds === 0) {
            banUntil = "9999-12-31 23:59:59";
        } else {
            var expireMs = new Date().getTime() + dur.seconds * 1000;
            var expireDate = new Date(expireMs);
            banUntil = expireDate.toISOString().replace("T", " ").substring(0, 19);
        }

        var ps = conn.prepareStatement(
            "UPDATE accounts SET banned = 1, banreason = ? WHERE name = " +
            "(SELECT accountid FROM characters WHERE name = ?)"
        );
        ps.setString(1, "[GM] " + cm.getPlayer().getName() + " 操作，时长：" + dur.label);
        ps.setString(2, targetName);
        ps.executeUpdate();
        ps.close();
        conn.close();

        cm.sendOk("封禁成功！\r\n\r\n玩家 #b" + targetName + "#k 已被封禁 #r" + dur.label + "#k。");
    } catch (e) {
        cm.sendOk("#r封禁失败：#k" + e.getMessage());
    }
    cm.dispose();
}

function doUnban() {
    try {
        var conn = DatabaseConnection.getConnection();
        var ps = conn.prepareStatement(
            "UPDATE accounts SET banned = 0, banreason = NULL WHERE id = " +
            "(SELECT accountid FROM characters WHERE name = ?)"
        );
        ps.setString(1, targetName);
        var rows = ps.executeUpdate();
        ps.close();
        conn.close();

        if (rows > 0) {
            cm.sendOk("解封成功！\r\n\r\n玩家 #b" + targetName + "#k 已解封。");
        } else {
            cm.sendOk("#d未找到该玩家账号，请检查名称是否正确。#k");
        }
    } catch (e) {
        cm.sendOk("#r解封失败：#k" + e.getMessage());
    }
    cm.dispose();
}

/** 查找在线玩家对象 */
function findOnlinePlayer(name) {
    try {
        var worlds = Server.getInstance().getWorlds();
        for (var w = 0; w < worlds.size(); w++) {
            var channels = worlds.get(w).getChannels();
            for (var c = 0; c < channels.size(); c++) {
                var p = channels.get(c).getPlayerStorage().getCharacterByName(name);
                if (p != null) return p;
            }
        }
    } catch (e) {}
    return null;
}
