var 开心冒险岛 = "#fEffect/CharacterEff1.img/QQ1408745/1/12#";
var 蓝心 = "#fEffect/CharacterEff/1022223/4/0#";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";//红桃心
var dd = " #r冒险岛一条龙服务#k★#bQQ:1408745#k★";
var 群粉心 =" "+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var Icon = Array(   Array("警报器", "#fUI/Basic/BtClaim/disabled/0#"),
    Array("奖杯", "#fUI/UIAchievement.img/achievement/pages/main/achievementForm/basic/difficultyIcon/unique#")
);
var text, GDP, UDP;
var ca = java.util.Calendar.getInstance();
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE); //获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK)-1;//周 
var 领奖时间 =10; //19点
var 结束领奖时间 = 24; //19点
var ctype=0;
var 显示数量=10;
var status = 0;
var yesnow=0;
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
        if (mode == 1)
            status++;
        else
            status--;
        if (status == 0) {
			    yesnow=-1;
				//cm.getPlayer().setWedding(1)
				text = ""+dd+"\r\n"+群粉心+"#e#r\t\t\t#i1802533#各职业战力排行榜#i1802533##n\r\n";
				//text += "#e#r\t\t\t\t#n\r\n\r\n";
				text += "#L0##b战士十强排名#l\t#L1#法师十强排名#l\t#L2##b射手十强排名#l\r\n\r\n";
				text += "\t\t#L3##r飞侠十强排名#l\t\t#L4#海盗十强排名#l\r\n\r\n";
				text +="#d------------------------------------------------------\r\n"
				var nnn  = JobTypeName(cm.getPlayer().getJob());
				var cs = cm.getPlayer().getWeekBossLog(nnn+"战力排名");
                var name = JobType(cm.getPlayer().getJob());
				var cs1 = cm.getPlayer().getWeekBossLog(name+"战士战力排名领取");
				//if(cs >0&&cs1>0){
                text += "#k(#r今天"+(星期())+"#k)各职业战力前三名可在#r星期日领取排名奖励\r\n\t#b第一名奖励：#r100元宝 + 5个月石 + 5个星石\r\n\t#b第二名奖励：#r50元宝 + 5个星石\r\n\t#b第三名奖励：#r30元宝 + 5个月石\r\n";
				if(weekday==0) {//领取时间 周一 2=周一
				text+="\t\t\t #e#d#L100#领取 #r"+JobType(ctype)+"排名 #d奖励#l#n\r\n\r\n"
		        }else {
                text += "";//100档1
			    }
				//text += "     现在星期:"+(weekday-1)+" \r\n"[#b最强"+JobType(ctype)+"#r]

                //text += "#k\t每周周末各职业前三名可获得排名奖励(#r今天星期"+(weekday-1)+"#k)\r\n\t#b第一名奖励：#r100元宝\r\n\t#b第二名奖励：#r50元宝\r\n\t#b第三名奖励：#r30元宝\r\n";
				text +="#d------------------------------------------------------\r\n"
				var NowList=getJobList(JobName(ctype));
				if(NowList.length>0)
				{
					text += " #g排名   职业\t\t　 角色 \t\t\t　   战力 \r\n";
					var nowdd=0;
                    for (i in NowList) {
						
						if(nowdd==0){text+="#r"}else if(nowdd>=1&&nowdd<=2){text+=""}else {text+="#b"}
                        text += "   "+format(" ", 3, (nowdd+1).toString())+"  " + format(" ", 10, JobType(ctype).toString()) + "  ";
                        text += format(" ", 20, NowList[i]['name'].toString()) + "　";
						if(NowList[i]['name']==cm.getPlayer().getName())
						{
							yesnow=nowdd;
						}
                        text += NowList[i]['level'] + "　";
                        text += "\r\n";
						nowdd++;
                    }
				}
				else
				{
					text+="    \t\t\t\t#r暂无相关排名!\r\n"
				}
				text+="\r\n"
				//if(weekday==1&&hour>=领奖时间&&hour<=结束领奖时间&&yesnow>=0&&yesnow<=2)//领取时间 周一 2=周一
				//{
				//	text+="#L100#领取职业战力奖励#l"

			//	}
      //  }
        //text += "\r\n\r\n";
                cm.sendSimple(text);
        } else if (status == 1) {
				if(selection ==101){
					cm.dispose()
					cm.openNpc(9900004,"战力排名领取")
					return 
				}
				
			    if(selection<100)
				{
                ctype=selection;
		        start();
				}
				else if(selection==100)
				{
					if(yesnow>3||yesnow<=-1)
					{
						cm.sendOk("很遗憾!本周的 #r#e"+JobType(ctype)+"职业前3名 #k#n没有你哦");
						cm.dispose();
						return;
					}
					if(cm.getPlayer().getBossLog("战力领取")>=1)
					{
						cm.sendOk("很抱歉,你已经领取过今日奖励了!");
						cm.dispose();
						return;
					}
					else if(!cm.canHold())
					{
						cm.sendOk("你的背包已满,请检查你的背包!");
						cm.dispose();
						return;
					}
					else
					{
						if(yesnow==0)//第一名
						{
						  //cm.gainItem(1115007,8,8,8,8,0,0,10,20, 0,0,0,0,0,0,7*24);
						  cm.gainItem(4021009, 5);
						  cm.gainItem(4011007, 5);
						  cm.setmoneyb(100);//元宝
 cm.全服漂浮喇叭("〖战力排名〗 恭喜 ["+cm.getName()+"] 荣升本周 "+JobType(ctype)+"战力第一名 ,获得排名荣誉奖励", 5121000);
						}
						else if(yesnow==1)//第二名
						{
						  cm.gainItem(4021009, 5);
						  cm.setmoneyb(50);//元宝
						 // cm.gainItem(1115008,5,5,5,5,0,0,5,10, 0,0,0,0,0,0,7*24);   
 cm.全服漂浮喇叭("〖战力排名〗 恭喜 ["+cm.getName()+"] 荣升本周 "+JobType(ctype)+"战力第二名 ,获得排名荣誉奖励", 5121000);
						}
						else if(yesnow==2)//第三名
						{
						  cm.gainItem(4011007, 5);
						  cm.setmoneyb(30);//元宝
						// cm.gainItem(1115009,3,3,3,3,0,0,3,6, 0,0,0,0,0,0,7*24);  
 cm.全服漂浮喇叭("〖战力排名〗 恭喜 ["+cm.getName()+"] 荣升本周 "+JobType(ctype)+"战力第三名 ,获得排名荣誉奖励", 5121000);
						}
						cm.getPlayer().setBossLog("战力领取");
						cm.sendOk("恭喜你获得本周 #r"+JobType(ctype)+"战力 #k排名荣誉奖励!");
						cm.dispose();
						return;
					}
				}
            }
        }
    }
	
	
