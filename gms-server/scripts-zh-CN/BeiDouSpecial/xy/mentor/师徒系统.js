/**
 * @description 师徒系统
 * 功能：创建师门、拜师、查看师门、徒弟管理、出师、退出师门
 * 依赖：MentorManager (Java, 静态配置), MentorService (Java, 静态业务方法)
 * 入口：9900001.js case 300
 */

var MentorManager = Java.type("org.gms.config.MentorManager");
var MentorService = Java.type("org.gms.service.MentorService");

// 全局状态变量（用于在nextLevel回调间传递额外数据）
var gActiveDiscipleList = null;

function start() { levelMain(); }

// ==================== 主菜单 ====================

function levelMain() {
    gActiveDiscipleList = null;
    var charId = cm.getPlayer().getId();
    var level = cm.getPlayer().getLevel();

    var text = "#e师徒系统#n\r\n\r\n";
    text += "您的等级：#b" + level + "#k\r\n";
    text += "━━━━━━━━━━━━━━━━\r\n\r\n";

    // 检查身份状态
    var masterInfo = MentorService.getMasterInfo(charId);
    var myMasterRel = MentorService.getMyMaster(charId);
    var isMaster = masterInfo != null;
    var isDisciple = myMasterRel != null;

    if (isMaster) {
        var disciples = MentorService.getDiscipleList(charId);
        var activeCount = 0;
        if (disciples) {
            for (var i = 0; i < disciples.size(); i++) {
                if (disciples.get(i).getStatus() == 0) activeCount++;
            }
        }
        text += "#b您是师父#k，当前活跃徒弟：#r" + activeCount + "#k 人（上限：" + MentorManager.getMaxDisciples() + " 人）\r\n\r\n";
        text += "#L1#查看师门情况#l\r\n";
        text += "#L2#徒弟管理#l\r\n";
    } else if (isDisciple) {
        var masterName = MentorService.getCharacterName(myMasterRel.getMasterCharacterId());
        var statusText = "";
        if (myMasterRel.getStatus() == 0) statusText = "#g修行中#k";
        else if (myMasterRel.getStatus() == 1) statusText = "#b已出师#k";
        else if (myMasterRel.getStatus() == 2) statusText = "#r已退出#k";

        text += "#b您的师父：#k" + masterName + "\r\n";
        text += "修行状态：" + statusText + "\r\n\r\n";
        text += "#L1#查看师门情况#l\r\n";
        if (myMasterRel.getStatus() == 0) {
            text += "#L5#出师（需达到 " + MentorManager.getGraduateLevel() + " 级）#l\r\n";
            text += "#L6#退出师门#l\r\n";
        }
    } else {
        text += "您当前没有师门关系。\r\n\r\n";
        if (level >= MentorManager.getCreateMasterLevel()) {
            text += "#L3#创建师门（成为师父，需 ≥" + MentorManager.getCreateMasterLevel() + " 级）#l\r\n\r\n";
        } else {
            text += "#r创建师门需要达到 " + MentorManager.getCreateMasterLevel() + " 级，您暂不满足条件。#k\r\n";
        }
        if (level <= MentorManager.getMaxBeDiscipleLevel()) {
            text += "#L4#  拜师（成为徒弟，需 ≤" + MentorManager.getMaxBeDiscipleLevel() + " 级）#l\r\n";
        } else {
            text += "#r  拜师需要等级 ≤" + MentorManager.getMaxBeDiscipleLevel() + " 级，您已超过上限。#k\r\n";
        }
    }

    text += "\r\n#L0#返回首页#l";
    cm.sendNextSelectLevel("HandleMain", text);
}

function levelHandleMain(selection) {
    if (selection == 0) { cm.dispose(); cm.openNpc(9900001); return; }
    if (selection == 1) levelViewMentor();
    else if (selection == 2) levelManageDisciples();
    else if (selection == 3) levelCreateMaster();
    else if (selection == 4) levelApplyDisciple();
    else if (selection == 5) levelGraduate();
    else if (selection == 6) levelLeave();
}

// ==================== 1. 查看师门 ====================

