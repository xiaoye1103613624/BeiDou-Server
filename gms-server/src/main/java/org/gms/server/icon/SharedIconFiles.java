package org.gms.server.icon;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;

/**
 * Filesystem mirror of {@code xy_icon_cache}: {@code tools/_icon_cache/{type}/{id}.png}.
 */
@Slf4j
public final class SharedIconFiles {
    public static final String WEB_URL_PREFIX = "/icons/";

    private SharedIconFiles() {
    }

    public static Path resolveOrCreateRoot() {
        Path existing = resolveExistingRoot().orElse(null);
        if (existing != null) {
            return existing;
        }
        Path created = Paths.get("tools/_icon_cache").toAbsolutePath().normalize();
        try {
            Files.createDirectories(created);
        } catch (IOException e) {
            log.warn("create icon cache root failed {}: {}", created, e.toString());
        }
        return created;
    }

    public static Optional<Path> resolveExistingRoot() {
        String[] candidates = {
                "tools/_icon_cache",
                "gms-server/tools/_icon_cache",
                System.getProperty("user.dir") + "/tools/_icon_cache",
                System.getProperty("user.dir") + "/gms-server/tools/_icon_cache",
                "F:/MXD_dev/BeiDou-Server/gms-server/tools/_icon_cache"
        };
        for (String candidate : candidates) {
            Path path = Paths.get(candidate);
            if (Files.isDirectory(path)) {
                return Optional.of(path.toAbsolutePath().normalize());
            }
        }
        return Optional.empty();
    }

    public static Path pngPath(String category, int objectId) {
        String cat = category == null ? "item" : category.trim().toLowerCase(Locale.ROOT);
        return resolveOrCreateRoot().resolve(cat).resolve(objectId + ".png");
    }

    public static Optional<byte[]> readPng(String category, int objectId) {
        Path path = pngPath(category, objectId);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            byte[] data = Files.readAllBytes(path);
            if (data.length >= 8 && data[0] == (byte) 0x89 && data[1] == 0x50) {
                return Optional.of(data);
            }
        } catch (IOException e) {
            log.warn("read icon cache {} failed: {}", path, e.toString());
        }
        return Optional.empty();
    }

    public static boolean writePng(String category, int objectId, byte[] png) {
        if (png == null || png.length < 8 || png[0] != (byte) 0x89 || png[1] != 0x50) {
            return false;
        }
        Path path = pngPath(category, objectId);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, png);
            return true;
        } catch (IOException e) {
            log.warn("write icon cache {} failed: {}", path, e.toString());
            return false;
        }
    }

    public static String webUrl(String category, int objectId) {
        String cat = category == null ? "item" : category.trim().toLowerCase(Locale.ROOT);
        return WEB_URL_PREFIX + cat + "/" + objectId + ".png";
    }
}
