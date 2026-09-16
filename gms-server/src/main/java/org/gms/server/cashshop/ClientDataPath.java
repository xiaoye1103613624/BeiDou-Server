package org.gms.server.cashshop;

import org.gms.config.GameConfig;
import org.gms.util.I18nUtil;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
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
 * 持久化与目录浏览见 {@link org.gms.service.ClientPathService}。套装/装备成长 WZ 初始化走服务端
 * {@code ContentRoot}/wz，不读本路径。
 */
public final class ClientDataPath {
    /** DB / 管理端沿用既有 code，语义为全局客户端 Data 根。 */
    public static final String CONFIG_CODE = "window_cashshop_client_data_path";
    /** game_config 子类，管理端 i18n：config.subType.Client */
    public static final String CONFIG_SUB_TYPE = "Client";
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
            return ValidationResult.fail(I18nUtil.getMessage("ClientDataPath.fail.empty"));
        }
        if (!Files.isDirectory(path)) {
            return ValidationResult.fail(I18nUtil.getMessage("ClientDataPath.fail.notDirectory", path.toString()));
        }
        // 常见 live 客户端：…/BeiDou-Client/Data 或 …/Data 下有 Character/Item/UI
        final boolean looksLikeData = Files.isDirectory(path.resolve("Character"))
                || Files.isDirectory(path.resolve("Item"))
                || Files.isDirectory(path.resolve("UI"))
                || path.getFileName() != null && "Data".equalsIgnoreCase(path.getFileName().toString());
        if (!looksLikeData) {
            return ValidationResult.warn(path, I18nUtil.getMessage("ClientDataPath.warn.noDataMarkers"));
        }
        return ValidationResult.ok(path);
    }

    public static ValidationResult validateConfigured() {
        return resolve().map(ClientDataPath::validate)
                .orElseGet(() -> ValidationResult.skip(I18nUtil.getMessage("ClientDataPath.skip.unconfigured")));
    }

    /**
     * @deprecated 请用 {@link org.gms.service.ClientPathService#setPath(String)}；保留仅兼容旧调用点。
     */
    @Deprecated
    public static void saveToGameConfig(String absolutePath) {
        final var ctx = org.gms.manager.ServerManager.getApplicationContext();
        ctx.getBean(org.gms.service.ClientPathService.class).setPath(absolutePath);
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
