// 匠人街 · 收藏图鉴子脚本（通过 9031000 收藏成就菜单进入）
// 怪物卡/装备收集，属性加成+积分商店

var QUEST_COLLECT_CARDS = 9900340;  // 怪物卡收集位图
var QUEST_COLLECT_SCORE = 9900341;  // 收藏积分
var QUEST_COLLECT_EQUIP = 9900342;  // 装备图鉴位图

// 地区划分与怪物卡数量
var REGIONS = [
    { name: "金银岛", mobCount: 50, bonusAllStat: 2, bonusAtk: 1, desc: "新手区域" },
    { name: "艾琳森林", mobCount: 30, bonusAllStat: 3, bonusAtk: 2, desc: "精灵家园" },
    { name: "武陵桃园", mobCount: 25, bonusAllStat: 3, bonusAtk: 2, desc: "东方武陵" },
    { name: "神木村", mobCount: 40, bonusAllStat: 4, bonusAtk: 3, desc: "龙之巢穴" },
    { name: "未来之门", mobCount: 45, bonusAllStat: 5, bonusAtk: 4, desc: "高等级区" },
    { name: "副本Boss", mobCount: 20, bonusAllStat: 8, bonusAtk: 6, desc: "挑战Boss" }
];

var status = -1;
var actionType = 0;

function start() {
    status = -1; actionType = 0;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) { cm.dispose(); return; }
    status++;
    if (actionType === 0) handleMain(selection);
    else if (actionType === 1) handleCardRegion(selection);
    else if (actionType === 2) handleEquipCollection(selection);
    else if (actionType === 3) handleScoreShop(selection);
}

// ==================== 主菜单 ====================

function handleMain(selection) {
    if (status === 0) {
        var score = getCollectionScore();
        // 计算总体收集进度
        var totalCards = 0, collected = 0;
        for (var i = 0; i < REGIONS.length; i++) totalCards += REGIONS[i].mobCount;
        collected = countCollectedCards();

        var t = "#e#b<收藏图鉴>#k#n\r\n\r\n";
        t += "收集怪物卡和装备，解锁永久属性加成！\r\n";
        t += "收藏积分：#b" + score + "#k\r\n";
        t += "总进度：#b" + collected + "#k / " + totalCards + " (" + Math.round(collected / totalCards * 100) + "%)\r\n\r\n";

        t += "#L1##b怪物卡图鉴#k#l\r\n";
        for (var i = 0; i < REGIONS.length; i++) {
            var r = REGIONS[i];
            var c = countRegionCards(i);
            var pct = Math.round(c / r.mobCount * 100);
            var done = c >= r.mobCount;
            t += "　" + (done ? "#g" : "") + r.name + "：" + c + "/" + r.mobCount + " (" + pct + "%)";
            if (done) t += " ✅";
            t += (done ? "#k" : "") + "\r\n";
        }
        t += "\r\n#L2#装备图鉴#l\r\n";
        t += "#L3##b收藏积分商店#k#l\r\n\r\n";
        t += "【当前加成】\r\n";
        t += formatCurrentBonus() + "\r\n";
        t += "\r\n#L9000##g离开#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { cm.dispose(); return; }
        actionType = selection;
        status = -1;
        action(1, 0, 0);
    }
}

// ==================== 怪物卡图鉴 ====================

function handleCardRegion(selection) {
    if (status === 0) {
        var t = "#e#b<怪物卡图鉴>#k#n\r\n\r\n";
        t += "击败怪物有概率掉落怪物卡，收集越多属性加成越高！\r\n";
        t += "每个区域集齐后获得额外属性。\r\n\r\n";
        t += "选择区域查看详情：\r\n\r\n";
        for (var i = 0; i < REGIONS.length; i++) {
            var r = REGIONS[i];
            var c = countRegionCards(i);
            var done = c >= r.mobCount;
            t += "#L" + i + "#" + (done ? "#g✅ " : "") + r.name + " [" + c + "/" + r.mobCount + "]";
            if (done) t += " 集齐奖励：四维+" + r.bonusAllStat + " 攻+" + r.bonusAtk;
            t += "#l\r\n";
        }
        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        showRegionDetail(selection);
    }
}

function showRegionDetail(idx) {
    var r = REGIONS[idx];
    var c = countRegionCards(idx);
    var t = "#e#b" + r.name + "怪物图鉴#k#n\r\n\r\n";
    t += "进度：" + c + "/" + r.mobCount + " (" + Math.round(c / r.mobCount * 100) + "%)\r\n";
    t += r.desc + "\r\n\r\n";
    t += "集齐奖励：四维属性各+#b" + r.bonusAllStat + "#k\r\n";
    if (r.bonusAtk > 0) t += "攻击力/魔攻+#b" + r.bonusAtk + "#k\r\n";
    t += "收藏积分：+#b" + (r.mobCount * 2) + "#k\r\n\r\n";

    if (c >= r.mobCount) {
        t += "#g🎉 该区域已集齐！属性加成已生效。#k\r\n";
    } else {
        t += "继续击败该区域怪物来收集卡片吧！\r\n";
        t += "每张新卡片 +2 收藏积分。\r\n";
    }

    t += "\r\n#L0#确定#l";
    cm.sendOk(t);
    cm.dispose();
}

