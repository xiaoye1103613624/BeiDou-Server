package org.gms.server.weather;

import org.gms.client.Character;
import org.gms.net.server.world.World;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.maps.MapleMap;
import org.gms.util.Randomizer;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Regional night-only ambient encounters (additive to WZ spawn cap).
 */
public final class NocturnalMobService {
    private NocturnalMobService() {
    }

    private static final Object LOCK = new Object();
    private static final Map<MapleMap, NocturnalState> SPAWNED = new IdentityHashMap<>();

    private static final class NocturnalState {
        private final int target;
        private final List<Monster> monsters = new ArrayList<>();

        private NocturnalState(int target) {
            this.target = target;
        }
    }

    private enum Pool {
        HENESYS(WeatherRegion.HENESYS, 3, 5, 2300100),
        ELLINIA(WeatherRegion.ELLINIA, 4, 6, 3000001, 3230101),
        KERNING(WeatherRegion.KERNING_CITY, 3, 5, 2130103),
        PERION(WeatherRegion.PERION, 3, 4, 3210100),
        LITH_HARBOUR(WeatherRegion.LITH_HARBOUR, 3, 5, 3230102);

        private final WeatherRegion region;
        private final int minCap;
        private final int maxCap;
        private final int[] mobIds;

        Pool(WeatherRegion region, int minCap, int maxCap, int... mobIds) {
            this.region = region;
            this.minCap = minCap;
            this.maxCap = maxCap;
            this.mobIds = mobIds;
        }

        private int targetForNight() {
            return minCap + Randomizer.nextInt(maxCap - minCap + 1);
        }

        private int nextMobId() {
            return mobIds[Randomizer.nextInt(mobIds.length)];
        }

        private static Pool forMap(int mapId) {
            WeatherRegion region = WeatherRegion.forMap(mapId);
            for (Pool pool : values()) {
                if (pool.region == region) {
                    return pool;
                }
            }
            return null;
        }
    }

    public static void refreshWorld(World world) {
        if (world == null || world.getPlayerStorage() == null) {
            return;
        }

        synchronized (LOCK) {
            if (!WeatherService.isNight()) {
                despawnAll();
                return;
            }

            Set<MapleMap> occupiedMaps = Collections.newSetFromMap(new IdentityHashMap<>());
            for (Character chr : world.getPlayerStorage().getAllCharacters()) {
                if (chr.getMap() != null) {
                    occupiedMaps.add(chr.getMap());
                }
            }
            for (MapleMap map : new ArrayList<>(SPAWNED.keySet())) {
                if (map.getCharacters().isEmpty()) {
                    despawnMap(map);
                }
            }
            for (MapleMap map : occupiedMaps) {
                refreshMapLocked(map);
            }
        }
    }

    public static void refreshMap(MapleMap map) {
        if (map == null) {
            return;
        }
        synchronized (LOCK) {
            if (WeatherService.isNight()) {
                refreshMapLocked(map);
            } else {
                despawnMap(map);
            }
        }
    }

    private static void refreshMapLocked(MapleMap map) {
        Pool pool = Pool.forMap(map.getId());
        if (pool == null || map.isTown() || map.getEventInstance() != null) {
            despawnMap(map);
            return;
        }

        List<Point> anchors = map.getNocturnalSpawnPositions();
        if (anchors.isEmpty()) {
            return;
        }

        NocturnalState state = SPAWNED.computeIfAbsent(map,
                ignored -> new NocturnalState(pool.targetForNight()));
        state.monsters.removeIf(monster -> !isStillInMap(map, monster));
        int missing = state.target - state.monsters.size();
        if (missing <= 0) {
            return;
        }

        Collections.shuffle(anchors);
        for (int i = 0; i < missing && i < anchors.size(); i++) {
            Monster monster = LifeFactory.getMonster(pool.nextMobId());
            if (monster == null) {
                continue;
            }
            monster.setNocturnal(true);
            map.spawnMonsterOnGroundBelow(monster, anchors.get(i));
            if (monster.getMap() == map) {
                state.monsters.add(monster);
            }
        }
    }

    private static boolean isStillInMap(MapleMap map, Monster monster) {
        return monster != null && monster.isAlive()
                && map.getMonsterByOid(monster.getObjectId()) == monster;
    }

    private static void despawnAll() {
        for (MapleMap map : new ArrayList<>(SPAWNED.keySet())) {
            despawnMap(map);
        }
    }

    private static void despawnMap(MapleMap map) {
        NocturnalState state = SPAWNED.remove(map);
        if (state == null) {
            return;
        }
        for (Monster monster : state.monsters) {
            if (isStillInMap(map, monster)) {
                map.killMonster(monster, null, false, (short) 0);
            }
        }
    }
}
