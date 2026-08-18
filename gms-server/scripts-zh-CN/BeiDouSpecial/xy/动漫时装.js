var a = 0;
var text;
var selects;
var buynum = 0;
var itemlist = [
    //道具代码，道具数量，单价，道具说明
	[1008900, 1, 50, "DlY皮肤"],
	[1008901, 1, 50, "DlY皮肤"],
	[1008902, 1, 50, "DlY皮肤"],
	[1008903, 1, 50, "DlY皮肤"],
	[1008904, 1, 50, "DlY皮肤"],
	[1008905, 1, 50, "DlY皮肤"],
	[1008906, 1, 50, "DlY皮肤"],
	[1008907, 1, 50, "DlY皮肤"],
	[1008908, 1, 50, "DlY皮肤"],
	[1008909, 1, 50, "DlY皮肤"],
	[1008910, 1, 50, "DlY皮肤"],
	[1008911, 1, 50, "DlY皮肤"],
	[1008912, 1, 50, "DlY皮肤"],
	[1008913, 1, 50, "DlY皮肤"],
	[1008914, 1, 50, "DlY皮肤"],
	[1008915, 1, 50, "DlY皮肤"],
	[1008916, 1, 50, "DlY皮肤"],
	[1008917, 1, 50, "DlY皮肤"],
	[1008918, 1, 50, "DlY皮肤"],
	[1008919, 1, 50, "DlY皮肤"],
	[1008920, 1, 50, "DlY皮肤"],
	[1008921, 1, 50, "DlY皮肤"],
	[1008922, 1, 50, "DlY皮肤"],
	[1008923, 1, 50, "DlY皮肤"],
	[1008924, 1, 50, "DlY皮肤"],
	[1008925, 1, 50, "DlY皮肤"],
	[1008926, 1, 50, "DlY皮肤"],
	[1008927, 1, 50, "DlY皮肤"],
	[1008928, 1, 50, "DlY皮肤"],
	[1008929, 1, 50, "DlY皮肤"],
	[1008930, 1, 50, "DlY皮肤"],
	[1008931, 1, 50, "DlY皮肤"],
	[1008932, 1, 50, "DlY皮肤"],
	[1008933, 1, 50, "DlY皮肤"],
	[1008934, 1, 50, "DlY皮肤"],
	[1008935, 1, 50, "DlY皮肤"],
	[1008936, 1, 50, "DlY皮肤"],
	[1008937, 1, 50, "DlY皮肤"],
	[1008938, 1, 50, "DlY皮肤"],
	[1008939, 1, 50, "DlY皮肤"],
	[1008940, 1, 50, "DlY皮肤"],
	[1008941, 1, 50, "DlY皮肤"],
	[1008942, 1, 50, "DlY皮肤"],
	[1008943, 1, 50, "DlY皮肤"],
	[1008944, 1, 50, "DlY皮肤"],
	[1008945, 1, 50, "DlY皮肤"],
	[1008946, 1, 50, "DlY皮肤"],
	[1008947, 1, 50, "DlY皮肤"],
	[1008948, 1, 50, "DlY皮肤"],
	[1008949, 1, 50, "DlY皮肤"],
	[1008950, 1, 50, "DlY皮肤"],
	//[1008951, 1, 50, "DlY皮肤"],
	[1008952, 1, 50, "DlY皮肤"],
	[1008953, 1, 50, "DlY皮肤"],
    [1008954, 1, 50, "DlY皮肤"],
	[1008955, 1, 50, "DlY皮肤"],
	[1008956, 1, 50, "DlY皮肤"],
	[1008957, 1, 50, "DlY皮肤"],
	[1008958, 1, 50, "DlY皮肤"],
	[1008959, 1, 50, "DlY皮肤"],
	[1008960, 1, 50, "DlY皮肤"],
	[1008961, 1, 50, "DlY皮肤"],
	[1008962, 1, 50, "DlY皮肤"],
	[1008963, 1, 50, "DlY皮肤"],
	[1008964, 1, 50, "DlY皮肤"],
	[1008965, 1, 50, "DlY皮肤"],
	[1008966, 1, 50, "DlY皮肤"],
	[1008967, 1, 50, "DlY皮肤"],
	[1008968, 1, 50, "DlY皮肤"],
	[1008969, 1, 50, "DlY皮肤"],
	[1008970, 1, 50, "DlY皮肤"],
	[1008971, 1, 50, "DlY皮肤"],
    [1008972, 1, 50, "DlY皮肤"],
    [1008973, 1, 50, "DlY皮肤"],
	[1008974, 1, 50, "DlY皮肤"],
	[1008975, 1, 50, "DlY皮肤"],
	[1008976, 1, 50, "DlY皮肤"],
	[1008977, 1, 50, "DlY皮肤"],
	[1008978, 1, 50, "DlY皮肤"],
	[1008979, 1, 50, "DlY皮肤"],
	[1008980, 1, 50, "DlY皮肤"],
	[1008981, 1, 50, "DlY皮肤"],
];

