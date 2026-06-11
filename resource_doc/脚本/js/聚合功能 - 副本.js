var 永恒回忆 = "#fEffect/CharacterEff1.img/QQ1408745/1/7#";
var 蓝心 = "#fEffect/CharacterEff/1022223/4/0#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var 群粉心 =" "+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 蘑菇 = "#fUI/UIWindow.img/Minigame/Common/mark#";
var 红箭头 = "#fUI/UIWindow.img/Quest/icon9/0#";//红色右箭头
var 蓝箭头 = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var 选择道具 = "#fUI/UIWindow.img/QuestIcon/3/0#";//选择道具
var 蓝框箭头 = "#fUI/Basic/BtHide3/mouseOver/0#";
var 感叹号 = "#fUI/UIWindow/Quest/icon0#";
var 美化new = "#fUI/UIWindow/Quest/icon5/1#";
var 圆形 = "#fUI/UIWindow/Quest/icon3/6#";
var 黑桃1 = "#fUI/GuildMark/Mark/Pattern/00004018/1#";
var 黑桃2 = "#fUI/GuildMark/Mark/Pattern/00004018/2#";
var 黑桃3 = "#fUI/GuildMark/Mark/Pattern/00004018/3#";
var 黑桃4 = "#fUI/GuildMark/Mark/Pattern/00004018/4#";
var 黑桃5 = "#fUI/GuildMark/Mark/Pattern/00004018/5#";
var 黑桃6 = "#fUI/GuildMark/Mark/Pattern/00004018/6#";
var 黑桃7 = "#fUI/GuildMark/Mark/Pattern/00004018/7#";
var 黑桃8 = "#fUI/GuildMark/Mark/Pattern/00004018/8#";
var 黑桃9 = "#fUI/GuildMark/Mark/Pattern/00004018/9#";
var 黑桃10 = "#fUI/GuildMark/Mark/Pattern/00004018/10#";
var 黑桃11 = "#fUI/GuildMark/Mark/Pattern/00004018/11#";
var 黑桃12 = "#fUI/GuildMark/Mark/Pattern/00004018/12#";
var 黑桃13 = "#fUI/GuildMark/Mark/Pattern/00004018/13#";
var 黑桃14 = "#fUI/GuildMark/Mark/Pattern/00004018/14#";
var 黑桃15 = "#fUI/GuildMark/Mark/Pattern/00004018/15#";
var 黑桃16 = "#fUI/GuildMark/Mark/Pattern/00004018/16#";
var 花花1 = "#fUI/GuildMark/Mark/Pattern/00004020/1#";
var 花花2 = "#fUI/GuildMark/Mark/Pattern/00004020/3#";
var 花花3 = "#fUI/GuildMark/Mark/Pattern/00004020/5#";
var 花花4 = "#fUI/GuildMark/Mark/Pattern/00004020/7#";
var 花花5 = "#fUI/GuildMark/Mark/Pattern/00004020/9#";
var 花花6 = "#fUI/GuildMark/Mark/Pattern/00004020/11#";
var 花花7 = "#fUI/GuildMark/Mark/Pattern/00004020/13#";
var 花花8 = "#fUI/GuildMark/Mark/Pattern/00004020/14#";
var 花花9 = "#fUI/GuildMark/Mark/Pattern/00004020/15#";
var 圆点 = "#fEffect/CharacterEff/1112908/0/1#";  //彩光3
var 小粉心 = "#fEffect/CharacterEff/1062114/1/0#";  //蓝心
var 蓝色角点 = "#fUI/UIWindow.img/PvP/Scroll/enabled/next2#";
var 表情 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/0#"; 
var 表情1 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/1#"; 
var 表情2 = "#fUI/GuildBBS.img/GuildBBS/Emoticon/Basic/2#"; 
var 蓝色小兔子 = "#fEffect/CharacterEff.img/1112960/3/1#"; 
var 小红星 = "#fEffect/CharacterEff.img/1112926/0/0#";
var 小蓝星 = "#fEffect/CharacterEff.img/1112925/0/0#";
var 蓝色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/0/0#";  
var 红色时钟 = "#fUI/UIWindow.img/Quest/TimeQuest/AlarmClock/default/4/0#";  
var 成功了 = "#fEffect/BasicEff.img/Fishing/6#";  
var 经验值 = "#fUI/UIWindow.img/QuestIcon/8/0#";  
var 神秘人 = "#fUI/UIWindow.img/MinigameTable/default#";
var dd = " #r冒险岛一条龙服务#k★#bQQ:1408745#k★";

