/**
 * @description 怪物卡片收集系统
 *   1. 按区域（地图ID对应城镇）分类展示所有怪物卡片
 *   2. 每张卡片需收集5张（检查 monsterbook.level >= 5）
 *   3. 完成收集后可提交领取 1AP，每种卡片仅能领取一次
 *   4. 已领取记录使用 CharacterExtendValue 持久化（非每日清理）
 *   5. 进度实时从 monsterbook 表读取，不检查背包
 *
 * 依赖：MonsterCardCollectionService (Java), MonsterBook (Client)
 * 入口：NPC 9900001 case 310
 */

var MonsterCardCollectionService = Java.type("org.gms.service.MonsterCardCollectionService");
var Integer = Java.type("java.lang.Integer");

// ==================== 常量 ====================
var EXTEND_KEY = "卡片收集AP";          // 持久化Key（非每日清理，永久记录AP领取状态）
var SEL_BACK = 0;                      // 返回选项值

// 当前浏览的区域mapid（跨level共享）
var currentRegionMapid = 0;

// ==================== 入口 ====================
function start() {
    levelMain();
}

// ==================== 数据持久化（AP领取记录） ====================
/**
 * 加载已领取AP的卡片记录
 * 格式：{ "cardid": true, ... }
 * 使用持久化存储（extend_type=21），非每日清理，确保AP不会重复领取
 */
function loadClaimedData() {
    var raw = cm.getCharacterExtendValue(EXTEND_KEY);
    if (raw && raw !== "" && raw !== "null") {
        try { return JSON.parse(String(raw)); } catch (e) {}
    }
    return {};
}

function saveClaimedData(data) {
    cm.saveOrUpdateCharacterExtendValue(EXTEND_KEY, JSON.stringify(data));
}

