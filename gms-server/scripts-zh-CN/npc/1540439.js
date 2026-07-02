

/*
	作者：狗哥
	QQ联系：1418181168
	制作时间：2022年/7月/2日
*/

//cm.sendGetNumber("#e#d请输入数量：", 1, 1, 32767);
//cm.sendSimple(txt);//用于选择
//cm.sendNextPrev("（#p9120025#眯"+status+"起眼睛2）");上一页，下一页
//获取 = #fUI/UIWindow.img/QuestIcon/4/0#;
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 分割线 = "#fUI/Login.img/WorldSelect/channel/chgauge1#";
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 未完成 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 已完成 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
//cm.喇叭(4, "[冲级活动]", "恭喜 ");//红色黄底 喇叭
//cm.全服漂浮喇叭("【星星提高】：恭喜玩家 [" + cm.getPlayer().getName() + "] ！", 5121009);

var 锁定数目 = 0;
var 是 = 2146000002;
var 否 = 2146000003;

var 潜能控制 = [
	{
		装备词条数量: 0,//装备没有词条时触发
		出现词条列表: [
			{ 出现词条: 3, 概率: 20 },
			{ 出现词条: 2, 概率: 100 },
			{ 出现词条: 1, 概率: 1000 },
		],
		鉴定消耗: [
			{ 代码: 2711003, 数量: 1 },
		]
	},
	{
		装备词条数量: 1,//装备一条词条时触发
		出现词条列表: [
			{ 出现词条: 3, 概率: 100 },
			{ 出现词条: 2, 概率: 300 },
			{ 出现词条: 1, 概率: 1000 },
		],
		鉴定消耗: [
			{ 代码: 2711003, 数量: 1 },
		]
	},
	{
		装备词条数量: 2,//装备两条词条时触发
		出现词条列表: [
			{ 出现词条: 3, 概率: 200 },
			{ 出现词条: 2, 概率: 1000 },
		],
		鉴定消耗: [
			{ 代码: 2711003, 数量: 1 },
		]
	},
	{
		装备词条数量: 3,//装备三条词条时触发
		出现词条列表: [
			{ 出现词条: 3, 概率: 1000 },
		],
		鉴定消耗: [
			{ 代码: 2711003, 数量: 1 },
		]
	},
]

var 重铸消耗 = [
	{
		锁定词条数量: 1, 消耗条件: [
			{ 代码: 4321012, 数量: 1 },
			{ 代码: 2711003, 数量: 1 },
		]
	},//锁定1条词条的要求
	{
		锁定词条数量: 2, 消耗条件: [
			{ 代码: 4321013, 数量: 1 },
			{ 代码: 2711003, 数量: 1 },
		]
	}//锁定2条词条的要求
]

var 继承消耗 = [
	{
		词条数量: 1,
		消耗条件: [
			{ 代码: 2711003, 数量: 50 },
		]
	},
	{
		词条数量: 2,
		消耗条件: [
			{ 代码: 2711003, 数量: 100 },
		]
	},
	{
		词条数量: 3,
		消耗条件: [
			{ 代码: 2711003, 数量: 150 },
		]
	},
]

var list = [
/*	{
		物品: 4321011, 购买类型: "金币",
		金币: 20000000, 抵用: 0, 点券: 0,
		材料: [
			//{ 代码: 4000000, 数量: 10 },
		]
	},
	{
		物品: 4321011, 购买类型: "点券",
		金币: 0, 抵用: 0, 点券: 500,
		材料: [
			//{ 代码: 4000000, 数量: 10 },
		]
	},
	*/
	{
		物品: 4321012, 购买类型: "金币+材料",    //A锁购买
		金币: 10000000, 抵用: 0, 点券: 0,
		材料: [
			{ 代码: 2711003, 数量: 2 },
		]
	},
/*	{
		物品: 4321012, 购买类型: "点券",         //A锁购买
		金币: 0, 抵用: 0, 点券: 2500,
		材料: [
			//{ 代码: 4000000, 数量: 10 },
		]
	},*/
	
	{
		物品: 4321013, 购买类型: "金币+材料",     //S锁购买
		金币: 20000000, 抵用: 0, 点券: 0,
		材料: [
			{ 代码: 4321012, 数量: 2 },
		]
	},
/*	{
		物品: 4321013, 购买类型: "点券",        //S锁购买
		金币: 0, 抵用: 0, 点券: 5000,
		材料: [
			//{ 代码: 4321026, 数量: 10 },
		]
	},
	
	{
		物品: 2029132, 购买类型: "点券",
		金币: 0, 抵用: 0, 点券: 5000,
		材料: [
			//{ 代码: 4321026, 数量: 10 },
		]
	},*/
]

var 坐标列表 = [
	{
		坐标: 2147000000, 标题: "鉴定", 展示: "潜能属性",
		内容: [
			{ 编辑: "该功能主要给装备随机鉴定/重洗词条。" },
			{ 编辑: "有概率出现 1 - 3 条属性词条。" },
			{ 编辑: "装备词条数量重洗后不会低于本身词条数量。" },
			{ 编辑: "装备鉴定或者重洗，需要将装备放置在第一个格！" },
		]
	},
	{
		坐标: 2147000001, 标题: "重铸", 展示: "重铸词条", 内容: [
			{ 编辑: "该功能主要给装备重铸词条。" },
			{ 编辑: "该功能必须有2条属性词条以上的装备才能使用！" },
			{ 编辑: "2条属性词条：最多可锁定1条属性词条。" },
			{ 编辑: "3条属性词条：最多可锁定2条属性词条。" },
		]
	},
	{
		坐标: 2147000002, 标题: "继承", 展示: "继承词条", 内容: [
			{ 编辑: "该功能主要将装备词条继承到另外一件装备。" },
			{ 编辑: "#r提示⑴：将第一格装备词条继承到第二格装备上！#d" },
			{ 编辑: "#r提示⑵：继承后第一格装备词条将会全部消失！#d" },
			{ 编辑: "#r提示⑶：继承后第二格装备词条将会被覆盖！#d" },
			{ 编辑: "#r提示⑷：继承装备必须是同一种类型的装备！#d" },
		]
	},
	{
		坐标: 2147000003, 标题: "购买", 展示: "购买商品", 内容: [
			//{ 编辑: "" },
		]
	},
	{
		坐标: 2147000004, 标题: "属性展示", 展示: "词条展示", 内容: [
			//{ 编辑: "" },
		]
	},
]

