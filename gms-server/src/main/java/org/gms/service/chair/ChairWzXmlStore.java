package org.gms.service.chair;

import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.model.dto.ChairPoseEffectDTO;
import org.gms.model.dto.ChairPoseWriteReqDTO;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 椅子 Install XML：{@code Item.wz/Install/0301.img.xml} 双写 base / 语言包。
 * <p>
 * 语言包缺文件时只写 base 并告警（不自动从 base 复制），与座椅设置决策一致。
 */
@Slf4j
@Component
public class ChairWzXmlStore {
    public static final String INSTALL_FILE = "0301.img.xml";
    public static final String STRING_INS_FILE = "Ins.img.xml";

    public Path itemBaseDir() {
        return WZFiles.ITEM.getBaseFile();
    }

    public Path itemLanguageDir() {
        return WZFiles.ITEM.getLanguageFile();
    }

    public Path stringBaseDir() {
        return WZFiles.STRING.getBaseFile();
    }

    public Path stringLanguageDir() {
        return WZFiles.STRING.getLanguageFile();
    }

    public Path installRelative() {
        return Path.of("Install", INSTALL_FILE);
    }

    public boolean itemLanguageExists() {
        Path lang = itemLanguageDir();
        return Files.isDirectory(lang) && !lang.equals(itemBaseDir());
    }

    public boolean stringLanguageExists() {
        Path lang = stringLanguageDir();
        return Files.isDirectory(lang) && !lang.equals(stringBaseDir());
    }

