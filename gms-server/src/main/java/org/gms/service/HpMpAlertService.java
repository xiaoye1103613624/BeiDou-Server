package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.mapper.HpMpAlertMapper;
import org.gms.dao.entity.HpMpAlertDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 【业务服务】HpMpAlertService：HP/MP警戒线服务类，管理角色的血量和蓝量警戒值设置。
 * 
 * <p>玩家可设置HP/MP警戒线百分比，当HP或MP低于警戒线时会触发警报提示。
 * 警戒线采用0-19共20个挡位，服务端按"挡位/20"换算成百分比（最大95%）。</p>
 * 
 * <p>数据采用内存缓存机制，修改时仅更新缓存，通过 {@link #saveAll()} 批量持久化到数据库。</p>
 */
@Slf4j
@Service
public class HpMpAlertService {
    /** 最大警戒挡位（0-19共20个挡位，对应0%-95%） */
    private static final int MAX_ALERT_STEP = 19;
    /** 挡位换算除数，用于将挡位转换为百分比 */
    private static final int ALERT_STEP_DIVISOR = 20;

    /** HP/MP警戒配置数据访问接口 */
    @Autowired
    private HpMpAlertMapper hpMpAlertMapper;
    
    /** 
     * 警戒配置缓存Map，key为角色ID，value为警戒配置实体。
     * 使用ConcurrentHashMap保证线程安全。
     */
    public static final Map<Integer, HpMpAlertDO> cacheMap = new ConcurrentHashMap<>();

    /**
     * 标准化警戒挡位值，确保在有效范围内（0-19）。
     * 
     * @param step 原始挡位值
     * @return 标准化后的挡位值（0-19）
     */
    private static byte normalizeAlertStep(byte step) {
        int normalized = Math.min(MAX_ALERT_STEP, Math.max(0, Byte.toUnsignedInt(step)));
        return (byte) normalized;
    }

    /**
     * 获取角色的HP警戒挡位。
     * 
     * <p>优先从缓存获取，缓存未命中时从数据库查询并加入缓存。
     * 返回值会进行标准化处理，确保在有效范围内。</p>
     * 
     * @param characterId 角色ID
     * @return HP警戒挡位（0-19），未设置返回0
     */
    public byte getHpAlert(int characterId) {
        HpMpAlertDO cached = cacheMap.get(characterId);
        if (cached != null) {
            // 缓存命中，标准化后返回
            byte normalized = normalizeAlertStep(cached.getHp());
            if (cached.getHp() != normalized) {
                cached.setHp(normalized);
            }
            return normalized;
        } else {
            // 缓存未命中，从数据库查询
            HpMpAlertDO hpMpAlert = hpMpAlertMapper.selectOneByQuery(QueryWrapper.create().eq("c_id", characterId));
            if (hpMpAlert != null) {
                byte normalized = normalizeAlertStep(hpMpAlert.getHp());
                if (hpMpAlert.getHp() != normalized) {
                    hpMpAlert.setHp(normalized);
                }
                cacheMap.put(characterId, hpMpAlert);
                return normalized;
            }
        }
        // 数据库也不存在，返回默认值0
        return 0;
    }

    /**
     * 设置角色的HP警戒挡位。
     * 
     * <p>挡位值会先进行标准化处理，确保在0-19范围内。
     * 数据仅更新缓存，通过{@link #saveAll()}批量持久化。</p>
     * 
     * @param characterId 角色ID
     * @param alert HP警戒挡位（0-19）
     */
    public void setHpAlert(int characterId, byte alert) {
        byte normalizedAlert = normalizeAlertStep(alert);
        HpMpAlertDO cached = cacheMap.get(characterId);
        if (cached != null) {
            // 更新缓存中的HP警戒值
            cached.setHp(normalizedAlert);
            return;
        }

        // 缓存未命中，查询数据库
        HpMpAlertDO hpMpAlert = hpMpAlertMapper.selectOneByQuery(QueryWrapper.create().eq("c_id", characterId));
        if (hpMpAlert != null) {
            // 更新数据库查询结果
            hpMpAlert.setHp(normalizedAlert);
        } else {
            // 创建新的配置实体，MP默认设为10（50%）
            hpMpAlert = HpMpAlertDO.builder().cId(characterId).hp(normalizedAlert).mp((byte) 10).build();
        }
        cacheMap.put(characterId, hpMpAlert);
    }

    /**
     * 获取角色的HP警戒百分比。
     * 
     * <p>将挡位值转换为百分比（挡位/20），范围为0%-95%。</p>
     * 
     * @param characterId 角色ID
     * @return HP警戒百分比（0.0f-0.95f）
     */
    public float getHpAlertPer(int characterId) {
        return (float) Byte.toUnsignedInt(getHpAlert(characterId)) / ALERT_STEP_DIVISOR;
    }

    /**
     * 获取角色的MP警戒挡位。
     * 
     * <p>优先从缓存获取，缓存未命中时从数据库查询并加入缓存。
     * 返回值会进行标准化处理，确保在有效范围内。</p>
     * 
     * @param characterId 角色ID
     * @return MP警戒挡位（0-19），未设置返回0
     */
    public byte getMpAlert(int characterId) {
        HpMpAlertDO cached = cacheMap.get(characterId);
        if (cached != null) {
            // 缓存命中，标准化后返回
            byte normalized = normalizeAlertStep(cached.getMp());
            if (cached.getMp() != normalized) {
                cached.setMp(normalized);
            }
            return normalized;
        } else {
            // 缓存未命中，从数据库查询
            HpMpAlertDO hpMpAlert = hpMpAlertMapper.selectOneByQuery(QueryWrapper.create().eq("c_id", characterId));
            if (hpMpAlert != null) {
                byte normalized = normalizeAlertStep(hpMpAlert.getMp());
                if (hpMpAlert.getMp() != normalized) {
                    hpMpAlert.setMp(normalized);
                }
                cacheMap.put(characterId, hpMpAlert);
                return normalized;
            }
        }
        // 数据库也不存在，返回默认值0
        return 0;
    }

    /**
     * 设置角色的MP警戒挡位。
     * 
     * <p>挡位值会先进行标准化处理，确保在0-19范围内。
     * 数据仅更新缓存，通过{@link #saveAll()}批量持久化。</p>
     * 
     * @param characterId 角色ID
     * @param alert MP警戒挡位（0-19）
     */
    public void setMpAlert(int characterId, byte alert) {
        byte normalizedAlert = normalizeAlertStep(alert);
        HpMpAlertDO cached = cacheMap.get(characterId);
        if (cached != null) {
            // 更新缓存中的MP警戒值
            cached.setMp(normalizedAlert);
            return;
        }

        // 缓存未命中，查询数据库
        HpMpAlertDO hpMpAlert = hpMpAlertMapper.selectOneByQuery(QueryWrapper.create().eq("c_id", characterId));
        if (hpMpAlert != null) {
            // 更新数据库查询结果
            hpMpAlert.setMp(normalizedAlert);
        } else {
            // 创建新的配置实体，HP默认设为10（50%）
            hpMpAlert = HpMpAlertDO.builder().cId(characterId).hp((byte) 10).mp(normalizedAlert).build();
        }
        cacheMap.put(characterId, hpMpAlert);
    }

    /**
     * 获取角色的MP警戒百分比。
     * 
     * <p>将挡位值转换为百分比（挡位/20），范围为0%-95%。</p>
     * 
     * @param characterId 角色ID
     * @return MP警戒百分比（0.0f-0.95f）
     */
    public float getMpAlertPer(int characterId) {
        return (float) Byte.toUnsignedInt(getMpAlert(characterId)) / ALERT_STEP_DIVISOR;
    }

    /**
     * 将缓存中的所有警戒配置持久化到数据库。
     * 
     * <p>使用insertOrUpdate策略，已存在的记录更新，不存在的记录插入。
     * 目前仅在saveall命令和关闭服务器时调用。</p>
     */
    public void saveAll() {
        for (int id : cacheMap.keySet()) {
            hpMpAlertMapper.insertOrUpdate(cacheMap.get(id));
        }
        log.info("已保存 Hp Mp 警戒线到数据库");
    }

    /**
     * 清除所有缓存数据。
     * 
     * <p>目前仅在关闭服务器时调用，用于重启前清理缓存。</p>
     */
    public void clear() {
        cacheMap.clear();
    }
}