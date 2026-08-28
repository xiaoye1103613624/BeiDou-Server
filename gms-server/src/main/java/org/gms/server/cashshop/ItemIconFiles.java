package org.gms.server.cashshop;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;

/**
 * Admin UI item icons under {@code /item-icons/{itemId}.png}.
 * <p>
 * Resolves an existing cache directory (MXD tools, legacy E: tree) or creates
 * {@code gms-server/tools/_full_icon_sync/web_png} so sync can write PNGs.
 */
@Slf4j
public final class ItemIconFiles {
    public static final String WEB_URL_PREFIX = "/item-icons/";

    private ItemIconFiles() {
    }

    public static String webUrl(int itemId) {
        return WEB_URL_PREFIX + itemId + ".png";
    }

    /** Prefer an existing directory with icons; otherwise create writable MXD path. */
    public static Path resolveOrCreateIconDir() {
        Path existing = resolveExistingIconDir().orElse(null);
        if (existing != null) {
            return existing;
        }
        Path created = Paths.get("tools/_full_icon_sync/web_png").toAbsolutePath().normalize();
        try {
            Files.createDirectories(created);
            return created;
        } catch (IOException e) {
            log.warn("create icon dir failed {}: {}", created, e.toString());
            return created;
        }
    }

    public static Optional<Path> resolveExistingIconDir() {
        String[] candidates = {
                "tools/_full_icon_sync/web_png",
                "gms-server/tools/_full_icon_sync/web_png",
                System.getProperty("user.dir") + "/tools/_full_icon_sync/web_png",
                System.getProperty("user.dir") + "/gms-server/tools/_full_icon_sync/web_png",
                "F:/MXD_dev/BeiDou-Server/gms-server/tools/_full_icon_sync/web_png",
                "E:/pro/BeiDou-Server_xy/gms-server/tools/_full_icon_sync/web_png"
        };
        for (String candidate : candidates) {
            Path path = Paths.get(candidate);
            if (Files.isDirectory(path)) {
                return Optional.of(path.toAbsolutePath().normalize());
            }
        }
        return Optional.empty();
    }

    public static Path pngPath(Path iconDir, int itemId) {
        return iconDir.resolve(itemId + ".png");
    }

    public static boolean pngExists(int itemId) {
        Path dir = resolveOrCreateIconDir();
        return Files.isRegularFile(pngPath(dir, itemId));
    }

    public static boolean writePng(int itemId, byte[] png) {
        if (png == null || png.length < 8) {
            return false;
        }
        if (png[0] != (byte) 0x89 || png[1] != 0x50) {
            return false;
        }
        Path dir = resolveOrCreateIconDir();
        try {
            Files.createDirectories(dir);
            Files.write(pngPath(dir, itemId), png);
            return true;
        } catch (IOException e) {
            log.warn("write item icon {} failed: {}", itemId, e.toString());
            return false;
        }
    }

    /**
     * Copy from another known cache root if present (legacy offline sync).
     */
    public static boolean copyFromLegacyCacheIfPresent(int itemId) {
        if (pngExists(itemId)) {
            return true;
        }
        Path targetDir = resolveOrCreateIconDir();
        Path legacy = Paths.get("E:/pro/BeiDou-Server_xy/gms-server/tools/_full_icon_sync/web_png");
        if (!Files.isDirectory(legacy) || legacy.toAbsolutePath().normalize().equals(targetDir)) {
            return false;
        }
        Path src = pngPath(legacy, itemId);
        if (!Files.isRegularFile(src)) {
            return false;
        }
        try {
            Files.createDirectories(targetDir);
            Files.copy(src, pngPath(targetDir, itemId), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            log.warn("copy legacy icon {} failed: {}", itemId, e.toString());
            return false;
        }
    }

    public static String describeIconDir() {
        return resolveOrCreateIconDir().toString();
    }

    /** Best-effort: locate Character/Item img for documentation / future extract. */
    public static Optional<Path> findClientItemImg(Path dataRoot, int itemId) {
        if (dataRoot == null) {
            return Optional.empty();
        }
        String id8 = String.format(Locale.ROOT, "%08d", itemId);
        int type = itemId / 10000;
        if (type == 100) {
            Path p = dataRoot.resolve("Character/Cap/" + id8 + ".img");
            if (Files.exists(p)) {
                return Optional.of(p);
            }
        } else if (type >= 101 && type <= 199) {
            String folder = CashShopAssetCheck.categoryFolderPublic(type);
            Path p = dataRoot.resolve("Character/" + folder + "/" + id8 + ".img");
            if (Files.exists(p)) {
                return Optional.of(p);
            }
        } else if (itemId >= 5000000 && itemId < 5010000) {
            Path p = dataRoot.resolve("Item/Pet/" + id8 + ".img");
            if (Files.exists(p)) {
                return Optional.of(p);
            }
        } else {
            String prefix4 = id8.substring(0, 4);
            for (String sub : new String[]{"Cash", "Consume", "Etc", "Install"}) {
                Path p = dataRoot.resolve("Item/" + sub + "/" + prefix4 + ".img");
                if (Files.exists(p)) {
                    return Optional.of(p);
                }
                Path loose = dataRoot.resolve("Item/" + sub + "/" + id8 + ".img");
                if (Files.exists(loose)) {
                    return Optional.of(loose);
                }
            }
        }
        return Optional.empty();
    }
}
