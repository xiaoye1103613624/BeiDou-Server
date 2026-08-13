package org.gms.config;

import org.gms.dao.entity.AlchemyTierDO;
import org.gms.manager.ServerManager;
import org.gms.service.AlchemyTierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 副职业品级静态门面（炼金/炼药/锻造共用同一张 xy_alchemy_tier 表，按 type 隔离），
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 * <p>
 * 品级数据为静态配置，按 {@code type}（1=炼金 2=炼药 3=锻造）缓存；后台修改品级后需调用
 * {@link #reload()} 刷新缓存。
 * </p>
 * <p>
 * 品级下标 = 已启用品级列表按其 expStart 排序后的下标（0=入门 1=普通 2=职业 3=大师 4=宗师），
 * 与配方表 {@code tier_required} 的下标约定保持一致。
 * </p>
 */
public class AlchemyTierManager {

    private static final Logger log = LoggerFactory.getLogger(AlchemyTierManager.class);

    /** 副职业类型常量：1=炼金 2=炼药 3=锻造 */
    public static final int TYPE_ALCHEMY = 1;
    public static final int TYPE_ALCHEMIST = 2;
    public static final int TYPE_FORGE = 3;

    /** 副职业类型 → 已启用品级列表缓存（按显示顺序升序） */
    private static final Map<Integer, List<AlchemyTierDO>> enabledTierListCache = new ConcurrentHashMap<>();

    private AlchemyTierManager() {}

    private static AlchemyTierService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(AlchemyTierService.class);
    }

    /**
     * 手动强制刷新某副职业品级缓存（从数据库重新加载）。
     * 脚本中可调用: {@code AlchemyTierManager.reload(type)}
     */
    public static synchronized void reload(int type) {
        try {
            enabledTierListCache.put(type, getService().listEnabledTiers(type));
            log.info("AlchemyTierManager 刷新 [type={}] 品级缓存：共 {} 条", type, enabledTierListCache.get(type).size());
        } catch (Exception e) {
            log.error("刷新副职业[type={}]品级缓存失败", type, e);
        }
    }

    /** 兼容旧调用：未指定 type 时刷新全部。 */
    public static synchronized void reload() {
        reload(TYPE_ALCHEMY);
        reload(TYPE_ALCHEMIST);
        reload(TYPE_FORGE);
    }

    /** 获取缓存中的品级列表，缓存为空时触发一次加载。 */
    private static List<AlchemyTierDO> getCachedList(int type) {
        List<AlchemyTierDO> cached = enabledTierListCache.get(type);
        if (cached == null) {
            reload(type);
            cached = enabledTierListCache.get(type);
        }
        return cached != null ? cached : new ArrayList<>();
    }

    /** 查询某副职业品级总数（默认炼金）。 */
    public static int getTierCount() {
        return getTierCount(TYPE_ALCHEMY);
    }

    public static int getTierCount(int type) {
        return getCachedList(type).size();
    }

    /** 查询指定副职业品级名称（下标越界时返回"未知品级"）。 */
    public static String getTierName(int type, int tierIndex) {
        List<AlchemyTierDO> tiers = getCachedList(type);
        if (tierIndex < 0 || tierIndex >= tiers.size()) {
            return "未知品级";
        }
        return tiers.get(tierIndex).getName();
    }

    /** 兼容旧调用：默认炼金。 */
    public static String getTierName(int tierIndex) {
        return getTierName(TYPE_ALCHEMY, tierIndex);
    }

    /** 查询指定副职业品级是否为最高品级（无上限）。 */
    public static boolean isMaxTier(int type, int tierIndex) {
        List<AlchemyTierDO> tiers = getCachedList(type);
        if (tierIndex < 0 || tierIndex >= tiers.size()) {
            return false;
        }
        return tiers.get(tierIndex).getIsMax() != null && tiers.get(tierIndex).getIsMax() == 1;
    }

    public static boolean isMaxTier(int tierIndex) {
        return isMaxTier(TYPE_ALCHEMY, tierIndex);
    }

    /** 查询指定副职业品级的最低累计经验阈值。 */
    public static long getExpStart(int type, int tierIndex) {
        List<AlchemyTierDO> tiers = getCachedList(type);
        if (tierIndex < 0 || tierIndex >= tiers.size()) {
            return 0L;
        }
        return tiers.get(tierIndex).getExpStart() != null ? tiers.get(tierIndex).getExpStart() : 0L;
    }

    /** 兼容旧调用：默认炼金。 */
    public static long getExpStart(int tierIndex) {
        return getExpStart(TYPE_ALCHEMY, tierIndex);
    }

    /** 查询指定副职业品级的经验跨度（下一品级阈值 - 本品级阈值），最高品级返回 -1 表示无上限。 */
    public static long getExpSize(int type, int tierIndex) {
        List<AlchemyTierDO> tiers = getCachedList(type);
        if (tierIndex < 0 || tierIndex >= tiers.size()) {
            return -1L;
        }
        AlchemyTierDO tier = tiers.get(tierIndex);
        if (tier.getIsMax() != null && tier.getIsMax() == 1) {
            return -1L;
        }
        long current = tier.getExpStart() != null ? tier.getExpStart() : 0L;
        if (tierIndex + 1 < tiers.size()) {
            long next = tiers.get(tierIndex + 1).getExpStart() != null
                    ? tiers.get(tierIndex + 1).getExpStart() : 0L;
            return next - current;
        }
        return -1L;
    }

    /** 兼容旧调用：默认炼金。 */
    public static long getExpSize(int tierIndex) {
        return getExpSize(TYPE_ALCHEMY, tierIndex);
    }

    /**
     * 根据指定副职业累计经验计算当前所处品级下标（达到某品级 exp_start 即进入该品级，最高品级无上限）。
     */
    public static int getTierIndex(int type, long exp) {
        List<AlchemyTierDO> tiers = getCachedList(type);
        int index = 0;
        for (int i = 0; i < tiers.size(); i++) {
            long start = tiers.get(i).getExpStart() != null ? tiers.get(i).getExpStart() : 0L;
            if (exp >= start) {
                index = i;
            }
        }
        return index;
    }

    /** 兼容旧调用：默认炼金。 */
    public static int getTierIndex(long exp) {
        return getTierIndex(TYPE_ALCHEMY, exp);
    }
}