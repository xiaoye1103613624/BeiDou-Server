/* var 美化1 = "#fUI/ChatBalloon.img/pet/120/nw#";//选择道具
var 美化3 = "#fUI/ChatBalloon.img/pet/120/ne#";//选择道具
var 美化2 = "#fUI/ChatBalloon.img/pet/120/n#";//选择道具
var 美化4 = "#fUI/ChatBalloon.img/pet/120/sw#";//选择道具
var 美化5 = "#fUI/ChatBalloon.img/pet/120/se#";//选择道具
var 美化6 = "#fUI/ChatBalloon.img/pet/120/s#";//选择道具
var 美化7 = "#fUI/ChatBalloon.img/156/arrow#";//选择道具 */
var mmm = "#fUI/PredictHarmony.img/card/13#";
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
var X方形图标 = "#fUI/Basic.img/BtClose/mouseOver/0#";
var X黑白方形图标 = "#fUI/Basic.img/BtClose2/normal/0#";


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
			
			
				var add = "              "+标题左+"  #e#d 个人常规挑战 #k#n  #r "+标题右+"#b#k#n\r\r\n";
				add += "                  #k今日挑战: #r["+cm.getBossLog("每日个人挑战BOSS")+"/10] \r\n";
				add += "          #r[ #b无论成功与否 都会消耗挑战次数 #r]\r\n\r\n";
				add += "   "+红色左上+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色上中+红色右上+"\r\n";
				
				add += "    #L1##d"+X黑白方形图标+"#r[折磨]#d 觉醒希拉#l #L2##d"+X黑白方形图标+"#r[折磨]#d 普通希拉#l\r\n" ;
				
				add += "    #L3##d"+X黑白方形图标+"#r[超难]#d 斯乌#l     #L4##d"+X黑白方形图标+"#r[超难]#d 郭敦尔#l\r\n";
				
				add += "    #L7##d"+X黑白方形图标+"#r[超难]#d 狮子王#l   #L8##d"+X黑白方形图标+"#r[超难]#d 进阶威尔#l\r\n";
				
				add += "    #L9##d"+X黑白方形图标+"#r[超难]#d 麦格纳斯#l #L10##d"+X黑白方形图标+"#r[超难]#d 戴米安#l\r\n";
				
				add += "    #L11##d"+X黑白方形图标+"#r[超难]#d 异变植物#l #L14##d"+X黑白方形图标+"#b[困难]#d 始皇帝#l\r\n";
				
				add += "    #L15##d"+X黑白方形图标+"#b[困难]#d 女皇#l     #L16##d"+X黑白方形图标+"#b[困难]#d 桃乐丝#l\r\n";
				
				add += "    #L17##d"+X黑白方形图标+"#b[困难]#d 阿卡伊勒#l #L18##d"+X黑白方形图标+"#b[困难]#d 火焰狼#l\r\n";
				
				add += "    #L19##d"+X黑白方形图标+"#b[困难]#d 2100#l     #L24##d"+X黑白方形图标+"#b[困难]#d 黑色之翼#l\r\n";
				
				add += "    #L21##d"+X黑白方形图标+"#b[困难]#d 东京战舰#l #L22##d"+X黑白方形图标+"#b[困难]#d 愤怒灵魂#l\r\n";
				
				add += "    #L23##d"+X黑白方形图标+"#b[困难]#d 外星飞船#l \r\n";
				
				add +="\r\n"
				add+= "   "+红色左下+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色下中+红色右下+"\r\n";
				//add += "    #L29#"+小金币+"#d 使用 #r5元 #d宝提升挑战次数  [5元宝+1次]\r\n";
				cm.sendSimple(add);

