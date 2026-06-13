package org.gms.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.DailyExploreConfigManager;
import org.gms.config.GameConfig;
import org.gms.dao.entity.DailyExploreFinalRewardDO;
import org.gms.dao.entity.DailyExploreMapDO;
import org.gms.dao.entity.DailyExploreRewardDO;
import org.gms.dao.mapper.DailyExploreFinalRewardMapper;
import org.gms.dao.mapper.DailyExploreMapMapper;
import org.gms.dao.mapper.DailyExploreRewardMapper;
import org.gms.model.dto.DailyExploreSaveDTO;
import org.gms.server.ItemInformationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每日探索服务类，负责地图池、每轮随机奖励、完成奖励的配置管理。
 * <p>
 * 配置变更时同步更新 {@link DailyExploreConfigManager} 的内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class DailyExploreService {

    private final DailyExploreMapMapper mapMapper;
    private final DailyExploreRewardMapper rewardMapper;
    private final DailyExploreFinalRewardMapper finalRewardMapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("每日探索配置加载完成");
    }

    /** 刷新配置缓存 */
    private void refreshCache() {
        DailyExploreConfigManager.load(
                mapMapper.selectAll(),
                rewardMapper.selectAll(),
                finalRewardMapper.selectAll());
    }

    // ==================== 地图池 CRUD ====================

    /** 获取地图池列表（按 sortOrder 排序） */
    public List<DailyExploreSaveDTO> getMapList() {
        List<DailyExploreMapDO> maps = mapMapper.selectAll();
        maps.sort(Comparator.comparingInt(m ->
                m.getSortOrder() != null ? m.getSortOrder() : 0));
        return maps.stream().map(this::toMapDTO).collect(Collectors.toList());
    }

    /** 获取单个地图配置 */
    public DailyExploreSaveDTO getMapById(Long id) {
        DailyExploreMapDO map = mapMapper.selectOneById(id);
        return map != null ? toMapDTO(map) : null;
    }

    /** 保存地图配置（新增或更新） */
    @Transactional
    public DailyExploreSaveDTO saveMap(DailyExploreSaveDTO dto) {
        DailyExploreMapDO map = DailyExploreMapDO.builder()
                .id(dto.getId())
                .mapId(dto.getMapId())
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (map.getId() != null) {
            mapMapper.update(map);
        } else {
            mapMapper.insert(map);
        }
        refreshCache();
        return getMapById(map.getId());
    }

    /** 删除地图配置 */
    @Transactional
    public void deleteMap(Long id) {
        mapMapper.deleteById(id);
        refreshCache();
    }

    /** 批量删除地图配置 */
    @Transactional
    public void deleteMapBatch(List<Long> ids) {
        mapMapper.deleteBatchByIds(ids);
        refreshCache();
    }

    // ==================== 每轮随机奖励 CRUD ====================

    /** 获取每轮随机奖励列表 */
    public List<DailyExploreSaveDTO.RewardDTO> getRewardList() {
        List<DailyExploreRewardDO> rewards = rewardMapper.selectAll();
        rewards.sort(Comparator.comparingInt(r ->
                r.getSortOrder() != null ? r.getSortOrder() : 0));
        return rewards.stream().map(this::toRewardDTO).collect(Collectors.toList());
    }

    /** 保存每轮随机奖励 */
    @Transactional
    public DailyExploreSaveDTO.RewardDTO saveReward(DailyExploreSaveDTO.RewardDTO dto) {
        DailyExploreRewardDO reward = DailyExploreRewardDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .minQuantity(dto.getMinQuantity() != null ? dto.getMinQuantity() : 1)
                .maxQuantity(dto.getMaxQuantity() != null ? dto.getMaxQuantity() : 1)
                .weight(dto.getWeight() != null ? dto.getWeight() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (reward.getId() != null) {
            rewardMapper.update(reward);
        } else {
            rewardMapper.insert(reward);
        }
        refreshCache();
        List<DailyExploreRewardDO> all = rewardMapper.selectAll();
        return all.stream()
                .filter(r -> r.getId().equals(reward.getId()))
                .findFirst()
                .map(this::toRewardDTO)
                .orElse(null);
    }

    /** 删除每轮随机奖励 */
    @Transactional
    public void deleteReward(Long id) {
        rewardMapper.deleteById(id);
        refreshCache();
    }

    // ==================== 完成奖励 CRUD ====================

    /** 获取完成奖励列表 */
    public List<DailyExploreSaveDTO.FinalRewardDTO> getFinalRewardList() {
        List<DailyExploreFinalRewardDO> rewards = finalRewardMapper.selectAll();
        rewards.sort(Comparator
                .comparingInt((DailyExploreFinalRewardDO r) ->
                        r.getExploreCount() != null ? r.getExploreCount() : 0)
                .thenComparingInt(r -> r.getSortOrder() != null ? r.getSortOrder() : 0));
        return rewards.stream().map(this::toFinalRewardDTO).collect(Collectors.toList());
    }

    /** 保存完成奖励 */
    @Transactional
    public DailyExploreSaveDTO.FinalRewardDTO saveFinalReward(DailyExploreSaveDTO.FinalRewardDTO dto) {
        DailyExploreFinalRewardDO reward = DailyExploreFinalRewardDO.builder()
                .id(dto.getId())
                .exploreCount(dto.getExploreCount())
                .rewardDesc(dto.getRewardDesc() != null ? dto.getRewardDesc() : "")
                .itemId(dto.getItemId() != null ? dto.getItemId() : 0)
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .build();
        if (reward.getId() != null) {
            finalRewardMapper.update(reward);
        } else {
            finalRewardMapper.insert(reward);
        }
        refreshCache();
        List<DailyExploreFinalRewardDO> all = finalRewardMapper.selectAll();
        return all.stream()
                .filter(r -> r.getId().equals(reward.getId()))
                .findFirst()
                .map(this::toFinalRewardDTO)
                .orElse(null);
    }

    /** 删除完成奖励 */
    @Transactional
    public void deleteFinalReward(Long id) {
        finalRewardMapper.deleteById(id);
        refreshCache();
    }

    // ==================== 游戏参数 ====================

    /** 获取每日探索上限 */
    public int getDailyLimit() {
        return GameConfig.get("server", "Game Mechanics", "daily_explore_limit", 10);
    }

    // ==================== 数据转换 ====================

    private DailyExploreSaveDTO toMapDTO(DailyExploreMapDO map) {
        return DailyExploreSaveDTO.builder()
                .id(map.getId())
                .mapId(map.getMapId())
                .sortOrder(map.getSortOrder() != null ? map.getSortOrder() : 0)
                .enabled(map.getEnabled())
                .createTime(map.getCreateTime())
                .updateTime(map.getUpdateTime())
                .build();
    }

    private DailyExploreSaveDTO.RewardDTO toRewardDTO(DailyExploreRewardDO r) {
        String itemName = resolveItemName(r.getItemId());
        return DailyExploreSaveDTO.RewardDTO.builder()
                .id(r.getId())
                .itemId(r.getItemId())
                .itemName(itemName)
                .minQuantity(r.getMinQuantity())
                .maxQuantity(r.getMaxQuantity())
                .weight(r.getWeight())
                .sortOrder(r.getSortOrder())
                .enabled(r.getEnabled())
                .build();
    }

    private DailyExploreSaveDTO.FinalRewardDTO toFinalRewardDTO(DailyExploreFinalRewardDO r) {
        String itemName = resolveItemName(r.getItemId());
        return DailyExploreSaveDTO.FinalRewardDTO.builder()
                .id(r.getId())
                .exploreCount(r.getExploreCount())
                .rewardDesc(r.getRewardDesc())
                .itemId(r.getItemId())
                .itemName(itemName)
                .quantity(r.getQuantity())
                .sortOrder(r.getSortOrder())
                .build();
    }

    /** 解析物品名称（0=金币，其他通过WZ查询） */
    private String resolveItemName(Integer itemId) {
        if (itemId == null || itemId == 0) return "金币";
        try {
            String name = ItemInformationProvider.getInstance().getName(itemId);
            return name != null ? name : String.valueOf(itemId);
        } catch (Exception e) {
            return String.valueOf(itemId);
        }
    }
}