/*if (im.getBossRank6("消费积分",1) == -1) {
       im.setBossRank6("累计积分",1,0);
       im.setBossRank5("消费积分",1,0);
       im.setBossRank1("钓鱼积分1",1,0);
       im.setBossRank7("无尽之塔分数",1,0);
       im.setBossRank2("钓鱼成功次数",1,0);
       im.setBossRank3("破功次数",1,0);
       im.setBossRank2("钓鱼成功次数",1,0);
       im.setBossRank4("每日充值2",1,0);
       im.setBossRank3("每周任务",1,0);
}*/
function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    } else {
        if (status >= 0 && mode == 0) {

            //cm.sendOk("感谢你的光临！");
            cm.dispose();
            return;
        }
        if (mode == 1) {
            status++;
        }
        else {
            status--;
        }
        if (status == 0) {
			/*if(cm.getJob() >= 0 && cm.getJob()<= 522 && cm.hasSkill(1017) == false){
			cm.teachSkill(1017,1,1);
			}else if(cm.getJob() >=1000 || cm.getJob() <= 2112 && cm.hasSkill(20001019) == false){
			cm.teachSkill(20001019,1,1);
			}*/
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }

			text += ""+群粉心+"\t\t\t"+永恒回忆+"\r\n"+群粉心+"";
// #b"+花花2+"排名:第 #r"+getpm()+" #b名"+花花8+"抵扣卷:#r"+cm.getPlayer().getCSPoints(2)+"#b点
			text += "  "+花花1+"#b玩家名称:#r"+cm.getPlayer().getName()+"   #b"+花花6+"在线时间:#r"+cm.getPlayer().getTodayOnlineTime()+"#b分钟\r\n";
			text += "  #b"+花花3+"赞助余额:#r"+cm.getPlayer().getmoney() +"#b点  "+花花4+"#b元宝:#r"+cm.getmoneyb()+"#b点  "+花花5+"#b累计:#r"+ cm.getPlayer().getlpjf() +"#b点\r\n";
			text += "  #b"+花花7+"拥有点卷:#r"+cm.getPlayer().getCSPoints(1)+"#b点   "+花花9+"当前破功:#r"+cm.getPlayer().getDamage()+"#b点\r\n"+群粉心+"";
            if (cm.getPlayer().getMapId() != 910000000) {
			text += "#r#L0#"+黑桃5+"自由市场"+黑桃5+"#l"
} else {
            text += "#r#L110#"+黑桃5+"离线挂机"+黑桃5+"#l"
			}
			text +="#n  #L1#"+黑桃1+"传送系统"+黑桃1+"#l  #L2#"+黑桃14+"服务系统"+黑桃14+"#l\r\n\r\n";
			
			text +="#d#L3#"+黑桃4+"任务系统"+黑桃4+"#l  #L4#"+黑桃3+"挑战大厅"+黑桃3+"#l  #L5#"+黑桃2+"强化大厅"+黑桃2+"#l\r\n\r\n";
			
			text +="#b#L6#"+黑桃8+"转职系统"+黑桃8+"#l  #L12#"+黑桃12+"怪怪卡片"+黑桃12+"#l  #L11#"+黑桃11+"购物系统"+黑桃11+"#l\r\n\r\n";
			text +=                   "#b#L13#"+黑桃8+"CDK兑换"+黑桃8+"#l\r\n";

			text +="#k    \r\n\r\n"+群粉心+"";
			text +="#k#L9#"+黑桃13+"吸怪系统"+黑桃13+"#l  #L8#"+黑桃9+"查看爆率"+黑桃9+"#l  #L10#"+黑桃12+"赞助系统"+黑桃12+"#l\r\n\r\n"+群粉心+"";

		    cm.sendSimple(text);
        } else if (selection == 0) {//自由市场
			cm.warp(910000000,0);
			cm.dispose();
        } else if (selection == 10000) {//新手礼包
            cm.openNpc(9900004,"全服查物");
        } else if (selection == 8999) {//新手礼包
            cm.openNpc(9310084,0);
			
        } else if (selection == 20000) {//新手礼包
            //cm.openNpc(9310061,0);
            cm.openNpc(9900004,"管理员喜从天降");
			
        } else if (selection == 30000) {//新手礼包
            cm.openNpc(9000018,"脚本测试");
			
		} else if (selection == 110) {
            cm.processCommand("@离线挂机");
			
        } else if (selection == 1000) {//新手礼包
            cm.openNpc(9900004,"满赞礼包");
			
        } else if (selection == 1) {//
            cm.openNpc(9900004,"传送中心");
			
        } else if (selection == 2) {//回收系统 
            cm.openNpc(9900004,"服务系统");
			
        } else if (selection == 3) {//每日系统
            cm.openNpc(9000435,0);
			
        } else if (selection == 4) {//赞助系统
			cm.warp(960000000,0);
		    cm.dispose();
        } else if (selection == 5) {//
		    cm.warp(910141010,0);
			cm.dispose();
			
        } else if (selection == 6) { //快捷商店
            cm.openNpc(9900004,"快速转职");
			
		} else if (selection == 7) {//我要变强
            cm.openNpc(9000446,0)
			
        } else if (selection == 8) {//发型脸型
           cm.openNpc(9900004, "怪物爆率")

        } else if (selection == 9) {//制作中心
            cm.openNpc(9900004,"吸怪盒子");	
			
        } else if (selection == 10) {//制作中心
            cm.openNpc(9000445,0);	

        } else if (selection == 11) {//美容美发
            cm.openNpc(9000444, 0);
        } else if (selection == 12) {//
            cm.openNpc(9900004, "卡片收集");
        } else if (selection == 13) {//勋章领取
            cm.openNpc(9900004, "CDKCZ");
        } else if (selection == 14) {//勋章领取
            cm.openNpc(9030100, 0);
     
        } else if (selection == 15) {//赞助介绍
            cm.openNpc(9000439, 0);
        } else if (selection == 16) {//
            cm.openNpc(9000445, 0);
        } else if (selection == 17) {//
            cm.openNpc(9000446, 0);
        } else if (selection == 18) {//
            cm.openNpc(9900004, 2);
        } else if (selection == 19) {//
            cm.openNpc(9900004, 2);
        } else if (selection == 20) {
            cm.openNpc(9900004, "超级技能1");//买血蓝

		} else if (selection == 31) {//转生
            cm.openNpc(9390366, "怪物爆率");
		} else if (selection == 32) {//美容美发
            cm.openNpc(9000431, 14);
		} else if (selection == 33) {//洗血洗蓝
            cm.openNpc(9000431, 2);
		} else if (selection == 34) {//白嫖
            cm.openNpc(9000442, 0);
		} else if (selection == 1111) {//白嫖
            cm.openNpc(9900004, "怪物公园");

        } else if (selection == 999) {//在线奖励
            cm.openNpc(9900004, "快速转职");
			 } else if (selection == 311) {
           var text = "";
            text += ""+圆点+"#L3111##r1阶段打金地图,需持有:#v3700010##k\r\n\r\n";
            text += ""+圆点+"#L3112##r2阶段打金地图,需持有:#v3700011##k\r\n\r\n";
			text += ""+圆点+"#L3113##r3阶段打金地图,需持有:#v3700012##k\r\n\r\n";
			text += ""+圆点+"#L3114##r4阶段打金地图,需持有:#v3700013##k\r\n\r\n";
			
			//cm.dispose();
		
			cm.sendOk(text);
			} else if (selection == 3111) {//在线奖励
			if(cm.haveItem(3700010,1)){
            cm.warp(271030500,0);
            cm.dispose();
			}else{
            cm.sendOk("你没有 足够的 材料~.");
            cm.dispose();
				}     
			} else if (selection == 3112) {//在线奖励
		    if(cm.haveItem(3700011,1)){
			  cm.warp(271030510,0);
			   cm.dispose();
			   }else{
					cm.sendOk("你没有 足够的 材料~.");
					cm.dispose();
				}    
			   } else if (selection == 3113) {//在线奖励
			if(cm.haveItem(3700012,1)){
			  cm.warp(271030520,0);
			   cm.dispose();
			   }else{
					cm.sendOk("你没有 足够的 材料~.");
					cm.dispose();
				}    
			   } else if (selection == 3114) {//在线奖励
			if(cm.haveItem(3700013,1)){
			  cm.warp(271030530,0);
			   cm.dispose();
			   }else{
					cm.sendOk("你没有 足够的 材料~.");
					cm.dispose();
				}    
	} else if (selection == 2500) {//在线奖励
            cm.openNpc(9900004,"拍卖");
        } else if (selection == 10086) {//个人信息
			var text = "";
            text += "\t\t\t"+圆点+"角色姓名#r【" + cm.getPlayer().getName() + "】"+圆点+"#k\r\n\r\n"
            text += ""+圆点+"#b可兑赞助:#r"+cm.getPlayer().getzb() +"\t    "+圆点+"#b余额宝箱#v2022613#:#r#c2022613#\r\n\r\n"
            text += ""+圆点+"#b元宝积分:#r"+cm.getmoneyb()+"\t   "+圆点+"#b累计积分:#r"+cm.getmoneym()+"\r\n\r\n"
            text += ""+圆点+"#b点卷余额:#r" + cm.getPlayer().getCSPoints(1) + "\t   "+圆点+"#b抵用余额:#r" + cm.getPlayer().getCSPoints(2) + "#k#n#n\r\n\r\n"
			text += ""+圆点+"#b额外破攻:#r" + cm.getPlayer().getDamage() + "\t   "+圆点+"#b在线时间:#r" + cm.getPlayer().getTodayOnlineTime() + "\r\n\r\n"
			//text += ""+圆点+"在线时间:#r" + cm.getPlayer().getTodayOnlineTime() + "分钟#k#l\r\n";
            //text += ""+圆点+"金币余额:#r" + cm.getMeso() + "#k\r\n"
			//text += ""+圆点+"境界：#r" + cm.getPlayer().getRebornsName() + "#k#l\r\n";
			cm.dispose();
			if(cm.getChar().isGM()){
				text += ""+圆点+"#b地图ID:#r" + cm.getPlayer().getMapId() + "#k#l\r\n";
				cm.dispose();
				//text += ""+圆点+"怪物数量是#r:"+cm.getChar().获取怪物数量(cm.getPlayer().getMapId()) + "#k 个#l\r\n";
				//text += ""+圆点+"副职业是#r:"+cm.getChar().findFzyNameByCharId() + "#k #l\r\n";
			}
			cm.sendOk(text);

	    } else if (selection == 68750) {
            cm.openNpc(9000431, "管理员怪物攻城");	
			
	    } else if (selection == 68753) {
            cm.openNpc(9000431, "吸怪盒子");	
			
	    } else if (selection == 68754) {
            cm.openNpc(9000431,"管理员跟踪");	
			
	    } else if (selection == 68755) {
            cm.openNpc(9000431,"管理员查询");	
			
	    } else if (selection == 68756) {
            cm.openNpc(9000431,"管理员充值");	

	    } else if (selection == 68757) {
            cm.openNpc(9000431, "怪物爆率");	
			
	    } else if (selection == 68758) {
            cm.openNpc(9000431,"重置查询");	

	    } else if (selection == 68759) {
            cm.openNpc(9000431,"管理员属性修改");				
			
		}
    }
}




