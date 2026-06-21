var status = 0;
var fbmc = "百草堂-(海盗副本)";//副本名称
var minLevel = 50;//最低等级
var maxLevel = 250;//最高等级
var minPartySize = 1;//最少人数
var maxPartySize = 6;//最多人数
var cishuxianzhi = 10;//限制次数
var maxjinbi = 50000;//判断征集令金币
var eventname = "Pirate";//副本配置文件


function checkMap() {
    var map = [925100000, 925100100, 925100200, 925100201, 925100202, 925100300, 925100301, 925100302, 925100400, 925100400, 925100500];
    for(var i = 0 ; i < map.length; i++) {
        if(cm.getPlayerCount(map[i]))
            return false;
    }
    return true;
}

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
            var tex2 = "";
            var text = "";
            for (i = 0; i < 15; i++) {
                text += "";
            }
		//显示物品ID图片用的代码是  #v这里写入ID#
            text += "#k\t\t\t\t欢迎来到#r" + fbmc + "#k\r\n副本进入要求如下：\r\n①人数限制:#r " + minPartySize + " #b- #r" + maxPartySize + "#k队员\t②等级限制：#r " + minLevel + " #b- #r" + maxLevel + "级 #k\r\n"
		text += "#k每天只能挑战:#b"+ cishuxianzhi +"#k次 你今天已进入:#b"+ cm.getPlayer().getBossLog("海盗副本") +"#k次#k\r\n"
            text += "#L1##r开始组队副本#l     \r\n\r\n"
		// text += "#L15##r兑换海盗副本透明属性时装#l\r\n\r\n"
		//text += "#L10##r收集150个#v4002002#兑换#v1022048##z1022048#全属性+5#l\r\n\r\n"
		// text += "#L11##r收集200个#v4002002#兑换#v1072356##z1072356##l\r\n\r\n"
		// text += "#L12##r收集200个#v4002002#兑换#v1072357##z1072357##l\r\n\r\n"
		// text += "#L13##r收集200个#v4002002#兑换#v1072358##z1072358##l\r\n\r\n"
		// text += "#L14##r收集200个#v4002002#兑换#v1072359##z1072359##l\r\n\r\n"
            cm.sendSimple(text);
} else if (selection == 1) {
cm.removeAll(4001117);
cm.removeAll(4031437);
cm.removeAll(4001120);
cm.removeAll(4001121);
cm.removeAll(4001260);
cm.removeAll(4001122);
    if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
        cm.sendOk("请找队长来找我。");
	cm.dispose();
                } else if (!cm.getPartyBossLog("海盗副本", 10)) { //判断组队是否2次
                    cm.sendOk("队伍中队友挑战次数已经用完10次！");
                    cm.dispose();
                    return;
		//}else if( cm.getPlayer().getBossLog("海盗副本") > cishuxianzhi) {
            //cm.sendOk("您好,限定每天只能挑战"+ cishuxianzhi +"次！");
                //cm.dispose();
			//return;
            } else {
        var party = cm.getPlayer().getParty().getMembers();
        var mapId = cm.getPlayer().getMapId();
        var next = true;
        var size = 0;
        var it = party.iterator();
	var party = cm.getParty().getMembers();
        while (it.hasNext()) {
		var cPlayer = it.next();
		var ccPlayer = cm.getPlayer().getMap().getCharacterById(cPlayer.getId());
		if (ccPlayer == null || ccPlayer.getLevel() < minLevel || ccPlayer.getLevel() > maxLevel) {
			next = false;
			break;
		}
	}
        if (party.size() >= minPartySize && next) {
            if(checkMap()) {
                var em = cm.getEventManager("Pirate");
                if (em == null) {
                    cm.sendOk("找不到脚本，请联系GM！！");
			cm.dispose();
                } else {
                    em.startInstance(cm.getPlayer().getParty(), cm.getPlayer().getMap());
			//cm.getPlayer().setBossLog("海盗副本");//给团队次数
			//cm.给团队每日("海盗副本");
			//cm.setPartyBosslog("海盗副本");//给团队次数
			cm.givePartyBossLog("海盗副本");
			cm.dispose();
                }
            } else {
                cm.sendOk("目前有人在打啰～");
		cm.dispose();
            }
        }else {
            cm.sendOk("需要" + minPartySize + "至" + maxPartySize + "个人 等级必须是" + minLevel+ "到" + maxLevel + "级");
		cm.dispose();
        }
    }

} else if (selection == 2) {
            if (cm.getMeso() >= maxjinbi){//判断多少金币
                cm.gainMeso(- maxjinbi );//扣除多少金币
		cm.全服黄色喇叭(cm.getPlayer().getName() + " [副本征集令]" + " : " + "[" + fbmc + "]需要勇士一起完成,我已在副本门口!");
                cm.dispose();
                }else{
                    cm.sendOk("你的冒险币不足" + maxjinbi + "。无法发送征集令");
                    cm.dispose();
    }
} else if (selection == 3) {
	cm.dispose();
	cm.openNpc(2094000, 1);
} else if (selection == 10) {
	        if (cm.haveItem(4002002,150)) {
	           cm.gainItem(4002002,-150);
                   cm.给属性装备(1022048, 0, 0, 5, 5, 5, 5, 0, 0, 5, 5, 0, 0, 0, 0, 0, 0);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002002#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
} else if (selection == 11) {
	        if (cm.haveItem(4002002,120)) {
	           cm.gainItem(4002002,-120);
                   cm.给属性装备(1072356, 0, 0, 5, 5, 5, 5, 0, 0, 5, 5, 0, 0, 0, 0, 0, 0);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002002#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
} else if (selection == 12) {
	        if (cm.haveItem(4002002,200)) {
	           cm.gainItem(4002002,-200);
	           cm.gainItem(1072357,1);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002002#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
} else if (selection == 13) {
	        if (cm.haveItem(4002002,200)) {
	           cm.gainItem(4002002,-200);
	           cm.gainItem(1072358,1);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002002#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
} else if (selection == 14) {
	        if (cm.haveItem(4002002,200)) {
	           cm.gainItem(4002002,-200);
	           cm.gainItem(1072359,1);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002002#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
}else if (selection == 15) {
	cm.dispose();
	cm.openNpc(2094000,"海盗装备升级");
}
}
