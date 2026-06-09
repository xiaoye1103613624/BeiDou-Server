package org.gms.service;

import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.model.dto.FileTreeNodeDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 文件树服务类
 * 管理服务器脚本文件的目录树结构，提供文件的读取、写入和目录浏览功能
 * 限制只能访问scripts、wz等指定目录
 */
@Slf4j
@Service
public class FileTreeService {

    /** 文件树根目录 */
    private static final String FILE_TREE_BASE_DIR = System.getProperty("user.dir");
    /** 文件树根目录路径 */
    private static final Path FILE_TREE_BASE_DIR_PATH = Path.of(FILE_TREE_BASE_DIR);
    /** 文件树Key分隔符 */
    private static final String FILE_TREE_KEY_DELIMITER = "-";
    /** 文件树限制访问的目录模式 */
    private static final Set<String> FILE_TREE_LIMITED_PATTERNS = new HashSet<>();
    /** 路径严格模式 */
    private static final boolean FILE_TREE_PATH_STRICT_MODE = true;

    static {
        FILE_TREE_LIMITED_PATTERNS.add("scripts");
        FILE_TREE_LIMITED_PATTERNS.add("scripts-zh-CN");
        FILE_TREE_LIMITED_PATTERNS.add("wz");
        FILE_TREE_LIMITED_PATTERNS.add("wz-zh-CN");
    }

    /**
     * 读取文件内容
     * 优先使用UTF-8编码，失败时回退到ISO-8859-1
     *
     * @param currentKey 当前目录的树形Key
     * @param filename   文件名称，用于校验目录是否发生变动
     * @return 文件内容字符串
     */
    public String readFile(String currentKey, String filename) {
        File file = resolveByTreeKey(currentKey);
        if (!filename.equals(file.getName())) throw new BizException("文件目录发生变动，请重新读取");
        try {
            return Files.readString(file.toPath(), UTF_8);
        } catch (MalformedInputException e) {
            // 文件非 UTF-8 编码时回退到 ISO-8859-1 尝试读取
            log.error("file {} is not using utf8", filename);
            try {
                return Files.readString(file.toPath(), ISO_8859_1);
            } catch (IOException ex) {
                log.error("io error", ex);
                throw new BizException("读取文件异常");
            }
        } catch (IOException e) {
            log.error("io error", e);
            throw new BizException("读取文件异常");
        }
    }

    /**
     * 写入文件内容
     * 使用UTF-8编码写入
     *
     * @param currentKey 当前目录的树形Key
     * @param filename   文件名称，用于校验目录是否发生变动
     * @param content    要写入的内容
     */
    public void writeFile(String currentKey, String filename, String content) {
        File file = resolveByTreeKey(currentKey);
        if (!filename.equals(file.getName())) throw new BizException("文件目录发生变动，请重新写入");
        try {
            Files.writeString(file.toPath(), content, UTF_8);
        } catch (IOException e) {
            log.error("io error", e);
            throw new BizException("读取文件异常");
        }
    }

    /**
     * 获取目录树节点列表
     * 返回指定目录下的子文件和子目录，仅展示受限目录范围内的内容
     *
     * @param currentKey 当前目录的树形Key，为空表示在根目录浏览
     * @return 子节点列表
     */
    public List<FileTreeNodeDTO> tree(String currentKey) {
        // key 为空表示在根目录浏览
        boolean root = !StringUtils.hasText(currentKey);
        File current = root ? new File(FILE_TREE_BASE_DIR) : resolveByTreeKey(currentKey);

        if (!current.isDirectory()) throw new BizException("请输入文件夹");

        boolean parentIsMatch = matchAnyLimitPattern(current.toPath());

        File[] listFiles = current.listFiles();
        if (listFiles == null) return Collections.emptyList();

        // 构建子节点列表：非限制目录的直接子节点需要再次检查是否匹配限制模式
        // 子节点 key = 父节点 key + "-" + 索引，方便递归解析
        List<FileTreeNodeDTO> nodes = new ArrayList<>();
        for (int i = 0; i < listFiles.length; i++) {
            File currentSubFile = listFiles[i];
            if (!parentIsMatch && !matchAnyLimitPattern(currentSubFile.toPath())) {
                continue;
            }
            String childKey = String.valueOf(i);
            String key = root ? childKey : String.join(FILE_TREE_KEY_DELIMITER, currentKey, childKey);
            nodes.add(new FileTreeNodeDTO(currentSubFile, key));
        }
        return nodes;
    }

    /**
     * 根据树形Key解析为实际文件
     * Key由数字索引组成，用分隔符连接，表示从根目录逐层listFiles()[index]的路径
     *
     * @param currentKey 树形Key
     * @return 解析出的文件或目录
     */
    public File resolveByTreeKey(String currentKey) {
        @SuppressWarnings("UnnecessaryLocalVariable")
        File base = FILE_TREE_BASE_DIR_PATH.toFile();
        File current = base;
        String[] keyArray = currentKey.split(FILE_TREE_KEY_DELIMITER);
        // 按 key 路径逐层解析：数字索引 → listFiles()[index]，解析出目标文件
        try {
            for (String keyStr : keyArray) {
                int key = Integer.parseInt(keyStr);
                current = Objects.requireNonNull(current.listFiles())[key];
            }
        } catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            log.error("file not exists", e);
            throw new BizException("文件/文件夹不存在");
        }

        // 严格模式：检查路径逃逸和访问范围限制
        if (FILE_TREE_PATH_STRICT_MODE) {
            Path userPath = current.toPath().toAbsolutePath().normalize();
            // 防止通过 .. 等跳转到根目录之外
            if (!userPath.startsWith(FILE_TREE_BASE_DIR_PATH)) {
                log.error("file escape base dir : {}", userPath);
                throw new BizException("检测到路径逃逸尝试");
            }
            // 检查是否在允许访问的目录范围内
            if (!matchAnyLimitPattern(userPath)) {
                log.error("file escape limit pattern : {}", userPath);
                throw new BizException("检测到路径逃逸尝试");
            }
        }
        return current;
    }

    /**
     * 检查路径是否匹配任意一个限制目录模式
     * 非严格模式下始终返回true
     *
     * @param path 待检查的路径
     * @return 是否匹配限制目录
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean matchAnyLimitPattern(Path path) {
        if (FILE_TREE_PATH_STRICT_MODE) {
            return FILE_TREE_LIMITED_PATTERNS.stream().anyMatch(it -> path.startsWith(FILE_TREE_BASE_DIR_PATH.resolve(it)));
        }
        return true;
    }

}