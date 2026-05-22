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
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;
import org.gms.constants.id.MapId;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.server.Trade;
import org.gms.server.maps.MapleMap;
import org.gms.server.maps.Portal;
import org.gms.util.PacketCreator;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.StringJoiner;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#CHANGE_MAP} 封包。
 * 负责处理客户端的换地图操作。
 */
public final class ChangeMapHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(ChangeMapHandler.class);

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        // 正在换图或被封禁时拒绝
        if (chr.isChangingMaps() || chr.isBanned()) {
            if (chr.isChangingMaps()) {
                log.warn(I18nUtil.getLogMessage("ChangeMapHandler.warn.message1"),
                        chr.getName(),      //玩家角色名称
                        chr.getMap().getMapName(),  //当前地图名称
                        chr.getMapId(),             //当前地图ID
                        getFormattedMapListLogMessage(chr.getLastVisitedMapIds(),c)  //最近访问的地图列表
                );
            }

            c.sendPacket(PacketCreator.enableActions());
            return;
        }
        // 交易中换图则取消交易
        if (chr.getTrade() != null) {
            Trade.cancelTrade(chr, Trade.TradeResult.UNSUCCESSFUL_ANOTHER_MAP);
        }

        // 封包长度为0表示从商城进入地图
        boolean enteringMapFromCashShop = p.available() == 0;
        if (enteringMapFromCashShop) {
            enterFromCashShop(c);
            return;
        }

        if (chr.getCashShop().isOpened()) {
            c.disconnect(false, false);
            return;
        }

        try {
            // 读取传送信息：死亡标志(1=死亡, 0=普通传送)、目标地图、传送口名称
            p.readByte(); // 1 = from dying 0 = regular portals
            int targetMapId = p.readInt();
            String portalName = p.readString();
            Portal portal = chr.getMap().getPortal(portalName);
            p.readByte();
            boolean wheel = p.readByte() > 0; // 是否使用命运轮

            // GM追踪玩家
            boolean chasing = p.readByte() == 1 && chr.isGM() && p.available() == 2 * Integer.BYTES;
            if (chasing) {
                chr.setChasing(true);
                chr.setPosition(new Point(p.readInt(), p.readInt()));
            }

            if (targetMapId != -1) {
                // 死亡状态换图：命运轮复活 / 事件复活 / 回城复活
                if (!chr.isAlive()) {
                    MapleMap map = chr.getMap();
                    if (wheel && chr.haveItemWithId(ItemId.WHEEL_OF_FORTUNE, false)) {
                        // thanks lucasziron (lziron) for showing revivePlayer() triggering by Wheel

                        InventoryManipulator.removeById(c, InventoryType.CASH, ItemId.WHEEL_OF_FORTUNE, 1, true, false);
                        chr.sendPacket(PacketCreator.showWheelsLeft(chr.getItemQuantity(ItemId.WHEEL_OF_FORTUNE, false)));

                        chr.updateHp(50);
                        chr.changeMap(map, map.findClosestPlayerSpawnpoint(chr.getPosition()));
                    } else {
                        boolean executeStandardPath = true;
                        if (chr.getEventInstance() != null) {
                            executeStandardPath = chr.getEventInstance().revivePlayer(chr);
                        }
                        if (executeStandardPath) {
                            chr.respawn(map.getReturnMapId());
                        }
                    }
                // 存活状态换图
                } else {
                    // GM可直接传送任意地图
                    if (chr.isGM()) {
                        MapleMap to = chr.getWarpMap(targetMapId);
                        chr.changeMap(to, to.getPortal(0));
                    } else {
                        // 普通玩家根据当前地图区域限制可传送的目标地图
                        final int divi = chr.getMapId() / 100;
                        boolean warp = false;
                        if (divi == 0) {
                            if (targetMapId == 10000) {
                                warp = true;
                            }
                        } else if (divi == 20100) {
                            if (targetMapId == MapId.LITH_HARBOUR) {
                                c.sendPacket(PacketCreator.lockUI(false));
                                c.sendPacket(PacketCreator.disableUI(false));
                                warp = true;
                            }
                        } else if (divi == 9130401) { // Only allow warp if player is already in Intro map, or else = hack
                            if (targetMapId == MapId.EREVE || targetMapId / 100 == 9130401) { // Cygnus introduction
                                warp = true;
                            }
                        } else if (divi == 9140900) { // Aran Introduction
                            if (targetMapId == MapId.ARAN_TUTO_2 || targetMapId == MapId.ARAN_TUTO_3 || targetMapId == MapId.ARAN_TUTO_4 || targetMapId == MapId.ARAN_INTRO) {
                                warp = true;
                            }
                        } else if (divi / 10 == 1020) { // Adventurer movie clip Intro
                            if (targetMapId == 1020000) {
                                warp = true;
                            }
                        } else if (divi / 10 >= 980040 && divi / 10 <= 980045) {
                            if (targetMapId == MapId.WITCH_TOWER_ENTRANCE) {
                                warp = true;
                            }
                        }
                        if (warp) {
                            final MapleMap to = chr.getWarpMap(targetMapId);
                            chr.changeMap(to, to.getPortal(0));
                        }
                    }
                }
            }

            if (portal != null && !portal.getPortalStatus()) {
                c.sendPacket(PacketCreator.blockedMessage(1));
                c.sendPacket(PacketCreator.enableActions());
                return;
            }

            if (chr.getMapId() == MapId.FITNESS_EVENT_LAST) {
                chr.getFitness().resetTimes();
            } else if (chr.getMapId() == MapId.OLA_EVENT_LAST_1 || chr.getMapId() == MapId.OLA_EVENT_LAST_2) {
                chr.getOla().resetTimes();
            }

            // 传送口距离检测（防止远距离传送作弊）
            if (portal != null) {
                if (portal.getPosition().distanceSq(chr.getPosition()) > 400000) {
                    c.sendPacket(PacketCreator.enableActions());
                    return;
                }

                portal.enterPortal(c);
            } else {
                c.sendPacket(PacketCreator.enableActions());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void enterFromCashShop(Client c) {
        final Character chr = c.getPlayer();

        if (!chr.getCashShop().isOpened()) {
            c.disconnect(false, false);
            return;
        }
        String[] socket = Server.getInstance().getInetSocket(c, c.getWorld(), c.getChannel());
        if (socket == null) {
            c.enableCSActions();
            return;
        }
        chr.getCashShop().open(false);

        chr.setSessionTransitionState();
        try {
            c.sendPacket(PacketCreator.getChannelChange(InetAddress.getByName(socket[0]), Integer.parseInt(socket[1])));
        } catch (UnknownHostException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 提供地图ID列表 返回格式化地图名称+地图ID
     * @param MapIds 传入地图ID列表
     * @param c 传入客户端
     * @return [蘑菇村西入口 (0),自由市场 (910000000)]
     */
    private static String getFormattedMapListLogMessage(List<Integer> MapIds,Client c) {
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (int mapid : MapIds) {
            MapleMap map = null;
            try {
                map = c.getChannelServer().getMapFactory().getMap(mapid);
            } catch (Exception ignored) {}
            String MapName = I18nUtil.getLogMessage("SystemRescue.info.map.message1");  //未知地图
            MapName = map != null && !map.getMapName().isEmpty() ? map.getMapName() : MapName;
            sj.add(String.format("%s (%d)", MapName, mapid));
        }
        return sj.toString();
    }
}