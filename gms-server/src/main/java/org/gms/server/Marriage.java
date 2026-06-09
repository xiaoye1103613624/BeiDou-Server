/*
    This file is part of the HeavenMS MapleStory Server
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.gms.server;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ItemFactory;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.scripting.event.EventInstanceManager;
import org.gms.scripting.event.EventManager;
import org.gms.util.DatabaseConnection;
import org.gms.util.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 婚礼
 * 管理婚礼事件流程，包括订婚戒指交换、愿望清单、结婚证制作等
 * 继承EventInstanceManager，在独立事件实例中运行
 *
 * @author Ronan
 */
public class Marriage extends EventInstanceManager {
    /**
     * 构造函数
     *
     * @param em   事件管理器
     * @param name 事件名称
     */
    public Marriage(EventManager em, String name) {
        super(em, name);
    }

    /**
     * 向配偶赠送物品
     *
     * @param cid 角色ID
     * @return 是否为愿望清单模式（非转账模式）
     */
    public boolean giftItemToSpouse(int cid) {
        return this.getIntProperty("wishlistSelection") == 0;
    }

    /**
     * 获取愿望清单物品列表
     *
     * @param groom true=新郎, false=新娘
     * @return 愿望清单行数组
     */
    public List<String> getWishlistItems(boolean groom) {
        String strItems = this.getProperty(groom ? "groomWishlist" : "brideWishlist");
        if (strItems != null) {
            return Arrays.asList(strItems.split("\r\n"));
        }

        return new LinkedList<>();
    }

    /**
     * 初始化礼物列表
     */
    public void initializeGiftItems() {
        List<Item> groomGifts = new ArrayList<>();
        this.setObjectProperty("groomGiftlist", groomGifts);

        List<Item> brideGifts = new ArrayList<>();
        this.setObjectProperty("brideGiftlist", brideGifts);
    }

    /**
     * 获取礼物列表
     *
     * @param c     客户端
     * @param groom true=新郎, false=新娘
     * @return 礼物列表副本
     */
    public List<Item> getGiftItems(Client c, boolean groom) {
        List<Item> gifts = getGiftItemsList(groom);
        synchronized (gifts) {
            return new LinkedList<>(gifts);
        }
    }

    /**
     * 获取礼物列表（内部方法，直接返回原列表）
     *
     * @param groom true=新郎, false=新娘
     * @return 礼物列表
     */
    private List<Item> getGiftItemsList(boolean groom) {
        return (List<Item>) this.getObjectProperty(groom ? "groomGiftlist" : "brideGiftlist");
    }

    /**
     * 获取指定索引的礼物
     *
     * @param c     客户端
     * @param groom true=新郎, false=新娘
     * @param idx   索引
     * @return 礼物，不存在返回null
     */
    public Item getGiftItem(Client c, boolean groom, int idx) {
        try {
            return getGiftItems(c, groom).get(idx);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * 添加礼物
     *
     * @param groom true=新郎, false=新娘
     * @param item  礼物物品
     */
    public void addGiftItem(boolean groom, Item item) {
        List<Item> gifts = getGiftItemsList(groom);
        synchronized (gifts) {
            gifts.add(item);
        }
    }

    /**
     * 移除礼物
     *
     * @param groom true=新郎, false=新娘
     * @param item  要移除的礼物
     */
    public void removeGiftItem(boolean groom, Item item) {
        List<Item> gifts = getGiftItemsList(groom);
        synchronized (gifts) {
            gifts.remove(item);
        }
    }

    /**
     * 判断角色是否为婚礼新郎
     *
     * @param chr 角色
     * @return true=新郎, false=新娘, null=非参与者
     */
    public Boolean isMarriageGroom(Character chr) {
        Boolean groom = null;
        try {
            int groomid = this.getIntProperty("groomId"), brideid = this.getIntProperty("brideId");
            if (chr.getId() == groomid) {
                groom = true;
            } else if (chr.getId() == brideid) {
                groom = false;
            }
        } catch (NumberFormatException nfe) {
        }

        return groom;
    }

    /**
     * 领取数据库中的婚礼礼物
     *
     * @param c   客户端
     * @param chr 角色
     * @return 领取成功返回true
     */
    public static boolean claimGiftItems(Client c, Character chr) {
        List<Item> gifts = loadGiftItemsFromDb(c, chr.getId());
        if (Inventory.checkSpot(chr, gifts)) {
            try (Connection con = DatabaseConnection.getConnection()) {
                ItemFactory.MARRIAGE_GIFTS.saveItems(new LinkedList<>(), chr.getId(), con);
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }

            for (Item item : gifts) {
                InventoryManipulator.addFromDrop(chr.getClient(), item, false);
            }

            return true;
        }

        return false;
    }

    /**
     * 从数据库加载礼物列表
     *
     * @param c   客户端
     * @param cid 角色ID
     * @return 礼物列表
     */
    public static List<Item> loadGiftItemsFromDb(Client c, int cid) {
        List<Item> items = new LinkedList<>();

        try {
            for (Pair<Item, InventoryType> it : ItemFactory.MARRIAGE_GIFTS.loadItems(cid, false)) {
                items.add(it.getLeft());
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }

        return items;
    }

    /**
     * 保存礼物到数据库
     *
     * @param c     客户端
     * @param groom true=新郎, false=新娘
     * @param cid   角色ID
     */
    public void saveGiftItemsToDb(Client c, boolean groom, int cid) {
        Marriage.saveGiftItemsToDb(c, getGiftItems(c, groom), cid);
    }

    /**
     * 保存礼物列表到数据库（静态方法）
     *
     * @param c         客户端
     * @param giftItems 礼物列表
     * @param cid       角色ID
     */
    public static void saveGiftItemsToDb(Client c, List<Item> giftItems, int cid) {
        List<Pair<Item, InventoryType>> items = new LinkedList<>();
        for (Item it : giftItems) {
            items.add(new Pair<>(it, it.getInventoryType()));
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            ItemFactory.MARRIAGE_GIFTS.saveItems(items, cid, con);
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }
}