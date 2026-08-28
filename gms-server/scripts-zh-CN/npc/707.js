// NPC 707 · 尼洛斯 · Arcforger 研究（遗忘山谷）
function start() {
    cm.sendOk(
        "#e#b<尼洛斯 · Arcforger>#k#n\r\n\r\n" +
        "欢迎来到我的 Arcforge 研究现场。\r\n" +
        "更深的锻造技艺仍在整理中——\r\n" +
        "装备强化请前往 #b匠人街铁砧#k（装备强化大师）。\r\n\r\n" +
        "洞穴材料（蘑菇孢子等）可在附近怪物身上取得。"
    );
    cm.dispose();
}

function action(mode, type, selection) {
    cm.dispose();
}
