package org.gms.server.cashshop;

import org.gms.client.inventory.InventoryType;
import org.gms.constants.inventory.ItemConstants;
import org.gms.manager.ServerManager;
import org.gms.service.WindowCashShopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 窗口版现金商城内存目录。
 * <p>
 * 加载顺序：DB（{@code xy_cashshop_*} 有启用数据）→ 否则 {@code cashshop/catalog.tsv}。
 * 支持全量 / 按 (tab,category) 桶热重载，避免每次打全库。
 */
public final class CashShopCatalog {
    private static final Logger log = LoggerFactory.getLogger(CashShopCatalog.class);

    private static final String DEFAULT_DIR = "cashshop";
    private static final String FILE_NAME = "catalog.tsv";

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

    public static Path file() {
        final String prop = System.getProperty("cashshop-path");
        return Path.of(prop != null ? prop : DEFAULT_DIR, FILE_NAME);
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
            log.warn("Cash Shop catalog: DB load skipped, falling back to TSV: {}", e.toString());
        }
        loadFromTsv();
    }

    public static synchronized void loadFromTsv() {
        final Path path = file();
        if (!Files.isRegularFile(path)) {
            log.warn("Cash Shop catalog: {} not found; the shop will be empty", path.toAbsolutePath());
            replaceAll(List.of(), "empty");
            return;
        }

        final List<Row> rows = new ArrayList<>();
        final Map<Integer, Row> seen = new HashMap<>();
        int lineNo = 0, bad = 0, dupe = 0, unusable = 0;

        try {
            for (String line : Files.readAllLines(path, StandardCharsets.UTF_8)) {
                lineNo++;
                final String s = line.strip();
                if (s.isEmpty() || s.charAt(0) == '#') {
                    continue;
                }
                final String[] f = s.split("\t");
                if (f.length < 7) {
                    if (bad++ < 5) {
                        log.warn("Cash Shop catalog line {}: expected 7+ columns, got {}", lineNo, f.length);
                    }
                    continue;
                }
                final int itemId, price, count, tab, cat, period, gender;
                try {
                    itemId = Integer.parseInt(f[0].strip());
                    price = Integer.parseInt(f[1].strip());
                    count = Integer.parseInt(f[2].strip());
                    tab = Integer.parseInt(f[3].strip());
                    cat = Integer.parseInt(f[4].strip());
                    period = Integer.parseInt(f[5].strip());
                    gender = Integer.parseInt(f[6].strip());
                } catch (NumberFormatException e) {
                    if (bad++ < 5) {
                        log.warn("Cash Shop catalog line {}: non-numeric field", lineNo);
                    }
                    continue;
                }

                if (ItemConstants.getInventoryType(itemId) == InventoryType.UNDEFINED) {
                    unusable++;
                    continue;
                }

                final String tsvName = f.length > 7 ? f[7].strip() : "";
                final String name = CashShopItemNames.resolve(itemId, tsvName);
                if (name == null) {
                    unusable++;
                    continue;
                }

                final Row row = new Row(itemId, Math.max(0, price), Math.max(1, count),
                        tab, cat, Math.max(0, period), gender, name);
                final Row prev = seen.putIfAbsent(itemId, row);
                if (prev != null) {
                    if (dupe++ < 5) {
                        log.warn("Cash Shop catalog line {}: duplicate item id {}, keeping the first", lineNo, itemId);
                    }
                    continue;
                }
                rows.add(row);
            }
        } catch (IOException e) {
            log.error("Cash Shop catalog: could not read {}", path.toAbsolutePath(), e);
            return;
        }

        replaceAll(rows, "tsv:" + path.toAbsolutePath());
        log.info("Cash Shop catalog: {} rows across {} categories from TSV {} "
                        + "({} unusable, {} duplicate serials, {} malformed lines)",
                all.size(), index.size(), path, unusable, dupe, bad);
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
