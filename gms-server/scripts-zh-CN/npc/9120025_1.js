var 热点推荐方框 = "#fUI/CashShop.img/CSChar/BtCoordination/mouseOver/0#";
var 大new = "#fUI/CashShop.img/CSEffect/new/3#";
var 小new = "#fUI/CashShop.img/CSEffect/new/0#";
var 大hot = "#fUI/CashShop.img/CSEffect/hot/3#";
var 小hot = "#fUI/CashShop.img/CSEffect/hot/0#";
var 鼠标左键点击 = "#fUI/Basic.img/Cursor/4/1#";
var 加号方形图标 = "#fUI/Basic.img/BtMax/mouseOver/0#";
var 加号方形图标 = "#fUI/Basic.img/BtMax/mouseOver/0#";
var X方形图标 = "#fUI/Basic.img/BtClose/mouseOver/0#";
var X黑白方形图标 = "#fUI/Basic.img/BtClose2/normal/0#";
var 黄色发光圆点 = "#fSkill/MobSkill.img/153/level/1/mob/0#";
var 新年快乐图片 = "#fEffect/ItemEff.img/4300000/0#";
var 圣诞铃铛图片 ="#fEffect/ItemEff.img/1112810/1/1#";
var 蛋糕礼物图片 ="#fEffect/ItemEff.img/1112811/1/0#";
var 围棋方形图标 ="#fUI/ChatBalloon.img/miniroom/Omok#";
var 红色左上 = "#fUI/ChatBalloon/33/nw#";
var 红色右上 = "#fUI/ChatBalloon/33/ne#";
var 红色上中 = "#fUI/ChatBalloon/33/n#";
var 红色下中 = "#fUI/ChatBalloon/33/s#";
var 红色左下 = "#fUI/ChatBalloon/33/sw#";
var 红色右下 = "#fUI/ChatBalloon/33/se#";
var 灰1 ="#fUI/GuildMark.img/Mark/Letter/00005027/15#";
var 灰2 ="#fUI/GuildMark.img/Mark/Letter/00005028/15#";
var 灰3 ="#fUI/GuildMark.img/Mark/Letter/00005029/15#";
var 灰4 ="#fUI/GuildMark.img/Mark/Letter/00005030/15#";
var 灰5 ="#fUI/GuildMark.img/Mark/Letter/00005031/15#";
var 灰6 ="#fUI/GuildMark.img/Mark/Letter/00005032/15#";
var 灰7 ="#fUI/GuildMark.img/Mark/Letter/00005033/15#";
var 灰8 ="#fUI/GuildMark.img/Mark/Letter/00005034/15#";
var 灰9 ="#fUI/GuildMark.img/Mark/Letter/00005035/15#";
var 灰色剑 ="#fUI/GuildMark.img/Mark/Etc/00009003/15#";
var 黄1 ="#fUI/GuildMark.img/Mark/Letter/00005027/3#";
var 黄2 ="#fUI/GuildMark.img/Mark/Letter/00005028/3#";
var 黄3 ="#fUI/GuildMark.img/Mark/Letter/00005029/3#";
var 黄4 ="#fUI/GuildMark.img/Mark/Letter/00005030/3#";
var 黄5 ="#fUI/GuildMark.img/Mark/Letter/00005031/3#";
var 黄6 ="#fUI/GuildMark.img/Mark/Letter/00005032/3#";
var 黄7 ="#fUI/GuildMark.img/Mark/Letter/00005033/3#";
var 黄8 ="#fUI/GuildMark.img/Mark/Letter/00005034/3#";
var 黄9 ="#fUI/GuildMark.img/Mark/Letter/00005035/3#";
var 黄色剑 ="#fUI/GuildMark.img/Mark/Etc/00009003/3#";
var 圣诞铃铛图片 ="#fEffect/ItemEff.img/1112810/1/1#";
var 蛋糕礼物图片 ="#fEffect/ItemEff.img/1112811/1/0#";
var 大new = "#fUI/CashShop.img/CSEffect/new/3#";
var 小new = "#fUI/CashShop.img/CSEffect/new/0#";
var 大hot = "#fUI/CashShop.img/CSEffect/hot/3#";
var 小hot = "#fUI/CashShop.img/CSEffect/hot/0#";
var 小金币 = "#fUI/UIWindow.img/Item/BtCoin/normal/0#";
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
			
			
				var add="";
				add += "   "+红色左上+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色右上+"\r\n";
				add += "                          #v1092042#\r\n";
				add += "                      -  "+小new+"  -\r\n";
				add += "       #L1##d"+黄1+"  #d海盗副本 #b[多人]#l\r\n";	
				add += "       #L2##d"+黄2+" #d大蜈蚣副本 #b[单人]#l\r\n";
				add += "       #L3##d"+黄3+" #d武陵道场副本 #r[暂不开放]#l\r\n";
				add += "       #L4##d"+黄4+" #d蜗牛公园副本 #b[暂不开放]#l\r\n";
				add += "       #L5##d"+黄5+" #d保护雪人副本 #b[单人/多人]#l\r\n";
				add += "       #L6##d"+黄6+" #d地铁训练场副本 #b[单人]#l\r\n";
				add += "       #L7##d"+黄7+" #d二十七宫副本 #b[单人]#l\r\n";
				add += "       #L8##d"+黄8+" #d绯红副本 #b[多人]#l\r\n";
				add += "       #L9##d"+黄8+" #狗男女 #b[多人]#l\r\n";
				
				add += "                                        #L10##b上一页#l\r\n";
				add +="\r\n"
				add+= "   "+红色左下+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色右下+"\r\n";
				cm.sendSimple(add);

//------------------------------------------------------------------------

        }
		else if (status == 1) 
		{
			var 当前队伍 = cm.getPlayer().getParty();
			if (selection == 1) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(251010404,当前队伍);
						cm.dispose();
					}
				}
			}
			if (selection == 2) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(701010321,当前队伍);
						cm.dispose();
					}
				}
			}
			if (selection == 3) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(925020000,当前队伍);
						cm.dispose();
					}
				}
			}
			if (selection == 4) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(221024500,当前队伍);
						cm.dispose();
					}
				}
			}
			if (selection == 5) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(889100100,当前队伍);
						cm.dispose();
					}
				}
			}
			if (selection == 6) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(910320000,当前队伍);
						cm.dispose();
					}
				}
			}
			if (selection == 7) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(970030000,当前队伍);
						cm.dispose();
					}
				}
			}
						if (selection == 8) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(803000502,当前队伍);
						cm.dispose();
					}
				}
			}
									if (selection == 9) 
			{
				if(cm.getPlayer().getParty() == null) //队伍判断
				{
					cm.playerMessage(1, "你没有队伍，请先创建队伍或加入一个队伍！"); 
					cm.dispose();
				}
				else
				{
					if(!cm.是否队长())
					{
						cm.playerMessage(1, "你不是队长，叫队长来找我"); 
						cm.dispose();
					}
					else
					{
						cm.团队传送地图(261000011,当前队伍);
						cm.dispose();
					}
				}
			}
			if (selection == 10) 
			{
				cm.openNpc(9120025);
				cm.dispose();
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