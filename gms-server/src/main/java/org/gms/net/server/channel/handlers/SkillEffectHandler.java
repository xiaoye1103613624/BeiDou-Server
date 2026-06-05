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

import org.gms.client.Client;
import org.gms.constants.skills.Bishop;
import org.gms.constants.skills.Bowmaster;
import org.gms.constants.skills.Brawler;
import org.gms.constants.skills.ChiefBandit;
import org.gms.constants.skills.Corsair;
import org.gms.constants.skills.DarkKnight;
import org.gms.constants.skills.Evan;
import org.gms.constants.skills.FPArchMage;
import org.gms.constants.skills.FPMage;
import org.gms.constants.skills.Gunslinger;
import org.gms.constants.skills.Hero;
import org.gms.constants.skills.ILArchMage;
import org.gms.constants.skills.Marksman;
import org.gms.constants.skills.NightWalker;
import org.gms.constants.skills.Paladin;
import org.gms.constants.skills.ThunderBreaker;
import org.gms.constants.skills.WindArcher;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.PacketCreator;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#SKILL_EFFECT} 封包。
 * 负责处理客户端技能特效广播（暴风箭雨、金属风暴等特殊技能的动作特效）的操作。
 */
public final class SkillEffectHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(SkillEffectHandler.class);

    @Override
    public void handlePacket(InPacket p, Client c) {
        int skillId = p.readInt();
        int level = p.readByte();
        byte flags = p.readByte();
        int speed = p.readByte();
        byte aids = p.readByte();//Mmmk
        // 处理需要广播到地图的技能效果
        switch (skillId) {
            // 魔法师：爆炸/超级火箭/链式闪电
            case FPMage.EXPLOSION:
            case FPArchMage.BIG_BANG:
            case ILArchMage.BIG_BANG:
            case Bishop.BIG_BANG:
            // 弓手：箭座风暴/穿透箭
            case Bowmaster.HURRICANE:
            case Marksman.PIERCING_ARROW:
            // 飞侠：chakra/螺旋注射
            case ChiefBandit.CHAKRA:
            case Brawler.CORKSCREW_BLOW:
            // 海盗：手雷/速射
            case Gunslinger.GRENADE:
            case Corsair.RAPID_FIRE:
            // 箭神：暴风弓
            case WindArcher.HURRICANE:
            // 夜行者：毒炸弹
            case NightWalker.POISON_BOMB:
            case ThunderBreaker.CORKSCREW_BLOW:
            // 圣/暗/英雄：怪物吸引
            case Paladin.MONSTER_MAGNET:
            case DarkKnight.MONSTER_MAGNET:
            case Hero.MONSTER_MAGNET:
            // 龙之子：火焰/冰息
            case Evan.FIRE_BREATH:
            case Evan.ICE_BREATH:
                c.getPlayer().getMap().broadcastMessage(c.getPlayer(), PacketCreator.skillEffect(c.getPlayer(), skillId, level, flags, speed, aids), false);
                return;
            default:
                log.warn("Chr {} entered SkillEffectHandler without being handled using {}", c.getPlayer(), skillId);
                return;
        }
    }
}