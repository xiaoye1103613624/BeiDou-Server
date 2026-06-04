var tknow = 0;
var typed = 0;
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var ca = java.util.Calendar.getInstance();


function start() {
    if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
        cm.getPlayer().dropMessage(6,"#b#e请保证背包所有栏位至少保留3个空格.");
        cm.dispose();
        return;
    }
/*    if (cm.getPlayer().getClient().getChannel() != 1 && cm.getPlayer().getClient().getChannel() != 2 && cm.getPlayer().getClient().getChannel() != 3) {
        cm.getPlayer().dropMessage(6,"活动只能在1-3频道进行！");
        cm.dispose();
        return;
    }
	*/
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {

    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            var text = "当前背包拥有:#v4001126##b#k数量：#r#c4001126# #k;#v4000313##b#k数量：#r#c4000313#\r\n";
		//	text += "当前背包拥有:#v4000313##b#k数量：#r#c4000313# #k\r\n";
			text += "" +正方箭头 + "#b#L1##v4001126#   3W  #r兑换#b  点券 *  10000#l\r\n\r\n";
			text += "" +正方箭头 + "#b#L2##v4001126#  15W  #r兑换#b  点券 *  50000#l\r\n\r\n";
			text += "" +正方箭头 + "#b#L3##v4001126#  30W  #r兑换#b  点券 * 100000#l\r\n\r\n";
			text += "" +正方箭头 + "#b#L4##v4000313#   3W  #r兑换#b  点券 *  15000#l\r\n\r\n";
			text += "" +正方箭头 + "#b#L5##v4000313#  15W  #r兑换#b  点券 *  75000#l\r\n\r\n";
			text += "" +正方箭头 + "#b#L6##v4000313#  30W  #r兑换#b  点券 * 150000#l\r\n\r\n";

            cm.sendSimple(text);

        } else if (status == 1) {

            var itemId  = (selection <= 3) ? 4001126 : 4000313;   // 1-3 4001126，4-6 4000313
            var need    = [0, 30000, 150000, 300000, 30000, 150000, 300000][selection];
            var reward  = [0, 10000,  50000, 100000, 15000,  75000, 150000][selection];

            if (cm.itemQuantity(itemId) < need) {
                cm.sendOk("你的 #v" + itemId + "##t" + itemId + "# 不足 " + need + " 个，无法兑换！");
                cm.dispose();
                return;
            }

            // 分次扣除（每轮最多 3W）
            var left = need;
            while (left > 0) {
                var once = Math.min(left, 30000);
                cm.gainItem(itemId, -once);
                left -= once;
            }

            cm.gainNX(reward);            // 给点券
            cm.sendOk("兑换成功！\r\n\r\n扣除：#v" + itemId + "# x " + need + "   获得：点券 x " + reward);
			var itemName = cm.getItemName(itemId);
			cm.喇叭(1, "玩家[" + cm.getName() + "]使用了 " + itemName + " x " + need + " 兑换 点券 x " + reward + "");
            status = -1;          // 重置状态
        }
    }
}

