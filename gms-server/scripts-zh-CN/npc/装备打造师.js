/**
 * @author: 萧曵
 * @npc: 装备打造师
 * @description:
 *   由 9031003.js 通过 cm.openNpc(9031003, "装备打造师") 跳转进入。
 *   职业等级与炼药师/炼金师一样，是独立的副职业体系：分5级——入门/普通/职业/大师/宗师，
 *   按角色隔离、经验只增不减、升级不重置。等级曲线见 org.gms.service.ForgeService.TIERS，
 *   与炼药师({@link 9031001.js})/炼金师({@link 9031005.js})完全独立的经验池，互不影响。
 *   打造配方统一存储在 xy_forge_recipe 配置表中(默认 enabled=0，确认数据后改为1)，
 *   每条配方可配置：所需品级、产出装备、增加经验、金币消耗、最多3种材料物品，
 *   以及每条属性的默认随机区间(intMin~intMax等，0表示该配方不涉及此属性)。
 *   品级校验与锻造师经验增加由 org.gms.service.ForgeRecipeService 完成；
 *   金币/材料的校验与扣除、产出装备的随机属性计算与发放均由本脚本完成(与炼金系统一致的约定)。
 *
 *   打造时可选投入两种加成材料(神铸石/重铸石可以同时叠加使用，但每种材料单次打造最多只能使用1个)：
 *     - 神铸石(4000729)：使用1个让该配方所有相关属性的随机区间整体 +5
 *     - 重铸石(4001527)：使用1个让区间上限额外随机 +1~8
 */

var ForgeManager = Java.type("org.gms.config.ForgeManager");
var ForgeRecipeManager = Java.type("org.gms.config.ForgeRecipeManager");

var neoCrystalId = 4000729;  // 神铸石
var chaosCrystalId = 4001527; // 重铸石
var neoCrystalFlatBonus = 5;     // 神铸石：使用1个让区间整体+5
var chaosCrystalMinExtra = 1;    // 重铸石：使用1个让区间上限额外+1~8(随机下限)
var chaosCrystalMaxExtra = 8;    // 重铸石：使用1个让区间上限额外+1~8(随机上限)

// 属性字段名(对应DB列前缀，intStat对应智力，避免与js关键字int冲突) -> 中文展示名
var statFieldNames = ["str", "dex", "int", "luk", "watk", "matk"];
var statDisplayName = { str: "力量", dex: "敏捷", int: "智力", luk: "运气", watk: "攻击力", matk: "魔攻" };

var characterId;
var recipeCache = [];
var pendingRecipe = null;
var neoCrystalCount = 0;
var chaosCrystalCount = 0;

function start() {
    characterId = cm.getPlayer().getId();
    levelMain();
}

// ==================== 主菜单：配方列表 ====================

function levelMain() {
    var info = ForgeManager.getInfo(characterId);
    if (!info.get("success")) {
        cm.sendOk("锻造师信息查询失败：" + info.get("message"));
        cm.dispose();
        return;
    }
    var tierName = info.get("tierName");
    var tierIndex = info.get("tierIndex");
    var progress = info.get("progress");
    var tierSize = info.get("tierSize");
    var progressText = info.get("isMax") ? (progress + "（宗师无上限）") : (progress + "/" + tierSize);

    recipeCache = ForgeRecipeManager.listEnabledRecipes();

    var text = "#e装备打造师#n\r\n\r\n"
        + "副职等级：#b" + tierName + "级锻造师#k(" + progressText + ")\r\n\r\n";

    if (recipeCache.length === 0) {
        text += "暂无可打造配方（数据待定，敬请期待）。\r\n\r\n#L9#离开#l";
        cm.sendNextSelectLevel("HandleMain", text);
        return;
    }

    for (var i = 0; i < recipeCache.length; i++) {
        var recipe = recipeCache[i];
        var needTierName = ForgeManager.getTierName(recipe.get("tierRequired"));
        var locked = tierIndex < recipe.get("tierRequired");
        text += "#L" + i + "##b打造" + recipe.get("name") + "#k"
            + "\r\n　需要副职等级：" + needTierName + "级锻造师" + (locked ? "#r(未满足)#k" : "#g(已满足)#k")
            + "\r\n　增加职业经验：" + recipe.get("expGain") + "#l\r\n";
    }
    text += "#L9#离开#l";
    cm.sendNextSelectLevel("HandleMain", text);
}

