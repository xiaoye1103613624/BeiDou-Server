package org.gms.config;

import org.gms.dao.entity.BossConfigDO;
import org.gms.server.life.Monster;
import org.gms.server.life.OverrideMonsterStats;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BOSS属性倍率配置管理器。
 * 服务启动时由 BossConfigService 加载所有启用的配置到内存，
 * 在怪物生成时通过 {@link #applyBossConfig(Monster)} 自动应用倍率覆盖。
 */
public class BossConfigManager {

    /** Boss配置缓存（怪物ID -> 配置） */
    private static final Map<Integer, BossConfigDO> configMap = new ConcurrentHashMap<>();

    private BossConfigManager() {
    }

    /**
     * 加载配置列表到内存缓存（由 BossConfigService 在 @PostConstruct 时调用）
     */
    public static void load(List<BossConfigDO> configs) {
        configMap.clear();
        for (BossConfigDO config : configs) {
            configMap.put(config.getMobId(), config);
        }
    }

    /**
     * 根据怪物ID获取配置（仅返回已启用的）
     */
    public static BossConfigDO getBossConfig(int mobId) {
        BossConfigDO config = configMap.get(mobId);
        if (config != null && config.getEnabled() != null && config.getEnabled() == 1) {
            return config;
        }
        return null;
    }

    /**
     * 新增配置后刷新缓存
     */
    public static void addConfig(BossConfigDO config) {
        if (config.getEnabled() != null && config.getEnabled() == 1) {
            configMap.put(config.getMobId(), config);
        }
    }

    /**
     * 更新配置后刷新缓存
     */
    public static void updateConfig(BossConfigDO config) {
        if (config.getEnabled() != null && config.getEnabled() == 1) {
            configMap.put(config.getMobId(), config);
        } else {
            configMap.remove(config.getMobId());
        }
    }

    /**
     * 删除配置后刷新缓存
     */
    public static void removeConfig(int mobId) {
        configMap.remove(mobId);
    }

    /**
     * 对即将生成的怪物应用BOSS属性倍率。
     * 仅在 MapleMap.spawnMonster() 中调用，对脚本透明。
     *
     * @param monster 刚创建尚未发送给客户端的怪物实例
     */
    public static void applyBossConfig(Monster monster) {
        BossConfigDO config = getBossConfig(monster.getId());
        if (config == null) {
            return;
        }

        // —— 1. 应用绝对值覆盖（先于倍率，直接修改stats） ——
        if (config.getLevel() != null) {
            monster.getStats().setLevel(config.getLevel());
        }
        if (config.getHp() != null) {
            monster.getStats().setHp(config.getHp());
        }
        if (config.getMp() != null) {
            monster.getStats().setMp(config.getMp());
        }
        if (config.getExp() != null) {
            monster.getStats().setExp(config.getExp());
        }
        if (config.getPdd() != null) {
            monster.getStats().setPDDamage(config.getPdd());
        }
        if (config.getMdd() != null) {
            monster.getStats().setMDDamage(config.getMdd());
        }
        if (config.getAcc() != null) {
            monster.getStats().acc = config.getAcc();
        }
        if (config.getEva() != null) {
            monster.getStats().eva = config.getEva();
        }

        // —— 2. 应用倍率覆盖 ——
        BigDecimal hpMul = config.getHpMultiplier();
        BigDecimal expMul = config.getExpMultiplier();
        BigDecimal dmgMul = config.getDamageMultiplier();

        if (hpMul == null && expMul == null && dmgMul == null) {
            return;
        }

        // HP倍率
        if (hpMul != null && hpMul.compareTo(BigDecimal.ONE) != 0) {
            int newHp = (int) (monster.getMobMaxHp() * hpMul.doubleValue());
            OverrideMonsterStats override = new OverrideMonsterStats();
            override.setOHp(newHp);
            override.setOMp(monster.getMp());
            monster.setOverrideStats(override);
        }

        // 经验倍率
        if (expMul != null && expMul.compareTo(BigDecimal.ONE) != 0) {
            int newExp = (int) (monster.getStats().getExp() * expMul.doubleValue());
            monster.getStats().setExp(newExp);
        }

        // 伤害倍率
        if (dmgMul != null && dmgMul.compareTo(BigDecimal.ONE) != 0) {
            double mul = dmgMul.doubleValue();
            monster.getStats().setPADamage((int) (monster.getStats().getPADamage() * mul));
            monster.getStats().setMADamage((int) (monster.getStats().getMADamage() * mul));
        }
    }
}