// ==================== Level: 主菜单 - 区域列表 ====================
function levelMain() {
    // 从角色内存中的 MonsterBook 获取实时卡片等级（不查数据库，避免延迟）
    var playerCards = cm.getPlayer().getMonsterBook().getCards();
    var regions = MonsterCardCollectionService.getRegionsWithCards(playerCards);

    var text = "\t\t#r#e< 怪物卡片收集中心 >#k#n\r\n\r\n";
    text += "#d收集5张相同怪物卡片后，可在此提交领取1点AP！#k\r\n";
    text += "#d每种怪物卡片仅能领取一次AP奖励。#k\r\n\r\n";

    if (regions.isEmpty()) {
        text += "#r暂无卡片数据，请联系管理员配置。#k\r\n";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    text += "━━━ 请选择地区 ━━━\r\n\r\n";

    for (var i = 0; i < regions.size(); i++) {
        var region = regions.get(i);
        var cards = region.getCards();
        var completed = 0;
        for (var j = 0; j < cards.size(); j++) {
            if (cards.get(j).getLevel() >= 5) completed++;
        }
        text += "#L" + region.getMapid() + "##b" + region.getRegionName() + "#k";
        text += "   进度：#b" + completed + "#k/#r" + cards.size() + "#k\r\n";
        text += "#l\r\n";
    }

    text += "\r\n#L" + SEL_BACK + "##g返回首页#k#l\r\n";
    cm.sendNextSelectLevel("HandleRegion", text);
}

// ==================== Level: 选择区域 → 进入卡片列表 ====================
function levelHandleRegion(selection) {
    if (selection === SEL_BACK) {
        cm.dispose();
        cm.openNpc(9900001);
        return;
    }

    currentRegionMapid = selection;
    showCardsInRegion(currentRegionMapid);
}

// ==================== 显示区域内卡片列表 ====================
function showCardsInRegion(mapid) {
    // 从角色内存中的 MonsterBook 获取实时卡片等级
    var playerCards = cm.getPlayer().getMonsterBook().getCards();
    var regions = MonsterCardCollectionService.getRegionsWithCards(playerCards);

    // 查找选中的区域
    var targetRegion = null;
    for (var i = 0; i < regions.size(); i++) {
        if (regions.get(i).getMapid() == mapid) {
            targetRegion = regions.get(i);
            break;
        }
    }

    if (targetRegion == null) {
        cm.sendOkLevel("Main", "#r地区数据异常，请重新选择。#k");
        return;
    }

    var claimedData = loadClaimedData();
    var cards = targetRegion.getCards();
    var regionName = targetRegion.getRegionName();

    // 统计完成/已领取数量
    var completedCount = 0;
    var claimedCount = 0;
    for (var i = 0; i < cards.size(); i++) {
        if (cards.get(i).getLevel() >= 5) completedCount++;
        if (claimedData[String(cards.get(i).getCardid())] === true) claimedCount++;
    }

    var text = "\t\t#r#e< " + regionName + " - 卡片列表 >#k#n\r\n\r\n";
    text += "已收集满5张：#b" + completedCount + "#k  已领AP：#g" + claimedCount + "#k\r\n";
    text += "━━━ 收集进度（需5张方可提交） ━━━\r\n\r\n";

    for (var i = 0; i < cards.size(); i++) {
        var card = cards.get(i);
        var cardid = card.getCardid();
        var level = card.getLevel();
        var isClaimed = claimedData[String(cardid)] === true;
        var isCompleted = level >= 5;

        text += "#b" + (i + 1) + ". #k#v" + cardid + "# #t" + cardid + "#\r\n";
        text += "   收集进度：" + makeProgressBar(level, 5)
            + " #r" + level + "#k/#b5#k";

        if (isCompleted) {
            if (isClaimed) {
                text += "  #g[奖励已领取]#k";
            } else {
                text += "  #L" + cardid + "##r[领取奖励]#k#l";
            }
        } else {
            text += "  #d还需" + (5 - level) + "张#k";
        }
        text += "\r\n\r\n";
    }

    text += "\r\n#L" + SEL_BACK + "##g返回地区列表#k#l\r\n";
    cm.sendNextSelectLevel("HandleSubmit", text);
}

// ==================== Level: 提交卡片领取AP ====================
function levelHandleSubmit(selection) {
    if (selection === SEL_BACK) {
        levelMain();
        return;
    }

    var cardid = selection;
    var claimedData = loadClaimedData();

    // 1. 验证：不能重复领取
    if (claimedData[String(cardid)] === true) {
        cm.sendOkLevel("RefreshRegion",
            "你已经领取过 #t" + cardid + "# 的AP奖励了！\r\n#r每种卡片仅能领取一次。#k");
        return;
    }

    // 2. 在Java层完成等级验证 + AP发放（避免GraalJS类型转换问题）
    var player = cm.getPlayer();
    var playerCards = player.getMonsterBook().getCards();
    var success = MonsterCardCollectionService.claimCardAp(player, playerCards, cardid);

    if (!success) {
        // 卡片等级不足（< 5），用Helper获取当前等级显示
        var level = MonsterCardCollectionService.getPlayerCardLevel(playerCards, cardid);
        cm.sendOkLevel("RefreshRegion",
            "你需要先收集5张 #t" + cardid + "# 才能提交！\r\n当前进度：#r" + level + "/5#k");
        return;
    }

    // 3. 标记已领取（持久化保存）
    claimedData[String(cardid)] = true;
    saveClaimedData(claimedData);

    // 4. 成功消息，点确定后返回卡片列表继续领取
    var text = "#b提交成功！#k\r\n\r\n";
    text += "#i" + cardid + "# #t" + cardid + "#\r\n";
    text += "恭喜获得 #r1点AP#k！\r\n\r\n";
    text += "#g提示：每个怪物卡片仅能领取一次AP奖励，请继续收集其他卡片。#k";

    cm.sendOkLevel("RefreshRegion", text);
}

// ==================== 刷新页（提交后回到卡片列表） ====================
function levelRefreshRegion() {
    showCardsInRegion(currentRegionMapid);
}

// ==================== 进度条工具 ====================
/**
 * 生成可视化进度条
 * @param current 当前值
 * @param max 最大值
 * @returns 带颜色的进度条字符串
 */
function makeProgressBar(current, max) {
    if (max <= 0) return "#r[N/A]#k";
    var total = 5;
    var filled = Math.round(current / max * total);
    if (filled < 0) filled = 0;
    if (filled > total) filled = total;
    var empty = total - filled;
    var bar = "#e#r";
    for (var k = 0; k < filled; k++) bar += "■";
    bar += "#k";
    for (var k = 0; k < empty; k++) bar += "□";
    bar += "#n";
    return bar;
}
