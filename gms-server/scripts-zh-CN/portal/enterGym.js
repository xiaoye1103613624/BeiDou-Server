function enter(pi) {
    if (pi.getQuestStatus(21701) == 1) {
	pi.playPortalSound();
	pi.warp(914010000, 1);
    } else if (pi.getQuestStatus(21702) == 1) {
	pi.playPortalSound();
	pi.warp(914010100, 1);
    } else if (pi.getQuestStatus(21703) == 1) {
	pi.playPortalSound();
	pi.warp(914010200, 1);
    } else {
	pi.playerMessage(5, "鍙湁寰楀埌鏅瓙淇反鏅傛墠鑳介€插叆浼侀禎淇崐鍫淬€?);
    }
}