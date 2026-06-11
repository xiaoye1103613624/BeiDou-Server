
var 上拉 = "#fUI/UIWindow.img/PvP/Scroll/enabled/prev2#";
var 下拉 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 开 = "#fUI/Basic/CheckBox/0#";   //有框框 无√
var 关 = "#fUI/Basic/CheckBox/1#";   //有框框 有√
var xx = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";

var 功能名称 = "混沌卷轴强化";
var 隐藏道具 = 4000000;

var 祝福卷轴 = 2340000;
var 强化消耗 = { 代码: 2049122, 数量: 1 };
var 属性波动 = [
	{ 概率: 10, 属性: 4 },
	{ 概率: 20, 属性: 3 },
	{ 概率: 50, 属性: 2 },
	{ 概率: 75, 属性: 1 },
	{ 概率: 100, 属性: 0 },
];

var 成功概率 = 50;

var 排除装备 = [1302000, 1302001];

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
			var Eq = cm.getInventory(1).getItem(1);
			if (Eq == null) {
				cm.sendOk("#e#d使用该功能！\r\n必须把要强化的装备放置在装备栏第一个格！");
				cm.dispose();
				return;
			} else if (banItemId(Eq.getItemId()).bool == false) {
				cm.sendOk("#e#d以下装备不能参与强化:\r\n" + banItemId(Eq.getItemId()).text);
				cm.dispose();
				return;
			} else if (cm.isCash(Eq.getItemId()) == true) {
				cm.getPlayer().dropMessage(1, "点卷时装不能强化的！");
				cm.dispose();
				return;
			} else if (Eq.getUpgradeSlots() == 0) {
				cm.getPlayer().dropMessage(1, "装备没有可升级次数了！");
				cm.dispose();
				return;
			} else {
				var maxArr = new Array();
				for (var i = 0; i < 属性波动.length; i++) {
					maxArr.push(属性波动[i].属性);
				}
				var max = Math.max.apply(null, maxArr);
				var min = Math.min.apply(null, maxArr);

				var text = "#k\r\n";
				var 隐藏 = "";
				text += " #k┏━#r冒险岛提示#k━━━━━━━━━━━━━━━━━━┓\r\n";
				text += "\t#d" + 广播 + ": 欢迎来到 [#r" + 功能名称 + "#d]\r\n";
				text += "\t#d" + 广播 + ": 该功能为#b#z" + 强化消耗.代码 + "##d进化版！\r\n";
				text += "\t#d" + 广播 + ": 成功概率：#r" + 成功概率 + "%#d 随机获得：#r" + min + " #d~ #r" + max + "#d点属性！\r\n";
				text += "\t#d" + 广播 + ": 使用该功能需要消耗：#r" + 强化消耗.数量 + "张 #b#z" + 强化消耗.代码 + "##d\r\n";
				text += "\t#d" + 广播 + ": 强化的装备展示：#v" + Eq.getItemId() + ":##b#z" + Eq.getItemId() + "##d\r\n";
				var 祝福卷状态 = (祝福卷轴机制 == false ? "#L2##v" + 祝福卷轴 + ":#" + 开 + "#l" : "#L2##v" + 祝福卷轴 + ":#" + 关 + "#l");
				if (物品数量(隐藏道具) >= 1) {
					隐藏 = (隐藏道具机制 == false ? "#L3##v" + 隐藏道具 + ":#" + 开 + "#l" : "#L3##v" + 隐藏道具 + ":#" + 关 + "#l");
				} else {
					隐藏道具机制 = false;
				}
				text += "\t#d#L1#" + xx + "#e开始强化#n#d" + xx + "#l  " + 祝福卷状态 + "  " + 隐藏 + "\r\n\r\n";
				text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#d\r\n";
				cm.sendYesNo(text);
			}
		} else if (status == 1) {
			sele = selection;
			switch (sele) {
				case 1:
					if (物品数量(强化消耗.代码) < 强化消耗.数量) {
						cm.sendOk("#e#v" + 强化消耗.代码 + ":##b#z" + 强化消耗.代码 + "##d 不足：#r" + 强化消耗.数量 + "#d张，无法强化！");
						cm.dispose();
						return;
					}
					if (祝福卷轴机制 == true) {
						if (物品数量(祝福卷轴) < 1) {
							cm.sendOk("#e#v" + 祝福卷轴 + ":##b#z" + 祝福卷轴 + "##d 不足：#r" + 1 + "#d张，无法使用该功能！");
							status = -1;
							return;
						}
					}
					if (隐藏道具机制 == true) {
						cm.gainItem(隐藏道具, -1);
					}
					if (祝福卷轴机制 == true) {
						cm.gainItem(祝福卷轴, -1);
					}
					cm.gainItem(强化消耗.代码, -强化消耗.数量);
					var random = Math.floor(Math.random() * 100) + 1;
					var item = cm.getInventory(1).getItem(1);
					if (random <= 成功概率) {
						item.setUpgradeSlots(item.getUpgradeSlots() - 1);
						item.setLevel(item.getLevel() + 1);
						item.setStr((item.getStr() != 0 ? item.getStr() + up() : 0));
						item.setDex((item.getDex() != 0 ? item.getDex() + up() : 0));
						item.setInt((item.getInt() != 0 ? item.getInt() + up() : 0));
						item.setLuk((item.getLuk() != 0 ? item.getLuk() + up() : 0));
						item.setHp((item.getHp() != 0 ? item.getHp() + up() : 0));
						item.setMp((item.getMp() != 0 ? item.getMp() + up() : 0));
						item.setWatk((item.getWatk() != 0 ? item.getWatk() + up() : 0));
						item.setMatk((item.getMatk() != 0 ? item.getMatk() + up() : 0));
						item.setWdef((item.getWdef() != 0 ? item.getWdef() + up() : 0));
						item.setMdef((item.getMdef() != 0 ? item.getMdef() + up() : 0));
						item.setAcc((item.getAcc() != 0 ? item.getAcc() + up() : 0));
						item.setAvoid((item.getAvoid() != 0 ? item.getAvoid() + up() : 0));
						cm.getPlayer().forceReAddItem_Flag(item.copy(), Packages.client.inventory.MapleInventoryType.EQUIP);
						cm.sendOk("#e#d恭喜您！强化成功了。");
						status = -1;
						return;
					} else {
						if (祝福卷轴机制 == false) {
							item.setUpgradeSlots(item.getUpgradeSlots() - 1);
						}
						cm.getPlayer().forceReAddItem_Flag(item.copy(), Packages.client.inventory.MapleInventoryType.EQUIP);
						cm.sendOk("#e#d很遗憾！强化失败了...");
						status = -1;
						return;
					}
				case 2:
					if (祝福卷轴机制 == false) {
						祝福卷轴机制 = true;
					} else {
						祝福卷轴机制 = false;
					}
					start();
					return;
				case 3:
					if (隐藏道具机制 == false) {
						隐藏道具机制 = true;
					} else {
						隐藏道具机制 = false;
					}
					start();
					return;
			}
		}
	}
}

