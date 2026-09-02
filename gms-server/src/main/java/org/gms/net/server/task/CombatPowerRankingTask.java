package org.gms.net.server.task;

import org.gms.manager.ServerManager;
import org.gms.service.RankingService;
import org.gms.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定期刷新战力 / 装备评分排行榜缓存。
 */
public class CombatPowerRankingTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(CombatPowerRankingTask.class);

    @Override
    public void run() {
        try {
            RankingService service = ServerManager.getApplicationContext().getBean(RankingService.class);
            service.refreshAll();
        } catch (Exception e) {
            log.error(I18nUtil.getLogMessage("RankingService.refresh.error3"), e);
        }
    }
}
