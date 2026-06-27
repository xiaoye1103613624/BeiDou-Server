function enter(pi) {
	var returnMap = pi.getPlayer().getSavedLocation("DONGDONGCHIANG");
	if (returnMap < 0) {
		returnMap = 100000000;
	}
	pi.getPlayer().clearSavedLocation("DONGDONGCHIANG");
	pi.warp(returnMap,0);
	return true;
}