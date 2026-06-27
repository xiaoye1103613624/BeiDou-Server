function enter(pi) {
    pi.playPortalSound();
    pi.warp(pi.getPlayer().getSavedLocation("EVENT"), 0);
    pi.getPlayer().clearSavedLocation("EVENT");
}