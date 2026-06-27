function enter(pi) {
    pi.playPortalSound();
    
    if (pi.getQuestStatus(21014) != 2) {
	pi.warp(140000000, 1);
    } else {
	pi.warp(140000000, 3);
    }
}