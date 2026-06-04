
var mi0 = "┏━━━━━━━━━━━┓";
var mi1 = "┃     - XiaoMiMS -     ┃";
var mi2 = "┃ 脚本仿制  　定制脚本 ┃";
var mi3 = "┃ 技术支持 　 游戏顾问 ┃";
var mi4 = "┃ ＷＺ添加　  地图制作 ┃";
var mi5 = "┣━━━━━━━━━━━┫";
var mi6 = "┃　唯一QQ:526703257    ┃";
var mi7 = "┗━━━━━━━━━━━┛";
var mi8 = "请不要修改版权信息，否则脚本将会报错";


var 表情高兴 = "#fUI/GuildBBS/GuildBBS/Emoticon/Basic/2#";
var status = -1;
var selection;
var 彩虹 ="#fEffect/ItemEff/1071085/effect/walk1/2#";
var 积分 = new Array(1,2);
var 随机积分 = 积分[Math.floor(Math.random() * 积分.length)];
var jilusl = new Array();
var jilupd = new Array();
var chaxx = new Array();
var xmml1 = 0;
var xmml2 = 0;
var xmml3 = 0;
var xmml4 = 0;
var nowchannel;
var nowmap;
var nowplayer;

var fuxuan1 = 0;
var 未勾 = "#fUI/Basic.img/CheckBox/0#"; //框空白
var 已勾 = "#fUI/Basic.img/CheckBox/1#"; //框选中
var gxkzt1 = 未勾;
function start() {
    
	status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
	if (selection == 110000) {
		status = -1;
	}
	
	if (selection == 110001) {
		if (fuxuan1 == 0){
			gxkzt1 = 已勾;
			fuxuan1 = 1;
			cm.getPlayer().dropMessage(5, "只查看当前地图玩家");
		} else {
			gxkzt1 = 未勾;
			fuxuan1 = 0;
			cm.getPlayer().dropMessage(5, "查看全服玩家信息");
		}
		
		status = -1;
	}
	
    if (mode == 1) {
        status++;
    } else if (mode == 0) {
        // status--;
		cm.dispose();
        return;
    } else {
        cm.dispose();
        return;
    }
    if (status == 0) {
		
	dqmapid = cm.getPlayer().getMapId();
	var i = 0;
	chaxx = new Array();
	
    var text = "";
	var text2 = "";
	var xmcserv = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
	while (xmcserv.hasNext()) {
		var xmfwq = xmcserv.next();	
		var cserv1 = xmfwq.getPlayerStorage().getAllCharacters().iterator();		
	    while (cserv1.hasNext()) {	
            
            var mch = cserv1.next();
			
			//mch.getClient().getChannel();
			jsname = mch.getName();
			mapid = mch.getMapId();
			pdid = mch.getClient().getChannel();
			
			chaxx.push( { chaid:mch.getId(),name:jsname,mapid:mapid,pdid:pdid  } );
			if (fuxuan1 == 1 && mapid == dqmapid ) {
				
				text += "#b#L"+i+"#"+jsname+""
				for (var j = 16 - jsname.replaceAll("[^\\x00-\\xff]", "**").getBytes().length; j > 0; j--) {
				text += " ";
				}
				text += "#d"+mch.getMap().getMapName()+"["+mapid+"]#l\r\n"
			} else if (fuxuan1 == 0 ) {
				
				text += "#b#L"+i+"#"+jsname+""
				for (var j = 16 - jsname.replaceAll("[^\\x00-\\xff]", "**").getBytes().length; j > 0; j--) {
				text += " ";
				}
				text += "#d"+mch.getMap().getMapName()+"["+mapid+"]#l\r\n"
			}
			
			i++;
			}
			
			
	    }
        
		
		
        text2 += "   #b<在线玩家管理>  #r在线人数："+i+"\r\n"
		text2 += "#b#L110001#"+gxkzt1+"[当前地图]#l\r\n"
		
		
		cm.sendSimple(text2+text);
        
		



		
    } else if (status == 1) {
		// cm.getPlayer().dropMessage(5, "信息：status "+status+"  selection "+selection);
		xmml1 = selection;
		var text = "";
		
		// text += "测试：#b"+chaxx[xmml1].name+" #kID："+chaxx[xmml1].chaid+"\r\n"
		text += "正在操作的角色：#b"+chaxx[xmml1].name+" #kID："+chaxx[xmml1].chaid+"\r\n\r\n"
		text += "#r#L10#[充值元宝]#l#L11#[充值累计]#l#L14#[充值每日赞助记录]#l\r\n\r\n"	
		text += "#b#L12#[扣除元宝]#l#L13#[扣除累计]#l#L15#[扣除每日赞助记录]#l\r\n\r\n\r\n"	
		text += "#b#L3#[给点卷]#l#L16#[给抵用]#l#L4#[给金币]#l#L5#[满技能]#l\r\n\r\n\r\n"	
		text += "		 #d- - - - - - - - - - - - - - - - - - \r\n"
	//	text += "#b#L1#[跟踪]#l#L2#[拉人]#l#L7#[掉线]#l#L8#[封号]#l\r\n\r\n"
		text += "#b#L1#[跟踪]#l#L2#[拉人]#l#L7#[掉线]#l\r\n\r\n"		
		
		text += "		  #d- - - - - - 角色信息修改 - - - - - \r\n\r\n"
		text += "#b#L101#[等级]#l#L102#[职业]#l#L103#[能力值]#l#L6#[转职]#l \r\n\r\n"//#L104#[技能点]#l
		
		text += "#b#L111#[力量]#l#L112#[敏捷]#l#L113#[智力]#l#L114#[运气]#l#L115#[HP]#l#L116#[MP]#l\r\n"
		text += "\r\n#r#L110000#[返回主页]#l\r\n"
		cm.sendSimple(text);
		// cm.warp(jilusl[sele],0);
		// cm.getPlayer().changeChannel(jilupd[sele]);
		// cm.getPlayer().dropMessage(5, "信息1：status " + chaxx[xmml1].chaid);
        // cm.dispose();			 
		
    } else if (status == 2) {
		xmml2 = selection;
		// cm.getPlayer().dropMessage(5, "信息2：status " + chaxx[xmml1].chaid);
		if (xmml2 == 1) {
			    var target = chaxx[xmml1];
			    /*----  1. 飞地图（同频道立即生效）----*/
			    var targetMap = cm.getMapFactory().getMap(target.mapid);
			    cm.getPlayer().changeMap(targetMap, targetMap.getPortal(0));
			    /*----  2. 如果不在同频道，再切线 ----*/
			    if (cm.getPlayer().getClient().getChannel() != target.pdid) {
			        cm.getPlayer().changeChannel(target.pdid);
			    }
			    cm.sendOk("操作成功：已传送到 #r" + target.name + "#k 所在位置！");
			status = -1;
			return;
		} else if (xmml2 == 2) {
			laren(chaxx[xmml1].chaid);
			cm.sendOk("操作成功2");
			status = -1;
			return;
		} else if (xmml2 == 3) {
			cm.sendGetNumber("请确定充值点卷？\r\n#b"+chaxx[xmml1].name+"\r\n请输入充值#r点卷",1,1,500000000);
		} else if (xmml2 == 16) {
			cm.sendGetNumber("请确定充值抵用？\r\n#b"+chaxx[xmml1].name+"\r\n请输入充值#r抵用",1,1,500000000);
		} else if (xmml2 == 4) {
			cm.sendGetNumber("请确定充值金币？\r\n#b"+chaxx[xmml1].name+"\r\n请输入充值#r金币",1,1,1000000000);
		} else if (xmml2 == 5) {
			maxAllSkills(chaxx[xmml1].chaid);
			cm.sendOk("操作成功2");
			status = -1;
			return;
		} else if (xmml2 == 6) {
			cm.sendGetNumber("转职玩家：#b"+chaxx[xmml1].name+"\r\n请输入职业代码",0,0,1000000000);
		} else if (xmml2 == 7) {
			forceRemovePlayerByCharName(chaxx[xmml1].chaid);
			cm.sendOk("操作成功：已将 #r" + chaxx[xmml1].name + "#k 踢下线！");
			status = -1;
			return;
		} else if (xmml2 == 8) {
			cm.封号(chaxx[xmml1].name);
			// ban
			cm.sendOk("操作成功");
			status = -1;
			return;
		} else if (xmml2 == 9) {
			//var dnowmap=cm.getPlayer().getClient().getChannelServer().getMapFactory().getMap(180000001);
			cm.getPlayer().changeMap(180000001);
			cm.getPlayer().dropMessage("由于你不规范操作被拉进了小黑屋。");
			cm.getPlayer().dropMessage("由于你不规范操作被拉进了小黑屋。");
			cm.getPlayer().dropMessage("由于你不规范操作被拉进了小黑屋。");
			cm.dispose();
			status = -1;
			return;
		} else if (xmml2 == 10) {
			cm.sendGetNumber("请确定充值元宝？\r\n#b"+chaxx[xmml1].name+"\r\n请输入充值#r元宝",1,1,1000000);
		} else if (xmml2 == 11) {
			cm.sendGetNumber("请确定充值累计充值？\r\n#b"+chaxx[xmml1].name+"\r\n请输入充值#r累计充值",1,1,1000000);	
			
		} else if (xmml2 == 12) {
			cm.sendGetNumber("请确定扣除元宝？\r\n#b"+chaxx[xmml1].name+"\r\n请输入扣除#r元宝",1,1,1000000);
		} else if (xmml2 == 13) {
			cm.sendGetNumber("请确定扣除累计充值？\r\n#b"+chaxx[xmml1].name+"\r\n请输入扣除#r累计充值",1,1,1000000);	
		} else if (xmml2 == 14) {
			cm.sendGetNumber("请确定充值充值每日赞助记录？\r\n#b"+chaxx[xmml1].name+"\r\n请输入充值#r每日赞助记录",1,1,1000000);	
		} else if (xmml2 == 15) {
			cm.sendGetNumber("请确定扣除充值每日赞助记录？\r\n#b"+chaxx[xmml1].name+"\r\n请输入扣除#r每日赞助记录",1,1,1000000);	
		} else {
			cm.sendGetNumber("正在修改玩家：#b"+chaxx[xmml1].name+"#r\r\n请输入修改值：",1,1,1000000);
		}
		// cm.dispose();
		
	} else if (status == 3) {
		// cm.getPlayer().dropMessage(5, "信息3：status " + chaxx[xmml1].chaid);
		xmml3 = selection;
		if (xmml2 == 1) {
		} else if (xmml2 == 2) {
		} else if (xmml2 == 3) {
			gainmodifyCSPoints(chaxx[xmml1].chaid,xmml3);
		} else if (xmml2 == 16) {
			gainmodifyCSPoints2(chaxx[xmml1].chaid,xmml3);
		} else if (xmml2 == 4) {
			gainMeso(chaxx[xmml1].chaid,xmml3);
		} else if (xmml2 == 6) {
			changeJob(chaxx[xmml1].chaid,xmml3);
		} else if (xmml2 == 10) {
			var chasl = getPlayerxm(chaxx[xmml1].chaid);
			if (chasl == null) {
				cm.getPlayer().dropMessage(5, "信息：玩家已离线 充值失败 "+chaxx[xmml1].name+"");
				return;
			}
			
				chasl.setmoneyb(chasl.getmoneyb() + xmml3);
				cm.getItemLog("GM元宝调整", "\r\n管理员[" + cm.getName() + "] 为玩家 [" + chaxx[xmml1].name + "] 增加了 " + xmml3 + " 元宝。\r\n");
				cm.sendOk("操作成功：为玩家 " + chaxx[xmml1].name + " 增加了 " + xmml3 + " 元宝。");
		} else if (xmml2 == 11) {
		var chasl = getPlayerxm(chaxx[xmml1].chaid);
			if (chasl == null) {
				cm.getPlayer().dropMessage(5, "信息：玩家已离线 充值失败 " + chaxx[xmml1].name + "");
				return;
			}
				chasl.setlpjf(chasl.getlpjf() + xmml3); // 使用 lpjf 字段
				cm.sendOk("操作成功：为玩家 " + chaxx[xmml1].name + " 增加了 " + xmml3 + " 点累计积分。");
				
		} else if (xmml2 == 12) {
			var chasl = getPlayerxm(chaxx[xmml1].chaid);
			if (chasl == null) {
				cm.getPlayer().dropMessage(5, "信息：玩家已离线 充值失败 "+chaxx[xmml1].name+"");
				return;
			}
			
				chasl.setmoneyb(chasl.getmoneyb() - xmml3);
				cm.getItemLog("GM元宝调整", "\r\n管理员[" + cm.getName() + "] 为玩家 [" + chaxx[xmml1].name + "] 扣除了 " + xmml3 + " 元宝。\r\n");
				cm.sendOk("操作成功：为玩家 " + chaxx[xmml1].name + " 扣除了 " + xmml3 + " 元宝。");
				
		} else if (xmml2 == 13) {
		var chasl = getPlayerxm(chaxx[xmml1].chaid);
			if (chasl == null) {
				cm.getPlayer().dropMessage(5, "信息：玩家已离线 充值失败 " + chaxx[xmml1].name + "");
				return;
			}
				chasl.setlpjf(chasl.getlpjf() - xmml3); // 使用 lpjf 字段
				cm.sendOk("操作成功：为玩家 " + chaxx[xmml1].name + " 减掉了 " + xmml3 + " 点累计积分。");
		} else if (xmml2 == 14) {
			if (!setDailyZanZhu(chaxx[xmml1].name, xmml3, true))
				cm.sendOk("玩家已离线，每日赞助记录失败！");
		} else if (xmml2 == 15) {
			if (!setDailyZanZhu(chaxx[xmml1].name, xmml3, false))
				cm.sendOk("玩家已离线，每日赞助扣除失败！");
		} else {
			
			setchashuxin(chaxx[xmml1].chaid,getidzhanshuxin(xmml2),xmml3);
			
		}
		cm.sendOk("操作成功");
		status = -1;
		return;
		
	}
}
// mch.modifyCSPoints(1, +数量, true);
// 统一函数：增减每日赞助记录（不允许出现负数）
function setDailyZanZhu(charName, amount, isAdd) {
    var found = false;
    var ch   = null;

    // 1. 找到在线角色
    var iter = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
    while (iter.hasNext()) {
        var pcs = iter.next().getPlayerStorage().getAllCharacters().iterator();
        while (pcs.hasNext()) {
            var c = pcs.next();
            if (c.getName() == charName) {
                ch = c;
                found = true;
                break;
            }
        }
        if (found) break;
    }
    if (!found) return false;          // 离线

    // 2. 读出今日已赞助额度
    var cur = ch.getBossLog("每日赞助");

    // 3. 扣除时做负值保护
    if (!isAdd && cur - amount < 0) {
        cm.sendOk("扣除失败：玩家今日赞助记录仅剩 " + cur + "，\r\n无法扣除 " + amount + "。");
        return false;                    // 拒绝写入
    }

    // 4. 真正写入
    var delta = isAdd ? amount : -amount;
    ch.setBossLog("每日赞助", 0, delta);
	// 6. 给 GM 回显
	var op = isAdd ? "增加" : "减少";
	cm.getPlayer().dropMessage(5, "信息：将玩家 " + charName + " 每日赞助 " + op + " " + amount + " 点（当前总共：" + (cur + delta) + "）");
    // 5. 日志
    Packages.tools.FileoutputUtil.log("log/玩家相关/GM每日赞助.log", "[" + cm.getName() + "] " + (isAdd ? "添加" : "扣除") + " " + charName + " 每日赞助 " + amount + "（当前：" + (cur + delta) + "）");
    return true;
}


