// 匠人街 · 埃珅 · 武器中心
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
        var text = "#e#b<武器中心 · 埃珅>#k#n\r\n";
        text += "武器进阶与项链成长。\r\n\r\n";
        text += "#L0#装备打造师#l\r\n";
        text += "#L1#领取初始武器（开发中）#l\r\n";
        text += "#L2#武器进阶兑换（开发中）#l\r\n";
        text += "#L3#阿里山守护者项链成长#l\r\n";
        text += "#L4#查看进阶说明#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            cm.dispose();
            cm.openNpc(9031003, "装备打造师");
            return;
        }
        if (selection === 3) {
            cm.dispose();
            cm.openNpc(9031003, "经验项链");
            return;
        }
        if (selection === 4) {
            cm.sendOk("武器线：圣诞六翼天使武器起步，按同类型链式进阶至创世系列。\r\n"
                + "项链线：制作阿里山守护者项链后，用逆袭银币升级（每次四维+1）。\r\n"
                + "打造线：装备打造师按配方锻造装备（神铸石/重铸石可提升属性区间）。");
            cm.dispose();
            return;
        }
        cm.sendOk("武器中心正在接入完整进阶表，请稍后再来。");
        cm.dispose();
    }
}
