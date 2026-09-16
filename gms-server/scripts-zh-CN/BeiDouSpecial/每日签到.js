/** 每日签到 — 对接 DailyCheckinRewards（28天循环） */
var DailyCheckinRewards = Java.type("org.gms.server.dailycheckin.DailyCheckinRewards");
var PacketCreator = Java.type("org.gms.util.PacketCreator");
var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        var player = cm.getPlayer();
        if (player.getLevel() < DailyCheckinRewards.MIN_LEVEL) {
            cm.sendOk("需要等级 #b" + DailyCheckinRewards.MIN_LEVEL + "#k 以上才能签到。");
            cm.dispose();
            return;
        }

        var claimable = player.refreshCheckin();
        var text = "#e#b<每日签到>#k#n\r\n\r\n";
        if (claimable >= 1) {
            text += "今日可领取：#r第 " + claimable + " 天#k\r\n";
            text += previewReward(claimable);
            text += "\r\n#L0##b领取今日奖励#k#l\r\n";
            text += "#L1#打开签到窗口（需插件）#l\r\n";
        } else {
            var secs = player.getCheckinCooldownSeconds();
            var h = Math.floor(secs / 3600);
            var m = Math.floor((secs % 3600) / 60);
            text += "#r今日已签到#k，冷却约 " + h + " 小时 " + m + " 分钟。\r\n";
            text += "#L1#查看签到窗口#l\r\n";
        }
        text += "#L9000##g离开#k#l";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 9000) {
            cm.dispose();
            return;
        }
        if (selection === 1) {
            openCheckinWindow();
            cm.dispose();
            return;
        }
        if (selection === 0) {
            claimToday();
        }
        cm.dispose();
    }
}

function previewReward(day) {
    var tip = DailyCheckinRewards.tooltip(day);
    return tip.length > 0 ? tip + "\r\n" : "";
}

function openCheckinWindow() {
    var player = cm.getPlayer();
    var claimable = player.refreshCheckin();
    var viewDay = claimable >= 1 ? claimable : player.getCheckinDay();
    cm.getClient().sendPacket(PacketCreator.dailyCheckinSnapshot(viewDay, player.getCheckinClaimed(), 0));
}

function claimToday() {
    var player = cm.getPlayer();
    var claimable = player.refreshCheckin();
    if (claimable < 1) {
        cm.sendOk("今日已签到或仍在冷却中。");
        return;
    }
    if (!DailyCheckinRewards.grantDay(cm.getClient(), claimable)) {
        cm.sendOk("签到失败，请检查背包空间。");
        return;
    }
    player.applyCheckinClaim(claimable);
    player.saveCharToDB();
    cm.sendOk("签到成功！已领取第 #b" + claimable + "#k 天奖励。\r\n\r\n" + previewReward(claimable));
}
