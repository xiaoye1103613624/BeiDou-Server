var 广播 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 使用道具ID = 5010019;//打开月卡商店所需道具
var 云朵1 = "#fEffect/ItemEff.img/1072919/effect/jump/2#"
var 云朵2 = "#fEffect/ItemEff.img/1072919/effect/jump/0#"
var 云朵3 = "#fEffect/ItemEff.img/1072919/effect/jump/1#"
var 云朵4 = "#fEffect/ItemEff.img/1072919/effect/walk1/2#"

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
			text += "\t#k" + 广播 + " 欢迎来到[#r" + cm.开服名称() + "#k]快捷商店\r\n";
			text += "\t#k" + 广播 + " 月卡商店跟杂货商店一样[#r售卖价格五折#k]\r\n";
			//text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
	  		//text = "#d\t请选择你想要打开的商店：\r\n"
		    text += "\t#L0##e#r"+云朵1+"打开 #b杂货商店"+云朵4+"#k#l#n\r\n\r\n"
		    text += "\t#L1##e#d"+云朵1+"打开 #r月卡商店"+云朵4+"#k#l#n\r\n\r\n"
		    text += "\t#L2##e#r"+云朵1+"打开 #r元宝商店"+云朵4+"#k#l#n\r\n\r\n"
		//	text += "\t#L3##e#r"+云朵1+"打开 #r点装商店"+云朵4+"#k#l#n\r\n\r\n"
			text += "\t#L4##e#r"+云朵1+"打开 #r技能商店"+云朵4+"#k#l#n\r\n\r\n"
	        //text += "\t#L3##e#d"+云朵1+"打开 #r赞助商店"+云朵4+"#k#l#n\r\n\r\n"
			text += " #k┗━━━━━━━━━━━━━━━━━━━━━━━━┛#k\r\n";
			text += "\r\n\r\n";
			cm.sendSimple(text);
		}else if (status == 1) {
			if (selection == 0) {
				cm.dispose();
				cm.openShop(43);

			} else if (selection == 1) {
				if (cm.haveItem(使用道具ID, 1) == false) {
					cm.sendOk("背包里未检测到 ：#r#z" + 使用道具ID + "##k，无法打开商店。");
					cm.dispose();
				} else {
					cm.dispose();
					cm.openShop(12);
				}
				
				} else if (selection == 2) {
					cm.dispose();
				cm.openNpc(9900004, "元宝商店1");
				
				} else if (selection == 3) {
					cm.dispose();
				cm.openNpc(9900004, "赞助商店");
				
				} else if (selection == 4) {
					cm.dispose();
				cm.openNpc(9000444, "技能商店");
				
			}
		}
	}
}