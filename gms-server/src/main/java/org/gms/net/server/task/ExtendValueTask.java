package org.gms.net.server.task;

import lombok.extern.slf4j.Slf4j;
import org.gms.constants.string.ExtendType;
import org.gms.dao.mapper.ExtendValueMapper;
import org.gms.manager.ServerManager;
import org.gms.util.I18nUtil;

/**
 * 服务器定时任务「ExtendValueTask」。
 * 在 org.gms.net.server.task 下注册执行，用于重置、刷新或持久化与在线玩家相关的数据。
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