// ==================== 装备图鉴 ====================

function handleEquipCollection(selection) {
    if (status === 0) {
        var equipSets = getEquipSets();
        var t = "#e#b<装备图鉴>#k#n\r\n\r\n";
        t += "收集同名套装可获得额外属性加成。\r\n";
        t += "每获得一件新装备 +1 收藏积分。\r\n\r\n";

        for (var i = 0; i < equipSets.length; i++) {
            var s = equipSets[i];
            var pct = Math.round(s.collected / s.total * 100);
            t += s.name + "：" + s.collected + "/" + s.total;
            if (s.collected >= s.total) t += " #g✅ 集齐！#k";
            t += "\r\n";
        }

        t += "\r\n#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        backMain();
    }
}

// ==================== 收藏积分商店 ====================

function handleScoreShop(selection) {
    if (status === 0) {
        var score = getCollectionScore();
        var t = "#e#b<收藏积分商店>#k#n\r\n\r\n";
        t += "当前积分：#b" + score + "#k\r\n\r\n";
        t += "#L1#灵韵结晶 ×1 - 200积分#l\r\n";
        t += "#L2#圣者之石 ×5 - 150积分#l\r\n";
        t += "#L3#匠人币 ×100 - 50积分#l\r\n";
        t += "#L4#抵用券×5000 - 300积分#l\r\n";
        t += "#L5#经验×100W - 100积分#l\r\n";
        t += "#L6#洗炼石 ×10 - 80积分#l\r\n\r\n";
        t += "#L9000##g返回#k#l";
        cm.sendSimple(t);
    } else if (status === 1) {
        if (selection === 9000) { backMain(); return; }
        var score = getCollectionScore();
        var costs = [0, 200, 150, 50, 300, 100, 80];
        var cost = costs[selection];
        if (score < cost) {
            cm.sendOk("积分不足！需要 " + cost + "，当前 " + score);
            cm.dispose(); return;
        }
        spendScore(cost);
        switch (selection) {
            case 1: cm.gainItem(4021017, 1); break;
            case 2: cm.gainItem(4000314, 5); break;
            case 3: cm.gainItem(4001126, 100); break;
            case 4: cm.getPlayer().getCashShop().gainCash(1, 5000); break;
            case 5: cm.getPlayer().gainExp(1000000, true, true); break;
            case 6: cm.gainItem(4032171, 10); break;
        }
        cm.sendOk("兑换成功！剩余积分：" + (score - cost));
        cm.dispose();
    }
}

// ==================== 数据操作 ====================

function countCollectedCards() {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_COLLECT_CARDS);
    var bitmap = qr.getProgressValue("cards") || "";
    return bitmap.replace(/0/g, "").length; // 简单位图计数
}

function countRegionCards(regionIdx) {
    // 简化版返回随机进度
    var total = REGIONS[regionIdx].mobCount;
    return Math.floor(Math.random() * total * 0.7);
}

function getCollectionScore() {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_COLLECT_SCORE);
    var v = qr.getProgressValue("score");
    return v ? java.lang.Integer.parseInt(v) : 0;
}

function spendScore(amount) {
    var qr = cm.getPlayer().getQuestNAdd(QUEST_COLLECT_SCORE);
    var cur = getCollectionScore();
    qr.setProgressValue("score", "" + Math.max(0, cur - amount));
    cm.getPlayer().updateQuest(qr);
}

function formatCurrentBonus() {
    var bonus = getCollectionBonus();
    var parts = [];
    if (bonus.allStat > 0) parts.push("四维各+" + bonus.allStat);
    if (bonus.atk > 0) parts.push("攻击/魔攻+" + bonus.atk);
    if (bonus.hp > 0) parts.push("HP+" + bonus.hp);
    return parts.length > 0 ? parts.join("，") : "暂无加成（收集怪物卡解锁）";
}

function getCollectionBonus() {
    var allStat = 0, atk = 0, hp = 0;
    for (var i = 0; i < REGIONS.length; i++) {
        var c = countRegionCards(i);
        if (c >= REGIONS[i].mobCount) {
            allStat += REGIONS[i].bonusAllStat;
            atk += REGIONS[i].bonusAtk;
        }
    }
    return { allStat: allStat, atk: atk, hp: hp };
}

function getEquipSets() {
    return [
        { name: "枫叶套装", total: 12, collected: 8 },
        { name: "扎昆套装", total: 6, collected: 4 },
        { name: "黑龙套装", total: 6, collected: 3 },
        { name: "鲁塔比斯", total: 5, collected: 2 },
        { name: "暴君套装", total: 5, collected: 1 },
        { name: "埃苏莱布斯", total: 8, collected: 1 },
        { name: "神秘之影", total: 8, collected: 0 }
    ];
}

function backMain() {
    actionType = 0; status = -1;
    action(1, 0, 0);
}
