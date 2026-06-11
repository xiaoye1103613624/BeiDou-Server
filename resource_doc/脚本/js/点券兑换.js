

var status = -1;
var itemList = Array(
		//Array(0兑换的物品id,1兑换的物品数量,3币种,2消耗的数量,4编号),
		//币种 0= 金币
		//币种 1= 点券
		//币种 2= 抵用卷
		//币种 3= 充值币
		//币种 4= 豆豆
		//-----------------
		//兑换的物品id 0= 金币
		//兑换的物品id 1= 点券
		//兑换的物品id 2= 抵用卷
		//兑换的物品id 3= 充值币
		//兑换的物品id 4= 豆豆
		//兑换的物品id 5= HP
		//兑换的物品id 6= MP
		Array(1, 10000, 4310108, 1, 0),//
		Array(4310108, 1, 1, 10000, 1)
		// Array(4001126, 10, 4032056, 1, 2),
		// Array(4001126, 100, 4000235, 1, 3),
		// Array(4001126, 100, 4000243, 1, 4),
		// Array(4000463, 1, 1, 1000, 5),
		// Array(1, 950, 4000463, 1, 6),
		// Array(2340000, 1, 3994730, 75, 7),
		// Array(2049100, 1, 3994730, 75, 8),
        // Array(2290285, 1,4170008, 10, 9),
	    // Array(2290285, 1, 4310066, 10, 10)//神秘能手册
		
		// Array(2041228, 1, 1, 10000, 8)

		);
var 兑换的物品 = -1;
var 兑换的数量 = -1;
var 兑换的编号 = -1;

var 当前金币 = 0;
var 当前点卷 = 0;
var 当前抵用卷 = 0;
var 当前积分 = 0;
var 当前豆豆 = 0;
var 份数 = 0;

