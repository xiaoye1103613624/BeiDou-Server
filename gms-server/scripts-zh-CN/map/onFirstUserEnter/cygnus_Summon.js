// 希纳斯殿堂首次进入召唤
function start(ms) {
    var map = ms.getPlayer().getMap();
    if (map.getMonsterById(8850011) != null) {
        return;
    }
    ms.getMap().spawnMonsterOnGroundBelow(8850011, -1063, 115);
}
