/**
 * @description 强化戒指系统
 * 功能：根据怪物卡片手册等级（册数），兑换/升级对应的强化戒指
 * 入口：9900001.js case 303
 *
 * 戒指等级对照（参考怪物手册完成度）：
 *   Bronze Monster Book Ring   → monsterbook.level >= 1  → 物品ID: 1119000
 *   Silver Monster Book Ring   → monsterbook.level >= 10 → 物品ID: 1119001
 *   Gold Monster Book Ring     → monsterbook.level >= 30 → 物品ID: 1119002
 *   Master Monster Book Ring   → monsterbook.level >= 50 → 物品ID: 1119003
 *   Legendary Monster Book Ring→ monsterbook.level >= 80 → 物品ID: 1119004
 */

// 戒指配置：[最低怪物手册等级, 戒指ItemID, 戒指名称]
var RING_TIERS = [
    [1,  1119000, "铜质怪物手册戒指"],
    [10, 1119001, "银质怪物手册戒指"],
    [30, 1119002, "黄金怪物手册戒指"],
    [50, 1119003, "大师怪物手册戒指"],
    [80, 1119004, "传说怪物手册戒指"]
];

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === 1) {
        status++;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        showMainMenu();
    } else if (status === 1) {
        handleSelection(selection);
    } else if (status === 2) {
        if (type === 1) {
            doExchange(selection);
        } else {
            cm.sendOk("已取消兑换。");
            cm.dispose();
        }
    }
}

function showMainMenu() {
    var player = cm.getPlayer();
    var bookLevel = player.getMonsterBook().getBookLevel();

    var text = "#e强化戒指兑换#n\r\n\r\n";
    text += "你的怪物手册等级：#r" + bookLevel + "#k\r\n";
    text += "#d收集怪物卡片以提升手册等级，兑换更强力的戒指！#k\r\n\r\n";
    text += "━━━ 可兑换戒指 ━━━\r\n\r\n";

    var hasAny = false;
    for (var i = RING_TIERS.length - 1; i >= 0; i--) {
        var tier = RING_TIERS[i];
        var minLevel = tier[0];
        var itemId = tier[1];
        var name = tier[2];
        var owned = cm.haveItem(itemId);

        if (bookLevel >= minLevel) {
            hasAny = true;
            text += "#L" + i + "#";
            text += "#i" + itemId + "# #b" + name + "#k";
            if (owned) {
                text += "  #d（已拥有）#k";
            } else {
                text += "  #r（可兑换）#k";
            }
            text += "#l\r\n";
        } else {
            text += "#d[需手册等级 " + minLevel + "] #i" + itemId + "# " + name + "#k\r\n";
        }
    }

    if (!hasAny) {
        text += "#r你的手册等级不足，请先收集怪物卡片！#k\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    text += "\r\n#L9#关闭#l\r\n";
    cm.sendSimple(text);
}

function handleSelection(selection) {
    if (selection === 9) {
        cm.dispose();
        return;
    }
    if (selection < 0 || selection >= RING_TIERS.length) {
        cm.dispose();
        return;
    }

    var tier = RING_TIERS[selection];
    var itemId = tier[1];
    var name = tier[2];
    var owned = cm.haveItem(itemId);

    if (owned) {
        cm.sendOk("你已经拥有 #i" + itemId + "# #b" + name + "#k，无需重复兑换。");
        cm.dispose();
        return;
    }

    // 检查手册等级
    var bookLevel = cm.getPlayer().getMonsterBook().getBookLevel();
    if (bookLevel < tier[0]) {
        cm.sendOk("#r手册等级不足！需要：#b" + tier[0] + "#k，当前：#r" + bookLevel + "#k");
        cm.dispose();
        return;
    }

    // 保存选中的tier index到status中用于确认回调
    // 通过sendYesNo触发status++，此时selection是之前传入的
    pendingTierIdx = selection;
    cm.sendYesNo("确认兑换 #i" + itemId + "# #b" + name + "#k？\r\n（无需消耗任何材料，根据手册等级免费兑换）");
}

var pendingTierIdx = -1;

function doExchange(selection) {
    if (pendingTierIdx < 0 || pendingTierIdx >= RING_TIERS.length) {
        cm.sendOk("兑换数据异常，请重试。");
        cm.dispose();
        return;
    }

    var tier = RING_TIERS[pendingTierIdx];
    var itemId = tier[1];
    var name = tier[2];

    if (!cm.canHold(itemId, 1)) {
        cm.sendOk("#r背包空间不足，请清理背包后再来领取！#k");
        cm.dispose();
        return;
    }

    cm.gainItem(itemId, 1);
    cm.sendOk("兑换成功！\r\n\r\n#i" + itemId + "# #b" + name + "#k 已放入背包。\r\n#g祝你冒险顺利！#k");
    cm.dispose();
}