//------------------------------------------------------------------------

        }
		else if (status == 1) 
		{
			//觉醒希拉
			if(selection == 1){

				var 挑战地图ID = 993063039;
				var BossID=8880400;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=500)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,-19,184,挑战地图ID,58000000000);
										cm.setBossLog("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"一个人去挑战 觉醒希拉 去了，大家祝福他吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足500万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "今天的挑战次数已经到上限了"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//普通希拉
			if(selection == 2){

				var 挑战地图ID = 481000000;
				var BossID=8870000;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=200)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,-19,184,挑战地图ID,28000000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 普通希拉 去了，大家祝福他吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足200万，不能挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "今天的挑战次数已经到上限了！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
		
			//斯乌
			if(selection == 3){

				var 挑战地图ID = 350056009;
				var BossID=8240099;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,118,-24,挑战地图ID,14000000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 斯乌 去了，大家祝福他吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余次数不足！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
		
			//郭敦尔
			if(selection == 4){

				var 挑战地图ID = 450012210;
				var BossID=8645009;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,118,-24,挑战地图ID,11000000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 亲卫队长郭敦尔 去了，大家祝福他吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "挑战次数不足！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
	
			//路西德
			if(selection == 5){

				var 挑战地图ID = 882100004;
				var BossID=8880141;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,118,-24,挑战地图ID,10000000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 路西德 去了，大家祝福他吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//贝伦
			if(selection == 6){

				var 挑战地图ID = 105200410;
				var BossID=8930000;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,-194,443,挑战地图ID,8800000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 进阶贝伦 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "挑战次数不足！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
		
		
			//狮子王
			if(selection == 7){

				var 挑战地图ID = 970050110;
				var BossID=8840000;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,4,-181,挑战地图ID,6200000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 班·雷昂 狮子王 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//进阶威尔
			if(selection == 8){

				var 挑战地图ID = 927020050;
				var BossID=8880302;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,1355,69,挑战地图ID,8800000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 进阶威尔 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			//暴君麦格纳斯
			if(selection == 9){

				var 挑战地图ID = 401060100;
				var BossID=8880000;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,2452,-1347,挑战地图ID,8800000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 暴君麦格纳斯 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			//戴米安
			if(selection == 10){

				var 挑战地图ID = 350160460;
				var BossID=9390624;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,774,17,挑战地图ID,5200000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 戴米安 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "挑战次数不足！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//异变植物
			if(selection == 11){

				var 挑战地图ID = 910150220;
				var BossID=8860001;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,-274,-7,挑战地图ID,4300000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 异变植物 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//血腥女王
			if(selection == 12){

				var 挑战地图ID = 105200310;
				var BossID=8920001;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,35,135,挑战地图ID,3800000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 血腥女王 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//进阶半半
			if(selection == 13){

				var 挑战地图ID = 105200110;
				var BossID=8910000;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,-211,455,挑战地图ID,3800000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 进阶半半 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//始皇帝
			if(selection == 14){

				var 挑战地图ID = 745010500;
				var BossID=9410224;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,5,-14,挑战地图ID,2500000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 始皇帝 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//希纳斯女皇
			if(selection == 15){

				var 挑战地图ID = 932100004;
				var BossID=8850011;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,-199,115,挑战地图ID,2100000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 希纳斯女皇 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//桃乐丝
			if(selection == 16){

				var 挑战地图ID = 992050000;
				var BossID=9309207;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,-199,115,挑战地图ID,2100000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 桃乐丝 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//阿卡伊勒
			if(selection == 17){

				var 挑战地图ID = 932100002;
				var BossID=8860000;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,14,-181,挑战地图ID,2100000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 阿卡伊勒 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//火焰狼
			if(selection == 18){

				var 挑战地图ID = 555000110;
				var BossID=9101078;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,392,-26,挑战地图ID,1800000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 火焰狼 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//贝尔加莫特
			if(selection == 19){

				var 挑战地图ID = 802000211;
				var BossID=7220003;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,392,-26,挑战地图ID,1000000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 贝尔加莫特 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			//蜘蛛女王
			if(selection == 20){

				var 挑战地图ID = 240093300;
				var BossID=8800400;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,392,-26,挑战地图ID,1000000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 蜘蛛女王 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//东京战舰
			if(selection == 21){

				var 挑战地图ID = 861000000;
				var BossID=8220013;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,392,-26,挑战地图ID,1000000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 东京战舰 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//愤怒灵魂
			if(selection == 22){

				var 挑战地图ID = 682020000;
				var BossID=9001058;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,-59,88,挑战地图ID,1300000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 东京战舰 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//外星飞船
			if(selection == 23){

				var 挑战地图ID = 861000529;
				var BossID=8880200;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,484,32,挑战地图ID,2500000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 外星飞船 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//黑色之翼
			if(selection == 24){

				var 挑战地图ID = 350060913;
				var BossID=8240019;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=100)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,333,-16,挑战地图ID,2000000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 黑色之翼 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足100万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//品克缤
			if(selection == 25){

				var 挑战地图ID = 270050100;
				var BossID=8820008;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=20)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.spawnMobOnMap(BossID,1,16,-42,挑战地图ID,2100000000);
										cm.给团队每日("每日个人挑战BOSS");
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 品克缤 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足40万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//暗黑龙王
			if(selection == 26){

				var 挑战地图ID = 240060200;
				var BossID=8810026;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=20)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.给团队每日("每日个人挑战BOSS");
										cm.spawnMobOnMap(BossID,1,71,260,挑战地图ID);
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 暗黑龙王 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足40万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			//千年树精
			if(selection == 27){

				var 挑战地图ID = 541020800;
				var BossID=9420520;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=10)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{	
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.清怪();
										cm.清怪();
										cm.清怪();
										cm.清怪();
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.给团队每日("每日个人挑战BOSS");
										cm.spawnMobOnMap(BossID,1,-149,-270,挑战地图ID,300000000);
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 千年树精 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足30万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			//扎昆
			if(selection == 28){

				var 挑战地图ID = 280030000;
				var BossID=9420520;
				var 当前队伍 = cm.getPlayer().getParty();
				if(cm.getClient().getChannel() == 3 || cm.getClient().getChannel() == 4) //频道判断
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
							if(cm.判断团队每日("每日个人挑战BOSS") <=100)
							{
								if(cm.getPlayer().GetCombat() >=10)
								{
									if(cm.判断指定地图玩家数量(挑战地图ID) <=0)
									{	
										cm.团队传送地图(挑战地图ID,当前队伍);
										cm.清怪();
										cm.清怪();
										cm.清怪();
										cm.清怪();
										cm.清除地图物品(挑战地图ID);
										cm.playerMessage(1, "猎杀时刻！"); 
										cm.给团队每日("每日个人挑战BOSS");
										cm.getMap().spawnZakum(-10, -215);
										cm.喇叭(2,""+cm.getPlayer().getName()+"个人去挑战 扎昆 去了，大家祝福他们吧！")
										cm.dispose();
									}
									else
									{
										cm.playerMessage(1, "这个副本有人，换线或者等一会！"); 
										cm.dispose();
									}
								}
								else
								{
									cm.playerMessage(1, cm.getPlayer().GetCombat()+"破功不足30万，不能带队挑战这个BOSS"); 
									cm.dispose();
								}	
							}
							else
							{
								cm.playerMessage(1, "剩余挑战次数不足！！"); 
								cm.dispose();
							}
						}
					}
				}
				else
				{
						cm.playerMessage(1, "你现在在"+cm.getClient().getChannel()+"频道，请到3频道或4频道"); 
						cm.dispose();
				}
			}
			
			if(selection == 29)
			{
				if(cm.getmoneyb() >= 5)
				{
					if(cm.getBossLog("元宝提升次数")<=10)
					{
						cm.setBossLog("每日个人挑战BOSS",1,-1);
						cm.setBossLog("元宝提升次数");
						cm.setmoneyb(-5);
						cm.playerMessage(1, "成功提升1次挑战次数"); 
						cm.dispose();
					}
					else
					{
						cm.playerMessage(1, "提升次数已达上限,不能再提升了"); 
						cm.dispose();
					}
				}
				else
				{
						cm.playerMessage(1, "你的元宝不足 5 个"); 
						cm.dispose();
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