package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.LevelRewardManager;
import org.gms.dao.entity.LevelRewardDO;
import org.gms.dao.entity.LevelRewardItemDO;
import org.gms.dao.mapper.LevelRewardItemMapper;
import org.gms.dao.mapper.LevelRewardMapper;
import org.gms.model.dto.LevelRewardSaveDTO;
import org.gms.model.dto.LevelRewardSaveDTO.ItemDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 等级奖励服务类，负责等级奖励系统的配置管理。
 * <p>
 * 提供等级奖励配置的增删改查操作，包括奖励配置和道具列表，
 * 并在配置变更时同步更新 {@link LevelRewardManager} 的内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class LevelRewardService {

    /**
     * 等级奖励主表数据访问对象
     */
    private final LevelRewardMapper rewardMapper;

    /**
     * 等级奖励道具表数据访问对象
     */
    private final LevelRewardItemMapper itemMapper;

    /**
     * 初始化方法，在 Spring 容器启动时执行。
     * 从数据库加载所有等级奖励配置并初始化到 LevelRewardManager。
     */
    @PostConstruct
    public void init() {
        refreshCache();
        log.info("等级奖励配置加载完成");
    }

    /**
     * 获取所有等级奖励配置列表，按等级升序排列。
     *
     * @return 等级奖励配置DTO列表
     */
    public List<LevelRewardSaveDTO> getConfigList() {
        List<LevelRewardDO> rewards = rewardMapper.selectAll();
        List<LevelRewardItemDO> allItems = itemMapper.selectAll();

        List<LevelRewardSaveDTO> result = new ArrayList<>();
        for (LevelRewardDO reward : rewards) {
            result.add(toDTO(reward, allItems));
        }
        // 按等级升序排列
        result.sort(java.util.Comparator.comparingInt(LevelRewardSaveDTO::getLevel));
        return result;
    }

    /**
     * 根据ID获取等级奖励配置。
     *
     * @param id 奖励配置ID
     * @return 等级奖励配置DTO，不存在则返回null
     */
    public LevelRewardSaveDTO getConfigById(Long id) {
        LevelRewardDO reward = rewardMapper.selectOneById(id);
        if (reward == null) return null;
        return toDTO(reward, itemMapper.selectAll());
    }

    /**
     * 保存等级奖励配置（新增或更新），级联保存/更新关联的道具子表。
     *
     * @param dto 等级奖励配置DTO
     * @return 保存后的等级奖励配置DTO
     */
    @Transactional
    public LevelRewardSaveDTO saveConfig(LevelRewardSaveDTO dto) {
        LevelRewardDO reward = LevelRewardDO.builder()
                .id(dto.getId())
                .level(dto.getLevel())
                .meso(dto.getMeso() != null ? dto.getMeso() : 0)
                .nxCredit(dto.getNxCredit() != null ? dto.getNxCredit() : 0)
                .maplePoint(dto.getMaplePoint() != null ? dto.getMaplePoint() : 0)
                .nxPrepaid(dto.getNxPrepaid() != null ? dto.getNxPrepaid() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (reward.getId() != null) {
            rewardMapper.update(reward);
            // 删除旧的关联道具
            deleteItemsByRewardId(reward.getId());
        } else {
            rewardMapper.insert(reward);
        }

        // 保存道具列表
        if (dto.getItems() != null) {
            for (ItemDTO itemDTO : dto.getItems()) {
                LevelRewardItemDO item = LevelRewardItemDO.builder()
                        .rewardId(reward.getId())
                        .itemId(itemDTO.getItemId())
                        .quantity(itemDTO.getCount() != null ? itemDTO.getCount() : 1)
                        .build();
                itemMapper.insert(item);
            }
        }

        // 刷新内存缓存
        refreshCache();
        return getConfigById(reward.getId());
    }

    /**
     * 删除等级奖励配置，级联删除关联道具。
     *
     * @param id 奖励配置ID
     */
    @Transactional
    public void deleteConfig(Long id) {
        deleteItemsByRewardId(id);
        rewardMapper.deleteById(id);
        // 刷新内存缓存
        refreshCache();
    }

    /**
     * 根据奖励ID删除所有关联的道具。
     *
     * @param rewardId 奖励ID
     */
    private void deleteItemsByRewardId(Long rewardId) {
        itemMapper.deleteByQuery(
                QueryWrapper.create().where("reward_id = ?", rewardId));
    }

    /**
     * 刷新内存缓存，从数据库重新加载所有配置。
     */
    private void refreshCache() {
        LevelRewardManager.load(rewardMapper.selectAll(), itemMapper.selectAll());
    }

    /**
     * 将DO实体转换为DTO对象，包含关联的道具列表。
     *
     * @param reward 等级奖励DO
     * @param allItems 所有道具列表
     * @return 转换后的DTO对象
     */
    private LevelRewardSaveDTO toDTO(LevelRewardDO reward, List<LevelRewardItemDO> allItems) {
        List<ItemDTO> itemDTOs = new ArrayList<>();
        for (LevelRewardItemDO item : allItems) {
            if (item.getRewardId().equals(reward.getId())) {
                itemDTOs.add(ItemDTO.builder()
                        .id(item.getId())
                        .itemId(item.getItemId())
                        .count(item.getQuantity())
                        .build());
            }
        }
        return LevelRewardSaveDTO.builder()
                .id(reward.getId())
                .level(reward.getLevel())
                .meso(reward.getMeso())
                .nxCredit(reward.getNxCredit())
                .maplePoint(reward.getMaplePoint())
                .nxPrepaid(reward.getNxPrepaid())
                .enabled(reward.getEnabled())
                .items(itemDTOs)
                .build();
    }
}
