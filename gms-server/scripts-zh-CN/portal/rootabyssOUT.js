// 鲁塔比斯庭院出口 -> 大厅
// ChallengeDungeon 实例内：开战不可离开；通关后回匠人街。
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

    pi.playPortalSound();
    pi.warp(105200000, 0);
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
