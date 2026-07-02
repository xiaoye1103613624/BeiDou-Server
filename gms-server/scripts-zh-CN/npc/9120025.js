

/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/6月/28日
*/

var 感叹号0 = "#fEffect/UIWindow/Quest/icon0#";
var 感叹号1 = "#fEffect/UIWindow/Quest/icon1#";
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
var 功能名称 = "材料制作系统";
var 列表 = [
	{
		制作类型: "", F1坐标: 2147000000, 编辑1: "\t   ", 编辑2: "",
		制作列表: [
			{
				代码: 2550003, 金币: 0, 开放天数: 1, //积分箱子
				需求条件: [
					{ 物品代码: 3994753, 数量: 1 },
					{ 物品代码: 3994754, 数量: 1 },
					{ 物品代码: 3994731, 数量: 1 },
				]
			},
			{
				代码: 2350014, 金币: 0, 开放天数: 1, //灵魂收集器
				需求条件: [
					{ 物品代码: 3994753, 数量: 1 },
					{ 物品代码: 3994754, 数量: 1 },
					{ 物品代码: 3994731, 数量: 10 },
				]
			},
			{
				代码: 2048718, 金币: 0, 开放天数: 1, //仙级强化石
				需求条件: [
					{ 物品代码: 2048722, 数量: 5 },
					{ 物品代码: 3994731, 数量: 10 },
				]
			},
			{
				代码: 2590008, 金币: 0, 开放天数: 1, //特殊灵魂附魔
				需求条件: [
					{ 物品代码: 2590006, 数量: 5 },
					{ 物品代码: 3994731, 数量: 5 },
				]
			},
			{
				代码: 2048400, 金币: 0, 开放天数: 1, //传承灵器孵化器
				需求条件: [
					{ 物品代码: 2048401, 数量: 5 },
					{ 物品代码: 3994731, 数量: 88 },
				]
			},
			{
				代码: 3605012, 金币: 0, 开放天数: 1, //传承灵器孵化器
				需求条件: [
					{ 物品代码: 3700293, 数量: 10 },
					{ 物品代码: 3994731, 数量: 18 },
				]
			},


			
		]
	},
]

var max;
var 取最高值;
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
            text += " #g┗━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
            制作选择 = 列表[0].制作列表;
            text += " \t\t\t\t" + xx + "  制作内容展示  " + xx + "\r\n";
            for (var j = 0; j < 制作选择.length; j++) {
                if (制作选择[j].开放天数 >= 读取开服天数()) {
                    text += "#L" + j + "#制作:#v" + 制作选择[j].代码 + ":##z" + 制作选择[j].代码 + "##l\r\n";
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
                取最高值 = new Array();
                text += "\r\n 需要材料：\r\n";
                for (var j = 0; j < is_2.需求条件.length; j++) {
                    var is_3 = is_2.需求条件[j];
                    取最高值.push(is_3.数量);
                    if (物品数量(is_3.物品代码) < is_3.数量) {
                        检测要求 = false;
                        text += "  " + 感叹号0 + "道具：#v" + is_3.物品代码 + ":##b#z" + is_3.物品代码 + "##r" + is_3.数量 + "#d个 缺少:[#r" + (is_3.数量 - 物品数量(is_3.物品代码)) + "#d]\r\n";
                    } else {
                        text += "  " + 感叹号1 + "道具：#v" + is_3.物品代码 + ":##b#z" + is_3.物品代码 + "##r" + is_3.数量 + "#d个 当前拥有:[#r#c"+ is_3.物品代码 +"##d]个\r\n";
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
                        text += "\t请输入制作数量！单次最多制作1000个！\r\n";
                        cm.sendGetNumber(text, 1, 1, Math.floor(20000 / max));
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
            var is_2 = 制作选择[sele1];
            var 检测要求 = true;
            for (var j = 0; j < is_2.需求条件.length; j++) {
                var is_3 = is_2.需求条件[j];
                if (物品数量(is_3.物品代码) < is_3.数量 * sele2) {
                    检测要求 = false;
                    cm.sendOk("#k 大佬, 你想啥呢, 你材料都不够!");
                    status = -1;
                    return;
                }
            }
            if (检测要求 == true) {
				var totalGoldRequired = is_2.金币 * sele2;
                 // 检查玩家是否拥有足够的金币
				if (cm.getMeso() < totalGoldRequired) {
				cm.sendOk("#k您没有足够的金币来制作！本次制作需要 #r" + totalGoldRequired + "#k 金币！");
				status = -1;
				return; // 结束当前操作
				}
				// 扣除金币
				cm.gainMeso(-totalGoldRequired);
                for (var j = 0; j < is_2.需求条件.length; j++) {
                    var is_3 = is_2.需求条件[j];
                    cm.gainItem(is_3.物品代码, -is_3.数量 * sele2);
                }
                cm.gainItem(is_2.代码, 1 * sele2);
                cm.sendOk("#e#d制作完成！\r\n获得物品：#v" + is_2.代码 + ":##b#z" + is_2.代码 + "# #d数量：#r" + sele2 + "#d 个");
				var itemName = cm.getItemName(is_2.代码);
				cm.喇叭(2, "" + cm.getName() + ":在 深渊洞穴（九大陆） 制作出了 "+ itemName +"*" + sele2 + "个");
                status = -1;
                return;
            }
        }
    }
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}

/*
switch (sele1) {
	case 商店:
		break;
	case 赠送:
		break;
	case 仓库:
		break;
	case 查看:
		break;
	default:
		break;
}
//cm.sendOk("感谢光临");
//cm.sendYesNo("更换发型吗？");
//cm.sendGetNumber("#e#d请输入数量：", 1, 1, 32767);
//cm.sendSimple(txt);//用于选择
//cm.openNpc(9310065, 0);//链接NPC
//cm.sendNextPrev("（#p9120025#眯"+status+"起眼睛2）");上一页，下一页

*/

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

function 数目美化(num, length) {//数字美化
	var ul_nums = [
		"#fEffect/UIWindow/KeyConfig/key/11#",
		"#fEffect/UIWindow/KeyConfig/key/2#",
		"#fEffect/UIWindow/KeyConfig/key/3#",
		"#fEffect/UIWindow/KeyConfig/key/4#",
		"#fEffect/UIWindow/KeyConfig/key/5#",
		"#fEffect/UIWindow/KeyConfig/key/6#",
		"#fEffect/UIWindow/KeyConfig/key/7#",
		"#fEffect/UIWindow/KeyConfig/key/8#",
		"#fEffect/UIWindow/KeyConfig/key/9#",
		"#fEffect/UIWindow/KeyConfig/key/10#",
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