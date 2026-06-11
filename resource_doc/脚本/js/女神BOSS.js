/* var 美化1 = "#fUI/ChatBalloon.img/pet/120/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/120/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/120/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/120/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/120/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/120/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具 */
var 花花1 = "#fUI/GuildMark/Mark/Pattern/00004020/1#";
var 花花2 = "#fUI/GuildMark/Mark/Pattern/00004020/3#";
var 花花3 = "#fUI/GuildMark/Mark/Pattern/00004020/5#";
var 花花4 = "#fUI/GuildMark/Mark/Pattern/00004020/7#";
var 花花5 = "#fUI/GuildMark/Mark/Pattern/00004020/9#";
var 花花6 = "#fUI/GuildMark/Mark/Pattern/00004020/11#";
var 花花7 = "#fUI/GuildMark/Mark/Pattern/00004020/13#";
var 花花8 = "#fUI/GuildMark/Mark/Pattern/00004020/14#";
var 花花9 = "#fUI/GuildMark/Mark/Pattern/00004020/15#";
var mmm = "#fUI/PredictHarmony.img/card/13#";
var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = "   "+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 大new = "#fUI/CashShop.img/CSEffect/new/3#";
var 小new = "#fUI/CashShop.img/CSEffect/new/0#"; //和字一样大
var 鼠标左键点击 = "#fUI/Basic.img/Cursor/4/1#";
var 黄色发光圆点 = "#fSkill/MobSkill.img/153/level/1/mob/0#";
var 围棋方形图标 ="#fUI/ChatBalloon.img/miniroom/Omok#";
var 红色左上 = "#fUI/ChatBalloon.img/10/nw#";
var 红色右上 = "#fUI/ChatBalloon.img/10/ne#";
var 红色上中 = "#fUI/ChatBalloon.img/10/n#";
var 红色下中 = "#fUI/ChatBalloon.img/10/s#";
var 红色左下 = "#fUI/ChatBalloon.img/10/sw#";
var 红色右下 = "#fUI/ChatBalloon.img/10/se#";
var 艾菲尼娅 = "#fEffect/CharacterEff1.img/QQ1408745/4/0#";
var 都纳斯 = "#fEffect/CharacterEff1.img/QQ1408745/4/1#";
var 欧啦啦 = "#fEffect/CharacterEff1.img/QQ1408745/4/2#";
var 欧碧拉 = "#fEffect/CharacterEff1.img/QQ1408745/4/3#";
var 斯乌 = "#fEffect/CharacterEff1.img/QQ1408745/4/4#";
var 希纳斯 = "#fEffect/CharacterEff1.img/QQ1408745/4/5#";
var 路西德 = "#fEffect/CharacterEff1.img/QQ1408745/4/6#";
var 觉醒希拉 = "#fEffect/CharacterEff1.img/QQ1408745/4/7#";
var 卡琳 = "#fEffect/CharacterEff1.img/QQ1408745/4/8#";
var 浓姬 = "#fEffect/CharacterEff1.img/QQ1408745/4/9#";
var 桃乐丝 = "#fEffect/CharacterEff1.img/QQ1408745/4/10#";
var 希拉 = "#fEffect/CharacterEff1.img/QQ1408745/4/11#";