//踢玩家下线
function forceRemovePlayerByCharName(id) {
	
	var chasl = getPlayerxm(id);
	if (chasl == null) {
		cm.getPlayer().dropMessage(5, "1信息：玩家已离线 "+chaxx[xmml1].name+"");
		return;
	}
	// cm.getPlayer().dropMessage(5, "测试信息22 ");
	// Packages.handling.channel.ChannelServer.forceRemovePlayerByCharName(chasl.getName());
	chasl.getClient().getSession().close();
	// cm.getPlayer().clearSkills();
	cm.getPlayer().dropMessage(5, "信息：成功将玩家踢下线 "+chaxx[xmml1].name+"");
}


//转职程序
function changeJob(id,cs) {
	
	var chasl = getPlayerxm(id);
	if (chasl == null) {
		cm.getPlayer().dropMessage(5, "信息：玩家已离线 "+chaxx[xmml1].name+"");
		return;
	}
	// cm.getPlayer().dropMessage(5, "测试信息22 ");
	chasl.changeJob(cs);
	// cm.getPlayer().clearSkills();
	cm.getPlayer().dropMessage(5, "信息：玩家转职成功 "+chaxx[xmml1].name+"");
}



//满技能
function maxAllSkills(id) {
	
	var chasl = getPlayerxm(id);
	if (chasl == null) {
		cm.getPlayer().dropMessage(5, "信息：玩家已离线 "+chaxx[xmml1].name+"");
		return;
	}
	// cm.getPlayer().dropMessage(5, "测试信息22 ");
	chasl.maxSkillsByJob();
	chasl.dropMessage(1, "信息：恭喜您，GM为你满技能");
	// cm.getPlayer().clearSkills();
	cm.getPlayer().dropMessage(5, "信息：满技能成功 "+chaxx[xmml1].name+"");
}



