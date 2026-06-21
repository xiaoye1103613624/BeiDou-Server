function enter(pi) {
    if (pi.getMap().getCharactersSize() >= 2 || pi.getMap(926100401).getCharactersSize() > 0) {
	pi.warpParty(926100401,0);
    } else {
	pi.playerMessage(5, "你需要至少2个队员在此地图!。");
    }
}