function enter(pi) {
    var returnMap = pi.getPlayer().getSavedLocation("TURNEGG");
    pi.getPlayer().clearSavedLocation("TURNEGG");
    if (returnMap < 0) {
	returnMap = 102000000;
    }
    var target = pi.getMap(returnMap);
    var portal = target.getPortal("GHousingIn00");
    if (portal == null) {
	portal = target.getPortal(0);
    }
    if (pi.getMapId() != target) {
	pi.playPortalSound();
	pi.getPlayer().changeMap(target, portal);
    }
}