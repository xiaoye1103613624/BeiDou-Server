// NPC 800015 · 秘法制作台（遗忘山谷）
function start() {
    cm.sendOk(
        "#e#b<秘法制作台>#k#n\r\n\r\n" +
        "古老的制作台静静伫立着。\r\n" +
        "目前可使用的合成配方仍在恢复中。\r\n" +
        "需要强化装备时，请前往 #b匠人街#k。"
    );
    cm.dispose();
}

function action(mode, type, selection) {
    cm.dispose();
}
