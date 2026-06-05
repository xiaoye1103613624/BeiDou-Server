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
 * 【类】Mist（class），包 {@code org.gms.server.maps}。
 * 
 * <p>迷雾/区域效果系统，表示地图中的持续性范围效果（毒雾、烟雾弹、回复光环等）。
 * 此类管理游戏中的各种区域效果，包括玩家技能产生的效果和怪物技能产生的效果。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>管理区域效果的显示和作用</li>
 *   <li>区分玩家和怪物产生的效果</li>
 *   <li>处理不同类型的效果（毒雾、回复雾等）</li>
 *   <li>控制效果的持续时间和延迟</li>
 * </ul>
 */
public class Mist extends AbstractMapObject {
    /** 迷雾覆盖的区域矩形 */
    private final Rectangle mistPosition; 
    /** 迷雾的创建者（玩家） */
    private Character owner = null; 
    /** 迷雾的创建者（怪物） */
    private Monster mob = null; 
    /** 来源技能效果 */
    private StatEffect source; 
    /** 怪物技能 */
    private MobSkill skill; 
    /** 是否为怪物迷雾 */
    private final boolean isMobMist; 
    /** 是否为毒雾 */
    private boolean isPoisonMist; 
    /** 是否为回复雾 */
    private boolean isRecoveryMist; 
    /** 技能延迟 */
    private final int skillDelay; 

    /**
     * 构造函数：创建怪物迷雾实例
     * 
     * @param mistPosition 迷雾覆盖的区域
     * @param mob 迷雾的创建怪物
     * @param skill 怪物使用的技能
     */
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
     * 构造函数：创建玩家迷雾实例
     * 
     * @param mistPosition 迷雾覆盖的区域
     * @param owner 迷雾的创建玩家
     * @param source 来源技能效果
     */
    public Mist(Rectangle mistPosition, Character owner, StatEffect source) {
        this.mistPosition = mistPosition;
        this.owner = owner;
        this.source = source;
        this.skillDelay = 8;
        this.isMobMist = false;
        this.isRecoveryMist = false;
        this.isPoisonMist = false;
        // 根据技能ID设置迷雾类型
        switch (source.getSourceId()) {
            // 圣光闪耀：恢复迷雾
            case Evan.RECOVERY_AURA:
                isRecoveryMist = true;
                break;

            // 烟雾弹：非中毒迷雾
            case Shadower.SMOKE_SCREEN:
                isPoisonMist = false;
                break;

            // 中毒迷雾：毒雾/火焰齿轮/毒炸弾
            case FPMage.POISON_MIST:
            case BlazeWizard.FLAME_GEAR:
            case NightWalker.POISON_BOMB:
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

    /**
     * 获取来源技能
     * 
     * @return 来源技能
     */
    public Skill getSourceSkill() {
        return SkillFactory.getSkill(source.getSourceId());
    }

    /**
     * 检查是否为怪物迷雾
     * 
     * @return 如果是怪物迷雾则返回true，否则返回false
     */
    public boolean isMobMist() {
        return isMobMist;
    }

    /**
     * 检查是否为毒雾
     * 
     * @return 如果是毒雾则返回true，否则返回false
     */
    public boolean isPoisonMist() {
        return isPoisonMist;
    }

    /**
     * 检查是否为回复雾
     * 
     * @return 如果是回复雾则返回true，否则返回false
     */
    public boolean isRecoveryMist() {
        return isRecoveryMist;
    }

    /**
     * 获取技能延迟
     * 
     * @return 技能延迟
     */
    public int getSkillDelay() {
        return skillDelay;
    }

    /**
     * 获取怪物所有者
     * 
     * @return 怪物所有者
     */
    public Monster getMobOwner() {
        return mob;
    }

    /**
     * 获取玩家所有者
     * 
     * @return 玩家所有者
     */
    public Character getOwner() {
        return owner;
    }

    /**
     * 获取迷雾区域
     * 
     * @return 迷雾区域矩形
     */
    public Rectangle getBox() {
        return mistPosition;
    }

    @Override
    public void setPosition(Point position) {
        throw new UnsupportedOperationException();
    }

    /**
     * 创建销毁数据包
     * 
     * @return 迷雾销毁数据包
     */
    public final Packet makeDestroyData() {
        return PacketCreator.removeMist(getObjectId());
    }

    /**
     * 创建生成数据包
     * 
     * @return 迷雾生成数据包
     */
    public final Packet makeSpawnData() {
        if (owner != null) {
            return PacketCreator.spawnMist(getObjectId(), owner.getId(), getSourceSkill().getId(), owner.getSkillLevel(SkillFactory.getSkill(source.getSourceId())), this);
        }
        return PacketCreator.spawnMobMist(getObjectId(), mob.getId(), skill.getId(), this);
    }

    /**
     * 创建假生成数据包
     * 
     * @param level 技能等级
     * @return 迷雾假生成数据包
     */
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

    /**
     * 执行随机结果判定
     * 
     * @return 如果随机判定成功则返回true，否则返回false
     */
    public boolean makeChanceResult() {
        return source.makeChanceResult();
    }
}