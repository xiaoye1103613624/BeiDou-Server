/**
 * @description 炼金师副职业系统
 * 1. 体力：与炼药师共用同一账号级体力池（每日发放100点，上限1000；可用 2431952 恢复，详见 xy/匠人街/炼药师.js）。
 * 2. 炼金师等级：角色级隔离，与炼药师经验池完全独立，分5级——入门/普通/职业/大师/宗师，
 *    累计经验只增不减，升级不重置。等级曲线见 org.gms.service.AlchemyService.TIERS。
 * 3. 炼制配方（产出物品/材料物品/各项消耗数值均待定）统一存储在 xy_alchemy_recipe 配置表中，
 *    每条配方可配置：所需品级、产出物品、增加经验、体力消耗、金币消耗、点券消耗、最多3种材料物品。
 *    新增/修改配方请直接在数据库 xy_alchemy_recipe 表中操作（默认 enabled=0，确认数据后改为1）。
 * 4. 金币/点券/材料的校验与扣除、产出物品的发放均由本脚本完成；品级校验与体力扣除、
 *    炼金师经验增加由 org.gms.service.AlchemyRecipeService 完成。
 * 数据库操作全部在 org.gms.service.AlchemyService / AlchemyRecipeService / StaminaService 中完成。
 */

var AlchemyManager = Java.type("org.gms.config.AlchemyManager");
var AlchemyRecipeManager = Java.type("org.gms.config.AlchemyRecipeManager");
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
    var info = AlchemyManager.getInfo(characterId);
    if (!info.get("success")) {
        cm.sendOk("炼金师信息查询失败：" + info.get("message"));
        cm.dispose();
        return;
    }
    var stamina = StaminaManager.getStamina(accountId);
    var tierName = info.get("tierName");
    var tierIndex = info.get("tierIndex");
    var progress = info.get("progress");
    var tierSize = info.get("tierSize");
    var progressText = info.get("isMax") ? (progress + "（宗师无上限）") : (progress + "/" + tierSize);

    recipeCache = AlchemyRecipeManager.listEnabledRecipes();

    var text = "#e炼金师#n\r\n\r\n"
        + "剩余体力：#r" + stamina + "#k/1000（体力是账号通用的）\r\n"
        + "副职等级：#b" + tierName + "级炼金师#k(" + progressText + ")\r\n\r\n";

    if (recipeCache.length === 0) {
        text += "暂无可炼制配方（数据待定，敬请期待）。\r\n\r\n#L9#离开#l";
        cm.sendNextSelectLevel("HandleMain", text);
        return;
    }

    for (var i = 0; i < recipeCache.length; i++) {
        var recipe = recipeCache[i];
        var needTierName = AlchemyManager.getTierName(recipe.get("tierRequired"));
        var locked = tierIndex < recipe.get("tierRequired");
        text += "#L" + i + "##b炼制#t" + recipe.get("resultItemId") + "##k x" + recipe.get("resultCount")
            + "\r\n　需要副职等级：" + needTierName + "级炼金师" + (locked ? "#r(未满足)#k" : "")
            + "\r\n　增加经验：" + recipe.get("expGain") + "#l\r\n";
    }
    text += "#L9#离开#l";
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
    var info = AlchemyManager.getInfo(characterId);
    if (info.get("tierIndex") < recipe.get("tierRequired")) {
        cm.sendOkLevel("Main", "炼金师等级不足，无法炼制该配方。");
        return;
    }

    var text = "是否消耗以下材料炼制#b#t" + recipe.get("resultItemId") + "##k x" + recipe.get("resultCount") + "？\r\n\r\n"
        + "体力：" + recipe.get("staminaCost") + "\r\n"
        + "金币：" + recipe.get("mesoCost") + "\r\n"
        + "点券：" + recipe.get("cashCost") + "\r\n";
    if (recipe.get("material1ItemId")) {
        text += "材料：#t" + recipe.get("material1ItemId") + "# x" + recipe.get("material1Count") + "\r\n";
    }
    if (recipe.get("material2ItemId")) {
        text += "材料：#t" + recipe.get("material2ItemId") + "# x" + recipe.get("material2Count") + "\r\n";
    }
    if (recipe.get("material3ItemId")) {
        text += "材料：#t" + recipe.get("material3ItemId") + "# x" + recipe.get("material3Count") + "\r\n";
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

    // 炼制前由脚本校验体力/金币/点券/材料是否充足（与配方所需品级一致，体力由Java侧二次校验扣除）
    if (StaminaManager.getStamina(accountId) < recipe.get("staminaCost")) {
        cm.sendOkLevel("Main", "体力不足，无法炼制。");
        return;
    }
    if (cm.getMeso() < recipe.get("mesoCost")) {
        cm.sendOkLevel("Main", "金币不足，无法炼制。");
        return;
    }
    if (getCash() < recipe.get("cashCost")) {
        cm.sendOkLevel("Main", "点券不足，无法炼制。");
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
    if (!cm.canHold(recipe.get("resultItemId"), recipe.get("resultCount"))) {
        cm.sendOkLevel("Main", "背包空间不足，无法炼制。");
        return;
    }

    // 品级校验 + 体力扣除 + 炼金师经验增加（Java侧权威处理）
    var result = AlchemyRecipeManager.craft(characterId, accountId, recipe.get("id"));
    if (!result.get("success")) {
        cm.sendOkLevel("Main", "炼制失败：" + result.get("message"));
        return;
    }

    // Java侧扣体力/加经验成功后，脚本侧扣材料/金币/点券并发放产出物品
    if (recipe.get("mesoCost") > 0) {
        cm.gainMeso(-recipe.get("mesoCost"));
    }
    if (recipe.get("cashCost") > 0) {
        cm.gainNX(-recipe.get("cashCost"));
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
    cm.gainItem(recipe.get("resultItemId"), recipe.get("resultCount"));

    cm.sendOkLevel("Main", "炼制成功！获得经验：#r" + recipe.get("expGain") + "#k");
}
