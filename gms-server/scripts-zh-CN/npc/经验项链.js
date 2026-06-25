/**
 * @author: 萧曵
 * @npc: 经验项链(阿里山守护者) 制作 & 升级 & 戒指制作
 * @description:
 *   由 9031003.js 通过 cm.openNpc(9031003, "经验项链") 跳转进入。
 *   截图"7、项链升级(经验项链)"官方菜单共3项：制作项链/升级项链/制作戒指，本脚本已对齐为3项菜单。
 *   1. 制作：消耗 蓝色蜗牛壳x50 + 红色蜗牛壳x50 + 绿色蜗牛壳x50 + 逆袭银币x10，制作出"阿里山守护者(经验项链)"(1122311)。
 *      物品ID已核实自 wz-zh-CN/String.wz/Etc.img.xml(蜗牛壳类)与Eqp.img.xml(1122311 阿里山守护者项链)。
 *      "逆袭银币"未在现有WZ/脚本中找到对应实际物品，itemId暂留TODO占位，需用户确认。
 *   2. 升级：读取背包装备栏第1格当前装备(经验项链)，消耗材料+金币后提升等级，增加四维(STR/DEX/INT/LUK)和攻击/魔攻。
 *      等级保存在装备自身的 level 字段(沿用本服"砸卷次数"复用同一字段的做法，此处含义改为"经验项链强化等级")。
 *      参考截图：4级时四维均+10、攻击力+10、魔攻+10，但每级具体消耗材料/数量、最大等级均未标注，
 *      暂用 TODO 占位 levelUpConfig，按用户校正前不影响脚本运行(只是占位数值)。
 *   3. 制作戒指：截图仅给出菜单文案"制作戒指 | 消耗材料制作强力的戒指"，未提供具体戒指物品ID/材料/数量，
 *      暂用 ringItemId=0 + 空材料数组占位(与"逆袭银币"同一处理方式：未配置时提示用户尚不可用)，
 *      待用户提供实际戒指物品ID及材料数据后回填，不影响脚本运行。
 */

var expNecklaceId = 1122311; // 阿里山守护者(经验项链)

// ========== 1.制作所需材料 ==========
var blueSnailShell = 4000000;  // 蓝色蜗牛壳
var redSnailShell = 4000016;   // 红色蜗牛壳
var greenSnailShell = 4000019; // 绿色蜗牛壳
var reverseSilverCoin = 0;     // TODO: "逆袭银币"实际物品ID未确认，暂占位为0(为0时制作功能会提示未配置)

var craftMaterials = [
    [blueSnailShell, 50],
    [redSnailShell, 50],
    [greenSnailShell, 50],
    [reverseSilverCoin, 10]
];

// ========== 2.升级所需材料(每级独立配置，TODO占位) ==========
// gain格式：{str, dex, int_, luk, watk, matk}；截图仅能确认4级时四维/攻击/魔攻均达到+10的最终结果，
// 无法确认每级独立增量与所需材料数量，暂按"每级四维各+2、攻击+2、魔攻+2"线性占位(4级正好达到+8，与截图+10有偏差，需用户校正)
var maxLevel = 10; // TODO: 最大等级未知，暂占位10级
var levelUpConfig = [];
for (var lv = 0; lv < maxLevel; lv++) {
    levelUpConfig.push({
        materials: [[reverseSilverCoin, 5 * (lv + 1)]], // TODO: 每级具体材料未知，暂占位为逆袭银币递增消耗
        gold: 1000000 * (lv + 1), // TODO
        gain: { str: 2, dex: 2, int_: 2, luk: 2, watk: 2, matk: 2 } // TODO
    });
}

// ========== 3.制作戒指(TODO占位，截图未提供具体物品ID/材料) ==========
var ringItemId = 0; // TODO: "强力的戒指"实际物品ID未确认，暂占位为0(为0时制作功能会提示未配置)
var ringCraftMaterials = []; // TODO: 戒指制作所需材料未知，暂留空数组

var status = 0;
var menu = -1; // 1=制作项链 2=升级项链 3=制作戒指

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }
    status++;

    if (status == 1) {
        var add = "欢迎来到阿里山守护者(经验项链)工坊\r\n";
        add += "\r\n#L1##b制作项链#k#l(消耗材料制作经验项链)";
        add += "\r\n#L2##b升级项链#k#l(消耗材料升级经验项链)";
        add += "\r\n#L3##b制作戒指#k#l(消耗材料制作强力的戒指)";
        cm.sendSimple(add);
    } else if (status == 2) {
        menu = selection;
        if (menu == 1) {
            执行制作确认();
        } else if (menu == 2) {
            执行升级确认();
        } else if (menu == 3) {
            执行戒指制作确认();
        }
    } else if (status == 3 && menu == 1 && type == 1 && selection == 1) {
        执行制作();
    } else if (status == 3 && menu == 2 && type == 1 && selection == 1) {
        执行升级();
    } else if (status == 3 && menu == 3 && type == 1 && selection == 1) {
        执行戒指制作();
    } else {
        cm.dispose();
    }
}

function 执行制作确认() {
    if (reverseSilverCoin <= 0) {
        cm.sendOk("#b制作所需材料\"逆袭银币\"尚未配置实际物品ID(TODO占位)，暂无法制作！");
        cm.dispose();
        return;
    }
    if (cm.haveItem(expNecklaceId, 1)) {
        cm.sendOk("#b你已经拥有经验项链，无需重复制作！");
        cm.dispose();
        return;
    }
    var add = "制作 #t" + expNecklaceId + "# 需要以下材料：\r\n\r\n";
    for (var i = 0; i < craftMaterials.length; i++) {
        add += "#v " + craftMaterials[i][0] + "# x" + craftMaterials[i][1] + "\r\n";
    }
    add += "\r\n是否确认制作？";
    cm.sendYesNo(add);
}

