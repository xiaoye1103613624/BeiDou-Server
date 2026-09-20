package org.gms.service.chair;

import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.model.dto.TamingMobPoseFrameDTO;
import org.gms.model.dto.TamingMobPoseWriteReqDTO;
import org.gms.provider.wz.WZFiles;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * 骑宠视觉锚点：{@code Character.wz/TamingMob/*.img.xml} 双写 base / 语言包。
 * <p>
 * 只改 canvas 下 {@code map/navel} 与必要 {@code origin}/{@code z}；不触碰 {@code TamingMob.wz} 移动属性。
 */
@Slf4j
@Component
public class TamingMobPoseWzXmlStore {
    public static final String TAMING_DIR = "TamingMob";
    public static final String STRING_EQP_FILE = "Eqp.img.xml";
    public static final String DEFAULT_ACTION = "stand1";

    private static final Set<String> META_DIRS = Set.of("info");

    public Path characterBaseDir() {
        return WZFiles.CHARACTER.getBaseFile();
    }

    public Path characterLanguageDir() {
        return WZFiles.CHARACTER.getLanguageFile();
    }

    public Path stringBaseDir() {
        return WZFiles.STRING.getBaseFile();
    }

    public Path stringLanguageDir() {
        return WZFiles.STRING.getLanguageFile();
    }

    public boolean characterLanguageExists() {
        Path lang = characterLanguageDir();
        return Files.isDirectory(lang) && !lang.equals(characterBaseDir());
    }

    public boolean stringLanguageExists() {
        Path lang = stringLanguageDir();
        return Files.isDirectory(lang) && !lang.equals(stringBaseDir());
    }

    public Path tamingRelative(int mobId) {
        return Path.of(TAMING_DIR, imgFileName(mobId));
    }

    public static String imgFileName(int mobId) {
        return String.format(Locale.ROOT, "%08d.img.xml", mobId);
    }

    public static Integer parseMobIdFromFileName(String fileName) {
        if (fileName == null || !fileName.endsWith(".img.xml")) {
            return null;
        }
        String stem = fileName.substring(0, fileName.length() - ".img.xml".length());
        try {
            return Integer.parseInt(stem);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Path resolveTamingXml(int mobId, boolean languagePreferred) {
        if (languagePreferred && characterLanguageExists()) {
            Path lang = characterLanguageDir().resolve(tamingRelative(mobId));
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        Path base = characterBaseDir().resolve(tamingRelative(mobId));
        if (Files.isRegularFile(base)) {
            return base;
        }
        if (characterLanguageExists()) {
            Path lang = characterLanguageDir().resolve(tamingRelative(mobId));
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        throw BizException.illegalArgument(
                I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.wz.missing", imgFileName(mobId)));
    }

    public Path resolveStringEqpXml(boolean languagePreferred) {
        if (languagePreferred && stringLanguageExists()) {
            Path lang = stringLanguageDir().resolve(STRING_EQP_FILE);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        Path base = stringBaseDir().resolve(STRING_EQP_FILE);
        if (Files.isRegularFile(base)) {
            return base;
        }
        if (stringLanguageExists()) {
            Path lang = stringLanguageDir().resolve(STRING_EQP_FILE);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        throw BizException.illegalArgument(
                I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.string.missing"));
    }

    public Document loadDocument(Path file) {
        if (!Files.isRegularFile(file)) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.wz.missing", file.toString()));
        }
        try (InputStream in = Files.newInputStream(file)) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(false);
            Document doc = factory.newDocumentBuilder().parse(in);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (BizException e) {
            throw e;
        } catch (Exception e) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.wz.parseFail", file.toString()));
        }
    }

    public void saveDocument(Path file, Document doc) {
        try {
            Files.createDirectories(file.getParent());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            try (OutputStream out = Files.newOutputStream(file)) {
                transformer.transform(new DOMSource(doc), new StreamResult(out));
            }
        } catch (Exception e) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.wz.writeFail", file.toString()));
        }
    }

    public Element childByName(Element parent, String name) {
        if (parent == null) {
            return null;
        }
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                Element el = (Element) n;
                if (name.equals(el.getAttribute("name"))) {
                    return el;
                }
            }
        }
        return null;
    }

    public List<Element> elementChildren(Element parent) {
        List<Element> list = new ArrayList<>();
        if (parent == null) {
            return list;
        }
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE) {
                list.add((Element) n);
            }
        }
        return list;
    }

    public static Integer parseIntAttr(String v) {
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(v.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 扫描 base（优先）与语言包目录中的骑宠 img 文件，按 mobId 去重。 */
    public List<Integer> listAllMobIds() {
        return listAllMobIdsCached();
    }

    private volatile List<Integer> mobIdListCache = List.of();
    private volatile long mobIdListFingerprint = Long.MIN_VALUE;

    /**
     * 缓存骑宠 ID 列表。语言包目录可达数千文件，每次 DirectoryStream 会让切 Tab/搜索卡死。
     */
    public List<Integer> listAllMobIdsCached() {
        long fingerprint = mobIdDirsFingerprint();
        List<Integer> cached = mobIdListCache;
        if (!cached.isEmpty() && mobIdListFingerprint == fingerprint) {
            return cached;
        }
        synchronized (this) {
            if (!mobIdListCache.isEmpty() && mobIdListFingerprint == fingerprint) {
                return mobIdListCache;
            }
            Set<Integer> ids = new TreeSet<>();
            collectMobIds(characterBaseDir().resolve(TAMING_DIR), ids);
            if (characterLanguageExists()) {
                collectMobIds(characterLanguageDir().resolve(TAMING_DIR), ids);
            }
            List<Integer> list = new ArrayList<>(ids);
            mobIdListCache = list;
            mobIdListFingerprint = fingerprint;
            return list;
        }
    }

    private long mobIdDirsFingerprint() {
        long fp = 0L;
        fp = mixDirFingerprint(fp, characterBaseDir().resolve(TAMING_DIR));
        if (characterLanguageExists()) {
            fp = mixDirFingerprint(fp, characterLanguageDir().resolve(TAMING_DIR));
        }
        return fp;
    }

    private long mixDirFingerprint(long fp, Path dir) {
        if (!Files.isDirectory(dir)) {
            return fp * 31;
        }
        try {
            long mtime = Files.getLastModifiedTime(dir).toMillis();
            long count;
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.img.xml")) {
                count = 0;
                for (Path ignored : stream) {
                    count++;
                }
            }
            return fp * 31 + mtime + count * 17;
        } catch (IOException e) {
            return fp * 31 - 1;
        }
    }

    private void collectMobIds(Path dir, Set<Integer> ids) {
        if (!Files.isDirectory(dir)) {
            return;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.img.xml")) {
            for (Path p : stream) {
                Integer id = parseMobIdFromFileName(p.getFileName().toString());
                if (id != null && id > 0) {
                    ids.add(id);
                }
            }
        } catch (IOException e) {
            log.warn(I18nUtil.getLogMessage("TamingMobPoseWzXmlStore.list.warn"), dir, e.getMessage());
        }
    }

    public Map<String, String> readEqpString(int mobId) {
        Map<String, String> out = new LinkedHashMap<>();
        Document doc = loadEqpDocumentCached();
        Element eqpRoot = childByName(doc.getDocumentElement(), "Eqp");
        Element taming = childByName(eqpRoot, "Taming");
        Element el = childByName(taming, String.valueOf(mobId));
        if (el == null) {
            return out;
        }
        for (Element child : elementChildren(el)) {
            if ("string".equals(child.getTagName())) {
                out.put(child.getAttribute("name"), child.getAttribute("value"));
            }
        }
        return out;
    }

    /**
     * 一次解析 Eqp.img.xml 的 Taming 段名称索引，避免列表对每个骑宠重复 parse（Eqp 约 800KB+）。
     */
    public Map<String, String> loadTamingNameIndex() {
        ensureEqpCache();
        return eqpTamingNameIndex;
    }

    private volatile Document eqpDocumentCache;
    private volatile Path eqpDocumentPath;
    private volatile long eqpDocumentMtime = Long.MIN_VALUE;
    private volatile Map<String, String> eqpTamingNameIndex = Map.of();

    private Document loadEqpDocumentCached() {
        ensureEqpCache();
        return eqpDocumentCache;
    }

    private synchronized void ensureEqpCache() {
        Path file = resolveStringEqpXml(true);
        long mtime;
        try {
            mtime = Files.getLastModifiedTime(file).toMillis();
        } catch (Exception e) {
            mtime = -1L;
        }
        if (eqpDocumentCache != null
                && Objects.equals(eqpDocumentPath, file)
                && eqpDocumentMtime == mtime) {
            return;
        }
        Document doc = loadDocument(file);
        Map<String, String> index = new LinkedHashMap<>();
        Element eqpRoot = childByName(doc.getDocumentElement(), "Eqp");
        Element taming = childByName(eqpRoot, "Taming");
        if (taming != null) {
            for (Element el : elementChildren(taming)) {
                if (!"imgdir".equals(el.getTagName())) {
                    continue;
                }
                String id = el.getAttribute("name");
                if (id == null || id.isBlank()) {
                    continue;
                }
                for (Element child : elementChildren(el)) {
                    if ("string".equals(child.getTagName()) && "name".equals(child.getAttribute("name"))) {
                        index.put(id, child.getAttribute("value"));
                        break;
                    }
                }
            }
        }
        eqpDocumentCache = doc;
        eqpDocumentPath = file;
        eqpDocumentMtime = mtime;
        eqpTamingNameIndex = index;
    }

    public List<String> listActions(Document doc) {
        List<String> actions = new ArrayList<>();
        for (Element child : elementChildren(doc.getDocumentElement())) {
            if (!"imgdir".equals(child.getTagName())) {
                continue;
            }
            String name = child.getAttribute("name");
            if (name == null || name.isBlank() || META_DIRS.contains(name)) {
                continue;
            }
            actions.add(name);
        }
        actions.sort(Comparator.naturalOrder());
        return actions;
    }

    /** 默认编辑动作：stand1 → 其它含 navel 的动作 → 首个动作。 */
    public String resolveDefaultAction(Document doc) {
        List<String> actions = listActions(doc);
        if (actions.contains(DEFAULT_ACTION) && actionHasNavel(doc, DEFAULT_ACTION)) {
            return DEFAULT_ACTION;
        }
        for (String action : actions) {
            if (actionHasNavel(doc, action)) {
                return action;
            }
        }
        if (actions.contains(DEFAULT_ACTION)) {
            return DEFAULT_ACTION;
        }
        return actions.isEmpty() ? DEFAULT_ACTION : actions.get(0);
    }

    public boolean actionHasNavel(Document doc, String action) {
        List<TamingMobPoseFrameDTO> frames = readFrames(doc, action);
        return frames.stream().anyMatch(f -> f.getNavelX() != null || f.getNavelY() != null);
    }

    /**
     * 列表用：扫描 DOM 是否出现 map/navel，避免整树 readFrames。
     */
    public boolean hasAnyNavelQuick(Document doc) {
        return hasNavelElement(doc.getDocumentElement());
    }

    private boolean hasNavelElement(Element el) {
        if (el == null) {
            return false;
        }
        if ("navel".equals(el.getAttribute("name")) && "vector".equals(el.getTagName())) {
            return true;
        }
        for (Element child : elementChildren(el)) {
            if (hasNavelElement(child)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAnyNavel(Document doc) {
        return hasAnyNavelQuick(doc);
    }

    public List<TamingMobPoseFrameDTO> readFrames(Document doc, String action) {
        List<TamingMobPoseFrameDTO> frames = new ArrayList<>();
        Element actionEl = childByName(doc.getDocumentElement(), action);
        if (actionEl == null) {
            return frames;
        }
        for (Element frameEl : elementChildren(actionEl)) {
            if (!"imgdir".equals(frameEl.getTagName())) {
                continue;
            }
            Integer frameIndex = parseIntAttr(frameEl.getAttribute("name"));
            if (frameIndex == null) {
                continue;
            }
            Element canvas = firstCanvas(frameEl);
            if (canvas == null) {
                continue;
            }
            Element map = childByName(canvas, "map");
            Element navel = childByName(map, "navel");
            Element origin = childByName(canvas, "origin");
            Element zEl = childByName(canvas, "z");
            String z = null;
            if (zEl != null) {
                z = zEl.hasAttribute("value") ? zEl.getAttribute("value") : null;
                if ((z == null || z.isBlank()) && "string".equals(zEl.getTagName())) {
                    z = zEl.getAttribute("value");
                }
            }
            frames.add(TamingMobPoseFrameDTO.builder()
                    .action(action)
                    .frameIndex(frameIndex)
                    .navelX(navel == null ? null : parseIntAttr(navel.getAttribute("x")))
                    .navelY(navel == null ? null : parseIntAttr(navel.getAttribute("y")))
                    .originX(origin == null ? null : parseIntAttr(origin.getAttribute("x")))
                    .originY(origin == null ? null : parseIntAttr(origin.getAttribute("y")))
                    .z(z)
                    .canvasWidth(parseIntAttr(canvas.getAttribute("width")))
                    .canvasHeight(parseIntAttr(canvas.getAttribute("height")))
                    .build());
        }
        frames.sort(Comparator.comparing(TamingMobPoseFrameDTO::getFrameIndex,
                Comparator.nullsLast(Integer::compareTo)));
        return frames;
    }

    public List<TamingMobPoseFrameDTO> readDefaultFrames(Document doc) {
        return readFrames(doc, resolveDefaultAction(doc));
    }

    /**
     * 双写 navel/origin/z。语言包缺文件时只写 base 并收集告警。
     */
    public List<String> writePose(TamingMobPoseWriteReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getMobId(),
                I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.mobId.required"));
        List<String> warnings = new ArrayList<>();
        Path base = characterBaseDir().resolve(tamingRelative(req.getMobId()));
        if (!Files.isRegularFile(base)) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.wz.missing", base.toString()));
        }
        applyWrite(base, req);
        if (characterLanguageExists()) {
            Path lang = characterLanguageDir().resolve(tamingRelative(req.getMobId()));
            if (Files.isRegularFile(lang)) {
                applyWrite(lang, req);
            } else {
                warnings.add(I18nUtil.getMessage("TamingMobPoseWzXmlStore.warn.langMissing", lang.toString()));
                log.warn(I18nUtil.getLogMessage("TamingMobPoseWzXmlStore.lang.missing.warn"), lang);
            }
        }
        log.info(I18nUtil.getLogMessage("TamingMobPoseWzXmlStore.write.info"), req.getMobId(), req.getWriteMode());
        return warnings;
    }

    private void applyWrite(Path file, TamingMobPoseWriteReqDTO req) {
        Document doc = loadDocument(file);
        String mode = req.getWriteMode() == null ? "currentFrame" : req.getWriteMode().trim();
        String action = req.getAction();
        if (action == null || action.isBlank()) {
            action = resolveDefaultAction(doc);
        }
        int frameIndex = req.getFrameIndex() == null ? 0 : req.getFrameIndex();

        switch (mode) {
            case "allActions" -> {
                for (String act : listActions(doc)) {
                    writeActionAllFrames(doc, act, req);
                }
            }
            case "action" -> writeActionAllFrames(doc, action, req);
            default -> writeOneFrame(doc, action, frameIndex, req);
        }
        saveDocument(file, doc);
    }

    private void writeActionAllFrames(Document doc, String action, TamingMobPoseWriteReqDTO req) {
        Element actionEl = childByName(doc.getDocumentElement(), action);
        if (actionEl == null) {
            return;
        }
        for (Element frameEl : elementChildren(actionEl)) {
            if (!"imgdir".equals(frameEl.getTagName())) {
                continue;
            }
            Integer idx = parseIntAttr(frameEl.getAttribute("name"));
            if (idx == null) {
                continue;
            }
            writeOneFrame(doc, action, idx, req);
        }
    }

    private void writeOneFrame(Document doc, String action, int frameIndex, TamingMobPoseWriteReqDTO req) {
        Element actionEl = childByName(doc.getDocumentElement(), action);
        if (actionEl == null) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.action.missing", action));
        }
        Element frameEl = childByName(actionEl, String.valueOf(frameIndex));
        if (frameEl == null) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.frame.missing", action, frameIndex));
        }
        Element canvas = firstCanvas(frameEl);
        if (canvas == null) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("TamingMobPoseWzXmlStore.canvas.missing", action, frameIndex));
        }
        if (req.getNavelX() != null || req.getNavelY() != null) {
            Element map = childByName(canvas, "map");
            if (map == null) {
                map = doc.createElement("imgdir");
                map.setAttribute("name", "map");
                canvas.appendChild(map);
            }
            Element navel = childByName(map, "navel");
            if (navel == null) {
                navel = doc.createElement("vector");
                navel.setAttribute("name", "navel");
                navel.setAttribute("x", "0");
                navel.setAttribute("y", "0");
                map.appendChild(navel);
            }
            if (req.getNavelX() != null) {
                navel.setAttribute("x", String.valueOf(req.getNavelX()));
            }
            if (req.getNavelY() != null) {
                navel.setAttribute("y", String.valueOf(req.getNavelY()));
            }
        }
        if (req.getOriginX() != null || req.getOriginY() != null) {
            Element origin = childByName(canvas, "origin");
            if (origin == null) {
                origin = doc.createElement("vector");
                origin.setAttribute("name", "origin");
                origin.setAttribute("x", "0");
                origin.setAttribute("y", "0");
                canvas.appendChild(origin);
            }
            if (req.getOriginX() != null) {
                origin.setAttribute("x", String.valueOf(req.getOriginX()));
            }
            if (req.getOriginY() != null) {
                origin.setAttribute("y", String.valueOf(req.getOriginY()));
            }
        }
        if (req.getZ() != null && !req.getZ().isBlank()) {
            Element zEl = childByName(canvas, "z");
            if (zEl == null) {
                zEl = doc.createElement("string");
                zEl.setAttribute("name", "z");
                canvas.appendChild(zEl);
            }
            zEl.setAttribute("value", req.getZ().trim());
        }
    }

    public Element firstCanvas(Element parent) {
        for (Element child : elementChildren(parent)) {
            if ("canvas".equals(child.getTagName())) {
                return child;
            }
        }
        return null;
    }
}
