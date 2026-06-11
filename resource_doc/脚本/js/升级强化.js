var 强化中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/0#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var a = 0;
var text;
var nice = false;
var selects; //记录玩家的选项
var buynum = 0;
var 几率 = Math.floor(Math.random() * 99);
var tz2 = "#fEffect/CharacterEff/1082565/2/0#"; //蓝兔子
var QHXH = 3605002;
var MVPXH = 3605020;
var VIPXH = 3605021;
var 加属性 = Math.floor(Math.random()* 10)+5;
var MVP属性 = Math.floor(Math.random() * 20)+10;
var VIP属性 = Math.floor(Math.random() * 10)+10;
var sel;
var attr = 5;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status == 0 && mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) {
        status++;
    } else {
        status--;
    }
    if (status == 0) {
        if (cm.getInventory(1).getItem(1) == null) {
            cm.sendOk("请将需要强化的装备放到装备栏第一格~");
            cm.dispose();
        } else {
			var ItemID = cm.getInventory(1).getItem(1).getItemId(); 
			var 升级次数 = cm.getInventory(1).getItem(1).getUpgradeSlots();
			var 当前 = cm.getInventory(1).getItem(1).getLevel();
            var selStr = ""+dd+"\r\n\t\t\t"+强化中心+"\r\n"+群粉心+"";    //#b" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "" + tz2 + "\r\n
			//selStr += "请选择卷轴#v2615032# #v2532001# #v2530000# #v2533000# #v2613008#\r\n"
			//selStr += "消耗一种卷轴#v2615032# #v2532001# #v2530000# #v2533000# #v2613008#全属性+5\r\n";
			selStr += "您选择升级强化的装备为：\r\n#r#e#v" + ItemID + "#【#z" + ItemID + "#】#k#n当前已升级 #n#r+ "+当前+" #k 可升级 #n#r"+升级次数+" #k次\r\n\r\n";
			
			//selStr += "#v"+MVPXH+"#x1每次强化消耗#v 2140002#2000W游戏币\r\n"; 
			//selStr += "#v"+QHXH+"#x1每次强化消耗#v 2140002#1000W游戏币\r\n";
            //selStr += "#r#L0##b#e消耗#v"+MVPXH+"##z"+MVPXH+"#=随机全属性+0-20成功率：50%#l\r\n";
            selStr += "\t\t#r#L0##r#e#v"+QHXH+"##z"+QHXH+"#强化#v"+QHXH+"##l#n#k\r\n\r\n";
			selStr += "说明：每次消耗#b#z"+QHXH+"##r*1 #b游戏金币#r*2000W\r\n#n#k全属性加 #r5 #k- #r15#k 点  成功率 #r60%#k  强化上限 #r100 #k次\r\n"; 
			
            selStr += "\t\t#r#L1##r#e#v"+MVPXH+"##z"+MVPXH+"#强化#v"+MVPXH+"##l#n#k\r\n\r\n";
			selStr += "说明：每次消耗#b#z"+MVPXH+"##r*1 #b游戏金币#r*2000W\r\n#n#k全属性加 #r10 #k- #r30#k 点  成功率 #r80%#k  强化上限 #r100 #k次\r\n"; 
			
            //selStr += "#r#L1##b#e消耗#v"+QHXH+"#x1=全属性+5 消耗2000万提升成功率：40%#l\r\n";
            //selStr += "#r#L2##b#e消耗#v"+QHXH+"#x1=全属性+5 消耗3000万提升成功率：60%#l\r\n";
            //selStr += "#r#L3##b#e消耗#v"+QHXH+"#x1=全属性+5 消耗4000万提升成功率：75%#l\r\n";

			
            selStr += "\t\t#r#e#L2##v"+VIPXH+"##z"+VIPXH+"#强化#v"+VIPXH+"##l#n#k\r\n\r\n";
			selStr += "说明：每次消耗#b#z"+VIPXH+"##r*1 #b游戏金币#r*2000W\r\n#n#k全属性加 #r10 #k- #r20#k 点  成功率 #r100%#k  强化上限 #r无限 #k次\r\n"; 
            cm.sendSimple(selStr);
        }
    } else if (status == 1) {
        sel = selection;
		var item = cm.getInventory(1).getItem(1).copy();
		if (item.getUpgradeSlots() == 0){
			cm.sendOk("道具没有升级次数，无法强化");
			cm.dispose();
			return;
		}
		if (selection == 0) {
			var itemIDxx = cm.getInventory(1).getItem(1).getItemId();
            if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置第一格~");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置在物品栏第一格。");
                cm.dispose();
            } else if (cm.isCash(cm.getInventory(1).getItem(1).getItemId()) == true) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 属于#r点装类#k，无法放入。");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1).getExpiration() != -1) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 为#r限时#k装备，无法放入。");
                cm.dispose();
            }else if (cm.haveItem(QHXH, 1) == false) {
					cm.sendOk("#v"+QHXH+"#不足，请确认后再来。");
					cm.dispose();
			}else if (cm.getPlayer().getMeso() < 20000000) {
					cm.sendOk("金币不足。");					
            } else if (cm.getInventory(1).getItem(1) != null) {
                if (几率 < 50) {
                    var item = cm.getInventory(1).getItem(1).copy();
                    item.setStr(item.getStr() + 加属性);
                    item.setInt(item.getInt() + 加属性);
                    item.setLuk(item.getLuk() + 加属性);
                    item.setDex(item.getDex() + 加属性);
                    item.setWatk(item.getWatk() + 加属性);
                    item.setMatk(item.getMatk() + 加属性);
                    item.setLocked(1);
					item.setLevel(item.getLevel() + 1);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
                    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(QHXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("强化成功，当前装备强化完毕。");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]成功为装备升级强化一次，恭喜他！");
                    cm.dispose();
                } else {
					var item = cm.getInventory(1).getItem(1).copy();
					item.setStr(item.getStr() - 0);
                    item.setInt(item.getInt() - 0);
                    item.setLuk(item.getLuk() - 0);
                    item.setDex(item.getDex() - 0);
                    item.setWatk(item.getWatk() - 0);
                    item.setMatk(item.getMatk() - 0);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(QHXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("很可惜，装备强化失败.");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]装备升级强化失败！");
                    cm.dispose();
                }
            }
        } else if (selection == 1) {
            var itemIDxx = cm.getInventory(1).getItem(1).getItemId();
            if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置第一格~");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置在物品栏第一格。");
                cm.dispose();
            } else if (cm.isCash(cm.getInventory(1).getItem(1).getItemId()) == true) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 属于#r点装类#k，无法放入。");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1).getExpiration() != -1) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 为#r限时#k装备，无法放入。");
                cm.dispose();
            } else if (cm.haveItem(MVPXH, 1) == false) {
                cm.sendOk("#v"+MVPXH+"#不足，请确认后再来。");
                cm.dispose();
			}else if (cm.getPlayer().getMeso() < 20000000) {
					cm.sendOk("金币不足。");
            } else if (cm.getInventory(1).getItem(1) != null) {
                if (几率 < 70) {
                    var item = cm.getInventory(1).getItem(1).copy();
                    item.setStr(item.getStr() + MVP属性);
                    item.setInt(item.getInt() + MVP属性);
                    item.setLuk(item.getLuk() + MVP属性);
                    item.setDex(item.getDex() + MVP属性);
                    item.setWatk(item.getWatk() + MVP属性);
                    item.setMatk(item.getMatk() + MVP属性);
                    item.setLocked(1);
					item.setLevel(item.getLevel() + 1);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
                    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(MVPXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("强化成功，当前装备强化完毕。");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]成功为装备升级强化一次，恭喜他！");
                    cm.dispose();
                } else {
					var item = cm.getInventory(1).getItem(1).copy();
					item.setStr(item.getStr() - 0);
                    item.setInt(item.getInt() - 0);
                    item.setLuk(item.getLuk() - 0);
                    item.setDex(item.getDex() - 0);
                    item.setWatk(item.getWatk() - 0);
                    item.setMatk(item.getMatk() - 0);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(MVPXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("很可惜，装备强化失败.");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]装备升级强化失败！");
                    cm.dispose();
                }
            }
        } else if (selection == 2) {
			var itemIDxx = cm.getInventory(1).getItem(1).getItemId();
            if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置第一格~");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置在物品栏第一格。");
                cm.dispose();
            } else if (cm.isCash(cm.getInventory(1).getItem(1).getItemId()) == true) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 属于#r点装类#k，无法放入。");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1).getExpiration() != -1) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 为#r限时#k装备，无法放入。");
                cm.dispose();
            } else if (cm.haveItem(VIPXH, 1) == false) {
                cm.sendOk("#v"+VIPXH+"#不足，请确认后再来。");
                cm.dispose();
			}else if (cm.getPlayer().getMeso() < 20000000) {
					cm.sendOk("金币不足。");
            } else if (cm.getInventory(1).getItem(1) != null) {
                //if (几率 < 100) {
                    var item = cm.getInventory(1).getItem(1).copy();
                    item.setStr(item.getStr() + VIP属性);
                    item.setInt(item.getInt() + VIP属性);
                    item.setLuk(item.getLuk() + VIP属性);
                    item.setDex(item.getDex() + VIP属性);
                    item.setWatk(item.getWatk() + VIP属性);
                    item.setMatk(item.getMatk() + VIP属性);
                    item.setLocked(1);
					//item.setLevel(item.getLevel() + 1);
					//item.setUpgradeSlots(item.getUpgradeSlots() - 1);
                    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(VIPXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("强化成功，当前装备强化完毕。");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]成功为装备升级强化一次，恭喜他！");
                    cm.dispose();
                /*} else {
					var item = cm.getInventory(1).getItem(1).copy();
					item.setStr(item.getStr() - 0);
                    item.setInt(item.getInt() - 0);
                    item.setLuk(item.getLuk() - 0);
                    item.setDex(item.getDex() - 0);
                    item.setWatk(item.getWatk() - 0);
                    item.setMatk(item.getMatk() - 0);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(QHXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("很可惜，装备强化失败.");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]装备升级强化失败！");
                    cm.dispose();
                }*/
            }
		} else if (selection == 3) {
			var itemIDxx = cm.getInventory(1).getItem(1).getItemId();
            if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置第一格~");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置在物品栏第一格。");
                cm.dispose();
            } else if (cm.isCash(cm.getInventory(1).getItem(1).getItemId()) == true) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 属于#r点装类#k，无法放入。");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1).getExpiration() != -1) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 为#r限时#k装备，无法放入。");
                cm.dispose();
            }else if (cm.haveItem(QHXH, 1) == false) {
					cm.sendOk("#v"+QHXH+"#不足，请确认后再来。");
					cm.dispose();
			}else if (cm.getPlayer().getMeso() < 30000000) {
					cm.sendOk("金币不足。");
            } else if (cm.getInventory(1).getItem(1) != null) {
                if (几率 < 45) {
                    var item = cm.getInventory(1).getItem(1).copy();
                    item.setStr(item.getStr() + 5);
                    item.setInt(item.getInt() + 5);
                    item.setLuk(item.getLuk() + 5);
                    item.setDex(item.getDex() + 5);
                    item.setWatk(item.getWatk() + 5);
                    item.setMatk(item.getMatk() + 5);
                    item.setLocked(1);
					item.setLevel(item.getLevel() + 1);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
                    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(QHXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("强化成功，当前装备强化完毕。");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]成功为装备升级强化一次，恭喜他！");
                    cm.dispose();
                } else {
					var item = cm.getInventory(1).getItem(1).copy();
					item.setStr(item.getStr() - 0);
                    item.setInt(item.getInt() - 0);
                    item.setLuk(item.getLuk() - 0);
                    item.setDex(item.getDex() - 0);
                    item.setWatk(item.getWatk() - 0);
                    item.setMatk(item.getMatk() - 0);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(QHXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("很可惜，装备强化失败.");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]装备升级强化失败！");
                    cm.dispose();
                }
            }
		} else if (selection == 4) {
			var itemIDxx = cm.getInventory(1).getItem(1).getItemId();
            if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置第一格~");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1) == null) {
                cm.sendOk("请将需要强化的装备放置在物品栏第一格。");
                cm.dispose();
            } else if (cm.isCash(cm.getInventory(1).getItem(1).getItemId()) == true) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 属于#r点装类#k，无法放入。");
                cm.dispose();
            } else if (cm.getInventory(1).getItem(1).getExpiration() != -1) {
                cm.sendOk("当前装备 #b#t" + cm.getInventory(1).getItem(1).getItemId() + "##k 为#r限时#k装备，无法放入。");
                cm.dispose();
            }else if (cm.haveItem(QHXH, 1) == false) {
					cm.sendOk("#v"+QHXH+"#不足，请确认后再来。");
					cm.dispose();
			}else if (cm.getPlayer().getMeso() < 40000000) {
					cm.sendOk("金币不足。");
            } else if (cm.getInventory(1).getItem(1) != null) {
                if (几率 < 60) {
                    var item = cm.getInventory(1).getItem(1).copy();
                    item.setStr(item.getStr() + 5);
                    item.setInt(item.getInt() + 5);
                    item.setLuk(item.getLuk() + 5);
                    item.setDex(item.getDex() + 5);
                    item.setWatk(item.getWatk() + 5);
                    item.setMatk(item.getMatk() + 5);
                    item.setLocked(1);
					item.setLevel(item.getLevel() + 1);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
                    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(QHXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("强化成功，当前装备强化完毕。");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]成功为装备升级强化一次，恭喜他！");
                    cm.dispose();
                } else {
					var item = cm.getInventory(1).getItem(1).copy();
					item.setStr(item.getStr() - 0);
                    item.setInt(item.getInt() - 0);
                    item.setLuk(item.getLuk() - 0);
                    item.setDex(item.getDex() - 0);
                    item.setWatk(item.getWatk() - 0);
                    item.setMatk(item.getMatk() - 0);
					item.setUpgradeSlots(item.getUpgradeSlots() - 1);
					Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(QHXH, -1);
					cm.gainMeso(-20000000);
                    cm.sendOk("很可惜，装备强化失败.");
                    cm.喇叭(1, "玩家：[" + cm.getName() + "]装备升级强化失败！");
                    cm.dispose();
                }
            }

		} else if (selection == 5) {
            var selStr = "\r\n";
		        selStr += " 终极强化\r\n\r\n";
				selStr += "消耗#v"+VIPXH+"# + #v4001126# * 5000\r\n\r\n";
		        selStr += "随机强化背包第一件装备，增加10-30四维和物攻魔攻\r\n\r\n";
		        cm.sendSimple(selStr);
        }
    } else if (status == 2) {
        if (sel === 5) {
			if (!cm.haveItem(VIPXH, 1) || !cm.haveItem(4001126, 1)) {
				cm.sendOk("物品不足");
			    cm.dispose();
				return;
			} else if (cm.getInventory(1).getItem(1) != null) {
				var item = cm.getInventory(1).getItem(1).copy();
				item.setStr(item.getStr() + VIP属性);
				item.setInt(item.getInt() + VIP属性);
				item.setLuk(item.getLuk() + VIP属性);
				item.setDex(item.getDex() + VIP属性);
				item.setWatk(item.getWatk() + VIP属性);
				item.setMatk(item.getMatk() + VIP属性);
				item.setLocked(1);
				Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
				Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
				cm.gainItem(4001126, -1);
				cm.gainItem(VIPXH,-1);
				cm.sendOk("强化成功，当前装备强化完毕。");
			cm.喇叭(1,"玩家：["+cm.getName()+"]使用终极强化卷强化成功，随机获取10—30全属性，　　　　　　　　　　　　　　　　　　【无任何消耗，单件装备可无限强化】");
				cm.dispose();
			} else {
				cm.sendOk("背包第一格没有物品");
			    cm.dispose();
			}
		}
    }
}


