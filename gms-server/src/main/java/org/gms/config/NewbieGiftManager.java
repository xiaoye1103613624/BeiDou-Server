package org.gms.config;

import org.gms.dao.entity.NewbieGiftConfigDO;
import org.gms.dao.entity.NewbieGiftCurrencyDO;
import org.gms.dao.entity.NewbieGiftItemDO;
import org.gms.dao.entity.NewbieGiftRecordDO;
import org.gms.dao.mapper.NewbieGiftConfigMapper;
import org.gms.dao.mapper.NewbieGiftCurrencyMapper;
import org.gms.dao.mapper.NewbieGiftItemMapper;
import org.gms.dao.mapper.NewbieGiftRecordMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 新手礼包配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询可用礼包和领取状态。
 * </p>
 */
public class NewbieGiftManager {

    private static final Logger log = LoggerFactory.getLogger(NewbieGiftManager.class);

    /** 已启用的礼包缓存 */
    private static final Map<Long, NewbieGiftConfigDO> configCache = new ConcurrentHashMap<>();

    private NewbieGiftManager() {}

    // ==================== 缓存管理 ====================

    /** 刷新缓存 */
    public static synchronized void reload() {
        configCache.clear();
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var configMapper = context.getBean(NewbieGiftConfigMapper.class);
                List<NewbieGiftConfigDO> configs = configMapper.selectAll();
                for (NewbieGiftConfigDO c : configs) {
                    if (c.getEnabled() != null && c.getEnabled() == 1) {
                        configCache.put(c.getId(), c);
                    }
                }
                log.info("NewbieGiftManager 缓存已刷新：{} 个启用的礼包", configCache.size());
            }
        } catch (Exception e) {
            log.error("刷新新手礼包缓存失败", e);
        }
    }

    // ==================== 查询方法（供JS脚本调用） ====================

    /** 获取指定玩家可领取的礼包列表 */
    public static List<Map<String, Object>> getAvailableGifts(int characterLevel, int characterId) {
        if (configCache.isEmpty()) reload();

        List<Map<String, Object>> result = new ArrayList<>();
        try {
            var context = ServerManager.getApplicationContext();
            if (context == null) return result;

            var recordMapper = context.getBean(NewbieGiftRecordMapper.class);
            List<NewbieGiftRecordDO> claimed = recordMapper.selectAll();

            for (NewbieGiftConfigDO config : configCache.values()) {
                // 等级检查
                if (characterLevel < config.getMinLevel() || characterLevel > config.getMaxLevel()) {
                    continue;
                }
                // 是否已领取
                boolean alreadyClaimed = false;
                for (NewbieGiftRecordDO r : claimed) {
                    if (r.getCharacterId().equals(characterId) && r.getGiftId().equals(config.getId())) {
                        alreadyClaimed = true;
                        break;
                    }
                }
                if (alreadyClaimed) continue;

                Map<String, Object> info = new LinkedHashMap<>();
                info.put("id", config.getId());
                info.put("name", config.getGiftName());
                info.put("minLevel", config.getMinLevel());
                info.put("maxLevel", config.getMaxLevel());
                result.add(info);
            }
        } catch (Exception e) {
            log.error("查询可用礼包失败", e);
        }
        return result;
    }

    /** 获取礼包配置信息 */
    public static Map<String, Object> getGiftInfo(long giftId) {
        NewbieGiftConfigDO config = configCache.get(giftId);
        if (config == null) return null;
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("id", config.getId());
        info.put("name", config.getGiftName());
        info.put("minLevel", config.getMinLevel());
        info.put("maxLevel", config.getMaxLevel());
        return info;
    }

    /** 获取礼包的物品列表（直连查询） */
    public static List<NewbieGiftItemDO> getGiftItems(long giftId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var itemMapper = context.getBean(NewbieGiftItemMapper.class);
                return itemMapper.selectListByQuery(
                        com.mybatisflex.core.query.QueryWrapper.create().where("gift_id = ?", giftId));
            }
        } catch (Exception e) {
            log.error("查询礼包物品失败", e);
        }
        return Collections.emptyList();
    }

    /** 获取礼包的货币列表（直连查询） */
    public static List<NewbieGiftCurrencyDO> getGiftCurrencies(long giftId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var currencyMapper = context.getBean(NewbieGiftCurrencyMapper.class);
                return currencyMapper.selectListByQuery(
                        com.mybatisflex.core.query.QueryWrapper.create().where("gift_id = ?", giftId));
            }
        } catch (Exception e) {
            log.error("查询礼包货币失败", e);
        }
        return Collections.emptyList();
    }

    /** 领取礼包（事务性插入记录），返回 true 表示成功 */
    public static boolean claimGift(int characterId, long giftId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var recordMapper = context.getBean(NewbieGiftRecordMapper.class);
                // 再次检查是否已领取
                List<NewbieGiftRecordDO> existing = recordMapper.selectListByQuery(
                        com.mybatisflex.core.query.QueryWrapper.create()
                                .where("character_id = ?", characterId)
                                .where("gift_id = ?", giftId));
                if (!existing.isEmpty()) return false;

                recordMapper.insert(NewbieGiftRecordDO.builder()
                        .characterId(characterId)
                        .giftId(giftId)
                        .build());
                return true;
            }
        } catch (Exception e) {
            log.error("领取礼包记录失败", e);
        }
        return false;
    }

    /** 检查是否已领取 */
    public static boolean hasClaimed(int characterId, long giftId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var recordMapper = context.getBean(NewbieGiftRecordMapper.class);
                List<NewbieGiftRecordDO> records = recordMapper.selectListByQuery(
                        com.mybatisflex.core.query.QueryWrapper.create()
                                .where("character_id = ?", characterId)
                                .where("gift_id = ?", giftId));
                return !records.isEmpty();
            }
        } catch (Exception e) {
            log.error("查询领取状态失败", e);
        }
        return false;
    }
}
