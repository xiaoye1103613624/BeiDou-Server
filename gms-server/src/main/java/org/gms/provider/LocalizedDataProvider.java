package org.gms.provider;

import org.gms.provider.wz.WZDirectoryEntry;
import org.gms.provider.wz.WZFileEntry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Layers a language WZ (e.g. {@code wz-zh-CN}) over the English base {@code wz/}.
 * <p>
 * {@link #getData} prefers the language pack and falls back to English.
 * {@link #getRoot} merges both trees so files that exist only in the language pack
 * (common for custom equips) remain discoverable by root enumeration such as
 * {@code ItemInformationProvider#getItemData}.
 */
public class LocalizedDataProvider implements DataProvider {
    private final DataProvider localized;
    private final DataProvider fallback;
    private final DataDirectoryEntry mergedRoot;

    public LocalizedDataProvider(DataProvider localized, DataProvider fallback) {
        this.localized = localized;
        this.fallback = fallback;
        this.mergedRoot = mergeDirectory(fallback.getRoot(), localized.getRoot(), null);
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
     * Build a navigation tree that contains every file/dir from the English base
     * plus language-only entries. Shared subdirectory names are merged recursively.
     */
    private static WZDirectoryEntry mergeDirectory(DataDirectoryEntry base, DataDirectoryEntry overlay, DataEntity parent) {
        String name = base != null ? base.getName() : (overlay != null ? overlay.getName() : null);
        WZDirectoryEntry merged = new WZDirectoryEntry(name, 0, 0, parent);

        Map<String, DataDirectoryEntry> overlayDirs = indexDirs(overlay);
        Set<String> seenFiles = new HashSet<>();
        Set<String> seenDirs = new HashSet<>();

        if (base != null) {
            for (DataFileEntry file : base.getFiles()) {
                merged.addFile(copyFile(file, merged));
                seenFiles.add(file.getName());
            }
            for (DataDirectoryEntry dir : base.getSubdirectories()) {
                DataDirectoryEntry overlayChild = overlayDirs.get(dir.getName());
                merged.addDirectory(mergeDirectory(dir, overlayChild, merged));
                seenDirs.add(dir.getName());
            }
        }

        if (overlay != null) {
            for (DataFileEntry file : overlay.getFiles()) {
                if (seenFiles.add(file.getName())) {
                    merged.addFile(copyFile(file, merged));
                }
            }
            for (DataDirectoryEntry dir : overlay.getSubdirectories()) {
                if (seenDirs.add(dir.getName())) {
                    merged.addDirectory(mergeDirectory(null, dir, merged));
                }
            }
        }

        return merged;
    }

    private static Map<String, DataDirectoryEntry> indexDirs(DataDirectoryEntry dir) {
        Map<String, DataDirectoryEntry> map = new HashMap<>();
        if (dir == null) {
            return map;
        }
        for (DataDirectoryEntry child : dir.getSubdirectories()) {
            map.put(child.getName(), child);
        }
        return map;
    }

    private static DataFileEntry copyFile(DataFileEntry file, DataEntity parent) {
        return new WZFileEntry(file.getName(), file.getSize(), file.getChecksum(), parent);
    }
}
