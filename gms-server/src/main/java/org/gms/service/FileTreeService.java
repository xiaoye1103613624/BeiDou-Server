package org.gms.service;

import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.model.dto.FileTreeNodeDTO;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
public class FileTreeService {

    private static final String FILE_TREE_BASE_DIR = System.getProperty("user.dir");
    private static final Path FILE_TREE_BASE_DIR_PATH = Path.of(FILE_TREE_BASE_DIR).toAbsolutePath().normalize();
    private static final String FILE_TREE_KEY_DELIMITER = "-";
    private static final Set<String> FILE_TREE_LIMITED_PATTERNS = new HashSet<>();
    private static final boolean FILE_TREE_PATH_STRICT_MODE = true;
    private static final Pattern SAFE_NAME = Pattern.compile("^[^\\\\/:*?\"<>|\\r\\n]+$");

    static {
        FILE_TREE_LIMITED_PATTERNS.add("scripts");
        FILE_TREE_LIMITED_PATTERNS.add("scripts-zh-CN");
        FILE_TREE_LIMITED_PATTERNS.add("wz");
        FILE_TREE_LIMITED_PATTERNS.add("wz-zh-CN");
    }

    public String readFile(String currentKey, String filename) {
        File file = resolveByTreeKey(currentKey);
        if (!filename.equals(file.getName())) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.STRUCTURE_CHANGED"));
        }
        try {
            return Files.readString(file.toPath(), UTF_8);
        } catch (MalformedInputException e) {
            log.error("file {} is not using utf8", filename);
            try {
                return Files.readString(file.toPath(), ISO_8859_1);
            } catch (IOException ex) {
                log.error("io error", ex);
                throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.READ_ERROR"));
            }
        } catch (IOException e) {
            log.error("io error", e);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.READ_ERROR"));
        }
    }

    public void writeFile(String currentKey, String filename, String content) {
        File file = resolveByTreeKey(currentKey);
        if (!filename.equals(file.getName())) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.STRUCTURE_CHANGED"));
        }
        try {
            Files.writeString(file.toPath(), content, UTF_8);
        } catch (IOException e) {
            log.error("io error", e);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.WRITE_ERROR"));
        }
    }

    public List<FileTreeNodeDTO> tree(String currentKey) {
        boolean root = !StringUtils.hasText(currentKey);
        File current = root ? FILE_TREE_BASE_DIR_PATH.toFile() : resolveByTreeKey(currentKey);

        if (!current.isDirectory()) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.NOT_DIRECTORY"));
        }

        boolean parentIsMatch = matchAnyLimitPattern(current.toPath());
        File[] listFiles = sortedChildren(current);
        if (listFiles.length == 0) {
            return Collections.emptyList();
        }

        List<FileTreeNodeDTO> nodes = new ArrayList<>();
        for (int i = 0; i < listFiles.length; i++) {
            File currentSubFile = listFiles[i];
            if (!parentIsMatch && !matchAnyLimitPattern(currentSubFile.toPath())) {
                continue;
            }
            String childKey = String.valueOf(i);
            String key = root ? childKey : String.join(FILE_TREE_KEY_DELIMITER, currentKey, childKey);
            nodes.add(new FileTreeNodeDTO(currentSubFile, key, FILE_TREE_BASE_DIR_PATH));
        }
        return nodes;
    }

    public FileTreeNodeDTO create(String parentKey, String name, boolean directory) {
        validateName(name);
        File parent = resolveParent(parentKey);
        Path target = parent.toPath().resolve(name).normalize();
        assertWithinAllowed(target);
        if (Files.exists(target)) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.ALREADY_EXISTS"));
        }
        try {
            if (directory) {
                Files.createDirectories(target);
            } else {
                Files.createDirectories(target.getParent());
                Files.createFile(target);
            }
        } catch (IOException e) {
            log.error("create error", e);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.CREATE_ERROR"));
        }
        return toNode(target.toFile(), parentKey);
    }

    public void rename(String currentKey, String newName) {
        validateName(newName);
        File source = resolveByTreeKey(currentKey);
        assertNotProtectedRoot(source);
        Path target = source.toPath().getParent().resolve(newName).normalize();
        assertWithinAllowed(target);
        if (Files.exists(target)) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.ALREADY_EXISTS"));
        }
        try {
            Files.move(source.toPath(), target);
        } catch (IOException e) {
            log.error("rename error", e);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.RENAME_ERROR"));
        }
    }

    public void copy(String currentKey, String targetParentKey, String newName) {
        File source = resolveByTreeKey(currentKey);
        File targetParent = resolveParent(targetParentKey);
        String name = StringUtils.hasText(newName) ? newName : source.getName();
        validateName(name);
        Path target = targetParent.toPath().resolve(name).normalize();
        assertWithinAllowed(target);
        if (source.toPath().normalize().equals(target)) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.INVALID_TARGET"));
        }
        if (Files.exists(target)) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.ALREADY_EXISTS"));
        }
        try {
            copyRecursive(source.toPath(), target);
        } catch (IOException e) {
            log.error("copy error", e);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.COPY_ERROR"));
        }
    }

    public void move(String currentKey, String targetParentKey) {
        File source = resolveByTreeKey(currentKey);
        assertNotProtectedRoot(source);
        File targetParent = resolveParent(targetParentKey);
        Path target = targetParent.toPath().resolve(source.getName()).normalize();
        assertWithinAllowed(target);
        Path sourcePath = source.toPath().normalize();
        if (sourcePath.equals(target)) {
            return;
        }
        if (Files.isDirectory(sourcePath) && target.startsWith(sourcePath)) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.INVALID_TARGET"));
        }
        if (Files.exists(target)) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.ALREADY_EXISTS"));
        }
        try {
            Files.move(sourcePath, target);
        } catch (IOException e) {
            log.error("move error", e);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.MOVE_ERROR"));
        }
    }

    public void delete(String currentKey) {
        File source = resolveByTreeKey(currentKey);
        Path path = source.toPath().normalize();
        assertWithinAllowed(path);
        assertNotProtectedRoot(source);
        try {
            deleteRecursive(path);
        } catch (IOException e) {
            log.error("delete error", e);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.DELETE_ERROR"));
        }
    }

    /** 白名单根目录（scripts / wz 等）不可重命名、移动或删除。 */
    private void assertNotProtectedRoot(File source) {
        Path path = source.toPath().toAbsolutePath().normalize();
        if (FILE_TREE_LIMITED_PATTERNS.contains(source.getName())
                && path.getParent() != null
                && path.getParent().equals(FILE_TREE_BASE_DIR_PATH)) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.CANNOT_MODIFY_ROOT"));
        }
    }

    public String toRelativePath(String currentKey) {
        File file = resolveByTreeKey(currentKey);
        return FILE_TREE_BASE_DIR_PATH.relativize(file.toPath().toAbsolutePath().normalize())
                .toString()
                .replace('\\', '/');
    }

    public File resolveByTreeKey(String currentKey) {
        if (!StringUtils.hasText(currentKey)) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.NOT_FOUND"));
        }
        File current = FILE_TREE_BASE_DIR_PATH.toFile();
        String[] keyArray = currentKey.split(FILE_TREE_KEY_DELIMITER);
        try {
            for (String keyStr : keyArray) {
                int key = Integer.parseInt(keyStr);
                File[] children = sortedChildren(current);
                current = children[key];
            }
        } catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            log.error("file not exists", e);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.NOT_FOUND"));
        }

        assertWithinAllowed(current.toPath());
        return current;
    }

    private File resolveParent(String parentKey) {
        if (!StringUtils.hasText(parentKey)) {
            return FILE_TREE_BASE_DIR_PATH.toFile();
        }
        File parent = resolveByTreeKey(parentKey);
        if (!parent.isDirectory()) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.NOT_DIRECTORY"));
        }
        return parent;
    }

    private FileTreeNodeDTO toNode(File file, String parentKey) {
        File parent = file.getParentFile();
        File[] siblings = sortedChildren(parent);
        int index = -1;
        for (int i = 0; i < siblings.length; i++) {
            if (siblings[i].getName().equals(file.getName())) {
                index = i;
                break;
            }
        }
        if (index < 0) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.NOT_FOUND"));
        }
        String key = !StringUtils.hasText(parentKey)
                ? String.valueOf(index)
                : String.join(FILE_TREE_KEY_DELIMITER, parentKey, String.valueOf(index));
        return new FileTreeNodeDTO(file, key, FILE_TREE_BASE_DIR_PATH);
    }

    private File[] sortedChildren(File dir) {
        File[] listFiles = dir.listFiles();
        if (listFiles == null) {
            return new File[0];
        }
        Arrays.sort(listFiles, Comparator.comparing(File::getName, String.CASE_INSENSITIVE_ORDER));
        return listFiles;
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name) || name.equals(".") || name.equals("..") || !SAFE_NAME.matcher(name).matches()) {
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.INVALID_NAME"));
        }
    }

    private void assertWithinAllowed(Path path) {
        if (!FILE_TREE_PATH_STRICT_MODE) {
            return;
        }
        Path userPath = path.toAbsolutePath().normalize();
        if (!userPath.startsWith(FILE_TREE_BASE_DIR_PATH)) {
            log.error("file escape base dir : {}", userPath);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.PATH_ESCAPE"));
        }
        if (!matchAnyLimitPattern(userPath) && !userPath.equals(FILE_TREE_BASE_DIR_PATH)) {
            log.error("file escape limit pattern : {}", userPath);
            throw new BizException(I18nUtil.getExceptionMessage("FILE_TREE.PATH_ESCAPE"));
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean matchAnyLimitPattern(Path path) {
        if (FILE_TREE_PATH_STRICT_MODE) {
            Path normalized = path.toAbsolutePath().normalize();
            return FILE_TREE_LIMITED_PATTERNS.stream()
                    .anyMatch(it -> normalized.startsWith(FILE_TREE_BASE_DIR_PATH.resolve(it)));
        }
        return true;
    }

    private void copyRecursive(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path relative = source.relativize(dir);
                Path dest = target.resolve(relative);
                Files.createDirectories(dest);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relative = source.relativize(file);
                Files.copy(file, target.resolve(relative), StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private void deleteRecursive(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
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
