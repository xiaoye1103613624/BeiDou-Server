/* ==================
 脚本类型:  副本
 脚本作者： 开心
 联系方式： qq870074996
 =====================
 */

var status = -1;
var fbmc = "毒雾森林-(毒物副本)";//副本名称
var minLevel = 50;
var maxLevel = 250;
var minPartySize = 1;
var maxPartySize = 6;
var cishuxianzhi = 500;//限制次数
var maxjinbi = 50000;//判断征集令金币
var eventname = "Ellin";//副本配置文件

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (mode == 1) {
	status++;
    } else {
	if (status == 0) {
	    cm.dispose();
	}
	status--;
    }
    if (status == 0) {
	    cm.givePartyItems(4001161, 0, true);
	    cm.givePartyItems(4001162, 0, true);
	    cm.givePartyItems(4001163, 0, true);
	    cm.givePartyItems(4001169, 0, true);
	    cm.givePartyItems(2270004, 0, true);
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            text += "#k\t\t\t\t欢迎来到#r" + fbmc + "#k\r\n副本进入要求如下：\r\n①人数限制:#r " + minPartySize + " #b- #r" + maxPartySize + "#k队员\t②等级限制：#r " + minLevel + " #b- #r" + maxLevel + "级 #k\r\n"
		text += "#k每天只能挑战:#b"+ cishuxianzhi +"#k次 你今天已进入:#b"+ cm.getPlayer().getBossLog("毒物森林") +"#k次#k\r\n"
            text += "#L1##r开始组队副本#l      \r\n\r\n"//#L2##r副本征集令#k" + maxjinbi + "金币/次#l
		// text += "#L10##r兑换毒物副本透明属性时装#l\r\n\r\n"
		// text += "#L3##k收集30个#v4170009#兑换 #v1113164#[#r四维+3 攻魔+2#k]#l\r\n\r\n"
		// text += "#L3##r#v4002001#200张 兑换#b #v1052155##z1052155##l\r\n\r\n"
		// text += "#L11##r#v4002001#200张 兑换#b #v1052156##z1052156##l\r\n\r\n"
		// text += "#L12##r#v4002001#200张 兑换#b #v1052157##z1052157##l\r\n\r\n"
		// text += "#L13##r#v4002001#200张 兑换#b #v1052158##z1052158##l\r\n\r\n"
		// text += "#L14##r#v4002001#200张 兑换#b #v1052159##z1052159##l\r\n\r\n"
		// text += "#L4##r#v4002001#15张 兑换 #b随机一张(卷轴成功率为100%攻魔卷+2)\r\n\r\n";
		// text += "            #v2040359##v2040360##v2040361##v2040362##v2040363##v2040364##l\r\n";
    cm.sendSimple(text);
    } else if (status == 1) {
        if (selection == 1) {
	    if (cm.getPlayer().getParty() == null || !cm.isLeader()) {
		cm.sendOk("找您的队长来和我谈话。");
		cm.dispose();
                } else if (!cm.getPartyBossLog("毒物森林", 500)) { //判断组队是否2次
                    cm.sendOk("队伍中队友挑战次数已经用完500次！");
                    cm.dispose();
                    return;
	   // }else if(cm.getPlayer().getBossLog("毒物森林") >= cishuxianzhi) {
	          //  cm.sendOk("您好,限定每天只能挑战"+ cishuxianzhi +"次！");
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
			var em = cm.getEventManager("Ellin");
			if (em == null) {
				cm.sendOk("当前副本有问题,请联络管理员.");
				cm.dispose();
			} else {
				var prop = em.getProperty("state");
                if (prop.equals("0") || prop == null) {
				em.startInstance(cm.getParty(), cm.getMap());
				//cm.getPlayer().setBossLog("毒物森林");//给团队次数
				//cm.给团队每日("毒物森林");
				//cm.setPartyBosslog("毒物森林");//给团队次数
				cm.givePartyBossLog("毒物森林");
				cm.dispose();
				return;
			} else {
				cm.sendOk("里面已经有人了,请你稍后在进入看看,或者更换频道");
				cm.dispose();
			}

			}
		} else {
			cm.sendOk("你的队伍#b成员#k需要#b" +minPartySize+ "人#k以上等级" + minLevel + "~" + maxLevel + "的队员才能进入!");
			cm.dispose();
		}
	    }
	} else if (selection == 2){
            if (cm.getMeso() >= maxjinbi){//判断多少金币
                cm.gainMeso(- maxjinbi );//扣除多少金币
		cm.全服黄色喇叭(cm.getPlayer().getName() + " [副本征集令]" + " : " + "[" + fbmc + "]需要勇士一起完成,我已在副本门口");
                cm.dispose();
                }else{
                    cm.sendOk("你的冒险币不足" + maxjinbi + "。无法发送征集令");
                    cm.dispose();
	}
		} else if (selection == 4) {
			   cm.dispose();
        	   cm.openNpc(2133000, 1);
		} else if (selection == 3) {
	        if (cm.haveItem(4002001,200)) {
	           cm.gainItem(4002001,-200);
	           cm.gainItem(1052155,1);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002001#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
		} else if (selection == 11) {
	        if (cm.haveItem(4002001,200)) {
	           cm.gainItem(4002001,-200);
	           cm.gainItem(1052156,1);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002001#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
		} else if (selection == 12) {
	        if (cm.haveItem(4002001,200)) {
	           cm.gainItem(4002001,-200);
	           cm.gainItem(1052157,1);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002001#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
		} else if (selection == 13) {
	        if (cm.haveItem(4002001,200)) {
	           cm.gainItem(4002001,-200);
	           cm.gainItem(1052158,1);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002001#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
		} else if (selection == 14) {
	        if (cm.haveItem(4002001,200)) {
	           cm.gainItem(4002001,-200);
	           cm.gainItem(1052159,1);
               //cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在毒物森林副本兑换了彩虹耳环!大家快恭喜他!");
	           cm.dispose();
			 }   else  {
			   cm.sendNext("你还没有收集到#v4002001#200个，请收集到后再来兑换！");
			   cm.dispose();
	}
	}else if (selection == 10) {
		cm.dispose();
		cm.openNpc(2133000,"毒物装备升级");
	}

    }

}
