
//云林天宫
/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/7月/1日
*/

var 感叹 = "#fUI/UIWindow/Quest/icon0#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 箭头 = "#fUI/UIWindow/Quest/icon6/7#";

var 是 = 2147000012;
var 否 = 2147000013;
var 功能名称 = "大神殿积分商城";

var 列表 = [
	/*{
		坐标: 2140000001, 标题: "门票", 编辑A: "", 编辑B: "",
		介绍: [
			{ 编辑: "#k入场券分为3种购买模式#r点卷#k,#r抵用#k,#r金币#k" },
			{ 编辑: "#k(金币会随着等级开放进行调整)" },
		]
	},*/
	{
		坐标: 2140000002, 标题: "BOSS扩充", 编辑A: "", 编辑B: "",
		介绍: [
			{ 编辑: "#k可使用点卷增加每天可挑战次数" },
		]
	},
	{
		坐标: 2140000003, 标题: "装备分解", 编辑A: "", 编辑B: "",
		介绍: [
			{ 编辑: "分解以下装备获得对应积分" },
			{ 编辑: "#v1690000:##r#z1690000#类  每件[100积分] " },
			{ 编辑: "#v1690001:##r#z1690001#类  每件[200积分] " },
			{ 编辑: "#v1690002:##r#z1690002#类  每件[300积分] " },
			{ 编辑: "#v1690003:##r#z1690003#类  每件[400积分] " },
			{ 编辑: "#v1690004:##r#z1690004#类  每件[500积分] " },
			{ 编辑: "#v1690005:##r#z1690005#类  每件[600积分] " },
			//{ 编辑: "#k半半:简单#v1003716:#[1积分]   困难#v1003720:#[5积分]" },
			//{ 编辑: "#k小丑:简单#v1003715:#[1积分]   困难#v1003719:#[5积分]" },
			//{ 编辑: "#k女王:简单#v1003717:#[1积分]   困难#v1003721:#[5积分]" },
			//{ 编辑: "#k贝伦:简单#v1003718:#[1积分]   困难#v1003722:#[5积分]" },
			//{ 编辑: "#k贝伦:简单#v1082149:#[1积分]   困难#v1012251:#[5积分]" },
		]
	},
	{
		坐标: 2140000004, 标题: "积分商城", 编辑A: "", 编辑B: "",
		介绍: [
		    { 编辑: "#k使用积分可购买物品" },
			{ 编辑: "将我需要的装备进行分解获得对应积分" },			
		]
	},
]

var 门票 = {
	代码: 4110001, 上限: 4, 支付: [
		{ 类型: "点券", 价格: 2000, },
		{ 类型: "抵用", 价格: 4000, },
		{ 类型: "金币", 价格: 20000000, },
	]
}

var 扩充 = { 类型: "积分", 价格: 40000, 扩充Log: "神殿", 上限: 1 }; //世界树扩充 改了影响到BOSS门口NPC，要同步

var 分解 = [
	{ 代码: 1690000, 积分: 100 }, //10积分
	{ 代码: 1690001, 积分: 200 }, //20积分
	{ 代码: 1690002, 积分: 300 }, //30积分
	{ 代码: 1690003, 积分: 400 }, //40积分
	{ 代码: 1690004, 积分: 500 }, //50积分
	{ 代码: 1690005, 积分: 600 }, //60积分
	{ 代码: 1690006, 积分: 700 }, //70积分
	{ 代码: 1690007, 积分: 800 }, //80积分
	{ 代码: 1690008, 积分: 900 }, //90积分
	{ 代码: 1690009, 积分: 1000 }, //100积分
	
//	{ 代码: 1352235, 积分: 500000 }, //刷积分备用
	
]

