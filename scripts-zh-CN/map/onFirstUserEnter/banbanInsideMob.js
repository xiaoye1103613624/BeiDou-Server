function start(ms) {
    spawnRootAbyssBoss(ms, 8910100, 8910000, 0, 134);
}

function spawnRootAbyssBoss(ms, normalId, chaosId, x, y) {
    var map = ms.getPlayer().getMap();
    var mobId = ms.getPlayer().getMapId() >= 105200500 ? chaosId : normalId;
    if (map.getMonsterById(mobId) != null) {
        return;
    }
    ms.spawnMonster(mobId, x, y);
}
