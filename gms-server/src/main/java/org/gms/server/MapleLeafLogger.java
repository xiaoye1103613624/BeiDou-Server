package org.gms.server;

import org.gms.client.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 枫叶日志
 * 记录玩家使用枫叶兑换物品或兑换VP枫叶的操作
 */
public class MapleLeafLogger {
    private static final Logger log = LoggerFactory.getLogger(MapleLeafLogger.class);

    /**
     * 记录枫叶操作日志
     *
     * @param player   玩家
     * @param gotPrize 是否获得奖品
     * @param operation 操作描述
     */
    public static void log(Character player, boolean gotPrize, String operation) {
        String action = gotPrize ? " used a maple leaf to buy " + operation : " redeemed " + operation + " VP for a leaf";
        log.info("{} {}", player.getName(), action);
    }
}