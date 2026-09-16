// 匠人街 · 守城战入口（家族/个人周常）
var EXT_KEY = "BeiDouCastleDefense";
var WEEKLY_MAX = 1;
var MIN_LEVEL = 70;

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
        var used = getWeeklyUsed();
        var remain = Math.max(0, WEEKLY_MAX - used);
        var t = "#e#b<北斗守城战>#k#n\r\n\r\n";
        t += "守住 GPQ 战场，击退三波入侵！\r\n";
        t += "本周剩余次数：#r" + remain + "#k / " + WEEKLY_MAX + "\r\n";
        t += "需要等级 #b" + MIN_LEVEL + "#k+\r\n\r\n";
        t += "奖励：金币 + #i4002000# #i4002001# #i4032171#\r\n\r\n";
        if (remain > 0) {
            t += "#L0##r开始守城#k#l\r\n";
        }
        t += "#L9000##g离开#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) {
            cm.dispose();
            return;
        }
        if (selection === 0) {
            startDefense();
        }
        cm.dispose();
    }
}

function getWeeklyUsed() {
    var raw = cm.getCharacterExtendValue(EXT_KEY, false);
    if (raw == null || raw === "") {
        return 0;
    }
    var n = parseInt(raw, 10);
    return isNaN(n) ? 0 : n;
}

function incWeeklyUsed() {
    cm.saveOrUpdateCharacterExtendValue(EXT_KEY, String(getWeeklyUsed() + 1), false);
}

function startDefense() {
    var chr = cm.getPlayer();
    if (chr.getLevel() < MIN_LEVEL) {
        cm.sendOk("需要等级 " + MIN_LEVEL + " 以上。");
        return;
    }
    if (getWeeklyUsed() >= WEEKLY_MAX) {
        cm.sendOk("本周守城次数已用完。");
        return;
    }
    if (chr.getEventInstance() != null) {
        cm.sendOk("你已在其他副本中。");
        return;
    }

    var em = cm.getEventManager("BeiDouCastleDefense");
    if (em == null) {
        cm.sendOk("守城事件未加载，请 !reloadevents。");
        return;
    }
    if (!em.startInstance(chr)) {
        cm.sendOk("当前频道守城大厅已满，请换线再试。");
        return;
    }
    incWeeklyUsed();
    chr.dropMessage(6, "【守城战】已加入防守，祝好运！");
}