function levelViewMentor() {
    var charId = cm.getPlayer().getId();
    var text = "#e师门信息#n\r\n\r\n";

    var masterInfo = MentorService.getMasterInfo(charId);
    var myMasterRel = MentorService.getMyMaster(charId);

    var masterCharId;
    if (masterInfo != null) {
        masterCharId = charId;
        text += "#b您是师父#k\r\n";
    } else if (myMasterRel != null) {
        masterCharId = myMasterRel.getMasterCharacterId();
        var masterName = MentorService.getCharacterName(masterCharId);
        text += "师父：#b" + masterName + "#k\r\n";
        var ct = myMasterRel.getCreateTime();
        if (ct) text += "拜师时间：" + ct.toString().substring(0, 10) + "\r\n";
        if (myMasterRel.getStatus() == 1) {
            var gt = myMasterRel.getGraduateTime();
            if (gt) text += "出师时间：" + gt.toString().substring(0, 10) + "\r\n";
        }
    } else {
        text += "没有师门信息。\r\n";
        cm.sendOkLevel("Main", text);
        return;
    }

    // 列出所有徒弟
    var disciples = MentorService.getDiscipleList(masterCharId);
    if (disciples && disciples.size() > 0) {
        text += "\r\n━━━ 师门成员 ━━━\r\n";
        for (var i = 0; i < disciples.size(); i++) {
            var d = disciples.get(i);
            var dName = MentorService.getCharacterName(d.getDiscipleCharacterId());
            var dLevel = MentorService.getCharacterLevel(d.getDiscipleCharacterId());
            var dStatus = "";
            if (d.getStatus() == 0) dStatus = "#g修行中#k";
            else if (d.getStatus() == 1) dStatus = "#b已出师#k";
            else if (d.getStatus() == 2) dStatus = "#r已退出#k";
            text += "  · " + dName + "（Lv." + dLevel + "）- " + dStatus + "\r\n";
        }
    } else {
        text += "\r\n暂无徒弟记录。\r\n";
    }

    cm.sendOkLevel("Main", text);
}

// ==================== 2. 徒弟管理（仅师父可见） ====================

function levelManageDisciples() {
    var charId = cm.getPlayer().getId();
    var disciples = MentorService.getDiscipleList(charId);

    var text = "#e徒弟管理#n\r\n\r\n";
    var activeList = [];

    if (disciples && disciples.size() > 0) {
        for (var i = 0; i < disciples.size(); i++) {
            var d = disciples.get(i);
            if (d.getStatus() == 0) {
                activeList.push(d);
                var dName = MentorService.getCharacterName(d.getDiscipleCharacterId());
                var dLevel = MentorService.getCharacterLevel(d.getDiscipleCharacterId());
                text += "#L" + activeList.length + "#" + dName + "（Lv." + dLevel + "）- 踢出师门#l\r\n";
            }
        }
    }

    if (activeList.length == 0) {
        text += "暂无活跃的徒弟。\r\n";
    }
    text += "\r\n#L0#返回主菜单#l";

    gActiveDiscipleList = activeList; // 存入全局变量
    cm.sendNextSelectLevel("HandleManageDisciples", text);
}

function levelHandleManageDisciples(selection) {
    if (selection == 0) { levelMain(); return; }
    var idx = selection - 1;
    if (gActiveDiscipleList && idx >= 0 && idx < gActiveDiscipleList.length) {
        var d = gActiveDiscipleList[idx];
        var dName = MentorService.getCharacterName(d.getDiscipleCharacterId());
        var text = "确定要将 #r" + dName + "#k 逐出师门吗？\r\n\r\n";
        text += "逐出后该角色将失去师门关系，但可以重新拜师。";
        gActiveDiscipleList = [d]; // 只保留当前选中的徒弟用于回调
        cm.sendYesNoLevel("ManageDisciples", "HandleKickDisciple", text);
    } else {
        levelMain();
    }
}

function levelHandleKickDisciple() {
    var charId = cm.getPlayer().getId();
    if (gActiveDiscipleList && gActiveDiscipleList.length > 0) {
        var discipleId = gActiveDiscipleList[0].getDiscipleCharacterId();
        var result = MentorService.removeDisciple(charId, discipleId);
        cm.sendOkLevel("ManageDisciples", result);
    } else {
        cm.sendOkLevel("Main", "操作已取消。");
    }
}

// ==================== 3. 创建师门 ====================

