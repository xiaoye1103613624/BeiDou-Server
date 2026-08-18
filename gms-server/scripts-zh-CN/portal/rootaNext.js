// 鲁塔比斯庭院 -> 下一层（+10）
// ChallengeDungeon 实例内：开战拦截；通关后回匠人街（不进下一层）。
function enter(pi) {
    var eim = pi.getEventInstance();
    if (eim != null && isChallengeDungeon(eim)) {
        var phase = eim.getProperty("phase") || "fight";
        if (phase !== "loot" && eim.getProperty("cleared") !== "1") {
            pi.playerMessage(5, "挑战进行中，暂不可离开。击杀 Boss 后再从出口离开。");
            return false;
        }
        pi.playPortalSound();
        eim.unregisterPlayer(pi.getPlayer());
        pi.warp(910001000, 0);
        return true;
    }

    var mapId = pi.getPlayer().getMapId();
    if (mapId < 105200000 || mapId > 105200999) {
        return false;
    }
    var nextMap = mapId + 10;
    pi.playPortalSound();
    pi.warp(nextMap, "sp");
    return true;
}

function isChallengeDungeon(eim) {
    try {
        var em = eim.getEm();
        if (em != null && em.getName() === "ChallengeDungeon") {
            return true;
        }
    } catch (e) {
    }
    var name = "" + eim.getName();
    return name.indexOf("ChallengeDungeon") === 0;
}
