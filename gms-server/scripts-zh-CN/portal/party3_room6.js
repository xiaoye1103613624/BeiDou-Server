function enter(pi) {
	if (pi.getPlayer().getParty() != null && pi.isLeader()) {
		pi.warpParty(920010700);
		pi.playPortalSound();
	} else {
		pi.playerMessage(5,"璜嬮殜闀烽€插叆閫欒！銆?);
	}
}