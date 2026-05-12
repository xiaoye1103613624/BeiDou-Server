package org.gms.net.server.coordinator.session;

/**
 * 会话与并发协调「HwidRelevance」。
 * 在多开检测、登录绕过、匹配、事件召回等场景下集中管理跨连接状态。
 */
public record HwidRelevance(String hwid, int relevance) {
    public int getIncrementedRelevance() {
        return relevance < Byte.MAX_VALUE ? relevance + 1 : relevance;
    }
}
