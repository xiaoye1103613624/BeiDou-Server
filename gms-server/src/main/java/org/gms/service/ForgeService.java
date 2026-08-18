package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.AlchemyTierManager;
import org.gms.dao.entity.ForgeDO;
import org.gms.dao.mapper.ForgeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 锻造师副职业服务。等级/经验按角色隔离，经验只增不减，升级不会重置为0，
 * 经验池与炼药师({@link AlchemistService})、炼金师({@link AlchemyService})完全独立。
 * <p>
 * 品级曲线由共享品级表（{@link AlchemyTierManager}，type=锻造）提供，默认与炼药/炼金一致
 * （入门/普通/职业/大师/宗师，宗师无上限），后台可独立调整。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class ForgeService {

    /** 锻造师副职业类型常量 */
    public static final int TIER_TYPE = AlchemyTierManager.TYPE_FORGE;

    private final ForgeMapper forgeMapper;

    /**
     * 根据累计经验计算当前所处等级下标（品级配置来自数据库缓存）。
     */
    public int getTierIndex(long exp) {
        return AlchemyTierManager.getTierIndex(TIER_TYPE, exp);
    }

    /**
     * 获取（或创建）角色的锻造师记录。
     */
    @Transactional
    public ForgeDO getOrCreate(Integer characterId) {
        ForgeDO forge = forgeMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        if (forge != null) {
            return forge;
        }
        Date now = new Date();
        forge = ForgeDO.builder()
                .characterId(characterId)
                .exp(0L)
                .createTime(now)
                .updateTime(now)
                .build();
        forgeMapper.insert(forge);
        return forge;
    }

    /**
     * 增加锻造师经验（升级不重置，超过最高等级跨度后经验继续累加但不再提升等级）。
     *
     * @return 增加后的累计经验
     */
    @Transactional
    public long addExp(Integer characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("增加的经验值必须大于0");
        }
        ForgeDO forge = getOrCreate(characterId);
        long newExp = forge.getExp() + amount;
        forge.setExp(newExp);
        forge.setUpdateTime(new Date());
        forgeMapper.update(forge);
        return newExp;
    }
}
