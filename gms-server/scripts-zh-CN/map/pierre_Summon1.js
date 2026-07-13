function start(ms) {
    spawnRootAbyssBoss(ms, 8900000, 8900000, 400, 531);
}

function spawnRootAbyssBoss(ms, normalId, chaosId, x, y) {
    var map = ms.getPlayer().getMap();
    var mobId = chaosId;
    if (map.getMonsterById(mobId) != null) {
        return;
    }
    ms.spawnMonster(mobId, x, y);
}
