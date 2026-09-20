package org.gms.service.doll;

import org.gms.service.doll.CharacterWzPartStore.DollPart;
import org.gms.service.doll.CharacterWzPartStore.SourceKind;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 人偶合成器：把 {@link CharacterWzPartStore} 给出的部位按真实 WZ 坐标合成一张透明 PNG。
 * <p>
 * <b>坐标系</b>：以 body 的 {@code origin} 为人偶空间原点 O(0,0)（客户端同款 “精灵左上 = attach − origin”）。
 * 每个部位的 map 锚点相对自身 origin，绘制时把锚点与目标锚点重合：
 * <pre>
 *   partOrigin + partAnchor == targetAnchor  →  partOrigin = targetAnchor − partAnchor
 *   left = partOriginX − part.originX        top = partOriginY − part.originY
 * </pre>
 * body 先落位并发布 neck/navel；head 对 neck 落位并发布 brow；发型/脸型/装备再对 brow/neck/navel 落位。
 * <p>
 * <b>层序</b>：取 {@code Base.wz/zmap.img.xml} 的下标，<b>下标越大越靠后</b>，绘制时倒序（先画深层）。
 */
public final class DollCompositor {
    /**
     * 锚点优先级：同时命中多个可用锚点时取前者。
     * 数据里身体件给 navel，头底给 neck/brow，穿搭件多用 brow/navel，覆盖实际样本即可。
     */
    private static final List<String> ANCHOR_PRIORITY = List.of("brow", "neck", "navel", "hand");

    private DollCompositor() {
    }

    /** 一个已定位部位：origin 为该部件原点在人偶空间的坐标，left/top 为画布左上角在该空间的坐标。 */
    public record Placement(DollPart part, int originX, int originY, int left, int top) {
    }

    /**
     * 合成布局：几何结果与画布尺寸、锚点。像素未就绪也可先算出来（用于缺图时仍给出正确坐标）。
     *
     * @param minX/minY 包围盒左上角在人偶空间的坐标（left/top 的最小值）
     * @param bodyOriginX/Y 身体原点相对合成 PNG 左上角（座椅 attach 点）
     * @param navelX/Y 肚脐锚点相对合成 PNG 左上角（坐骑挂载点），无 navel 时为 null
     */
    public record Layout(
            List<Placement> placements,
            List<DollPart> unresolved,
            int minX,
            int minY,
            int width,
            int height,
            int bodyOriginX,
            int bodyOriginY,
            Integer navelX,
            Integer navelY
    ) {
    }

    /**
     * 依据结构数据计算几何布局（不含像素）。
     *
     * @return 无可用种子部位（body）时为空
     */
    public static Optional<Layout> layout(List<DollPart> parts) {
        if (parts == null || parts.isEmpty()) {
            return Optional.empty();
        }
        List<DollPart> ordered = new ArrayList<>(parts);
        ordered.sort(Comparator.comparingInt((DollPart p) -> p.sourceKind().order)
                .thenComparing(p -> p.partName() == null ? "" : p.partName()));

        DollPart seed = ordered.stream()
                .filter(DollCompositor::isBodySeed)
                .findFirst()
                .orElse(ordered.get(0));

        Map<String, int[]> anchors = new HashMap<>();
        List<Placement> placements = new ArrayList<>();
        List<DollPart> unresolved = new ArrayList<>();

        place(seed, 0, 0, anchors, placements);
        for (DollPart part : ordered) {
            if (part == seed) {
                continue;
            }
            String anchorName = firstCommonAnchor(part, anchors);
            if (anchorName == null) {
                if (part.anchors().isEmpty()) {
                    // 无 map 的部件：把自己的 origin 直接对齐人偶原点（保底不丢件）
                    place(part, 0, 0, anchors, placements);
                } else {
                    unresolved.add(part);
                }
                continue;
            }
            int[] target = anchors.get(anchorName);
            int[] own = part.anchors().get(anchorName);
            place(part, target[0] - own[0], target[1] - own[1], anchors, placements);
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Placement pl : placements) {
            minX = Math.min(minX, pl.left());
            minY = Math.min(minY, pl.top());
            maxX = Math.max(maxX, pl.left() + pl.part().width());
            maxY = Math.max(maxY, pl.top() + pl.part().height());
        }
        int[] navel = anchors.get("navel");
        return Optional.of(new Layout(
                placements,
                unresolved,
                minX,
                minY,
                Math.max(maxX - minX, 1),
                Math.max(maxY - minY, 1),
                -minX,
                -minY,
                navel == null ? null : navel[0] - minX,
                navel == null ? null : navel[1] - minY
        ));
    }

    /**
     * 渲染 PNG（WZ 1:1，不含任何缩放）。缺像素的部位直接跳过。
     *
     * @param pixels nodePath → 部位像素
     * @param zRank z 名 → zmap 下标
     */
    public static byte[] renderPng(Layout layout, Map<String, BufferedImage> pixels, Map<String, Integer> zRank)
            throws IOException {
        BufferedImage canvas = new BufferedImage(
                Math.max(layout.width(), 1), Math.max(layout.height(), 1), BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = canvas.createGraphics();
        try {
            for (Placement pl : drawOrder(layout, zRank)) {
                BufferedImage img = pixels.get(pl.part().key());
                if (img == null) {
                    continue;
                }
                graphics.drawImage(img, pl.left() - layout.minX(), pl.top() - layout.minY(), null);
            }
        } finally {
            graphics.dispose();
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ImageIO.write(canvas, "png", bos);
        return bos.toByteArray();
    }

    /** 绘制顺序：深层在前。 */
    public static List<Placement> drawOrder(Layout layout, Map<String, Integer> zRank) {
        List<Placement> ordered = new ArrayList<>(layout.placements());
        ordered.sort(Comparator.comparingInt((Placement pl) -> rankOf(pl.part(), zRank)).reversed());
        return ordered;
    }

    /** 未知 z 视为最深层（下标最大），先绘制，避免遮挡已正确排序的部位。 */
    static int rankOf(DollPart part, Map<String, Integer> zRank) {
        Integer rank = part.z() == null ? null : zRank.get(part.z());
        if (rank == null && part.partName() != null) {
            rank = zRank.get(part.partName());
        }
        return rank == null ? Integer.MAX_VALUE - 1 : rank;
    }

    private static boolean isBodySeed(DollPart part) {
        return part.sourceKind() == SourceKind.BODY
                && ("body".equals(part.partName()) || "body".equals(part.z()));
    }

    private static void place(DollPart part, int originX, int originY,
                              Map<String, int[]> anchors, List<Placement> placements) {
        placements.add(new Placement(part, originX, originY, originX - part.originX(), originY - part.originY()));
        // 首次发布者为准：body 的 neck/navel、head 的 brow 才是挂载基准
        part.anchors().forEach((name, vec) ->
                anchors.putIfAbsent(name, new int[]{originX + vec[0], originY + vec[1]}));
    }

    private static String firstCommonAnchor(DollPart part, Map<String, int[]> anchors) {
        for (String name : ANCHOR_PRIORITY) {
            if (anchors.containsKey(name) && part.anchors().containsKey(name)) {
                return name;
            }
        }
        return null;
    }
}
