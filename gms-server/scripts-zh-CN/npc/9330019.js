/* 
var status = 0;
var zones = 0;
var selectedMap = -1;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 0 && mode == 0) {
	cm.dispose();
	return;
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendYesNo("你想要回副本大厅？？");
    } 
	else if (status == 1) 
	{
		if(cm.判断当前地图怪物数量 != 0)
		{
			var mapid = cm.当前地图ID();
			cm.清怪();
			cm.清除地图物品(mapid);
			cm.warp(802000101,0);
			cm.dispose();
		}
		else
		{
			cm.warp(802000101,0);
			cm.dispose();
		}
    }
}	 */

/* var 美化1 = "#fUI/ChatBalloon.img/pet/120/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/120/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/120/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/120/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/120/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/120/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具 */
var 一级救赎者次数 = 3;
var 二级救赎者次数 = 5;
var 三级救赎者次数 = 10;
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
				add += "你想干嘛?\r\n";
				add += "#L1##b回副本大厅#l\r\n"
				add += "#L2##b复活我和队友";
				cm.sendSimple(add);

//------------------------------------------------------------------------

        }
		else if (status == 1) 
		{
			if (selection == 1) 
			{
				if(cm.判断当前地图怪物数量 != 0)
				{
					var mapid = cm.当前地图ID();
					cm.清怪();
					cm.清除地图物品(mapid);
					cm.warp(749020920,0);
					cm.dispose();
				}
				else
				{
					cm.warp(749020920,0);
					cm.dispose();
				}
			}
			else if (selection == 2) 
			{
				var 最大次数=0;
				if(cm.getPlayer().getPrizeLog("3级救赎者"))
				{
					cm.playerMessage(1,"1");
					最大次数 = 三级救赎者次数;
					var 已用次数 = cm.getBossLog("救赎者复活次数");
					if(已用次数 < 最大次数)
					{
						cm.复活当前地图所有人();
						cm.setBossLog("救赎者复活次数");
						cm.给指定地图发公告(cm.当前地图ID(),"救赎者 "+cm.getPlayer().getName()+" 复活了所有人",5120007);
						cm.dispose();
					}
					else
					{
						cm.playerMessage(1,"复活次数已达上限");
						cm.dispose();
					}
				}
				else if(cm.getPlayer().getPrizeLog("2级救赎者"))
				{
					cm.playerMessage(1,"1");
					最大次数 = 二级救赎者次数;
					var 已用次数 = cm.getBossLog("救赎者复活次数");
					if(已用次数 < 最大次数)
					{
						cm.复活当前地图所有人();
						cm.setBossLog("救赎者复活次数");
						cm.给指定地图发公告(cm.当前地图ID(),"救赎者 "+cm.getPlayer().getName()+" 复活了所有人",5120007);
						cm.dispose();
					}
					else
					{
						cm.playerMessage(1,"复活次数已达上限");
						cm.dispose();
					}
				}
				else if(cm.getPlayer().getPrizeLog("1级救赎者"))
				{
					最大次数 = 一级救赎者次数;
					var 已用次数 = cm.getBossLog("救赎者复活次数");
					if(已用次数 < 最大次数)
					{
						cm.playerMessage(1,"1");
						cm.复活当前地图所有人(true);
						cm.setBossLog("救赎者复活次数");
						cm.给指定地图发公告(cm.当前地图ID(),"救赎者 "+cm.getPlayer().getName()+" 复活了所有人",5120007);
						cm.dispose();
					}
					else
					{
						cm.playerMessage(1,"复活次数已达上限");
						cm.dispose();
					}
				}
				else
				{
					cm.playerMessage(1,"你不是救赎者用不了这个功能");
				}
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