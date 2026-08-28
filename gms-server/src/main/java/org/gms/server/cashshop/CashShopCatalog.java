package org.gms.server.cashshop;

import org.gms.manager.ServerManager;
import org.gms.service.WindowCashShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 窗口版现金商城内存目录。
 * <p>
 * 仅从 DB（{@code xy_cashshop_*}）加载；支持全量 / 按 (tab,category) 桶热重载。
 */
public final class CashShopCatalog {
    private static final Logger log = LoggerFactory.getLogger(CashShopCatalog.class);

    public record Row(int itemId, int price, int count,
                      int tab, int category, int period, int gender, String name) {
        public int bucket() {
            return (tab << 8) | category;
        }
    }

    private static volatile List<Row> all = List.of();
    private static volatile Map<Integer, Row> byItemId = Map.of();
    private static volatile Map<Integer, List<Row>> byBucket = Map.of();
    private static volatile List<int[]> index = List.of();
    private static volatile String source = "empty";

    private CashShopCatalog() {
    }

    public static List<Row> all() {
        return all;
    }

    public static Row byItemId(int itemId) {
        return byItemId.get(itemId);
    }

    public static List<Row> bucket(int tab, int category) {
        return byBucket.getOrDefault((tab << 8) | category, List.of());
    }

    public static List<int[]> index() {
        return index;
    }

    public static int size() {
        return all.size();
    }

    public static String source() {
        return source;
    }

    public static synchronized void load() {
        try {
            final var ctx = ServerManager.getApplicationContext();
            if (ctx != null && ctx.containsBean("windowCashShopService")) {
                final WindowCashShopService svc = ctx.getBean(WindowCashShopService.class);
                if (svc.loadFromDbIntoMemory()) {
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("Cash Shop catalog: DB load failed: {}", e.toString());
        }
        replaceAll(List.of(), "empty");
        log.warn("Cash Shop catalog: no enabled data in xy_cashshop_* tables; shop is empty");
    }

    public static synchronized void replaceAll(List<Row> rows, String src) {
        rebuild(rows);
        source = src == null ? "memory" : src;
        log.info("Cash Shop catalog: replaced all → {} rows / {} buckets ({})", all.size(), index.size(), source);
    }

    /**
     * 只替换一个 legacy (tab,category) 桶，其余桶保持不变。用于按分类热重载。
     */
    public static synchronized void replaceBucket(int tab, int category, List<Row> bucketRows) {
        final int key = (tab << 8) | category;
        final List<Row> next = new ArrayList<>(all.size());
        for (Row r : all) {
            if (r.bucket() != key) {
                next.add(r);
            }
        }
        if (bucketRows != null) {
            next.addAll(bucketRows);
        }
        rebuild(next);
        log.info("Cash Shop catalog: replaced bucket tab={} cat={} → {} items (total {})",
                tab, category, bucketRows == null ? 0 : bucketRows.size(), all.size());
    }

    private static void rebuild(List<Row> rows) {
        final Map<Integer, Row> byId = new HashMap<>(rows.size() * 2);
        final Map<Integer, List<Row>> buckets = new LinkedHashMap<>();
        for (Row r : rows) {
            byId.putIfAbsent(r.itemId(), r);
            buckets.computeIfAbsent(r.bucket(), k -> new ArrayList<>()).add(r);
        }
        final List<int[]> idx = new ArrayList<>(buckets.size());
        final List<Integer> keys = new ArrayList<>(buckets.keySet());
        Collections.sort(keys);
        for (int k : keys) {
            final List<Row> b = buckets.get(k);
            buckets.put(k, List.copyOf(b));
            idx.add(new int[]{k >> 8, k & 0xFF, b.size()});
        }

        all = List.copyOf(rows);
        byItemId = Map.copyOf(byId);
        byBucket = Map.copyOf(buckets);
        index = List.copyOf(idx);
    }
}
