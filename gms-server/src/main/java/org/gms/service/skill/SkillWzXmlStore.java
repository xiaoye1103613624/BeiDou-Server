package org.gms.service.skill;

import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.model.dto.SkillLevelDTO;
import org.gms.model.dto.SkillStringWriteReqDTO;
import org.gms.model.dto.SkillWriteReqDTO;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Skill.wz / String.wz(Skill.img) XML 读写：同步维护 base {@code wz} 与语言目录（若存在）。
 * <p>
 * 对齐 {@link org.gms.service.quest.QuestWzXmlStore}：中文服须写 {@code wz-zh-CN} 才对 LocalizedDataProvider 生效。
 */
@Slf4j
@Component
public class SkillWzXmlStore {
    public static final String STRING_SKILL_FILE = "Skill.img.xml";

    private final XPath xpath = XPathFactory.newInstance().newXPath();

    public Path skillBaseDir() {
        return WZFiles.SKILL.getBaseFile();
    }

    public Path skillLanguageDir() {
        return WZFiles.SKILL.getLanguageFile();
    }

    public Path stringBaseDir() {
        return WZFiles.STRING.getBaseFile();
    }

    public Path stringLanguageDir() {
        return WZFiles.STRING.getLanguageFile();
    }

    public boolean skillLanguageExists() {
        Path lang = skillLanguageDir();
        return Files.isDirectory(lang) && !lang.equals(skillBaseDir());
    }

    public boolean stringLanguageExists() {
        Path lang = stringLanguageDir();
        return Files.isDirectory(lang) && !lang.equals(stringBaseDir());
    }

    /** jobId=0 → {@code 000.img.xml}；其余 {@code {jobId}.img.xml}。 */
    public String skillImgFileName(int jobId) {
        if (jobId == 0) {
            return "000.img.xml";
        }
        return jobId + ".img.xml";
    }

    public Path resolveSkillXml(int jobId, boolean languagePreferred) {
        String name = skillImgFileName(jobId);
        if (languagePreferred && skillLanguageExists()) {
            Path lang = skillLanguageDir().resolve(name);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        Path base = skillBaseDir().resolve(name);
        if (Files.isRegularFile(base)) {
            return base;
        }
        if (skillLanguageExists()) {
            Path lang = skillLanguageDir().resolve(name);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        throw BizException.illegalArgument(
                I18nUtil.getExceptionMessage("SkillWzXmlStore.wz.missing", skillImgFileName(jobId)));
    }

    public Path resolveSkillXmlOrNull(int jobId, boolean languagePreferred) {
        try {
            return resolveSkillXml(jobId, languagePreferred);
        } catch (BizException e) {
            return null;
        }
    }

    public Path resolveStringXml(boolean languagePreferred) {
        if (languagePreferred && stringLanguageExists()) {
            Path lang = stringLanguageDir().resolve(STRING_SKILL_FILE);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        Path base = stringBaseDir().resolve(STRING_SKILL_FILE);
        if (Files.isRegularFile(base)) {
            return base;
        }
        if (stringLanguageExists()) {
            Path lang = stringLanguageDir().resolve(STRING_SKILL_FILE);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        throw BizException.illegalArgument(
                I18nUtil.getExceptionMessage("SkillWzXmlStore.string.missing"));
    }

    public Document loadDocument(Path file) {
        if (!Files.isRegularFile(file)) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("SkillWzXmlStore.wz.missing", file.toString()));
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
                    I18nUtil.getExceptionMessage("SkillWzXmlStore.wz.parseFail", file.toString()));
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
                    I18nUtil.getExceptionMessage("SkillWzXmlStore.wz.writeFail", file.toString()));
        }
    }