var 词条展示 = [
	{ 编辑: "(#r1 #d- #r5#d)力：+ #r10  #d~  #r50#d 力量" },
	{ 编辑: "(#r1 #d- #r5#d)敏：+ #r10  #d~  #r50#d 敏捷" },
	{ 编辑: "(#r1 #d- #r5#d)智：+ #r10  #d~  #r50#d 智力" },
	{ 编辑: "(#r1 #d- #r5#d)运：+ #r10  #d~  #r50#d 运气" },
	{ 编辑: "(#r1 #d- #r5#d)攻：+ #r10  #d~  #r50#d 攻击力" },
	{ 编辑: "(#r1 #d- #r5#d)魔：+ #r20  #d~  #r100#d 魔法力" },
	{ 编辑: "(#r1 #d- #r5#d)血：+ #r100  #d~  #r500#d Hp" },
	{ 编辑: "(#r1 #d- #r5#d)蓝：+ #r100  #d~  #r500#d Mp" },
	{ 编辑: "(#r1 #d- #r5#d)防：+ #r10  #d~  #r50#d 物/魔防御" },
	{ 编辑: "(#r1 #d- #r5#d)准：+ #r10  #d~  #r50#d 命中率" },
	{ 编辑: "(#r1 #d- #r5#d)闪：+ #r10  #d~  #r50#d 回避率" },
	{ 编辑: "(#r1 #d- #r5#d)速：+ #r2  #d~  #r10#d 移动速度" },
	{ 编辑: "(#r1 #d- #r5#d)跳：+ #r2  #d~  #r10#d 跳跃力" },
	{ 编辑: "(#r1 #d- #r5#d)全：+ #r10  #d~  #r50#d 四维属性" },
]

//⑴ ⑵ ⑶ ⑷ ⑸ ⑹ ⑺ ⑻ ⑼ ⑽ ⑾ ⑿ ⒀ ⒁ ⒂ ⒃ ⒄ ⒅ ⒆ ⒇
var 潜能属性 = [//概率 千分比
	{ 名: "⑴力", 概率: 1000, 属性: 10 }, { 名: "⑵力", 概率: 800, 属性: 20 }, { 名: "⑶力", 概率: 600, 属性: 30 }, { 名: "⑷力", 概率: 400, 属性: 40 }, { 名: "⑸力", 概率: 200, 属性: 50 },
	{ 名: "⑴敏", 概率: 1000, 属性: 10 }, { 名: "⑵敏", 概率: 800, 属性: 20 }, { 名: "⑶敏", 概率: 600, 属性: 30 }, { 名: "⑷敏", 概率: 400, 属性: 40 }, { 名: "⑸敏", 概率: 200, 属性: 50 },
	{ 名: "⑴智", 概率: 1000, 属性: 10 }, { 名: "⑵智", 概率: 800, 属性: 20 }, { 名: "⑶智", 概率: 600, 属性: 30 }, { 名: "⑷智", 概率: 400, 属性: 40 }, { 名: "⑸智", 概率: 200, 属性: 50 },
	{ 名: "⑴运", 概率: 1000, 属性: 10 }, { 名: "⑵运", 概率: 800, 属性: 20 }, { 名: "⑶运", 概率: 600, 属性: 30 }, { 名: "⑷运", 概率: 400, 属性: 40 }, { 名: "⑸运", 概率: 200, 属性: 50 },
	{ 名: "⑴攻", 概率: 1000, 属性: 10 }, { 名: "⑵攻", 概率: 800, 属性: 20 }, { 名: "⑶攻", 概率: 600, 属性: 30 }, { 名: "⑷攻", 概率: 400, 属性: 40 }, { 名: "⑸攻", 概率: 200, 属性: 50 },
	{ 名: "⑴魔", 概率: 1000, 属性: 20 }, { 名: "⑵魔", 概率: 800, 属性: 40 }, { 名: "⑶魔", 概率: 600, 属性: 60 }, { 名: "⑷魔", 概率: 400, 属性: 80 }, { 名: "⑸魔", 概率: 200, 属性: 100 },
	{ 名: "⑴血", 概率: 1000, 属性: 100 }, { 名: "⑵血", 概率: 800, 属性: 200 }, { 名: "⑶血", 概率: 600, 属性: 300 }, { 名: "⑷血", 概率: 400, 属性: 400 }, { 名: "⑸血", 概率: 200, 属性: 500 },
	{ 名: "⑴蓝", 概率: 1000, 属性: 100 }, { 名: "⑵蓝", 概率: 800, 属性: 200 }, { 名: "⑶蓝", 概率: 600, 属性: 300 }, { 名: "⑷蓝", 概率: 400, 属性: 400 }, { 名: "⑸蓝", 概率: 200, 属性: 500 },
	{ 名: "⑴防", 概率: 1000, 属性: 10 }, { 名: "⑵防", 概率: 800, 属性: 20 }, { 名: "⑶防", 概率: 600, 属性: 30 }, { 名: "⑷防", 概率: 400, 属性: 40 }, { 名: "⑸防", 概率: 200, 属性: 50 },
	{ 名: "⑴准", 概率: 1000, 属性: 10 }, { 名: "⑵准", 概率: 800, 属性: 20 }, { 名: "⑶准", 概率: 600, 属性: 30 }, { 名: "⑷准", 概率: 400, 属性: 40 }, { 名: "⑸准", 概率: 200, 属性: 50 },
	{ 名: "⑴闪", 概率: 1000, 属性: 10 }, { 名: "⑵闪", 概率: 800, 属性: 20 }, { 名: "⑶闪", 概率: 600, 属性: 30 }, { 名: "⑷闪", 概率: 400, 属性: 40 }, { 名: "⑸闪", 概率: 200, 属性: 50 },
	{ 名: "⑴速", 概率: 1000, 属性: 2 }, { 名: "⑵速", 概率: 800, 属性: 4 }, { 名: "⑶速", 概率: 600, 属性: 6 }, { 名: "⑷速", 概率: 400, 属性: 8 }, { 名: "⑸速", 概率: 200, 属性: 10 },
	{ 名: "⑴跳", 概率: 1000, 属性: 2 }, { 名: "⑵跳", 概率: 800, 属性: 4 }, { 名: "⑶跳", 概率: 600, 属性: 6 }, { 名: "⑷跳", 概率: 400, 属性: 8 }, { 名: "⑸跳", 概率: 200, 属性: 10 },
	{ 名: "⑴全", 概率: 1000, 属性: 10 }, { 名: "⑵全", 概率: 800, 属性: 20 }, { 名: "⑶全", 概率: 600, 属性: 30 }, { 名: "⑷全", 概率: 400, 属性: 40 }, { 名: "⑸全", 概率: 200, 属性: 50 },
]

