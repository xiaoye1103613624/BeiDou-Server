function enter(pi) {
    if (pi.getQuestStatus(21011) == 2) {
	pi.playPortalSound();
	pi.warp(140090300, 1);
    } else {
	pi.playerMessage(5, "鍐嶉€插叆涓嬩竴寮靛湴鍦栦箣鍓嶏紝璜嬪厛瀹屾垚浠诲嫏銆?);
    }
}