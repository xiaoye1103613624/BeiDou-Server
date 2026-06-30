/**
 * @description 拍卖行中心 — 最小化测试版（排查闪退用）
 * 逐步加回功能以定位闪退根因
 */
var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else if (mode === -1) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        // 阶段0：纯文本测试，不用任何 #f 图标
        cm.sendSimple("#L0#测试选项1 - 打印信息#l\r\n#L1#测试选项2 - 打开商店#l\r\n#L2#测试选项3 - HeaderInfo#l");
    } else if (status === 1) {
        if (selection === 0) {
            cm.sendOk("测试：纯文本对话正常");
            cm.dispose();
        } else if (selection === 1) {
            cm.dispose();
            cm.openShopNPC(9900001);
        } else if (selection === 2) {
            // 测试带玩家信息的文本
            var text = buildTestHeaderInfo();
            cm.sendOk(text);
            cm.dispose();
        } else {
            cm.dispose();
        }
    } else {
        cm.dispose();
    }
}

function buildTestHeaderInfo() {
    var cashShop = cm.getPlayer().getCashShop();
    var info = "";
    info += "点券：" + cashShop.getCash(1) + "\r\n";
    info += "抵用：" + cashShop.getCash(2) + "\r\n";
    info += "信用：" + cashShop.getCash(4) + "\r\n";
    info += "金币：" + cm.getPlayer().getMeso() + "\r\n";
    return info;
}
