// 普通挑战副本入口
var status = -1;
var SCRIPT_BRIDGE_QUEST = 9031001;
var selectedBoss = null;

var BOSSES = [
    {name: "地鼠王", map: 105200500, mobs: [3501008], x: 0, y: 0},
    {name: "盖奥勒克", map: 105200510, mobs: [3502008], x: 0, y: 0},
    {name: "黑色之翼飞船", map: 105200520, mobs: [8240046], x: 0, y: 0},
    {name: "贝尔加莫特", map: 240070203, mobs: [7220003], x: 0, y: 0},
    {name: "钻机", map: 105200600, mobs: [9600086], x: 0, y: 0},
    {name: "斯乌", map: 105200610, mobs: [8240098, 8240105], x: 0, y: 0}
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
        var text = "#e#b<普通挑战副本>#k#n\r\n";
        text += "主要掉落：勇者之石、圣者之石、一级宝石、二级宝石\r\n\r\n";
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