    /**
     * 按多种节点名查找技能：无补零 / %07d / %08d（WZ 常见 {@code 0001001}）。
     */
    public Element findSkillElement(Document doc, int skillId) {
        Element skillRoot;
        try {
            Node rootNode = (Node) xpath.evaluate("//imgdir[@name='skill']", doc, XPathConstants.NODE);
            skillRoot = rootNode instanceof Element el ? el : null;
        } catch (Exception e) {
            return null;
        }
        if (skillRoot == null) {
            return null;
        }
        for (String key : stringSkillKeyCandidates(skillId)) {
            Element el = childByName(skillRoot, key);
            if (el != null) {
                return el;
            }
        }
        return null;
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
        String v = el.getAttribute("value");
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public Map<Integer, Integer> readReq(Element skillEl) {
        Map<Integer, Integer> req = new LinkedHashMap<>();
        Element reqEl = childByName(skillEl, "req");
        if (reqEl == null) {
            return req;
        }
        for (Element child : elementChildren(reqEl)) {
            if (!"int".equals(child.getTagName())) {
                continue;
            }
            try {
                int sid = Integer.parseInt(child.getAttribute("name"));
                int lv = Integer.parseInt(child.getAttribute("value"));
                req.put(sid, lv);
            } catch (NumberFormatException ignored) {
                // skip bad nodes
            }
        }
        return req;
    }

    public List<SkillLevelDTO> readLevels(Element skillEl) {
        List<SkillLevelDTO> levels = new ArrayList<>();
        Element levelRoot = childByName(skillEl, "level");
        if (levelRoot == null) {
            return levels;
        }
        for (Element lvEl : elementChildren(levelRoot)) {
            if (!"imgdir".equals(lvEl.getTagName())) {
                continue;
            }
            Integer lv;
            try {
                lv = Integer.parseInt(lvEl.getAttribute("name"));
            } catch (NumberFormatException e) {
                continue;
            }
            Map<String, String> attrs = new LinkedHashMap<>();
            for (Element attr : elementChildren(lvEl)) {
                String tag = attr.getTagName();
                if ("int".equals(tag) || "string".equals(tag) || "float".equals(tag) || "double".equals(tag)
                        || "short".equals(tag)) {
                    attrs.put(attr.getAttribute("name"), attr.getAttribute("value"));
                }
            }
            levels.add(SkillLevelDTO.builder().level(lv).attrs(attrs).build());
        }
        return levels;
    }

    /**
     * 双写 Skill 节点到 base + 语言包（语言文件存在时）。
     */
    public void writeSkill(SkillWriteReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getSkillId(),
                I18nUtil.getExceptionMessage("SkillWzXmlStore.skillId.required"));
        int skillId = req.getSkillId();
        int jobId = skillId / 10000;

        applySkillWrite(resolveSkillXml(jobId, false), skillId, req);
        if (skillLanguageExists()) {
            Path lang = skillLanguageDir().resolve(skillImgFileName(jobId));
            if (Files.isRegularFile(lang)) {
                applySkillWrite(lang, skillId, req);
            } else {
                // 语言包缺整文件时从 base 复制再改
                Path base = skillBaseDir().resolve(skillImgFileName(jobId));
                if (Files.isRegularFile(base)) {
                    try {
                        Files.createDirectories(lang.getParent());
                        Files.copy(base, lang);
                    } catch (Exception e) {
                        throw BizException.illegalArgument(
                                I18nUtil.getExceptionMessage("SkillWzXmlStore.wz.writeFail", lang.toString()));
                    }
                    applySkillWrite(lang, skillId, req);
                }
            }
        }
        log.info(I18nUtil.getLogMessage("SkillWzXmlStore.skill.write.info"), skillId, jobId);
    }