var 商城 = [
	{ 代码: 3605011, 价格: 20000, 数量: 100, 类型: "积分" },
	{ 代码: 3605011, 价格: 200000, 数量: 1000, 类型: "积分" },
	
    { 代码: 4031952, 价格: 100, 数量: 10, 类型: "积分" },
    { 代码: 4031952, 价格: 1000, 数量: 100, 类型: "积分" },
	{ 代码: 4031952, 价格: 10000, 数量: 1000, 类型: "积分" },
	
	{ 代码: 3700294, 价格: 100000, 数量: 1, 类型: "积分" },
    { 代码: 3700294, 价格: 1000000, 数量: 10, 类型: "积分" },
	
    { 代码: 2048708, 价格: 100, 数量: 10, 类型: "积分" },
    { 代码: 2048708, 价格: 1000, 数量: 100, 类型: "积分" },
	{ 代码: 2048708, 价格: 10000, 数量: 1000, 类型: "积分" },
	
    { 代码: 4310088, 价格: 3000, 数量: 10, 类型: "积分" },
    { 代码: 4310088, 价格: 30000, 数量: 100, 类型: "积分" },
	{ 代码: 4310088, 价格: 300000, 数量: 1000, 类型: "积分" },
	
	{ 代码: 3994659, 价格: 50, 数量: 10, 类型: "积分" },
	{ 代码: 3994659, 价格: 500, 数量: 100, 类型: "积分" },
	{ 代码: 3994659, 价格: 5000, 数量: 1000, 类型: "积分" },
	
//	{ 代码: 3604010, 价格: 388888, 数量: 1, 类型: "积分" },
	
]

var 标题, 选择1;
function start() {
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
			var text = "#d\r\n";
			text += "#k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 欢迎来到:[#r" + 功能名称 + "#d]\r\n";
			text += "\t#d" + 广播 + " #k大神殿所有BOSS共享一个扩充次数\r\n";
			for (var jj = 0; jj < 列表.length; jj++) {
				if (标题 == 列表[jj].标题) {
					text += "\t#d" + 广播 + " 功能介绍：\r\n";
					for (var ii = 0; ii < 列表[jj].介绍.length; ii++) {
						text += "\t#d" + 广播 + " " + 列表[jj].介绍[ii].编辑 + "\r\n";
					}
					text += "" + 分割线() + "";
					break;
				}
			}
			for (var i = 0; i < 列表.length; i++) {
				if (标题 == 列表[i].标题) {
					text += "  " + 列表[i].编辑A + "#L" + 列表[i].坐标 + "#[#r" + 列表[i].标题 + "#d]" + 关 + "" + 列表[i].编辑B + "#l";
				} else {
					text += "  " + 列表[i].编辑A + "#L" + 列表[i].坐标 + "#[#b" + 列表[i].标题 + "#d]" + 开 + "" + 列表[i].编辑B + "#l";
				}
			}
			text += "\r\n\r\n#k┗━━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			if (标题 != null) {
				text += "" + 分割线() + "";
			}
			switch (标题) {
				case "门票": text += 购买门票A(选择1); break;
				case "BOSS扩充": text += 扩充次数A(); break;
				case "装备分解": text += 分解装备A(); break;
				case "积分商城": text += 商城购买A(选择1); break;
				default:
					break;
			}
			cm.sendYesNo(text);
		} else if (status == 1) {
			sele1 = selection;
			for (var i = 0; i < 列表.length; i++) {
				if (sele1 == 列表[i].坐标) {
					标题 = 列表[i].标题;
					选择1 = null;
					start();
					return;
				}
			}
			switch (标题) {
				case "门票": 选择1 = 购买门票B(sele1, 选择1); start(); break;
				case "BOSS扩充": 扩充次数B(); start(); break;
				case "装备分解": 分解装备B(sele1); start(); break;
				case "积分商城": 选择1 = 商城购买B(sele1, 选择1); start(); break;
					break;
				default:
					break;
			}
		}
	}
}

function 商城购买B(sele1, 选择1) {
	switch (sele1) {
		case 是:
			var 筛选 = 商城[选择1];
			更改积分(-筛选.价格);
			cm.gainItem(筛选.代码, 筛选.数量);
			var 当前积分 = 判断代价("积分"); // 获取当前积分
			cm.getPlayer().dropMessage(5, "购买完成！当前积分剩余：" + 当前积分 + "");
			选择1 = null;
			break;
		case 否:
			选择1 = null;
			break;
		default:
			选择1 = sele1;
			break;
	}
	return 选择1;
}

