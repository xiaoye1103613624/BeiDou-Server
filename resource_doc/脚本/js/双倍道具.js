var 美化1 = "#fUI/ChatBalloon.img/118/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/118/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/118/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/118/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/118/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/118/s#";//选择道具
//var 美化7 = "#fUI/ChatBalloon.img/118/head#";//选择道具
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var 正在进行中 = "#fUI/UIWindow/Quest/Tab/enabled/1#";
var 完成 = "#fUI/UIWindow/Quest/Tab/enabled/2#";
var 正在进行中蓝 = "#fUI/UIWindow/MonsterCarnival/icon1#";
var 完成红 = "#fUI/UIWindow/MonsterCarnival/icon0#";
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 元宝数量 = 8888888
var 元宝数量1 = 20
var 元宝数量2 = 100
var 元宝数量3 = 20
var 元宝数量4 = 300
var 元宝数量5 = 200


var 使用期限 = 30
var 使用期限1 = 1
var 使用期限2 = 7
var 使用期限3 = 1
var 使用期限4 = 7
var 使用期限5 = 7





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
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
			var text = "   #r欢迎来到~e 开心 下面是本服的游戏商城区.\r\n\r\n";
			text += "   您当前位置:#b商城首页>>元宝商城>>双倍道具#k\r\n\r\n";
			text += "                #b当前元宝:#r" + cm.getString(cm.getmoneyb(),5) + "#k\r\n"
		//	text += "  #d#L0##v4300000##z4300000##l\r\n\r\n\r\n"; 
			//text += "            需要元宝:#r"+元宝数量+"#d    使用期限:#r"+使用期限+"\r\n";
			
			text += "  #d#L1##v5210002##z5210002##l\r\n\r\n\r\n"; 
			text += "            需要元宝:#r"+元宝数量1+"#d         使用期限:#r"+使用期限1+"\r\n";		
			
			text += "  #d#L5##v5210003##z5210003##l\r\n\r\n\r\n"; 
			text += "            需要元宝:#r"+元宝数量5+"#d        使用期限:#r"+使用期限5+"\r\n";			
			text += "  #d#L2##v5360016##z5360016##l\r\n\r\n\r\n"; 
			text += "            需要元宝:#r"+元宝数量2+"#d         使用期限:#r"+使用期限2+"\r\n";			
			text += "  #d#L3##v5360015##z5360015##l\r\n\r\n\r\n"; 
			text += "            需要元宝:#r"+元宝数量3+"#d         使用期限:#r"+使用期限3+"\r\n";			
			text += "  #d#L4##v5211060##z5211060##l\r\n\r\n\r\n"; 
			text += "            需要元宝:#r"+元宝数量4+"#d        使用期限:#r"+使用期限4+"\r\n";			
			
            text += "\r\n      -----#d-------#g-------#b--------#g-----#b----#r------#k\r\n";
			cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                beauty = 0;//                
                 var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买1小时双倍爆率卡\r\n"
                cm.sendGetNumber(txt, cm.itemQuantity(4300000), 1,100);
           } else if (selection == 1) {
                beauty = 1;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买龙背镖\r\n"
                cm.sendGetNumber(txt, cm.itemQuantity(5210002), 1,100);
           } else if (selection == 2) {
                beauty = 2;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买高级混沌卷轴20%\r\n"
                cm.sendGetNumber(txt, cm.itemQuantity(5360016), 1,100);				
           } else if (selection == 3) {
                beauty = 3;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买高级混沌卷轴30%\r\n"
                cm.sendGetNumber(txt, cm.itemQuantity(5360015), 1,100);
           } else if (selection == 4) {
                beauty = 4;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买恶魔卷轴\r\n"
                cm.sendGetNumber(txt, cm.itemQuantity(5211060), 1,100);			
           } else if (selection == 5) {
                beauty = 5;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买恶魔卷轴\r\n"
                cm.sendGetNumber(txt, cm.itemQuantity(5210003), 1,100);						
            }
        } else if (status == 2) {
            if (beauty == 0) {
              if (cm.getmoneyb() < selection) {
		        cm.sendOk("您没有足够元宝");
		        cm.dispose();
                } else {
				cm.gainItem(4300000,+selection*1,720);
                cm.setmoneyb(-selection*8888888);
                cm.sendOk("成功购买了:#v4300000##r#z4300000#");
                cm.dispose();
}
          } else if (beauty == 1) {
              if (cm.getmoneyb() < selection) {
		        cm.sendOk("您没有足够元宝");
		        cm.dispose();
       } else {
				cm.gainItem(5210002,+selection*1,24);
                cm.setmoneyb(-selection*20);
                cm.sendOk("成功购买了:#v5210002##r#z5210002#");
                cm.dispose();
}
          } else if (beauty == 2) {
              if (cm.getmoneyb() < selection) {
		        cm.sendOk("您没有足够元宝");
		        cm.dispose();
       } else {
				cm.gainItem(5360016,+selection*1,168);
                cm.setmoneyb(-selection*100);
                cm.sendOk("成功购买了:#v5360016##r#z5360016#");
                cm.dispose();
}



          } else if (beauty == 3) {
              if (cm.getmoneyb() < selection) {
		        cm.sendOk("您没有足够元宝");
		        cm.dispose();
       } else {
				cm.gainItem(5360015,+selection*1,24);
                cm.setmoneyb(-selection*20);
                cm.sendOk("成功购买了:#v5360015##r#z5360015#");
                cm.dispose();
}
          } else if (beauty == 4) {
              if (cm.getmoneyb() < selection) {
		        cm.sendOk("您没有足够元宝");
		        cm.dispose();
       } else {
				cm.gainItem(5211060,+selection*1,168);
                cm.setmoneyb(-selection*300);
                cm.sendOk("成功购买了:#v5211060##r#z5211060#");
                cm.dispose();
}//5210003

          } else if (beauty == 5) {
              if (cm.getmoneyb() < selection) {
		        cm.sendOk("您没有足够元宝");
		        cm.dispose();
       } else {
				cm.gainItem(5210003,+selection*1,168);
                cm.setmoneyb(-selection*200);
                cm.sendOk("成功购买了:#v5210003##r#z5210003#");
                cm.dispose();
}



				}
        }
    }
}


		
		