// Boss 区域出口 -> 上一层庭院（-10）
function enter(pi) {
    var mapId = pi.getPlayer().getMapId();
    if (mapId < 105200000 || mapId > 105200999) {
        return false;
    }
    pi.playPortalSound();
    pi.warp(mapId - 10, "out00");
    return true;
}
