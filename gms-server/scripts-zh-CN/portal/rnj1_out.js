function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
	pi.warp(926100100,0);
    } else {
	pi.playerMessage(5, "下一关尚未打开.");
    }
}