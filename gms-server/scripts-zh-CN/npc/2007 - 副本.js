function start() {
    cm.sendNext("欢迎来到冒险岛世界！我是一个NPC。");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.sendNext("你想要传送到天皇地图吗？");
    } else {
        cm.dispose();
        return;
    }
}

function start() {
    cm.sendNext("准备好了吗？我将会传送你到天皇地图。#r但是会收取你300万金币！");
}

function action(mode, type, selection) {
    if (mode == 1) {
        cm.warp(800040211, 0); // 传送到800040211地图
        cm.gainMeso(-3000000); // 收取300万金币
        cm.dispose();
    } else {
        cm.dispose();
    }
}
