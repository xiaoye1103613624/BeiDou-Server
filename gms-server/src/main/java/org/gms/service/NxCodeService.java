package org.gms.service;

import lombok.AllArgsConstructor;
import org.gms.dao.mapper.NxcodeItemsMapper;
import org.gms.dao.mapper.NxcodeMapper;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * NX兑换码服务
 * <p>管理NX兑换码及其关联物品的过期清理</p>
 */
@Service
@AllArgsConstructor
public class NxCodeService {
    /** NX兑换码数据访问接口 */
    private final NxcodeMapper nxcodeMapper;
    /** NX兑换码物品数据访问接口 */
    private final NxcodeItemsMapper nxcodeItemsMapper;

    /**
     * 清理过期的兑换码
     * <p>删除14天前已过期的兑换码及其关联物品记录</p>
     */
    public void clearExpirations() {
        // 计算14天前的时间戳
        long timeClear = System.currentTimeMillis() - DAYS.toMillis(14);
        // 清理过期的兑换码物品和主记录
        nxcodeItemsMapper.clearExpirations(timeClear);
        nxcodeMapper.clearExpirations(timeClear);
    }

}