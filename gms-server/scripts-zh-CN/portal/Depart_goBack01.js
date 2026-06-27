function enter(pi) {
    pi.playPortalSound();
    pi.warp(pi.getPlayer().getMapId() - 10,"left01");
}