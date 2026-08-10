// 匠人街 · 锻造子脚本（通过 9031003 装备铸造中心菜单进入）
// 装备锻造：按配方打造装备，随机属性区间（含物防/魔防/MaxHP/MaxMP）
// Java层 ForgeService + ForgeRecipeService 已实现；体力为账号级（StaminaService）
// 品级曲线见 org.gms.service.ForgeService.TIERS：入门0/普通16000/职业32000/大师64000/宗师128000

var ForgeManager = Java.type("org.gms.config.ForgeManager");
var ForgeRecipeManager = Java.type("org.gms.config.ForgeRecipeManager");
var StaminaManager = Java.type("org.gms.config.StaminaManager");
var InventoryType = Java.type("org.gms.client.inventory.InventoryType");
var ItemInformationProvider = Java.type("org.gms.server.ItemInformationProvider");
var InventoryManipulator = Java.type("org.gms.client.inventory.manipulator.InventoryManipulator");
var Randomizer = Java.type("org.gms.util.Randomizer");

var characterId;
var accountId;
var recipeCache = [];
var pendingRecipe = null;

function start() {
    characterId = cm.getPlayer().getId();
    accountId = cm.getPlayer().getAccountId();
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;

    if (status === 0) {
        levelMain();
    } else if (status === 1) {
        levelHandleMain(selection);
    } else if (status === 2) {
        levelConfirmCraft(selection);
    } else if (status === 3) {
        levelDoCraft();
    }
}

// ==================== 主菜单：配方列表 ====================

function levelMain() {
    var info = ForgeManager.getInfo(characterId);
    if (!info.get("success")) {
        cm.sendOk("锻造师信息查询失败：" + info.get("message"));
        cm.dispose();
        return;
    }
    var stamina = StaminaManager.getStamina(accountId);
    var tierName = info.get("tierName");
    var tierIndex = info.get("tierIndex");
    var progress = info.get("progress");
    var tierSize = info.get("tierSize");
    var progressText = info.get("isMax") ? (progress + "（宗师无上限）") : (progress + "/" + tierSize);

    recipeCache = ForgeRecipeManager.listEnabledRecipes();

    var text = "#e#b<装备锻造>#k#n\r\n\r\n";
    text += "剩余体力：#r" + stamina + "#k/1000（账号通用）\r\n";
    text += "锻造师等级：#b" + tierName + "级#k(" + progressText + ")\r\n";
    text += "锻造经验与炼金/炼药完全独立。\r\n\r\n";

    if (recipeCache.isEmpty()) {
        text += "暂无可锻造配方（数据待定，敬请期待）。\r\n\r\n#L99#离开#l";
        cm.sendNextSelectLevel("HandleMain", text);
        return;
    }

    for (var i = 0; i < recipeCache.size(); i++) {
        var recipe = recipeCache.get(i);
        var needTierName = ForgeManager.getTierName(recipe.get("tierRequired"));
        var locked = tierIndex < recipe.get("tierRequired");
        text += "#L" + i + "##b锻造 #t" + recipe.get("resultItemId") + "##k"
            + "\r\n　需要等级：" + needTierName + "级锻造师" + (locked ? " #r(未满足)#k" : "")
            + "\r\n　体力：" + recipe.get("staminaCost") + "　经验：" + recipe.get("expGain") + "　金币：" + recipe.get("mesoCost");
        var statParts = [];
        if (recipe.get("strMax") > 0) statParts.push("力" + recipe.get("strMin") + "~" + recipe.get("strMax"));
        if (recipe.get("dexMax") > 0) statParts.push("敏" + recipe.get("dexMin") + "~" + recipe.get("dexMax"));
        if (recipe.get("intMax") > 0) statParts.push("智" + recipe.get("intMin") + "~" + recipe.get("intMax"));
        if (recipe.get("lukMax") > 0) statParts.push("运" + recipe.get("lukMin") + "~" + recipe.get("lukMax"));
        if (recipe.get("watkMax") > 0) statParts.push("攻" + recipe.get("watkMin") + "~" + recipe.get("watkMax"));
        if (recipe.get("matkMax") > 0) statParts.push("魔攻" + recipe.get("matkMin") + "~" + recipe.get("matkMax"));
        if (recipe.get("pddMax") > 0) statParts.push("物防" + recipe.get("pddMin") + "~" + recipe.get("pddMax"));
        if (recipe.get("mddMax") > 0) statParts.push("魔防" + recipe.get("mddMin") + "~" + recipe.get("mddMax"));
        if (recipe.get("hpMax") > 0) statParts.push("HP" + recipe.get("hpMin") + "~" + recipe.get("hpMax"));
        if (recipe.get("mpMax") > 0) statParts.push("MP" + recipe.get("mpMin") + "~" + recipe.get("mpMax"));
        if (statParts.length > 0) text += "\r\n　属性区间：" + statParts.join("，");
        text += "#l\r\n";
    }
    text += "\r\n#L99#离开#l";
    cm.sendNextSelectLevel("HandleMain", text);
}

