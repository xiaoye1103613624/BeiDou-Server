// enter141040003 — 北斗GMS083 地图进入事件脚本
// 进入地图时弹出NPC 1510008(芭芭拉)对话窗口
// 不需要lockUI/摄像机/隐藏效果 — 只要弹出对话即可

function start(ms) {
    ms.openNpc(1510008,"141040003juqing");
}