var 限制装备 = [
	["<帽子>", 100, 100], ["<上衣>", 104, 104], ["<套服>", 105, 105], ["<裤子>", 106, 106], ["<鞋子>", 107, 107], ["<手套>", 108, 108], ["<披风>", 110, 110],
	["<脸饰>", 101, 101], ["<眼饰>", 102, 102], ["<耳环>", 103, 103], ["<吊坠>", 112, 112], ["<腰带>", 113, 113], ["<武器>", 130, 149], ["<盾牌>", 109, 109],
]

var 展示;
var 坐标;
var costitem;
var 功能名称 = "装备鉴定系统";

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
			var text = "#d";
			var Eq = cm.getInventory(1).getItem(1);
			text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#d" + 广播 + ": 欢迎来到 [#r" + 功能名称 + "#d]\r\n";
			for (var i = 0; i < 坐标列表.length; i++) {
				var i坐标 = 坐标列表[i];
				if (坐标 == i坐标.坐标) {
					for (var j = 0; j < i坐标.内容.length; j++) {
						text += "\t#d" + 广播 + ": " + i坐标.内容[j].编辑 + "\r\n";
					}
					break;
				}
			}
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n  ";
			for (var k = 0; k < 坐标列表.length; k++) {
				var k坐标 = 坐标列表[k];
				if (坐标 == k坐标.坐标) {
					text += "#L" + k坐标.坐标 + "#[#r" + k坐标.标题 + "#d]" + 关 + "#l ";
				} else {
					text += "#L" + k坐标.坐标 + "#[#b" + k坐标.标题 + "#d]" + 开 + "#l ";
				}
				if ((k + 1) % 4 == 0) {
					text += "\r\n\t\t\t\t";
				}
			}
			text += "\r\n\r\n";
			text += "   " + 分割线 + " " + 分割线 + " " + 分割线 + "\r\n";
			if (Eq == null) {
				text += "\t#d" + 广播 + ": 您装备栏第一个格子没有放置装备呢！\r\n\t ";
			} else if (cm.isCash(Eq.getItemId())) {
				text += "\t#d" + 广播 + ": 您装备栏第一个格#r是时装#d！无法使用该功能！\r\n";
			} else {
				switch (展示) {
					case "词条展示":
						text += "\r\n";
						for (var kk = 0; kk < 词条展示.length; kk++) {
							text += "\t#d" + 广播 + ": " + 词条展示[kk].编辑 + "  \r\n";
						}
						break;
					case "潜能属性":
						var Eq = cm.getInventory(1).getItem(1);
						if (Eq == null) {
							text += "\t#d" + 广播 + ": 您装备栏第一个格子必须存放以下类型装备：\r\n\t ";
							for (var l = 0; l < 限制装备.length; l++) {
								text += "#b" + 限制装备[l][0] + "#d";
								if ((l + 1) % 7 == 0) {
									text += "\r\n\t ";
								}
							}
						} else {
							var Inventory = Math.floor(Eq.getItemId() / 10000);
							var 匹配检测 = false;
							var 匹配类型 = "";
							for (var l = 0; l < 限制装备.length; l++) {
								if (Inventory >= 限制装备[l][1] && Inventory <= 限制装备[l][2]) {
									匹配类型 += 限制装备[l][0];
									匹配检测 = true;
									break;
								}
							}
							if (匹配检测 == false) {
								text += "\t#d" + 广播 + ": 您装备栏第一个格子必须存放以下类型装备：\r\n\t ";
								for (var l = 0; l < 限制装备.length; l++) {
									text += "#b" + 限制装备[l][0] + "#d";
									if ((l + 1) % 7 == 0) {
										text += "\r\n\t ";
									}
								}
							} else {
								text += "\t#d" + 广播 + ": 检测到符合鉴定的装备类型#b" + 匹配类型 + "#d\r\n";
								text += "\t#d" + 广播 + ": 装备展示：#v" + Eq.getItemId() + "#\r\n";
								var 词条 = 读取潜能词条内容(1);
								for (var m = 0; m < 词条.length; m++) {
									switch (m) {
										case 0:
											text += "\t#d" + 广播 + ": 第#r一#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]\r\n";
											break;
										case 1:
											text += "\t#d" + 广播 + ": 第#b二#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]\r\n";
											break;
										case 2:
											text += "\t#d" + 广播 + ": 第#k三#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]\r\n";
											break;
										default:
											break;
									}
								}
								var 检测条件 = true;
								var 条件提示 = "\r\n\t#d" + 广播 + ": 潜能需要条件：#d\r\n";
								costitem = new Array();
								var sum = 读取潜能词条数量(1);
								for (var n = 0; n < 潜能控制[sum].鉴定消耗.length; n++) {
									var itemsum = 物品数量(潜能控制[sum].鉴定消耗[n].代码);
									costitem.push({ 代码: 潜能控制[sum].鉴定消耗[n].代码, 数量: 潜能控制[sum].鉴定消耗[n].数量 });
									if (itemsum < 潜能控制[sum].鉴定消耗[n].数量) {
										检测条件 = false;
									}
									条件提示 += "\t#d  消耗材料：#v" + 潜能控制[sum].鉴定消耗[n].代码 + ":# [拥有:#b" + itemsum + "#d/需要:#r" + 潜能控制[sum].鉴定消耗[n].数量 + "#d]\r\n";
								}
								if (检测条件 == true) {
									text += 条件提示;
									text += "\r\n\t\t\t\t\t\t\t  是否要鉴定呢？\r\n";
									text += "\t\t\t\t\t\t";
									text += "#L" + 是 + "#" + xx + "#r是#d" + xx + "#l  ";
									text += "#L" + 否 + "#" + xx + "#k否#d" + xx + "#l\r\n\r\n";
								} else {
									text += 条件提示;
									text += "\t\t\t\t\t\t\t状态：[#r条件不满足#d]\r\n";
								}
							}
						}
						break;
					case "重铸词条":
						var Eq = cm.getInventory(1).getItem(1);
						if (Eq == null) {
							text += "\t#d" + 广播 + ": 您装备栏第一个格子必须存放以下类型装备：\r\n\t ";
							for (var l = 0; l < 限制装备.length; l++) {
								text += "#b" + 限制装备[l][0] + "#d";
								if ((l + 1) % 7 == 0) {
									text += "\r\n\t ";
								}
							}
						} else {
							var Inventory = Math.floor(Eq.getItemId() / 10000);
							var 匹配检测 = false;
							var 匹配类型 = "";
							for (var l = 0; l < 限制装备.length; l++) {
								if (Inventory >= 限制装备[l][1] && Inventory <= 限制装备[l][2]) {
									匹配类型 += 限制装备[l][0];
									匹配检测 = true;
									break;
								}
							}
							if (匹配检测 == false) {
								text += "\t#d" + 广播 + ": 您装备栏第一个格子必须存放以下类型装备：\r\n\t ";
								for (var l = 0; l < 限制装备.length; l++) {
									text += "#b" + 限制装备[l][0] + "#d";
									if ((l + 1) % 7 == 0) {
										text += "\r\n\t ";
									}
								}
							} else {
								var sum0 = 读取潜能词条数量(1);
								if (sum0 < 2) {
									text += "\t#d" + 广播 + ": 装备属性词条必须大于2条才能使用该功能！\r\n";
									text += "\t#d" + 广播 + ": 装备展示：#v" + Eq.getItemId() + "#\r\n";
									var 词条 = 读取潜能词条内容(1);
									for (var m = 0; m < 词条.length; m++) {
										switch (m) {
											case 0:
												text += "\t#d" + 广播 + ": 第#r一#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]\r\n";
												break;
											case 1:
												text += "\t#d" + 广播 + ": 第#b二#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]\r\n";
												break;
											case 2:
												text += "\t#d" + 广播 + ": 第#k三#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]\r\n";
												break;
											default:
												break;
										}
									}
								} else {
									text += "\t#d" + 广播 + ": 检测到符合重铸的装备类型#b" + 匹配类型 + "#d\r\n";
									text += "\t#d" + 广播 + ": 装备展示：#v" + Eq.getItemId() + "#(#r请选择要锁定的属性#d[#b" + 锁定数目 + "#d/#k" + (sum0 - 1) + "#d])\r\n";
									var 词条 = 读取潜能词条内容(1);
									for (var m = 0; m < 词条.length; m++) {
										switch (m) {
											case 0:
												if (锁属性[m].属性 == 0 && 锁属性[m].名称 == "") {
													text += "\t#L" + m + "##d" + xx + "第#r一#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]" + 开 + "" + xx + "#l\r\n";
												} else {
													text += "\t#L" + m + "##d" + xx + "第#r一#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]" + 关 + "" + xx + "#l\r\n";
												}
												break;
											case 1:
												if (锁属性[m].属性 == 0 && 锁属性[m].名称 == "") {
													text += "\t#L" + m + "##d" + xx + "第#b二#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]" + 开 + "" + xx + "#l\r\n";
												} else {
													text += "\t#L" + m + "##d" + xx + "第#b二#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]" + 关 + "" + xx + "#l\r\n";
												}
												break;
											case 2:
												if (锁属性[m].属性 == 0 && 锁属性[m].名称 == "") {
													text += "\t#L" + m + "##d" + xx + "第#k三#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]" + 开 + "" + xx + "#l\r\n";
												} else {
													text += "\t#L" + m + "##d" + xx + "第#k三#d条潜能 [" + 字色(词条[m]) + "" + 词条[m] + "#d]" + 关 + "" + xx + "#l\r\n";
												}
												break;
											default:
												break;
										}
									}
									if (锁定数目 != 0) {
										var 检测条件 = true;
										costitem = new Array();
										var 条件提示 = "\r\n\t#d" + 广播 + ": 潜能需要条件：#d\r\n";
										for (var o = 0; o < 重铸消耗.length; o++) {
											if (重铸消耗[o].锁定词条数量 == 锁定数目) {
												for (var p = 0; p < 重铸消耗[o].消耗条件.length; p++) {
													var itemsum = 物品数量(重铸消耗[o].消耗条件[p].代码);
													costitem.push({ 代码: 重铸消耗[o].消耗条件[p].代码, 数量: 重铸消耗[o].消耗条件[p].数量 });
													if (itemsum < 重铸消耗[o].消耗条件[p].数量) {
														检测条件 = false;
													}
													条件提示 += "\t#d  消耗材料：#v" + 重铸消耗[o].消耗条件[p].代码 + ":# [拥有:#b" + itemsum + "#d/需要:#r" + 重铸消耗[o].消耗条件[p].数量 + "#d]\r\n";
												}
												break;
											}
										}
										if (检测条件 == true) {
											text += 条件提示;
											text += "\r\n\t\t\t\t\t\t\t  是否要重铸呢？\r\n";
											text += "\t\t\t\t\t\t";
											text += "#L" + 是 + "#" + xx + "#r是#d" + xx + "#l  ";
											text += "#L" + 否 + "#" + xx + "#k否#d" + xx + "#l\r\n\r\n";
										} else {
											text += 条件提示;
											text += "\t\t\t\t\t\t\t状态：[#r条件不满足#d]\r\n";
										}
									}
								}
							}
						}
						break;
					case "继承词条":
						var Eq1 = cm.getInventory(1).getItem(1);
						var Eq2 = cm.getInventory(1).getItem(2);
						if (Eq1 == null) {
							text += "\t#d" + 广播 + ": 您装备栏第一个格子必须存放以下类型装备：\r\n\t ";
							for (var l = 0; l < 限制装备.length; l++) {
								text += "#b" + 限制装备[l][0] + "#d";
								if ((l + 1) % 7 == 0) {
									text += "\r\n\t ";
								}
							}
						} else if (Eq2 == null) {
							text += "\t#d" + 广播 + ": 您装备栏第二个格子必须存放以下类型装备：\r\n\t ";
							for (var l = 0; l < 限制装备.length; l++) {
								text += "#b" + 限制装备[l][0] + "#d";
								if ((l + 1) % 7 == 0) {
									text += "\r\n\t ";
								}
							}
						} else {
							var Inventory1 = Math.floor(Eq1.getItemId() / 10000);
							var Inventory2 = Math.floor(Eq2.getItemId() / 10000);
							var 匹配检测 = false;
							var 匹配类型 = "";
							for (var l = 0; l < 限制装备.length; l++) {
								if (Inventory1 >= 限制装备[l][1] && Inventory1 <= 限制装备[l][2]) {
									匹配类型 += 限制装备[l][0];
									匹配检测 = true;
									break;
								}
							}
							var sum = 读取潜能词条数量(1);
							if (匹配检测 == false) {
								text += "\t#d" + 广播 + ": 您装备栏第一个格子必须存放以下类型装备：\r\n\t ";
								for (var l = 0; l < 限制装备.length; l++) {
									text += "#b" + 限制装备[l][0] + "#d";
									if ((l + 1) % 7 == 0) {
										text += "\r\n\t ";
									}
								}
							} else if (sum == 0) {
								text += "\t#d" + 广播 + ": 当前您装备栏一格的装备没有属性词条！\r\n";
							} else {
								if (Inventory1 != Inventory2) {
									text += "\t#d" + 广播 + ": 当前您装备栏一、二格装备不是同一种类型！\r\n";
								} else {
									text += "\t#d" + 广播 + ": 检测到符合继承的装备类型#b" + 匹配类型 + "#d\r\n";
									text += "\t#d" + 广播 + ": 展示   #v" + Eq1.getItemId() + "# #r→#k继承#r→#d #v" + Eq2.getItemId() + "#\r\n";
									var 词条1 = 读取潜能词条内容(1);
									var 词条2 = 读取潜能词条内容(2);
									text += "\t\t #r第一格装备词条      #b第二格装备词条\r\n\r\n";
									for (var r = 0; r < 3; r++) {
										if (r == 0) { var 字 = "#r一#d"; };
										if (r == 1) { var 字 = "#b二#d"; };
										if (r == 2) { var 字 = "#k三#d"; };
										text += "\t\t#d第" + 字 + "条潜能 [#r" + (词条1[r] != null ? 词条1[r] : "xxxx") + "#d]\t第" + 字 + "条潜能 [#r" + (词条2[r] != null ? 词条2[r] : "xxxx") + "#d]\r\n";
									}
									var 检测条件 = true;
									var 条件提示 = "\r\n\t#d" + 广播 + ": 转移需要条件：#d\r\n";
									costitem = new Array();
									for (var n = 0; n < 继承消耗[sum - 1].消耗条件.length; n++) {
										var itemsum = 物品数量(继承消耗[sum - 1].消耗条件[n].代码);
										costitem.push({ 代码: 继承消耗[sum - 1].消耗条件[n].代码, 数量: 继承消耗[sum - 1].消耗条件[n].数量 });
										if (itemsum < 继承消耗[sum - 1].消耗条件[n].数量) {
											检测条件 = false;
										}
										条件提示 += "\t#d  消耗材料：#v" + 继承消耗[sum - 1].消耗条件[n].代码 + ":# [拥有:#b" + itemsum + "#d/需要:#r" + 继承消耗[sum - 1].消耗条件[n].数量 + "#d]\r\n";
									}
									if (检测条件 == true) {
										text += 条件提示;
										text += "\r\n\t\t\t\t\t\t\t  是否要继承呢？\r\n";
										text += "\t\t\t\t\t\t";
										text += "#L" + 是 + "#" + xx + "#r是#d" + xx + "#l  ";
										text += "#L" + 否 + "#" + xx + "#k否#d" + xx + "#l\r\n\r\n";
									} else {
										text += 条件提示;
										text += "\t\t\t\t\t\t\t状态：[#r条件不满足#d]\r\n";
									}
								}
							}
						}
						break;
					case "购买商品":
						text += "  请选择你要购买的商品\r\n";
						for (var i = 0; i < list.length; i++) {
							var is = list[i];
							text += "#L" + i + "#" + xx + "购买:#v" + is.物品 + ":##b#z" + is.物品 + "#" + xx + "#d(#r" + is.购买类型 + "#d)#l\r\n";
						}
						break;
					default:
						break;
				}
			}
			text += "\r\n";
			cm.sendYesNo(text);
		} else if (status == 1) {
			sele = selection;
			for (var i = 0; i < 坐标列表.length; i++) {
				if (sele == 坐标列表[i].坐标) {
					展示 = 坐标列表[i].展示;
					坐标 = 坐标列表[i].坐标;
					锁定数目 = 0;
					for (var ll = 0; ll < 锁属性.length; ll++) {
						锁属性[ll].属性 = 0;
						锁属性[ll].名称 = "";
					}
					start();
					return;
				}
			}
			switch (sele) {
				case 是:
					switch (展示) {
						case "潜能属性": case "重铸词条":
							for (var q = 0; q < costitem.length; q++) {
								cm.gainItem(costitem[q].代码, -costitem[q].数量);
							}
							var 概率最高值 = new Array();
							var sum1 = 读取潜能词条数量(1);
							for (var i = 0; i < 潜能控制.length; i++) {
								if (sum1 == 潜能控制[i].装备词条数量) {
									for (var j = 0; j < 潜能控制[i].出现词条列表.length; j++) {
										概率最高值.push(潜能控制[i].出现词条列表[j].概率);
									}
									break;
								}
							}
							var max = Math.max.apply(null, 概率最高值);
							var random = Math.floor(Math.random() * max) + 1;
							var 随便出现词条数量 = new Array();
							for (var i = 0; i < 潜能控制.length; i++) {
								if (sum1 == 潜能控制[i].装备词条数量) {
									for (var j = 0; j < 潜能控制[i].出现词条列表.length; j++) {
										if (random <= 潜能控制[i].出现词条列表[j].概率) {
											随便出现词条数量.push(潜能控制[i].出现词条列表[j].出现词条);
										}
									}
									break;
								}
							}
							var random1 = Math.floor(Math.random() * 随便出现词条数量.length);
							var 结果数量 = 随便出现词条数量[random1];
							开始赋予潜能(1, 结果数量);
							var 词条 = 读取潜能词条内容(1);
							var 词条文编 = "";
							var item = cm.getInventory(1).getItem(1);
							for (var n = 0; n < 词条.length; n++) {
								词条文编 += " " + 词条[n] + "";
							}
							if (展示 == "潜能属性") {
								cm.itemlaba("[" + cm.getPlayer().getName() + "]","鉴定完毕！[详情：" + 结果数量 + " 条  属性:" + 词条文编 + "]",item,true);
							} else {
								cm.itemlaba("[" + cm.getPlayer().getName() + "]","重铸完毕！[详情：" + 结果数量 + " 条  属性:" + 词条文编 + "]",item,true);
							}
							start();
							return;
						case "继承词条":
							for (var q = 0; q < costitem.length; q++) {
								cm.gainItem(costitem[q].代码, -costitem[q].数量);
							}
							var 词条 = 读取潜能词条内容(1);
							var sum = 读取潜能词条数量(1);
							开始赋予潜能(1, 0);//清除原有属性
							开始赋予潜能(2, 0);//清除原有属性
							for (var n = 0; n < 词条.length; n++) {
								for (var w = 0; w < 潜能属性.length; w++) {
									if (词条[n] == 潜能属性[w].名) {
										锁属性[n].属性 = 潜能属性[w].属性;
										锁属性[n].名称 = 潜能属性[w].名;
										break;
									}
								}
							}
							开始赋予潜能(2, sum);//转移属性
							for (var s = 0; s < sum; s++) {
								锁属性[s].属性 = 0;
								锁属性[s].名称 = "";
							}
							start();
							return;
						default:
							break;
					}
					break;
				case 否:
					switch (展示) {
						case "潜能属性": case "重铸词条": case "继承词条":
							cm.dispose();
							break;
						default:
							break;
					}
					break;
				default:
					switch (展示) {
						case "重铸词条":
							switch (sele) {
								case 0: case 1: case 2:
									if (锁属性[sele].属性 == 0 && 锁属性[sele].名称 == "") {
										锁定数目++;
										if (读取潜能词条数量(1) == 锁定数目) {
											锁定数目--;
											//cm.sendOk("#e#d最高只能锁定：#r" + 锁定数目 + "#d条属性!");
											cm.getPlayer().dropMessage(6, "最高只能锁定：" + 锁定数目 + "条属性!");
										} else {
											var 词条 = 读取潜能词条内容(1);
											for (var n = 0; n < 潜能属性.length; n++) {
												if (词条[sele] == 潜能属性[n].名) {
													锁属性[sele].属性 = 潜能属性[n].属性;
													锁属性[sele].名称 = 潜能属性[n].名;
													break;
												}
											}
										}
									} else {
										锁属性[sele].属性 = 0;
										锁属性[sele].名称 = "";
										锁定数目--;
									}
									start();
									return;
								default:
									break;
							}
							cm.dispose();
							break;
						case "购买商品":
							var text = "#d";
							text += "#k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━┓#d\r\n";
							text += "\t商品详情：#v" + list[sele].物品 + ":##b#z" + list[sele].物品 + "##r1#d个\r\n\r\n";
							if (list[sele].金币 != 0 && list[sele].金币 != null) {
								text += "\t每个价格：#r" + list[sele].金币 + "#d 金币 (#b拥有:#r" + 判断费用("金币") + "#b)#d\r\n\r\n";
							}
							if (list[sele].抵用 != 0 && list[sele].抵用 != null) {
								text += "\t每个价格：#r" + list[sele].抵用 + "#d 抵用 (#b拥有:#r" + 判断费用("抵用") + "#b)#d\r\n\r\n";
							}
							if (list[sele].点券 != 0 && list[sele].点券 != null) {
								text += "\t每个价格：#r" + list[sele].点券 + "#d 点券 (#b拥有:#r" + 判断费用("点券") + "#b)#d\r\n\r\n";
							}
							if (list[sele].材料.length != 0) {
								text += "  需要物品：\r\n";
								for (var oo = 0; oo < list[sele].材料.length; oo++) {
									text += "\t#v" + list[sele].材料[oo].代码 + ":##b#z" + list[sele].材料[oo].代码 + "##d(拥有:#r#c" + list[sele].材料[oo].代码 + "##d / 需要:#r" + list[sele].材料[oo].数量 + "#d个)#d\r\n";
								}
							}
							text += "\t\t\t\t\t\t请在下方输入购买数量！\r\n";
							text += "#k┗━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
							cm.sendGetNumber(text, 1, 1, 2000);
							break;
						default:
							break;
					}
					break;
			}
		} else if (status == 2) {
			sele2 = selection;
			ii = list[sele];
			var text = "#d";
			text += " #k┏━#r选择商品内容#k━━━━━━━━━━━━━━━━━┓#d\r\n\r\n";
			if (ii.金币 != 0 && ii.金币 != null) {
				text += "\t商品费用：#r" + (ii.金币 * sele2) + "#d金币\r\n";
			}
			if (ii.抵用 != 0 && ii.抵用 != null) {
				text += "\t商品费用：#r" + (ii.抵用 * sele2) + "#d抵用\r\n";
			}
			if (ii.点券 != 0 && ii.点券 != null) {
				text += "\t商品费用：#r" + (ii.点券 * sele2) + "#d点券\r\n";
			}
			if (ii.材料.length != 0) {
				text += "  需要物品：\r\n";
				for (var oo = 0; oo < ii.材料.length; oo++) {
					text += "\t#v" + list[sele].材料[oo].代码 + ":##b#z" + list[sele].材料[oo].代码 + "##d(拥有:#r#c" + list[sele].材料[oo].代码 + "##d / 需要:#r" + (list[sele].材料[oo].数量 * sele2) + "#d个)#d\r\n";
				}
			}
			text += "\t\t\t购买商品：#v" + ii.物品 + ":##b#z" + ii.物品 + "##d x #r" + sele2 + "#d个\r\n\r\n";
			text += " \t\t\t\t\t\t\t\t是否确定要购买？";
			text += "\r\n #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
			cm.sendYesNo(text);
		} else if (status == 3) {
			var 判断背包 = 判断背包空间_单个(ii.物品);
			if (判断背包.bool == false) {
				判断背包.text;
				cm.dispose();
			} else {
				var text = "#d";
				var 检测要求 = true;
				if (ii.金币 != 0 && ii.金币 != null) {
					var 金币 = 判断费用("金币");
					if (金币 < (ii.金币 * sele2)) {
						检测要求 = false;
						text += "\t商品费用不足！需要:#r" + (ii.金币 * sele2) + "#d金币！\r\n\t\t缺少:[#r" + ((ii.金币 * sele2) - 金币) + "#d]\r\n";
					}
				}
				if (ii.抵用 != 0 && ii.抵用 != null) {
					var 抵用 = 判断费用("抵用");
					if (抵用 < (ii.抵用 * sele2)) {
						检测要求 = false;
						text += "\t商品费用不足！需要:#r" + (ii.抵用 * sele2) + "#d抵用！\r\n\t\t缺少:[#r" + ((ii.抵用 * sele2) - 抵用) + "#d]\r\n";
					}
				}
				if (ii.点券 != 0 && ii.点券 != null) {
					var 点券 = 判断费用("点券");
					if (点券 < (ii.点券 * sele2)) {
						检测要求 = false;
						text += "\t商品费用不足！需要:#r" + (ii.点券 * sele2) + "#d点券！\r\n\t\t缺少:[#r" + ((ii.点券 * sele2) - 点券) + "#d]\r\n";
					}
				}
				if (ii.材料.length != 0) {
					for (var oo = 0; oo < ii.材料.length; oo++) {
						var itemsum = 物品数量(list[sele].材料[oo].代码);
						if (itemsum < (list[sele].材料[oo].数量 * sele2)) {
							检测要求 = false;
							text += "缺少道具:#v" + list[sele].材料[oo].代码 + ":##b#z" + list[sele].材料[oo].代码 + "##d(拥有:#r#c" + list[sele].材料[oo].代码 + "##d / 需要:#r" + (list[sele].材料[oo].数量 * sele2) + "#d个)#d\r\n";
						}
					}
				}
				if (检测要求 == false) {
					cm.sendOk(text);
					cm.dispose();
				} else {
					if (ii.金币 != 0 && ii.金币 != null) {
						扣除费用("金币", -(ii.金币 * sele2));
					}
					if (ii.抵用 != 0 && ii.抵用 != null) {
						扣除费用("抵用", -(ii.抵用 * sele2));
					}
					if (ii.点券 != 0 && ii.点券 != null) {
						扣除费用("点券", -(ii.点券 * sele2));
					}
					if (ii.材料.length != 0) {
						for (var oo = 0; oo < ii.材料.length; oo++) {
							cm.gainItem(ii.材料[oo].代码, -(ii.材料[oo].数量 * sele2));
						}
					}
					cm.gainItem(ii.物品, sele2);
					cm.sendOk("#e#d购买完毕！祝您游戏愉快。");
					ii = null;
					status = -1;
					return;
				}
			}
		}
	}
}

