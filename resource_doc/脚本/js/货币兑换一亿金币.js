/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/6月/28日
	防负/防强开/防并发版本
*/

var 感叹号0 = "#fUI/UIWindow/Quest/icon0#";
var 感叹号1 = "#fUI/UIWindow/Quest/icon1#";
var 开 = "#fUI/Basic/CheckBox/0#";
var 关 = "#fUI/Basic/CheckBox/1#";
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 是 = 2146000001;
var 否 = 2146000002;
var 返回 = 2146000003;
var 类型选择;
var 制作选择;
var 展示 = "核心界面";
var 功能名称 = "金币兑换系统";
var 列表 = [
	{
		制作类型: "", F1坐标: 2147000000, 编辑1: "\t   ", 编辑2: "",
		制作列表: [
			{
				代码: 3994731, 金币: 0, 开放天数: 1,
				需求条件: [
					{ 物品代码: 3994720, 数量: 1000 },
				]
			},
			{
				代码: 3994731, 金币: 0, 开放天数: 1,
				需求条件: [
					{ 物品代码: 3994732, 数量: 100 },
				]
			},
			{
				代码: 3994731, 金币: 0, 开放天数: 1,
				需求条件: [
					{ 物品代码: 3994730, 数量: 10 },
				]
			},
		]
	},
];

var max;
var 取最高值;