function getJobList() {
   /* var rs = cm.sql_Select("SELECT DISTINCT chr.`name`, chr.`job`, chr.`id` , SUM(men.`str` + men.`dex` + men.`int` + men.`luk` + men.`watk` + men.`matk` + men.`hp` + men.`mp` + men.`wdef` + men.`upgradeslots` + men.`level` + men.`enhance`) AS max FROM inventoryitems it, inventoryequipment men, characters chr WHERE (it.position < 0 AND it.inventoryitemid = men.inventoryitemid AND chr.id = it.characterid AND chr.gm <= 0) GROUP BY id ORDER BY max DESC;");
	var ds = cm.sql_Select("SELECT DISTINCT chr.`name`, chr.`job`, chr.`id` , SUM(men.`nstr` + men.`ndex` + men.`nint` + men.`nluk` + men.`nwatk` + men.`nmatk` + men.`nhp` + men.`nmp` + men.`nwdef`) AS max FROM inventoryitems it, nirvanaflame men, characters chr WHERE (it.position < 0 AND it.inventoryitemid = men.inventoryitemid AND chr.id = it.characterid AND chr.gm <= 0) GROUP BY id ORDER BY max DESC;");*/
	var Container = [];
	var conn =cm.getConnection();
	var sql = ""
	
	sql = "SELECT * from xmwnjl where  bossid = '战力计算' order by count desc limit 100;"
	
	var ps = conn.prepareStatement(sql);
	var rs =ps.executeQuery();
	while(rs.next()){
        var RankGroup = [];
        RankGroup['name'] = rs.getString("ty");
        RankGroup['job'] = rs.getInt("jobid");
        RankGroup['level'] = rs.getInt("count");
        Container.push(RankGroup);
    }
	rs.close();
	ps.close();
	conn.close();
    return Container;
}


