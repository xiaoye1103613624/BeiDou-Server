function enter(pi) {
    if (pi.getMap().getAllMonstersThreadsafe().size() == 0) {
	pi.warpParty(925100300,0); //next
    } else {
		pi.playerMessage(5, "传送门尚未打开.请把怪物清理干净");
    }
}