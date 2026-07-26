package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.dao.entity.CharacterSponsorDO;
import org.gms.dao.entity.SponsorClaimDO;
import org.gms.dao.entity.SponsorConfigDO;
import org.gms.dao.entity.SponsorRewardDO;
import org.gms.dao.mapper.CharacterSponsorMapper;
import org.gms.dao.mapper.SponsorClaimMapper;
import org.gms.dao.mapper.SponsorConfigMapper;
import org.gms.dao.mapper.SponsorRewardMapper;
import org.gms.model.pojo.SponsorConfigView;
import org.gms.model.pojo.SponsorRewardView;
import org.gms.server.CashShop;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色赞助服务。
 * <ul>
 *   <li>总赞助 totalSponsor：充值累加，只增不减，用于档位达标领奖</li>
 *   <li>可消费赞助 spendableSponsor：充值增加，商店购买扣减</li>
 * </ul>
 */
@Slf4j
@Service("sponsorService")
@AllArgsConstructor
public class SponsorService {

    private final CharacterSponsorMapper sponsorMapper;
    private final SponsorConfigMapper configMapper;
    private final SponsorRewardMapper rewardMapper;
    private final SponsorClaimMapper claimMapper;

    // ==================== 余额 ====================

    @Transactional
    public CharacterSponsorDO getOrCreate(int characterId) {
        CharacterSponsorDO row = sponsorMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        if (row != null) {
            return row;
        }
        Date now = new Date();
        row = CharacterSponsorDO.builder()
                .characterId(characterId)
                .totalSponsor(0)
                .spendableSponsor(0)
                .createTime(now)
                .updateTime(now)
                .build();
        sponsorMapper.insert(row);
        return row;
    }

    /** 供赞助中心脚本：getRecordByPlayerId → getTotalSponsor / getSpendableSponsor */
    @Transactional
    public CharacterSponsorDO getRecordByPlayerId(int playerId) {
        return getOrCreate(playerId);
    }

    public int getTotalSponsor(int characterId) {
        return safeInt(getOrCreate(characterId).getTotalSponsor());
    }

    public int getSpendableSponsor(int characterId) {
        return safeInt(getOrCreate(characterId).getSpendableSponsor());
    }