function 执行制作() {
    for (var i = 0; i < craftMaterials.length; i++) {
        if (!cm.haveItem(craftMaterials[i][0], craftMaterials[i][1])) {
            cm.sendOk("#b材料不足，无法制作！");
            cm.dispose();
            return;
        }
    }
    for (var j = 0; j < craftMaterials.length; j++) {
        cm.gainItem(craftMaterials[j][0], -craftMaterials[j][1]);
    }
    cm.gainItem(expNecklaceId, 1);
    cm.ShowWZEffect("UI/UIWindow/Quest/icon0");
    cm.sendOk("#b制作成功！经验项链已放入背包。");
    cm.dispose();
}

function 执行戒指制作确认() {
    if (ringItemId <= 0 || ringCraftMaterials.length == 0) {
        cm.sendOk("#b制作戒指所需的物品ID/材料数据尚未配置(TODO占位)，暂无法制作！");
        cm.dispose();
        return;
    }
    var add = "制作 #t" + ringItemId + "# 需要以下材料：\r\n\r\n";
    for (var i = 0; i < ringCraftMaterials.length; i++) {
        add += "#v " + ringCraftMaterials[i][0] + "# x" + ringCraftMaterials[i][1] + "\r\n";
    }
    add += "\r\n是否确认制作？";
    cm.sendYesNo(add);
}

function 执行戒指制作() {
    if (ringItemId <= 0 || ringCraftMaterials.length == 0) {
        cm.sendOk("#b制作戒指所需的物品ID/材料数据尚未配置(TODO占位)，制作已取消！");
        cm.dispose();
        return;
    }
    for (var i = 0; i < ringCraftMaterials.length; i++) {
        if (!cm.haveItem(ringCraftMaterials[i][0], ringCraftMaterials[i][1])) {
            cm.sendOk("#b材料不足，无法制作！");
            cm.dispose();
            return;
        }
    }
    for (var j = 0; j < ringCraftMaterials.length; j++) {
        cm.gainItem(ringCraftMaterials[j][0], -ringCraftMaterials[j][1]);
    }
    cm.gainItem(ringItemId, 1);
    cm.ShowWZEffect("UI/UIWindow/Quest/icon0");
    cm.sendOk("#b制作成功！戒指已放入背包。");
    cm.dispose();
}

function 执行升级确认() {
    var necklace = cm.getInventory(1).getItem(1); // 装备栏第1格
    if (necklace == null || necklace.getItemId() != expNecklaceId) {
        cm.sendOk("#b请将经验项链放置在背包装备栏第1格后再来升级！");
        cm.dispose();
        return;
    }
    var curLevel = necklace.getLevel(); // 复用level字段记录经验项链强化等级
    if (curLevel >= maxLevel) {
        cm.sendOk("#b经验项链已达到最高等级(" + maxLevel + ")！");
        cm.dispose();
        return;
    }
    var next = levelUpConfig[curLevel];
    var add = "当前等级：#r" + curLevel + "#k\r\n下一级需要消耗：\r\n";
    for (var i = 0; i < next.materials.length; i++) {
        add += "#v " + next.materials[i][0] + "# x" + next.materials[i][1] + "\r\n";
    }
    add += "金币 #r" + next.gold + "#k\r\n\r\n是否确认升级？";
    cm.sendYesNo(add);
}

function 执行升级() {
    var necklace = cm.getInventory(1).getItem(1);
    if (necklace == null || necklace.getItemId() != expNecklaceId) {
        cm.sendOk("#b经验项链不在装备栏第1格，升级已取消！");
        cm.dispose();
        return;
    }
    var curLevel = necklace.getLevel();
    var next = levelUpConfig[curLevel];
    for (var i = 0; i < next.materials.length; i++) {
        if (!cm.haveItem(next.materials[i][0], next.materials[i][1])) {
            cm.sendOk("#b材料不足，升级失败！");
            cm.dispose();
            return;
        }
    }
    if (cm.getMeso() < next.gold) {
        cm.sendOk("#b金币不足，升级失败！");
        cm.dispose();
        return;
    }

    for (var j = 0; j < next.materials.length; j++) {
        cm.gainItem(next.materials[j][0], -next.materials[j][1]);
    }
    cm.gainMeso(-next.gold);

    var newNecklace = necklace.copy();
    newNecklace.setStr((newNecklace.getStr() + next.gain.str) & 0xFFFF);
    newNecklace.setDex((newNecklace.getDex() + next.gain.dex) & 0xFFFF);
    newNecklace.setInt((newNecklace.getInt() + next.gain.int_) & 0xFFFF);
    newNecklace.setLuk((newNecklace.getLuk() + next.gain.luk) & 0xFFFF);
    newNecklace.setWatk((newNecklace.getWatk() + next.gain.watk) & 0xFFFF);
    newNecklace.setMatk((newNecklace.getMatk() + next.gain.matk) & 0xFFFF);
    newNecklace.setLevel(curLevel + 1); // 升级后等级+1，复用level字段
    newNecklace.setFlag(1); // 上锁

    Packages.server.MapleInventoryManipulator.removeFromSlot(cm.getC(), Packages.client.inventory.MapleInventoryType.EQUIP, 1, 1, false);
    Packages.server.MapleInventoryManipulator.addFromDrop(cm.getC(), newNecklace, false);

    cm.ShowWZEffect("UI/UIWindow/Quest/icon0");
    cm.sendOk("#b升级成功！当前等级：" + (curLevel + 1));
    cm.dispose();
}
