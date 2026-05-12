package org.gms.service;

import lombok.AllArgsConstructor;
import org.gms.dao.mapper.NxcodeItemsMapper;
import org.gms.dao.mapper.NxcodeMapper;
import org.springframework.stereotype.Service;

import static java.util.concurrent.TimeUnit.DAYS;

/**
 * 【业务服务】NxCodeService：封装 `service` 相关应用逻辑与数据协作。
 */
@Service
@AllArgsConstructor
public class NxCodeService {
    private final NxcodeMapper nxcodeMapper;
    private final NxcodeItemsMapper nxcodeItemsMapper;

    public void clearExpirations() {
        long timeClear = System.currentTimeMillis() - DAYS.toMillis(14);
        nxcodeItemsMapper.clearExpirations(timeClear);
        nxcodeMapper.clearExpirations(timeClear);
    }

}
