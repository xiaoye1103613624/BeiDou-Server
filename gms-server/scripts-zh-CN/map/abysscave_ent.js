function start(ms) {
    spawnRootAbyssBoss(ms, 8930100, 8930000, 0, 180);
}

function spawnRootAbyssBoss(ms, normalId, chaosId, x, y) {
    var map = ms.getPlayer().getMap();
    var mobId = ms.getPlayer().getMapId() >= 105200500 ? chaosId : normalId;
    if (map.getMonsterById(mobId) != null) {
        return;
    }
    ms.spawnMonster(mobId, x, y);
}
