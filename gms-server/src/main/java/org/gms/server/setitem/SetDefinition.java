package org.gms.server.setitem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetDefinition {
    public int setId;
    /** 展示主名（优先中文），兼容旧逻辑 */
    public String setName = "";
    public String setNameZh = "";
    public String setNameEn = "";
    public int completeCount;
    public boolean enabled = true;
    /** 是否来自 WZ */
    public boolean fromWz;
    /** 是否有 DB 覆盖记录 */
    public boolean fromDb;
    public final Set<Integer> itemIds = new HashSet<>();
    public final Map<Integer, SetBonus> tiers = new HashMap<>();

    /** 客户端/管理端展示名：中文优先，否则英文，再回退 setName。 */
    public String displayName() {
        if (setNameZh != null && !setNameZh.isBlank()) {
            return setNameZh.trim();
        }
        if (setNameEn != null && !setNameEn.isBlank()) {
            return setNameEn.trim();
        }
        if (setName != null && !setName.isBlank()) {
            return setName.trim();
        }
        return "Set " + setId;
    }

    /** 根据 zh/en 同步展示主名。 */
    public void syncDisplayName() {
        setName = displayName();
    }
}
