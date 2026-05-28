/*
 * ==================
 * 脚本类型: 一键满技能（修复版）
 * 脚本作者：北斗项目组
 * 功能说明：
 *   1. 一键将当前职业及其所有前置职业的技能升级至满级
 *   2. 自动跳过精灵的祝福（12/10000012/20000012/20010012）
 *   3. 不包含未转职职业的技能
 *   4. 遍历WZ全部技能数据，解决旧版仅提升已学技能的问题
 * ==================
 */

var SkillFactory = Java.type('org.gms.client.SkillFactory');
var DataProviderFactory = Java.type('org.gms.provider.DataProviderFactory');
var WZFiles = Java.type('org.gms.provider.wz.WZFiles');
var Job = Java.type('org.gms.client.Job');

// GraalVM JS 无法自动将 Number 窄化转换为 byte，需通过 Java Byte 显式转换
var ByteClass = Java.type('java.lang.Byte');
function toByte(n) {
    return ByteClass.parseByte(String(n));
}

// 精灵的祝福技能ID —— 不可改动
var BLESSING_IDS = [12, 10000012, 20000012, 20010012];

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
        var playerJob = Job.getById(curJobId);
        var player = cm.getPlayer();
        var skillCount = player.getSkills().size();

        var text = "#e#b=== 一键满技能（修复版） ===#k#n\r\n\r\n";
        text += "当前职业：#b" + playerJob.getName() + "（" + curJobId + "）#k\r\n";
        text += "当前已学技能数：#b" + skillCount + "#k\r\n";
        text += "#d" + "".padStart(26, "——") + "#k\r\n\r\n";
        text += "将遍历WZ全部技能数据，自动匹配当前职业可用的技能，\r\n";
        text += "并提升至满级（包含新学技能 + 已学技能）。\r\n\r\n";
        text += "覆盖范围：\r\n";
        text += "  #b● 新手技能（全职业通用）#k\r\n";
        text += "  #b● 当前职业分支各阶段技能（一转～最高转职）#k\r\n\r\n";
        text += "#r注意：#k\r\n";
        text += "#r● 精灵的祝福不会被改动#k\r\n";
        text += "#r● 不会添加其他职业分支的技能#k\r\n\r\n";
        text += "#L0##b确认执行一键满技能#k#l\r\n";
        text += "#L1##r取消#k#l\r\n";

        cm.sendSimple(text);
    } else if (status === 1) {
        if (selection === 0) {
            var player = cm.getPlayer();
            var curJobId = cm.getJobId();
            var playerJob = Job.getById(curJobId);
            var maxedCount = 0;

            // 遍历String.wz中全部技能数据（与MaxSkillCommand相同方式）
            var skillDataList = DataProviderFactory.getDataProvider(WZFiles.STRING).getData("Skill.img").getChildren();
            for (var i = 0; i < skillDataList.size(); i++) {
                var skillData = skillDataList.get(i);
                try {
                    var skillId = parseInt(skillData.getName());

                    // 跳过精灵的祝福
                    if (BLESSING_IDS.indexOf(skillId) !== -1) {
                        continue;
                    }

                    // 判断技能是否属于当前职业体系
                    var skillJobId = Math.floor(skillId / 10000);

                    if (skillJobId !== 0) {
                        // 非新手技能：需要检查职业归属
                        var skillJob = Job.getById(skillJobId);
                        // 未被识别的职业或不属于当前职业分支，跳过
                        if (skillJob.getId() === 0 || !playerJob.isA(skillJob)) {
                            continue;
                        }
                    }

                    var skill = SkillFactory.getSkill(skillId);
                    if (skill === null) {
                        continue;
                    }

                    player.changeSkillLevel(skill, toByte(skill.getMaxLevel()), skill.getMaxLevel(), -1);
                    maxedCount++;
                } catch (e) {
                    // 跳过分类目录（如"000"、"100"等无对应Skill的条目）
                }
            }

            cm.sendOk("一键满技能完成！\r\n\r\n已提升 #b" + maxedCount + "#k 个技能至满级。\r\n#r精灵的祝福未被改动#k");
        }
        cm.dispose();
    }
}