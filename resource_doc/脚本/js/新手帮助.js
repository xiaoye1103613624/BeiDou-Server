

var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星

var 新手礼包 = {
	抵用: 20000,
	物品: [
		{ 代码: 2000019, 数量: 1000, 时限: 0 },
		{ 代码: 5030008, 数量: 1, 时限: 0 },
		{ 代码: 5030000, 数量: 1, 时限: 0 },
		{ 代码: 5150038, 数量: 1, 时限: 7 },
		{ 代码: 5151001, 数量: 1, 时限: 7 },
		{ 代码: 5153000, 数量: 1, 时限: 7 },
	//	{ 代码: 5041000, 数量: 10, 时限: 0 }, //高级瞬移石
		{ 代码: 2022519, 数量: 4, 时限: 3 }, //复活术

	]
};

var 新手技能 = [
	{ minjob: 0, maxjob: 999, 代码: 8, 等级: 1, 价格: 1000000 },
	{ minjob: 0, maxjob: 999, 代码: 1003, 等级: 1, 价格: 1000000 },
	{ minjob: 0, maxjob: 999, 代码: 1004, 等级: 1, 价格: 1000000 },
	{ minjob: 0, maxjob: 999, 代码: 1007, 等级: 3, 价格: 1000000 },	
	//{ minjob: 0, maxjob: 999, 代码: 1013, 等级: 1, 价格: 1000000 },
//	{ minjob: 0, maxjob: 999, 代码: 1017, 等级: 1, 价格: 1000000 },

	{ minjob: 1000, maxjob: 1999, 代码: 10000018, 等级: 1, 价格: 1000000 },
	{ minjob: 1000, maxjob: 1999, 代码: 10001003, 等级: 1, 价格: 1000000 },
	{ minjob: 1000, maxjob: 1999, 代码: 10001004, 等级: 1, 价格: 1000000 },
	{ minjob: 1000, maxjob: 1999, 代码: 10001007, 等级: 3, 价格: 1000000 },

	{ minjob: 2000, maxjob: 2299, 代码: 20000024, 等级: 1, 价格: 1000000 },
	{ minjob: 2000, maxjob: 2299, 代码: 20001003, 等级: 1, 价格: 1000000 },
	{ minjob: 2000, maxjob: 2299, 代码: 20001004, 等级: 1, 价格: 1000000 },
	{ minjob: 2000, maxjob: 2299, 代码: 20001007, 等级: 3, 价格: 1000000 },
]

var 等级礼包 = [
	{
		要求等级: 10, 抵用: 2000,
		物品: [
			{ 代码: 5150038, 数量: 5, 时限: 0 }, //皇家理发卷
			{ 代码: 5220000, 数量: 1, 时限: 0 }, //快乐百宝卡
		]
	},
	{
		要求等级: 30, 抵用: 3000,
		物品: [
			{ 代码: 5150038, 数量: 5, 时限: 0 },//皇家理发卷
			{ 代码: 5220000, 数量: 2, 时限: 0 },//快乐百宝卡
			{ 代码: 2022465, 数量: 1, 时限: 0 },//精灵吊坠
		]
	},
	{
		要求等级: 70, 抵用: 4000,
		物品: [
			{ 代码: 5150038, 数量: 5, 时限: 0 },//皇家理发卷
			{ 代码: 5220000, 数量: 3, 时限: 0 },//快乐百宝卡
			{ 代码: 5050000, 数量: 20, 时限: 0 }, //洗能力点
		]
	},
	{
		要求等级: 120, 抵用: 10000,
		物品: [
			{ 代码: 5150038, 数量: 5, 时限: 0 },//皇家理发卷
			{ 代码: 5220000, 数量: 5, 时限: 0 },//快乐百宝卡
			{ 代码: 5050000, 数量: 20, 时限: 0 },//洗能力点
		]
	},
	{
		要求等级: 180, 抵用: 12000,
		物品: [
			{ 代码: 5150038, 数量: 5, 时限: 0 },//皇家理发卷
			{ 代码: 4310086, 数量: 1, 时限: 0 },//转职币
		]
	},
	{
		要求等级: 250, 抵用: 12000,
		物品: [
			{ 代码: 4310086, 数量: 1, 时限: 0 },//转职币
		]
	},
];