function 物品数量(itemid) {
	return cm.getPlayer().getItemQuantity(itemid, false);
}

function up() {
	var attribute = 0;
	if (隐藏道具机制 == true) {
		var maxArr = new Array();
		for (var i = 0; i < 属性波动.length; i++) {
			maxArr.push(属性波动[i].属性);
		}
		var max = Math.max.apply(null, maxArr);
		attribute = max;
	} else {
		var Arr = new Array();
		var maxArr = new Array();
		for (var i = 0; i < 属性波动.length; i++) {
			maxArr.push(属性波动[i].概率);
		}
		var max = Math.max.apply(null, maxArr);
		var random = Math.floor(Math.random() * max) + 1;
		for (var i = 0; i < 属性波动.length; i++) {
			if (random <= 属性波动[i].概率) {
				Arr.push(属性波动[i]);
			}
		}
		var sj = Math.floor(Math.random() * Arr.length);
		attribute = Arr[sj].属性;
	}
	return attribute;
}

function banItemId(id) {
	var bool = true;
	var text = "#d";
	for (var i = 0; i < 排除装备.length; i++) {
		if (id == 排除装备[i]) {
			bool = false;
		}
		text += "#v" + 排除装备[i] + ":#";
	}
	return ret = { bool: bool, text: text };
}

var 隐藏道具机制 = false;
var 祝福卷轴机制 = false;