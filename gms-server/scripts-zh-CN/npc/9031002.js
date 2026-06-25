/**
 * @author: 萧曵
 * @npc: 矿石系统
 * @description:
 *   矿石/宝石/锻造石 合成兑换NPC脚本，分三大类：
 *   1. 矿石合成：母矿(金属/宝石/属性) -> 高级矿/宝石/属性水晶，比例10:1
 *   2. 高级矿石合成：多种(不同种类的)高级矿/宝石各消耗1个 -> 1个 月石(金属类) 或 星石(宝石类)
 *   3. 锻造石合成：锻造石低阶 -> 高阶(如 4032169勇者之石 -> 4032171圣者之石)，比例10:1
 *   4. 勇者之石/圣者之石互换：4032169与4032171之间 1:1 双向互换，可输入数量，每个手续费5万金币
 *   所有物品ID均已核实自 wz-zh-CN/String.wz/Etc.img.xml 实际命名，未做猜测。
 *   交互流程沿用本服 物品兑换.js 的状态机模式，外层多加一层"分类选择"菜单。
 */

// ========== 1.矿石合成：母矿 -> 高级矿/宝石/属性水晶 (比例10:1) ==========
var oreSet = Array(
    // 金属类母矿 -> 高级矿 (注意：4010007锂母矿 对应 4011008锂，4011007被月石占用)
    Array(4011000, 4010000, 10),    //青铜母矿 -> 青铜
    Array(4011001, 4010001, 10),    //钢铁母矿 -> 钢铁
    Array(4011002, 4010002, 10),    //锂矿石母矿 -> 锂矿石
    Array(4011003, 4010003, 10),    //朱矿石母矿 -> 朱矿石
    Array(4011004, 4010004, 10),    //银母矿 -> 银
    Array(4011005, 4010005, 10),    //紫矿石母矿 -> 紫矿石
    Array(4011006, 4010006, 10),    //黄金母矿 -> 黄金
    //Array(4011008, 4010007, 10),    //锂母矿 -> 锂

    // 宝石类母矿 -> 高级宝石
    Array(4021000, 4020000, 10),    //石榴石母矿 -> 石榴石
    Array(4021001, 4020001, 10),    //紫水晶母矿 -> 紫水晶
    Array(4021002, 4020002, 10),    //海蓝石母矿 -> 海蓝宝石
    Array(4021003, 4020003, 10),    //祖母绿母矿 -> 祖母绿
    Array(4021004, 4020004, 10),    //蛋白石母矿 -> 蛋白石
    Array(4021005, 4020005, 10),    //蓝宝石母矿 -> 蓝宝石
    Array(4021006, 4020006, 10),    //黄晶母矿 -> 黄晶
    Array(4021007, 4020007, 10),    //钻石母矿 -> 钻石
    Array(4021008, 4020008, 10),    //黑水晶母矿 -> 黑水晶

    // 属性母矿 -> 属性水晶
    Array(4005000, 4004000, 10),    //力量母矿 -> 力量水晶
    Array(4005001, 4004001, 10),    //智慧母矿 -> 智慧水晶
    Array(4005002, 4004002, 10),    //敏捷母矿 -> 敏捷水晶
    Array(4005003, 4004003, 10),    //幸运母矿 -> 幸运水晶
    Array(4005004, 4004004, 10)     //黑暗水晶母矿 -> 黑暗水晶
);

// ========== 2.高级矿石合成：多种高级矿/宝石各1个 -> 1个月石/星石 ==========
// 注意：与第1类不同，这里不是"同一种材料10:1"，而是"每种材料各消耗1个(按合成个数倍增)"
var advancedRecipes = Array(
    {
        target: 4011007,
        name: "月石",
        materials: [4011000, 4011001, 4011002, 4011003, 4011004, 4011005, 4011006]
    },    //金属类高级矿(青铜/钢铁/锂矿石/朱矿石/银/紫矿石/黄金/锂)各1个 -> 月石
    {
        target: 4021009,
        name: "星石",
        materials: [4021000, 4021001, 4021002, 4021003, 4021004, 4021005, 4021006, 4021007, 4021008]
    }    //宝石类高级宝石(石榴石/紫水晶/海蓝宝石/祖母绿/蛋白石/蓝宝石/黄晶/钻石/黑水晶)各1个 -> 星石
);