function start() {
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	/* ======== 入口防强开 ======== */
	if (cm.getPlayer() == null || cm.getPlayer().getClient() == null) {
		Packages.tools.FileoutputUtil.log("log\\玩家相关\\强开非法记录.log","[" + cm.getName() + "] 强开非法记录  mode=" + mode + " type=" + type + " selection=" + selection);
		cm.dispose();
		return;
	}

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
			text += " #g┏━#r冒险岛提示#g━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 欢迎来到 [#r" + 功能名称 + "#d]\r\n";
			text += " #g┗━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			制作选择 = 列表[0].制作列表;
			text += " \t\t\t\t" + xx + "  制作内容展示  " + xx + "\r\n";
			for (var j = 0; j < 制作选择.length; j++) {
				if (制作选择[j].开放天数 >= 读取开服天数()) {
					text += "#L" + j + "#使用 ";
					for (var k = 0; k < 制作选择[j].需求条件.length; k++) {
						var cond = 制作选择[j].需求条件[k];
						var name = cm.getItemName(cond.物品代码);
						name = 字体美化(14, name, true);
						text += "#v" + cond.物品代码 + "#" + name;
						if (k < 制作选择[j].需求条件.length - 1) text += "+";
					}
					text += " 兑换 #v" + 制作选择[j].代码 + "# #z" + 制作选择[j].代码 + "##l\r\n";
				}
			}
			text += "\r\n";
			cm.sendYesNo(text);
		} else if (status == 1) {
			sele1 = selection;
			var 检测F1坐标 = false;
			for (var i = 0; i < 列表.length; i++) {
				var is_1 = 列表[i];
				if (sele1 == is_1.F1坐标) {
					类型选择 = is_1.制作类型;
					检测F1坐标 = true;
					start();
					return;
				}
			}
			if (检测F1坐标 == false) {
				var text = "#d\r\n";
				var is_2 = 制作选择[sele1];
				var 检测要求 = true;
				text += " #g┏━#r冒险岛提示#g━━━━━━━━━━━━━━━━━━┓\r\n#d";
				text += "\t      " + 感叹号0 + "表示不满足条件 \t" + 感叹号1 + "表示满足条件    \r\n\r\n";
				text += "\t 制作道具：#v" + is_2.代码 + ":##b#z" + is_2.代码 + "##d\r\n";
				text += " #g┗━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n\r\n";
				if (is_2.金币 != 0 && is_2.金币 != null) {
					if (cm.getMeso() < is_2.金币) {
						检测要求 = false;
						text += "  " + 感叹号0 + "需要金币：[#r" + is_2.金币 + "#d] 缺少:[#r" + (is_2.金币 - cm.getMeso()) + "#d]\r\n";
					} else {
						text += "  " + 感叹号1 + "需要金币：[#r" + is_2.金币 + "#d]\r\n";
					}
				}
				取最高值 = [];
				text += "\r\n 需要材料：\r\n";
				for (var j = 0; j < is_2.需求条件.length; j++) {
					var is_3 = is_2.需求条件[j];
					取最高值.push(is_3.数量);
					if (物品数量(is_3.物品代码) < is_3.数量) {
						检测要求 = false;
						text += "  " + 感叹号0 + "道具：#v" + is_3.物品代码 + ":##b#z" + is_3.物品代码 + "##r" + is_3.数量 + "#d个 缺少:[#r" + (is_3.数量 - 物品数量(is_3.物品代码)) + "#d]\r\n";
					} else {
						text += "  " + 感叹号1 + "道具：#v" + is_3.物品代码 + ":##b#z" + is_3.物品代码 + "##r" + is_3.数量 + "#d个 当前拥有:[#r#c" + is_3.物品代码 + "##d]个\r\n";
					}
				}
				text += "\r\n";
				if (检测要求 == true) {
					if (!cm.canHold(is_2.代码, 1)) {
						text += "\t\t\t\t\t\t\t\t[#r背包空间不足#d]\r\n";
						cm.sendOk(text);
						status = -1;
						return;
					} else {
						max = Math.max.apply(null, 取最高值);
						text += "\t请输入制作数量！单次最多制作#r" + Math.floor(30000 / max) + "#d个！\r\n";
						cm.sendGetNumber(text, 1, 1, Math.floor(30000 / max));
					}
				} else {
					text += "\t\t\t\t\t\t\t\t[#r条件不满足#d]\r\n";
					cm.sendOk(text);
					status = -1;
					return;
				}
			}
		} else if (status == 2) {
			sele2 = selection;

			/* ======== 防负数/防溢出 ======== */
			if (sele2 <= 0 || sele2 > 30000) {
				Packages.tools.FileoutputUtil.log("log\\玩家相关\\强开非法记录.log", "[" + cm.getName() + "] 非法数量尝试: " + sele2 + " 时间: " + new Date());
				cm.sendOk("非法数量！");
				cm.dispose();
				return;
			}

			/* ======== 并发事务锁 ======== */
			if (cm.getPlayer().getBossLog("兑换锁") > 0) {
				Packages.tools.FileoutputUtil.log("log\\玩家相关\\强开非法记录.log", "[" + cm.getName() + "] 并发尝试绕过锁 时间: " + new Date());
				cm.sendOk("操作过于频繁，请稍后再试！");
				cm.dispose();
				return;
			}
			cm.getPlayer().setBossLog("兑换Lock", 1, 1);

			var is_2 = 制作选择[sele1];
			var totalGoldRequired = (is_2.金币 || 0) * sele2;

			/* ======== 二次实时校验 ======== */
			for (var j = 0; j < is_2.需求条件.length; j++) {
				var is_3 = is_2.需求条件[j];
				if (物品数量(is_3.物品代码) < is_3.数量 * sele2) {
					Packages.tools.FileoutputUtil.log("log\\玩家相关\\强开非法记录.log", "[" + cm.getName() + "] 材料数量异常，尝试兑换: " + sele2 + " 个，缺 " + (is_3.数量 * sele2 - 物品数量(is_3.物品代码)) + " 个 " + is_3.物品代码 + " 时间: " + new Date());
					cm.sendOk("材料不足，请重试！");
					cm.getPlayer().setBossLog("兑换Lock", -1);
					cm.dispose();
					return;
				}
			}
			if (cm.getMeso() < totalGoldRequired) {
				cm.sendOk("金币不足，请重试！");
				cm.getPlayer().setBossLog("兑换Lock", -1);
				cm.dispose();
				return;
			}
			if (!cm.canHold(is_2.代码, sele2)) {
				cm.sendOk("背包空间不足！");
				cm.getPlayer().setBossLog("兑换Lock", -1);
				cm.dispose();
				return;
			}

			/* ======== 真正的事务开始 ======== */
			cm.gainMeso(-totalGoldRequired);
			for (var j = 0; j < is_2.需求条件.length; j++) {
				var is_3 = is_2.需求条件[j];
				cm.gainItem(is_3.物品代码, -is_3.数量 * sele2);
			}
			cm.gainItem(is_2.代码, sele2);

			var itemName = cm.getItemName(is_2.代码);
			var itemName1 = cm.getItemName(is_3.物品代码);
			cm.sendOk("#e#d制作完成！\r\n获得物品：#v" + is_2.代码 + ":##b#z" + is_2.代码 + "# #d数量：#r" + sele2 + "#d 个");
			cm.喇叭(2, cm.getName() + ": 使用 " + itemName1 + " x " + is_3.数量 * sele2 + " 兑换了 " + itemName + " x" + sele2);
			cm.getPlayer().setBossLog("兑换Lock", -1);
			status = -1;      // 回到 start() 前的状态
			return;           // 等玩家点『确定』后会再次进入 action(1,0,0)
		}
	}
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}

function 读取开服天数() {
	return 1;
}

function 字体美化(length, content, boolean) {
	var str = "";
	var cs = "";
	if (content.length > length) {
		str = content;
	} else {
		for (var j = 0; j < length - content.getBytes("GB2312").length; j++) {
			cs += " ";
		}
	}
	if (boolean == true) {
		str = content + cs;
	} else {
		str = cs + content;
	}
	return str;
}