function levelHandleMain(selection) {
    if (selection < 0 || selection >= recipeCache.size()) {
        cm.dispose();
        return;
    }
    pendingRecipe = recipeCache.get(selection);
    levelShowConfirm();
}

// ==================== 确认界面 ====================

function levelShowConfirm() {
    var recipe = pendingRecipe;
    var info = ForgeManager.getInfo(characterId);
    if (info.get("tierIndex") < recipe.get("tierRequired")) {
        cm.sendOkLevel("Main", "锻造师等级不足，无法打造该配方。");
        return;
    }

    var text = "是否消耗以下材料锻造 #b#t" + recipe.get("resultItemId") + "##k？\r\n\r\n";
    text += "体力：" + recipe.get("staminaCost") + "\r\n";
    text += "金币：" + recipe.get("mesoCost") + "\r\n";
    var mats = getMaterialList(recipe);
    for (var j = 0; j < mats.length; j++) {
        text += "材料：#t" + mats[j].id + "# ×" + mats[j].count + "\r\n";
    }
    text += "\r\n属性区间（随机）：\r\n";
    if (recipe.get("strMax") > 0) text += "力量：" + recipe.get("strMin") + " ~ " + recipe.get("strMax") + "\r\n";
    if (recipe.get("dexMax") > 0) text += "敏捷：" + recipe.get("dexMin") + " ~ " + recipe.get("dexMax") + "\r\n";
    if (recipe.get("intMax") > 0) text += "智力：" + recipe.get("intMin") + " ~ " + recipe.get("intMax") + "\r\n";
    if (recipe.get("lukMax") > 0) text += "运气：" + recipe.get("lukMin") + " ~ " + recipe.get("lukMax") + "\r\n";
    if (recipe.get("watkMax") > 0) text += "攻击力：" + recipe.get("watkMin") + " ~ " + recipe.get("watkMax") + "\r\n";
    if (recipe.get("matkMax") > 0) text += "魔法攻击力：" + recipe.get("matkMin") + " ~ " + recipe.get("matkMax") + "\r\n";
    if (recipe.get("pddMax") > 0) text += "物理防御力：" + recipe.get("pddMin") + " ~ " + recipe.get("pddMax") + "\r\n";
    if (recipe.get("mddMax") > 0) text += "魔法防御力：" + recipe.get("mddMin") + " ~ " + recipe.get("mddMax") + "\r\n";
    if (recipe.get("hpMax") > 0) text += "MaxHP：" + recipe.get("hpMin") + " ~ " + recipe.get("hpMax") + "\r\n";
    if (recipe.get("mpMax") > 0) text += "MaxMP：" + recipe.get("mpMin") + " ~ " + recipe.get("mpMax") + "\r\n";

    cm.sendYesNoLevel("Main", "DoCraft", text);
}

