/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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

import org.gms.client.inventory.Item;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * 交易市场物品信息
 * 表示MTS交易市场中上架的物品，包含价格、卖家、上架时间等信息
 *
 * @author Traitor
 */
public class MTSItemInfo {
    /** 价格 */
    private final int price;
    /** 物品 */
    private final Item item;
    /** 卖家名称 */
    private final String seller;
    /** 物品ID */
    private final int id;
    /** 下架年份 */
    private final int year;
    /** 下架月份 */
    private final int month;
    /** 下架日 */
    private int day = 1;

    public MTSItemInfo(Item item, int price, int id, int cid, String seller, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate sellEnd = LocalDate.parse(date, formatter);

        this.item = item;
        this.price = price;
        this.seller = seller;
        this.id = id;
        this.year = sellEnd.getYear();
        this.month = sellEnd.getMonthValue();
        this.day = sellEnd.getDayOfMonth();
    }

    /**
     * 获取物品
     *
     * @return 物品
     */
    public Item getItem() {
        return item;
    }

    public int getPrice() {
        return price;
    }

    public int getTaxes() {
        return 100 + price / 10;
    }

    /**
     * 获取交易ID
     *
     * @return 交易ID
     */
    public int getID() {
        return id;
    }

    public long getEndingDate() {
        Calendar now = Calendar.getInstance();
        now.set(year, month - 1, day);
        return now.getTimeInMillis();
    }

    /**
     * 获取卖家名称
     *
     * @return 卖家名称
     */
    public String getSeller() {
        return seller;
    }
}