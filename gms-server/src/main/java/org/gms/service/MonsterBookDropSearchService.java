package org.gms.service;

import org.gms.client.Character;
import org.gms.constants.id.ItemId;
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.MonsterDropEntry;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.util.DatabaseConnection;
import org.gms.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Drop queries for the Monster Book client mod (0x372B / 0x372C).
 */
public final class MonsterBookDropSearchService {
    private static final Logger log = LoggerFactory.getLogger(MonsterBookDropSearchService.class);
    private static final int DROP_DENOMINATOR = 1_000_000;
    private static final int MAX_SEARCH_HITS = 200;

    private static volatile Set<Integer> bookItemIds;

    private MonsterBookDropSearchService() {
    }

    public static void clearDropCaches() {
        bookItemIds = null;
    }

    public static LinkedHashMap<Integer, Integer> mobDropChances(Character chr, int mobId) {
        final boolean boss = MonsterInformationProvider.getInstance().isBoss(mobId);
        final float dropRate = boss ? chr.getBossDropRate() : chr.getDropRate();

        List<int[]> rows = new ArrayList<>();
        for (MonsterDropEntry de : MonsterInformationProvider.getInstance().retrieveDrop(mobId)) {
            if (de.chance <= 0 || de.itemId == 0) {
                continue;
            }
            String name = ItemInformationProvider.getInstance().getName(de.itemId);
            if (name == null || name.isEmpty() || "null".equals(name)) {
                continue;
            }
            double eff = (double) de.chance * dropRate * chr.getCardRate(de.itemId);
            long ppm = Math.round(eff);
            rows.add(new int[]{de.itemId, (int) Math.min(DROP_DENOMINATOR, Math.max(1, ppm))});
        }
        rows.sort(Comparator.<int[]>comparingInt(row -> -row[1]).thenComparingInt(row -> row[0]));

        LinkedHashMap<Integer, Integer> ret = new LinkedHashMap<>();
        for (int[] row : rows) {
            ret.putIfAbsent(row[0], row[1]);
        }
        return ret;
    }

    public static int[] findBookItems(String query) {
        if (query == null || query.trim().length() < 3) {
            return new int[0];
        }
        Set<Integer> allowed = bookItemIds();
        List<Integer> hits = new ArrayList<>();
        for (Pair<Integer, String> entry : ItemInformationProvider.getItemsIDsFromName(query.trim())) {
            if (!allowed.isEmpty() && !allowed.contains(entry.getLeft())) {
                continue;
            }
            hits.add(entry.getLeft());
            if (hits.size() >= MAX_SEARCH_HITS) {
                break;
            }
        }
        return hits.stream().mapToInt(Integer::intValue).toArray();
    }

    public static LinkedHashMap<Integer, Integer> itemDroppers(Character chr, int itemId) {
        final float itemRate = chr.getCardRate(itemId);

        List<int[]> rows = new ArrayList<>();
        for (int mobId : loadCardedDroppers(itemId)) {
            int baseChance = 0;
            for (MonsterDropEntry de : MonsterInformationProvider.getInstance().retrieveDrop(mobId)) {
                if (de.itemId == itemId && de.chance > baseChance) {
                    baseChance = de.chance;
                }
            }
            if (baseChance <= 0) {
                continue;
            }
            final boolean boss = MonsterInformationProvider.getInstance().isBoss(mobId);
            double eff = (double) baseChance
                    * (boss ? chr.getBossDropRate() : chr.getDropRate()) * itemRate;
            long ppm = Math.round(eff);
            rows.add(new int[]{mobId, (int) Math.min(DROP_DENOMINATOR, Math.max(1, ppm))});
        }
        rows.sort(Comparator.<int[]>comparingInt(row -> -row[1]).thenComparingInt(row -> row[0]));

        LinkedHashMap<Integer, Integer> ret = new LinkedHashMap<>();
        for (int[] row : rows) {
            ret.put(row[0], row[1]);
        }
        return ret;
    }

    private static Set<Integer> bookItemIds() {
        Set<Integer> ids = bookItemIds;
        if (ids != null) {
            return ids;
        }
        Set<Integer> loading = new HashSet<>();
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT DISTINCT d.itemid FROM drop_data d "
                                + "JOIN monstercarddata m ON m.mobid = d.dropperid "
                                + "WHERE d.chance > 0 AND d.itemid > 0");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                loading.add(rs.getInt("itemid"));
            }
        } catch (SQLException e) {
            log.error("Failed to index book droppable items", e);
            return Set.of();
        }
        bookItemIds = loading;
        return loading;
    }

    private static List<Integer> loadCardedDroppers(int itemId) {
        List<Integer> droppers = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(
                        "SELECT DISTINCT d.dropperid FROM drop_data d "
                                + "JOIN monstercarddata m ON m.mobid = d.dropperid "
                                + "WHERE d.itemid = ? AND d.chance > 0")) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    droppers.add(rs.getInt("dropperid"));
                }
            }
        } catch (SQLException e) {
            log.error("Failed to load carded droppers for item {}", itemId, e);
        }
        return droppers;
    }
}
