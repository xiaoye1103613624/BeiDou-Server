package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.DropDataDO;
import org.gms.dao.entity.DropDataGlobalDO;
import org.gms.dao.entity.GameIconDO;
import org.gms.dao.entity.XyLotteryItemDO;
import org.gms.dao.mapper.DropDataGlobalMapper;
import org.gms.dao.mapper.DropDataMapper;
import org.gms.dao.mapper.GameIconMapper;
import org.gms.dao.mapper.XyLotteryItemMapper;
import org.gms.model.dto.GameIconSyncReqDTO;
import org.gms.model.dto.GameIconSyncRtnDTO;
import org.gms.server.cashshop.ItemIconFiles;
import org.gms.server.icon.SharedIconFiles;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * 共用图标缓存：磁盘 {@code tools/_icon_cache} + 表 {@code xy_icon_cache}。
 * 拉取顺序：本地盘 → DB → maplestory.io → 小册子 dvg（仅 item）。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GameIconService {
    public static final int DEFAULT_VERSION = 227;
    public static final String DEFAULT_REGION = "GMS";
    public static final String CATEGORY_MOB = "mob";
    public static final String CATEGORY_ITEM = "item";
    public static final String CATEGORY_NPC = "npc";
    /** 兼容旧 drop 接口路径 */
    public static final String ICON_URL_PREFIX = "/drop/v1/icon/";
    /** 新统一路径（懒加载） */
    public static final String SHARED_ICON_URL_PREFIX = "/icon/v1/";

    private static final int CONNECT_TIMEOUT_MS = 8_000;
    private static final int READ_TIMEOUT_MS = 12_000;
    private static final int MAX_ICON_BYTES = 512 * 1024;
    private static final String DVG_ITEM_ICON =
            "https://mxd.dvg.cn/dbsource/icon/item/%d.png";

    private final GameIconMapper gameIconMapper;
    private final DropDataMapper dropDataMapper;
    private final DropDataGlobalMapper dropDataGlobalMapper;
    private final XyLotteryItemMapper lotteryItemMapper;

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

    public String sharedIconUrl(String category, int objectId) {
        if (objectId <= 0) {
            return null;
        }
        return SHARED_ICON_URL_PREFIX + normalizeCategory(category) + "/" + objectId;
    }

    /** 若库中无图标则按默认版本拉取并持久化；已有则跳过。 */
    public void ensureIcon(String category, int objectId) {
        resolveIconBytes(normalizeCategory(category), objectId, false);
    }

    /**
     * Ensure item icon bytes exist in cache (CDN/DVG pull when missing / force).
     */
    public Optional<byte[]> ensureItemIconBytes(int itemId, boolean force) {
        return resolveIconBytes(CATEGORY_ITEM, itemId, force);
    }

    /**
     * 懒加载入口：本地盘 → DB → 远端拉取并写入盘+库。
     */
    public Optional<byte[]> resolveIconBytes(String category, int objectId, boolean force) {
        if (objectId <= 0) {
            return Optional.empty();
        }
        String cat = normalizeCategory(category);
        if (!force) {
            Optional<byte[]> fromDisk = readLocalBytes(cat, objectId);
            if (fromDisk.isPresent()) {
                // 盘有库无时补写库，便于 drop 旧接口
                if (!hasIcon(cat, objectId)) {
                    persist(cat, objectId, fromDisk.get(), DEFAULT_VERSION, DEFAULT_REGION, "local");
                }
                return fromDisk;
            }
            Optional<GameIconDO> fromDb = findIcon(cat, objectId);
            if (fromDb.isPresent() && fromDb.get().getIconData() != null) {
                byte[] data = fromDb.get().getIconData();
                writeLocalBytes(cat, objectId, data);
                return Optional.of(data);
            }
        }
        SyncResult result = syncOne(cat, objectId, DEFAULT_VERSION, DEFAULT_REGION, force);
        if (result == SyncResult.FAILED) {
            return Optional.empty();
        }
        return findIcon(cat, objectId).map(GameIconDO::getIconData)
                .or(() -> readLocalBytes(cat, objectId));
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
        Set<Integer> npcIds = new HashSet<>();

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
                if (categories.contains(CATEGORY_NPC)) {
                    npcIds.add(id);
                }
            }
        } else if (req.getDropperId() != null && req.getDropperId() > 0) {
            if (categories.contains(CATEGORY_MOB)) {
                mobIds.add(req.getDropperId());
            }
            if (categories.contains(CATEGORY_ITEM)) {
                itemIds.addAll(collectItemIdsForMob(req.getDropperId()));
            }
        } else if (Boolean.TRUE.equals(req.getFromLottery())) {
            if (categories.contains(CATEGORY_ITEM)) {
                itemIds.addAll(collectLotteryItemIds(req.getLotteryNpcId()));
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
                switch (syncOne(CATEGORY_MOB, mobId, version, region, force)) {
                    case SUCCESS -> success++;
                    case SKIPPED -> skipped++;
                    case FAILED -> failed++;
                }
            }
        }
        if (categories.contains(CATEGORY_ITEM)) {
            for (Integer itemId : itemIds) {
                requested++;
                switch (syncOne(CATEGORY_ITEM, itemId, version, region, force)) {
                    case SUCCESS -> success++;
                    case SKIPPED -> skipped++;
                    case FAILED -> failed++;
                }
            }
        }
        if (categories.contains(CATEGORY_NPC)) {
            for (Integer npcId : npcIds) {
                requested++;
                switch (syncOne(CATEGORY_NPC, npcId, version, region, force)) {
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
        if (!force) {
            Optional<byte[]> local = readLocalBytes(category, objectId);
            if (local.isPresent()) {
                persist(category, objectId, local.get(), version, region, "local");
                return SyncResult.SUCCESS;
            }
        }
        try {
            FetchedIcon fetched = downloadIcon(category, objectId, version, region);
            if (fetched == null || fetched.data() == null || fetched.data().length == 0) {
                return SyncResult.FAILED;
            }
            persist(category, objectId, fetched.data(), fetched.version(), fetched.region(), fetched.source());
            writeLocalBytes(category, objectId, fetched.data());
            return SyncResult.SUCCESS;
        } catch (Exception e) {
            log.warn("sync icon failed category={} id={} version={}: {}", category, objectId, version, e.getMessage());
            return SyncResult.FAILED;
        }
    }

    private void persist(String category, int objectId, byte[] png, int version, String region, String source) {
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
                .source(source)
                .build();
        gameIconMapper.insertOrUpdate(row, true);
    }

    private Optional<byte[]> readLocalBytes(String category, int objectId) {
        Optional<byte[]> shared = SharedIconFiles.readPng(category, objectId);
        if (shared.isPresent()) {
            return shared;
        }
        if (CATEGORY_ITEM.equals(category)) {
            if (ItemIconFiles.copyFromLegacyCacheIfPresent(objectId) || ItemIconFiles.pngExists(objectId)) {
                PathBytes fromItemIcons = readItemIconFile(objectId);
                if (fromItemIcons != null) {
                    return Optional.of(fromItemIcons.data());
                }
            }
        }
        return Optional.empty();
    }

    private PathBytes readItemIconFile(int itemId) {
        try {
            var dir = ItemIconFiles.resolveOrCreateIconDir();
            var path = ItemIconFiles.pngPath(dir, itemId);
            if (!java.nio.file.Files.isRegularFile(path)) {
                return null;
            }
            byte[] data = java.nio.file.Files.readAllBytes(path);
            if (data.length >= 8 && data[0] == (byte) 0x89 && data[1] == 0x50) {
                return new PathBytes(data);
            }
        } catch (Exception e) {
            log.debug("read item-icons {} failed: {}", itemId, e.toString());
        }
        return null;
    }

    private void writeLocalBytes(String category, int objectId, byte[] png) {
        SharedIconFiles.writePng(category, objectId, png);
        if (CATEGORY_ITEM.equals(category)) {
            ItemIconFiles.writePng(objectId, png);
        }
    }

    private FetchedIcon downloadIcon(String category, int objectId, int version, String region) throws Exception {
        List<SourceTry> tries = buildSourceTries(category, objectId, version, region);
        for (SourceTry trySrc : tries) {
            byte[] png = httpGetPng(trySrc.url());
            if (png != null) {
                return new FetchedIcon(png, trySrc.version(), trySrc.region(), trySrc.source());
            }
        }
        return null;
    }

    private List<SourceTry> buildSourceTries(String category, int objectId, int version, String region) {
        List<SourceTry> list = new ArrayList<>();
        String reg = region == null || region.isBlank() ? DEFAULT_REGION : region;
        int ver = version <= 0 ? DEFAULT_VERSION : version;

        // maplestory.io primary
        list.add(new SourceTry(
                String.format(Locale.ROOT, "https://maplestory.io/api/%s/%d/%s/%d/icon",
                        reg, ver, category, objectId),
                ver, reg, "maplestory.io"));

        // GMS/83 often has npc/mob when high version 404/502
        if (!CATEGORY_ITEM.equals(category) || ver != 83) {
            list.add(new SourceTry(
                    String.format(Locale.ROOT, "https://maplestory.io/api/GMS/83/%s/%d/icon",
                            category, objectId),
                    83, "GMS", "maplestory.io"));
        }

        if (CATEGORY_ITEM.equals(category) && !"CMS".equalsIgnoreCase(reg)) {
            list.add(new SourceTry(
                    String.format(Locale.ROOT, "https://maplestory.io/api/CMS/%d/item/%d/icon",
                            ver, objectId),
                    ver, "CMS", "maplestory.io"));
        }

        // 小册子静态图（道具覆盖高版本/自定义更全）
        if (CATEGORY_ITEM.equals(category)) {
            list.add(new SourceTry(
                    String.format(Locale.ROOT, DVG_ITEM_ICON, objectId),
                    ver, "DVG", "dvg"));
        }
        return list;
    }

    private byte[] httpGetPng(String url) throws Exception {
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
            if (data.length < 8 || data[0] != (byte) 0x89 || data[1] != 0x50) {
                return null;
            }
            return data;
        } finally {
            conn.disconnect();
        }
    }

    private Set<Integer> collectLotteryItemIds(Integer npcId) {
        Set<Integer> ids = new HashSet<>();
        QueryWrapper qw = QueryWrapper.create().select(XyLotteryItemDO::getItemId);
        if (npcId != null && npcId > 0) {
            qw.where(XyLotteryItemDO::getNpcId).eq(npcId);
        }
        qw.groupBy(XyLotteryItemDO::getItemId);
        List<XyLotteryItemDO> rows = lotteryItemMapper.selectListByQuery(qw);
        for (XyLotteryItemDO row : rows) {
            if (row.getItemId() != null && row.getItemId() > 0) {
                ids.add(row.getItemId());
            }
        }
        return ids;
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
        ids.addAll(collectLotteryItemIds(null));
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
            if (CATEGORY_MOB.equals(n) || CATEGORY_ITEM.equals(n) || CATEGORY_NPC.equals(n)) {
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
        String n = category.trim().toLowerCase(Locale.ROOT);
        // equip/consume/etc 统一按 itemId 存
        if ("equip".equals(n) || "consume".equals(n) || "etc".equals(n)
                || "cash".equals(n) || "install".equals(n) || "pet".equals(n)) {
            return CATEGORY_ITEM;
        }
        return n;
    }

    private enum SyncResult {
        SUCCESS, SKIPPED, FAILED
    }

    private record FetchedIcon(byte[] data, int version, String region, String source) {
    }

    private record SourceTry(String url, int version, String region, String source) {
    }

    private record PathBytes(byte[] data) {
    }
}
