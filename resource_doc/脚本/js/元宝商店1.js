var a = 0;
var text;
var selects;
var buynum = 0;
var itemlist = [
    //道具代码，道具数量，单价，道具说明
	[2022309, 1, 1, "双击兑换点券或抵用卷"],
	[2022517, 2, 1, "使用后获得21亿经验值.每份2个"],
	[4321029, 1, 3, "可以直接完成传统副本"],
	[5520000, 1, 1, "可以部分道具的不可交易改为可交易"],
	[2022699, 1, 5, "使装备的属性变得更好"],
	[2460005, 1, 10, "使装备的属性变得更好"],
	[2460006, 1, 20, "用于指定戒指强化,不消耗砸卷次数"],
	[2460007, 1, 15, "购买后双击即可强化勋章"],
	[2049104, 1, 15, "随机提升装备5点属性"],
	[3994742, 50, 1, "强化小鱼戒指必需品"],
	[2711003, 1, 1, "使用此物品在匠人街进行鉴定"],
	[2340000, 1, 1, "即使装备升级失败，次数也不会减少"],
	[2049122, 1, 3, "特殊物品制作的重要材料"],
	[2450000, 1, 2, "30分钟内，物品经验倍率提高2倍"],
//	[2022530, 1, 5, "30分钟内，物品掉落倍率提高2倍"],
	[2022682, 1,88, "一键升满四转技能到30级"],
	[2614006, 1, 2, "有30%几率提升伤害上限"],
	[2022519, 1, 1, "角色死亡时可以原地复活"],
	[4322899, 1, 30, "可镶嵌在时装上的宝石"],
	[4310086, 1, 300, "购买后可在自由市场重选职业"],
	
	[2022505, 1, 500, "可以选择的自己职业的五转技能书"],
	[4000487, 1, 10, "绯红副本专用"],
	
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
            txt += " - 请输入1-1000所需兑换数值：\r\n\r\n"
            cm.sendGetNumber(txt, 1, 1, 1000);
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
            cm.sendOk("花费了#r " + itemlist[selects][2] * jg + " #k元宝购买了#v" + itemlist[selects][0] + "# * #r" + jg + "#k份，共#r" + itemlist[selects][1] * jg + "#k个。");
			var itemName = cm.getItemName(itemlist[selects][0]);
			cm.喇叭(2, "[元宝商店]" + ":" + "土豪[" + cm.getPlayer().getName() + "] 花费" + itemlist[selects][2] * jg + "元宝购买了 ["+ itemName +"*" + jg + "份，共" + itemlist[selects][1] * jg + "个]！ ");
			cm.喇叭(2, "[元宝商店]" + ":" + "土豪[" + cm.getPlayer().getName() + "] 花费" + itemlist[selects][2] * jg + "元宝购买了 ["+ itemName +"*" + jg + "份，共" + itemlist[selects][1] * jg + "个]！ ");
			cm.喇叭(2, "[元宝商店]" + ":" + "土豪[" + cm.getPlayer().getName() + "] 花费" + itemlist[selects][2] * jg + "元宝购买了 ["+ itemName +"*" + jg + "份，共" + itemlist[selects][1] * jg + "个]！ ");
			Packages.tools.FileoutputUtil.log("log\\玩家相关\\元宝商店.log", "[" + cm.getPlayer().getName() + "] 花费" + itemlist[selects][2] * jg + "元宝购买了 ["+ itemName +"*" + jg + "份，共" + itemlist[selects][1] * jg + "个]！！ ");  //  记录日志
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