function levelHandleMain(selection) {
    if (selection < 0 || selection >= recipeCache.length) {
        cm.dispose();
        return;
    }
    pendingRecipe = recipeCache[selection];
    var info = ForgeManager.getInfo(characterId);
    if (info.get("tierIndex") < pendingRecipe.get("tierRequired")) {
        cm.sendOkLevel("Main", "锻造师等级不足，无法打造该配方。");
        return;
    }
    询问神铸石();
}

// ==================== 加成材料询问：神铸石 -> 重铸石 ====================

function 询问神铸石() {
    if (neoCrystalId <= 0 || cm.itemQuantity(neoCrystalId) <= 0) {
        neoCrystalCount = 0;
        询问重铸石();
        return;
    }
    cm.sendYesNoLevel("AskNeoNo", "AskNeoYes",
        "是否使用1个#b神铸石#k来提升属性区间？(让区间整体+" + neoCrystalFlatBonus + "，最多使用1个，你拥有" + cm.itemQuantity(neoCrystalId) + "个)");
}

function levelAskNeoYes() {
    neoCrystalCount = 1;
    询问重铸石();
}

function levelAskNeoNo() {
    neoCrystalCount = 0;
    询问重铸石();
}

function 询问重铸石() {
    if (chaosCrystalId <= 0 || cm.itemQuantity(chaosCrystalId) <= 0) {
        chaosCrystalCount = 0;
        显示预览并确认();
        return;
    }
    cm.sendYesNoLevel("AskChaosNo", "AskChaosYes",
        "是否使用1个#b重铸石#k来提升属性区间？(让区间上限额外随机+" + chaosCrystalMinExtra + "~" + chaosCrystalMaxExtra + "，最多使用1个，你拥有" + cm.itemQuantity(chaosCrystalId) + "个)");
}

function levelAskChaosYes() {
    chaosCrystalCount = 1;
    显示预览并确认();
}

function levelAskChaosNo() {
    chaosCrystalCount = 0;
    显示预览并确认();
}

// ==================== 预览与确认 ====================

function 取属性区间(recipe) {
    var ranges = {};
    for (var i = 0; i < statFieldNames.length; i++) {
        var stat = statFieldNames[i];
        var min, max;
        if (stat == "int") {
            min = recipe.get("intMin"); max = recipe.get("intMax");
        } else if (stat == "str") {
            min = recipe.get("strMin"); max = recipe.get("strMax");
        } else if (stat == "dex") {
            min = recipe.get("dexMin"); max = recipe.get("dexMax");
        } else if (stat == "luk") {
            min = recipe.get("lukMin"); max = recipe.get("lukMax");
        } else if (stat == "watk") {
            min = recipe.get("watkMin"); max = recipe.get("watkMax");
        } else if (stat == "matk") {
            min = recipe.get("matkMin"); max = recipe.get("matkMax");
        }
        if (max > 0) {
            ranges[stat] = [min, max];
        }
    }
    return ranges;
}

function 显示预览并确认() {
    var recipe = pendingRecipe;
    var ranges = 取属性区间(recipe);

    var text = "是否消耗以下材料打造#b" + recipe.get("name") + "##k？\r\n\r\n";
    if (recipe.get("material1ItemId")) {
        text += "材料：#t" + recipe.get("material1ItemId") + "# x" + recipe.get("material1Count") + "\r\n";
    }
    if (recipe.get("material2ItemId")) {
        text += "材料：#t" + recipe.get("material2ItemId") + "# x" + recipe.get("material2Count") + "\r\n";
    }
    if (recipe.get("material3ItemId")) {
        text += "材料：#t" + recipe.get("material3ItemId") + "# x" + recipe.get("material3Count") + "\r\n";
    }
    if (neoCrystalCount > 0) {
        text += "材料：#v " + neoCrystalId + "# x" + neoCrystalCount + "\r\n";
    }
    if (chaosCrystalCount > 0) {
        text += "材料：#v " + chaosCrystalId + "# x" + chaosCrystalCount + "\r\n";
    }
    text += "金币：" + recipe.get("mesoCost") + "\r\n\r\n";
    text += "增加职业经验：" + recipe.get("expGain") + "\r\n\r\n";
    text += "#b预览属性(您打造出的装备可能在该属性区间中)#k\r\n\r\n";

    var flatBonus = neoCrystalCount * neoCrystalFlatBonus;
    var maxChaosBonus = chaosCrystalCount * chaosCrystalMaxExtra; // 重铸石只提升上限随机性，预览展示理论最大值

    for (var stat in ranges) {
        var range = ranges[stat];
        var min = range[0] + flatBonus;
        var max = range[1] + flatBonus + maxChaosBonus;
        text += statDisplayName[stat] + "：" + min + "~" + max + "\r\n";
    }
    text += "\r\n是否确认打造？";
    cm.sendYesNoLevel("Main", "DoCraft", text);
}

