package org.gms.service;

import lombok.AllArgsConstructor;
import org.gms.dao.mapper.NxcodeItemsMapper;
import org.gms.dao.mapper.NxcodeMapper;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * NX兑换码服务类，管理NX兑换码及其物品的过期清理。
 */
@Service
@AllArgsConstructor
public class NxCodeService {

    /**
     * NX兑换码数据访问对象
     */
    private final NxcodeMapper nxcodeMapper;

    /**
     * NX兑换码物品数据访问对象
     */
    private final NxcodeItemsMapper nxcodeItemsMapper;

    /**
     * 清理过期14天的兑换码记录。
     * 先清理兑换码物品，再清理兑换码主记录。
     */
    public void clearExpirations() {
        // 计算14天前的时间戳
        long timeClear = System.currentTimeMillis() - DAYS.toMillis(14);
        // 先清理过期的兑换码物品
        nxcodeItemsMapper.clearExpirations(timeClear);
        // 再清理过期的兑换码主记录
        nxcodeMapper.clearExpirations(timeClear);
    }

}