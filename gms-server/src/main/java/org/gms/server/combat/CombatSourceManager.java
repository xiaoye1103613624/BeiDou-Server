package org.gms.server.combat;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.CarryItemStatDO;
import org.gms.dao.entity.EquipEnhanceRuleDO;
import org.gms.dao.mapper.CarryItemStatMapper;
import org.gms.dao.mapper.EquipEnhanceRuleMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 强化规则 / 携带物属性内存缓存。
 */
public final class CombatSourceManager {
    private static final Logger log = LoggerFactory.getLogger(CombatSourceManager.class);

    private static final CopyOnWriteArrayList<EquipEnhanceRuleDO> ENHANCE_RULES = new CopyOnWriteArrayList<>();
    private static final ConcurrentHashMap<Integer, CarryItemStatDO> CARRY_BY_ITEM = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;

    private CombatSourceManager() {}

    public static void loadOrSeed() {
        if (loaded) {
            return;
        }
        reload();
    }

    public static synchronized void reload() {
        try {
            EquipEnhanceRuleMapper enhanceMapper =
                    ServerManager.getApplicationContext().getBean(EquipEnhanceRuleMapper.class);
            CarryItemStatMapper carryMapper =
                    ServerManager.getApplicationContext().getBean(CarryItemStatMapper.class);

            List<EquipEnhanceRuleDO> rules = enhanceMapper.selectListByQuery(
                    QueryWrapper.create().orderBy("sort_order", true).orderBy("id", true));
            ENHANCE_RULES.clear();
            if (rules != null) {
                ENHANCE_RULES.addAll(rules);
            }

            List<CarryItemStatDO> carries = carryMapper.selectListByQuery(QueryWrapper.create());
            CARRY_BY_ITEM.clear();
            if (carries != null) {
                for (CarryItemStatDO row : carries) {
                    if (row.getItemId() != null) {
                        CARRY_BY_ITEM.put(row.getItemId(), row);
                    }
                }
            }
            loaded = true;
            log.info("CombatSourceManager reloaded: enhanceRules={}, carryItems={}",
                    ENHANCE_RULES.size(), CARRY_BY_ITEM.size());
            markOnlinePlayersDirty();
        } catch (Exception e) {
            log.warn("CombatSourceManager reload failed: {}", e.getMessage());
            loaded = true;
        }
    }

    private static void markOnlinePlayersDirty() {
        try {
            for (var wserv : org.gms.net.server.Server.getInstance().getWorlds()) {
                for (var cserv : wserv.getChannels()) {
                    for (org.gms.client.Character chr : cserv.getPlayerStorage().getAllCharacters()) {
                        if (chr != null) {
                            chr.markCombatStatsDirty();
                            chr.ensureCombatStatsFresh();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // Server 尚未启动时 reload 无在线角色
        }
    }

    public static List<EquipEnhanceRuleDO> listEnhanceRules() {
        loadOrSeed();
        return Collections.unmodifiableList(new ArrayList<>(ENHANCE_RULES));
    }

    public static Map<Integer, CarryItemStatDO> carryMap() {
        loadOrSeed();
        return Collections.unmodifiableMap(CARRY_BY_ITEM);
    }

    public static CarryItemStatDO getCarry(int itemId) {
        loadOrSeed();
        return CARRY_BY_ITEM.get(itemId);
    }

    public static boolean isConfiguredCarry(int itemId) {
        loadOrSeed();
        return CARRY_BY_ITEM.containsKey(itemId);
    }

    public static boolean hasAnyCarryConfig() {
        loadOrSeed();
        return !CARRY_BY_ITEM.isEmpty();
    }
}
