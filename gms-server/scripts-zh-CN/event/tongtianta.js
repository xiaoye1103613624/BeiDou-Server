var fuben_mapId = 253000008;//市场21洞

var monstersArray = [// [怪物ID,怪物血量,坐标放置怪物数量,时间]
]
var points = [//怪物刷新坐标,925020001,1,-365
];

var 血量系数 = 0.5

function initDatasForTa(){
		
	var _ta = parseInt(em.getProperty("Number_CS"));
	
	if(_ta == 0){monstersArray = [
		[9410193,50000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 1){monstersArray = [
		[9410193,60000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 2){monstersArray = [
		[9410193,70000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 3){monstersArray = [
		[9410199,80000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 4){monstersArray = [
		[9410199,90000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 5){monstersArray = [
		[9410199,100000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 6){monstersArray = [
		[9410200,110000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 7){monstersArray = [
	    [9410200,120000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 8){monstersArray = [
		[9410200,130000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 9){monstersArray = [
		[9410206,140000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 10){monstersArray = [
		[9410206,150000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 11){monstersArray = [
		[9410206,160000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 12){monstersArray = [
		[9410207,170000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 13){monstersArray = [
		[9410207,180000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 14){monstersArray = [
		[9410207,190000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 15){monstersArray = [
		[9410208,200000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 16){monstersArray = [
		[9410208,210000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 17){monstersArray = [
		[9410208,220000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 18){monstersArray = [
		[9410209,230000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 19){monstersArray = [
		[9410209,240000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 20){monstersArray = [
	    [9410209,250000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 21){monstersArray = [
		[9410211,260000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 22){monstersArray = [
		[9410211,270000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 23){monstersArray = [
		[9410211,280000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 24){monstersArray = [
		[9410212,290000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 25){monstersArray = [
		[9410212,300000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 26){monstersArray = [
		[9410212,310000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 27){monstersArray = [
		[9410213,320000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 28){monstersArray = [
		[9410213,330000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 29){monstersArray = [
		[9410213,340000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 30){monstersArray = [
		[9410214,350000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 31){monstersArray = [
		[9410214,360000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 32){monstersArray = [
		[9410214,370000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 33){monstersArray = [
		[9410215,380000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 34){monstersArray = [
		[9410215,390000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 35){monstersArray = [
		[9410215,400000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 36){monstersArray = [
		[9410216,410000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 37){monstersArray = [
		[9410216,420000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 38){monstersArray = [
		[9410216,430000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 39){monstersArray = [
		[9410218,440000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 40){monstersArray = [
		[9410218,450000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 41){monstersArray = [
		[9410218,460000000000*血量系数,1,600000]];
		points = [[-106,128]];
	}else if(_ta == 42){monstersArray = [
		[9001014,470000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 43){monstersArray = [
		[9001014,480000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 44){monstersArray = [
		[9001014,490000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 45){monstersArray = [
		[9100033,500000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 46){monstersArray = [
		[9100033,510000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 47){monstersArray = [
		[9100033,520000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 48){monstersArray = [
		[9410198,530000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 49){monstersArray = [
		[9410198,540000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 50){monstersArray = [
		[9410198,550000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 51){monstersArray = [
		[9410210,560000000000*血量系数,1,600000]];
		points = [[-106,128]];

	}else if(_ta == 52){monstersArray = [
		[9410210,570000000000*血量系数,1,600000]];
		points = [[-106,128]];
	
	}else if(_ta == 53){monstersArray = [
		[9410210,580000000000*血量系数,1,600000]];
		points = [[-106,128]];
		
	}else if(_ta == 54){monstersArray = [
		[9601505,1000000000000*血量系数,1,600000]];
		points = [[-106,128]];
		
		
		
		

	}
	
}


function init() {
    em.setProperty("state", "0");
    em.setProperty("batch", "1");
}

function monsterValue(eim, mobId) {
    
    return 1;
}

function setup() {
    em.setProperty("state", "1");
    em.setProperty("batch", "1");
    em.setProperty("leader", "true");
    var eim = em.newInstance("tongtianta");
	eim.setProperty("times", 0);
	var map = eim.setInstanceMap(fuben_mapId);
	map.resetFully();
	eim.getMapFactory().getMap(fuben_mapId).killAllMonsters(true);//dui ma ?
	initDatasForTa();
	spawnMonster(eim);
	
	//em.schedule("spawnMonster", time4fMonster,eim);
    return eim;
}
function sendMessage(str, item, eim,mapId) {

    var map = eim.getMapInstance(mapId);
    var characterList = map.getCharacters();
    for (var i = 0; i < characterList.size(); i++) {
        var cha = characterList.get(i);
        cha.startMapEffect(str, item);
    }
}

function spawnMonster(eim) {
	
	if (em.getProperty("state")==1) {
		var map = eim.getMapInstance(0);
		var mob = null;

		var overrideStats = em.newMonsterStats();
		
		//批次编号
		var batchIndex =  parseInt(em.getProperty("batch"));
		
		em.setProperty("batch", (batchIndex+1)+"");
		var curMonster = monstersArray[batchIndex-1];
		if(curMonster!=null){
			overrideStats.setOHp(curMonster[1]);
			if(batchIndex == 1){
				eim.startEventTimer(curMonster[3]);
			}else{
				eim.restartEventTimer(curMonster[3]);
			}
			for(var i=0;i < points.length; i++) {
				for(var km = 0; km < curMonster[2];km ++){
					mob = em.getMonster(curMonster[0]);
					mob.setOverrideStats(overrideStats);
					mob.setHp(curMonster[1]);
					eim.registerMonster(mob);
					var x = points[i];
					map.spawnMonsterOnGroundBelow(mob, new java.awt.Point(x[0],x[1])); //刷出这个怪物
				}
			}
		}else{
			//所有批次打完了。
			if(batchIndex > monstersArray.length){
				em.setProperty("state", "2");
				map.killAllMonsters(true);
				map.spawnNpc(9000102, new java.awt.Point(-47,94));
				sendMessage("恭喜你通关『通天塔』第"+(parseInt(em.getProperty("Number_CS"))+1)+"层,请领取奖励", 5121000, eim,fuben_mapId);
				winner = eim.getPlayers().get(0);
				em.broadcastServerMsg(5,"玩家：[" + winner.getName() + "]通关『通天塔』第（"+(parseInt(em.getProperty("Number_CS"))+1)+"）层,大家恭喜他吧",false);
			}
		}
	}
}

//倒计时结束
function scheduledTimeout(eim) {
	if (em.getProperty("state")!=2) {
		em.setProperty("state", "0");
		eim.disposeIfPlayerBelow(100, 910000000);
	}
	else{
		em.setProperty("state", "0");
		em.setProperty("Number_CS", "0");
		eim.disposeIfPlayerBelow(100, 910000000);
	}
}

//切换地图
function changedMap(eim, player, mapid) {
    if (mapid != fuben_mapId) {
        eim.unregisterPlayer(player);
        if (eim.disposeIfPlayerBelow(0, 0)) {
            em.setProperty("state", "0");
			em.setProperty("Number_CS", "0");
            em.setProperty("leader", "true");
        }
		return ;
    }
}

//玩家进场
function playerEntry(eim, player) {
    var map = eim.getMapInstance(0);
    player.changeMap(map, map.getPortal(0));
	
	
}

function playerRevive(eim, player) {}

function playerDisconnected(eim, player) {
    playerExit(eim, player);
}

function leftParty(eim, player) {
    // If only 2 players are left, uncompletable
    if (eim.disposeIfPlayerBelow(100, eim.getProperty("cleared") == null ? 910000000 : 910000000)) {
        em.setProperty("state", "0");
		em.setProperty("Number_CS", "0");
    } else {
        playerExit(eim, player);
    }
}

function disbandParty(eim) {
    // Boot whole party and end
    eim.disposeIfPlayerBelow(100, eim.getProperty("cleared") == null ? 910000000 : 910000000);
    em.setProperty("state", "0");
	em.setProperty("Number_CS", "0");
}

function playerExit(eim, player) {
	em.setProperty("state", "0");
	em.setProperty("Number_CS", "0");
	eim.disposeIfPlayerBelow(100, 910000000);
}

// For offline players
function removePlayer(eim, player) {
    eim.unregisterPlayer(player);
}

function clearPQ(eim) {
	timeOut(eim)
    eim.disposeIfPlayerBelow(100, eim.getProperty("cleared") == null ? 910000000 : 910000000);

	em.setProperty("Number_CS", "0");
    em.setProperty("state", "0");
}

function finish(eim) {
    eim.disposeIfPlayerBelow(100, eim.getProperty("cleared") == null ? 910000000 : 910000000);

	em.setProperty("Number_CS", "0");
    em.setProperty("state", "0");
}

function timeOut(eim) {
    eim.disposeIfPlayerBelow(100, eim.getProperty("cleared") == null ? 910000000 : 910000000);

	em.setProperty("Number_CS", "0");
    em.setProperty("state", "0");
}

function cancelSchedule() {}

function playerDead(eim, player) {
    eim.disposeIfPlayerBelow(100, eim.getProperty("cleared") == null ? 910000000 : 910000000);
    em.setProperty("state", "0");
	em.setProperty("Number_CS", "0");
}

function allMonstersDead(eim) {
	if (em.getProperty("state")==1) {
			spawnMonster(eim);
	}
}

	
/*
	模式2

if(_ta > 0 && _ta <= 10){
	monstersArray = [];
	points = [];
}else if(_ta > 10 && _ta <= 20){
	monstersArray = [];
	points = [];
}*/