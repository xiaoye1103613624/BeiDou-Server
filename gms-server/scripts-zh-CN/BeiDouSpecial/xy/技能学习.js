/**
 * 技能学习NPC —— 展示技能图标 → 手动确认 → 一键学习
 * 已学会的技能显示绿色勾，未学会的显示目标等级
 * 全部已学会时提示无需学习
 *
 * ========== 配置区（修改技能ID和等级即可） ==========
 */
var SKILL_LIST = [
    { skillId: 8,    level: 1, name: "群宠" },
    { skillId: 1003, level: 1, name: "匠人之魂" },
    { skillId: 1004, level: 1, name: "骑兽" },
    { skillId: 1007, level: 3, name: "锻造" },
];

// ==================== 逻辑代码（一般不需要修改） ====================

var SkillFactory = Java.type('org.gms.client.SkillFactory');
var status = -1;
var TITLE_TEXT = "#e#d技能学习中心#n#k";

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    // 标准模式处理：1=前进, -1=后退, 其他=关闭对话
    if (mode === 1) {
        status++;
    } else if (mode === -1) {
        status--;
    } else {
        cm.dispose();
        return;
    }

    if (status === 0) {
        // ===== 第一步：展示所有技能图标及学习状态 =====
        showSkillIcons();
    } else if (status === 1) {
        // ===== 第二步：确认是否执行学习 =====
        if (selection === 0) {
            showConfirmDialog();
        } else {
            cm.dispose();
        }
    } else if (status === 2) {
        // ===== 第三步：执行学习并展示结果 =====
        if (selection !== 2) { cm.dispose(); return; }
        executeLearning();
    }
}

/** 第一步：展示技能图标，区分已学/未学 */
function showSkillIcons() {
    var allLearned = isAllLearned();
    if (allLearned) {
        // 全部已学会，展示后关闭
        var text = TITLE_TEXT + "\r\n\r\n";
        text += "#g你已经完成了全部技能学习，无需重复领取。#k\r\n\r\n";
        text += "#b════════════════#k\r\n\r\n";
        for (var i = 0; i < SKILL_LIST.length; i++) {
            var sk = SKILL_LIST[i];
            var currentLevel = getPlayerSkillLevel(sk.skillId);
            text += "#s" + sk.skillId + "#  #b#q" + sk.skillId + "##k  #g已学会 Lv." + currentLevel + "#k\r\n\r\n";
        }
        text += "#b════════════════#k\r\n\r\n";
        text += "如有疑问请联系管理员。";
        cm.sendOk(text);
        cm.dispose();
        return;
    }

    // 有未学技能 → 展示图标 + 确认按钮
    var text = TITLE_TEXT + "\r\n\r\n";
    text += "以下是可学习的技能列表：\r\n\r\n";
    text += "#b════════════════#k\r\n\r\n";

    for (var i = 0; i < SKILL_LIST.length; i++) {
        var sk = SKILL_LIST[i];
        var currentLevel = getPlayerSkillLevel(sk.skillId);
        if (currentLevel > 0) {
            // 已学会 → 绿色勾
            text += "#s" + sk.skillId + "#  #b#q" + sk.skillId + "##k  #g✓ 已学会 Lv." + currentLevel + "#k\r\n\r\n";
        } else {
            // 未学会 → 显示目标等级
            text += "#s" + sk.skillId + "#  #b#q" + sk.skillId + "##k  #d→ 即将学习 Lv." + sk.level + "#k\r\n\r\n";
        }
    }

    text += "#b════════════════#k\r\n\r\n";
    text += "#d注意：已学会的技能不会重复学习。#k\r\n\r\n";
    text += "#L0##b确认学习全部技能#k#l\r\n";
    text += "#L1##r取消#k#l";
    cm.sendSimple(text);
}

/** 第二步：确认对话框 */
function showConfirmDialog() {
    var text = TITLE_TEXT + "\r\n\r\n";
    text += "#e即将学习以下技能：#n\r\n\r\n";
    text += "#b════════════════#k\r\n\r\n";

    var learnCount = 0;
    for (var i = 0; i < SKILL_LIST.length; i++) {
        var sk = SKILL_LIST[i];
        var currentLevel = getPlayerSkillLevel(sk.skillId);
        if (currentLevel > 0) {
            text += "#s" + sk.skillId + "#  #b#q" + sk.skillId + "##k  #g已学会，跳过#k\r\n\r\n";
        } else {
            learnCount++;
            text += "#s" + sk.skillId + "#  #b#q" + sk.skillId + "##k  #d→ 学习至 Lv." + sk.level + "#k\r\n\r\n";
        }
    }

    text += "#b════════════════#k\r\n\r\n";
    if (learnCount === 0) {
        text += "#g所有技能已学会，无需操作。#k";
        cm.sendOk(text);
        cm.dispose();
        return;
    }
    text += "将学习 #d" + learnCount + "#k 个技能，确定继续吗？\r\n\r\n";
    text += "#L2##b确定学习#k#l\r\n";
    text += "#L3##r取消#k#l";
    cm.sendSimple(text);
}

/** 第三步：执行学习 */
function executeLearning() {
    var successCount = 0;
    var skipCount = 0;
    var failCount = 0;
    var resultText = "";

    for (var i = 0; i < SKILL_LIST.length; i++) {
        var sk = SKILL_LIST[i];
        try {
            var skill = SkillFactory.getSkill(sk.skillId);
            if (skill === null) {
                failCount++;
                resultText += "#s" + sk.skillId + "#  #b" + sk.name + "#k  →  #r技能不存在#k\r\n\r\n";
                continue;
            }

            var currentLevel = cm.getPlayer().getSkillLevel(skill);
            if (currentLevel >= sk.level) {
                skipCount++;
                resultText += "#s" + sk.skillId + "#  #b" + sk.name + "#k  →  #g已学会 Lv." + currentLevel + "#k\r\n\r\n";
                continue;
            }

            // 使用 teachSkill API 学习技能（自动处理类型转换和已学检测，比直接调用 changeSkillLevel 更安全）
            cm.teachSkill(sk.skillId, sk.level, sk.level, -1);
            successCount++;
            resultText += "#s" + sk.skillId + "#  #b" + sk.name + "#k  →  #g学习成功 Lv." + sk.level + "#k\r\n\r\n";

        } catch (e) {
            failCount++;
            resultText += "#s" + sk.skillId + "#  #b" + sk.name + "#k  →  #r出错: " + e + "#k\r\n\r\n";
        }
    }

    var finalText = TITLE_TEXT + "\r\n\r\n";
    finalText += "#e学习结果#n\r\n";
    finalText += "#b══════════════════#k\r\n\r\n";
    finalText += resultText;
    finalText += "#b══════════════════#k\r\n\r\n";
    finalText += "成功: #g" + successCount + "#k  |  跳过: #d" + skipCount + "#k  |  失败: #r" + failCount + "#k";
    cm.sendOk(finalText);
    cm.dispose();
}

/** 检测是否所有技能都已学会 */
function isAllLearned() {
    for (var i = 0; i < SKILL_LIST.length; i++) {
        if (getPlayerSkillLevel(SKILL_LIST[i].skillId) <= 0) {
            return false;
        }
    }
    return true;
}

/** 获取玩家指定技能当前等级（安全封装） */
function getPlayerSkillLevel(skillId) {
    try {
        var skill = SkillFactory.getSkill(skillId);
        if (skill == null) return 0;
        return cm.getPlayer().getSkillLevel(skill);
    } catch (e) {
        return 0;
    }
}
