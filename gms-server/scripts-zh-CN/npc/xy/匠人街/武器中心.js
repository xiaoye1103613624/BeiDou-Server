// 匠人街 · 埃珅 · 武器中心
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
        var text = "#e#b<武器中心 · 埃珅>#k#n\r\n";
        text += "19 阶武器成长线，同类型武器可继承进阶。\r\n\r\n";
        text += "#L0#领取初始武器（开发中）#l\r\n";
        text += "#L1#武器进阶兑换（开发中）#l\r\n";
        text += "#L2#查看进阶说明#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 2) {
            cm.sendOk("武器线：圣诞六翼天使武器起步，按同类型链式进阶至创世系列。高阶需要 Boss 象征材料。");
        } else {
            cm.sendOk("武器中心正在接入完整进阶表，请稍后再来。");
        }
        cm.dispose();
    }
}
