// 匠人街 · 斯塔切 · 血衣套服进阶
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
        var text = "#e#b<套服进阶 · 斯塔切>#k#n\r\n";
        text += "从降落伞工作人员套装起步，可进阶至 Lv18 血衣。\r\n\r\n";
        text += "#L0#购买起点套服 1052165（开发中）#l\r\n";
        text += "#L1#套服进阶（开发中）#l\r\n";
        text += "#L2#查看三路线说明#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 2) {
            cm.sendOk("三条路线：伤害（攻魔向）、均衡（四维向）、血量（生存向）。每次进阶三选一，失败只扣材料。");
        } else {
            cm.sendOk("套服进阶系统正在接入，请稍后再来。");
        }
        cm.dispose();
    }
}
