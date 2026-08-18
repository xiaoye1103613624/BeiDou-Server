// 匠人街 · Boss 材料兑换
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
        var text = "#e#b<Boss 材料兑换>#k#n\r\n";
        text += "用挑战副本掉落的象征材料，兑换高阶制作素材。\r\n\r\n";
        text += "#L0#象征材料兑换（开发中）#l\r\n";
        text += "#L1#查看材料说明#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 1) {
            cm.sendOk("象征材料来自各层挑战副本 Boss 掉落，可在炼金、武器、套服进阶中使用。");
        } else {
            cm.sendOk("Boss 材料兑换表正在接入，请稍后再来。");
        }
        cm.dispose();
    }
}
