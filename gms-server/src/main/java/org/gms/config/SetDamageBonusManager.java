package org.gms.config;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.Inventory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.dao.entity.SetDamageBonusConfigDO;
import org.gms.dao.mapper.SetDamageBonusConfigMapper;
import org.gms.manager.ServerManager;
import org.gms.server.ItemInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 套装伤害加成配置的静态缓存管理器。
 * <p>
 * 与 {@link EquipDamageBonusManager} 结构一致，区别在于配置维度是"套装ID + 穿戴件数档位"而非单件装备。
 * 穿戴件数达到某档位即视为该档位生效，与多个档位同时满足时可叠加（沿用WZ原生套装属性的叠加规则）。
 * 不做缓存，配置量小，攻击时按需计算即可。
 * </p>
 */
public class SetDamageBonusManager {

    private static final Logger log = LoggerFactory.getLogger(SetDamageBonusManager.class);

    /** 套装ID → 该套装所有启用档位配置（按tierCount升序无要求，遍历时逐个比较即可） */
    private static final Map<Integer, List<SetDamageBonusConfigDO>> configMap = new ConcurrentHashMap<>();

    private SetDamageBonusManager() {
    }

    /** 加载配置到缓存 */
    public static synchronized void load(List<SetDamageBonusConfigDO> configs) {
        configMap.clear();
        int enabledCount = 0;
        for (SetDamageBonusConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                configMap.computeIfAbsent(c.getSetItemId(), k -> new ArrayList<>()).add(c);
                enabledCount++;
            }
        }
        log.info("SetDamageBonusManager 缓存已刷新：总配置 {} 条，启用的 {} 条", configs.size(), enabledCount);
    }

    /** 手动强制刷新缓存（从数据库重新加载） */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(SetDamageBonusConfigMapper.class);
                load(mapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载套装伤害加成配置");
            }
        } catch (Exception e) {
            log.error("重新加载套装伤害加成配置失败", e);
        }
    }

    /**
     * 汇总角色当前穿戴装备(EQUIPPED库存)所属套装的伤害加成。
     *
     * @param chr 角色
     * @return 汇总结果（百分比之和）
     */
    public static Bonus aggregate(Character chr) {
        if (configMap.isEmpty()) {
            return Bonus.EMPTY;
        }

        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        Map<Integer, Integer> wornCountBySet = new HashMap<>();
        Inventory equipped = chr.getInventory(InventoryType.EQUIPPED);
        for (Item item : equipped) {
            if (item == null || !(item instanceof Equip)) {
                continue;
            }
            int setId = ii.getSetItemID(item.getItemId());
            if (setId > 0) {
                wornCountBySet.merge(setId, 1, Integer::sum);
            }
        }
        if (wornCountBySet.isEmpty()) {
            return Bonus.EMPTY;
        }

        int damagePct = 0;
        int bossDamagePct = 0;
        List<SetBonus> items = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : wornCountBySet.entrySet()) {
            List<SetDamageBonusConfigDO> tiers = configMap.get(entry.getKey());
            if (tiers == null) {
                continue;
            }
            int wornCount = entry.getValue();
            for (SetDamageBonusConfigDO tier : tiers) {
                if (tier.getTierCount() == null || tier.getTierCount() > wornCount) {
                    continue;
                }
                int tierDamagePct = tier.getDamagePct() != null ? tier.getDamagePct() : 0;
                int tierBossDamagePct = tier.getBossDamagePct() != null ? tier.getBossDamagePct() : 0;

                damagePct += tierDamagePct;
                bossDamagePct += tierBossDamagePct;

                if (tierDamagePct != 0 || tierBossDamagePct != 0) {
                    items.add(new SetBonus(tier.getSetName(), tier.getTierCount(), tierDamagePct, tierBossDamagePct));
                }
            }
        }

        if (damagePct == 0 && bossDamagePct == 0) {
            return Bonus.EMPTY;
        }
        return new Bonus(damagePct, bossDamagePct, items);
    }

    /** 单个套装档位贡献的伤害加成明细 */
    public record SetBonus(String setName, int tierCount, int damagePct, int bossDamagePct) {
    }

    /** 套装伤害加成汇总结果 */
    public record Bonus(int damagePct, int bossDamagePct, List<SetBonus> items) {
        public static final Bonus EMPTY = new Bonus(0, 0, List.of());
    }
}
