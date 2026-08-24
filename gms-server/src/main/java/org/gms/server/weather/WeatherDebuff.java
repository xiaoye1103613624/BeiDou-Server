package org.gms.server.weather;

import org.gms.client.Character;
import org.gms.net.server.world.World;

/**
 * P0 stub: blizzard slow is deferred. WeatherPackets / WeatherTask call these;
 * behaviour stays off until Character/StatEffect familiar-buff hooks are ported.
 */
public final class WeatherDebuff {
    private WeatherDebuff() {
    }

    public static void refresh(Character chr) {
        // intentional no-op (P0 cosmetic)
    }

    public static void refreshAll(World world) {
        // intentional no-op (P0 cosmetic)
    }
}
