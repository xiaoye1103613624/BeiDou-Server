package org.gms.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.DailyExploreConfigManager;
import org.gms.config.GameConfig;
import org.gms.dao.entity.DailyExploreFinalRewardDO;
import org.gms.dao.entity.DailyExploreMapDO;
import org.gms.dao.entity.DailyExploreRewardDO;
import org.gms.dao.mapper.DailyExploreFinalRewardMapper;
import org.gms.dao.mapper.DailyExploreMapMapper;
import org.gms.dao.mapper.DailyExploreRewardMapper;
import org.gms.model.dto.DailyExploreSaveDTO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.maps.MapFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每日探索服务类，负责地图池、每轮随机奖励、完成奖励的配置管理。
 * <p>
 * 配置变更时同步更新 {@link DailyExploreConfigManager} 的内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class DailyExploreService {

    private final DailyExploreMapMapper mapMapper;
    private final DailyExploreRewardMapper rewardMapper;
    private final DailyExploreFinalRewardMapper finalRewardMapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("每日探索配置加载完成");
    }

    /** 刷新配置缓存 */
    private void refreshCache() {
        DailyExploreConfigManager.load(
                mapMapper.selectAll(),
                rewardMapper.selectAll(),
                finalRewardMapper.selectAll());
    }

    // ==================== 地图池 CRUD ====================

    /** 获取地图池列表（按 sortOrder 排序） */
    public List<DailyExploreSaveDTO> getMapList() {
        List<DailyExploreMapDO> maps = mapMapper.selectAll();
        maps.sort(Comparator.comparingInt(m ->
                m.getSortOrder() != null ? m.getSortOrder() : 0));
        return maps.stream().map(this::toMapDTO).collect(Collectors.toList());
    }

    /** 获取单个地图配置 */
    public DailyExploreSaveDTO getMapById(Long id) {
        DailyExploreMapDO map = mapMapper.selectOneById(id);
        return map != null ? toMapDTO(map) : null;
    }

    /** 保存地图配置（新增或更新） */
    @Transactional
    public DailyExploreSaveDTO saveMap(DailyExploreSaveDTO dto) {
        // 自动解析地图名称
        String mapName = resolveMapName(dto.getMapId());
        DailyExploreMapDO map = DailyExploreMapDO.builder()
                .id(dto.getId())
                .mapId(dto.getMapId())
                .mapName(mapName)
                .description(dto.getDescription() != null ? dto.getDescription() : "")
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (map.getId() != null) {
            mapMapper.update(map);
        } else {
            mapMapper.insert(map);
        }
        refreshCache();
        return getMapById(map.getId());
    }

    /** 删除地图配置 */
    @Transactional
    public void deleteMap(Long id) {
        mapMapper.deleteById(id);
        refreshCache();
    }

    /** 批量删除地图配置 */
    @Transactional
    public void deleteMapBatch(List<Long> ids) {
        mapMapper.deleteBatchByIds(ids);
        refreshCache();
    }

    // ==================== 每轮随机奖励 CRUD ====================

    /** 获取每轮随机奖励列表 */
    public List<DailyExploreSaveDTO.RewardDTO> getRewardList() {
        List<DailyExploreRewardDO> rewards = rewardMapper.selectAll();
        rewards.sort(Comparator.comparingInt(r ->
                r.getSortOrder() != null ? r.getSortOrder() : 0));
        return rewards.stream().map(this::toRewardDTO).collect(Collectors.toList());
    }

    /** 保存每轮随机奖励 */
    @Transactional
    public DailyExploreSaveDTO.RewardDTO saveReward(DailyExploreSaveDTO.RewardDTO dto) {
        DailyExploreRewardDO reward = DailyExploreRewardDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .minQuantity(dto.getMinQuantity() != null ? dto.getMinQuantity() : 1)
                .maxQuantity(dto.getMaxQuantity() != null ? dto.getMaxQuantity() : 1)
                .weight(dto.getWeight() != null ? dto.getWeight() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (reward.getId() != null) {
            rewardMapper.update(reward);
        } else {
            rewardMapper.insert(reward);
        }
        refreshCache();
        List<DailyExploreRewardDO> all = rewardMapper.selectAll();
        return all.stream()
                .filter(r -> r.getId().equals(reward.getId()))
                .findFirst()
                .map(this::toRewardDTO)
                .orElse(null);
    }

    /** 删除每轮随机奖励 */
    @Transactional
    public void deleteReward(Long id) {
        rewardMapper.deleteById(id);
        refreshCache();
    }

    // ==================== 完成奖励 CRUD ====================

    /** 获取完成奖励列表 */
    public List<DailyExploreSaveDTO.FinalRewardDTO> getFinalRewardList() {
        List<DailyExploreFinalRewardDO> rewards = finalRewardMapper.selectAll();
        rewards.sort(Comparator
                .comparingInt((DailyExploreFinalRewardDO r) ->
                        r.getExploreCount() != null ? r.getExploreCount() : 0)
                .thenComparingInt(r -> r.getSortOrder() != null ? r.getSortOrder() : 0));
        return rewards.stream().map(this::toFinalRewardDTO).collect(Collectors.toList());
    }

    /** 保存完成奖励 */
    @Transactional
    public DailyExploreSaveDTO.FinalRewardDTO saveFinalReward(DailyExploreSaveDTO.FinalRewardDTO dto) {
        DailyExploreFinalRewardDO reward = DailyExploreFinalRewardDO.builder()
                .id(dto.getId())
                .exploreCount(dto.getExploreCount())
                .rewardDesc(dto.getRewardDesc() != null ? dto.getRewardDesc() : "")
                .itemId(dto.getItemId() != null ? dto.getItemId() : 0)
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .build();
        if (reward.getId() != null) {
            finalRewardMapper.update(reward);
        } else {
            finalRewardMapper.insert(reward);
        }
        refreshCache();
        List<DailyExploreFinalRewardDO> all = finalRewardMapper.selectAll();
        return all.stream()
                .filter(r -> r.getId().equals(reward.getId()))
                .findFirst()
                .map(this::toFinalRewardDTO)
                .orElse(null);
    }

    /** 删除完成奖励 */
    @Transactional
    public void deleteFinalReward(Long id) {
        finalRewardMapper.deleteById(id);
        refreshCache();
    }

    // ==================== 游戏参数 ====================

    /** 获取每日探索上限 */
    public int getDailyLimit() {
        return GameConfig.get("server", "Game Mechanics", "daily_explore_limit", 10);
    }

    // ==================== 数据转换 ====================

    private DailyExploreSaveDTO toMapDTO(DailyExploreMapDO map) {
        return DailyExploreSaveDTO.builder()
                .id(map.getId())
                .mapId(map.getMapId())
                .mapName(map.getMapName() != null ? map.getMapName() : "")
                .description(map.getDescription() != null ? map.getDescription() : "")
                .mapImage(map.getMapImage() != null ? map.getMapImage() : "")
                .sortOrder(map.getSortOrder() != null ? map.getSortOrder() : 0)
                .enabled(map.getEnabled())
                .createTime(map.getCreateTime())
                .updateTime(map.getUpdateTime())
                .build();
    }

    private DailyExploreSaveDTO.RewardDTO toRewardDTO(DailyExploreRewardDO r) {
        String itemName = resolveItemName(r.getItemId());
        return DailyExploreSaveDTO.RewardDTO.builder()
                .id(r.getId())
                .itemId(r.getItemId())
                .itemName(itemName)
                .minQuantity(r.getMinQuantity())
                .maxQuantity(r.getMaxQuantity())
                .weight(r.getWeight())
                .sortOrder(r.getSortOrder())
                .enabled(r.getEnabled())
                .build();
    }

    private DailyExploreSaveDTO.FinalRewardDTO toFinalRewardDTO(DailyExploreFinalRewardDO r) {
        String itemName = resolveItemName(r.getItemId());
        return DailyExploreSaveDTO.FinalRewardDTO.builder()
                .id(r.getId())
                .exploreCount(r.getExploreCount())
                .rewardDesc(r.getRewardDesc())
                .itemId(r.getItemId())
                .itemName(itemName)
                .quantity(r.getQuantity())
                .sortOrder(r.getSortOrder())
                .build();
    }

    /** 解析地图名称（通过WZ查询） */
    private String resolveMapName(Integer mapId) {
        if (mapId == null || mapId <= 0) return "";
        try {
            return MapFactory.loadPlaceName(mapId);
        } catch (Exception e) {
            log.warn("解析地图名称失败: mapId={}", mapId, e);
            return "";
        }
    }

    /** 解析物品名称（0=金币，其他通过WZ查询） */
    private String resolveItemName(Integer itemId) {
        if (itemId == null || itemId == 0) return "金币";
        try {
            String name = ItemInformationProvider.getInstance().getName(itemId);
            return name != null ? name : String.valueOf(itemId);
        } catch (Exception e) {
            return String.valueOf(itemId);
        }
    }

    // ==================== 地图图片爬取 ====================

    /** maplestory.io 地图渲染API地址 */
    private static final String MAP_RENDER_URL = "https://maplestory.io/api/GMS/83/map/%d/render";

    /**
     * 为单条地图记录爬取并存储地图图片
     *
     * @param id 地图配置主键ID
     * @return 更新后的DTO（含mapImage），失败返回null
     */
    @Transactional
    public DailyExploreSaveDTO fetchMapImage(Long id) {
        DailyExploreMapDO map = mapMapper.selectOneById(id);
        if (map == null || map.getMapId() == null || map.getMapId() <= 0) {
            log.warn("爬取地图图片失败：地图记录不存在或mapId无效, id={}", id);
            return null;
        }
        String base64 = downloadMapImage(map.getMapId());
        if (base64 == null || base64.isEmpty()) {
            log.warn("爬取地图图片失败：下载失败, id={}, mapId={}", id, map.getMapId());
            // 仍更新为空字符串标记已尝试过，避免重复爬取
            map.setMapImage("");
            mapMapper.update(map);
            return null;
        }
        map.setMapImage(base64);
        mapMapper.update(map);
        refreshCache();
        log.info("地图图片爬取成功: id={}, mapId={}, size={}", id, map.getMapId(), base64.length());
        return getMapById(id);
    }

    /**
     * 批量爬取所有地图图片（仅处理mapImage为空的记录）
     *
     * @return 成功/失败计数
     */
    @Transactional
    public java.util.Map<String, Integer> fetchAllMapImages() {
        List<DailyExploreMapDO> allMaps = mapMapper.selectAll();
        int success = 0;
        int fail = 0;
        int skip = 0;
        for (DailyExploreMapDO map : allMaps) {
            if (map.getMapImage() != null && !map.getMapImage().isEmpty()) {
                skip++;
                continue; // 已有图片，跳过
            }
            if (map.getMapId() == null || map.getMapId() <= 0) {
                fail++;
                continue;
            }
            String base64 = downloadMapImage(map.getMapId());
            if (base64 != null && !base64.isEmpty()) {
                map.setMapImage(base64);
                mapMapper.update(map);
                success++;
            } else {
                map.setMapImage(""); // 标记已尝试
                mapMapper.update(map);
                fail++;
            }
        }
        refreshCache();
        log.info("批量爬取地图图片完成: 成功={}, 失败={}, 跳过={}", success, fail, skip);
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
        result.put("success", success);
        result.put("fail", fail);
        result.put("skip", skip);
        return result;
    }

    /**
     * 从 maplestory.io 下载地图渲染图并转为 base64 data URL
     *
     * @param mapId 地图ID
     * @return base64 data URL 字符串，失败返回null
     */
    private String downloadMapImage(int mapId) {
        String urlStr = String.format(MAP_RENDER_URL, mapId);
        HttpURLConnection conn = null;
        try {
            URI uri = URI.create(urlStr);
            conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(15000);
            conn.setRequestProperty("User-Agent", "BeiDou-Server/1.0");
            int code = conn.getResponseCode();
            if (code != 200) {
                log.warn("下载地图图片HTTP {}: mapId={}", code, mapId);
                return null;
            }
            String contentType = conn.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("下载地图图片非图片类型: mapId={}, contentType={}", mapId, contentType);
                return null;
            }
            try (InputStream is = conn.getInputStream();
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int n;
                while ((n = is.read(buf)) != -1) {
                    bos.write(buf, 0, n);
                }
                byte[] imageBytes = bos.toByteArray();
                String b64 = Base64.getEncoder().encodeToString(imageBytes);
                return "data:" + contentType + ";base64," + b64;
            }
        } catch (Exception e) {
            log.error("下载地图图片异常: mapId={}", mapId, e);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}
