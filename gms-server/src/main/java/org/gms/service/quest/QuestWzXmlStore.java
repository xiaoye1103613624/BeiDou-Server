package org.gms.service.quest;

import lombok.extern.slf4j.Slf4j;
import org.gms.exception.BizException;
import org.gms.model.dto.QuestItemNodeDTO;
import org.gms.model.dto.QuestLinkNodeDTO;
import org.gms.model.dto.QuestMobNodeDTO;
import org.gms.model.dto.QuestWriteReqDTO;
import org.gms.provider.wz.WZFiles;
import org.gms.util.I18nUtil;
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
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Quest.wz XML 读写：同步维护 base {@code wz/Quest.wz} 与语言目录（若存在）。
 * <p>
 * LocalizedDataProvider 以整文件优先语言包，中文服必须写 {@code wz-zh-CN} 才生效。
 */
@Slf4j
@Component
public class QuestWzXmlStore {
    public static final String FILE_INFO = "QuestInfo.img.xml";
    public static final String FILE_CHECK = "Check.img.xml";
    public static final String FILE_ACT = "Act.img.xml";
    public static final String FILE_SAY = "Say.img.xml";

    private final XPath xpath = XPathFactory.newInstance().newXPath();

    public Path baseDir() {
        return WZFiles.QUEST.getBaseFile();
    }

    public Path languageDir() {
        return WZFiles.QUEST.getLanguageFile();
    }

    public boolean languageExists() {
        Path lang = languageDir();
        return Files.isDirectory(lang) && !lang.equals(baseDir());
    }

