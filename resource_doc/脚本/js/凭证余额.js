//KaixinMS
	/*function start() {

	
	
	            // cm.gainItem(1022048, 1500, 1500, 1500, 1500, 0, 0, 1500, 1500, 0, 0, 0, 0, 0, 0);
				//cm.gainItem(1082102, 1500, 1500, 1500, 1500, 0, 0, 1500, 1500, 0, 0, 0, 0, 0, 0);
    var 余额 = cm.getPlayer().getItemQuantity(3100000, false);
                    cm.setmoneyb(+余额);
                    cm.gainItem(3100000,-余额);
                    cm.playerMessage(5, "[领取余额]:获得:"+余额+"点余额");
					cm.dispose();
               // }
		}*/
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
var KaixinMS = "#fUI/GuildMark.img/Mark/Animal/00002015/16#";
var KaixinMS1 = "#fUI/GuildMark.img/Mark/Animal/00002015/15#";
var KaixinMS2 = "#fUI/GuildMark.img/Mark/Animal/00002015/14#";
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
			var text = "#r"+美化1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+"#e「领取余额」#n"+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n";
			text += "                #r当前未领取余额:#c3100000##k\r\n";
			text += "                #b当前拥有余额:"+cm.getmoneyb()+"#k\r\n";
			text += "              #d#L0#"+KaixinMS+""+KaixinMS+"< 凭证换余额 >"+KaixinMS+""+KaixinMS+"#l\r\n\r\n";
			text += "              #d#L1#"+KaixinMS+""+KaixinMS+"< 余额换凭证 >"+KaixinMS+""+KaixinMS+"#l\r\n\r\n";
            text += "\r\n      -----#d-------#g-------#b--------#g-----#b----#r------#k\r\n";
			cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                beauty = 0;//                
                var txt = "检测您的背包有#i3100000#:#c3100000#，是否全部兑换请填写兑换的数量\r\n已为你计算填入最大兑换量，是否将它兑换成余额\r\n"
                cm.sendGetNumber(txt, cm.itemQuantity(3100000), 1,99999);
           } else if (selection == 1) {
                beauty = 1;//                
                var txt = "检测您的背包有:"+cm.getmoneyb()+"，是否全部兑换请填写兑换的数量\r\n已为你计算填入最大兑换量，是否将它兑换成凭证\r\n"
                cm.sendGetNumber(txt, cm.getmoneyb(), 1,9999);



            }

        } else if (status == 2) {
            if (beauty == 0) {
              if (cm.haveItem(3100000,selection) == false) {
		        cm.sendOk("您没有足够的#v3100000##r#z3100000# 当前背包数量:#c3100000#");
		        cm.dispose();
                } else {
cm.setmoneyb(+selection);
//cm.getPlayer().setjf(cm.getPlayer().getjf() +selection);//cm.setmoneyb(+selection);
				cm.gainItem(3100000,-selection);
                cm.playerMessage(1, "兑换成功\r\n扣除未领取余额:"+selection+"\r\n获得余额积分:"+selection+"");
                cm.dispose();
}
          } else if (beauty == 1) {
              	if (cm.getmoneyb() < selection) {
                cm.sendNext("余额不足 "+selection+"");
                cm.dispose();
        } else if (cm.canHold(3100000, selection) == false) {
                cm.sendOkS("您的其他栏背包空间不足，请整理后再兑换");
		        cm.dispose();
                } else {
                cm.setmoneyb(-selection);
//cm.getPlayer().setjf(cm.getPlayer().getjf() +selection);//cm.setmoneyb(+selection);
				cm.gainItem(3100000,selection);
                cm.playerMessage(1, "兑换成功\r\n扣除未领取余额:"+selection+"\r\n获得余额积分:"+selection+"");
                cm.dispose();
}







				}
        }
    }
}


		
		
