var status = 0;
var 红星星 = "#fItem/Etc/0427/04270001/Icon8/5#";
var 黄星星 = "#fItem/Etc/04270001/Icon9/0#";
var 分割线 = "#fMap/Back/zerek/拍卖/标题1#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 红方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/0#";
var 蓝方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/1#";
var 绿方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/2#";
var 黄方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/3#";

var 力量宝石代码 = [4440100, 4440200, 4440300, 4441000, 4441100, 4441200, 4441300, 4442000, 4442001];
var 敏捷宝石代码 = [4000000, 4000001, 4000002, 4000003, 4000004, 4000005, 4000006, 4000007, 4000008];
var 智力宝石代码 = [4000009, 4000010, 4000011, 4000012, 4000013, 4000014, 4000015, 4000016, 4000017];
var 运气宝石代码 = [4000018, 4000019, 4000020, 4000021, 4000022, 4000023, 4000024, 4000025, 4000026];

var 力量宝石提高属性 = [1, 2, 4, 8, 15, 30, 40, 60, 90];
var 敏捷宝石提高属性 = [1, 2, 4, 8, 15, 30, 40, 60, 90];
var 智力宝石提高属性 = [1, 2, 4, 8, 15, 30, 40, 60, 90];
var 运气宝石提高属性 = [1, 2, 4, 8, 15, 30, 40, 60, 90];
var 宝石提高攻击属性 = [1, 2, 4, 8, 10, 12, 15, 18, 25];
var 宝石提高魔攻属性 = [1, 2, 4, 8, 10, 12, 15, 18, 25];
var 宝石确定选择 = [1, 2, 3, 4, 5, 6, 7, 8, 9];

var 力量宝石1 = 0, 敏捷宝石1 = 0, 智力宝石1 = 0, 运气宝石1 = 0;
var nrwb = "";
var nrpd3 = 0;
var xz1, xz2, xz3;
var 宝石选择, 宝石属性, 本身属性;
var 名称 = ["力量宝石槽", "敏捷宝石槽", "智力宝石槽", "运气宝石槽"];

function start() {
    status = -1;
    action(1, 0, 0);
}

