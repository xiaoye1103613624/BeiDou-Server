package org.gms.config;

import org.gms.dao.entity.XyCollectionStageDO;
import org.gms.dao.entity.XyCollectionStageItemDO;
import org.gms.dao.entity.XyCollectionTypeDO;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * XY收集配置的静态缓存管理器。
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 */
public class XyCollectionManager {

    /** 收集类型缓存列表 */
    private static List<TypeEntry> types = new ArrayList<>();

    /** 私有构造函数，防止实例化 */
    private XyCollectionManager() {
    }

    /**
     * 加载配置数据到缓存
     * @param typeList 类型DO列表
     * @param stageList 阶段DO列表
     * @param itemList 物品DO列表
     */
    public static synchronized void load(List<XyCollectionTypeDO> typeList,
                                         List<XyCollectionStageDO> stageList,
                                         List<XyCollectionStageItemDO> itemList) {
        types.clear();

        // 按排序字段排序
        typeList.sort(Comparator.comparingInt(t -> t.getSortOrder() != null ? t.getSortOrder() : 0));
        stageList.sort(Comparator.comparingInt(s -> s.getSortOrder() != null ? s.getSortOrder() : 0));
        itemList.sort(Comparator.comparingInt(i -> i.getSortOrder() != null ? i.getSortOrder() : 0));

        // 构建阶段和物品的映射关系
        Map<Long, List<XyCollectionStageDO>> stageMap = stageList.stream()
                .collect(Collectors.groupingBy(XyCollectionStageDO::getTypeId, LinkedHashMap::new, Collectors.toList()));
        Map<Long, List<XyCollectionStageItemDO>> itemMap = itemList.stream()
                .collect(Collectors.groupingBy(XyCollectionStageItemDO::getStageId, LinkedHashMap::new, Collectors.toList()));

        // 组装缓存数据结构
        for (XyCollectionTypeDO typeDO : typeList) {
            List<StageEntry> stages = new ArrayList<>();
            List<XyCollectionStageDO> typeStages = stageMap.getOrDefault(typeDO.getId(), new ArrayList<>());
            for (XyCollectionStageDO stageDO : typeStages) {
                List<ItemEntry> items = new ArrayList<>();
                List<XyCollectionStageItemDO> stageItems = itemMap.getOrDefault(stageDO.getId(), new ArrayList<>());
                for (XyCollectionStageItemDO itemDO : stageItems) {
                    items.add(new ItemEntry(itemDO.getId(), itemDO.getItemId(), itemDO.getQuantity()));
                }
                stages.add(new StageEntry(stageDO.getId(), stageDO.getStageName(), stageDO.getSortOrder(),
                        stageDO.getRewardType(), stageDO.getRewardAmount(), items));
            }
            types.add(new TypeEntry(typeDO.getId(), typeDO.getTypeName(), typeDO.getDescription(),
                    typeDO.getSortOrder(), typeDO.getEnabled(),
                    typeDO.getRewardType(), typeDO.getRewardAmount(), stages));
        }
    }

    /**
     * 获取所有收集类型（包括禁用的）
     * @return 类型条目列表
     */
    public static List<TypeEntry> getAllTypes() {
        return types;
    }

    /**
     * 获取启用的收集类型
     * @return 启用的类型条目列表
     */
    public static List<TypeEntry> getEnabledTypes() {
        return types.stream().filter(t -> t.enabled == 1).collect(Collectors.toList());
    }

    /**
     * 根据ID获取收集类型
     * @param id 类型ID
     * @return 类型条目，不存在返回null
     */
    public static TypeEntry getTypeById(Long id) {
        return types.stream().filter(t -> t.id.equals(id)).findFirst().orElse(null);
    }

    // ---- 内部数据类 ----

    /**
     * 收集类型条目
     */
    public static class TypeEntry {
        /** 类型ID */
        private final Long id;
        /** 类型名称 */
        private final String name;
        /** 类型描述 */
        private final String description;
        /** 排序顺序 */
        private final int sortOrder;
        /** 是否启用（1启用，0禁用） */
        private final int enabled;
        /** 奖励类型（CASH/MAPLE_POINT/MESO/AP） */
        private final String rewardType;
        /** 奖励数量 */
        private final int rewardAmount;
        /** 阶段列表 */
        private final List<StageEntry> stages;

        public TypeEntry(Long id, String name, String description, int sortOrder, int enabled,
                         String rewardType, int rewardAmount, List<StageEntry> stages) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.sortOrder = sortOrder;
            this.enabled = enabled;
            this.rewardType = rewardType;
            this.rewardAmount = rewardAmount;
            this.stages = stages;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public String getDescription() { return description; }
        public int getSortOrder() { return sortOrder; }
        public int getEnabled() { return enabled; }
        public String getRewardType() { return rewardType; }
        public int getRewardAmount() { return rewardAmount; }
        public List<StageEntry> getStages() { return stages; }
    }

    /**
     * 收集阶段条目
     */
    public static class StageEntry {
        /** 阶段ID */
        private final Long id;
        /** 阶段名称 */
        private final String name;
        /** 排序顺序 */
        private final int sortOrder;
        /** 奖励类型 */
        private final String rewardType;
        /** 奖励数量 */
        private final int rewardAmount;
        /** 需求物品列表 */
        private final List<ItemEntry> items;

        public StageEntry(Long id, String name, int sortOrder,
                          String rewardType, int rewardAmount, List<ItemEntry> items) {
            this.id = id;
            this.name = name;
            this.sortOrder = sortOrder;
            this.rewardType = rewardType;
            this.rewardAmount = rewardAmount;
            this.items = items;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
        public int getSortOrder() { return sortOrder; }
        public String getRewardType() { return rewardType; }
        public int getRewardAmount() { return rewardAmount; }
        public List<ItemEntry> getItems() { return items; }
    }

    /**
     * 收集物品条目
     */
    public static class ItemEntry {
        /** 物品配置ID */
        private final Long id;
        /** 游戏物品ID */
        private final int itemId;
        /** 需求数量 */
        private final int quantity;

        public ItemEntry(Long id, int itemId, int quantity) {
            this.id = id;
            this.itemId = itemId;
            this.quantity = quantity;
        }

        public Long getId() { return id; }
        public int getItemId() { return itemId; }
        public int getQuantity() { return quantity; }
    }
}