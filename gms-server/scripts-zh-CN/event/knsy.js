var eventmapid = 912010000;
var returnmap = 910000000;
var monster = new Array(8610010,8610011,8610012,8610013,8610014,8610024,8610005,8610006,8610007,9700004,8610009,8600003,8600004,8600005,9800180,8610010,8610011,8610012,8610013,8610014,8610024,8610005,8610006,8610007,8610008,8610009,8600003,8600004,8600005,8610024,8610010,8610011,8610012,8610013,8610014,8610024,8610005,8610006,8610007,9700008,8610009,8600003,8600004,8600005,9800180,8610009,8600003,8600004,8610024,8610005,8610006);
var zuidacishu = 50;
var countDown = 1000 * 60 * 60;

function init() {
// After loading, ChannelServer
}

function setup() {
    var instanceName = "BossQuest123";

    var eim = em.newInstance(instanceName);
    // If there are more than 1 map for this, you'll need to do mapid + instancename
    var map = eim.getInstanceMap(eventmapid);
    map.toggleDrops();
    //map.spawnNpc(9250156, new java.awt.Point(-364, 220));

    eim.setProperty("points", 0);
    eim.setProperty("monster_number", 0);
    eim.startEventTimer(countDown);
    beginQuest(eim);
    return eim;
}

function beginQuest(eim) { // Custom function
    if (eim != null) {
    	eim.startEventTimer(5000); // After 5 seconds -> scheduledTimeout()
    }
}
function monsterSpawn(eim) { // Custom function
    var monsterid = monster[parseInt(eim.getProperty("monster_number"))];
    var mob = em.getMonster(monsterid);
    var mob1 = em.getMonster(monsterid);
    var mob2 = em.getMonster(monsterid);
    var mob3 = em.getMonster(monsterid);

   switch (monsterid) {
        case 8610010:
        case 8610011:
        case 8610012:
        case 8610013:
        case 8610014:
        case 8610024:
        case 8610005:
        case 8610006:
        case 8610007:
        case 9700004:
        case 8610009:
        case 8600003:
        case 8600004:
        case 8600005:
        case 9800180:
        case 8610010:
        case 8610011:
        case 8610012:
        case 8610013:
        case 8610014:
        case 8610024:
        case 8610005:
        case 8610006:
        case 8610007:
        case 8610008:
        case 8610009:
        case 8600003:
        case 8600004:
        case 8600005:
        case 8610024:
        case 8610010:
        case 8610011:
        case 8610012:
        case 8610013:
        case 8610014:
        case 8610024:
        case 8610005:
        case 8610006:
        case 8610007:
        case 9700008:
        case 8610009:
        case 8600003:
        case 8600004:
        case 8600005:
        case 9800180:
        case 8610009:
        case 8600003:
        case 8600004:
        case 8610024:
        case 8610005:
        case 8610006:
	    var modified = em.newMonsterStats();
	    modified.setOExp(mob.getMobExp()*1);
	    modified.setOHp(mob.getMobMaxHp() *50);
	    modified.setOMp(mob.getMobMaxMp());
	    mob.setOverrideStats(modified);
	    break;
	case 8300006: // Dragonoir
	    var modified = em.newMonsterStats();
	    modified.setOExp(mob.getMobExp() * 0.5);
	    modified.setOHp(mob.getMobMaxHp() * 0.7);
	    modified.setOMp(mob.getMobMaxMp());

	    mob.setOverrideStats(modified);
	    break;
	case 9400121: // Anego
	    var modified = em.newMonsterStats();
	    modified.setOExp(mob.getMobExp());
	    modified.setOHp(mob.getMobMaxHp() * 1.8);
	    modified.setOMp(mob.getMobMaxMp());

	    mob.setOverrideStats(modified);
	    break;
	case 9400405: // Samurai
	    var modified = em.newMonsterStats();
	    modified.setOExp(mob.getMobExp() * 1);
	    modified.setOHp(mob.getMobMaxHp() * 2.6);
	    modified.setOMp(mob.getMobMaxMp());

	    mob.setOverrideStats(modified);
	    break;
	case 9420549: // Scarlion
	case 9420544: // Targa
	    var modified = em.newMonsterStats();
	    modified.setOExp(mob.getMobExp() * 0.8);
	    modified.setOHp(mob.getMobMaxHp() * 1.8);
	    modified.setOMp(mob.getMobMaxMp());

	    mob.setOverrideStats(modified);
	    break;
	case 8800002: // Zakum 3
	    var modified = em.newMonsterStats();
	    modified.setOExp(mob.getMobExp() * 1);
	    modified.setOHp(mob.getMobMaxHp() * 2.6);
	    modified.setOMp(mob.getMobMaxMp());

	    mob.setOverrideStats(modified);
	    break;
    }
    eim.registerMonster(mob);
    eim.registerMonster(mob1);
    eim.registerMonster(mob2);
    eim.registerMonster(mob3);

    var map = eim.getMapInstance(0);
  map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(22, 149));
  map.spawnMonsterOnGroundBelow(mob1, new java.awt.Point(22, 149));
  map.spawnMonsterOnGroundBelow(mob2, new java.awt.Point(22, 149));
  map.spawnMonsterOnGroundBelow(mob3, new java.awt.Point(22, 149));
}

