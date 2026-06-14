package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.DailyBossConfigManager;
import org.gms.dao.entity.DailyBossConfigDO;
import org.gms.dao.entity.DailyBossRewardDO;
import org.gms.dao.mapper.DailyBossConfigMapper;
import org.gms.dao.mapper.DailyBossRewardMapper;
import org.gms.model.dto.DailyBossSaveDTO;
import org.gms.model.dto.DailyBossSaveDTO.RewardDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 每日Boss服务 —— 配置的增删改查 + 缓存刷新
 */
@Slf4j
@Service
@AllArgsConstructor
public class DailyBossService {

    private final DailyBossConfigMapper configMapper;
    private final DailyBossRewardMapper rewardMapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("每日Boss配置加载完成");
    }

    /** 获取所有配置列表 */
    public List<DailyBossSaveDTO> getConfigList() {
        List<DailyBossConfigDO> configs = configMapper.selectAll();
        List<DailyBossRewardDO> allRewards = rewardMapper.selectAll();
        List<DailyBossSaveDTO> result = new ArrayList<>();
        for (DailyBossConfigDO c : configs) {
            result.add(toDTO(c, allRewards));
        }
        result.sort(Comparator.comparingInt(DailyBossSaveDTO::getSortOrder));
        return result;
    }

    /** 根据ID获取配置 */
    public DailyBossSaveDTO getConfigById(Long id) {
        DailyBossConfigDO config = configMapper.selectOneById(id);
        if (config == null) return null;
        return toDTO(config, rewardMapper.selectAll());
    }

    /** 保存配置（新增或更新） */
    @Transactional
    public DailyBossSaveDTO saveConfig(DailyBossSaveDTO dto) {
        DailyBossConfigDO config = DailyBossConfigDO.builder()
                .id(dto.getId())
                .bossKey(dto.getBossKey())
                .bossName(dto.getBossName())
                .bossMobId(dto.getBossMobId())
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
                rewardMapper.insert(DailyBossRewardDO.builder()
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

    /** 获取环式系统游戏参数 */
    public Map<String, Object> getGameParams() {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("bossRingEnabled", DailyBossConfigManager.getBossRingEnabled());
        params.put("dailyLimit", DailyBossConfigManager.getDailyLimit());
        params.put("expBase", DailyBossConfigManager.getExpBase());
        params.put("mesoBase", DailyBossConfigManager.getMesoBase());
        params.put("killMin", DailyBossConfigManager.getKillMin());
        params.put("killMax", DailyBossConfigManager.getKillMax());
        params.put("abandonFee", DailyBossConfigManager.getAbandonFee());
        params.put("finalItemId", DailyBossConfigManager.getFinalItemId());
        params.put("finalItemQty", DailyBossConfigManager.getFinalItemQty());
        params.put("milestoneRewards", DailyBossConfigManager.getMilestoneRewardsJson());
        params.put("randomRewards", DailyBossConfigManager.getRandomRewardsJson());
        return params;
    }

    private void refreshCache() {
        DailyBossConfigManager.load(configMapper.selectAll(), rewardMapper.selectAll());
    }

    private DailyBossSaveDTO toDTO(DailyBossConfigDO c, List<DailyBossRewardDO> allRewards) {
        List<RewardDTO> rewardDTOs = new ArrayList<>();
        for (DailyBossRewardDO r : allRewards) {
            if (r.getConfigId().equals(c.getId())) {
                rewardDTOs.add(RewardDTO.builder()
                        .id(r.getId())
                        .completeCount(r.getCompleteCount())
                        .rewardDesc(r.getRewardDesc())
                        .itemId(r.getItemId())
                        .quantity(r.getQuantity())
                        .sortOrder(r.getSortOrder())
                        .build());
            }
        }
        rewardDTOs.sort(Comparator.comparingInt(RewardDTO::getSortOrder));
        return DailyBossSaveDTO.builder()
                .id(c.getId())
                .bossKey(c.getBossKey())
                .bossName(c.getBossName())
                .bossMobId(c.getBossMobId())
                .sortOrder(c.getSortOrder())
                .enabled(c.getEnabled())
                .rewards(rewardDTOs)
                .build();
    }
}
