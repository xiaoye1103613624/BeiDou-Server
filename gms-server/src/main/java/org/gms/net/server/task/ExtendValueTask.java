package org.gms.net.server.task;

import lombok.extern.slf4j.Slf4j;
import org.gms.constants.string.ExtendType;
import org.gms.dao.mapper.ExtendValueMapper;
import org.gms.manager.ServerManager;
import org.gms.util.I18nUtil;

/**
 * 扩展值清理定时任务
 * 定期清理过期的扩展值数据，防止数据膨胀
 */
@Slf4j
public class ExtendValueTask implements Runnable {
    @Override
    public void run() {
        ExtendValueMapper extendValueMapper = ServerManager.getApplicationContext().getBean(ExtendValueMapper.class);
        ExtendType.getCleanMap().forEach((key, value) -> {
            try {
                extendValueMapper.clean(key, value);
            } catch (Exception e) {
                log.error(I18nUtil.getLogMessage("ExtendValueTask.error1"), e);
            }
        });
    }
}