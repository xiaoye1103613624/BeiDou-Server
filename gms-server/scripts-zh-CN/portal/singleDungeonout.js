function enter(pi) {
    pi.playPortalSound();
    pi.warp(pi.getPlayer().getSavedLocation("MIRROR"), 0);
    pi.getPlayer().clearSavedLocation("MIRROR");
    return true;
}