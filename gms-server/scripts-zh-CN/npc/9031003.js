var 点券 = 0;
var 抵用 = 0;
var text = "";
var 标题 = "套装收集";
var 可升级物品 =1132300;
var equips = [
	[1, [1003242, 1052357, 1082314, 1102294, 1132092, 1072521]],
	[2, [1003364, 1052405, 1082391, 1102322, 1132110, 1072610]],
	[3, [1122174, 1032121, 1082401, 1042231, 1062148, 1072618]],
	[4, [1003552, 1052461, 1102441, 1082433, 1132154, 1072666]],
	[5, [1003561, 1052467, 1102467, 1082438, 1132161, 1072672]],
	[6, [1003740, 1052569, 1102506, 1082498, 1132182, 1072768]],
	[7, [1002939, 1050127, 1012251, 1082149, 1102163, 1092049]],
	[8, [1003540, 1052460, 1032142, 1072664, 1082432, 1112738, 1122197, 1132152]], ];

function start() {
	chr = cm["getPlayer"]();皇冠白 = "#fUI/GuildMark/Mark/Etc/00009004/15#";完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";圆形 = "#fUI/UIWindow/Quest/icon3/6#";感叹号 = "#fUI/UIWindow/Quest/icon0#";粉心 = "#fEffect/CharacterEff/1042176/2/0#";红心 = "#fItem/Etc/0427/04270001/Icon9/0#";粉星 = "#fEffect/CharacterEff/1112925/0/1#";正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";小熊 = "#fUI/UIWindow/UserInfo/bossPetCrown#";
	status = -1;
	action(1, 0, 0);
}

