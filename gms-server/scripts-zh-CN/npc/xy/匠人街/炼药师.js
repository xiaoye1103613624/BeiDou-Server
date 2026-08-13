/**
 * @description 炼药师（炼药）副职业系统
 * 1. 体力：账号级共享（同账号下所有角色共用一份），体力由 StaminaService 管理。
 * 2. 炼药师等级：角色隔离，累计经验只增不减，升级不重置为0；品级曲线由共享品级表
 *    （AlchemyTierManager，type=炼药）配置，默认 入门/普通/职业/大师/宗师，最高品级无上限。
 * 3. 炼制配方（产出物品/材料物品/各项消耗数值）统一存储在 xy_alchemist_recipe 配置表中；
 *    每条配方可配置：所需品级、产出物品、增加经验、体力消耗、金币消耗、至多5种材料物品。
 * 4. 金币/材料的校验与扣除、产出物品的发放均由本脚本完成；品级校验与体力扣除、炼药师经验
 *    增加由 AlchemistRecipeManager / StaminaService 完成。
 */

var AlchemistManager = Java.type("org.gms.config.AlchemistManager");
var AlchemistRecipeManager = Java.type("org.gms.config.AlchemistRecipeManager");
var StaminaManager = Java.type("org.gms.config.StaminaManager");
var CashShop = Java.type("org.gms.server.CashShop");

var characterId;
var accountId;
var recipeCache = [];
var pendingRecipe = null;

// ==================== 入口 ====================

function start() {
    characterId = cm.getPlayer().getId();
    accountId = cm.getClient().getAccID();
    levelMain();
}

function getCash() {
    return cm.getPlayer().getCashShop().getCash(CashShop.NX_CREDIT);
}

// ==================== 主菜单：配方列表 ====================

function levelMain() {
    var info = AlchemistManager.getInfo(characterId);
    if (!info.get("success")) {
        cm.sendOk("炼药师信息查询失败：" + info.get("message"));
        cm.dispose();
        return;
    }
    var stamina = StaminaManager.getStamina(accountId);
    var tierName = info.get("tierName");
    var tierIndex = info.get("tierIndex");
    var progress = info.get("progress");
    var tierSize = info.get("tierSize");
    var progressText = info.get("isMax") ? (progress + "（宗师无上限）") : (progress + "/" + tierSize);

    recipeCache = AlchemistRecipeManager.listEnabledRecipes();

    var text = "#e炼药师\r\n\r\n"
        + "剩余体力：#r" + stamina + "#k/1000（体力是账号通用的）\r\n"
        + "副职业等级：#b" + tierName + "级炼药师#k(" + progressText + ")\r\n\r\n";

    if (recipeCache.length === 0) {
        text += "暂无可炼制的配方（数据待定，敬请期待）。\r\n\r\n#L0#离开#l";
        cm.sendNextSelectLevel("HandleMain", text);
        return;
    }

    for (var i = 0; i < recipeCache.length; i++) {
        var recipe = recipeCache[i];
        var needTierName = AlchemistManager.getTierName(recipe.get("tierRequired"));
        var locked = tierIndex < recipe.get("tierRequired");
        text += "#L" + i + "##b炼制#t" + recipe.get("resultItemId") + "##k x" + recipe.get("resultCount")
            + "\r\n    需要副职业等级：" + needTierName + "级炼药师" + (locked ? "#r(未满足)#k" : "")
            + "\r\n    增加经验：" + recipe.get("expGain") + "#l\r\n";
    }
    text += "#l 离开";
    cm.sendNextSelectLevel("HandleMain", text);
}

function levelHandleMain(selection) {
    if (selection < 0 || selection >= recipeCache.length) {
        cm.dispose();
        return;
    }
    levelConfirmCraft(recipeCache[selection]);
}

// ==================== 炼制确认 ====================

