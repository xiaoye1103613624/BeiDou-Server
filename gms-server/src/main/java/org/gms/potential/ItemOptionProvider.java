package org.gms.potential;

import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.DataType;
import org.gms.provider.wz.WZFiles;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 加载 {@code Item.wz/ItemOption.img}（潜能/星岩词条池）到内存，供潜能结算与套装 Option 解析复用。
 * <p>
 * 结构：{@code ItemOption.img/{optionId}/level/{n}/<int name="incSTR" value=".."/>}。
 * optionId 为数值（WZ 节点名为零填充字符串，如 {@code 000001}），level 为 1~20。
 * <p>
 * 必须在无 IO 路径上调用（{@code computeBonus} 逐件装备被调用），因此启动后只查内存 Map。
 */
public final class ItemOptionProvider {
    private static final ItemOptionProvider INSTANCE = new ItemOptionProvider();

    /** optionId -> (level -> (statKey -> value)) */
    private static final Map<Integer, Map<Integer, Map<String, Integer>>> OPTIONS = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;
    private static final Object LOCK = new Object();

    private ItemOptionProvider() {}

    public static ItemOptionProvider getInstance() {
        return INSTANCE;
    }

    public static void preload() {
        ensureLoaded();
    }

    private static void ensureLoaded() {
        if (loaded) {
            return;
        }
        synchronized (LOCK) {
            if (loaded) {
                return;
            }
            loadFromWz();
            loaded = true;
        }
    }

    private static void loadFromWz() {
        DataProvider dp = DataProviderFactory.getDataProvider(WZFiles.ITEM);
        if (dp == null) {
            return;
        }
        Data root = dp.getData("ItemOption.img");
        if (root == null) {
            return;
        }
        for (Data optNode : root.getChildren()) {
            int optionId;
            try {
                optionId = Integer.parseInt(optNode.getName());
            } catch (NumberFormatException nfe) {
                continue;
            }
            if (optionId <= 0) {
                continue;
            }
            Map<Integer, Map<String, Integer>> byLevel = new HashMap<>();
            Data levelRoot = optNode.getChildByPath("level");
            if (levelRoot != null) {
                for (Data lvNode : levelRoot.getChildren()) {
                    int lv;
                    try {
                        lv = Integer.parseInt(lvNode.getName());
                    } catch (NumberFormatException nfe) {
                        continue;
                    }
                    Map<String, Integer> stats = new HashMap<>();
                    for (Data statNode : lvNode.getChildren()) {
                        if (statNode.getType() == DataType.INT) {
                            stats.put(statNode.getName(), DataTool.getInt(statNode));
                        }
                    }
                    if (!stats.isEmpty()) {
                        byLevel.put(lv, stats);
                    }
                }
            }
            if (!byLevel.isEmpty()) {
                OPTIONS.put(optionId, byLevel);
            }
        }
    }

    /**
     * @return 指定词条在指定等级下的属性 Map（WZ 风格 key，如 {@code incSTR}）；缺失时返回空 Map（不抛异常）。
     *         等级不存在时回退到不超过该等级的最高等级，再回退到最低等级。
     */
    public Map<String, Integer> getStats(int optionId, int level) {
        ensureLoaded();
        Map<Integer, Map<String, Integer>> byLevel = OPTIONS.get(optionId);
        if (byLevel == null || byLevel.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> exact = byLevel.get(level);
        if (exact != null) {
            return exact;
        }
        int best = -1;
        for (int lv : byLevel.keySet()) {
            if (lv <= level && lv > best) {
                best = lv;
            }
        }
        if (best < 0) {
            best = Integer.MAX_VALUE;
            for (int lv : byLevel.keySet()) {
                if (lv < best) {
                    best = lv;
                }
            }
        }
        Map<String, Integer> fallback = byLevel.get(best);
        return fallback == null ? Collections.emptyMap() : fallback;
    }

    /** 调试/统计用：已加载的词条数量。 */
    public static int loadedOptionCount() {
        ensureLoaded();
        return OPTIONS.size();
    }

    /** 已加载的词条 ID 集合（供 GM 随机 roll 使用）。 */
    public static java.util.Set<Integer> loadedKeys() {
        ensureLoaded();
        return OPTIONS.keySet();
    }

    /** 仅供单测模拟注入。 */
    static void injectForTest(int optionId, int level, Map<String, Integer> stats) {
        OPTIONS.computeIfAbsent(optionId, k -> new ConcurrentHashMap<>()).put(level, stats);
        loaded = true;
    }
}
