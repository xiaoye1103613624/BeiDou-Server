// 匠人街 · 卡利安 · 炼金/炼药入口
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
        text += "炼金与炼药共用账号体力；副职经验各自独立。\r\n";
        text += "体力可用 #b#t2431952##k 恢复（每次+100）。\r\n\r\n";
        text += "#L0#炼金师#l\r\n";
        text += "#L1#炼药师#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        cm.dispose();
        if (selection === 1) {
            cm.openNpc(9031005, "xy/匠人街/炼药师");
            return;
        }
        cm.openNpc(9031005, "xy/匠人街/炼金大师");
    }
}
