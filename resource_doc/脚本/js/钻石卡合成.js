var 感叹号0 = "#fUI/UIWindow/Quest/icon0#";
var 感叹号1 = "#fUI/UIWindow/Quest/icon1#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 是 = 2146000001;
var 否 = 2146000002;
var 返回 = 2146000003;
var 类型选择;
var 制作选择;
var 展示 = "核心界面";
var 功能名称 = "切割石制作系统";
var 列表 = [
	{
		制作类型: "副本装备展示", F1坐标: 2147000001, 编辑1: "\t\t\t  ", 编辑2: "",
		制作列表: [
			{
				装备代码: 3700069, 
				金币: 10000000, 
				开放天数: 1,
				成功率: 50, // 新增：成功率 50%
				需求条件: [
					{ 物品代码: 3700070, 数量: 2 }, // 修改：需要2个
				]
			}
		]
	},
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
			text += " #g┏━#r冒险岛提示#g━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + " 欢迎来到 [#r" + 功能名称 + "#d]\r\n";
			text += "\t#d" + 广播 + " 下方请选择您要制作的道具类型\r\n";
			text += " #g┗━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			制作选择 = 列表[0].制作列表;
			类型选择 = "副本装备展示";
			检测F1坐标 = true;
			if (类型选择 != null) {
				text += " \t\t\t\t" + xx + "  制作内容展示  " + xx + "\r\n";
				for (var j = 0; j < 制作选择.length; j++) {
					if (制作选择[j].开放天数 >= 读取开服天数()) {
						var damageDesc = "";
						if (制作选择[j].装备代码 === 1114215 || 制作选择[j].装备代码 === 1116038) {
							damageDesc = " #r(额外伤害增加%30)#d";
						}
						text += "#L" + j + "#制作:#v" + 制作选择[j].装备代码 + ":##z" + 制作选择[j].装备代码 + "##d" + damageDesc + "#l\r\n";
					}
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
				text += "\t\t\t  #L" + 返回 + "#" + xx + "[#b返回上一页#d]" + xx + "#l\r\n\r\n";
				text += "\t      " + 感叹号0 + "表示不满足条件 \t" + 感叹号1 + "表示满足条件    \r\n\r\n";
				text += "\t 制作道具：#v" + is_2.装备代码 + ":##b#z" + is_2.装备代码 + "##d\r\n";
                // 新增：显示成功率
                text += "\t 成功率：#r" + is_2.成功率 + "%#d\r\n";
				text += " #g┗━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n\r\n";
				if (is_2.金币 != 0 && is_2.金币 != null) {
					if (cm.getMeso() < is_2.金币) {
						检测要求 = false;
						text += "  " + 感叹号0 + "需要金币：[#r" + is_2.金币 + "#d] 缺少:[#r" + (is_2.金币 - cm.getMeso()) + "#d]\r\n";
					} else {
						text += "  " + 感叹号1 + "需要金币：[#r" + is_2.金币 + "#d]\r\n";
					}
				}
				text += "\r\n 需要材料：\r\n";
				for (var j = 0; j < is_2.需求条件.length; j++) {
					var is_3 = is_2.需求条件[j];
					if (物品数量(is_3.物品代码) < is_3.数量) {
						检测要求 = false;
						text += "  " + 感叹号0 + "道具：#v" + is_3.物品代码 + ":##b#z" + is_3.物品代码 + "# #r" + is_3.数量 + "#d个 缺少:[#r" + (is_3.数量 - 物品数量(is_3.物品代码)) + "#d]\r\n";
					} else {
						text += "  " + 感叹号1 + "道具：#v" + is_3.物品代码 + ":##b#z" + is_3.物品代码 + "# #r" + is_3.数量 + "#d个\r\n";
					}
				}
				text += "\r\n";
				if (检测要求 == true) {
					if (!cm.canHold(is_2.装备代码, 1)) {
						text += "\t\t\t\t\t\t\t\t[#r背包空间不足#d]\r\n";
					} else {
						text += "\t\t\t\t\t\t\t 是否要制作呢？\r\n";
						text += "\t\t\t\t\t\t";
						text += "#L" + 是 + "#" + xx + "#r是#d" + xx + "#l ";
						text += "#L" + 否 + "#" + xx + "#k否#d" + xx + "#l\r\n\r\n";
					}
				} else {
					text += "\t\t\t\t\t\t\t\t[#r条件不满足#d]\r\n";
				}
				cm.sendYesNo(text);
			}
		} else if (status == 2) {
			sele2 = selection;
			switch (sele2) {
				case 是:
					var is_2 = 制作选择[sele1];
                    
                    // 新增：概率判断逻辑
                    var successRate = is_2.成功率 || 100; // 如果没有设置成功率，默认为100%
                    var random = Math.floor(Math.random() * 100); // 生成一个0-99的随机数

					if (is_2.金币 != 0 && is_2.金币 != null) {
						cm.gainMeso(-is_2.金币);
					}
					for (var j = 0; j < is_2.需求条件.length; j++) {
						var is_3 = is_2.需求条件[j];
						cm.gainItem(is_3.物品代码, -is_3.数量);
					}

                    // 根据随机结果和成功率判断
                    if (random < successRate) {
                        // 成功
					    cm.gainItem(is_2.装备代码, 1);
					    cm.sendOk("#e#d制作成功！获得物品：#v" + is_2.装备代码 + ":##b#z" + is_2.装备代码 + "#");
					    var itemName = cm.getItemName(is_2.装备代码);
					    cm.喇叭(2, "" + cm.getName() + ":在 匠人街饰品大师 制作出了 "+ itemName +"");
                    } else {
                        // 失败
                        cm.sendOk("#e#d制作失败！材料已消耗。");
                    }
                    
					status = -1;
					return;
				case 否:
					start();
					break;
				case 返回:
					start();
					break;
				default:
					break;
			}
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

function 数目美化(num, length) {
	var ul_nums = [
		"#fUI/UIWindow/KeyConfig/key/11#",
		"#fUI/UIWindow/KeyConfig/key/2#",
		"#fUI/UIWindow/KeyConfig/key/3#",
		"#fUI/UIWindow/KeyConfig/key/4#",
		"#fUI/UIWindow/KeyConfig/key/5#",
		"#fUI/UIWindow/KeyConfig/key/6#",
		"#fUI/UIWindow/KeyConfig/key/7#",
		"#fUI/UIWindow/KeyConfig/key/8#",
		"#fUI/UIWindow/KeyConfig/key/9#",
		"#fUI/UIWindow/KeyConfig/key/10#",
	];
	var showTxt = "";
	var tempNums = num.toString().split("");
	for (var i = 0; i < tempNums.length; i++) {
		showTxt += ul_nums[parseInt(tempNums[i])];
	}
	var sss = "";
	for (var i = tempNums.length; i < length; i++) {
		sss += " ";
	}
	return sss + showTxt;
}