function levelConfirmCraft(recipe) {
    pendingRecipe = recipe;
    var info = AlchemistManager.getInfo(characterId);
    if (info.get("tierIndex") < recipe.get("tierRequired")) {
        cm.sendOkLevel("Main", "炼药师等级不足，无法炼制该配方。");
        return;
    }

    var text = "是否消耗以下材料炼制#b#t" + recipe.get("resultItemId") + "##k x" + recipe.get("resultCount") + "？\r\n\r\n"
        + "体力：" + recipe.get("staminaCost") + "\r\n"
        + "金币：" + recipe.get("mesoCost") + "\r\n";
    if (recipe.get("material1ItemId")) {
        text += "材料1：#t" + recipe.get("material1ItemId") + "# x" + recipe.get("material1Count") + "\r\n";
    }
    if (recipe.get("material2ItemId")) {
        text += "材料2：#t" + recipe.get("material2ItemId") + "# x" + recipe.get("material2Count") + "\r\n";
    }
    if (recipe.get("material3ItemId")) {
        text += "材料3：#t" + recipe.get("material3ItemId") + "# x" + recipe.get("material3Count") + "\r\n";
    }
    if (recipe.get("material4ItemId")) {
        text += "材料4：#t" + recipe.get("material4ItemId") + "# x" + recipe.get("material4Count") + "\r\n";
    }
    if (recipe.get("material5ItemId")) {
        text += "材料5：#t" + recipe.get("material5ItemId") + "# x" + recipe.get("material5Count") + "\r\n";
    }
    cm.sendYesNoLevel("Main", "DoCraft", text);
}

function levelDoCraft() {
    var recipe = pendingRecipe;
    pendingRecipe = null;
    if (recipe == null) {
        cm.dispose();
        return;
    }

    // 炼制前由脚本校验体力/金币/材料是否充足（体力由 Java 侧二次校验扣除）
    if (StaminaManager.getStamina(accountId) < recipe.get("staminaCost")) {
        cm.sendOkLevel("Main", "体力不足，无法炼制。");
        return;
    }
    if (cm.getMeso() < recipe.get("mesoCost")) {
        cm.sendOkLevel("Main", "金币不足，无法炼制。");
        return;
    }
    if (recipe.get("material1ItemId") && !cm.haveItem(recipe.get("material1ItemId"), recipe.get("material1Count"))) {
        cm.sendOkLevel("Main", "材料不足，无法炼制。");
        return;
    }
    if (recipe.get("material2ItemId") && !cm.haveItem(recipe.get("material2ItemId"), recipe.get("material2Count"))) {
        cm.sendOkLevel("Main", "材料不足，无法炼制。");
        return;
    }
    if (recipe.get("material3ItemId") && !cm.haveItem(recipe.get("material3ItemId"), recipe.get("material3Count"))) {
        cm.sendOkLevel("Main", "材料不足，无法炼制。");
        return;
    }
    if (recipe.get("material4ItemId") && !cm.haveItem(recipe.get("material4ItemId"), recipe.get("material4Count"))) {
        cm.sendOkLevel("Main", "材料不足，无法炼制。");
        return;
    }
    if (recipe.get("material5ItemId") && !cm.haveItem(recipe.get("material5ItemId"), recipe.get("material5Count"))) {
        cm.sendOkLevel("Main", "材料不足，无法炼制。");
        return;
    }
    if (!cm.canHold(recipe.get("resultItemId"), recipe.get("resultCount"))) {
        cm.sendOkLevel("Main", "背包空间不足，无法炼制。");
        return;
    }

    // 品级校验 + 体力扣除 + 炼药师经验增加（Java 侧权威处理）
    var result = AlchemistRecipeManager.craft(characterId, accountId, recipe.get("id"));
    if (!result.get("success")) {
        cm.sendOkLevel("Main", "炼制失败：" + result.get("message"));
        return;
    }

    // Java 侧扣体力/加经验成功后，脚本侧扣材料/金币并发放产出物品
    if (recipe.get("mesoCost") > 0) {
        cm.gainMeso(-recipe.get("mesoCost"));
    }
    if (recipe.get("material1ItemId")) {
        cm.gainItem(recipe.get("material1ItemId"), -recipe.get("material1Count"));
    }
    if (recipe.get("material2ItemId")) {
        cm.gainItem(recipe.get("material2ItemId"), -recipe.get("material2Count"));
    }
    if (recipe.get("material3ItemId")) {
        cm.gainItem(recipe.get("material3ItemId"), -recipe.get("material3Count"));
    }
    if (recipe.get("material4ItemId")) {
        cm.gainItem(recipe.get("material4ItemId"), -recipe.get("material4Count"));
    }
    if (recipe.get("material5ItemId")) {
        cm.gainItem(recipe.get("material5ItemId"), -recipe.get("material5Count"));
    }
    cm.gainItem(recipe.get("resultItemId"), recipe.get("resultCount"));

    cm.sendOkLevel("Main", "炼制成功！获得经验：#r" + recipe.get("expGain") + "#k");
}