    /**
     * 充值入账：总赞助与可消费赞助同时增加。
     *
     * @return 入账后的记录
     */
    @Transactional
    public CharacterSponsorDO recharge(int characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }
        CharacterSponsorDO row = getOrCreate(characterId);
        long total = (long) safeInt(row.getTotalSponsor()) + amount;
        long spendable = (long) safeInt(row.getSpendableSponsor()) + amount;
        if (total > Integer.MAX_VALUE || spendable > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("赞助余额溢出");
        }
        row.setTotalSponsor((int) total);
        row.setSpendableSponsor((int) spendable);
        row.setUpdateTime(new Date());
        sponsorMapper.update(row);
        log.info("角色 {} 赞助充值 +{}，总赞助={}，可消费={}", characterId, amount, total, spendable);
        return row;
    }

    /**
     * 仅增加可消费赞助（不计入总赞助）。一般不用于充值。
     */
    @Transactional
    public CharacterSponsorDO addSpendableOnly(int characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("增加金额必须大于0");
        }
        CharacterSponsorDO row = getOrCreate(characterId);
        long spendable = (long) safeInt(row.getSpendableSponsor()) + amount;
        if (spendable > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("可消费赞助溢出");
        }
        row.setSpendableSponsor((int) spendable);
        row.setUpdateTime(new Date());
        sponsorMapper.update(row);
        return row;
    }

    /**
     * 商店扣减可消费赞助；不足则抛异常。
     */
    @Transactional
    public void spend(int characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("扣减金额必须大于0");
        }
        CharacterSponsorDO row = getOrCreate(characterId);
        int bal = safeInt(row.getSpendableSponsor());
        if (bal < amount) {
            throw new IllegalArgumentException("赞助不足，需要 " + amount + "，当前 " + bal);
        }
        row.setSpendableSponsor(bal - amount);
        row.setUpdateTime(new Date());
        sponsorMapper.update(row);
        log.info("角色 {} 消费赞助 -{}，剩余可消费={}", characterId, amount, bal - amount);
    }

    /**
     * 尝试扣减；成功返回 true，余额不足返回 false。
     */
    @Transactional
    public boolean trySpend(int characterId, int amount) {
        try {
            spend(characterId, amount);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ==================== 档位配置 / 领取 ====================

    /** 启用中的档位（含奖励），按 sort_order、amount 排序 */
    public List<SponsorConfigView> listConfigs() {
        List<SponsorConfigDO> configs = configMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("enabled = ?", 1)
                        .orderBy("sort_order", true)
                        .orderBy("amount", true));
        List<SponsorConfigView> views = new ArrayList<>();
        for (SponsorConfigDO c : configs) {
            views.add(toView(c));
        }
        return views;
    }

    public boolean isClaimed(int characterId, int configId) {
        SponsorClaimDO claim = claimMapper.selectOneByQuery(
                QueryWrapper.create()
                        .where("character_id = ?", characterId)
                        .and("config_id = ?", configId));
        return claim != null;
    }

    /**
     * 领取档位奖励（校验总赞助、未领取、背包），成功返回描述文案。
     */
    @Transactional
    public String claimReward(int playerId, int configId, Character chr) {
        if (chr == null || chr.getId() != playerId) {
            throw new IllegalArgumentException("角色状态异常");
        }
        SponsorConfigDO config = configMapper.selectOneById(configId);
        if (config == null || config.getEnabled() == null || config.getEnabled() != 1) {
            throw new IllegalArgumentException("档位不存在或已停用");
        }
        if (isClaimed(playerId, configId)) {
            throw new IllegalArgumentException("该档奖励已经领取过了");
        }
        int total = getTotalSponsor(playerId);
        int need = safeInt(config.getAmount());
        if (total < need) {
            throw new IllegalArgumentException("总赞助不足，当前 " + total + "，需要 " + need);
        }

        List<SponsorRewardDO> rewards = rewardMapper.selectListByQuery(
                QueryWrapper.create().where("config_id = ?", configId));
        // 预检背包
        for (SponsorRewardDO r : rewards) {
            if ("item".equalsIgnoreCase(r.getType()) && safeInt(r.getItemId()) > 0) {
                if (!InventoryManipulator.checkSpace(chr.getClient(), r.getItemId(),
                        (short) Math.max(1, safeInt(r.getQty())), null)) {
                    throw new IllegalArgumentException("背包空间不足");
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (SponsorRewardDO r : rewards) {
            String type = r.getType() == null ? "" : r.getType().toLowerCase();
            int qty = safeInt(r.getQty());
            if (qty <= 0) {
                continue;
            }
            switch (type) {
                case "nx" -> {
                    chr.getCashShop().gainCash(CashShop.NX_CREDIT, qty);
                    sb.append("点券×").append(qty).append(" ");
                }
                case "maple" -> {
                    chr.getCashShop().gainCash(CashShop.MAPLE_POINT, qty);
                    sb.append("抵用券×").append(qty).append(" ");
                }
                case "meso" -> {
                    chr.gainMeso(qty, false);
                    sb.append("金币×").append(qty).append(" ");
                }
                case "item" -> {
                    int itemId = safeInt(r.getItemId());
                    if (itemId > 0) {
                        InventoryManipulator.addById(chr.getClient(), itemId, (short) qty);
                        sb.append("道具#").append(itemId).append("×").append(qty).append(" ");
                    }
                }
                default -> log.warn("未知赞助奖励类型: {}", type);
            }
        }

        claimMapper.insert(SponsorClaimDO.builder()
                .characterId(playerId)
                .configId(configId)
                .claimTime(new Date())
                .build());

        String msg = sb.length() == 0 ? "领取成功" : ("获得：" + sb);
        log.info("角色 {} 领取赞助档位 {}，{}", playerId, configId, msg);
        return msg;
    }

    private SponsorConfigView toView(SponsorConfigDO c) {
        List<SponsorRewardDO> rewards = rewardMapper.selectListByQuery(
                QueryWrapper.create().where("config_id = ?", c.getId()));
        List<SponsorRewardView> rewardViews = new ArrayList<>();
        for (SponsorRewardDO r : rewards) {
            rewardViews.add(SponsorRewardView.builder()
                    .type(r.getType())
                    .id(safeInt(r.getItemId()))
                    .qty(safeInt(r.getQty()))
                    .build());
        }
        return SponsorConfigView.builder()
                .id(safeInt(c.getId()))
                .name(c.getName() != null ? c.getName() : "")
                .amount(safeInt(c.getAmount()))
                .rewards(rewardViews)
                .build();
    }

    private static int safeInt(Integer v) {
        return v == null ? 0 : v;
    }
}
