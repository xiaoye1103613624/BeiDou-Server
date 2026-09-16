package org.gms.service.skill;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 抽样：WZ 节点名常为补零（0001001），按 int skillId 查找须兼容候选键。
 * 工作目录需为 gms-server（与 IDE / mvn -pl gms-server 一致）。
 */
class SkillWzXmlStoreLookupTest {

    @Test
    void stringSkillKeyCandidates_includePaddedForms() {
        List<String> keys1001 = SkillWzXmlStore.stringSkillKeyCandidates(1001);
        assertTrue(keys1001.contains("1001"));
        assertTrue(keys1001.contains("0001001"));
        assertTrue(keys1001.contains("00001001"));

        List<String> keysCygnus = SkillWzXmlStore.stringSkillKeyCandidates(10001001);
        assertTrue(keysCygnus.contains("10001001"));
    }

    @Test
    void findSkillElement_resolves1001And10001001() throws Exception {
        SkillWzXmlStore store = new SkillWzXmlStore();
        Document beginner = parse(Path.of("wz", "Skill.wz", "000.img.xml"));
        Element el1001 = store.findSkillElement(beginner, 1001);
        assertNotNull(el1001, "应找到补零节点 0001001");
        assertEquals("0001001", el1001.getAttribute("name"));

        Document noblesse = parse(Path.of("wz", "Skill.wz", "1000.img.xml"));
        Element elCygnus = store.findSkillElement(noblesse, 10001001);
        assertNotNull(elCygnus, "应找到骑士团技能 10001001");
        assertEquals("10001001", elCygnus.getAttribute("name"));
    }

    @Test
    void findStringSkillElement_resolvesPaddedName() throws Exception {
        SkillWzXmlStore store = new SkillWzXmlStore();
        Document doc = parse(Path.of("wz", "String.wz", "Skill.img.xml"));
        Element el = store.findStringSkillElement(doc.getDocumentElement(), 1001);
        assertNotNull(el);
        assertEquals("0001001", el.getAttribute("name"));
    }

    private static Document parse(Path file) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(false);
        Document doc = factory.newDocumentBuilder().parse(file.toFile());
        doc.getDocumentElement().normalize();
        return doc;
    }
}
