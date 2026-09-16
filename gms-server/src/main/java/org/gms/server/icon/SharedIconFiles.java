package org.gms.server.icon;

import lombok.extern.slf4j.Slf4j;
import org.gms.server.cashshop.ItemIconFiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Optional;

/**
 * Unified game icon cache on disk: {@code static/game-assets/{type}/{id}.png}.
 * <p>
 * Also reads legacy {@code tools/_icon_cache} and item-only {@code /item-icons} trees.
 * DB does not store icon blobs — only optional URL strings (e.g. window cash shop).
 */
@Slf4j
public final class SharedIconFiles {
    public static final String WEB_URL_PREFIX = "/game-assets/";
    /** Legacy URL prefix still mapped by {@link org.gms.config.IconStaticConfig}. */
    public static final String LEGACY_WEB_URL_PREFIX = "/icons/";

    private SharedIconFiles() {
    }

    public static Path resolveOrCreateRoot() {
        Path existing = resolveExistingRoot().orElse(null);
        if (existing != null) {
            return existing;
        }
        Path created = Paths.get("static/game-assets").toAbsolutePath().normalize();
        try {
            Files.createDirectories(created);
        } catch (IOException e) {
            log.warn("create game-assets root failed {}: {}", created, e.toString());
        }
        return created;
    }

    public static Optional<Path> resolveExistingRoot() {
        String[] candidates = {
                "static/game-assets",
                "gms-server/static/game-assets",
                System.getProperty("user.dir") + "/static/game-assets",
                System.getProperty("user.dir") + "/gms-server/static/game-assets",
                "src/main/resources/static/game-assets",
                "gms-server/src/main/resources/static/game-assets",
                "tools/_icon_cache",
                "gms-server/tools/_icon_cache",
                System.getProperty("user.dir") + "/tools/_icon_cache",
                System.getProperty("user.dir") + "/gms-server/tools/_icon_cache"
        };
        for (String candidate : candidates) {
            Path path = Paths.get(candidate);
            if (Files.isDirectory(path)) {
                return Optional.of(path.toAbsolutePath().normalize());
            }
        }
        return Optional.empty();
    }

    public static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return "item";
        }
        String cat = category.trim().toLowerCase(Locale.ROOT);
        return switch (cat) {
            case "cash", "consume", "eqp", "etc", "ins", "pet", "equip" -> "item";
            case "monster", "mobs" -> "mob";
            case "npcs" -> "npc";
            case "skills" -> "skill";
            case "maps" -> "map";
            default -> cat;
        };
    }

    public static Path pngPath(String category, int objectId) {
        String cat = normalizeCategory(category);
        return resolveOrCreateRoot().resolve(cat).resolve(objectId + ".png");
    }

    public static boolean pngExists(String category, int objectId) {
        return Files.isRegularFile(pngPath(category, objectId));
    }

    public static Optional<byte[]> readPng(String category, int objectId) {
        Path path = pngPath(category, objectId);
        Optional<byte[]> data = readPngFile(path);
        if (data.isPresent()) {
            return data;
        }
        String cat = normalizeCategory(category);
        if ("item".equals(cat)) {
            try {
                Path itemDir = ItemIconFiles.resolveOrCreateIconDir();
                return readPngFile(ItemIconFiles.pngPath(itemDir, objectId));
            } catch (Exception e) {
                log.debug("read legacy item-icons {} failed: {}", objectId, e.toString());
            }
        }
        return Optional.empty();
    }

    private static Optional<byte[]> readPngFile(Path path) {
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            byte[] data = Files.readAllBytes(path);
            if (isPng(data)) {
                return Optional.of(data);
            }
        } catch (IOException e) {
            log.warn("read icon cache {} failed: {}", path, e.toString());
        }
        return Optional.empty();
    }

    public static boolean isPng(byte[] png) {
        return png != null && png.length >= 8 && png[0] == (byte) 0x89 && png[1] == 0x50;
    }

    /**
     * Write only under {@code game-assets}. Legacy {@code /item-icons} is read/promote only.
     */
    public static boolean writePng(String category, int objectId, byte[] png) {
        if (!isPng(png)) {
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

    /**
     * Promote legacy item-icons / old cache into unified game-assets when missing.
     */
    public static boolean promoteLegacyIfPresent(String category, int objectId) {
        if (pngExists(category, objectId)) {
            return true;
        }
        String cat = normalizeCategory(category);
        if (!"item".equals(cat)) {
            return false;
        }
        if (ItemIconFiles.copyFromLegacyCacheIfPresent(objectId) || ItemIconFiles.pngExists(objectId)) {
            try {
                Path itemDir = ItemIconFiles.resolveOrCreateIconDir();
                Path src = ItemIconFiles.pngPath(itemDir, objectId);
                if (Files.isRegularFile(src)) {
                    Path dest = pngPath(cat, objectId);
                    Files.createDirectories(dest.getParent());
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    return true;
                }
            } catch (IOException e) {
                log.warn("promote legacy item icon {} failed: {}", objectId, e.toString());
            }
        }
        return false;
    }

    public static String webUrl(String category, int objectId) {
        String cat = normalizeCategory(category);
        return WEB_URL_PREFIX + cat + "/" + objectId + ".png";
    }

    public static String describeRoot() {
        return resolveOrCreateRoot().toString();
    }
}
