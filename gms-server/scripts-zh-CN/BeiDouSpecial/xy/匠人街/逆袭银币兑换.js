/**
 * 逆袭银币兑换（经验 → 银币）
 * 入口：匠人街炼金术魔法书(9031014) → 逆袭银币兑换
 * 比例：100万经验 = 1 个逆袭银币/逆奥银币(4032181)
 */

var COIN_ID = 4032181;
var EXP_PER_COIN = 1000000;
var MAX_ONCE = 100;

var status = -1;
var qty = 1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status === 0) {
        var exp = cm.getPlayer().getExp();
        var can = Math.floor(exp / EXP_PER_COIN);
        var text = "#e#b逆袭银币兑换#k#n\r\n\r\n";
        text += "兑换比例：#r100万#k 当前经验 → #v" + COIN_ID + "# #t" + COIN_ID + "# ×1\r\n";
        text += "当前经验：#b" + exp + "#k\r\n";
        text += "最多可兑：#b" + can + "#k 个\r\n";
        text += "持有银币：#b" + cm.getPlayer().getItemQuantity(COIN_ID, false) + "#k\r\n\r\n";
        text += "请输入要兑换的数量（1～" + MAX_ONCE + "）：";
        cm.sendGetNumber(text, 1, 1, MAX_ONCE);
    } else if (status === 1) {
        qty = selection;
        if (qty < 1 || qty > MAX_ONCE) {
            cm.sendOk("数量无效。");
            cm.dispose();
            return;
        }
        var need = qty * EXP_PER_COIN;
        var text = "确认消耗 #r" + need + "#k 经验，兑换：\r\n";
        text += "#v" + COIN_ID + "# #t" + COIN_ID + "# ×#b" + qty + "#k ？";
        cm.sendYesNo(text);
    } else if (status === 2) {
        doExchange(qty);
    } else {
        cm.dispose();
    }
}

function doExchange(amount) {
    var need = amount * EXP_PER_COIN;
    var exp = cm.getPlayer().getExp();
    if (exp < need) {
        cm.sendOk("经验不足。需要 #r" + need + "#k，当前 #b" + exp + "#k。");
        cm.dispose();
        return;
    }
    if (!cm.canHold(COIN_ID, amount)) {
        cm.sendOk("其他栏空间不足。");
        cm.dispose();
        return;
    }
    cm.getPlayer().loseExp(need, true, false);
    cm.gainItem(COIN_ID, amount);
    cm.sendOk("兑换成功！获得 #v" + COIN_ID + "# ×#b" + amount + "#k\r\n"
        + "剩余经验：#b" + cm.getPlayer().getExp() + "#k");
    cm.dispose();
}
