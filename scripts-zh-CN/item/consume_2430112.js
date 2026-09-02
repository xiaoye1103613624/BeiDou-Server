/**
 * 神奇魔方碎片 2430112 — 兑换奇迹魔方
 * 10 个碎片 → 1 个 5062000
 */
var NEED = 10;
var FRAG = 2430112;
var CUBE = 5062000;

function start() {
    var qty = im.getItemQuantity(FRAG);
    im.sendYesNo("神奇魔方碎片兑换\r\n\r\n当前碎片：#b" + qty + "#k\r\n规则：#b" + NEED + "#k 个碎片 → #v" + CUBE + "# 奇迹魔方 ×1\r\n\r\n是否兑换？");
}

function action(mode, type, selection) {
    if (mode != 1) {
        im.dispose();
        return;
    }
    if (im.getItemQuantity(FRAG) < NEED) {
        im.sendOk("碎片不足（需要 " + NEED + " 个）。");
        im.dispose();
        return;
    }
    if (!im.canHold(CUBE)) {
        im.sendOk("背包已满，请先腾出空位（现金栏）。");
        im.dispose();
        return;
    }
    im.gainItem(FRAG, -NEED);
    im.gainItem(CUBE, 1);
    im.sendOk("兑换成功：获得 #v" + CUBE + "# ×1。");
    im.dispose();
}
