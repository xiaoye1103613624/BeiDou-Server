/*
 * ==================
 * 脚本类型: Boss入口NPC
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. Boss入口快捷传送，含冷却时间管理
 *   2. 冷却时间存储在extend_value中
 *   3. 部分Boss需要消耗金币
 * ==================
 */

var status = -1;
var COOLDOWN_KEY = "bossCooldown";

var bossList = [
    { name: "扎昆", mapId: 211042300, cost: 380000, cooldown: 86400000, icon: 4000082 },
    { name: "暗黑龙王", mapId: 240050400, cost: 380000, cooldown: 86400000, icon: 4000085 },
    { name: "品克缤", mapId: 270050000, cost: 380000, cooldown: 86400000, icon: 4000088 },
    { name: "闹钟", mapId: 220080000, cost: 380000, cooldown: 86400000, icon: 4000053 },
    { name: "鱼王", mapId: 230040420, cost: 380000, cooldown: 86400000, icon: 4000075 },
    { name: "巨魔蝙蝠", mapId: 105100100, cost: 380000, cooldown: 86400000, icon: 4000030 },
    { name: "妖僧", mapId: 702070400, cost: 380000, cooldown: 43200000, icon: 4000093 },
    { name: "树精", mapId: 541020700, cost: 380000, cooldown: 43200000, icon: 4000110 },
    { name: "暴力熊", mapId: 942054900, cost: 0, cooldown: 43200000, icon: 0 },
    { name: "天狗", mapId: 800000000, cost: 0, cooldown: 43200000, icon: 0 }
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var cooldowns = getCooldowns();
        var now = new Date().getTime();

        var text = "#e#b=== Boss入口 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";

        for (var i = 0; i < bossList.length; i++) {
            var boss = bossList[i];
            var cdEnd = cooldowns[boss.mapId] || 0;
            var remaining = Math.max(0, cdEnd - now);
            var statusText;

            if (remaining <= 0) {
                statusText = "#b[可进入]#k";
            } else {
                var minutes = Math.floor(remaining / 60000);
                var hours = Math.floor(minutes / 60);
                var mins = minutes % 60;
                statusText = "#r[冷却中 " + hours + "时" + mins + "分]#k";
            }

            text += "#L" + i + "#";
            text += "#i" + (boss.icon > 0 ? boss.icon : 4000001) + "# ";
            text += "#b" + boss.name + "#k ";
            text += statusText + " ";
            if (boss.cost > 0) {
                text += "#r(" + (boss.cost / 10000).toFixed(1) + "万)#k";
            }
            text += "#l\r\n";
        }

        cm.sendSimple(text);
    } else if (status === 1) {
        var idx = selection;
        if (idx < 0 || idx >= bossList.length) {
            cm.dispose();
            return;
        }

        var boss = bossList[idx];
        var cooldowns = getCooldowns();
        var now = new Date().getTime();
        var cdEnd = cooldowns[boss.mapId] || 0;

        if (cdEnd > now) {
            var remaining = Math.floor((cdEnd - now) / 60000);
            cm.sendOk("该Boss仍在冷却中！剩余 #b" + remaining + "#k 分钟。");
            cm.dispose();
            return;
        }

        if (boss.cost > 0) {
            if (cm.getPlayer().getMeso() < boss.cost) {
                cm.sendOk("金币不足！需要 #b" + boss.cost.toLocaleString() + "#k 金币。");
                cm.dispose();
                return;
            }
            cm.gainMeso(-boss.cost);
        }

        // 设置冷却时间
        cooldowns[boss.mapId] = now + boss.cooldown;
        saveCooldowns(cooldowns);

        cm.getPlayer().saveLocationOnWarp();
        cm.warp(boss.mapId);
        cm.dispose();
    }
}

function getCooldowns() {
    var data = cm.getCharacterExtendValue(COOLDOWN_KEY);
    if (data == null || data === "") {
        return {};
    }
    try {
        return JSON.parse(data);
    } catch (e) {
        return {};
    }
}

function saveCooldowns(data) {
    cm.saveOrUpdateCharacterExtendValue(COOLDOWN_KEY, JSON.stringify(data));
}
