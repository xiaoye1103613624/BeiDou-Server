// 鲁塔比斯庭院 -> 下一层（+10）
function enter(pi) {
    var mapId = pi.getPlayer().getMapId();
    if (mapId < 105200000 || mapId > 105200999) {
        return false;
    }
    var nextMap = mapId + 10;
    pi.playPortalSound();
    pi.warp(nextMap, "sp");
    return true;
}
