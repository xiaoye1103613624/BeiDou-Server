package org.gms.server.weather;

import org.gms.client.BuffStat;
import org.gms.client.Character;
import org.gms.net.server.world.World;
import org.gms.server.StatEffect;
import org.gms.util.Pair;

import java.util.List;

/**
 * Blizzard movement slow via item-sourced timed buff (client reads SPEED stat).
 */
public final class WeatherDebuff {

    public static final int BLIZZARD_SPEED = -10;
    public static final int SOURCE_ITEM_ID = 5120994;
    public static final int DURATION_MS = 75 * 1000;

    private WeatherDebuff() {
    }

    private static boolean slows(int mapId) {
        return WeatherMapVisibility.hasVisibleSky(mapId)
                && WeatherService.skyForMap(mapId) == WeatherProfile.BLIZZARD.id();
    }

    public static void refresh(Character chr) {
        if (chr == null || !chr.isLoggedInWorld() || !chr.isAlive()) {
            return;
        }
        if (!slows(chr.getMapId())) {
            return;
        }
        try {
            if (chr.refreshTimedItemBuff(SOURCE_ITEM_ID, DURATION_MS)) {
                return;
            }
            StatEffect.createFamiliarEffect(SOURCE_ITEM_ID, DURATION_MS,
                            List.of(new Pair<>(BuffStat.SPEED, BLIZZARD_SPEED)))
                    .applyTo(chr);
        } catch (Exception e) {
            LOG.warn("blizzard slow failed for {}", chr.getName(), e);
        }
    }

    public static void refreshAll(World world) {
        if (world == null || world.getPlayerStorage() == null) {
            return;
        }
        for (Character chr : world.getPlayerStorage().getAllCharacters()) {
            refresh(chr);
        }
    }

    private static final org.slf4j.Logger LOG =
            org.slf4j.LoggerFactory.getLogger(WeatherDebuff.class);
}
