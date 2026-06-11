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
package org.gms.client;

/**
 * 坐骑
 * 管理玩家的坐骑信息：道具ID、技能ID、疲劳度、经验值和等级
 * 疲劳度越高移动速度越快，需要喂食胡萝卜恢复
 *
 * @author PurpleMadness < Patrick :O >
 */
public class Mount {
    /** 坐骑道具ID */
    private int itemid;
    /** 坐骑技能ID */
    private int skillid;
    /** 疲劳度 */
    private int tiredness;
    /** 坐骑经验值 */
    private int exp;
    /** 坐骑等级 */
    private int level;
    /** 所属玩家 */
    private Character owner;
    /** 是否激活 */
    private boolean active;

    public Mount(Character owner, int id, int skillid) {
        this.itemid = id;
        this.skillid = skillid;
        this.tiredness = 0;
        this.level = 1;
        this.exp = 0;
        this.owner = owner;
        active = true;
    }

    /**
     * 获取道具ID
     */
    public int getItemId() {
        return itemid;
    }

    /**
     * 获取坐骑技能ID
     */
    public int getSkillId() {
        return skillid;
    }

    /**
     * 1902000 - Hog
     * 1902001 - Silver Mane
     * 1902002 - Red Draco
     * 1902005 - Mimiana
     * 1902006 - Mimio
     * 1902007 - Shinjou
     * 1902008 - Frog
     * 1902009 - Ostrich
     * 1902010 - Frog
     * 1902011 - Turtle
     * 1902012 - Yeti
     *
     * @return the id
     */
    public int getId() {
        if (this.itemid < 1903000) {
            return itemid - 1901999;
        }
        return 5;
    }

    /**
     * 获取当前疲劳度
     */
    public int getTiredness() {
        return tiredness;
    }

    /**
     * 获取当前经验值
     */
    public int getExp() {
        return exp;
    }

    /**
     * 获取当前等级
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置疲劳度，不允许负数
     *
     * @param newtiredness 新疲劳度
     */
    public void setTiredness(int newtiredness) {
        this.tiredness = newtiredness;
        if (tiredness < 0) {
            tiredness = 0;
        }
    }

    /**
     * 疲劳度自增1并返回
     *
     * @return 自增后的疲劳度
     */
    public int incrementAndGetTiredness() {
        this.tiredness++;
        return this.tiredness;
    }

    /**
     * 设置经验值
     *
     * @param newexp 新经验值
     */
    public void setExp(int newexp) {
        this.exp = newexp;
    }

    /**
     * 设置等级
     *
     * @param newlevel 新等级
     */
    public void setLevel(int newlevel) {
        this.level = newlevel;
    }

    /**
     * 设置道具ID
     *
     * @param newitemid 新道具ID
     */
    public void setItemId(int newitemid) {
        this.itemid = newitemid;
    }

    /**
     * 设置技能ID
     *
     * @param newskillid 新技能ID
     */
    public void setSkillId(int newskillid) {
        this.skillid = newskillid;
    }

    /**
     * 设置是否激活坐骑
     *
     * @param set true表示激活，false表示未激活
     */
    public void setActive(boolean set) {
        this.active = set;
    }

    /**
     * 判断坐骑是否激活
     *
     * @return true表示激活，false表示未激活
     */
    public boolean isActive() {
        return active;
    }

    /**
     * 清空坐骑信息，取消饥饿度定时器注册
     */
    public void empty() {
        if (owner != null) {
            owner.getClient().getWorldServer().unregisterMountHunger(owner);
        }
        this.owner = null;
    }
}