function start() {
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status >= 0) {
			cm.dispose();
			return;
		}
		status--;
	}
	if (status == 0) {
		var selStr = "您好，请选择您需要兑换的物品：\r\n";
		当前金币 = cm.getMeso();
		当前点卷 = cm.getPlayer().getCSPoints(1);
		当前抵用卷 = cm.getPlayer().getCSPoints(2);
		var txt = "";
		for (var i = 0; i < itemList.length; i++) {
			txt = "";
			//Array(0兑换的物品id,1兑换的物品数量,3币种,2消耗的数量，4编号),

			if (itemList[i][2] == 0) { //金币
				txt += "金币 * " + itemList[i][3] + "#k[#r" + 当前金币 + "#k/" + itemList[i][3] + "]\r\n";
			} else if (itemList[i][2] == 1) { //电卷
				txt += "点卷 * " + itemList[i][3] + "#k[#r" + 当前点卷 + "#k/" + itemList[i][3] + "]\r\n";
			} else if (itemList[i][2] == 2) { //抵用
				txt += "抵用卷 * " + itemList[i][3] + "#k[#r" + 当前抵用卷 + "#k/" + itemList[i][3] + "]\r\n";
			} else if (itemList[i][2] == 3) { //jifen
				txt += "充值币 * " + itemList[i][3] + "#k[#r" + 当前积分 + "#k/" + itemList[i][3] + "]\r\n";
			} else if (itemList[i][2] == 4) { //豆豆
				txt += "豆豆 * " + itemList[i][3] + "#k[#r" + 当前豆豆 + "#k/" + itemList[i][3] + "]\r\n";
			} else { //物品

				txt += "#v" + itemList[i][2] + "# * " + itemList[i][3] + "#k  [#r" + cm.getPlayer().getItemQuantity(itemList[i][2], false) + "#k/" + itemList[i][3] + "]\r\n";
			}
			///-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

			if (itemList[i][0] == 0) { //金币
				selStr += "#L" + itemList[i][4] + "#批量兑换金币 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 1) { //电卷
				selStr += "#L" + itemList[i][4] + "#批量兑换点卷 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 2) { //抵用
				selStr += "#L" + itemList[i][4] + "#批量兑换抵用卷 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 3) { //jifen
				selStr += "#L" + itemList[i][4] + "#批量兑换充值币 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 4) { //豆豆
				selStr += "#L" + itemList[i][4] + "#批量兑换  豆豆 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 5) { //hp
				selStr += "#L" + itemList[i][4] + "#批量兑换  HP *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 6) { //mp
				selStr += "#L" + itemList[i][4] + "#批量兑换  MP *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			}
			else { //物品
				selStr += "#L" + itemList[i][4] + "#批量兑换#v" + itemList[i][0] + "# *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			}

		}
		cm.sendSimple(selStr);
	} else if (status == 1) {
		兑换的物品 = itemList[selection][0];
		兑换的数量 = itemList[selection][1];
		兑换的编号 = selection;
		cm.sendGetNumber("请输入需要多少份 ！\r\n只能输入（1-9999）", 1, 1, 9999);
	} else if (status == 2) {
		//兑换的物品 = itemList[selection][0];
		//兑换的数量 = itemList[selection][1];
		份数 = selection;

		var 材料足够 = true;
		var txt = "";
		if (itemList[兑换的编号][2] == 0) { //金币
			txt += "金币 * " + (itemList[兑换的编号][3] * 份数) + "#k  [#r" + 当前金币 + "#k/" + (itemList[兑换的编号][3] * 份数) + "]\r\n";
			if (当前金币 < (itemList[兑换的编号][3] * 份数)) {
				材料足够 = false;
			}
		} else if (itemList[兑换的编号][2] == 1) { //电卷
			txt += "点卷 * " + (itemList[兑换的编号][3] * 份数) + "#k  [#r" + 当前点卷 + "#k/" + (itemList[兑换的编号][3] * 份数) + "]\r\n";
			if (当前点卷 < (itemList[兑换的编号][3] * 份数)) {
				材料足够 = false;
			}
		} else if (itemList[兑换的编号][2] == 2) { //抵用
			txt += "抵用卷 * " + (itemList[兑换的编号][3] * 份数) + "#k  [#r" + 当前抵用卷 + "#k/" + (itemList[兑换的编号][3] * 份数) + "]\r\n";
			if (当前抵用卷 < (itemList[兑换的编号][3] * 份数)) {
				材料足够 = false;
			}
		} else if (itemList[兑换的编号][2] == 3) { //jifen
			txt += "充值币 * " + (itemList[兑换的编号][3] * 份数) + "#k  [#r" + 当前积分 + "#k/" + (itemList[兑换的编号][3] * 份数) + "]\r\n";
			if (当前积分 < (itemList[兑换的编号][3] * 份数)) {
				材料足够 = false;
			}
		} else if (itemList[兑换的编号][2] == 4) { //豆豆
			txt += "豆豆 * " + (itemList[兑换的编号][3] * 份数) + "#k  [#r" + 当前豆豆 + "#k/" + (itemList[兑换的编号][3] * 份数) + "]\r\n";
			if (当前豆豆 < (itemList[兑换的编号][3] * 份数)) {
				材料足够 = false;
			}

		} else { //物品

			txt += "#v" + itemList[兑换的编号][2] + "# * " + (itemList[兑换的编号][3] * 份数) + "#k  [#r" + cm.getPlayer().getItemQuantity(itemList[兑换的编号][2], false) + "#k/" + (itemList[兑换的编号][3] * 份数) + "]\r\n";
			if (cm.getPlayer().getItemQuantity(itemList[兑换的编号][2], false) < (itemList[兑换的编号][3] * 份数)) {
				材料足够 = false;
			}
		}

		if (材料足够 == false) {
			cm.sendOk("  所需物品不足 \r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n");
			cm.dispose();
			return;

		}

		兑换的数量 = 兑换的数量 * 份数;
		if (itemList[兑换的编号][0] == 0) { //金币
			cm.sendYesNo("兑换 金币  * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?");
		} else if (itemList[兑换的编号][0] == 1) { //电卷
			cm.sendYesNo("兑换 点卷  * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?");
		} else if (itemList[兑换的编号][0] == 2) { //抵用
			cm.sendYesNo("兑换 抵用卷 * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?");
		} else if (itemList[兑换的编号][0] == 3) { //jifen
			cm.sendYesNo("兑换 充值币  * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?");
		} else if (itemList[兑换的编号][0] == 4) { //豆豆
			cm.sendYesNo("兑换 豆豆  * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?");
		} else if (itemList[兑换的编号][0] == 5) { //豆豆
			cm.sendYesNo("兑换 HP  * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?");
		} else if (itemList[兑换的编号][0] == 6) { //豆豆
			cm.sendYesNo("兑换 MP  * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?");

		} else { //物品
			if (兑换的数量 > 30000) {

				cm.sendOk("兑换数量超过3w兑换失败！");
				cm.dispose();
				return;
			}

			cm.sendYesNo("兑换 #i" + 兑换的物品 + "#  * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?"); //修炼点代码 记得换
		}

		//cm.sendYesNo("兑换 #i" + 兑换的物品 + "#  * "+ 兑换的数量 +"  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?"); //修炼点代码 记得换 40000000是蓝蜗牛壳
	} else if (status == 3) {

		if (兑换的物品 > 3) {
			if (!cm.canHold(兑换的物品, 兑换的数量)) {
				cm.sendOk("背包所有栏目窗口有三格以上的空间才可以进行兑换。");
				cm.dispose();
				return;
			}
		}

		if (itemList[兑换的编号][2] == 0) { //金币
			cm.gainMeso( - (itemList[兑换的编号][3] * 份数)); //扣除多少金币
		} else if (itemList[兑换的编号][2] == 1) { //电卷
			cm.getPlayer().modifyCSPoints(1,  - (itemList[兑换的编号][3] * 份数), true); //点券
		} else if (itemList[兑换的编号][2] == 2) { //抵用
			cm.getPlayer().modifyCSPoints(2,  - (itemList[兑换的编号][3] * 份数), true); //点券
		} else if (itemList[兑换的编号][2] == 3) { //jifen
			cm.gainwzcz( - (itemList[兑换的编号][3] * 份数));
		} else if (itemList[兑换的编号][2] == 4) { //豆豆
			cm.getPlayer().gainBeans( - (itemList[兑换的编号][3] * 份数));
		} else { //物品

			cm.gainItem(itemList[兑换的编号][2],  - (itemList[兑换的编号][3] * 份数));
		}

		if (兑换的物品 == 0) { //金币
			cm.gainMeso(+兑换的数量); //扣除多少金币
			//cm.laba("兑换商店处 兑换了 金币 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 金币 ：" + 兑换的数量);
		} else if (兑换的物品 == 1) { //电卷
			cm.getPlayer().modifyCSPoints(1, +兑换的数量, true); //点券
			cm.getPlayer().指定喇叭("白骨喇叭", "点券兑换", "恭喜[" + cm.getPlayer().getName() + "]兑换了"+兑换的数量+"点券!");
			//cm.laba("兑换商店处 兑换了 点卷 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 点卷 ：" + 兑换的数量);
		} else if (兑换的物品 == 2) { //抵用
			cm.getPlayer().modifyCSPoints(2, +兑换的数量, true); //点券
			//cm.laba("兑换商店处 兑换了 抵用卷 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 抵用卷 ：" + 兑换的数量);
		} else if (兑换的物品 == 3) { //jifen
			cm.gainwzcz(+兑换的数量);
			//cm.laba("兑换商店处 兑换了 充值币 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 充值币 ：" + 兑换的数量);
		} else if (兑换的物品 == 4) { //豆豆
			cm.getPlayer().gainBeans(+兑换的数量);
			//cm.laba("兑换商店处 兑换了 豆豆 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 豆豆 ：" + 兑换的数量);
		} else if (兑换的物品 == 5) { //hp
			cm.getPlayer().addmaxHP(+兑换的数量);
			cm.laba("兑换了 HP ：" + 兑换的数量); //新增快捷喇叭
			cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 HP ：" + 兑换的数量);
		} else if (兑换的物品 == 6) { //mp
			cm.getPlayer().addmaxMP(+兑换的数量);
			//cm.laba("兑换了 MP ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 MP ：" + 兑换的数量);
		} else { //物品
			cm.gainItem(兑换的物品, 兑换的数量);
			cm.getPlayer().指定喇叭("白骨喇叭", "点券兑换", "恭喜[" + cm.getPlayer().getName() + "]兑换了"+兑换的数量+"个点券币!");
			//cm.laba("兑换商店处 兑换了 道具 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 道具 ：" + 兑换的数量);
		}

		cm.sendOk("#b#k成功兑换了");
		status = -1;
		//------------------------------
	}
}
