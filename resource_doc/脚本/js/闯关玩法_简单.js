load("nashorn:mozilla_compat.js");
importPackage(Packages.database);
importPackage(java.util);
importPackage(Packages.client);
importPackage(Packages.server);
importPackage(Packages.tools);
importPackage(Packages.tools.packet);
importPackage(Packages.scripting);

var 关 = "#fUI/UIWindow.img/Memo/check0#"//方框 - 未打钩
var 开 = "#fUI/UIWindow.img/Memo/check1#"//方框 - 打钩
var 彩虹 = "#fEffect/ItemEff/1071085/effect/walk1/2#";
var time = new Date().getTime();

var status, 副本, victim;
var fbmc = "怪物公园简单";//副本名称
var minLevel = 50;//最低等级
var maxLevel = 255;//最高等级
var minPartySize = 1;//最低人数
var maxPartySize = 6;//最高人数
var cishuxianzhi = 2;//限制次数
var LogName = "怪物公园_简单"
var 蓝色箭头 = "1";
var 红色箭头 = "1";
var 广播 = "1";
var acanms = {
	每日简单: 2,
	每日普通: 2,
	每日困难: 2,
	最小等级: 50,
	最大等级: 255,
	最小人数: 1,
	最大人数: 6,
};

var 地图代码 = [
    952010000, 
    952010100,
    952010200,
    952010300,
    952010400,
    952010500
]



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
		     var acanlog = "怪物公园_简单";
			 var acancishu = 2;			
			if ( getxmwnjltuanduimeiria(acanlog,acancishu) ) {
				cm.sendOk("团队中有人次数已经用完"+acancishu+"次");
				cm.dispose();
				return;
			}
			if (!Partymipd()){
				cm.dispose();
				return;
			}
        if (cm.getParty() == null) {
            cm.sendOk("你没有队伍无法进入！");
            cm.dispose();
            return;
        } else if (!cm.isLeader()) {
            cm.sendOk("请让你的队长和我说话~");
            cm.dispose();
            return;
        } else {
            var party = cm.getParty().getMembers();
            var inMap = cm.partyMembersInMap();
            var levelValid = 0;
            for (var i = 0; i < party.size(); i++) {
                if (party.get(i).getLevel() >= minLevel && party.get(i).getLevel() <= maxLevel)
                    levelValid++;
            }
            if (inMap < minPartySize || inMap > maxPartySize) {
                cm.sendOk("你的队伍人数不足" + minPartySize + "人.请把你的队伍人员召集到#m" + cm.getPlayer().getMapId() + "#在进入副本.");
                cm.dispose();
                return;
            } else if (levelValid != inMap) {
                cm.sendOk("请确保你的队伍里所有人员都在本地图，且最小等级在 " + minLevel + " 和 " + maxLevel + "之间.");
                cm.dispose();
                return;
            } else {
                var em = cm.getEventManager(fbmc);
                if (em == null) {
                    cm.sendOk("#d当前副本未开启，请联系管理员。");
                    cm.dispose();
                    return;
                }
                副本 = true;
                for (var i = 0; i < 地图代码.length; i++) {
                    if (cm.getPlayerCount(地图代码[i]) > 0) {
                        副本 = false;
                    }
                }
                if (副本 == true) {
                    var party = cm.getParty().getMembers();
                    var inMap = cm.partyMembersInMap();
                    em.startInstance(cm.getParty(), cm.getPlayer().getMap());
					gainxmwnjltuanduimeiria(acanlog,1);
                    for (var i = 0; i < 召唤怪物.length; i++) {
                        cm.spawnMobOnMap(召唤怪物[i][0], 召唤怪物[i][1], 召唤怪物[i][2], 召唤怪物[i][3], 召唤怪物[i][4], 召唤怪物[i][5]);
                    }
                    var it = party.iterator();
                    while (it.hasNext()) {
                        var cPlayer = it.next();
                        victim = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
						victim.setBossLog("怪物公园_简单", -victim.getBossLog("怪物公园_简单"))
						victim.setBossLog("怪物公园_普通", -victim.getBossLog("怪物公园_普通"))
						victim.setBossLog("怪物公园_困难", -victim.getBossLog("怪物公园_困难"))
						victim.setBossLog("怪物公园_简单",1)
                        victim.setBossLog("记录怪物公园时间", -victim.getBossLog("记录怪物公园时间"))
                        victim.setBossLog("怪物公园时间", -victim.getBossLog("怪物公园时间"))
                        victim.setBossLog("怪物公园时间", parseInt(time / 1000));
                    }
                    cm.dispose();
                } else {
                    cm.sendOk("#d当前频道副本正在挑战，请更换频道后再试。");
                    cm.dispose();
                }
            }
        }
    }
}

