
var v = "#fUI/GuildMark/Mark/Letter/00005021/4#";
var 哎 = "#fUI/GuildMark/Mark/Letter/00005008/4#";
var p = "#fUI/GuildMark/Mark/Letter/00005015/4#";
var 皇冠白 ="#fUI/GuildMark/Mark/Etc/00009004/15#";
var 幸运草 ="#fUI/GuildMark/Mark/Plant/00003006/15#";
var 香水 ="#fUI/GuildMark/Mark/Pattern/00004008/15#";
var 中条白 ="#fUI/Basic/HScr7/disabled/base#";
var 中条蓝 ="#fUI/ChatBalloon/tutorial/w#";

var 中条猫 ="#fUI/ChatBalloon/119/n#";
var 猫右 =  "#fUI/ChatBalloon/119/ne#";
var 猫左 =  "#fUI/ChatBalloon/119/nw#";
var 右 =    "#fUI/ChatBalloon/119/e#";
var 左 =    "#fUI/ChatBalloon/119/w#";
  
var 下条猫 ="#fUI/ChatBalloon/119/s#";
var 猫下右 ="#fUI/ChatBalloon/119/se#";
var 猫下左 ="#fUI/ChatBalloon/119/sw#";

var 彩虹1 ="#fUI/ChatBalloon/122/n#";
var 彩虹上1 =  "#fUI/ChatBalloon/122/ne#";
var 彩虹上2 =  "#fUI/ChatBalloon/122/nw#";
var 彩1 =    "#fUI/ChatBalloon/122/e#";
var 彩2 =    "#fUI/ChatBalloon/122/w#";
var 黑冠 = "#fUI/GuildMark.img/Mark/Etc/00009004/16#"; 
var 彩虹下 ="#fUI/ChatBalloon/122/s#";
var 彩虹下1 ="#fUI/ChatBalloon/122/se#";
var 彩虹下2 ="#fUI/ChatBalloon/122/sw#";
var 彩虹中 ="#fUI/ChatBalloon/122/head#";
var 金枫叶 ="#fMap/MapHelper/weather/maple/2#";
var 梅花 ="#fUI/GuildMark/Mark/Animal/00002008/14#";
var 蝴蝶 ="#fUI/GuildMark/Mark/Animal/00002020/14#";
var 星星 = "#fEffect/CharacterEff/1114000/2/0#";
var 爱心 = "#fEffect/CharacterEff/1022223/4/0#";
var 红色箭头 = "#fUI/UIWindow/Quest/icon6/7#";
var 正方形 = "#fUI/UIWindow/Quest/icon3/6#";
var 蓝色箭头 = "#fUI/UIWindow/Quest/icon2/7#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";//"+圆形+"
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 退出 = "#fUI/CN_Chat/ChattingRoom/BtExit/0/mouseOver/0#";
var 奖励 = "#fUI/UIWindow/Quest/reward#";
var 键盘 = "#fUI/UIWindow/SoftKeyboard/key/97/mouseOver/0#";
var 二段跳 = "#fSkill/411/skill/4111006/iconMouseOver#";
var 购买 = "#fUI/UIWindow/PersonalShop/BtBuy/mouseOver/0#";
var 钻石 = "#fUI/GuildMark/Mark/Animal/00002006/16#";
var 热点 = "#fUI/CashShop/CSCh ar/BtCoordination/mouseOver/0#";

