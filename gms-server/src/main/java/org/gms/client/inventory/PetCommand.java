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
 * 宠物命令
 * 封装宠物的命令技能：宠物ID、技能ID、触发概率和亲密度增量
 *
 * @author Leifde
 */
public class PetCommand {
    /** 宠物ID */
    private final int petId;
    /** 技能ID */
    private final int skillId;
    /** 命令触发概率 */
    private final int prob;
    /** 亲密度增量 */
    private final int inc;

    public PetCommand(int petId, int skillId, int prob, int inc) {
        this.petId = petId;
        this.skillId = skillId;
        this.prob = prob;
        this.inc = inc;
    }

    public int getPetId() {
        return petId;
    }

    public int getSkillId() {
        return skillId;
    }

    public int getProbability() {
        return prob;
    }

    public int getIncrease() {
        return inc;
    }
}