var 召唤怪物 = [//怪物代码,数量,x坐标,y坐标,地图代码,怪物血量
    //----第一张地图-----
    [7120103, 10, -1047, -1, 952010000, 500000],
    [7120103, 10, -947, -1, 952010000, 500000],
    [7120103, 10, -847, -1, 952010000, 500000],
    [7120103, 10, -747, -1, 952010000, 500000],
    [7120103, 10, -647, -1, 952010000, 500000],
    [7120103, 10, -547, -1, 952010000, 500000],
    [7120103, 10, -347, -1, 952010000, 500000],
    [7120103, 10, -247, -1, 952010000, 500000],
    [7120103, 10, -147, -1, 952010000, 500000],
    [7120103, 10, 47, -1, 952010000, 500000],
    [7120103, 10, 147, -1, 952010000, 500000],
    [7120103, 10, 247, -1, 952010000, 500000],
    [7120103, 10, 347, -1, 952010000, 500000],
    [7120103, 10, -1047, -330, 952010000, 500000],
    [7120103, 10, -947, -330, 952010000, 500000],
    [7120103, 10, -847, -330, 952010000, 500000],
    [7120103, 10, -747, -330, 952010000, 500000],
    [7120103, 10, -647, -330, 952010000, 500000],
    [7120103, 10, -547, -330, 952010000, 500000],
    [7120103, 10, -347, -330, 952010000, 500000],
    [7120103, 10, -247, -330, 952010000, 500000],
    [7120103, 10, -147, -330, 952010000, 500000],
    [7120103, 10, 47, -330, 952010000, 500000],
    [7120103, 10, 147, -330, 952010000, 500000],
    [7120103, 10, 247, -330, 952010000, 500000],
    [7120103, 10, 347, -330, 952010000, 500000],
    //----第二张地图-----
    [7120104, 10, -1314, 60, 952010100, 1000000],
    [7120104, 10, -1214, 60, 952010100, 1000000],
    [7120104, 10, -1114, 60, 952010100, 1000000],
    [7120104, 10, -1014, 60, 952010100, 1000000],
    [7120104, 10, -914, 60, 952010100, 1000000],
    [7120104, 10, -814, 60, 952010100, 1000000],
    [7120104, 10, -714, 60, 952010100, 1000000],
    [7120104, 10, -614, 60, 952010100, 1000000],
    [7120104, 10, -514, 60, 952010100, 1000000],
    [7120104, 10, -414, 60, 952010100, 1000000],
    [7120104, 10, -314, 60, 952010100, 1000000],
    [7120104, 10, -214, 60, 952010100, 1000000],
    [7120104, 10, -114, 60, 952010100, 1000000],
    [7120104, 10, -14, 60, 952010100, 1000000],
    [7120104, 10, 14, 60, 952010100, 1000000],
    [7120104, 10, 114, 60, 952010100, 1000000],
    [7120104, 10, 214, 60, 952010100, 1000000],
    [7120104, 10, 314, 60, 952010100, 1000000],
    [7120104, 10, 414, 60, 952010100, 1000000],
    [7120104, 10, 514, 60, 952010100, 1000000],
    [7120104, 10, -1314, -266, 952010100, 1000000],
    [7120104, 10, -1214, -266, 952010100, 1000000],
    [7120104, 10, -1114, -266, 952010100, 1000000],
    [7120104, 10, -1014, -266, 952010100, 1000000],
    [7120104, 10, -914, -266, 952010100, 1000000],
    [7120104, 10, -814, -266, 952010100, 1000000],
    [7120104, 10, -714, -266, 952010100, 1000000],
    [7120104, 10, -614, -266, 952010100, 1000000],
    [7120104, 10, -514, -266, 952010100, 1000000],
    [7120104, 10, -414, -266, 952010100, 1000000],
    [7120104, 10, -314, -266, 952010100, 1000000],
    [7120104, 10, -214, -266, 952010100, 1000000],
    [7120104, 10, -114, -266, 952010100, 1000000],
    [7120104, 10, -14, -266, 952010100, 1000000],
    [7120104, 10, 14, -266, 952010100, 1000000],
    [7120104, 10, 114, -266, 952010100, 1000000],
    [7120104, 10, 214, -266, 952010100, 1000000],
    [7120104, 10, 314, -266, 952010100, 1000000],
    [7120104, 10, 414, -266, 952010100, 1000000],
    [7120104, 10, 514, -266, 952010100, 1000000],
    //----第三张地图-----
    [7120105, 10, -557, 50, 952010200, 1000000],
    [7120105, 10, -457, 50, 952010200, 1000000],
    [7120105, 10, -357, 50, 952010200, 1000000],
    [7120105, 10, -257, 50, 952010200, 1000000],
    [7120105, 10, -157, 50, 952010200, 1000000],
    [7120105, 10, -57, 50, 952010200, 1000000],
    [7120105, 10, 57, 50, 952010200, 1000000],
    [7120105, 10, 157, 50, 952010200, 1000000],
    [7120105, 10, 257, 50, 952010200, 1000000],
    [7120105, 10, 357, 50, 952010200, 1000000],
    [7120105, 10, 457, 50, 952010200, 1000000],
    [7120105, 10, 557, 50, 952010200, 1000000],
    [7120105, 10, 657, 50, 952010200, 1000000],
    [7120105, 10, 757, 50, 952010200, 1000000],
    [7120105, 10, 857, 50, 952010200, 1000000],
    [7120105, 10, 957, 50, 952010200, 1000000],
    [7120105, 10, 1057, 50, 952010200, 1000000],
    [7120105, 10, 1157, 50, 952010200, 1000000],
    [7120105, 10, -557, -280, 952010200, 1000000],
    [7120105, 10, -457, -280, 952010200, 1000000],
    [7120105, 10, -357, -280, 952010200, 1000000],
    [7120105, 10, -257, -280, 952010200, 1000000],
    [7120105, 10, -157, -280, 952010200, 1000000],
    [7120105, 10, -57, -280, 952010200, 1000000],
    [7120105, 10, 57, -280, 952010200, 1000000],
    [7120105, 10, 157, -280, 952010200, 1000000],
    [7120105, 10, 257, -280, 952010200, 1000000],
    [7120105, 10, 357, -280, 952010200, 1000000],
    [7120105, 10, 457, -280, 952010200, 1000000],
    [7120105, 10, 557, -280, 952010200, 1000000],
    [7120105, 10, 657, -280, 952010200, 1000000],
    [7120105, 10, 757, -280, 952010200, 1000000],
    [7120105, 10, 857, -280, 952010200, 1000000],
    [7120105, 10, 957, -280, 952010200, 1000000],
    [7120105, 10, 1057, -280, 952010200, 1000000],
    [7120105, 10, 1157, -280, 952010200, 1000000],

    //----第四张地图-----
    [3501000, 10, -1050, 61, 952010300, 1000000],
    [3501000, 10, -950, 61, 952010300, 1000000],
    [3501000, 10, -850, 61, 952010300, 1000000],
    [3501000, 10, -750, 61, 952010300, 1000000],
    [3501000, 10, -650, 61, 952010300, 1000000],
    [3501000, 10, -550, 61, 952010300, 1000000],
    [3501000, 10, -450, 61, 952010300, 1000000],
    [3501000, 10, -350, 61, 952010300, 1000000],
    [3501000, 10, -250, 61, 952010300, 1000000],
    [3501000, 10, -150, 61, 952010300, 1000000],
    [3501000, 10, -50, 61, 952010300, 1000000],
    [3501000, 10, 50, 61, 952010300, 1000000],
    [3501000, 10, 150, 61, 952010300, 1000000],
    [3501000, 10, 250, 61, 952010300, 1000000],
    [3501000, 10, 350, 61, 952010300, 1000000],
    [3501000, 10, 450, 61, 952010300, 1000000],
    [3501000, 10, 550, 61, 952010300, 1000000],
    [3501000, 10, 650, 61, 952010300, 1000000],
    [3501000, 10, 750, 61, 952010300, 1000000],
    [3501000, 10, 850, 61, 952010300, 1000000],
    [3501000, 10, 950, 61, 952010300, 1000000],
    [3501000, 10, -1050, -267, 952010300, 1000000],
    [3501000, 10, -950, -267, 952010300, 1000000],
    [3501000, 10, -850, -267, 952010300, 1000000],
    [3501000, 10, -750, -267, 952010300, 1000000],
    [3501000, 10, -650, -267, 952010300, 1000000],
    [3501000, 10, -550, -267, 952010300, 1000000],
    [3501000, 10, -450, -267, 952010300, 1000000],
    [3501000, 10, -350, -267, 952010300, 1000000],
    [3501000, 10, -250, -267, 952010300, 1000000],
    [3501000, 10, -150, -267, 952010300, 1000000],
    [3501000, 10, -50, -267, 952010300, 1000000],
    [3501000, 10, 50, -267, 952010300, 1000000],
    [3501000, 10, 150, -267, 952010300, 1000000],
    [3501000, 10, 250, -267, 952010300, 1000000],
    [3501000, 10, 350, -267, 952010300, 1000000],
    [3501000, 10, 450, -267, 952010300, 1000000],
    [3501000, 10, 550, -267, 952010300, 1000000],
    [3501000, 10, 650, -267, 952010300, 1000000],
    [3501000, 10, 750, -267, 952010300, 1000000],
    [3501000, 10, 850, -267, 952010300, 1000000],
    [3501000, 10, 950, -267, 952010300, 1000000],
    //----第五张地图-----
    [3501001, 2, -1043, 60, 952010400, 1000000],
    [3501001, 2, -943, 60, 952010400, 1000000],
    [3501001, 2, -843, 60, 952010400, 1000000],
    [3501001, 2, -743, 60, 952010400, 1000000],
    [3501001, 2, -643, 60, 952010400, 1000000],
    [3501001, 2, -543, 60, 952010400, 1000000],
    [3501001, 2, -443, 60, 952010400, 1000000],
    [3501001, 2, -343, 60, 952010400, 1000000],
    [3501001, 2, -243, 60, 952010400, 1000000],
    [3501001, 2, -143, 60, 952010400, 1000000],
    [3501001, 2, -43, 60, 952010400, 1000000],
    [3501001, 2, 43, 60, 952010400, 1000000],
    [3501001, 2, 143, 60, 952010400, 1000000],
    [3501001, 2, 243, 60, 952010400, 1000000],
    [3501001, 2, 343, 60, 952010400, 1000000],
    [3501001, 2, 443, 60, 952010400, 1000000],
    [3501001, 2, 543, 60, 952010400, 1000000],
    [3501001, 2, 643, 60, 952010400, 1000000],
    [3501001, 2, 743, 60, 952010400, 1000000],
    [3501001, 2, 843, 60, 952010400, 1000000],
    [3501001, 2, 943, 60, 952010400, 1000000],
    [3501001, 2, 1043, 60, 952010400, 1000000],
    [3501001, 2, -1043, -340, 952010400, 1000000],
    [3501001, 2, -943, -340, 952010400, 1000000],
    [3501001, 2, -843, -340, 952010400, 1000000],
    [3501001, 2, -743, -340, 952010400, 1000000],
    [3501001, 2, -643, -340, 952010400, 1000000],
    [3501001, 2, -543, -340, 952010400, 1000000],
    [3501001, 2, -443, -340, 952010400, 1000000],
    [3501001, 2, -343, -340, 952010400, 1000000],
    [3501001, 2, -243, -340, 952010400, 1000000],
    [3501001, 2, -143, -340, 952010400, 1000000],
    [3501001, 2, -43, -340, 952010400, 1000000],
    [3501001, 2, 43, -340, 952010400, 1000000],
    [3501001, 2, 143, -340, 952010400, 1000000],
    [3501001, 2, 243, -340, 952010400, 1000000],
    [3501001, 2, 343, -340, 952010400, 1000000],
    [3501001, 2, 443, -340, 952010400, 1000000],
    [3501001, 2, 543, -340, 952010400, 1000000],
    [3501001, 2, 643, -340, 952010400, 1000000],
    [3501001, 2, 743, -340, 952010400, 1000000],
    [3501001, 2, 843, -340, 952010400, 1000000],
    [3501001, 2, 943, -340, 952010400, 1000000],
    [3501001, 2, 1043, -340, 952010400, 1000000],
    //----BOSS地图-----
    [8880700, 1, 99, 50, 952010500, 10000000],
]

