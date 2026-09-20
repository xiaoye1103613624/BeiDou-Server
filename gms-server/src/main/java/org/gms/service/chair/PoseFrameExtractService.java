package org.gms.service.chair;

import lombok.extern.slf4j.Slf4j;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.server.icon.PoseFrameFiles;
import org.gms.server.icon.SharedIconFiles;
import org.gms.service.skill.SkillWzXmlStore;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Extract WZ 1:1 effect/stand canvas PNGs from client {@code .img} via orange-wz {@code DumpPoseFrame}.
 * Cached under {@link PoseFrameFiles}; inventory icons are never accepted as pose frames.
 */
@Slf4j
@Service
public class PoseFrameExtractService {
    private static final long EXTRACT_TIMEOUT_SEC = 60L;

    public Optional<String> ensureChairEffectFrame(int itemId, String layer, Integer expectW, Integer expectH) {
        String layerName = StringUtils.hasText(layer) ? layer.trim() : "effect";
        Path cached = PoseFrameFiles.pngPath(PoseFrameFiles.KIND_CHAIR, itemId, layerName, 0);
        if (dimensionsMatch(cached, expectW, expectH)) {
            return Optional.of(PoseFrameFiles.webUrl(PoseFrameFiles.KIND_CHAIR, itemId, layerName, 0));
        }

        Optional<Path> img = resolveInstallImg(itemId);
        if (img.isEmpty()) {
            return Optional.empty();
        }
        String nodePath = String.format(Locale.ROOT, "%08d/%s/0", itemId, layerName);
        if (!runDump(img.get(), nodePath, cached)) {
            return Optional.empty();
        }
        if (!dimensionsMatch(cached, expectW, expectH)) {
            log.warn(I18nUtil.getLogMessage("PoseFrameExtract.dimension.mismatch"),
                    cached, expectW, expectH);
            deleteQuietly(cached);
            return Optional.empty();
        }
        return Optional.of(PoseFrameFiles.webUrl(PoseFrameFiles.KIND_CHAIR, itemId, layerName, 0));
    }

    public Optional<String> ensureTamingFrame(int mobId, String action, int frameIndex,
                                              Integer expectW, Integer expectH) {
        String act = StringUtils.hasText(action) ? action.trim() : "stand1";
        Path cached = PoseFrameFiles.pngPath(PoseFrameFiles.KIND_TAMING, mobId, act, frameIndex);
        if (dimensionsMatch(cached, expectW, expectH)) {
            return Optional.of(PoseFrameFiles.webUrl(PoseFrameFiles.KIND_TAMING, mobId, act, frameIndex));
        }

        Optional<Path> img = resolveTamingImg(mobId);
        if (img.isEmpty()) {
            return Optional.empty();
        }
        // Character/TamingMob: stand1/0/0 （帧目录下名为 0 的 canvas）
        String nodePath = act + "/" + frameIndex + "/0";
        if (!runDump(img.get(), nodePath, cached)) {
            return Optional.empty();
        }
        if (!dimensionsMatch(cached, expectW, expectH)) {
            deleteQuietly(cached);
            return Optional.empty();
        }
        return Optional.of(PoseFrameFiles.webUrl(PoseFrameFiles.KIND_TAMING, mobId, act, frameIndex));
    }

    /**
     * 从客户端 {@code Skill/{job}.img} 抽出技能 icon，写入 {@code game-assets/skill/{id}.png}。
     *
     * @param skillId 技能 ID
     * @param force   true 时忽略已有缓存强制重抽
     * @return PNG 字节；缺客户端路径 / DumpPoseFrame / 节点时 empty
     */
    public Optional<byte[]> ensureSkillIconBytes(int skillId, boolean force) {
        if (skillId <= 0) {
            return Optional.empty();
        }
        if (!force) {
            Optional<byte[]> cached = SharedIconFiles.readPng("skill", skillId);
            if (cached.isPresent()) {
                return cached;
            }
        }
        int jobId = skillId / 10000;
        Optional<Path> img = resolveSkillImg(jobId);
        if (img.isEmpty()) {
            return Optional.empty();
        }
        Path outPng = SharedIconFiles.pngPath("skill", skillId);
        for (String key : SkillWzXmlStore.stringSkillKeyCandidates(skillId)) {
            String nodePath = key + "/icon";
            if (runDump(img.get(), nodePath, outPng)) {
                Optional<byte[]> bytes = SharedIconFiles.readPng("skill", skillId);
                if (bytes.isPresent()) {
                    return bytes;
                }
            }
        }
        return Optional.empty();
    }

