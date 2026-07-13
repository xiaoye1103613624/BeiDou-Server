// 团队挑战副本入口
var status = -1;
var SCRIPT_BRIDGE_QUEST = 9031001;
var selectedBoss = null;

var BOSSES = [
    {name: "希拉", map: 105200710, mobs: [8870000], x: 0, y: 0},
    {name: "麦格纳斯", map: 105200711, mobs: [8880000], x: 0, y: 0},
    {name: "暴君 / 困难麦格纳斯", map: 105200712, mobs: [8880002], x: 0, y: 0},
    {name: "四凶 · 穷奇", map: 105200800, mobs: [8880830], x: 0, y: 0},
    {name: "四凶 · 梼杌", map: 105200801, mobs: [8880831], x: 0, y: 0},
    {name: "四凶 · 混沌", map: 105200802, mobs: [8880832], x: 0, y: 0}
];

function enterChallenge(boss) {
    cm.resetMap(boss.map);
    cm.warp(boss.map, 0);
    for (var i = 0; i < boss.mobs.length; i++) {
        cm.spawnMonster(boss.mobs[i], boss.x + (i * 120), boss.y);
    }
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
        var text = "#e#b<团队挑战副本>#k#n\r\n";
        text += "主要掉落：A/S 级宝石、暴君装、四凶材料\r\n\r\n";
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
            enterChallenge(selectedBoss);
            cm.dispose();
        } else {
            openDropViewer(selectedBoss);
        }
    }
}
