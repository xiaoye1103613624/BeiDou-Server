// 麦格纳斯（困难/暴君）首次进入召唤
function start(ms) {
    var map = ms.getPlayer().getMap();
    if (map.getMonsterById(8880002) != null) {
        return;
    }
    ms.getMap().spawnMonsterOnGroundBelow(8880002, 2831, -1347);
}
