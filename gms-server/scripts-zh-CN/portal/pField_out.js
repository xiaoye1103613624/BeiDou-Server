function enter(pi) {
    pi.playPortalSound();
    pi.warp(pi.getPlayer().getSavedLocation("RICHIE"), 0);
    pi.getPlayer().clearSavedLocation("RICHIE");
}