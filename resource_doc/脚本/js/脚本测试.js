var 星星 = "#fEffect/CharacterEff/1112903/0/0#";
var 爱心 = "#fEffect/CharacterEff/1032063/0/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var ttt ="#fUI/UIWindow.img/Quest/icon9/0#";
var xxx ="#fUI/UIWindow.img/Quest/icon8/0#";
var sss ="#fUI/UIWindow.img/QuestIcon/3/0#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#";
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#";
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#";
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#";
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#"; 
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#"; 
var 任务简介 = "#fUI/UIWindow.img/Quest/summary#"; 
var 任务提示 = "#fUI/UIWindow.img/Quest/BtAlert/mouseOver/0#"; 
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#"; 
var 橙条 = "#fUI/UIWindow.img/Minigame/Common/barTeamA#"; 
var 蘑菇 = "#fUI/UIWindow.img/Minigame/Common/mark#";

function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
            // 展示代码
            var text = "";

            text += "\t\t\t\t脚本测试+收集UI\r\n\r\n"

			text += " #L0#脚本测试0#l #L1#脚本测试1#l #L2#制作中心#l\r\n\r\n"
			
			text += " #L3#脚本测试3#l #L4#脚本测试4#l #L5#5转技能5#l\r\n\r\n"

			text += " #L6#5转技能5#l #L7#管理员密码功能测试#l\r\n\r\n"
			
			text += "  #L10#脚本测试10#l   #L100#脚本测试100#l   #L1000#制作中心1000#l\r\n\r\n"
			
			cm.sendSimple(text);
        } else if (status == 1){
        // 第一部分代码
			if(selection == 0){
				cm.openNpc(9310034,"测试0")
				cm.dispose();
			} else if(selection == 1){ // 办理会员
				cm.openNpc(9310073,"测试1");
			} else if(selection == 2){ // 会员工资
				cm.openNpc(9000436,"测试5");
			} else if(selection == 3){ // 办理理财
				cm.openNpc(9000436,"测试3");
			} else if(selection == 4){ // 理财收益
				cm.openNpc(9000436,"测试4");
			} else if(selection == 5){ // 理财收益
				cm.openNpc(9000436,"连续签到新");
			} else if(selection == 6){
				cm.openNpc(9100003,"喜从天降");
			} else if(selection == 10){ // 办理会员
				cm.openNpc(9300011,"游戏点装");
			} else if(selection == 100){ // 办理会员
				//cm.openNpc(9310070,0);
				cm.openNpc(9300011,"时装自选新");
            } else if(selection == 1000){ // 办理会员
				cm.openNpc(9300011,0);	
			}else if(selection == 7){
				cm.openNpc(9100003,"管理员密码");
			}
        // ------------------------------beauty部分-------------------------------------- //
        } else if (status == 2){       
        // 第二部分代码
        }
    }
}


