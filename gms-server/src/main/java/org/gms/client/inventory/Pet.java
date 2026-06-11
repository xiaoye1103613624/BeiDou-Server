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
package org.gms.client.inventory;

import org.gms.client.Character;
import org.gms.util.CashIdGenerator;
import org.gms.constants.game.ExpTable;
import org.gms.server.ItemInformationProvider;
import org.gms.server.movement.AbsoluteLifeMovement;
import org.gms.server.movement.LifeMovement;
import org.gms.server.movement.LifeMovementFragment;
import org.gms.util.DatabaseConnection;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 宠物
 * 继承{@link Item}，管理宠物状态：名称、驯服度、等级、饱食度、属性
 * 支持宠物移动、拾取物品、宠物属性加成和数据库持久化
 *
 * @author Matze
 */
public class Pet extends Item {

    /**
     * 宠物属性枚举
     */
    public enum PetAttribute {
        /** 主人速度加成 */
        OWNER_SPEED(0x01);

        private final int i;

        PetAttribute(int i) {
            this.i = i;
        }

        public int getValue() {
            return i;
        }
    }

    /** 宠物名称 */
    private String name;
    /** 宠物唯一ID */
    private int uniqueid;
    /** 宠物驯服度（亲密度） */
    private int tameness = 0;
    /** 宠物等级 */
    private byte level = 1;
    /** 宠物饱食度 */
    private int fullness = 100;
    /** 脚梯ID */
    private int Fh;
    /** 宠物位置坐标 */
    private Point pos;
    /** 宠物动作姿态 */
    private int stance;
    /** 是否已召唤 */
    private boolean summoned;
    /** 宠物属性掩码 */
    private int petAttribute = 0;

    private Pet(int id, short position, int uniqueid) {
        super(id, position, (short) 1);
        this.uniqueid = uniqueid;
        this.pos = new Point(0, 0);
    }

