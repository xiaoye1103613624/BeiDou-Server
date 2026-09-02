// 四凶远征图首次进入：按地图召唤对应 Boss
function start(ms) {
    var map = ms.getPlayer().getMap();
    var mapId = map.getId();
    var mobId = 0;
    var x = -135;
    var y = 381;

    if (mapId === 511000100) {
        mobId = 8880830;
    } else if (mapId === 511000120) {
        mobId = 8880831;
    } else if (mapId === 511000140) {
        mobId = 8880832;
    }

    if (mobId === 0) {
        return;
    }
    if (map.getMonsterById(mobId) != null) {
        return;
    }
    ms.getMap().spawnMonsterOnGroundBelow(mobId, x, y);
}
