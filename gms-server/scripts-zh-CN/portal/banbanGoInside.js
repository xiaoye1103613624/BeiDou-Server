// 半半内心世界入口（庭院 +10）
function enter(pi) {
    var mapId = pi.getPlayer().getMapId();
    if (mapId < 105200000 || mapId > 105200999) {
        return false;
    }
    pi.playPortalSound();
    pi.warp(mapId + 10, "sp");
    return true;
}
