package org.gms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.model.dto.ScriptTreeNodeDTO;
import org.gms.property.ServiceProperty;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

/**
 * 【业务服务】ScriptFileService：脚本文件管理，支持外部覆盖目录。
 *
 * @author 萧曵
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScriptFileService {

    /** 脚本目录名称 */
    private static final String SCRIPTS_NAME = "scripts";
    /** 用户工作目录 */
    private static final Path USER_DIR = Path.of(System.getProperty("user.dir"));

    /** 服务配置属性 */
    private final ServiceProperty serviceProperty;

    /**
     * 获取基础目录：覆盖路径优先，否则user.dir。
     */
    private Path resolveBaseDir() {
        String overridePath = serviceProperty.getScriptOverridePath();
        if (overridePath != null && !overridePath.isBlank()) {
            Path p = Path.of(overridePath);
            if (!p.isAbsolute()) {
                p = USER_DIR.resolve(p);
            }
            return p.toAbsolutePath().normalize();
        }
        return USER_DIR;
    }

    /**
     * 获取脚本语言目录名，如 "scripts-zh-CN"。
     */
    private String getScriptLangName() {
        return SCRIPTS_NAME + "-" + serviceProperty.getLanguage();
    }

    /**
     * 读取脚本文件时使用：优先语言目录，回退基础目录。
     */
    private Path resolveReadPath(String relativePath) {
        Path baseDir = resolveBaseDir();
        Path langPath = baseDir.resolve(getScriptLangName()).resolve(relativePath).normalize();
        if (Files.exists(langPath)) {
            validatePath(langPath, baseDir);
            return langPath;
        }
        Path fallbackPath = baseDir.resolve(SCRIPTS_NAME).resolve(relativePath).normalize();
        if (Files.exists(fallbackPath)) {
            validatePath(fallbackPath, baseDir);
            return fallbackPath;
        }
        // 如果都不存在，返回语言目录路径（供后续创建等操作使用）
        validatePath(langPath, baseDir);
        return langPath;
    }

    /**
     * 写入时始终使用语言目录。
     */
    private Path resolveWritePath(String relativePath) {
        Path baseDir = resolveBaseDir();
        Path langPath = baseDir.resolve(getScriptLangName()).resolve(relativePath).normalize();
        validatePath(langPath, baseDir);
        return langPath;
    }

    /**
     * 目录列表使用语言目录。
     */
    private Path resolveListPath(String relativePath) {
        Path baseDir = resolveBaseDir();
        Path dirPath;
        if (relativePath == null || relativePath.isEmpty()) {
            dirPath = baseDir.resolve(getScriptLangName());
        } else {
            dirPath = baseDir.resolve(getScriptLangName()).resolve(relativePath);
        }
        dirPath = dirPath.normalize();
        if (Files.exists(dirPath)) {
            validatePath(dirPath, baseDir);
            return dirPath;
        }
        // 语言目录不存在时，尝试基础目录
        Path fallbackDir = baseDir.resolve(SCRIPTS_NAME);
        if (relativePath != null && !relativePath.isEmpty()) {
            fallbackDir = fallbackDir.resolve(relativePath);
        }
        fallbackDir = fallbackDir.normalize();
        if (Files.exists(fallbackDir)) {
            validatePath(fallbackDir, baseDir);
            return fallbackDir;
        }
        validatePath(dirPath, baseDir);
        return dirPath;
    }

    /**
     * 路径安全校验。
     */
    private void validatePath(Path resolved, Path baseDir) {
        Path normalized = resolved.toAbsolutePath().normalize();
        Path baseNormalized = baseDir.toAbsolutePath().normalize();
        if (!normalized.startsWith(baseNormalized)) {
            throw new BizException("检测到路径逃逸尝试");
        }
        String pathStr = normalized.toString();
        if (!pathStr.contains("scripts")) {
            throw new BizException("只允许访问scripts目录");
        }
    }

    // ==================== 公共方法 ====================

    /**
     * 获取文件树（指定路径下的子节点）。
     */
    public List<ScriptTreeNodeDTO> tree(String relativePath) {
        Path dirPath = resolveListPath(relativePath);
        File dir = dirPath.toFile();
        if (!dir.isDirectory()) {
            return Collections.emptyList();
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return Collections.emptyList();
        }

        // 排序：目录在前，文件在后；各自按名称排序
        Arrays.sort(files, (a, b) -> {
            if (a.isDirectory() && !b.isDirectory()) return -1;
            if (!a.isDirectory() && b.isDirectory()) return 1;
            return a.getName().compareToIgnoreCase(b.getName());
        });

        String prefix = (relativePath == null || relativePath.isEmpty()) ? "" : relativePath + "/";
        List<ScriptTreeNodeDTO> nodes = new ArrayList<>();
        for (File f : files) {
            String name = f.getName();
            // 跳过隐藏文件和.class/.java文件
            if (name.startsWith(".")) continue;
            if (f.isFile() && !name.endsWith(".js") && !name.endsWith(".sql") && !name.endsWith(".txt")) {
                continue;
            }
            String key = prefix + name;
            if (f.isDirectory()) {
                nodes.add(ScriptTreeNodeDTO.builder()
                        .title(name)
                        .key(key)
                        .children(new ArrayList<>())
                        .leaf(false)
                        .type("directory")
                        .build());
            } else {
                nodes.add(ScriptTreeNodeDTO.builder()
                        .title(name)
                        .key(key)
                        .children(null)
                        .leaf(true)
                        .type("file")
                        .build());
            }
        }
        return nodes;
    }

    /**
     * 读取文件内容。
     */
    public String readFile(String relativePath) {
        Path filePath = resolveReadPath(relativePath);
        if (!Files.exists(filePath)) {
            throw new BizException("文件不存在: " + relativePath);
        }
        try {
            return Files.readString(filePath, StandardCharsets.UTF_8);
        } catch (MalformedInputException e) {
            log.warn("文件 {} 非UTF-8编码，尝试ISO-8859-1", relativePath);
            try {
                return Files.readString(filePath, StandardCharsets.ISO_8859_1);
            } catch (IOException ex) {
                throw new BizException("读取文件异常: " + ex.getMessage());
            }
        } catch (IOException e) {
            throw new BizException("读取文件异常: " + e.getMessage());
        }
    }

    /**
     * 写入文件内容。覆盖模式：写入覆盖目录或语言目录。
     */
    public void writeFile(String relativePath, String content) {
        Path filePath = resolveWritePath(relativePath);
        try {
            Files.createDirectories(filePath.getParent());
            Files.writeString(filePath, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("写入文件失败: {}", filePath, e);
            throw new BizException("写入文件异常: " + e.getMessage());
        }
    }

    /**
     * 创建文件或目录。
     */
    public void createFile(String relativePath, boolean isDirectory) {
        Path targetPath = resolveWritePath(relativePath);
        if (Files.exists(targetPath)) {
            throw new BizException("文件或目录已存在: " + relativePath);
        }
        try {
            Files.createDirectories(targetPath.getParent());
            if (isDirectory) {
                Files.createDirectories(targetPath);
            } else {
                // 只有文件才限制扩展名
                String name = targetPath.getFileName().toString();
                if (!name.contains(".")) {
                    throw new BizException("文件名必须包含扩展名（如 .js）");
                }
                Files.createFile(targetPath);
            }
        } catch (IOException e) {
            log.error("创建失败: {}", targetPath, e);
            throw new BizException("创建异常: " + e.getMessage());
        }
    }

    /**
     * 删除文件或空目录。
     */
    public void deleteFile(String relativePath) {
        Path targetPath = resolveWritePath(relativePath);
        if (!Files.exists(targetPath)) {
            throw new BizException("文件或目录不存在: " + relativePath);
        }
        if (Files.isDirectory(targetPath)) {
            try {
                String[] contents = targetPath.toFile().list();
                if (contents != null && contents.length > 0) {
                    throw new BizException("目录非空，无法删除");
                }
                Files.delete(targetPath);
            } catch (IOException e) {
                throw new BizException("删除异常: " + e.getMessage());
            }
        } else {
            try {
                Files.delete(targetPath);
            } catch (IOException e) {
                throw new BizException("删除异常: " + e.getMessage());
            }
        }
    }

    /**
     * 重命名文件或目录。
     */
    public void renameFile(String oldRelativePath, String newRelativePath) {
        Path oldPath = resolveWritePath(oldRelativePath);
        Path newPath = resolveWritePath(newRelativePath);
        if (!Files.exists(oldPath)) {
            throw new BizException("源文件不存在: " + oldRelativePath);
        }
        if (Files.exists(newPath)) {
            throw new BizException("目标文件已存在: " + newRelativePath);
        }
        try {
            Files.createDirectories(newPath.getParent());
            Files.move(oldPath, newPath, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            log.error("重命名失败: {} -> {}", oldPath, newPath, e);
            throw new BizException("重命名异常: " + e.getMessage());
        }
    }

    /**
     * 检查覆盖目录是否激活。
     */
    public boolean isOverrideActive() {
        String overridePath = serviceProperty.getScriptOverridePath();
        return overridePath != null && !overridePath.isBlank();
    }

    /**
     * 获取覆盖目录的绝对路径。
     */
    public String getOverridePath() {
        if (!isOverrideActive()) return null;
        return resolveBaseDir().toString();
    }
}