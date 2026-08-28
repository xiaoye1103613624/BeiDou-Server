// 匠人街 · 诺布 · 矿石精炼 / Boss 材料
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
        var text = "#e#b<诺布 · 矿石大师>#k#n\r\n";
        text += "矿石提炼与 Boss 材料兑换。\r\n\r\n";
        text += "#L0#矿石结晶提炼#l\r\n";
        text += "#L1#Boss 材料兑换#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        cm.dispose();
        if (selection === 0) {
            cm.openNpc(9031002, "xy/匠人街/矿石精炼");
        } else {
            cm.openNpc(9031002, "xy/匠人街/Boss材料兑换");
        }
    }
}
