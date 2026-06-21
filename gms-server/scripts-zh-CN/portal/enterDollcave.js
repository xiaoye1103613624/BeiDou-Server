/*
function enter(pi) {
    if (cm.getPlayer().getMeso()>1) {
        pi.openNpc(1063011);
	} else if (cm.getPlayer().getMeso()>1) {
		pi.forceCompleteQuest(21728);
		pi.gainExp(200);
		pi.playerMessage(5, "任務完成。");
    } else {
        pi.playerMessage(5, "因不明的力量，而無法進入此洞穴。");
        return false;
    }
	return true;

}
*/
function enter(pi) {
    pi.warp(910000000, 0);   // 0 是入口 portal 编号，通常写 0 即可
	pi.playerMessage(5, "一股未知力量将你传送至自由市场！！！");
    return true;
}