function enter(pi) {
try {
	if (pi.getPlayer().getParty() != null && pi.getMap().getMonsterById(9300275) == null && pi.isLeader()) {
		if (pi.getPlayer().getEventInstance() != null) {
			pi.warpParty_Instanced(((pi.getPlayer().getMapId() / 100) + 1) * 100 - (pi.getPlayer().getMapId() % 100));
		} else {
			pi.warpParty(((pi.getPlayer().getMapId() / 100) + 1) * 100 - (pi.getPlayer().getMapId() % 100));
		}
		pi.playPortalSound();
	} else {
		pi.playerMessage(5,"璇风‘淇濋偑鎽╂柉鍦ㄨ繖閲屻€?);
	}
} catch (e) {
	pi.playerMessage(5, "Error: " + e);
}
}