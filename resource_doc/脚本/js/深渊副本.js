var 挑战中心 = "#fEffect/CharacterEff1.img/QQ1408745/0/10#";
var dd = " ";
var 粉心 = "#fEffect/CharacterEff/1112903/0/0#";
var 群粉心 = ""+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+粉心+"\r\n";
var status = -1;
var minLevel = 120; // 35
var maxLevel = 250; // 65

var minPartySize = 1;
var maxPartySize = 1;

function action(mode, type, selection) {
	if (mode == 1) {
		status++;
	} else {
		if (status == 0) {
			cm.dispose();
			return;
		}
		status--;
	}

	if (status == 0) {
		if (cm.getMapId() == 105200311) {//深渊地图
			cm.warp(910000000);
		}
		if (cm.getMeso() < 200000000){//判断多少金币
			cm.sendOk("#e#r挑战需要2亿金币才能为你开启传送大门！\r\n\r\n#e#r每人每日可以进入1次,当前进入次数"+cm.getPlayer().getBossLog('wuxianhuoli')+"");
			cm.dispose();
			return;
		}
		if (cm.getPlayerCount(105200311) > 0){
			cm.sendOk("有人正在挑战你无法进入！");
			cm.dispose();
			return;
		}
		if(cm.getLevel() < 120){//判断等级
			cm.sendOk("请达到120级再进进去吧！");
			cm.dispose();
			return;
		}

		if (cm.maxPartySize > 1) { // 需要组队
			cm.sendOk("只能一个人进入挑战，不能请人帮忙哦！");
			cm.dispose();
			return;
		}

		if(cm.getPlayer().getBossLog('wuxianhuoli') > 99){
			cm.sendOk("#e#r每人每日可以进入1次,当前进入 "+cm.getPlayer().getBossLog('wuxianhuoli')+" 次");
			cm.dispose();
			return;
		}
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
                                        cm.getC().getChannelServer().getMapFactory().getMap(105200311).resetReactors();
					em.startInstance(cm.getParty(), cm.getMap());
					cm.gainMeso(- 200000000);//扣除多少金币
					cm.givePartyBossLog('wuxianhuoli');
					//   cm.spawnMobOnMap(9600009, 1,260,248, 105200311);
					cm.喇叭(3,"[" + cm.getName() + "] 开始挑战每日深渊副本,击杀每层BOSS都会获取超多经验和奖励");
					cm.dispose();
					return;
				} else {
					cm.sendSimple("另一方已进入 #r恐惧深渊副本#k 请等待他们的任务完成.#b#");
					cm.dispose();
				}
			}

		}else {
			cm.sendSimple("申请进入失败。请遵守以下规定:\r\n\r\n#r要求: " + minPartySize + " 队员, 所有级别 " + minLevel + " ~ " + maxLevel + ".#b#l");
			cm.dispose();
		}
	}
}
