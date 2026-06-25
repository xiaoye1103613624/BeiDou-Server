package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.AlchemyDO;
import org.gms.dao.mapper.AlchemyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 炼金师副职业服务。等级/经验按角色隔离，经验只增不减，升级不会重置为0，
 * 经验池与炼药师（{@link AlchemistService}）完全独立。
 * <p>
 * 品级曲线默认与炼药师相同（入门/普通/职业/大师/宗师，宗师无上限），如需独立调整
 * 直接修改 {@link #TIERS} 即可，不影响炼药师数据。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlchemyService {

    /** 炼金师等级定义：名称、该等级起始累计经验、该等级所需经验跨度 */
    public record Tier(String name, long expStart, long expSize) {
        public boolean isMax() {
            return expSize == Long.MAX_VALUE;
        }
    }

    public static final Tier[] TIERS = new Tier[]{
            new Tier("入门", 0L, 800L),
            new Tier("普通", 800L, 1600L),
            new Tier("职业", 2400L, 64000L),
            new Tier("大师", 66400L, 128000L),
            new Tier("宗师", 194400L, Long.MAX_VALUE),
    };

    private final AlchemyMapper alchemyMapper;

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
