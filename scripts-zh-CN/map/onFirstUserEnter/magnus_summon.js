// 麦格纳斯（普通）首次进入召唤
function start(ms) {
    var map = ms.getPlayer().getMap();
    if (map.getMonsterById(8880000) != null) {
        return;
    }
    ms.getMap().spawnMonsterOnGroundBelow(8880000, 2452, -1347);
}