var 产出1 = 2340000
var 产出2 = 2340000
var 产出3 = 2340000
var 产出4 = 2340000
var 产出5 = 2340000
var 产出6 = 2340000
var 产出7 = 2340000
var 产出8 = 2340000
var 产出9 = 2340000
var 产出10 = 2340000
var chosenMap = -1;
var monsters = 0;
var towns = 0;
var bosses = 0;
var fuben = 0;
function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) 
	{
       
        cm.dispose();
    } 
	else 
	{
        if (status >= 0 && mode == 0) 
		{
            cm.sendOk("#b好的,下次再见.");
            cm.dispose();
            return;
        }
        if (mode == 1) 
		{
            status++;
        } else 
		{
            status--;
        }
        if (status == 0) 
		{

			if(cm.getPlayer().getMapId()==180000001)
			{
				cm.dispose();
				cm.openNpc(9900005);
				return;
			}
			
			
				var add = ""+dd+"\r\n\t\t\t"+挑战中心+"\r\n";
				add += "\t\t\t#b每日可任选挑战 #r6次 #b您今日已挑战 #r"+cm.getBossLog("女神BOSS")+"次\r\n";
				add += "#L30#"+小new+"#r每日6次#k用完可#r(5元宝/一次)#k增加挑战次数"+小new+"#l\r\n\r\n";
				add += "   "+红色左上+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色右上+"\r\n";
                if (cm.getBossLog("女神BOSS") <=50) { 
				add += "\t\t\t\t\t#k#e初 级 材 料 BOSS#n\r\n" ;
				add += "   #L1#"+欧啦啦+"#l       #L2#"+欧碧拉+"#l\t  #L3#"+斯乌+"#l\r\n" ;
				add +="#L1##k"+花花9+" 爆率10% "+花花9+"#l #L2#"+花花9+" 爆率15% "+花花9+"#l #L3#"+花花9+" 爆率20% "+花花9+"#l\r\n\r\n"+群粉心+""
				//add += "\t#L1#[欧啦啦]#l\t#L2#[欧碧拉]#l\t#L3#[斯  乌]#l\r\n\r\n"+群粉心+"" ;
				
				add += "\t\t\t\t\t#d#e中 级 材 料 BOSS#n\r\n" ;
				add += "   #L4#"+希纳斯+"#l       #L5#"+希拉+"#l\t  #L6#"+觉醒希拉+"#l\r\n" ;
				add +="#L4#"+花花7+" 爆率10% "+花花7+"#l #L5#"+花花7+" 爆率15% "+花花7+"#l #L6#"+花花7+" 爆率20% "+花花7+"#l\r\n\r\n"+群粉心+""
				
				add += "\t\t\t\t\t#b#e高 级 材 料 BOSS#n\r\n" ;
				add += "   #L7#"+浓姬+"#l       #L8#"+桃乐丝+"#l\t  #L9#"+卡琳+"#l\r\n" ;
				add +="#L7#"+花花5+" 爆率10% "+花花5+"#l #L8#"+花花5+" 爆率15% "+花花5+"#l #L9#"+花花5+" 爆率20% "+花花5+"#l\r\n\r\n"+群粉心+""
				
                }else {
                add += "\t\t\t\t#b#e您今日已经挑战满#r 6 #b次#n\r\n"
        }
				//add += "    	#L1##d"+都纳斯+"#l  #L1##d[都纳斯]#l    #L8##d"+路西德+"#l #L8#[路西德]#l#L9##r"+艾菲尼娅+"#l  #L9##r[艾菲尼娅]#l #L17##d"+艾菲尼娅+"#r [阿卡伊勒]#l\r\n";
			
				//add +="\r\n"
				add+= "   "+红色左下+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色右下+"\r\n";
				//add += "    #L29#"+小金币+"#d 使用 #r5元 #d宝提升挑战次数  [5元宝+1次]\r\n";
				cm.sendSimple(add);

//------------------------------------------------------------------------

        }
		else if (status == 1) 
		{
			if(selection == 1){
			   cm.openNpc(9000434,"女神BOSS1");
				}
				
			if(selection == 2){
			   cm.openNpc(9000434,"女神BOSS2");
				}
				
			if(selection == 3){
			   cm.openNpc(9000434,"女神BOSS3");
				}
				
			if(selection == 4){
			   cm.openNpc(9000434,"女神BOSS4");
				}
												
			if(selection == 5){
			   cm.openNpc(9000434,"女神BOSS5");
				}
												
			if(selection == 6){
			   cm.openNpc(9000434,"女神BOSS6");
				}
												
			if(selection == 7){
			   cm.openNpc(9000434,"女神BOSS7");
				}
												
			if(selection == 8){
			   cm.openNpc(9000434,"女神BOSS8");
				}
												
			if(selection == 9){
			   cm.openNpc(9000434,"女神BOSS9");
				}
												
			if(selection == 10){
			   cm.openNpc(9000434,"女神BOSS10");
				}
												
			if(selection == 11){
			   cm.openNpc(9000434,"女神BOSS11");
				}
												
			if(selection == 12){
			   cm.openNpc(9000434,"女神BOSS12");
				}
			if(selection == 30){
			//if (cm.getBossLog("高级BOSS") <= 1) {
            if (cm.getPlayer().getBossLog("女神BOSS") < 5)  {
            cm.sendOk("您今天的高级BOSS挑战次数还没有使用完哦");
            cm.dispose();
            return;
            } else if (cm.getmoneyb() >=5){   //&&cm.getPlayer().getCSPoints(1)>=6666
				cm.setmoneyb(-5)
				//cm.setBossLog("高级BOSS",-1);
				cm.getPlayer().setBossLog("女神BOSS", 0, -1);
				cm.喇叭(2, " 【"+cm.getName()+"】使用 5元宝 兑换了一次女神BOSS挑战次数"); 
			    cm.dispose();
		    }else{
			cm.sendOk("您没有足够的元宝哦");
			cm.dispose();
		   }
			   cm.openNpc(9000434,0);
				}
		} 
	}
}
//---------------------------------------------------------------------------
var acc = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 美化ne = "#fUI/UIWindow/Quest/icon6/7#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 中条猫 ="#fUI/ChatBalloon/37/n#";
var 猫右 =  "#fUI/ChatBalloon/37/ne#";
var 猫左 =  "#fUI/ChatBalloon/37/nw#";
var 右 =    "#fUI/ChatBalloon/37/e#";
var 左 =    "#fUI/ChatBalloon/37/w#";
var 下条猫 ="#fUI/ChatBalloon/37/s#";
var 猫下右 ="#fUI/ChatBalloon/37/se#";
var 猫下左 ="#fUI/ChatBalloon/37/sw#";
var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/16#";
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 草莓 = "#fUI/GuildMark/Mark/Plant/00003000/1#"; // 红色草莓
var 草莓1 = "#fUI/GuildMark/Mark/Plant/00003000/10#"; // 淡蓝色草莓
var 草莓2 = "#fUI/GuildMark/Mark/Plant/00003000/11#"; // 紫色草莓
var 草莓3 = "#fUI/GuildMark/Mark/Plant/00003000/15#"; // 白色草莓
var 草莓4 = "#fUI/GuildMark/Mark/Plant/00003000/3#"; // 黄色草莓
var 草莓5 = "#fUI/GuildMark/Mark/Plant/00003000/8#"; // 绿色草莓
var 小黄星 = "#fItem/Etc/0427/04270001/Icon9/0#";  //
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 大黄星 = "#fItem/Etc/0427/04270001/Icon9/1#";  //
var 小兔 = "#fEffect/CharacterEff/1112960/3/0#";  //邪恶小兔 【小】
var 小水滴 = "#fItem/Etc/0427/04270001/Icon10/5#";  //
var 大水滴 = "#fItem/Etc/0427/04270001/Icon10/4#";  //
var 红爱心 ="#fEffect/CharacterEff/1112905/0/1#";
var 金币图标 = "#fUI/UIWindow.img/QuestIcon/7/0#";
var aaa = "#fUI/UIWindow.img/Quest/icon9/0#";
var zzz = "#fUI/UIWindow.img/Quest/icon8/0#";
var sss = "#fUI/UIWindow.img/QuestIcon/3/0#";
var 标题右 = "#fUI/ChatBalloon/119/se#";
var 标题左 = "#fUI/ChatBalloon/119/sw#";
var 边框上拐角左 = "#fUI/ChatBalloon/122/nw#";
var 边框上拐角右 = "#fUI/ChatBalloon/122/ne#";
var 边框上 = "#fUI/ChatBalloon/122/n#";
var 边框下拐角左 = "#fUI/ChatBalloon/122/sw#";
var 边框下拐角右 = "#fUI/ChatBalloon/122/se#";
var 边框下 = "#fUI/ChatBalloon/122/s#";
var 兔子头 = "#fUI/ChatBalloon/122/head#";
var 小金币 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
var 点券图标 = "#fUI/CashShop/CashItem/0#";
var 警报灯 = "#fUI/StatusBar/BtClaim/normal/0#";
var 金色箭头右图标 = "#fUI/UIWindow/UserList/Guild/GuildRank/BtRight/pressed/0#";
var 金色箭头左图标 = "#fUI/UIWindow/UserList/Guild/GuildRank/BtLeft/pressed/0#";
var 银色箭头右图标 = "#fUI/UIWindow/UserList/Guild/GuildRank/BtRight/disabled/0#";
var 银色箭头左图标 = "#fUI/UIWindow/UserList/Guild/GuildRank/BtLeft/disabled/0#";
var 左小括号 = "#fUI/UIWindow.img/createCygnus/BtLeft/mouseOver/0#";//
var 右小括号 = "#fUI/UIWindow.img/createCygnus/BtRight/mouseOver/0#";//