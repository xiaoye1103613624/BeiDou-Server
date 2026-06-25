/**
 * @author: 萧曵
 * @npc: 基纽 - 体能与象征训练 (9031015)
 * @description:
 *   1. 象征强化：消耗 扎昆/帕普拉图斯/皮亚努斯 的象征(各限用1次)，永久增加固定点数MaxHP。
 *   2. 体能强化：消耗 黄金枫叶(可配合金币/点券) 持续增加MaxHP或MaxMP，每次+10点，
 *      MaxHP、MaxMP各自最多强化999次(与图中"上限限制：增加次数(MaxHP):999/增加次数(MaxMP):999"一致)。
 *
 *   说明：
 *   - 象征/体能强化增加的是真实的MaxHP/MaxMP(调用Character.assignHP/assignMP(delta,0)，
 *     deltaAp传0表示不占用/消耗角色的AP点数，仅是单纯的数值永久叠加)。
 *   - 次数限制使用 cm.getQuestRecord(questId).getCustomData() 作为持久化计数器(不随天重置，
 *     与bosslog每日表不同)，写法沿用本服 2101014.js/9001000.js 等脚本里通用积分/次数记录的既有约定。
 *     这里选用的 questId(161200~161204) 仅作为存储用的Key，不对应任何真实任务。
 *   - 图中"黄金枫叶"暂以 4000313 替代(本服现成的"黄金枫叶兑换"道具，参考 枫叶兑换.js)。
 *   - 三个象征道具ID：4001083(扎昆的象征) / 4001084(帕普拉图斯的象征) / 4001085(皮亚努斯的象征)。
 *   - 象征限制：每个角色每种象征#r限用1次#k(用完后剩余0次，与图二"剩余0次"一致)。
 */

// ========== 象征强化数据(各限用1次) ==========
var symbols = [
    { id: 4001083, name: "扎昆的象征", hp: 150, quest: 161200 },
    { id: 4001084, name: "帕普拉图斯的象征", hp: 100, quest: 161201 },
    { id: 4001085, name: "皮亚努斯的象征", hp: 100, quest: 161202 }
];

// ========== 体能强化数据(MaxHP/MaxMP各限999次) ==========
var goldMapleLeaf = 4000313;    //黄金枫叶(替代道具)
var trainMaxCount = 999;        //每项(MaxHP/MaxMP)最多可强化次数，与图中"999"一致
var trainGainPerUse = 10;       //每次成功强化增加的点数，与图中"每次增加数值10点"一致
var trainQuestId = [161203, 161204];    //[0]=MaxHP强化次数记录，[1]=MaxMP强化次数记录

// 6个可选方案 = 3种提交材料 × 2种属性(MaxHP/MaxMP)，与图三逐条对应
var trainOptions = [
    { statType: 0, qty: 10, meso: 0, cash: 0, rate: 0.2, desc: "提交#i" + goldMapleLeaf + "#x10 增加MaxHP，20%成功率" },
    { statType: 1, qty: 10, meso: 0, cash: 0, rate: 0.2, desc: "提交#i" + goldMapleLeaf + "#x10 增加MaxMP，20%成功率" },
    { statType: 0, qty: 100, meso: 20000000, cash: 0, rate: 0.5, desc: "提交#i" + goldMapleLeaf + "#x100和2000万金币 增加MaxHP，50%成功率" },
    { statType: 1, qty: 100, meso: 20000000, cash: 0, rate: 0.5, desc: "提交#i" + goldMapleLeaf + "#x100和2000万金币 增加MaxMP，50%成功率" },
    { statType: 0, qty: 10, meso: 0, cash: 1999, rate: 1.0, desc: "提交#i" + goldMapleLeaf + "#x10和1999点券 增加MaxHP，100%成功率" },
    { statType: 1, qty: 10, meso: 0, cash: 1999, rate: 1.0, desc: "提交#i" + goldMapleLeaf + "#x10和1999点券 增加MaxMP，100%成功率" }
];

var status = 0;
var pendingSymbol = -1;
var pendingOption = -1;