function start() {
    a = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1)
            a++;
        else
            a--;
        if (a == -1) {
            cm.dispose();
        } else if (a == 0) {
            text = "  尊敬的#r#h0##k,您好！在这里可以使用“#b 元宝 #k”兑换你想要的物品。\r\n";
            text += "  - 您当前剩余的元宝为：#r#e " + cm.getChar().getmoneyb() + " #n #k点\r\n";
            for (var i = 0; i < itemlist.length; i++) {
                text += "#L" + i + "##e#d购买道具：[#b#v" + itemlist[i][0] + "##z" + itemlist[i][0] + "##d]#l\r\n\r\n#d   - 数量：#r" + itemlist[i][1] + "#d个  购买价格：#r" + itemlist[i][2] + "#d元宝\r\n   - [#b道具说明：#r" + itemlist[i][3] + "#k]\r\n\r\n";
            }
            cm.sendSimple(text);
        } else if (a == 1) {
            selects = selection;
            var txt = " - 当前兑换道具：#r#i" + itemlist[selects][0] + "##t" + itemlist[selects][0] + "##d * " + itemlist[selects][1] + "\r\n\r\n"
            txt += " #k- 当前道具说明：#d" + itemlist[selects][3] + "#k\r\n\r\n"
            txt += " - 当前道具单价：#r" + itemlist[selects][2] + "#k 元宝。\r\n\r\n"
            txt += " - 请输入所需兑换数值：\r\n\r\n"
            cm.sendGetNumber(txt, 1, 1, 100);
        } else if (a == 2) {
            jg = selection
            var itemid = itemlist[selects][0];
            if (cm.canHold(itemlist[selects][0], itemlist[selects][1] * jg) == false) {
                cm.sendOk("您的背包空间不足，请整理后再兑换。");
                cm.dispose();
                return;
            }
            if (cm.getChar().getmoneyb() < itemlist[selects][2] * jg) {
                cm.sendOk("#d所需元宝不足，购买道具需要元宝[#r" + cm.getChar().getmoneyb() + "#d/#b" + itemlist[selects][2] * jg + "#d]");
                cm.dispose();
                return;
            }
            cm.setmoneyb(-itemlist[selects][2] * jg);
			//修改账号数据("赞助积分", +itemlist[selects][2] * jg);
            cm.gainItem(itemlist[selects][0], itemlist[selects][1] * jg);
            cm.sendOk("恭喜你花费了#r " + itemlist[selects][2] * jg + " #k元宝购买了#v" + itemlist[selects][0] + "# * #r" + jg + "#k份。");
			var itemName = cm.getItemName(itemlist[selects][0]);
			cm.喇叭(2, "[元宝商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在元宝商店购买了 ["+ itemName +"*" + jg + "份]！ ");
			cm.喇叭(2, "[元宝商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在元宝商店购买了 ["+ itemName +"*" + jg + "份]！ ");
			cm.喇叭(2, "[元宝商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在元宝商店购买了 ["+ itemName +"*" + jg + "份]！ ");
            cm.dispose();
        }
    }
}

function 修改账号数据(type, sum) {
	var accid = cm.getPlayer().getAccountID();
	sqlMultiPurpose("UPDATE accounts SET " + type + " = " + type + " + " + sum + " WHERE id = " + accid + "");
}

function sqlMultiPurpose(sql) {//
	var con = cm.getConnection();
	var ps = con.prepareStatement(sql);
	ret = ps.executeUpdate();
	ps.close();
	con.close();
	return ret;
}