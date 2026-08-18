/*
    卷轴中心 — 可选择进入卷轴分解或卷轴兑换
**/
var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) { cm.dispose(); return; }
    if (mode == 0) { cm.dispose(); return; }
    if (mode == 1) status++; else status--;

    if (status == 0) {
        var text = "#e#d卷轴中心#n#k\r\n\r\n";
        text += "请选择服务：\r\n\r\n";
        text += "#L0##b卷轴分解#k — 将消耗栏中的卷轴分解为卷轴碎片#l\r\n\r\n";
        text += "#L1##b卷轴兑换#k — 用卷轴碎片兑换指定卷轴#l\r\n";
        cm.sendSimple(text);

    } else if (status == 1) {
        if (selection == 0) {
            cm.dispose();
            cm.openNpc(9900001, "xy/other/卷轴分解");
        } else if (selection == 1) {
            cm.dispose();
            cm.openNpc(9900001, "xy/other/卷轴兑换");
        } else {
            cm.dispose();
        }
    }
}
