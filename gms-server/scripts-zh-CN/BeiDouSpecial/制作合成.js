/*
 * ==================
 * 脚本类型: 制作/合成NPC
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 使用材料合成道具/装备
 *   2. 提供多种合成配方
 * ==================
 */

var status = -1;

var recipes = [
    {
        name: "药水组合包",
        desc: "包含红蓝药水各100瓶",
        materials: [
            { id: 4000000, qty: 50, name: "蜗牛壳" }
        ],
        result: { id: 2000000, qty: 100, name: "红色药水" },
        bonusResult: { id: 2000003, qty: 100, name: "蓝色药水" }
    },
    {
        name: "装备强化石",
        desc: "用于强化装备",
        materials: [
            { id: 4000012, qty: 5, name: "钢铁" },
            { id: 4000010, qty: 2, name: "蓝宝石" }
        ],
        result: { id: 4000134, qty: 1, name: "制作宝石" }
    },
    {
        name: "迷你经验药水",
        desc: "获得少量经验值",
        materials: [
            { id: 4000001, qty: 30, name: "蘑菇盖" },
            { id: 4000003, qty: 20, name: "树妖木块" }
        ],
        expReward: 5000
    },
    {
        name: "矿石精炼包",
        desc: "矿石合成更稀有矿石",
        materials: [
            { id: 4000013, qty: 3, name: "银矿石" }
        ],
        result: { id: 4000012, qty: 5, name: "钢铁" }
    },
    {
        name: "幸运礼盒",
        desc: "随机获得道具",
        materials: [
            { id: 4000000, qty: 100, name: "蜗牛壳" },
            { id: 4000001, qty: 50, name: "蘑菇盖" }
        ],
        randomResult: [
            { id: 2000006, qty: 1, name: "特殊药水" },
            { id: 4000134, qty: 1, name: "制作宝石" },
            { id: 4006001, qty: 1, name: "道具探测器" }
        ]
    },
    {
        name: "金币袋",
        desc: "材料兑换金币",
        materials: [
            { id: 4000000, qty: 100, name: "蜗牛壳" }
        ],
        mesoReward: 50000
    }
];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        cm.dispose();
        return;
    }
    if (mode === 0 && status === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var text = "#e#b=== 制作合成 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(30, "——") + "#k\r\n\r\n";
        text += "选择合成配方：\r\n\r\n";

        for (var i = 0; i < recipes.length; i++) {
            var r = recipes[i];
            text += "#L" + i + "##b" + r.name + "#k - " + r.desc + "#l\r\n";
        }

        cm.sendSimple(text);
    } else if (status === 1) {
        showRecipeDetail(selection);
    } else if (status === 2) {
        craftItem(selection);
    }
}

function showRecipeDetail(idx) {
    var r = recipes[idx];
    var text = "#e#b=== " + r.name + " ===#k#n\r\n\r\n";
    text += "描述：#b" + r.desc + "#k\r\n\r\n";
    text += "#d所需材料：#k\r\n";

    for (var i = 0; i < r.materials.length; i++) {
        var m = r.materials[i];
        var hasCount = cm.getPlayer().getItemQuantity(m.id, false);
        var color = (hasCount >= m.qty) ? "#b" : "#r";
        text += "  #i" + m.id + "# " + m.name + " x" + m.qty + " ";
        text += color + "(拥有: " + hasCount + ")#k\r\n";
    }

    text += "\r\n#d合成结果：#k\r\n";
    if (r.result) {
        text += "  #i" + r.result.id + "# #b" + r.result.name + "#k x" + r.result.qty + "\r\n";
    }
    if (r.bonusResult) {
        text += "  #i" + r.bonusResult.id + "# #b" + r.bonusResult.name + "#k x" + r.bonusResult.qty + " (额外)\r\n";
    }
    if (r.expReward) {
        text += "  #b" + r.expReward.toLocaleString() + "#k 经验\r\n";
    }
    if (r.mesoReward) {
        text += "  #b" + r.mesoReward.toLocaleString() + "#k 金币\r\n";
    }
    if (r.randomResult) {
        text += "  #b随机道具#k (共" + r.randomResult.length + "种)\r\n";
    }

    text += "\r\n#L0##b确认合成#k#l\r\n";

    cm.sendSimple(text);
}

function craftItem(idx) {
    var r = recipes[idx];

    // 检查材料
    for (var i = 0; i < r.materials.length; i++) {
        var m = r.materials[i];
        var hasCount = cm.getPlayer().getItemQuantity(m.id, false);
        if (hasCount < m.qty) {
            cm.sendOk("材料不足！#b" + m.name + "#k 需要 " + m.qty + " 个，当前拥有 " + hasCount + " 个。");
            cm.dispose();
            return;
        }
    }

    // 消耗材料
    for (var j = 0; j < r.materials.length; j++) {
        var m = r.materials[j];
        cm.gainItem(m.id, -m.qty);
    }

    // 给予结果
    if (r.result) {
        cm.gainItem(r.result.id, r.result.qty);
    }
    if (r.bonusResult) {
        cm.gainItem(r.bonusResult.id, r.bonusResult.qty);
    }
    if (r.expReward) {
        cm.getPlayer().gainExp(r.expReward, true, true);
    }
    if (r.mesoReward) {
        cm.getPlayer().gainMeso(r.mesoReward);
    }
    if (r.randomResult) {
        var randomIdx = Math.floor(Math.random() * r.randomResult.length);
        var randomItem = r.randomResult[randomIdx];
        cm.gainItem(randomItem.id, randomItem.qty);
    }

    cm.sendOk("合成成功！#b" + r.name + "#k 完成！");
    cm.dispose();
}