    /**
     * 抽出技能 effect 单帧到 {@code pose-frame/skill/{skillId}/{layer}/{frame}.png}。
     *
     * @param skillId   技能 ID
     * @param nodePath  相对 .img 根的 canvas 路径，如 {@code 1121008/effect/0}
     * @param layer     缓存层名（{@code effect} 或 {@code effect_0}）
     * @param frameIndex 帧序号
     * @param force     强制重抽
     */
    public Optional<String> ensureSkillEffectFrame(int skillId, String nodePath, String layer,
                                                   int frameIndex, boolean force) {
        if (skillId <= 0 || !StringUtils.hasText(nodePath)) {
            return Optional.empty();
        }
        String layerName = StringUtils.hasText(layer) ? layer.trim() : "effect";
        Path cached = PoseFrameFiles.pngPath(PoseFrameFiles.KIND_SKILL, skillId, layerName, frameIndex);
        if (!force && Files.isRegularFile(cached)) {
            Optional<byte[]> existing = PoseFrameFiles.readPngBytes(cached);
            if (existing.isPresent()) {
                return Optional.of(PoseFrameFiles.webUrl(PoseFrameFiles.KIND_SKILL, skillId, layerName, frameIndex));
            }
        }
        int jobId = skillId / 10000;
        Optional<Path> img = resolveSkillImg(jobId);
        if (img.isEmpty()) {
            return Optional.empty();
        }
        if (!runDump(img.get(), nodePath.trim(), cached)) {
            return Optional.empty();
        }
        if (!Files.isRegularFile(cached)) {
            return Optional.empty();
        }
        return Optional.of(PoseFrameFiles.webUrl(PoseFrameFiles.KIND_SKILL, skillId, layerName, frameIndex));
    }

