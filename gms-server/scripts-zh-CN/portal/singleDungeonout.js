function enter(pi) {

    pi.warp(pi.getSavedLocation("MIRROR"), 0);
    pi.clearSavedLocation("MIRROR");
    return true;
}