function 商城购买A(选择1) {
	var text = "";
	if (选择1 == null) {
		text += "\t#d" + 广播 + " 选择您要购买的商品：\r\n";
		var 当前积分 = 判断代价("积分"); // 获取当前积分
        text += "\t#d" + 广播 + " 当前积分剩余：【#r" + 当前积分 + "#d】\r\n"; // 显示当前积分
		for (var j = 0; j < 商城.length; j++) {
			var 筛选 = 商城[j];
			text += "#L" + j + "##d" + 箭头 + " " + 呈现代价(筛选.类型, 筛选.价格) + " #d购买：#v" + 筛选.代码 + ":# * " + 筛选.数量 + " 件#l\r\n";
			//text += "#L" + j + "##d" + 箭头 + " " + 呈现代价(筛选.类型, 筛选.价格) + " #d购买：#v" + 筛选.代码 + ":##l\r\n";
		}
	} else {
		var 筛选 = 商城[选择1];
		text += "\t#d" + 广播 + " " + 呈现代价(筛选.类型, 筛选.价格) + " #d购买：#v" + 筛选.代码 + ":#* " + 筛选.数量 + "\r\n\r\n";
		if (判断代价(筛选.类型) < 筛选.价格) {
			text += "\t#d" + 感叹 + " 需要：#r" + 筛选.价格 + "" + 筛选.类型 + "#d 缺少[#r" + (筛选.价格 - 判断代价(筛选.类型)) + "#d]\r\n";
		} else if (!cm.canHold(筛选.代码, 1)) {
			text += "\t#d" + 感叹 + " 背包空间不足！！！\r\n";
		} else {
			text += "\t\t\t\t\t\t\t  确定要购买吗？\r\n";
			text += "\t\t\t\t\t\t";
			text += "#L" + 是 + "#" + xx + "#r是#d" + xx + "#l  ";
			text += "#L" + 否 + "#" + xx + "#k否#d" + xx + "#l\r\n\r\n";
		}
	}
	return text;
}

function 更改积分(sum) {
	var chr = cm.getPlayer();
	sqlMultiPurpose("UPDATE characters SET ps = ps + " + sum + " WHERE id = " + chr.getId() + "");
	if (sum >= 1) {
		cm.getPlayer().dropMessage(5, "获得：" + sum + " 积分(BOSS积分商店专属)");
	} else if (sum < 0) {
		cm.getPlayer().dropMessage(5, "消费：" + sum + " 积分(BOSS积分商店专属)");
	}
}

function 分解装备B(sele1) {
	var Eq = cm.getInventory(1).getItem(sele1);
	for (var i = 0; i < 分解.length; i++) {
		if (Eq.getItemId() == 分解[i].代码) {
			更改积分(+分解[i].积分);
			cm.removeSlot(1, sele1, 1);
			break;
		}
	}
}

function 扩充次数B() {
    switch (sele1) {
        case 是:
            var Log = cm.getPlayer().getBossLog(扩充.扩充Log); // 获取当前的扩充次数
            if (Log >= 扩充.上限) {
                cm.getPlayer().dropMessage(5, "您已达扩充上限，无法继续扩充！");
            } else {
                扣除代价(扩充.类型, -扩充.价格);
                cm.getPlayer().setBossLog(扩充.扩充Log, Log + 1); // 增加扩充次数
                cm.getPlayer().dropMessage(5, "可挑战次数已增加1次！！！");
            }
            break;
        case 否:
            break;
        default:
            break;
    }
}

function 购买门票B(sele1, 选择1) {
	switch (sele1) {
		case 是:
			var 筛选 = 门票.支付[选择1];
			if (!cm.canHold(门票.代码, 1)) {
				cm.getPlayer().dropMessage(5, "您的背包满咯！！！");
			} else {
				扣除代价(筛选.类型, -筛选.价格);
				cm.gainItem(门票.代码, 1);
				cm.getPlayer().setBossLog("世界树门票", 1);//每日LOG
				cm.getPlayer().dropMessage(5, "购买完成！");
				选择1 = null;
			}
			break;
		case 否:
			选择1 = null;
			break;
		default:
			选择1 = sele1;
			break;
	}
	return 选择1;
}

function 分解装备A() {
	var text = "#d\r\n";
	text += "\t#d" + 广播 + " 选择您要分解的装备：\r\n";
	var 当前积分 = 判断代价("积分"); // 获取当前积分
    text += "\t#d" + 广播 + " 当前积分剩余：【#r" + 当前积分 + "#d】\r\n"; // 显示当前积分
	for (var i = 1, j = 1; i <= 96; i++) {
		var Eq = cm.getInventory(1).getItem(i);
		if (Eq != null) {
			if (判断装备(Eq.getItemId())) {
				text += "#L" + i + "##v" + Eq.getItemId() + ":##l";
				if (j % 6 == 0) {
					text += "\r\n";
				}
				j++;
			}
		}
	}
	return text;
}

