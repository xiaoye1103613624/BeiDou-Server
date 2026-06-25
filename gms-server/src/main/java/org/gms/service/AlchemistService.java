package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.AlchemistDO;
import org.gms.dao.mapper.AlchemistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 炼药师（药剂师副职业）服务。等级/经验按角色隔离，经验只增不减，升级不会重置为0。
 * <p>
 * 炼药师分5个等级：入门、普通、职业、大师、宗师。每个等级所需经验累计递增，
 * 宗师为最高等级，经验无上限（不会再升级）。炼制不同品级的药水获得固定经验，
 * 且需要炼药师等级达到对应品级才能炼制该品级的药水。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlchemistService {

    /** 炼药师等级定义：名称、该等级起始累计经验、该等级所需经验跨度、炼制该品级药水获得的经验、炼制消耗体力 */
    public record Tier(String name, long expStart, long expSize, int brewExp, int staminaCost) {
        public boolean isMax() {
            return expSize == Long.MAX_VALUE;
        }
    }

    public static final Tier[] TIERS = new Tier[]{
            new Tier("入门", 0L, 800L, 8, 1),
            new Tier("普通", 800L, 1600L, 16, 2),
            new Tier("职业", 2400L, 64000L, 64, 4),
            new Tier("大师", 66400L, 128000L, 128, 8),
            new Tier("宗师", 194400L, Long.MAX_VALUE, 256, 16),
    };

    private final AlchemistMapper alchemistMapper;

    /**
     * 根据累计经验计算当前所处等级下标。
     */
    public int getTierIndex(long exp) {
        int index = 0;
        for (int i = 0; i < TIERS.length; i++) {
            if (exp >= TIERS[i].expStart()) {
                index = i;
            }
        }
        return index;
    }

    /**
     * 获取（或创建）角色的炼药师记录。
     */
    @Transactional
    public AlchemistDO getOrCreate(Integer characterId) {
        AlchemistDO alchemist = alchemistMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        if (alchemist != null) {
            return alchemist;
        }
        Date now = new Date();
        alchemist = AlchemistDO.builder()
                .characterId(characterId)
                .exp(0L)
                .createTime(now)
                .updateTime(now)
                .build();
        alchemistMapper.insert(alchemist);
        return alchemist;
    }

    /**
     * 增加炼药师经验（升级不重置，超过最高等级跨度后经验继续累加但不再提升等级）。
     *
     * @return 增加后的累计经验
     */
    @Transactional
    public long addExp(Integer characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("增加的经验值必须大于0");
        }
        AlchemistDO alchemist = getOrCreate(characterId);
        long newExp = alchemist.getExp() + amount;
        alchemist.setExp(newExp);
        alchemist.setUpdateTime(new Date());
        alchemistMapper.update(alchemist);
        return newExp;
    }

    /**
     * 炼制指定品级的药水：校验炼药师等级是否达到该品级，扣除对应体力（由调用方处理），
     * 并增加该品级对应的固定经验。
     *
     * @param tierIndex 品级下标（对应 {@link #TIERS}）
     * @return 增加后的累计经验
     * @throws IllegalArgumentException 品级不合法或炼药师等级未达到该品级
     */
    @Transactional
    public long brew(Integer characterId, int tierIndex) {
        if (tierIndex < 0 || tierIndex >= TIERS.length) {
            throw new IllegalArgumentException("药水品级不合法");
        }
        AlchemistDO alchemist = getOrCreate(characterId);
        int currentTierIndex = getTierIndex(alchemist.getExp());
        if (currentTierIndex < tierIndex) {
            throw new IllegalArgumentException("炼药师等级不足，无法炼制该品级的药水");
        }
        return addExp(characterId, TIERS[tierIndex].brewExp());
    }
}
