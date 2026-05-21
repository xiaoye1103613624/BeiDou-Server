/*
 * ==================
 * 脚本类型: 一键满技能
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 一键将所有已学技能升级至满级
 *   2. 自动识别当前职业的最大技能等级
 *   3. 使用与GM命令MaxSkill相同的实现逻辑
 * ==================
 */

var SkillFactory = Java.type('org.gms.client.SkillFactory');
var DataProviderFactory = Java.type('org.gms.provider.DataProviderFactory');
var WZFiles = Java.type('org.gms.provider.wz.WZFiles');

var status = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode === -1 || mode === 0) {
        cm.dispose();
        return;
    }

    status++;

    if (status === 0) {
        var curJobId = cm.getJobId();
        var player = cm.getPlayer();
        var skills = player.getSkills();
        var skillCount = skills.size();

        var text = "#e#b=== 一键满技能 ===#k#n\r\n\r\n";
        text += "当前职业ID：#b" + curJobId + "#k\r\n";
        text += "当前已学技能数：#b" + skillCount + "#k\r\n";
        text += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";
        text += "将会把所有已学技能等级提升至满级。\r\n";
        text += "#r注意：仅提升已学技能，不会学习新技能。#k\r\n\r\n";
        text += "#L0##b确认执行一键满技能#k#l\r\n";
        text += "#L1##r取消#k#l\r\n";

        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            var player = cm.getPlayer();
            var skills = player.getSkills();
            var keys = skills.keySet().toArray();
            var maxedCount = 0;

            for (var i = 0; i < keys.length; i++) {
                var skill = keys[i];
                try {
                    var maxLevel = skill.getMaxLevel();
                    var curLevel = skills.get(skill).skillLevel;
                    if (curLevel < maxLevel) {
                        player.changeSkillLevel(skill, maxLevel, maxLevel, -1);
                        maxedCount++;
                    }
                } catch (e) {
                    // 跳过无法处理的技能
                }
            }

            cm.sendOk("一键满技能完成！\r\n已提升 #b" + maxedCount + "#k 个技能至满级。");
        }
        cm.dispose();
    }
}