/* ========== 工具：编解码（零源码方案） ========== */
function packGem(str, dex, _int, luk) {
    return (str % 10) * 1e6 + (dex % 10) * 1e5 + (_int % 10) * 1e4 + (luk % 10) * 1e3;
}
function unpackGem(exp) {
    var str = Math.floor(exp / 1e6) % 10;
    var dex = Math.floor(exp / 1e5) % 10;
    var _int = Math.floor(exp / 1e4) % 10;
    var luk = Math.floor(exp / 1e3) % 10;
    return [str, dex, _int, luk];
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1) status++;
        else status--;

        if (status == 0) {
            var text = "";
            text = "        #v4440100##e宝石拆卸 - #n[打造顶级装备之路]";
            text += "" + 分割线 + "\r\n";
            text += "#b#L1#" + 蓝色箭头 + " 选择装备#l\r\n";
            cm.sendSimple(text);

        } else if (status == 1) {
            var text = "";
            text += "#k#e- 请选择你要拆卸宝石的装备-#n\r\n\r\n#b";
            for (var i = 0; i < 96; i++) {
                var it = cm.getInventory(1).getItem(i);
                if (it == null) continue;
                if (!Packages.server.MapleItemInformationProvider.getInstance().isEquip(it.getItemId())) continue;
                var gem = unpackGem(it.getItemEXP());
                var 力量宝石 = gem[0], 敏捷宝石 = gem[1], 智力宝石 = gem[2], 运气宝石 = gem[3];
                nrwb = "[力:" + 力量宝石 + "敏:" + 敏捷宝石 + "智:" + 智力宝石 + "运:" + 运气宝石 + "]";
                if (Packages.server.MapleItemInformationProvider.getInstance().isCash(it.getItemId()) != true && it.getExpiration() == -1) {
                    text += "#L" + i + "##k第#b" + i + "#k格:#v" + it.getItemId() + "##b#z" + it.getItemId() + "#  #r" + nrwb + "#l\r\n";
                    nrpd3++;
                }
            }
            if (nrpd3 < 1) {
                text += "#r很抱歉你貌似没有什么东西可以强化";
                cm.sendOk(text);
                cm.dispose();
                return;
            } else {
                cm.sendSimple(text);
            }

        } else if (status == 2) {
            xz1 = selection;
            var it = cm.getInventory(1).getItem(xz1);
            if (it == null || !Packages.server.MapleItemInformationProvider.getInstance().isEquip(it.getItemId())) {
                cm.sendOk("请选择一件有效装备。");
                cm.dispose();
                return;
            }
            var gem = unpackGem(it.getItemEXP());
            力量宝石1 = gem[0];
            敏捷宝石1 = gem[1];
            智力宝石1 = gem[2];
            运气宝石1 = gem[3];
            nrwb = "[力:" + 力量宝石1 + "敏:" + 敏捷宝石1 + "智:" + 智力宝石1 + "运:" + 运气宝石1 + "]";

            var text = "";
            text += "#k#e选择:[第#r" + xz1 + "#k格][#v" + it.getItemId() + "##t" + it.getItemId() + "#]#n\r\n\r\n#b";
            text += "     #k当前力量宝石等级:#r" + 力量宝石1 + "\t";
            text += "     #k当前敏捷宝石等级:#r" + 敏捷宝石1 + "\r\n";
            text += "     #k当前智力宝石等级:#r" + 智力宝石1 + "\t";
            text += "     #k当前运气宝石等级:#r" + 运气宝石1 + "\r\n";
            text += "   #b#L0#" + 红方 + " 拆卸力量宝石#l\t";
            text += "   #b#L1#" + 绿方 + " 拆卸敏捷宝石#l\r\n";
            text += "   #b#L2#" + 蓝方 + " 拆卸智力宝石#l\t";
            text += "   #b#L3#" + 黄方 + " 拆卸运气宝石#l\r\n";
            cm.sendSimple(text);

        } else if (status == 3) {
            xz2 = selection;
            var text = "";
            switch (xz2) {
                case 0:
                    if (力量宝石1 < 1) {
                        cm.sendOk("当前装备没上过力量宝石，无法拆卸。");
                        cm.dispose();
                        return;
                    }
                    宝石选择 = 力量宝石代码;
                    宝石属性 = 力量宝石提高属性;
                    本身属性 = 力量宝石1;
                    break;
                case 1:
                    if (敏捷宝石1 < 1) {
                        cm.sendOk("当前装备没上过敏捷宝石，无法拆卸。");
                        cm.dispose();
                        return;
                    }
                    宝石选择 = 敏捷宝石代码;
                    宝石属性 = 敏捷宝石提高属性;
                    本身属性 = 敏捷宝石1;
                    break;
                case 2:
                    if (智力宝石1 < 1) {
                        cm.sendOk("当前装备没上过智力宝石，无法拆卸。");
                        cm.dispose();
                        return;
                    }
                    宝石选择 = 智力宝石代码;
                    宝石属性 = 智力宝石提高属性;
                    本身属性 = 智力宝石1;
                    break;
                case 3:
                    if (运气宝石1 < 1) {
                        cm.sendOk("当前装备没上过运气宝石，无法拆卸。");
                        cm.dispose();
                        return;
                    }
                    宝石选择 = 运气宝石代码;
                    宝石属性 = 运气宝石提高属性;
                    本身属性 = 运气宝石1;
                    break;
            }
            text += "#k#e选择:[第#r" + xz1 + "#k格][#v" + cm.getInventory(1).getItem(xz1).getItemId() + "##t" + cm.getInventory(1).getItem(xz1).getItemId() + "#]#n\r\n\r\n#b";
            text += "当前选择拆卸宝石槽为" + 名称[xz2] + ", 扣除属性-" + 宝石属性[(本身属性 - 1)] + "\r\n\r\n";
            text += "#r请问你是否要进行拆卸";
            cm.sendYesNo(text);

        } else if (status == 4) {
            var it = cm.getInventory(1).getItem(xz1);
            var ItemCopy = it.copy();
            var gem = unpackGem(it.getItemEXP());
            var newStr = gem[0], newDex = gem[1], newInt = gem[2], newLuk = gem[3];
            switch (xz2) {
                case 0:
                    newStr = 0;
                    ItemCopy.setStr(ItemCopy.getStr() - 宝石属性[(本身属性 - 1)]);
                    ItemCopy.setWatk(ItemCopy.getWatk() - 宝石提高攻击属性[(本身属性 - 1)]);
                    ItemCopy.setMatk(ItemCopy.getMatk() - 宝石提高魔攻属性[(本身属性 - 1)]);
                    break;
                case 1:
                    newDex = 0;
                    ItemCopy.setDex(ItemCopy.getDex() - 宝石属性[(本身属性 - 1)]);
                    ItemCopy.setWatk(ItemCopy.getWatk() - 宝石提高攻击属性[(本身属性 - 1)]);
                    ItemCopy.setMatk(ItemCopy.getMatk() - 宝石提高魔攻属性[(本身属性 - 1)]);
                    break;
                case 2:
                    newInt = 0;
                    ItemCopy.setInt(ItemCopy.getInt() - 宝石属性[(本身属性 - 1)]);
                    ItemCopy.setWatk(ItemCopy.getWatk() - 宝石提高攻击属性[(本身属性 - 1)]);
                    ItemCopy.setMatk(ItemCopy.getMatk() - 宝石提高魔攻属性[(本身属性 - 1)]);
                    break;
                case 3:
                    newLuk = 0;
                    ItemCopy.setLuk(ItemCopy.getLuk() - 宝石属性[(本身属性 - 1)]);
                    ItemCopy.setWatk(ItemCopy.getWatk() - 宝石提高攻击属性[(本身属性 - 1)]);
                    ItemCopy.setMatk(ItemCopy.getMatk() - 宝石提高魔攻属性[(本身属性 - 1)]);
                    break;
            }
            // 写回新的宝石等级
            ItemCopy.setItemEXP(packGem(newStr, newDex, newInt, newLuk));
            ItemCopy.setOwner(ItemCopy.getOwner());
            Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, xz1, 1, false);
            Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), ItemCopy, false);
            // 返还宝石
            cm.gainItem(宝石选择[(本身属性 - 1)], 1);
            cm.sendSimple("拆卸成功！已返还对应宝石。");
            cm.dispose();
            return;
        }
    }
}