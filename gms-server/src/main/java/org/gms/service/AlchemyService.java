package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.AlchemyTierManager;
import org.gms.dao.entity.AlchemyDO;
import org.gms.dao.mapper.AlchemyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 炼金师副职业服务。等级/经验按角色隔离，经验只增不减，升级不会重置为0，
 * 经验池与炼药师（{@link AlchemistService}）完全独立。
 * <p>
 * 品级曲线（等级/升级经验值）在管理后台 xy_alchemy_tier 表中配置，由
 * {@link AlchemyTierManager} 缓存提供，默认 入门/普通/职业/大师/宗师 五档，最高品级无上限。
 * 调整品级无需改代码，后台修改后自动刷新缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlchemyService {

    private final AlchemyMapper alchemyMapper;

    /**
     * 根据累计经验计算当前所处等级下标（品级配置来自数据库缓存）。
     */
    public int getTierIndex(long exp) {
        return AlchemyTierManager.getTierIndex(AlchemyTierManager.TYPE_ALCHEMY, exp);
    }

    /**
     * 获取（或创建）角色的炼金师记录。
     */
    @Transactional
    public AlchemyDO getOrCreate(Integer characterId) {
        AlchemyDO alchemy = alchemyMapper.selectOneByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        if (alchemy != null) {
            return alchemy;
        }
        Date now = new Date();
        alchemy = AlchemyDO.builder()
                .characterId(characterId)
                .exp(0L)
                .createTime(now)
                .updateTime(now)
                .build();
        alchemyMapper.insert(alchemy);
        return alchemy;
    }

    /**
     * 增加炼金师经验（升级不重置，超过最高等级跨度后经验继续累加但不再提升等级）。
     *
     * @return 增加后的累计经验
     */
    @Transactional
    public long addExp(Integer characterId, int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("增加的经验值必须大于0");
        }
        AlchemyDO alchemy = getOrCreate(characterId);
        long newExp = alchemy.getExp() + amount;
        alchemy.setExp(newExp);
        alchemy.setUpdateTime(new Date());
        alchemyMapper.update(alchemy);
        return newExp;
    }
}