function getpm() {
    var yesnow = -1;
    var NowList = getJobList(JobName(Math.floor(cm.getPlayer().getJob()/100)));
    if (NowList.length > 0) {
        var nowdd = 0;
        for (i in NowList) {
			nowdd++;
            if (NowList[i]['name'] == cm.getPlayer().getName()) {
                yesnow = nowdd;
				break;
            }
        }
    }
    return yesnow;
}

function JobName(ctype)
{
	if(ctype==0)
	{
		return 100;
	}
	else if(ctype==1)
	{
		return 200;
	}
	else if(ctype==2)
	{
		return 300;
	}
	else if(ctype==3)
	{
		return 400;
	}
	else if(ctype==4)
	{
		return 500;
	}
	else if(ctype==5)
	{
		return 2100;
	}
	else
	{
		return 0;
	}
}
function 职业() {
	job = cm.getPlayer().getJob();
	if (job < 100) {
		result = "新手";
		return result;
	}
	if (job == 100) {
		result = "战士";
		return result;
	}
	if (job == 110) {
		result = "剑客";
		return result;
	}
	if (job == 111) {
		result = "勇士";
		return result;
	}
	if (job == 112) {
		result = "英雄";
		return result;
	}
	if (job == 120) {
		result = "准骑士";
		return result;
	}
	if (job == 121) {
		result = "骑士";
		return result;
	}
	if (job == 122) {
		result = "圣骑士";
		return result;
	}
	if (job == 130) {
		result = "枪战士";
		return result;
	}
	if (job == 131) {
		result = "龙骑士";
		return result;
	}
	if (job == 132) {
		result = "黑骑士";
		return result;
	}
	if (job == 200) {
		result = "魔法师";
		return result;
	}
	if (job == 210) {
		result = "法师(火，毒)";
		return result;
	}
	if (job == 211) {
		result = "巫师(火，毒)";
		return result;
	}
	if (job == 212) {
		result = "魔导师(火，毒)";
		return result;
	}
	if (job == 220) {
		result = "法师(冰，雷)";
		return result;
	}
	if (job == 221) {
		result = "巫师(冰，雷)";
		return result;
	}
	if (job == 222) {
		result = "魔导师(冰，雷)";
		return result;
	}
	if (job == 230) {
		result = "牧师";
		return result;
	}
	if (job == 231) {
		result = "祭司";
		return result;
	}
	if (job == 232) {
		result = "主教";
		return result;
	}
	if (job == 300) {
		result = "弓箭手";
		return result;
	}
	if (job == 310) {
		result = "猎手";
		return result;
	}
	if (job == 311) {
		result = "射手";
		return result;
	}
	if (job == 312) {
		result = "神射手";
		return result;
	}
	if (job == 320) {
		result = "弩弓手";
		return result;
	}
	if (job == 321) {
		result = "游侠";
		return result;
	}
	if (job == 322) {
		result = "箭神";
		return result;
	}
	if (job == 400) {
		result = "飞侠";
		return result;
	}
	if (job == 410) {
		result = "刺客";
		return result;
	}
	if (job == 411) {
		result = "无影人";
		return result;
	}
	if (job == 412) {
		result = "隐士";
		return result;
	}
	if (job == 420) {
		result = "侠客";
		return result;
	}
	if (job == 421) {
		result = "独行客";
		return result;
	}
	if (job == 422) {
		result = "侠盗";
		return result;
	}

	if (job == 500) {
		result = "海盗";
		return result;
	}
	if (job == 510) {
		result = "拳手";
		return result;
	}
	if (job == 511) {
		result = "斗士";
		return result;
	}
	if (job == 512) {
		result = "冲锋队长";
		return result;
	}
	if (job == 520) {
		result = "火枪手";
		return result;
	}
	if (job == 521) {
		result = "大副";
		return result;
	}
	if (job == 522) {
		result = "船长";
		return result;
	}
	if (job == 1000) {
		result = "初心者";
		return result;
	}
	if (job == 1100) {
		result = "魂骑士";
		return result;
	}
	if (job == 1110) {
		result = "魂骑士";
		return result;
	}
	if (job == 1111) {
		result = "魂骑士";
		return result;
	}
	if (job == 1112) {
		result = "魂骑士";
		return result;
	}
	if (job == 1200) {
		result = "炎术士";
		return result;
	}
	if (job == 1210) {
		result = "炎术士";
		return result;
	}
	if (job == 1211) {
		result = "炎术士";
		return result;
	}
	if (job == 1212) {
		result = "炎术士";
		return result;
	}
	if (job == 1300) {
		result = "风灵使者";
		return result;
	}
	if (job == 1310) {
		result = "风灵使者";
		return result;
	}
	if (job == 1311) {
		result = "风灵使者";
		return result;
	}
	if (job == 1312) {
		result = "风灵使者";
		return result;
	}
	if (job == 1400) {
		result = "夜行者";
		return result;
	}
	if (job == 1410) {
		result = "夜行者";
		return result;
	}
	if (job == 1411) {
		result = "夜行者";
		return result;
	}
	if (job == 1412) {
		result = "夜行者";
		return result;
	}
	if (job == 1500) {
		result = "奇袭者";
		return result;
	}
	if (job == 1510) {
		result = "奇袭者";
		return result;
	}
	if (job == 1511) {
		result = "奇袭者";
		return result;
	}
	if (job == 1512) {
		result = "奇袭者";
		return result;
	}
	if (job == 2000) {
		result = "战童";
		return result;
	}
	if (job == 2100) {
		result = "战神";
		return result;
	}
	if (job == 2110) {
		result = "战神";
		return result;
	}
	if (job == 2111) {
		result = "战神";
		return result;
	}
	if (job == 2112) {
		result = "战神";
		return result;
	}

}
