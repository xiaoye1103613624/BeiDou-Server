function enter(pi) {
    if (pi.getPlayer().getLevel() >= 1) {
	pi.warp(910000000,0);

    } else {
	pi.playerMessage(5, "You must be level 30.");
    }
}