var 药水礼包 = [
	{ 代码: 2000016, 数量: 5000, 时限: 0 },
	{ 代码: 2000018, 数量: 5000, 时限: 0 },
]

/*
cm.getPlayer().getAccYjLog("名字");//角色
cm.getPlayer().getAccYjLog("名字",true);//账号
	
cm.getPlayer().setAccYjLog("名字",次数);//永久LOG
cm.getPlayer().setBossLog("名字",次数);//每日LOG
*/

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
			var text = "#k";
			text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━┓\r\n";
			text += "\t#k" + 广播 + " 欢迎来到[#r新手帮助中心#k]\r\n";
			text += "\t#k" + 广播 + " [#r新手礼包#k]每个角色只能领取一个！\r\n";
			text += "\t#k" + 广播 + " [#r学习技能#k]达到30级可以学习部分技能！\r\n";
			text += "\t#k" + 广播 + " [#r等级奖励#k]达成相应等级可以领取礼包哟！\r\n";
		//	text += "\t#k" + 广播 + " [#r药水礼包#k]可以领取大量药水！\r\n";
			text += "\t#k" + 广播 + " [#r成长基金#k]#b可以领取大量点券、元宝、装备！\r\n";
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
			text += "" + 分割线() + "\r\n";
			text += "\t#L1#" + xx + "[#r新手礼包#k]" + xx + "#l";
			text += "\t#L2#" + xx + "[#r学习技能#k]" + xx + "#l\r\n";
			text += "\t#L3#" + xx + "[#r等级奖励#k]" + xx + "#l";
			text += "\t#L5#" + xx + "[#r成长基金#k]" + xx + "#l\r\n\r\n";
			text += "\t\t\t\t#L4#" + xx + "[#r快速专职#k]" + xx + "#l";
			cm.sendYesNo(text);
		} else if (status == 1) {
			sele1 = selection;
			switch (sele1) {
				case 1:
					var text = "#k";
					text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━┓\r\n";
					text += "\t#k" + 广播 + " 欢迎来到[#r新手礼包#k]\r\n";
					text += "\t#k" + 广播 + " 每个角色只能领取一次，不可重复领取！\r\n";
					text += "\t#k" + 广播 + " 该礼包为了帮助新手度过前期而准备的！\r\n";
					text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
					text += "" + 分割线() + "\r\n";
					text += "\t#b抵用：#k+ " + 新手礼包.抵用 + "\r\n";
					for (var i = 0; i < 新手礼包.物品.length; i++) {
						var is_i = 新手礼包.物品[i]
						var 时效 = (is_i.时限 != 0 ? "(#b时效:#r" + is_i.时限 + "#k天)" : "(#b时效:#r无限制#k)");
						text += "  #v" + is_i.代码 + ":##b#z" + is_i.代码 + "##k x " + is_i.数量 + "个 " + 时效 + "\r\n";
					}
					var 判断背包 = 判断背包空间_数组(新手礼包.物品);
					var 领取记录 = cm.getPlayer().getOneTimeLog("新手礼包1");
					if (领取记录 != 0) {
						text += "\t\t\t\t\t\t您已经领取过礼包了！\r\n";
						cm.sendOk(text);
						cm.dispose();
						return;
					} else if (判断背包.bool == false) {
						text += "\t\t您背包满了哦，办理前腾出背包吧！\r\n";
						text += 判断背包.text;
						cm.sendOk(text);
						cm.dispose();
						return;
					} else {
						text += "\t\t\t\t\t\t\t是否要领取新手礼包呢？\r\n";
						cm.sendYesNo(text);
					}
					break;
				case 2:
					var text = "";
					text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━┓\r\n";
					text += "\t#k" + 广播 + " 您好，这里是[#r新手技能#k]\r\n";
					text += "\t#k" + 广播 + " 想要学习新手技能，需要付一点费用哦！\r\n";
					text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
					text += "" + 分割线() + "\r\n";
					for (var i = 0; i < 新手技能.length; i++) {
						if (cm.getJob() >= 新手技能[i].minjob && cm.getJob() <= 新手技能[i].maxjob) {
							var Lv = cm.getPlayer().getSkillLevel(新手技能[i].代码);
							var state = (Lv == 0 ? "[#b未学习#k]" : "[#r已学习#k]");
							text += "#L" + i + "#" + xx + "" + state + "#s" + 新手技能[i].代码 + "#学习需要：#r" + 新手技能[i].价格 + "#k金币" + xx + "#l\r\n";
						}
					}
					cm.sendYesNo(text);
					break;
				case 3:
					var text = "#k";
					text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━┓\r\n";
					text += "\t#k" + 广播 + " 欢迎来到[#r新手等级礼包#k]\r\n";
					text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
					text += "" + 分割线() + "\r\n";
					for (var i = 0; i < 等级礼包.length; i++) {
						var is_i = 等级礼包[i];
						var 领取记录 = cm.getPlayer().getOneTimeLog("新手等级礼包:" + is_i.要求等级);
						var 领取状态 = (领取记录 == 0 ? "#b未领取#k" : "#r已领取#k");
						text += "#L" + i + "#" + xx + "查看：新手[#r" + is_i.要求等级 + "#k]级礼品(#b状态:" + 领取状态 + ")" + xx + "#l\r\n";
					}
					cm.sendYesNo(text);
					break;
				case 5:
					cm.dispose();
					cm.openNpc(1400000,"成长基金");
					break;
				case 4:
				cm.dispose();
					cm.openNpc(9900004,"快速转职");
				default:
					break;
			}
		} else if (status == 2) {
			sele2 = selection;
			switch (sele1) {
				case 1:
					cm.getPlayer().setOneTimeLog("新手礼包1");//永久LOG
					cm.gainDY(新手礼包.抵用);
					for (var i = 0; i < 新手礼包.物品.length; i++) {
						var is_i = 新手礼包.物品[i]
						cm.gainItem(is_i.代码, is_i.数量, is_i.时限);
					}
					cm.sendOk("#e#d领取完成，祝您冒险之旅愉快！！！");
					cm.喇叭(1,"[新手礼包] 恭喜玩家:[" + cm.getPlayer().getName() + "] 领取新人礼包！");//白色  喇叭
					cm.dispose();
					break;
				case 2:
					var Lv = cm.getPlayer().getSkillLevel(新手技能[sele2].代码);
					if (Lv != 0) {
						cm.sendOk("#e#d您已经习得该技能了！无需再学习！");
						cm.dispose();
					} else if (cm.getMeso() < 新手技能[sele2].价格) {
						cm.sendOk("#e#d额~~~你没有这么多金币学习技能哦！");
						cm.dispose();
					} else {
						cm.sendYesNo("#e#d您确定要花费：#r" + 新手技能[sele2].价格 + "#d金币学习#s" + 新手技能[sele2].代码 + "#");
					}
					break;
				case 3:
					var 选择礼包 = 等级礼包[sele2];
					var text = "#k";
					text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━┓\r\n";
					text += "\t#k" + 广播 + " 下方为[#r" + 选择礼包.要求等级 + "#k]级礼品展示\r\n";
					text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
					text += "" + 分割线() + "\r\n";
					text += "\t#b抵用：#k+ " + 选择礼包.抵用 + "\r\n";
					for (var i = 0; i < 选择礼包.物品.length; i++) {
						var is_i = 选择礼包.物品[i];
						var 时效 = (is_i.时限 != 0 ? "(#b时效:#r" + is_i.时限 + "#k小时)" : "(#b时效:#r无限制#k)");
						text += "  #v" + is_i.代码 + ":##b#z" + is_i.代码 + "##k x " + is_i.数量 + "个 " + 时效 + "\r\n";
					}
					text += "\r\n";
					var 领取记录 = cm.getPlayer().getOneTimeLog("新手等级礼包:" + 选择礼包.要求等级);
					if (cm.getPlayer().getLevel() >= 选择礼包.要求等级) {
						if (领取记录 == 0) {
							text += "\t\t\t\t\t\t是否要领取该礼品呢？\r\n";
							cm.sendYesNo(text);
						} else {
							text += "\t\t\t\t\t\t您已经领取过该礼品了！\r\n";
							cm.sendOk(text);
							cm.dispose();
							return;
						}
					} else {
						text += "\t\t\t\t\t\t您的等级要求不足以领取！\r\n";
						cm.sendOk(text);
						cm.dispose();
						return;
					}
					break;
				case 4:
					cm.getPlayer().setOneTimeLog("新手药水礼包");//永久LOG
					cm.getPlayer().setOneTimeLog("新手药水礼包_每日");//每日LOG
					for (var i = 0; i < 药水礼包.length; i++) {
						var is_i = 药水礼包[i];
						cm.gainItem(is_i.代码, is_i.数量, is_i.时限);
						cm.喇叭(1,"[新手礼包] 恭喜玩家:[" + cm.getPlayer().getName() + "] 领取新人药水礼包！");//白色  喇叭
					}
					cm.sendOk("#e#d领取完成！");
					cm.dispose();
					break;
				default:
					break;
			}
		} else if (status == 3) {
			sele3 = selection;
			switch (sele1) {
				case 2:
					cm.gainMeso(-新手技能[sele2].价格);
					cm.teachSkill(新手技能[sele2].代码, 新手技能[sele2].等级, 新手技能[sele2].等级);
					cm.sendOk("#e#d学习成功，打开技能栏查看一下吧！");
					cm.dispose();
					break;
				case 3:
					var 选择礼包 = 等级礼包[sele2];
					cm.getPlayer().setOneTimeLog("新手等级礼包:" + 选择礼包.要求等级);
					cm.gainDY(选择礼包.抵用);
					for (var i = 0; i < 选择礼包.物品.length; i++) {
						var is_i = 选择礼包.物品[i];
						cm.gainItem(is_i.代码, is_i.数量, is_i.时限);
					}
					cm.sendOk("#e#d恭喜您，领取了#r" + 选择礼包.要求等级 + "#d级礼品！！！");
					cm.喇叭(1,"[等级礼包] 恭喜玩家:[" + cm.getPlayer().getName() + "] 领取了" + 选择礼包.要求等级 + "级礼品！！");//白色  喇叭
					cm.dispose();
					break;
				default:
					break;
			}
		}
	}
}

