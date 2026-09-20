package org.gms.service.chair;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.GameConfig;
import org.gms.exception.BizException;
import org.gms.model.dto.ChairPosePatchReqDTO;
import org.gms.model.dto.ChairPosePatchRtnDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 开发态：将服务端 Item(Install)/Character(TamingMob)/String 同步到客户端 Data/EN。
 * <p>
 * 门禁：{@code allow_chair_client_write} + 已配置 {@link ClientDataPath}。
 * 有 xml-img-patcher.exe 则 batch；否则导出到 docs/features/client-chair-pose/patches/。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChairPoseClientSyncService {
    public static final String CONFIG_ALLOW_WRITE = "allow_chair_client_write";
    public static final String CONFIG_EN_PATH = "chair_client_en_path";
    public static final String CONFIG_PATCHER_PATH = "chair_xml_img_patcher_path";

    private final ChairWzXmlStore chairWzXmlStore;
    private final TamingMobPoseWzXmlStore tamingMobPoseWzXmlStore;

    public boolean isWriteAllowedPublic() {
        return isWriteAllowed();
    }

    public Optional<Path> resolveEnPathPublic(Path dataRoot) {
        return resolveEnPath(dataRoot);
    }

    public Optional<Path> findPatcherPublic() {
        return findPatcher();
    }

    public ChairPosePatchRtnDTO sync(ChairPosePatchReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        boolean dryRun = !Boolean.FALSE.equals(req.getDryRun());
        List<String> warnings = new ArrayList<>();
        List<String> files = new ArrayList<>();

        Optional<Path> dataOpt = ClientDataPath.resolve();
        Optional<Path> enOpt = resolveEnPath(dataOpt.orElse(null));
        Optional<Path> patcherOpt = findPatcher();

        if (dataOpt.isEmpty()) {
            if (!dryRun) {
                throw BizException.illegalArgument(
                        I18nUtil.getExceptionMessage("ChairPoseClientSync.clientPath.required"));
            }
            warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.noClientPath"));
        }
        if (!isWriteAllowed() && !dryRun) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairPoseClientSync.writeDisabled"));
        }

        if (req.getItemIds() != null && !req.getItemIds().isEmpty()) {
            warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.installWholeFile"));
        }

        List<Integer> mobIds = req.getMobIds() == null || req.getMobIds().isEmpty()
                ? tamingMobPoseWzXmlStore.listAllMobIds()
                : req.getMobIds();

        Path exportDir = createExportDir();
        List<Path> exported = exportSnapshot(exportDir, mobIds, warnings);
        for (Path p : exported) {
            files.add(p.toString());
        }
        writeManifest(exportDir, req.getItemIds(), mobIds, dataOpt.orElse(null), enOpt.orElse(null),
                patcherOpt.orElse(null));

        if (dryRun) {
            return ChairPosePatchRtnDTO.builder()
                    .dryRun(true)
                    .applied(false)
                    .patcherAvailable(patcherOpt.isPresent())
                    .clientDataPath(dataOpt.map(Path::toString).orElse(""))
                    .clientEnPath(enOpt.map(Path::toString).orElse(""))
                    .exportDir(exportDir.toString())
                    .files(files)
                    .warnings(warnings)
                    .message(I18nUtil.getMessage("ChairPoseClientSync.dryRun.ok"))
                    .build();
        }

        Path dataRoot = dataOpt.get();
        boolean applied = false;
        if (patcherOpt.isPresent()) {
            applied = runPatcher(patcherOpt.get(), exportDir, dataRoot, enOpt.orElse(null), warnings);
        } else {
            warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.noPatcher"));
            Path fallback = dataRoot.getParent() != null
                    ? dataRoot.getParent().resolve("chair-pose-patches")
                    : dataRoot.resolve("chair-pose-patches");
            try {
                Files.createDirectories(fallback);
                Path dest = fallback.resolve(exportDir.getFileName());
                copyDirectory(exportDir, dest);
                files.add(dest.toString());
                warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.fallbackExport", dest.toString()));
            } catch (IOException e) {
                throw BizException.illegalArgument(
                        I18nUtil.getExceptionMessage("ChairPoseClientSync.exportFail", e.getMessage()));
            }
        }

        log.info(I18nUtil.getLogMessage("ChairPoseClientSync.sync.info"), dryRun, applied, exportDir);
        return ChairPosePatchRtnDTO.builder()
                .dryRun(false)
                .applied(applied)
                .patcherAvailable(patcherOpt.isPresent())
                .clientDataPath(dataRoot.toString())
                .clientEnPath(enOpt.map(Path::toString).orElse(""))
                .exportDir(exportDir.toString())
                .files(files)
                .warnings(warnings)
                .message(applied
                        ? I18nUtil.getMessage("ChairPoseClientSync.apply.ok")
                        : I18nUtil.getMessage("ChairPoseClientSync.apply.degraded"))
                .build();
    }

    private boolean isWriteAllowed() {
        try {
            return GameConfig.getServerBoolean(CONFIG_ALLOW_WRITE);
        } catch (Exception e) {
            return false;
        }
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
            String skillCfg = GameConfig.getServerString("skill_xml_img_patcher_path");
            if (StringUtils.hasText(skillCfg)) {
                candidates.add(Path.of(skillCfg.trim()));
            }
            String questCfg = GameConfig.getServerString("quest_xml_img_patcher_path");
            if (StringUtils.hasText(questCfg)) {
                candidates.add(Path.of(questCfg.trim()));
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
        Path dir = Path.of("docs", "features", "client-chair-pose", "patches", stamp);
        try {
            Files.createDirectories(dir);
            return dir.toAbsolutePath().normalize();
        } catch (IOException e) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairPoseClientSync.exportFail", e.getMessage()));
        }
    }

    private List<Path> exportSnapshot(Path exportDir, List<Integer> mobIds, List<String> warnings) {
        List<Path> out = new ArrayList<>();
        try {
            // Item Install
            Path baseItemInstall = exportDir.resolve("server-wz").resolve("Item.wz").resolve("Install");
            Files.createDirectories(baseItemInstall);
            Path installBase = chairWzXmlStore.itemBaseDir().resolve(chairWzXmlStore.installRelative());
            if (Files.isRegularFile(installBase)) {
                Path dest = baseItemInstall.resolve(ChairWzXmlStore.INSTALL_FILE);
                Files.copy(installBase, dest, StandardCopyOption.REPLACE_EXISTING);
                out.add(dest);
            }
            if (chairWzXmlStore.itemLanguageExists()) {
                Path langInstall = chairWzXmlStore.itemLanguageDir().resolve(chairWzXmlStore.installRelative());
                if (Files.isRegularFile(langInstall)) {
                    Path langOut = exportDir.resolve("server-wz-zh-CN").resolve("Item.wz").resolve("Install");
                    Files.createDirectories(langOut);
                    Path dest = langOut.resolve(ChairWzXmlStore.INSTALL_FILE);
                    Files.copy(langInstall, dest, StandardCopyOption.REPLACE_EXISTING);
                    out.add(dest);
                } else {
                    warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.noLangItem"));
                }
            } else {
                warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.noLangItem"));
            }

            // String Ins / Eqp
            copyStringFile(exportDir, "server-wz", ChairWzXmlStore.STRING_INS_FILE,
                    chairWzXmlStore.stringBaseDir(), out);
            if (chairWzXmlStore.stringLanguageExists()) {
                copyStringFile(exportDir, "server-wz-zh-CN", ChairWzXmlStore.STRING_INS_FILE,
                        chairWzXmlStore.stringLanguageDir(), out);
            }
            copyStringFile(exportDir, "server-wz", TamingMobPoseWzXmlStore.STRING_EQP_FILE,
                    tamingMobPoseWzXmlStore.stringBaseDir(), out);
            if (tamingMobPoseWzXmlStore.stringLanguageExists()) {
                copyStringFile(exportDir, "server-wz-zh-CN", TamingMobPoseWzXmlStore.STRING_EQP_FILE,
                        tamingMobPoseWzXmlStore.stringLanguageDir(), out);
            }

            // Character TamingMob
            Path baseTaming = exportDir.resolve("server-wz").resolve("Character.wz").resolve("TamingMob");
            Files.createDirectories(baseTaming);
            boolean anyLang = false;
            Path langTaming = exportDir.resolve("server-wz-zh-CN").resolve("Character.wz").resolve("TamingMob");
            for (Integer mobId : mobIds) {
                if (mobId == null) {
                    continue;
                }
                String name = TamingMobPoseWzXmlStore.imgFileName(mobId);
                Path srcBase = tamingMobPoseWzXmlStore.characterBaseDir()
                        .resolve(tamingMobPoseWzXmlStore.tamingRelative(mobId));
                if (Files.isRegularFile(srcBase)) {
                    Path dest = baseTaming.resolve(name);
                    Files.copy(srcBase, dest, StandardCopyOption.REPLACE_EXISTING);
                    out.add(dest);
                }
                if (tamingMobPoseWzXmlStore.characterLanguageExists()) {
                    Path srcLang = tamingMobPoseWzXmlStore.characterLanguageDir()
                            .resolve(tamingMobPoseWzXmlStore.tamingRelative(mobId));
                    if (Files.isRegularFile(srcLang)) {
                        Files.createDirectories(langTaming);
                        Path dest = langTaming.resolve(name);
                        Files.copy(srcLang, dest, StandardCopyOption.REPLACE_EXISTING);
                        out.add(dest);
                        anyLang = true;
                    }
                }
            }
            if (tamingMobPoseWzXmlStore.characterLanguageExists() && !anyLang) {
                warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.noLangCharacter"));
            } else if (!tamingMobPoseWzXmlStore.characterLanguageExists()) {
                warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.noLangCharacter"));
            }
        } catch (IOException e) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairPoseClientSync.exportFail", e.getMessage()));
        }
        return out;
    }

    private void copyStringFile(Path exportDir, String branch, String fileName, Path stringDir, List<Path> out)
            throws IOException {
        Path src = stringDir.resolve(fileName);
        if (!Files.isRegularFile(src)) {
            return;
        }
        Path destDir = exportDir.resolve(branch).resolve("String.wz");
        Files.createDirectories(destDir);
        Path dest = destDir.resolve(fileName);
        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        out.add(dest);
    }

    private void writeManifest(Path exportDir, List<Integer> itemIds, List<Integer> mobIds,
                               Path data, Path en, Path patcher) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Chair pose client sync export\n");
        sb.append("time=").append(LocalDateTime.now()).append('\n');
        sb.append("itemIds=").append(itemIds == null ? "ALL_INSTALL" : itemIds).append('\n');
        sb.append("mobIds=").append(mobIds).append('\n');
        sb.append("clientData=").append(data == null ? "" : data).append('\n');
        sb.append("clientEn=").append(en == null ? "" : en).append('\n');
        sb.append("patcher=").append(patcher == null ? "" : patcher).append('\n');
        sb.append("\n# Mapping (CLAUDE.md)\n");
        sb.append("# server wz/          -> client EN/\n");
        sb.append("# server wz-zh-CN/    -> client Data/\n");
        sb.append("# Item.wz/Install/0301.img.xml -> Item/Install/0301.img\n");
        sb.append("# Character.wz/TamingMob/X.img.xml -> Character/TamingMob/X.img\n");
        sb.append("# String.wz/Ins|Eqp.img.xml -> String/Ins|Eqp.img\n");
        try {
            Files.writeString(exportDir.resolve("MANIFEST.txt"), sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn(I18nUtil.getLogMessage("ChairPoseClientSync.manifest.warn"), e.getMessage());
        }
    }

    private boolean runPatcher(Path patcher, Path exportDir, Path dataRoot, Path enRoot, List<String> warnings) {
        boolean ok = true;
        // zh-CN → Data
        ok &= batchIfPresent(patcher, exportDir.resolve("server-wz-zh-CN").resolve("Item.wz"),
                dataRoot.resolve("Item"), warnings);
        ok &= batchIfPresent(patcher, exportDir.resolve("server-wz-zh-CN").resolve("Character.wz"),
                dataRoot.resolve("Character"), warnings);
        ok &= batchIfPresent(patcher, exportDir.resolve("server-wz-zh-CN").resolve("String.wz"),
                dataRoot.resolve("String"), warnings);
        if (enRoot != null) {
            ok &= batchIfPresent(patcher, exportDir.resolve("server-wz").resolve("Item.wz"),
                    enRoot.resolve("Item"), warnings);
            ok &= batchIfPresent(patcher, exportDir.resolve("server-wz").resolve("Character.wz"),
                    enRoot.resolve("Character"), warnings);
            ok &= batchIfPresent(patcher, exportDir.resolve("server-wz").resolve("String.wz"),
                    enRoot.resolve("String"), warnings);
        } else {
            warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.noEnPath"));
        }
        return ok;
    }

    private boolean batchIfPresent(Path patcher, Path xmlDir, Path targetDir, List<String> warnings) {
        if (!Files.isDirectory(xmlDir)) {
            return true;
        }
        return invokeBatch(patcher, xmlDir, targetDir, warnings);
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
            boolean finished = p.waitFor(180, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.patcherTimeout"));
                return false;
            }
            int code = p.exitValue();
            if (code != 0) {
                warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.patcherExit", code, truncate(output, 500)));
                return false;
            }
            log.info(I18nUtil.getLogMessage("ChairPoseClientSync.patcher.info"), targetDir, truncate(output, 200));
            return true;
        } catch (Exception e) {
            warnings.add(I18nUtil.getMessage("ChairPoseClientSync.warn.patcherFail", e.getMessage()));
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
