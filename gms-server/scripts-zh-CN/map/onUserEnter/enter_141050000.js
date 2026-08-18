// enter141050000 — 北斗GMS083 地图进入事件脚本

function start(ms) {
    if (ms.isQuestActive(32187)) {
        ms.forceCompleteQuest(32187);
    }
}