function 判断背包空间_数组(list) {
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
	if (装备栏空位 < k1) { 检测背包 = false; text += "\t\t请确保#r装备#d栏有 #r" + k1 + "#d 空间以上！\r\n"; };
	if (消耗栏空位 < k2) { 检测背包 = false; text += "\t\t请确保#r消耗#d栏有 #r" + k2 + "#d 空间以上！\r\n"; };
	if (设置栏空位 < k3) { 检测背包 = false; text += "\t\t请确保#r设置#d栏有 #r" + k3 + "#d 空间以上！\r\n"; };
	if (其他栏空位 < k4) { 检测背包 = false; text += "\t\t请确保#r其他#d栏有 #r" + k4 + "#d 空间以上！\r\n"; };
	if (现金栏空位 < k5) { 检测背包 = false; text += "\t\t请确保#r现金#d栏有 #r" + k5 + "#d 空间以上！\r\n"; };
	return ret = { bool: 检测背包, text: text };
}

function 分割线() {
	var text = "   ";
	var list = [
		"#fEffect/CharacterEff.img/1022223/7/0#",
		"#fEffect/CharacterEff.img/1022223/8/0#",
		"#fEffect/CharacterEff.img/1022223/9/0#",
	];
	for (var i = 0; i < 21; i++) {
		var random = Math.floor(Math.random() * list.length);
		text += list[random];
	}
	text += "  ";
	return text;
}