function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
}

function changedMap(eim, player, mapid) {
    if (mapid != eventmapid) {
	eim.unregisterPlayer(player);

	eim.disposeIfPlayerBelow(0, 0);
    }
}

function scheduledTimeout(eim) {
    var num = parseInt(eim.getProperty("monster_number"));
    if (num < monster.length) {
	monsterSpawn(eim);
	eim.setProperty("monster_number", num + 1);
    } else {
	eim.disposeIfPlayerBelow(100, returnmap);
    }
}

function allMonstersDead(eim) {
    eim.restartEventTimer(3000);
    var mobnum = parseInt(eim.getProperty("monster_number"));
    var num = mobnum; // Total 19250
    var totalp = parseInt(eim.getProperty("points")) +1;
	var iter = em.getInstances().iterator();
    var chr = iter.next();
    eim.setProperty("points", totalp);
    eim.saveBossQuest(num);
  eim.broadcastPlayerMsg(6, "当前层数 " + totalp + "层， 下一波的BOSS即将来临。请做好对抗的准备");


 if (totalp == zuidacishu) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i)
			cccyy.modifyCSPoints(1, 100000, true);
			//gainItem
			cccyy.gainItem(2614000, 1);
			cccyy.gainItem(2460005, 2);
			cccyy.gainItem(3994731, 5);
            cccyy.dropMessage(5, "恭喜您获得一万破攻1个，超级正向混沌券2个，一亿金币5个，点券 100000");
			// chr.gainIten(itemSet[rand],1);

}
			//eim.broadcastPlayerMsg(5, "");
				return;
    } else if (totalp == 5) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i)
			cccyy.modifyCSPoints(1, 100000, true);
			//gainItem
			cccyy.gainItem(2614000, 5);
			cccyy.gainItem(3994731, 2);
            cccyy.dropMessage(5, "恭喜您获得一万破攻5个，一亿金币2个，点券 100000");
}
			//eim.broadcastPlayerMsg(5, "");
		//return;//return;
} else if (totalp == 10) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i);
			cccyy.modifyCSPoints(1, 50000, true);
			//gainItem
			cccyy.gainItem(2614000, 3);
			cccyy.gainItem(3994731, 1);
	        cccyy.dropMessage(5, "恭喜您获得一万破攻3个，一亿金币1个 点券 50000");
}
		
} else if (totalp == 15) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i);
			cccyy.modifyCSPoints(1, 50000, true);
			//gainItem
			cccyy.gainItem(2614000, 3);
			cccyy.gainItem(3994731, 1);
          cccyy.dropMessage(5, "恭喜您获得一万破攻3个，一亿金币1个 点券 50000");
}
 // cccyy.getPlayer().dropMessage(5, "恭喜您获得终极挑战奖励宝箱一个");
		//return;

} else if (totalp == 20) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i);
			//gainItem
			cccyy.gainItem(2614000, 3);
			cccyy.gainItem(3994731, 1);
