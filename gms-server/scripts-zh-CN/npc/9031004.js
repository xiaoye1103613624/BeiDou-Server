// 匠人街 · 梅兹
// 服务：宝石镶嵌（宝1~宝16） / 成长戒指中心
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
        var text = "#e#b<匠人街 · 梅兹>#k#n\r\n\r\n";
        text += "宝石镶嵌师梅兹，可为你为武器 / 上衣 / 裤子 / 套服\r\n";
        text += "镶嵌 #r1~16 级宝石#k，逐级累积属性。\r\n\r\n";
        text += "#L0##b宝石镶嵌#k#l\r\n";
        text += "#L1##b成长戒指中心#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            cm.dispose();
            cm.openNpc(9031004, "xy/匠人街/宝石镶嵌");
        } else if (selection === 1) {
            cm.dispose();
            cm.openNpc(9031004, "xy/匠人街/戒指中心");
        } else {
            cm.dispose();
        }
    }
}