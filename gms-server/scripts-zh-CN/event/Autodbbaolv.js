
function init() {
    em.setProperty("state", "0");
}

function setup() {
    em.setProperty("state", "1");
    var eim = em.newInstance("Autodbbaolv");
    eim.startEventTimer(60 * 60 * 1000 * 2); //1小时
	em.setDropRate(2);
	em.broadcastYellowMsg("系统已开启双倍爆率活动。");
    return eim;
}


function scheduledTimeout(eim) {
    em.broadcastYellowMsg("双倍爆率活动已结束。");
	em.setDropRate(1);
    em.setProperty("state", "0");
}
