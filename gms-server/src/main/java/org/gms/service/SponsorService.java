package org.gms.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.dao.entity.SponsorConfigDO;
import org.gms.dao.entity.SponsorLogDO;
import org.gms.dao.entity.SponsorRecordDO;
import org.gms.dao.mapper.SponsorConfigMapper;
import org.gms.dao.mapper.SponsorLogMapper;
import org.gms.dao.mapper.SponsorRecordMapper;
import org.gms.model.dto.SponsorConfigDTO;
import org.gms.net.server.Server;
import org.gms.server.CashShop;
import org.gms.server.ItemInformationProvider;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 赞助系统服务类
 * 负责赞助配置管理、赞助记录维护、奖励领取
 */
@Slf4j
@Service
@AllArgsConstructor
public class SponsorService {

    private final SponsorConfigMapper configMapper;
    private final SponsorRecordMapper recordMapper;
    private final SponsorLogMapper logMapper;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ==================== 配置管理 ====================

    /** 查询所有赞助配置 */
    public List<SponsorConfigDTO> listConfigs() {
        List<SponsorConfigDO> configs = configMapper.selectAll();
        return configs.stream()
                .sorted(Comparator.comparingInt(SponsorConfigDO::getAmount))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /** 保存赞助配置（新增/更新） */
    @Transactional
    public SponsorConfigDTO saveConfig(SponsorConfigDTO dto) {
        SponsorConfigDO config = SponsorConfigDO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .amount(dto.getAmount())
                .rewardsJson(toJson(dto.getRewards()))
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .comment(dto.getComment())
                .build();

        if (config.getId() != null) {
            config.setCreateTime(configMapper.selectOneById(config.getId()).getCreateTime());
            configMapper.update(config);
        } else {
            config.setCreateTime(new Date());
            configMapper.insert(config);
        }
        return toDTO(configMapper.selectOneById(config.getId()));
    }

    /** 删除赞助配置 */
    @Transactional
    public void deleteConfig(Long id) {
        configMapper.deleteById(id);
    }

    // ==================== 赞助记录 ====================

    /**
     * 查询玩家赞助记录
     */
    public SponsorRecordDO getRecordByPlayerId(Integer playerId) {
        return recordMapper.selectOneByQuery(
                QueryWrapper.create().where("player_id = ?", playerId));
    }

    /**
     * 增加玩家赞助金额（由CDK兑换或管理员操作触发）
     */
    @Transactional
    public void addSponsorAmount(Integer playerId, String playerName, Integer accountId,
                                  String accountName, int amount, int type, String detail) {
        SponsorRecordDO record = recordMapper.selectOneByQuery(
                QueryWrapper.create().where("player_id = ?", playerId));

        if (record == null) {
            record = SponsorRecordDO.builder()
                    .playerId(playerId)
                    .playerName(playerName)
                    .accountId(accountId)
                    .accountName(accountName)
                    .totalSponsor(amount)
                    .createTime(new Date())
                    .build();
            recordMapper.insert(record);
        } else {
            record.setTotalSponsor(record.getTotalSponsor() + amount);
            record.setPlayerName(playerName);
            record.setAccountId(accountId);
            record.setAccountName(accountName);
            recordMapper.update(record);
        }

        // 写日志
        SponsorLogDO logDO = SponsorLogDO.builder()
                .playerId(playerId)
                .playerName(playerName)
                .accountId(accountId)
                .type(type)
                .amount(amount)
                .detail(detail)
                .createTime(new Date())
                .build();
        logMapper.insert(logDO);

        log.info("赞助金额变动：player={}, amount={}, total={}, type={}",
                playerName, amount, record.getTotalSponsor(), type);
    }

    /**
     * 查询所有赞助记录（Web管理端用）
     */
    public List<SponsorRecordDO> listRecords(String playerName) {
        QueryWrapper qw = QueryWrapper.create();
        if (playerName != null && !playerName.isEmpty()) {
            qw.where("player_name LIKE ?", "%" + playerName + "%");
        }
        qw.orderBy("total_sponsor desc");
        return recordMapper.selectListByQuery(qw);
    }

    /**
     * 查询赞助日志
     */
    public List<SponsorLogDO> listLogs(String playerName, Integer type, String startTime, String endTime) {
        QueryWrapper qw = QueryWrapper.create();
        if (playerName != null && !playerName.isEmpty()) {
            qw.where("player_name LIKE ?", "%" + playerName + "%");
        }
        if (type != null) {
            qw.where("type = ?", type);
        }
        if (startTime != null && !startTime.isEmpty()) {
            qw.where("create_time >= ?", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            qw.where("create_time <= ?", endTime);
        }
        qw.orderBy("create_time desc");
        return logMapper.selectListByQuery(qw);
    }

    // ==================== 奖励领取 ====================

    /**
     * 获取玩家可领取的赞助奖励列表
     * @return Map<configId, 是否已领取>
     */
    public Map<Long, Boolean> getClaimStatus(Integer playerId) {
        SponsorRecordDO record = getRecordByPlayerId(playerId);
        int totalSponsor = record != null ? record.getTotalSponsor() : 0;

        List<SponsorConfigDO> configs = configMapper.selectListByQuery(
                QueryWrapper.create().where("enabled = ?", 1).orderBy("amount asc"));

        Map<Long, Boolean> status = new LinkedHashMap<>();
        for (SponsorConfigDO config : configs) {
            boolean canClaim = totalSponsor >= config.getAmount();
            status.put(config.getId(), canClaim);
        }
        return status;
    }

    /**
     * 领取赞助奖励（一次性的，通过CharacterExtendValue记录）
     */
    @Transactional
    public String claimReward(Integer playerId, Long configId, Character chr) {
        // 检查配置
        SponsorConfigDO config = configMapper.selectOneById(configId);
        if (config == null || config.getEnabled() == null || config.getEnabled() != 1) {
            return "该奖励配置不存在或已禁用。";
        }

        // 检查赞助额是否达标
        SponsorRecordDO record = getRecordByPlayerId(playerId);
        int totalSponsor = record != null ? record.getTotalSponsor() : 0;
        if (totalSponsor < config.getAmount()) {
            return "赞助金额不足！当前累计赞助 #r" + totalSponsor + "#k，需要 #r" + config.getAmount() + "#k。";
        }

        // 检查是否已领取（通过CharacterExtendValue持久化记录）
        String claimKey = "sponsor_claimed_" + configId;
        // 在Java层无法直接调用JS的ExtendValue，此处由调用方（NPC脚本）通过cm.getCharacterExtendValue检查
        // claimReward仅在脚本确认未领取后调用

        // 解析奖励JSON并发放
        Client c = chr.getClient();
        List<SponsorConfigDTO.RewardItem> rewards;
        try {
            rewards = objectMapper.readValue(config.getRewardsJson(),
                    new TypeReference<List<SponsorConfigDTO.RewardItem>>() {});
        } catch (Exception e) {
            log.error("解析赞助奖励JSON失败：configId={}", configId, e);
            return "奖励配置解析失败，请联系管理员。";
        }

        StringBuilder result = new StringBuilder();
        for (SponsorConfigDTO.RewardItem reward : rewards) {
            try {
                switch (reward.getType()) {
                    case "nx":
                        chr.getCashShop().gainCash(CashShop.NX_CREDIT, reward.getQty());
                        result.append("点券x").append(reward.getQty()).append("; ");
                        break;
                    case "meso":
                        chr.gainMeso(reward.getQty(), true);
                        result.append("金币x").append(reward.getQty()).append("; ");
                        break;
                    case "item":
                        if (reward.getId() != null && reward.getQty() != null) {
                            InventoryManipulator.addById(c, reward.getId(), reward.getQty().shortValue(), "", -1);
                            String itemName = ItemInformationProvider.getInstance().getName(reward.getId());
                            result.append(itemName != null ? itemName : ("道具#" + reward.getId()))
                                    .append("x").append(reward.getQty()).append("; ");
                        }
                        break;
                    default:
                        log.warn("未知赞助奖励类型：{}", reward.getType());
                }
            } catch (Exception e) {
                log.error("发放赞助奖励失败：configId={}, reward={}", configId, reward, e);
                return "发放奖励时发生错误（部分奖励可能已发放），请联系管理员。";
            }
        }

        log.info("赞助奖励领取成功：player={}, configAmount={}, rewards={}",
                chr.getName(), config.getAmount(), result);
        return "领取成功！获得：" + result.toString().trim();
    }

    // ==================== 辅助 ====================

    private SponsorConfigDTO toDTO(SponsorConfigDO config) {
        List<SponsorConfigDTO.RewardItem> rewards = new ArrayList<>();
        if (config.getRewardsJson() != null) {
            try {
                rewards = objectMapper.readValue(config.getRewardsJson(),
                        new TypeReference<List<SponsorConfigDTO.RewardItem>>() {});
            } catch (Exception e) {
                log.warn("解析赞助奖励JSON失败：configId={}", config.getId(), e);
            }
        }
        return SponsorConfigDTO.builder()
                .id(config.getId())
                .name(config.getName())
                .amount(config.getAmount())
                .rewards(rewards)
                .enabled(config.getEnabled())
                .comment(config.getComment())
                .createTime(config.getCreateTime() != null ? config.getCreateTime().toString() : null)
                .build();
    }

    private String toJson(List<SponsorConfigDTO.RewardItem> rewards) {
        if (rewards == null || rewards.isEmpty()) return "[]";
        try {
            return objectMapper.writeValueAsString(rewards);
        } catch (Exception e) {
            log.error("序列化赞助奖励JSON失败", e);
            return "[]";
        }
    }

    /** 查找在线角色（供NPC脚本调用） */
    public Character findOnlineCharacter(String playerName) {
        if (playerName == null || playerName.isEmpty()) return null;
        for (org.gms.net.server.world.World world : Server.getInstance().getWorlds()) {
            Character chr = world.getPlayerStorage().getCharacterByName(playerName);
            if (chr != null && chr.isLoggedIn()) return chr;
        }
        return null;
    }
}
