package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.DailyActiveProgressDO;
import org.gms.dao.entity.DailyActiveTaskDO;
import org.gms.dao.mapper.DailyActiveProgressMapper;
import org.gms.dao.mapper.DailyActiveTaskMapper;
import org.gms.model.dto.DailyActiveTaskDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 每日活跃服务：维护任务配置与角色每日进度。
 * <p>
 * 进度采用"懒重置"方案，与 {@code bosslog}/{@code onetimelog} 表一致的约定：
 * 每次读写进度时比对 logDate 是否还是今天，不是则视为0并把logDate刷新为今天，
 * 不需要额外的每日0点清空定时任务。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class DailyActiveService {

    private final DailyActiveTaskMapper taskMapper;
    private final DailyActiveProgressMapper progressMapper;

    /**
     * 查询所有已启用任务，按sortOrder升序。
     */
    public List<DailyActiveTaskDO> listEnabledTasks() {
        return taskMapper.selectListByQuery(
                QueryWrapper.create().where("enabled = 1").orderBy("sort_order", true));
    }

    /**
     * 按taskKey查询任务配置，不存在或未启用返回null。
     */
    public DailyActiveTaskDO getEnabledTask(String taskKey) {
        return taskMapper.selectOneByQuery(
                QueryWrapper.create().where("task_key = ? and enabled = 1", taskKey));
    }

    /**
     * 获取（或创建）角色某任务的当日进度记录；若记录的logDate不是今天，懒重置为0并刷新日期。
     */
    @Transactional
    public DailyActiveProgressDO getOrCreateTodayProgress(Integer characterId, String taskKey) {
        DailyActiveProgressDO progress = progressMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ? and task_key = ?", characterId, taskKey));
        Date now = new Date();
        LocalDate today = LocalDate.now();
        if (progress == null) {
            progress = DailyActiveProgressDO.builder()
                    .characterId(characterId)
                    .taskKey(taskKey)
                    .progress(0)
                    .logDate(today)
                    .claimed(0)
                    .createTime(now)
                    .updateTime(now)
                    .build();
            progressMapper.insert(progress);
            return progress;
        }
        if (!today.equals(progress.getLogDate())) {
            progress.setProgress(0);
            progress.setClaimed(0);
            progress.setLogDate(today);
            progress.setUpdateTime(now);
            progressMapper.update(progress);
        }
        return progress;
    }

    /**
     * 给角色的指定任务累计进度（超过target_count后继续累加，仅用于展示，不影响完成判定）。
     *
     * @return 累计后的当日进度
     * @throws IllegalArgumentException 任务不存在或已禁用
     */
    @Transactional
    public int addProgress(Integer characterId, String taskKey, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("累计的进度必须大于0");
        }
        DailyActiveTaskDO task = getEnabledTask(taskKey);
        if (task == null) {
            throw new IllegalArgumentException("每日活跃任务不存在或已禁用：" + taskKey);
        }
        DailyActiveProgressDO progress = getOrCreateTodayProgress(characterId, taskKey);
        int newProgress = progress.getProgress() + amount;
        progress.setProgress(newProgress);
        progress.setUpdateTime(new Date());
        progressMapper.update(progress);
        return newProgress;
    }

    /**
     * 领取任务奖励：需进度已达标且当日未领取过。
     *
     * @return 领取后的进度记录（claimed已置1）
     * @throws IllegalArgumentException 任务不存在/已禁用、进度未达标、或当日已领取过
     */
    @Transactional
    public DailyActiveProgressDO claim(Integer characterId, String taskKey) {
        DailyActiveTaskDO task = getEnabledTask(taskKey);
        if (task == null) {
            throw new IllegalArgumentException("每日活跃任务不存在或已禁用：" + taskKey);
        }
        DailyActiveProgressDO progress = getOrCreateTodayProgress(characterId, taskKey);
        if (progress.getProgress() < task.getTargetCount()) {
            throw new IllegalArgumentException("任务进度未达标，无法领取");
        }
        if (progress.getClaimed() != null && progress.getClaimed() == 1) {
            throw new IllegalArgumentException("今日已领取过该任务奖励");
        }
        progress.setClaimed(1);
        progress.setUpdateTime(new Date());
        progressMapper.update(progress);
        return progress;
    }

    // ==================== 管理后台：任务增删改查 ====================

    /** 查询所有任务（含已禁用），按sortOrder升序，供后台管理列表使用 */
    public List<DailyActiveTaskDTO> listAllTasks() {
        List<DailyActiveTaskDTO> result = new ArrayList<>();
        for (DailyActiveTaskDO d : taskMapper.selectListByQuery(
                QueryWrapper.create().orderBy("sort_order", true))) {
            result.add(toDTO(d));
        }
        return result;
    }

    /** 查询单个任务详情（含已禁用） */
    public DailyActiveTaskDTO getTaskDetail(Long id) {
        DailyActiveTaskDO d = taskMapper.selectOneById(id);
        return d == null ? null : toDTO(d);
    }

    /** 保存任务（新增或更新） */
    public DailyActiveTaskDTO saveTask(DailyActiveTaskDTO dto) {
        DailyActiveTaskDO entity = DailyActiveTaskDO.builder()
                .id(dto.getId())
                .taskKey(dto.getTaskKey())
                .taskName(dto.getTaskName())
                .targetCount(dto.getTargetCount())
                .rewardMeso(dto.getRewardMeso())
                .rewardItemId(dto.getRewardItemId())
                .rewardItemCount(dto.getRewardItemCount())
                .extraConfig(dto.getExtraConfig())
                .sortOrder(dto.getSortOrder())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (entity.getId() != null) {
            taskMapper.update(entity);
        } else {
            taskMapper.insert(entity);
        }
        return toDTO(taskMapper.selectOneById(entity.getId()));
    }

    /** 切换任务启用状态 */
    public void toggleEnabled(Long id) {
        DailyActiveTaskDO d = taskMapper.selectOneById(id);
        if (d == null) {
            return;
        }
        d.setEnabled(d.getEnabled() != null && d.getEnabled() == 1 ? 0 : 1);
        taskMapper.update(d);
    }

    /** 删除任务 */
    public void deleteTask(Long id) {
        taskMapper.deleteById(id);
    }

    private DailyActiveTaskDTO toDTO(DailyActiveTaskDO d) {
        return DailyActiveTaskDTO.builder()
                .id(d.getId())
                .taskKey(d.getTaskKey())
                .taskName(d.getTaskName())
                .targetCount(d.getTargetCount())
                .rewardMeso(d.getRewardMeso())
                .rewardItemId(d.getRewardItemId())
                .rewardItemCount(d.getRewardItemCount())
                .extraConfig(d.getExtraConfig())
                .sortOrder(d.getSortOrder())
                .enabled(d.getEnabled())
                .build();
    }
}
