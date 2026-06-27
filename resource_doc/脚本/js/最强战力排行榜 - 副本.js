
//var Icon = Array(   Array("警报器", "#fUI/Basic/BtClaim/disabled/0#"),
    //Array("奖杯", "#fUI/UIAchievement.img/achievement/pages/main/achievementForm/basic/difficultyIcon/unique#")
//);
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var 笑脸1 ="#fUI/GuildMark/Mark/Etc/00009025/1#";
var 笑脸3 ="#fUI/GuildMark/Mark/Etc/00009025/3#";
var 笑脸5 ="#fUI/GuildMark/Mark/Etc/00009025/5#";
var 笑脸7 ="#fUI/GuildMark/Mark/Etc/00009025/7#";
var 笑脸9 ="#fUI/GuildMark/Mark/Etc/00009025/9#";
var 笑脸11 ="#fUI/GuildMark/Mark/Etc/00009025/11#";
var 笑脸13 ="#fUI/GuildMark/Mark/Etc/00009025/13#";
var 笑脸16 ="#fUI/GuildMark/Mark/Etc/00009025/16#";
var 萧曳冒险岛 = "#fEffect/CharacterEff1.img/QQ1408745/1/12#";
var txt, GDP, UDP;
var ca = java.util.Calendar.getInstance();
var hour = ca.get(java.util.Calendar.HOUR_OF_DAY); //获得小时
var minute = ca.get(java.util.Calendar.MINUTE); //获得分钟
var second = ca.get(java.util.Calendar.SECOND); //获得秒
var weekday = ca.get(java.util.Calendar.DAY_OF_WEEK);//周 
var 领奖时间 =10; //19点
var 结束领奖时间 = 24; //19点
var ctype=99;
var 显示数量=20;
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
				txt = ""+dd+"\r\n"+群粉心+"#e#r\t\t\t#i1802533#战 力 排 行 榜#i1802533##n\r\n";
				txt +="#k------------------------------------------------------\r\n"
            //    txt += "\t #b今天:"+粉心+"#r"+(星期())+"#k\t#r"+JobType(ctype)+"#k \r\n\r\n";
				txt += "#L102# #d战力说明#b" + 粉心 + "战力介绍" + 粉心 + "#l\r\n\r\n";
				txt += "#e#L99##r"+笑脸1+"总 排 名"+笑脸1+"#l #L0##r"+笑脸3+"战士排名"+笑脸3+"#l #L1#"+笑脸13+"法师排名"+笑脸13+"#l\r\n\r\n";
				txt += "#L2#"+笑脸7+"射手排名"+笑脸7+"#l #L3##r"+笑脸9+"飞侠排名"+笑脸9+"#l #L4#"+笑脸11+"海盗排名"+笑脸11+"#l#n\r\n\r\n";
				
				txt +="#k------------------------------------------------------\r\n"
                txt += "#k(今天#r"+(星期())+"#k)各职业战力#r前三名#k可在#r星期日#k领取#r[排名奖励]\r\n\t#b第一名奖励：#r100元宝 + 5个月石 + 5个星石\r\n\t#b第二名奖励：#r50元宝 + 5个星石\r\n\t#b第三名奖励：#r30元宝 + 5个月石\r\n";
				if(weekday==1) {//领取时间 周一 2=周一
				txt+="\t\t#e#d#L100##v4031223#领取 #r"+JobType(ctype)+"排名 #d奖励#v4031223##l#n\r\n\r\n"
		        }else {
                txt += "";//100档1
			    }
				txt +="#k------------------------------------------------------\r\n"
				var nnn  = JobTypeName(cm.getPlayer().getJob());
				var cs = cm.getPlayer().getWeekBossLog(nnn+"战力排名");
                var name = JobType(cm.getPlayer().getJob());

				var NowList=getJobList(JobName(ctype));
				if(NowList.length>0){
					txt +=  "#r#e" + 粉心 + "排 名" + 粉心 + "    " + 粉心 + "职 业" + 粉心 + "    " + 粉心 + "角色名" + 粉心 + "    " + 粉心 + "战力值" + 粉心 + "#n\r\n\r\n";
					
					var nowdd=0;
                    for (i in NowList) {
						if(nowdd<=2){
							txt+="#r"
							/*}else if(nowdd==1){
							txt+="#g"
							}else if(nowdd==2){
							txt+="#b"*/
					}else{
						txt+="#k"
						}
                        txt += "\t"+format(" ",3, (nowdd+1).toString())+"\t\t" + format(" ", 10, JobTypeName(NowList[i]["job"]).toString()) + "";
						
                        txt += "   "+format(" ",16, NowList[i]['name'].toString()) + "";//战力

						if(NowList[i]['name']==cm.getPlayer().getName())
						{
						yesnow=nowdd;
						}
                        txt += "\t"+NowList[i]['damagec'] + "";

						nowdd++;
                        txt += "\r\n";
                    }
				}else{
					txt+="    \t\t\t\t#r暂无相关排名!\r\n"
				}
				//txt+="\r\n"

                cm.sendSimple(txt);
        } else if (status == 1) {
				
				if(selection ==102){
                 var txtw = "- 战力算法说明\r\n\r\n"
                //txtw += "以下算法佩戴与加点,全部属性加上得出的数值\r\n\r\n"
				txtw += "能力值装备加成 力量x5 + 敏捷X5 + 智力X5 + 运气X5\r\n\r\n"
                txtw += "装备>+攻击X50+魔法攻击X50物理防御+1 魔法防御+1 \r\n\r\n"
				//txtw += "- 法系职业战力: 装备魔法力 + 10 + 装备四维 +人物四维\r\n"
				//txtw += "- 物理职业战力: 装备攻击力 + 10 + 装备四维 +人物四维\r\n"
				//txtw += "- 标飞职业战力: 装备攻击力 + 50 \r\n"
			//	txtw += "- 海盗职业战力: 装备攻击力 + 25\r\n\r\n\r\n"
				//txtw += "- 因部分职业装备成长差异化所进行的平衡算法\r\n"
                cm.sendSimple(txtw);
                cm.dispose();
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
						cm.sendOk("很遗憾,你并不是该职业前3名的玩家!");
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
	else if(ctype==5)
	{
		return "战神";
	}
	else
	{
		return "职业前三";
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
	else if(ctype==5)
	{
		return 2100;
	}
	else
	{
		return 0;
	}
}



function getJobList(job) {
   /* var rs = cm.sql_Select("SELECT DISTINCT chr.`name`, chr.`job`, chr.`id` , SUM(men.`str` + men.`dex` + men.`int` + men.`luk` + men.`watk` + men.`matk` + men.`hp` + men.`mp` + men.`wdef` + men.`upgradeslots` + men.`level` + men.`enhance`) AS max FROM inventoryitems it, inventoryequipment men, characters chr WHERE (it.position < 0 AND it.inventoryitemid = men.inventoryitemid AND chr.id = it.characterid AND chr.gm <= 0) GROUP BY id ORDER BY max DESC;");
	var ds = cm.sql_Select("SELECT DISTINCT chr.`name`, chr.`job`, chr.`id` , SUM(men.`nstr` + men.`ndex` + men.`nint` + men.`nluk` + men.`nwatk` + men.`nmatk` + men.`nhp` + men.`nmp` + men.`nwdef`) AS max FROM inventoryitems it, nirvanaflame men, characters chr WHERE (it.position < 0 AND it.inventoryitemid = men.inventoryitemid AND chr.id = it.characterid AND chr.gm <= 0) GROUP BY id ORDER BY max DESC;");*/
	var Container = [];
	var conn =cm.getConnection();
	var sql = ""
	if(job == 0){
		 //SELECT * FROM characters WHERE gm < 1 ORDER BY `meso` DESC
		sql = "SELECT * FROM characters WHERE gm < 1 and job ORDER BY `damagec` DESC LIMIT "+显示数量+";"
		//sql = "SELECT * from characterid where damagec order by desc limit "+显示数量+";"
	}else{
		sql = "SELECT * FROM characters WHERE job >="+job+" and job <"+(job+100)+" and gm < 1 ORDER BY `damagec` DESC LIMIT "+显示数量+";"
		//sql = "SELECT * from characterid where damagec order by desc limit "+显示数量+";"
		//sql = "SELECT * from characterid where job >="+job+" and job <"+(job+100)+" and damagec  order by  desc limit "+显示数量+";"
	}
	var ps = conn.prepareStatement(sql);
	var rs =ps.executeQuery();
	while(rs.next()){
        var RankGroup = [];
        RankGroup['name'] = rs.getString("name");
        RankGroup['job'] = rs.getInt("job");
        RankGroup['damagec'] = rs.getInt("damagec");
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
		case 1:
			return "星期日";
		case 2:
			return "星期一";
		case 3:
			return "星期二";
		case 4:
			return "星期三";
		case 5:
			return "星期四";
		case 6:
			return "星期五";
		case 7:
			return "星期六";
		default:
			return 0;
	}
}
function JobTypeName(job)
{
	 if ( job >= 100 &&  job <= 132) {
           return " 战 士"
        } else if ( job >= 200 &&  job <= 232) {
            return "魔法师"
        } else if ( job >= 300 &&  job <= 322) {
            return " 射 手"
        } else if ( job >= 400 &&  job <= 422) {
            return " 飞 侠"
        } else if ( job >= 500 &&  job <= 522) {
            return " 海 盗"
      //  }
        } else if ( job >= 2000 &&  job <= 2112) {
            return " 战 神"
        }
		return "";
}