/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gms.client.inventory;

import org.gms.util.DatabaseConnection;
import org.gms.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 物品工厂枚举
 * 管理9种物品存储类型，提供物品的数据库加载和保存功能
 * 使用ReentrantLock数组（分段锁）保护并发写操作，按id哈希分配锁
 *
 * @author Flav
 */
public enum ItemFactory {

    /** 玩家背包（角色维度） */
    INVENTORY(1, false),
    /** 账号仓库（账号维度） */
    STORAGE(2, true),
    /** 冒险家现金仓库（账号维度） */
    CASH_EXPLORER(3, true),
    /** 骑士团现金仓库（账号维度） */
    CASH_CYGNUS(4, true),
    /** 战神现金仓库（账号维度） */
    CASH_ARAN(5, true),
    /** 雇佣商店/个人商店（角色维度） */
    MERCHANT(6, false),
    /** 通用现金仓库（账号维度） */
    CASH_OVERALL(7, true),
    /** 结婚礼物（角色维度） */
    MARRIAGE_GIFTS(8, false),
    /** 快速递送（角色维度） */
    DUEY(9, false);

    /** 存储类型标识，对应数据库inventoryitems.type字段 */
    private final int value;
    /** 是否为账号维度（true=账号，false=角色） */
    private final boolean account;

    /** 分段锁数量，按id取模分配 */
    private static final int lockCount = 400;
    /** 分段锁数组，用于保护并发写操作 */
    private static final Lock[] locks = new Lock[lockCount];

    static {
        for (int i = 0; i < lockCount; i++) {
            locks[i] = new ReentrantLock(true);
        }
    }

    ItemFactory(int value, boolean account) {
        this.value = value;
        this.account = account;
    }

    public int getValue() {
        return value;
    }

    /**
     * 按账号id or 角色id加载物品
     *
     * @param id 账号id or 角色id。注意：如果是仓库，这个id是storageId，而非账号id
     * @param login 是否是已装备
     * @return 物品信息
     * @throws SQLException 查询异常
     */
    public List<Pair<Item, InventoryType>> loadItems(int id, boolean login) throws SQLException {
        if (value != 6) {
            return loadItemsCommon(id, login);
        } else {
            return loadItemsMerchant(id, login);
        }
    }

    /**
     * 保存物品列表到数据库（不含雇佣商店捆绑数）
     *
     * @param items 物品-背包类型配对列表
     * @param id    账号id或角色id
     * @param con   数据库连接
     * @throws SQLException 数据库异常
     */
    public void saveItems(List<Pair<Item, InventoryType>> items, int id, Connection con) throws SQLException {
        saveItems(items, null, id, con);
    }

    /**
     * 保存物品列表到数据库（含雇佣商店捆绑数）
     *
     * @param items       物品-背包类型配对列表
     * @param bundlesList 雇佣商店捆绑数列表（非雇佣商店传null）
     * @param id          账号id或角色id
     * @param con         数据库连接
     * @throws SQLException 数据库异常
     */
    public void saveItems(List<Pair<Item, InventoryType>> items, List<Short> bundlesList, int id, Connection con) throws SQLException {
        // 分段锁替代synchronized，减少并发瓶颈

        if (value != 6) {
            saveItemsCommon(items, id, con);
        } else {
            saveItemsMerchant(items, bundlesList, id, con);
        }
    }

