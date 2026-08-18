// 希梅尔 - 匠人街远征 / 挑战副本枢纽
var status = -1;

function enterQuickBoss(mapId, mobId, x, y) {
    cm.resetMap(mapId);
    cm.warp(mapId, 0);
    cm.spawnMonster(mobId, x, y);
}

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }
    if (mode === 1) {
        status++;
    } else {
        status--;
    }

    if (status === 0) {
        var text = "#e#b<副本枢纽 - 希梅尔>#k#n\r\n\r\n";
        text += "#b── 083 远征（原版）──#k\r\n";
        text += "#L1#挑战蜈蚣#l\r\n";
        text += "#L2#挑战巨型蝙蝠怪#l\r\n";
        text += "#L3#挑战闹钟（帕普拉图斯）#l\r\n";
        text += "#L4#挑战扎昆#l\r\n";
        text += "#L5#挑战暴力熊 / 心疤狮王#l\r\n";
        text += "#L6#挑战黑龙#l\r\n";
        text += "#L7#挑战树精王遗迹#l\r\n";
        text += "#L8#挑战少林妖僧#l\r\n";
        text += "#L9#挑战时间宠儿（品克缤）#l\r\n";
        text += "#L11#蝙蝠魔碎片兑换#l\r\n\r\n";
        text += "#b── 普通挑战副本（材料）──#k\r\n";
        text += "#L10#普通挑战菜单#l\r\n\r\n";
        text += "#b── 进阶挑战副本（饰品）──#k\r\n";
        text += "#L20#进阶挑战菜单#l\r\n\r\n";
        text += "#b── 团队副本（后期装备）──#k\r\n";
        text += "#L30#团队挑战菜单#l\r\n\r\n";
        text += "#L31#前往匠人街#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        switch (selection) {
            case 1:
                enterQuickBoss(251010102, 5220004, 650, 50);
                cm.dispose();
                break;
            case 2:
                cm.dispose();
                cm.openNpc(1061014);
                break;
            case 3:
                cm.dispose();
                cm.openNpc(2043000);
                break;
            case 4:
                cm.dispose();
                cm.openNpc(2030013);
                break;
            case 5:
                cm.dispose();
                cm.openNpc(9270047);
                break;
            case 6:
                cm.dispose();
                cm.openNpc(2083004);
                break;
            case 7:
                enterQuickBoss(551030200, 9420522, 0, 0);
                cm.dispose();
                break;
            case 8:
                enterQuickBoss(702070400, 9600025, 0, 0);
                cm.dispose();
                break;
            case 9:
                cm.dispose();
                cm.openNpc(2141001);
                break;
            case 10:
                cm.dispose();
                cm.openNpc(9031000, "xy/匠人街/普通挑战副本");
                break;
            case 11:
                cm.dispose();
                cm.openNpc(1061016);
                break;
            case 20:
                cm.dispose();
                cm.openNpc(9031000, "xy/匠人街/进阶挑战副本");
                break;
            case 30:
                cm.dispose();
                cm.openNpc(9031000, "xy/匠人街/团队挑战副本");
                break;
            case 31:
                cm.warp(910001000, 0);
                cm.dispose();
                break;
        }
    }
}
