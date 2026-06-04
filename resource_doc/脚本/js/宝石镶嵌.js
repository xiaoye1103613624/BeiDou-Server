var status = 0;
var selAmount = 0;   // 记录批量合成的数量
var opMode = 0;   // 1=镶嵌  2=拆卸
var selSlot = -1; // 装备格子
var selLv  = 0;   // 选中的宝石等级
var 红星星 ="#fItem/Etc/0427/04270001/Icon8/5#";
var 黄星星 ="#fItem/Etc/0427/04270001/Icon9/0#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 红方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/0#";
var 蓝方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/1#";
var 绿方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/2#";
var 黄方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/3#";
var 紫方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/4#";
var 橙方 = "#fUI/UIWindow.img/AriantMatch/characterIcon/5#";
var 分割线 = "#fMap/Back/zerek/拍卖/标题1#";
var 提示 = "#fUI/CN_Chat/ChattingRoom/BtVolUp/0/normal/0#";
var 黄条上 = "#fUI/ChatBalloon.img/pet/25/head#";
var 黄条下 = "#fUI/ChatBalloon.img/pet/25/s#";
var 黄条下左 = "#fUI/ChatBalloon.img/pet/25/sw#";
var 黄条下右 = "#fUI/ChatBalloon.img/pet/25/se#";
var 黄条左 = "#fUI/ChatBalloon.img/pet/25/nw#";
var 黄条右 = "#fUI/ChatBalloon.img/pet/25/ne#";
var 五子棋 = "#fUI/ChatBalloon.img/miniroom/Omok#";
var 斜金币 = "#fUI/ChatBalloon.img/miniroom/PersonalShop#";
var 熊猫 = "#fUI/ChatBalloon.img/pet/1/nw#";
var 毛球 = "#fUI/ChatBalloon.img/pet/12/nw#";
var 金冠 = "#fUI/UIWindow.img/UserInfo/bossPetCrown#";
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";
var 窗口名称="宝石 合成系统";

/* ========  可自由配置区  ======== */
var 宝石等级代码 = [4322899, 4322898, 4322897, 	4322885, 4322884, 4322883, 		4322869, 4322868, 4322867, 		4322853, 4322852, 4322851, 		4322831, 4322836, 4322837];
var 禁止列表 = [1000000, 1000001];   // 禁止镶嵌的时装 ID
var 全属性数组 = [10, 15, 25, 40, 70, 100, 170, 270, 440, 700, 1111, 1780, 2850, 4560, 7300];
var 攻魔攻数组 = [10, 15, 25, 40, 70, 100, 170, 270, 440, 700, 1111, 1780, 2850, 4560, 7300];

/* ================================= */
// 左侧补空格
function padLeft(str, len) {
    str = String(str);
    while (str.length < len) str = ' ' + str;
    return str;
}

