/*
This file is part of the OdinMS Maple Story Server
Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
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
package org.gms.server.life;

/**
 * 怪物属性覆盖
 * 用于在某些场景下覆盖怪物的基础属性值，如HP、MP、经验值
 * ChangeableStats继承此类以支持更灵活的属性调整
 */
public class OverrideMonsterStats {

    /** 生命值（支持超过21.47亿的长整型） */
    public long hp;
    /** 经验值 */
    public int exp;
    /** 魔法值 */
    public int mp;

    /**
     * 默认构造，初始化为最小有效值
     */
    public OverrideMonsterStats() {
        hp = 1;
        exp = 0;
        mp = 0;
    }

    /**
     * 构造覆盖属性
     *
     * @param hp 生命值
     * @param mp 魔法值
     * @param exp 经验值
     * @param change 是否应用变更系数（已废弃）
     */
    public OverrideMonsterStats(long hp, int mp, int exp, boolean change) {
        // 旧有倍率计算已废弃：change ? (hp * 3L / 2L) : hp
        this.hp = hp;
        this.mp = mp;
        this.exp = exp;
    }

    /**
     * 构造覆盖属性（默认应用变更）
     *
     * @param hp 生命值
     * @param mp 魔法值
     * @param exp 经验值
     */
    public OverrideMonsterStats(long hp, int mp, int exp) {
        this(hp, mp, exp, true);
    }

    /**
     * 获取经验值
     *
     * @return 经验值
     */
    public int getExp() {
        return exp;
    }

    /**
     * 设置经验值
     *
     * @param exp 经验值
     */
    public void setOExp(int exp) {
        this.exp = exp;
    }

    /**
     * 获取生命值
     *
     * @return 生命值
     */
    public long getHp() {
        return hp;
    }

    /**
     * 设置生命值
     *
     * @param hp 生命值
     */
    public void setOHp(long hp) {
        this.hp = hp;
    }

    /**
     * 获取魔法值
     *
     * @return 魔法值
     */
    public int getMp() {
        return mp;
    }

    /**
     * 设置魔法值
     *
     * @param mp 魔法值
     */
    public void setOMp(int mp) {
        this.mp = mp;
    }
}