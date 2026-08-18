var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        cm.dispose();
    } else {
        status++;
        if (status == 0) {
            if (cm.getQuestStatus(3870) === 1) {
                cm.sendSimple("听说白毛公猴消失在这里。我有件东西必须要找回来。请让我进去。'#l\r\n#L3#前往任务地图#l#k");
            } else {
                cm.sendOk("这里是禁止出入的地区。请回去。");
                cm.dispose();
            }
        } else if (status == 1) {
            if (selection === 3) {
                cm.warp(925120000, 0);
                if (cm.getMapId() == 925120000) {
                    cm.spawnMonster(9100024, -948 , -225);
                }
                cm.dispose();
                return;
            }
            if (!cm.haveItem(4001431) && !cm.haveItem(4001432)) {
                cm.sendOk("你需要一个金庙门票.");
                cm.dispose();
            } else if (cm.getMap(950100500 + (selection * 100)).getCharactersSize() > 0) {
                cm.sendOk("有已经有人在地图.");
                cm.dispose();
            } else {
                if (cm.haveItem(4001431) && !cm.haveItem(4001432)) {
                    cm.gainItem(4001431, -1);
                }
                cm.warp(950100500 + (selection * 100), 0);
                cm.dispose();
            }
        }
    }
}