    public Path resolveInstallXml(boolean languagePreferred) {
        if (languagePreferred && itemLanguageExists()) {
            Path lang = itemLanguageDir().resolve(installRelative());
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        Path base = itemBaseDir().resolve(installRelative());
        if (Files.isRegularFile(base)) {
            return base;
        }
        if (itemLanguageExists()) {
            Path lang = itemLanguageDir().resolve(installRelative());
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        throw BizException.illegalArgument(
                I18nUtil.getExceptionMessage("ChairWzXmlStore.wz.missing", INSTALL_FILE));
    }

    public Path resolveStringInsXml(boolean languagePreferred) {
        if (languagePreferred && stringLanguageExists()) {
            Path lang = stringLanguageDir().resolve(STRING_INS_FILE);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        Path base = stringBaseDir().resolve(STRING_INS_FILE);
        if (Files.isRegularFile(base)) {
            return base;
        }
        if (stringLanguageExists()) {
            Path lang = stringLanguageDir().resolve(STRING_INS_FILE);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        throw BizException.illegalArgument(
                I18nUtil.getExceptionMessage("ChairWzXmlStore.string.missing"));
    }

    public Document loadDocument(Path file) {
        if (!Files.isRegularFile(file)) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairWzXmlStore.wz.missing", file.toString()));
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
                    I18nUtil.getExceptionMessage("ChairWzXmlStore.wz.parseFail", file.toString()));
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
                    I18nUtil.getExceptionMessage("ChairWzXmlStore.wz.writeFail", file.toString()));
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

    public Integer getIntAttr(Element parent, String name) {
        Element el = childByName(parent, name);
        if (el == null) {
            return null;
        }
        return parseIntAttr(el.getAttribute("value"));
    }

    public Integer getVectorX(Element vectorEl) {
        return vectorEl == null ? null : parseIntAttr(vectorEl.getAttribute("x"));
    }

    public Integer getVectorY(Element vectorEl) {
        return vectorEl == null ? null : parseIntAttr(vectorEl.getAttribute("y"));
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

    /** XML imgdir 名 {@code 03010000} → 道具 ID {@code 3010000}。 */
    public static Integer parseChairItemId(String imgdirName) {
        if (imgdirName == null || imgdirName.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(imgdirName.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** 道具 ID → XML 节点名（8 位前导零）。 */
    public static String chairImgdirName(int itemId) {
        return String.format(Locale.ROOT, "%08d", itemId);
    }

    public boolean isChairItemId(int itemId) {
        return itemId >= 3010000 && itemId < 3020000;
    }

    public Element findChairElement(Document doc, int itemId) {
        Element root = doc.getDocumentElement();
        Element byPadded = childByName(root, chairImgdirName(itemId));
        if (byPadded != null) {
            return byPadded;
        }
        return childByName(root, String.valueOf(itemId));
    }

    public List<Element> listChairElements(Document doc) {
        List<Element> out = new ArrayList<>();
        for (Element child : elementChildren(doc.getDocumentElement())) {
            if (!"imgdir".equals(child.getTagName())) {
                continue;
            }
            Integer id = parseChairItemId(child.getAttribute("name"));
            if (id != null && isChairItemId(id)) {
                out.add(child);
            }
        }
        return out;
    }

    public ChairPoseEffectDTO readEffectLayer(Element chairEl, String layer) {
        Element effectEl = childByName(chairEl, layer);
        if (effectEl == null) {
            return null;
        }
        Element firstCanvas = firstCanvas(effectEl);
        Integer frameOx = null;
        Integer frameOy = null;
        Integer frameZ = null;
        Integer cw = null;
        Integer ch = null;
        if (firstCanvas != null) {
            cw = parseIntAttr(firstCanvas.getAttribute("width"));
            ch = parseIntAttr(firstCanvas.getAttribute("height"));
            Element origin = childByName(firstCanvas, "origin");
            frameOx = getVectorX(origin);
            frameOy = getVectorY(origin);
            frameZ = getIntAttr(firstCanvas, "z");
        }
        // 层级 origin 少见；以首帧 canvas origin 作为可编辑主值
        Integer originX = frameOx;
        Integer originY = frameOy;
        return ChairPoseEffectDTO.builder()
                .layer(layer)
                .originX(originX)
                .originY(originY)
                .pos(getIntAttr(effectEl, "pos"))
                .z(getIntAttr(effectEl, "z"))
                .canvasWidth(cw)
                .canvasHeight(ch)
                .frameOriginX(frameOx)
                .frameOriginY(frameOy)
                .frameZ(frameZ)
                .build();
    }

    public List<ChairPoseEffectDTO> readEffects(Element chairEl) {
        List<ChairPoseEffectDTO> list = new ArrayList<>();
        ChairPoseEffectDTO e1 = readEffectLayer(chairEl, "effect");
        if (e1 != null) {
            list.add(e1);
        }
        ChairPoseEffectDTO e2 = readEffectLayer(chairEl, "effect2");
        if (e2 != null) {
            list.add(e2);
        }
        return list;
    }

    public Map<String, String> readInsString(int itemId) {
        Map<String, String> out = new LinkedHashMap<>();
        Path file = resolveStringInsXml(true);
        Document doc = loadDocument(file);
        Element el = childByName(doc.getDocumentElement(), String.valueOf(itemId));
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
     * 双写 effect/effect2 的 origin/pos/z。
     * 语言包缺 Install 文件时只写 base 并收集告警。
     */
    public List<String> writeChair(ChairPoseWriteReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getItemId(),
                I18nUtil.getExceptionMessage("ChairWzXmlStore.itemId.required"));
        if (!isChairItemId(req.getItemId())) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairWzXmlStore.itemId.invalid", req.getItemId()));
        }
        List<String> warnings = new ArrayList<>();
        Path base = itemBaseDir().resolve(installRelative());
        if (!Files.isRegularFile(base)) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairWzXmlStore.wz.missing", base.toString()));
        }
        applyChairWrite(base, req);
        if (itemLanguageExists()) {
            Path lang = itemLanguageDir().resolve(installRelative());
            if (Files.isRegularFile(lang)) {
                applyChairWrite(lang, req);
            } else {
                warnings.add(I18nUtil.getMessage("ChairWzXmlStore.warn.langMissing", lang.toString()));
                log.warn(I18nUtil.getLogMessage("ChairWzXmlStore.lang.missing.warn"), lang);
            }
        }
        log.info(I18nUtil.getLogMessage("ChairWzXmlStore.chair.write.info"), req.getItemId());
        return warnings;
    }

    private void applyChairWrite(Path file, ChairPoseWriteReqDTO req) {
        Document doc = loadDocument(file);
        Element chairEl = findChairElement(doc, req.getItemId());
        if (chairEl == null) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairWzXmlStore.chair.notFound", req.getItemId()));
        }
        if (req.getEffects() != null) {
            for (ChairPoseEffectDTO effect : req.getEffects()) {
                if (effect == null || effect.getLayer() == null || effect.getLayer().isBlank()) {
                    continue;
                }
                String layer = effect.getLayer().trim();
                if (!"effect".equals(layer) && !"effect2".equals(layer)) {
                    continue;
                }
                applyEffectWrite(doc, chairEl, layer, effect);
            }
        }
        saveDocument(file, doc);
    }

    private void applyEffectWrite(Document doc, Element chairEl, String layer, ChairPoseEffectDTO effect) {
        Element effectEl = childByName(chairEl, layer);
        if (effectEl == null) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairWzXmlStore.effect.missing", layer));
        }
        if (effect.getPos() != null) {
            setOrCreateInt(doc, effectEl, "pos", effect.getPos());
        }
        if (effect.getZ() != null) {
            setOrCreateInt(doc, effectEl, "z", effect.getZ());
        }
        Element firstCanvas = firstCanvas(effectEl);
        if (firstCanvas != null) {
            if (effect.getOriginX() != null || effect.getOriginY() != null) {
                Element origin = childByName(firstCanvas, "origin");
                if (origin == null) {
                    origin = doc.createElement("vector");
                    origin.setAttribute("name", "origin");
                    origin.setAttribute("x", "0");
                    origin.setAttribute("y", "0");
                    firstCanvas.appendChild(origin);
                }
                if (effect.getOriginX() != null) {
                    origin.setAttribute("x", String.valueOf(effect.getOriginX()));
                }
                if (effect.getOriginY() != null) {
                    origin.setAttribute("y", String.valueOf(effect.getOriginY()));
                }
            }
            if (effect.getFrameZ() != null) {
                setOrCreateInt(doc, firstCanvas, "z", effect.getFrameZ());
            }
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

    public void setOrCreateInt(Document doc, Element parent, String name, int value) {
        Element el = childByName(parent, name);
        if (el == null) {
            el = doc.createElement("int");
            el.setAttribute("name", name);
            parent.appendChild(el);
        } else if (!"int".equals(el.getTagName())) {
            parent.removeChild(el);
            el = doc.createElement("int");
            el.setAttribute("name", name);
            parent.appendChild(el);
        }
        el.setAttribute("value", String.valueOf(value));
    }

    /** 供测试/工具：在已加载 Document 上执行变更但不落盘。 */
    public void mutateChairInMemory(Document doc, int itemId, Consumer<Element> mutator) {
        Element chairEl = findChairElement(doc, itemId);
        Objects.requireNonNull(chairEl, "chair");
        mutator.accept(chairEl);
    }
}
