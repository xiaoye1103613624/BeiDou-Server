/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
                       Matthias Butz <matze@odinms.de>
                       Jan Christian Meyer <vimes@odinms.de>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation. You may not use, modify
    or distribute this program under any other version of the
    GNU Affero General Public License.

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
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.net.server.Server;
import org.gms.server.maps.MiniDungeonInfo;
import org.gms.util.PacketCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 进入商城处理器
 * 处理玩家进入现金商城（Cash Shop）的操作
 *
 * @author Flav
 */
/**
 * 进入商城处理器
 * 处理玩家进入现金商城的操作
 */
public final class EnterCashShopHandler extends AbstractPacketHandler {
    private static final Logger log = LoggerFactory.getLogger(EnterCashShopHandler.class);

    @Override
    public void handlePacket(InPacket p, Client c) {
        try {
            Character mc = c.getPlayer();
            log.info("商城进入流程开始: 玩家={}, 地图={}", mc.getName(), mc.getMapId());

            if (mc.cannotEnterCashShop()) {
                c.sendPacket(PacketCreator.enableActions());
                return;
            }

            if (mc.getEventInstance() != null) {
                c.sendPacket(PacketCreator.serverNotice(5, "Entering Cash Shop or MTS are disabled when registered on an event."));
                c.sendPacket(PacketCreator.enableActions());
                return;
            }

            if (MiniDungeonInfo.isDungeonMap(mc.getMapId())) {
                c.sendPacket(PacketCreator.serverNotice(5, "Changing channels or entering Cash Shop or MTS are disabled when inside a Mini-Dungeon."));
                c.sendPacket(PacketCreator.enableActions());
                return;
            }

            if (mc.getCashShop().isOpened()) {
                return;
            }
            /* 防止极端情况下点券为负数导致无法进入商城 */
            for (int i = 0; i < 3; i++) {
                int quantity = mc.getCashShop().getCash(i);
                if (quantity < 0) {
                    mc.getCashShop().gainCash(i,-quantity);
                }
            }

            mc.closePlayerInteractions();
            mc.closePartySearchInteractions();

            mc.unregisterChairBuff();
            Server.getInstance().getPlayerBuffStorage().addBuffsToStorage(mc.getId(), mc.getAllBuffs());
            Server.getInstance().getPlayerBuffStorage().addDiseasesToStorage(mc.getId(), mc.getAllDiseases());
            mc.setAwayFromChannelWorld();
            mc.notifyMapTransferToPartner(-1);
            mc.removeIncomingInvites();
            mc.cancelAllBuffs(true);
            mc.cancelAllDebuffs();
            mc.cancelBuffExpireTask();
            mc.cancelDiseaseExpireTask();
            mc.cancelSkillCooldownTask();
            mc.cancelExpirationTask();

            mc.forfeitExpirableQuests();
            mc.cancelQuestExpirationTask();

            // 诊断日志：逐个发送封包，定位闪退位置
            log.info("[商城诊断1/5] 发送 openCashShop...");
            c.sendPacket(PacketCreator.openCashShop(c, false));
            log.info("[商城诊断2/5] 发送 showCashInventory (库存{}件)...", mc.getCashShop().getInventorySize());
            c.sendPacket(PacketCreator.showCashInventory(c));
            log.info("[商城诊断3/5] 发送 showGifts...");
            c.sendPacket(PacketCreator.showGifts(mc.getCashShop().loadGifts()));
            log.info("[商城诊断4/5] 发送 showWishList...");
            c.sendPacket(PacketCreator.showWishList(mc, false));
            log.info("[商城诊断5/5] 发送 showCash (NX={}, MP={}, NXPre={})...",
                    mc.getCashShop().getCash(1), mc.getCashShop().getCash(2), mc.getCashShop().getCash(4));
            c.sendPacket(PacketCreator.showCash(mc));

            log.info("商城进入完成: 玩家={}", mc.getName());
            c.getChannelServer().removePlayer(mc);
            mc.getMap().removePlayer(mc);
            mc.getCashShop().open(true);
            mc.saveCharToDB();
        } catch (Exception e) {
            log.error("商城进入流程异常", e);
            e.printStackTrace();
        }
    }
}