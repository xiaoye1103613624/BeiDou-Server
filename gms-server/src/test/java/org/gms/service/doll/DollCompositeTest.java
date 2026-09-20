package org.gms.service.doll;

import org.gms.service.doll.CharacterWzPartStore.DollPart;
import org.gms.service.doll.CharacterWzPartStore.SourceKind;
import org.gms.service.doll.DollCompositor.Layout;
import org.gms.service.doll.DollCompositor.Placement;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 人偶合成回归测试。
 * <p>
 * 重点是<b>坐标与几何</b>——这正是原先 CDN 整体人偶偏上/偏下、比例失真的根因：
 * 现在每一步都能用真实 WZ 数值校验（工作目录需为 gms-server，与其它测试一致）。
 */
class DollCompositeTest {

    /** 00002000.img sit/0 的真实数值（见 wz/Character.wz）。 */
    private static final DollPart REAL_BODY = new DollPart(SourceKind.BODY, 2000, "sit/0/body", "body", "body",
            19, 28, 25, 25, Map.of("neck", new int[]{-1, -28}, "navel", new int[]{-2, -17}));
    private static final DollPart REAL_ARM = new DollPart(SourceKind.BODY, 2000, "sit/0/arm", "arm", "arm",
            5, 7, 11, 15, Map.of("navel", new int[]{-4, 2}, "hand", new int[]{-1, -7}));
    /** 00012000.img front/head 的真实数值。 */
    private static final DollPart REAL_HEAD = new DollPart(SourceKind.HEAD, 12000, "front/head", "head", "head",
            19, 17, 39, 35, Map.of("neck", new int[]{0, 15}, "brow", new int[]{-4, -5}));

    @Test
    void layout_bodyArmHead_matchesWzCoordinateMath() {
        Layout layout = DollCompositor.layout(List.of(REAL_BODY, REAL_ARM, REAL_HEAD)).orElseThrow();

        // body/arm/head 的精灵左上角（人偶空间，body origin = 0,0）
        assertArrayEquals(new int[]{-19, -28}, topLeftOf(layout, "sit/0/body"));
        assertArrayEquals(new int[]{-3, -26}, topLeftOf(layout, "sit/0/arm"));
        assertArrayEquals(new int[]{-20, -60}, topLeftOf(layout, "front/head"));

        // 包围盒 → 画布尺寸（WZ 1:1，不做任何缩放）
        assertEquals(39, layout.width());
        assertEquals(57, layout.height());
        // 身体原点/肚脐锚点相对 PNG 左上角
        assertEquals(20, layout.bodyOriginX());
        assertEquals(60, layout.bodyOriginY());
        assertEquals(18, layout.navelX());
        assertEquals(43, layout.navelY());
    }

    @Test
    void layout_seedsFromBodyEvenWhenUnordered() {
        Layout layout = DollCompositor.layout(List.of(REAL_HEAD, REAL_ARM, REAL_BODY)).orElseThrow();
        assertArrayEquals(new int[]{-19, -28}, topLeftOf(layout, "sit/0/body"));
        assertEquals(List.of(), layout.unresolved(), "所有部件都应能解析出锚点");
    }

    @Test
    void drawOrder_paintsDeeperLayerFirst() {
        Layout layout = DollCompositor.layout(List.of(REAL_BODY, REAL_ARM, REAL_HEAD)).orElseThrow();
        // zmap 下标越大越靠后：body(79) → arm(54) → head(48)
        Map<String, Integer> zRank = Map.of("body", 79, "arm", 54, "head", 48);
        List<Placement> ordered = DollCompositor.drawOrder(layout, zRank);
        assertEquals(List.of("body", "arm", "head"),
                ordered.stream().map(pl -> pl.part().partName()).toList());
    }

    @Test
    void collectFrom_headSitPose_resolvesUolToFrontHead() throws Exception {
        Element root = parseRoot(Path.of("wz", "Character.wz", "00012000.img.xml"));
        List<DollPart> parts = CharacterWzPartStore.collectFrom(root, SourceKind.HEAD, 12000, "sit", 0);

        assertEquals(1, parts.size());
        DollPart head = parts.get(0);
        assertEquals("front/head", head.nodePath(), "uol ../../front/head 必须解析到真实节点");
        assertEquals("head", head.z());
        assertEquals(39, head.width());
        assertEquals(35, head.height());
        assertEquals(19, head.originX());
        assertEquals(17, head.originY());
        assertEquals(-5, head.anchors().get("brow")[1]);
    }

