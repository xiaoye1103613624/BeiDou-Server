package org.gms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 在已配置的客户端 Data 根目录下浏览 / 建目录 / 删除（严格限制在根内）。
 * <p>
 * 不负责窗口商城业务同步；同步见 {@link WindowCashShopService}。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientFileService {

    private static final Pattern SAFE_NAME = Pattern.compile("^[^\\\\/:*?\"<>|\\r\\n]+$");
    /** Data 根下一层关键目录：删除需 recursive=true。 */
    private static final Set<String> PROTECTED_TOP = Set.of(
            "character", "item", "ui", "map", "sound", "etc", "quest", "string", "skill", "npc", "mob");

    public Map<String, Object> list(String relativePath) {
        final Path root = requireRoot();
        final Path dir = resolveUnderRoot(root, relativePath);
        if (!Files.isDirectory(dir)) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.notDirectory", dir.toString()));
        }
        try (Stream<Path> stream = Files.list(dir)) {
            final List<Map<String, Object>> entries = stream
                    .sorted(Comparator
                            .comparing((Path p) -> !Files.isDirectory(p))
                            .thenComparing(p -> {
                                final Path name = p.getFileName();
                                return name == null ? "" : name.toString().toLowerCase(Locale.ROOT);
                            }))
                    .map(p -> toEntry(root, p))
                    .collect(Collectors.toList());
            final Map<String, Object> out = new LinkedHashMap<>();
            out.put("root", root.toString());
            out.put("relativePath", toRelative(root, dir));
            out.put("absolutePath", dir.toString());
            out.put("entries", entries);
            return out;
        } catch (IOException e) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.list", e.getMessage()));
        }
    }

    public Map<String, Object> mkdir(String parentRelativePath, String name) {
        validateName(name);
        final Path root = requireRoot();
        final Path parent = resolveUnderRoot(root, parentRelativePath);
        if (!Files.isDirectory(parent)) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.notDirectory", parent.toString()));
        }
        final Path target = parent.resolve(name).normalize();
        assertUnderRoot(root, target);
        if (Files.exists(target)) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.alreadyExists", target.toString()));
        }
        try {
            Files.createDirectory(target);
        } catch (IOException e) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.mkdir", e.getMessage()));
        }
        return toEntry(root, target);
    }

    /**
     * @param confirm  前端二次确认后必须传 true
     * @param recursive 非空目录或顶层受保护目录必须为 true
     */
    public Map<String, Object> delete(String relativePath, boolean recursive, boolean confirm) {
        if (!confirm) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.deleteNeedConfirm"));
        }
        if (!StringUtils.hasText(relativePath)) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.cannotDeleteRoot"));
        }
        final Path root = requireRoot();
        final Path target = resolveUnderRoot(root, relativePath);
        if (!Files.exists(target)) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.notFound", target.toString()));
        }
        if (target.equals(root)) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.cannotDeleteRoot"));
        }
        if (isProtectedTopLevel(root, target) && !recursive) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.protectedNeedRecursive",
                    target.getFileName() == null ? target.toString() : target.getFileName().toString()));
        }
        final Map<String, Object> snapshot = toEntry(root, target);
        try {
            if (Files.isDirectory(target)) {
                if (!recursive) {
                    try (Stream<Path> children = Files.list(target)) {
                        if (children.findAny().isPresent()) {
                            throw new BizException(I18nUtil.getMessage("ClientFile.fail.dirNotEmpty"));
                        }
                    }
                    Files.delete(target);
                } else {
                    deleteRecursive(target);
                }
            } else {
                Files.delete(target);
            }
        } catch (BizException e) {
            throw e;
        } catch (IOException e) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.delete", e.getMessage()));
        }
        log.warn("[clientFile] deleted path={} recursive={}", snapshot.get("relativePath"), recursive);
        snapshot.put("deleted", true);
        return snapshot;
    }

    private Path requireRoot() {
        return ClientDataPath.resolve()
                .orElseThrow(() -> new BizException(I18nUtil.getMessage("ClientFile.fail.unconfigured")));
    }

    private Path resolveUnderRoot(Path root, String relativePath) {
        final Path resolved;
        if (!StringUtils.hasText(relativePath) || ".".equals(relativePath.trim()) || "/".equals(relativePath.trim())) {
            resolved = root;
        } else {
            String rel = relativePath.trim().replace('\\', '/');
            while (rel.startsWith("/")) {
                rel = rel.substring(1);
            }
            resolved = root.resolve(rel).normalize();
        }
        assertUnderRoot(root, resolved);
        return resolved;
    }

    private void assertUnderRoot(Path root, Path candidate) {
        final Path normalized = candidate.toAbsolutePath().normalize();
        final Path rootNorm = root.toAbsolutePath().normalize();
        if (!normalized.startsWith(rootNorm)) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.pathEscape"));
        }
    }

    private boolean isProtectedTopLevel(Path root, Path target) {
        final Path rootNorm = root.toAbsolutePath().normalize();
        final Path targetNorm = target.toAbsolutePath().normalize();
        final Path parent = targetNorm.getParent();
        if (parent == null || !parent.equals(rootNorm)) {
            return false;
        }
        final Path name = targetNorm.getFileName();
        if (name == null) {
            return false;
        }
        return PROTECTED_TOP.contains(name.toString().toLowerCase(Locale.ROOT));
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name) || ".".equals(name) || "..".equals(name) || !SAFE_NAME.matcher(name).matches()) {
            throw new BizException(I18nUtil.getMessage("ClientFile.fail.invalidName"));
        }
    }

    private Map<String, Object> toEntry(Path root, Path path) {
        final Map<String, Object> m = new LinkedHashMap<>();
        final Path fileName = path.getFileName();
        m.put("name", fileName == null ? path.toString() : fileName.toString());
        m.put("relativePath", toRelative(root, path));
        m.put("absolutePath", path.toString());
        final boolean directory = Files.isDirectory(path);
        m.put("directory", directory);
        try {
            m.put("size", directory ? 0L : Files.size(path));
            m.put("lastModified", Files.getLastModifiedTime(path).toMillis());
        } catch (IOException e) {
            m.put("size", 0L);
            m.put("lastModified", 0L);
        }
        return m;
    }

    private String toRelative(Path root, Path path) {
        final Path rootNorm = root.toAbsolutePath().normalize();
        final Path pathNorm = path.toAbsolutePath().normalize();
        if (pathNorm.equals(rootNorm)) {
            return "";
        }
        return rootNorm.relativize(pathNorm).toString().replace('\\', '/');
    }

    private void deleteRecursive(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
