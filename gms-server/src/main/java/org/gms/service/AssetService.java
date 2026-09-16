package org.gms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.XyCashShopItemDO;
import org.gms.dao.mapper.XyCashShopItemMapper;
import org.gms.model.dto.AssetEnsureReqDTO;
import org.gms.model.dto.AssetEnsureRtnDTO;
import org.gms.model.dto.AssetInfoRtnDTO;
import org.gms.model.dto.IconCacheRtnDTO;
import org.gms.model.dto.IconResolveRtnDTO;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.server.cashshop.ItemIconFiles;
import org.gms.server.icon.SharedIconFiles;
import org.gms.service.asset.BookletIconProvider;
import org.gms.service.asset.IconProvider;
import org.gms.service.asset.MapleStoryIoIconProvider;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * Unified admin-UI game asset resolve/cache.
 * <p>
 * Chain: local {@code game-assets} (+ promote legacy item-icons) → optional client PNG dirs
 * → remote {@link IconProvider}s (CDN then booklet) → write only under {@code game-assets}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {
    public static final String CATEGORY_ITEM = "item";
    public static final String SOURCE_LOCAL = "local";
    public static final String SOURCE_CDN = "cdn";
    public static final String SOURCE_BOOKLET = "booklet";
    public static final String SOURCE_CLIENT = "client";
    public static final String SOURCE_NONE = "none";

    private static final int MAX_DETAIL_ROWS = 200;
    private static final int MAX_RANGE_SIZE = 5000;

    private final List<IconProvider> iconProviders;
    private final MapleStoryIoIconProvider mapleStoryIoIconProvider;
    private final BookletIconProvider bookletIconProvider;
    private final XyCashShopItemMapper cashShopItemMapper;

    public Optional<byte[]> ensureItemIconBytes(int itemId, boolean force) {
        return ensureIconBytes(CATEGORY_ITEM, itemId, force);
    }

    public Optional<byte[]> ensureIconBytes(String category, int objectId, boolean force) {
        if (objectId <= 0) {
            return Optional.empty();
        }
        String cat = SharedIconFiles.normalizeCategory(category);
        if (!force) {
            SharedIconFiles.promoteLegacyIfPresent(cat, objectId);
            Optional<byte[]> cached = SharedIconFiles.readPng(cat, objectId);
            if (cached.isPresent()) {
                return cached;
            }
            Optional<byte[]> fromClient = tryCopyClientPng(cat, objectId);
            if (fromClient.isPresent()) {
                return fromClient;
            }
        }
        for (IconProvider provider : iconProviders) {
            if (provider == null || !provider.enabled()) {
                continue;
            }
            Optional<byte[]> downloaded = provider.fetchPng(cat, objectId);
            if (downloaded.isPresent() && SharedIconFiles.writePng(cat, objectId, downloaded.get())) {
                log.debug(I18nUtil.getLogMessage("GameIcon.cache.ok"), cat, objectId);
                return downloaded;
            }
        }
        return SharedIconFiles.readPng(cat, objectId);
    }

    /**
     * Phase-1 client extract: copy already-exported PNGs under known dirs next to ClientDataPath.
     */
    private Optional<byte[]> tryCopyClientPng(String category, int objectId) {
        if (!CATEGORY_ITEM.equals(SharedIconFiles.normalizeCategory(category))) {
            return Optional.empty();
        }
        Optional<Path> root = ClientDataPath.resolve();
        if (root.isEmpty()) {
            return Optional.empty();
        }
        Path data = root.get();
        Path[] candidates = {
                data.resolve("web_png").resolve(objectId + ".png"),
                data.getParent() != null ? data.getParent().resolve("web_png").resolve(objectId + ".png") : null,
                data.resolve("item-icons").resolve(objectId + ".png"),
        };
        for (Path src : candidates) {
            if (src == null || !Files.isRegularFile(src)) {
                continue;
            }
            try {
                byte[] bytes = Files.readAllBytes(src);
                if (SharedIconFiles.isPng(bytes) && SharedIconFiles.writePng(category, objectId, bytes)) {
                    log.info(I18nUtil.getLogMessage("Asset.client.copy"), category, objectId, src);
                    return Optional.of(bytes);
                }
            } catch (Exception e) {
                log.debug("client png copy {} failed: {}", objectId, e.toString());
            }
        }
        return Optional.empty();
    }

    public IconResolveRtnDTO resolve(String category, int objectId) {
        String cat = SharedIconFiles.normalizeCategory(category);
        if (objectId <= 0) {
            return IconResolveRtnDTO.builder()
                    .category(cat)
                    .id(objectId)
                    .url("")
                    .cdnUrl("")
                    .local(false)
                    .source(SOURCE_NONE)
                    .build();
        }
        SharedIconFiles.promoteLegacyIfPresent(cat, objectId);
        boolean local = SharedIconFiles.pngExists(cat, objectId)
                || (CATEGORY_ITEM.equals(cat) && ItemIconFiles.pngExists(objectId));
        String cdnUrl = mapleStoryIoIconProvider.buildUrl(cat, objectId);
        if (local) {
            // Prefer unified URL even if only legacy file exists (promote on next ensure).
            if (!SharedIconFiles.pngExists(cat, objectId)) {
                SharedIconFiles.promoteLegacyIfPresent(cat, objectId);
            }
            return IconResolveRtnDTO.builder()
                    .category(cat)
                    .id(objectId)
                    .url(SharedIconFiles.webUrl(cat, objectId))
                    .cdnUrl(cdnUrl)
                    .local(true)
                    .source(SOURCE_LOCAL)
                    .build();
        }
        if (!cdnUrl.isEmpty()) {
            return IconResolveRtnDTO.builder()
                    .category(cat)
                    .id(objectId)
                    .url(cdnUrl)
                    .cdnUrl(cdnUrl)
                    .local(false)
                    .source(SOURCE_CDN)
                    .build();
        }
        String bookletUrl = bookletIconProvider.buildUrl(cat, objectId);
        if (!bookletUrl.isEmpty()) {
            return IconResolveRtnDTO.builder()
                    .category(cat)
                    .id(objectId)
                    .url(bookletUrl)
                    .cdnUrl("")
                    .local(false)
                    .source(SOURCE_BOOKLET)
                    .build();
        }
        return IconResolveRtnDTO.builder()
                .category(cat)
                .id(objectId)
                .url("")
                .cdnUrl("")
                .local(false)
                .source(SOURCE_NONE)
                .build();
    }

    public IconCacheRtnDTO cacheOne(String category, int objectId, boolean force) {
        String cat = SharedIconFiles.normalizeCategory(category);
        if (objectId <= 0) {
            return IconCacheRtnDTO.builder()
                    .category(cat)
                    .id(objectId)
                    .cached(false)
                    .url("")
                    .source(SOURCE_NONE)
                    .message("invalid id")
                    .build();
        }
        if (!force && SharedIconFiles.pngExists(cat, objectId)) {
            return IconCacheRtnDTO.builder()
                    .category(cat)
                    .id(objectId)
                    .cached(true)
                    .url(SharedIconFiles.webUrl(cat, objectId))
                    .source(SOURCE_LOCAL)
                    .message("already local")
                    .build();
        }
        Optional<byte[]> bytes = ensureIconBytes(cat, objectId, force);
        if (bytes.isPresent() && SharedIconFiles.pngExists(cat, objectId)) {
            return IconCacheRtnDTO.builder()
                    .category(cat)
                    .id(objectId)
                    .cached(true)
                    .url(SharedIconFiles.webUrl(cat, objectId))
                    .source(SOURCE_LOCAL)
                    .message("cached")
                    .build();
        }
        String cdnUrl = mapleStoryIoIconProvider.buildUrl(cat, objectId);
        String bookletUrl = bookletIconProvider.buildUrl(cat, objectId);
        String fallback = !cdnUrl.isEmpty() ? cdnUrl : bookletUrl;
        return IconCacheRtnDTO.builder()
                .category(cat)
                .id(objectId)
                .cached(false)
                .url(fallback)
                .source(fallback.isEmpty() ? SOURCE_NONE
                        : (!cdnUrl.isEmpty() ? SOURCE_CDN : SOURCE_BOOKLET))
                .message("remote miss or write failed")
                .build();
    }

    public List<IconCacheRtnDTO> cacheBatch(List<IconCacheRtnDTO.IconRef> refs, boolean force) {
        List<IconCacheRtnDTO> out = new ArrayList<>();
        if (refs == null || refs.isEmpty()) {
            return out;
        }
        for (IconCacheRtnDTO.IconRef ref : refs) {
            if (ref == null || ref.getId() == null) {
                continue;
            }
            out.add(cacheOne(ref.getCategory(), ref.getId(), force));
        }
        return out;
    }

    public AssetEnsureRtnDTO ensure(AssetEnsureReqDTO req) {
        long started = System.currentTimeMillis();
        AssetEnsureReqDTO body = req == null ? new AssetEnsureReqDTO() : req;
        String cat = SharedIconFiles.normalizeCategory(body.getCategory());
        boolean force = Boolean.TRUE.equals(body.getForce());
        List<Integer> ids = resolveEnsureIds(cat, body);

        int cached = 0;
        int skipped = 0;
        int failed = 0;
        List<IconCacheRtnDTO> details = new ArrayList<>();
        for (Integer id : ids) {
            if (id == null || id <= 0) {
                skipped++;
                continue;
            }
            if (!force && SharedIconFiles.pngExists(cat, id)) {
                skipped++;
                if (details.size() < MAX_DETAIL_ROWS) {
                    details.add(IconCacheRtnDTO.builder()
                            .category(cat)
                            .id(id)
                            .cached(true)
                            .url(SharedIconFiles.webUrl(cat, id))
                            .source(SOURCE_LOCAL)
                            .message("already local")
                            .build());
                }
                continue;
            }
            IconCacheRtnDTO one = cacheOne(cat, id, force);
            if (one.isCached()) {
                cached++;
            } else {
                failed++;
            }
            if (details.size() < MAX_DETAIL_ROWS) {
                details.add(one);
            }
        }
        long duration = System.currentTimeMillis() - started;
        String message = String.format(Locale.ROOT,
                "category=%s force=%s requested=%d cached=%d skipped=%d failed=%d durationMs=%d",
                cat, force, ids.size(), cached, skipped, failed, duration);
        log.info(I18nUtil.getLogMessage("Asset.ensure.done"), cat, ids.size(), cached, skipped, failed, duration);
        return AssetEnsureRtnDTO.builder()
                .category(cat)
                .force(force)
                .requested(ids.size())
                .cached(cached)
                .skipped(skipped)
                .failed(failed)
                .durationMs(duration)
                .root(SharedIconFiles.describeRoot())
                .message(message)
                .details(details)
                .build();
    }

    private List<Integer> resolveEnsureIds(String category, AssetEnsureReqDTO body) {
        Set<Integer> out = new LinkedHashSet<>();
        if (body.getIds() != null) {
            for (Integer id : body.getIds()) {
                if (id != null && id > 0) {
                    out.add(id);
                }
            }
        }
        if (out.isEmpty() && body.getIdFrom() != null && body.getIdTo() != null) {
            int from = Math.min(body.getIdFrom(), body.getIdTo());
            int to = Math.max(body.getIdFrom(), body.getIdTo());
            if (to - from + 1 > MAX_RANGE_SIZE) {
                to = from + MAX_RANGE_SIZE - 1;
            }
            for (int i = from; i <= to; i++) {
                out.add(i);
            }
        }
        if (out.isEmpty() && Boolean.TRUE.equals(body.getFromCatalog()) && CATEGORY_ITEM.equals(category)) {
            List<XyCashShopItemDO> items = cashShopItemMapper.selectAll();
            if (items != null) {
                for (XyCashShopItemDO item : items) {
                    if (item != null && item.getItemId() != null && item.getItemId() > 0) {
                        out.add(item.getItemId());
                    }
                }
            }
        }
        return new ArrayList<>(out);
    }

    public AssetInfoRtnDTO info() {
        Optional<Path> client = ClientDataPath.resolve();
        List<AssetInfoRtnDTO.ProviderInfo> providers = new ArrayList<>();
        for (IconProvider provider : iconProviders) {
            if (provider == null) {
                continue;
            }
            providers.add(AssetInfoRtnDTO.ProviderInfo.builder()
                    .name(provider.name())
                    .enabled(provider.enabled())
                    .build());
        }
        return AssetInfoRtnDTO.builder()
                .root(SharedIconFiles.describeRoot())
                .legacyItemIconsDir(ItemIconFiles.describeIconDir())
                .clientDataConfigured(client.isPresent())
                .clientDataPath(client.map(Path::toString).orElse(""))
                .providers(providers)
                .build();
    }

    public String buildCdnUrl(String category, int objectId) {
        return mapleStoryIoIconProvider.buildUrl(category, objectId);
    }
}