function 判断装备(id) {
	var bool = false;
	for (var i = 0; i < 分解.length; i++) {
		if (id == 分解[i].代码) {
			bool = true;
			break;
		}
	}
	return bool;
}

function 扩充次数A() {
		    // 检查玩家是否持有会员特权道具
    var 特权道具ID = 5010019; // 假设特权道具的ID为123456，根据实际情况修改
    var 持有特权道具 = cm.haveItem(特权道具ID);

    if (!持有特权道具) {
        cm.sendOk("您需要持有会员特权道具才能进行扩充操作！");
        cm.dispose();
        return;
    }
    var Log = cm.getPlayer().getBossLog(扩充.扩充Log);
    var text = "#d";
    text += "\t#d" + 广播 + " " + 呈现代价(扩充.类型, 扩充.价格) + "购买：#r1#d次挑战次数！\r\n";
    if (Log >= 扩充.上限) {
        text += "\t#d" + 感叹 + " 每天只能购买[#b" + Log + "#d/#r" + 扩充.上限 + "#d]次！当天达到上限！\r\n";
    } else if (判断代价(扩充.类型) < 扩充.价格) {
        text += "\t#d" + 感叹 + " 需要：#r" + 扩充.价格 + "" + 扩充.类型 + "#d 缺少[#r" + (扩充.价格 - 判断代价(扩充.类型)) + "#d]\r\n";
    } else {
        text += "\t#d" + 广播 + " 每天只能购买[#b" + Log + "#d/#r" + 扩充.上限 + "#d]次！\r\n";
		text += "\t#d" + 广播 + " 扩充当前地图所有BOSS副本次数！\r\n\r\n";
        text += "\t\t\t\t\t\t\t  确定要购买吗？\r\n";
        text += "\t\t\t\t\t\t";
        text += "#L" + 是 + "#" + xx + "#r是#d" + xx + "#l  ";
        text += "#L" + 否 + "#" + xx + "#k否#d" + xx + "#l\r\n\r\n";
    }
    return text;
}

function 购买门票A(选择1) {
	var text = "";
	var Log = cm.getPlayer().getBossLog("世界树门票");
	if (选择1 == null) {
		text += "\t#d" + 广播 + " 每天可以购买[#b" + Log + "#d/#r" + 门票.上限 + "#d]次,下方请选择购买方式：\r\n";
		for (var j = 0; j < 门票.支付.length; j++) {
			var 筛选 = 门票.支付[j];
			text += "#L" + j + "##d" + 箭头 + " " + 呈现代价(筛选.类型, 筛选.价格) + "购买：#v" + 门票.代码 + ":##l\r\n";
		}
	} else {
		var 筛选 = 门票.支付[选择1];
		text += "\t#d" + 广播 + " " + 呈现代价(筛选.类型, 筛选.价格) + "购买：#v" + 门票.代码 + ":#\r\n";
		if (Log >= 门票.上限) {
			text += "\t#d" + 广播 + " 每天只能购买[#b" + Log + "#d/#r" + 门票.上限 + "#d]次！当天达到上限！\r\n";
		} else if (判断代价(筛选.类型) < 筛选.价格) {
			text += "\t#d" + 广播 + " 需要：#r" + 筛选.价格 + "" + 筛选.类型 + "#d 缺少[#r" + (筛选.价格 - 判断代价(筛选.类型)) + "#d]\r\n";
		} else {
			text += "\t\t\t\t\t\t\t  确定要购买吗？\r\n";
			text += "\t\t\t\t\t\t";
			text += "#L" + 是 + "#" + xx + "#r是#d" + xx + "#l  ";
			text += "#L" + 否 + "#" + xx + "#k否#d" + xx + "#l\r\n\r\n";
		}
	}
	return text;
}

function 扣除代价(类型, 数量) {
	switch (类型) {
		case "点券": cm.gainNX(数量); break;
		case "抵用": cm.gainDY(数量); break;
		case "金币": cm.gainMeso(数量); break;
		case "交易币": 扣除交易币(类型, 数量); cm.getPlayer().dropMessage(5, "您消费了 " + 数量 + " 交易币"); break;
		case "积分": 更改积分(数量); break;
	}
}