var job = 0;
var type = -1;
var skill = [[8, 1004, 1013],[10000018, 10001004],[20000024, 20001004]];
function start() {
    status = -1;
    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        im.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {
            im.sendOk("感谢你的光临！");
            im.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
        var tex2 = ""; 
		if(im.getPlayer().getBossLog("新手礼包",1) == 0){
        var text  = "#b          #k"+黑冠+" #r#e梦幻Maplestory #d新手礼包#n#k "+黑冠+"\r\n\r\n#l"
			text += "         "+彩虹上2+""+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹中+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+""+彩虹上1+"\r\n";
			text += "                #L1##b#e点击领取新手礼包#b#l\r\n\r\n"
			text += "        "+彩虹下2+""+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+""+彩虹下1+"\r\n";
            im.sendSimple(text);
		}
		//if(im.getPlayer().getBossLog("新手礼包",1) == 1 && im.getPlayer().getBossLog("等级奖励",1) < 11 ){
        //var text  = "                  #d"+皇冠白+" - #r#e宿主箱子#d - "+皇冠白+"#n#l\r\n\r\n";
		//	text += "         "+彩虹上2+""+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹中+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+彩虹1+""+彩虹上1+"\r\n";
		//	text += "             #L2##b[#r等级奖励#b]#l  #L3##b[#r必做主线#b]#l\r\n\r\n"
		//if (im.getLevel() >= 120) {
		//	text += "             #L100##b[#r杀怪点数#b]#l  #L999##b[#r技能全满#b]#l\r\n\r\n"
		//}
		//	text += "         "+彩虹下2+""+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+彩虹下+""+彩虹下1+"\r\n";
        //text += "         #n#d[#b领取完10-150级的等级奖励后开启副职业#d]#l\r\n";
        //    im.sendSimple(text);
		//}
		//im.getPlayer().getBossLog("等级奖励",1) == 11 && //加在下面的IF里面
		if(im.getPlayer().getOneTimeLog("副职业炼丹师") == 0){
			im.getPlayer().setOneTimeLog("副职业炼丹师");
            im.sendOk("请重新打开");
            im.dispose();
		}
		if(im.getPlayer().getOneTimeLog("副职业炼丹师") == 1 || im.getPlayer().getOneTimeLog("副职业炼器师") == 1){
        var text  = "#b          #k"+黑冠+" #r#e梦幻Maplestory #d便捷箱子#n#k "+黑冠+"\r\n\r\n#l"
		if(im.getPlayer().getOneTimeLog("副职业炼器师") == 1){

		}
		    //text += "#b#e  10频道为全线PK频道！！！萌新进入需要谨慎~！！！\r\n"
		    text += " #L20#"+金枫叶+"#e#r回到自由#l#L21#万能传送#l#L22##r神秘地图#l#L23#赞助中心#k"+金枫叶+"#l\r\n\r\n";
            text += " #L24##k"+金枫叶+"等级奖励#l#L25#日常中心#l#L26##k装备回收#l#L27#杂货商店#k"+金枫叶+"#l\r\n\r\n";
	        text += " #L28##r"+金枫叶+"快速转职#l#L32#交易商行#l#L35#删除物品#l#L31#破攻系统#k"+金枫叶+"#l\r\n\r\n";
            text += " #L37##k"+金枫叶+"群宠技能#l#L38#点装商城#l#L34#双倍道具#l#L999#满 技 能#k"+金枫叶+"#l\r\n\r\n";
			im.sendSimple(text);
		}
        }else if(status == 1){
			if (selection == 1) {
				if (im.getPlayer().getBossLog("新手礼包",1) > 0){
					im.sendOk("你已经领取过新手入驻礼包！");
					im.dispose();
				}else if(!packageHave(1,7)){
					im.sendOk("装备栏空余不足7个空格！");
					im.dispose();
				}else if(!packageHave(2,2)){
					im.sendOk("消耗栏空余不足2个空格！");
					im.dispose();
				}else if(!packageHave(5,1)){
					im.sendOk("现金栏空余不足1个空格！");
					im.dispose();
				}else{
				    im.gainItem(5211060,1,1);//3倍经验一天
					if (im.getPlayer().getGender()==0){
						im.gainItem(1142477,10,10,10,10,200,200,10,10,10,10,10,10,10,10);//新手勋章
					}else{
						im.gainItem(1142477,10,10,10,10,200,200,10,10,10,10,10,10,10,10);//新手勋章
					}
					im.gainItem(2643114,20);//破功石
					im.gainItem(2370008,1);//兵法
					im.gainItem(4161001,-1);//特殊药水
			        im.gainMeso(1500000000);
					im.gainItem(2022466,2);//新手武器箱子
					im.gainItem(5030008,1);//精灵商店
					im.gainItem(1112941,1);//套装戒指
					im.gainItem(2022552,1);//随身仓库
					im.gainItem(2022468,1);//回收箱子
					im.gainItem(1112907,50,50,50,50,888,888,50,50,0,0,20,20,5,5,48);//小鱼戒指
					im.gainItem(1112907,50,50,50,50,888,888,50,50,0,0,20,20,5,5,48);//小鱼戒指
					im.gainItem(1112907,50,50,50,50,888,888,50,50,0,0,20,20,5,5,48);//小鱼戒指
					im.gainItem(1112907,50,50,50,50,888,888,50,50,0,0,20,20,5,5,48);//小鱼戒指
					im.gainItem(1112907,50,50,50,50,888,888,50,50,0,0,20,20,5,5,48);//小鱼戒指
					im.gainItem(1112907,50,50,50,50,888,888,50,50,0,0,20,20,5,5,48);//小鱼戒指
					im.gainNX(200000);
					im.gainDY(100000);
					
					im.getInventory(1).setSlotLimit(96);
					im.getInventory(2).setSlotLimit(96);
					im.getInventory(3).setSlotLimit(96); 
					im.getInventory(4).setSlotLimit(96); 
					im.getInventory(5).setSlotLimit(96); 
					//im.getPlayer().setLimitBreak(499999);
					job = im.getPlayer().getJob();
					if (job < 1000){// Adv(0 ~ 522)
						type = 0;
							im.teachSkill(1320008,25,25);
							im.teachSkill(1320009,25,25);
					} else if (job < 2000) {// Cy(1000 ~ 1512)
						type = 1;
					} else if (job < 3000) {// Aran(2000 ~ 2112)
						type = 2;
					} else {
						im.dispose();
						return;
					}
					for(var i = 0; i < skill[type].length;i++){
						var level = 1;
						if(i == 2) {
							level = 3;
						}
						im.teachSkill(skill[type][i], level);
						im.teachSkill(1007,3,3);
					}
					im.刷新状态();
					//im.used();//删除这个物品
					im.getPlayer().setBossLog('新手礼包',1,1);
					im.sendOk("领取成功！");
					im.dispose();
				}
			}else if(selection == 20){
				im.dispose();
				im.warp(910000000)	
				
			}else if(selection == 21){
				im.dispose();
				im.openNpc(9900004,1)
				
			}else if(selection == 22){
				im.dispose();
				im.warp(910000007);
			    //im.喇叭(2,"玩家["+cm.getName()+"]进入了神秘地图！！！超丰富内容由此打开！！！");

			}else if(selection == 23){
				im.dispose();
				im.openNpc(9900004,1110001)

            }else if(selection == 24){
				im.dispose();
				im.openNpc(9900004,1110006)				
				
			}else if(selection == 25){
				im.dispose();
				im.openNpc(9900004,4)		

			}else if(selection == 26){
				im.dispose();
				im.openNpc(9100200,1)	

			}else if(selection == 27){
				im.dispose();
				im.openShop(7)

			}else if(selection == 28){
				im.dispose();
				im.openNpc(9050001,1)	

			}else if(selection == 29){
				im.dispose();
				im.openNpc(1063006,0)	

			}else if(selection == 30){
				im.dispose();
				im.openNpc(9900004,1110006)	

			}else if(selection == 31){
				im.dispose();
				im.openNpc(9900004,8100)	

			}else if(selection == 32){
				im.dispose();
				im.openNpc(9030100,0)	

			}else if(selection == 33){
				im.dispose();
				im.openNpc(9900004,745)	

			}else if(selection == 34){
				im.dispose();
				im.openNpc(1052015,0)	

			}else if(selection == 35){
				im.dispose();
				im.openNpc(9900004,81100)	

			}else if(selection == 36){
				im.dispose();
				im.openNpc(1063010,30)	

			}else if(selection == 37){
				im.dispose();
				im.openNpc(9900004,6002)	

			}else if(selection == 38){
				im.dispose();
				im.openNpc(9900004,6001)	

			}else if(selection == 39){
				im.dispose();
				im.openNpc(9900004,19998)	
							
			}else if(selection == 4){
			warp = 4;
			im.sendYesNo(""+热点+":【#r炼丹师#k】\r\n\r\n"+圆形+" #d炼丹师可以炼制出[#r5#d]种级别的丹药,#b[都可买卖]#d\r\n"+圆形+" 对应给自身增加#r [2,4,6,10,15] #d点属性\r\n"+圆形+" #d炼丹师自身分为五级每次升级可提高炼丹成功率\r\n"+圆形+" #d每次炼丹需要消耗#r20000#d杀怪点以及#r5000#d万金币\r\n\r\n"+感叹号+" 副职业角色只能选一个,选了#r[炼丹师]#d就不能选#r[炼器师]\r\n"+感叹号+" #k考虑清楚了吗?消耗#k[#r"+im.getPlayer().getAccountLog("杀怪点数",1)+"#k/#b30000#k]#k万点杀怪点数转职");
			
            }else if (selection == 999) {
		    warp = 999;
		    im.sendYesNo(""+圆形+" 尊敬的 [#b 梦幻玩家 #k]\r\n\r\n( #b选择技能全满#k )\r\n");
		    
			}else if(selection == 5){
			warp = 5;
			im.sendYesNo(""+热点+":【#r炼器师#k】\r\n\r\n"+圆形+" #d炼器师可以炼制出[#r5#d]种级别的法器,#b[都可买卖]#d\r\n"+圆形+" 对应给装备增加#r [1.2.3.4.5] #d点攻击\r\n"+圆形+" #d炼器师自身分为五级每次升级可提高炼器成功率\r\n"+圆形+" #d每次炼器需要消耗#r20000#d杀怪点以及#r1#d亿金币\r\n\r\n"+感叹号+" 副职业角色只能选一个,选了#r[炼器师]#d就不能选#r[炼丹师]\r\n"+感叹号+" #k考虑清楚了吗?消耗#k[#r"+im.getPlayer().getAccountLog("杀怪点数",1)+"#k/#b30000#k]#k点杀怪点数转职");
		
			}
		
		}else if(status == 2) {
			if(warp == 4){
	            if (im.getPlayer().getOneTimeLog("副职业炼器师") == 1) {
	                im.sendOk("你已经选择了炼器师了!");
					im.dispose();
	            return false;
	            }
				if(im.getPlayer().getAccountLog("杀怪点数",1) >= 3000){ //物品条件
			       im.getPlayer().getAccountLog("杀怪点数",1);
			       im.getPlayer().setAccountLog("杀怪点数",1,-30000);
				   im.getPlayer().setOneTimeLog("副职业炼丹师");
	               im.sendOk("成功选择炼丹师");
				   /*Packages.handling.world..World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice
				   (9,im.getC().getChannel(),"副职业" + " : " + im.getPlayer().getName() +"：成功选择了炼丹师",true));*/
				   im.dispose();
				}else{
				   im.sendOk("杀怪点数不足3万");
				   im.dispose();
				}
		    }else if(warp == 5){
	            if (im.getPlayer().getOneTimeLog("副职业炼丹师") == 1) {
	                im.sendOk("你已经选择了炼丹师了!");
					im.dispose();
	            return false;
	            }
				if(im.getPlayer().getAccountLog("杀怪点数",1) >= 30000){ //物品条件
			       im.getPlayer().getAccountLog("杀怪点数",1);
			       im.getPlayer().setAccountLog("杀怪点数",1,-30000);
				   im.getPlayer().setOneTimeLog("副职业炼器师");
	               im.sendOk("成功选择炼器师");
				   /*Packages.handling.world..World.Broadcast.broadcastMessage(Packages.tools.MaplePacketCreator.serverNotice
				   (9,im.getC().getChannel(),"副职业" + " : " + im.getPlayer().getName() +"：成功选择了炼器师",true));*/
				   im.dispose();
				}else{
				   im.sendOk("杀怪点数不足3万");
				   im.dispose();
		
		        }
		    }else if(warp == 999){
			if(im.getPlayer().getOneTimeLog("购买技能全满") > 99 ){ 
			   im.sendOk("角色只能购买一次");
			   im.dispose();
	        return false;
			}
			if(im.getPlayer().getAccountLog("杀怪点数",1) >= 0){ 
			im.teachSkill(21000000,10,10); //矛连击强化
			im.teachSkill(21001001,15,15); //战斗步伐
			im.teachSkill(21000002,20,20); //双重重击
			im.teachSkill(21001003,20,20); //快速矛
			im.teachSkill(21100000,20,20); //精准矛
			im.teachSkill(21100001,20,20); //三重重击
			im.teachSkill(21100002,30,30); //战神突进
			im.teachSkill(21101003,20,20); //抗压
			im.teachSkill(21100004,20,20); //斗气爆裂
			im.teachSkill(21100005,20,20); //连环吸血
			im.teachSkill(21110000,20,20); //爆击强化
			im.teachSkill(21111001,20,20); //灵巧击退
			im.teachSkill(21110002,20,20); //全力挥击
			im.teachSkill(21110003,30,30); //终极投掷
			im.teachSkill(21110004,30,30); //幻影狼牙
			im.teachSkill(21111005,20,20); //冰雪矛
			im.teachSkill(21110006,20,20); //旋风
			im.teachSkill(21110007,20,20); //全力挥击
			im.teachSkill(21110008,20,20); //全力挥击
			im.teachSkill(21121000,30,30); //回顾冒险岛勇士
			im.teachSkill(21120001,30,30); //攻击策略
			im.teachSkill(21120002,30,30); //战神之舞
			im.teachSkill(21121003,30,30); //战神的意志
			im.teachSkill(21120004,30,30); //防守策略
			im.teachSkill(21120005,30,30); //巨熊咆哮
			im.teachSkill(21120006,30,30); //真神星辰
			im.teachSkill(21120007,30,30); //战神之盾
			im.teachSkill(21121008,5,5); //勇士的意志
			im.teachSkill(1121011,1,1);
			im.teachSkill(1221012,1,1);
			im.teachSkill(1321010,1,1);
			im.teachSkill(2121008,1,1);
			im.teachSkill(2221008,1,1);
			im.teachSkill(2321009,1,1);
			im.teachSkill(3121009,1,1);
			im.teachSkill(3221008,1,1);
			im.teachSkill(4121009,1,1);
			im.teachSkill(4221008,1,1); //End of max-level "1" skills
			im.teachSkill(1000002,8,8); //Start of max-level "8" skills
			im.teachSkill(3000002,8,8);
			im.teachSkill(4000001,8,8); //End of max-level "8" skills
			im.teachSkill(1000001,10,10); //Start of max-level "10" skills
			im.teachSkill(2000001,10,10); //End of max-level "10" skills
			im.teachSkill(1000000,16,16); //Start of max-level "16" skills
			im.teachSkill(2000000,16,16);
			im.teachSkill(3000000,16,16); //End of max-level "16" skills
			im.teachSkill(1001003,20,20); //Start of max-level "20" skills
			im.teachSkill(3200001,30,30);
			im.teachSkill(1001004,20,20);
			im.teachSkill(1001005,20,20);
			im.teachSkill(2001002,20,20);
			im.teachSkill(2001003,20,20);
			im.teachSkill(2001004,20,20);
			im.teachSkill(2001005,20,20);
			im.teachSkill(3000001,20,20);
			im.teachSkill(3001003,20,20);
			im.teachSkill(3001004,20,20);
			im.teachSkill(3001005,20,20);
			im.teachSkill(4000000,20,20);
			im.teachSkill(4001344,20,20);
			im.teachSkill(4001334,20,20);
			im.teachSkill(4001002,20,20);
			im.teachSkill(4001003,20,20);
			im.teachSkill(1101005,20,20);
			im.teachSkill(1100001,20,20); //Start of mastery's
			im.teachSkill(1100000,20,20);
			im.teachSkill(1200001,20,20);
			im.teachSkill(1200000,20,20);
			im.teachSkill(1300000,20,20);
			im.teachSkill(1300001,20,20);
			im.teachSkill(3100000,20,20);
			im.teachSkill(3200000,20,20);
			im.teachSkill(4100000,20,20);
			im.teachSkill(4200000,20,20); //End of mastery's
			im.teachSkill(4201002,20,20);
			im.teachSkill(4101003,20,20);
			im.teachSkill(3201002,20,20);
			im.teachSkill(3101002,20,20);
			im.teachSkill(1301004,20,20);
			im.teachSkill(1301005,20,20);
			im.teachSkill(1201004,20,20);
			im.teachSkill(1201005,20,20);
			im.teachSkill(1101004,20,20); //End of boosters
			im.teachSkill(1101006,20,20);
			im.teachSkill(1201006,20,20);
			im.teachSkill(1301006,20,20);
			im.teachSkill(2101001,20,20);
			im.teachSkill(2100000,20,20);
			im.teachSkill(2101003,20,20);
			im.teachSkill(2101002,20,20);
			im.teachSkill(2201001,20,20);
			im.teachSkill(2200000,20,20);
			im.teachSkill(2201003,20,20);
			im.teachSkill(2201002,20,20);
			im.teachSkill(2301004,20,20);
			im.teachSkill(2301003,20,20);
			im.teachSkill(2300000,20,20);
			im.teachSkill(2301001,20,20);
			im.teachSkill(3101003,20,20);
			im.teachSkill(3101004,20,20);
			im.teachSkill(3201003,20,20);
			im.teachSkill(3201004,20,20);
			im.teachSkill(4100002,20,20);
			im.teachSkill(4101004,20,20);
			im.teachSkill(4200001,20,20);
			im.teachSkill(4201003,20,20); //End of second-job skills and first-job
			im.teachSkill(4211005,20,20);
			im.teachSkill(4211003,20,20);
			im.teachSkill(4210000,20,20);
			im.teachSkill(4110000,20,20);
			im.teachSkill(4111001,20,20);
			im.teachSkill(4111003,20,20);
			im.teachSkill(3210000,20,20);
			im.teachSkill(3110000,20,20);
			im.teachSkill(3210001,20,20);
			im.teachSkill(3110001,20,20);
			im.teachSkill(3211002,20,20);
			im.teachSkill(3111002,20,20);
			im.teachSkill(2210000,20,20);
			im.teachSkill(2211004,20,20);
			im.teachSkill(2211005,20,20);
			im.teachSkill(2111005,20,20);
			im.teachSkill(2111004,20,20);
			im.teachSkill(2110000,20,20);
			im.teachSkill(2311001,20,20);
			im.teachSkill(2311005,30,30);
			im.teachSkill(2310000,20,20);
			im.teachSkill(1311007,20,20);
			im.teachSkill(1310000,20,20);
			im.teachSkill(1311008,20,20);
			im.teachSkill(1210001,20,20);
			im.teachSkill(1211009,20,20);
			im.teachSkill(1210000,20,20);
			im.teachSkill(1110001,20,20);
			im.teachSkill(1111007,20,20);
			im.teachSkill(1110000,20,20); //End of 3rd job skills
			im.teachSkill(1121000,20,20);
			im.teachSkill(1221000,20,20);
			im.teachSkill(1321000,20,20);
			im.teachSkill(2121000,20,20);
			im.teachSkill(2221000,20,20);
			im.teachSkill(2321000,20,20);
			im.teachSkill(3121000,20,20);
			im.teachSkill(3221000,20,20);
			im.teachSkill(4121000,20,20);
			im.teachSkill(4221000,20,20); //End of Maple Warrior // Also end of max-level "20" skills
			im.teachSkill(1321007,10,10);
			im.teachSkill(1320009,25,25);
			im.teachSkill(1320008,25,25);
			im.teachSkill(2321006,10,10);
			im.teachSkill(1220010,10,10);
			im.teachSkill(1221004,25,25);
			im.teachSkill(1221003,25,25);
			im.teachSkill(1100003,30,30);
			im.teachSkill(1100002,30,30);
			im.teachSkill(1101007,30,30);
			im.teachSkill(1200003,30,30);
			im.teachSkill(1200002,30,30);
			im.teachSkill(1201007,30,30);
			im.teachSkill(1300003,30,30);
			im.teachSkill(1300002,30,30);
			im.teachSkill(1301007,30,30);
			im.teachSkill(2101004,30,30);
			im.teachSkill(2101005,30,30);
			im.teachSkill(2201004,30,30);
			im.teachSkill(2201005,30,30);
			im.teachSkill(2301002,30,30);
			im.teachSkill(2301005,30,30);
			im.teachSkill(3101005,30,30);
			im.teachSkill(3201005,30,30);
			im.teachSkill(4100001,30,30);
			im.teachSkill(4101005,30,30);
			im.teachSkill(4201005,30,30);
			im.teachSkill(4201004,30,30);
			im.teachSkill(1111006,30,30);
			im.teachSkill(1111005,30,30);
			im.teachSkill(1111002,30,30);
			im.teachSkill(1111004,30,30);
			im.teachSkill(1111003,30,30);
			im.teachSkill(1111008,30,30);
			im.teachSkill(1211006,30,30);
			im.teachSkill(1211002,30,30);
			im.teachSkill(1211004,30,30);
			im.teachSkill(1211003,30,30);
			im.teachSkill(1211005,30,30);
			im.teachSkill(1211008,30,30);
			im.teachSkill(1211007,30,30);
			im.teachSkill(1311004,30,30);
			im.teachSkill(1311003,30,30);
			im.teachSkill(1311006,30,30);
			im.teachSkill(1311002,30,30);
			im.teachSkill(1311005,30,30);
			im.teachSkill(1311001,30,30);
			im.teachSkill(2110001,30,30);
			im.teachSkill(2111006,30,30);
			im.teachSkill(2111002,30,30);
			im.teachSkill(2111003,30,30);
			im.teachSkill(2210001,30,30);
			im.teachSkill(2211006,30,30);
			im.teachSkill(2211002,30,30);
			im.teachSkill(2211003,30,30);
			im.teachSkill(2311003,30,30);
			im.teachSkill(2311004,30,30);
			im.teachSkill(2311006,30,30);
			im.teachSkill(3111004,30,30);
			im.teachSkill(3111003,30,30);
			im.teachSkill(3111005,30,30);
			im.teachSkill(3111006,30,30);
			im.teachSkill(3211004,30,30);
			im.teachSkill(3211003,30,30);
			im.teachSkill(3211005,30,30);
			im.teachSkill(3211006,30,30);
			im.teachSkill(4111005,30,30);
			im.teachSkill(4111006,20,20);
			im.teachSkill(4111004,30,30);
			im.teachSkill(4111002,30,30);
			im.teachSkill(4211002,30,30);
			im.teachSkill(4211004,30,30);
			im.teachSkill(4211001,30,30);
			im.teachSkill(4211006,30,30);
			im.teachSkill(1120004,30,30);
			im.teachSkill(1120003,30,30);
			im.teachSkill(1120005,30,30);
			im.teachSkill(1121000,30,30);
			im.teachSkill(1121008,30,30);
			im.teachSkill(1121010,30,30);
            im.teachSkill(1121011,5,5);
			im.teachSkill(1121006,30,30);
			im.teachSkill(1121002,30,30);
			im.teachSkill(1220005,30,30);
			im.teachSkill(1221009,30,30);
			im.teachSkill(1220006,30,30);
			im.teachSkill(1221007,30,30);
            im.teachSkill(1221012,5,5);
			im.teachSkill(1221002,30,30);
			im.teachSkill(1221000,30,30);
			im.teachSkill(1320005,30,30);
			im.teachSkill(1320006,30,30);
			im.teachSkill(1321003,30,30);
			im.teachSkill(1321002,30,30);
			im.teachSkill(1321000,30,30);
            im.teachSkill(1321010,5,5);
			im.teachSkill(2121005,30,30);
			im.teachSkill(2121003,30,30);
			im.teachSkill(2121004,30,30);
			im.teachSkill(2121002,30,30);
			im.teachSkill(2121007,30,30);
			im.teachSkill(2121006,30,30);
            im.teachSkill(2121008,5,5);
			im.teachSkill(2121000,30,30);
			im.teachSkill(2221007,30,30);
			im.teachSkill(2221006,30,30);
			im.teachSkill(2221003,30,30);
			im.teachSkill(2221005,30,30);
			im.teachSkill(2221004,30,30);
			im.teachSkill(2221002,30,30);
            im.teachSkill(2221008,5,5);
			im.teachSkill(2221000,30,30);
			im.teachSkill(2321007,30,30);
			im.teachSkill(2321003,30,30);
			im.teachSkill(2321008,30,30);
            im.teachSkill(2321009,5,5);
			im.teachSkill(2321005,30,30);
			im.teachSkill(2321004,30,30);
			im.teachSkill(2321000,30,30);
			im.teachSkill(2321002,30,30);
			im.teachSkill(3120005,30,30);
			im.teachSkill(3121008,30,30);
			im.teachSkill(3121000,30,30);
            im.teachSkill(3121009,5,5);
			im.teachSkill(3121003,30,30);
			im.teachSkill(3121007,30,30);
			im.teachSkill(3121006,30,30);
            im.teachSkill(3121000,30,30);
			im.teachSkill(3121002,30,30);
			im.teachSkill(3121004,30,30);
			im.teachSkill(3221006,30,30);
			im.teachSkill(3221000,30,30);
			im.teachSkill(3220004,30,30);
            im.teachSkill(3221000,30,30);
			im.teachSkill(3221003,30,30);
			im.teachSkill(3221005,30,30);
			im.teachSkill(3221001,30,30);
			im.teachSkill(3221002,30,30);
			im.teachSkill(3221007,30,30);
            im.teachSkill(3221008,5,5);
			im.teachSkill(4121004,30,30);
			im.teachSkill(4121008,30,30);
			im.teachSkill(4121000,30,30);
			im.teachSkill(4121003,30,30);
			im.teachSkill(4121006,30,30);
			im.teachSkill(4121007,30,30);
            im.teachSkill(4121000,30,30);
            im.teachSkill(4121009,5,5);
			im.teachSkill(4221001,30,30);
			im.teachSkill(4221007,30,30);
			im.teachSkill(4221004,30,30);
            im.teachSkill(4221000,30,30);  
			im.teachSkill(4221000,30,30);
			im.teachSkill(4221003,30,30);
			im.teachSkill(4221006,30,30);
            im.teachSkill(4221008,5,5);
			im.teachSkill(1321001,30,30);
			im.teachSkill(4120002,30,30);
			im.teachSkill(2221001,30,30);
			im.teachSkill(3100001,30,30);
			im.teachSkill(1121001,30,30);
			im.teachSkill(1221001,30,30);
			im.teachSkill(2121001,30,30);
			im.teachSkill(2221001,30,30);
			im.teachSkill(2321001,30,30);
			im.teachSkill(4220002,30,30);
			im.teachSkill(5000000,20,20); //Bullet Time
			im.teachSkill(5001001,20,20); //Flash Fist
			im.teachSkill(5001002,20,20); //Sommersault Kick
			im.teachSkill(5001003,20,20); //Double Shot
			im.teachSkill(5001005,10,10); //Dash
			im.teachSkill(5100000,10,10); //Improve MaxHP
			im.teachSkill(5100001,20,20); //Knuckler Mastery
			im.teachSkill(5101002,20,20); //Backspin Blow
			im.teachSkill(5101003,20,20); //Double Uppercut
			im.teachSkill(5101004,20,20); //Corkscrew Blow
			im.teachSkill(5101005,10,10); //MP Recovery
			im.teachSkill(5101006,20,20); //Knuckler Booster
			im.teachSkill(5101007,10,10); //Oak Barrel
			im.teachSkill(5200000,20,20); //Gun Mastery
			im.teachSkill(5201001,20,20); //Invisible Shot
			im.teachSkill(5201002,20,20); //Grenade
			im.teachSkill(5201003,20,20); //Gun Booster
			im.teachSkill(5201004,20,20); //Blank Shot
			im.teachSkill(5201005,10,10); //Wings
			im.teachSkill(5201006,20,20); //Recoil Shot
			im.teachSkill(5110000,20,20); //Stun Mastery
			im.teachSkill(5110001,40,40); //Energy Charge
			im.teachSkill(5111002,30,30); //Energy Blast
			im.teachSkill(5111004,20,20);  //Energy Drain
			im.teachSkill(5111005,20,20); //Transformation
			im.teachSkill(5210000,20,20); //Burst Fire
			im.teachSkill(5211001,30,30); //Octopus
			im.teachSkill(5211002,30,30); //Gaviota
			im.teachSkill(5211004,30,30); //FlameThrower
			im.teachSkill(5211005,30,30); //Ice Splitter
			im.teachSkill(5211006,30,30); //Homing Beacon
			im.teachSkill(5121000,30,30); //Maple Warrior
			im.teachSkill(5121001,30,30); //Dragon Strike
			im.teachSkill(5121002,30,30); //Energy Orb
			im.teachSkill(5121003,20,20); //Super Transformation
			im.teachSkill(5121004,30,30); //Demolition
			im.teachSkill(5121005,30,30); //Snatch
            im.teachSkill(5111006,30,30); //Caonima
			im.teachSkill(5121007,30,30); //Barrage
			im.teachSkill(5121008,30,30);   //Pirate's Rage
			im.teachSkill(2311002,30,30);   //时空门
			im.teachSkill(5121009,20,20); //Speed Infusion
			im.teachSkill(5121010,30,30); //Time Leap
			im.teachSkill(5221000,30,30); //Maple Warrior
			im.teachSkill(5220001,30,30); //Elemental Boost
			im.teachSkill(5220002,20,20); //Wrath of the Octopi
			im.teachSkill(5221003,30,30); //Aerial Strike
			im.teachSkill(5221004,30,30); //Rapid Fire
			im.teachSkill(5221006,10,10); //BattleShip
			im.teachSkill(5221007,30,30); //BattleShip Cannon
			im.teachSkill(5221008,30,30); //BattleShop Torpedo
			im.teachSkill(5221009,20,20); //Hypnotize
			im.teachSkill(5221010,25,25); //Speed Infusion
			im.teachSkill(5220011,20,20); //BullsEye
 //魂骑士
			im.teachSkill(11000000,10,10); //生命加强
			im.teachSkill(11001001,10,10); //圣甲术
			im.teachSkill(11001002,20,20); //强力攻击
			im.teachSkill(11001003,20,20); //群体攻击
			im.teachSkill(11001004,20,20); //魂精灵
			im.teachSkill(11100000,20,20); //精准剑
			im.teachSkill(11101001,20,20); //快速剑
			im.teachSkill(11101002,30,30); //终极剑
			im.teachSkill(11101003,20,20); //愤怒之火
			im.teachSkill(11101004,30,30); //灵魂之刃
			im.teachSkill(11101005,10,10); //灵魂迅移
			im.teachSkill(11110000,20,20); //魔力恢复
			im.teachSkill(11111001,20,20); //斗气集中
			im.teachSkill(11111002,20,20); //恐慌
			im.teachSkill(11111003,20,20); //昏迷
			im.teachSkill(11111004,30,30); //轻舞飞扬
			im.teachSkill(11110005,20,20); //进阶斗气
			im.teachSkill(11111006,30,30); //灵魂突刺
			im.teachSkill(11111007,20,20); //灵魂属性
 //炎术士
			im.teachSkill(12000000,10,10); //魔力强化
			im.teachSkill(12001001,10,10); //魔法盾
			im.teachSkill(12001002,10,10); //魔法铠甲
			im.teachSkill(12001003,20,20); //魔法双击
			im.teachSkill(12001004,20,20); //炎精灵
			im.teachSkill(12101000,20,20); //精神力
			im.teachSkill(12101001,20,20); //缓速术
			im.teachSkill(12101002,20,20); //火焰箭
			im.teachSkill(12101003,20,20); //快速移动
			im.teachSkill(12101004,20,20); //魔法狂暴
			im.teachSkill(12101005,20,20); //自然力重置
			im.teachSkill(12101006,20,20); //火柱
			im.teachSkill(12110000,20,20); //魔法抗性
			im.teachSkill(12110001,20,20); //魔力激化
			im.teachSkill(12111002,20,20); //封印术
			im.teachSkill(12111003,20,20); //天降落星
			im.teachSkill(12111004,10,10); //火魔兽
			im.teachSkill(12111005,30,30); //火牢术屏障
			im.teachSkill(12111006,30,30); //火风暴
			im.teachSkill(13000000,20,20); //强力箭
			im.teachSkill(13000001,8,8); //远程箭
			im.teachSkill(13001002,10,10); //集中术
			im.teachSkill(13001003,20,20); //二连射
			im.teachSkill(13001004,20,20); //风精灵
			im.teachSkill(13100000,20,20); //精准弓
			im.teachSkill(13101001,20,20); //快速箭
			im.teachSkill(13101002,30,30); //终极弓
			im.teachSkill(13101003,20,20); //无形箭
			im.teachSkill(13100004,20,20); //疾风步
			im.teachSkill(13101005,20,20); //暴风射击
			im.teachSkill(13101006,10,10); //风影漫步
			im.teachSkill(13111000,20,20); //箭雨
			im.teachSkill(13111001,30,30); //箭扫射
			im.teachSkill(13111002,20,20); //暴风箭雨
			im.teachSkill(13110003,20,20); //神箭手
			im.teachSkill(13111004,20,20); //替身术
			im.teachSkill(13111005,10,10); //信天翁
			im.teachSkill(13111006,20,20); //风灵穿越
			im.teachSkill(13111007,20,20); //疾风扫射
			im.teachSkill(14000000,10,10); //集中术
			im.teachSkill(14000001,8,8); //远程暗器
			im.teachSkill(14001002,10,10); //诅咒术
			im.teachSkill(14001003,10,10); //隐身术
			im.teachSkill(14001004,20,20); //双飞斩
			im.teachSkill(14001005,20,20); //夜精灵
			im.teachSkill(14100000,20,20); //精准暗器
			im.teachSkill(14100001,30,30); //强力投掷
			im.teachSkill(14101002,20,20); //快速暗器
			im.teachSkill(14101003,20,20); //轻功
			im.teachSkill(14101004,20,20); //二段跳
			im.teachSkill(14100005,10,10); //驱逐
			im.teachSkill(14101006,20,20); //吸血
			im.teachSkill(14111000,30,30); //影分身
			im.teachSkill(14111001,20,20); //影网术
			im.teachSkill(14111002,30,30); //多重飞镖
			im.teachSkill(14110003,20,20); //药剂精通
			im.teachSkill(14110004,20,20); //武器用毒液
			im.teachSkill(14111005,20,20); //三连环光击破
			im.teachSkill(14111006,30,30); //毒炸弹
			im.teachSkill(15000000,10,10); //快动作
			im.teachSkill(15001001,20,20); //百裂拳
			im.teachSkill(15001002,20,20); //半月踢
			im.teachSkill(15001003,10,10); //疾驰
			im.teachSkill(15001004,20,20); //雷精灵
			im.teachSkill(15100000,10,10); //强体术
			im.teachSkill(15100001,20,20); //精准拳
			im.teachSkill(15101002,20,20); //急速拳
			im.teachSkill(15101003,20,20); //贯骨击
			im.teachSkill(15100004,20,20); //能量获得
			im.teachSkill(15101005,20,20); //能量爆破
			im.teachSkill(15101006,20,20); //雷鸣
			im.teachSkill(15110000,20,20); //必杀拳
			im.teachSkill(15111001,20,20); //能量耗转
			im.teachSkill(15111002,10,10); //超人变形
			im.teachSkill(15111003,20,20); //碎石乱击
			im.teachSkill(15111004,20,20); //光速拳
			im.teachSkill(15111005,20,20); //极速领域
			im.teachSkill(15111006,20,20); //闪光击
			im.teachSkill(15111007,30,30); //鲨鱼波
			   im.刷新状态();
			   im.getPlayer().getAccountLog("杀怪点数",1);
			   im.getPlayer().setAccountLog("杀怪点数",1,-0);
			   im.getPlayer().setOneTimeLog("购买技能全满");
			   im.sendOk("你的技能以全满");
			   //im.全服漂浮喇叭("恭喜玩家["+im.getName()+"]购买了[ 技能全满 ] 技能已经全部帮你加满了！", 5121006);
	           im.dispose();
			}else{
			   im.sendOk("你的点数不足50000");
			   im.dispose();
	        }
			}
		   }
		}
    }
	

