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
 * 【类型】MobAttackInfo，class，包 {@code org.gms.server.life}。
 *
 * 怪物攻击信息数据类，描述怪物某一攻击动作的属性，包括是否致死攻击、MP燃烧量、异常状态技能及等级、MP消耗等。
 *
 * @author Danny (Leifde)
 */
public class MobAttackInfo {
    /** 是否为致死攻击（无视防御直接击杀） */
    private boolean isDeadlyAttack;
    /** MP燃烧量（攻击时消耗目标MP） */
    private int mpBurn;
    /** 异常状态技能ID */
    private int diseaseSkill;
    /** 异常状态技能等级 */
    private int diseaseLevel;
    /** 攻击消耗的MP */
    private int mpCon;

    /**
     * 构造怪物攻击信息
     * @param mobId    怪物ID
     * @param attackId 攻击动作ID
     */
    public MobAttackInfo(int mobId, int attackId) {
    }

    public void setDeadlyAttack(boolean isDeadlyAttack) {
        this.isDeadlyAttack = isDeadlyAttack;
    }

    public boolean isDeadlyAttack() {
        return isDeadlyAttack;
    }

    public void setMpBurn(int mpBurn) {
        this.mpBurn = mpBurn;
    }

    public int getMpBurn() {
        return mpBurn;
    }

    public void setDiseaseSkill(int diseaseSkill) {
        this.diseaseSkill = diseaseSkill;
    }

    public int getDiseaseSkill() {
        return diseaseSkill;
    }

    public void setDiseaseLevel(int diseaseLevel) {
        this.diseaseLevel = diseaseLevel;
    }

    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    public void setMpCon(int mpCon) {
        this.mpCon = mpCon;
    }

    public int getMpCon() {
        return mpCon;
    }
}