var ii;
var 锁属性 = [
	{ 属性: 0, 名称: "" },
	{ 属性: 0, 名称: "" },
	{ 属性: 0, 名称: "" },
]

function 实时道具喇叭(invType, pos, msg) {//rc端使用
	var MapleInventoryType = Packages.client.inventory.MapleInventoryType;
	var item = cm.getPlayer().getInventory(MapleInventoryType.getByType(invType)).getItem(pos);
	cm.道具喇叭(item, msg);
}

function 开始赋予潜能(装备栏位置, 词条数量) {
	var 词条属性_名称 = new Array();
	var jj = 0;
	var 词条 = 读取潜能词条内容(装备栏位置);
	for (var m = 0; m < 词条.length; m++) {
		for (var n = 0; n < 潜能属性.length; n++) {
			if (词条[m] == 潜能属性[n].名) {
				词条属性_名称.push({ 属性: 潜能属性[n].属性, 名称: 潜能属性[n].名 });
				//cm.getPlayer().dropMessage(5, "潜能属性名 = " + 潜能属性[n].名 + "  潜能属性值 = " + 潜能属性[n].属性 + "");
				jj++;
				break;
			}
		}
	}
	for (var ii = 0; ii < 词条数量; ii++) {
		if (锁属性[ii].属性 != 0 || 锁属性[ii].名称 != "") {
			词条属性_名称.push({ 属性: 锁属性[ii].属性, 名称: 锁属性[ii].名称 });
		} else {
			var random = Math.floor(Math.random() * 1000) + 1;
			var 词条列表封装 = new Array();
			for (var i = 0; i < 潜能属性.length; i++) {
				if (random <= 潜能属性[i].概率) {
					词条列表封装.push(潜能属性[i]);
				}
			}
			var random1 = Math.floor(Math.random() * 词条列表封装.length);
			var 潜能属性值 = 词条列表封装[random1].属性;
			var 潜能属性名 = 词条列表封装[random1].名;
			词条属性_名称.push({ 属性: 潜能属性值, 名称: 潜能属性名 });
		}
	}
	var 词条名称 = "";
	for (var l = 0; l < 词条属性_名称.length; l++) {
		if (l >= jj) {
			词条名称 += 词条属性_名称[l].名称;
		}
		var item = cm.getInventory(1).getItem(装备栏位置);
		switch (词条属性_名称[l].名称) {
			case "⑴力": case "⑵力": case "⑶力": case "⑷力": case "⑸力":
				item.setStr((l < jj ? item.getStr() - 词条属性_名称[l].属性 : item.getStr() + 词条属性_名称[l].属性));
				break;
			case "⑴敏": case "⑵敏": case "⑶敏": case "⑷敏": case "⑸敏":
				item.setDex((l < jj ? item.getDex() - 词条属性_名称[l].属性 : item.getDex() + 词条属性_名称[l].属性));
				break;
			case "⑴智": case "⑵智": case "⑶智": case "⑷智": case "⑸智":
				item.setInt((l < jj ? item.getInt() - 词条属性_名称[l].属性 : item.getInt() + 词条属性_名称[l].属性));
				break;
			case "⑴运": case "⑵运": case "⑶运": case "⑷运": case "⑸运":
				item.setLuk((l < jj ? item.getLuk() - 词条属性_名称[l].属性 : item.getLuk() + 词条属性_名称[l].属性));
				break;
			case "⑴攻": case "⑵攻": case "⑶攻": case "⑷攻": case "⑸攻":
				item.setWatk((l < jj ? item.getWatk() - 词条属性_名称[l].属性 : item.getWatk() + 词条属性_名称[l].属性));
				break;
			case "⑴魔": case "⑵魔": case "⑶魔": case "⑷魔": case "⑸魔":
				item.setMatk((l < jj ? item.getMatk() - 词条属性_名称[l].属性 : item.getMatk() + 词条属性_名称[l].属性));
				break;
			case "⑴血": case "⑵血": case "⑶血": case "⑷血": case "⑸血":
				item.setHp((l < jj ? item.getHp() - 词条属性_名称[l].属性 : item.getHp() + 词条属性_名称[l].属性));
				break;
			case "⑴蓝": case "⑵蓝": case "⑶蓝": case "⑷蓝": case "⑸蓝":
				item.setMp((l < jj ? item.getMp() - 词条属性_名称[l].属性 : item.getMp() + 词条属性_名称[l].属性));
				break;
			case "⑴防": case "⑵防": case "⑶防": case "⑷防": case "⑸防":
				item.setWdef((l < jj ? item.getWdef() - 词条属性_名称[l].属性 : item.getWdef() + 词条属性_名称[l].属性));
				item.setMdef((l < jj ? item.getMdef() - 词条属性_名称[l].属性 : item.getMdef() + 词条属性_名称[l].属性));
				break;
			case "⑴准": case "⑵准": case "⑶准": case "⑷准": case "⑸准":
				item.setAcc((l < jj ? item.getAcc() - 词条属性_名称[l].属性 : item.getAcc() + 词条属性_名称[l].属性));
				break;
			case "⑴闪": case "⑵闪": case "⑶闪": case "⑷闪": case "⑸闪":
				item.setAvoid((l < jj ? item.getAvoid() - 词条属性_名称[l].属性 : item.getAvoid() + 词条属性_名称[l].属性));
				break;
			case "⑴速": case "⑵速": case "⑶速": case "⑷速": case "⑸速":
				item.setSpeed((l < jj ? item.getSpeed() - 词条属性_名称[l].属性 : item.getSpeed() + 词条属性_名称[l].属性));
				break;
			case "⑴跳": case "⑵跳": case "⑶跳": case "⑷跳": case "⑸跳":
				item.setJump((l < jj ? item.getJump() - 词条属性_名称[l].属性 : item.getJump() + 词条属性_名称[l].属性));
				break;
			case "⑴全": case "⑵全": case "⑶全": case "⑷全": case "⑸全":
				item.setDex((l < jj ? item.getDex() - 词条属性_名称[l].属性 : item.getDex() + 词条属性_名称[l].属性));
				item.setStr((l < jj ? item.getStr() - 词条属性_名称[l].属性 : item.getStr() + 词条属性_名称[l].属性));
				item.setInt((l < jj ? item.getInt() - 词条属性_名称[l].属性 : item.getInt() + 词条属性_名称[l].属性));
				item.setLuk((l < jj ? item.getLuk() - 词条属性_名称[l].属性 : item.getLuk() + 词条属性_名称[l].属性));
				break;
			default:
				break;
		}
		cm.getPlayer().forceReAddItem_Flag(item.copy(), Packages.client.inventory.MapleInventoryType.EQUIP);
		//cm.getPlayer().dropMessage(5, "潜能属性名 = " + 词条属性_名称[l].名称 + "  潜能属性值 = " + 词条属性_名称[l].属性 + "");
	}
	var item = cm.getInventory(1).getItem(装备栏位置);
	item.setOwner(词条名称);
	cm.getPlayer().forceReAddItem_Flag(item.copy(), Packages.client.inventory.MapleInventoryType.EQUIP);
	//已装备-EQUIPPED 装备栏-EQUIP 消耗栏-USE 装饰栏-SETUP 其他栏-ETC 特殊栏-CASH
}

