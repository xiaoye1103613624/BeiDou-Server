/**
 * 小型体力回复药剂 (2431952)
 * 双击使用：回复账号级体力 +100（炼金/炼药等共用体力池）
 */
var status = -1;

function start() {
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode != 1) {
        im.dispose();
        return;
    }
    status++;
    if (status == 0) {
        if (!im.haveItem(2431952, 1)) {
            im.sendOk("你没有小型体力回复药剂。");
            im.dispose();
            return;
        }
        var StaminaManager = Java.type("org.gms.config.StaminaManager");
        var accountId = im.getPlayer().getAccountId();
        var before = StaminaManager.getStamina(accountId);
        if (before >= 1000) {
            im.sendOk("体力已满（1000），无需使用。");
            im.dispose();
            return;
        }
        var result = StaminaManager.addStamina(accountId, 100);
        if (result.get("success")) {
            im.gainItem(2431952, -1);
            im.sendOk("使用成功！体力 " + before + " → #b" + result.get("stamina") + "#k / 1000");
        } else {
            im.sendOk("使用失败：" + result.get("message"));
        }
        im.dispose();
    }
}
