package org.gms.config;

import org.gms.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 装备强化配置读取（供 GraalVM JS {@code BeiDouSpecial/装备强化.js} 调用）。
 */
public final class EquipEnhanceManager {

    private static final Logger log = LoggerFactory.getLogger(EquipEnhanceManager.class);

    private EquipEnhanceManager() {
    }

    public static int safeAddStat(int current, int add) {
        long sum = (long) current + add;
        if (sum > Short.MAX_VALUE) {
            return Short.MAX_VALUE;
        }
        if (sum < 0) {
            return 0;
        }
        return (int) sum;
    }

    public static List<ConfigView> queryEnabledConfigs() {
        List<ConfigView> list = new ArrayList<>();
        String sql = """
                SELECT id, item_id, item_name, max_enhance, unique_per_char
                FROM xy_equip_enhance_config
                WHERE enabled = 1
                ORDER BY id
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new ConfigView(
                        rs.getLong("id"),
                        rs.getInt("item_id"),
                        rs.getString("item_name"),
                        rs.getInt("max_enhance"),
                        rs.getInt("unique_per_char")));
            }
        } catch (Exception e) {
            log.error("读取装备强化配置失败", e);
        }
        return list;
    }

    public static LevelView queryLevel(long configId, int starLevel) {
        String sql = """
                SELECT id, config_id, star_level, success_rate, destroy_on_fail, meso_cost,
                       str_add, dex_add, int_add, luk_add, hp_add, mp_add,
                       watk_add, matk_add, wdef_add, mdef_add, acc_add, avoid_add, speed_add, jump_add
                FROM xy_equip_enhance_level
                WHERE config_id = ? AND star_level = ?
                LIMIT 1
                """;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, configId);
            ps.setInt(2, starLevel);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new LevelView(rs);
                }
            }
        } catch (Exception e) {
            log.error("读取装备强化等级失败 configId={} star={}", configId, starLevel, e);
        }
        return null;
    }

    public static List<CostView> queryCosts(long levelId) {
        List<CostView> list = new ArrayList<>();
        String sql = "SELECT item_id, count FROM xy_equip_enhance_cost WHERE level_id = ? ORDER BY id";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setLong(1, levelId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new CostView(rs.getInt("item_id"), rs.getInt("count")));
                }
            }
        } catch (Exception e) {
            log.error("读取装备强化消耗失败 levelId={}", levelId, e);
        }
        return list;
    }

    public static final class ConfigView {
        private final long id;
        private final int itemId;
        private final String itemName;
        private final int maxEnhance;
        private final int uniquePerChar;

        ConfigView(long id, int itemId, String itemName, int maxEnhance, int uniquePerChar) {
            this.id = id;
            this.itemId = itemId;
            this.itemName = itemName;
            this.maxEnhance = maxEnhance;
            this.uniquePerChar = uniquePerChar;
        }

        public long getId() {
            return id;
        }

        public int getItemId() {
            return itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public int getMaxEnhance() {
            return maxEnhance;
        }

        public Integer getUniquePerChar() {
            return uniquePerChar;
        }
    }

    public static final class LevelView {
        private final long id;
        private final long configId;
        private final int starLevel;
        private final int successRate;
        private final int destroyOnFail;
        private final long mesoCost;
        private final int strAdd;
        private final int dexAdd;
        private final int intAdd;
        private final int lukAdd;
        private final int hpAdd;
        private final int mpAdd;
        private final int watkAdd;
        private final int matkAdd;
        private final int wdefAdd;
        private final int mdefAdd;
        private final int accAdd;
        private final int avoidAdd;
        private final int speedAdd;
        private final int jumpAdd;

        LevelView(ResultSet rs) throws java.sql.SQLException {
            this.id = rs.getLong("id");
            this.configId = rs.getLong("config_id");
            this.starLevel = rs.getInt("star_level");
            this.successRate = rs.getInt("success_rate");
            this.destroyOnFail = rs.getInt("destroy_on_fail");
            this.mesoCost = rs.getLong("meso_cost");
            this.strAdd = rs.getInt("str_add");
            this.dexAdd = rs.getInt("dex_add");
            this.intAdd = rs.getInt("int_add");
            this.lukAdd = rs.getInt("luk_add");
            this.hpAdd = rs.getInt("hp_add");
            this.mpAdd = rs.getInt("mp_add");
            this.watkAdd = rs.getInt("watk_add");
            this.matkAdd = rs.getInt("matk_add");
            this.wdefAdd = rs.getInt("wdef_add");
            this.mdefAdd = rs.getInt("mdef_add");
            this.accAdd = rs.getInt("acc_add");
            this.avoidAdd = rs.getInt("avoid_add");
            this.speedAdd = rs.getInt("speed_add");
            this.jumpAdd = rs.getInt("jump_add");
        }

        public long getId() {
            return id;
        }

        public int getSuccessRate() {
            return successRate;
        }

        public Integer getDestroyOnFail() {
            return destroyOnFail;
        }

        public Long getMesoCost() {
            return mesoCost;
        }

        public int getStrAdd() {
            return strAdd;
        }

        public int getDexAdd() {
            return dexAdd;
        }

        public int getIntAdd() {
            return intAdd;
        }

        public int getLukAdd() {
            return lukAdd;
        }

        public int getHpAdd() {
            return hpAdd;
        }

        public int getMpAdd() {
            return mpAdd;
        }

        public int getWatkAdd() {
            return watkAdd;
        }

        public int getMatkAdd() {
            return matkAdd;
        }

        public int getWdefAdd() {
            return wdefAdd;
        }

        public int getMdefAdd() {
            return mdefAdd;
        }

        public int getAccAdd() {
            return accAdd;
        }

        public int getAvoidAdd() {
            return avoidAdd;
        }

        public int getSpeedAdd() {
            return speedAdd;
        }

        public int getJumpAdd() {
            return jumpAdd;
        }
    }

    public static final class CostView {
        private final int itemId;
        private final int count;

        CostView(int itemId, int count) {
            this.itemId = itemId;
            this.count = count;
        }

        public int getItemId() {
            return itemId;
        }

        public int getCount() {
            return count;
        }
    }
}