function 读取潜能词条数量(装备栏位置) {
	var Eq = cm.getInventory(1).getItem(装备栏位置);
	if (Eq != null) {
		var 词条封装 = new Array();
		var str = 字符串转换为组(Eq.getOwner());
		var 词条内容 = "";
		for (var l = 0; l < str.length; l++) {
			词条内容 += str[l];
			if ((l + 1) % 2 == 0) {
				词条封装.push(词条内容);
				词条内容 = "";
			}
		}
		return parseInt(词条封装.length);
	} else {
		return "第 " + 装备栏位置 + " 格装备 为空(null)";
	}
}

function 读取潜能词条内容(装备栏位置) {
	var Eq = cm.getInventory(1).getItem(装备栏位置);
	if (Eq != null) {
		var 词条封装 = new Array();
		var str = 字符串转换为组(Eq.getOwner());
		var 词条内容 = "";
		for (var l = 0; l < str.length; l++) {
			词条内容 += str[l];
			if ((l + 1) % 2 == 0) {
				词条封装.push(词条内容);
				词条内容 = "";
			}
		}
		return 词条封装;
	} else {
		return "第 " + 装备栏位置 + " 格装备 为空(null)";
	}
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}

function Inventory_Up(装备栏位置) {
	var item = cm.getInventory(1).getItem(装备栏位置);
	item.setStr(item.getStr());
	item.setDex(item.getDex());
	item.setInt(item.getInt());
	item.setLuk(item.getLuk());
	item.setWatk(item.getWatk());
	item.setMatk(item.getMatk());
	item.setWdef(item.getWdef());
	item.setMdef(item.getMdef());
	item.setHp(item.getHp());
	item.setMp(item.getMp());
	item.setOwner("⑴力⑵攻⑶敏");
	item.setFlag(8);
	cm.getPlayer().forceReAddItem_Flag(item.copy(), Packages.client.inventory.MapleInventoryType.EQUIP);
	//已装备-EQUIPPED 装备栏-EQUIP 消耗栏-USE 装饰栏-SETUP 其他栏-ETC 特殊栏-CASH
}

