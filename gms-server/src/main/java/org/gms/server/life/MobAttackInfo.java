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
package org.gms.server.life;

/**
 * 怪物攻击信息
 * 存储怪物的攻击配置信息，包括致命攻击、MP燃烧、异常状态技能等
 *
 * @author Danny (Leifde)
 */
public class MobAttackInfo {
    /** 是否为致命攻击 */
    private boolean isDeadlyAttack;
    /** MP燃烧量 */
    private int mpBurn;
    /** 异常状态技能ID */
    private int diseaseSkill;
    /** 异常状态技能等级 */
    private int diseaseLevel;
    /** MP消耗 */
    private int mpCon;

    /**
     * 构造怪物攻击信息
     *
     * @param mobId 怪物ID
     * @param attackId 攻击ID
     */
    public MobAttackInfo(int mobId, int attackId) {
    }

    /**
     * 设置是否为致命攻击
     *
     * @param isDeadlyAttack 是否致命攻击
     */
    public void setDeadlyAttack(boolean isDeadlyAttack) {
        this.isDeadlyAttack = isDeadlyAttack;
    }

    /**
     * 是否为致命攻击
     *
     * @return true表示致命攻击
     */
    public boolean isDeadlyAttack() {
        return isDeadlyAttack;
    }

    /**
     * 设置MP燃烧量
     *
     * @param mpBurn MP燃烧量
     */
    public void setMpBurn(int mpBurn) {
        this.mpBurn = mpBurn;
    }

    /**
     * 获取MP燃烧量
     *
     * @return MP燃烧量
     */
    public int getMpBurn() {
        return mpBurn;
    }

    /**
     * 设置异常状态技能ID
     *
     * @param diseaseSkill 异常状态技能ID
     */
    public void setDiseaseSkill(int diseaseSkill) {
        this.diseaseSkill = diseaseSkill;
    }

    /**
     * 获取异常状态技能ID
     *
     * @return 异常状态技能ID
     */
    public int getDiseaseSkill() {
        return diseaseSkill;
    }

    /**
     * 设置异常状态技能等级
     *
     * @param diseaseLevel 异常状态技能等级
     */
    public void setDiseaseLevel(int diseaseLevel) {
        this.diseaseLevel = diseaseLevel;
    }

    /**
     * 获取异常状态技能等级
     *
     * @return 异常状态技能等级
     */
    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    /**
     * 设置MP消耗
     *
     * @param mpCon MP消耗
     */
    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    /**
     * 获取MP消耗
     *
     * @return MP消耗
     */
    public int getMpCon() {
        return mpCon;
    }
}