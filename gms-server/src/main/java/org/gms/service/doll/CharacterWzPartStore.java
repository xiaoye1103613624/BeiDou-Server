package org.gms.service.doll;

import lombok.extern.slf4j.Slf4j;
import org.gms.provider.wz.WZFiles;
import org.gms.server.cashshop.CashShopAssetCheck;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人偶部位数据仓库：读服务端 {@code wz(-zh-CN)/Character.wz} 的结构 XML，产出某一姿势帧的部位清单。
 * <p>
 * <b>为什么只读结构不读像素</b>：仓库 XML 里 {@code <canvas>} 没有像素数据，只有
 * {@code origin}/{@code width}/{@code height}/{@code z}/{@code map} 等结构信息；像素由
 * {@code PoseFrameExtractService.ensureCharacterPartFrame} 从客户端 {@code .img} 抽取。
 * 二者通过 {@link DollPart#nodePath()} 对齐（DumpPoseFrame 用的是同一套斜杠路径）。
 * <p>
 * 节点语义（与客户端绘制一致）：
 * <ul>
 *   <li>{@code <canvas name="X"><vector name="origin" x y/>}：X 部件的画布尺寸 + 自身原点在画布内的位置。</li>
 *   <li>{@code <imgdir name="map">}：该部件提供的锚点，<b>相对自身 origin</b>（如 neck/navel/brow/hand）。</li>
 *   <li>{@code <uol name="X" value="../../default/hair"/>}：节点引用，需按相对路径解析后再取真实 canvas。</li>
 *   <li>动画子层（如 {@code hairShade/0..11}）：按帧序号取子 canvas。</li>
 * </ul>
 */
@Slf4j
@Component
public class CharacterWzPartStore {
    public static final int DEFAULT_SKIN_ID = 2000;
    public static final int DEFAULT_HAIR_ID = 30000;
    public static final int DEFAULT_FACE_ID = 20000;
    public static final String DEFAULT_POSE = "sit";

    /** uol 递归解析深度上限，防引用成环。 */
    private static final int MAX_UOL_DEPTH = 8;
    /**
     * 非绘制节点的名字，收集时跳过。
     * <p>
     * 注意不要塞画布名（如 {@code face}/{@code body} 是真实画布）：{@code <int name="delay">}
     * 这类属性节点由 collectNode 的 tag 分支天然忽略，此处只需拦 info。
     */
    private static final Set<String> SKIP_NAMES = Set.of("info");
    /** 姿势缺失时的兜底节点（头底/发型/脸型等常见静态层）。 */
    private static final List<String> POSE_FALLBACKS = List.of("front", "default", "backDefault");

    /**
     * 部位来源，决定合成时的放置顺序：
     * 锚点必须先由提供方建立，消费方才能对齐（body 提供 neck/navel → head 提供 brow → 其余部件）。
     */
    public enum SourceKind {
        /** {@code 0000{skin}.img} 身体/四肢，提供身体原点与 neck/navel。 */
        BODY(0),
        /** {@code 0001{skin}.img} 头底，提供 brow。 */
        HEAD(1),
        /** 脸型/发型/装备等穿搭部件。 */
        WEAR(2);

        public final int order;

        SourceKind(int order) {
            this.order = order;
        }
    }

    /**
     * 一个可绘制部位。
     *
     * @param nodePath 相对 .img 根的节点路径（像素抽取与缓存键，如 {@code sit/0/body}）
     * @param partName 画布名（如 {@code body}/{@code arm}/{@code mail}/{@code hair}）
     * @param z 层序名，最终按 {@code Base.wz/zmap.img.xml} 排序
     * @param originX 自身原点在画布内的 x（客户端：精灵左上 = attach − origin）
     * @param anchors map 锚点：name → {x, y}，<b>相对自身 origin</b>
     * @param ownerId 该部位所属 IMG 的 ID（皮肤号或 itemId），用于定位客户端 .img
     */
    public record DollPart(
            SourceKind sourceKind,
            int ownerId,
            String nodePath,
            String partName,
            String z,
            int originX,
            int originY,
            int width,
            int height,
            Map<String, int[]> anchors
    ) {
        /**
         * 部位唯一键：不同 IMG 里可能存在同名节点路径（如两件外套都有 {@code sit/0/mail}），
         * 像素映射与缓存必须带上来源部位 ID。
         */
        public String key() {
            return sourceKind.name() + "/" + ownerId + "/" + nodePath;
        }
    }

    private volatile Map<String, Integer> zRankCache;

    // ============================== 路径：服务端 XML / 客户端 img ==============================

    public Path characterBaseDir() {
        return WZFiles.CHARACTER.getBaseFile();
    }

    public Path characterLanguageDir() {
        return WZFiles.CHARACTER.getLanguageFile();
    }

    private boolean languageExists() {
        Path lang = characterLanguageDir();
        return Files.isDirectory(lang) && !lang.equals(characterBaseDir());
    }

    /** {@code Character.wz} 内的相对 XML 路径（含 {@code .img.xml} 后缀）。 */
    public Optional<Path> resolveCharacterXml(String relativeInCharacterWz) {
        return resolveInRoots("Character.wz/" + relativeInCharacterWz);
    }

    /** wz 根下的相对路径，如 {@code Base.wz/zmap.img.xml}。语言包优先，缺失回落基础包。 */
    public Optional<Path> resolveRootXml(String relativeFromWzRoot) {
        return resolveInRoots(relativeFromWzRoot);
    }

    private Optional<Path> resolveInRoots(String relative) {
        List<Path> roots = new ArrayList<>();
        if (languageExists()) {
            roots.add(characterLanguageDir().getParent());
        }
        roots.add(characterBaseDir().getParent());
        for (Path root : roots) {
            if (root == null) {
                continue;
            }
            Path p = root.resolve(relative);
            if (Files.isRegularFile(p)) {
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    /** 身体/四肢：{@code 0000{skinId}.img.xml}（如 skin=2000 → 00002000）。 */
    public static String bodyImgName(int skinId) {
        return String.format(Locale.ROOT, "%08d", skinId);
    }

    /** 头底：{@code 0001{skinId}.img.xml}（skin=2000 → 00012000）。 */
    public static String headImgName(int skinId) {
        return String.format(Locale.ROOT, "%08d", skinId + 10000);
    }

    public static String bodyRelativeXml(int skinId) {
        return bodyImgName(skinId) + ".img.xml";
    }

    public static String headRelativeXml(int skinId) {
        return headImgName(skinId) + ".img.xml";
    }

    /** 身体/四肢对应的客户端 .img（相对 Data 根）。 */
    public static String bodyClientImg(int skinId) {
        return "Character/" + bodyImgName(skinId) + ".img";
    }

    /** 头底对应的客户端 .img。 */
    public static String headClientImg(int skinId) {
        return "Character/" + headImgName(skinId) + ".img";
    }

    /**
     * 穿搭部件（脸型/发型/装备）所在子目录，复用商城既有映射：
     * 3/4/6 → Hair，2/5 → Face，其余按 {@link CashShopAssetCheck#categoryFolderPublic(int)}。
     */
    public static String wearFolder(int itemId) {
        int type = itemId / 10000;
        if (type == 3 || type == 4 || type == 6) {
            return "Hair";
        }
        if (type == 2 || type == 5) {
            return "Face";
        }
        return CashShopAssetCheck.categoryFolderPublic(type);
    }

    public static String wearRelativeXml(int itemId) {
        return wearFolder(itemId) + "/" + String.format(Locale.ROOT, "%08d.img.xml", itemId);
    }

    public static String wearClientImg(int itemId) {
        return "Character/" + wearFolder(itemId) + "/" + String.format(Locale.ROOT, "%08d.img", itemId);
    }

    /** 按来源取该部位所属文件的相对 XML 路径。 */
    public static String relativeXmlOf(SourceKind kind, int ownerId) {
        return switch (kind) {
            case BODY -> bodyRelativeXml(ownerId);
            case HEAD -> headRelativeXml(ownerId);
            case WEAR -> wearRelativeXml(ownerId);
        };
    }

    /** 按来源取对应客户端 .img 的相对路径（拼在 Data 根之后）。 */
    public static String clientImgOf(SourceKind kind, int ownerId) {
        return switch (kind) {
            case BODY -> bodyClientImg(ownerId);
            case HEAD -> headClientImg(ownerId);
            case WEAR -> wearClientImg(ownerId);
        };
    }

    // ============================== 解析：部位清单 ==============================

    private final Map<Path, Optional<Document>> docCache = new ConcurrentHashMap<>();

    public Optional<Document> xmlDocument(Path xml) {
        return docCache.computeIfAbsent(xml.toAbsolutePath().normalize(), this::parseQuiet);
    }

    private Optional<Document> parseQuiet(Path path) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            try (InputStream in = Files.newInputStream(path)) {
                Document doc = builder.parse(in);
                return Optional.ofNullable(doc);
            }
        } catch (Exception e) {
            // 日志文案用 SLF4J `{}`，参数交给 logger，避免 MessageFormat 解析空 `{}`
            log.warn(I18nUtil.getLogMessage("CharacterDoll.partStore.parseFailed"), path, e.toString());
            return Optional.empty();
        }
    }

    /**
     * 收集某部件文件在指定姿势帧的全部可绘制部位（含 uol 解析与动画子层取帧）。
     *
     * @return 部位清单；文件缺失或姿势不存在时为空列表（调用方需降级）
     */
    public List<DollPart> collectParts(SourceKind kind, int ownerId, String pose, int frameIndex) {
        Optional<Path> xml = resolveCharacterXml(relativeXmlOf(kind, ownerId));
        if (xml.isEmpty()) {
            log.debug("doll part xml missing: {}", relativeXmlOf(kind, ownerId));
            return List.of();
        }
        Optional<Document> doc = xmlDocument(xml.get());
        if (doc.isEmpty() || doc.get().getDocumentElement() == null) {
            return List.of();
        }
        return collectFrom(doc.get().getDocumentElement(), kind, ownerId, pose, frameIndex);
    }

    /** 纯静态解析入口（可单测），从根 {@code <imgdir>} 收集部位。 */
    static List<DollPart> collectFrom(Element root, SourceKind kind, int ownerId, String pose, int frameIndex) {
        String framePath = resolveFramePath(root, pose, frameIndex);
        if (framePath == null) {
            return List.of();
        }
        Element frameNode = findNode(root, framePath);
        if (frameNode == null) {
            return List.of();
        }
        List<DollPart> out = new ArrayList<>();
        int frame = Math.max(frameIndex, 0);
        for (Element child : childElements(frameNode)) {
            collectNode(out, root, kind, ownerId, framePath, child, frame, 0);
        }
        return out;
    }

    /**
     * 递归收集节点：
     * canvas 直接成部位；uol 先按相对路径解析再递归；imgdir 视为动画层，按帧序号取子节点。
     */
    private static void collectNode(List<DollPart> out, Element root, SourceKind kind, int ownerId,
                                    String parentPath, Element node, int frameIndex, int depth) {
        if (depth > MAX_UOL_DEPTH || node == null) {
            return;
        }
        String nodeName = attributeOf(node, "name");
        if (nodeName == null || nodeName.isBlank() || SKIP_NAMES.contains(nodeName)) {
            return;
        }
        String ownPath = parentPath + "/" + nodeName;
        switch (node.getTagName()) {
            case "canvas" -> {
                DollPart part = toPart(kind, ownerId, ownPath, nodeName, node);
                if (part != null) {
                    out.add(part);
                }
            }
            case "uol" -> {
                String value = attributeOf(node, "value");
                if (value == null || value.isBlank()) {
                    return;
                }
                String target = resolveRelativePath(parentPath, value);
                Element targetEl = findNode(root, target);
                if (targetEl == null) {
                    return;
                }
                String targetParent = parentOfPath(target);
                collectNode(out, root, kind, ownerId, targetParent, targetEl, frameIndex, depth + 1);
            }
            case "imgdir" -> {
                Element picked = pickFrameChild(node, frameIndex);
                if (picked != null) {
                    collectNode(out, root, kind, ownerId, ownPath, picked, frameIndex, depth + 1);
                }
            }
            default -> {
                // int/short/string 等属性型节点：不参与绘制
            }
        }
    }

    /** 动画层取帧：优先同名帧号，其次首个可用子节点。 */
    private static Element pickFrameChild(Element group, int frameIndex) {
        Element byFrame = childElements(group).stream()
                .filter(e -> String.valueOf(frameIndex).equals(attributeOf(e, "name")))
                .findFirst()
                .orElse(null);
        if (byFrame != null) {
            return byFrame;
        }
        return childElements(group).stream()
                .filter(e -> !SKIP_NAMES.contains(String.valueOf(attributeOf(e, "name"))))
                .findFirst()
                .orElse(null);
    }

    private static DollPart toPart(SourceKind kind, int ownerId, String nodePath, String partName, Element canvas) {
        int width = attributeInt(canvas, "width", 0);
        int height = attributeInt(canvas, "height", 0);
        if (width <= 0 || height <= 0) {
            return null;
        }
        int[] origin = childVector(canvas, "origin");
        String z = childString(canvas, "z");
        return new DollPart(kind, ownerId, nodePath, partName, z,
                origin[0], origin[1], width, height, readAnchors(canvas));
    }

    /** map imgdir 下所有 vector：相对自身 origin 的锚点。 */
    private static Map<String, int[]> readAnchors(Element canvas) {
        Element map = childElements(canvas).stream()
                .filter(e -> "map".equals(attributeOf(e, "name")) && "imgdir".equals(e.getTagName()))
                .findFirst()
                .orElse(null);
        if (map == null) {
            return Map.of();
        }
        Map<String, int[]> anchors = new LinkedHashMap<>();
        for (Element e : childElements(map)) {
            if (!"vector".equals(e.getTagName())) {
                continue;
            }
            String name = attributeOf(e, "name");
            if (name == null || name.isBlank()) {
                continue;
            }
            anchors.put(name, new int[]{attributeInt(e, "x", 0), attributeInt(e, "y", 0)});
        }
        return anchors;
    }

    // ============================== 层序：Base.wz/zmap ==============================

    /**
     * 层序字典：z 名 → 在 {@code Base.wz/zmap.img.xml} 中的下标。
     * <p>
     * zmap 自前往后列举，<b>下标越大越靠后</b>；绘制时按下标降序（先画深层），这样下标最小的最后画、位于最前。
     */
    public Map<String, Integer> zRank() {
        Map<String, Integer> cached = zRankCache;
        if (cached != null) {
            return cached;
        }
        Map<String, Integer> rank = new LinkedHashMap<>();
        Optional<Path> xml = resolveRootXml("Base.wz/zmap.img.xml");
        if (xml.isPresent()) {
            Optional<Document> doc = xmlDocument(xml.get());
            if (doc.isPresent() && doc.get().getDocumentElement() != null) {
                int index = 0;
                for (Element e : childElements(doc.get().getDocumentElement())) {
                    String name = attributeOf(e, "name");
                    if (name != null && !name.isBlank()) {
                        rank.putIfAbsent(name, index);
                    }
                    index++;
                }
            }
        } else {
            log.warn(I18nUtil.getLogMessage("CharacterDoll.partStore.zmapMissing"));
        }
        zRankCache = Collections.unmodifiableMap(rank.isEmpty() ? new LinkedHashMap<>() : rank);
        return zRankCache;
    }

    // ============================== DOM 工具 ==============================

    static List<Element> childElements(Element parent) {
        List<Element> result = new ArrayList<>();
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element el) {
                result.add(el);
            }
        }
        return result;
    }

    static Element childByName(Element parent, String name) {
        return childElements(parent).stream()
                .filter(e -> Objects.equals(name, attributeOf(e, "name")))
                .findFirst()
                .orElse(null);
    }

    /** 斜杠路径下钻查找（相对 .img 根）；找不到返回 null。 */
    static Element findNode(Element root, String nodePath) {
        if (nodePath == null || nodePath.isBlank()) {
            return null;
        }
        Element current = root;
        for (String segment : nodePath.split("/")) {
            if (segment.isEmpty()) {
                continue;
            }
            current = childByName(current, segment);
            if (current == null) {
                return null;
            }
        }
        return current;
    }

    /**
     * 解析 uol 的相对路径。
     *
     * @param currentPath uol 节点自身路径（如 {@code sit/0/hair}）
     * @param relative 形如 {@code ../../default/hair}
     * @return 解析后的绝对路径（如 {@code default/hair}）
     */
    static String resolveRelativePath(String currentPath, String relative) {
        // currentPath 已是 uol 所在「目录」（如帧节点 sit/0），不再额外弹出一层
        Deque<String> dir = new ArrayDeque<>(Arrays.asList(currentPath.split("/")));
        for (String segment : relative.split("/")) {
            if (segment.isEmpty() || ".".equals(segment)) {
                continue;
            }
            if ("..".equals(segment)) {
                dir.removeLast();
            } else {
                dir.addLast(segment);
            }
        }
        return String.join("/", dir);
    }

    /** 取路径的父级：{@code default/hairShade/0} → {@code default/hairShade}。 */
    static String parentOfPath(String path) {
        int slash = path.lastIndexOf('/');
        return slash < 0 ? "" : path.substring(0, slash);
    }

    /**
     * 定位某姿势的帧节点路径：优先 {@code pose/frame}（帧号不匹配时取该姿势首帧），
     * 姿势缺失时按 front/default 静态层兜底。
     */
    static String resolveFramePath(Element root, String pose, int frameIndex) {
        if (pose != null && !pose.isBlank()) {
            Element poseNode = childByName(root, pose.trim());
            if (poseNode != null) {
                String wanted = String.valueOf(Math.max(frameIndex, 0));
                Element frameNode = childByName(poseNode, wanted);
                if (frameNode == null) {
                    frameNode = childElements(poseNode).stream()
                            .filter(e -> !SKIP_NAMES.contains(String.valueOf(attributeOf(e, "name"))))
                            .findFirst()
                            .orElse(null);
                }
                return frameNode == null ? null : pose.trim() + "/" + attributeOf(frameNode, "name");
            }
        }
        for (String fallback : POSE_FALLBACKS) {
            if (childByName(root, fallback) != null) {
                return fallback;
            }
        }
        return childElements(root).stream()
                .map(e -> attributeOf(e, "name"))
                .filter(name -> name != null && !SKIP_NAMES.contains(name))
                .map(String::valueOf)
                .findFirst()
                .orElse(null);
    }

    static String attributeOf(Element el, String name) {
        return el.hasAttribute(name) ? el.getAttribute(name) : null;
    }

    static int attributeInt(Element el, String name, int fallback) {
        String raw = attributeOf(el, name);
        if (raw == null) {
            return fallback;
        }
        try {
            return Integer.parseInt(raw.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    /** 取直接子 vector（如 origin）。不存在返回 {0,0}。 */
    static int[] childVector(Element parent, String name) {
        Element vector = childElements(parent).stream()
                .filter(e -> "vector".equals(e.getTagName()) && Objects.equals(name, attributeOf(e, "name")))
                .findFirst()
                .orElse(null);
        if (vector == null) {
            return new int[]{0, 0};
        }
        return new int[]{attributeInt(vector, "x", 0), attributeInt(vector, "y", 0)};
    }

    /** 取直接子 string（如 z）。不存在返回 null。 */
    private static String childString(Element parent, String name) {
        Element s = childElements(parent).stream()
                .filter(e -> "string".equals(e.getTagName()) && Objects.equals(name, attributeOf(e, "name")))
                .findFirst()
                .orElse(null);
        return s == null ? null : attributeOf(s, "value");
    }
}
