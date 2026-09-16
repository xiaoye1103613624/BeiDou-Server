package org.gms.service;

import lombok.RequiredArgsConstructor;
import org.gms.model.dto.IconCacheRtnDTO;
import org.gms.model.dto.IconResolveRtnDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Backward-compatible façade over {@link AssetService}.
 * Prefer injecting {@link AssetService} for new code.
 */
@Service
@RequiredArgsConstructor
public class GameIconService {
    public static final String CATEGORY_ITEM = AssetService.CATEGORY_ITEM;
    public static final String SOURCE_LOCAL = AssetService.SOURCE_LOCAL;
    public static final String SOURCE_CDN = AssetService.SOURCE_CDN;
    public static final String SOURCE_NONE = AssetService.SOURCE_NONE;

    private final AssetService assetService;

    public Optional<byte[]> ensureItemIconBytes(int itemId, boolean force) {
        return assetService.ensureItemIconBytes(itemId, force);
    }

    public Optional<byte[]> ensureIconBytes(String category, int objectId, boolean force) {
        return assetService.ensureIconBytes(category, objectId, force);
    }

    public IconResolveRtnDTO resolve(String category, int objectId) {
        return assetService.resolve(category, objectId);
    }

    public IconCacheRtnDTO cacheOne(String category, int objectId, boolean force) {
        return assetService.cacheOne(category, objectId, force);
    }

    public List<IconCacheRtnDTO> cacheBatch(List<IconCacheRtnDTO.IconRef> refs, boolean force) {
        return assetService.cacheBatch(refs, force);
    }

    public String buildCdnUrl(String category, int objectId) {
        return assetService.buildCdnUrl(category, objectId);
    }
}