function 字色(str) {
	//⑴ ⑵ ⑶ ⑷ ⑸
	var ret = "";
	var zt = [["⑴", "#k"], ["⑵", "#k"], ["⑶", "#d"], ["⑷", "#b"], ["⑸", "#r"],];
	var tempString = str.toString().split("");
	for (var i = 0; i < zt.length; i++) {
		if (tempString[0] == zt[i][0]) {
			ret = zt[i][1];
			break;
		}
	}
	return ret;
}

function 字符串转换为组(str) {
	var tempString = str.toString().split("");
	return tempString;
}

function 提取字符串数值(str) {
	var num = str.replace(/[^0-9]/ig, "");//提取字符串中的数值  返回 = 字符串数值(较为精准)
	//var num = str.match(/\d+(.\d+)?/g);//提取字符串中的数值  返回 = 字符串数值(不太精准)
	return num;
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

function 美化(length, content, bool) {
	var str = "";
	var cs = "";
	if (content.length > length) {
		str = content;
	} else {
		for (var j = 0; j < length - content.getBytes("GB2312").length; j++) {
			cs += " ";
		}
	}
	if (bool == true) {
		str = content + cs;
	} else {
		str = cs + content;
	}
	return str;
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

function 判断费用(type) {
	var ret;
	if (type == "金币") {
		ret = cm.getMeso();
	} else if (type == "点券") {
		ret = cm.getPlayer().getCSPoints(1);
	} else if (type == "抵用") {
		ret = cm.getPlayer().getCSPoints(2);
	}
	return ret;
}

function 扣除费用(type, num) {
	if (type == "金币") {
		cm.gainMeso(num);
	} else if (type == "点券") {
		cm.gainNX(num);
	} else if (type == "抵用") {
		cm.gainDY(num);
	}
}