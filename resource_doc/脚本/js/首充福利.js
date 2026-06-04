var 礼包物品 = "#v1302000#";
var x1 = "1302000,+1";// 物品ID,数量
var x2;
var x3;
var x4;
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 蓝色小兔子 = "#fUI/UIWindow/Quest/icon6/7#";
var 蓝色小兔子 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 礼包物品 = "#v1302000#";
var x1 = "1302000,+1";// 物品ID,数量
var x2;
var x3;
var x4;
var 强化中心 = "#fEffect/CharacterEff1.img/QQ1408745/1/1#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 赞助中心 = "#fEffect/CharacterEff1.img/QQ1408745/1/9#";
var add = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 红色箭头 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var ttt1 = "#fEffect/CharacterEff/1062114/1/0#";  //爱心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#"; 
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#"; 
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#"; 
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#"; 
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 音符3 = "#fEffect/CharacterEff.img/1112949/2/0#"; 
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#";  
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#";  
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  
var 粉小于号 = "#fUI/Initials.img/Button/Button2/mouseOver/0#";
var 粉大于号 = "#fUI/Initials.img/Button/Button3/mouseOver/0#";
var 蓝色小喇叭 = "#fUI/CN_Chat.img/ChattingRoom/BtVolUp/0/mouseOver/0#";  
var 热点推荐 = "#fUI/CashShop.img/CSChar/BtCoordination/normal/0#";
var 铅笔 = "#fUI/GuildBBS.img/GuildBBS/BtReply/mouseOver/0#"; 
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
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
			/*if(cm.getJob() >= 0 && cm.getJob()<= 522 && cm.hasSkill(1017) == false){
			cm.teachSkill(1017,1,1);
			}else if(cm.getJob() >=1000 || cm.getJob() <= 2112 && cm.hasSkill(20001019) == false){
			cm.teachSkill(20001019,1,1);
			}*/
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            text += "\r\n\t\t\t"+赞助中心+"\r\n"+群粉心+"\r\n"
			text += "#r#e首充福利介绍：#k#n\r\n\r\n";  //---------------------------------五一 改为 首充
			text += "1、首充享受赞助#r双倍#k福利。\r\n";  //---------------------------------五一 改为 首充
			text += "2、#r额外奖励#k三天双倍爆率特权#v5360015#。\r\n\r\n";
			text += "3、#k游戏赞助比例：#r1 #k赞助 = #r2#k 元宝 + #r2#k 累计 + #r200#k点卷\r\n\r\n";
			text += "\r\n\t\t#r#e#L0##v5360015#兑换翻倍赞助和礼包#v5360015##l\r\n\r\n"
			text += "\t#n#r首充限额：(10-500)#k赞助#b(每个账号只能享受一次)\r\n\r\n";  //--------------------------------节日这里注销
		    cm.sendSimple(text);
			
        }else if(status == 1){
            if (selection == 0) {
            if(cm.getPlayer().getOneTimeLog("首充礼包") > 0){//---------------------------正常是=0，翻倍的时候写9999
               cm.sendNext("#r你没有可兑换的赞助或者你已经兑换过一次了，不能进行兑换！");
               status = -1;
                } else {
                    money1 = 1;
					cm.sendGetNumber("#r请输入你需要兑换的翻倍赞助数量:(10-9999以内)\r\n\r\n#b您当前拥有赞助数量为：#r"+cm.getPlayer().getmoney() +"#b 点\r\n", 1, 10, 500);  //----------------正常是500，翻倍写9999
                }

        }  
		}//status1结束
		else if(status == 2){
			if(money1 == 1){//首充礼包
				if(cm.getPlayer().getmoney() >= selection){//判断表吧应该是 && cm.haveItem(5360015)
				cm.getPlayer().setmoney(cm.getPlayer().getmoney()-selection);
				cm.getPlayer().modifyCSPoints(1, +selection*200,true);//点券
		        cm.setmoneyb(selection*2);//元宝
		        cm.getPlayer().setlpjf(cm.getPlayer().getlpjf()+selection*2);
				cm.gainItem(5360015,-1);//双倍爆率卡3天
				cm.gainItem(5360015,1,3);//双倍爆率卡3天
				cm.getPlayer().setOneTimeLog("首充礼包");
				//cm.getPlayer().setBossLog("每日赞助",0,selection*2);
				cm.getItemLog("首充明细","\r\n【"+cm.getName()+"】 首充兑换元宝  "+selection*2+"  个， 还拥有  "+cm.getPlayer().getmoney()+"  赞助\r\n")
                cm.sendOk("\t\t\t"+赞助中心+"#n\r\n\r\n兑换成功：您获得"+ selection*2 + "点元宝,"+ selection*2 + "点累计积分,并获得"+ selection*200 + "点券.");
			    cm.喇叭(2,"恭喜 ["+cm.getPlayer().getName()+"] 兑换 [首冲双倍礼包] 获得 "+ selection*2 + "元宝, "+ selection*2 + "累计积分, "+ selection*200 + "点券."); //---------------------首冲改为五一
				cm.喇叭(2,"恭喜 ["+cm.getPlayer().getName()+"] 兑换 [首冲双倍礼包] 获得 "+ selection*2 + "元宝, "+ selection*2 + "累计积分, "+ selection*200 + "点券."); //---------------------首冲改为五一
				cm.喇叭(2,"恭喜 ["+cm.getPlayer().getName()+"] 兑换 [首冲双倍礼包] 获得 "+ selection*2 + "元宝, "+ selection*2 + "累计积分, "+ selection*200 + "点券."); //---------------------首冲改为五一
				cm.getItemLog("赞助兑换元宝明细","\r\n【"+cm.getName()+"】 双倍兑换元宝  "+selection*2+"  个， 还拥有  "+cm.getPlayer().getmoney()+"  赞助\r\n")
				//cm.getChar().setqiandao(1);
				cm.dispose();
				}else {
					cm.sendOk("\t\t  "+赞助中心+"#n\r\n\r\n您的可用赞助不足或者您有月卡特权没到期，请仔细查看");
					cm.dispose();
				}
			}
		} 
    }
}
