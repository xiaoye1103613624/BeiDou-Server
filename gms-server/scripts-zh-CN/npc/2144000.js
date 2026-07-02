var 怪物 = "#fUI/PredictHarmony/card/13#";//卡片效果菊花
var 光球 = "#fUI/PredictHarmony/card/10#";//卡片效果菊花
var 蝙蝠魔 = "#fUI/PredictHarmony/card/15#";//卡片效果菊花
var 黄色剑 ="#fUI/GuildMark.img/Mark/Etc/00009003/3#";
var 红色左上 = "▎";
var 红色右上 = "▎";
var 红色上中 = "▎";
var 红色下中 = "▎";
var 红色左下 = "▎";
var 红色右下 = "▎";
var X方形图标 = "#fUI/Basic.img/BtClose/mouseOver/0#";
var X黑白方形图标 = "#fUI/Basic.img/BtClose2/normal/0#";
var 红蓝点 = "#fEffect/CharacterEff.img/1032054/0/0#";
var 蓝星 = "#fEffect/CharacterEff.img/1052203/1/0#";
var 红星 = "#fEffect/CharacterEff.img/1052203/2/0#";
var 大蓝星 = "#fEffect/CharacterEff.img/1022223/2/0#";
var 大红星 = "#fEffect/CharacterEff.img/1022223/1/0#";
var 蓝点 = "#fEffect/CharacterEff.img/1022223/6/0#";
var 红点 = "#fEffect/CharacterEff.img/1022223/7/0#";

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
			
			
				var add = "";
				add += "\t#r#e   	       "+ 红星 + ""+ 大红星 + ""+ 红点 + "" + cm.开服名称() + ""+ 红蓝点 + ""+ 蓝点 + ""+ 大蓝星 + ""+ 蓝星 + "#k#n \r\n\r\n";
				add += "   "+红色左上+红色上中+红色上中+红色上中+红色上中+红色上中+红色右上+"\r\n";
				add +=" "+蝙蝠魔+光球+怪物+"\r\n\r\n"
				add += "\t               #L4#"+X方形图标+" #r积分商城 "+X方形图标+"#l\r\n\r\n\r\n";
				add += "           #r[ #d称号BOSS掉落称号材料和不同的装备 #r]\r\n";
				add += "           #r[ #d破攻BOSS掉落破攻材料和不同的装备 #r]\r\n";
				add += "           #r[ #d战力BOSS掉落高级装备和不同的装备 #r]\r\n";
				add += "\r\n\r\n"
				add += "\t      #L1#"+红色箭头+蓝色箭头+"  #k挑战 神者塞伦·帝（战力）#l\r\n";
				add += "\t      #L2#"+红色箭头+蓝色箭头+"  #b挑战 神者塞伦·圣（破攻）#l\r\n";
				add += "\t      #L3#"+红色箭头+蓝色箭头+"  #r挑战 神者塞伦·尊（称号）#l\r\n\r\n";

				cm.sendSimple(add);

//------------------------------------------------------------------------

        }
		else if (status == 1) 
		{
			if (selection == 1) 
			{
					cm.dispose();
					cm.openNpc(2144000, "圣地庭院-神者塞伦·帝");
			}
			if (selection == 2) 
			{
					cm.dispose();
					cm.openNpc(2144000, "圣地庭院-神者塞伦·圣");
			}
			if (selection == 3) 
			{
					cm.dispose();
					cm.openNpc(2144000, "圣地庭院-神者塞伦·尊");//这个代码三小的挑战NPC外观，9000434
			}
			if (selection == 4) 
			{
					cm.dispose();
					cm.openNpc(2144000, "圣地积分商店");
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
var 蓝色箭头 = "#fEffect/UIWindow/Quest/icon2/7#";
var 红色箭头 = "#fEffect/UIWindow/Quest/icon6/7#";
var 圆形 = "#fEffect/UIWindow/Quest/icon3/6#";
var 美化ne = "#fEffect/UIWindow/Quest/icon6/7#";
var 感叹号 = "#fEffect/UIWindow/Quest/icon0#";
var 正方箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 中条猫 ="▎";
var 猫右 =  "▎";
var 猫左 =  "▎";
var 右 =    "▎";
var 左 =    "▎";
var 下条猫 ="▎";
var 猫下右 ="▎";
var 猫下左 ="▎";
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