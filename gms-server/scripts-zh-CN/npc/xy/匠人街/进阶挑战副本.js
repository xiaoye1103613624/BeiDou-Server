// 进阶挑战副本入口
var status = -1;
var SCRIPT_BRIDGE_QUEST = 9031001;
var selectedBoss = null;

var BOSSES = [
    {name: "鲁塔比斯四皇", mobs: [8910100, 8900100, 8920100, 8930100], special: "warp", map: 105200000},
    {name: "希纳斯", map: 105200700, mobs: [8850011], x: 0, y: 0},
    {name: "进阶扎昆", map: 280030000, mobs: [8800102], x: 0, y: 0},
    {name: "混沌扎昆（远征版）", mobs: [8800102], special: "expedition", script: "xy/匠人街/混沌扎昆"}
];

function enterChallenge(boss) {
    cm.resetMap(boss.map);
    cm.warp(boss.map, 0);
    for (var i = 0; i < boss.mobs.length; i++) {
        cm.spawnMonster(boss.mobs[i], boss.x + (i * 120), boss.y);
    }
}

function startChallenge(boss) {
    if (boss.special === "warp") {
        cm.warp(boss.map, 0);
        cm.dispose();
        return;
    }
    if (boss.special === "expedition") {
        cm.dispose();
        cm.openNpc(9031000, boss.script);
        return;
    }
    enterChallenge(boss);
    cm.dispose();
}

function openDropViewer(boss) {
    cm.getQuestRecord(SCRIPT_BRIDGE_QUEST).setCustomData(boss.name + "|" + boss.mobs.join(","));
    cm.dispose();
    cm.openNpc(9031000, "xy/匠人街/挑战掉落查询");
}

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
        var text = "#e#b<进阶挑战副本>#k#n\r\n";
        text += "主要掉落：饰品、鲁塔比斯符文、封印箱\r\n\r\n";
        for (var i = 0; i < BOSSES.length; i++) {
            text += "#L" + i + "#" + BOSSES[i].name + "#l\r\n";
        }
        cm.sendSimple(text);
    } else if (status === 1) {
        selectedBoss = BOSSES[selection];
        var menu = "#e#b" + selectedBoss.name + "#k#n\r\n\r\n";
        menu += "#L0#开始挑战#l\r\n";
        menu += "#L1#查看掉落#l\r\n";
        cm.sendSimple(menu);
    } else if (status === 2) {
        if (selection === 0) {
            startChallenge(selectedBoss);
        } else {
            openDropViewer(selectedBoss);
        }
    }
}
