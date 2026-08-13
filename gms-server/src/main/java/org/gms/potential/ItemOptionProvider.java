package org.gms.potential;

import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 加载 Item.wz/ItemOption.img — 潜能选项定义（含 095 info/optionType、reqLevel）。
 */
public final class ItemOptionProvider {
    private static final Logger log = LoggerFactory.getLogger(ItemOptionProvider.class);
    private static final ItemOptionProvider INSTANCE = new ItemOptionProvider();

    public static ItemOptionProvider getInstance() {
        return INSTANCE;
    }

    public static final class OptionMeta {
        public final int optionId;
        public final int optionType;
        public final int reqLevel;
        public final int weight;

        OptionMeta(int optionId, int optionType, int reqLevel, int weight) {
            this.optionId = optionId;
            this.optionType = optionType;
            this.reqLevel = reqLevel;
            this.weight = weight;
        }
    }

    /** optionId -> level -> (statKey -> value) */
    private final Map<Integer, Map<Integer, Map<String, Integer>>> options = new HashMap<>();
    private final Map<Integer, OptionMeta> meta = new HashMap<>();
    private volatile boolean loaded;

    private ItemOptionProvider() {}

    public synchronized void load() {
        if (loaded) {
            return;
        }
        try {
            DataProvider item = DataProviderFactory.getDataProvider(WZFiles.ITEM);
            Data root = item.getData("ItemOption.img");
            if (root == null) {
                log.warn("ItemOption.img missing — potential system disabled");
                loaded = true;
                return;
            }
            for (Data optNode : root.getChildren()) {
                int optionId;
                try {
                    optionId = Integer.parseInt(optNode.getName());
                } catch (NumberFormatException e) {
                    continue;
                }
                Data info = optNode.getChildByPath("info");
                int optionType = 0;
                int reqLevel = 0;
                int weight = 0;
                if (info != null) {
                    optionType = DataTool.getInt("optionType", info, 0);
                    reqLevel = DataTool.getInt("reqLevel", info, 0);
                    weight = DataTool.getInt("weight", info, 0);
                }
                meta.put(optionId, new OptionMeta(optionId, optionType, reqLevel, weight));

                Data levelRoot = optNode.getChildByPath("level");
                if (levelRoot == null) {
                    continue;
                }
                Map<Integer, Map<String, Integer>> byLevel = new HashMap<>();
                for (Data lvNode : levelRoot.getChildren()) {
                    int lv;
                    try {
                        lv = Integer.parseInt(lvNode.getName());
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    Map<String, Integer> stats = new HashMap<>();
                    for (Data s : lvNode.getChildren()) {
                        stats.put(s.getName(), DataTool.getIntConvert(s, 0));
                    }
                    byLevel.put(lv, stats);
                }
                options.put(optionId, byLevel);
            }
            loaded = true;
            log.info("ItemOption loaded: {} options (meta={})", options.size(), meta.size());
        } catch (Exception e) {
            log.error("Failed to load ItemOption.img", e);
            loaded = true;
        }
    }

    public Map<String, Integer> getStats(int optionId, int level) {
        load();
        Map<Integer, Map<String, Integer>> byLevel = options.get(optionId);
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
            best = byLevel.keySet().stream().min(Integer::compareTo).orElse(-1);
        }
        return best < 0 ? Collections.emptyMap() : byLevel.getOrDefault(best, Collections.emptyMap());
    }

    public int getWeight(int optionId) {
        load();
        OptionMeta m = meta.get(optionId);
        return m == null ? 0 : m.weight;
    }

    public OptionMeta getMeta(int optionId) {
        load();
        return meta.get(optionId);
    }

    public boolean hasOption(int optionId) {
        load();
        return options.containsKey(optionId);
    }

    public List<Integer> listOptionIds() {
        load();
        return new ArrayList<>(options.keySet());
    }

    /** 全部选项元数据（洗潜池）。 */
    public List<OptionMeta> listMeta() {
        load();
        return new ArrayList<>(meta.values());
    }
}
