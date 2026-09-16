package org.gms.service;

import lombok.RequiredArgsConstructor;
import org.gms.config.GameConfig;
import org.gms.dao.entity.GameConfigDO;
import org.gms.dao.mapper.GameConfigMapper;
import org.gms.exception.BizException;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 全局客户端 Data 根目录：解析 / 校验 / 持久化 / 目录浏览。
 * <p>
 * 窗口商城同步、资源校验等客户端相关能力共用此服务；不依赖具体业务 Controller。
 */
@Service
@RequiredArgsConstructor
public class ClientPathService {

    private final ConfigService configService;
    private final GameConfigMapper gameConfigMapper;

    public Map<String, Object> getInfo() {
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("configured", ClientDataPath.configuredRaw());
        m.put("resolved", ClientDataPath.resolve().map(Path::toString).orElse(""));
        m.put("jvmProperty", ClientDataPath.SYS_PROP);
        m.put("configCode", ClientDataPath.CONFIG_CODE);
        final var v = ClientDataPath.validateConfigured();
        m.put("ok", v.ok());
        m.put("skipped", v.skipped());
        m.put("warning", v.warning());
        m.put("message", v.message());
        return m;
    }

    /**
     * 写入 game_config（热更新内存）。系统属性优先时仅改 DB，运行时仍以 -D 为准。
     * 空串=清空并跳过客户端校验。
     */
    public Map<String, Object> setPath(String absolutePath) {
        if (StringUtils.hasText(absolutePath)) {
            final Path p = Path.of(absolutePath.trim()).toAbsolutePath().normalize();
            final var v = ClientDataPath.validate(p);
            if (!v.ok()) {
                throw new BizException(v.message());
            }
            saveToGameConfig(p.toString());
        } else {
            saveToGameConfig("");
        }
        return getInfo();
    }

    public Map<String, Object> validate(String absolutePath) {
        final Path p = StringUtils.hasText(absolutePath)
                ? Path.of(absolutePath.trim()).toAbsolutePath().normalize()
                : ClientDataPath.resolve().orElse(null);
        final var v = p == null
                ? ClientDataPath.ValidationResult.skip(I18nUtil.getMessage("ClientDataPath.skip.empty"))
                : ClientDataPath.validate(p);
        final Map<String, Object> m = new LinkedHashMap<>();
        m.put("ok", v.ok());
        m.put("skipped", v.skipped());
        m.put("warning", v.warning());
        m.put("path", v.path());
        m.put("message", v.message());
        return m;
    }

    public List<Map<String, Object>> listDirectories(String absolutePath) {
        RequireUtil.requireNotEmpty(absolutePath, "path");
        final Path root = Path.of(absolutePath.trim()).toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            throw new BizException(I18nUtil.getMessage("ClientDataPath.fail.notDirectory", root.toString()));
        }
        try (var stream = Files.list(root)) {
            return stream.filter(Files::isDirectory)
                    .sorted()
                    .map(p -> {
                        final Map<String, Object> n = new LinkedHashMap<>();
                        n.put("name", p.getFileName() != null ? p.getFileName().toString() : p.toString());
                        n.put("path", p.toString());
                        return n;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new BizException(I18nUtil.getMessage("ClientDataPath.fail.listDirectories", e.getMessage()));
        }
    }

    private void saveToGameConfig(String absolutePath) {
        final List<GameConfigDO> list = configService.loadGameConfigs().stream()
                .filter(c -> ClientDataPath.CONFIG_CODE.equalsIgnoreCase(c.getConfigCode()))
                .toList();
        final String value = absolutePath == null ? "" : absolutePath.trim();
        final String desc = I18nUtil.getMessage("ClientDataPath.config.desc");
        if (list.isEmpty()) {
            final GameConfigDO neu = GameConfigDO.builder()
                    .configType("server")
                    .configSubType(ClientDataPath.CONFIG_SUB_TYPE)
                    .configClazz("java.lang.String")
                    .configCode(ClientDataPath.CONFIG_CODE)
                    .configValue(value)
                    .configDesc(desc)
                    .updateTime(new Date())
                    .build();
            gameConfigMapper.insertSelective(neu);
            GameConfig.add(neu);
            return;
        }
        final GameConfigDO existing = list.getFirst();
        gameConfigMapper.update(GameConfigDO.builder()
                .id(existing.getId())
                .configSubType(ClientDataPath.CONFIG_SUB_TYPE)
                .configValue(value)
                .configDesc(desc)
                .updateTime(new Date())
                .build());
        existing.setConfigSubType(ClientDataPath.CONFIG_SUB_TYPE);
        existing.setConfigValue(value);
        existing.setConfigDesc(desc);
        GameConfig.update(existing);
    }
}
