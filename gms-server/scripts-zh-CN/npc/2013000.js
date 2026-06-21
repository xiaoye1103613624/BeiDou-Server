/* ==================
 脚本类型:  NPC
 脚本作者： 梦之岛
 联系方式： QQ338150
 =====================
 */

var status = 0;
//副本名称
var fbmc = "通天塔-(女神副本)";
var eventname = "OrbisPQ";//副本配置文件
var maxjinbi = 50000;//判断征集令金币
var minLevel = 50;
var maxLevel = 250;//等级设置
var cishuxianzhi = 10;//限制次数
var minPartySize = 1;
var maxPartySize = 6;//人数设置


function start() {
    status = -1;

    action(1, 0, 0);
}
function action(mode, type, selection) {
    if (mode == -1) {
        cm.dispose();
    }
    else {
        if (status >= 0 && mode == 0) {

            cm.sendOk("感谢你的光临！");
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
			if (cm.getMapId() == 920010000) {
		cm.sendOk("我们必须拯救他 需要10个云的碎片然后找左边的云朵进行对话!");
		cm.dispose();
		return;
	}
            var tex2 = "";
            var text = "";
            for (i = 0; i < 10; i++) {
                text += "";
            }
            text += "#k\t\t\t\t欢迎来到#r" + fbmc + "#k\r\n副本进入要求如下：\r\n①人数限制:#r " + minPartySize + " #b- #r" + maxPartySize + "#k队员\t②等级限制：#r " + minLevel + " #b- #r" + maxLevel + "级 #k\r\n"
			text += "#k每天只能挑战:#b"+ cishuxianzhi +"#k次 你今天已进入:#b"+ cm.getPlayer().getBossLog("天空女神副本") +"#k次#k\r\n"
            text += "#L1##r开始组队副本#l      \r\n\r\n"
			// text += "#L10##r兑换女神副本透明属性时装#l\r\n\r\n"#L2##r副本征集令#k" + maxjinbi + "金币/次#l
			//text += "#L3##r收集50个#v4002002#兑换#v1082232##z1082232#\r\n\r\n"
			//text += "#L4##r收集60个#v4002002#兑换#v1072534##z1072534##l\r\n\r\n"
			//text += "#L5##r升级#v1072153#需要2个#v4002002##b(最多升级10次)#l\r\n\r\n"
            cm.sendSimple(text);
        } else if (selection == 1) {
	for (var i = 4001044; i < 4001064; i++) {
		cm.removeAll(i);
	}
	if (cm.getParty() == null) { //判断又没有组队
	    cm.sendSimple("你貌似没有达到要求...:\r\n\r\n#r玩家成员要求: " + minPartySize + " , 每个人的等级必须在 " + minLevel + " 到 等级 " + maxLevel + ".");
		cm.dispose();
	} else if (!cm.isLeader()) { //判断队长
	    cm.sendSimple("如果你想做任务，请#b队长#k 跟我谈.");
		cm.dispose();
                } else if (!cm.getPartyBossLog("天空女神副本", 10)) { //判断组队是否2次
                    cm.sendOk("队伍中队友挑战次数已经用完10次！");
                    cm.dispose();
                    return;
	//} else if(cm.getPartyBosslog("天空女神副本",(cishuxianzhi)) == false) {//判断组队是否2次
	      //      cm.sendOk("队伍中队友挑战次数已经用完"+ cishuxianzhi +"次！");
            //    cm.dispose();
				//return;
	//}else if( cm.getPlayer().getBossLog("天空女神副本") >= cishuxianzhi) {
	            //cm.sendOk("您好,限定每天只能挑战"+ cishuxianzhi +"次！");
                //cm.dispose();
				//return;
	} else {
	    // Check if all 队员 are within PQ levels
	    var party = cm.getParty().getMembers();
	    var mapId = cm.getMapId();
	    var next = true;
	    var levelValid = 0;
	    var inMap = 0;
	    var it = party.iterator();

	    while (it.hasNext()) {
		var cPlayer = it.next();
		if ((cPlayer.getLevel() >= minLevel) && (cPlayer.getLevel() <= maxLevel)) {
		    levelValid += 1;
		} else {
		    next = false;
		}
		if (cPlayer.getMapid() == mapId) {
		    inMap += (cPlayer.getJobId() == 900 ? 6 : 1);
		}
	    }
	    if (party.size() > maxPartySize || inMap < minPartySize) {
		next = false;
	    }
	    if (next) {
		var em = cm.getEventManager("OrbisPQ");
		if (em == null) {
		    cm.sendSimple("找不到脚本请联络GM#b\r\n");
		} else {
		    var prop = em.getProperty("state");
		    if (prop.equals("0") || prop == null) {
			em.startInstance(cm.getParty(), cm.getMap());
			cm.givePartyBossLog("天空女神副本");
	        cm.spawnNpc(2013001);
	//cm.getPlayer().setBossLog("天空女神副本");//给团队次数
			//cm.给团队每日("天空女神副本");
			//cm.setPartyBosslog("天空女神副本");//给团队次数
			cm.dispose();
			return;
		    } else {
			cm.sendSimple("其他队伍已经在里面做 #r组队任务了#k 请尝试换频道或者等其他队伍完成。");
			cm.dispose();
		    }
		}
	    } else {
		cm.sendSimple("你的队伍貌似没有达到要求...:\r\n\r\n#r要求: " + minPartySize + " 玩家成员, 每个人的等级必须在 " + minLevel + " 到 等级 " + maxLevel + ".");
	    }
		cm.dispose();
	}
        } else if (selection == 2) {
		if (cm.getMeso() >= maxjinbi){//判断多少金币
        cm.gainMeso(- maxjinbi );//扣除多少金币
	    cm.全服黄色喇叭(cm.getPlayer().getName() + " [副本征集令]" + " : " + "[" + fbmc + "]需要勇士一起完成");
        cm.dispose();
        }else{
        cm.sendOk("你的冒险币不足" + maxjinbi + "。无法发送征集令");
        cm.dispose();
					}
        } else if (selection == 3) {
		if (cm.getInventory(1).isFull(0)){//判断第一个也就是装备栏的装备栏是否有一个空格
		cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法兑换.");
		cm.dispose();
		} else if(cm.haveItem(4002002 ,50)){
		cm.gainItem(4002002,-50);
        cm.给属性装备(1082232, 0, 0, 4, 4, 4, 4, 50, 50, 2, 4, 0, 0, 0, 0, 0, 0);
		cm.sendOk("恭喜你,成功的领取了#v1082232##z1082232#!");
        cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在女神副本中成功的兑换了女神手镯!");
	    status = -1;
		}else{
        cm.sendOk("你还没有收集到#v4002002#50个，请收集到后再来兑换！");
		cm.dispose();

					}
        } else if (selection == 4) {
		if (cm.getInventory(1).isFull(0)){//判断第一个也就是装备栏的装备栏是否有一个空格
		cm.sendOk("#b请保证装备栏位至少有1个空格,否则无法兑换.");
		cm.dispose();
		} else if(cm.haveItem(4002002 ,60)){
		cm.gainItem(4002002,-60);
		cm.gainItem(1072534,1);//物品代码,数量,随机属性
		cm.sendOk("恭喜你,成功的领取了#v1072534##z1072534#!");
        cm.worldMessage(6,"恭喜玩家：["+cm.getName()+"]在女神副本中成功的兑换了雅典娜女神的鞋子!");
	    status = -1;
		}else{
        cm.sendOk("你还没有集齐到#v4002002#60个，请收集到后再来兑换！");
		cm.dispose();

					}
		} else if (selection == 5) {
		cm.dispose();
        cm.openNpc(2013000, 1);
	}else if (selection == 10) {
		cm.dispose();
        cm.openNpc(2013000, "天空装备升级");
	}
    }
	}
