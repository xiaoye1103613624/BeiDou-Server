function enter(pi) {
    pi.warp(pi.getPlayer().getSavedLocation("CRYSTALGARDEN"));
    pi.getPlayer().clearSavedLocation("CRYSTALGARDEN");
    return true;
}
