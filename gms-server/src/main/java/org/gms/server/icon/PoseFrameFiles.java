package org.gms.server.icon;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

/**
 * WZ 1:1 pose-frame PNG cache for chair/mount preview (not inventory icons).
 * <p>
 * Layout: {@code static/game-assets/pose-frame/{kind}/{id}/{layerOrAction}/{frame}.png}
 * Web: {@code /game-assets/pose-frame/...}
 */
@Slf4j
public final class PoseFrameFiles {
    public static final String KIND_CHAIR = "chair";
    public static final String KIND_TAMING = "taming";
    /** 技能 effect 等多帧预览缓存。 */
    public static final String KIND_SKILL = "skill";

    private PoseFrameFiles() {
    }

    public static Path pngPath(String kind, int objectId, String layerOrAction, int frameIndex) {
        String k = normalizeKind(kind);
        String layer = sanitizeSegment(layerOrAction, "effect");
        return SharedIconFiles.resolveOrCreateRoot()
                .resolve("pose-frame")
                .resolve(k)
                .resolve(String.valueOf(objectId))
                .resolve(layer)
                .resolve(frameIndex + ".png");
    }

    public static String webUrl(String kind, int objectId, String layerOrAction, int frameIndex) {
        String k = normalizeKind(kind);
        String layer = sanitizeSegment(layerOrAction, "effect");
        return SharedIconFiles.WEB_URL_PREFIX + "pose-frame/" + k + "/" + objectId + "/" + layer + "/" + frameIndex + ".png";
    }

    public static boolean pngExists(String kind, int objectId, String layerOrAction, int frameIndex) {
        return Files.isRegularFile(pngPath(kind, objectId, layerOrAction, frameIndex));
    }

    public static Optional<byte[]> readPng(String kind, int objectId, String layerOrAction, int frameIndex) {
        Path path = pngPath(kind, objectId, layerOrAction, frameIndex);
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try {
            byte[] data = Files.readAllBytes(path);
            if (SharedIconFiles.isPng(data)) {
                return Optional.of(data);
            }
        } catch (IOException e) {
            log.warn("read pose-frame {} failed: {}", path, e.toString());
        }
        return Optional.empty();
    }

    public static boolean writePng(String kind, int objectId, String layerOrAction, int frameIndex, byte[] png) {
        if (!SharedIconFiles.isPng(png)) {
            return false;
        }
        Path path = pngPath(kind, objectId, layerOrAction, frameIndex);
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, png);
            return true;
        } catch (IOException e) {
            log.warn("write pose-frame {} failed: {}", path, e.toString());
            return false;
        }
    }

    /**
     * 人偶合成 PNG（整偶）路径：{@code pose-frame/doll/{lookKey}/{pose}_{frame}.png}。
     * <p>
     * lookKey 为外观摘要，见 {@code CharacterDollService#lookKey}。
     */
    public static Path dollPngPath(String lookKey, String pose, int frameIndex) {
        String key = sanitizeFileSegment(lookKey, "default");
        return SharedIconFiles.resolveOrCreateRoot()
                .resolve("pose-frame")
                .resolve("doll")
                .resolve(key)
                .resolve(pose + "_" + frameIndex + ".png");
    }

    public static String dollWebUrl(String lookKey, String pose, int frameIndex) {
        String key = sanitizeFileSegment(lookKey, "default");
        String act = sanitizeFileSegment(pose, "sit");
        return SharedIconFiles.WEB_URL_PREFIX + "pose-frame/doll/" + key + "/" + act + "_" + frameIndex + ".png";
    }

    /**
     * 人偶单部位像素路径：{@code pose-frame/doll-part/{kind}/{ownerId}/{nodePath}.png}。
     * <p>
     * nodePath 可能含层级（如 {@code default/hairShade/0}），此处把分隔符拍平为 {@code _}。
     */
    public static Path dollPartPngPath(String kind, int ownerId, String nodePath) {
        String k = sanitizeFileSegment(kind, "part");
        String node = sanitizeFileSegment(nodePath, "node");
        return SharedIconFiles.resolveOrCreateRoot()
                .resolve("pose-frame")
                .resolve("doll-part")
                .resolve(k)
                .resolve(String.valueOf(ownerId))
                .resolve(node + ".png");
    }

    public static boolean dollPngExists(String lookKey, String pose, int frameIndex) {
        return Files.isRegularFile(dollPngPath(lookKey, pose, frameIndex));
    }

    public static boolean writeTo(Path target, byte[] png) {
        if (!SharedIconFiles.isPng(png)) {
            return false;
        }
        try {
            Files.createDirectories(target.getParent());
            Files.write(target, png);
            return true;
        } catch (IOException e) {
            log.warn("write png {} failed: {}", target, e.toString());
            return false;
        }
    }

    public static Optional<byte[]> readPngBytes(Path path) {
        if (!Files.isRegularFile(path)) {
            return Optional.empty();
        }
        try (InputStream in = Files.newInputStream(path)) {
            byte[] data = in.readAllBytes();
            if (SharedIconFiles.isPng(data)) {
                return Optional.of(data);
            }
        } catch (IOException e) {
            log.warn("read png {} failed: {}", path, e.toString());
        }
        return Optional.empty();
    }

    /** 整段路径拍平成安全文件名（含层级分隔符），不含扩展名。 */
    private static String sanitizeFileSegment(String raw, String fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String s = raw.trim().replace('\\', '/').replaceAll("[^a-zA-Z0-9._-]", "_");
        return s.isEmpty() ? fallback : s;
    }

    private static String normalizeKind(String kind) {
        if (kind == null || kind.isBlank()) {
            return KIND_CHAIR;
        }
        String k = kind.trim().toLowerCase(Locale.ROOT);
        if (k.contains("skill")) {
            return KIND_SKILL;
        }
        if (k.contains("tam") || k.contains("mob") || k.contains("mount")) {
            return KIND_TAMING;
        }
        return KIND_CHAIR;
    }

    private static String sanitizeSegment(String raw, String fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String s = raw.trim().replace('\\', '/');
        int slash = s.lastIndexOf('/');
        if (slash >= 0) {
            s = s.substring(slash + 1);
        }
        s = s.replaceAll("[^a-zA-Z0-9._-]", "_");
        return s.isEmpty() ? fallback : s;
    }
}
