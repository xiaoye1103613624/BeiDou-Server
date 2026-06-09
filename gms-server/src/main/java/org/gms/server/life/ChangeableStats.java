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

import org.gms.constants.game.GameConstants;

/**
 * 可变怪物属性
 * 继承OverrideMonsterStats，支持根据等级或属性倍率动态计算怪物的攻击力、防御力、等级等属性
 * 常用于组队任务（PQ）和等级调整场景
 */
public class ChangeableStats extends OverrideMonsterStats {

    /** 物理攻击力 */
    public int watk;
    /** 魔法攻击力 */
    public int matk;
    /** 物理防御力 */
    public int wdef;
    /** 魔法防御力 */
    public int mdef;
    /** 等级 */
    public int level;

    /**
     * 基于已有属性和覆盖属性构造
     *
     * @param stats 怪物基础属性
     * @param ostats 覆盖属性
     */
    public ChangeableStats(MonsterStats stats, OverrideMonsterStats ostats) {
        hp = ostats.getHp();
        exp = ostats.getExp();
        mp = ostats.getMp();
        watk = stats.getPADamage();
        matk = stats.getMADamage();
        wdef = stats.getPDDamage();
        mdef = stats.getMDDamage();
        level = stats.getLevel();
    }

    /**
     * 基于新等级动态计算属性
     * 根据等级比例调整HP、MP、攻击力、防御力等属性
     * BOSS怪物使用原始HP按比例缩放，普通怪物使用标准等级HP公式
     * 防御力上限：BOSS为30，普通怪物为20
     *
     * @param stats 怪物基础属性
     * @param newLevel 新等级
     * @param pqMob 是否为组队任务怪物（PQ怪物属性乘以1.5倍）
     */
    public ChangeableStats(MonsterStats stats, int newLevel, boolean pqMob) {
        final double mod = (double) newLevel / (double) stats.getLevel();
        final double hpRatio = (double) stats.getHp() / (double) stats.getExp();
        final double pqMod = (pqMob ? 1.5 : 1.0);
        hp = Math.min((int) Math.round((!stats.isBoss() ? GameConstants.getMonsterHP(newLevel) : (stats.getHp() * mod)) * pqMod), Integer.MAX_VALUE);
        exp = Math.min((int) Math.round((!stats.isBoss() ? (GameConstants.getMonsterHP(newLevel) / hpRatio) : (stats.getExp())) * pqMod), Integer.MAX_VALUE);
        mp = Math.min((int) Math.round(stats.getMp() * mod * pqMod), Integer.MAX_VALUE);
        watk = Math.min((int) Math.round(stats.getPADamage() * mod), Integer.MAX_VALUE);
        matk = Math.min((int) Math.round(stats.getMADamage() * mod), Integer.MAX_VALUE);
        wdef = Math.min(Math.min(stats.isBoss() ? 30 : 20, (int) Math.round(stats.getPDDamage() * mod)), Integer.MAX_VALUE);
        mdef = Math.min(Math.min(stats.isBoss() ? 30 : 20, (int) Math.round(stats.getMDDamage() * mod)), Integer.MAX_VALUE);
        level = newLevel;
    }

    /**
     * 基于属性倍率计算属性（通过倍率推算新等级）
     *
     * @param stats 怪物基础属性
     * @param statModifier 属性倍率
     * @param pqMob 是否为组队任务怪物
     */
    public ChangeableStats(MonsterStats stats, float statModifier, boolean pqMob) {
        this(stats, (int) (statModifier * stats.getLevel()), pqMob);
    }
}