// 匠人街 · 梅兹 · 成长戒指中心
// 入口：十字旅团戒指领取 / 野外Boss材料进阶
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
        var text = "#e#b<戒指中心 · 梅兹>#k#n\r\n";
        text += "十字旅团成长戒指：#b1112599 → 1112613#k（15阶）\r\n\r\n";
        text += "每级材料：#r野外Boss特有掉落#k + #d地区小怪×3种×200#k + #r金币#k\r\n";
        text += "金币阶梯：10W / 50W / 100W / 200W / 300W / 500W / 700W / 1000W ...\r\n\r\n";
        text += "#L0##b领取 / 升级成长戒指#k#l\r\n";
        text += "#L1##b玩法说明#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            cm.dispose();
            cm.openNpc(9031004, "xy/装备系统/v002/戒指升级");
        } else if (selection === 1) {
            var t = "#e成长戒指玩法说明#n\r\n\r\n";
            t += "1. 将要升级的戒指放在#r装备栏第1格#k后再来对话。\r\n";
            t += "2. 每级对应一只#b野外Boss#k，例如新手戒指对应#r红蜗牛王#k掉落的 #i2210006# #z2210006#。\r\n";
            t += "3. 另需该Boss所在地区常见小怪掉落#b3种各200个#k，以及对应金币。\r\n";
            t += "4. 升级后按职业增加二维属性+10，物理职业攻+5 / 法师魔力+5。\r\n";
            t += "5. 戒指为固有道具，不可交换。\r\n\r\n";
            t += "#d野外Boss可在对应地图等待刷新，或通过GM工具查看刷新点。#k";
            cm.sendOk(t);
            cm.dispose();
        } else {
            cm.dispose();
        }
    }
}
