// 匠人街 · 导览 NPC
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
        var text = "#e#b<匠人街导览>#k#n\r\n";
        text += "欢迎来到匠人街！这里是装备成长与挑战副本的枢纽。\r\n\r\n";
        text += "左侧：套服、矿石、武器工作台\r\n";
        text += "中间：戒指、炼金、材料商人\r\n";
        text += "上层：希梅尔（副本枢纽 / 博学大师）\r\n";
        text += "右侧：修炼大师、仓库\r\n\r\n";
        text += "#L0#前往副本枢纽#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        cm.dispose();
        cm.openNpc(9031000);
    }
}