//充值金币
function gainMeso(id,cs) {
	var chasl = getPlayerxm(id);
	if (chasl == null) {
		cm.getPlayer().dropMessage(5, "信息：玩家已离线 充值失败 "+chaxx[xmml1].name+"");
		return;
	}
	chasl.gainMeso(cs,true);
	chasl.dropMessage(5, "信息：GM给了你 "+cs+" 金币");
	cm.getPlayer().dropMessage(5, "信息：充值金币成功 "+chaxx[xmml1].name+"");
}

//充值点卷
function gainmodifyCSPoints(id,cs) {
	
	var chasl = getPlayerxm(id);
	if (chasl == null) {
		cm.getPlayer().dropMessage(5, "信息：玩家已离线 充值失败 "+chaxx[xmml1].name+"");
		return;
	}
	chasl.modifyCSPoints(1, +cs, true);
	chasl.dropMessage(1, "信息：GM 给了你 "+cs+" 点卷");
	cm.getPlayer().dropMessage(5, "信息：成功给玩家 ["+chaxx[xmml1].name+"] 充值 "+cs+" 点卷");
}

//充值抵用
function gainmodifyCSPoints2(id,cs) {
	
	var chasl = getPlayerxm(id);
	if (chasl == null) {
		cm.getPlayer().dropMessage(5, "信息：玩家已离线 充值失败 "+chaxx[xmml1].name+"");
		return;
	}
	chasl.modifyCSPoints(2, +cs, true);
	chasl.dropMessage(1, "信息：GM 给了你 "+cs+" 抵用卷");
	cm.getPlayer().dropMessage(5, "信息：成功给玩家 ["+chaxx[xmml1].name+"] 充值 "+cs+" 抵用卷");
}

