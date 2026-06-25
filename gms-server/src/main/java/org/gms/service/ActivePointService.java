package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.ActivePointDailyClaimDO;
import org.gms.dao.entity.ActivePointMonthlyDO;
import org.gms.dao.entity.DailyActiveProgressDO;
import org.gms.dao.mapper.ActivePointDailyClaimMapper;
import org.gms.dao.mapper.ActivePointMonthlyMapper;
import org.gms.dao.mapper.DailyActiveProgressMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

/**
 * 每日活跃度积分服务：在"每日活跃"13项任务基础上叠加的积分阶梯奖励。
 * <p>
 * 积分=当日 xy_daily_active_progress 按角色求和(每完成1次任意任务记1分，不重复存储)，
 * 本服务只维护每日4个阶梯(1/5/10/20点)与月度3个阶梯(200/300/500点)的领取状态，懒重置约定与
 * {@link DailyActiveService} 一致。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class ActivePointService {

    /** 每日积分阶梯阈值，索引0~3对应阶梯1~4 */
    public static final int[] DAILY_TIER_THRESHOLDS = {1, 5, 10, 20};

    /** 月度积分阶梯阈值，索引0~2对应阶梯1~3 */
    public static final int[] MONTHLY_TIER_THRESHOLDS = {200, 300, 500};

    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final DailyActiveProgressMapper progressMapper;
    private final ActivePointDailyClaimMapper dailyClaimMapper;
    private final ActivePointMonthlyMapper monthlyMapper;

    /**
     * 统计角色当日活跃度积分：xy_daily_active_progress中logDate为今天的全部任务progress之和。
     */
    public int getTodayPoints(Integer characterId) {
        LocalDate today = LocalDate.now();
        List<DailyActiveProgressDO> rows = progressMapper.selectListByQuery(
                QueryWrapper.create().where("character_id = ? and log_date = ?", characterId, today));
        int total = 0;
        for (DailyActiveProgressDO row : rows) {
            if (row.getProgress() != null) {
                total += row.getProgress();
            }
        }
        return total;
    }

    /**
     * 获取（或创建）角色当日的积分阶梯领取记录；若logDate不是今天，懒重置4个阶梯为未领取。
     */
    @Transactional
    public ActivePointDailyClaimDO getOrCreateTodayClaim(Integer characterId) {
        ActivePointDailyClaimDO claim = dailyClaimMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        Date now = new Date();
        LocalDate today = LocalDate.now();
        if (claim == null) {
            claim = ActivePointDailyClaimDO.builder()
                    .characterId(characterId)
                    .logDate(today)
                    .tier1Claimed(0).tier2Claimed(0).tier3Claimed(0).tier4Claimed(0)
                    .createTime(now).updateTime(now)
                    .build();
            dailyClaimMapper.insert(claim);
            return claim;
        }
        if (!today.equals(claim.getLogDate())) {
            claim.setLogDate(today);
            claim.setTier1Claimed(0);
            claim.setTier2Claimed(0);
            claim.setTier3Claimed(0);
            claim.setTier4Claimed(0);
            claim.setUpdateTime(now);
            dailyClaimMapper.update(claim);
        }
        return claim;
    }

    /**
     * 获取（或创建）角色当月的累计积分记录；若yearMonth不是当前月份，懒重置为0并清空3个阶梯。
     */
    @Transactional
    public ActivePointMonthlyDO getOrCreateCurrentMonth(Integer characterId) {
        ActivePointMonthlyDO monthly = monthlyMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        Date now = new Date();
        String currentMonth = LocalDate.now().format(YEAR_MONTH_FORMATTER);
        if (monthly == null) {
            monthly = ActivePointMonthlyDO.builder()
                    .characterId(characterId)
                    .yearMonth(currentMonth)
                    .totalPoints(0)
                    .tier1Claimed(0).tier2Claimed(0).tier3Claimed(0)
                    .createTime(now).updateTime(now)
                    .build();
            monthlyMapper.insert(monthly);
            return monthly;
        }
        if (!currentMonth.equals(monthly.getYearMonth())) {
            monthly.setYearMonth(currentMonth);
            monthly.setTotalPoints(0);
            monthly.setTier1Claimed(0);
            monthly.setTier2Claimed(0);
            monthly.setTier3Claimed(0);
            monthly.setUpdateTime(now);
            monthlyMapper.update(monthly);
        }
        return monthly;
    }

    /**
     * 领取每日积分阶梯奖励：需当日积分达标且该阶梯当日未领取过；领取后把该阶梯阈值叠加进月度累计。
     *
     * @param tierIndex 阶梯下标，0~3对应阶梯1~4(阈值1/5/10/20)
     * @throws IllegalArgumentException 阶梯下标非法、积分未达标、或该阶梯今日已领取
     */
    @Transactional
    public void claimDailyTier(Integer characterId, int tierIndex) {
        if (tierIndex < 0 || tierIndex >= DAILY_TIER_THRESHOLDS.length) {
            throw new IllegalArgumentException("每日积分阶梯下标非法：" + tierIndex);
        }
        int threshold = DAILY_TIER_THRESHOLDS[tierIndex];
        int todayPoints = getTodayPoints(characterId);
        if (todayPoints < threshold) {
            throw new IllegalArgumentException("今日活跃度积分未达标，无法领取");
        }

        ActivePointDailyClaimDO claim = getOrCreateTodayClaim(characterId);
        if (isTierClaimed(claim, tierIndex)) {
            throw new IllegalArgumentException("该积分阶梯今日已领取过");
        }
        setTierClaimed(claim, tierIndex);
        claim.setUpdateTime(new Date());
        dailyClaimMapper.update(claim);

        ActivePointMonthlyDO monthly = getOrCreateCurrentMonth(characterId);
        monthly.setTotalPoints(monthly.getTotalPoints() + threshold);
        monthly.setUpdateTime(new Date());
        monthlyMapper.update(monthly);
    }

    /**
     * 领取月度积分阶梯奖励：需月度累计积分达标且该阶梯本月未领取过。
     *
     * @param tierIndex 阶梯下标，0~2对应阶梯1~3(阈值200/300/500)
     * @throws IllegalArgumentException 阶梯下标非法、积分未达标、或该阶梯本月已领取
     */
    @Transactional
    public void claimMonthlyTier(Integer characterId, int tierIndex) {
        if (tierIndex < 0 || tierIndex >= MONTHLY_TIER_THRESHOLDS.length) {
            throw new IllegalArgumentException("月度积分阶梯下标非法：" + tierIndex);
        }
        ActivePointMonthlyDO monthly = getOrCreateCurrentMonth(characterId);
        if (monthly.getTotalPoints() < MONTHLY_TIER_THRESHOLDS[tierIndex]) {
            throw new IllegalArgumentException("本月累计活跃度积分未达标，无法领取");
        }
        if (isMonthlyTierClaimed(monthly, tierIndex)) {
            throw new IllegalArgumentException("该积分阶梯本月已领取过");
        }
        setMonthlyTierClaimed(monthly, tierIndex);
        monthly.setUpdateTime(new Date());
        monthlyMapper.update(monthly);
    }

    private boolean isTierClaimed(ActivePointDailyClaimDO claim, int tierIndex) {
        Integer value = switch (tierIndex) {
            case 0 -> claim.getTier1Claimed();
            case 1 -> claim.getTier2Claimed();
            case 2 -> claim.getTier3Claimed();
            default -> claim.getTier4Claimed();
        };
        return value != null && value == 1;
    }

    private void setTierClaimed(ActivePointDailyClaimDO claim, int tierIndex) {
        switch (tierIndex) {
            case 0 -> claim.setTier1Claimed(1);
            case 1 -> claim.setTier2Claimed(1);
            case 2 -> claim.setTier3Claimed(1);
            default -> claim.setTier4Claimed(1);
        }
    }

    private boolean isMonthlyTierClaimed(ActivePointMonthlyDO monthly, int tierIndex) {
        Integer value = switch (tierIndex) {
            case 0 -> monthly.getTier1Claimed();
            case 1 -> monthly.getTier2Claimed();
            default -> monthly.getTier3Claimed();
        };
        return value != null && value == 1;
    }

    private void setMonthlyTierClaimed(ActivePointMonthlyDO monthly, int tierIndex) {
        switch (tierIndex) {
            case 0 -> monthly.setTier1Claimed(1);
            case 1 -> monthly.setTier2Claimed(1);
            default -> monthly.setTier3Claimed(1);
        }
    }
}
