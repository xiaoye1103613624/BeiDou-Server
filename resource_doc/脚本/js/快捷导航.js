var ca = java.util.Calendar.getInstance();
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE);//获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒

var 红色指向 = "#fUI/UIWindow/Item/BtFull/pressed/0#";
var 大箭头 ="#fUI/Basic/BtHide3/mouseOver/0#";
var 草莓5 = "#fUI/GuildMark/Mark/Plant/00003000/8#" // 绿色草莓
//var 时装天堂 ="#fUI/UIWindow/AdminClaim/BtCClaim/normal/0#"

var CDKEY兑换 ="#fUI/UIWindow/Delivery/line0#"
var 游戏攻略 ="#fUI/UIWindow/Delivery/line1#"

var 便捷商店 ="#fUI/UIWindow/AdminClaim/BtCancel/disabled/0#"

var 地图导航 ="#fUI/UIWindow/AdminClaim/BtCancel/normal/0#"
var 每日福利 ="#fUI/UIWindow/AdminClaim/BtCancel/pressed/0#"


var 通行证 ="#fUI/UIWindow/AdminClaim/BtCancel/mouseOver/0#"

var 系统活动 = "#fUI/UIWindow/DragonBall_B/BtClose/normal/0#";

var 全服排行 ="#fUI/UIWindow/AdminClaim/BtCClaim/disabled/0#"
var 日常任务 ="#fUI/UIWindow/AdminClaim/BtCClaim/mouseOver/0#"
var 赞助福利 ="#fUI/UIWindow/AdminClaim/BtCClaim/pressed/0#"

var 禁止脚本 ="#fUI/UIWindow/AdminClaim/BtClaim/disabled/0#"
var 自由市场 ="#fUI/UIWindow/AdminClaim/BtPClaim/pressed/0#"

var Logo = "#fUI/UIWindow/AdminClaim/default/1#"

var 超级仓库 = "#fUI/UIWindow/AdminClaim/BtCClaim/normal/0#"
var 互动交友 ="#fUI/UIWindow/AdminClaim/default/6#"

var 左修饰 = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星
var 右修饰 = "#fItem/Etc/0427/04270001/Icon9/0#";  //小黄星

var 左修饰A = "#fUI/ChatBalloon.img/33/w#"
var 右修饰A = "#fUI/ChatBalloon.img/33/e#"


var 分割线1 = "#fUI/UIWindow/AdminClaim/default/2#"
var 分割线2 ="__________________________________________________"
var 分割线3 = "#fUI/UIWindow/AdminClaim/default/3#"
var 分割线4 = "#fUI/UIWindow/AdminClaim/default/4#"



var 金枫叶 = "#fMap/MapHelper/weather/maple/2#";




