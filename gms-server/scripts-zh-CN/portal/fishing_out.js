function enter(pi) {
    var returnMap = pi.getPlayer().getSavedLocation("FISHING");
    if (returnMap < 0) {
        returnMap = 102000000;
    }
    var target = pi.getPlayer().getClient().getChannelServer().getMapFactory().getMap(returnMap);
    var portal = target.getPortal(0);


    pi.getPlayer().clearSavedLocation("FISHING");
    pi.getPlayer().changeMap(target, portal);
}