cccyy.dropMessage(5, "恭喜您获得一万破攻3个，一亿金币1个");
}

} else if (totalp == 25) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i);
			//gainItem
			cccyy.gainItem(2614000, 3);
			cccyy.gainItem(3994731, 1);
cccyy.dropMessage(5, "恭喜您获得一万破攻3个，一亿金币1个");
}

} else if (totalp == 30) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i);
		cccyy.modifyCSPoints(1, 50000, true);
			//gainItem
}
	     cccyy.gainItem(2614000, 3);
	     cccyy.gainItem(3994731, 1);
cccyy.dropMessage(5, "恭喜您获得一万破攻3个，一亿金币1个 点券 50000");

} else if (totalp == 35) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i);
			cccyy.modifyCSPoints(1, 50000, true);
			//gainItem
			cccyy.gainItem(2614000, 3);
			cccyy.gainItem(3994731, 1);
cccyy.dropMessage(5, "恭喜您获得一万破攻3个，一亿金币1个 点券 50000");
}
	//		cccyy.getPlayer().dropMessage(5, "恭喜您获得一万破攻三个，一亿金币1个");
} else if (totalp == 40) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i);
			cccyy.modifyCSPoints(1, 50000, true);
			//gainItem
			cccyy.gainItem(2614000, 3);
			cccyy.gainItem(3994731, 1);
cccyy.dropMessage(5, "恭喜您获得一万破攻3个，一亿金币1个 点券 50000");
}
		//	cccyy.getPlayer().dropMessage(5, "恭喜您获得一万破攻三个，一亿金币1个");
} else if (totalp == 45) {
for(var i =0;i<eim.getPlayers().size();i++) {
	var cccyy = eim.getPlayers().get(i);
			cccyy.modifyCSPoints(1, 50000, true);
			//gainItem
			cccyy.gainItem(2614000, 3);
			cccyy.gainItem(3994731, 1);
cccyy.dropMessage(5, "恭喜您获得一万破攻3个，一亿金币1个 点券 50000");
}
		//	cccyy.getPlayer().dropMessage(5, "恭喜您获得一万破攻三个，一亿金币1个");
}
}
function playerDead(eim, player) {
// Happens when player dies
}

function playerRevive(eim, player) {
    return true;
// Happens when player's revived.
// @Param : returns true/false
}

function playerDisconnected(eim, player) {
    return 0;
// return 0 - Deregister player normally + Dispose instance if there are zero player left
// return x that is > 0 - Deregister player normally + Dispose instance if there x player or below
// return x that is < 0 - Deregister player normally + Dispose instance if there x player or below, if it's leader = boot all
}

function monsterValue(eim, mobid) {
    return 0;
// Invoked when a monster that's registered has been killed
// return x amount for this player - "Saved Points"
}

function leftParty(eim, player) {
    // Happens when a player left the party
    eim.unregisterPlayer(player);

    var map = em.getMapFactory().getMap(returnmap);
    player.changeMap(map, map.getPortal(0));

    eim.disposeIfPlayerBelow(0, 0);
}

function disbandParty(eim, player) {
    // Boot whole party and end
    eim.disposeIfPlayerBelow(100, returnmap);
}

function clearPQ(eim) {
// Happens when the function EventInstanceManager.finishPQ() is invoked by NPC/Reactor script
}

function removePlayer(eim, player) {
    eim.dispose();
// Happens when the funtion NPCConversationalManager.removePlayerFromInstance() is invoked
}

function registerCarnivalParty(eim, carnivalparty) {
// Happens when carnival PQ is started. - Unused for now.
}

function onMapLoad(eim, player) {
// Happens when player change map - Unused for now.
}

function cancelSchedule() {
}