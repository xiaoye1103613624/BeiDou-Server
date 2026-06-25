package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.GemLevelDO;
import org.gms.dao.entity.GemSocketLevelDO;
import org.gms.dao.mapper.GemLevelMapper;
import org.gms.dao.mapper.GemSocketLevelMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 * 宝石合成 / 宝石镶嵌 静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 * <p>
 * 与 {@link EquipEnhanceManager} 一致，全部查询直连数据库（不走 static 缓存），
 * 避免 GraalVM 脚本 ClassLoader 隔离导致缓存不可见的问题。
 * 属性叠加复用 {@link EquipEnhanceManager#safeAddStat(short, int)}，钳制范围保持一致。
 * </p>
 */
public class GemManager {

    private static final Logger log = LoggerFactory.getLogger(GemManager.class);

    private GemManager() {}

    // ==================== 宝石等级（合成用） ====================

    /** 查询所有已启用的宝石等级配置，按等级升序 */
    public static List<GemLevelDO> queryEnabledGemLevels() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(GemLevelMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("enabled = 1").orderBy("gem_level", true));
            }
        } catch (Exception e) {
            log.error("查询宝石等级配置失败", e);
        }
        return Collections.emptyList();
    }

    /** 按等级查询宝石配置，不存在或未启用返回 null */
    public static GemLevelDO queryGemLevel(int gemLevel) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(GemLevelMapper.class);
                List<GemLevelDO> list = mapper.selectListByQuery(
                        QueryWrapper.create().where("gem_level = ?", gemLevel).where("enabled = 1"));
                return list.isEmpty() ? null : list.get(0);
            }
        } catch (Exception e) {
            log.error("查询宝石等级配置失败", e);
        }
        return null;
    }

    /** 按物品ID反查宝石等级配置，不存在或未启用返回 null */
    public static GemLevelDO queryGemLevelByItemId(int itemId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(GemLevelMapper.class);
                List<GemLevelDO> list = mapper.selectListByQuery(
                        QueryWrapper.create().where("item_id = ?", itemId).where("enabled = 1"));
                return list.isEmpty() ? null : list.get(0);
            }
        } catch (Exception e) {
            log.error("按物品ID查询宝石等级配置失败", e);
        }
        return null;
    }

    // ==================== 宝石镶嵌 ====================

    /** 按宝石等级查询镶嵌配置，不存在或未启用返回 null */
    public static GemSocketLevelDO querySocketLevel(int gemLevel) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(GemSocketLevelMapper.class);
                List<GemSocketLevelDO> list = mapper.selectListByQuery(
                        QueryWrapper.create().where("gem_level = ?", gemLevel).where("enabled = 1"));
                return list.isEmpty() ? null : list.get(0);
            }
        } catch (Exception e) {
            log.error("查询宝石镶嵌配置失败", e);
        }
        return null;
    }

    /**
     * 将镶嵌配置中的属性加成安全应用到装备对象上（钳制到 [0, 32767]，防止 short 溢出）。
     */
    public static void applySocketStats(org.gms.client.inventory.Equip equip, GemSocketLevelDO cfg) {
        if (cfg.getStrAdd() != null && cfg.getStrAdd() > 0)
            equip.setStr(EquipEnhanceManager.safeAddStat(equip.getStr(), cfg.getStrAdd()));
        if (cfg.getDexAdd() != null && cfg.getDexAdd() > 0)
            equip.setDex(EquipEnhanceManager.safeAddStat(equip.getDex(), cfg.getDexAdd()));
        if (cfg.getIntAdd() != null && cfg.getIntAdd() > 0)
            equip.setInt(EquipEnhanceManager.safeAddStat(equip.getInt(), cfg.getIntAdd()));
        if (cfg.getLukAdd() != null && cfg.getLukAdd() > 0)
            equip.setLuk(EquipEnhanceManager.safeAddStat(equip.getLuk(), cfg.getLukAdd()));
        if (cfg.getWatkAdd() != null && cfg.getWatkAdd() > 0)
            equip.setWatk(EquipEnhanceManager.safeAddStat(equip.getWatk(), cfg.getWatkAdd()));
        if (cfg.getMatkAdd() != null && cfg.getMatkAdd() > 0)
            equip.setMatk(EquipEnhanceManager.safeAddStat(equip.getMatk(), cfg.getMatkAdd()));
    }
}
