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
			//var text = "#r"+美化1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+"#e「热卖推荐」#n"+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n";
			var text = "   #r欢迎来到~e 开心 下面是本服的游戏商城区.\r\n\r\n";
			text += "   您当前位置:#b商城首页>>元宝商城>>热卖推荐#k\r\n\r\n";//" + cm.getString(cm.getmoneyb(),5) + "
			text += "                #b当前元宝:#r" + cm.getString(cm.getmoneyb(),5) + "#k\r\n"
			text += "  #d#L23##v4110000##z4110000##l                \r\n\r\n     需要元宝:#r10\r\n";
			text += "  #d#L0##v2022531##z2022531##l                \r\n\r\n     需要元宝:#r15\r\n";
			text += "  #d#L1##v2079995##z2079995##l                \r\n\r\n     需要元宝:#r600\r\n";		
			text += "  #d#L2##v2049124##z2049124##l                \r\n\r\n     需要元宝:#r5\r\n";
			text += "  #d#L3##v2049122##z2049122##l                \r\n\r\n     需要元宝:#r25\r\n";
			text += "  #d#L4##v2049104##z2049104##l                \r\n\r\n     需要元宝:#r20\r\n";
			text += "  #d#L5##v2531000##z2531000##l                \r\n\r\n     需要元宝:#r15\r\n";
			text += "  #d#L6##v5520000##z5520000##l                \r\n\r\n     需要元宝:#r30\r\n";
			text += "  #d#L7##v4310108##z4310108##l                \r\n\r\n     需要元宝:#r5\r\n";			
			text += "  #d#L8##v3994731##z3994731##l                \r\n\r\n     需要元宝:#r10\r\n";
			text += "  #d#L9##v3700001##z3700001##l                \r\n\r\n     需要元宝:#r100\r\n";
			text += "  #d#L10##v5510000##z5510000##l                \r\n\r\n     需要元宝:#r15\r\n";
			text += "  #d#L11##v5570000##z5570000##l                \r\n\r\n     需要元宝:#r15\r\n";
			text += "  #d#L12##v4170016##z4170016##l                \r\n\r\n     需要元宝:#r3\r\n";
			text += "  #d#L13##v4170007##z4170007##l                \r\n\r\n     需要元宝:#r3\r\n";
			text += "  #d#L14##v4000464##z4000464##l                \r\n\r\n     需要元宝:#r10\r\n";
			text += "  #d#L15##v4310098##z4310098##l                \r\n\r\n     需要元宝:#r10\r\n";
			text += "  #d#L16##v4310097##z4310097##l                \r\n\r\n     需要元宝:#r10\r\n";
			text += "  #d#L17##v4310156##z4310156##l                \r\n\r\n     需要元宝:#r15\r\n";
			text += "  #d#L18##v4310088##z4310088##l                \r\n\r\n     需要元宝:#r1\r\n";
			text += "  #d#L19##v4440200##z4440200##l                \r\n\r\n     需要元宝:#r2\r\n";
			text += "  #d#L20##v4441200##z4441200##l                \r\n\r\n     需要元宝:#r2\r\n";
			text += "  #d#L21##v4442200##z4442200##l                \r\n\r\n     需要元宝:#r2\r\n";
			text += "  #d#L22##v4443200##z4443200##l                \r\n\r\n     需要元宝:#r2\r\n";
            text += "\r\n      -----#d-------#g-------#b--------#g-----#b----#r------#k\r\n";
			cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                beauty = 0;//                
                 var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买1小时双倍爆率卡\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 1) {
                beauty = 1;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买龙背镖\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 2) {
                beauty = 2;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买高级混沌卷轴20%\r\n"
                cm.sendGetNumber(txt, 1, 1,100);				
           } else if (selection == 3) {
                beauty = 3;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买高级混沌卷轴30%\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 4) {
                beauty = 4;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买恶魔卷轴\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 5) {
                beauty = 5;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买防爆卷轴\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 6) {
                beauty = 6;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买宿命剪刀\r\n"
                cm.sendGetNumber(txt, 1, 1,100);		
           } else if (selection == 7) {
                beauty = 7;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买点券币\r\n"
                cm.sendGetNumber(txt,1, 1,100);				
           } else if (selection == 8) {
                beauty = 8;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买一亿金币\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 9) {
                beauty = 9;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买会员开通凭证\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 10) {
                beauty = 10;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买原地复活术\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 11) {
                beauty = 11;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买金锤子\r\n"
                cm.sendGetNumber(txt, 1, 1,100);		
           } else if (selection == 12) {
                beauty = 12;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买彩蛋\r\n"
                cm.sendGetNumber(txt, 1, 1,100);				
           } else if (selection == 13) {
                beauty = 13;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买时装蛋\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 14) {
                beauty = 14;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买中国心\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 15) {
                beauty = 15;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买低级贝勒德币\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 16) {
                beauty = 16;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买贝勒德币\r\n"
                cm.sendGetNumber(txt, 1, 1,100);	
           } else if (selection == 17) {
                beauty = 17;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买埃苏莱布斯币\r\n"
                cm.sendGetNumber(txt, 1, 1,100);				
           } else if (selection == 18) {
                beauty = 18;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买RED币\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 19) {
                beauty = 19;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买B级力量宝石\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 20) {
                beauty = 20;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买B级运气宝石\r\n"
                cm.sendGetNumber(txt, 1, 1,100);
           } else if (selection == 21) {
                beauty = 21;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买B级智慧宝石\r\n"
                cm.sendGetNumber(txt, 1, 1,100);			
           } else if (selection == 22) {
                beauty = 22;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买B级敏捷宝石\r\n"
                cm.sendGetNumber(txt, 1, 1,100);			
           } else if (selection == 23) {
                beauty = 23;//                
                var txt = "请填写购买的数量\r\n已为你计算填入最大兑换量，是否购买扫荡卷\r\n"
                cm.sendGetNumber(txt, 1, 1,100);						
            }
        } else if (status == 2) {
            if (beauty == 0) {
              if (cm.getmoneyb() < selection*15) {
		        cm.sendOk("您没有足够元宝 "+selection*15+"");
		        cm.dispose();
                } else {
				cm.gainItem(2022531,+selection*1);
                cm.setmoneyb(-selection*15);
                cm.sendOk("成功购买了:#v2022531##r#z2022531#");
                cm.dispose();
}
          } else if (beauty == 1) {
              if (cm.getmoneyb() < selection*600) {
		        cm.sendOk("您没有足够元宝 "+selection*600+"");
		        cm.dispose();
       } else {
				cm.gainItem(2079995,+selection*1);
                cm.setmoneyb(-selection*600);
                cm.sendOk("成功购买了:#v2079995##r#z2079995#");
                cm.dispose();
}
          } else if (beauty == 2) {
              if (cm.getmoneyb() < selection*5) {
		        cm.sendOk("您没有足够元宝 "+selection*5+"");
		        cm.dispose();
       } else {
				cm.gainItem(2049124,+selection*1);
                cm.setmoneyb(-selection*5);
                cm.sendOk("成功购买了:#v2049124##r#z2049124#");
                cm.dispose();
}



          } else if (beauty == 3) {
              if (cm.getmoneyb() < selection*25) {
		        cm.sendOk("您没有足够元宝 "+selection*25+"");
		        cm.dispose();
       } else {
				cm.gainItem(2049122,+selection*1);
                cm.setmoneyb(-selection*25);
                cm.sendOk("成功购买了:#v2049122##r#z2049122#");
                cm.dispose();
}
          } else if (beauty == 4) {
              if (cm.getmoneyb() < selection*20) {
		        cm.sendOk("您没有足够元宝 "+selection*20+"");
		        cm.dispose();
       } else {
				cm.gainItem(2049104,+selection*1);
                cm.setmoneyb(-selection*20);
                cm.sendOk("成功购买了:#v2049104##r#z2049104#");
                cm.dispose();
}
          } else if (beauty == 5) {
              if (cm.getmoneyb() < selection*15) {
		        cm.sendOk("您没有足够元宝 "+selection*15+"");
		        cm.dispose();
       } else {
				cm.gainItem(2531000,+selection*1);
                cm.setmoneyb(-selection*15);
                cm.sendOk("成功购买了:#v2531000##r#z2531000#");
                cm.dispose();
}
          } else if (beauty == 6) {
              if (cm.getmoneyb() < selection*30) {
		        cm.sendOk("您没有足够元宝 "+selection*30+"");
		        cm.dispose();
       } else {
				cm.gainItem(5520000,+selection*1);
                cm.setmoneyb(-selection*30);
                cm.sendOk("成功购买了:#v5520000##r#z5520000#");
                cm.dispose();
}
          } else if (beauty == 7) {
              if (cm.getmoneyb() < selection*5) {
		        cm.sendOk("您没有足够元宝 "+selection*5+"");
		        cm.dispose();
       } else {
				cm.gainItem(4310108,+selection*1);
                cm.setmoneyb(-selection*5);
                cm.sendOk("成功购买了:#v4310108##r#z4310108#");
                cm.dispose();
}
          } else if (beauty == 8) {
              if (cm.getmoneyb() < selection*10) {
		        cm.sendOk("您没有足够元宝 "+selection*10+"");
		        cm.dispose();
       } else {
				cm.gainItem(3994731,+selection*1);
                cm.setmoneyb(-selection*10);
                cm.sendOk("成功购买了:#v3994731##r#z3994731#");
                cm.dispose();
}
          } else if (beauty == 9) {
              if (cm.getmoneyb() < selection*100) {
		        cm.sendOk("您没有足够元宝 "+selection*100+"");
		        cm.dispose();
       } else {
				cm.gainItem(3700001,+selection*1);
                cm.setmoneyb(-selection*100);
                cm.sendOk("成功购买了:#v3700001##r#z3700001#");
                cm.dispose();
}
          } else if (beauty == 10) {
              if (cm.getmoneyb() < selection*15) {
		        cm.sendOk("您没有足够元宝 "+selection*15+"");
		        cm.dispose();
       } else {
				cm.gainItem(5510000,+selection*1);
                cm.setmoneyb(-selection*15);
                cm.sendOk("成功购买了:#v5510000##r#z5510000#");
                cm.dispose();
}
          } else if (beauty == 11) {
              if (cm.getmoneyb() < selection*5) {
		        cm.sendOk("您没有足够元宝 "+selection*5+"");
		        cm.dispose();
       } else {
				cm.gainItem(5570000,+selection*1);
                cm.setmoneyb(-selection*5);
                cm.sendOk("成功购买了:#v5570000##r#z5570000#");
                cm.dispose();
}
          } else if (beauty == 12) {
              if (cm.getmoneyb() < selection*3) {
		        cm.sendOk("您没有足够元宝 "+selection*3+"");
		        cm.dispose();
       } else {
				cm.gainItem(4170016,+selection*1);
                cm.setmoneyb(-selection*3);
                cm.sendOk("成功购买了:#v4170016##r#z4170016#");
                cm.dispose();
}
          } else if (beauty == 13) {
              if (cm.getmoneyb() < selection*3) {
		        cm.sendOk("您没有足够元宝 "+selection*3+"");
		        cm.dispose();
       } else {
				cm.gainItem(4170007,+selection*1);
                cm.setmoneyb(-selection*3);
                cm.sendOk("成功购买了:#v4170007##r#z4170007#");
                cm.dispose();
}
          } else if (beauty == 14) {
              if (cm.getmoneyb() < selection*10) {
		        cm.sendOk("您没有足够元宝 "+selection*10+"");
		        cm.dispose();
       } else {
				cm.gainItem(4000464,+selection*1);
                cm.setmoneyb(-selection*10);
                cm.sendOk("成功购买了:#v4000464##r#z4000464#");
                cm.dispose();
}
          } else if (beauty == 15) {
              if (cm.getmoneyb() < selection*10) {
		        cm.sendOk("您没有足够元宝 "+selection*10+"");
		        cm.dispose();
       } else {
				cm.gainItem(4310098,+selection*1);
                cm.setmoneyb(-selection*10);
                cm.sendOk("成功购买了:#v4310098##r#z4310098#");
                cm.dispose();
}
          } else if (beauty == 16) {
              if (cm.getmoneyb() < selection*10) {
		        cm.sendOk("您没有足够元宝 "+ selection*10+"");
		        cm.dispose();
       } else {
				cm.gainItem(4310097,+selection*1);
                cm.setmoneyb(-selection*10);
                cm.sendOk("成功购买了:#v4310097##r#z4310097#");
                cm.dispose();
}
          } else if (beauty == 17) {
              if (cm.getmoneyb() < selection*15) {
		        cm.sendOk("您没有足够元宝 "+selection*15+"");
		        cm.dispose();
       } else {
				cm.gainItem(4310156,+selection*1);
                cm.setmoneyb(-selection*15);
                cm.sendOk("成功购买了:#v4310156##r#z4310156#");
                cm.dispose();
}
          } else if (beauty == 18) {
              if (cm.getmoneyb() < selection*1) {
		        cm.sendOk("您没有足够元宝 "+selection*1+"");
		        cm.dispose();
       } else {
				cm.gainItem(4310088,+selection*1);
                cm.setmoneyb(-selection*1);
                cm.sendOk("成功购买了:#v4310088##r#z4310088#");
                cm.dispose();
}
          } else if (beauty == 19) {
              if (cm.getmoneyb() < selection*2) {
		        cm.sendOk("您没有足够元宝 "+selection*2+"");
		        cm.dispose();
       } else {
				cm.gainItem(4440200,+selection*1);
                cm.setmoneyb(-selection*2);
                cm.sendOk("成功购买了:#v4440200##r#z4440200#");
                cm.dispose();
}
          } else if (beauty == 20) {
              if (cm.getmoneyb() < selection*2) {
		        cm.sendOk("您没有足够元宝 "+selection*2+"");
		        cm.dispose();
       } else {
				cm.gainItem(4441200,+selection*1);
                cm.setmoneyb(-selection*2);
                cm.sendOk("成功购买了:#v4441200##r#z4441200#");
                cm.dispose();
}
          } else if (beauty == 21) {
              if (cm.getmoneyb() < selection*2) {
		        cm.sendOk("您没有足够元宝 "+selection*2+"");
		        cm.dispose();
       } else {
				cm.gainItem(4442200,+selection*1);
                cm.setmoneyb(-selection*2);
                cm.sendOk("成功购买了:#v4442200##r#z4442200#");
                cm.dispose();
}
          } else if (beauty == 22) {
              if (cm.getmoneyb() < selection*2) {
		        cm.sendOk("您没有足够元宝 "+selection*2+"");
		        cm.dispose();
       } else {
				cm.gainItem(4443200,+selection*1);
                cm.setmoneyb(-selection*2);
                cm.sendOk("成功购买了:#v4443200##r#z4443200#");
                cm.dispose();
}
          } else if (beauty == 23) {
              if (cm.getmoneyb() < selection*10) {
		        cm.sendOk("您没有足够元宝 "+selection*10+"");
		        cm.dispose();
       } else {
				cm.gainItem(4110000,+selection*1);
                cm.setmoneyb(-selection*10);
                cm.sendOk("成功购买了:#v4110000##r#z4110000#");
                cm.dispose();
}




				}
        }
    }
}


		
		