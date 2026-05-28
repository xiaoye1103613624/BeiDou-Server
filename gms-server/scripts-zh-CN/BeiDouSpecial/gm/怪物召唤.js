/*
 * ==================
 * 脚本类型: GM怪物召唤工具
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 按怪物ID或名称关键字搜索怪物
 *   2. 展示怪物图片、HP、等级、经验、BOSS标记等信息
 *   3. 可设置召唤数量，在当前地图玩家位置召唤
 * ==================
 */

var MonsterInformationProvider;
var LifeFactory;
var ItemInformationProvider;

var status = -1;
var searchInput = "";
var searchResults = [];
var selectedMobId = -1;

// Boss成套召唤配置：选择以下ID时自动连带召唤所有部位
var BOSS_GROUPS = {};
// 扎昆 - 8800000~8800002 是三阶段本体, 8800003~8800010 是8个手臂
BOSS_GROUPS[8800000] = [8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010];
BOSS_GROUPS[8800001] = [8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010];
BOSS_GROUPS[8800002] = [8800003, 8800004, 8800005, 8800006, 8800007, 8800008, 8800009, 8800010];
// 暗黑龙王 - 本体8810018, 部位8810002~8810009
BOSS_GROUPS[8810018] = [8810002, 8810003, 8810004, 8810005, 8810006, 8810007, 8810008, 8810009];

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1) {
        if (status >= 2) { status -= 2; action(1, 0, 0); return; }
        cm.dispose();
        return;
    }
    if (mode === 0) {
        cm.dispose();
        return;
    }

    if (!cm.getPlayer().isGM()) {
        cm.sendOk("该功能仅GM可用。");
        cm.dispose();
        return;
    }

    if (MonsterInformationProvider == null) {
        MonsterInformationProvider = Java.type('org.gms.server.life.MonsterInformationProvider');
        LifeFactory = Java.type('org.gms.server.life.LifeFactory');
        ItemInformationProvider = Java.type('org.gms.server.ItemInformationProvider').getInstance();
    }

    if (mode === 1) { status++; }

    // ========================================
    // status 0: 输入搜索内容
    // ========================================
    if (status === 0) {
        cm.sendGetText("#e#b=== 怪物召唤 ===#k#n\r\n\r\n请输入怪物ID或名称关键字进行搜索：");
    }

    // ========================================
    // status 1: 执行搜索
    // ========================================
    else if (status === 1) {
        searchInput = cm.getText().trim();
        if (searchInput === "") {
            cm.sendOk("输入不能为空。");
            cm.dispose();
            return;
        }

        searchResults = [];

        // 先尝试按ID精确查找
        var idNum = parseInt(searchInput);
        if (!isNaN(idNum) && idNum.toString() === searchInput) {
            var mob = LifeFactory.getMonster(idNum);
            if (mob != null && mob.getName() !== "MISSINGNO") {
                searchResults.push({id: idNum, name: mob.getName()});
            }
        }

        // 如果ID查找没有结果，按名称搜索
        if (searchResults.length === 0) {
            var nameResults = MonsterInformationProvider.getMobsIDsFromName(searchInput);
            for (var i = 0; i < nameResults.size(); i++) {
                var pair = nameResults.get(i);
                searchResults.push({id: pair.getLeft(), name: pair.getRight()});
            }
        }

        // 同名怪物去重：同名保留第一个（最低ID），多部位怪物召唤时是一起出的
        if (searchResults.length > 1) {
            var seenNames = {};
            var deduped = [];
            for (var i = 0; i < searchResults.length; i++) {
                var r = searchResults[i];
                if (!seenNames[r.name]) {
                    seenNames[r.name] = true;
                    deduped.push(r);
                }
            }
            searchResults = deduped;
        }

        if (searchResults.length === 0) {
            cm.sendOk("#r未找到匹配的怪物。#k\r\n\r\n请检查输入是否正确。");
            cm.dispose();
            return;
        }

        // 只有一个结果 → 直接确认
        if (searchResults.length === 1) {
            selectedMobId = searchResults[0].id;
            status = 2;
            showMonsterInfoAndAskQty();
            return;
        }

        // 多个结果 → 显示选择列表
        var text = "#e#b=== 搜索结果 ===#k#n\r\n\r\n";
        text += "找到 #b" + searchResults.length + "#k 个匹配怪物：\r\n\r\n";
        for (var j = 0; j < searchResults.length; j++) {
            if (j >= 100) {
                text += "... 仅显示前100个结果\r\n";
                break;
            }
            var r = searchResults[j];
            var bossTag = MonsterInformationProvider.getInstance().isBoss(r.id) ? " #r[BOSS]#k" : "";
            text += "#L" + j + "# #b" + r.name + "#k (ID:" + r.id + ")" + bossTag + "#l\r\n";
        }
        cm.sendSimple(text);
    }

    // ========================================
    // status 2: 从搜索结果中选择了一个怪物
    // ========================================
    else if (status === 2) {
        if (selectedMobId === -1) {
            selectedMobId = searchResults[selection].id;
        }
        showMonsterInfoAndAskQty();
    }

    // ========================================
    // status 3: 获取数量并执行召唤
    // ========================================
    else if (status === 3) {
        // sendGetNumber 将数字放在 selection 参数中
        var qty = selection;
        if (qty < 1 || qty > 100) {
            cm.sendOk("#r数量必须在1~100之间。#k");
            cm.dispose();
            return;
        }

        var map = cm.getPlayer().getMap();
        var pos = cm.getPlayer().getPosition();
        var mobName = getMonsterName(selectedMobId);
        var parts = BOSS_GROUPS[selectedMobId];

        for (var k = 0; k < qty; k++) {
            var offsetX = pos.x + ((Math.random() - 0.5) * 200) | 0;
            var pt = new java.awt.Point(offsetX, pos.y);

            if (parts) {
                // Boss成套召唤：本体 + 所有部位
                spawnMonsterInternal(selectedMobId, pt.x, pt.y);
                for (var p = 0; p < parts.length; p++) {
                    var px = pt.x + ((Math.random() - 0.5) * 120) | 0;
                    spawnMonsterInternal(parts[p], px, pt.y);
                }
            } else {
                spawnMonsterInternal(selectedMobId, pt.x, pt.y);
            }
        }

        var resultText = "#e#b=== 召唤成功 ===#k#n\r\n\r\n" +
            "怪物：#b" + mobName + "#k (ID:" + selectedMobId + ")\r\n" +
            "数量：#b" + qty + "#k 组\r\n";
        if (parts) {
            resultText += "连带部位：#b" + parts.length + "#k 个 (ID:" + parts.join(",") + ")\r\n";
        }
        resultText += "地图：#b" + map.getId() + "#k";
        cm.sendOk(resultText);
        cm.dispose();
    }
}

