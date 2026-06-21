function enter(pi) {
    var em = pi.getEventManager("Pirate");
    if (em != null && em.getProperty("stage2").equals("3")) {
	pi.warpParty(925100200,0); //next
    } else {
	pi.playerMessage(5, "传送门尚未打开.请把怪物清理干净");
    }
}