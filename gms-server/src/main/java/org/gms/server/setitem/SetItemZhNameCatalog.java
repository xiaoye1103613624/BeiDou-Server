package org.gms.server.setitem;

import lombok.extern.slf4j.Slf4j;
import org.gms.util.I18nUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 客户端 Data/Etc/SetItemInfo.img 导出的中文套装名（setId → 中文名）。
 * 资源：classpath:setitem/set_item_names_zh.tsv
 */
@Slf4j
public final class SetItemZhNameCatalog {
    private static final String RESOURCE = "/setitem/set_item_names_zh.tsv";
    private static volatile Map<Integer, String> names;

    private SetItemZhNameCatalog() {
    }

    public static Map<Integer, String> getAll() {
        Map<Integer, String> local = names;
        if (local == null) {
            synchronized (SetItemZhNameCatalog.class) {
                local = names;
                if (local == null) {
                    local = load();
                    names = local;
                }
            }
        }
        return local;
    }

    public static String get(int setId) {
        return getAll().get(setId);
    }

    /** 测试或热更新资源后可清空缓存。 */
    public static void clearCache() {
        names = null;
    }

    private static Map<Integer, String> load() {
        Map<Integer, String> map = new LinkedHashMap<>();
        try (InputStream in = SetItemZhNameCatalog.class.getResourceAsStream(RESOURCE)) {
            if (in == null) {
                log.warn(I18nUtil.getLogMessage("SetItemZhNameCatalog.warn.missing", RESOURCE));
                return Collections.emptyMap();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    int tab = line.indexOf('\t');
                    if (tab <= 0 || tab >= line.length() - 1) {
                        continue;
                    }
                    try {
                        int setId = Integer.parseInt(line.substring(0, tab).trim());
                        String name = line.substring(tab + 1).trim();
                        if (setId > 0 && !name.isEmpty()) {
                            map.put(setId, name);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            log.info(I18nUtil.getLogMessage("SetItemZhNameCatalog.info.loaded", map.size()));
        } catch (Exception e) {
            log.error(I18nUtil.getLogMessage("SetItemZhNameCatalog.error.load", RESOURCE), e);
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(map);
    }
}
