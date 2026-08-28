package org.gms.server.setitem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetDefinition {
    public int setId;
    public String setName = "";
    public int completeCount;
    public boolean enabled = true;
    /** 是否来自 WZ */
    public boolean fromWz;
    /** 是否有 DB 覆盖记录 */
    public boolean fromDb;
    public final Set<Integer> itemIds = new HashSet<>();
    public final Map<Integer, SetBonus> tiers = new HashMap<>();
}
