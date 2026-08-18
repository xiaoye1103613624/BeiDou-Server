package org.gms.server.coloring;

import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.ItemFactory;
import org.gms.util.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 七彩棱镜 JDBC 存取：按 inventoryitemid 主键持久化 HSL。
 */
public final class ColoringPrismStorage {
    private static final Logger log = LoggerFactory.getLogger(ColoringPrismStorage.class);

    private ColoringPrismStorage() {
    }

    public static List<ColoringPrismDye> loadByCharacter(int characterId) {
        List<ColoringPrismDye> result = new ArrayList<>();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT inventoryitemid, characterid, itemid, hue, sat, light "
                             + "FROM coloring_prism_dye WHERE characterid = ?")) {
            ps.setInt(1, characterId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(fromRs(rs));
                }
            }
        } catch (SQLException e) {
            log.error("加载染色失败 characterId={}", characterId, e);
        }
        return result;
    }

    public static void upsert(ColoringPrismDye dye) {
        ColoringPrismDye d = dye.clamped();
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "INSERT INTO coloring_prism_dye (inventoryitemid, characterid, itemid, hue, sat, light) "
                             + "VALUES (?, ?, ?, ?, ?, ?) "
                             + "ON DUPLICATE KEY UPDATE characterid=VALUES(characterid), itemid=VALUES(itemid), "
                             + "hue=VALUES(hue), sat=VALUES(sat), light=VALUES(light)")) {
            ps.setInt(1, d.inventoryItemId());
            ps.setInt(2, d.characterId());
            ps.setInt(3, d.itemId());
            ps.setFloat(4, d.hue());
            ps.setFloat(5, d.sat());
            ps.setFloat(6, d.light());
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("写入染色失败 inventoryitemid={}", d.inventoryItemId(), e);
        }
    }

    public static void deleteByInventoryItemId(int inventoryItemId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM coloring_prism_dye WHERE inventoryitemid = ?")) {
            ps.setInt(1, inventoryItemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("按 inventoryitemid 删除染色失败 id={}", inventoryItemId, e);
        }
    }

    public static void deleteByCharacterAndItemId(int characterId, int itemId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "DELETE FROM coloring_prism_dye WHERE characterid = ? AND itemid = ?")) {
            ps.setInt(1, characterId);
            ps.setInt(2, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            log.error("按角色+模板删除染色失败 char={} item={}", characterId, itemId, e);
        }
    }

    /**
     * 解析背包实例 PK。EQUIPPED 的 invType 客户端发 0xFF（有符号 -1）。
     * type 列对应 ItemFactory.INVENTORY（角色背包）。
     */
    public static Optional<Integer> resolveInventoryItemId(int characterId, byte invType, short position, int itemId) {
        InventoryType mit = InventoryType.getByType(invType);
        if (mit == InventoryType.UNDEFINED && invType == (byte) 0xFF) {
            mit = InventoryType.EQUIPPED;
        }
        // 先精确匹配
        Optional<Integer> hit = queryInventoryItemId(characterId, mit.getType(), position, itemId);
        if (hit.isPresent()) {
            return hit;
        }
        // 回退：仅按角色+位置+itemId（忽略 inventorytype 差异）
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT inventoryitemid FROM inventoryitems "
                             + "WHERE type = ? AND characterid = ? AND position = ? AND itemid = ? "
                             + "LIMIT 1")) {
            ps.setInt(1, ItemFactory.INVENTORY.getValue());
            ps.setInt(2, characterId);
            ps.setInt(3, position);
            ps.setInt(4, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            log.error("回退解析 inventoryitemid 失败 char={} pos={} item={}", characterId, position, itemId, e);
        }
        return Optional.empty();
    }

    private static Optional<Integer> queryInventoryItemId(int characterId, byte invType, short position, int itemId) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT inventoryitemid FROM inventoryitems "
                             + "WHERE type = ? AND characterid = ? AND inventorytype = ? AND position = ? AND itemid = ? "
                             + "LIMIT 1")) {
            ps.setInt(1, ItemFactory.INVENTORY.getValue());
            ps.setInt(2, characterId);
            ps.setByte(3, invType);
            ps.setInt(4, position);
            ps.setInt(5, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            log.error("查询 inventoryitemid 失败 char={} invType={} pos={} item={}",
                    characterId, invType, position, itemId, e);
        }
        return Optional.empty();
    }

    private static ColoringPrismDye fromRs(ResultSet rs) throws SQLException {
        return new ColoringPrismDye(
                rs.getInt("inventoryitemid"),
                rs.getInt("characterid"),
                rs.getInt("itemid"),
                rs.getFloat("hue"),
                rs.getFloat("sat"),
                rs.getFloat("light"));
    }
}
