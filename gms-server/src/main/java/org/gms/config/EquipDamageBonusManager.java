package org.gms.config;

import org.gms.client.Character;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.dao.entity.EquipDamageBonusConfigDO;
import org.gms.dao.mapper.EquipDamageBonusConfigMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装备伤害加成配置的静态缓存管理器。
 * <p>
 * 每件装备可单独配置：普通伤害百分比加成、Boss伤害百分比加成、额外攻击段数。
 * 角色换装/登录时调用 {@link #aggregate(Character)} 汇总当前穿戴装备的加成，
 * 结果应缓存到角色身上（参考套装伤害加成的做法），攻击时直接读缓存，避免每次攻击都遍历背包。
 * </p>
 */
public class EquipDamageBonusManager {

    private static final Logger log = LoggerFactory.getLogger(EquipDamageBonusManager.class);

    /** 物品ID → 配置实体映射（仅包含启用的配置） */
    private static final Map<Integer, EquipDamageBonusConfigDO> configMap = new ConcurrentHashMap<>();

    private EquipDamageBonusManager() {
    }

    /** 加载配置到缓存 */
    public static synchronized void load(List<EquipDamageBonusConfigDO> configs) {
        configMap.clear();
        int enabledCount = 0;
        for (EquipDamageBonusConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                configMap.put(c.getItemId(), c);
                enabledCount++;
            }
        }
        log.info("EquipDamageBonusManager 缓存已刷新：总配置 {} 条，启用的 {} 条", configs.size(), enabledCount);
    }

    /** 手动强制刷新缓存（从数据库重新加载） */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(EquipDamageBonusConfigMapper.class);
                load(mapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载装备伤害加成配置");
            }
        } catch (Exception e) {
            log.error("重新加载装备伤害加成配置失败", e);
        }
    }

    public static EquipDamageBonusConfigDO getConfig(int itemId) {
        return configMap.get(itemId);
    }

    /**
     * 汇总角色当前穿戴装备(EQUIPPED库存)的伤害加成。
     *
     * @param chr 角色
     * @return 汇总结果（百分比之和、Boss百分比之和、额外段数之和）
     */
    public static Bonus aggregate(Character chr) {
        if (configMap.isEmpty()) {
            return Bonus.EMPTY;
        }

        int damagePct = 0;
        int bossDamagePct = 0;
        long extraSegments = 0L;
        List<ItemBonus> items = new ArrayList<>();

        Inventory equipped = chr.getInventory(InventoryType.EQUIPPED);
        for (Item item : equipped) {
            if (item == null) {
                continue;
            }
            EquipDamageBonusConfigDO cfg = configMap.get(item.getItemId());
            if (cfg == null) {
                continue;
            }
            int itemDamagePct = cfg.getDamagePct() != null ? cfg.getDamagePct() : 0;
            int itemBossDamagePct = cfg.getBossDamagePct() != null ? cfg.getBossDamagePct() : 0;
            long itemExtraSegs = cfg.getFixedDamage() != null ? cfg.getFixedDamage() : 0L;

            damagePct += itemDamagePct;
            bossDamagePct += itemBossDamagePct;
            extraSegments += itemExtraSegs;

            if (itemDamagePct != 0 || itemBossDamagePct != 0 || itemExtraSegs != 0L) {
                String name = cfg.getItemName();
                if (name == null || name.isBlank()) {
                    // 回退：从WZ数据获取物品名称
                    var ii = org.gms.server.ItemInformationProvider.getInstance();
                    name = ii.getName(cfg.getItemId());
                    if (name == null || name.isBlank()) {
                        name = "物品#" + cfg.getItemId();
                    }
                }
                items.add(new ItemBonus(name, itemDamagePct, itemBossDamagePct, itemExtraSegs));
            }
        }

        if (damagePct == 0 && bossDamagePct == 0 && extraSegments == 0L) {
            return Bonus.EMPTY;
        }
        return new Bonus(damagePct, bossDamagePct, extraSegments, items);
    }

    /**
     * 将数值格式化为易读的字符串：&le;1万显示具体数值，&lt;1亿显示"x.y万"，&ge;1亿显示"x.y亿"。
     */
    public static String formatNumber(long n) {
        if (n <= 10000L) {
            return String.valueOf(n);
        }
        if (n < 100000000L) {
            return String.format("%.1f万", n / 10000.0);
        }
        return String.format("%.1f亿", n / 100000000.0);
    }

    /**
     * 单件装备贡献的伤害加成明细
     * @param itemName     装备名称
     * @param damagePct    普通伤害加成百分比
     * @param bossDamagePct Boss伤害加成百分比
     * @param extraSegments 额外攻击段数
     */
    public record ItemBonus(String itemName, int damagePct, int bossDamagePct, long extraSegments) {
    }

    /** 装备伤害加成汇总结果（百分比的伤害加成已在 AbstractDealDamageHandler 中按总伤害比例折算，extraSegments 作为额外攻击段数叠加） */
    public record Bonus(int damagePct, int bossDamagePct, long extraSegments, List<ItemBonus> items) {
        public static final Bonus EMPTY = new Bonus(0, 0, 0L, List.of());
    }
}