    private void applySkillWrite(Path file, int skillId, SkillWriteReqDTO req) {
        Document doc = loadDocument(file);
        Element skillEl = findSkillElement(doc, skillId);
        if (skillEl == null) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("SkillWzXmlStore.skill.notFound", String.valueOf(skillId)));
        }
        if (req.getMasterLevel() != null) {
            setOrCreateInt(doc, skillEl, "masterLevel", req.getMasterLevel());
        }
        if (req.getInvisible() != null) {
            setOrCreateInt(doc, skillEl, "invisible", Boolean.TRUE.equals(req.getInvisible()) ? 1 : 0);
        }
        if (req.getReq() != null) {
            replaceReq(doc, skillEl, req.getReq());
        }
        if (req.getLevels() != null) {
            replaceLevels(doc, skillEl, req.getLevels());
        }
        saveDocument(file, doc);
    }

    public void writeString(SkillStringWriteReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getSkillId(),
                I18nUtil.getExceptionMessage("SkillWzXmlStore.skillId.required"));
        applyStringWrite(resolveStringXml(false), req);
        if (stringLanguageExists()) {
            Path lang = stringLanguageDir().resolve(STRING_SKILL_FILE);
            if (Files.isRegularFile(lang)) {
                applyStringWrite(lang, req);
            }
        }
        log.info(I18nUtil.getLogMessage("SkillWzXmlStore.string.write.info"), req.getSkillId());
    }

    private void applyStringWrite(Path file, SkillStringWriteReqDTO req) {
        Document doc = loadDocument(file);
        Element root = doc.getDocumentElement();
        Element skillStr = findStringSkillElement(root, req.getSkillId());
        if (skillStr == null) {
            skillStr = doc.createElement("imgdir");
            skillStr.setAttribute("name", stringSkillKey(req.getSkillId()));
            root.appendChild(skillStr);
        }
        if (req.getName() != null) {
            setOrCreateString(doc, skillStr, "name", req.getName());
        }
        if (req.getDesc() != null) {
            setOrCreateString(doc, skillStr, "desc", req.getDesc());
        }
        if (req.getHs() != null) {
            for (Map.Entry<String, String> e : req.getHs().entrySet()) {
                if (e.getKey() == null || e.getValue() == null) {
                    continue;
                }
                setOrCreateString(doc, skillStr, e.getKey(), e.getValue());
            }
        }
        saveDocument(file, doc);
    }

    public Element findStringSkillElement(Element root, int skillId) {
        for (String key : stringSkillKeyCandidates(skillId)) {
            Element el = childByName(root, key);
            if (el != null) {
                return el;
            }
        }
        return null;
    }

    public Map<String, String> readStringFields(int skillId) {
        Map<String, String> out = new LinkedHashMap<>();
        Path file = resolveStringXml(true);
        Document doc = loadDocument(file);
        Element el = findStringSkillElement(doc.getDocumentElement(), skillId);
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

    public static String stringSkillKey(int skillId) {
        return String.valueOf(skillId);
    }

    /** 兼容 0000008 / 1000000 等命名。 */
    public static List<String> stringSkillKeyCandidates(int skillId) {
        List<String> keys = new ArrayList<>();
        keys.add(String.valueOf(skillId));
        String padded = String.format("%07d", skillId);
        if (!keys.contains(padded)) {
            keys.add(padded);
        }
        String padded8 = String.format("%08d", skillId);
        if (!keys.contains(padded8)) {
            keys.add(padded8);
        }
        return keys;
    }

    private void setOrCreateInt(Document doc, Element parent, String name, int value) {
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

    private void setOrCreateString(Document doc, Element parent, String name, String value) {
        Element el = childByName(parent, name);
        if (el == null) {
            el = doc.createElement("string");
            el.setAttribute("name", name);
            parent.appendChild(el);
        } else if (!"string".equals(el.getTagName())) {
            parent.removeChild(el);
            el = doc.createElement("string");
            el.setAttribute("name", name);
            parent.appendChild(el);
        }
        el.setAttribute("value", Objects.requireNonNullElse(value, ""));
    }

    private void replaceReq(Document doc, Element skillEl, Map<Integer, Integer> req) {
        Element reqEl = childByName(skillEl, "req");
        if (reqEl != null) {
            skillEl.removeChild(reqEl);
        }
        if (req == null || req.isEmpty()) {
            return;
        }
        reqEl = doc.createElement("imgdir");
        reqEl.setAttribute("name", "req");
        for (Map.Entry<Integer, Integer> e : req.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) {
                continue;
            }
            Element intEl = doc.createElement("int");
            intEl.setAttribute("name", String.valueOf(e.getKey()));
            intEl.setAttribute("value", String.valueOf(e.getValue()));
            reqEl.appendChild(intEl);
        }
        skillEl.appendChild(reqEl);
    }

    private void replaceLevels(Document doc, Element skillEl, List<SkillLevelDTO> levels) {
        Element levelRoot = childByName(skillEl, "level");
        if (levelRoot == null) {
            levelRoot = doc.createElement("imgdir");
            levelRoot.setAttribute("name", "level");
            skillEl.appendChild(levelRoot);
        } else {
            while (levelRoot.hasChildNodes()) {
                levelRoot.removeChild(levelRoot.getFirstChild());
            }
        }
        if (levels == null) {
            return;
        }
        for (SkillLevelDTO lv : levels) {
            if (lv == null || lv.getLevel() == null) {
                continue;
            }
            Element lvEl = doc.createElement("imgdir");
            lvEl.setAttribute("name", String.valueOf(lv.getLevel()));
            if (lv.getAttrs() != null) {
                for (Map.Entry<String, String> a : lv.getAttrs().entrySet()) {
                    if (a.getKey() == null) {
                        continue;
                    }
                    String val = a.getValue() == null ? "" : a.getValue();
                    boolean asInt = looksLikeInt(val) && !"hs".equals(a.getKey());
                    Element attr = doc.createElement(asInt ? "int" : "string");
                    attr.setAttribute("name", a.getKey());
                    attr.setAttribute("value", val);
                    lvEl.appendChild(attr);
                }
            }
            levelRoot.appendChild(lvEl);
        }
    }

    private static boolean looksLikeInt(String v) {
        if (v == null || v.isBlank()) {
            return false;
        }
        try {
            Integer.parseInt(v);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