//拉人函数
function laren(id) {
	var chasl = getPlayerxm(id);
	if (chasl == null) {
		cm.getPlayer().dropMessage(5, "信息：玩家已离线");
		return;
	}
	 if (cm.getPlayer().getClient().getChannel() != chasl.getClient().getChannel() ){
		 chasl.changeChannel(cm.getPlayer().getClient().getChannel());
	 }
	chasl.changeMap(cm.getPlayer().getMapId());
	chasl.dropMessage(5, "信息：GM传送了你");
}

//获取玩家实例函数
function getPlayerxm(id) {
	var cha = null;
	var xmcserv = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
	while (xmcserv.hasNext()) {
		var xmfwq = xmcserv.next();//服务器频道
		var cserv1 = xmfwq.getPlayerStorage().getAllCharacters().iterator();
	    while (cserv1.hasNext()) {
        var mch = cserv1.next();//玩家
			if (id == mch.getId() ){
				cha = mch;
				break;
			}
			
			
			
	    }			
	}
	return cha;
}


function getidzhanshuxin(id) {
	
	var sx = "";
	if (id == 101) {
		sx = "Level"
	} else if (id == 102) {
		sx = "Job"
	} else if (id == 103) {
		sx = "Ap"
	} else if (id == 104) {
		sx = "Sp"
	} else if (id == 111) {
		sx = "Str"
	} else if (id == 112) {
		sx = "Dex"
	} else if (id == 113) {
		sx = "Int"
	} else if (id == 114) {
		sx = "Luk"
	} else if (id == 115) {
		sx = "MaxHp"
	} else if (id == 116) {
		sx = "MaxMp"
		
		
	
	} else {
		
	}
	return sx;
}


