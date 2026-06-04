var 蓝色箭头 = "#fUI/UIWindow.img/Quest/icon8/0#";//蓝色右箭头
var 黑桃1 = "#fUI/GuildMark/Mark/Pattern/00004018/1#";
var 广播 = "广播";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var minPartySize = 1;//最低人数
var maxPartySize = 6;//最高人数
var minLevel = 15;//最低等级
var maxLevel = 250;//最高等级
var 任务简述 = "任务简述";
var Fi = "#fUI/GuildMark.img/Mark/Letter/00005005/1#";
var Ui = "#fUI/GuildMark.img/Mark/Letter/00005020/1#";
var Ni = "#fUI/GuildMark.img/Mark/Letter/00005013/1#";
var Ci = "#fUI/GuildMark.img/Mark/Letter/00005002/1#";
var Ti = "#fUI/GuildMark.img/Mark/Letter/00005019/1#";
var Ii = "#fUI/GuildMark.img/Mark/Letter/00005008/1#";
var Oi = "#fUI/GuildMark.img/Mark/Letter/00005014/1#";
var acanms = {
	每日简单: 2,
	每日普通: 2,
	每日困难: 2,
	最小等级: 50,
	最大等级: 255,
	最小人数: 1,
	最大人数: 6,
};
function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1)
        status++;
    else {
        cm.dispose();
        return;
    }
    if (status == 0) {
		/*if (cm.getPlayer().isGM() == 0){
			cm.sendOk("副本暂时调整。");
			cm.dispose();
			return;
		}*/
		wodcid = cm.getPlayer().getId();
		
		当前简单 = getacanwnjzd("怪物公园_简单",1,wodcid);
		当前普通 = getacanwnjzd("怪物公园_普通",1,wodcid);
		当前困难 = getacanwnjzd("怪物公园_困难",1,wodcid);
		
        var text =  "   "+Fi+Ui+Ni+Ci+Ti+Ii+Oi+Ni+" #d- 怪物公园[团队副本]#k\r\n";
		text += ""+粉心+" #k#e玩法介绍如下:#n#l\r\n";	
        text += ""+粉心+" ①人数限制:#r " + minPartySize + " #b- #r" + maxPartySize + "#k队员\r\n"
        text += ""+粉心+" ②等级限制：#r " + minLevel + " #b- #r" + maxLevel + "级 #k\r\n"
		text += ""+粉心+" #b每日简单：["+当前简单+"/"+acanms.每日简单+"]\r\n"
		text += ""+粉心+" #b每日普通：["+当前普通+"/"+acanms.每日普通+"]\r\n"
		text += ""+粉心+" #b每日困难：["+当前困难+"/"+acanms.每日困难+"]\r\n"
		text += ""+粉心+" #r该玩法分简单/普通/困难/三个阶段#l\r\n";
		text += ""+粉心+" #r每个阶段的怪物不同/难度也不同/奖励都一样#l\r\n";
		text += ""+粉心+" #b限时30分钟,30分钟内没有挑战成功视为失败#r(无奖)#l\r\n";
		text += ""+粉心+" #k挑战完最后一关后从最右边传送门出来(获取奖励)#l\r\n";
		//text += ""+粉心+" #k30分钟内挑战成功的根据挑战时长给出评分#r(获取奖励)#l\r\n";
		//text += ""+粉心+" #r奖励列表如下：#l\r\n";
        //text += "#v4321029##v4321023##v2019025##v2019026##v2019027##v2011103##v2019019##v2049124##v2049116##v2340000##l\r\n\r\n\r\n";
		text += "   #b#L1#"+黑桃1+"简单#l    #L2#"+黑桃1+"普通#l    #L3#"+黑桃1+"困难#l\r\n"
        cm.sendSimpleS(text,2);

    } else if (status == 1) {
        if (selection == 1) {
			cm.dispose();
            cm.openNpc(9000049,"闯关玩法_简单");
		} else  if (selection == 2) {
			cm.dispose();
			//cm.sendOk("暂时未开放");
            cm.openNpc(9000049,"闯关玩法_普通");
			} else  if (selection == 3) {
			cm.dispose();
			//cm.sendOk("暂时未开放");
            cm.openNpc(9000049,"闯关玩法_困难");
}
	}
}


function getacanwnjzd(jiluid,type,accid) {
	var xmsjfh = 0;
	var conn = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
	var sql = "SELECT * FROM xmwnjl WHERE characterid = "+accid+" AND bossid = '"+jiluid+"' ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();		
	if (result.next()) {
	xmsjfh = result.getInt("count");
	//type = result.getInt("type");
	var kzhq_time = result.getTimestamp("time");
	//var kzhq_year = kzhq_time.getFullYear(); //得到年份
	var kzhq_month = kzhq_time.getMonth()+1;
	var kzhq_date = kzhq_time.getDate();
	var kzhq_hour = kzhq_time.getHours();
	if (type == 1) {
		var now = new Date();
        var year = now.getFullYear(); //得到年份
        var month = now.getMonth()+1;//得到月份
        var date = now.getDate();//得到日期
		//month = 6
		//date = 10
		if (month != kzhq_month || date >= (kzhq_date+1) || date < kzhq_date ) {
	    xmsjfh = 0;
	    var conn = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
	    var sql = "UPDATE xmwnjl SET count = 0  WHERE characterid = "+accid+" AND bossid = '"+jiluid+"' ;";
	    var pstmt = conn.prepareStatement(sql);
	    pstmt.executeUpdate();
		pstmt.close();
	    }
	}
	} 
	result.close();
	pstmt.close();
	return xmsjfh;
}
