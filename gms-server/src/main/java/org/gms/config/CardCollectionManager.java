package org.gms.config;

import org.gms.dao.entity.CardCollectionConfigDO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 卡片收集配置的静态缓存管理器。
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 */
public class CardCollectionManager {

    /** 地区条目列表 */
    private static final List<RegionEntry> regions = new ArrayList<>();
    /** 卡片物品ID到怪物ID的映射 */
    private static final Map<Integer, Integer> cardToMonsterMap = new ConcurrentHashMap<>();

    private CardCollectionManager() {
    }

    /**
     * 加载/刷新全部配置
     */
    public static synchronized void load(List<CardCollectionConfigDO> configs) {
        regions.clear();
        cardToMonsterMap.clear();

        // 按地区分组，保持 DB 中的排序
        Map<String, List<CardEntry>> grouped = new LinkedHashMap<>();
        configs.sort(Comparator.comparingInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 0));
        for (CardCollectionConfigDO row : configs) {
            String regionName = row.getRegionName();
            int monsterId = row.getMonsterId();
            int cardItemId = row.getCardItemId();
            grouped.computeIfAbsent(regionName, k -> new ArrayList<>())
                    .add(new CardEntry(monsterId, cardItemId));
            cardToMonsterMap.put(cardItemId, monsterId);
        }

        for (Map.Entry<String, List<CardEntry>> entry : grouped.entrySet()) {
            regions.add(new RegionEntry(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * 获取所有地区及其卡片列表，供 NPC 脚本使用。
     */
    public static List<RegionEntry> getRegions() {
        return regions;
    }

    /**
     * 根据卡片物品ID查询对应怪物ID，供 item 脚本使用。
     */
    public static int getMonsterByCard(int cardItemId) {
        return cardToMonsterMap.getOrDefault(cardItemId, 0);
    }

    // ---- 内部数据类 ----

    public static class RegionEntry {
        private final String name;
        private final List<CardEntry> monsters;

        public RegionEntry(String name, List<CardEntry> monsters) {
            this.name = name;
            this.monsters = monsters;
        }

        public String getName() {
            return name;
        }

        public List<CardEntry> getMonsters() {
            return monsters;
        }
    }

    public static class CardEntry {
        private final int monsterId;
        private final int cardItemId;

        public CardEntry(int monsterId, int cardItemId) {
            this.monsterId = monsterId;
            this.cardItemId = cardItemId;
        }

        public int getMonsterId() {
            return monsterId;
        }

        public int getCardItemId() {
            return cardItemId;
        }
    }
}