/*
 * ==================
 * 脚本类型: 钓鱼NPC
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 消耗金币进行钓鱼
 *   2. 随机获得各种奖励
 *   3. 钓鱼次数记录
 * ==================
 */

var status = -1;
var FISHING_KEY = "fishingStats";

var fishPool = [
    { id: 2000000, name: "红色药水", qty: 10, weight: 200 },
    { id: 2000001, name: "橙色药水", qty: 5, weight: 150 },
    { id: 2000003, name: "蓝色药水", qty: 3, weight: 100 },
    { id: 4000000, name: "蜗牛壳", qty: 5, weight: 200 },
    { id: 4000001, name: "蘑菇盖", qty: 5, weight: 180 },
    { id: 4000012, name: "钢铁", qty: 1, weight: 50 },
    { id: 4000010, name: "蓝宝石", qty: 1, weight: 30 },
    { id: 4000011, name: "红宝石", qty: 1, weight: 30 },
    { id: 4000014, name: "金矿石", qty: 1, weight: 20 },
    { id: 4000015, name: "钻石", qty: 1, weight: 10 },
    { id: 4000134, name: "制作宝石", qty: 1, weight: 5 },
    { name: "10万金币", gold: 100000, weight: 10 },
    { name: "1000经验", exp: 1000, weight: 30 },
    { name: "5000经验", exp: 5000, weight: 10 },
    { name: "空钓", nothing: true, weight: 150 }
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
        var stats = getFishingStats();
        var text = "#e#b=== 钓鱼 ===#k#n\r\n\r\n";
        text += "#d" + "".padStart(26, "——") + "#k\r\n";
        text += "累计钓鱼：#b" + (stats.count || 0) + "#k 次\r\n";
        text += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";
        text += "每次钓鱼消耗 #b5000#k 金币\r\n\r\n";
        text += "#L0##b钓一次 (5000金币)#k#l\r\n";
        text += "#L1##b钓十次 (45000金币, 9折)#k#l\r\n";
        cm.sendSimple(text);
    } else if (status === 1) {
        var times = (selection === 0) ? 1 : 10;
        var cost = (selection === 0) ? 5000 : 45000;

        if (cm.getPlayer().getMeso() < cost) {
            cm.sendOk("金币不足！需要 #b" + cost.toLocaleString() + "#k 金币。");
            cm.dispose();
            return;
        }

        cm.getPlayer().gainMeso(-cost);

        var results = {};
        var totalGold = 0;
        var totalExp = 0;

        for (var i = 0; i < times; i++) {
            var prize = rollFish();
            if (prize.nothing) continue;
            if (prize.gold) {
                totalGold += prize.gold;
            } else if (prize.exp) {
                totalExp += prize.exp;
            } else if (prize.id) {
                var key = prize.id + ":" + prize.name;
                if (!results[key]) results[key] = { id: prize.id, name: prize.name, qty: 0 };
                results[key].qty += prize.qty;
            }
        }

        // 发放奖励
        for (var k in results) {
            var r = results[k];
            cm.gainItem(r.id, r.qty);
        }
        if (totalGold > 0) cm.getPlayer().gainMeso(totalGold);
        if (totalExp > 0) cm.getPlayer().gainExp(totalExp, true, true);

        // 更新统计
        var stats = getFishingStats();
        stats.count = (stats.count || 0) + times;
        saveFishingStats(stats);

        // 显示结果
        var text = "#e#b=== 钓鱼结果 ===#k#n\r\n\r\n";
        text += "钓鱼 #b" + times + "#k 次：\r\n\r\n";

        var hasResult = false;
        for (var k in results) {
            hasResult = true;
            var r = results[k];
            text += "#i" + r.id + "# #b" + r.name + "#k x" + r.qty + "\r\n";
        }
        if (totalGold > 0) {
            hasResult = true;
            text += "#b" + totalGold.toLocaleString() + "#k 金币\r\n";
        }
        if (totalExp > 0) {
            hasResult = true;
            text += "#b" + totalExp.toLocaleString() + "#k 经验\r\n";
        }
        if (!hasResult) {
            text += "#r什么都没钓到...#k\r\n";
        }

        cm.sendOk(text);
        cm.dispose();
    }
}

function rollFish() {
    var totalWeight = 0;
    for (var i = 0; i < fishPool.length; i++) {
        totalWeight += fishPool[i].weight;
    }

    var roll = Math.floor(Math.random() * totalWeight);
    var cumulative = 0;
    for (var j = 0; j < fishPool.length; j++) {
        cumulative += fishPool[j].weight;
        if (roll < cumulative) {
            return fishPool[j];
        }
    }
    return fishPool[0];
}

function getFishingStats() {
    var data = cm.getCharacterExtendValue(FISHING_KEY);
    if (data == null || data === "") return {};
    try { return JSON.parse(data); } catch (e) { return {}; }
}

function saveFishingStats(data) {
    cm.saveOrUpdateCharacterExtendValue(FISHING_KEY, JSON.stringify(data));
}