    /**
     * 抽出 {@code Character.wz} 单部位 canvas 像素，供人偶合成使用。
     * <p>
     * nodePath 形如 {@code sit/0/body}、{@code default/hairShade/0}（斜杠分隔，相对 .img 根）。
     * 命中缓存且是合法 PNG 时直接返回，避免反复起 DumpPoseFrame 进程。
     *
     * @param imgFile 客户端 {@code Character/<...>.img} 文件
     * @param nodePath 部位节点路径
     * @param outPng 目标缓存 PNG（不存在时执行抽取）
     */
    public Optional<byte[]> ensureCharacterPartFrame(Path imgFile, String nodePath, Path outPng) {
        Optional<byte[]> cached = PoseFrameFiles.readPngBytes(outPng);
        if (cached.isPresent()) {
            return cached;
        }
        if (imgFile == null || !Files.isRegularFile(imgFile) || !StringUtils.hasText(nodePath)) {
            return Optional.empty();
        }
        try {
            if (!runDump(imgFile, nodePath.trim(), outPng)) {
                return Optional.empty();
            }
            return PoseFrameFiles.readPngBytes(outPng);
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("PoseFrameExtract.dump.error"), nodePath, e.toString());
            return Optional.empty();
        }
    }

    public boolean isWzScalePng(byte[] png, Integer expectW, Integer expectH) {
        if (!SharedIconFiles.isPng(png)) {
            return false;
        }
        if (expectW == null || expectH == null || expectW <= 0 || expectH <= 0) {
            return true;
        }
        try {
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(png));
            return img != null && img.getWidth() == expectW && img.getHeight() == expectH;
        } catch (IOException e) {
            return false;
        }
    }

    private boolean dimensionsMatch(Path png, Integer expectW, Integer expectH) {
        if (!Files.isRegularFile(png)) {
            return false;
        }
        if (expectW == null || expectH == null || expectW <= 0 || expectH <= 0) {
            return true;
        }
        try (InputStream in = Files.newInputStream(png)) {
            BufferedImage img = ImageIO.read(in);
            return img != null && img.getWidth() == expectW && img.getHeight() == expectH;
        } catch (IOException e) {
            return false;
        }
    }

    private Optional<Path> resolveInstallImg(int itemId) {
        Optional<Path> data = ClientDataPath.resolve();
        if (data.isEmpty()) {
            return Optional.empty();
        }
        int prefix = itemId / 10000;
        String fileName = String.format(Locale.ROOT, "%04d.img", prefix);
        Path p = data.get().resolve("Item").resolve("Install").resolve(fileName);
        if (Files.isRegularFile(p)) {
            return Optional.of(p);
        }
        Path alt = data.get().resolve("Item").resolve(fileName);
        return Files.isRegularFile(alt) ? Optional.of(alt) : Optional.empty();
    }

    private Optional<Path> resolveTamingImg(int mobId) {
        Optional<Path> data = ClientDataPath.resolve();
        if (data.isEmpty()) {
            return Optional.empty();
        }
        String id8 = String.format(Locale.ROOT, "%08d.img", mobId);
        Path p = data.get().resolve("Character").resolve("TamingMob").resolve(id8);
        return Files.isRegularFile(p) ? Optional.of(p) : Optional.empty();
    }

    /**
     * 解析客户端 {@code Data/Skill/{job}.img}；兼容 {@code 112.img} / {@code 0112.img} / {@code 000.img}。
     */
    public Optional<Path> resolveSkillImg(int jobId) {
        Optional<Path> data = ClientDataPath.resolve();
        if (data.isEmpty()) {
            return Optional.empty();
        }
        Path skillDir = data.get().resolve("Skill");
        if (!Files.isDirectory(skillDir)) {
            return Optional.empty();
        }
        List<String> names = new ArrayList<>();
        if (jobId == 0) {
            names.add("000.img");
            names.add("0.img");
        } else {
            names.add(jobId + ".img");
            names.add(String.format(Locale.ROOT, "%03d.img", jobId));
            names.add(String.format(Locale.ROOT, "%04d.img", jobId));
        }
        for (String name : names) {
            Path p = skillDir.resolve(name);
            if (Files.isRegularFile(p)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    private boolean runDump(Path imgPath, String nodePath, Path outPng) {
        Optional<Path> orangeHome = resolveOrangeWzHome();
        if (orangeHome.isEmpty()) {
            log.debug(I18nUtil.getLogMessage("PoseFrameExtract.orangeWz.missing"));
            return false;
        }
        Path home = orangeHome.get();
        Path toolsOut = home.resolve("tools_out");
        Path toolClass = toolsOut.resolve("DumpPoseFrame.class");
        if (!Files.isRegularFile(toolClass)) {
            log.debug(I18nUtil.getLogMessage("PoseFrameExtract.dumpTool.missing"), toolClass);
            return false;
        }
        try {
            Files.createDirectories(outPng.getParent());
            String cp = toolsOut + System.getProperty("path.separator") + buildClasspath(home);
            List<String> cmd = List.of(
                    resolveJavaBin(),
                    "-cp", cp,
                    "DumpPoseFrame",
                    imgPath.toAbsolutePath().normalize().toString(),
                    nodePath,
                    outPng.toAbsolutePath().normalize().toString()
            );
            boolean ok = runProcess(cmd, home);
            if (ok && Files.isRegularFile(outPng)) {
                log.info(I18nUtil.getLogMessage("PoseFrameExtract.dump.ok"), nodePath, outPng);
                return true;
            }
            log.warn(I18nUtil.getLogMessage("PoseFrameExtract.dump.fail"), nodePath, imgPath);
            return false;
        } catch (Exception e) {
            log.warn(I18nUtil.getLogMessage("PoseFrameExtract.dump.error"), nodePath, e.toString());
            return false;
        }
    }

    private static String buildClasspath(Path home) {
        String sep = System.getProperty("path.separator");
        List<String> parts = new ArrayList<>();
        Path classes = home.resolve("target").resolve("classes");
        Path jarOriginal = home.resolve("target").resolve("OrzRepacker.jar.original");
        Path jar = home.resolve("target").resolve("OrzRepacker.jar");
        if (Files.isDirectory(classes)) {
            parts.add(classes.toString());
        }
        if (Files.isRegularFile(jarOriginal)) {
            parts.add(jarOriginal.toString());
        } else if (Files.isRegularFile(jar)) {
            parts.add(jar.toString());
        }
        Path lib = home.resolve("target").resolve("lib");
        if (Files.isDirectory(lib)) {
            try (var stream = Files.list(lib)) {
                stream.filter(p -> p.getFileName().toString().endsWith(".jar"))
                        .forEach(p -> parts.add(p.toString()));
            } catch (IOException e) {
                parts.add(lib.resolve("*").toString());
            }
        }
        return String.join(sep, parts);
    }

    private static Optional<Path> resolveOrangeWzHome() {
        String[] candidates = {
                System.getenv("ORANGE_WZ_HOME"),
                System.getProperty("orange.wz.home"),
                "E:/project/orange-wz",
                Paths.get(System.getProperty("user.dir")).resolve("../orange-wz").toString(),
                Paths.get(System.getProperty("user.dir")).resolve("../../orange-wz").toString()
        };
        for (String c : candidates) {
            if (!StringUtils.hasText(c)) {
                continue;
            }
            Path p = Paths.get(c.trim()).toAbsolutePath().normalize();
            if (Files.isRegularFile(p.resolve("tools_out").resolve("DumpPoseFrame.class"))
                    || Files.isRegularFile(p.resolve("tools").resolve("DumpPoseFrame.java"))) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    private static String resolveJavaBin() {
        String home = System.getProperty("java.home");
        if (StringUtils.hasText(home)) {
            boolean win = System.getProperty("os.name", "").toLowerCase(Locale.ROOT).contains("win");
            Path bin = Paths.get(home, "bin", win ? "java.exe" : "java");
            if (Files.isRegularFile(bin)) {
                return bin.toString();
            }
        }
        return "java";
    }

    private static boolean runProcess(List<String> cmd, Path workDir) {
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(workDir.toFile());
        pb.redirectErrorStream(true);
        try {
            Process p = pb.start();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            p.getInputStream().transferTo(bos);
            boolean finished = p.waitFor(EXTRACT_TIMEOUT_SEC, TimeUnit.SECONDS);
            if (!finished) {
                p.destroyForcibly();
                return false;
            }
            String out = bos.toString(StandardCharsets.UTF_8);
            if (p.exitValue() != 0) {
                log.debug("DumpPoseFrame exit={} out={}", p.exitValue(), out);
                return false;
            }
            return out.contains("OK ");
        } catch (Exception e) {
            log.debug("DumpPoseFrame process failed: {}", e.toString());
            return false;
        }
    }

    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
            // ignore
        }
    }
}
