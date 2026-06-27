function enter(pi) {
	var returnMap = pi.getPlayer().getSavedLocation("MULUNG_TC");
	if (returnMap < 0) {
		returnMap = 100000000; // to fix people who entered the fm trough an unconventional way
	}
	pi.getPlayer().clearSavedLocation("MULUNG_TC");
	pi.warp(returnMap,0);
	return true;
}