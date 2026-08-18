package org.gms.skilltech;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.SkillTechDO;
import org.gms.dao.mapper.SkillTechMapper;
import org.gms.manager.ServerManager;
import org.gms.provider.wz.WZFiles;
import org.gms.server.cashshop.ClientDataPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 技改双端同步：扩展服务端 Skill XML，并尽量通过 orange-wz HTTP MCP 同步客户端 Data/Skill。
 */
public final class SkillTechWzSync {
    private static final Logger log = LoggerFactory.getLogger(SkillTechWzSync.class);

    /**
     * 仅作未配置时的提示文案；实际路径走 {@link ClientDataPath}。
     * @deprecated 请配置 game_config / {@code -Dgms.client.data=}，勿依赖写死路径。
     */
    @Deprecated
    public static final String DEFAULT_CLIENT_DATA = "F:\\MXD_dev\\BeiDou-Client\\Data";
    public static final String ORANGE_WZ_MCP = "http://127.0.0.1:10002/mcp";

    private SkillTechWzSync() {}

    /** 客户端 Data 根：统一配置优先，未配置时回退到 MXD_dev live 提示路径。 */
    public static Path resolveClientDataRoot() {
        return ClientDataPath.resolve()
                .orElseGet(() -> Path.of(DEFAULT_CLIENT_DATA).toAbsolutePath().normalize());
    }

    public static Map<String, Object> sync(Integer skillId) {
        Map<String, Object> result = new HashMap<>();
        result.put("ok", false);
        if (skillId == null || skillId <= 0) {
            result.put("message", "skillId required");
            return result;
        }
        try {
            SkillTechMapper mapper = ServerManager.getApplicationContext().getBean(SkillTechMapper.class);
            SkillTechDO row = mapper.selectOneByQuery(
                    QueryWrapper.create().eq(SkillTechDO::getSkillId, skillId));
            if (row == null) {
                result.put("message", "未找到技改配置: " + skillId);
                return result;
            }

            Path serverXml = resolveServerSkillXml(skillId);
            result.put("serverXml", serverXml.toString());
            extendServerXml(serverXml, skillId, row);
            result.put("serverXmlUpdated", true);

            Map<String, Object> client = tryOrangeWzClientSync(skillId, row);
            result.put("client", client);
            boolean clientOk = Boolean.TRUE.equals(client.get("ok"));
            result.put("ok", true);
            result.put("clientSynced", clientOk);
            if (clientOk) {
                result.put("message", "服务端 XML 已扩展，客户端已通过 orange-wz 同步");
            } else {
                result.put("message", "服务端 XML 已扩展。"
                        + (client.get("message") != null ? client.get("message") : "请手动用 orange-wz 同步客户端 Skill.img"));
            }
            return result;
        } catch (Exception e) {
            log.error("SkillTech sync failed skill={}", skillId, e);
            result.put("message", e.getMessage());
            return result;
        }
    }

    static Path resolveServerSkillXml(int skillId) {
        int job = skillId / 10000;
        // WZFiles.SKILL 指向 ContentRoot 下 Skill.wz；运行目录通常是 gms-server/
        Path p = WZFiles.SKILL.getFile().resolve(job + ".img.xml");
        if (Files.isRegularFile(p)) {
            return p;
        }
        Path alt = Path.of("wz-zh-CN", "Skill.wz", job + ".img.xml");
        if (Files.isRegularFile(alt)) {
            return alt.toAbsolutePath().normalize();
        }
        throw new IllegalStateException("找不到服务端技能 XML: " + p + " / " + alt);
    }

    static void extendServerXml(Path xmlPath, int skillId, SkillTechDO row) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        Document doc = dbf.newDocumentBuilder().parse(xmlPath.toFile());
        Element skillEl = findSkillElement(doc.getDocumentElement(), String.valueOf(skillId));
        if (skillEl == null) {
            throw new IllegalStateException("XML 中无技能节点: " + skillId);
        }

        int spMax = row.getSpMaxLevel() != null ? row.getSpMaxLevel() : 0;
        int effectMax = row.getEffectMaxLevel() != null ? row.getEffectMaxLevel() : spMax;
        ensureIntChild(doc, skillEl, "spMaxLevel", spMax);

        Element levelRoot = getChildImgDir(skillEl, "level");
        if (levelRoot == null) {
            throw new IllegalStateException("技能无 level 节点: " + skillId);
        }

