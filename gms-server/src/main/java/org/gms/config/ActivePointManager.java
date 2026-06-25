package org.gms.config;

import org.gms.dao.entity.ActivePointDailyClaimDO;
import org.gms.dao.entity.ActivePointMonthlyDO;
import org.gms.manager.ServerManager;
import org.gms.service.ActivePointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 每日活跃度积分静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 * <p>
 * 每日阶梯奖励：阈值1/5/10/20点 -> 100/200/300抵用券(CashShop.MAPLE_POINT=2)/600点券(CashShop.NX_CREDIT=1)。
 * 月度阶梯奖励：阈值200/300/500点 -> 2000/6000/10000点券(CashShop.NX_CREDIT=1)。
 * 货币发放由脚本侧在领取成功后自行调用 {@code cm.getPlayer().getCashShop().gainCash(cashType, amount)}
 * (与每日活跃13项任务奖励发放约定一致)，本门面只负责积分统计与阶梯领取状态校验。
 * </p>
 */
public class ActivePointManager {

    private static final Logger log = LoggerFactory.getLogger(ActivePointManager.class);

    /** CashShop货币类型：点券 */
    private static final int CASH_NX_CREDIT = 1;
    /** CashShop货币类型：抵用券 */
    private static final int CASH_MAPLE_POINT = 2;

    /** 每日阶梯奖励：{cashType, amount}，下标0~3对应阶梯1~4(阈值1/5/10/20点) */
    private static final int[][] DAILY_TIER_REWARDS = {
            {CASH_MAPLE_POINT, 100},
            {CASH_MAPLE_POINT, 200},
            {CASH_MAPLE_POINT, 300},
            {CASH_NX_CREDIT, 600},
    };

    /** 月度阶梯奖励：{cashType, amount}，下标0~2对应阶梯1~3(阈值200/300/500点) */
    private static final int[][] MONTHLY_TIER_REWARDS = {
            {CASH_NX_CREDIT, 2000},
            {CASH_NX_CREDIT, 6000},
            {CASH_NX_CREDIT, 10000},
    };

    private ActivePointManager() {}

    private static ActivePointService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(ActivePointService.class);
    }

    /**
     * 查询角色当前积分总览，供NPC脚本展示。
     *
     * @return {todayPoints, dailyThresholds, dailyClaimed(boolean[4]),
     *          monthlyPoints, monthlyThresholds, monthlyClaimed(boolean[3])}
     */
    public static Map<String, Object> getOverview(Integer characterId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            ActivePointService service = getService();
            int todayPoints = service.getTodayPoints(characterId);
            ActivePointDailyClaimDO dailyClaim = service.getOrCreateTodayClaim(characterId);
            ActivePointMonthlyDO monthly = service.getOrCreateCurrentMonth(characterId);

            result.put("todayPoints", todayPoints);
            result.put("dailyThresholds", ActivePointService.DAILY_TIER_THRESHOLDS);
            result.put("dailyClaimed", new boolean[]{
                    dailyClaim.getTier1Claimed() != null && dailyClaim.getTier1Claimed() == 1,
                    dailyClaim.getTier2Claimed() != null && dailyClaim.getTier2Claimed() == 1,
                    dailyClaim.getTier3Claimed() != null && dailyClaim.getTier3Claimed() == 1,
                    dailyClaim.getTier4Claimed() != null && dailyClaim.getTier4Claimed() == 1,
            });

            result.put("monthlyPoints", monthly.getTotalPoints());
            result.put("monthlyThresholds", ActivePointService.MONTHLY_TIER_THRESHOLDS);
            result.put("monthlyClaimed", new boolean[]{
                    monthly.getTier1Claimed() != null && monthly.getTier1Claimed() == 1,
                    monthly.getTier2Claimed() != null && monthly.getTier2Claimed() == 1,
                    monthly.getTier3Claimed() != null && monthly.getTier3Claimed() == 1,
            });
        } catch (Exception e) {
            log.error("查询活跃度积分总览失败", e);
        }
        return result;
    }

    /**
     * 领取每日积分阶梯奖励。
     *
     * @param tierIndex 阶梯下标，0~3对应阶梯1~4
     * @return {success, message, cashType, amount}
     */
    public static Map<String, Object> claimDailyTier(Integer characterId, int tierIndex) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            getService().claimDailyTier(characterId, tierIndex);
            int[] reward = DAILY_TIER_REWARDS[tierIndex];
            result.put("success", true);
            result.put("message", "领取成功");
            result.put("cashType", reward[0]);
            result.put("amount", reward[1]);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 领取月度积分阶梯奖励。
     *
     * @param tierIndex 阶梯下标，0~2对应阶梯1~3
     * @return {success, message, cashType, amount}
     */
    public static Map<String, Object> claimMonthlyTier(Integer characterId, int tierIndex) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            getService().claimMonthlyTier(characterId, tierIndex);
            int[] reward = MONTHLY_TIER_REWARDS[tierIndex];
            result.put("success", true);
            result.put("message", "领取成功");
            result.put("cashType", reward[0]);
            result.put("amount", reward[1]);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
