package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.GameConfig;
import org.gms.config.PaohuanConfigManager;
import org.gms.dao.entity.PaohuanConfigDO;
import org.gms.dao.entity.PaohuanRewardDO;
import org.gms.dao.entity.PaohuanRingRewardDO;
import org.gms.dao.mapper.PaohuanConfigMapper;
import org.gms.dao.mapper.PaohuanRewardMapper;
import org.gms.dao.mapper.PaohuanRingRewardMapper;
import org.gms.model.dto.PaohuanSaveDTO;
import org.gms.model.dto.PaohuanSaveDTO.RewardDTO;
import org.gms.model.dto.PaohuanSaveDTO.RingRewardDTO;
import org.gms.server.ItemInformationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 跑环服务类，负责跑环系统的配置管理。
 * <p>
 * 管理三类数据：
 * 1) 物品池（任务随机选取）— xy_paohuan_config
 * 2) 每环随机奖励池 — xy_paohuan_ring_reward
 * 3) 里程碑奖励 — xy_paohuan_reward
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class PaohuanService {

    private final PaohuanConfigMapper configMapper;
    private final PaohuanRewardMapper rewardMapper;
    private final PaohuanRingRewardMapper ringRewardMapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("跑环配置加载完成");
    }

    // ==================== 物品池 ====================

    public List<PaohuanSaveDTO> getConfigList() {
        List<PaohuanConfigDO> configs = configMapper.selectAll();
        List<PaohuanSaveDTO> result = new ArrayList<>();
        for (PaohuanConfigDO config : configs) {
            result.add(toDTO(config));
        }
        result.sort(java.util.Comparator.comparingInt(dto ->
                dto.getSortOrder() != null ? dto.getSortOrder() : 0));
        return result;
    }

    public PaohuanSaveDTO getConfigById(Long id) {
        PaohuanConfigDO config = configMapper.selectOneById(id);
        return config != null ? toDTO(config) : null;
    }

    @Transactional
    public PaohuanSaveDTO saveConfig(PaohuanSaveDTO dto) {
        PaohuanConfigDO config = PaohuanConfigDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : 1)
                .dropMapId(dto.getDropMapId() != null ? dto.getDropMapId() : 0)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (config.getId() != null) {
            configMapper.update(config);
        } else {
            configMapper.insert(config);
        }
        refreshCache();
        return getConfigById(config.getId());
    }

    @Transactional
    public void deleteConfig(Long id) {
        configMapper.deleteById(id);
        refreshCache();
    }

    /**
     * 批量删除跑环物品池配置
     *
     * @param ids 配置ID列表
     */
    @Transactional
    public void deleteConfigBatch(List<Long> ids) {
        configMapper.deleteBatchByIds(ids);
        refreshCache();
    }

    // ==================== 每环随机奖励 ====================

    public List<RingRewardDTO> getRingRewardList() {
        List<PaohuanRingRewardDO> list = ringRewardMapper.selectAll();
        list.sort(java.util.Comparator.comparingInt(r ->
                r.getSortOrder() != null ? r.getSortOrder() : 0));
        return list.stream().map(this::toRingRewardDTO).collect(Collectors.toList());
    }

    @Transactional
    public RingRewardDTO saveRingReward(RingRewardDTO dto) {
        PaohuanRingRewardDO rw = PaohuanRingRewardDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .minQuantity(dto.getMinQuantity() != null ? dto.getMinQuantity() : 1)
                .maxQuantity(dto.getMaxQuantity() != null ? dto.getMaxQuantity() : 1)
                .weight(dto.getWeight() != null ? dto.getWeight() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (rw.getId() != null) {
            ringRewardMapper.update(rw);
        } else {
            ringRewardMapper.insert(rw);
        }
        refreshCache();
        return toRingRewardDTO(rw);
    }

    @Transactional
    public void deleteRingReward(Long id) {
        ringRewardMapper.deleteById(id);
        refreshCache();
    }

    // ==================== 里程碑奖励 ====================

    public List<RewardDTO> getRewardList() {
        List<PaohuanRewardDO> rewards = rewardMapper.selectAll();
        rewards.sort(java.util.Comparator.comparingInt(r ->
                r.getRingCount() != null ? r.getRingCount() : 0));
        return rewards.stream().map(this::toRewardDTO).collect(Collectors.toList());
    }

    @Transactional
    public RewardDTO saveReward(RewardDTO dto) {
        PaohuanRewardDO reward = PaohuanRewardDO.builder()
                .id(dto.getId())
                .ringCount(dto.getRingCount())
                .rewardDesc(dto.getRewardDesc() != null ? dto.getRewardDesc() : "")
                .itemId(dto.getItemId() != null ? dto.getItemId() : 0)
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .build();
        if (reward.getId() != null) {
            rewardMapper.update(reward);
        } else {
            rewardMapper.insert(reward);
        }
        refreshCache();
        return toRewardDTO(reward);
    }

    @Transactional
    public void deleteReward(Long id) {
        rewardMapper.deleteById(id);
        refreshCache();
    }

    // ==================== 缓存 ====================

    private void refreshCache() {
        PaohuanConfigManager.load(
                configMapper.selectAll(),
                rewardMapper.selectAll(),
                ringRewardMapper.selectAll());
    }

    // ==================== 游戏参数 ====================

    public int getDailyLimit() {
        return GameConfig.get("server", "Game Mechanics", "paohuan_daily_limit", 20);
    }

    public int getExpPerRing() {
        return GameConfig.get("server", "Game Mechanics", "paohuan_exp_per_ring", 10000);
    }

    public int getMesoPerRing() {
        return GameConfig.get("server", "Game Mechanics", "paohuan_meso_per_ring", 10000);
    }

    // ==================== 数据转换 ====================

    /**
     * 根据物品ID解析物品名称（用于展示）。
     * itemId=0 返回"金币"，WZ中不存在返回"未知物品(ID)"。
     */
    private String resolveItemName(Integer itemId) {
        if (itemId == null || itemId == 0) {
            return "金币";
        }
        try {
            String name = ItemInformationProvider.getInstance().getName(itemId);
            return (name != null && !name.isEmpty()) ? name : "未知物品(" + itemId + ")";
        } catch (Exception e) {
            return "未知物品(" + itemId + ")";
        }
    }

    private PaohuanSaveDTO toDTO(PaohuanConfigDO config) {
        return PaohuanSaveDTO.builder()
                .id(config.getId())
                .itemId(config.getItemId())
                .itemName(resolveItemName(config.getItemId()))
                .quantity(config.getQuantity())
                .dropMapId(config.getDropMapId() != null ? config.getDropMapId() : 0)
                .sortOrder(config.getSortOrder())
                .enabled(config.getEnabled())
                .createTime(config.getCreateTime())
                .updateTime(config.getUpdateTime())
                .build();
    }

    private RewardDTO toRewardDTO(PaohuanRewardDO reward) {
        return RewardDTO.builder()
                .id(reward.getId())
                .ringCount(reward.getRingCount())
                .rewardDesc(reward.getRewardDesc())
                .itemId(reward.getItemId())
                .itemName(resolveItemName(reward.getItemId()))
                .quantity(reward.getQuantity())
                .sortOrder(reward.getSortOrder())
                .build();
    }

    private RingRewardDTO toRingRewardDTO(PaohuanRingRewardDO rw) {
        return RingRewardDTO.builder()
                .id(rw.getId())
                .itemId(rw.getItemId())
                .itemName(resolveItemName(rw.getItemId()))
                .minQuantity(rw.getMinQuantity())
                .maxQuantity(rw.getMaxQuantity())
                .weight(rw.getWeight())
                .sortOrder(rw.getSortOrder())
                .enabled(rw.getEnabled())
                .build();
    }
}
