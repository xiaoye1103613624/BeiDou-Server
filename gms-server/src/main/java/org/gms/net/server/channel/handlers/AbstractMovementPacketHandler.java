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

import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.maps.AnimatedMapObject;
import org.gms.server.movement.AbsoluteLifeMovement;
import org.gms.server.movement.ChangeEquip;
import org.gms.server.movement.JumpDownMovement;
import org.gms.server.movement.LifeMovementFragment;
import org.gms.server.movement.RelativeLifeMovement;
import org.gms.server.movement.TeleportMovement;
import org.gms.exception.EmptyMovementException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 【Handler】抽象基类，为子类提供移动封包解析（{@link #parseMovement}）与位置更新（{@link #updatePosition}）的通用逻辑。
 * <p>
 * 子类分别处理：
 * {@link org.gms.net.opcodes.RecvOpcode#MOVE_PLAYER}（玩家移动）、
 * {@link org.gms.net.opcodes.RecvOpcode#MOVE_PET}（宠物移动）、
 * {@link org.gms.net.opcodes.RecvOpcode#MOVE_SUMMON}（召唤兽移动）、
 * {@link org.gms.net.opcodes.RecvOpcode#MOVE_DRAGON}（龙移动）、
 * {@link org.gms.net.opcodes.RecvOpcode#MOVE_LIFE}（生命体移动）封包。
 * </p>
 */
public abstract class AbstractMovementPacketHandler extends AbstractPacketHandler {
    /** 记录未识别的移动 command 等异常路径。 */
    private static final Logger log = LoggerFactory.getLogger(AbstractMovementPacketHandler.class);

    /**
     * 解析一整段移动指令序列，生成供客户端回放或逻辑校验用的移动片段列表。
     * <p>
     * 先读取指令条数，再对每条 {@code command} 分支解析为绝对位移、相对位移、传送、跳落、换装等具体片段类型。
     *
     * @param p 入站封包，读指针应位于移动数据起点（通常为移动 opcode 后的负载）
     * @return 至少包含一个元素的移动片段列表
     * @throws EmptyMovementException 当指令条数小于 1、未解析出任何片段，或遇到未支持的 {@code command} 时抛出
     */
    protected List<LifeMovementFragment> parseMovement(InPacket p) throws EmptyMovementException {
        List<LifeMovementFragment> res = new ArrayList<>();
        byte numCommands = p.readByte();
        if (numCommands < 1) {
            throw new EmptyMovementException(p);
        }
        for (byte i = 0; i < numCommands; i++) {
            byte command = p.readByte();
            // 根据移动命令类型解析不同的移动数据
            switch (command) {
                // 0/5/17: 绝对移动（普通移动/跳跃移动/漂浮）
                case 0: // normal move
                case 5:
                case 17: { // Float
                    short xpos = p.readShort();
                    short ypos = p.readShort();
                    short xwobble = p.readShort();
                    short ywobble = p.readShort();
                    short fh = p.readShort();
                    byte newstate = p.readByte();
                    short duration = p.readShort();
                    AbsoluteLifeMovement alm = new AbsoluteLifeMovement(command, new Point(xpos, ypos), duration,
                            newstate);
                    alm.setFh(fh);
                    alm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    res.add(alm);
                    break;
                }
                // 1/2/6/12/13/16/18/19/20/22: 相对移动（跳跃/击退/自由跳跃/射箭跳跃/弹簧等）
                case 1: // jump
                case 2: // knockback
                case 6: // fj
                case 12:
                case 13: // Shot-jump-back thing
                case 16: // Float
                case 18:
                case 19: // Springs on maps
                case 20: // Aran Combat Step
                case 22: {
                    short xpos = p.readShort();
                    short ypos = p.readShort();
                    byte newstate = p.readByte();
                    short duration = p.readShort();
                    RelativeLifeMovement rlm = new RelativeLifeMovement(command, new Point(xpos, ypos), duration,
                            newstate);
                    res.add(rlm);
                    break;
                }
                // 3/4/7/8/9/11: 传送移动（瞬移/刺客/冲刺/椅子）
                case 3: // teleport disappear
                case 4: // teleport appear
                case 7: // assaulter
                case 8: // assassinate
                case 9: // rush
                case 11: // chair
                {
                    short xpos = p.readShort();
                    short ypos = p.readShort();
                    short xwobble = p.readShort();
                    short ywobble = p.readShort();
                    byte newstate = p.readByte();
                    TeleportMovement tm = new TeleportMovement(command, new Point(xpos, ypos), newstate);
                    tm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    res.add(tm);
                    break;
                }
                // 14: 跳下（跳过9字节）
                case 14:
                    p.skip(9); // jump down (?)
                    break;
                // 10: 更换装备
                case 10: // Change Equip
                    res.add(new ChangeEquip(p.readByte()));
                    break;
                /*
                 * case 11: { // Chair
                 * short xpos = lea.readShort();
                 * short ypos = lea.readShort();
                 * short fh = lea.readShort();
                 * byte newstate = lea.readByte();
                 * short duration = lea.readShort();
                 * ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos),
                 * duration, newstate);
                 * cm.setFh(fh);
                 * res.add(cm);
                 * break;
                 * }
                 */
                case 15: {
                    short xpos = p.readShort();
                    short ypos = p.readShort();
                    short xwobble = p.readShort();
                    short ywobble = p.readShort();
                    short fh = p.readShort();
                    short ofh = p.readShort();
                    byte newstate = p.readByte();
                    short duration = p.readShort();
                    JumpDownMovement jdm = new JumpDownMovement(command, new Point(xpos, ypos), duration, newstate);
                    jdm.setFh(fh);
                    jdm.setPixelsPerSecond(new Point(xwobble, ywobble));
                    jdm.setOriginFh(ofh);
                    res.add(jdm);
                    break;
                }
                case 21: {// Causes aran to do weird stuff when attacking o.o
                    /*
                     * byte newstate = lea.readByte();
                     * short unk = lea.readShort();
                     * AranMovement am = new AranMovement(command, null, unk, newstate);
                     * res.add(am);
                     */
                    p.skip(3);
                    break;
                }
                default:
                    log.warn("Unhandled case: {}", command);
                    throw new EmptyMovementException(p);
            }
        }

        if (res.isEmpty()) {
            throw new EmptyMovementException(p);
        }
        return res;
    }

    /**
     * 消费与 {@link #parseMovement} 相同编码的移动封包负载，并直接把最终位置、姿态同步到地图对象。
     * <p>
     * 对绝对移动类 command 会设置 {@link AnimatedMapObject#setPosition}；相对类多仅更新姿态并跳过位移字段；
     * 传送、跳落等分支按协议跳过已不需要持久化的中间字段。
     *
     * @param p       入站封包，读指针位于移动数据起点
     * @param target  被更新位置/姿态的可动画对象（怪物、宠物等）
     * @param yOffset 叠加在读取到的 Y 坐标上的像素偏移，用于不同模型锚点或脚底修正
     * @throws EmptyMovementException 当指令条数非法或遇到未支持的 {@code command} 时抛出
     */
    protected void updatePosition(InPacket p, AnimatedMapObject target, int yOffset) throws EmptyMovementException {

        byte numCommands = p.readByte();
        if (numCommands < 1) {
            throw new EmptyMovementException(p);
        }
        for (byte i = 0; i < numCommands; i++) {
            byte command = p.readByte();
            // 根据移动命令类型更新对象位置/姿态
            switch (command) {
                // 0/5/17: 绝对移动（普通移动/跳跃移动/漂浮）- 服务端需要精确位置
                case 0: // normal move
                case 5:
                case 17: { // Float
                    short xpos = p.readShort(); // is signed fine here?
                    short ypos = p.readShort();
                    target.setPosition(new Point(xpos, ypos + yOffset));
                    p.skip(6); // xwobble = lea.readShort(); ywobble = lea.readShort(); fh = lea.readShort();
                    byte newstate = p.readByte();
                    target.setStance(newstate);
                    p.readShort(); // duration
                    break;
                }
                // 1/2/6/12/13/16/18/19/20/22: 相对移动 - 服务端只关心姿态变化
                case 1:
                case 2:
                case 6: // fj
                case 12:
                case 13: // Shot-jump-back thing
                case 16: // Float
                case 18:
                case 19: // Springs on maps
                case 20: // Aran Combat Step
                case 22: {
                    p.skip(4); // xpos = lea.readShort(); ypos = lea.readShort();
                    byte newstate = p.readByte();
                    target.setStance(newstate);
                    p.readShort(); // duration
                    break;
                }
                // 3/4/7/8/9/11: 传送移动 - 瞬移/刺客/冲刺/椅子等
                case 3:
                case 4: // tele... -.-
                case 7: // assaulter
                case 8: // assassinate
                case 9: // rush
                case 11: // chair
                {
                    p.skip(8); // xpos = lea.readShort(); ypos = lea.readShort(); xwobble = lea.readShort();
                               // ywobble = lea.readShort();
                    byte newstate = p.readByte();
                    target.setStance(newstate);
                    break;
                }
                // 14: 跳下（跳过9字节）
                case 14:
                    p.skip(9); // jump down (?)
                    break;
                // 10: 更换装备（服务端忽略）
                case 10: // Change Equip
                    p.readByte();
                    break;
                /*
                 * case 11: { // Chair
                 * short xpos = lea.readShort();
                 * short ypos = lea.readShort();
                 * short fh = lea.readShort();
                 * byte newstate = lea.readByte();
                 * short duration = lea.readShort();
                 * ChairMovement cm = new ChairMovement(command, new Point(xpos, ypos),
                 * duration, newstate);
                 * cm.setFh(fh);
                 * res.add(cm);
                 * break;
                 * }
                 */
                case 15: {
                    // Jump down movement - stance only
                    p.skip(12); // short xpos = lea.readShort(); ypos = lea.readShort(); xwobble =
                                // lea.readShort(); ywobble = lea.readShort(); fh = lea.readShort(); ofh =
                                // lea.readShort();
                    byte newstate = p.readByte();
                    target.setStance(newstate);
                    p.readShort(); // duration
                    break;
                }
                case 21: {// Causes aran to do weird stuff when attacking o.o
                    /*
                     * byte newstate = lea.readByte();
                     * short unk = lea.readShort();
                     * AranMovement am = new AranMovement(command, null, unk, newstate);
                     * res.add(am);
                     */
                    p.skip(3);
                    break;
                }
                default:
                    log.warn("Unhandled Case: {}", command);
                    throw new EmptyMovementException(p);
            }
        }
    }
}