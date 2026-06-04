var tt = "#fEffect/CharacterEff/1082565/0/0#"; //饼干兔子
var bb = "#fUI/UIWindow4.img/PQRank/rank/gold#";//皇冠1
var cc = "#fUI/UIWindow2.img/Quest/quest_info/summary_icon/startcondition#"//开始条件
var A0 = "#fUI/Basic/LevelNo/0#"; //[1]+1
var A1 = "#fUI/Basic/LevelNo/1#"; //[1]+1
var A2 = "#fUI/Basic/LevelNo/2#"; //[1]+1
var A3 = "#fUI/Basic/LevelNo/3#"; //[1]+1
var A4 = "#fUI/Basic/LevelNo/4#"; //[1]+1
var A5 = "#fUI/Basic/LevelNo/5#"; //[1]+1
var z3 = "#fUI/GuildMark/Mark/Animal/00002006/16#";
var z4 = "#fUI/UIWindow2/MemoInGame/Get/BtClame/pressed/0#";
var z5 = "#fEffect/CharacterEff/1112904/2/1#";//五角花
var z6 = "#fUI/GuildMark/Mark/Etc/00009004/3#";//皇冠
var z7 = "#fUI/GuildMark/Mark/Etc/00009023/10#";//皇冠
var z8 = "#fUI/NameTag/medal/468/c#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var status;
var selstatus = -1;
var itemList = new Array();
var inventoryType;
var deleteSlot;
var deleteQuantity;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 3 && mode == 0) {
		cm.sendOk("保留成功！请继续选择要保留的物品...");
		status = 1;
		return;
    } else if (mode <= 0) {
        cm.dispose();
		return;
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            var text = "\t\t\t#b  #d#e  道具清理中心  #d#d#d\r\n\r\n";
            text += "\t\t #L0#"+正方箭头+"清理包裹内#r指定#d道具#l\r\n\r\n\r\n";
			text += "\t\t #L1#"+正方箭头+"清理包裹内#r24格后#d道具#l\r\n\r\n\r\n";
            text += "\r\n\r\n\r\n\r\n";
            cm.sendSimple(text);
        } else {
            if (selstatus == -1) {
                selstatus = selection;
            }
            switch (selstatus) {
                case 0:
                    deleteItemBySlot(selection);
                    break;
                case 1:
                    deleteItemBySlot1(selection);
					break;
				case 2:
                    deleteItemBySlot2(selection);
					break;
            }
        }
    }
}

function deleteItemBySlot(selection) {
    if (status == 1) {
        text = "\t\t#e- 请选择要清理的道具类型 -#n\r\n#b";
        text += "\t\t\t\t#L1#"+正方箭头+"装备栏#l\r\n";
        text += "\t\t\t\t#L2#"+正方箭头+"消耗栏#l\r\n";
        text += "\t\t\t\t#L4#"+正方箭头+"其它栏#l\r\n";
        text += "\t\t\t\t#L3#"+正方箭头+"设置栏#l\r\n";
        text += "\t\t\t\t#L5#"+正方箭头+"特殊栏#l\r\n";
        cm.sendSimple(text);
    } else if (status == 2) {
        inventoryType = selection;
		var aaa = false;
			var indexof = 1;
	        inv = cm.getInventory(inventoryType);
            text = "\t\t\t  #e- 请选择要清理的道具 -#n\r\n\r\n#b";
			for (var i = 1; i <= inv.getSlotLimit(); i++) {
			var it = inv.getItem(i);
                if (it == null) {
                    continue;
                }
			var itemid = it.getItemId();
			    aaa = true;
			text += "#L" + i + "##v" + itemid + "##l";
            if (indexof > 1 && indexof % 6 == 0) {
                text += "\r\n";
            }
            indexof++;
		}
        if (!aaa) {
            cm.playerMessage(1,"该栏中没有道具");
            cm.dispose();
            return;
        }
        cm.sendSimple(text + "#k");
    } else if (status == 3) {
        var item = cm.getInventory(inventoryType).getItem(selection);
        deleteSlot = selection;
        deleteQuantity = item.getQuantity();
        text = "#e确定要清理#r#v" + item.getItemId() + "##z" + item.getItemId() + "# " + deleteQuantity + "个 #k吗？";
        cm.sendNextPrev(text);
    } else if (status == 4) {
        cm.removeSlot(inventoryType, deleteSlot, deleteQuantity);
        cm.sendOk("清理成功，祝你游戏愉快~");
        status = 0;
    }
}

function deleteItemBySlot1(selection) {
    if (status == 1) {
		
        text = "\t\t#e- 请选择要清理的道具类型 -#n\r\n#b";
        text += "\t\t\t\t#L1#"+正方箭头+"装备栏#l\r\n";
        text += "\t\t\t\t#L2#"+正方箭头+"消耗栏#l\r\n";
        text += "\t\t\t\t#L4#"+正方箭头+"其它栏#l\r\n";
        text += "\t\t\t\t#L3#"+正方箭头+"设置栏#l\r\n";
        text += "\t\t\t\t#L5#"+正方箭头+"特殊栏#l\r\n";
        cm.sendSimple(text);
    } else if (status == 2) {
        inventoryType = selection;
		if (selection == 1) {
		   var xianshi = "装备栏";
 } else if (selection == 2) {
	       var xianshi = "消耗栏";
 } else if (selection == 4) {
	       var xianshi = "其他栏";
 } else if (selection == 3) {
	       var xianshi = "设置栏";
 } else if (selection == 5) {
	       var xianshi = "特殊栏";
            }
	    var aaa = false;
	        inv = cm.getInventory(inventoryType);
		    text = "#r"+ xianshi +"#b 内有以下道具，你确定都要清理吗？#k\r\n";
			for (var i = 25; i <= inv.getSlotLimit(); i++) {
			var it = inv.getItem(i);
                if (it == null) {
                    continue;
                }
			var itemid = it.getItemId();
			    aaa = true;
			text += "#e名称：#n#v" + itemid + "##b#z" + itemid + "##k，#e数量：#n#r"+ it.getQuantity() +"#k 个。\r\n";
		    }
        if (!aaa) {
            cm.playerMessage(1,"该栏中没有道具");
            cm.dispose();
            return;
        }
        cm.sendYesNo(text);
    } else if (status == 3) {
	        inv = cm.getInventory(inventoryType);
			text = "您成功清理了以下道具，祝您游戏愉快！~\r\n";
			for (var i = 25; i <= inv.getSlotLimit(); i++) {
			var it = inv.getItem(i);
                if (it == null) {
                    continue;
                }
			var itemid = it.getItemId();
			cm.removeSlot(inventoryType, i, it.getQuantity());
		    }
        cm.sendOk("您成功清理了道具，祝您游戏愉快！");
        status = 0;
    }
}