// 右侧补空格
function padRight(str, len) {
    str = String(str);
    while (str.length < len) str = str + ' ';
    return str;
}

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
        return;
    }
    if (mode == 0) {
        cm.dispose();
        return;
    }
    if (mode == 1) status++;
    else status--;
	
    if (cm.getInventory(1).isFull(2) || cm.getInventory(2).isFull(2) || cm.getInventory(3).isFull(2) || cm.getInventory(4).isFull(2) || cm.getInventory(5).isFull(2)) {
        cm.sendOk("请保证背包所有栏位至少保留3个空格！");
        cm.dispose();
        return;
    }
    if (status == 0) {
		var text = "           #v4322884##e宝石系统 - #n[打造顶级宝石]";
        text += 分割线 + "\r\n";
		text += "   " + 提示 + " #e#k<远古至尊宝石>说明#n\r\n";
		text += "   " + 提示 + " #k1、宝石合成 - 所有宝石全部为二合一，免费合成\r\n";
		text += "   " + 提示 + " #k2、宝石镶嵌 - 镶嵌宝石费用为#r宝石等级×100元宝#k\r\n";
		text += "   " + 提示 + " #k3、宝石拆卸 - 拆卸宝石费用为#r宝石等级×10元宝#k\r\n";
        text += 分割线 + "\r\n";
		text += "   " + 提示 + " 一经镶嵌#e#b全属性与双攻瞬间暴涨#k#n,战力飞跃肉眼可见\r\n";
		text += "   " + 提示 + " #r警告：宝石力量过于狂暴，请谨慎选择镶嵌时机！\r\n";
        text += 分割线 + "\r\n";
		text += "#b#L3#" + 正方箭头 + " 宝石合成#l";   // ← 新增
        text += "   #b#L1#" + 正方箭头 + " 镶嵌宝石#l";
        text += "   #b#L2#" + 正方箭头 + " 拆卸宝石#l\r\n";
        cm.sendSimple(text);

    /* ===================== 选 模 式 ===================== */
    } else if (status == 1) {
        opMode = selection;
        if (opMode == 1) {
            /* ===== 镶嵌：选装备 ===== */
            var inv = cm.getInventory(1);
            var text = "#e请选择要镶嵌的时装：#n\r\n\r\n";
            var count = 0;
            for (var i = 0; i < 96; i++) {
                var it = inv.getItem(i);
                if (it == null) continue;
                var ii = Packages.server.MapleItemInformationProvider.getInstance();
                if (!ii.isCash(it.getItemId())) continue;
                if (禁止列表.indexOf(it.getItemId()) >= 0) continue;
                if (it.getLevel() > 0) continue;
                var upg = it.getUpgradeSlots();
                text += "#L" + i + "#第 " + i + " 格：#v" + it.getItemId() + "# #t" + it.getItemId() + "#  (#g可镶嵌#k)\r\n";
                count++;
            }
            if (count == 0) {
                cm.sendOk("没有可以镶嵌的时装（需未镶嵌且≤最高等级）。");
                cm.dispose();
                return;
            }
            cm.sendSimple(text);

        } else if (opMode == 2) {
            /* ===== 拆卸：选装备 ===== */
            var inv = cm.getInventory(1);
            var text = "#e请选择要拆卸宝石的时装：#n\r\n\r\n";
            var count = 0;
            for (var i = 0; i < 96; i++) {
                var it = inv.getItem(i);
                if (it == null) continue;
                var ii = Packages.server.MapleItemInformationProvider.getInstance();
                if (!ii.isCash(it.getItemId())) continue;
                if (禁止列表.indexOf(it.getItemId()) >= 0) continue;
                if (it.getLevel() == 0) continue;
                text += "#L" + i + "#第 " + i + " 格：#v" + it.getItemId() + "# #t" + it.getItemId() + "# #r(已镶嵌 " + it.getLevel() + " 级宝石)#k#l\r\n";
                count++;
            }
            if (count == 0) {
                cm.sendOk("没有已镶嵌宝石的时装。");
                cm.dispose();
                return;
            }
            cm.sendSimple(text);

        } else if (opMode == 3) {
            /* ===== 宝石合成 ===== */
			var text = "\t#r#e   	     "+ 红星 + ""+ 大红星 + ""+ 红点 + "" + cm.开服名称() + ""+ 红蓝点 + ""+ 蓝点 + ""+ 大蓝星 + ""+ 蓝星 + "#k \r\n";
			text += ""+ 黄条左 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 金冠 + "#b#e#r"+窗口名称+"#b#n"+ 金冠 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条上 + ""+ 黄条右 + "#k  \r\n";
			text += "" + 提示 + ":所有宝石免费合成\r\n";
            text += "" + 提示 + ":请选择要合成的宝石等级\r\n";
			for (var a = 0; a < 宝石等级代码.length - 1; a++) {
    			var gemID  = 宝石等级代码[a];
    			var nextID = 宝石等级代码[a + 1];
    			var has    = cm.getPlayer().getItemQuantity(gemID, false);

    			var line = formatGemLine(a + 1, a + 2, has);
    			text += "   #L" + a + "#" + 正方箭头 + " " + line + "#l\r\n\r\n";
			}
			text += "\r\n"+ 黄条下左 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下 + ""+ 黄条下右 + "#k  ";
            cm.sendSimple(text);
        }

    /* ===================== 镶 嵌 选 宝 石 ===================== */
    } else if (status == 2 && opMode == 1) {
        selSlot = selection;
        var it = cm.getInventory(1).getItem(selSlot);
        if (it == null || it.getLevel() > 0) {
            cm.sendOk("装备异常或已镶嵌。");
            cm.dispose();
            return;
        }

        var text = "#k#e- 你想镶嵌几级宝石？（背包中需拥有）-#n\r\n\r\n#b";
        text += "" + 分割线 + "\r\n";
        for (var a = 0; a < 宝石等级代码.length; a++) {
            var gemID = 宝石等级代码[a];
            var qty = cm.getPlayer().getItemQuantity(gemID, false);
            text += "   #L" + a + "##v" + gemID + "# " + (a + 1) + " 级宝石  (拥有 #r" + qty + " #b颗)#l\r\n";
            if ((a + 1) % 2 == 0) text += "";
            else text += "";
        }
        text += "\r\n\r\n" + 分割线 + "\r\n";
        cm.sendSimple(text);

    /* ===================== 镶 嵌 确 认 ===================== */
    } else if (status == 3 && opMode == 1) {
        selLv = selection + 1; // 1-9
        var gemID = 宝石等级代码[selLv - 1];
        var addStat = 全属性数组[selLv - 1];
        var addAtk = 攻魔攻数组[selLv - 1];

        /* 立即判断背包里有没有 */
        if (cm.getPlayer().getItemQuantity(gemID, false) < 1) {
            cm.sendOk("宝石不足！你需要 #v" + gemID + "# ×1 才能镶嵌 " + selLv + " 级宝石。");
            cm.dispose();
            return;
        }

        var text = "#d本次镶嵌信息：\r\n";
        text += "#d消耗宝石：#b#v" + gemID + "# #z" + gemID + "# ×1\r\n";
        text += "#d获得属性：#r全属性 + #r" + addStat + "#d  /  攻/魔攻 + #r" + addAtk + "#k\r\n";
        text += "#d镶嵌后装备将变为：#t" + cm.getInventory(1).getItem(selSlot).getItemId() + "##g(+" + selLv + ")\r\n\r\n";
        text += "#b是否立即镶嵌？";
        cm.sendYesNo(text);

    /* ===================== 镶 嵌 执 行 ===================== */
    } else if (status == 4 && opMode == 1) {
        var it = cm.getInventory(1).getItem(selSlot);
        if (it == null || it.getLevel() > 0) {
            cm.sendOk("装备异常或已镶嵌。");
            cm.dispose();
            return;
        }
		/* 时装融合检测：要求融合过 2 次（getHands() == 2） */
		if (it.getHands() != 2) {
			cm.sendOk("该装备没有进行时装融合，无法进行操作。\r\n请在匠人街#r时装融合#k处融合两次才可镶嵌！");
			cm.dispose();
			return;
		}
		
        var gemID = 宝石等级代码[selLv - 1];
        var addStat = 全属性数组[selLv - 1];
        var addAtk = 攻魔攻数组[selLv - 1];

        /* 扣宝石 */
        cm.gainItem(gemID, -1);

        /* 复制装备并加属性 */
        var copy = it.copy();
        copy.setStr(copy.getStr() + addStat);
        copy.setDex(copy.getDex() + addStat);
        copy.setInt(copy.getInt() + addStat);
        copy.setLuk(copy.getLuk() + addStat);
        copy.setWatk(copy.getWatk() + addAtk);
        copy.setMatk(copy.getMatk() + addAtk);

        /* 装备名称后加 (+等级) */
        var baseName = Packages.server.MapleItemInformationProvider.getInstance().getName(it.getItemId());

        /* 用装备等级记录宝石等级 */
        copy.setLevel(selLv);

        /* 覆盖原装备 */
        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, selSlot, 1, false);
        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), copy, false);

        cm.playerMessage(5, "当前装备：宝石等级为: " + selLv + " 级");
        cm.sendOk("镶嵌成功！\r\n#r全属性 + " + addStat + "  /  攻/魔攻 + " + addAtk + "\r\n#k当前装备，宝石等级为：#b" + baseName + "(" + selLv + "级)#k");
		cm.喇叭(1, "玩家:[" + cm.getPlayer().getName() + "] 在 " + baseName + " 成功镶嵌了 " + selLv + "级宝石，全属性大量提升 ！");
        cm.dispose();

    /* ===================== 拆 卸 确 认 ===================== */
    } else if (status == 2 && opMode == 2) {
        selSlot = selection;
        var it = cm.getInventory(1).getItem(selSlot);
        if (it == null || it.getLevel() == 0) {
            cm.sendOk("装备异常或未镶嵌。");
            cm.dispose();
            return;
        }
        selLv = it.getLevel();
        var gemID = 宝石等级代码[selLv - 1];
        var text = "#e拆卸信息：\r\n";
        text += "返还宝石：#v" + gemID + "# #z" + gemID + "# ×1\r\n";
        text += "装备等级将清零，属性随之扣除\r\n\r\n";
        text += "#r是否立即拆卸？";
        cm.sendYesNo(text);

    } else if (status == 2 && opMode == 3) {
        selLv = selection;   // 0~13 对应 1~14 级
        var gemID = 宝石等级代码[selLv];
        var has   = cm.getPlayer().getItemQuantity(gemID, false);
        var text  = "#e你选择了：#v" + gemID + "# " + (selLv + 1) + " 级宝石#n\r\n";
        text += "当前拥有：#r" + has + "#k 颗\r\n";
        text += "每次合成消耗 2 颗，最多可合成：#r" + Math.floor(has / 2) + "#k 颗\r\n\r\n";
        text += "请输入想合成的数量：";
        cm.sendGetNumber(text, 1, 1, Math.floor(has / 2));

    /* ===================== 拆 卸 执 行 ===================== */
    } else if (status == 3 && opMode == 2) {
        var it = cm.getInventory(1).getItem(selSlot);
        if (it == null || it.getLevel() == 0) {
            cm.sendOk("装备异常或未镶嵌。");
            cm.dispose();
            return;
        }
        var lv = it.getLevel();
        var gemID = 宝石等级代码[lv - 1];
        var subStat = 全属性数组[lv - 1];
        var subAtk = 攻魔攻数组[lv - 1];

        /* 返还宝石 */
        cm.gainItem(gemID, 1);

        /* 复制装备并扣属性 */
        var copy = it.copy();
        copy.setStr(copy.getStr() - subStat);
        copy.setDex(copy.getDex() - subStat);
        copy.setInt(copy.getInt() - subStat);
        copy.setLuk(copy.getLuk() - subStat);
        copy.setWatk(copy.getWatk() - subAtk);
        copy.setMatk(copy.getMatk() - subAtk);

        /* 等级清零 & 名称恢复 */
        copy.setLevel(0);
        var baseName = Packages.server.MapleItemInformationProvider.getInstance().getName(it.getItemId());
    //    copy.setOwner(baseName);

        /* 覆盖原装备 */
        Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, selSlot, 1, false);
        Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), copy, false);

        cm.playerMessage(5, "当前装备：宝石等级为: 0 级");
        cm.sendOk("拆卸成功！\r\n已返还 #v" + gemID + "# ×1\r\n属性已扣除");
		cm.喇叭(3, "玩家:[" + cm.getPlayer().getName() + "] 把 " + baseName + " 装备上的" + selLv + "级宝石拆了下来！");
        cm.dispose();
		
    } else if (status == 3 && opMode == 3) {
        var amount = selection;   // 玩家输入的合成个数
        if (amount <= 0) {
            cm.sendOk("合成数量必须 ≥1 ！");
            cm.dispose();
            return;
        }
        selAmount = amount;       // 保存到全局，下一步还要用
        var gemID  = 宝石等级代码[selLv];
        var nextID = 宝石等级代码[selLv + 1];
        var need   = 2 * amount;

        var text = "#d批量合成信息：\r\n";
        text += "消耗：#v" + gemID + "##b#t" + gemID + "# #d×" + need + "\r\n";
        text += "获得：#v" + nextID + "##b#t" + nextID + "# #d×" + amount + "\r\n\r\n";
        text += "#r是否立即合成？";
        cm.sendYesNo(text);
		
    } else if (status == 4 && opMode == 3) {
        var amount = selAmount;
        var gemID  = 宝石等级代码[selLv];
        var nextID = 宝石等级代码[selLv + 1];
        var need   = 2 * amount;

        if (cm.getPlayer().getItemQuantity(gemID, false) < need) {
            cm.sendOk("宝石数量不足，合成失败。");
            cm.dispose();
            return;
        }

        /* 扣除材料 & 返还成品 */
        cm.gainItem(gemID,  -need);
        cm.gainItem(nextID, amount);

        /* 系统提示 + 喇叭 */
        cm.喇叭(1, "玩家:[" + cm.getPlayer().getName() + "] 一次性合成出 " + amount + " 颗 " + (selLv + 2) + " 级宝石！");
		Packages.tools.FileoutputUtil.log("log\\玩家相关\\宝石合成.log", "[" + cm.getName() + "] 一次性合成出 " + amount + " 颗 " + (selLv + 2) + " 级宝石！ ");  //  记录日志
        cm.sendOk("批量合成成功！\r\n\r\n你获得了：#v" + nextID + "##r#t" + nextID + "##k ×#r" + amount);
        cm.dispose();
    }
}

// 把数字补成 2 位宽度（半角空格）
function pad2(str) {
    str = String(str);
    if (str.length === 1) return ' ' + str;
    return str;
}
function formatGemLine(fromLvl, toLvl, count) {
    var arrow = '合成';
    var fromTxt = pad2(fromLvl) + '级宝石 ' + arrow;   // ' 1级 →' 与 '10级 →' 同宽
    var toTxt   = pad2(toLvl)   + '级宝石';
    var countTxt = '(拥有 ' + padLeft(count, 5) + ' 颗)';
    return '#k' + fromTxt + ' #r' + toTxt + '#b ' + countTxt;
}