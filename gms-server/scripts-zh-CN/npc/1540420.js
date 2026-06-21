var a = 0;
var text;
var selects;
var buynum = 0;
// 定义特殊装备列表（只能购买一个的装备）
var specialItems = [1112020, 1112021, 1112019];
var itemlist = [
    //道具代码，道具数量，单价，道具说明
//	[2022503, 1, 388, "购买后永久无限使用"],
//	[1116049, 1, 888, "购买后永久不限时使用"],
	[2022428, 1, 1, "高级仓库箱子（使用条件：需月卡）"],
	[2550007, 1, 38, "随机抽全属性：10-200 时装皮肤"],
	[1112020, 1, 88, "装备后额外伤害增加10%"],
	[1112021, 1, 138, "装备后额外伤害增加15%"],
	[1112019, 1, 188, "装备后额外伤害增加20%"],
	[2022504, 1, 188, "购买后永久无限使用"],
	[2048403, 1, 10, "制作神器装备必须品"],
//	[3604010, 1, 8, "身外化身伤害强化物品"],
	[2049306, 1, 3, "强化神器装备必须品"],
	[2614013, 1, 1, "突破伤害上限的神秘石头"],
	[4310100, 1, 1, "回收后可获得一万积分"],
	[2022509, 10, 1, "使用后1：1获得元宝"],
	[3605006, 200, 1, "非常重要的游戏道具"],
	[1007000, 1, 10, "无属性，玩具时装头盔"],
	[1007001, 1, 10, "无属性，玩具时装头盔"],
	[1007002, 1, 10, "无属性，玩具时装头盔"],
	[1007003, 1, 10, "无属性，玩具时装头盔"],
	[1703300, 1, 10, "无属性，玩具时装武器-单手剑"],
];

function start() {
    a = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (mode == 1)
            a++;
        else
            a--;
        if (a == -1) {
            cm.dispose();
        } else if (a == 0) {
            text = "#w  尊敬的#r#h0##k,您好！在这里可以使用“#b 赞助 #k”兑换\r\n你想要的物品。\r\n";
            text += "  - 您当前剩余的赞助为：#r#e " + cm.getPlayer().getmoney() + " #n #k点\r\n";
            text += "  - #r注：#k此赞助商店，购买后无累计赠送\r\n";
            text += "  - #r注：#k此赞助商店，购买后无累计赠送\r\n";
            text += "  - #r注：#k此赞助商店，购买后无累计赠送\r\n";
            for (var i = 0; i < itemlist.length; i++) {
                text += "#L" + i + "##e#d购买道具：[#b#v" + itemlist[i][0] + ":##z" + itemlist[i][0] + "##d]#l\r\n\r\n#d   - 数量：#r" + itemlist[i][1] + "#d个  购买价格：#r" + itemlist[i][2] + " #d赞助\r\n   - [#b道具说明：#r" + itemlist[i][3] + "#k]\r\n\r\n";
            }
            cm.sendSimple(text);
        } else if (a == 1) {
            selects = selection;
            var itemId = itemlist[selects][0];
            
            // 检查是否为特殊装备
            if (specialItems.indexOf(itemId) !== -1) {
                // 特殊装备：直接购买一个，跳过数量输入
                var itemid = itemlist[selects][0];
                if (cm.canHold(itemlist[selects][0], itemlist[selects][1]) == false) {
                    cm.sendOk("您的背包空间不足，请整理后再兑换。");
                    cm.dispose();
                    return;
                }
                if (cm.getPlayer().getmoney() < itemlist[selects][2]) {
                    cm.sendOk("#d所需赞助不足，购买道具需要赞助[#r" + cm.getPlayer().getmoney() + "#d/#b" + itemlist[selects][2] + "#d]");
                    cm.dispose();
                    return;
                }
                cm.getPlayer().setmoney(cm.getPlayer().getmoney() - itemlist[selects][2]);
                cm.gainItem(itemlist[selects][0], itemlist[selects][1]);
                cm.sendOk("恭喜你花费了#r " + itemlist[selects][2] + " #k赞助购买了#v" + itemlist[selects][0] + "# * #r1#k份。");
                var itemName = cm.getItemName(itemlist[selects][0]);
				var jg = 1; // ? 明确设置购买数量为1
                cm.喇叭(2, "[赞助商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在赞助商店购买了 [" + itemName + "*1份]！ ");
                cm.喇叭(2, "[赞助商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在赞助商店购买了 [" + itemName + "*1份]！ ");
                cm.喇叭(2, "[赞助商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在赞助商店购买了 [" + itemName + "*1份]！ ");
				Packages.tools.FileoutputUtil.log("log\\玩家相关\\赞助商店.log", "[" + cm.getName() + "]在赞助商店花费了" + itemlist[selects][2] * jg + " 赞助，购买了【" + itemName + " * " + jg + "份】。");  //  记录日志
                cm.dispose();
            } else {
                // 普通装备：显示数量输入界面
                var txt = " - 当前兑换道具：#r#i" + itemlist[selects][0] + "##t" + itemlist[selects][0] + "##d * " + itemlist[selects][1] + "\r\n\r\n"
                txt += " #k- 当前道具说明：#d" + itemlist[selects][3] + "#k\r\n\r\n"
                txt += " - 当前道具单价：#r" + itemlist[selects][2] + "#k 赞助。\r\n\r\n"
                txt += " - 请输入所需兑换数值：\r\n\r\n"
                cm.sendGetNumber(txt, 1, 1, 100);
            }
        } else if (a == 2) {
            // 普通装备的购买逻辑（特殊装备已在a==1时处理）
            jg = selection
            var itemid = itemlist[selects][0];
            if (cm.canHold(itemlist[selects][0], itemlist[selects][1] * jg) == false) {
                cm.sendOk("您的背包空间不足，请整理后再兑换。");
                cm.dispose();
                return;
            }
            if (cm.getPlayer().getmoney() < itemlist[selects][2] * jg) {
                cm.sendOk("#d所需赞助不足，购买道具需要赞助[#r" + cm.getPlayer().getmoney() + "#d/#b" + itemlist[selects][2] * jg + "#d]");
                cm.dispose();
                return;
            }
            cm.getPlayer().setmoney(cm.getPlayer().getmoney() - itemlist[selects][2] * jg);
            cm.gainItem(itemlist[selects][0], itemlist[selects][1] * jg);
            cm.sendOk("恭喜你花费了#r " + itemlist[selects][2] * jg + " #k赞助购买了#v" + itemlist[selects][0] + "# * #r" + jg + "#k份。");
            var itemName = cm.getItemName(itemlist[selects][0]);
            cm.喇叭(2, "[赞助商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在赞助商店购买了 [" + itemName + "*" + jg + "份]！ ");
            cm.喇叭(2, "[赞助商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在赞助商店购买了 [" + itemName + "*" + jg + "份]！ ");
            cm.喇叭(2, "[赞助商店]" + " : " + "土豪[" + cm.getPlayer().getName() + "] 在赞助商店购买了 [" + itemName + "*" + jg + "份]！ ");
			Packages.tools.FileoutputUtil.log("log\\玩家相关\\赞助商店.log", "[" + cm.getName() + "]在赞助商店花费了" + itemlist[selects][2] * jg + " 赞助，购买了【" + itemName + " * " + jg + "份】。");  //  记录日志
            cm.dispose();
        }
    }
}
