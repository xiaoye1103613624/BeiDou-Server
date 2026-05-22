package org.gms.server;

import org.gms.client.Character;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 【类型】MapleLeafLogger（class），包 `org.gms.server`。
 *
 * 枫叶日志工具类，记录玩家使用枫叶兑换道具或通过道具兑换枫叶的操作，便于追踪商城积分流动。
 *
 * @author 萧曵
 */
public class MapleLeafLogger {
    private static final Logger log = LoggerFactory.getLogger(MapleLeafLogger.class);

    public static void log(Character player, boolean gotPrize, String operation) {
        String action = gotPrize ? " used a maple leaf to buy " + operation : " redeemed " + operation + " VP for a leaf";
        log.info("{} {}", player.getName(), action);
    }
}
