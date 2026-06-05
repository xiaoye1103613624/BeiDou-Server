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
package org.gms.server.maps;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.SkillFactory;
import org.gms.util.PacketCreator;

import java.awt.*;

/**
 * 【类】Summon（class），包 {@code org.gms.server.maps}。
 * 
 * <p>召唤兽对象，表示玩家技能召唤出的战斗伙伴，支持跟随、攻击、傀儡等行为。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理召唤兽的基本属性</li>
 *   <li>处理召唤兽的生成和销毁</li>
 *   <li>管理召唤兽的移动类型</li>
 *   <li>处理召唤兽的血量变化</li>
 *   <li>识别召唤兽的特殊类型（如傀儡）</li>
 * </ul>
 */
public class Summon extends AbstractAnimatedMapObject {
    /** 召喚獸所有者 */
    private final Character owner; 
    /** 技能等級 */
    private final byte skillLevel; 
    /** 技能ID */
    private final int skill; 
    /** 當前血量 */
    private int hp; 
    /** 移動類型 */
    private final SummonMovementType movementType;

    /**
     * 构造函数：创建召唤兽实例
     * 
     * @param owner 召喚獸所有者
     * @param skill 技能ID
     * @param pos 位置
     * @param movementType 移动类型
     */
    public Summon(Character owner, int skill, Point pos, SummonMovementType movementType) {
        this.owner = owner;
        this.skill = skill;
        this.skillLevel = owner.getSkillLevel(SkillFactory.getSkill(skill));
        if (skillLevel == 0) {
            throw new RuntimeException();
        }

        this.movementType = movementType;
        setPosition(pos);
    }

    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(PacketCreator.spawnSummon(this, false));
    }

    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(PacketCreator.removeSummon(this, true));
    }

    /**
     * 获取召唤兽所有者
     * 
     * @return 召喚獸所有者
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 获取技能ID
     * 
     * @return 技能ID
     */
    public int getSkill() {
        return skill;
    }

    /**
     * 获取当前血量
     * 
     * @return 当前血量
     */
    public int getHP() {
        return hp;
    }

    /**
     * 添加血量
     * 
     * @param delta 血量变化值
     */
    public void addHP(int delta) {
        this.hp += delta;
    }

    /**
     * 获取移动类型
     * 
     * @return 移动类型
     */
    public SummonMovementType getMovementType() {
        return movementType;
    }

    /**
     * 检查是否为静止类型
     * 
     * @return 如果是静止类型则返回true，否则返回false
     */
    public boolean isStationary() {
        return (skill == 3111002 || skill == 3211002 || skill == 5211001 || skill == 13111004);
    }

    /**
     * 获取技能等级
     * 
     * @return 技能等级
     */
    public byte getSkillLevel() {
        return skillLevel;
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.SUMMON;
    }

    /**
     * 检查是否为傀儡类型
     * 
     * @return 如果是傀儡类型则返回true，否则返回false
     */
    public final boolean isPuppet() {
        switch (skill) {
            // 替身术
            case 3111002:
            // 替身术
            case 3211002:
            // 替身术
            case 13111004:
                return true;
        }
        return false;
    }
}