function 扣除交易币(类型, 数量) {
	var 账号id = cm.getPlayer().getAccountID();
	sqlMultiPurpose("UPDATE accounts SET " + 类型 + " = " + 类型 + " + " + 数量 + " WHERE id = " + 账号id + "");
}

function 判断代价(类型) {
	switch (类型) {
		case "点券": ret = cm.getPlayer().getCSPoints(1); break;
		case "抵用": ret = cm.getPlayer().getCSPoints(2); break;
		case "金币": ret = cm.getMeso(); break;
		case "交易币": ret = getAccCount(cm.getPlayer().getAccountID(), "交易币"); break;
		case "积分": ret = sqlSelect("SELECT * FROM characters WHERE id = " + cm.getPlayer().getId() + "")[0]["ps"]; break;

	}
	return ret;
}

function 呈现代价(类型, 数量) {
	var text = "";
	switch (类型) {
		case "点券": text += (数量 != 0 && 数量 != null ? "使用:#r" + 数量 + " 点券#d" : ""); break;
		case "抵用": text += (数量 != 0 && 数量 != null ? "使用:#r" + 数量 + " 抵用#d" : ""); break;
		case "金币": text += (数量 != 0 && 数量 != null ? "使用:#r" + 数量 + " 金币#d" : ""); break;
		case "交易币": text += (数量 != 0 && 数量 != null ? "使用:#r" + 数量 + " 交易币#d" : ""); break;
		case "积分": text += (数量 != 0 && 数量 != null ? "使用:#r" + 数量 + " 积分#d" : ""); break;
	}
	return text;
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}

function 判断背包空间_素组(list) {
	var text = "#e#d";
	var 检测背包 = true;
	var k1 = 0; var k2 = 0; var k3 = 0; var k4 = 0; var k5 = 0;
	for (var i = 0; i < list.length; i++) {
		var is = list[i];
		if (is.代码 >= 1000000 && is.代码 <= 1999999) { k1++; };
		if (is.代码 >= 2000000 && is.代码 <= 2999999) { k2++; };
		if (is.代码 >= 3000000 && is.代码 <= 3999999) { k3++; };
		if (is.代码 >= 4000000 && is.代码 <= 4999999) { k4++; };
		if (is.代码 >= 5000000 && is.代码 <= 5999999) { k5++; };
	}
	var 装备栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNumFreeSlot();
	var 消耗栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.USE).getNumFreeSlot();
	var 设置栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.SETUP).getNumFreeSlot();
	var 其他栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.ETC).getNumFreeSlot();
	var 现金栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.CASH).getNumFreeSlot();
	if (装备栏空位 < k1) { 检测背包 = false; text += " 请确保#r装备#d栏有 #r" + k1 + "#d 空间以上！\r\n"; };
	if (消耗栏空位 < k2) { 检测背包 = false; text += " 请确保#r消耗#d栏有 #r" + k2 + "#d 空间以上！\r\n"; };
	if (设置栏空位 < k3) { 检测背包 = false; text += " 请确保#r设置#d栏有 #r" + k3 + "#d 空间以上！\r\n"; };
	if (其他栏空位 < k4) { 检测背包 = false; text += " 请确保#r其他#d栏有 #r" + k4 + "#d 空间以上！\r\n"; };
	if (现金栏空位 < k5) { 检测背包 = false; text += " 请确保#r现金#d栏有 #r" + k5 + "#d 空间以上！\r\n"; };
	return ret = { bool: 检测背包, text: text };
}

function 判断背包空间_单个(itemid) {
	var text = "#e#d";
	var 检测背包 = true;
	var k1 = 0; var k2 = 0; var k3 = 0; var k4 = 0; var k5 = 0;
	if (itemid >= 1000000 && itemid <= 1999999) { k1++; };
	if (itemid >= 2000000 && itemid <= 2999999) { k2++; };
	if (itemid >= 3000000 && itemid <= 3999999) { k3++; };
	if (itemid >= 4000000 && itemid <= 4999999) { k4++; };
	if (itemid >= 5000000 && itemid <= 5999999) { k5++; };
	var 装备栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.EQUIP).getNumFreeSlot();
	var 消耗栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.USE).getNumFreeSlot();
	var 设置栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.SETUP).getNumFreeSlot();
	var 其他栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.ETC).getNumFreeSlot();
	var 现金栏空位 = cm.getChar().getInventory(Packages.client.inventory.MapleInventoryType.CASH).getNumFreeSlot();
	if (装备栏空位 < k1) { 检测背包 = false; text += " 请确保#r装备#d栏有 #r" + k1 + "#d 空间以上！\r\n"; };
	if (消耗栏空位 < k2) { 检测背包 = false; text += " 请确保#r消耗#d栏有 #r" + k2 + "#d 空间以上！\r\n"; };
	if (设置栏空位 < k3) { 检测背包 = false; text += " 请确保#r设置#d栏有 #r" + k3 + "#d 空间以上！\r\n"; };
	if (其他栏空位 < k4) { 检测背包 = false; text += " 请确保#r其他#d栏有 #r" + k4 + "#d 空间以上！\r\n"; };
	if (现金栏空位 < k5) { 检测背包 = false; text += " 请确保#r现金#d栏有 #r" + k5 + "#d 空间以上！\r\n"; };
	return ret = { bool: 检测背包, text: text };
}

