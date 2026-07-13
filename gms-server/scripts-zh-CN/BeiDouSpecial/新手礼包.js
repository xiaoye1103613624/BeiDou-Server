/*
 * ==================
 * 脚本类型: 新手礼包领取 (status状态机模式)
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 显示当前可领取的礼包列表
 *   2. 检查等级限制和是否已领取
 *   3. 领取后发放物品、金币、点卷、抵用券
 *   4. 每个角色每个礼包只能领取一次
 * ==================
 */

var status = -1;
var NewbieGiftManager, ItemInformationProvider;

var giftList = [];
var selectedGift = null;
var selectedIndex = -1;

// ===== 入口 =====

function start() {
    status = -1;
    try {
        NewbieGiftManager = Java.type('org.gms.config.NewbieGiftManager');
        // 确保缓存有数据
        NewbieGiftManager.reload();
        ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider');
    } catch (e) {
        cm.sendOk("新手礼包系统初始化失败，请联系管理员。");
        cm.dispose();
        return;
    }
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 1) {
        status++;
    } else {
        status--;
    }

    try {
        if (status === 0) {
            showAvailableGifts();
        } else if (status === 1) {
            handleGiftSelect(selection);
        } else if (status === 2) {
            handleConfirm(selection);
        } else {
            cm.dispose();
        }
    } catch (e) {
        cm.sendOk("新手礼包系统异常，请联系管理员。\r\n错误: " + e);
        cm.dispose();
    }
}

// ===== 菜单：显示可领取的礼包 =====

function showAvailableGifts() {
    var playerLevel = cm.getPlayer().getLevel();
    var charId = cm.getPlayer().getId();

    giftList = NewbieGiftManager.getAvailableGifts(playerLevel, charId);

    if (giftList.isEmpty()) {
        cm.sendOk("当前没有可领取的新手礼包。\r\n\r\n#r可能原因：#k\r\n  - 暂未达到领取等级\r\n  - 礼包已领取完毕\r\n  - 管理员尚未配置礼包");
        cm.dispose();
        return;
    }

    var text = "#e#b=== 新手礼包 ===#k#n\r\n";
    text += "欢迎领取新手礼包，每个礼包限领一次！\r\n";
    text += "当前等级: #bLv." + playerLevel + "#k\r\n\r\n";

    for (var i = 0; i < giftList.size(); i++) {
        var g = giftList.get(i);
        text += "#L" + i + "##b" + g.get("name") + "#k";
        text += " (Lv." + g.get("minLevel") + "~" + g.get("maxLevel") + ")";
        text += "#l\r\n";
    }

    cm.sendSimple(text);
}

// ===== 选礼包：显示详情 =====

function handleGiftSelect(selection) {
    if (selection < 0 || selection >= giftList.size()) {
        cm.dispose();
        return;
    }

    selectedIndex = selection;
    selectedGift = giftList.get(selection);
    var giftId = selectedGift.get("id");

    var items = NewbieGiftManager.getGiftItems(giftId);
    var currencies = NewbieGiftManager.getGiftCurrencies(giftId);

    var text = "【" + selectedGift.get("name") + "】\r\n";
    text += "等级要求: Lv." + selectedGift.get("minLevel") + " ~ " + selectedGift.get("maxLevel") + "\r\n\r\n";

    // 物品奖励
    if (!items.isEmpty()) {
        text += "#b【物品奖励】#k\r\n";
        for (var i = 0; i < items.size(); i++) {
            var it = items.get(i);
            text += "  #i" + it.getItemId() + "# " + ItemInformationProvider.getInstance().getName(it.getItemId()) + " ×" + it.getQuantity() + "\r\n";
        }
        text += "\r\n";
    }

    // 货币奖励
    if (!currencies.isEmpty()) {
        text += "#b【货币奖励】#k\r\n";
        for (var c = 0; c < currencies.size(); c++) {
            var cu = currencies.get(c);
            switch (cu.getCurrencyType()) {
                case "meso":
                    text += "  金币: #r" + cu.getAmount().toLocaleString() + " 金币#k\r\n";
                    break;
                case "cash":
                    text += "  点卷: #r" + cu.getAmount() + " 点卷#k\r\n";
                    break;
                case "credit":
                    text += "  抵用券: #r" + cu.getAmount() + " 抵用券#k\r\n";
                    break;
            }
        }
        text += "\r\n";
    }

    text += "#L0#确认领取#l\r\n#L1#我再想想#l";
    cm.sendSimple(text);
}

// ===== 确认领取 =====

function handleConfirm(selection) {
    if (selection !== 0) {
        cm.dispose();
        return;
    }

    var giftId = selectedGift.get("id");
    var charId = cm.getPlayer().getId();

    // 再次验证资格
    var playerLevel = cm.getPlayer().getLevel();
    if (playerLevel < selectedGift.get("minLevel") || playerLevel > selectedGift.get("maxLevel")) {
        cm.sendOk("你的等级已不满足领取条件。\r\n需要 Lv." + selectedGift.get("minLevel") + " ~ " + selectedGift.get("maxLevel"));
        cm.dispose();
        return;
    }

    if (NewbieGiftManager.hasClaimed(charId, giftId)) {
        cm.sendOk("你已经领取过该礼包了！");
        cm.dispose();
        return;
    }

    // 插入领取记录（防止重复领取）
    if (!NewbieGiftManager.claimGift(charId, giftId)) {
        cm.sendOk("领取失败，你已经领取过该礼包。");
        cm.dispose();
        return;
    }

    // 发放物品
    var items = NewbieGiftManager.getGiftItems(giftId);
    var gotItems = "";
    for (var i = 0; i < items.size(); i++) {
        var it = items.get(i);
        if (cm.canHold(it.getItemId(), it.getQuantity())) {
            cm.gainItem(it.getItemId(), it.getQuantity());
            gotItems += "#i" + it.getItemId() + "# ×" + it.getQuantity() + "\r\n";
        } else {
            gotItems += "#r（背包已满，无法领取：#i" + it.getItemId() + "# ×" + it.getQuantity() + "）#k\r\n";
        }
    }

    // 发放货币
    var currencies = NewbieGiftManager.getGiftCurrencies(giftId);
    var gotCurrencies = "";
    for (var c = 0; c < currencies.size(); c++) {
        var cu = currencies.get(c);
        switch (cu.getCurrencyType()) {
            case "meso":
                cm.gainMeso(cu.getAmount()); // 修复：gainMeso无3参数重载，改用单参数版本
                gotCurrencies += "金币 +" + cu.getAmount().toLocaleString() + "\r\n";
                break;
            case "cash":
                cm.getPlayer().getCashShop().gainCash(1, cu.getAmount());
                gotCurrencies += "点卷 +" + cu.getAmount() + "\r\n";
                break;
            case "credit":
                cm.getPlayer().getCashShop().gainCash(2, cu.getAmount());
                gotCurrencies += "抵用券 +" + cu.getAmount() + "\r\n";
                break;
        }
    }

    var text = "#e#b领取成功！#k#n\r\n\r\n";
    text += "【" + selectedGift.get("name") + "】\r\n\r\n";
    if (gotItems !== "") {
        text += "#b物品：#k\r\n" + gotItems + "\r\n";
    }
    if (gotCurrencies !== "") {
        text += "#b货币：#k\r\n" + gotCurrencies + "\r\n";
    }
    text += "感谢你的支持，祝游戏愉快！";

    cm.sendOk(text);
    cm.dispose();
}
