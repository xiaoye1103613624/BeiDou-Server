/*
锅盖自制事件，自制开双
QQ338150
*/
function init() {
    em.setProperty("state", "0");
}

function setup() {
    em.setProperty("state", "1");
    var eim = em.newInstance("Autodbexp3");
    eim.startEventTimer(60 * 60 * 1000 * 12); //1小时
	em.setExpRate(4);
	em.broadcastYellowMsg("系统已开启双倍经验活动。");
    return eim;
}


function scheduledTimeout(eim) {
    em.broadcastYellowMsg("双倍经验活动已结束。");
	em.setExpRate(2);
    em.setProperty("state", "0");
}
