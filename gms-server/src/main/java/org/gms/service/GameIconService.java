package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.DropDataDO;
import org.gms.dao.entity.DropDataGlobalDO;
import org.gms.dao.entity.GameIconDO;
import org.gms.dao.mapper.DropDataGlobalMapper;
import org.gms.dao.mapper.DropDataMapper;
import org.gms.dao.mapper.GameIconMapper;
import org.gms.model.dto.GameIconSyncReqDTO;
import org.gms.model.dto.GameIconSyncRtnDTO;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * 从小册子（maplestory.io）按版本拉取图标并持久化到 {@code xy_game_icon}。
 * 默认版本 227（小册子 / maplestory.io）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameIconService {
    public static final int DEFAULT_VERSION = 227;
    public static final String DEFAULT_REGION = "GMS";
    public static final String CATEGORY_MOB = "mob";
    public static final String CATEGORY_ITEM = "item";
    public static final String ICON_URL_PREFIX = "/drop/v1/icon/";

    private static final int CONNECT_TIMEOUT_MS = 8_000;
    private static final int READ_TIMEOUT_MS = 12_000;
    private static final int MAX_ICON_BYTES = 512 * 1024;

    private final GameIconMapper gameIconMapper;
    private final DropDataMapper dropDataMapper;
    private final DropDataGlobalMapper dropDataGlobalMapper;

    public Optional<GameIconDO> findIcon(String category, int objectId) {
        if (category == null || objectId <= 0) {
            return Optional.empty();
        }
        GameIconDO row = gameIconMapper.selectOneByQuery(QueryWrapper.create()
                .where(GameIconDO::getCategory).eq(normalizeCategory(category))
                .and(GameIconDO::getObjectId).eq(objectId));
        return Optional.ofNullable(row);
    }

    public boolean hasIcon(String category, int objectId) {
        return findIcon(category, objectId).isPresent();
    }

    public String iconUrl(String category, Integer objectId) {
        if (objectId == null || objectId <= 0) {
            return null;
        }
        if (!hasIcon(category, objectId)) {
            return null;
        }
        return ICON_URL_PREFIX + normalizeCategory(category) + "/" + objectId;
    }

    /** 若库中无图标则按默认版本拉取并持久化；已有则跳过。 */
    public void ensureIcon(String category, int objectId) {
        syncOne(normalizeCategory(category), objectId, DEFAULT_VERSION, DEFAULT_REGION, false);
    }

    public GameIconSyncRtnDTO sync(GameIconSyncReqDTO req) {
        int version = req.getVersion() == null || req.getVersion() <= 0 ? DEFAULT_VERSION : req.getVersion();
        String region = (req.getRegion() == null || req.getRegion().isBlank())
                ? DEFAULT_REGION
                : req.getRegion().trim().toUpperCase(Locale.ROOT);
        boolean force = Boolean.TRUE.equals(req.getForce());

        Set<String> categories = resolveCategories(req.getCategories());
        Set<Integer> mobIds = new HashSet<>();
        Set<Integer> itemIds = new HashSet<>();

        if (req.getObjectIds() != null && !req.getObjectIds().isEmpty()) {
            for (Integer id : req.getObjectIds()) {
                if (id == null || id <= 0) {
                    continue;
                }
                if (categories.contains(CATEGORY_MOB)) {
                    mobIds.add(id);
                }
                if (categories.contains(CATEGORY_ITEM)) {
                    itemIds.add(id);
                }
            }
        } else if (req.getDropperId() != null && req.getDropperId() > 0) {
            if (categories.contains(CATEGORY_MOB)) {
                mobIds.add(req.getDropperId());
            }
            if (categories.contains(CATEGORY_ITEM)) {
                itemIds.addAll(collectItemIdsForMob(req.getDropperId()));
            }
        } else {
            if (categories.contains(CATEGORY_MOB)) {
                mobIds.addAll(collectAllMobIds());
            }
            if (categories.contains(CATEGORY_ITEM)) {
                itemIds.addAll(collectAllItemIds());
            }
        }

        int requested = 0;
        int success = 0;
        int skipped = 0;
        int failed = 0;

        if (categories.contains(CATEGORY_MOB)) {
            for (Integer mobId : mobIds) {
                requested++;
                SyncResult r = syncOne(CATEGORY_MOB, mobId, version, region, force);
                switch (r) {
                    case SUCCESS -> success++;
                    case SKIPPED -> skipped++;
                    case FAILED -> failed++;
                }
            }
        }
        if (categories.contains(CATEGORY_ITEM)) {
            for (Integer itemId : itemIds) {
                requested++;
                SyncResult r = syncOne(CATEGORY_ITEM, itemId, version, region, force);
                switch (r) {
                    case SUCCESS -> success++;
                    case SKIPPED -> skipped++;
                    case FAILED -> failed++;
                }
            }
        }

        return GameIconSyncRtnDTO.builder()
                .version(version)
                .region(region)
                .requested(requested)
                .success(success)
                .skipped(skipped)
                .failed(failed)
                .message(String.format(Locale.ROOT,
                        "version=%d region=%s requested=%d success=%d skipped=%d failed=%d",
                        version, region, requested, success, skipped, failed))
                .build();
    }

    private SyncResult syncOne(String category, int objectId, int version, String region, boolean force) {
        if (objectId <= 0) {
            return SyncResult.SKIPPED;
        }
        if (!force && hasIcon(category, objectId)) {
            return SyncResult.SKIPPED;
        }
        try {
            byte[] png = downloadIcon(category, objectId, version, region);
            if (png == null || png.length == 0) {
                return SyncResult.FAILED;
            }
            GameIconDO existing = gameIconMapper.selectOneByQuery(QueryWrapper.create()
                    .where(GameIconDO::getCategory).eq(category)
                    .and(GameIconDO::getObjectId).eq(objectId));
            GameIconDO row = GameIconDO.builder()
                    .id(existing == null ? null : existing.getId())
                    .category(category)
                    .objectId(objectId)
                    .version(version)
                    .region(region)
                    .iconData(png)
                    .contentType("image/png")
                    .build();
            gameIconMapper.insertOrUpdate(row, true);
            return SyncResult.SUCCESS;
        } catch (Exception e) {
            log.warn("sync icon failed category={} id={} version={}: {}", category, objectId, version, e.getMessage());
            return SyncResult.FAILED;
        }
    }

    private byte[] downloadIcon(String category, int objectId, int version, String region) throws Exception {
        String url = String.format(Locale.ROOT,
                "https://maplestory.io/api/%s/%d/%s/%d/icon",
                region, version, category, objectId);
        HttpURLConnection conn = (HttpURLConnection) URI.create(url).toURL().openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
        conn.setReadTimeout(READ_TIMEOUT_MS);
        conn.setInstanceFollowRedirects(true);
        conn.setRequestProperty("User-Agent", "BeiDou-Server-Admin/1.0");
        conn.setRequestMethod("GET");
        int code = conn.getResponseCode();
        if (code != 200) {
            conn.disconnect();
            return null;
        }
        try (InputStream in = conn.getInputStream()) {
            byte[] data = in.readNBytes(MAX_ICON_BYTES + 1);
            if (data.length == 0 || data.length > MAX_ICON_BYTES) {
                return null;
            }
            // 简单 PNG 魔数校验
            if (data.length < 8 || data[0] != (byte) 0x89 || data[1] != 0x50) {
                return null;
            }
            return data;
        } finally {
            conn.disconnect();
        }
    }

    private Set<Integer> collectAllMobIds() {
        Set<Integer> ids = new HashSet<>();
        List<DropDataDO> rows = dropDataMapper.selectListByQuery(
                QueryWrapper.create().select(DropDataDO::getDropperid).groupBy(DropDataDO::getDropperid));
        for (DropDataDO row : rows) {
            if (row.getDropperid() != null && row.getDropperid() > 0) {
                ids.add(row.getDropperid());
            }
        }
        return ids;
    }

    private Set<Integer> collectAllItemIds() {
        Set<Integer> ids = new HashSet<>();
        List<DropDataDO> rows = dropDataMapper.selectListByQuery(
                QueryWrapper.create().select(DropDataDO::getItemid).groupBy(DropDataDO::getItemid));
        for (DropDataDO row : rows) {
            if (row.getItemid() != null && row.getItemid() > 0) {
                ids.add(row.getItemid());
            }
        }
        List<DropDataGlobalDO> globals = dropDataGlobalMapper.selectListByQuery(
                QueryWrapper.create().select(DropDataGlobalDO::getItemid).groupBy(DropDataGlobalDO::getItemid));
        for (DropDataGlobalDO row : globals) {
            if (row.getItemid() != null && row.getItemid() > 0) {
                ids.add(row.getItemid());
            }
        }
        return ids;
    }

    private Set<Integer> collectItemIdsForMob(int dropperId) {
        Set<Integer> ids = new HashSet<>();
        List<DropDataDO> rows = dropDataMapper.selectListByQuery(QueryWrapper.create()
                .select(DropDataDO::getItemid)
                .where(DropDataDO::getDropperid).eq(dropperId)
                .groupBy(DropDataDO::getItemid));
        for (DropDataDO row : rows) {
            if (row.getItemid() != null && row.getItemid() > 0) {
                ids.add(row.getItemid());
            }
        }
        return ids;
    }

    private static Set<String> resolveCategories(List<String> raw) {
        Set<String> set = new HashSet<>();
        if (raw == null || raw.isEmpty()) {
            set.add(CATEGORY_MOB);
            set.add(CATEGORY_ITEM);
            return set;
        }
        for (String c : raw) {
            if (c == null || c.isBlank()) {
                continue;
            }
            String n = normalizeCategory(c);
            if (CATEGORY_MOB.equals(n) || CATEGORY_ITEM.equals(n) || "npc".equals(n)) {
                set.add(n);
            }
        }
        if (set.isEmpty()) {
            set.add(CATEGORY_MOB);
            set.add(CATEGORY_ITEM);
        }
        return set;
    }

    private static String normalizeCategory(String category) {
        return category.trim().toLowerCase(Locale.ROOT);
    }

    private enum SyncResult {
        SUCCESS, SKIPPED, FAILED
    }
}
