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

import java.sql.Timestamp;
import java.util.Calendar;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 快递包裹
 * 表示玩家间通过快递系统发送的包裹，可包含物品和/或金币
 * 包裹有30天有效期，过期自动删除
 */
public class DueyPackage {
    /** 发送者名称 */
    private String sender = null;
    /** 包裹中的物品 */
    private Item item = null;
    /** 包裹中的金币 */
    private int mesos = 0;
    /** 留言 */
    private String message = null;
    /** 发送时间戳 */
    private Calendar timestamp;
    /** 包裹ID */
    private int packageId = 0;
    /** 接收者ID */
    private Integer receiverId;

    /**
     * 构造函数（包含物品）
     *
     * @param pId  包裹ID
     * @param item 物品
     */
    public DueyPackage(int pId, Item item) {
        this.item = item;
        packageId = pId;
    }

    /**
     * 构造函数（仅金币包裹）
     *
     * @param pId 包裹ID
     */
    public DueyPackage(int pId) { // Meso only package.
        this.packageId = pId;
    }

    /**
     * 获取发送者
     *
     * @return 发送者名称
     */
    public String getSender() {
        return sender;
    }

    public void setSender(String name) {
        sender = name;
    }

    /**
     * 获取物品
     *
     * @return 包裹中的物品
     */
    public Item getItem() {
        return item;
    }

    public int getMesos() {
        return mesos;
    }

    public void setMesos(int set) {
        mesos = set;
    }

    /**
     * 获取留言
     *
     * @return 留言
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置留言
     *
     * @param m 留言
     */
    public void setMessage(String m) {
        message = m;
    }

    /**
     * 获取包裹ID
     *
     * @return 包裹ID
     */
    public int getPackageId() {
        return packageId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Integer receiverId) {
        this.receiverId = receiverId;
    }

    public long sentTimeInMilliseconds() {
        Calendar ts = timestamp;
        if (ts != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(ts.getTime());
            cal.add(Calendar.MONTH, 1);  // duey representation is in an array of months.

            return cal.getTimeInMillis();
        } else {
            return 0;
        }
    }

    /**
     * 判断是否在配送时间内
     *
     * @return 在配送时间内返回true
     */
    public boolean isDeliveringTime() {
        Calendar ts = timestamp;
        if (ts != null) {
            return ts.getTimeInMillis() >= System.currentTimeMillis();
        } else {
            return false;
        }
    }

    /**
     * 设置发送时间
     *
     * @param ts    时间戳
     * @param quick 是否快速配送（时间往前推1天）
     */
    public void setSentTime(Timestamp ts, boolean quick) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ts.getTime());

        if (quick) {
            // thanks inhyuk for noticing quick delivery packages unavailable to retrieve from the get-go
            if (System.currentTimeMillis() - ts.getTime() < DAYS.toMillis(1)) {
                cal.add(Calendar.DATE, -1);
            }
        }

        this.timestamp = cal;
    }
}