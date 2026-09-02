// 炼金术魔法书 · 天赋系统 NPC 9031014
var status = -1;
var TalentId = Java.type('org.gms.talent.TalentId');
var TalentTier = Java.type('org.gms.talent.TalentTier');
var TalentService = Java.type('org.gms.talent.TalentService');
var TalentConfig = Java.type('org.gms.talent.TalentConfig');

var modeSel = 0; // 1学 2兑 3买
var tierSel = 0; // 1~4
var bookList = [];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || (mode === 0 && status <= 0)) {
        cm.dispose();
        return;
    }
    if (mode === 0) {
        status--;
    } else {
        status++;
    }

    if (status === 0) {
        var st = cm.talentTierStatus();
        cm.sendSimple(
            "#e炼金术魔法书#n\r\n" + st +
            "\r\n\r\n请选择功能：\r\n" +
            "#L1#天赋学习#l\r\n" +
            "#L2#天赋兑换#l\r\n" +
            "#L3#天赋购买#l\r\n" +
            "#L4#查看我的天赋#l\r\n" +
            "#L5##b逆袭银币兑换#k（100万经验=1个）#l"
        );
    } else if (status === 1) {
        modeSel = selection;
        if (modeSel === 4) {
            cm.sendOk(buildOwnedText());
            cm.dispose();
            return;
        }
        if (modeSel === 5) {
            cm.dispose();
            cm.openNpc(9031014, "xy/匠人街/逆袭银币兑换");
            return;
        }
        if (modeSel < 1 || modeSel > 3) {
            cm.dispose();
            return;
        }
        cm.sendSimple(
            "选择天赋阶位：\r\n" +
            "#L1#初级#l\r\n" +
            "#L2#中级#l\r\n" +
            "#L3#高级#l\r\n" +
            "#L4#终极#l"
        );
    } else if (status === 2) {
        tierSel = selection;
        if (tierSel < 1 || tierSel > 4) {
            cm.dispose();
            return;
        }
        if (modeSel === 1 && !cm.isTalentTierUnlocked(tierSel)) {
            cm.sendOk("该阶位尚未解锁。\r\n" + cm.talentTierStatus());
            cm.dispose();
            return;
        }
        bookList = listTier(tierSel);
        var tip = "";
        if (modeSel === 2) {
            if (tierSel === 4) {
                tip = "终极兑换：材料 #r10#k 个 → 1 本\r\n";
            } else {
                tip = "兑换比例：材料 #r10#k 个 → 1 本天赋书\r\n";
            }
        } else if (modeSel === 3) {
            tip = "价格：" + priceText(tierSel) + "\r\n";
        } else {
            tip = "将消耗 1 本对应天赋书进行学习。\r\n";
            if (tierSel === 4) {
                tip += "终极天赋可能失败（Lv1 必成）。\r\n";
            }
        }
        cm.sendSimple(tip + buildBookMenu(bookList, modeSel === 1));
    } else if (status === 3) {
        if (selection < 0 || selection >= bookList.length) {
            cm.dispose();
            return;
        }
        var book = bookList[selection];
        var msg;
        if (modeSel === 1) {
            var rateTip = "";
            if (tierSel === 4) {
                rateTip = "\r\n（本次成功率 " + cm.ultimateLearnRate(book.id()) + "%）";
            }
            msg = cm.learnTalent(book.id()) + rateTip;
        } else if (modeSel === 2) {
            msg = cm.exchangeTalentBook(book.itemId());
        } else {
            msg = cm.buyTalentBook(book.itemId());
        }
        cm.sendOk(msg);
        cm.dispose();
    } else {
        cm.dispose();
    }
}

function listTier(tierOrder) {
    var tier = tierFromOrder(tierOrder);
    var arr = TalentService.listByTier(tier);
    var out = [];
    for (var i = 0; i < arr.size(); i++) {
        out.push(arr.get(i));
    }
    return out;
}

function tierFromOrder(o) {
    if (o === 2) return TalentTier.MID;
    if (o === 3) return TalentTier.ADVANCED;
    if (o === 4) return TalentTier.ULTIMATE;
    return TalentTier.PRIMARY;
}

function priceText(tierOrder) {
    if (tierOrder === 2) return "10万 金币/本";
    if (tierOrder === 3) return "20万 金币/本";
    if (tierOrder === 4) return "50万 金币/本";
    return "5万 金币/本";
}

function buildBookMenu(list, showLv) {
    var s = "";
    for (var i = 0; i < list.length; i++) {
        var t = list[i];
        var lv = cm.getTalentLevel(t.id());
        var extra = showLv ? ("  (当前 Lv." + lv + "/" + t.maxLevel() + ")") : "";
        if (showLv && t.tier() === TalentTier.ULTIMATE) {
            extra += " 成功：" + cm.ultimateLearnRate(t.id()) + "%";
        }
        s += "#L" + i + "##v" + t.itemId() + "# " + t.displayName() + extra + "#l\r\n";
    }
    return s;
}

function buildOwnedText() {
    var s = "#e已学天赋#n\r\n" + cm.talentTierStatus() + "\r\n\r\n";
    var all = TalentId.values();
    for (var i = 0; i < all.length; i++) {
        var t = all[i];
        var lv = cm.getTalentLevel(t.id());
        if (lv > 0) {
            s += t.tier().label() + " · " + t.displayName() + " Lv." + lv + "/" + t.maxLevel() + "\r\n";
        }
    }
    return s;
}
