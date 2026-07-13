// 匠人街 · 卡利安 · 炼金大师
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
        var text = "#e#b<炼金大师 · 卡利安>#k#n\r\n";
        text += "卷轴、药水、强化材料，都可以在这里炼制。\r\n\r\n";
        text += "#L0#卷轴炼金（开发中）#l\r\n";
        text += "#L1#药水炼金（开发中）#l\r\n";
        text += "#L2#查看炼金说明#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 2) {
            cm.sendOk("炼金消耗矿石结晶与 Boss 材料，可制作卷轴、药水与部分强化道具。");
        } else {
            cm.sendOk("炼金配方正在接入，请稍后再来。");
        }
        cm.dispose();
    }
}
