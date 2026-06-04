
var 皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/15#";
var 幸运草 = "#fUI/GuildMark/Mark/Plant/00003006/15#";
var 香水 = "#fUI/GuildMark/Mark/Pattern/00004008/15#";
var 中条白 = "#fUI/Basic/HScr7/disabled/base#";
var 中条蓝 = "#fUI/ChatBalloon/tutorial/w#";

var 中条猫 = "#fUI/ChatBalloon/119/n#";
var 猫右 = "#fUI/ChatBalloon/119/ne#";
var 猫左 = "#fUI/ChatBalloon/119/nw#";
var 右 = "#fUI/ChatBalloon/119/e#";
var 左 = "#fUI/ChatBalloon/119/w#";

var 下条猫 = "#fUI/ChatBalloon/119/s#";
var 猫下右 = "#fUI/ChatBalloon/119/se#";
var 猫下左 = "#fUI/ChatBalloon/119/sw#";

var 彩虹1 = "#fUI/ChatBalloon/122/n#";
var 彩虹上1 = "#fUI/ChatBalloon/122/ne#";
var 彩虹上2 = "#fUI/ChatBalloon/122/nw#";
var 彩1 = "#fUI/ChatBalloon/122/e#";
var 彩2 = "#fUI/ChatBalloon/122/w#";

var 彩虹下 = "#fUI/ChatBalloon/122/s#";
var 彩虹下1 = "#fUI/ChatBalloon/122/se#";
var 彩虹下2 = "#fUI/ChatBalloon/122/sw#";
var 彩虹中 = "#fUI/ChatBalloon/122/head#";
//
var 梅花 = "#fUI/GuildMark/Mark/Animal/00002008/14#";
var 蝴蝶 = "#fUI/GuildMark/Mark/Animal/00002020/14#";
var 寄售方式 = 0; //0是金币   1是点卷
var name = "";
var cc = null;
var 当前上架个数 = -1;
var 售价 = -1;
var 上架手续费 = 0;

var 上架最大个数 = 20;
var 点券手续费 = 10;
var 金币手续费 = 20000;
var 抵用手续费 = 10;

var 交易币手续费 = 0;

var 不支持上架的所有东西 =[1302001,4000000];



