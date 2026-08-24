package org.gms.server.weather;

/**
 * Silent night combat scalars (damage taken/dealt, EXP, spawn density).
 */
public final class WeatherCombat {

    private WeatherCombat() {
    }

    private static final float NIGHT_THRESHOLD = 0.5f;
    private static final double NIGHT_MOB_DAMAGE = 1.10;
    private static final double NIGHT_MOB_DEFENCE = 1.0 / 1.10;
    private static final double NIGHT_EXP = 1.15;
    private static final double NIGHT_SPAWN = 1.35;

    public static boolean isNight() {
        return WeatherService.nightLevel() > NIGHT_THRESHOLD;
    }

    public static int scaleDamageToMonster(int damage) {
        if (damage <= 0 || !isNight()) {
            return damage;
        }
        return apply(damage, NIGHT_MOB_DEFENCE);
    }

    public static int scaleDamageToPlayer(int damage) {
        if (damage <= 0) {
            return damage;
        }
        return isNight() ? apply(damage, NIGHT_MOB_DAMAGE) : damage;
    }

    public static int scaleExp(int exp) {
        if (exp <= 0) {
            return exp;
        }
        return isNight() ? apply(exp, NIGHT_EXP) : exp;
    }

    public static double spawnMultiplier() {
        return isNight() ? NIGHT_SPAWN : 1.0;
    }

    private static int apply(int value, double factor) {
        long scaled = Math.round(value * factor);
        if (scaled < 1L) {
            scaled = 1L;
        }
        if (scaled > Integer.MAX_VALUE) {
            scaled = Integer.MAX_VALUE;
        }
        return (int) scaled;
    }
}
