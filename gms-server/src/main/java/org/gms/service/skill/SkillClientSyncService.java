package org.gms.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.GameConfig;
import org.gms.exception.BizException;
import org.gms.model.dto.SkillPatchReqDTO;
import org.gms.model.dto.SkillPatchRtnDTO;
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
 * 开发态：将服务端 Skill.wz / String.wz(Skill) 同步到客户端 Data/EN。
 * <p>
 * 门禁：{@code allow_skill_client_write}（默认 false）+ 已配置 {@link ClientDataPath}。
 * 有 xml-img-patcher.exe 则 batch；否则导出补丁清单到 docs/features/skill-web-admin/patches/。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkillClientSyncService {
    public static final String CONFIG_ALLOW_WRITE = "allow_skill_client_write";
    public static final String CONFIG_EN_PATH = "skill_client_en_path";
    public static final String CONFIG_PATCHER_PATH = "skill_xml_img_patcher_path";

    private final SkillWzXmlStore wzXmlStore;

    public boolean isWriteAllowedPublic() {
        return isWriteAllowed();
    }

    public Optional<Path> resolveEnPathPublic(Path dataRoot) {
        return resolveEnPath(dataRoot);
    }

    public Optional<Path> findPatcherPublic() {
        return findPatcher();
    }

    public SkillPatchRtnDTO sync(SkillPatchReqDTO req) {
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
                        I18nUtil.getExceptionMessage("SkillClientSync.clientPath.required"));
            }
            warnings.add(I18nUtil.getMessage("SkillClientSync.warn.noClientPath"));
        }
        if (!isWriteAllowed() && !dryRun) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("SkillClientSync.writeDisabled"));
        }

        List<Integer> jobIds = req.getJobIds() == null || req.getJobIds().isEmpty()
                ? SkillJobCatalog.allEnabledJobIds()
                : req.getJobIds();

        Path exportDir = createExportDir();
        List<Path> exported = exportSkillXmlSnapshot(exportDir, jobIds, warnings);
        for (Path p : exported) {
            files.add(p.toString());
        }
        writeManifest(exportDir, jobIds, dataOpt.orElse(null), enOpt.orElse(null), patcherOpt.orElse(null));

        if (dryRun) {
            return SkillPatchRtnDTO.builder()
                    .dryRun(true)
                    .applied(false)
                    .patcherAvailable(patcherOpt.isPresent())
                    .clientDataPath(dataOpt.map(Path::toString).orElse(""))
                    .clientEnPath(enOpt.map(Path::toString).orElse(""))
                    .exportDir(exportDir.toString())
                    .files(files)
                    .warnings(warnings)
                    .message(I18nUtil.getMessage("SkillClientSync.dryRun.ok"))
                    .build();
        }

        Path dataRoot = dataOpt.get();
        boolean applied = false;
        if (patcherOpt.isPresent()) {
            applied = runPatcher(patcherOpt.get(), exportDir, dataRoot, enOpt.orElse(null), warnings);
        } else {
            warnings.add(I18nUtil.getMessage("SkillClientSync.warn.noPatcher"));
            Path fallback = dataRoot.getParent() != null
                    ? dataRoot.getParent().resolve("skill-management-patches")
                    : dataRoot.resolve("skill-management-patches");
            try {
                Files.createDirectories(fallback);
                Path dest = fallback.resolve(exportDir.getFileName());
                copyDirectory(exportDir, dest);
                files.add(dest.toString());
                warnings.add(I18nUtil.getMessage("SkillClientSync.warn.fallbackExport", dest.toString()));
            } catch (IOException e) {
                throw BizException.illegalArgument(
                        I18nUtil.getExceptionMessage("SkillClientSync.exportFail", e.getMessage()));
            }
        }

        log.info(I18nUtil.getLogMessage("SkillClientSync.sync.info"), dryRun, applied, exportDir);
        return SkillPatchRtnDTO.builder()
                .dryRun(false)
                .applied(applied)
                .patcherAvailable(patcherOpt.isPresent())
                .clientDataPath(dataRoot.toString())
                .clientEnPath(enOpt.map(Path::toString).orElse(""))
                .exportDir(exportDir.toString())
                .files(files)
                .warnings(warnings)
                .message(applied
                        ? I18nUtil.getMessage("SkillClientSync.apply.ok")
                        : I18nUtil.getMessage("SkillClientSync.apply.degraded"))
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
        Path dir = Path.of("docs", "features", "skill-web-admin", "patches", stamp);
        try {
            Files.createDirectories(dir);
            return dir.toAbsolutePath().normalize();
        } catch (IOException e) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("SkillClientSync.exportFail", e.getMessage()));
        }
    }

    private List<Path> exportSkillXmlSnapshot(Path exportDir, List<Integer> jobIds, List<String> warnings) {
        List<Path> out = new ArrayList<>();
        Path baseSkillOut = exportDir.resolve("server-wz").resolve("Skill.wz");
        Path langSkillOut = exportDir.resolve("server-wz-zh-CN").resolve("Skill.wz");
        Path baseStringOut = exportDir.resolve("server-wz").resolve("String.wz");
        Path langStringOut = exportDir.resolve("server-wz-zh-CN").resolve("String.wz");
        try {
            Files.createDirectories(baseSkillOut);
            Files.createDirectories(baseStringOut);
            for (Integer jobId : jobIds) {
                if (jobId == null) {
                    continue;
                }
                String name = wzXmlStore.skillImgFileName(jobId);
                Path src = wzXmlStore.skillBaseDir().resolve(name);
                if (Files.isRegularFile(src)) {
                    Path dest = baseSkillOut.resolve(name);
                    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                    out.add(dest);
                }
            }
            Path strBase = wzXmlStore.stringBaseDir().resolve(SkillWzXmlStore.STRING_SKILL_FILE);
            if (Files.isRegularFile(strBase)) {
                Path dest = baseStringOut.resolve(SkillWzXmlStore.STRING_SKILL_FILE);
                Files.copy(strBase, dest, StandardCopyOption.REPLACE_EXISTING);
                out.add(dest);
            }

            if (wzXmlStore.skillLanguageExists()) {
                Files.createDirectories(langSkillOut);
                for (Integer jobId : jobIds) {
                    if (jobId == null) {
                        continue;
                    }
                    String name = wzXmlStore.skillImgFileName(jobId);
                    Path src = wzXmlStore.skillLanguageDir().resolve(name);
                    if (Files.isRegularFile(src)) {
                        Path dest = langSkillOut.resolve(name);
                        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
                        out.add(dest);
                    }
                }
            } else {
                warnings.add(I18nUtil.getMessage("SkillClientSync.warn.noLangWz"));
            }
            if (wzXmlStore.stringLanguageExists()) {
                Files.createDirectories(langStringOut);
                Path strLang = wzXmlStore.stringLanguageDir().resolve(SkillWzXmlStore.STRING_SKILL_FILE);
                if (Files.isRegularFile(strLang)) {
                    Path dest = langStringOut.resolve(SkillWzXmlStore.STRING_SKILL_FILE);
                    Files.copy(strLang, dest, StandardCopyOption.REPLACE_EXISTING);
                    out.add(dest);
                }
            }
        } catch (IOException e) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("SkillClientSync.exportFail", e.getMessage()));
        }
        return out;
    }

    private void writeManifest(Path exportDir, List<Integer> jobIds, Path data, Path en, Path patcher) {
        StringBuilder sb = new StringBuilder();
        sb.append("# Skill client sync export\n");
        sb.append("time=").append(LocalDateTime.now()).append('\n');
        sb.append("jobIds=").append(jobIds).append('\n');
        sb.append("clientData=").append(data == null ? "" : data).append('\n');
        sb.append("clientEn=").append(en == null ? "" : en).append('\n');
        sb.append("patcher=").append(patcher == null ? "" : patcher).append('\n');
        sb.append("\n# Mapping (CLAUDE.md)\n");
        sb.append("# server wz/          -> client EN/\n");
        sb.append("# server wz-zh-CN/    -> client Data/\n");
        sb.append("# Skill.wz/X.img.xml  -> Skill/X.img\n");
        sb.append("# String.wz/Skill.img.xml -> String/Skill.img\n");
        sb.append("\n# Manual (when patcher missing):\n");
        sb.append("#   xml-img-patcher.exe batch --full-xml-dir=<export>/server-wz-zh-CN/Skill.wz --target=<Data>/Skill\n");
        sb.append("#   xml-img-patcher.exe batch --full-xml-dir=<export>/server-wz/Skill.wz --target=<EN>/Skill\n");
        try {
            Files.writeString(exportDir.resolve("MANIFEST.txt"), sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn(I18nUtil.getLogMessage("SkillClientSync.manifest.warn"), e.getMessage());
        }
    }

    private boolean runPatcher(Path patcher, Path exportDir, Path dataRoot, Path enRoot, List<String> warnings) {
        boolean ok = true;
        Path zhSkill = exportDir.resolve("server-wz-zh-CN").resolve("Skill.wz");
        Path enSkill = exportDir.resolve("server-wz").resolve("Skill.wz");
        Path zhString = exportDir.resolve("server-wz-zh-CN").resolve("String.wz");
        Path enString = exportDir.resolve("server-wz").resolve("String.wz");
        if (Files.isDirectory(zhSkill)) {
            ok &= invokeBatch(patcher, zhSkill, dataRoot.resolve("Skill"), warnings);
        }
        if (Files.isDirectory(zhString)) {
            ok &= invokeBatch(patcher, zhString, dataRoot.resolve("String"), warnings);
        }
        if (enRoot != null) {
            if (Files.isDirectory(enSkill)) {
                ok &= invokeBatch(patcher, enSkill, enRoot.resolve("Skill"), warnings);
            }
            if (Files.isDirectory(enString)) {
                ok &= invokeBatch(patcher, enString, enRoot.resolve("String"), warnings);
            }
        } else {
            warnings.add(I18nUtil.getMessage("SkillClientSync.warn.noEnPath"));
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
            boolean finished = p.waitFor(180, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                warnings.add(I18nUtil.getMessage("SkillClientSync.warn.patcherTimeout"));
                return false;
            }
            int code = p.exitValue();
            if (code != 0) {
                warnings.add(I18nUtil.getMessage("SkillClientSync.warn.patcherExit", code, truncate(output, 500)));
                return false;
            }
            log.info(I18nUtil.getLogMessage("SkillClientSync.patcher.info"), targetDir, truncate(output, 200));
            return true;
        } catch (Exception e) {
            warnings.add(I18nUtil.getMessage("SkillClientSync.warn.patcherFail", e.getMessage()));
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
