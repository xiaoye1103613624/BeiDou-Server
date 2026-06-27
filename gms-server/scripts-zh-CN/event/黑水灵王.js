
function init() {
	//em.setProperty("state", "0");
}

var Breakmap;
var Fieldmap;
var moblist;
var 入场人数;
var 增加血量;
var awards;
var 触发奖项怪物;

function setup(eim) {
	var Events = parseInt(em.getProperty("Event"));
	var Time = parseInt(em.getProperty("Time"));
	Breakmap = parseInt(em.getProperty("Breakmap"));
	Fieldmap = parseInt(em.getProperty("Fieldmap"));
	增加血量 = parseInt(em.getProperty("增加血量"));
	触发奖项怪物 = parseInt(em.getProperty("触发奖项怪物"));
	eim = em.newInstance(Events);
	if (parseInt(em.getProperty("状态")) == 0) {
		//em.setProperty("state", "2");
		em.setProperty("状态", "1");//
		eim.schedule("monsterParty", 2000);//
	}
	em.setProperty("Eims", eim);
	eim.setInstanceMap(Fieldmap);
	eim.startEventTimer(1000 * Time); //
	return eim;
}
var 线程;

function 统计伤害线程(eim) {
	线程 = em.schedule("统计伤害", 1 *10* 1000, eim);
}


function 统计伤害(eim) {
	var map = eim.getMapInstance(0);
	if (eim != null && map != null) {
		map.showDamageRank(eim.getDamageTaken(), eim.getTimeLeft() / 1000);
		统计伤害线程(eim);
	}
}

function scheduledTimeout(eim) {
	end(eim);
}

function end(eim) {
	//em.setProperty("state", "0");
	eim.disposeIfPlayerBelow(100, Breakmap);
}

function changedMap(eim, player, mapid) {
	switch (mapid) {
		case Fieldmap:
			return;
	}
	eim.unregisterPlayer(player);
	if (eim.disposeIfPlayerBelow(0, 0)) {
		end(eim);
	}
}

function playerEntry(eim, player) {
	var map = em.getMapFactory().getMap(Fieldmap);
	player.changeMap(map, map.getPortal(0));
}

function monsterValue(eim, mobId) {
    if (mobId == 触发奖项怪物) {
        publicAwards(eim);
        return 1;
    }
}

function Returnmap() {
	var map = em.getMapFactory().getMap(Fieldmap);
	var list = map.getCharactersThreadsafe();
	for (var i = 0; i < list.length; i++) {
		var chr = list[i];
		var returnmap = em.getMapFactory().getMap(Breakmap);
		chr.changeMap(returnmap, returnmap.getPortal(0));
	}
}

function openNpc(chr, id, script) {
	Packages.scripting.NPCScriptManager.getInstance().dispose(chr.getClient());
	Packages.scripting.NPCScriptManager.getInstance().start(chr.getClient(), id, script);
}

//漂浮喇叭
function 漂浮喇叭(msg, itemId) {
	var xmcserv = Packages.handling.channel.ChannelServer.getAllInstances().iterator();
	while (xmcserv.hasNext()) {
		var xmfwq = xmcserv.next();//服务器频道
		var cserv1 = xmfwq.getPlayerStorage().getAllCharacters().iterator();
		while (cserv1.hasNext()) {
			var mch = cserv1.next();//玩家
			if (mch.getMapId() == Fieldmap) {
				mch.startMapEffect(msg, itemId);
			}
		}
	}
}

function allMonstersDead(eim) {

}

function publicAwards(eim) {//公共奖项
	awards = eval(em.getProperty("公共奖项"));
	var map = em.getMapFactory().getMap(Fieldmap);
	var list = map.getCharactersThreadsafe();
	if (map.getCharactersSize() >= 1 && list != null) {
		for (var i = 0; i < list.length; i++) {
			var chr = list[i];
			openNpc(chr, 9900004, "远征队奖励");

		}
	}
}

function monsterParty(eim) {
	moblist = eval(em.getProperty("怪物列表"));
	入场人数 = parseInt(em.getProperty("入场人数"));
	for (var i = 0; i < moblist.length; i++) {
		for (var j = 0; j < moblist[i].数量; j++) {
			var map = eim.setInstanceMap(Fieldmap);
			var mob = em.getMonster(moblist[i].代码);
			var modified = em.newMonsterStats();
			modified.setOHp(moblist[i].血量 + ((moblist[i].血量 * (0.01 * 增加血量)) * (入场人数 - 1)));
			modified.setOMp(mob.getMobMaxMp());
			modified.setOExp(mob.getMobExp() + ((mob.getMobExp() * (0.01 * 增加血量)) * (入场人数 - 1)));
			mob.setOverrideStats(modified);
			eim.registerMonster(mob);
			统计伤害线程(eim);
			var x轴 = Math.floor(Math.random() * (moblist[i].x轴[1] + 1 - moblist[i].x轴[0]) + moblist[i].x轴[0]);
			map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(x轴, moblist[i].y轴));
		}
	}
}

function playerRevive(eim, player) { }

function playerDisconnected(eim, player) {
}

function leftParty(eim, player) {
}

function disbandParty(eim) {
}

function playerExit(eim, player) {
	eim.unregisterPlayer(player);
}

function removePlayer(eim, player) {
}

function clearPQ(eim) {
}

function finish(eim) {
}

function timeOut(eim) {
}

function cancelSchedule() { }

function playerDead(eim, player) {
}

