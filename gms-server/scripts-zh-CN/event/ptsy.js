var monsterIds = new Array(9410206,9300680,9300681,2400100,2400101,2400102,9410206,9410207,9410209,9410210,9410211,9410212,9410213,9410214,9410215);
var itemSet = new Array(1302142,1332114,1372071,1382093,1402085,1432075,1442104,1452100,1462085,1472111,1312056,1322084,1412055,1422057,1482073,1492073,2340000,2040915,1002677,1002798,1002743,1002939,1082149,1102042,1102041,1102086,1102084,1012050,1012071,1012072,1012073,1022060,1022067,1012056,1402063,1302058,1092022,1302106,1302080,1302087,1382015,1382016,1402044,1302024,1322051,1402014,1442039,1302019,1332021,1302128,1302084,1402013);

var count = 0; //波数,这里不要改
var maxCount = 15; //最大波数
var mobMaxCount = 30; //生成怪物数量
var exitMap; //退出地图
var fbMapId = 912010000; //副本地图ID
var countDown = 1000 * 60 * 30; //副本时间
var instanceId;
var eim;

function init() {
	instanceId = 1;
}

function setup() {
	instanceId = em.getChannelServer().getInstanceId();
	exitMap = em.getChannelServer().getMapFactory().getMap(910000000);
	var eim = em.newInstance("ptsy" + instanceId);
	var map = eim.setInstanceMap(fbMapId);
	em.getChannelServer().addInstanceId();	
	map.resetFully(true);
	map.setSpawns(false);
	map.killAllMonsters(true);
	eim.startEventTimer(countDown);
	createMonster(eim);
	return eim;
}

//玩家进场
function playerEntry(eim, player) {
	var map = eim.getMapInstance(fbMapId);
	player.changeMap(map, map.getPortal(0));
}

//时间到了
function scheduledTimeout(eim) {
	end(eim);
}

//所有怪物都死了
function allMonstersDead(eim) {
	var iter = eim.getPlayers().iterator();
	//如果达到了最大波数领取奖励
	if ((count + 1) == maxCount) {
		eim.broadcastPlayerMsg(5, "通关成功,领取奖励!3");
		while (iter.hasNext()) {
			var chr = iter.next();
			var rand = Math.floor(Math.random() * itemSet.length);
			//给点券
			chr.modifyCSPoints(1, 800, true);
			//gainItem
			chr.gainIten(4001126, 500);
			 chr.gainIten(itemSet[rand],1);
		}
		//结束副本
		end(eim);
		return;
	} else if ((count + 1) == 5) { //5波奖励
		while (iter.hasNext()) {
			var chr = iter.next();
			//给点券
			chr.modifyCSPoints(1, 500, true);
			//gainItem
			chr.gainIten(4251201, 1);
		}

	} else if ((count + 1) == 10) { //10波奖励
		while (iter.hasNext()) {
			var chr = iter.next();
			//给点券
			chr.modifyCSPoints(1, 500, true);
			//gainItem
			chr.gainIten(4310034, 1);
		}


	}
	count++;
	createMonster(eim);
}

//生成怪物
function createMonster(eim) {
	var monsterId = monsterIds[count];
	for (var i = 0; i <= mobMaxCount; i++) {
		var mob = em.getMonster(monsterId);
		//重新设置怪物属性
		var modified = em.newMonsterStats();
		//设置经验
		modified.setOExp(mob.getMobExp() * 1);
		//设置血量
		modified.setOHp(mob.getMobMaxHp() * 1);
		//设置蓝量
		modified.setOMp(mob.getMobMaxMp());
		//状态重新赋值给怪物
		mob.setOverrideStats(modified);
		//注册怪物
		eim.registerMonster(mob);
		var map = eim.getMapInstance(0);
		map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(22, 149));
	}
	this.eim = eim;
	eim.schedule("broadcastMsg", 1000);
}

//发送消息
function broadcastMsg() {
	eim.broadcastPlayerMsg(5, "当前第" + (count + 1) + "波怪物!");
}

//玩家断线
function playerDisconnected(eim, player) {
	eim.unregisterPlayer(player);
}

//怪物被杀死
function monsterValue(eim, mobId) {
	return 1;
}

//玩家退出
function playerExit(eim, player) {
	eim.unregisterPlayer(player);
	player.changeMap(exitMap, exitMap.getPortal(0));
}

//副本结束
function end(eim) {
	count = 0;
	var party = eim.getPlayers();
	for (var i = 0; i < party.size(); i++) {
		playerExit(eim, party.get(i));
	}
	eim.dispose();
}

//传送地图
function changedMap(eim, player, mapid) {
	if (mapid != fbMapId) {
		eim.unregisterPlayer(player);
	}
	if (eim.getPlayers().size() == 0) {
		end(eim);
	}
}

function removePlayer(eim, player) {
	eim.unregisterPlayer(player);
	player.getMap().removePlayer(player);
	player.setMap(exitMap);
}

function clearPQ(eim) {
	end(eim);
}

//玩家退出组队
function leftParty(eim, player) {
	eim.unregisterPlayer(player);
	player.changeMap(exitMap, exitMap.getPortal(0));
}

//解散组队
function disbandParty(eim) {
	end(eim);
}

//玩家死了
function playerDead(eim, player) {}

//玩家复活
function playerRevive(eim, player) {}

function cancelSchedule() {}
