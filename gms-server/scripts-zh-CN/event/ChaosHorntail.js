function init() {
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
    em.setProperty("preheadCheck", "0");
}

function setup(eim, leaderid) {
    em.setProperty("state", "1");
    em.setProperty("leader", "true");
    em.setProperty("preheadCheck", "0");
    var eim = em.newInstance("ChaosHorntail");
    // eim.getInstanceMap(240060001).resetFully();
    // eim.getInstanceMap(240060101).resetFully();
    eim.getInstanceMap(240060201).resetFully();
    //eim.getInstanceMap(240060201).resetFully();
    eim.startEventTimer(7200000); //2 hr
    eim.schedule("CheckHorntailHead", 3000);
    return eim;
}

function CheckHorntailHead(eim) {
    var prop = em.getProperty("preheadCheck");
    if (prop.equals("0")) {
        eim.schedule("CheckHorntailHead", 3000);
    } else if (prop.equals("1")) {
        em.setProperty("preheadCheck", "2");
        var mob = em.getMonster(8810128); // First HT Head
        eim.registerMonster(mob);
        eim.getMapFactory().getMap(240060001).spawnMonsterOnGroundBelow(mob, new java.awt.Point(890, 230));
        eim.broadcastPlayerMsg(5, "混沌闇黑龍王吼了一聲，你必須殺死闇黑龍王的左頭顱，才能進入下一關。");
        eim.schedule("CheckHorntailHead", 3000);
        em.setProperty("state", "2");
    } else if (prop.equals("2")) {
        eim.schedule("CheckHorntailHead", 3000);
    } else if (prop.equals("3")) {
        em.setProperty("preheadCheck", "4");
        var mob = em.getMonster(8810129); // Second HT Head
        eim.registerMonster(mob);
        eim.getMapFactory().getMap(240060101).spawnMonsterOnGroundBelow(mob, new java.awt.Point(-360, 230));
        eim.broadcastPlayerMsg(5, "混沌闇黑龍王吼了一聲，你必須殺死闇黑龍王的右頭顱，才能進入下一關。");
        em.setProperty("state", "3");
    }
}

function playerEntry(eim, player) {
    var NowMapID = eim.getProperty("NowMapID");
    var toMapid = 240060201;
    if (NowMapID != null) {
        toMapid = java.lang.Integer.parseInt(NowMapID);
    }
    var map = eim.getMapFactory().getMap(toMapid);
    player.changeMap(map, map.getPortal(0));
    //加入远征队玩家信息
    eim.setProperty("isSquadPlayerID_" + player.getId(), "1");
    //var map = eim.getMapFactory().getMap(240060001);
    //player.changeMap(map, map.getPortal(0));
    //var map = eim.getMapInstance(0);
    //player.changeMap(map, map.getPortal(0));
}

function changedMap(eim, player, mapid) {
    switch (mapid) {
        // case 240060001:
        // case 240060101:
        case 240060201:
            //保存当前执行地图ID
            eim.setProperty("NowMapID", "" + mapid);
            return;
    }
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("state", "0");
        em.setProperty("leader", "true");
    }
    //switch (mapid) {
    //    case 240060001:
    //    case 240060101:
    //    case 240060201:
    //        return;
    //}
    //eim.unregisterPlayer(player);

    //if (eim.disposeIfPlayerBelow(0, 0)) {
    //    em.setProperty("state", "0");
    //     em.setProperty("leader", "true");
    // }
    //if (mapid != 240060201) {
    //   eim.unregisterPlayer(player);

    //    if (eim.disposeIfPlayerBelow(0, 0)) {
    //        em.setProperty("state", "0");
    //        em.setProperty("leader", "true");
    //     }
    //}
}

function playerDisconnected(eim, player) {
    playerExit(eim, player);
    return 0;
    //return 0;
}

function scheduledTimeout(eim) {
    eim.disposeIfPlayerBelow(100, 240050400);
    em.setProperty("state", "0");
    em.setProperty("leader", "true");
}

function playerExit(eim, player) {
    eim.unregisterPlayer(player);

    if (eim.disposeIfPlayerBelow(0, 0)) {
        em.setProperty("state", "0");
        em.setProperty("leader", "true");
    }
}

function monsterValue(eim, mobId) {
    return 1;
}

function allMonstersDead(eim) {
    //var state = em.getProperty("state");

    // if (state.equals("1")) {
    //     em.setProperty("state", "2");
    // } else if (state.equals("2")) {
    //     em.setProperty("state", "3");
    //}
}

function playerRevive(eim, player) {
    return false;
}

function clearPQ(eim) {
    //scheduledTimeout(eim);
}
function leftParty(eim, player) {}
function disbandParty(eim) {}
function playerDead(eim, player) {
    eim.setProperty("isSquadPlayerID_" + player.getId(), "0");
}
function cancelSchedule() {}