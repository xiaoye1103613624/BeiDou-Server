package org.gms.net.server.coordinator.session;

/**
 * 硬件ID关联度记录
 * 表示硬件ID与账号的关联程度，关联度越高越受信任
 *
 * @param hwid      硬件ID
 * @param relevance 关联度
 */
public record HwidRelevance(String hwid, int relevance) {
    /**
     * 获取递增后的关联度，上限为Byte.MAX_VALUE
     *
     * @return 递增后的关联度
     */
    public int getIncrementedRelevance() {
        return relevance < Byte.MAX_VALUE ? relevance + 1 : relevance;
    }
}