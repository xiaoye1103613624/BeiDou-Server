// 匠人街 · 武器工作台 / 铁砧
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
        var text = "#e#b<装备制作铁砧>#k#n\r\n";
        text += "锻造与灵韵注入之所。\r\n\r\n";
        text += "#L0#灵韵觉醒#l\r\n";
        text += "#L1#打开武器中心#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            cm.dispose();
            cm.openNpc(9031012, "xy/匠人街/灵韵觉醒");
        } else {
            cm.dispose();
            cm.openNpc(9031003);
        }
    }
}
