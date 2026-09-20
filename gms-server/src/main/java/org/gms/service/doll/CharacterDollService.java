package org.gms.service.doll;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.model.dto.CharacterDollReqDTO;
import org.gms.model.dto.CharacterDollRtnDTO;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.server.icon.PoseFrameFiles;
import org.gms.service.chair.PoseFrameExtractService;
import org.gms.service.doll.CharacterWzPartStore.DollPart;
import org.gms.service.doll.CharacterWzPartStore.SourceKind;
import org.gms.service.doll.DollCompositor.Layout;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * 本地人偶合成（替代 maplestory.io CDN 整体渲染图）。
 * <p>
 * 链路：
 * <ol>
 *   <li>结构：{@link CharacterWzPartStore} 读 {@code Character.wz} XML，得到部位节点、origin、map 锚点、z 层序。</li>
 *   <li>像素：{@link PoseFrameExtractService#ensureCharacterPartFrame} 从客户端 {@code Character/*.img} 抽各部位 PNG（带缓存）。</li>
 *   <li>合成：{@link DollCompositor} 按锚点对齐 + zmap 层序叠加，产出 WZ 1:1 透明 PNG 及锚点坐标。</li>
 * </ol>
 * 几何（尺寸/锚点）只依赖结构数据，缺像素也能算出来，便于前端在降级时仍保持正确的对齐与比例。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterDollService {
    /** lookKey 超长时的截断长度，避免文件名越界。 */
    private static final int LOOK_KEY_MAX = 60;

    private final CharacterWzPartStore partStore;
    private final PoseFrameExtractService poseFrameExtractService;

    /**
     * 合成人偶。
     *
     * @return 含合成 PNG 与锚点坐标；无法本地合成时返回 {@code mode=NONE}（前端据此降级）
     */
    public CharacterDollRtnDTO render(CharacterDollReqDTO req) {
        Objects.requireNonNull(req, "CharacterDollReqDTO");
        int skinId = clamp(req.getSkinId() == null ? CharacterWzPartStore.DEFAULT_SKIN_ID : req.getSkinId(), 0, 9999);
        int hairId = req.getHairId() == null ? CharacterWzPartStore.DEFAULT_HAIR_ID : req.getHairId();
        int faceId = req.getFaceId() == null ? CharacterWzPartStore.DEFAULT_FACE_ID : req.getFaceId();
        String pose = req.getPose() == null || req.getPose().isBlank()
                ? CharacterWzPartStore.DEFAULT_POSE
                : req.getPose().trim();
        int frame = Math.max(req.getFrame() == null ? 0 : req.getFrame(), 0);
        List<Integer> equipIds = new ArrayList<>(new LinkedHashSet<>(
                req.getEquipIds() == null ? List.of() : req.getEquipIds()));
        equipIds.removeIf(id -> id == null || id <= 0);
        String lookKey = lookKey(skinId, faceId, hairId, equipIds);

        List<DollPart> parts = collectAllParts(skinId, hairId, faceId, equipIds, pose, frame);
        if (parts.isEmpty()) {
            // 日志文案用 SLF4J `{}`，参数须交给 logger，勿传入 getLogMessage（否则 MessageFormat 解析 `{}` 会炸）
            log.info(I18nUtil.getLogMessage("CharacterDoll.render.noStructure"), pose, skinId);
            return CharacterDollRtnDTO.builder()
                    .mode("NONE")
                    .lookKey(lookKey)
                    .message(I18nUtil.getMessage("CharacterDoll.structureMissing"))
                    .build();
        }

        Optional<Layout> layoutOpt = DollCompositor.layout(parts);
        if (layoutOpt.isEmpty()) {
            return CharacterDollRtnDTO.builder()
                    .mode("NONE")
                    .lookKey(lookKey)
                    .message(I18nUtil.getMessage("CharacterDoll.structureMissing"))
                    .build();
        }
        Layout layout = layoutOpt.get();

        Path cachedPng = PoseFrameFiles.dollPngPath(lookKey, pose, frame);
        boolean hit = Boolean.TRUE.equals(req.getRefresh())
                ? false
                : cacheMatches(cachedPng, layout.width(), layout.height());
        List<String> missingParts = new ArrayList<>();
        if (!hit) {
            Map<String, BufferedImage> pixels = ensurePixels(parts, missingParts);
            if (pixels.isEmpty()) {
                log.info(I18nUtil.getLogMessage("CharacterDoll.render.noPixels"), lookKey, parts.size());
                return CharacterDollRtnDTO.builder()
                        .mode("NONE")
                        .width(layout.width())
                        .height(layout.height())
                        .bodyOriginX(layout.bodyOriginX())
                        .bodyOriginY(layout.bodyOriginY())
                        .navelX(layout.navelX())
                        .navelY(layout.navelY())
                        .lookKey(lookKey)
                        .message(I18nUtil.getMessage("CharacterDoll.mode.none"))
                        .build();
            }
            try {
                byte[] png = DollCompositor.renderPng(layout, pixels, partStore.zRank());
                PoseFrameFiles.writeTo(cachedPng, png);
            } catch (IOException e) {
                log.warn(I18nUtil.getLogMessage("CharacterDoll.render.failed"), lookKey, e.toString());
                return CharacterDollRtnDTO.builder()
                        .mode("NONE")
                        .lookKey(lookKey)
                        .missingParts(missingParts)
                        .message(I18nUtil.getMessage("CharacterDoll.renderFailed"))
                        .build();
            }
        }

        List<String> zOrder = new ArrayList<>();
        for (DollCompositor.Placement pl : DollCompositor.drawOrder(layout, partStore.zRank())) {
            zOrder.add(pl.part().partName() + ":" + (pl.part().z() == null ? "-" : pl.part().z()));
        }
        log.info(I18nUtil.getLogMessage("CharacterDoll.render.ok"), lookKey, pose,
                layout.width() + "x" + layout.height(),
                missingParts.size() + "/" + parts.size());

        return CharacterDollRtnDTO.builder()
                .mode("DOLL")
                .imageUrl(PoseFrameFiles.dollWebUrl(lookKey, pose, frame))
                .width(layout.width())
                .height(layout.height())
                .bodyOriginX(layout.bodyOriginX())
                .bodyOriginY(layout.bodyOriginY())
                .navelX(layout.navelX())
                .navelY(layout.navelY())
                .zOrder(zOrder)
                .missingParts(missingParts)
                .lookKey(lookKey)
                .message(missingParts.isEmpty()
                        ? I18nUtil.getMessage("CharacterDoll.mode.doll")
                        : I18nUtil.getMessage("CharacterDoll.mode.partial", String.valueOf(missingParts.size())))
                .build();
    }

    /**
     * 依次收集：身体/四肢 → 头底 → 发型 → 脸型 → 装备。
     * 顺序即锚点依赖顺序（提供neck/navel → 提供brow → 消费锚点）。
     */
    private List<DollPart> collectAllParts(int skinId, int hairId, int faceId, List<Integer> equipIds,
                                           String pose, int frame) {
        List<DollPart> parts = new ArrayList<>();
        parts.addAll(partStore.collectParts(SourceKind.BODY, skinId, pose, frame));
        parts.addAll(partStore.collectParts(SourceKind.HEAD, skinId, pose, frame));
        parts.addAll(partStore.collectParts(SourceKind.WEAR, hairId, pose, frame));
        parts.addAll(partStore.collectParts(SourceKind.WEAR, faceId, pose, frame));
        for (Integer equipId : equipIds) {
            parts.addAll(partStore.collectParts(SourceKind.WEAR, equipId, pose, frame));
        }
        return parts;
    }

    /** 抽取各部位像素；抽不到的节点路径写入 missingParts。 */
    private Map<String, BufferedImage> ensurePixels(List<DollPart> parts, List<String> missingParts) {
        Map<String, BufferedImage> pixels = new HashMap<>();
        Optional<Path> clientRootOpt = ClientDataPath.resolve();
        if (clientRootOpt.isEmpty()) {
            parts.forEach(part -> missingParts.add(part.key()));
            log.debug("doll pixels unavailable: client Data path not configured");
            return pixels;
        }
        Path clientRoot = clientRootOpt.get();
        for (DollPart part : parts) {
            Path img = clientRoot.resolve(CharacterWzPartStore.clientImgOf(part.sourceKind(), part.ownerId()));
            if (!Files.isRegularFile(img)) {
                missingParts.add(part.key());
                continue;
            }
            Path cacheOut = PoseFrameFiles.dollPartPngPath(
                    part.sourceKind().name().toLowerCase(Locale.ROOT), part.ownerId(), part.nodePath());
            Optional<byte[]> bytes = poseFrameExtractService.ensureCharacterPartFrame(img, part.nodePath(), cacheOut);
            if (bytes.isEmpty()) {
                missingParts.add(part.key());
                continue;
            }
            BufferedImage image = decode(bytes.get());
            if (image == null) {
                missingParts.add(part.key());
                continue;
            }
            pixels.put(part.key(), image);
        }
        return pixels;
    }

    private static BufferedImage decode(byte[] png) {
        try {
            return ImageIO.read(new ByteArrayInputStream(png));
        } catch (IOException e) {
            return null;
        }
    }

    /** 缓存可用且与当前几何一致（尺寸变了说明 WZ 改过，须重合成）。 */
    private static boolean cacheMatches(Path png, int width, int height) {
        Optional<byte[]> bytes = PoseFrameFiles.readPngBytes(png);
        if (bytes.isEmpty()) {
            return false;
        }
        BufferedImage image = decode(bytes.get());
        return image != null && image.getWidth() == width && image.getHeight() == height;
    }

    /** 外观缓存键： skin-face-hair-equips（超长时截断 + hash）。 */
    public static String lookKey(int skinId, int faceId, int hairId, List<Integer> equipIds) {
        List<Integer> sorted = new ArrayList<>(equipIds);
        sorted.sort(Integer::compareTo);
        StringBuilder sb = new StringBuilder("s").append(skinId)
                .append("-f").append(faceId)
                .append("-h").append(hairId);
        for (int id : sorted) {
            sb.append('-').append(id);
        }
        String raw = sb.toString();
        if (raw.length() <= LOOK_KEY_MAX) {
            return raw;
        }
        return raw.substring(0, LOOK_KEY_MAX / 2) + Integer.toHexString(raw.hashCode());
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
