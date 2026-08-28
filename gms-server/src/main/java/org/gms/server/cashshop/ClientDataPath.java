package org.gms.server.cashshop;

import org.gms.config.GameConfig;
import org.gms.dao.entity.GameConfigDO;
import org.gms.manager.ServerManager;
import org.gms.service.ConfigService;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 统一「客户端 Data 根目录」解析（窗口商城校验、技改客户端同步等共用）。
 * <p>
 * 优先级：
 * <ol>
 *   <li>{@code -Dgms.client.data=}（推荐）</li>
 *   <li>{@code -Dcashshop.client-data=}（兼容旧 JVM 参数）</li>
 *   <li>game_config {@code window_cashshop_client_data_path}</li>
 * </ol>
 * 路径不写死；换 live 客户端时改配置或系统属性即可。套装/装备成长 WZ 初始化走服务端
 * {@code ContentRoot}/wz，不读本路径。
 */
public final class ClientDataPath {
    /** DB / 管理端沿用既有 code，语义已扩展为全局客户端 Data 根。 */
    public static final String CONFIG_CODE = "window_cashshop_client_data_path";
    /** 推荐 JVM 属性。 */
    public static final String SYS_PROP = "gms.client.data";
    /** 兼容旧窗口商城 JVM 属性。 */
    public static final String SYS_PROP_LEGACY = "cashshop.client-data";

    private ClientDataPath() {
    }

    public static Optional<Path> resolve() {
        for (String key : new String[]{SYS_PROP, SYS_PROP_LEGACY}) {
            final String prop = System.getProperty(key);
            if (StringUtils.hasText(prop)) {
                return Optional.of(Path.of(prop.trim()).toAbsolutePath().normalize());
            }
        }
        try {
            final String cfg = GameConfig.getServerString(CONFIG_CODE);
            if (StringUtils.hasText(cfg)) {
                return Optional.of(Path.of(cfg.trim()).toAbsolutePath().normalize());
            }
        } catch (Exception ignored) {
            // GameConfig 未就绪时视为未配置
        }
        return Optional.empty();
    }

    public static String configuredRaw() {
        for (String key : new String[]{SYS_PROP, SYS_PROP_LEGACY}) {
            final String prop = System.getProperty(key);
            if (StringUtils.hasText(prop)) {
                return prop.trim() + " (JVM -D" + key + ")";
            }
        }
        try {
            final String cfg = GameConfig.getServerString(CONFIG_CODE);
            return cfg == null ? "" : cfg.trim();
        } catch (Exception e) {
            return "";
        }
    }

    public static ValidationResult validate(Path path) {
        if (path == null) {
            return ValidationResult.fail("路径为空");
        }
        if (!Files.isDirectory(path)) {
            return ValidationResult.fail("不是有效目录: " + path);
        }
        // 常见 live 客户端：…/BeiDou-Client/Data 或 …/Data 下有 Character/Item/UI
        final boolean looksLikeData = Files.isDirectory(path.resolve("Character"))
                || Files.isDirectory(path.resolve("Item"))
                || Files.isDirectory(path.resolve("UI"))
                || path.getFileName() != null && "Data".equalsIgnoreCase(path.getFileName().toString());
        if (!looksLikeData) {
            return ValidationResult.warn(path, "目录存在，但未检测到 Character/Item/UI，请确认是否为客户端 Data 根");
        }
        return ValidationResult.ok(path);
    }

    public static ValidationResult validateConfigured() {
        return resolve().map(ClientDataPath::validate)
                .orElseGet(() -> ValidationResult.skip("未配置客户端 Data 路径，将跳过客户端资源校验"));
    }

    /**
     * 写入 game_config（热更新内存）。系统属性优先时仅改 DB，运行时仍以 -D 为准。
     * 允许空串（清空=跳过客户端校验）。
     */
    public static void saveToGameConfig(String absolutePath) {
        final var ctx = ServerManager.getApplicationContext();
        final ConfigService configService = ctx.getBean(ConfigService.class);
        final org.gms.dao.mapper.GameConfigMapper mapper = ctx.getBean(org.gms.dao.mapper.GameConfigMapper.class);
        final List<GameConfigDO> list = configService.loadGameConfigs().stream()
                .filter(c -> CONFIG_CODE.equalsIgnoreCase(c.getConfigCode()))
                .toList();
        final String value = absolutePath == null ? "" : absolutePath.trim();
        if (list.isEmpty()) {
            final GameConfigDO neu = GameConfigDO.builder()
                    .configType("server")
                    .configSubType("Client")
                    .configClazz("java.lang.String")
                    .configCode(CONFIG_CODE)
                    .configValue(value)
                    .configDesc("客户端 Data 根目录（窗口商城/技改等共用；空=跳过）")
                    .updateTime(new Date())
                    .build();
            mapper.insertSelective(neu);
            GameConfig.add(neu);
            return;
        }
        final GameConfigDO existing = list.getFirst();
        mapper.update(GameConfigDO.builder()
                .id(existing.getId())
                .configValue(value)
                .updateTime(new Date())
                .build());
        existing.setConfigValue(value);
        GameConfig.update(existing);
    }

    public record ValidationResult(boolean ok, boolean skipped, boolean warning, String path, String message) {
        static ValidationResult ok(Path path) {
            return new ValidationResult(true, false, false, path.toString(), "OK");
        }

        static ValidationResult warn(Path path, String msg) {
            return new ValidationResult(true, false, true, path.toString(), msg);
        }

        static ValidationResult fail(String msg) {
            return new ValidationResult(false, false, false, null, msg);
        }

        public static ValidationResult skip(String msg) {
            return new ValidationResult(true, true, false, null, msg);
        }
    }
}
