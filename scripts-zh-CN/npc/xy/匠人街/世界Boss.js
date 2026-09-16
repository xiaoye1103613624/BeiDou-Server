// 匠人街 · 世界Boss（9031000 副本讨伐中心子脚本）
// 禁止直 warp：统一走 WorldBoss Event + portalOpen + startInstance

var BOSS_NAME = "扎昆之王";
var MIN_LEVEL = 120;
var DAILY_ENTER_KEY = "WorldBossEnter";
var DAILY_ENTER_MAX = 3;

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
        var em = cm.getEventManager("WorldBoss");
        if (em == null) {
            cm.sendOk("世界Boss事件未加载，请联系管理员执行 !reloadevents。");
            cm.dispose();
            return;
        }

        var portalOpen = em.getProperty("portalOpen") === "true";
        var remaining = getDailyRemaining();

        var t = "#e#b<世界Boss · " + BOSS_NAME + ">#k#n\r\n\r\n";
        t += "全服定时Boss，按伤害贡献排名发放奖励。\r\n";
        t += "今日剩余进入次数：#r" + remaining + "#k / " + DAILY_ENTER_MAX + "\r\n\r\n";

        if (portalOpen) {
            t += "#b入口已开放！#k\r\n\r\n";
            t += "#L0##r进入讨伐#k#l\r\n";
        } else {
            t += "#r入口未开放#k（每6小时刷新一次，请关注全服公告）\r\n\r\n";
        }

        t += "\r\n#e#b奖励预览#k#n\r\n";
        t += "#i4000313# 黄金枫叶  #i4021017# 灵韵结晶\r\n";
        t += "排名越高，金币/抵用/材料越多。\r\n\r\n";
        t += "#L9000##g离开#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) {
            cm.dispose();
            return;
        }
        if (selection === 0) {
            enterBoss();
        }
        cm.dispose();
    }
}

function getDailyRemaining() {
    var used = cm.getPlayer().getBossLog(DAILY_ENTER_KEY);
    return Math.max(0, DAILY_ENTER_MAX - used);
}

function enterBoss() {
    var em = cm.getEventManager("WorldBoss");
    if (em == null || em.getProperty("portalOpen") !== "true") {
        cm.sendOk("世界Boss入口未开放，请留意全服公告。");
        return;
    }

    var chr = cm.getPlayer();
    if (chr.getLevel() < MIN_LEVEL) {
        cm.sendOk("需要等级 #b" + MIN_LEVEL + "#k 以上才能进入世界Boss讨伐！");
        return;
    }
    if (chr.getEventInstance() != null) {
        cm.sendOk("你已在其他副本中，请先退出。");
        return;
    }
    if (getDailyRemaining() <= 0) {
        cm.sendOk("今日世界Boss进入次数已用完（" + DAILY_ENTER_MAX + "次/日）。");
        return;
    }

    if (!em.startInstance(chr)) {
        cm.sendOk("当前频道讨伐大厅已满，请稍后再试或换线。");
        return;
    }

    chr.setBossLog(DAILY_ENTER_KEY);
    chr.dropMessage(6, "已进入" + BOSS_NAME + "讨伐！击败Boss后按伤害排名发放奖励。");
}