// ========== 3.锻造石合成 (比例10:1) ==========
// 4032170贤者之石 是WZ中存在但目前未使用的中间阶，后续如需扩展可补充
// Array(4032170, 4032169, 10), //勇者之石 -> 贤者之石(预留，暂未启用)
var forgeStoneSet = Array(
    Array(4032171, 4032169, 10)    //勇者之石 -> 圣者之石(后续新增合成对照此格式补充)
);

// ========== 4.勇者之石/圣者之石互换 (1:1双向) ==========
var exchangeDirs = Array(
    Array(4032169, 4032171),    //[0] 勇者之石 -> 圣者之石
    Array(4032171, 4032169)     //[1] 圣者之石 -> 勇者之石
);

var itemSets = [oreSet, null, forgeStoneSet, null];    //category==1/3走专用逻辑，不使用此数组对应位
var categoryNames = ["矿石合成", "星月石合成", "锻造石合成", "勇者之石/圣者之石互换(1:1)"];
// 各分类每个合成/互换单位收取的金币手续费(实际收取 = 该值 * 数量)
var categoryFee = [100000, 500000, 500000, 50000];

var status = 0;
var category = -1;
var selectedItem;
var item;
var req;
var co;
var qty;
var cost;
var fee;
var advRecipe;    //category==1时使用：当前选中的多材料合成配方
var exchangeFrom;    //category==3时使用：互换的源物品ID
var exchangeTo;      //category==3时使用：互换的目标物品ID

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    status++;
    if (mode == -1) {
        cm.dispose();
        return;
    } else if (mode == 0) {
        cm.dispose();
        return;
    }

    if (status == 1) {
        // 第一层：选择合成分类
        var add = "请选择你想进行的矿石合成类型\r\n";
        for (var i = 0; i < categoryNames.length; i++) {
            add += "\r\n#L" + i + "##b" + categoryNames[i] + "#k#l";
        }
        cm.sendSimple(add);
    } else if (status == 2) {
        // 第二层：选择该分类下的具体合成项
        category = selection;
        if (category == 1) {
            var add = "请选择你想合成的高级矿石/宝石类型\r\n";
            for (var i = 0; i < advancedRecipes.length; i++) {
                var r = advancedRecipes[i];
                add += "\r\n#L" + i + "##v " + r.target + "##z" + r.target + "#\t需要 " + r.materials.length + " 种材料各1个#l";
            }
            cm.sendSimple(add);
        } else if (category == 3) {
            var add3 = "请选择互换方向(1:1)\r\n";
            for (var d = 0; d < exchangeDirs.length; d++) {
                add3 += "\r\n#L" + d + "##v " + exchangeDirs[d][0] + "# -> #v " + exchangeDirs[d][1] + "##l";
            }
            cm.sendSimple(add3);
        } else {
            var set = itemSets[category];
            var add2 = "请选择你想合成的物品\r\n";
            for (var j = 0; j < set.length; j++) {
                add2 += "\r\n#L" + j + "##v " + set[j][0] + "##z";
                add2 += set[j][0] + "#\t需要材料:#v " + set[j][1] + "#";
            }
            cm.sendSimple(add2);
        }
    } else if (status == 3) {
        selectedItem = selection;
        if (category == 1) {
            advRecipe = advancedRecipes[selectedItem];
            var feePerUnit1 = categoryFee[category];
            var bdd1 = "你确定要合成 #t" + advRecipe.target + "# 吗？\r\n\r\n";
            bdd1 += "需要以下材料各1个(实际消耗按合成个数倍增):\r\n";
            for (var k = 0; k < advRecipe.materials.length; k++) {
                bdd1 += "#v " + advRecipe.materials[k] + "#  ";
            }
            bdd1 += "\r\n\r\n每个合成手续费:#r " + feePerUnit1 + " 金币#k(实际收取按合成个数计算)\r\n\r\n\r\n";
            bdd1 += "请输入合成个数\r\n";
            cm.sendGetNumber(bdd1, 1, 1, 999);
        } else if (category == 3) {
            exchangeFrom = exchangeDirs[selectedItem][0];
            exchangeTo = exchangeDirs[selectedItem][1];
            var feePerUnit3 = categoryFee[category];
            var bdd3 = "你确定要将 #t" + exchangeFrom + "# 互换为 #t" + exchangeTo + "# 吗？(1:1)\r\n\r\n";
            bdd3 += "每个互换手续费:#r " + feePerUnit3 + " 金币#k(实际收取按数量计算)\r\n\r\n\r\n";
            bdd3 += "请输入互换数量\r\n";
            cm.sendGetNumber(bdd3, 1, 1, 999);
        } else {
            var set2 = itemSets[category];
            item = set2[selectedItem][0];
            req = set2[selectedItem][1];
            co = set2[selectedItem][2];
            var feePerUnit2 = categoryFee[category];
            var bdd2 = "你确定要合成\r\n";
            bdd2 += "\r\n#i" + item + "# #t" + item + "#";
            bdd2 += "    需要材料:#v " + req + "#\r\n\r\n";
            bdd2 += "单个物品需要材料个数:#r " + co + "个\r\n\r\n";
            bdd2 += "每个合成手续费:#r " + feePerUnit2 + " 金币#k(实际收取按合成个数计算)\r\n\r\n\r\n";
            bdd2 += "请输入合成个数\r\n";
            cm.sendGetNumber(bdd2, 1, 1, 999);
        }
    } else if (status == 4) {
        qty = (selection > 0) ? selection : (selection < 0 ? -selection : 1);
        fee = categoryFee[category] * qty;    //手续费按合成个数计算
        if (category == 1) {
            var lackMaterial = -1;
            for (var m = 0; m < advRecipe.materials.length; m++) {
                if (!cm.haveItem(advRecipe.materials[m], qty)) {
                    lackMaterial = advRecipe.materials[m];
                    break;
                }
            }
            if (lackMaterial != -1) {
                cm.sendOk("#b您的材料不足：#t" + lackMaterial + "#");
                cm.dispose();
            } else if (cm.getMeso() < fee) {
                cm.sendOk("#b您的金币不足，无法支付手续费(" + fee + ")");
                cm.dispose();
            } else {
                var confirmMsg1 = "你确定要合成 #b" + qty + " 个#k #t" + advRecipe.target + "# 吗？\r\n";
                confirmMsg1 += "将消耗以下材料各 #b" + qty + " 个#k：\r\n";
                for (var n = 0; n < advRecipe.materials.length; n++) {
                    confirmMsg1 += "#v " + advRecipe.materials[n] + "#  ";
                }
                confirmMsg1 += "\r\n并支付手续费 #b" + fee + " 金币#k。";
                cm.sendYesNo(confirmMsg1);
            }
        } else if (category == 3) {
            if (!cm.haveItem(exchangeFrom, qty)) {
                cm.sendOk("#b您的材料不足：#t" + exchangeFrom + "#");
                cm.dispose();
            } else if (cm.getMeso() < fee) {
                cm.sendOk("#b您的金币不足，无法支付手续费(" + fee + ")");
                cm.dispose();
            } else {
                var confirmMsg3 = "你确定要将 #b" + qty + " 个#k #t" + exchangeFrom + "# 互换为 #b" + qty + " 个#k #t" + exchangeTo + "# 吗？\r\n";
                confirmMsg3 += "并支付手续费 #b" + fee + " 金币#k。";
                cm.sendYesNo(confirmMsg3);
            }
        } else {
            cost = co * qty;    //花费为单价*数量
            if (!cm.haveItem(req, cost)) {
                cm.sendOk("#b您的材料不足");
                cm.dispose();
            } else if (cm.getMeso() < fee) {
                cm.sendOk("#b您的金币不足，无法支付手续费(" + fee + ")");
                cm.dispose();
            } else {
                var confirmMsg2 = "你确定要合成 #b" + qty + " 个#k #t" + item + "# 吗？\r\n";
                confirmMsg2 += "将消耗 #v" + req + "# #b" + cost + " 个#k\r\n";
                confirmMsg2 += "并支付手续费 #b" + fee + " 金币#k。";
                cm.sendYesNo(confirmMsg2);
            }
        }
    } else if (status == 5) {
        if (category == 1) {
            for (var p = 0; p < advRecipe.materials.length; p++) {
                cm.gainItem(advRecipe.materials[p], -qty);
            }
            cm.gainItem(advRecipe.target, qty);
        } else if (category == 3) {
            cm.gainItem(exchangeFrom, -qty);
            cm.gainItem(exchangeTo, qty);
        } else {
            cm.gainItem(req, -cost);
            cm.gainItem(item, qty);
        }
        cm.gainMeso(-fee);
        cm.sendOk("#b合成成功！");
        cm.dispose();
    }
}
