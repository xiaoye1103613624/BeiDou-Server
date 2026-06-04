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
 * 【业务服务】FileTreeService：文件树服务类，提供服务器端文件系统的安全访问接口。
 * 
 * <p>支持文件目录树的浏览、文件读取和写入操作。通过树形key机制安全地访问文件，
 * 防止路径遍历攻击。仅允许访问白名单目录（scripts、scripts-zh-CN、wz、wz-zh-CN）。</p>
 * 
 * <p>树形key格式：使用"-"分隔的数字序列，每个数字代表目录层级中的索引位置。
 * 例如 "0-2-1" 表示根目录第0个子项的第2个子项的第1个子项。</p>
 */
@Slf4j
@Service
public class FileTreeService {

    /** 文件树基础目录，使用系统属性user.dir */
    private static final String FILE_TREE_BASE_DIR = System.getProperty("user.dir");
    /** 文件树基础目录路径对象 */
    private static final Path FILE_TREE_BASE_DIR_PATH = Path.of(FILE_TREE_BASE_DIR);
    /** 树形key的层级分隔符 */
    private static final String FILE_TREE_KEY_DELIMITER = "-";
    /** 允许访问的目录白名单 */
    private static final Set<String> FILE_TREE_LIMITED_PATTERNS = new HashSet<>();
    /** 是否启用严格路径模式（防止路径逃逸攻击） */
    private static final boolean FILE_TREE_PATH_STRICT_MODE = true;

    /** 静态初始化：配置允许访问的目录白名单 */
    static {
        FILE_TREE_LIMITED_PATTERNS.add("scripts");
        FILE_TREE_LIMITED_PATTERNS.add("scripts-zh-CN");
        FILE_TREE_LIMITED_PATTERNS.add("wz");
        FILE_TREE_LIMITED_PATTERNS.add("wz-zh-CN");
    }

    /**
     * 读取文件内容。
     * 
     * <p>优先使用UTF-8编码读取，若遇到编码错误则尝试ISO-8859-1编码。</p>
     * 
     * @param currentKey 文件的树形key
     * @param filename 文件名（用于验证）
     * @return 文件内容字符串
     * @throws BizException 当文件不存在、目录变动或读取失败时抛出
     */
    public String readFile(String currentKey, String filename) {
        File file = resolveByTreeKey(currentKey);
        // 验证文件名是否匹配，防止目录遍历攻击
        if (!filename.equals(file.getName())) throw new BizException("文件目录发生变动，请重新读取");
        try {
            return Files.readString(file.toPath(), UTF_8);
        } catch (MalformedInputException e) {
            // UTF-8编码失败，尝试ISO-8859-1
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
     * 写入文件内容。
     * 
     * <p>使用UTF-8编码写入文件。</p>
     * 
     * @param currentKey 文件的树形key
     * @param filename 文件名（用于验证）
     * @param content 要写入的内容
     * @throws BizException 当文件不存在、目录变动或写入失败时抛出
     */
    public void writeFile(String currentKey, String filename, String content) {
        File file = resolveByTreeKey(currentKey);
        // 验证文件名是否匹配，防止目录遍历攻击
        if (!filename.equals(file.getName())) throw new BizException("文件目录发生变动，请重新写入");
        try {
            Files.writeString(file.toPath(), content, UTF_8);
        } catch (IOException e) {
            log.error("io error", e);
            throw new BizException("写入文件异常");
        }
    }

    /**
     * 获取指定目录的文件树节点列表。
     * 
     * <p>根据树形key获取目录下的所有子文件/文件夹，生成对应的节点列表。
     * 仅返回白名单目录内的文件。</p>
     * 
     * @param currentKey 目录的树形key，为空表示根目录
     * @return 文件树节点列表
     * @throws BizException 当路径不是目录时抛出
     */
    public List<FileTreeNodeDTO> tree(String currentKey) {
        // 入参为空表示在根目录
        boolean root = !StringUtils.hasText(currentKey);
        File current = root ? new File(FILE_TREE_BASE_DIR) : resolveByTreeKey(currentKey);

        if (!current.isDirectory()) throw new BizException("请输入文件夹");

        // 检查当前目录是否在白名单内
        boolean parentIsMatch = matchAnyLimitPattern(current.toPath());

        File[] listFiles = current.listFiles();
        if (listFiles == null) return Collections.emptyList();

        List<FileTreeNodeDTO> nodes = new ArrayList<>();
        for (int i = 0; i < listFiles.length; i++) {
            File currentSubFile = listFiles[i];
            // 父目录不在白名单内时，子项必须在白名单内才显示
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
     * 根据树形key解析对应的文件/目录。
     * 
     * <p>树形key格式：使用"-"分隔的数字序列，每个数字代表目录层级中的索引位置。
     * 启用严格模式时会进行路径逃逸检测，确保访问的文件在允许的目录范围内。</p>
     * 
     * @param currentKey 树形key
     * @return 对应的File对象
     * @throws BizException 当key无效、文件不存在或路径逃逸时抛出
     */
    public File resolveByTreeKey(String currentKey) {
        File base = FILE_TREE_BASE_DIR_PATH.toFile();
        File current = base;
        String[] keyArray = currentKey.split(FILE_TREE_KEY_DELIMITER);
        try {
            for (String keyStr : keyArray) {
                int key = Integer.parseInt(keyStr);
                current = Objects.requireNonNull(current.listFiles())[key];
            }
        } catch (NullPointerException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            log.error("file not exists", e);
            throw new BizException("文件/文件夹不存在");
        }

        // 严格模式下进行路径安全检查
        if (FILE_TREE_PATH_STRICT_MODE) {
            Path userPath = current.toPath().toAbsolutePath().normalize();
            // 检查路径是否在基础目录内
            if (!userPath.startsWith(FILE_TREE_BASE_DIR_PATH)) {
                log.error("file escape base dir : {}", userPath);
                throw new BizException("检测到路径逃逸尝试");
            }
            // 检查路径是否在白名单目录内
            if (!matchAnyLimitPattern(userPath)) {
                log.error("file escape limit pattern : {}", userPath);
                throw new BizException("检测到路径逃逸尝试");
            }
        }
        return current;
    }

    /**
     * 检查路径是否匹配任何白名单模式。
     * 
     * @param path 待检查的路径
     * @return true表示匹配白名单，false表示不匹配
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean matchAnyLimitPattern(Path path) {
        if (FILE_TREE_PATH_STRICT_MODE) {
            return FILE_TREE_LIMITED_PATTERNS.stream()
                    .anyMatch(it -> path.startsWith(FILE_TREE_BASE_DIR_PATH.resolve(it)));
        }
        return true;
    }

}