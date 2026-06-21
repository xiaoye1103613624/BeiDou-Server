function enter(pi) {
    if (pi.getMap().getReactorByName("rnj3_out1").getState() > 0) {
	pi.warp(926100201,0);
    } else {
	pi.playerMessage(5, "下一关尚未打开.");
    }
}