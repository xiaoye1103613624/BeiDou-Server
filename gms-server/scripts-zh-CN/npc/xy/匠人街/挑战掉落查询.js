// 挑战副本 Boss 掉落查询（纯文字，避免缺失物品图标导致闪退）
var status = -1;
var SCRIPT_BRIDGE_QUEST = 9031001;

var MonsterInformationProvider = Java.type("org.gms.server.life.MonsterInformationProvider");
var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");
var LifeFactory = Java.type("org.gms.server.life.LifeFactory");

var bossName = "";
var mobIds = [];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }

    if (status === 0) {
        var payload = cm.getQuestRecord(SCRIPT_BRIDGE_QUEST).getCustomData();
        if (!payload) {
            cm.sendOk("未找到 Boss 信息，请重新选择。");
            cm.dispose();
            return;
        }

        var parts = payload.split("|");
        bossName = parts[0];
        mobIds = parts[1].split(",").map(function (s) {
            return parseInt(s, 10);
        });

        if (mobIds.length === 1) {
            showDropList(mobIds[0]);
            status = 2;
            return;
        }

        var text = "#e#b[" + bossName + "] 掉落查询#k#n\r\n请选择要查看的怪物：\r\n\r\n";
        for (var i = 0; i < mobIds.length; i++) {
            var mob = LifeFactory.getMonster(mobIds[i]);
            var name = mob.getName();
            if (!name || name === "MISSINGNO") {
                name = "怪物#" + mobIds[i];
            }
            text += "#L" + i + "#" + name + " (" + mobIds[i] + ")#l\r\n";
        }
        cm.sendSimple(text);
        status = 1;
        return;
    }

    if (status === 1) {
        showDropList(mobIds[selection]);
        status = 2;
        return;
    }

    cm.dispose();
}

function showDropList(mobId) {
    var player = cm.getPlayer();
    var mob = LifeFactory.getMonster(mobId);
    var mobName = mob.getName();
    if (!mobName || mobName === "MISSINGNO") {
        mobName = "怪物#" + mobId;
    }

    var rate = mob.isBoss() ? player.getBossDropRate() : player.getDropRate();
    var dropall = MonsterInformationProvider.getInstance().retrieveDrop(mobId);

    var text = "#e#b[" + bossName + " · " + mobName + "]#k#n\r\n";
    text += "你的爆率倍率：#r" + rate.toFixed(2) + "x#k\r\n\r\n";

    if (dropall.size() <= 0) {
        text += "暂无掉落配置。";
        cm.sendOk(text);
        return;
    }

    text += "#b物品名称\t基础掉率\t你的掉率#k\r\n";
    text += "────────────────────────\r\n";

    var entries = [];
    var iter = dropall.iterator();
    while (iter.hasNext()) {
        var drop = iter.next();
        if (drop.itemId <= 0) {
            continue;
        }
        var itemName = ItemInformationProvider.getInstance().getName(drop.itemId);
        if (!itemName) {
            itemName = "未知道具(" + drop.itemId + ")";
        }
        var basePct = (drop.chance / 10000).toFixed(2);
        var yourPct = (drop.chance / 10000 * rate).toFixed(2);
        var qty = "" + drop.Minimum;
        if (drop.Maximum > drop.Minimum) {
            qty = drop.Minimum + "~" + drop.Maximum;
        }
        entries.push({
            name: itemName,
            base: basePct,
            yours: yourPct,
            qty: qty,
            questid: drop.questid
        });
    }

    entries.sort(function (a, b) {
        return parseFloat(b.base) - parseFloat(a.base);
    });

    for (var i = 0; i < entries.length; i++) {
        var e = entries[i];
        text += e.name;
        if (e.qty !== "1") {
            text += " x" + e.qty;
        }
        text += "\t" + e.base + "%\t#d" + e.yours + "%#k";
        if (e.questid > 0) {
            text += " #r[任务]#k";
        }
        text += "\r\n";
    }

    cm.sendOk(text);
}