//修改属性值
function setchashuxin(id, sxlx, cssz) {
	var chasl = getPlayerxm(id);
	if (chasl == null) {
		cm.getPlayer().dropMessage(5, "信息：玩家已离线");
		return;
	}

	var MapleStat = Packages.client.MapleStat;
	var getStat = chasl.getStat();

	if ("MaxHp".equals(sxlx)) {//最大血量
		getStat.setMaxHp(cssz,chasl);
		chasl.updateSingleStat(MapleStat.MAXHP, cssz);
		getStat.setHp(cssz,chasl);
		chasl.updateSingleStat(MapleStat.HP, cssz);
	} else if ("Hp".equals(sxlx)) {
		getStat.setHp(cssz,chasl);
		chasl.updateSingleStat(MapleStat.HP, cssz);
	} else if ("MaxMp".equals(sxlx)) {//最大蓝
		getStat.setMaxMp(cssz,chasl);
		chasl.updateSingleStat(MapleStat.MAXMP, cssz);
		getStat.setMp(cssz,chasl);
		chasl.updateSingleStat(MapleStat.MP, cssz);
	} else if ("Mp".equals(sxlx)) {
		getStat.setMp(cssz,chasl);
		chasl.updateSingleStat(MapleStat.MP, cssz);
	} else if ("Str".equals(sxlx)) {//力量
		getStat.setStr(cssz,chasl);
		chasl.updateSingleStat(MapleStat.STR, cssz);
	} else if ("Dex".equals(sxlx)) {//敏捷
		getStat.setDex(cssz,chasl);
		chasl.updateSingleStat(MapleStat.DEX, cssz);
	} else if ("Int".equals(sxlx)) {//智力
		getStat.setInt(cssz,chasl);
		chasl.updateSingleStat(MapleStat.INT, cssz);
	} else if ("Luk".equals(sxlx)) {//运气
		getStat.setLuk(cssz,chasl);
		chasl.updateSingleStat(MapleStat.LUK, cssz);
	} else if ("Level".equals(sxlx)) {//等级
		chasl.setLevel(cssz);
		// chasl.levelup();
		// chasl.setLevel(cssz,chasl);
		chasl.updateSingleStat(MapleStat.LEVEL, cssz);
	} else if ("Job".equals(sxlx)) {//职业
		//chasl.setJob(cssz);
		chasl.updateSingleStat(MapleStat.JOB, cssz);
	} else if ("Ap".equals(sxlx)) {//能力值
		chasl.remainingAp = cssz;
		chasl.updateSingleStat(MapleStat.AVAILABLEAP, cssz);
	} else if ("Sp".equals(sxlx)) {//技能点
		chasl.resetSP(cssz);
		chasl.updateSingleStat(MapleStat.AVAILABLESP, cssz);
	} else {
		cm.getPlayer().dropMessage(5, "脚本错误:setjssx");
	}
	chasl.dropMessage(5, "GM为您修改属性：" + sxlx + "  参数：" + cssz);
}