function start() {
    status = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1 || mode == 0) {
        cm.dispose();
        return;
    }

    if (status == 0) {
        var add = "使用下列道具可以为角色增长最大生命值，不过每种道具都限制了使用的上限。\r\n\r\n";
        add += "#L0#查看详细(本职业)#l\r\n";
        add += "#L1#使用扎昆的象征进行血量增长#l\r\n";
        add += "#L2#使用帕普拉图斯的象征进行血量增长#l\r\n";
        add += "#L3#使用皮亚努斯的象征进行血量增长#l\r\n";
        add += "#L4#体能强化(持续消耗材料增加MaxHP/MaxMP)#l";
        status = 1;
        cm.sendSimple(add);
    } else if (status == 1) {
        if (selection == 0) {
            // 查看详细(本职业)：逐项列出象征剩余次数 + 体能强化已用/上限次数
            var info = "#e本职业强化记录#n\r\n\r\n";
            for (var i = 0; i < symbols.length; i++) {
                var used = cm.getQuestRecord(symbols[i].quest).getCustomData();
                info += symbols[i].name + "  增加" + symbols[i].hp + "HP，剩余" + (used == "1" ? "0" : "1") + "次\r\n";
            }
            var hpUsed = parseInt(cm.getQuestRecord(trainQuestId[0]).getCustomData() || "0");
            var mpUsed = parseInt(cm.getQuestRecord(trainQuestId[1]).getCustomData() || "0");
            info += "\r\n上限限制：\r\n";
            info += "增加次数(MaxHP):" + trainMaxCount + " / " + hpUsed + "\r\n";
            info += "增加次数(MaxMP):" + trainMaxCount + " / " + mpUsed + "\r\n";
            cm.sendOk(info);
            cm.dispose();
            return;
        } else if (selection >= 1 && selection <= 3) {
            // 选择某个象征
            var sym = symbols[selection - 1];
            var used = cm.getQuestRecord(sym.quest).getCustomData();
            if (used == "1") {
                cm.sendOk("#r" + sym.name + "#k 已经使用过了，每个角色限用1次，剩余0次。");
                cm.dispose();
                return;
            }
            if (!cm.haveItem(sym.id, 1)) {
                cm.sendOk("您没有 #t" + sym.id + "#，无法进行强化。");
                cm.dispose();
                return;
            }
            pendingSymbol = selection - 1;
            status = 10;
            cm.sendYesNo("确定要消耗 #t" + sym.id + "# x1，永久增加 #r" + sym.hp + "点#k 最大生命值吗？\r\n(#r每个角色限用1次#k，使用后剩余0次)");
        } else if (selection == 4) {
            // 体能强化：直接列出全部6个方案，并展示上限限制(与图三一致)
            var hpUsed2 = parseInt(cm.getQuestRecord(trainQuestId[0]).getCustomData() || "0");
            var mpUsed2 = parseInt(cm.getQuestRecord(trainQuestId[1]).getCustomData() || "0");
            var add2 = "如果你能给我特殊的材料，我可以为你进行体能训练，增加最大生命值或者最大法力值，不过每天都是有上限的，不然过度生长不行，每次增加数值" + trainGainPerUse + "点。\r\n\r\n";
            add2 += "上限限制：\r\n";
            add2 += "增加次数(MaxHP):" + trainMaxCount + " / " + hpUsed2 + "\r\n";
            add2 += "增加次数(MaxMP):" + trainMaxCount + " / " + mpUsed2 + "\r\n\r\n";
            for (var i = 0; i < trainOptions.length; i++) {
                add2 += "#L" + i + "#" + trainOptions[i].desc + "#l\r\n";
            }
            status = 21;
            cm.sendSimple(add2);
        }
    } else if (status == 10) {
        // 象征强化确认结果
        var sym = symbols[pendingSymbol];
        if (mode == 1) {
            cm.gainItem(sym.id, -1);
            cm.getPlayer().assignHP(sym.hp, 0);    //deltaAp传0，不占用角色AP点数，仅永久叠加MaxHP
            cm.getQuestRecord(sym.quest).setCustomData("1");
            cm.sendOk("合成成功，最大生命值永久增加了 #r" + sym.hp + "点#k！");
        } else {
            cm.sendOk("已取消。");
        }
        cm.dispose();
    } else if (status == 21) {
        // 选择具体提交方案
        var opt = trainOptions[selection];
        var label = opt.statType == 0 ? "MaxHP" : "MaxMP";
        var usedCount = parseInt(cm.getQuestRecord(trainQuestId[opt.statType]).getCustomData() || "0");

        if (usedCount >= trainMaxCount) {
            cm.sendOk("#r" + label + "#k 强化次数已达上限(" + trainMaxCount + "次)，无法继续强化。");
            cm.dispose();
            return;
        }
        if (!cm.haveItem(goldMapleLeaf, opt.qty)) {
            cm.sendOk("您的 #t" + goldMapleLeaf + "# 数量不足，需要" + opt.qty + "个。");
            cm.dispose();
            return;
        }
        if (opt.meso > 0 && cm.getMeso() < opt.meso) {
            cm.sendOk("您的金币不足，需要" + opt.meso + "金币。");
            cm.dispose();
            return;
        }
        if (opt.cash > 0 && cm.getPlayer().getCashShop().getCash(1) < opt.cash) {
            cm.sendOk("您的点券不足，需要" + opt.cash + "点券。");
            cm.dispose();
            return;
        }

        pendingOption = selection;
        status = 22;
        var confirmMsg = "确定要" + opt.desc + "吗？\r\n";
        confirmMsg += "当前" + label + "强化次数：" + usedCount + " / " + trainMaxCount + "\r\n";
        confirmMsg += "失败则材料/金币/点券照常扣除，但不会增加属性。";
        cm.sendYesNo(confirmMsg);
    } else if (status == 22) {
        // 体能强化执行结果
        if (mode == 1) {
            var opt = trainOptions[pendingOption];
            var label = opt.statType == 0 ? "MaxHP" : "MaxMP";

            cm.gainItem(goldMapleLeaf, -opt.qty);
            if (opt.meso > 0) {
                cm.gainMeso(-opt.meso);
            }
            if (opt.cash > 0) {
                cm.gainNX(-opt.cash);
            }

            if (Math.random() < opt.rate) {
                if (opt.statType == 0) {
                    cm.getPlayer().assignHP(trainGainPerUse, 0);
                } else {
                    cm.getPlayer().assignMP(trainGainPerUse, 0);
                }
                var usedCount = parseInt(cm.getQuestRecord(trainQuestId[opt.statType]).getCustomData() || "0");
                cm.getQuestRecord(trainQuestId[opt.statType]).setCustomData("" + (usedCount + 1));
                cm.sendOk("恭喜，强化成功！" + label + " 永久增加了 #r" + trainGainPerUse + "点#k！(已用次数：" + (usedCount + 1) + "/" + trainMaxCount + ")");
            } else {
                cm.sendOk("很遗憾，本次强化失败了，材料已消耗。");
            }
        } else {
            cm.sendOk("已取消。");
        }
        cm.dispose();
    }
}
