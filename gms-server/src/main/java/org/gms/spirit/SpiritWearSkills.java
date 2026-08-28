package org.gms.spirit;

import org.gms.client.Character;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** S9 stub: spirit wear skill sync not fully ported. */
public final class SpiritWearSkills {
    private SpiritWearSkills() {}

    public static Map<Integer, Integer> sync(Character chr) {
        return new HashMap<>();
    }

    public static Map<Integer, Integer> empty() {
        return Collections.emptyMap();
    }
}
