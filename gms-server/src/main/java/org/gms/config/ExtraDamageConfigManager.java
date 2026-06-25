package org.gms.config;

import org.gms.client.Character;
import org.gms.dao.entity.ExtraDamageConfigDO;
import org.gms.dao.mapper.ExtraDamageConfigMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 额外伤害全局配置的静态缓存管理器。
 * <p>
 * 参考079破功版"额外伤害"机制：按角色总属性(力量/敏捷/智力/运气/物攻/魔攻/血量/魔量)
 * 分别乘以可配置的比例系数后求和，得到一笔额外伤害，在普通伤害结算之后叠加到怪物身上。
 * 触发条件为"本次普通伤害达到阈值" 或 "玩家持有指定触发道具"，两者满足任一即可。
 * 注：v83客户端原生没有总物防/总魔防的角色侧聚合字段，wdef_ratio/mdef_ratio暂不参与计算，仅作配置预留。
 * </p>
 */
public class ExtraDamageConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ExtraDamageConfigManager.class);

    private static volatile ExtraDamageConfigDO config;

    private ExtraDamageConfigManager() {
    }

    /** 加载配置到缓存 */
    public static void load(ExtraDamageConfigDO cfg) {
        config = cfg;
        log.info("ExtraDamageConfigManager 缓存已刷新：enabled={}", cfg != null ? cfg.getEnabled() : null);
    }

    /** 手动强制刷新缓存（从数据库重新加载） */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(ExtraDamageConfigMapper.class);
                load(mapper.selectOneById(1L));
            } else {
                log.warn("Spring 上下文不可用，无法重新加载额外伤害配置");
            }
        } catch (Exception e) {
            log.error("重新加载额外伤害配置失败", e);
        }
    }

    public static ExtraDamageConfigDO getConfig() {
        return config;
    }

    /**
     * 计算本次攻击应叠加的额外伤害。
     *
     * @param player 攻击的角色
     * @param damage 本次普通伤害结算值（已完成暴击/技能倍率等计算后的真实伤害）
     * @return 额外伤害数值，未触发或未启用时返回0
     */
    public static long calcExtraDamage(Character player, long damage) {
        ExtraDamageConfigDO cfg = config;
        if (cfg == null || cfg.getEnabled() == null || cfg.getEnabled() != 1) {
            return 0L;
        }

        long threshold = cfg.getDamageThreshold() != null ? cfg.getDamageThreshold() : 0L;
        int triggerItemId = cfg.getTriggerItemId() != null ? cfg.getTriggerItemId() : 0;

        boolean triggeredByDamage = damage >= threshold;
        boolean triggeredByItem = triggerItemId > 0 && player.haveItem(triggerItemId);
        if (!triggeredByDamage && !triggeredByItem) {
            return 0L;
        }

        long extra = 0L;
        extra += (long) player.getTotalStr() * orZero(cfg.getStrRatio());
        extra += (long) player.getTotalDex() * orZero(cfg.getDexRatio());
        extra += (long) player.getTotalInt() * orZero(cfg.getIntRatio());
        extra += (long) player.getTotalLuk() * orZero(cfg.getLukRatio());
        extra += (long) player.getTotalWatk() * orZero(cfg.getWatkRatio());
        extra += (long) player.getTotalMagic() * orZero(cfg.getMatkRatio());
        extra += (long) player.getCurrentMaxHp() * orZero(cfg.getHpRatio());
        extra += (long) player.getCurrentMaxMp() * orZero(cfg.getMpRatio());

        return Math.max(extra, 0L);
    }

    /** 是否展示头顶悬浮提示 */
    public static boolean isShowHint() {
        ExtraDamageConfigDO cfg = config;
        return cfg != null && cfg.getShowHint() != null && cfg.getShowHint() == 1;
    }

    /** 是否展示系统喇叭(黄字) */
    public static boolean isShowMessage() {
        ExtraDamageConfigDO cfg = config;
        return cfg != null && cfg.getShowMessage() != null && cfg.getShowMessage() == 1;
    }

    private static int orZero(Integer v) {
        return v != null ? v : 0;
    }
}
