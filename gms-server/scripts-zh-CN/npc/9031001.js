/**
 * @description 药剂师（炼药）副职业系统
 * 1. 体力：账号级共享（同账号下所有角色共用一份），每日首次进入自动发放100点，上限1000点；
 *    可通过体力药水恢复（不会超过上限）。
 * 2. 炼药师等级：角色级隔离，分5级——入门/普通/职业/大师/宗师，累计经验只增不减，升级不重置为0。
 *    各等级所需累计经验：入门0~800，普通800~2400，职业2400~66400，大师66400~194400，宗师194400以上（无上限）。
 * 3. 炼制不同品级药水获得固定经验：入门8/普通16/职业64/大师128/宗师256，
 *    消耗对应体力：入门1/普通2/职业4/大师8/宗师16；炼药师等级需达到对应品级才能炼制。
 * 4. 产出/材料物品ID均为占位（TODO：正式数据确定后替换 POTION_ITEM_IDS / STAMINA_POTION_ITEM_ID）。
 * 数据库操作全部在 org.gms.service.AlchemistService / StaminaService 中完成。
 */

var AlchemistManager = Java.type("org.gms.config.AlchemistManager");
var StaminaManager = Java.type("org.gms.config.StaminaManager");

var characterId;
var accountId;

// TODO: 占位物品ID，正式数据确定后替换。下标对应品级 0=入门 1=普通 2=职业 3=大师 4=宗师
var POTION_ITEM_IDS = [9031900, 9031901, 9031902, 9031903, 9031904];
// TODO: 占位体力药水物品ID，使用后恢复的体力值同样为占位数值
var STAMINA_POTION_ITEM_ID = 9031909;
var STAMINA_POTION_RESTORE = 50;

// ==================== 入口 ====================

function start() {
    characterId = cm.getPlayer().getId();
    accountId = cm.getClient().getAccID();
    levelMain();
}

// ==================== 主菜单 ====================

function levelMain() {
    var info = AlchemistManager.getInfo(characterId);
    if (!info.get("success")) {
        cm.sendOk("炼药师信息查询失败：" + info.get("message"));
        cm.dispose();
        return;
    }
    var stamina = StaminaManager.getStamina(accountId);
    var tierName = info.get("tierName");
    var progress = info.get("progress");
    var tierSize = info.get("tierSize");
    var progressText = info.get("isMax") ? (progress + "（宗师无上限）") : (progress + "/" + tierSize);

    var text = "#e药剂师#n\r\n\r\n"
        + "剩余体力：#r" + stamina + "#k/1000（体力是账号通用的）\r\n"
        + "副职等级：#b" + tierName + "级炼药师#k(" + progressText + ")\r\n\r\n"
        + "#L0#炼制药水#l\r\n"
        + "#L1#使用体力药水#l\r\n"
        + "#L9#离开#l";
    cm.sendNextSelectLevel("HandleMain", text);
}

function levelHandleMain(selection) {
    if (selection === 0) {
        levelChooseBrewTier();
        return;
    }
    if (selection === 1) {
        levelUseStaminaPotion();
        return;
    }
    cm.dispose();
}

// ==================== 炼制药水 ====================

function levelChooseBrewTier() {
    var info = AlchemistManager.getInfo(characterId);
    if (!info.get("success")) {
        cm.sendOk("炼药师信息查询失败：" + info.get("message"));
        cm.dispose();
        return;
    }
    var currentTierIndex = info.get("tierIndex");
    var tierCount = AlchemistManager.getTierCount();
    var text = "请选择要炼制的药水品级：\r\n\r\n";
    for (var i = 0; i <= currentTierIndex && i < tierCount; i++) {
        text += "#L" + i + "##b" + AlchemistManager.getTierName(i) + "#k药水#l\r\n";
    }
    cm.sendNextSelectLevel("HandleBrewTier", text);
}

function levelHandleBrewTier(selection) {
    var tierIndex = selection;
    var result = AlchemistManager.brew(characterId, accountId, tierIndex);
    if (result.get("success")) {
        var itemId = POTION_ITEM_IDS[tierIndex];
        var gained = cm.canHold(itemId, 1);
        if (gained) {
            cm.gainItem(itemId, 1);
        }
        cm.sendOkLevel("Main", "炼制成功！获得经验：#r" + result.get("brewExp") + "#k，剩余体力：#r" + result.get("staminaLeft") + "#k"
            + (gained ? "" : "\r\n（背包空间不足，未能获得药水实物）"));
    } else {
        cm.sendOkLevel("Main", "炼制失败：" + result.get("message"));
    }
}

// ==================== 使用体力药水 ====================

function levelUseStaminaPotion() {
    if (!cm.haveItem(STAMINA_POTION_ITEM_ID, 1)) {
        cm.sendOkLevel("Main", "您没有体力药水。");
        return;
    }
    cm.gainItem(STAMINA_POTION_ITEM_ID, -1);
    var result = StaminaManager.addStamina(accountId, STAMINA_POTION_RESTORE);
    if (result.get("success")) {
        cm.sendOkLevel("Main", "使用体力药水成功，当前体力：#r" + result.get("stamina") + "#k/1000");
    } else {
        cm.sendOkLevel("Main", "使用失败：" + result.get("message"));
    }
}
