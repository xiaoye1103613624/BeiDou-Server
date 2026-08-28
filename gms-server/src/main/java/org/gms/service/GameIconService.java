package org.gms.service;

import lombok.extern.slf4j.Slf4j;
import org.gms.server.cashshop.ItemIconFiles;
import org.gms.server.icon.SharedIconFiles;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * 窗口现金商城图标同步所需的最小实现：优先读本地 item-icons / 共用 icon 缓存。
 */
@Slf4j
@Service
public class GameIconService {
    public static final String CATEGORY_ITEM = "item";

    public Optional<byte[]> ensureItemIconBytes(int itemId, boolean force) {
        if (itemId <= 0) {
            return Optional.empty();
        }
        if (!force) {
            Optional<byte[]> cached = readLocalItemIcon(itemId);
            if (cached.isPresent()) {
                return cached;
            }
        }
        ItemIconFiles.copyFromLegacyCacheIfPresent(itemId);
        return readLocalItemIcon(itemId);
    }

    private Optional<byte[]> readLocalItemIcon(int itemId) {
        Optional<byte[]> fromShared = SharedIconFiles.readPng(CATEGORY_ITEM, itemId);
        if (fromShared.isPresent()) {
            return fromShared;
        }
        try {
            Path dir = ItemIconFiles.resolveOrCreateIconDir();
            Path png = ItemIconFiles.pngPath(dir, itemId);
            if (!Files.isRegularFile(png)) {
                return Optional.empty();
            }
            byte[] data = Files.readAllBytes(png);
            if (data.length >= 8 && data[0] == (byte) 0x89 && data[1] == 0x50) {
                return Optional.of(data);
            }
        } catch (Exception e) {
            log.debug("read local item icon {} failed: {}", itemId, e.toString());
        }
        return Optional.empty();
    }
}
