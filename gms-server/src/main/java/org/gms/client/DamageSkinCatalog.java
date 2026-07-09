package org.gms.client;

import lombok.extern.slf4j.Slf4j;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.wz.WZFiles;
import org.gms.util.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public final class DamageSkinCatalog {
    public static final long DEFAULT_PRICE_MESOS = 10_000_000L;
    private static final String[] REQUIRED_CHILDREN = {"NoRed0", "NoRed1", "NoCri0", "NoCri1"};
    private static final TreeMap<Integer, Long> prices = new TreeMap<>();
    private static boolean loaded = false;

    private DamageSkinCatalog() {}

    public static synchronized void loadOrSeed() {
        if (loaded) return;
        try {
            int imported = importFromWz();
            loadFromDb();
            loaded = true;
            log.info("DamageSkinCatalog: {} new rows from WZ, {} total in catalog", imported, prices.size());
        } catch (SQLException e) {
            log.error("DamageSkinCatalog load failed", e);
        } catch (Exception e) {
            log.error("DamageSkinCatalog WZ scan failed", e);
        }
    }

    private static int importFromWz() throws SQLException {
        DataProvider dp = DataProviderFactory.getDataProvider(WZFiles.EFFECT);
        if (dp == null) {
            log.warn("DamageSkinCatalog: EFFECT data provider unavailable");
            return 0;
        }
        Data root = dp.getData("BasicEff.img");
        if (root == null) return 0;
        Data node = root.getChildByPath("damageSkin");
        if (node == null) {
            log.warn("DamageSkinCatalog: BasicEff.img/damageSkin not found in WZ");
            return 0;
        }
        int inserted = 0;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "INSERT IGNORE INTO damageskin_catalog (skinId, priceMesos) VALUES (?, ?)")) {
            for (Data skin : node.getChildren()) {
                int id;
                try { id = Integer.parseInt(skin.getName()); } catch (NumberFormatException nfe) { continue; }
                if (id <= 0) continue;
                boolean complete = true;
                for (String child : REQUIRED_CHILDREN) {
                    if (skin.getChildByPath(child) == null) { complete = false; break; }
                }
                if (!complete) continue;
                ps.setInt(1, id);
                ps.setLong(2, DEFAULT_PRICE_MESOS);
                if (ps.executeUpdate() > 0) inserted++;
            }
        }
        return inserted;
    }

    private static void loadFromDb() throws SQLException {
        prices.clear();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                 "SELECT skinId, priceMesos FROM damageskin_catalog ORDER BY skinId");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("skinId");
                long price = rs.getLong("priceMesos");
                if (id != DamageSkinInventory.DEFAULT_SKIN_ID) prices.put(id, price);
            }
        }
    }

    public static long getPrice(int skinId) {
        if (skinId == DamageSkinInventory.DEFAULT_SKIN_ID) return -1L;
        Long p = prices.get(skinId);
        return p == null ? -1L : p;
    }

    public static boolean contains(int skinId) {
        return skinId != DamageSkinInventory.DEFAULT_SKIN_ID && prices.containsKey(skinId);
    }

    public static Map<Integer, Long> getAll() {
        return Collections.unmodifiableMap(prices);
    }
}