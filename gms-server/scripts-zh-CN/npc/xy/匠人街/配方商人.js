// 匠人街 · 拉菲纳特 · 配方商店 / 装备分解
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
        var text = "#e#b<配方商人 · 拉菲纳特>#k#n\r\n";
        text += "购买配方，或把多余装备分解为材料。\r\n\r\n";
        text += "#L0#打开配方商店#l\r\n";
        text += "#L1#装备分解（开发中）#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            cm.dispose();
            cm.openShop(9031006);
        } else {
            cm.sendOk("装备分解功能正在接入，请稍后再来。");
            cm.dispose();
        }
    }
}
