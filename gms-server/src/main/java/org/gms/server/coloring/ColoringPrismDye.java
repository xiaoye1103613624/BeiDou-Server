package org.gms.server.coloring;

/**
 * 七彩棱镜单条染色记录（按背包实例 inventoryitemid 持久化）。
 */
public record ColoringPrismDye(
        int inventoryItemId,
        int characterId,
        int itemId,
        float hue,
        float sat,
        float light
) {
    /** 近零 HSL 视为未染色 / 应清除。 */
    public boolean isNearZero() {
        return Math.abs(hue) < 0.01f && Math.abs(sat) < 0.001f && Math.abs(light) < 0.001f;
    }

    public ColoringPrismDye clamped() {
        float h = Math.max(-180f, Math.min(180f, hue));
        float s = Math.max(-1f, Math.min(1f, sat));
        float l = Math.max(-1f, Math.min(1f, light));
        return new ColoringPrismDye(inventoryItemId, characterId, itemId, h, s, l);
    }
}
