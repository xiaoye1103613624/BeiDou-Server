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
 * 【类型】SkillMacro（class），包 `org.gms.client`。
 * <p>技能宏数据类，支持最多绑定3个技能，点击宏可同时释放多个技能</p>
 */
public class SkillMacro {
    /** 技能1 ID */
    private int skill1;
    /** 技能2 ID */
    private int skill2;
    /** 技能3 ID */
    private int skill3;
    /** 宏名称 */
    private final String name;
    /** 是否喊话（0=否, 1=是） */
    private final int shout;
    /** 在宏栏中的位置 */
    private final int position;

    /**
     * 构造技能宏
     * @param skill1 技能1 ID
     * @param skill2 技能2 ID
     * @param skill3 技能3 ID
     * @param name 宏名称
     * @param shout 是否喊话
     * @param position 宏栏位置
     */
    public SkillMacro(int skill1, int skill2, int skill3, String name, int shout, int position) {
        this.skill1 = skill1;
        this.skill2 = skill2;
        this.skill3 = skill3;
        this.name = name;
        this.shout = shout;
        this.position = position;
    }

    public int getSkill1() {
        return skill1;
    }

    public int getSkill2() {
        return skill2;
    }

    public int getSkill3() {
        return skill3;
    }

    public void setSkill1(int skill) {
        skill1 = skill;
    }

    public void setSkill2(int skill) {
        skill2 = skill;
    }

    public void setSkill3(int skill) {
        skill3 = skill;
    }

    public String getName() {
        return name;
    }

    public int getShout() {
        return shout;
    }

    public int getPosition() {
        return position;
    }
}