// ==================== 执行锻造 ====================

function levelDoCraft() {
    var recipe = pendingRecipe;
    pendingRecipe = null;
    if (recipe == null) { cm.dispose(); return; }

    // 校验体力
    if (StaminaManager.getStamina(accountId) < recipe.get("staminaCost")) {
        cm.sendOkLevel("Main", "体力不足，无法锻造。");
        return;
    }
    // 校验金币
    if (cm.getMeso() < recipe.get("mesoCost")) {
        cm.sendOkLevel("Main", "金币不足，无法锻造。需要 " + recipe.get("mesoCost") + " 金币。");
        return;
    }
    // 校验材料1~8
    var mats = getMaterialList(recipe);
    for (var k = 0; k < mats.length; k++) {
        if (!cm.haveItem(mats[k].item, mats[k].count)) {
            cm.sendOkLevel("Main", "材料不足：#t" + mats[k].item + "# ×" + mats[k].count);
            return;
        }
    }
    // 校验背包空间
    if (!cm.canHold(recipe.get("resultItemId"), 1)) {
        cm.sendOkLevel("Main", "背包空间不足，无法接收锻造产物。");
        return;
    }

    // Java侧：品级校验 + 体力扣除 + 锻造师经验增加
    var result;
    try {
        result = ForgeRecipeManager.craft(characterId, accountId, recipe.get("id"));
    } catch (e) {
        cm.sendOkLevel("Main", "锻造失败：" + e.toString());
        return;
    }
    if (!result.get("success")) {
        cm.sendOkLevel("Main", "锻造失败：" + result.get("message"));
        return;
    }

    // 扣金币/材料
    if (recipe.get("mesoCost") > 0) cm.gainMeso(-recipe.get("mesoCost"));
    for (var m = 0; m < mats.length; m++) {
        cm.gainItem(mats[m].item, -mats[m].count);
    }

    // 生成随机属性装备并发放
    var c = cm.getPlayer().getClient();
    var ii = ItemInformationProvider.getInstance();
    var newEquip = ii.getEquipById(recipe.get("resultItemId"));
    if (newEquip == null) {
        cm.gainItem(recipe.get("resultItemId"), 1);
    } else {
        newEquip.setStr(rand(recipe, "str"));
        newEquip.setDex(rand(recipe, "dex"));
        newEquip.setInt(rand(recipe, "int"));
        newEquip.setLuk(rand(recipe, "luk"));
        newEquip.setWatk(rand(recipe, "watk"));
        newEquip.setMatk(rand(recipe, "matk"));
        newEquip.setWdef(rand(recipe, "pdd"));
        newEquip.setMdef(rand(recipe, "mdd"));
        newEquip.setHp(rand(recipe, "hp"));
        newEquip.setMp(rand(recipe, "mp"));
        var added = InventoryManipulator.addFromDrop(c, newEquip, true);
        if (!added) {
            cm.sendOkLevel("Main", "背包空间不足，无法接收锻造产物。");
            return;
        }
    }

    cm.sendOkLevel("Main", "#b锻造成功！#k\r\n获得：#t" + recipe.get("resultItemId") + "#\r\n增加锻造经验：" + recipe.get("expGain") + "，扣除体力 " + recipe.get("staminaCost"));
}

// ==================== 工具 ====================

function getMaterialList(recipe) {
    var list = [];
    for (var i = 1; i <= 8; i++) {
        var id = recipe.get("material" + i + "ItemId");
        var count = recipe.get("material" + i + "Count");
        if (id != null && id > 0 && count > 0) {
            list.push({ item: id, count: count });
        }
    }
    return list;
}

function rand(recipe, key) {
    var min = recipe.get(key + "Min") || 0;
    var max = recipe.get(key + "Max") || 0;
    if (max <= 0) return 0;
    if (max == min) return min;
    return Randomizer.nextInt(max - min + 1) + min;
}