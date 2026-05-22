package org.gms.server;

import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 【类型】ChatLogger（class），包 `org.gms.server`。
 *
 * 聊天日志工具类，在配置开关启用时将玩家的聊天内容记录到日志中，用于聊天监控与审计。
 *
 * @author 萧曵
 */
public class ChatLogger {
    private static final Logger log = LoggerFactory.getLogger(ChatLogger.class);

    /**
     * Log a chat message (if enabled in the config)
     */
    public static void log(Client c, String chatType, String message) {
        if (GameConfig.getServerBoolean("use_enable_chat_log")) {
            log.info("({}) {}: {}", chatType, c.getPlayer().getName(), message);
        }
    }
}
