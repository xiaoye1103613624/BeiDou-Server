//DabaiMs
	/*function start() {

	
	
	            // cm.gainItem(1022048, 1500, 1500, 1500, 1500, 0, 0, 1500, 1500, 0, 0, 0, 0, 0, 0);
				//cm.gainItem(1082102, 1500, 1500, 1500, 1500, 0, 0, 1500, 1500, 0, 0, 0, 0, 0, 0);
    var 金元 = cm.getPlayer().getItemQuantity(4430000, false);
                    cm.setmoneyb(+金元);
                    cm.gainItem(4430000,-金元);
                    cm.playerMessage(5, "[领取金元]:获得:"+金元+"点金元");
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
var Dabaims = "#fUI/GuildMark.img/Mark/Animal/00002015/16#";
var Dabaims1 = "#fUI/GuildMark.img/Mark/Animal/00002015/15#";
var Dabaims2 = "#fUI/GuildMark.img/Mark/Animal/00002015/14#";
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
			var text = "#r"+美化1+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+"#e「领取累冲」#n"+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化2+""+美化3+"#k\r\n\r\n";
			text += "                #r当前未领取累冲:#c4430000##k\r\n";
			text += "                #b当前拥有累冲:"+cm.getPlayer().getCzlj()+"#k\r\n";
			text += "              #d#L0#"+Dabaims+""+Dabaims+"< 领取累冲 >"+Dabaims+""+Dabaims+"#l\r\n\r\n";
            text += "\r\n      -----#d-------#g-------#b--------#g-----#b----#r------#k\r\n";
			cm.sendSimple(text);
        } else if (status == 1) {
            if (selection == 0) {
                beauty = 0;//                
                var txt = "检测您的背包有#i4430000#:#c4430000#，是否全部兑换请填写兑换的数量\r\n已为你计算填入最大兑换量，是否将它兑换成累冲\r\n"
                cm.sendGetNumber(txt, cm.itemQuantity(4430000), 1,9999);
            }

        } else if (status == 2) {
            if (beauty == 0) {
              if (cm.haveItem(4430000,selection) == false) {
		        cm.sendOk("您没有足够的#v4430000##r#z4430000# 当前背包数量:#c4430000#");
		        cm.dispose();
                } else {
                cm.getPlayer().setCzlj(cm.getPlayer().getCzlj() +selection);//cm.setmoneyb(+selection);
				cm.gainItem(4430000,-selection);
                cm.playerMessage(1, "兑换成功\r\n扣除未领取金元:"+selection+"\r\n获得累积积分:"+selection+"");
                cm.dispose();
}
				}
        }
    }
}


		
		