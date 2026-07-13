// 匠人街 · 警示 · 基础材料商店
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
        var text = "#e#b<材料商人 · 警示>#k#n\r\n";
        text += "矿石、水晶、基础材料，应有尽有。\r\n\r\n";
        text += "#L0#打开材料商店#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        cm.dispose();
        cm.openShop(9031007);
    }
}