function action(mode, type, selection) {
	if (mode == -1) {
		status = -1;
	} else {
		if (status >= 0 && mode == 0) {
			cm.sendOk("感谢你的光临！");
			status = -1;
			return;
		}
		if (mode == 1) {
			status++;
		} else {
			status--;
		}
		if (status == 0) {
			var tex2 = "";
			var text = "";
			for (i = 0; i < 10; i++) {
				text += "";
			}
			text = "\r\n";
			text += "   		  		 #b" + 小熊 + "╭ #e#r" + 标题 + " #b#n╮" + 小熊 + "#n\r\n";
			text += "   #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "       #L1#" + 红心 + "[ " + (_CheckShow(1) ? "#g" : "#b") + "宝石套装#k ]" + 红心 + "#l#L2#" + 红心 + "[ " + (_CheckShow(2) ? "#g" : "#b") + "传说套装#k ]" + 红心 + "#l\r\n\r\n";
			text += "       #L3#" + 红心 + "[ " + (_CheckShow(3) ? "#g" : "#b") + "运动套装#k ]" + 红心 + "#l#L4#" + 红心 + "[ " + (_CheckShow(4) ? "#g" : "#b") + "紫金套装#k ]" + 红心 + "#l\r\n\r\n";
			text += "       #L5#" + 红心 + "[ " + (_CheckShow(5) ? "#g" : "#b") + "风暴套装#k ]" + 红心 + "#l#L6#" + 红心 + "[ " + (_CheckShow(6) ? "#g" : "#b") + "终极套装#k ]" + 红心 + "#l\r\n\r\n";
			text += "       #L7#" + 红心 + "[ " + (_CheckShow(7) ? "#g" : "#b") + "抽奖套装#k ]" + 红心 + "#l#L8#" + 红心 + "[ " + (_CheckShow(8) ? "#g" : "#b") + "外星套装#k ]" + 红心 + "#l\r\n\r\n";
			text += "  #b#e╰ ～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "              #r#L101#" + 感叹号 + "{ 轮回升星强化 }" + 感叹号 + "#l#k\r\n\r\n";
			text += "              #r#L100#" + 感叹号 + "{ 套装获取说明 }" + 感叹号 + "#l#k\r\n\r\n";
			cm.sendSimple(text);
		} else if (selection == 1) {属性 = 1;攻击 = 1;
			warp = 1;
			text = "";
			text += "" + 感叹号 + " [#v " + 可升级物品 + "#]#z " + 可升级物品 + "#[#r可强化的物品#k]#l\r\n";
			text += " #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "" + 圆形 + " [#v 1003242##r#c 1003242##k/1 ] [ #v 1052357##r#c 1052357##k/1 ] [ #v 1082314##r#c 1082314##k/1 ]#l\r\n";
			text += "" + 圆形 + " [#v 1102294##r#c 1102294##k/1 ] [ #v 1132092##r#c 1132092##k/1 ] [ #v 1072521##r#c 1072521##k/1 ]#l\r\n";
			text += "#b#e╰ ～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "" + 感叹号 + " 收集以上装备可强化物品[#b 属性+" + 属性 + " 攻击+" + 攻击 + " #k]\r\n";
			text += "" + 感叹号 + " #d请把这6件套放进背包,强化后[#r装备会消失#k]\r\n";
			text += "" + 感叹号 + " #d宝石套装最多只能激活#k[ #r" + chr.getBossLog1("宝石套装属性", 1) + " #k/ 10 #k]#d套\r\n";
			cm.sendYesNo(text);

		} else if (selection == 2) {属性 = 2;攻击 = 2;
			warp = 2;
			text = "";
			text += "" + 感叹号 + " [#v " + 可升级物品 + "#]#z " + 可升级物品 + "#[#r可强化的物品#k]#l\r\n";
			text += " #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "" + 圆形 + " [#v 1003364##r#c 1003364##k/1 ] [ #v 1052405##r#c 1052405##k/1 ] [ #v 1082391##r#c 1082391##k/1 ]#l\r\n";
			text += "" + 圆形 + " [#v 1102322##r#c 1102322##k/1 ] [ #v 1132110##r#c 1132110##k/1 ] [ #v 1072610##r#c 1072610##k/1 ]#l\r\n";
			text += "#b#e╰ ～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "" + 感叹号 + " 收集以上装备可强化物品[#b 属性+" + 属性 + " 攻击+" + 攻击 + " #k]\r\n";
			text += "" + 感叹号 + " #d请把这6件套放进背包,强化后[#r装备会消失#k]\r\n";
			text += "" + 感叹号 + " #d传说套装最多只能激活#k[ #r" + chr.getBossLog1("传说套装属性", 1) + " #k/ 10 #k]#d套\r\n";
			cm.sendYesNo(text);

		} else if (selection == 3) {属性 = 3;攻击 = 3;
			warp = 3;
			text = "";
			text += "" + 感叹号 + " [#v " + 可升级物品 + "#]#z " + 可升级物品 + "#[#r可强化的物品#k]#l\r\n";
			text += " #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "" + 圆形 + " [#v 1122174##r#c 1122174##k/1 ] [ #v 1032121##r#c 1032121##k/1 ] [ #v 1082401##r#c 1082401##k/1 ]#l\r\n";
			text += "" + 圆形 + " [#v 1042231##r#c 1042231##k/1 ] [ #v 1062148##r#c 1062148##k/1 ] [ #v 1072618##r#c 1072618##k/1 ]#l\r\n";
			text += "#b#e╰ ～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "" + 感叹号 + " 收集以上装备可强化物品[#b 属性+" + 属性 + " 攻击+" + 攻击 + " #k]\r\n";
			text += "" + 感叹号 + " #d请把这6件套放进背包,强化后[#r装备会消失#k]\r\n";
			text += "" + 感叹号 + " #d运动套装最多只能激活#k[ #r" + chr.getBossLog1("运动套装属性", 1) + " #k/ 10 #k]#d套\r\n";
			cm.sendYesNo(text);

		} else if (selection == 4) {属性 = 4;攻击 = 4;
			warp = 4;
			text = "";
			text += "" + 感叹号 + " [#v " + 可升级物品 + "#]#z " + 可升级物品 + "#[#r可强化的物品#k]#l\r\n";
			text += " #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "" + 圆形 + " [#v 1003552##r#c 1003552##k/1 ] [ #v 1052461##r#c 1052461##k/1 ] [ #v 1102441##r#c 1102441##k/1 ]#l\r\n";
			text += "" + 圆形 + " [#v 1082433##r#c 1082433##k/1 ] [ #v 1132154##r#c 1132154##k/1 ] [ #v 1072666##r#c 1072666##k/1 ]#l\r\n";
			text += "#b#e╰ ～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "" + 感叹号 + " 收集以上装备可强化物品[#b 属性+" + 属性 + " 攻击+" + 攻击 + " #k]\r\n";
			text += "" + 感叹号 + " #d请把这6件套放进背包,强化后[#r装备会消失#k]\r\n";
			text += "" + 感叹号 + " #d紫金套装最多只能激活#k[ #r" + chr.getBossLog1("紫金套装属性", 1) + " #k/ 10 #k]#d套\r\n";
			cm.sendYesNo(text);

		} else if (selection == 5) {属性 = 5;攻击 = 5;
			warp = 5;
			text = "";
			text += "" + 感叹号 + " [#v " + 可升级物品 + "#]#z " + 可升级物品 + "#[#r可强化的物品#k]#l\r\n";
			text += " #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "" + 圆形 + " [#v 1003561##r#c 1003561##k/1 ] [ #v 1052467##r#c 1052467##k/1 ] [ #v 1102467##r#c 1102467##k/1 ]#l\r\n";
			text += "" + 圆形 + " [#v 1082438##r#c 1082438##k/1 ] [ #v 1132161##r#c 1132161##k/1 ] [ #v 1072672##r#c 1072672##k/1 ]#l\r\n";
			text += "#b#e╰ ～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "" + 感叹号 + " 收集以上装备可强化物品[#b 属性+" + 属性 + " 攻击+" + 攻击 + " #k]\r\n";
			text += "" + 感叹号 + " #d请把这6件套放进背包,强化后[#r装备会消失#k]\r\n";
			text += "" + 感叹号 + " #d风暴套装最多只能激活#k[ #r" + chr.getBossLog1("风暴套装属性", 1) + " #k/ 10 #k]#d套\r\n";
			cm.sendYesNo(text);

		} else if (selection == 6) {属性 = 6;攻击 = 6;
			warp = 6;
			text = "";
			text += "" + 感叹号 + " [#v " + 可升级物品 + "#]#z " + 可升级物品 + "#[#r可强化的物品#k]#l\r\n";
			text += " #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "" + 圆形 + " [#v 1003740##r#c 1003740##k/1 ] [ #v 1052569##r#c 1052569##k/1 ] [ #v 1102506##r#c 1102506##k/1 ]#l\r\n";
			text += "" + 圆形 + " [#v 1082498##r#c 1082498##k/1 ] [ #v 1132182##r#c 1132182##k/1 ] [ #v 1072768##r#c 1072768##k/1 ]#l\r\n";
			text += "#b#e╰ ～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "" + 感叹号 + " 收集以上装备可强化物品[#b 属性+" + 属性 + " 攻击+" + 攻击 + " #k]\r\n";
			text += "" + 感叹号 + " #d请把这6件套放进背包,强化后[#r装备会消失#k]\r\n";
			text += "" + 感叹号 + " #d终极套装最多只能激活#k[ #r" + chr.getBossLog1("终极套装属性", 1) + " #k/ 10 #k]#d套\r\n";
			cm.sendYesNo(text);

		} else if (selection == 7) {属性 = 10;攻击 = 10;
			warp = 7;
			text = "";
			text += "" + 感叹号 + " [#v " + 可升级物品 + "#]#z " + 可升级物品 + "#[#r可强化的物品#k]#l\r\n";
			text += " #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "" + 圆形 + " [#v 1002939##r#c 1002939##k/1 ] [ #v 1050127##r#c 1050127##k/1 ] [ #v 1012251##r#c 1012251##k/1 ]#l\r\n";
			text += "" + 圆形 + " [#v 1082149##r#c 1082149##k/1 ] [ #v 1102163##r#c 1102163##k/1 ] [ #v 1092049##r#c 1092049##k/1 ]#l\r\n";
			text += "#b#e╰ ～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "" + 感叹号 + " 收集以上装备可强化物品[#b 属性+" + 属性 + " 攻击+" + 攻击 + " #k]\r\n";
			text += "" + 感叹号 + " #d请把这6件套放进背包,强化后[#r装备会消失#k]\r\n";
			text += "" + 感叹号 + " #d抽奖套装最多只能激活#k[ #r" + chr.getBossLog1("抽奖套装属性", 1) + " #k/ 10 #k]#d套\r\n";
			cm.sendYesNo(text);

		} else if (selection == 8) {属性 = 7;攻击 = 7;
			warp = 8;
			text = "";
			text += "" + 感叹号 + " [#v " + 可升级物品 + "#]#z " + 可升级物品 + "#[#r可强化的物品#k]#l\r\n";
			text += " #b#e╭～#n" + 粉星 + "" + 粉星 + "#e～～～～～～～～～～～～～～～～#n" + 粉星 + "#e～╮#n#k\r\n";
			text += "" + 圆形 + " [#v 1003540##r#c 1003540##k/1 ][ #v 1052460##r#c 1052460##k/1 ][ #v 1032142##r#c 1032142##k/1 ][ #v 1072664##r#c 1072664##k/1 ]#l\r\n";
			text += "" + 圆形 + " [#v 1082432##r#c 1082432##k/1 ][ #v 1112738##r#c 1112738##k/1 ][ #v 1122197##r#c 1122197##k/1 ][ #v 1132152##r#c 1132152##k/1 ]#l\r\n";
			text += "#b#e╰ ～～～～～～～～～～～～～～～～～～～～～ ╯#n#k#l\r\n";
			text += "" + 感叹号 + " 收集以上装备可强化物品[#b 属性+" + 属性 + " 攻击+" + 攻击 + " #k]\r\n";
			text += "" + 感叹号 + " #d请把这6件套放进背包,强化后[#r装备会消失#k]\r\n";
			text += "" + 感叹号 + " #d外星套装最多只能激活#k[ #r" + chr.getBossLog1("外星套装属性", 1) + " #k/ 10 #k]#d套\r\n";
			cm.sendYesNo(text);

		} else if (selection == 100) {
			text = " #k┏━━━━━━━━━━━━━━━━━━━━━━━━┓\r\n\r\n";
			text += "   [#r宝石套装#k][#b匠 人 街合成#k] [#r传说套装#k][#b匠 人 街合成#k]#l\r\n";
			text += "   [#r运动套装#k][#b每日任务获得#k] [#r紫金套装#k][#b复古BOSS掉落#k]#l\r\n";
			text += "   [#r风暴套装#k][#b复古BOSS掉落#k] [#r终极套装#k][#b复古BOSS掉落#k]#l\r\n";
			text += "   [#r抽奖套装#k][#b市场抽奖获得#k] [#r外星套装#k][#b维利塔斯掉落#k]#l\r\n\r\n";
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛\r\n";
			status = -1;
			cm.sendOk(text);
			
		} else if (selection == 101) {
			cm.openNpc(9000441,1);	
			
		} else if (status = 1) {
			if (cm["getInventory"](1)["getItem"](1) == null) {
				cm["sendOk"]("[#v"+可升级物品+"#]#z"+可升级物品+"#\r\n" + 正方箭头 + " 请把#r#z"+可升级物品+"##k放到装备栏第一格");
				cm["dispose"]();
				return;
			}
			id = cm["getInventory"](1)["getItem"](1)["getItemId"]();
			if (id != 可升级物品) {
				cm["sendOk"]("[#v"+可升级物品+"#]#z"+可升级物品+"#\r\n" + 正方箭头 + " 请把#r#z"+可升级物品+"##k放到装备栏第一格");
				cm["dispose"]();
				return;
			}
			var id = cm.getInventory(1).getItem(1).getItemId();
			var item = cm.getInventory(1).getItem(1).copy();
			var ii = Packages.server.MapleItemInformationProvider.getInstance();
			//var type = ii.getInventoryType(id);
			var job = cm.getJob();
			if (warp == 1) {
				if (!cm.haveItem(1003242, 1)) {
					cm.sendOk("请将#v 1003242##b#z 1003242##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1052357, 1)) {
					cm.sendOk("请将#v 1052357##b#z 1052357##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1102294, 1)) {
					cm.sendOk("请将#v 1102294##b#z 1102294##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1072521, 1)) {
					cm.sendOk("请将#v 1072521##b#z 1072521##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1082314, 1)) {
					cm.sendOk("请将#v 1082314##b#z 1082314##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1132092, 1)) {
					cm.sendOk("请将#v 1132092##b#z 1132092##k放入背包中");
					status = -1;
				} else if (chr.getBossLog1("宝石套装属性", 1) >= 10) {
					cm.sendOk("宝石套装只能提炼十套");
					status = -1;
				} else {
/*
					if((job >= 100 && job <= 132) || (job >= 500 && job <= 512) || (job >= 2000 && job <= 2112) || (job >= 1100 && job <= 1112)){
						item.setStr (item.getStr()+宝石属性);
						item.setWatk(item.getWatk()+宝石物攻);
					}
					if((job >= 300 && job <= 322) || (job >= 520 && job <= 522) || (job >= 3100 && job <= 3112)){
						item.setDex (item.getDex()+宝石属性);
						item.setWatk(item.getWatk()+宝石物攻);
					}
					if((job >= 200 && job <= 232) || (job >= 2100 && job <= 2112)){
						item.setInt (item.getInt()+宝石属性);
						item.setMatk(item.getMatk()+宝石魔攻);
					}
					if((job >= 400 && job <= 422) || (job >= 4100 && job <= 4112)){
						item.setLuk (item.getLuk()+宝石属性);
						item.setWatk(item.getWatk()+宝石物攻);
					}
					*/
					item.setStr(item.getStr() + 属性);
					item.setDex(item.getDex() + 属性);
					item.setInt(item.getInt() + 属性);
					item.setLuk(item.getLuk() + 属性);
					item.setWatk(item.getWatk() + 攻击);
					item.setMatk(item.getMatk() + 攻击);
					item.setLocked(1);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
					cm.gainItem(1003242, -1);
					cm.gainItem(1052357, -1);
					cm.gainItem(1102294, -1);
					cm.gainItem(1072521, -1);
					cm.gainItem(1082314, -1);
					cm.gainItem(1132092, -1);
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功
					chr.setBossLog1("宝石套装属性", 1);
					cm.sendOk("宝石套装属性强化成功");
					cm.道具喇叭("[套装激活]"+cm.getName()+""," 成功激活了一次《宝石套装》，大家一起来恭喜他吧",1,1);
					status = -1;
				}
			} else if (warp == 2) {
				if (!cm.haveItem(1132110, 1)) {
					cm.sendOk("请将#v 1132110##b#z 1132110##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1102322, 1)) {
					cm.sendOk("请将#v 1102322##b#z 1102322##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1082391, 1)) {
					cm.sendOk("请将#v 1082391##b#z 1082391##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1072610, 1)) {
					cm.sendOk("请将#v 1072610##b#z 1072610##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1052405, 1)) {
					cm.sendOk("请将#v 1052405##b#z 1052405##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1003364, 1)) {
					cm.sendOk("请将#v 1003364##b#z 1003364##k放入背包中");
					status = -1;
				} else if (chr.getBossLog1("传说套装属性", 1) >= 10) {
					cm.sendOk("传说套装只能提炼十套");
					status = -1;
				} else {
					var id = cm.getInventory(1).getItem(1).getItemId();
					var item = cm.getInventory(1).getItem(1).copy();
					var ii = Packages.server.MapleItemInformationProvider.getInstance();
					//var type = ii.getInventoryType(id);

					item.setStr(item.getStr() + 属性);
					item.setDex(item.getDex() + 属性);
					item.setInt(item.getInt() + 属性);
					item.setLuk(item.getLuk() + 属性);
					item.setWatk(item.getWatk() + 攻击);
					item.setMatk(item.getMatk() + 攻击);
					item.setLocked(1);
					//Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, 1, 1, false);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
					cm.gainItem(1132110, -1);
					cm.gainItem(1102322, -1);
					cm.gainItem(1082391, -1);
					cm.gainItem(1072610, -1);
					cm.gainItem(1052405, -1);
					cm.gainItem(1003364, -1);
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功
					chr.setBossLog1("传说套装属性", 1);
					cm.sendOk("传说套装属性强化成功");
					cm.道具喇叭("[套装激活]"+cm.getName()+""," 成功激活了一次《传说套装》，大家一起来恭喜他吧",1,1);
					status = -1;
				}
			} else if (warp == 3) {
				if (!cm.haveItem(1122174, 1)) {
					cm.sendOk("请将#v 1122174##b#z 1122174##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1082401, 1)) {
					cm.sendOk("请将#v 1082401##b#z 1082401##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1072618, 1)) {
					cm.sendOk("请将#v 1072618##b#z 1072618##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1062148, 1)) {
					cm.sendOk("请将#v 1062148##b#z 1062148##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1042231, 1)) {
					cm.sendOk("请将#v 1042231##b#z 1042231##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1032121, 1)) {
					cm.sendOk("请将#v 1032121##b#z 1032121##k放入背包中");
					status = -1;
				} else if (chr.getBossLog1("运动套装属性", 1) >= 10) {
					cm.sendOk("运动套装只能提炼十套");
					status = -1;
				} else {
					var id = cm.getInventory(1).getItem(1).getItemId();
					var item = cm.getInventory(1).getItem(1).copy();
					var ii = Packages.server.MapleItemInformationProvider.getInstance();
					//var type = ii.getInventoryType(id);
					var job = cm.getJob();

					item.setStr(item.getStr() + 属性);
					item.setDex(item.getDex() + 属性);
					item.setInt(item.getInt() + 属性);
					item.setLuk(item.getLuk() + 属性);
					item.setWatk(item.getWatk() + 攻击);
					item.setMatk(item.getMatk() + 攻击);
					item.setLocked(1);
					//Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, 1, 1, false);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
					cm.gainItem(1122174, -1);
					cm.gainItem(1082401, -1);
					cm.gainItem(1072618, -1);
					cm.gainItem(1062148, -1);
					cm.gainItem(1042231, -1);
					cm.gainItem(1032121, -1);
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功
					chr.setBossLog1("运动套装属性", 1);
					cm.sendOk("运动套装属性强化成功");
					cm.道具喇叭("[套装激活]"+cm.getName()+""," 成功激活了一次《运动套装》，大家一起来恭喜他吧",1,1);
					status = -1;
				}
			} else if (warp == 4) {
				if (!cm.haveItem(1132154, 1)) {
					cm.sendOk("请将#v 1132154##b#z 1132154##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1102441, 1)) {
					cm.sendOk("请将#v 1102441##b#z 1102441##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1082433, 1)) {
					cm.sendOk("请将#v 1082433##b#z 1082433##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1072666, 1)) {
					cm.sendOk("请将#v 1072666##b#z 1072666##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1052461, 1)) {
					cm.sendOk("请将#v 1052461##b#z 1052461##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1003552, 1)) {
					cm.sendOk("请将#v 1003552##b#z 1003552##k放入背包中");
					status = -1;
				} else if (chr.getBossLog1("紫金套装属性", 1) >= 10) {
					cm.sendOk("紫金套装只能提炼十套");
					status = -1;
				} else {
					var id = cm.getInventory(1).getItem(1).getItemId();
					var item = cm.getInventory(1).getItem(1).copy();
					var ii = Packages.server.MapleItemInformationProvider.getInstance();
					//var type = ii.getInventoryType(id);
					var job = cm.getJob();

					item.setStr(item.getStr() + 属性);
					item.setDex(item.getDex() + 属性);
					item.setInt(item.getInt() + 属性);
					item.setLuk(item.getLuk() + 属性);
					item.setWatk(item.getWatk() + 攻击);
					item.setMatk(item.getMatk() + 攻击);
					item.setLocked(1);
					//Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, 1, 1, false);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
					cm.gainItem(1132154, -1);
					cm.gainItem(1102441, -1);
					cm.gainItem(1082433, -1);
					cm.gainItem(1072666, -1);
					cm.gainItem(1052461, -1);
					cm.gainItem(1003552, -1);
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功
					chr.setBossLog1("紫金套装属性", 1);
					cm.sendOk("紫金套装属性强化成功");
					cm.道具喇叭("[套装激活]"+cm.getName()+""," 成功激活了一次《紫金套装》，大家一起来恭喜他吧",1,1);
					status = -1;
				}
			} else if (warp == 5) {
				if (!cm.haveItem(1132161, 1)) {
					cm.sendOk("请将#v 1132161##b#z 1132161##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1102467, 1)) {
					cm.sendOk("请将#v 1102467##b#z 1102467##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1082438, 1)) {
					cm.sendOk("请将#v 1082438##b#z 1082438##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1072672, 1)) {
					cm.sendOk("请将#v 1072672##b#z 1072672##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1052467, 1)) {
					cm.sendOk("请将#v 1052467##b#z 1052467##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1003561, 1)) {
					cm.sendOk("请将#v 1003561##b#z 1003561##k放入背包中");
					status = -1;
				} else if (chr.getBossLog1("风暴套装属性", 1) >= 10) {
					cm.sendOk("风暴套装只能提炼十套");
					status = -1;
				} else {
					var id = cm.getInventory(1).getItem(1).getItemId();
					var item = cm.getInventory(1).getItem(1).copy();
					var ii = Packages.server.MapleItemInformationProvider.getInstance();
					//var type = ii.getInventoryType(id);
					var job = cm.getJob();

					item.setStr(item.getStr() + 属性);
					item.setDex(item.getDex() + 属性);
					item.setInt(item.getInt() + 属性);
					item.setLuk(item.getLuk() + 属性);
					item.setWatk(item.getWatk() + 攻击);
					item.setMatk(item.getMatk() + 攻击);
					item.setLocked(1);
					//Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, 1, 1, false);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
					cm.gainItem(1132161, -1);
					cm.gainItem(1102467, -1);
					cm.gainItem(1082438, -1);
					cm.gainItem(1072672, -1);
					cm.gainItem(1052467, -1);
					cm.gainItem(1003561, -1);
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功
					chr.setBossLog1("风暴套装属性", 1);
					cm.sendOk("风暴套装属性强化成功");
					cm.道具喇叭("[套装激活]"+cm.getName()+""," 成功激活了一次《风暴套装》，大家一起来恭喜他吧",1,1);
					status = -1;
				}
			} else if (warp == 6) {
				if (!cm.haveItem(1132182, 1)) {
					cm.sendOk("请将#v 1132182##b#z 1132182##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1102506, 1)) {
					cm.sendOk("请将#v 1102506##b#z 1102506##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1082498, 1)) {
					cm.sendOk("请将#v 1082498##b#z 1082498##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1072768, 1)) {
					cm.sendOk("请将#v 1072768##b#z 1072768##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1052569, 1)) {
					cm.sendOk("请将#v 1052569##b#z 1052569##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1003740, 1)) {
					cm.sendOk("请将#v 1003740##b#z 1003740##k放入背包中");
					status = -1;
				} else if (chr.getBossLog1("终极套装属性", 1) >= 10) {
					cm.sendOk("终极套装只能提炼十套");
					status = -1;
				} else {
					var id = cm.getInventory(1).getItem(1).getItemId();
					var item = cm.getInventory(1).getItem(1).copy();
					var ii = Packages.server.MapleItemInformationProvider.getInstance();
					//var type = ii.getInventoryType(id);
					var job = cm.getJob();

					item.setStr(item.getStr() + 属性);
					item.setDex(item.getDex() + 属性);
					item.setInt(item.getInt() + 属性);
					item.setLuk(item.getLuk() + 属性);
					item.setWatk(item.getWatk() + 攻击);
					item.setMatk(item.getMatk() + 攻击);
					item.setLocked(1);
					//Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, 1, 1, false);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
					cm.gainItem(1132182, -1);
					cm.gainItem(1102506, -1);
					cm.gainItem(1082498, -1);
					cm.gainItem(1072768, -1);
					cm.gainItem(1052569, -1);
					cm.gainItem(1003740, -1);
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功
					chr.setBossLog1("终极套装属性", 1);
					cm.sendOk("终极套装属性强化成功");
					cm.道具喇叭("[套装激活]"+cm.getName()+""," 成功激活了一次《终极套装》，大家一起来恭喜他吧",1,1);
					status = -1;
				}
			} else if (warp == 7) {
				if (!cm.haveItem(1102163, 1)) {
					cm.sendOk("请将#v 1102163##b#z 1102163##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1012251, 1)) {
					cm.sendOk("请将#v 1012251##b#z 1012251##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1092049, 1)) {
					cm.sendOk("请将#v 1092049##b#z 1092049##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1082149, 1)) {
					cm.sendOk("请将#v 1082149##b#z 1082149##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1050127, 1)) {
					cm.sendOk("请将#v 1050127##b#z 1050127##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1002939, 1)) {
					cm.sendOk("请将#v 1002939##b#z 1002939##k放入背包中");
					status = -1;
				} else if (chr.getBossLog1("抽奖套装属性", 1) >= 10) {
					cm.sendOk("抽奖套装只能提炼十套");
					status = -1;
				} else {
					var id = cm.getInventory(1).getItem(1).getItemId();
					var item = cm.getInventory(1).getItem(1).copy();
					var ii = Packages.server.MapleItemInformationProvider.getInstance();
					//var type = ii.getInventoryType(id);
					var job = cm.getJob();

					item.setStr(item.getStr() + 属性);
					item.setDex(item.getDex() + 属性);
					item.setInt(item.getInt() + 属性);
					item.setLuk(item.getLuk() + 属性);
					item.setWatk(item.getWatk() + 攻击);
					item.setMatk(item.getMatk() + 攻击);
					item.setLocked(1);
					//Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, 1, 1, false);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
					cm.gainItem(1102163, -1);
					cm.gainItem(1012251, -1);
					cm.gainItem(1082149, -1);
					cm.gainItem(1092049, -1);
					cm.gainItem(1050127, -1);
					cm.gainItem(1002939, -1);
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功
					chr.setBossLog1("抽奖套装属性", 1);
					cm.sendOk("抽奖套装属性强化成功");
					cm.道具喇叭("[套装激活]"+cm.getName()+""," 成功激活了一次《抽奖套装》，大家一起来恭喜他吧",1,1);
					status = -1;
				}
			} else if (warp == 8) {
				if (!cm.haveItem(1132152, 1)) {
					cm.sendOk("请将#v 1132152##b#z 1132152##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1122197, 1)) {
					cm.sendOk("请将#v 1122197##b#z 1122197##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1112738, 1)) {
					cm.sendOk("请将#v 1112738##b#z 1112738##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1082432, 1)) {
					cm.sendOk("请将#v 1082432##b#z 1082432##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1072664, 1)) {
					cm.sendOk("请将#v 1072664##b#z 1072664##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1052460, 1)) {
					cm.sendOk("请将#v 1052460##b#z 1052460##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1032142, 1)) {
					cm.sendOk("请将#v 1032142##b#z 1032142##k放入背包中");
					status = -1;
				} else if (!cm.haveItem(1003540, 1)) {
					cm.sendOk("请将#v 1003540##b#z 1003540##k放入背包中");
					status = -1;
				} else if (chr.getBossLog1("外星套装属性", 1) >= 10) {
					cm.sendOk("外星套装只能提炼十套");
					status = -1;
				} else {
					var id = cm.getInventory(1).getItem(1).getItemId();
					var item = cm.getInventory(1).getItem(1).copy();
					var ii = Packages.server.MapleItemInformationProvider.getInstance();
					//var type = ii.getInventoryType(id);
					var job = cm.getJob();

					item.setStr(item.getStr() + 属性);
					item.setDex(item.getDex() + 属性);
					item.setInt(item.getInt() + 属性);
					item.setLuk(item.getLuk() + 属性);
					item.setWatk(item.getWatk() + 攻击);
					item.setMatk(item.getMatk() + 攻击);
					item.setLocked(1);
					//Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), type, 1, 1, false);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
					Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
					cm.gainItem(1132152, -1);
					cm.gainItem(1122197, -1);
					cm.gainItem(1112738, -1);
					cm.gainItem(1082432, -1);
					cm.gainItem(1072664, -1);
					cm.gainItem(1052460, -1);
					cm.gainItem(1032142, -1);
					cm.gainItem(1003540, -1);
					cm.ShowWZEffect("Effect/BasicEff.img/SkillBook/Success/0"); //成功
					chr.setBossLog1("外星套装属性", 1);
					cm.sendOk("外星套装属性强化成功");
					cm.道具喇叭("[套装激活]"+cm.getName()+""," 成功激活了一次《外星套装》，大家一起来恭喜他吧",1,1);
					status = -1;
				}
			}
		}
	}
}

function _CheckShow(id) {
	var isShow = true;
	for (var i = 0; i < equips.length; i++) {
		if (equips[i][0] == id) {
			for (var k = 0; k < equips[i][1].length; k++) {
				if (cm.itemQuantity(equips[i][1][k]) <= 0) {
					isShow = false;
				}
			}
			break;
		}
	}
	return isShow;
}


//╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯
//╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯╰╯