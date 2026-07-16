package org.gms.provider;

import org.gms.provider.wz.WZDirectoryEntry;
import org.gms.provider.wz.WZFileEntry;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 语言目录优先、原始 WZ 回退。
 * <p>
 * 导航目录必须合并两边文件树：否则仅存在于 {@code wz-zh-CN} 的资源
 * （例如额外 Character Cap）无法被 {@code getRoot()} 枚举到，
 * {@link org.gms.server.ItemInformationProvider} 会误判为「缺少装备数据」。
 */
public class LocalizedDataProvider implements DataProvider {
    private final DataProvider localized;
    private final DataProvider fallback;
    private final DataDirectoryEntry mergedRoot;

    public LocalizedDataProvider(DataProvider localized, DataProvider fallback) {
        this.localized = localized;
        this.fallback = fallback;
        this.mergedRoot = mergeDirectory(localized.getRoot(), fallback.getRoot(), null);
    }

    @Override
    public Data getData(String path) {
        Data data = localized.getData(path);
        // 语言目录里没有该 XML 时，使用原始 WZ，避免为了少量翻译复制整包资源。
        return data != null ? data : fallback.getData(path);
    }

    @Override
    public DataDirectoryEntry getRoot() {
        return mergedRoot;
    }

    /**
     * 合并目录树：同名子目录递归合并，同名文件去重（保留任一侧即可，加载时仍走 getData 优先语言包）。
     */
    private static DataDirectoryEntry mergeDirectory(
            DataDirectoryEntry primary,
            DataDirectoryEntry secondary,
            DataEntity parent
    ) {
        String name = primary != null ? primary.getName() : (secondary != null ? secondary.getName() : null);
        WZDirectoryEntry merged = new WZDirectoryEntry(name, 0, 0, parent);

        Map<String, DataDirectoryEntry> primaryDirs = indexDirs(primary);
        Map<String, DataDirectoryEntry> secondaryDirs = indexDirs(secondary);
        Set<String> dirNames = new LinkedHashSet<>();
        dirNames.addAll(primaryDirs.keySet());
        dirNames.addAll(secondaryDirs.keySet());
        for (String dirName : dirNames) {
            merged.addDirectory(mergeDirectory(primaryDirs.get(dirName), secondaryDirs.get(dirName), merged));
        }

        Map<String, DataFileEntry> primaryFiles = indexFiles(primary);
        Map<String, DataFileEntry> secondaryFiles = indexFiles(secondary);
        Set<String> fileNames = new LinkedHashSet<>();
        fileNames.addAll(primaryFiles.keySet());
        fileNames.addAll(secondaryFiles.keySet());
        for (String fileName : fileNames) {
            DataFileEntry src = primaryFiles.containsKey(fileName)
                    ? primaryFiles.get(fileName)
                    : secondaryFiles.get(fileName);
            merged.addFile(new WZFileEntry(src.getName(), src.getSize(), src.getChecksum(), merged));
        }

        return merged;
    }

    private static Map<String, DataDirectoryEntry> indexDirs(DataDirectoryEntry dir) {
        Map<String, DataDirectoryEntry> map = new LinkedHashMap<>();
        if (dir == null) {
            return map;
        }
        for (DataDirectoryEntry child : dir.getSubdirectories()) {
            map.put(child.getName(), child);
        }
        return map;
    }

    private static Map<String, DataFileEntry> indexFiles(DataDirectoryEntry dir) {
        Map<String, DataFileEntry> map = new LinkedHashMap<>();
        if (dir == null) {
            return map;
        }
        for (DataFileEntry file : dir.getFiles()) {
            map.put(file.getName(), file);
        }
        return map;
    }
}
