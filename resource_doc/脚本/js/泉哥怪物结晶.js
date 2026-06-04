

/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/7月/1日
*/

var 感叹号0 = "#fUI/UIWindow/Quest/icon0#";
var 感叹号1 = "#fUI/UIWindow/Quest/icon1#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 功能名称 = "怪物结晶提炼";

var 列表 = [
	{ 代码: 4260000, 数量: 500, 材料条件: [4000024,4000026,4000013,4000035,4000014,4000031,4000036,4000204] },
	{ 代码: 4260001, 数量: 500, 材料条件: [4000170,4000172,4000171,4000364,4000365,4000283,4000022,4000025,4000206,4000041] },
	{ 代码: 4260002, 数量: 500, 材料条件: [4000354,4000363,4000045,4000043,4000044,4000115,4000122,4000171] },
	{ 代码: 4260003, 数量: 500,  材料条件: [4000380,4000379,4000382] },
	{ 代码: 4260004, 数量: 500,  材料条件: [4000114,4000143,4000145,] },
	{ 代码: 4260005, 数量: 500,  材料条件: [4000229,4000260,4000261,] },
	{ 代码: 4260006, 数量: 30,  材料条件: [4000151,] },
	{ 代码: 4260007, 数量: 30,  材料条件: [4020009,] },
	{ 代码: 4260008, 数量: 30,  材料条件: [4000235,4000243,4000244,] },
]

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
			text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 欢迎来到:[#r" + 功能名称 + "#d]\r\n";
			text += "\t#d" + 广播 + " 想要炼制一些什么结晶呢？\r\n";
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			for (var i = 0; i < 列表.length; i++) {
				text += "#L" + i + "#制作一些:#v" + 列表[i].代码 + ":##b#z" + 列表[i].代码 + "##l#d\r\n";
			}
			cm.sendYesNo(text);
		} else if (status == 1) {
			sele1 = selection;
			is_i = 列表[sele1];
			var text = "#d\r\n";
			text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 选择炼制物品：#v" + is_i.代码 + ":##b#z" + is_i.代码 + "#\r\n";
			text += "\t#d" + 广播 + " 请选择要使用什么材料来提炼怪物结晶呢？\r\n";
			text += "\t#d" + 广播 + " 以下其中任意一种材料需要#r" + is_i.数量 + "#d个可以提炼1个#v" + is_i.代码 + ":#\r\n";
			for (var j = 0; j < is_i.材料条件.length; j++) {
				var is_j = is_i.材料条件[j];
				text += "  #L" + j + "#使用:#v" + is_j + ":##b#z" + is_j + "#[#r拥有:#b" + 物品数量(is_j) + "#d]#l\r\n";
			}
			text += "\r\n #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			cm.sendYesNo(text);
		} else if (status == 2) {
			sele2 = selection;
			var text = "#d\r\n";
			text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 选择炼制物品：#v" + is_i.代码 + ":##b#z" + is_i.代码 + "#\r\n";
			text += "\t#d" + 广播 + " 选择提炼材料：#v" + is_i.材料条件[sele2] + ":##b#z" + is_i.材料条件[sele2] + "#[#r拥有:#b" + 物品数量(is_i.材料条件[sele2]) + "#d]#l\r\n";
			text += "\r\n\t请输入制作数量！\r\n";
			text += "\r\n #k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			cm.sendGetNumber(text, 1, 1, 100);
		} else if (status == 3) {
			sele3 = selection;
			if (物品数量(is_i.材料条件[sele2]) < is_i.数量 * sele3) {
				cm.sendOk("#e#d需要材料#v" + is_i.材料条件[sele2] + ":##b#z" + is_i.材料条件[sele2] + "#\r\n条件不足！[#r拥有:#b" + 物品数量(is_i.材料条件[sele2]) + "#d / 需要:#r" + (is_i.数量 * sele3) + "#d]");
				cm.dispose();
			} else if (!cm.canHold(is_i.代码, sele3)) {
				cm.sendOk("#e#d背包空间容不下物品呢，清理一下背包再来吧！");
				cm.dispose();
			} else {
				cm.gainItem(is_i.材料条件[sele2], -is_i.数量 * sele3);
				cm.gainItem(is_i.代码, sele3);
				cm.sendOk("#e#d提炼完毕！\r\n获得物品：#v" + is_i.代码 + ":##b#z" + is_i.代码 + "# #d数量：#r" + sele3 + "#d 个");
				var itemName = cm.getItemName(is_i.代码);
				var itemName1 = cm.getItemName(is_i.材料条件[sele2]);
				cm.喇叭(1, "" + cm.getName() + ":用 " + itemName1 + " * " + is_i.数量 * sele3 + "个 提炼出了 "+ itemName +" * " + sele3 + "个");
				cm.dispose();
			}
		}
	}
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}
