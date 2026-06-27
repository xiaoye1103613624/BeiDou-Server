function enter(pi) {
    if (pi.getQuestStatus(21013) == 2) {
	pi.playPortalSound();
	pi.warp(140090500, 1);
    } else {
	pi.playerMessage(5, "鍐嶉€插叆涓嬩竴寮靛湴鍦栦箣鍓嶏紝璜嬪厛瀹屾垚浠诲嫏銆?);
    }
}