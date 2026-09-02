// 匠人街 · 大当家 · 修炼大师（洗血）
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
        var text = "#e#b<修炼大师 · 大当家>#k#n\r\n";
        text += "调整体质，洗练血量上限。\r\n\r\n";
        text += "#L0#洗血（开发中）#l\r\n";
        text += "#L1#查看说明#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 1) {
            cm.sendOk("洗血可重新分配体质点数，影响最大 HP。功能接入中。");
        } else {
            cm.sendOk("洗血功能正在接入，请稍后再来。");
        }
        cm.dispose();
    }
}
