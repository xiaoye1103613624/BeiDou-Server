function enter(pi) {
    pi.playPortalSound();
    pi.warp(pi.getPlayer().getSavedLocation("MULUNG_TC"), 0);
    pi.getPlayer().clearSavedLocation("MULUNG_TC");
}