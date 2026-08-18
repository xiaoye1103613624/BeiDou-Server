function enter(pi) {
    if (pi.isQuestCompleted(32104)) {
		pi.playPortalSound();
        pi.warp(101070010, 1)
    } else {
		pi.playPortalSound();
        pi.warp(101070000, 0)
    }
    return true
};