var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
		  if(mode<0){//外挂修改数据判断
           cm.dispose();
		   return;
        }	


	   if(selection<-1){//外挂修改数据判断
		 cm.getPlayer().ban("修改封包",true,true,false);//封号处理
        }		
		   if(selection<0){//外挂修改数据判断
        selection= Math.abs(selection);
        }	
	
    if (mode == -1) {
		cm.sendOk("#b今天，也是充满希望的一天。");
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            cm.dispose();
            return;
        }
       if(selection<0){
        selection= Math.abs(selection);
        }				
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            if (cm.getPlayer().getMapId() == 108010101 || cm.getPlayer().getMapId() == 108010201 || cm.getPlayer().getMapId() == 108010301 || cm.getPlayer().getMapId() == 108010401 || cm.getPlayer().getMapId() == 108010501) {
                cm.sendOk("本地图暂时无法使用使用拍卖功能");				              
                cm.dispose();
                return;
            }
			if(cm.getPlayer().getLevel()< 1){
                cm.playerMessage(1, "此功能需要等级达到8级后使用");	              
                cm.dispose();
                return;				
			}
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }	

			text +=""
			
            if (cm.getPlayer().getMapId() == 180000001 && !cm.getPlayer().isGM()) {
                cm.sendOk("目前正在劳改监禁中...");
                cm.dispose();
                return;
            }

			
           // text +="\t\t\t\t   "+Logo+"\r\n"
			text+= "    #r#L22#"+草莓5+" #b#e强化大厅#r "+草莓5+"#l      #r#L195#"+草莓5+" #r#e远征大厅#r "+草莓5+"#l#n#k\r\n\r\n"
			text+= "    #r#L196#"+草莓5+" #d#e每日副本#r "+草莓5+"#l      #r#L197#"+草莓5+" #k#e独特副本#r "+草莓5+"#l#n#k\r\n\r\n"
			
			text +=   "#k#L1#"+左修饰+"地图导航"+右修饰+"#l #L2#"+左修饰+"日常任务"+右修饰+"#l #L3#"+左修饰+"每日福利"+右修饰+"#l\r\n\r\n"
			text +=   "#k#L4#"+左修饰+"师徒功能"+右修饰+"#l #L5#"+左修饰+"便捷商店"+右修饰+"#l #L6#"+左修饰+"全服排行"+右修饰+"#l\r\n\r\n"
			text +=   "#L2233#"+左修饰+"系统活动"+右修饰+"#l #L8#"+左修饰+"超级仓库"+右修饰+"#l #L499#"+左修饰+"经验戒指"+右修饰+"#l \r\n\r\n"
			
			text += "   "+分割线3+"#n"
			
			text +=  "#k#d#L12#"+左修饰+"爆物查询"+右修饰+"#l #L13#"+左修饰+"道具删除"+右修饰+"#l #L11#"+左修饰+"世界封印"+右修饰+"#n#l\r\n\r\n"	
			text +=  "#k#d#L9#"+左修饰+"交易市场"+右修饰+"#l #L191#"+左修饰+"职业变更"+右修饰+"#l #L193#"+左修饰+"卡片收集"+右修饰+"#l\r\n\r\n"
			text +=  "#k#d#L199#"+左修饰+"快速转职"+右修饰+"#l #L109#"+左修饰+"属性洗点"+右修饰+"#l #L333#"+左修饰+"五转技能"+右修饰+"#l\r\n\r\n"
			text +=  "#k#d#L299#"+左修饰+"双倍办理"+右修饰+"#l #L398#"+左修饰+"快乐翻翻"+右修饰+"#l #L335#"+左修饰+"主线任务"+右修饰+"#l\r\n\r\n"
			text +=  "#k#d#L603#"+左修饰+"银行功能"+右修饰+"#l #L599#"+左修饰+"挂机功能"+右修饰+"#l #L601#"+左修饰+"查询掉落"+右修饰+"#l\r\n\r\n"
			text +=  "#k#d#L600#"+左修饰+"时装玩法"+右修饰+"#l #L602#"+左修饰+"转生功能"+右修饰+"#l #L607#"+左修饰+"新手套装"+右修饰+"#l\r\n\r\n"
			 
			text +="   "+分割线3+"\r\n"
			
			text += "#r#L115#"+左修饰+"充值兑换"+右修饰+"#l #L200#"+左修饰+"充值礼包"+右修饰+"#l #L118#"+左修饰+"回馈礼包"+右修饰+"#l \r\n\r\n"
			//text += "#r#L117#"+左修饰+"周 赞 榜"+右修饰+"#l #L334#"+左修饰+"玩具收集"+右修饰+"#l #L335#"+左修饰+"主线任务"+右修饰+"#l\r\n\r\n"
			

			
			
			text +="#L106#"+游戏攻略+"#l#L119#"+CDKEY兑换+"#l\r\n" 
				
			text += "   "+分割线3+"#n"
 			
			 		 
			 

			
            cm.sendOk(text); 
        } else if (selection == 1) {

	    cm.dispose();
        cm.openNpc(9900004,'地图传送/城镇传送');
		//cm.addMember("VonLeon", false);
		cm.getSquad("VonLeon")

        } else if (selection == 0) {
		 cm.dispose();
		 cm.openNpc(9900004, '悬赏发布/全部订单');			
        } else if (selection == 2) {
		 cm.dispose();
		 cm.openNpc(9900004, '日常/每日任务');	
        } else if (selection == 3) {
		 cm.dispose();
		 cm.openNpc(9900004, '每日福利');		
		
        } else if (selection == 4) {	
		cm.dispose();	
		cm.openNpc(9900004, '师徒系统/师徒系统'); 				 
        } else if (selection == 5) {	
			cm.dispose();
		  cm.openShop(40); 
        } else if (selection == 6) {

		 cm.dispose();
		 cm.openNpc(9900004, '全服排行/排行榜导航');			
		
        } else if (selection == 7) {
		 cm.dispose();
		 cm.openNpc(9000001);		 
        } else if (selection == 8) {	
	         cm.dispose();
             cm.openNpc(9900004,'超级仓库/道具保管-消耗');	   		
        } else if (selection == 9) {
		 cm.dispose();
		 cm.openNpc(9900004, '云端交易/云端交易');		
        } else if (selection == 10) {		 
		cm.dispose();
		cm.openNpc(9900004, "进阶功能/卷轴管理"); 		 
        } else if (selection == 11) {
		 cm.dispose();
		 cm.openNpc(9900004, '世界封印');				
        } else if (selection == 12) {		
		 cm.dispose();
		 cm.openNpc(9900004, '爆物查询');	
        } else if (selection == 13) {
		 cm.dispose();
		 cm.openNpc(9900004, '道具删除/道具删除-装备');			
        } else if (selection == 14) {
		 cm.dispose();
		 cm.openNpc(9900004, '装备制作/装备制作')		
        } else if (selection == 15) {
		 cm.dispose();
		 cm.openNpc(9900004, '双倍办理')	
        } else if (selection == 16) {
		 cm.dispose();
		 cm.openNpc(9900004, '进阶功能/装备洗练')		
        } else if (selection == 18) {	
		 cm.dispose();
		 cm.openNpc(9900004, '刮刮乐/刮刮乐');
        } else if (selection == 19) {	
		 cm.dispose();
		 cm.openNpc(9900004, '副本系统/世界BOSS/世界BOSS');		
        } else if (selection == 20) {	
		 cm.dispose();
		 cm.openNpc(9900004, '洗血洗蓝');	
        } else if (selection == 21) {	
		 cm.dispose();
		 cm.openNpc(9900004, '独家玩法/天赋系统');	
        } else if (selection == 22) {
		  cm.dispose();			
	      cm.warp(350020004);
        } else if (selection == 23) {			 
		c.getPlayer().ban("异常跳转",true,true,false);//封号处理			 
		cm.dispose();
        } else if (selection == 106) {//游戏攻略
		 cm.dispose();
		 cm.openNpc(9900004, '游戏攻略')	
		 
        } else if (selection == 107) {//CDKEY兑换	
		cm.playerMessage(1, "暂未开放");
		cm.dispose();
        } else if (selection == 112) {//萌新必看攻略
		 cm.dispose();
		 cm.openNpc(9900004, '游戏攻略')	
        } else if (selection == 113) {//开区庆典
		
		 cm.dispose();
		 cm.openNpc(9900004, '活动举办/开区庆典');
	   } else if (selection == 2233) {	
		 cm.dispose();
		 cm.openNpc(9900004, '节日活动/新年活动');	
       } else if (selection == 114) {//开区庆典	

		 cm.dispose();
		 cm.openNpc(9900004, '扫黑除恶');

       } else if (selection == 115) {//	
			cm.dispose();	
			cm.openNpc(9900004, '赞助系统/充值兑换');		
       } else if (selection == 201) {//	
			cm.dispose();	
			cm.openNpc(9900004, '魂兽系统/魂兽技能');		
	  } else if (selection == 202) {//	
			cm.dispose();	
			cm.openNpc(9900004, '羽翼升级/羽翼升级');		
       } else if (selection == 200) {//	
			cm.dispose();	
			cm.openNpc(9900004, '赞助系统/累计积分');				
       } else if (selection == 116) {//	
			cm.dispose();	
			cm.openNpc(9900004, '赞助系统/每日充值');	   
       } else if (selection == 117) {//	
			cm.dispose();	
			cm.openNpc(9900004, '赞助系统/每周赞助排行榜');		   
       } else if (selection == 118) {//	
			cm.dispose();	
			cm.openNpc(9900004, '赞助系统/礼包购买');		   
	   
       } else if (selection == 119) {//		   
			cm.dispose();	
			cm.openNpc(9900004, 'CDK兑换');			   
       } else if (selection == 120) {//	
		cm.dispose();	
		cm.openNpc(9900004, '赞助系统/钻石月卡');
       } else if (selection == 121) {//	
		cm.dispose();	
		cm.openNpc(9900004, '抽奖中心/装备抽奖积分商店');

       } else if (selection == 122) {//
		 cm.dispose();
		 cm.openNpc(9900004, '通行证/通行证');		
	   
       } else if (selection == 111) {//系统活动
		 cm.dispose();
		 cm.openNpc(9000011)
       } else if (selection == 190) {//系统活动
		 cm.dispose();
		 cm.openNpc(9900004, '装备鉴定');		
       } else if (selection == 191) {//系统活动	 
		 cm.dispose();
		 cm.openNpc(9900004, '重生职业');		
       } else if (selection == 192) {//系统活动	 
		 cm.dispose();
		 cm.openNpc(9900004, '矿石合成');	
		} else if (selection == 193) {//系统活动	 
		 cm.dispose();
		 cm.openNpc(9900004, '卡片收集');	
	    } else if (selection == 194) {//系统活动	 
		 cm.dispose();
		 cm.openNpc(9900004, '装备制作/神力戒指');	
		} else if (selection == 11111111) {	
		 cm.dispose();
		 cm.openNpc(9900004, '节日活动/新年活动');
        } else if (selection == 195) {		 
		cm.warp(866010451,0);
		cm.dispose();	
        } else if (selection == 196) {
		  cm.dispose();			
	      cm.warp(970010000);
        } else if (selection == 197) {
		  cm.dispose();			
	      cm.warp(970000005);	
        } else if (selection == 198) {
		  cm.dispose();			
	      cm.openNpc(9900004, '身外之身');			  
		} else if (selection == 199) {
		  cm.dispose();			
	      cm.openNpc(9900004, '快速转职');	
		} else if (selection == 222) {
		  cm.dispose();			
	      cm.openNpc(9900004, '万能剪刀');		
		} else if (selection == 333) {
		  cm.dispose();			
	      cm.openNpc(9900004, '五转技能');		
		} else if (selection == 109) {
		  cm.dispose();			
	      cm.openNpc(9900004, '属性洗点');			
		} else if (selection == 334) {
		  cm.dispose();			
	      cm.openNpc(9900004, '玩具收集');		
		} else if (selection == 335) {
		  cm.dispose();			
	      cm.openNpc(2144001);	
		} else if (selection == 299) {
		  cm.dispose();			
	      cm.openNpc(9900004, '双倍办理');			 
		} else if (selection == 398) {
		  cm.dispose();			
	      cm.openNpc(9900004, '翻翻乐/翻翻乐');		
		} else if (selection == 499) {
		  cm.dispose();			
	      cm.openNpc(9900004, '经验存储/经验戒指');		
		}else if (selection == 600) {
		  cm.dispose();			
	      cm.openNpc(9900004, '时装玩法');		
		}else if (selection == 601) {
		  cm.dispose();			
	      cm.openNpc(9900004, '查询掉落');		
		}else if (selection == 599) {
		  cm.dispose();			
	      cm.openNpc(9900004, '吸怪盒子');
		}else if (selection == 603) {
		  cm.dispose();			
	      cm.openNpc(9900004, '银行');
		}else if (selection == 604) {
		  cm.dispose();			
	      cm.openNpc(9900004, '一键转职');
		}else if (selection == 605) {
		  cm.dispose();			
	      cm.openNpc(9900004, '回到新手');
		}else if (selection == 606) {
		  cm.dispose();			
	      cm.openNpc(9900004, '内测资源');
		}else if (selection == 607) {
		  cm.dispose();			
	      cm.openNpc(9900004, '10周年装备');
		}else if (selection == 608) {
		  cm.dispose();			
	      cm.openNpc(9900004, '卡片系统/卡片系统');
		}else if (selection == 602) {
		  cm.dispose();			
	      cm.openNpc(9900004, '转世重生/转生系统');				  




		
		
//===================================================================		
/*            } else if (selection == 108) {//内测功能
		  
		 更新累计充值(10000,cm.getPlayer().getId());
		cm.getPlayer().addMAXMPHP(20000,20000);
		cm.getPlayer().setFame(300);
	
		cm.gainItem(4021009, 10000);//	星石
		cm.gainItem(4011007, 10000);//	月石
		cm.gainItem(4005004, 10000);//	黑暗水晶
		cm.gainItem(4005003, 10000);//	幸运水晶
		cm.gainItem(4005002, 10000);//	敏捷水晶
		cm.gainItem(4005001, 10000);//	智慧水晶
		cm.gainItem(4005000, 10000);//	力量水晶
		cm.gainItem(2028003, 10000);
		cm.gainItem(2028004, 10000);
		cm.gainItem(4000082, 10000);
		cm.gainItem(4001126, 30000);
		cm.gainMeso(500000000);//给金币
		cm.给点券(1, 2000000);		
 		cm.playerMessage(1, "成功领取内测无限资源,一键满赞。");
		
		cm.dispose();		
		} else if (selection == 109) {//满技能
		 if(cm.getPlayer().getLevel() >=121){
		  cm.getPlayer().maxSkills();
          cm.playerMessage(1, "恭喜您，技能已成功全满了!!");//弹窗领取 
          cm.dispose();
		  }else{
          cm.playerMessage(1, "抱歉，需要等级达到121级后学习!!");//弹窗领取 
          cm.dispose();	 		  
		 }    */
 //==========================================================================

		 
		
       }
    }
}

function 更新累计充值(累计,uid) {
    cm.sql_Update("update 自建_累计充值 set 累计充值=累计充值+? where uid=?",累计,uid);
}