// 显示怪物详细信息并询问召唤数量
function showMonsterInfoAndAskQty() {
    var mob = LifeFactory.getMonster(selectedMobId);
    if (mob == null || mob.getName() === "MISSINGNO") {
        cm.sendOk("#r怪物数据无效，请重新搜索。#k");
        cm.dispose();
        return;
    }

    var isBoss = MonsterInformationProvider.getInstance().isBoss(selectedMobId);

    var text = "#e#b=== 怪物信息 ===#k#n\r\n\r\n";
    if (isBoss) {
        text += "#r[BOSS]#k\r\n\r\n";
    }
    text += "名称：#b" + mob.getName() + "#k\r\n";
    text += "ID：#b" + selectedMobId + "#k\r\n";
    text += "等级：#b" + mob.getLevel() + "#k\r\n";
    text += "HP：#b" + formatNumber(mob.getMaxHp()) + "#k\r\n";
    text += "MP：#b" + formatNumber(mob.getMaxMp()) + "#k\r\n";
    text += "经验：#b" + formatNumber(mob.getExp()) + "#k\r\n";
    var s = mob.getStats();
    text += "物理攻击：#b" + s.getPADamage() + "#k\r\n";
    text += "魔法攻击：#b" + s.getMADamage() + "#k\r\n";
    text += "物理防御：#b" + s.getPDDamage() + "#k\r\n";
    text += "魔法防御：#b" + s.getMDDamage() + "#k\r\n";
    text += "闪避：#b" + s.eva + "#k\r\n";
    text += "命中：#b" + s.acc + "#k\r\n";
    if (isBoss) {
        text += "类型：#rBOSS#k\r\n";
    }
    var parts = BOSS_GROUPS[selectedMobId];
    if (parts) {
        text += "部位数：#b" + (parts.length + 1) + "#k (本体+" + parts.length + "部位，召唤时一并生成)\r\n";
    }

    text += "\r\n#e#d请输入召唤数量（1~100）：#k#n";
    cm.sendGetNumber(text, 1, 1, 100);
}

// 召唤单个怪物（清除自毁/定时消失）
function spawnMonsterInternal(mobId, x, y) {
    var monster = LifeFactory.getMonster(mobId);
    if (monster == null || monster.getName() === "MISSINGNO") {
        return;
    }
    monster.getStats().setRemoveAfter(0);
    monster.getStats().setSelfDestruction(null);
    cm.getPlayer().getMap().spawnMonsterOnGroundBelow(monster, new java.awt.Point(x, y));
}

// 获取怪物名称
function getMonsterName(mobId) {
    var mob = LifeFactory.getMonster(mobId);
    if (mob != null && mob.getName() !== "MISSINGNO") {
        return mob.getName();
    }
    return "未知怪物";
}

// 格式化大数字
function formatNumber(num) {
    if (num >= 100000000) {
        return (num / 100000000).toFixed(1) + "亿";
    } else if (num >= 10000) {
        return (num / 10000).toFixed(1) + "万";
    }
    return num.toString();
}
