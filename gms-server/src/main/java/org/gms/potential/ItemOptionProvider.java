package org.gms.potential;

import java.util.Collections;
import java.util.Map;

/** S9 stub: ItemOption.img lookup not ported; SetTiersV2Parser uses known fallbacks. */
public final class ItemOptionProvider {
    private static final ItemOptionProvider INSTANCE = new ItemOptionProvider();

    private ItemOptionProvider() {}

    public static ItemOptionProvider getInstance() {
        return INSTANCE;
    }

    public Map<String, Integer> getStats(int optionId, int level) {
        return Collections.emptyMap();
    }
}