//给团队账号每日记录值
function gainxmwnjltuanduimeiria(log,cs) {
	
	if (cm.getParty() == null) {
		gainxmwnjlzd(log,1,cm.getPlayer().getId());
		return ;
	}
	party = cm.getParty().getMembers().iterator();
	while (party.hasNext()) {
		var mcha = party.next();
		var xmcserv = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
		while (xmcserv.hasNext()) {
			var xmfwq = xmcserv.next();//服务器频道
			var cserv1 = xmfwq.getPlayerStorage().getAllCharacters().iterator();
			
			while (cserv1.hasNext()) {
				var mch = cserv1.next();//玩家
				
				if ( mch.getId() == mcha.getId() ) {
					gainxmwnjlzd(log,1,mch.getId());
					cm.getPlayer().dropMessage(5, "["+log+"] 数值 +1");
					
				}
			}
		
				
		}

	}
	return ;
}

//读取团队账号每日记录并返回是否通过
function getxmwnjltuanduimeiria(log,cs) {
	fhlx = false;
	wodcid = cm.getPlayer().getId();
	if (cm.getParty() == null) {
		if (getacanwnjzd(log,1,wodcid) >= cs) {
		fhlx = true;
		}
		return fhlx;
	}
	party = cm.getParty().getMembers().iterator();
	while (party.hasNext()) {
		var mcha = party.next();
		var xmcserv = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
		while (xmcserv.hasNext()) {
			var xmfwq = xmcserv.next();//服务器频道
			var cserv1 = xmfwq.getPlayerStorage().getAllCharacters().iterator();
			
			while (cserv1.hasNext()) {
				var mch = cserv1.next();//玩家
				
				if ( mch.getId() == mcha.getId() ) {
					accid = mch.getId();
					jlzcs = getacanwnjzd(log,1,accid);
					cm.getPlayer().dropMessage(5, "["+log+"]记录值 玩家："+mch.getName()+" ["+jlzcs+"/"+cs+"]");
					
					if (jlzcs >= cs) {
						cm.getPlayer().dropMessage(5, "玩家："+mch.getName()+" 次数已经用完,请踢出队伍");
						fhlx = true;
						break;
					}
					
				}
			}
		
				
		}

	}
	return fhlx;
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
	    var conn =  Packages.database.DBConPool.getInstance().getDataSource().getConnection();
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

function gainxmwnjlzd(wnjllog,cs,accid) {
	var conn = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
	var sql = "SELECT * FROM xmwnjl WHERE bossid = '"+wnjllog+"' AND characterid = "+accid+" ;";
	var pstmt = conn.prepareStatement(sql);
	var result = pstmt.executeQuery();	
	
	if (result.next()) {
		result.close();
	    var conn = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
	    var sql = "UPDATE xmwnjl SET count = count+"+cs+"  WHERE bossid = '"+wnjllog+"' AND characterid = "+accid+" ;";
	    var pstmt = conn.prepareStatement(sql);
	    pstmt.executeUpdate();
		pstmt.close();		
	} else {
	var conn = Packages.database.DBConPool.getInstance().getDataSource().getConnection();
	var sql = "insert into xmwnjl (time,bossid,count,characterid) values (CURRENT_TIMESTAMP(),?,?,?);";          
    var psu = conn.prepareStatement(sql);
	psu.setString(1,wnjllog);
	psu.setInt(2,cs);
	psu.setInt(3,accid);
    psu.executeUpdate();	
	psu.close();	
	}	
}

function getConnection() {
	return cm.getConnection();
}

function Partymipd() {
	最大等级 = acanms.最大等级;
	最小等级 = acanms.最小等级;
	最大人数 = acanms.最大人数;
	最小人数 = acanms.最小人数;
	返回 = true;
    if (cm.getParty() == null) { // No Party
	cm.sendOk("请组队再来找我");
	cm.dispose();
	return;
    } else if (!cm.isLeader()) { // Not Party Leader
	cm.sendOk("请叫你的队长来找我!");
	cm.dispose();
	return;
    }		
	party = cm.getParty().getMembers();	
	it = party.iterator();	
	next = true;	
	mapId = cm.getMapId();
	levelValid = 0;
	inMap = 0;
    while (it.hasNext()) {
    cPlayer = it.next();
	
	if ((cPlayer.getLevel() >= 最小等级 && cPlayer.getLevel() <= 最大等级) ) {
	levelValid += 1;
	} else {
	next = false;
	}		
	if (cPlayer.getMapid() == mapId) {
	inMap += 1;
	}	
	}
	if (party.size() > 最大人数 || inMap < 最小人数) {
	    next = false;
	}
	if (!next) {
	返回 = false;
	cm.sendOk("你的队伍需要"+最小人数+"个人以上,等级必须在"+最小等级+"-"+最大等级+"之间,请确认你的队友有没有都在这里!");
	// cm.dispose();
	}
	// cm.getPlayer().dropMessage(5, "测试信息:"+party.size()+"  inMap:"+inMap);
	return 返回;
	
}