function enter(pi) {
	if (pi.getPlayer().getParty() != null && pi.isLeader()) {
		pi.warpParty(920011000);
		pi.playPortalSound();
	} else {
		pi.playerMessage(5,"請隊長進入洞口。");
	}
}