function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		cm.dispose();
	} else {
		if (mode == 0 && status == 0) {
			cm.dispose();
			return;
		}
		if (mode == 1) {
			status++;
		} else {
			status--;
		}
	}
	var id = cm.getPlayer().getId(); //角色id
	if (status == 0) {
		var text = "";
		text += "#L7#" + 猫左 + "#e#b 元宝寄售" + 猫右 + "#l\r\n\r\n";
		cm.sendSimpleS(text,2);
	} else if (status == 1) {
		if (selection == 5) { //金币
			寄售方式 = 0;
			上架手续费 = 金币手续费;

		}
		if (selection == 6) { //点卷
			寄售方式 = 1;
			上架手续费 = 点券手续费;
		}
		if (selection == 7) { //交易币
			寄售方式 = 3;
			上架手续费 = 交易币手续费;
		}
		if (selection == 8) { //交易币
			寄售方式 = 2;
			上架手续费 = 抵用手续费;
		}
		var text = "\r\n\r\n";
		text += "#L1#" + 猫下左 + " 上架#r装备栏#d" + 猫下右 + "#l\r\n\r\n";
		text += "#L2#" + 猫下左 + " 上架#r消耗栏#d" + 猫下右 + "#l\r\n\r\n";
		text += "#L3#" + 猫下左 + " 上架#r设置栏#d" + 猫下右 + "#l\r\n\r\n";
		text += "#L4#" + 猫下左 + " 上架#r其他栏#d" + 猫下右 + "#l\r\n\r\n";
		cm.sendSimpleS(text,2);
	} else if (status == 2) {
		当前上架个数 = cm.getPlayer().getBossLog3("交易行上架");
		//------------------------------------------------------------------
		var iz = cm.getPlayer().ssjyhitemb();
		if (iz != "") {
			var strs = new Array();
			strs = iz.split("@"); //字符分割

			var 卖出的货架 = 当前上架个数 - strs.length;
			if (strs.length > 0 && 卖出的货架 > 0) { //被人购买了东西
				cm.getPlayer().setBossLog3("交易行上架",-卖出的货架);
				cm.getPlayer().dropMessage(5, "获得货架+" + 卖出的货架);
				当前上架个数 = cm.getPlayer().getBossLog3("交易行上架");
			}

		}
		//------------------------------------------------------------------------------


		cc = cm.getInventory(selection).getItem(1);
		if (cc == null) {
			cm.sendOkS("你的第一格没有装备!",2);
			cm.dispose();
			return;

		}
		var pd =false;
		for(var a=0;a<不支持上架的所有东西.length;a++){
		if(cc.getItemId()==不支持上架的所有东西[a]){
		pd =true;
		break;
		}	
		}
		if(pd){
		cm.sendOkS("该物品暂时不支持上架!",2);
			cm.dispose();
			return;	
		}
		
		if (cc.getExpiration() != -1) {
			cm.sendOkS("限时装备不能上架.",2);
			cm.dispose();
		/*} else if (Packages.server.MapleItemInformationProvider.getInstance().isCash(cc.getItemId())) {
			cm.sendOkS("点装不能上架。",2);
			cm.dispose();*/
		} else if (cm.checkNOJY(cc.getFlag()) == true && 寄售方式 != 3) {
			cm.sendOkS("锁定绑定装备不能上架。",2);
			cm.dispose();	
		} else if (cm.checkNOJY2(cc.getFlag()) == true && 寄售方式 != 3) {
			cm.sendOkS("只能获取一件的装备,锁定,绑定装备不能上架。",2);
			cm.dispose();
		} else {
			if (寄售方式 == 0) {
				name = "金币寄售";

				if (cm.getMeso() < 上架手续费) {
					cm.sendOkS("错误!上架手续费不足" + 上架手续费,2);
					cm.dispose();
					return;
				}

			}
			if (寄售方式 == 1) {
				name = "点卷寄售";

				if (cm.getPlayer().getCSPoints(1) < 上架手续费) {
					cm.sendOkS("错误!上架手续费不足" + 上架手续费,2);
					cm.dispose();
					return;
				}

			}
			if (寄售方式 == 2) {
				name = "抵用寄售";

				if (cm.getPlayer().getCSPoints(2) < 上架手续费) {
					cm.sendOkS("错误!上架手续费不足" + 上架手续费,2);
					cm.dispose();
					return;
				}

			}
			if (寄售方式 == -1) {
				cm.sendOkS("错误!",2);
				cm.dispose();
				return;
			}

			if (寄售方式 == 3) {
				name = "元宝寄售";
			
			}

			cm.getPlayer().dropMessage(6, "当前已经上架 " + 当前上架个数 + " 个  最大上架个数：" + 上架最大个数);
			cm.getPlayer().dropMessage(-1, "当前已经上架 " + 当前上架个数 + " 个  最大上架个数：" + 上架最大个数);
			cm.sendYesNoS("#e#d您是否以 #r" + name + "#d 的方式\r\n\r\n上架装备 #v" + cc.getItemId() + "# * " + cc.getQuantity() + "\r\n\r\n#r#e确定上架吗? ",2);
		}
	} else if (status == 3) {
		cm.sendGetNumber("【" + name + "】请输入售价（100-20E）\r\n", 10, 10, 2000000000);
	} else if (status == 4) {
		售价 = selection;
		cm.sendYesNoS("#e#d您是否以 #r" + name + "#d 的方式\r\n\r\n上架装备 #v" + cc.getItemId() + "# * " + cc.getQuantity() + "\r\n\r\n售价: #r" + selection + "（" + NumberToChinese(selection) + "）#d\r\n\r\n#d#e确定上架吗?",2);
	} else if (status == 5) {
		if (当前上架个数 >= 上架最大个数) {
			cm.sendOkS("错误!最大上架个数 :" + 上架最大个数,2);
			cm.dispose();
			return;
		}
		///-------------------------------------------------------------
		if (cm.upitem(cc.copy(), 寄售方式, 售价) == false) {
			cm.sendOkS("上架失败",2);
			cm.dispose();
			return;
		} else {

			
			Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.server.MapleItemInformationProvider.getInstance().getInventoryTypea(cc.getItemId()), 1, cc.getQuantity(), false);
			//--------------------------------
			if (寄售方式 == 0) {

				cm.gainMeso(-上架手续费);
			}
			if (寄售方式 == 1) {

				cm.getPlayer().modifyCSPoints(1, -上架手续费, true); //给点券第一种函数
			}
			if (寄售方式 == 3) {

				cm.getPlayer().modifyCSPoints(5, -上架手续费, true); //给点券第一种函数
			}
			cm.getPlayer().setBossLog3("交易行上架",+1);
			cm.getPlayer().saveToDB(false, false);

			if (!cm.getPlayer().isGM()) {
			cm.itemlaba("<云端交易行>", "玩家 " + cm.getPlayer().getName() + " 把此宝寄售于交易行！" + name + "价：" + 售价, cc.copy(), 15);
			//cm.道具喇叭(cc.copy(), "玩家 " + cm.getPlayer().getName() + " 把此宝寄售于交易行！" + name + "价：" + 售价);
			}

			cm.sendOkS("成功上架,会在0-10分钟之内,展示出来！",2);
			cm.getPlayer().dropMessage(6, "当前已经上架 " + (当前上架个数 + 1) + " 个  最大上架个数：" + 上架最大个数);
			cm.getPlayer().dropMessage(-1, "当前已经上架 " + (当前上架个数 + 1) + " 个  最大上架个数：" + 上架最大个数);
			//cm.dispose();
			status =-1;
			return;
			//cm.openNpc(2001, 4);
		}

	}

}

function SectionToChinese(section) {
	var chnNumChar = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九"];
	var chnUnitSection = ["", "万", "亿", "万亿", "亿亿"];
	var chnUnitChar = ["", "十", "百", "千"];
	var strIns = '',
	chnStr = '';
	var unitPos = 0;
	var zero = true;
	while (section > 0) {
		var v = section % 10;
		if (v === 0) {
			if (!zero) {
				zero = true;
				chnStr = chnNumChar[v] + chnStr;
			}
		} else {
			zero = false;
			strIns = chnNumChar[v];
			strIns += chnUnitChar[unitPos];
			chnStr = strIns + chnStr;
		}
		unitPos++;
		section = Math.floor(section / 10);
	}
	return chnStr;
}

function NumberToChinese(num) {
	var chnNumChar = ["零", "一", "二", "三", "四", "五", "六", "七", "八", "九"];
	var chnUnitSection = ["", "万", "亿", "万亿", "亿亿"];
	var chnUnitChar = ["", "十", "百", "千"];
	var unitPos = 0;
	var strIns = '',
	chnStr = '';
	var needZero = false;

	if (num === 0) {
		return chnNumChar[0];
	}
	while (num > 0) {
		var section = num % 10000;
		if (needZero) {
			chnStr = chnNumChar[0] + chnStr;
		}
		strIns = SectionToChinese(section);
		strIns += (section !== 0) ? chnUnitSection[unitPos] : chnUnitSection[0];
		chnStr = strIns + chnStr;
		needZero = (section < 1000) && (section > 0);
		num = Math.floor(num / 10000);
		unitPos++;
	}

	return chnStr;
}
