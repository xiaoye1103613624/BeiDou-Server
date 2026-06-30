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
package org.gms.net.server.channel.handlers;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.gms.net.packet.InPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.life.MobSkill;
import org.gms.server.life.MobSkillFactory;
import org.gms.server.life.MobSkillId;
import org.gms.server.life.MobSkillType;
import org.gms.server.life.Monster;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.MapleMap;
import org.gms.util.PacketCreator;
import org.gms.exception.EmptyMovementException;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

/**
 * 怪物移动处理器
 * 处理怪物（Mob/Life）的移动逻辑，包括普通移动、攻击、技能释放等行为
 *
 * @author Danny (Leifde)
 * @author ExtremeDevilz
 * @author Ronan (HeavenMS)
 */
public final class MoveLifeHandler extends AbstractMovementPacketHandler {
    /** 日志记录器 */
    private static final Logger log = LoggerFactory.getLogger(MoveLifeHandler.class);

    /**
     * 处理怪物移动包，解析怪物的活动类型（移动/攻击/技能），更新位置并广播给地图中的玩家
     *
     * @param p 输入数据包，包含怪物的移动和活动数据
     * @param c 客户端连接，包含当前玩家信息
     */
    @Override
    public void handlePacket(InPacket p, Client c) {
        Character player = c.getPlayer();
        MapleMap map = player.getMap();

        if (player.isChangingMaps()) {  // thanks Lame for noticing mob movement shuffle (mob OID on different maps) happening on map transitions
            return;
        }

        int objectid = p.readInt();
        short moveid = p.readShort();
        MapObject mmo = map.getMapObject(objectid);
        if (mmo == null || mmo.getType() != MapObjectType.MONSTER) {
            return;
        }

        Monster monster = (Monster) mmo;
        List<Character> banishPlayers = null;

        byte pNibbles = p.readByte();
        byte rawActivity = p.readByte();
        int skillId = p.readByte() & 0xff;
        int skillLv = p.readByte() & 0xff;
        short pOption = p.readShort();
        p.skip(8);

        if (rawActivity >= 0) {
            rawActivity = (byte) (rawActivity & 0xFF >> 1);
        }

        boolean isAttack = inRangeInclusive(rawActivity, 24, 41);
        boolean isSkill = inRangeInclusive(rawActivity, 42, 59);

        int useSkillId = 0;
        int useSkillLevel = 0;

        if (isSkill) {
            useSkillId = skillId;
            useSkillLevel = skillLv;

            if (monster.hasSkill(useSkillId, useSkillLevel)) {
                // 防御性获取技能数据，WZ可能缺少某些等级，用getMobSkill避免抛异常
                Optional<MobSkillType> mobSkillType = MobSkillType.from(useSkillId);
                if (mobSkillType.isEmpty()) {
                    log.warn("未知怪物技能类型 skillId={}", useSkillId);
                    isSkill = false;
                } else {
                    Optional<MobSkill> optToUse = MobSkillFactory.getMobSkill(mobSkillType.get(), useSkillLevel);
                    if (optToUse.isEmpty()) {
                        log.warn("怪物技能数据缺失 type={} level={}", mobSkillType.get(), useSkillLevel);
                        isSkill = false;
                    } else {
                        MobSkill toUse = optToUse.get();
                        if (monster.canUseSkill(toUse, true)) {
                            int animationTime = MonsterInformationProvider.getInstance().getMobSkillAnimationTime(toUse);
                            if (animationTime > 0 && toUse.getType() != MobSkillType.BANISH) {
                                toUse.applyDelayedEffect(player, monster, true, animationTime);
                            } else {
                                banishPlayers = new LinkedList<>();
                                toUse.applyEffect(player, monster, true, banishPlayers);
                            }
                        }
                    }
                }
            }
        } else {
            int castPos = (rawActivity - 24) / 2;
            int atkStatus = monster.canUseAttack(castPos, isSkill);
            if (atkStatus < 1) {
                rawActivity = -1;
                pOption = 0;
            }
        }

        boolean nextMovementCouldBeSkill = !(isSkill || (pNibbles != 0));
        MobSkill nextUse = null;
        int nextSkillId = 0;
        int nextSkillLevel = 0;
        int mobMp = monster.getMp();
        if (nextMovementCouldBeSkill && monster.hasAnySkill()) {
            MobSkillId skillToUse = monster.getRandomSkill();
            nextSkillId = skillToUse.type().getId();
            nextSkillLevel = skillToUse.level();
            Optional<MobSkill> optNextUse = MobSkillFactory.getMobSkill(skillToUse.type(), skillToUse.level());
            // 防御性处理：技能数据可能在WZ中缺失
            if (optNextUse.isPresent()) {
                MobSkill tempNextUse = optNextUse.get();
                if (monster.canUseSkill(tempNextUse, false)
                        && tempNextUse.getHP() >= (int) (((float) monster.getHp() / monster.getMaxHp()) * 100)
                        && mobMp >= tempNextUse.getMpCon()) {
                    nextUse = tempNextUse;
                }
            } else {
                // 技能数据缺失，不设置nextUse，让怪物跳过此技能
                nextSkillId = 0;
                nextSkillLevel = 0;
                nextUse = null;
            }
        }

        p.readByte();
        p.readInt(); // whatever
        short start_x = p.readShort(); // hmm.. startpos?
        short start_y = p.readShort(); // hmm...
        Point startPos = new Point(start_x, start_y - 2);
        Point serverStartPos = new Point(monster.getPosition());

        Boolean aggro = monster.aggroMoveLifeUpdate(player);
        if (aggro == null) {
            return;
        }

        if (nextUse != null) {
            c.sendPacket(PacketCreator.moveMonsterResponse(objectid, moveid, mobMp, aggro, nextSkillId, nextSkillLevel));
        } else {
            c.sendPacket(PacketCreator.moveMonsterResponse(objectid, moveid, mobMp, aggro));
        }


        try {
            int movementDataStart = p.getPosition();
            updatePosition(p, monster, -2);  // Thanks Doodle & ZERO傑洛 for noticing sponge-based bosses moving out of stage in case of no-offset applied
            long movementDataLength = p.getPosition() - movementDataStart; //how many bytes were read by updatePosition
            p.seek(movementDataStart);

            if (GameConfig.getServerBoolean("use_debug_show_life_move")) {
                log.info("{} rawAct: {}, opt: {}, skillId: {}, skillLv: {}, allowSkill: {}, mobMp: {}",
                        isSkill ? "SKILL" : (isAttack ? "ATTCK" : ""), rawActivity, pOption, useSkillId,
                        useSkillLevel, nextMovementCouldBeSkill, mobMp);
            }

            map.broadcastMessage(player, PacketCreator.moveMonster(objectid, nextMovementCouldBeSkill, rawActivity, useSkillId, useSkillLevel, pOption, startPos, p, movementDataLength), serverStartPos);
            //updatePosition(res, monster, -2); //does this need to be done after the packet is broadcast?
            map.moveMonster(monster, monster.getPosition());
        } catch (EmptyMovementException e) {
        }

        if (banishPlayers != null) {
            for (Character chr : banishPlayers) {
                chr.changeMapBanish(monster.getBanish().getMap(), monster.getBanish().getPortal(), monster.getBanish().getMsg());
            }
        }
    }

    /**
     * 判断值是否在指定范围内（包含边界）
     *
     * @param pVal 待判断的值
     * @param pMin 范围最小值
     * @param pMax 范围最大值
     * @return 如果值在范围内返回true，否则返回false
     */
    private static boolean inRangeInclusive(Byte pVal, Integer pMin, Integer pMax) {
        return !(pVal < pMin) || (pVal > pMax);
    }
}