    /**
     * 从数据库查询结果集构造装备对象
     *
     * @param rs 数据库查询结果集
     * @return 装备对象
     * @throws SQLException 数据库异常
     */
    private static Equip loadEquipFromResultSet(ResultSet rs) throws SQLException {
        Equip equip = new Equip(rs.getInt("itemid"), (short) rs.getInt("position"));
        equip.setOwner(rs.getString("owner"));
        equip.setQuantity((short) rs.getInt("quantity"));
        // 基础属性
        equip.setAcc((short) rs.getInt("acc"));
        equip.setAvoid((short) rs.getInt("avoid"));
        equip.setDex((short) rs.getInt("dex"));
        equip.setHands((short) rs.getInt("hands"));
        equip.setHp((short) rs.getInt("hp"));
        equip.setInt((short) rs.getInt("int"));
        equip.setJump((short) rs.getInt("jump"));
        equip.setVicious((short) rs.getInt("vicious"));
        equip.setFlag((short) rs.getInt("flag"));
        equip.setLuk((short) rs.getInt("luk"));
        // 魔法属性
        equip.setMatk((short) rs.getInt("matk"));
        equip.setMdef((short) rs.getInt("mdef"));
        equip.setMp((short) rs.getInt("mp"));
        // 物理属性
        equip.setSpeed((short) rs.getInt("speed"));
        equip.setStr((short) rs.getInt("str"));
        equip.setWatk((short) rs.getInt("watk"));
        equip.setWdef((short) rs.getInt("wdef"));
        // 强化相关
        equip.setUpgradeSlots((byte) rs.getInt("upgradeslots"));
        equip.setLevel(rs.getByte("level"));
        equip.setItemExp(rs.getInt("itemexp"));
        equip.setItemLevel(rs.getByte("itemlevel"));
        // 时效与来源
        equip.setExpiration(rs.getLong("expiration"));
        equip.setGiftFrom(rs.getString("giftFrom"));
        // 特殊系统
        equip.setRingId(rs.getInt("ringid"));
        equip.setEnhanceLevel((short) rs.getInt("enhance_level"));
        equip.setCustomProperties(rs.getString("custom_properties"));

        return equip;
    }

