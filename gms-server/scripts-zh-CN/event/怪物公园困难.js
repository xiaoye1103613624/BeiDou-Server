
function init() {
    em.setProperty("state", "0");
}

function setup() {
    em.setProperty("state", "1");
    var eim = em.newInstance("怪物公园困难");
    var map = eim.setInstanceMap(952010000);
    map.resetFully();
    //map.getPortal("next00").setScriptName("kpq1");
    map = eim.setInstanceMap(952010100);
    map.resetFully();
    map = eim.setInstanceMap(952010200);
    map.resetFully();
    map = eim.setInstanceMap(952010300);
    map.resetFully();
    map = eim.setInstanceMap(952010400);
    map.resetFully();
    map = eim.setInstanceMap(952010500);
	map.resetFully();
    eim.startEventTimer(1800000);
    return eim;
}

function playerEntry(eim, player) {
    var map = eim.getMapFactory().getMap(952010000);
    player.changeMap(map, map.getPortal(0));
    player.tryPartyQuest(1201);
}

function playerDead(eim, player) { }

function sendMessage(str, item, eim) {
    var map = eim.getMapInstance(952010000);
    var characterList = map.getCharacters();
    for (var i = 0; i < characterList.size(); i++) {
        var cha = characterList.get(i);
        cha.startMapEffect(str, item);
    }
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
        case 952010000:
            eim.getMapInstance(952010000).startMapEffect("团本怪物Party开始,请各自站好位置清干净当前地图的怪物", 5121000);
        case 952010100:
            eim.getMapInstance(952010100).startMapEffect("哈哈,终于来到第二关了吧,越到后面越困难哦 ", 5121000);
        case 952010200:
            eim.getMapInstance(952010200).startMapEffect("恭喜你来到第三关 奖励会越来越丰厚了 加油", 5121000);
        case 952010300:
            eim.getMapInstance(952010300).startMapEffect("不愧是大佬 这么容易就来到了第四关 接近尾声了", 5121000);
        case 952010400:
            eim.getMapInstance(952010400).startMapEffect("第五关过了就要面临Boss了，大家打起12分精神来 别泄气", 5121000);
        case 952010500:
            eim.getMapInstance(952010500).startMapEffect("欢迎来到怪物Party 团本最后阶段 请击杀Boss ", 5121000);
            return;
    }
    eim.unregisterPlayer(player);
    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("state", "0");
    }
}


function monsterValue(eim, mobId) {
    return 1;
}
function playerRevive(eim, player) { }

function playerDisconnected(eim, player) {
    return -2;
}

function leftParty(eim, player) {
    // If only 2 players are left, uncompletable
    if (eim.disposeIfPlayerBelow(3, 910000000)) {
        em.setProperty("started", "false");
    } else {
        playerExit(eim, player);
    }
}

function disbandParty(eim) {
    // Boot whole party and end
    eim.disposeIfPlayerBelow(100, 910000000);

    em.setProperty("state", "0");
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    var exit = eim.getMapFactory().getMap(910000000);
    player.changeMap(exit, exit.getPortal(0));
}

function clearPQ(eim) {
    // KPQ does nothing special with winners
    eim.disposeIfPlayerBelow(100, 910000000);

    em.setProperty("state", "0");
}

function scheduledTimeout(eim) {

    var players = eim.getPlayers();
    var exit = eim.getMapFactory().getMap(910000000);
    for (var i = 0; i < players.size(); i++) {
        var player = players.get(i);
        eim.unregisterPlayer(player);
        player.changeMap(exit, exit.getPortal(0));
    }

}


function allMonstersDead(eim) { }

function cancelSchedule() { }

function monsterDamaged(eim, chr, mobId, damage) {}
