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

/**
 * 武器类型枚举
 * 定义所有武器类型的攻击系数（挥舞/刺击），用于计算伤害
 * 系数越大，攻击速度越快
 */
public enum WeaponType {
    /** 非武器 */
    NOT_A_WEAPON(0),
    /** 单手武器-挥舞 */
    GENERAL1H_SWING(4.4),
    /** 单手武器-刺击 */
    GENERAL1H_STAB(3.2),
    /** 双手武器-挥舞 */
    GENERAL2H_SWING(4.8),
    /** 双手武器-刺击 */
    GENERAL2H_STAB(3.4),
    /** 弓 */
    BOW(3.4),
    /** 飞镖（拳套） */
    CLAW(3.6),
    /** 弩 */
    CROSSBOW(3.6),
    /** 短刀（飞侠） */
    DAGGER_THIEVES(3.6),
    /** 短刀（其他职业） */
    DAGGER_OTHER(4),
    /** 火枪 */
    GUN(3.6),
    /** 指节 */
    KNUCKLE(4.8),
    /** 长柄武器-挥舞 */
    POLE_ARM_SWING(5.0),
    /** 长柄武器-刺击 */
    POLE_ARM_STAB(3.0),
    /** 枪-刺击 */
    SPEAR_STAB(5.0),
    /** 枪-挥舞 */
    SPEAR_SWING(3.0),
    STAFF(3.6),
    SWORD1H(4.0),
    SWORD2H(4.6),
    WAND(3.6);

    private final double damageMultiplier;

    WeaponType(double maxDamageMultiplier) {
        this.damageMultiplier = maxDamageMultiplier;
    }

    public double getMaxDamageMultiplier() {
        return damageMultiplier;
    }
}