    /**
     * 加载指定账号/角色的已装备物品
     * 联表查询：characters → inventoryitems → inventoryequipment
     *
     * @param id        账号id或角色id
     * @param isAccount true=按账号id查询，false=按角色id查询
     * @param login     是否仅加载装备栏物品（登陆时使用）
     * @return 装备物品-角色id配对列表
     * @throws SQLException 数据库查询异常
     */
    public static List<Pair<Item, Integer>> loadEquippedItems(int id, boolean isAccount, boolean login) throws SQLException {
        List<Pair<Item, Integer>> items = new ArrayList<>();

        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ");
        query.append("(SELECT id, accountid FROM characters) AS accountterm ");
        query.append("RIGHT JOIN ");
        query.append("(SELECT * FROM (`inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`))) AS equipterm");
        query.append(" ON accountterm.id=equipterm.characterid ");
        query.append("WHERE accountterm.`");
        query.append(isAccount ? "accountid" : "characterid");
        query.append("` = ?");
        query.append(login ? " AND `inventorytype` = " + InventoryType.EQUIPPED.getType() : "");

        try (Connection con = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(query.toString())) {
                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Integer cid = rs.getInt("characterid");
                        items.add(new Pair<>(loadEquipFromResultSet(rs), cid));
                    }
                }
            }
        }

        return items;
    }

    /**
     * 通用物品加载（背包、仓库、现金仓库等）
     * 根据枚举的account字段自动选择按账号id或角色id查询
     *
     * @param id    账号id或角色id
     * @param login 是否仅加载装备栏（登陆优化）
     * @return 物品-背包类型配对列表
     * @throws SQLException 数据库查询异常
     */
    private List<Pair<Item, InventoryType>> loadItemsCommon(int id, boolean login) throws SQLException {
        List<Pair<Item, InventoryType>> items = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {
            // 动态构造查询条件：按账号id或角色id
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) WHERE `type` = ? AND `");
            query.append(account ? "accountid" : "characterid").append("` = ?");

            if (login) {
                query.append(" AND `inventorytype` = ").append(InventoryType.EQUIPPED.getType());
            }

            try (PreparedStatement ps = con.prepareStatement(query.toString())) {
                ps.setInt(1, value);
                ps.setInt(2, id);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        InventoryType mit = InventoryType.getByType(rs.getByte("inventorytype"));

                        // 装备类型：从inventoryequipment联表构造Equip对象
                        if (mit.equals(InventoryType.EQUIP) || mit.equals(InventoryType.EQUIPPED)) {
                            items.add(new Pair<>(loadEquipFromResultSet(rs), mit));
                        } else {
                            // 消耗/其他类型：构造普通Item对象
                            int petid = rs.getInt("petid");
                            if (rs.wasNull()) {
                                petid = -1;
                            }

                            Item item = new Item(rs.getInt("itemid"), (byte) rs.getInt("position"), (short) rs.getInt("quantity"), petid);
                            item.setOwner(rs.getString("owner"));
                            item.setExpiration(rs.getLong("expiration"));
                            item.setGiftFrom(rs.getString("giftFrom"));
                            item.setFlag((short) rs.getInt("flag"));
                            items.add(new Pair<>(item, mit));
                        }
                    }
                }
            }
        }
        return items;
    }

    /**
     * 通用物品保存（先删后插策略）
     * 使用分段锁按id哈希保护并发写，步骤：删除旧记录 → 批量插入新记录
     *
     * @param items 物品-背包类型配对列表
     * @param id    账号id或角色id
     * @param con   数据库连接
     * @throws SQLException 数据库异常
     */
    private void saveItemsCommon(List<Pair<Item, InventoryType>> items, int id, Connection con) throws SQLException {
        Lock lock = locks[id % lockCount];
        lock.lock();
        try {
            // 先删除该类型下的所有旧物品记录
            StringBuilder query = new StringBuilder();
            query.append("DELETE `inventoryitems`, `inventoryequipment` FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) WHERE `type` = ? AND `");
            query.append(account ? "accountid" : "characterid").append("` = ?");

            try (PreparedStatement ps = con.prepareStatement(query.toString())) {
                ps.setInt(1, value);
                ps.setInt(2, id);
                ps.executeUpdate();
            }

            // 批量插入新物品记录
            try (PreparedStatement psItem = con.prepareStatement("INSERT INTO `inventoryitems` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                if (!items.isEmpty()) {
                    for (Pair<Item, InventoryType> pair : items) {
                        Item item = pair.getLeft();
                        InventoryType mit = pair.getRight();
                        psItem.setInt(1, value);
                        psItem.setString(2, account ? null : String.valueOf(id));
                        psItem.setString(3, account ? String.valueOf(id) : null);
                        psItem.setInt(4, item.getItemId());
                        psItem.setInt(5, mit.getType());
                        psItem.setInt(6, item.getPosition());
                        psItem.setInt(7, item.getQuantity());
                        psItem.setString(8, item.getOwner());
                        // 设置petId，注意唯一约束冲突
                        psItem.setInt(9, item.getPetId());
                        psItem.setInt(10, item.getFlag());
                        psItem.setLong(11, item.getExpiration());
                        psItem.setString(12, item.getGiftFrom());
                        psItem.executeUpdate();

                        // 装备类型需要额外插入inventoryequipment表
                        if (mit.equals(InventoryType.EQUIP) || mit.equals(InventoryType.EQUIPPED)) {
                            try (PreparedStatement psEquip = con.prepareStatement("INSERT INTO `inventoryequipment` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                                try (ResultSet rs = psItem.getGeneratedKeys()) {
                                    if (!rs.next()) {
                                        throw new RuntimeException("Inserting item failed.");
                                    }

                                    psEquip.setInt(1, rs.getInt(1));
                                }

                                Equip equip = (Equip) item;
                                psEquip.setInt(2, equip.getUpgradeSlots());
                                psEquip.setInt(3, equip.getLevel());
                                psEquip.setInt(4, equip.getStr());
                                psEquip.setInt(5, equip.getDex());
                                psEquip.setInt(6, equip.getInt());
                                psEquip.setInt(7, equip.getLuk());
                                psEquip.setInt(8, equip.getHp());
                                psEquip.setInt(9, equip.getMp());
                                psEquip.setInt(10, equip.getWatk());
                                psEquip.setInt(11, equip.getMatk());
                                psEquip.setInt(12, equip.getWdef());
                                psEquip.setInt(13, equip.getMdef());
                                psEquip.setInt(14, equip.getAcc());
                                psEquip.setInt(15, equip.getAvoid());
                                psEquip.setInt(16, equip.getHands());
                                psEquip.setInt(17, equip.getSpeed());
                                psEquip.setInt(18, equip.getJump());
                                psEquip.setInt(19, 0);
                                psEquip.setInt(20, equip.getVicious());
                                psEquip.setInt(21, equip.getItemLevel());
                                psEquip.setInt(22, equip.getItemExp());
                                psEquip.setInt(23, equip.getRingId());
                                psEquip.setInt(24, equip.getEnhanceLevel());
                                psEquip.setString(25, equip.getCustomProperties());
                                psEquip.executeUpdate();
                            }
                        }
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 雇佣商店物品加载
     * 与通用加载的区别是额外查询inventorymerchant表获取捆绑销售数量
     *
     * @param id    角色id
     * @param login 是否仅加载装备栏
     * @return 物品-背包类型配对列表
     * @throws SQLException 数据库查询异常
     */
    private List<Pair<Item, InventoryType>> loadItemsMerchant(int id, boolean login) throws SQLException {
        List<Pair<Item, InventoryType>> items = new ArrayList<>();

        try (Connection con = DatabaseConnection.getConnection()) {
            StringBuilder query = new StringBuilder();
            query.append("SELECT * FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) WHERE `type` = ? AND `");
            query.append(account ? "accountid" : "characterid").append("` = ?");

            if (login) {
                query.append(" AND `inventorytype` = ").append(InventoryType.EQUIPPED.getType());
            }

            try (PreparedStatement ps = con.prepareStatement(query.toString())) {
                ps.setInt(1, value);
                ps.setInt(2, id);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        // 查询雇佣商店捆绑数（每条物品独立查询）
                        short bundles = 0;
                        try (PreparedStatement psBundle = con.prepareStatement("SELECT `bundles` FROM `inventorymerchant` WHERE `inventoryitemid` = ?")) {
                            psBundle.setInt(1, rs.getInt("inventoryitemid"));

                            try (ResultSet rs2 = psBundle.executeQuery()) {
                                if (rs2.next()) {
                                    bundles = rs2.getShort("bundles");
                                }
                            }
                        }

                        InventoryType mit = InventoryType.getByType(rs.getByte("inventorytype"));

                        if (mit.equals(InventoryType.EQUIP) || mit.equals(InventoryType.EQUIPPED)) {
                            items.add(new Pair<>(loadEquipFromResultSet(rs), mit));
                        } else {
                            // bundles > 0 才有效，防止空记录污染
                            if (bundles > 0) {
                                int petid = rs.getInt("petid");
                                if (rs.wasNull()) {
                                    petid = -1;
                                }

                                // 数量 = 捆绑数 × 单组数量
                                Item item = new Item(rs.getInt("itemid"), (byte) rs.getInt("position"), (short) (bundles * rs.getInt("quantity")), petid);
                                item.setOwner(rs.getString("owner"));
                                item.setExpiration(rs.getLong("expiration"));
                                item.setGiftFrom(rs.getString("giftFrom"));
                                item.setFlag((short) rs.getInt("flag"));
                                items.add(new Pair<>(item, mit));
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    /**
     * 雇佣商店物品保存（三表联动写入）
     * 先清空旧数据，再逐条写入inventoryitems → inventorymerchant →
     * inventoryequipment三张表
     *
     * @param items       物品-背包类型配对列表
     * @param bundlesList 捆绑销售数量列表（与items一一对应）
     * @param id          角色id
     * @param con         数据库连接
     * @throws SQLException 数据库异常
     */
    private void saveItemsMerchant(List<Pair<Item, InventoryType>> items, List<Short> bundlesList, int id, Connection con) throws SQLException {
        Lock lock = locks[id % lockCount];
        lock.lock();
        try {
            // 清空旧雇佣商店记录
            try (PreparedStatement ps = con.prepareStatement("DELETE FROM `inventorymerchant` WHERE `characterid` = ?")) {
                ps.setInt(1, id);
                ps.executeUpdate();
            }

            // 清空旧物品和装备记录
            StringBuilder query = new StringBuilder();
            query.append("DELETE `inventoryitems`, `inventoryequipment` FROM `inventoryitems` LEFT JOIN `inventoryequipment` USING(`inventoryitemid`) WHERE `type` = ? AND `");
            query.append(account ? "accountid" : "characterid").append("` = ?");

            try (PreparedStatement ps = con.prepareStatement(query.toString())) {
                ps.setInt(1, value);
                ps.setInt(2, id);
                ps.executeUpdate();
            }

            // 逐条写入三表
            int i = 0;
            for (Pair<Item, InventoryType> pair : items) {
                final Item item = pair.getLeft();
                final Short bundles = bundlesList.get(i);
                final InventoryType mit = pair.getRight();
                i++;

                final int genKey;
                // 写入inventoryitems表，获取自增主键
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO `inventoryitems` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, value);
                    ps.setString(2, account ? null : String.valueOf(id));
                    ps.setString(3, account ? String.valueOf(id) : null);
                    ps.setInt(4, item.getItemId());
                    ps.setInt(5, mit.getType());
                    ps.setInt(6, item.getPosition());
                    ps.setInt(7, item.getQuantity());
                    ps.setString(8, item.getOwner());
                    ps.setInt(9, item.getPetId());
                    ps.setInt(10, item.getFlag());
                    ps.setLong(11, item.getExpiration());
                    ps.setString(12, item.getGiftFrom());
                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new RuntimeException("Inserting item failed.");
                        }

                        genKey = rs.getInt(1);
                    }
                }

                // 写入inventorymerchant表（捆绑数）
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO `inventorymerchant` VALUES (DEFAULT, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, genKey);
                    ps.setInt(2, id);
                    ps.setInt(3, bundles);
                    ps.executeUpdate();
                }

                // 装备类型额外写入inventoryequipment表
                if (mit.equals(InventoryType.EQUIP) || mit.equals(InventoryType.EQUIPPED)) {
                    try (PreparedStatement ps = con.prepareStatement("INSERT INTO `inventoryequipment` VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        ps.setInt(1, genKey);

                        Equip equip = (Equip) item;
                        ps.setInt(2, equip.getUpgradeSlots());
                        ps.setInt(3, equip.getLevel());
                        ps.setInt(4, equip.getStr());
                        ps.setInt(5, equip.getDex());
                        ps.setInt(6, equip.getInt());
                        ps.setInt(7, equip.getLuk());
                        ps.setInt(8, equip.getHp());
                        ps.setInt(9, equip.getMp());
                        ps.setInt(10, equip.getWatk());
                        ps.setInt(11, equip.getMatk());
                        ps.setInt(12, equip.getWdef());
                        ps.setInt(13, equip.getMdef());
                        ps.setInt(14, equip.getAcc());
                        ps.setInt(15, equip.getAvoid());
                        ps.setInt(16, equip.getHands());
                        ps.setInt(17, equip.getSpeed());
                        ps.setInt(18, equip.getJump());
                        ps.setInt(19, 0);
                        ps.setInt(20, equip.getVicious());
                        ps.setInt(21, equip.getItemLevel());
                        ps.setInt(22, equip.getItemExp());
                        ps.setInt(23, equip.getRingId());
                        ps.setInt(24, equip.getEnhanceLevel());
                        ps.setString(25, equip.getCustomProperties());
                        ps.executeUpdate();
                    }
                }
            }
        } finally {
            lock.unlock();
        }
    }
}