function JobType(ctype)
{
	if(ctype==0)
	{
		return "战士";
	}
	else if(ctype==1)
	{
		return "法师";
	}
	else if(ctype==2)
	{
		return "射手";
	}
	else if(ctype==3)
	{
		return "飞侠";
	}
	else if(ctype==4)
	{
		return "海盗";
	}
	else
	{
		return "新手";
	}
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
	else
	{
		return 0;
	}
}



function getJobList(jobid) {
   /* var rs = cm.sql_Select("SELECT DISTINCT chr.`name`, chr.`job`, chr.`id` , SUM(men.`str` + men.`dex` + men.`int` + men.`luk` + men.`watk` + men.`matk` + men.`hp` + men.`mp` + men.`wdef` + men.`upgradeslots` + men.`level` + men.`enhance`) AS max FROM inventoryitems it, inventoryequipment men, characters chr WHERE (it.position < 0 AND it.inventoryitemid = men.inventoryitemid AND chr.id = it.characterid AND chr.gm <= 0) GROUP BY id ORDER BY max DESC;");
	var ds = cm.sql_Select("SELECT DISTINCT chr.`name`, chr.`job`, chr.`id` , SUM(men.`nstr` + men.`ndex` + men.`nint` + men.`nluk` + men.`nwatk` + men.`nmatk` + men.`nhp` + men.`nmp` + men.`nwdef`) AS max FROM inventoryitems it, nirvanaflame men, characters chr WHERE (it.position < 0 AND it.inventoryitemid = men.inventoryitemid AND chr.id = it.characterid AND chr.gm <= 0) GROUP BY id ORDER BY max DESC;");*/
	var Container = [];
	var conn =cm.getConnection();
	
	var ps = conn.prepareStatement("SELECT * from xmwnjl where jobid >="+jobid+" and jobid <"+(jobid+100)+" and bossid = '战力计算' order by count desc limit "+显示数量+";");
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



var format = function FormatString(c, length, content) { //符号 位置 代码 - 文本类型 .toString()
    var str = "";
    var cs = "";
    if (content.length > length) {
        str = content;
    } else {
        for (var j = 0; j < length - content.getBytes("GB2312").length; j++) {
            cs = cs + c;
        }
    }
    str = content + cs;
    return str;
}


function  星期() {
	switch (weekday) {
		case 0:
			return "星期日";
		case 1:
			return "星期一";
		case 2:
			return "星期二";
		case 3:
			return "星期三";
		case 4:
			return "星期四";
		case 5:
			return "星期五";
		case 6:
			return "星期六";
		default:
			return 0;
	}
}
function JobTypeName(job)
{
	 if ( job >= 100 &&  job <= 132) {
           return "战士"
        } else if ( job >= 200 &&  job <= 232) {
            return "魔师"
        } else if ( job >= 300 &&  job <= 322) {
            return "射手"
        } else if ( job >= 400 &&  job <= 422) {
            return "飞侠"
        } else if ( job >= 500 &&  job <= 522) {
            return "海盗"
        }
		return "";
}