package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.AlchemyTierManager;
import org.gms.dao.entity.AlchemistDO;
import org.gms.dao.mapper.AlchemistMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 炼药师（药剂师副职业）服务。等级/经验按角色隔离，经验只增不减，升级不会重置为0。
 * <p>
 * 品级曲线由共享品级表（{@link AlchemyTierManager}，type=炼药）提供，
 * 默认入门/普通/职业/大师/宗师五档，最高品级解锁无上限。炼制不同品级的药水获得对应经验，
 * 且需要炼药师等级达到对应品级才能炼制该品级的药水。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlchemistService {

    /** 炼药师副职业类型常量 */
    public static final int TIER_TYPE = AlchemyTierManager.TYPE_ALCHEMIST;

    private final AlchemistMapper alchemistMapper;

    /**
     * 根据累计经验计算当前所处等级下标（品级配置来自数据库缓存）。
     */
    public int getTierIndex(long exp) {
        return AlchemyTierManager.getTierIndex(TIER_TYPE, exp);
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
     * @param tierIndex 品级下标（对应品级表，0=入门 1=普通 2=职业 3=大师 4=宗师）
     * @param expGain   炼制成功获得的炼药师经验（由配方决定）
     * @return 增加后的累计经验
     * @throws IllegalArgumentException 品级不合法或炼药师等级未达到该品级
     */
    @Transactional
    public long brew(Integer characterId, int tierIndex, int expGain) {
        if (tierIndex < 0 || tierIndex >= AlchemyTierManager.getTierCount(TIER_TYPE)) {
            throw new IllegalArgumentException("药水品级不合法");
        }
        AlchemistDO alchemist = getOrCreate(characterId);
        int currentTierIndex = getTierIndex(alchemist.getExp());
        if (currentTierIndex < tierIndex) {
            throw new IllegalArgumentException("炼药师等级不足，无法炼制该品级的药水");
        }
        return addExp(characterId, expGain);
    }
}
