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
var 月卡系统 = "#fEffect/CharacterEff1.img/QQ1408745/2/3#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
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
var aa = "#fUi/ChatBalloon.img/pet/12/nw#"
var bb = "#fUi/ChatBalloon.img/pet/5/nw#"
var cc = "#fUi/ChatBalloon.img/pet/5/ne#"
var dd = "#fUi/ChatBalloon.img/pet/5/s#"
var ff = "#fUi/ChatBalloon.img/pet/5/n#"
var ss = "#fUi/ChatBalloon.img/pet/5/sw#"
var gg = "#fUi/ChatBalloon.img/pet/5/se#"
///////////////////////////////////////////
var vipLevel = 5010019;
// 赠送材料
var 必成 = 2022615;
var 冒险岛卷 = 2022465;
var 高贵防具自选箱 = 2022613;
var 翅膀自选 = 2022613;
var 精灵吊坠 = 1122017;
var 红武自选 = 2022355;
var 网吧勋章 = 1142145;
var 枫叶 = 4001126;
var 黄金武器 = 2022503;
var S级魔方 = 4000463;

// 月卡工资材料
var x11卷轴 = 2049345;
var x12卷轴 = 2049346;
var x13卷轴 = 2049347;
var x14卷轴 = 2049348;
var x15卷轴 = 2049349;
var 祝福 = 2340000;
var 放大镜 = 2460005;
var 防爆 = 2531000;
var 高等五彩 = 4251202;
var 白嫖 = 5252001;
var 月卡币 = 2022511;
var D片 = 4031179;
var 枫叶球 = 4031456;
var 中国心 = 4000464;
var 白银月卡 = 5010019



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
            var text ="";
			text += "\t\t\t"+月卡系统+"\r\n"+群粉心+"";
            //text += ""+aa+"月卡办理，可每日领取以下对应的福利!\r\n\r\n"
			
			text += "\t\t\t\t#r#e#L0#超值月卡介绍#l\r\n\r\n     #L1#办理VIP月卡#l     #L2#领取月卡工资#l  \r\n\r\n"
			
            //text += ""+ss+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+dd+""+gg+"\r\n\r\n";
			cm.sendSimple(text);
        } else if (status == 1){
        // 第一部分代码
			if(selection == 0){
				var pdd = "";
				pdd += "\t\t\t"+月卡系统+"\r\n"+群粉心+"";
				pdd += "\t\t#k购买月卡#v5010019#[30日]特权需要 #r188赞助\r\n\r\n";
				//\t每周星期天提供一张 #r永久月卡 #k拍卖(起拍价 #r588#k)\r\n\r\n
				pdd += "\t#k购买后立刻获得#r [30日]月卡特权#k 加 #r188累计积分#k\r\n\r\n"
				
				pdd += "\t\t\t\t\t#r#e月卡特权\r\n\r\n#d#n\t\t【每日挑战扩充】\t【死亡自救次数】\r\n\t\t【自动贩卖物品】\t【自动兑换金币】\r\n\r\n";
				
				pdd += "\t\t\t\t#r#e月卡特权每天礼包#k#n\r\n";
				pdd += "#d#v2022509#*20 #v2022519#*2 #v2022124#*1 #v2460005#*1 #v2460006#*1 #v4321029#*2 #v2022530#*1 #v3994731#*1 #v2022309#*10 #v2022515#*1 #v2022141#*999 \r\n\r\n";

				pdd += "";
				cm.sendOk(pdd);
				cm.dispose();
			} else if(selection == 1){ // 办理月卡\t【每日领20元宝】\t【每日领2个复活术】\r\n
			if(cm.getPlayer().getmoney()>=188 && !cm.haveItem(5010019)) {
				cm.gainItem(5010019,1,30);
				//cm.gainItem(2022524,1,720);
				cm.getPlayer().setmoney(cm.getPlayer().getmoney()-188);
				cm.getPlayer().setlpjf(cm.getPlayer().getlpjf()+188); //累计积分
				cm.sendOk("办理月卡成功，30天使用权");
				cm.getItemLog("月卡明细","\r\n【"+cm.getName()+"】 开通了  188  月卡，  还有  "+cm.getPlayer().getmoney()+"  赞助\r\n")
				cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功办理了开心冒险岛月卡特权！！");
				cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功办理了开心冒险岛月卡特权！！");
				cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功办理了开心冒险岛月卡特权！！");
				cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功办理了开心冒险岛月卡特权！！");
				cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功办理了开心冒险岛月卡特权！！");
				cm.喇叭(2, "恭喜玩家：[" + cm.getPlayer().getName() + "]成功办理了开心冒险岛月卡特权！！");
				cm.dispose();
				}else {
					cm.sendOk("购买月卡失败！(原因)\r\n\r\n#r1.可能您没有188个赞助\r\n\r\n2.可能您已经拥有#i5010019#了");
				cm.dispose();
					}
			} else if(selection == 2){ // 月卡工资
			if (cm.haveItem(5010019,1)&&cm.getPlayer().getBossLog("超值月卡")<1) {
				cm.gainItem(2022509,20); //元宝箱子
				cm.gainItem(2022519,2,1); //复活术
				cm.gainItem(2022124,1); //金币双倍暴率
				cm.gainItem(2460005,1); //高级混沌卷
				cm.gainItem(2460006,1); //专属戒指正向
		//		cm.gainItem(4170007,1);
				cm.gainItem(4321029,2);	//扫荡器
				cm.gainItem(3994731,1); //一亿金币
				cm.gainItem(2022530,1); //花语
				cm.gainItem(2022309,10); //点券抵用置换卡
				cm.gainItem(2022515,1); //时装箱子
				cm.gainItem(2022141,999)				
				cm.gainMeso(+1000000);
				//#v2049124#*1  #v2340000#*1
				cm.getPlayer().setBossLog("超值月卡");
				cm.sendOk("你已经成功领取了今日超值月卡");
				cm.喇叭(2, "恭喜玩家:["+cm.getPlayer().getName()+"]领取了今天的月卡福利礼包");
				cm.dispose();
				}else {
					cm.sendOk("有没有一种可能你今天已经领过一次了，或者你没有月卡特权");
				cm.dispose();
					}
			} else if(selection == 3){ // 办理理财
				cm.openNpc(9900004,180883);
			} else if(selection == 4){ // 理财收益
				cm.openNpc(9900004,180884);
			}
        // ------------------------------beauty部分-------------------------------------- //
        } else if (status == 2){       
        // 第二部分代码
        }
    }
}