        int existingMax = 0;
        Element lastLevel = null;
        NodeList children = levelRoot.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) n;
            if (!"imgdir".equals(el.getTagName())) {
                continue;
            }
            try {
                int lv = Integer.parseInt(el.getAttribute("name"));
                if (lv >= existingMax) {
                    existingMax = lv;
                    lastLevel = el;
                }
            } catch (NumberFormatException ignored) {
            }
        }
        if (lastLevel == null) {
            throw new IllegalStateException("技能 level 为空: " + skillId);
        }

        Map<Integer, Map<String, Integer>> overrides = SkillTechManager.parseLevelsJson(row.getLevelsJson());
        for (int lv = existingMax + 1; lv <= effectMax; lv++) {
            Element clone = (Element) lastLevel.cloneNode(true);
            clone.setAttribute("name", String.valueOf(lv));
            applyOverridesToLevelEl(doc, clone, overrides.get(lv));
            levelRoot.appendChild(clone);
            lastLevel = clone;
        }
        // 覆盖已有等级属性
        for (Map.Entry<Integer, Map<String, Integer>> e : overrides.entrySet()) {
            if (e.getKey() == null || e.getKey() > effectMax) {
                continue;
            }
            Element lvEl = getChildImgDir(levelRoot, String.valueOf(e.getKey()));
            if (lvEl != null) {
                applyOverridesToLevelEl(doc, lvEl, e.getValue());
            }
        }

        var tf = TransformerFactory.newInstance().newTransformer();
        tf.setOutputProperty(OutputKeys.INDENT, "yes");
        tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        tf.transform(new DOMSource(doc), new StreamResult(xmlPath.toFile()));
        log.info("SkillTech extended server XML {} skill={} effectMax={}", xmlPath, skillId, effectMax);
    }

    private static void applyOverridesToLevelEl(Document doc, Element levelEl, Map<String, Integer> ov) {
        if (ov == null || ov.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Integer> e : ov.entrySet()) {
            ensureIntChild(doc, levelEl, e.getKey(), e.getValue());
        }
    }

    private static void ensureIntChild(Document doc, Element parent, String name, int value) {
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) n;
            if ("int".equals(el.getTagName()) && name.equals(el.getAttribute("name"))) {
                el.setAttribute("value", String.valueOf(value));
                return;
            }
        }
        Element neu = doc.createElement("int");
        neu.setAttribute("name", name);
        neu.setAttribute("value", String.valueOf(value));
        parent.appendChild(neu);
    }

    private static Element findSkillElement(Element root, String skillId) {
        // 结构: 112.img / skill / {skillId}
        Element skillRoot = getChildImgDir(root, "skill");
        if (skillRoot == null) {
            return null;
        }
        return getChildImgDir(skillRoot, skillId);
    }

    private static Element getChildImgDir(Element parent, String name) {
        if (parent == null) {
            return null;
        }
        NodeList children = parent.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element el = (Element) n;
            if ("imgdir".equals(el.getTagName()) && name.equals(el.getAttribute("name"))) {
                return el;
            }
        }
        return null;
    }

    /**
     * 通过 orange-wz MCP tools/call 同步客户端（若服务可用）。
     * 失败时返回提示，不抛错。
     */
    static Map<String, Object> tryOrangeWzClientSync(int skillId, SkillTechDO row) {
        Map<String, Object> r = new HashMap<>();
        r.put("ok", false);
        int job = skillId / 10000;
        Path clientRoot = resolveClientDataRoot();
        String clientImg = clientRoot.resolve("Skill.wz").resolve(job + ".img").toString();
        // 简化：探测 MCP 是否可达；完整 copy_nodes 链路依赖会话态，后台 HTTP 不一定有会话
        try {
            HttpURLConnection conn = (HttpURLConnection) URI.create(ORANGE_WZ_MCP).toURL().openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(3000);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json, text/event-stream");
            String body = "{\"jsonrpc\":\"2.0\",\"id\":1,\"method\":\"initialize\",\"params\":{"
                    + "\"protocolVersion\":\"2024-11-05\",\"capabilities\":{},"
                    + "\"clientInfo\":{\"name\":\"beidou-skill-tech\",\"version\":\"1.0\"}}}";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            }
            int code = conn.getResponseCode();
            String resp;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(
                    code >= 400 ? conn.getErrorStream() : conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                resp = sb.toString();
            }
            r.put("mcpHttpStatus", code);
            r.put("mcpProbe", resp != null && resp.length() > 200 ? resp.substring(0, 200) : resp);
            r.put("clientTarget", clientImg);
            r.put("message", "orange-wz 可达，但后台 HTTP 无会话态无法可靠 paste。"
                    + " 请用 Cursor MCP（orange-wz）从服务端 XML 同步到: " + clientImg
                    + "（补 level/" + ((row.getSpMaxLevel() == null ? 0 : row.getSpMaxLevel()) + 1)
                    + ".." + row.getEffectMaxLevel() + "，并写 spMaxLevel）");
            // 标记为「需人工 MCP」但仍算服务端侧成功
            r.put("ok", false);
            r.put("needsMcpSession", true);
        } catch (Exception e) {
            r.put("message", "orange-wz MCP 不可用 (" + e.getMessage()
                    + ")。请启动 ensure-mcp.ps1 后，用 MCP 把服务端扩展节点同步到客户端 "
                    + clientImg);
            r.put("clientTarget", clientImg);
        }
        return r;
    }
}