    public Document loadDocument(Path file) {
        if (!Files.isRegularFile(file)) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.wz.missing", file.toString()));
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
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.wz.parseFail", file.toString()));
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
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.wz.writeFail", file.toString()));
        }
    }

    public Element findQuestElement(Document doc, int questId) {
        try {
            String expr = "/*/*[@name='" + questId + "']";
            Node node = (Node) xpath.evaluate(expr, doc, XPathConstants.NODE);
            return node instanceof Element el ? el : null;
        } catch (Exception e) {
            return null;
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

    public String getStringAttr(Element parent, String name) {
        Element el = childByName(parent, name);
        return el == null ? null : el.getAttribute("value");
    }

    public Integer getIntAttr(Element parent, String name) {
        String v = getStringAttr(parent, name);
        if (v == null || v.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public List<Element> namedChildren(Element parent, String tagHint) {
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

    public List<QuestItemNodeDTO> parseItemDir(Element phaseEl, String dirName) {
        List<QuestItemNodeDTO> items = new ArrayList<>();
        Element itemDir = childByName(phaseEl, dirName);
        if (itemDir == null) {
            return items;
        }
        for (Element entry : namedChildren(itemDir, "imgdir")) {
            Integer id = getIntAttr(entry, "id");
            if (id == null) {
                continue;
            }
            Integer count = getIntAttr(entry, "count");
            items.add(QuestItemNodeDTO.builder().id(id).count(count == null ? 1 : count).build());
        }
        return items;
    }

    public List<QuestMobNodeDTO> parseMobDir(Element phaseEl) {
        List<QuestMobNodeDTO> mobs = new ArrayList<>();
        Element mobDir = childByName(phaseEl, "mob");
        if (mobDir == null) {
            return mobs;
        }
        for (Element entry : namedChildren(mobDir, "imgdir")) {
            Integer id = getIntAttr(entry, "id");
            if (id == null) {
                continue;
            }
            Integer count = getIntAttr(entry, "count");
            mobs.add(QuestMobNodeDTO.builder().id(id).count(count == null ? 1 : count).build());
        }
        return mobs;
    }

    public List<QuestLinkNodeDTO> parseQuestLinks(Element phaseEl) {
        List<QuestLinkNodeDTO> links = new ArrayList<>();
        Element questDir = childByName(phaseEl, "quest");
        if (questDir == null) {
            return links;
        }
        for (Element entry : namedChildren(questDir, "imgdir")) {
            Integer id = getIntAttr(entry, "id");
            if (id == null) {
                continue;
            }
            Integer state = getIntAttr(entry, "state");
            links.add(QuestLinkNodeDTO.builder().questId(id).state(state == null ? 2 : state).build());
        }
        return links;
    }

    public List<Integer> parseFieldEnter(Element phaseEl) {
        List<Integer> maps = new ArrayList<>();
        Element field = childByName(phaseEl, "fieldEnter");
        if (field == null) {
            return maps;
        }
        // fieldEnter 可能是 int 或 imgdir
        if ("int".equals(field.getTagName())) {
            try {
                maps.add(Integer.parseInt(field.getAttribute("value")));
            } catch (NumberFormatException ignored) {
            }
            return maps;
        }
        for (Element entry : namedChildren(field, null)) {
            if ("int".equals(entry.getTagName())) {
                try {
                    maps.add(Integer.parseInt(entry.getAttribute("value")));
                } catch (NumberFormatException ignored) {
                }
            } else {
                Integer id = getIntAttr(entry, "id");
                if (id != null) {
                    maps.add(id);
                } else if (entry.hasAttribute("value")) {
                    try {
                        maps.add(Integer.parseInt(entry.getAttribute("value")));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return maps;
    }

    /**
     * 在 base 与 language（若存在）上执行同一写操作。
     */
    public void mutateBoth(String fileName, Consumer<Path> mutator) {
        mutator.accept(baseDir().resolve(fileName));
        if (languageExists()) {
            Path langFile = languageDir().resolve(fileName);
            if (Files.isRegularFile(langFile) || Files.isDirectory(languageDir())) {
                mutator.accept(langFile);
            }
        }
    }

    public void upsertQuest(QuestWriteReqDTO req, boolean create) {
        RequireQuestId(req);
        int questId = req.getQuestId();
        mutateBoth(FILE_INFO, path -> upsertInfo(path, req, create));
        mutateBoth(FILE_CHECK, path -> upsertCheck(path, req, create));
        mutateBoth(FILE_ACT, path -> upsertAct(path, req, create));
        log.info(I18nUtil.getLogMessage("QuestAdminService.wz.upsert.info"), questId, create);
    }

    public void deleteQuest(int questId) {
        mutateBoth(FILE_INFO, path -> removeQuestNode(path, questId));
        mutateBoth(FILE_CHECK, path -> removeQuestNode(path, questId));
        mutateBoth(FILE_ACT, path -> removeQuestNode(path, questId));
        if (languageExists()) {
            Path say = languageDir().resolve(FILE_SAY);
            if (Files.isRegularFile(say)) {
                removeQuestNode(say, questId);
            }
        }
        Path baseSay = baseDir().resolve(FILE_SAY);
        if (Files.isRegularFile(baseSay)) {
            removeQuestNode(baseSay, questId);
        }
        log.info(I18nUtil.getLogMessage("QuestAdminService.wz.delete.info"), questId);
    }

    private void RequireQuestId(QuestWriteReqDTO req) {
        if (req == null || req.getQuestId() == null || req.getQuestId() <= 0) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.questId.required"));
        }
    }

    private void upsertInfo(Path path, QuestWriteReqDTO req, boolean create) {
        Document doc = ensureDocument(path, "QuestInfo.img");
        Element root = doc.getDocumentElement();
        Element existing = findQuestElement(doc, req.getQuestId());
        if (create && existing != null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.quest.exists", req.getQuestId()));
        }
        if (!create && existing == null) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.quest.notExist", req.getQuestId()));
        }
        Element questEl = existing != null ? existing : doc.createElement("imgdir");
        if (existing == null) {
            questEl.setAttribute("name", String.valueOf(req.getQuestId()));
            root.appendChild(questEl);
        }
        String name = path.toString().contains("zh-CN") && req.getNameZh() != null && !req.getNameZh().isBlank()
                ? req.getNameZh() : req.getName();
        setStringChild(doc, questEl, "name", name == null ? "" : name);
        setStringChild(doc, questEl, "parent", req.getParentName());
        setStringChild(doc, questEl, "0", req.getText0());
        setStringChild(doc, questEl, "1", req.getText1());
        setStringChild(doc, questEl, "2", req.getText2());
        setIntChild(doc, questEl, "area", req.getArea());
        setIntChild(doc, questEl, "order", req.getOrder());
        setFlagChild(doc, questEl, "autoStart", req.getAutoStart());
        setFlagChild(doc, questEl, "autoPreComplete", req.getAutoPreComplete());
        setFlagChild(doc, questEl, "autoComplete", req.getAutoComplete());
        saveDocument(path, doc);
    }

    private void upsertCheck(Path path, QuestWriteReqDTO req, boolean create) {
        Document doc = ensureDocument(path, "Check.img");
        Element root = doc.getDocumentElement();
        Element existing = findQuestElement(doc, req.getQuestId());
        Element questEl = existing != null ? existing : doc.createElement("imgdir");
        if (existing == null) {
            questEl.setAttribute("name", String.valueOf(req.getQuestId()));
            root.appendChild(questEl);
        }
        Element start = ensurePhase(doc, questEl, "0");
        Element end = ensurePhase(doc, questEl, "1");
        setIntChild(doc, start, "npc", req.getStartNpcId());
        setIntChild(doc, end, "npc", req.getEndNpcId() != null ? req.getEndNpcId() : req.getStartNpcId());
        setIntChild(doc, start, "lvmin", req.getMinLevel());
        setIntChild(doc, start, "lvmax", req.getMaxLevel());
        setStringChild(doc, start, "startscript", req.getStartScript());
        setStringChild(doc, end, "endscript", req.getEndScript());
        rewriteItemDir(doc, start, "item", req.getStartItems());
        rewriteItemDir(doc, end, "item", req.getEndItems());
        rewriteMobDir(doc, end, req.getEndMobs());
        rewriteQuestLinks(doc, start, req.getUpstreamQuests());
        saveDocument(path, doc);
    }

    private void upsertAct(Path path, QuestWriteReqDTO req, boolean create) {
        Document doc = ensureDocument(path, "Act.img");
        Element root = doc.getDocumentElement();
        Element existing = findQuestElement(doc, req.getQuestId());
        Element questEl = existing != null ? existing : doc.createElement("imgdir");
        if (existing == null) {
            questEl.setAttribute("name", String.valueOf(req.getQuestId()));
            root.appendChild(questEl);
        }
        Element start = ensurePhase(doc, questEl, "0");
        Element end = ensurePhase(doc, questEl, "1");
        if (start.getChildNodes().getLength() == 0) {
            // keep empty start act
        }
        setIntChild(doc, end, "nextQuest", req.getNextQuestId());
        setIntChild(doc, end, "exp", req.getEndExp());
        setIntChild(doc, end, "money", req.getEndMeso());
        rewriteItemDir(doc, end, "item", req.getEndRewards());
        saveDocument(path, doc);
    }

    private Document ensureDocument(Path path, String rootName) {
        if (Files.isRegularFile(path)) {
            return loadDocument(path);
        }
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            Document doc = factory.newDocumentBuilder().newDocument();
            Element root = doc.createElement("imgdir");
            root.setAttribute("name", rootName);
            doc.appendChild(root);
            return doc;
        } catch (Exception e) {
            throw BizException.illegalArgument(I18nUtil.getExceptionMessage("QuestAdminService.wz.parseFail", path.toString()));
        }
    }

    private void removeQuestNode(Path path, int questId) {
        if (!Files.isRegularFile(path)) {
            return;
        }
        Document doc = loadDocument(path);
        Element el = findQuestElement(doc, questId);
        if (el != null && el.getParentNode() != null) {
            el.getParentNode().removeChild(el);
            saveDocument(path, doc);
        }
    }

    private Element ensurePhase(Document doc, Element questEl, String phase) {
        Element phaseEl = childByName(questEl, phase);
        if (phaseEl == null) {
            phaseEl = doc.createElement("imgdir");
            phaseEl.setAttribute("name", phase);
            questEl.appendChild(phaseEl);
        }
        return phaseEl;
    }

    private void setStringChild(Document doc, Element parent, String name, String value) {
        if (value == null) {
            return;
        }
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
        el.setAttribute("value", value);
    }

    private void setIntChild(Document doc, Element parent, String name, Integer value) {
        if (value == null) {
            return;
        }
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

    private void setFlagChild(Document doc, Element parent, String name, Boolean flag) {
        if (flag == null) {
            return;
        }
        if (Boolean.TRUE.equals(flag)) {
            setIntChild(doc, parent, name, 1);
        } else {
            Element el = childByName(parent, name);
            if (el != null) {
                parent.removeChild(el);
            }
        }
    }

    private void rewriteItemDir(Document doc, Element phase, String dirName, List<QuestItemNodeDTO> items) {
        if (items == null) {
            return;
        }
        Element old = childByName(phase, dirName);
        if (old != null) {
            phase.removeChild(old);
        }
        if (items.isEmpty()) {
            return;
        }
        Element dir = doc.createElement("imgdir");
        dir.setAttribute("name", dirName);
        int idx = 0;
        for (QuestItemNodeDTO item : items) {
            if (item == null || item.getId() == null) {
                continue;
            }
            Element entry = doc.createElement("imgdir");
            entry.setAttribute("name", String.valueOf(idx++));
            setIntChild(doc, entry, "id", item.getId());
            setIntChild(doc, entry, "count", item.getCount() == null ? 1 : item.getCount());
            dir.appendChild(entry);
        }
        if (idx > 0) {
            phase.appendChild(dir);
        }
    }

    private void rewriteMobDir(Document doc, Element phase, List<QuestMobNodeDTO> mobs) {
        if (mobs == null) {
            return;
        }
        Element old = childByName(phase, "mob");
        if (old != null) {
            phase.removeChild(old);
        }
        if (mobs.isEmpty()) {
            return;
        }
        Element dir = doc.createElement("imgdir");
        dir.setAttribute("name", "mob");
        int idx = 0;
        for (QuestMobNodeDTO mob : mobs) {
            if (mob == null || mob.getId() == null) {
                continue;
            }
            Element entry = doc.createElement("imgdir");
            entry.setAttribute("name", String.valueOf(idx++));
            setIntChild(doc, entry, "id", mob.getId());
            setIntChild(doc, entry, "count", mob.getCount() == null ? 1 : mob.getCount());
            dir.appendChild(entry);
        }
        if (idx > 0) {
            phase.appendChild(dir);
        }
    }

    private void rewriteQuestLinks(Document doc, Element phase, List<QuestLinkNodeDTO> links) {
        if (links == null) {
            return;
        }
        Element old = childByName(phase, "quest");
        if (old != null) {
            phase.removeChild(old);
        }
        if (links.isEmpty()) {
            return;
        }
        Element dir = doc.createElement("imgdir");
        dir.setAttribute("name", "quest");
        int idx = 0;
        for (QuestLinkNodeDTO link : links) {
            if (link == null || link.getQuestId() == null) {
                continue;
            }
            Element entry = doc.createElement("imgdir");
            entry.setAttribute("name", String.valueOf(idx++));
            setIntChild(doc, entry, "id", link.getQuestId());
            setIntChild(doc, entry, "state", link.getState() == null ? 2 : link.getState());
            dir.appendChild(entry);
        }
        if (idx > 0) {
            phase.appendChild(dir);
        }
    }

    public Path preferredXml(String fileName) {
        if (languageExists()) {
            Path lang = languageDir().resolve(fileName);
            if (Files.isRegularFile(lang)) {
                return lang;
            }
        }
        return baseDir().resolve(fileName);
    }

    public boolean questExistsInWz(int questId) {
        Path info = preferredXml(FILE_INFO);
        if (!Files.isRegularFile(info)) {
            return false;
        }
        return findQuestElement(loadDocument(info), questId) != null;
    }

    public List<String> listQuestWzRelativePaths() {
        List<String> files = new ArrayList<>();
        for (String name : List.of(FILE_INFO, FILE_CHECK, FILE_ACT, FILE_SAY)) {
            if (Files.isRegularFile(baseDir().resolve(name))) {
                files.add("wz/Quest.wz/" + name);
            }
            if (languageExists() && Files.isRegularFile(languageDir().resolve(name))) {
                files.add(languageDir().getParent().getFileName() + "/Quest.wz/" + name);
            }
        }
        return files;
    }

    public boolean equalsPath(Path a, Path b) {
        return Objects.equals(
                a == null ? null : a.toAbsolutePath().normalize(),
                b == null ? null : b.toAbsolutePath().normalize());
    }
}
