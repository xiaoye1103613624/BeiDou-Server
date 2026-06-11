var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var minLevel = 120; // 35
var maxLevel = 255; // 65

var minPartySize = 1;
var maxPartySize = 1;
var status = 0;
var cost;

function start() {
    status = -1;
    action(1, 0, 0);
}

function action(mode, type, selection) {
    if (status >= 1 && mode == 0){
	cm.sendNext("不打就别点我");
	cm.dispose();
	return;	
    }
    if (mode == 1)
	status++;
    else
	status--;
    if (status == 0) {
	cm.sendNext(""+dd+"\r\n\t\t\t"+挑战中心+"\r\n"+群粉心+"\t\t     #r#e“劳资蜀道山”无尽深渊副本#n\r\n副本介绍:\r\n\r\n#d传说川渝古地有一座神山(劳资蜀道山)\r\n山中有一个喜怒无常的女皇\r\n她时而开心、时而发怒、时而流泪、时而温柔\r\n战胜她后你会获得一天的开心生活和丰富的奖励\r\n\r\n\r\n#e#b每人每日可挑战1次，您今天已挑战 #r"+cm.getPlayer().getBossLog('wuxianhuoli')+" #b次");
    } else if (status == 1) {
	var job = cm.getJob();
	if (job == 0 || job == 2000 || job == 1000) {
	    cm.sendYesNo("你是否愿意花费2亿金币去挑战呢??");
	    cost = 1000;
	} else {
	    cm.sendYesNo("你是否愿意花费2亿金币去挑战呢?");
	    cost = 10000;
	}
    } else if (status == 2) {
	if (!cm.isLeader()) { // 不是队长
                    cm.sendOk("请叫队长和我谈话。");
                    cm.dispose();
		}
		if (cm.getMeso() < 200000000){//判断多少金币
			cm.sendOk("#e#r没有2E金币无法为你开启传送大门！\r\n\r\n#e#r每人每日可以进入1次,当前进入次数"+cm.getPlayer().getBossLog('wuxianhuoli')+"");
			cm.dispose();
			return;
		}
		
		if(cm.getPlayer().getBossLog('wuxianhuoli') >= 10){
			cm.sendOk("#e#r每人每日可以进入1次,当前进入 "+cm.getPlayer().getBossLog('wuxianhuoli')+" 次");
			cm.dispose();
			return;
		}
//		var party = cm.getParty().getMembers();
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
		if (inMap > maxPartySize || inMap < minPartySize) {
			next = false;
		}
		if (next) {

			var em = cm.getEventManager("knsy");
			if (em == null) {
				cm.sendSimple("PQ遇到了一个错误。请联系GM，与截图.#b#l");
			} else {
				var prop = em.getProperty("state");
				if (prop == null || prop.equals("0")) {
					for (var i = 4001095; i < 4001099; i++) {
						cm.givePartyItems(i, 0, true);
					}
					for (var i = 4001100; i < 4001101; i++) {
						cm.givePartyItems(i, 0, true);
					}
                                        cm.getC().getChannelServer().getMapFactory().getMap(912010000).resetReactors();
					em.startInstance(cm.getParty(), cm.getMap());
					cm.gainMeso(- 200000000);//扣除多少金币
					cm.givePartyBossLog('wuxianhuoli');
					//   cm.spawnMobOnMap(9600009, 1,260,248, 912010000);
					cm.喇叭(3,"[" + cm.getName() + "]进入了无尽深渊副本,击杀怪物获取强化材料奖励！");
					cm.dispose();
					return;
				} else {
					cm.sendSimple("另一方已进入 #r无尽深渊副本#k 请等待他们的任务完成.#b#");
					cm.dispose();
				}
			}

		}else {
			cm.sendSimple("申请进入失败。请遵守以下规定:\r\n\r\n#r要求: " + minPartySize + " 队员, 所有级别 " + minLevel + " ~ " + maxLevel + ".#b#l");
			cm.dispose();
		}
}
}