package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.DailyDungeonConfigManager;
import org.gms.dao.entity.DailyDungeonConfigDO;
import org.gms.dao.entity.DailyDungeonDailyRewardDO;
import org.gms.dao.entity.DailyDungeonRewardDO;
import org.gms.dao.entity.DailyDungeonVipConfigDO;
import org.gms.dao.mapper.DailyDungeonConfigMapper;
import org.gms.dao.mapper.DailyDungeonDailyRewardMapper;
import org.gms.dao.mapper.DailyDungeonRewardMapper;
import org.gms.dao.mapper.DailyDungeonVipConfigMapper;
import org.gms.model.dto.DailyDungeonSaveDTO;
import org.gms.model.dto.DailyDungeonSaveDTO.DailyRewardDTO;
import org.gms.model.dto.DailyDungeonSaveDTO.RewardDTO;
import org.gms.model.dto.DailyDungeonSaveDTO.VipConfigDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.maps.MapFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 每日副本服务 —— 副本配置、完成奖励、每日奖励、VIP配置的增删改查 + 缓存刷新
 */
@Slf4j
@Service
@AllArgsConstructor
public class DailyDungeonService {

    private final DailyDungeonConfigMapper configMapper;
    private final DailyDungeonRewardMapper rewardMapper;
    private final DailyDungeonDailyRewardMapper dailyRewardMapper;
    private final DailyDungeonVipConfigMapper vipConfigMapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("每日副本配置加载完成");
    }

    // ==================== 副本配置 CRUD ====================

    /** 获取所有配置列表 */
    public List<DailyDungeonSaveDTO> getConfigList() {
        List<DailyDungeonConfigDO> configs = configMapper.selectAll();
        List<DailyDungeonRewardDO> allRewards = rewardMapper.selectAll();
        List<DailyDungeonSaveDTO> result = new ArrayList<>();
        for (DailyDungeonConfigDO c : configs) {
            result.add(toConfigDTO(c, allRewards));
        }
        result.sort(Comparator.comparingInt(DailyDungeonSaveDTO::getSortOrder));
        return result;
    }

    /** 根据ID获取配置 */
    public DailyDungeonSaveDTO getConfigById(Long id) {
        DailyDungeonConfigDO config = configMapper.selectOneById(id);
        if (config == null) return null;
        return toConfigDTO(config, rewardMapper.selectAll());
    }

    /** 保存配置（新增或更新） */
    @Transactional
    public DailyDungeonSaveDTO saveConfig(DailyDungeonSaveDTO dto) {
        // 自动解析地图名称
        String mapName = resolveMapName(dto.getMapId());
        DailyDungeonConfigDO config = DailyDungeonConfigDO.builder()
                .id(dto.getId())
                .dungeonKey(dto.getDungeonKey())
                .dungeonName(dto.getDungeonName())
                .mapId(dto.getMapId())
                .mapName(mapName)
                .completeCount(dto.getCompleteCount() != null ? dto.getCompleteCount() : 3)
                .sweepItemId(dto.getSweepItemId() != null ? dto.getSweepItemId() : 0)
                .sweepItemCost(dto.getSweepItemCost() != null ? dto.getSweepItemCost() : 1)
                .maxSweep(dto.getMaxSweep() != null ? dto.getMaxSweep() : 0)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (config.getId() != null) {
            configMapper.update(config);
            deleteRewardsByConfigId(config.getId());
        } else {
            configMapper.insert(config);
        }

        if (dto.getRewards() != null) {
            for (RewardDTO r : dto.getRewards()) {
                rewardMapper.insert(DailyDungeonRewardDO.builder()
                        .configId(config.getId())
                        .completeCount(r.getCompleteCount())
                        .rewardDesc(r.getRewardDesc())
                        .itemId(r.getItemId())
                        .quantity(r.getQuantity() != null ? r.getQuantity() : 1)
                        .sortOrder(r.getSortOrder() != null ? r.getSortOrder() : 0)
                        .build());
            }
        }

        refreshCache();
        return getConfigById(config.getId());
    }

    /** 删除配置 */
    @Transactional
    public void deleteConfig(Long id) {
        deleteRewardsByConfigId(id);
        configMapper.deleteById(id);
        refreshCache();
    }

    private void deleteRewardsByConfigId(Long configId) {
        rewardMapper.deleteByQuery(
                QueryWrapper.create().where("config_id = ?", configId));
    }

    // ==================== 每日完成奖励 CRUD ====================

    /** 获取每日完成奖励列表（所有副本完成后可领取） */
    public List<DailyRewardDTO> getDailyRewardList() {
        List<DailyDungeonDailyRewardDO> list = dailyRewardMapper.selectAll();
        List<DailyRewardDTO> result = new ArrayList<>();
        for (DailyDungeonDailyRewardDO r : list) {
            result.add(DailyRewardDTO.builder()
                    .id(r.getId())
                    .itemId(r.getItemId())
                    .itemName(resolveItemName(r.getItemId()))
                    .quantity(r.getQuantity())
                    .rewardDesc(r.getRewardDesc())
                    .sortOrder(r.getSortOrder())
                    .build());
        }
        result.sort(Comparator.comparingInt(DailyRewardDTO::getSortOrder));
        return result;
    }

    /** 保存每日完成奖励 */
    @Transactional
    public DailyRewardDTO saveDailyReward(DailyRewardDTO dto) {
        DailyDungeonDailyRewardDO reward = DailyDungeonDailyRewardDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : 1)
                .rewardDesc(dto.getRewardDesc())
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .build();
        if (reward.getId() != null) {
            dailyRewardMapper.update(reward);
        } else {
            dailyRewardMapper.insert(reward);
        }
        refreshCache();
        return DailyRewardDTO.builder()
                .id(reward.getId())
                .itemId(reward.getItemId())
                .itemName(resolveItemName(reward.getItemId()))
                .quantity(reward.getQuantity())
                .rewardDesc(reward.getRewardDesc())
                .sortOrder(reward.getSortOrder())
                .build();
    }

    /** 删除每日完成奖励 */
    @Transactional
    public void deleteDailyReward(Long id) {
        dailyRewardMapper.deleteById(id);
        refreshCache();
    }

    // ==================== VIP物品配置 CRUD ====================

    /** 获取VIP物品配置列表 */
    public List<VipConfigDTO> getVipConfigList() {
        List<DailyDungeonVipConfigDO> list = vipConfigMapper.selectAll();
        List<VipConfigDTO> result = new ArrayList<>();
        for (DailyDungeonVipConfigDO v : list) {
            result.add(VipConfigDTO.builder()
                    .id(v.getId())
                    .itemId(v.getItemId())
                    .itemName(resolveItemName(v.getItemId()))
                    .description(v.getDescription())
                    .enabled(v.getEnabled())
                    .sortOrder(v.getSortOrder())
                    .build());
        }
        result.sort(Comparator.comparingInt(VipConfigDTO::getSortOrder));
        return result;
    }

    /** 保存VIP物品配置 */
    @Transactional
    public VipConfigDTO saveVipConfig(VipConfigDTO dto) {
        DailyDungeonVipConfigDO config = DailyDungeonVipConfigDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .description(dto.getDescription())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .build();
        if (config.getId() != null) {
            vipConfigMapper.update(config);
        } else {
            vipConfigMapper.insert(config);
        }
        refreshCache();
        return VipConfigDTO.builder()
                .id(config.getId())
                .itemId(config.getItemId())
                .itemName(resolveItemName(config.getItemId()))
                .description(config.getDescription())
                .enabled(config.getEnabled())
                .sortOrder(config.getSortOrder())
                .build();
    }

    /** 删除VIP物品配置 */
    @Transactional
    public void deleteVipConfig(Long id) {
        vipConfigMapper.deleteById(id);
        refreshCache();
    }

    // ==================== 缓存刷新 ====================

    private void refreshCache() {
        DailyDungeonConfigManager.load(
                configMapper.selectAll(),
                rewardMapper.selectAll(),
                dailyRewardMapper.selectAll(),
                vipConfigMapper.selectAll());
    }

    // ==================== DTO 转换 ====================

    private DailyDungeonSaveDTO toConfigDTO(DailyDungeonConfigDO c, List<DailyDungeonRewardDO> allRewards) {
        List<RewardDTO> rewardDTOs = new ArrayList<>();
        for (DailyDungeonRewardDO r : allRewards) {
            if (r.getConfigId().equals(c.getId())) {
                rewardDTOs.add(RewardDTO.builder()
                        .id(r.getId())
                        .completeCount(r.getCompleteCount())
                        .rewardDesc(r.getRewardDesc())
                        .itemId(r.getItemId())
                        .itemName(resolveItemName(r.getItemId()))
                        .quantity(r.getQuantity())
                        .sortOrder(r.getSortOrder())
                        .build());
            }
        }
        rewardDTOs.sort(Comparator.comparingInt(RewardDTO::getSortOrder));
        return DailyDungeonSaveDTO.builder()
                .id(c.getId())
                .dungeonKey(c.getDungeonKey())
                .dungeonName(c.getDungeonName())
                .mapId(c.getMapId())
                .mapName(c.getMapName())
                .completeCount(c.getCompleteCount() != null ? c.getCompleteCount() : 3)
                .sweepItemId(c.getSweepItemId())
                .sweepItemCost(c.getSweepItemCost())
                .maxSweep(c.getMaxSweep())
                .sortOrder(c.getSortOrder())
                .enabled(c.getEnabled())
                .rewards(rewardDTOs)
                .build();
    }

    // ==================== 名称解析工具 ====================

    /** 解析地图名称 */
    private String resolveMapName(Integer mapId) {
        if (mapId == null || mapId <= 0) return "";
        try {
            return MapFactory.loadPlaceName(mapId);
        } catch (Exception e) {
            log.warn("解析地图名称失败: mapId={}", mapId, e);
            return "";
        }
    }

    /** 解析物品名称 */
    private String resolveItemName(Integer itemId) {
        if (itemId == null || itemId <= 0) return itemId != null && itemId == 0 ? "金币" : "";
        try {
            return ItemInformationProvider.getInstance().getName(itemId);
        } catch (Exception e) {
            log.warn("解析物品名称失败: itemId={}", itemId, e);
            return "";
        }
    }
}