function 分割线() {
	var text = " ";
	var list = [
		//{ UI: "#fEffect/CharacterEff.img/1022223/3/0#", length: 21 },
		//{ UI: "#fEffect/CharacterEff.img/1022223/4/0#", length: 21 },
		//{ UI: "#fEffect/CharacterEff.img/1022223/6/0#", length: 31 },
		{ UI: "#fEffect/CharacterEff.img/1022223/7/0#", length: 21 },
		{ UI: "#fEffect/CharacterEff.img/1022223/8/0#", length: 22 },
		{ UI: "#fEffect/CharacterEff.img/1022223/9/0#", length: 24 },
	];
	var random = Math.floor(Math.random() * list.length);
	for (var i = 0; i < list[random].length; i++) {
		//var random = Math.floor(Math.random() * list.length);
		text += list[random].UI;
	}
	text += "\r\n";
	return text;
}

function 更改呈现奖励(类型, 数量) {
	switch (类型) {
		case "点券":
			if (数量 != 0 && 数量 != null) { cm.gainNX(数量) };
			break;
		case "抵用":
			if (数量 != 0 && 数量 != null) { cm.gainDY(数量) };
			break;
		case "金币":
			if (数量 != 0 && 数量 != null) { cm.gainMeso(数量) };
			break;
		case "经验":
			if (数量 != 0 && 数量 != null) { cm.gainExp(数量) };
			break;
		default:
			break;
	}
}

function 呈现奖励货币(类型, 数量) {
	var 章鱼 = "#fUI/UIWindow/Minigame/Omok/stone/3/white/0#";
	var 蘑菇 = "#fUI/UIWindow/Minigame/Omok/stone/0/black/0#";
	var 绿水 = "#fUI/UIWindow/Minigame/Omok/stone/1/white/0#";
	var 猪猪 = "#fUI/UIWindow/Minigame/Omok/stone/2/black/0#";
	var text = "";
	switch (类型) {
		case "点券":
			text += (数量 != 0 && 数量 != null ? "" + 章鱼 + "奖励: #r" + 数量 + "#d点券" : "");
			break;
		case "抵用":
			text += (数量 != 0 && 数量 != null ? "" + 蘑菇 + "奖励: #r" + 数量 + "#d抵用" : "");
			break;
		case "金币":
			text += (数量 != 0 && 数量 != null ? "" + 绿水 + "奖励: #r" + 数量 + "#d金币" : "");
			break;
		case "经验":
			text += (数量 != 0 && 数量 != null ? "" + 猪猪 + "奖励: #r" + 数量 + "#d经验" : "");
			break;
		default:
			break;
	}
	return text;
}

function sqlSelect(sql) {
	var con = cm.getConnection();
	var ret = new Array();
	var ps = con.prepareStatement(sql);
	var rs = ps.executeQuery();
	var metaData = ps.getMetaData();
	while (rs.next()) {
		var rsdata = new java.util.HashMap();
		for (var j = 1; j <= metaData.getColumnCount(); j++) {
			columnLabel = metaData.getColumnLabel(j);
			rsdata.put(columnLabel, rs.getObject(columnLabel));
		}
		if (!rsdata.isEmpty()) {
			ret.push(rsdata);
		}
	}
	rs.close();
	ps.close();
	con.close();
	return ret;
}

function sqlMultiPurpose(sql) {//
	var con = cm.getConnection();
	var ps = con.prepareStatement(sql);
	ret = ps.executeUpdate();
	ps.close();
	con.close();
	return ret;
}