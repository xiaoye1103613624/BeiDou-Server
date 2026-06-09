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
 * HP/MP警报服务类
 * 管理角色的HP/MP警报阈值设置，使用内存缓存提升查询性能
 */
@Slf4j
@Service
public class HpMpAlertService {
    /**
     * 客户端上报 0-19 共 20 个挡位；服务端按"挡位/20"换算比例（最大 95%）。
     */
    private static final int MAX_ALERT_STEP = 19;
    private static final int ALERT_STEP_DIVISOR = 20;

    /** HP/MP警报数据访问对象 */
    @Autowired
    private HpMpAlertMapper hpMpAlertMapper;
    /** 警报缓存，key为角色ID */
    public static final Map<Integer, HpMpAlertDO> cacheMap = new ConcurrentHashMap<>();

    /**
     * 标准化警报挡位
     * 将输入挡位限制在 0~MAX_ALERT_STEP 范围内
     *
     * @param step 原始挡位
     * @return 标准化后的挡位
     */
    private static byte normalizeAlertStep(byte step) {
        int normalized = Math.min(MAX_ALERT_STEP, Math.max(0, Byte.toUnsignedInt(step)));
        return (byte) normalized;
    }

    /**
     * 获取角色的HP警报挡位
     * 优先从缓存读取，缓存未命中则查询数据库
     *
     * @param characterId 角色ID
     * @return HP警报挡位（0~19）
     */
    public byte getHpAlert(int characterId) {
        HpMpAlertDO cached = cacheMap.get(characterId);
        if (cached != null) {
            byte normalized = normalizeAlertStep(cached.getHp());
            if (cached.getHp() != normalized) {
                cached.setHp(normalized);
            }
            return normalized;
        } else {
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

        return 0;
    }

    /**
     * 设置角色的HP警报挡位
     * 先更新缓存，未命中缓存时查询数据库并更新/新建
     *
     * @param characterId 角色ID
     * @param alert       HP警报挡位
     */
    public void setHpAlert(int characterId, byte alert) {
        byte normalizedAlert = normalizeAlertStep(alert);
        HpMpAlertDO cached = cacheMap.get(characterId);
        if (cached != null) {
            cached.setHp(normalizedAlert);
            return;
        }

        HpMpAlertDO hpMpAlert = hpMpAlertMapper.selectOneByQuery(QueryWrapper.create().eq("c_id", characterId));
        if (hpMpAlert != null) {
            hpMpAlert.setHp(normalizedAlert);
        } else {
            hpMpAlert = HpMpAlertDO.builder().cId(characterId).hp(normalizedAlert).mp((byte) 10).build();
        }
        cacheMap.put(characterId, hpMpAlert);
    }

    /**
     * 获取角色的HP警报比例
     *
     * @param characterId 角色ID
     * @return HP警报比例（0.0 ~ 0.95）
     */
    public float getHpAlertPer(int characterId) {
        return (float) Byte.toUnsignedInt(getHpAlert(characterId)) / ALERT_STEP_DIVISOR;
    }

    /**
     * 获取角色的MP警报挡位
     * 优先从缓存读取，缓存未命中则查询数据库
     *
     * @param characterId 角色ID
     * @return MP警报挡位（0~19）
     */
    public byte getMpAlert(int characterId) {
        HpMpAlertDO cached = cacheMap.get(characterId);
        if (cached != null) {
            byte normalized = normalizeAlertStep(cached.getMp());
            if (cached.getMp() != normalized) {
                cached.setMp(normalized);
            }
            return normalized;
        } else {
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

        return 0;
    }

    /**
     * 设置角色的MP警报挡位
     * 先更新缓存，未命中缓存时查询数据库并更新/新建
     *
     * @param characterId 角色ID
     * @param alert       MP警报挡位
     */
    public void setMpAlert(int characterId, byte alert) {
        byte normalizedAlert = normalizeAlertStep(alert);
        HpMpAlertDO cached = cacheMap.get(characterId);
        if (cached != null) {
            cached.setMp(normalizedAlert);
            return;
        }

        HpMpAlertDO hpMpAlert = hpMpAlertMapper.selectOneByQuery(QueryWrapper.create().eq("c_id", characterId));
        if (hpMpAlert != null) {
            hpMpAlert.setMp(normalizedAlert);
        } else {
            hpMpAlert = HpMpAlertDO.builder().cId(characterId).hp((byte) 10).mp(normalizedAlert).build();
        }
        cacheMap.put(characterId, hpMpAlert);
    }

    /**
     * 获取角色的MP警报比例
     *
     * @param characterId 角色ID
     * @return MP警报比例（0.0 ~ 0.95）
     */
    public float getMpAlertPer(int characterId) {
        return (float) Byte.toUnsignedInt(getMpAlert(characterId)) / ALERT_STEP_DIVISOR;
    }

    /**
     * 保存缓存数据到数据库，目前仅在saveall命令和关闭服务器时调用
     */
    public void saveAll() {
        for (int id : cacheMap.keySet()) {
            hpMpAlertMapper.insertOrUpdate(cacheMap.get(id));
        }
        log.info("已保存 Hp Mp 警戒线到数据库");
    }

    /**
     * 清除缓存，目前仅在关闭服务器函数中被调用（重启时用）
     */
    public void clear() {
        cacheMap.clear();
    }
}