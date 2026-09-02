// NPC 700 · 露玛 · 洞穴精灵杂货商人（遗忘山谷）
// 店铺表未单独配置时，先挂到通用杂货店；后续可补 shops 行。
function start() {
    cm.sendSimple(
        "欢迎来到洞穴精灵百货商店。\r\n" +
        "需要补给的话尽管跟我说。\r\n\r\n" +
        "#L0#购买杂货#l\r\n" +
        "#L1#离开#l"
    );
}

function action(mode, type, selection) {
    if (mode !== 1) {
        cm.dispose();
        return;
    }
    if (selection === 0) {
        // 优先本 NPC 店；若无配置则退回明珠港杂货
        try {
            cm.openShopNPC(700);
        } catch (e) {
            cm.openShop(100);
        }
    }
    cm.dispose();
}
