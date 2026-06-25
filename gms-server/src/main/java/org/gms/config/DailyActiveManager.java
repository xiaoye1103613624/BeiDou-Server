package org.gms.config;

import org.gms.dao.entity.DailyActiveProgressDO;
import org.gms.dao.entity.DailyActiveTaskDO;
import org.gms.manager.ServerManager;
import org.gms.service.DailyActiveService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 每日活跃静态门面，供 GraalVM JS 脚本（以及Java其他模块）通过 {@code Java.type()} 调用。
 * <p>
 * 金币/物品奖励的发放由脚本侧在 {@link #claim} 调用成功后自行处理(与炼金/锻造系统一致的约定)，
 * 本门面只负责进度累计与达标/领取状态的校验。
 * </p>
 */
public class DailyActiveManager {

    private static final Logger log = LoggerFactory.getLogger(DailyActiveManager.class);

    private DailyActiveManager() {}

    private static DailyActiveService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(DailyActiveService.class);
    }

    private static Map<String, Object> taskToMap(DailyActiveTaskDO task) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("taskKey", task.getTaskKey());
        map.put("taskName", task.getTaskName());
        map.put("targetCount", task.getTargetCount());
        map.put("rewardMeso", task.getRewardMeso());
        map.put("rewardItemId", task.getRewardItemId());
        map.put("rewardItemCount", task.getRewardItemCount());
        map.put("extraConfig", task.getExtraConfig());
        return map;
    }

    /**
     * 给角色的指定任务累计一次(或多次)进度，供事件钩子/脚本调用。任务不存在或已禁用时静默跳过(不抛异常)，
     * 避免某个埋点因配置缺失而影响正常游戏流程。
     */
    public static void addProgress(Integer characterId, String taskKey, int amount) {
        try {
            getService().addProgress(characterId, taskKey, amount);
        } catch (Exception e) {
            log.warn("每日活跃累计进度失败：characterId={}, taskKey={}, amount={}, reason={}",
                    characterId, taskKey, amount, e.getMessage());
        }
    }

    /**
     * 判断指定怪物ID是否在"击杀野外精英"任务的extraConfig配置列表中(逗号分隔的怪物ID)。
     * 配置为空/任务未启用时返回false。
     */
    public static boolean isEliteMob(int mobId) {
        try {
            DailyActiveTaskDO task = getService().getEnabledTask("kill_elite");
            if (task == null || task.getExtraConfig() == null || task.getExtraConfig().isBlank()) {
                return false;
            }
            for (String part : task.getExtraConfig().split(",")) {
                part = part.trim();
                if (!part.isEmpty() && Integer.parseInt(part) == mobId) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn("校验野外精英怪物ID失败：mobId={}, reason={}", mobId, e.getMessage());
        }
        return false;
    }

    /**
     * 查询角色当前所有已启用任务的总览，供NPC脚本展示进度列表。
     *
     * @return 每项任务：{taskKey, taskName, targetCount, progress, completed, claimed, rewardMeso, rewardItemId, rewardItemCount}
     */
    public static List<Map<String, Object>> getOverview(Integer characterId) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            DailyActiveService service = getService();
            for (DailyActiveTaskDO task : service.listEnabledTasks()) {
                DailyActiveProgressDO progress = service.getOrCreateTodayProgress(characterId, task.getTaskKey());
                Map<String, Object> map = taskToMap(task);
                map.put("progress", progress.getProgress());
                map.put("completed", progress.getProgress() >= task.getTargetCount());
                map.put("claimed", progress.getClaimed() != null && progress.getClaimed() == 1);
                list.add(map);
            }
        } catch (Exception e) {
            log.error("查询每日活跃总览失败", e);
        }
        return list;
    }

    /**
     * 领取任务奖励：校验进度达标且当日未领取过，校验通过后将claimed置1。
     * 金币/物品奖励需脚本在调用成功后自行使用 {@code cm.gainMeso}/{@code cm.gainItem} 发放
     * (本门面返回的rewardMeso/rewardItemId/rewardItemCount即为应发放的数值)。
     *
     * @return {success, message, rewardMeso, rewardItemId, rewardItemCount}
     */
    public static Map<String, Object> claim(Integer characterId, String taskKey) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            DailyActiveService service = getService();
            DailyActiveTaskDO task = service.getEnabledTask(taskKey);
            service.claim(characterId, taskKey);
            result.put("success", true);
            result.put("message", "领取成功");
            result.put("rewardMeso", task.getRewardMeso());
            result.put("rewardItemId", task.getRewardItemId());
            result.put("rewardItemCount", task.getRewardItemCount());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
