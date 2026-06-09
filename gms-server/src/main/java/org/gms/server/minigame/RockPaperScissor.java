package org.gms.server.minigame;

import org.gms.client.Client;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;
import org.gms.util.PacketCreator;
import org.gms.util.Randomizer;

/**
 * 石头剪刀布小游戏
 * 玩家与NPC进行石头剪刀布对决，最多10轮，赢的轮数越多奖励越好
 *
 * @Author Arnah
 * @Website http://Vertisy.ca/
 * @since Aug 15, 2016
 */
public class RockPaperScissor {
    /** 当前轮数 */
    private int round = 0;
    /** 是否可以回答 */
    private boolean ableAnswer = true;
    /** 是否赢了 */
    private boolean win = false;

    public RockPaperScissor(final Client c, final byte mode) {
        c.sendPacket(PacketCreator.rpsMode((byte) (9 + mode)));
        if (mode == 0) {
            c.getPlayer().gainMeso(-1000, true, true, true);
        }
    }

    public final boolean answer(final Client c, final int answer) {
        if (ableAnswer && !win && answer >= 0 && answer <= 2) {
            final int response = Randomizer.nextInt(3);
            if (response == answer) {
                c.sendPacket(PacketCreator.rpsSelection((byte) response, (byte) round));
                // dont do anything. they can still answer once a draw
            } else if ((answer == 0 && response == 2) || (answer == 1 && response == 0) || (answer == 2 && response == 1)) {
                // they win
                c.sendPacket(PacketCreator.rpsSelection((byte) response, (byte) (round + 1)));
                ableAnswer = false;
                win = true;
            } else {
                // they lose
                c.sendPacket(PacketCreator.rpsSelection((byte) response, (byte) -1));
                ableAnswer = false;
            }
            return true;
        }
        reward(c);
        return false;
    }

    /**
     * 超时处理
     * 玩家未在规定时间内回答视为超时
     *
     * @param c 客户端
     * @return true超时有效，false游戏已结束
     */
    public final boolean timeOut(final Client c) {
        if (ableAnswer && !win) {
            ableAnswer = false;
            c.sendPacket(PacketCreator.rpsMode((byte) 0x0A));
            return true;
        }
        reward(c);
        return false;
    }

    public final boolean nextRound(final Client c) {
        if (win) {
            round++;
            if (round < 10) {
                win = false;
                ableAnswer = true;
                c.sendPacket(PacketCreator.rpsMode((byte) 0x0C));
                return true;
            } else {
                round = 10;
            }
        }
        reward(c);
        return false;
    }

    /**
     * 发放奖励
     * 获胜时根据轮数给予对应的证书道具
     *
     * @param c 客户端
     */
    public final void reward(final Client c) {
        if (win) {
            InventoryManipulator.addFromDrop(c, new Item(ItemId.RPS_CERTIFICATE_BASE + round, (short) 0, (short) 1), true);
        }
        c.getPlayer().setRPS(null);
    }

    /**
     * 清理小游戏
     * 发放奖励并发送结束数据包
     *
     * @param c 客户端
     */
    public final void dispose(final Client c) {
        reward(c);
        c.sendPacket(PacketCreator.rpsMode((byte) 0x0D));
    }
}