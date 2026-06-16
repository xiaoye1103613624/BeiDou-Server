var status = -1;
var itemList = Array(
		//Array(0兑换的物品id,1兑换的物品数量,3币种,2消耗的数量,4编号),
		//币种 0= 金币
		//币种 1= 点券
		//币种 2= 抵用卷
		//币种 3= 充值币-元宝
		//币种 4= 豆豆
		//-----------------
		//兑换的物品id 0= 金币
		//兑换的物品id 1= 点券
		//兑换的物品id 2= 抵用卷
		//兑换的物品id 3= 充值币
		//兑换的物品id 4= 豆豆
		//兑换的物品id 5= HP
		//兑换的物品id 6= MP
		
	//兑换 抵用*1需要枫叶*2  编号排序
		
		Array(2, 1, 4000038, 1, 0),
		Array(2, 5, 4001165, 1, 1),
		Array(2, 10, 4321010, 1, 2),
		Array(2, 1000, 2643002, 1, 3),
		Array(2, 1000, 3700290, 1, 4),
		Array(2, 2000, 3605009, 1, 5),
		Array(2, 4000, 3700291, 1, 6),
		Array(2, 8000, 3700294, 1, 7),
		Array(2, 16000, 3700293, 1, 8),
		
		Array(1, 1000, 2049300, 1, 9), 
		Array(1, 2000, 2049301, 1, 10),
		Array(1, 4000, 2049302, 1, 11),
		Array(1, 8000, 2049303, 1, 12),
		Array(1, 16000, 2049304, 1, 13),
		Array(1, 32000, 2049305, 1, 14),
		Array(1, 64000, 2049306, 1, 15),
		Array(1, 128000, 2049307, 1, 16),
		
		Array(2, 10000, 4031160, 1, 17),
		Array(2, 10000, 3700292, 1, 18), //上古秘术
		
		Array(3604010, 1, 2614000, 1000, 19), //突破一万
		Array(3604010, 1, 2614012, 2000, 20), //突破一万50
		Array(3604010, 1, 2614006, 3000, 21), //突破一万30
		
		Array(3604010, 1, 2614001, 100, 22), //突破十万
		Array(3604010, 1, 2614013, 200, 23), //突破十万50
		Array(3604010, 1, 2614007, 300, 24), //突破十万30
		
		Array(3604010, 1, 2614002, 10, 25), //突破百万
		Array(3604010, 1, 2614014, 20, 26), //突破百万50
		Array(3604010, 1, 2614008, 30, 27), //突破百万30
		Array(3604010, 1, 2614018, 40, 28) //突破百万10

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
		当前元宝 = cm.getPlayer().getmoneyb();
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
				txt += "元宝 * " + itemList[i][3] + "#k[#r" + 当前元宝 + "#k/" + itemList[i][3] + "]\r\n";
			} else if (itemList[i][2] == 4) { //豆豆
				txt += "豆豆 * " + itemList[i][3] + "#k[#r" + 当前豆豆 + "#k/" + itemList[i][3] + "]\r\n";
			} else { //物品

				txt += "#v" + itemList[i][2] + "# * " + itemList[i][3] + "#k  [#r" + cm.getPlayer().getItemQuantity(itemList[i][2], false) + "#k/" + itemList[i][3] + "]\r\n";
			}
			///-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

			if (itemList[i][0] == 0) { //金币
				selStr += "#L" + itemList[i][4] + "#兑换 金币 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 1) { //电卷
				selStr += "#L" + itemList[i][4] + "#兑换 点卷 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 2) { //抵用
				selStr += "#L" + itemList[i][4] + "#兑换 抵用 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 3) { //jifen
				selStr += "#L" + itemList[i][4] + "#兑换 元宝 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 4) { //豆豆
				selStr += "#L" + itemList[i][4] + "#兑换  豆豆 *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 5) { //hp
				selStr += "#L" + itemList[i][4] + "#兑换  HP *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			} else if (itemList[i][0] == 6) { //mp
				selStr += "#L" + itemList[i][4] + "#兑换  MP *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			}
			else { //物品
				selStr += "#L" + itemList[i][4] + "#兑换 #v" + itemList[i][0] + ":# *" + itemList[i][1] + "#k 需：#r" + txt + "#k#l";
			}

		}
		cm.sendSimple(selStr);
	} else if (status == 1) {
		    // 防御性检查
		if (selection == null || selection < 0 || selection >= itemList.length) {
			cm.sendOk("操作异常，请重新尝试。");
			Packages.tools.FileoutputUtil.log("log\\玩家相关\\强开非法记录.log", "[" + cm.getName() + "]强开非法记录 selection=" + selection);
			cm.dispose();
			return;
		}
		兑换的物品 = itemList[selection][0];
		兑换的数量 = itemList[selection][1];
		兑换的编号 = selection;
		/* —————— 计算“最大可兑换份数” —————— */
		var 每份需数量 = itemList[兑换的编号][3];        // 单份材料消耗量
		var 玩家拥有量 = 0;

		switch (itemList[兑换的编号][2]) {              // 材料类型
			case 0: 玩家拥有量 = 当前金币; break;
			case 1: 玩家拥有量 = 当前点卷; break;
			case 2: 玩家拥有量 = 当前抵用卷; break;
			case 3: 玩家拥有量 = 当前元宝; break;
			case 4: 玩家拥有量 = 当前豆豆; break;
			default: // 物品
				玩家拥有量 = cm.getPlayer().getItemQuantity(itemList[兑换的编号][2], false);
		}

		var 最大份数 = Math.min(3000000, Math.floor(玩家拥有量 / 每份需数量)); // 1000 为全局上限
	//	if (最大份数 <= 0) 最大份数 = 1;   // 至少让输入框能弹出来
		if (最大份数 <= 0) {
			cm.sendOk("你的材料不足，最多可兑换 0 份。");
			cm.dispose();
			return;
		}

		/* —————— 弹出输入框，最大值已动态化 —————— */
		cm.sendGetNumber("请输入需要多少份！\r\n你最多可以兑换#r " + 最大份数 + " #k份", 最大份数, 1, 最大份数);
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
			txt += "元宝 * " + (itemList[兑换的编号][3] * 份数) + "#k  [#r" + 当前元宝 + "#k/" + (itemList[兑换的编号][3] * 份数) + "]\r\n";
			if (当前元宝 < (itemList[兑换的编号][3] * 份数)) {
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
			cm.sendYesNo("兑换 元宝  * " + 兑换的数量 + "  需要：\r\n\r\n-------------------------------------------------\r\n\r\n#r" + txt + "#k\r\n\r\n-------------------------------------------------\r\n\r\n你确定兑换吗?");
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
		} else if (兑换的物品 == 1) { //点卷
			cm.getPlayer().modifyCSPoints(1, +兑换的数量, true); //点券
			var 使用的物品名称 = cm.getItemName(itemList[兑换的编号][2]);
			var 兑换的物品名称 = cm.getItemName(兑换的物品);
			cm.喇叭(2, "玩家[" + cm.getName() + "]在兑换商店处 使用了 " + 使用的物品名称 + "*" + (itemList[兑换的编号][3] * 份数) + " 兑换了 点卷*" + 兑换的数量);
			//cm.laba("兑换商店处 兑换了 点卷 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 点卷 ：" + 兑换的数量);
		} else if (兑换的物品 == 2) { //抵用
			cm.getPlayer().modifyCSPoints(2, +兑换的数量, true); //点券
			var 使用的物品名称 = cm.getItemName(itemList[兑换的编号][2]);
			var 兑换的物品名称 = cm.getItemName(兑换的物品);
			cm.喇叭(2, "玩家[" + cm.getName() + "]在兑换商店处 使用了 " + 使用的物品名称 + "*" + (itemList[兑换的编号][3] * 份数) + " 兑换了 抵用卷*" + 兑换的数量);
			//cm.laba("兑换商店处 兑换了 抵用卷 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 抵用卷 ：" + 兑换的数量);
		} else if (兑换的物品 == 3) { //jifen
			cm.setmoneyb(+兑换的数量);
			var 使用的物品名称 = cm.getItemName(itemList[兑换的编号][2]);
			var 兑换的物品名称 = cm.getItemName(兑换的物品);
			cm.喇叭(2, "玩家[" + cm.getName() + "]在兑换商店处 使用了 " + 使用的物品名称 + "*" + (itemList[兑换的编号][3] * 份数) + " 兑换了 元宝*" + 兑换的数量);
			//cm.laba("兑换商店处 兑换了 充值币 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 充值币 ：" + 兑换的数量);
		} else if (兑换的物品 == 4) { //豆豆
			cm.getPlayer().gainBeans(+兑换的数量);
			//cm.laba("兑换商店处 兑换了 豆豆 ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 豆豆 ：" + 兑换的数量);
		} else if (兑换的物品 == 5) { //hp
			cm.getPlayer().addmaxHP(+兑换的数量);
			//cm.laba("兑换了 HP ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 HP ：" + 兑换的数量);
		} else if (兑换的物品 == 6) { //mp
			cm.getPlayer().addmaxMP(+兑换的数量);
			//cm.laba("兑换了 MP ：" + 兑换的数量); //新增快捷喇叭
			//cm.youlog("充值东西记录.txt", "兑换商店处 兑换了 MP ：" + 兑换的数量);
		} else { //物品
			cm.gainItem(兑换的物品, 兑换的数量);
			    // 获取使用的物品名称和兑换的物品名称
			var 使用的物品名称 = cm.getItemName(itemList[兑换的编号][2]);
			var 兑换的物品名称 = cm.getItemName(兑换的物品);
			cm.喇叭(2, "玩家[" + cm.getName() + "]在兑换商店处使用了 " + 使用的物品名称 + "*" + (itemList[兑换的编号][3] * 份数) + " 兑换了 " + 兑换的物品名称 + "*" + 兑换的数量 + "");
			var 背包总数 = cm.itemQuantity(兑换的物品);
			Packages.tools.FileoutputUtil.log("log\\玩家相关\\九大陆材料兑换.log", "[" + cm.getName() + "]使用物品【" + 使用的物品名称 + "*" + (itemList[兑换的编号][3] * 份数) + "】 兑换了 【" + 兑换的物品名称 + "*" + 兑换的数量 + "】背包总共有" + 背包总数 + "个");
		}

		cm.sendOk("#b#k成功兑换了");
		status = -1;
		//------------------------------
	}
}