    /**
     * 从数据库加载宠物数据
     * 读取 pets 表的 name/level/closeness/fullness/summoned/flag 字段
     *
     * @param itemid   物品ID
     * @param position 背包槽位
     * @param petid    宠物唯一ID
     * @return 宠物对象，加载失败返回null
     */
    public static Pet loadFromDb(int itemid, short position, int petid) {
        Pet ret = new Pet(itemid, position, petid);
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT name, level, closeness, fullness, summoned, flag FROM pets WHERE petid = ?")) {
            ps.setInt(1, petid);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                ret.setName(rs.getString("name"));
                ret.setTameness(Math.min(rs.getInt("closeness"), 30000));
                ret.setLevel((byte) Math.min(rs.getByte("level"), 30));
                ret.setFullness(Math.min(rs.getInt("fullness"), 100));
                ret.setSummoned(rs.getInt("summoned") == 1);
                ret.setPetAttribute(rs.getInt("flag"));
            }
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从数据库删除宠物（含内存缓存清理和现金ID回收）
     *
     * @param owner 宠物主人
     * @param petid 宠物唯一ID
     */
    public static void deleteFromDb(Character owner, int petid) {
        try {
            // 先清理角色内存中的宠物忽略列表缓存（petignores表通过外键级联清理）
            owner.deletePetExcludedData(petid);
            CashIdGenerator.freeCashId(petid);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 将宠物当前状态持久化到数据库
     */
    public void saveToDb() {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE pets SET name = ?, level = ?, closeness = ?, fullness = ?, summoned = ?, flag = ? WHERE petid = ?")) {
            ps.setString(1, getName());
            ps.setInt(2, getLevel());
            ps.setInt(3, getTameness());
            ps.setInt(4, getFullness());
            ps.setInt(5, isSummoned() ? 1 : 0);
            ps.setInt(6, getPetAttribute());
            ps.setInt(7, getUniqueId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建新宠物（默认等级1、亲密度0、饱食度100）
     *
     * @param itemid 宠物物品ID
     * @return 宠物唯一ID，失败返回-1
     */
    public static int createPet(int itemid) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, summoned, flag) VALUES (?, ?, 1, 0, 100, 0, 0)")) {
            int ret = CashIdGenerator.generateCashId();
            ps.setInt(1, ret);
            ps.setString(2, ItemInformationProvider.getInstance().getName(itemid));
            ps.executeUpdate();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 创建新宠物（可指定等级/亲密度/饱食度）
     *
     * @param itemid   宠物物品ID
     * @param level    初始等级
     * @param tameness 初始亲密度
     * @param fullness 初始饱食度
     * @return 宠物唯一ID，失败返回-1
     */
    public static int createPet(int itemid, byte level, int tameness, int fullness) {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO pets (petid, name, level, closeness, fullness, summoned, flag) VALUES (?, ?, ?, ?, ?, 0, 0)")) {
            int ret = CashIdGenerator.generateCashId();
            ps.setInt(1, ret);
            ps.setString(2, ItemInformationProvider.getInstance().getName(itemid));
            ps.setByte(3, level);
            ps.setInt(4, tameness);
            ps.setInt(5, fullness);
            ps.executeUpdate();
            return ret;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUniqueId() {
        return uniqueid;
    }

    public void setUniqueId(int id) {
        this.uniqueid = id;
    }

    public int getTameness() {
        return tameness;
    }

    public void setTameness(int tameness) {
        this.tameness = tameness;
    }

    public byte getLevel() {
        return level;
    }

    /**
     * 增加宠物亲密度和饱食度（不强制享受）
     * 喂食成功时提升亲密度，饱食度满时下降亲密度
     *
     * @param owner       主人角色
     * @param incTameness 亲密度增量
     * @param incFullness 饱食度增量
     * @param type        食物类型
     */
    public void gainTamenessFullness(Character owner, int incTameness, int incFullness, int type) {
        gainTamenessFullness(owner, incTameness, incFullness, type, false);
    }

    /**
     * 增加宠物亲密度和饱食度（可强制享受）
     * 食物消耗逻辑：饱食度<100时正常增加，饱食度满时下降1点亲密度
     *
     * @param owner       主人角色
     * @param incTameness 亲密度增量
     * @param incFullness 饱食度增量
     * @param type        食物类型
     * @param forceEnjoy  是否强制享受（现金商城物品为true）
     */
    public void gainTamenessFullness(Character owner, int incTameness, int incFullness, int type, boolean forceEnjoy) {
        byte slot = owner.getPetIndex(this);
        boolean enjoyed;

        // 饱食度未满、饱食增量为0（命令触发）或强制享受时，正常增加
        if (fullness < 100 || incFullness == 0 || forceEnjoy) {
            int newFullness = fullness + incFullness;
            if (newFullness > 100) {
                newFullness = 100;
            }
            fullness = newFullness;

            if (incTameness > 0 && tameness < 30000) {
                int newTameness = tameness + incTameness;
                if (newTameness > 30000) {
                    newTameness = 30000;
                }

                tameness = newTameness;
                // 亲密度达到升级阈值时提升等级
                while (newTameness >= ExpTable.getTamenessNeededForLevel(level)) {
                    level += 1;
                    owner.sendPacket(PacketCreator.showOwnPetLevelUp(slot));
                    owner.getMap().broadcastMessage(PacketCreator.showPetLevelUp(owner, slot));
                }
            }

            enjoyed = true;
        } else {
            // 饱食度已满时强行喂养 → 亲密度-1，高等级可能降级
            int newTameness = tameness - 1;
            if (newTameness < 0) {
                newTameness = 0;
            }

            tameness = newTameness;
            if (level > 1 && newTameness < ExpTable.getTamenessNeededForLevel(level - 1)) {
                level -= 1;
            }

            enjoyed = false;
        }

        owner.getMap().broadcastMessage(PacketCreator.petFoodResponse(owner.getId(), slot, enjoyed, false));
        saveToDb();

        Item petz = owner.getInventory(InventoryType.CASH).getItem(getPosition());
        if (petz != null) {
            owner.forceUpdateItem(petz);
        }
    }

    public void setLevel(byte level) {
        this.level = level;
    }

    public int getFullness() {
        return fullness;
    }

    public void setFullness(int fullness) {
        this.fullness = fullness;
    }

    public int getFh() {
        return Fh;
    }

    public void setFh(int Fh) {
        this.Fh = Fh;
    }

    public Point getPos() {
        return pos;
    }

    public void setPos(Point pos) {
        this.pos = pos;
    }

    public int getStance() {
        return stance;
    }

    public void setStance(int stance) {
        this.stance = stance;
    }

    public boolean isSummoned() {
        return summoned;
    }

    public void setSummoned(boolean yes) {
        this.summoned = yes;
    }

    public int getPetAttribute() {
        return this.petAttribute;
    }

    private void setPetAttribute(int flag) {
        this.petAttribute = flag;
    }

    /**
     * 为宠物添加属性标记（位运算OR）
     *
     * @param owner 主人角色
     * @param flag  属性枚举
     */
    public void addPetAttribute(Character owner, PetAttribute flag) {
        this.petAttribute |= flag.getValue();
        saveToDb();

        Item petz = owner.getInventory(InventoryType.CASH).getItem(getPosition());
        if (petz != null) {
            owner.forceUpdateItem(petz);
        }
    }

    /**
     * 移除宠物属性标记（位运算AND NOT）
     *
     * @param owner 主人角色
     * @param flag  属性枚举
     */
    public void removePetAttribute(Character owner, PetAttribute flag) {
        this.petAttribute &= 0xFFFFFFFF ^ flag.getValue();
        saveToDb();

        Item petz = owner.getInventory(InventoryType.CASH).getItem(getPosition());
        if (petz != null) {
            owner.forceUpdateItem(petz);
        }
    }

    /**
     * 查询宠物能否消费指定物品（如宠物食品）
     *
     * @param itemId 物品ID
     * @return Pair<概率, 是否可消费>
     */
    public Pair<Integer, Boolean> canConsume(int itemId) {
        return ItemInformationProvider.getInstance().canPetConsume(this.getItemId(), itemId);
    }

    /**
     * 根据移动包更新宠物位置和姿态
     *
     * @param movement 移动片段列表
     */
    public void updatePosition(List<LifeMovementFragment> movement) {
        for (LifeMovementFragment move : movement) {
            if (move instanceof LifeMovement) {
                if (move instanceof AbsoluteLifeMovement) {
                    this.setPos(move.getPosition());
                }
                this.setStance(((LifeMovement) move).getNewstate());
            }
        }
    }
}