// ==================== 执行打造 ====================

function levelDoCraft() {
    var recipe = pendingRecipe;
    pendingRecipe = null;
    if (recipe == null) {
        cm.dispose();
        return;
    }

    if (recipe.get("material1ItemId") && !cm.haveItem(recipe.get("material1ItemId"), recipe.get("material1Count"))) {
        cm.sendOkLevel("Main", "材料不足，无法打造。");
        return;
    }
    if (recipe.get("material2ItemId") && !cm.haveItem(recipe.get("material2ItemId"), recipe.get("material2Count"))) {
        cm.sendOkLevel("Main", "材料不足，无法打造。");
        return;
    }
    if (recipe.get("material3ItemId") && !cm.haveItem(recipe.get("material3ItemId"), recipe.get("material3Count"))) {
        cm.sendOkLevel("Main", "材料不足，无法打造。");
        return;
    }
    if (neoCrystalCount > 0 && !cm.haveItem(neoCrystalId, neoCrystalCount)) {
        cm.sendOkLevel("Main", "神铸石数量不足，无法打造。");
        return;
    }
    if (chaosCrystalCount > 0 && !cm.haveItem(chaosCrystalId, chaosCrystalCount)) {
        cm.sendOkLevel("Main", "重铸石数量不足，无法打造。");
        return;
    }
    if (cm.getMeso() < recipe.get("mesoCost")) {
        cm.sendOkLevel("Main", "金币不足，无法打造。");
        return;
    }
    if (!cm.canHold(recipe.get("resultItemId"), 1)) {
        cm.sendOkLevel("Main", "背包空间不足，无法打造。");
        return;
    }

    // 品级校验 + 体力扣除 + 锻造师经验增加（Java侧权威处理）
    var result = ForgeRecipeManager.craft(characterId, cm.getPlayer().getAccountId(), recipe.get("id"));
    if (!result.get("success")) {
        cm.sendOkLevel("Main", "打造失败：" + result.get("message"));
        return;
    }

    // Java侧加经验成功后，脚本侧扣材料/金币并发放产出装备
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
    if (neoCrystalCount > 0) {
        cm.gainItem(neoCrystalId, -neoCrystalCount);
    }
    if (chaosCrystalCount > 0) {
        cm.gainItem(chaosCrystalId, -chaosCrystalCount);
    }

    var ranges = 取属性区间(recipe);
    var flatBonus = neoCrystalCount * neoCrystalFlatBonus;
    var totalChaosBonus = 0;
    for (var c = 0; c < chaosCrystalCount; c++) {
        totalChaosBonus += chaosCrystalMinExtra + Math.floor(Math.random() * (chaosCrystalMaxExtra - chaosCrystalMinExtra + 1));
    }

    var ii = Packages.server.MapleItemInformationProvider.getInstance();
    var newEquip = ii.getEquipById(recipe.get("resultItemId")).copy();

    for (var stat in ranges) {
        var range = ranges[stat];
        var rolled = range[0] + Math.floor(Math.random() * (range[1] - range[0] + 1));
        rolled = (rolled + flatBonus + totalChaosBonus) & 0xFFFF;
        // 不使用动态方法名调用(newEquip[setterName])，本服脚本未见该用法，显式调用对应setter
        if (stat == "str") { newEquip.setStr(rolled); }
        else if (stat == "dex") { newEquip.setDex(rolled); }
        else if (stat == "int") { newEquip.setInt(rolled); }
        else if (stat == "luk") { newEquip.setLuk(rolled); }
        else if (stat == "watk") { newEquip.setWatk(rolled); }
        else if (stat == "matk") { newEquip.setMatk(rolled); }
    }
    newEquip.setFlag(1); // 上锁

    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), newEquip, false);

    cm.ShowWZEffect("UI/UIWindow/Quest/icon0");
    cm.sendOk("#b打造成功！获得职业经验：#r" + recipe.get("expGain") + "#k\r\n" + recipe.get("name") + " 已放入背包。");
    cm.dispose();
}
