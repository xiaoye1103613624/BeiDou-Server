/*
QQ327321366
重生职业
*/
var 琴符 = "#fEffect/CharacterEff/1032063/0/0#";
var 音符 = "#fEffect/CharacterEff/1032063/0/0#";
var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
//道具代码：
var 职业道具 = 3991027;
var 新手道具 = 3991027;

var status = 0;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {
            cm.dispose();
            return;
        }
        if (mode == 1)
            status++;
        else
            status--;

        if (status == 0) {
			if (cm.getInventory(1).isFull(25) || cm.getInventory(2).isFull(1)) {
				cm.sendOk("请保证背包#r装备栏25个空格、消耗栏2个空格#k以上");
				cm.dispose();
				return;
			}
            var textz = "\r\n\t\t#r#e" + 琴符 + "【重生职业系统】" + 音符 + "\r\n";
            textz += "#b转职说明：技能不保留，会清空键盘上所有技能\r\n";
            textz += "#b等级要求：120级,自动把身上的装备脱下\r\n";
            textz += "#b需要物品：#r#z4310086:##b \r\n";
            textz += "请选择您想重生的职业：#k#n\r\n";
            textz += "\t\t\t\t#r 【- 冒险家 -】 #k#l\r\n";
            textz += "\t\t  #L1#" + 表情高兴 + " 重生职业 [英雄] #k#l\r\n";
            textz += "\t\t  #L2#" + 表情高兴 + " 重生职业 [圣骑士] #k#l#k#l\r\n";
            textz += "\t\t  #L3#" + 表情高兴 + " 重生职业 [黑骑士] #k#l#k#l\r\n";
            textz += "\t\t  #L4#" + 表情高兴 + " 重生职业 [火毒魔导师] #k#l#k#l\r\n";
            textz += "\t\t  #L5#" + 表情高兴 + " 重生职业 [冰雷魔导师] #k#l#k#l\r\n";
            textz += "\t\t  #L6#" + 表情高兴 + " 重生职业 [主教] #k#l#k#l\r\n";
            textz += "\t\t  #L7#" + 表情高兴 + " 重生职业 [神射手] #k#l#k#l\r\n";
            textz += "\t\t  #L8#" + 表情高兴 + " 重生职业 [箭神] #k#l#k#l\r\n";
            textz += "\t\t  #L9#" + 表情高兴 + " 重生职业 [隐士] #k#l#k#l\r\n";
            textz += "\t\t  #L10#" + 表情高兴 + " 重生职业 [侠盗] #k#l#k#l\r\n";
            textz += "\t\t  #L11#" + 表情高兴 + " 重生职业 [冲锋队长] #k#l#k#l\r\n";
            textz += "\t\t  #L12#" + 表情高兴 + " 重生职业 [船长] #k#l#k#l\r\n\r\n\r\n";
			textz += "#r#e特殊说明：如果强化过五转技能#b#z2643002##r则会全部退还 \r\n\r\n";
            cm.sendSimple(textz);
        } else if (status == 1) {
            var skillConfig = [
                { 职业id: 132, 学习技能id: 14001002, 技能名称: "枪舞旋风", 技能书ID: 2643002 },
                { 职业id: 122, 学习技能id: 15111007, 技能名称: "威力神锤", 技能书ID: 2643002 },
                { 职业id: 112, 学习技能id: 11111006, 技能名称: "剑影分身", 技能书ID: 2643002 },
                { 职业id: 322, 学习技能id: 13111001, 技能名称: "奥义乱箭", 技能书ID: 2643002 },
                { 职业id: 312, 学习技能id: 13101002, 技能名称: "精灵元素", 技能书ID: 2643002 },
                { 职业id: 422, 学习技能id: 11001003, 技能名称: "利刃风暴", 技能书ID: 2643002 },
                { 职业id: 412, 学习技能id: 14111002, 技能名称: "死神刺杀", 技能书ID: 2643002 },
                { 职业id: 222, 学习技能id: 12001003, 技能名称: "魔力漩涡", 技能书ID: 2643002 },
                { 职业id: 212, 学习技能id: 12101006, 技能名称: "黑暗灵气", 技能书ID: 2643002 },
                { 职业id: 232, 学习技能id: 12111006, 技能名称: "星座法阵", 技能书ID: 2643002 },
                { 职业id: 512, 学习技能id: 15111003, 技能名称: "元气弹", 技能书ID: 2643002 },
                { 职业id: 522, 学习技能id: 15001001, 技能名称: "子弹盛宴", 技能书ID: 2643002 }
            ];

            var playerJob = cm.getPlayer().getJob();
            var currentSkillLevel = 0;
            var skillBookID = 2643002;
            var totalSkillBooksToRefund = 0;

            for (var i = 0; i < skillConfig.length; i++) {
                if (skillConfig[i].职业id === playerJob) {
                    currentSkillLevel = cm.getPlayer().getSkillLevel(skillConfig[i].学习技能id);
                    if (currentSkillLevel > 0) {
						for (var j = 1; j < currentSkillLevel; j++) { // 从第1级开始累加到当前等级
                            totalSkillBooksToRefund += Math.pow(2, j);
                        }
                    }
                }
            }

            if (cm.haveItem(4310086, 1)) {
                if (cm.getLevel() > 119) {
                    cm.gainItem(4310086, -1);
                    cm.unequipEverything(); // 脱装备语句
                    cm.getPlayer().clearSkills(); // 清理技能
                    // 根据玩家选择的重生职业，设置新的职业ID
                    var newJobId;
                    switch (selection) {
                        case 1: newJobId = 112; break; // 英雄
                        case 2: newJobId = 122; break; // 圣骑士
                        case 3: newJobId = 132; break; // 黑骑士
                        case 4: newJobId = 212; break; // 火毒魔导师
                        case 5: newJobId = 222; break; // 冰雷魔导师
                        case 6: newJobId = 232; break; // 主教
                        case 7: newJobId = 312; break; // 神射手
                        case 8: newJobId = 322; break; // 箭神
                        case 9: newJobId = 412; break; // 隐士
                        case 10: newJobId = 422; break; // 侠盗
                        case 11: newJobId = 512; break; // 冲锋队长
                        case 12: newJobId = 522; break; // 船长
                    }

                    cm.getPlayer().changeJob(newJobId); // 更改职业
                    cm.getChar().resetStats(4, 4, 4, 4);

                    if (totalSkillBooksToRefund > 0) {
                        cm.gainItem(skillBookID, totalSkillBooksToRefund);
                        cm.sendOk("由于你之前强化了五转技能！\r\n现在重生后，已退还你 #r#v2643002##z2643002# * " + totalSkillBooksToRefund + " 个！");
                    }

                    cm.喇叭(2, "恭喜玩家[" + cm.getPlayer().getName() + "]重生职业成功，大家一起祝贺他！");
                    cm.sendOk("哈哈，恭喜你年轻人，你已经完成了#r重生职业#k！");
                    cm.dispose();
                } else {
                    cm.sendOk("好像你没有达到120级，抱歉，无法操作！");
                    cm.dispose();
                }
            } else {
                cm.sendOk("请把#v4310086##z4310086#1个交给我！");
                cm.dispose();
            }
        }
    }
}