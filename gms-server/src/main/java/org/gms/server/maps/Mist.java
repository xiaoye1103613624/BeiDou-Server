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
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.constants.skills.BlazeWizard;
import org.gms.constants.skills.Evan;
import org.gms.constants.skills.FPMage;
import org.gms.constants.skills.NightWalker;
import org.gms.constants.skills.Shadower;
import org.gms.net.packet.Packet;
import org.gms.server.StatEffect;
import org.gms.server.life.MobSkill;
import org.gms.server.life.Monster;
import org.gms.util.PacketCreator;

import java.awt.*;

/**
 * 迷雾/毒雾
 * 表示地图上的持续效果区域，如毒雾、火焰、烟雾等，对范围内怪物造成持续伤害
 * 支持怪物技能释放和玩家技能释放两种来源，支持伤害和恢复两种类型
 *
 * @author LaiLaiNoob
 */
public class Mist extends AbstractMapObject {
    /** 迷雾区域矩形边界 */
    private final Rectangle mistPosition;
    /** 迷雾来源玩家（玩家技能） */
    private Character owner = null;
    /** 迷雾来源怪物（怪物技能） */
    private Monster mob = null;
    /** 技能效果（玩家技能） */
    private StatEffect source;
    /** 怪物技能（怪物技能） */
    private MobSkill skill;
    /** 是否怪物技能产生的迷雾 */
    private final boolean isMobMist;
    /** 是否毒雾（造成伤害） */
    private boolean isPoisonMist;
    /** 是否恢复迷雾（恢复HP） */
    private boolean isRecoveryMist;
    /** 伤害延迟（单位：tick） */
    private final int skillDelay;

    public Mist(Rectangle mistPosition, Monster mob, MobSkill skill) {
        this.mistPosition = mistPosition;
        this.mob = mob;
        this.skill = skill;
        isMobMist = true;
        isPoisonMist = true;
        isRecoveryMist = false;
        skillDelay = 0;
    }

    /**
     * 构造玩家技能释放的迷雾
     * 根据技能ID自动判断类型（毒雾/恢复/烟雾）
     *
     * @param mistPosition 迷雾区域
     * @param owner        释放技能的玩家
     * @param source       技能效果
     */
    public Mist(Rectangle mistPosition, Character owner, StatEffect source) {
        this.mistPosition = mistPosition;
        this.owner = owner;
        this.source = source;
        this.skillDelay = 8;
        this.isMobMist = false;
        this.isRecoveryMist = false;
        this.isPoisonMist = false;
        switch (source.getSourceId()) {
            case Evan.RECOVERY_AURA:
                isRecoveryMist = true;
                break;

            case Shadower.SMOKE_SCREEN: // Smoke Screen
                isPoisonMist = false;
                break;

            case FPMage.POISON_MIST: // FP mist
            case BlazeWizard.FLAME_GEAR: // Flame Gear
            case NightWalker.POISON_BOMB: // Poison Bomb
                isPoisonMist = true;
                break;
        }
    }

    @Override
    public MapObjectType getType() {
        return MapObjectType.MIST;
    }

    @Override
    public Point getPosition() {
        return mistPosition.getLocation();
    }

    public Skill getSourceSkill() {
        return SkillFactory.getSkill(source.getSourceId());
    }

    public boolean isMobMist() {
        return isMobMist;
    }

    public boolean isPoisonMist() {
        return isPoisonMist;
    }

    public boolean isRecoveryMist() {
        return isRecoveryMist;
    }

    public int getSkillDelay() {
        return skillDelay;
    }

    public Monster getMobOwner() {
        return mob;
    }

    public Character getOwner() {
        return owner;
    }

    public Rectangle getBox() {
        return mistPosition;
    }

    @Override
    public void setPosition(Point position) {
        throw new UnsupportedOperationException();
    }

    public final Packet makeDestroyData() {
        return PacketCreator.removeMist(getObjectId());
    }

    public final Packet makeSpawnData() {
        if (owner != null) {
            return PacketCreator.spawnMist(getObjectId(), owner.getId(), getSourceSkill().getId(), owner.getSkillLevel(SkillFactory.getSkill(source.getSourceId())), this);
        }
        return PacketCreator.spawnMobMist(getObjectId(), mob.getId(), skill.getId(), this);
    }

    public final Packet makeFakeSpawnData(int level) {
        if (owner != null) {
            return PacketCreator.spawnMist(getObjectId(), owner.getId(), getSourceSkill().getId(), level, this);
        }
        return PacketCreator.spawnMobMist(getObjectId(), mob.getId(), skill.getId(), this);
    }

    @Override
    public void sendSpawnData(Client client) {
        client.sendPacket(makeSpawnData());
    }

    @Override
    public void sendDestroyData(Client client) {
        client.sendPacket(makeDestroyData());
    }

    public boolean makeChanceResult() {
        return source.makeChanceResult();
    }
}