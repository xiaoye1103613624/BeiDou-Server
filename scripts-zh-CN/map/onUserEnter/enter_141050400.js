// enter141050400 — 北斗GMS083 地图进入事件脚本
// 进入地图时弹出NPC 1514000对话窗口，由NPC脚本展示全部剧情对话
// 不需要lockUI/摄像机/NPC上场 — 只要弹出对话即可

function start(ms) {
    ms.openNpc(1514000,"141050400juqing");
}
