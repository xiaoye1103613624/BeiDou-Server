package org.gms.service.quest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.GameConfig;
import org.gms.exception.BizException;
import org.gms.model.dto.QuestSyncClientReqDTO;
import org.gms.model.dto.QuestSyncClientRtnDTO;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 开发态：将服务端 Quest WZ XML 同步到客户端 Data/EN 的 Quest .img。
 * <p>
 * 门禁：{@code allow_quest_client_write}（默认 false）+ 已配置 {@link ClientDataPath}。
 * 有 {@code xml-img-patcher.exe} 则尝试调用；否则导出补丁清单到
 * {@code docs/features/quest-management/patches/}。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestClientSyncService {
    public static final String CONFIG_ALLOW_WRITE = "allow_quest_client_write";
    public static final String CONFIG_EN_PATH = "quest_client_en_path";
    public static final String CONFIG_PATCHER_PATH = "quest_xml_img_patcher_path";

    private static final String[] QUEST_XML_FILES = {
            QuestWzXmlStore.FILE_INFO,
            QuestWzXmlStore.FILE_CHECK,
            QuestWzXmlStore.FILE_ACT,
            QuestWzXmlStore.FILE_SAY
    };

    private final QuestWzXmlStore wzXmlStore;

    public Map<String, Object> status() {
        Map<String, Object> m = new LinkedHashMap<>();
        Optional<Path> data = ClientDataPath.resolve();
        Optional<Path> en = resolveEnPath(data.orElse(null));
        Optional<Path> patcher = findPatcher();
        m.put("clientDataConfigured", data.isPresent());
        m.put("clientDataPath", data.map(Path::toString).orElse(""));
        m.put("clientEnConfigured", en.isPresent());
        m.put("clientEnPath", en.map(Path::toString).orElse(""));
        m.put("allowWrite", isWriteAllowed());
        m.put("patcherAvailable", patcher.isPresent());
        m.put("patcherPath", patcher.map(Path::toString).orElse(""));
        m.put("configAllowWrite", CONFIG_ALLOW_WRITE);
        m.put("configEnPath", CONFIG_EN_PATH);
        m.put("configPatcherPath", CONFIG_PATCHER_PATH);
        return m;
    }

    public QuestSyncClientRtnDTO sync(QuestSyncClientReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        boolean dryRun = !Boolean.FALSE.equals(req.getDryRun()); // 默认 dry-run 更安全
        List<String> warnings = new ArrayList<>();
        List<String> files = new ArrayList<>();

        Optional<Path> dataOpt = ClientDataPath.resolve();
        Optional<Path> enOpt = resolveEnPath(dataOpt.orElse(null));
        Optional<Path> patcherOpt = findPatcher();

        if (dataOpt.isEmpty()) {
            if (!dryRun) {
                throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestClientSync.clientPath.required"));
            }
            warnings.add(I18nUtil.getMessage("QuestClientSync.warn.noClientPath"));
        }
        if (!isWriteAllowed() && !dryRun) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestClientSync.writeDisabled"));
        }

        Path exportDir = createExportDir();
        // 导出当前服务端 XML 快照（base + 语言包）供 patcher / 人工使用
        List<Path> exported = exportQuestXmlSnapshot(exportDir, warnings);
        for (Path p : exported) {
            files.add(p.toString());
        }

        writeManifest(exportDir, req.getQuestIds(), dataOpt.orElse(null), enOpt.orElse(null), patcherOpt.orElse(null));

        if (dryRun) {
            return QuestSyncClientRtnDTO.builder()
                    .dryRun(true)
                    .applied(false)
                    .patcherAvailable(patcherOpt.isPresent())
                    .clientDataPath(dataOpt.map(Path::toString).orElse(""))
                    .exportDir(exportDir.toString())
                    .files(files)
                    .warnings(warnings)
                    .message(I18nUtil.getMessage("QuestClientSync.dryRun.ok"))
                    .build();
        }

        // 实写
        Path dataRoot = dataOpt.get();
        boolean applied = false;
        if (patcherOpt.isPresent()) {
            applied = runPatcher(patcherOpt.get(), exportDir, dataRoot, enOpt.orElse(null), warnings);
        } else {
            warnings.add(I18nUtil.getMessage("QuestClientSync.warn.noPatcher"));
            // 降级：仅复制 XML 旁注到客户端旁的 patches 目录，不改 .img
            Path fallback = dataRoot.getParent() != null
                    ? dataRoot.getParent().resolve("quest-management-patches")
                    : dataRoot.resolve("quest-management-patches");
            try {
                Files.createDirectories(fallback);
                Path dest = fallback.resolve(exportDir.getFileName());
                copyDirectory(exportDir, dest);
                files.add(dest.toString());
                warnings.add(I18nUtil.getMessage("QuestClientSync.warn.fallbackExport", dest.toString()));
            } catch (IOException e) {
                throw BizException.illegalArgument(
                        I18nUtil.getExceptionMessage("QuestClientSync.exportFail", e.getMessage()));
            }
        }

        log.info(I18nUtil.getLogMessage("QuestClientSync.sync.info"), dryRun, applied, exportDir);
        return QuestSyncClientRtnDTO.builder()
                .dryRun(false)
                .applied(applied)
                .patcherAvailable(patcherOpt.isPresent())
                .clientDataPath(dataRoot.toString())
                .exportDir(exportDir.toString())
                .files(files)
                .warnings(warnings)
                .message(applied
                        ? I18nUtil.getMessage("QuestClientSync.apply.ok")
                        : I18nUtil.getMessage("QuestClientSync.apply.degraded"))
                .build();
    }

    private boolean isWriteAllowed() {
        return GameConfig.getServerBoolean(CONFIG_ALLOW_WRITE);
    }

    private Optional<Path> resolveEnPath(Path dataRoot) {
        try {
            String cfg = GameConfig.getServerString(CONFIG_EN_PATH);
            if (StringUtils.hasText(cfg)) {
                Path p = Path.of(cfg.trim()).toAbsolutePath().normalize();
                if (Files.isDirectory(p)) {
                    return Optional.of(p);
                }
            }
        } catch (Exception ignored) {
            // GameConfig 未就绪
        }
        if (dataRoot != null && dataRoot.getParent() != null) {
            Path sibling = dataRoot.getParent().resolve("EN");
            if (Files.isDirectory(sibling)) {
                return Optional.of(sibling.toAbsolutePath().normalize());
            }
        }
        return Optional.empty();
    }

    private Optional<Path> findPatcher() {
        List<Path> candidates = new ArrayList<>();
        try {
            String cfg = GameConfig.getServerString(CONFIG_PATCHER_PATH);
            if (StringUtils.hasText(cfg)) {
                candidates.add(Path.of(cfg.trim()));
            }
        } catch (Exception ignored) {
            // ignore
        }
        candidates.add(Path.of(".claude", "skills", "wz-patch-java", "xml-img-patcher.exe"));
        candidates.add(Path.of("tools", "xml-img-patcher.exe"));
        candidates.add(Path.of("gms-server", "tools", "xml-img-patcher.exe"));
        for (Path c : candidates) {
            Path abs = c.toAbsolutePath().normalize();
            if (Files.isRegularFile(abs)) {
                return Optional.of(abs);
            }
        }
        return Optional.empty();
    }

    private Path createExportDir() {
        String stamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        Path dir = Path.of("docs", "features", "quest-management", "patches", stamp);
        try {
            Files.createDirectories(dir);
            return dir.toAbsolutePath().normalize();
        } catch (IOException e) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("QuestClientSync.exportFail", e.getMessage()));
        }
    }

    private List<Path> exportQuestXmlSnapshot(Path exportDir, List<String> warnings) {
        List<Path> out = new ArrayList<>();
        Path baseOut = exportDir.resolve("server-wz");
        Path langOut = exportDir.resolve("server-wz-zh-CN");
        try {
            Files.createDirectories(baseOut);
            for (String name : QUEST_XML_FILES) {
                Path src = wzXmlStore.baseDir().resolve(name);
                if (Files.isRegularFile(src)) {
                    Path dest = baseOut.resolve(name);
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    out.add(dest);
                }
            }
            if (wzXmlStore.languageExists()) {
                Files.createDirectories(langOut);
                for (String name : QUEST_XML_FILES) {
                    Path src = wzXmlStore.languageDir().resolve(name);
                    if (Files.isRegularFile(src)) {
                        Path dest = langOut.resolve(name);
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                        out.add(dest);
                    }
                }
            } else {
                warnings.add(I18nUtil.getMessage("QuestClientSync.warn.noLangWz"));
            }
        } catch (IOException e) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("QuestClientSync.exportFail", e.getMessage()));
        }
        return out;
    }

    private void writeManifest(Path exportDir, List<Integer> questIds, Path data, Path en, Path patcher) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Quest client sync export\n");
        sb.append("time=").append(LocalDateTime.now()).append('\n');
        sb.append("questIds=").append(questIds == null ? "ALL" : questIds).append('\n');
        sb.append("clientData=").append(data == null ? "" : data).append('\n');
        sb.append("clientEn=").append(en == null ? "" : en).append('\n');
        sb.append("patcher=").append(patcher == null ? "" : patcher).append('\n');
        sb.append("\n# Mapping (CLAUDE.md)\n");
        sb.append("# server wz/          -> client EN/\n");
        sb.append("# server wz-zh-CN/    -> client Data/\n");
        sb.append("# Quest.wz/X.img.xml  -> Quest/X.img\n");
        sb.append("\n# Manual (when patcher missing):\n");
        sb.append("#   xml-img-patcher.exe batch --full-xml-dir=<export>/server-wz-zh-CN --target=<Data>/Quest\n");
        sb.append("#   xml-img-patcher.exe batch --full-xml-dir=<export>/server-wz --target=<EN>/Quest\n");
        try {
            Files.writeString(exportDir.resolve("MANIFEST.txt"), sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn(I18nUtil.getLogMessage("QuestClientSync.manifest.warn"), e.getMessage());
        }
    }

    private boolean runPatcher(Path patcher, Path exportDir, Path dataRoot, Path enRoot, List<String> warnings) {
        boolean ok = true;
        Path zhXml = exportDir.resolve("server-wz-zh-CN");
        Path enXml = exportDir.resolve("server-wz");
        if (Files.isDirectory(zhXml)) {
            ok &= invokeBatch(patcher, zhXml, dataRoot.resolve("Quest"), warnings);
        }
        if (enRoot != null && Files.isDirectory(enXml)) {
            ok &= invokeBatch(patcher, enXml, enRoot.resolve("Quest"), warnings);
        } else if (enRoot == null) {
            warnings.add(I18nUtil.getMessage("QuestClientSync.warn.noEnPath"));
        }
        return ok;
    }

    private boolean invokeBatch(Path patcher, Path xmlDir, Path targetDir, List<String> warnings) {
        try {
            Files.createDirectories(targetDir);
            List<String> cmd = List.of(
                    patcher.toString(),
                    "batch",
                    "--full-xml-dir=" + xmlDir,
                    "--target=" + targetDir
            );
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();
            String output = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            boolean finished = p.waitFor(120, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                warnings.add(I18nUtil.getMessage("QuestClientSync.warn.patcherTimeout"));
                return false;
            }
            int code = p.exitValue();
            if (code != 0) {
                warnings.add(I18nUtil.getMessage("QuestClientSync.warn.patcherExit", code, truncate(output, 500)));
                return false;
            }
            log.info(I18nUtil.getLogMessage("QuestClientSync.patcher.info"), targetDir, truncate(output, 200));
            return true;
        } catch (Exception e) {
            warnings.add(I18nUtil.getMessage("QuestClientSync.warn.patcherFail", e.getMessage()));
            return false;
        }
    }

    private static void copyDirectory(Path src, Path dest) throws IOException {
        Files.walk(src).forEach(source -> {
            try {
                Path relative = src.relativize(source);
                Path target = dest.resolve(relative.toString());
                if (Files.isDirectory(source)) {
                    Files.createDirectories(target);
                } else {
                    Files.createDirectories(target.getParent());
                    Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        String oneLine = s.lines().collect(Collectors.joining(" | "));
        return oneLine.length() <= max ? oneLine : oneLine.substring(0, max) + "...";
    }
}