function levelCreateMaster() {
    var charId = cm.getPlayer().getId();
    var level = cm.getPlayer().getLevel();
    var required = MentorManager.getCreateMasterLevel();

    if (level < required) {
        cm.sendOkLevel("Main", "您的等级不足！创建师门需要达到 " + required + " 级，您当前 " + level + " 级。");
        return;
    }

    var text = "#e创建师门#n\r\n\r\n";
    text += "创建师门后，您将成为师父，可以招收徒弟。\r\n";
    text += "创建条件：\r\n";
    text += "  · 等级 ≥ " + required + " 级\r\n";
    text += "  · 最大收徒数：" + MentorManager.getMaxDisciples() + " 人\r\n";
    text += "  · 徒弟出师等级：" + MentorManager.getGraduateLevel() + " 级\r\n\r\n";
    text += "确定要创建师门吗？";

    cm.sendYesNoLevel("Main", "HandleCreateMaster", text);
}

function levelHandleCreateMaster() {
    var charId = cm.getPlayer().getId();
    var result = MentorService.createMentorGroup(charId);
    cm.sendOkLevel("Main", result);
}

// ==================== 4. 拜师 ====================

function levelApplyDisciple() {
    var charId = cm.getPlayer().getId();
    var level = cm.getPlayer().getLevel();
    var maxLevel = MentorManager.getMaxBeDiscipleLevel();

    if (level > maxLevel) {
        cm.sendOkLevel("Main", "您的等级超过了拜师上限（" + maxLevel + " 级），无法拜师！请先升级后创建自己的师门。");
        return;
    }

    // 检查是否已有关系
    var myMasterRel = MentorService.getMyMaster(charId);
    if (myMasterRel != null) {
        cm.sendOkLevel("Main", "您已经有师父了，无法重复拜师！");
        return;
    }

    cm.getInputTextLevel("HandleApplyDisciple", "请输入您要拜师的师父角色名：");
}

function levelHandleApplyDisciple(masterName) {
    if (!masterName || masterName == "") {
        cm.sendOkLevel("Main", "输入无效，已取消拜师。");
        return;
    }
    var charId = cm.getPlayer().getId();
    var discipleName = cm.getName();
    var result = MentorService.addDisciple(charId, masterName, discipleName);
    cm.sendOkLevel("Main", result);
}

// ==================== 5. 出师 ====================

function levelGraduate() {
    var charId = cm.getPlayer().getId();
    var level = cm.getPlayer().getLevel();
    var requiredLevel = MentorManager.getGraduateLevel();

    if (level < requiredLevel) {
        cm.sendOkLevel("Main", "您的等级不足！出师需要达到 " + requiredLevel + " 级，您当前 " + level + " 级。");
        return;
    }

    var myMasterRel = MentorService.getMyMaster(charId);
    if (myMasterRel == null || myMasterRel.getStatus() != 0) {
        cm.sendOkLevel("Main", "您当前没有活跃的师门关系，无法出师！");
        return;
    }

    var masterName = MentorService.getCharacterName(myMasterRel.getMasterCharacterId());
    var text = "#e出师确认#n\r\n\r\n";
    text += "师父：#b" + masterName + "#k\r\n";
    text += "您的等级：#b" + level + "#k\r\n";
    text += "出师条件：≥" + requiredLevel + " 级\r\n\r\n";
    text += "出师后您将获得毕业奖励，师门关系标记为已出师。\r\n";
    text += "确定要出师吗？";

    cm.sendYesNoLevel("Main", "HandleGraduate", text);
}

function levelHandleGraduate() {
    var charId = cm.getPlayer().getId();
    var result = MentorService.graduateDisciple(charId);
    cm.sendOkLevel("Main", result);
}

// ==================== 6. 退出师门 ====================

function levelLeave() {
    var charId = cm.getPlayer().getId();
    var myMasterRel = MentorService.getMyMaster(charId);
    if (myMasterRel == null || myMasterRel.getStatus() != 0) {
        cm.sendOkLevel("Main", "您当前没有活跃的师门关系，无法退出！");
        return;
    }

    var masterName = MentorService.getCharacterName(myMasterRel.getMasterCharacterId());
    var text = "#e退出师门#n\r\n\r\n";
    text += "师父：#b" + masterName + "#k\r\n\r\n";
    text += "#r注意：退出师门后无法获得出师奖励！#k\r\n";
    text += "确定要退出师门吗？";

    cm.sendYesNoLevel("Main", "HandleLeave", text);
}

function levelHandleLeave() {
    var charId = cm.getPlayer().getId();
    var result = MentorService.leaveMentor(charId);
    cm.sendOkLevel("Main", result);
}
