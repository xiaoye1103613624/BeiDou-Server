package org.gms.server;

import org.gms.client.Client;
import org.gms.config.GameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 聊天日志
 * 记录玩家聊天消息，通过配置开关控制是否启用
 */
public class ChatLogger {
    private static final Logger log = LoggerFactory.getLogger(ChatLogger.class);

    /**
     * 记录聊天消息（需配置启用）
     *
     * @param c        客户端
     * @param chatType 聊天类型
     * @param message  聊天内容
     */
    public static void log(Client c, String chatType, String message) {
        if (GameConfig.getServerBoolean("use_enable_chat_log")) {
            log.info("({}) {}: {}", chatType, c.getPlayer().getName(), message);
        }
    }
}