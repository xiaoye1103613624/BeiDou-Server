// 匠人街 · 梅兹 · 成长戒指中心
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
        text += "十字旅团戒指链：1112599 → 1112613\r\n\r\n";
        text += "#L0#领取新手戒指（开发中）#l\r\n";
        text += "#L1#戒指进阶（开发中）#l\r\n";
        text += "#L2#低级饰品合成（开发中）#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        cm.sendOk("戒指中心正在接入完整材料表，请稍后再来。");
        cm.dispose();
    }
}
