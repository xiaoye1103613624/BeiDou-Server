//load('nashorn:mozilla_compat.js');

var 装备最大升级次数 = 25;
var 强化几率 = 10; // 强化几率 * 10%（点券强化）
var 随机值 = Math.floor(Math.random() * 100); // 生成一个0-99的随机数
var status = 0;

// 指定允许强化的装备列表
var 允许强化的装备列表 = [
1003624, //上古
1052532,
1082490,
1072695,
1102948,
1132187,
1402332,
1432306,
1382267,
1452307,
1462245,
1472267,
1332281,
1482204,
1492237,
1098008,

1006032,  //传承
1053816,
1082491,
1073758,
1102947,
1132292,
1402333,
1432307,
1442248,
1382268,
1452308,
1462246,
1472268,
1332282,
1482205,
1492238,
1099014,

1022320, //史诗佩饰
1022321,
1012759,
1012760,
1122170,
1122171,
1032334,
1032335
]; // 示例装备ID，根据需要修改

function start() {
    if (cm.getInventory(1).getItem(1) == null) {
        cm.sendOk("请将装备放置在装备栏 “#b第一格#k” 。");
        cm.dispose();
    } else if (cm.isCash(cm.getInventory(1).getItem(1).getItemId())) {
        cm.sendOk("现金装备无法参与强化。");
        cm.dispose();
    } else if (cm.getInventory(1).getItem(1).getItemId() == 1122000) {
        cm.sendOk("#v1122000#装备不可以进行强化");
        cm.dispose();
    } else if (cm.getInventory(1).getItem(1).getItemId() == 1152206) {
        cm.sendOk("#i1152206#不可进行使用");
        cm.dispose();
    } else if (cm.getInventory(1).getItem(1).getItemId() == 1002140) {
        cm.sendOk("#i1002140# 不可进行使用");
        cm.dispose();
    } else if (cm.getInventory(1).getItem(1).getItemId() == 1003112) {
        cm.sendOk("#i1003112# 不能使用本功能");
        cm.dispose();
    } else {
        status = -1;
        action(1, 0, 0);
    }
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
            var ItemID = cm.getInventory(1).getItem(1).getItemId();
            var 可升级次数 = cm.getInventory(1).getItem(1).getUpgradeSlots();
            var 已升级次数 = cm.getInventory(1).getItem(1).getLevel();
            var textz = "#i" + ItemID + "#检测第一件装备可砸卷次数:#b" + 可升级次数 + "#k 已升级次数:#r" + 已升级次数 + "\r\n";
            textz += "#r1.这里只提升装备可升级次数，不改变属性\r\n";
            textz += "2.请将装备放到背包第1格，点券或抵用提升几率成功！\r\n";
			textz += "3.只支持#b上古法器套装、武器、盾牌、史诗佩饰#r使用此功能！\r\n";
            textz += "4.装备最多可强化#b" +装备最大升级次数+ "次#r（包括装备自带的可升级次数）\r\n";
			textz += "5.装备的金锤子一定要在" +装备最大升级次数+ "次之后再使用！#k\r\n";
    //        textz += "#k#L0#使用20000点券『装备可升级次数+1』（成功率" +强化几率+ "%）\r\n\r\n";
            textz += "#L1#使用40000抵用『装备可升级次数+1』（成功率" +强化几率+ "%）\r\n\r\n";
			textz += "#L3#使用 20亿金币『装备可升级次数+1』（成功率" +强化几率+ "%）\r\n\r\n";
            textz += "#L2#使用#z3605005#1个+#z2340000#188个（成功率100%）\r\n\r\n";
            cm.sendSimple(textz);
        } else if (status == 1) {
            var ItemID = cm.getInventory(1).getItem(1).getItemId();
            var 可升级次数 = cm.getInventory(1).getItem(1).getUpgradeSlots();
            var 已升级次数 = cm.getInventory(1).getItem(1).getLevel();
            if (允许强化的装备列表.indexOf(ItemID) === -1) {
                var 支持强化的装备列表字符串 = 允许强化的装备列表.map(function(id) {
                    return "#v" + id + "#";
                }).join(", ");
                cm.sendOk("#r当前装备不支持强化功能。\r\n#b只有以下装备才可以强化：\r\n" + 支持强化的装备列表字符串);
                cm.dispose();
                return;
            }
            if (selection == 0) {
                if (cm.getPlayer().getCSPoints(1) < 20000) {
                    cm.sendOk("点券不足");
                    cm.dispose();
                } else if ((装备最大升级次数 - 可升级次数 - 已升级次数) < 1) {
                    cm.sendOk("当前装备无法升级。");
                    cm.dispose();
                } else {
                    随机值 = Math.floor(Math.random() * 100); // 生成一个0-99的随机数
                    if (随机值 < 强化几率) {
                        var statup = new java.util.ArrayList();
                        var item = cm.getInventory(1).getItem(1).copy();
                        item.setUpgradeSlots(item.getUpgradeSlots() + 1);
                        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                        cm.gainNX(-10000);
                        cm.getPlayer().dropMessage(5, "强化成功！装备可升级次数+1");   //红字私聊提示
                    } else {
						cm.gainNX(-10000);
                        cm.getPlayer().dropMessage(5, "强化失败。点券：- 10000");   //红字私聊提示
                    }
                    cm.dispose();
					cm.openNpc(9000221);
                }
            } else if (selection == 1) {
                if (cm.getPlayer().getCSPoints(2) < 40000) {
                    cm.sendOk("抵用券不足");
                    cm.dispose();
                } else if ((装备最大升级次数 - 可升级次数 - 已升级次数) < 1) {
                    cm.sendOk("当前装备无法升级。");
                    cm.dispose();
                } else {
                    随机值 = Math.floor(Math.random() * 100); // 生成一个0-99的随机数
                    if (随机值 < 强化几率) {
                        var statup = new java.util.ArrayList();
                        var item = cm.getInventory(1).getItem(1).copy();
                        item.setUpgradeSlots(item.getUpgradeSlots() + 1);
                        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                        cm.gainDY(-40000);
                        cm.getPlayer().dropMessage(5, "强化成功！装备可升级次数+1");   //红字私聊提示
                    } else {
						cm.gainDY(-40000);
                        cm.getPlayer().dropMessage(5, "强化失败。抵用券：- 40000");   //红字私聊提示
                    }
                    cm.dispose();
					cm.openNpc(9000221);
                }
            } else if (selection == 2) {
                if (cm.haveItem(3605005, 1) == false) {
                    cm.sendOk("#v3605005#道具不足，无法使用");
                    cm.dispose();
                } else if (cm.haveItem(2340000, 188) == false) {
                    cm.sendOk("#v2340000# 道具不足 188 个，无法使用");
                    cm.dispose();
                } else if ((装备最大升级次数 - 可升级次数 - 已升级次数) < 1) {
                    cm.sendOk("当前装备无法升级已经 15次强化。");
                    cm.dispose();
                } else {
                    var statup = new java.util.ArrayList();
                    var item = cm.getInventory(1).getItem(1).copy();
                    item.setUpgradeSlots(item.getUpgradeSlots() + 1);
                    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
                    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
                    cm.gainItem(3605005, -1);
                    cm.gainItem(2340000, -188);
                    cm.getPlayer().dropMessage(5, "强化成功！装备可升级次数+1");   //红字私聊提示
                    cm.dispose();
					cm.openNpc(9000221);
                }
			} else if (selection == 3) {
    			if (!cm.haveItem(3994731, 20)) {
    			    cm.sendOk("道具不足，需要 20 个。");
    			    cm.dispose();
    			    return;
    			}
    			if ((装备最大升级次数 - 可升级次数 - 已升级次数) < 1) {
    			    cm.sendOk("当前装备无法升级。");
    			    cm.dispose();
    			    return;
    			}
    			随机值 = Math.floor(Math.random() * 100); // 0-99
    			if (随机值 < 强化几率) {
        			var statup = new java.util.ArrayList();
        			var item = cm.getInventory(1).getItem(1).copy();
        			item.setUpgradeSlots(item.getUpgradeSlots() + 1);
    			    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
    			    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), item, false);
    			    cm.gainItem(3994731, -20);
    			//    cm.sendOk("强化成功！装备可升级次数+1");
					cm.getPlayer().dropMessage(5, "强化成功！装备可升级次数+1");   //红字私聊提示
    			} else {
    			    cm.gainItem(3994731, -20);
    			//    cm.sendOk("强化失败！");
					cm.getPlayer().dropMessage(5, "强化失败。");   //红字私聊提示
    			}
    			cm.dispose();
				cm.openNpc(9000221);
			}

        }
    }
}