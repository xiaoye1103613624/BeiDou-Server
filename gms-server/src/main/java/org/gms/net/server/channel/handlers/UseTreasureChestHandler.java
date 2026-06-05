package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.game.GameConstants;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.server.ItemInformationProvider;
import org.gms.util.PacketCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * 【Handler】处理 {@link org.gms.net.opcodes.RecvOpcode#USE_TREASUER_CHEST} 封包。
 * 负责处理客户端使用宝箱（金宝箱/银宝箱，使用对应的钥匙开启获取随机奖励）的操作。
 */
public final class UseTreasureChestHandler extends AbstractPacketHandler {

    @Override
    public final void handlePacket(InPacket p, Client c) {

        final short slot = p.readShort();
        final int itemid = p.readInt();

        final Item toUse = c.getPlayer().getInventory(InventoryType.ETC).getItem((byte) slot);
        if (toUse == null || toUse.getQuantity() <= 0 || toUse.getItemId() != itemid) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        if (!c.getPlayer().isAlive()) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }
        int reward;
        int keyIDforRemoval;
        String box;
        String keyname = "";

        // 根据宝箱类型设置奖励和钥匙ID
        switch (toUse.getItemId()) {
            // 4280000: 金宝箱
            case 4280000:
                reward = GameConstants.selectRandomReward(GameConstants.goldrewards);
                keyIDforRemoval = 5490000;
                box = "金宝箱";
                break;
            // 4280001: 银宝箱
            case 4280001:
                reward = GameConstants.selectRandomReward(GameConstants.goldrewards);
                keyIDforRemoval = 5490001;
                box = "银宝箱";
                break;
            default:
                return;
        }

        // 根据奖励物品ID设置奖励数量
        int amount = 1;
        keyname = ItemInformationProvider.getInstance().getName(keyIDforRemoval);
        switch (reward) {
            // 2000004: 获得200个
            case 2000004:
                amount = 200;
                break;
            // 2000005: 获得100个
            case 2000005:
                amount = 100;
                break;
        }

        if (c.getPlayer().getInventory(InventoryType.CASH).countById(keyIDforRemoval) > 0) {
            if (c.getPlayer().getInventory(InventoryType.EQUIP).getNextFreeSlot() > -1 && c.getPlayer().getInventory(InventoryType.USE).getNextFreeSlot() > -1 && c.getPlayer().getInventory(InventoryType.SETUP).getNextFreeSlot() > -1 && c.getPlayer().getInventory(InventoryType.ETC).getNextFreeSlot() > -1) {


                InventoryManipulator.removeFromSlot(c, InventoryType.ETC, (byte) slot, (short) 1, true);
                InventoryManipulator.removeById(c, InventoryType.CASH, keyIDforRemoval, 1, true, false);
                c.sendPacket(PacketCreator.getShowItemGain(reward, (short) amount, true));
                c.sendPacket(PacketCreator.UseTreasureBox(toUse.getItemId()));
            } else {
                c.getPlayer().dropMessage(5, "你有一个栏位满了，请空出来再打开" + box + "！");
                c.sendPacket(PacketCreator.enableActions());
            }
        } else {
            c.getPlayer().dropMessage(5, "请确认是否有" + keyname);
            c.sendPacket(PacketCreator.enableActions());
        }

    }

}