function packageHave(type,number){
	var object = -1;
	var count = 0;
	var flag = true;
//	for(var i = 0;i<32;i++){
//		object = im.getInventory(type).getItem(i);
//		if(null == object){
//			count++;
//			if(count>=number){
//				flag = false;
//				break;
//			}
//		}
//	}
	return flag;
}

/*
	}else if(selection == 10000){
		if (im.getInventory(4).isFull(29) ) {
		    im.sendOk("#b其他栏空间不足30格.");	
	           im.dispose();
	       return false;
		}
		im.gainExp(50000);
		if(im.getPlayer().getLevel() <= 230){
		im.getChar().setLevel(250);
		im.gainAp(1250);
		}
		im.gainSp(500);
		im.gainNX(50000);
		im.gainMeso(888888888);
		im.gainItem(4310154,10000);
		im.gainItem(2049100,10);
		im.gainItem(2340000,10);
		im.gainItem(2049306,10);
		im.gainItem(2049415,10);
		if(im.getPlayer().getBossLog("lqcs1") < 2){
		im.gainItem(4000435,999);
		im.gainItem(4000460,999);
		im.gainItem(4000461,999);
		im.gainItem(4000462,999);
		im.gainItem(4000243,999);
		im.gainItem(4000235,999);
		im.gainItem(2210006,10);
		im.gainItem(4031475,999);
		im.gainItem(4000382,999);
		im.gainItem(4000379,999);
		im.gainItem(4000383,999);
		im.gainItem(4000431,999);
		im.gainItem(4000430,999);
		im.gainItem(4000020,999);
		im.gainItem(4000021,999);
		im.gainItem(4000040,999);
		im.gainItem(4000478,999);
		im.gainItem(4000037,999);
		im.gainItem(4000107,999);
		im.gainItem(4000106,999);
		im.gainItem(4000025,999);
		im.gainItem(4000177,999);
		im.gainItem(4003004,999);
		im.gainItem(4020008,999);
		im.gainItem(4000429,999);
		im.gainItem(4001111,999);
		im.gainItem(4000000,999);
		im.gainItem(4000016,999);
		im.gainItem(4000019,999);
		im.gainItem(4031026,999);
		im.gainItem(4000176,999);
		im.gainItem(4031227,999);
		im.gainItem(4000245,999);
		im.gainItem(4000244,999);
		}
		im.setBossLog("月秒副本");
		im.setBossLog("废弃副本");
		im.setBossLog("玩具副本");
		im.setBossLog("毒物副本");
		im.setBossLog("海盗副本");
		im.setBossLog("月秒副本");
		im.setBossLog("废弃副本");
		im.setBossLog("玩具副本");
		im.setBossLog("毒物副本");
		im.setBossLog("海盗副本");
		im.getPlayer().setAccountLog('在线时间');
		im.getPlayer().setAccountLog('在线时间');
		im.getPlayer().setAccountLog('在线时间');
		im.getPlayer().setBossLog("lqcs1")
		im.刷新状态();
		
	    im.dispose();
			*/