    @Test
    void collectFrom_bodySitPose_readsOriginAndNavel() throws Exception {
        Element root = parseRoot(Path.of("wz", "Character.wz", "00002000.img.xml"));
        List<DollPart> parts = CharacterWzPartStore.collectFrom(root, SourceKind.BODY, 2000, "sit", 0);

        Optional<DollPart> body = parts.stream().filter(p -> "body".equals(p.partName())).findFirst();
        assertTrue(body.isPresent());
        assertEquals("sit/0/body", body.get().nodePath());
        assertEquals(25, body.get().width());
        assertEquals(-2, body.get().anchors().get("navel")[0]);
        assertEquals(-17, body.get().anchors().get("navel")[1]);
        assertTrue(parts.stream().anyMatch(p -> "arm".equals(p.partName())), "sit/0 还应含 arm");
    }

    @Test
    void collectFrom_hairSitPose_expandsUolAndAnimationGroup() throws Exception {
        Path hair = Path.of("wz", "Character.wz", "Hair", "00030000.img.xml");
        Assumptions.assumeTrue(Files.isRegularFile(hair), "发型 XML 缺失则跳过");
        List<DollPart> parts = CharacterWzPartStore.collectFrom(parseRoot(hair), SourceKind.WEAR, 30000, "sit", 0);

        List<String> nodePaths = parts.stream().map(DollPart::nodePath).toList();
        assertTrue(nodePaths.contains("default/hairOverHead"), "uol 应解析到 default/hairOverHead");
        assertTrue(nodePaths.contains("default/hair"));
        assertTrue(nodePaths.contains("default/hairShade/0"), "动画层应按帧号取子 canvas");
    }

    @Test
    void layout_defaultLook_hasNavelAnchorInsideCanvas() throws Exception {
        Path body = Path.of("wz", "Character.wz", "00002000.img.xml");
        Path head = Path.of("wz", "Character.wz", "00012000.img.xml");
        Assumptions.assumeTrue(Files.isRegularFile(body) && Files.isRegularFile(head), "WZ 缺失则跳过");

        List<DollPart> parts = new java.util.ArrayList<>();
        parts.addAll(CharacterWzPartStore.collectFrom(parseRoot(body), SourceKind.BODY, 2000, "sit", 0));
        parts.addAll(CharacterWzPartStore.collectFrom(parseRoot(head), SourceKind.HEAD, 12000, "sit", 0));
        Path hair = Path.of("wz", "Character.wz", "Hair", "00030000.img.xml");
        if (Files.isRegularFile(hair)) {
            parts.addAll(CharacterWzPartStore.collectFrom(parseRoot(hair), SourceKind.WEAR, 30000, "sit", 0));
        }

        Layout layout = DollCompositor.layout(parts).orElseThrow();
        assertFalse(layout.placements().isEmpty());
        assertNotNull(layout.navelX(), "必须给出肚脐锚点（坐骑挂载点）");
        assertTrue(layout.width() >= 25 && layout.height() >= 50, "外观围太小说明合成退化：" + layout.width() + "x" + layout.height());

        // 所有落位精灵必须完整落在画布内（WZ 1:1，无缩放、无裁切）
        for (Placement pl : layout.placements()) {
            assertTrue(pl.left() - layout.minX() >= 0 && pl.top() - layout.minY() >= 0, "精灵溢出画布左上");
            assertTrue(pl.left() - layout.minX() + pl.part().width() <= layout.width(), "精灵溢出画布右边");
            assertTrue(pl.top() - layout.minY() + pl.part().height() <= layout.height(), "精灵溢出画布底边");
        }
        // 锚点（origin/navel）可能落在画布之外，这是 WZ 的正常语义，但必须在合理范围内
        assertTrue(layout.bodyOriginX() >= 0 && layout.bodyOriginX() <= layout.width());
        assertTrue(layout.navelX() >= 0 && layout.navelX() <= layout.width(), "肚脐锚点：" + layout.navelX());
        assertTrue(layout.navelY() > 0, "肚脐锚点：" + layout.navelY());
    }

    @Test
    void resolveRelativePath_handlesParentDirs() {
        // 第一个参数是 uol 所在目录（帧节点路径），不是 uol 自身路径
        assertEquals("default/hair", CharacterWzPartStore.resolveRelativePath("sit/0", "../../default/hair"));
        assertEquals("front/head", CharacterWzPartStore.resolveRelativePath("sit/0", "../../front/head"));
        assertEquals("default/hairShade/0", CharacterWzPartStore.resolveRelativePath("default/hairShade", "0"));
    }

    private static int[] topLeftOf(Layout layout, String nodePath) {
        return layout.placements().stream()
                .filter(pl -> nodePath.equals(pl.part().nodePath()))
                .map(pl -> new int[]{pl.left(), pl.top()})
                .findFirst()
                .orElseThrow();
    }

    private static Element parseRoot(Path xml) throws Exception {
        assertTrue(Files.isRegularFile(xml), "缺少 WZ 文件: " + xml);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        Document doc = factory.newDocumentBuilder().parse(xml.toFile());
        return doc.getDocumentElement();
    }
}
