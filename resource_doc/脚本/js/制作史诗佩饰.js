/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/6月/28日
*/

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
var 功能名称 = "佩饰进阶系统";
var 列表 = [
	{
		制作类型: "副本装备展示", F1坐标: 2147000001, 编辑1: "\t\t\t  ", 编辑2: "",
		制作列表: [
			{
				装备代码: 1022320, 点券: 3000000, 开放天数: 1,
				需求条件: [
					{ 物品代码: 3994731, 数量: 100 },
					{ 物品代码: 1022319, 数量: 1 },
					{ 物品代码: 2048402, 数量: 10 },
					{ 物品代码: 4001245, 数量: 100 },
					{ 物品代码: 4310020, 数量: 3000 },
				],
				仙级: 20  // 圣人
			},
			
			{
				装备代码: 1012759, 点券: 3000000, 开放天数: 1,
				需求条件: [
					{ 物品代码: 3994731, 数量: 100 },
					{ 物品代码: 1012758, 数量: 1 },
					{ 物品代码: 2048402, 数量: 10 },
					{ 物品代码: 4310020, 数量: 3000 },
					{ 物品代码: 4001245, 数量: 100 },
				],
				仙级: 20  // 圣人
			},
			
			{
				装备代码: 1122170, 点券: 3000000, 开放天数: 1,
				需求条件: [
					{ 物品代码: 3994731, 数量: 100 },
					{ 物品代码: 1122169, 数量: 1 },
					{ 物品代码: 2048402, 数量: 10 },
					{ 物品代码: 4310020, 数量: 3000 },
					{ 物品代码: 4001245, 数量: 100 },
				],
				仙级: 20  // 圣人
			},
			
			{
				装备代码: 1032334, 点券: 3000000, 开放天数: 1,
				需求条件: [
					{ 物品代码: 3994731, 数量: 100 },
					{ 物品代码: 1032333, 数量: 1 },
					{ 物品代码: 2048402, 数量: 10 },
					{ 物品代码: 4310020, 数量: 3000 },
					{ 物品代码: 4001245, 数量: 100 },
				],
				仙级: 20  // 圣人
			},
			

			

			
			
			
			
			
			
			
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
			text += "\t#d" + 广播 + " 进阶需要达到指定的仙级\r\n";
			text += "\t#d" + 广播 + " 下方请选择您要进阶的道具类型\r\n";
			text += "\t#d" + 广播 + " #r配饰四件激活套装属性：#b + 10% 额外伤害\r\n";
			text += " #g┗━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			制作选择 = 列表[0].制作列表;
			类型选择 = "副本装备展示";
			检测F1坐标 = true;
			if (类型选择 != null) {
				text += " \t\t\t\t" + xx + "  制作内容展示  " + xx + "\r\n";
				for (var j = 0; j < 制作选择.length; j++) {
					if (制作选择[j].开放天数 >= 读取开服天数()) {
						text += "#L" + j + "#进阶:#v" + 制作选择[j].装备代码 + ":##z" + 制作选择[j].装备代码 + "##r[全属性+500]#d#l\r\n";
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
				text += "\t 制作道具：#v" + is_2.装备代码 + ":##b#z" + is_2.装备代码 + "##r[全属性+500]#d\r\n";
				text += " #g┗━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n\r\n";
				if (is_2.点券 != 0 && is_2.点券 != null) {
					if (cm.getNX(1) < is_2.点券) {
						检测要求 = false;
						text += "  " + 感叹号0 + "需要点券：[#r" + is_2.点券 + "#d] 缺少:[#r" + (is_2.点券 - cm.getNX(1)) + "#d]\r\n";
					} else {
						text += "  " + 感叹号1 + "需要点券：[#r" + is_2.点券 + "#d]\r\n";
					}
				}
				text += "\r\n 需要材料：\r\n";
				for (var j = 0; j < is_2.需求条件.length; j++) {
					var is_3 = is_2.需求条件[j];
					if (物品数量(is_3.物品代码) < is_3.数量) {
						检测要求 = false;
						text += "  " + 感叹号0 + "道具：#v" + is_3.物品代码 + ":##b#z" + is_3.物品代码 + "# #r" + is_3.数量 + "#d个 缺少:[#r" + (is_3.数量 - 物品数量(is_3.物品代码)) + "#d]\r\n";
					} else {
						text += "  " + 感叹号1 + "道具：#v" + is_3.物品代码 + ":##b#z" + is_3.物品代码 + "# #r" + is_3.数量 + "#d个\r\n\r\n";
					}
				}
				// 检查仙级条件
				if (is_2.仙级 != null && is_2.仙级 != 0) {
					var 当前仙级 = getxmwnjlc("XM飞升系统_仙级");
					var 需要仙级名称 = 仙级名称[is_2.仙级];
					var 当前仙级名称 = 仙级名称[当前仙级];
					if (当前仙级 < is_2.仙级) {
						检测要求 = false;
						text += "  " + 感叹号0 + "需要达到仙级：[#r" + 需要仙级名称 + "#d] 当前仙级：[#r" + 当前仙级名称 + "#d]\r\n";
					} else {
						text += "  " + 感叹号1 + "当前仙级：[#r" + 当前仙级名称 + "#d]\r\n";
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
					if (is_2.点券 != 0 && is_2.点券 != null) {
						cm.给点券(-is_2.点券);
					}
					for (var j = 0; j < is_2.需求条件.length; j++) {
						var is_3 = is_2.需求条件[j];
						cm.gainItem(is_3.物品代码, -is_3.数量);
					}
					//cm.gainItem(is_2.装备代码, 1);    //这个是不上锁
					cm.gainItem(is_2.装备代码,1, 1, 500, 500, 500, 500, 0, 0, 500, 500, 0, 0, 0, 0, 0, 0);
					cm.sendOk("#e#d制作完成！获得物品：#v" + is_2.装备代码 + ":##b#z" + is_2.装备代码 + "#");
					var itemName = cm.getItemName(is_2.装备代码);
					var itemName1 = cm.getItemName(is_3.物品代码);
			//		cm.喇叭(2, "" + cm.getName() + ": 用 " + itemName1 + " 成功进阶 " + itemName + "");
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

// 检测玩家当前仙级
function getxmwnjlc(log) {
    return getxmwnjljsc(log);
}

// 仙级名称列表
var 仙级名称 = [
    "凡人", "筑基", "金丹", "元婴", "出窍", "分神", "合体", "渡劫", 
    "大乘", "天仙", "仙君", "玄仙", "仙帝", "神人", "神将", "神君", 
    "神帝", "神皇", "神尊", "圣人", "至尊", "主宰", "永恒", "创世", 
	"超脱"
];

// 检测玩家当前仙级的具体实现
function getxmwnjljsc(jiluid) {
    var xmsjfh = 0;
    zhjsid = cm.getPlayer().getId();
    var conn = cm.getConnection();
    var sql = "SELECT * FROM xmwnjl WHERE characterid = " + zhjsid + " AND bossid = '" + jiluid + "' ;";
    var pstmt = conn.prepareStatement(sql);
    var result = pstmt.executeQuery();		
    if (result.next()) {
        xmsjfh = result.getInt("count");
    } 
    result.close();
    pstmt.close();
    conn.close();
    return xmsjfh;
}