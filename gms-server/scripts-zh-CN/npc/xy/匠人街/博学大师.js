// 匠人街 · 希梅尔（博学大师）· 天赋 / 技能 / 伤害突破
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
        var text = "#e#b<博学大师 · 希梅尔>#k#n\r\n";
        text += "战斗找上层小屋的我，修炼找这里的我。\r\n\r\n";
        text += "#L0#技能学习中心#l\r\n";
        text += "#L1#天赋学习（开发中）#l\r\n";
        text += "#L2#伤害突破（开发中）#l\r\n";
        text += "#L3#二段跳教学（开发中）#l\r\n";
        text += "#L10#返回副本枢纽#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        switch (selection) {
            case 0:
                cm.dispose();
                cm.openNpc(9900001, "技能学习");
                break;
            case 1:
            case 2:
            case 3:
                cm.sendOk("该功能正在接入中，敬请期待。");
                cm.dispose();
                break;
            case 10:
                cm.dispose();
                cm.openNpc(9031000);
                break;
        }
    }
}
