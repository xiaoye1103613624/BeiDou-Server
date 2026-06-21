function enter(pi) {
	if (pi.haveItem(1002971,1)) {
		pi.warp(980040010,0);
 		pi.playPortalSE();
		pi.dispose();
	} else {
		pi.playerMessage(5, "进去之前你需要有玩具品克缤帽子.赶紧去魔女3层打boos爆出吧");
	}
}