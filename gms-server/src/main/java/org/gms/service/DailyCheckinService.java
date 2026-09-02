package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.DailyCheckinRewardDO;
import org.gms.dao.mapper.DailyCheckinRewardMapper;
import org.gms.server.dailycheckin.DailyCheckinRewards;
import org.gms.util.I18nUtil;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 每日签到奖励配置 — DB CRUD + 热重载到内存表。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DailyCheckinService {

    private final DailyCheckinRewardMapper rewardMapper;

    /**
     * 等 Spring / Flyway / I18n 全部就绪后再加载，避免 {@code @PostConstruct} 阶段
     * 触发 {@link I18nUtil} 静态初始化或表尚未迁移导致启动失败。
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            reload();
        } catch (Exception e) {
            try {
                log.error(I18nUtil.getLogMessage("DailyCheckin.reloadFailed"), e);
            } catch (Exception ignored) {
                log.error("[DailyCheckin] Failed to load reward configs on startup", e);
            }
        }
    }

    public List<DailyCheckinRewardDO> listAll() {
        ensureRows();
        return rewardMapper.selectListByQuery(
                QueryWrapper.create().orderBy("day", true));
    }

    @Transactional
    public void save(DailyCheckinRewardDO row) {
        if (row == null || row.getDay() == null) {
            throw new IllegalArgumentException("day required");
        }
        int day = row.getDay();
        if (day < 1 || day > DailyCheckinRewards.CYCLE_DAYS) {
            throw new IllegalArgumentException("day out of range");
        }
        normalize(row);
        DailyCheckinRewardDO existing = rewardMapper.selectOneById(day);
        if (existing == null) {
            rewardMapper.insert(row);
        } else {
            rewardMapper.update(row);
        }
        reload();
    }

    @Transactional
    public void saveAll(List<DailyCheckinRewardDO> rows) {
        if (rows == null || rows.isEmpty()) {
            return;
        }
        for (DailyCheckinRewardDO row : rows) {
            if (row == null || row.getDay() == null) {
                continue;
            }
            int day = row.getDay();
            if (day < 1 || day > DailyCheckinRewards.CYCLE_DAYS) {
                continue;
            }
            normalize(row);
            DailyCheckinRewardDO existing = rewardMapper.selectOneById(day);
            if (existing == null) {
                rewardMapper.insert(row);
            } else {
                rewardMapper.update(row);
            }
        }
        reload();
    }

    public void reload() {
        ensureRows();
        List<DailyCheckinRewardDO> rows = rewardMapper.selectListByQuery(
                QueryWrapper.create().orderBy("day", true));
        DailyCheckinRewards.reload(rows);
        log.info(I18nUtil.getLogMessage("DailyCheckin.reload"), rows.size());
    }

    private void ensureRows() {
        long count = rewardMapper.selectCountByQuery(QueryWrapper.create());
        if (count >= DailyCheckinRewards.CYCLE_DAYS) {
            return;
        }
        List<DailyCheckinRewardDO> toInsert = new ArrayList<>();
        for (int d = 1; d <= DailyCheckinRewards.CYCLE_DAYS; d++) {
            if (rewardMapper.selectOneById(d) == null) {
                toInsert.add(DailyCheckinRewardDO.builder()
                        .day(d)
                        .iconItemId(2000000)
                        .mesos(1)
                        .itemId(0)
                        .itemQty(0)
                        .expireDays(0)
                        .item2Id(0)
                        .item2Qty(0)
                        .item2Expire(0)
                        .slotType(0)
                        .slotCount(0)
                        .build());
            }
        }
        for (DailyCheckinRewardDO row : toInsert) {
            rewardMapper.insert(row);
        }
    }

    private static void normalize(DailyCheckinRewardDO row) {
        if (row.getIconItemId() == null) {
            row.setIconItemId(2000000);
        }
        if (row.getMesos() == null) {
            row.setMesos(0);
        }
        if (row.getItemId() == null) {
            row.setItemId(0);
        }
        if (row.getItemQty() == null) {
            row.setItemQty(0);
        }
        if (row.getExpireDays() == null) {
            row.setExpireDays(0);
        }
        if (row.getItem2Id() == null) {
            row.setItem2Id(0);
        }
        if (row.getItem2Qty() == null) {
            row.setItem2Qty(0);
        }
        if (row.getItem2Expire() == null) {
            row.setItem2Expire(0);
        }
        if (row.getSlotType() == null) {
            row.setSlotType(0);
        }
        if (row.getSlotCount() == null) {
            row.setSlotCount(0);
        }
    }
}
