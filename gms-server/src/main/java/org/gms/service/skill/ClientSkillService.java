package org.gms.service.skill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.SkillFactory;
import org.gms.config.GameConfig;
import org.gms.config.SkillEditorProperties;
import org.gms.exception.BizException;
import org.gms.model.dto.SkillBookRtnDTO;
import org.gms.model.dto.SkillDetailRtnDTO;
import org.gms.model.dto.SkillEdgeDTO;
import org.gms.model.dto.SkillEditorStatusRtnDTO;
import org.gms.model.dto.SkillEnsureIconsReqDTO;
import org.gms.model.dto.SkillEnsureIconsRtnDTO;
import org.gms.model.dto.SkillJobLineDTO;
import org.gms.model.dto.SkillJobStageDTO;
import org.gms.model.dto.SkillLevelDTO;
import org.gms.model.dto.SkillLineageDTO;
import org.gms.model.dto.SkillNodeDTO;
import org.gms.model.dto.SkillReloadRtnDTO;
import org.gms.model.dto.SkillStringWriteReqDTO;
import org.gms.model.dto.SkillWriteReqDTO;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.server.icon.SharedIconFiles;
import org.gms.service.AssetService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 技能 Web 管理门面：浏览、写盘、图标、热重载、编辑器状态。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientSkillService {
    public static final String CONFIG_EDITOR_WRITE = "allow_skill_editor_write";
    public static final String CONFIG_CLIENT_WRITE = SkillClientSyncService.CONFIG_ALLOW_WRITE;

    private final SkillWzXmlStore wzXmlStore;
    private final AssetService assetService;
    private final SkillEditorProperties skillEditorProperties;
    private final SkillClientSyncService skillClientSyncService;

    public List<SkillLineageDTO> lineages() {
        List<SkillLineageDTO> list = SkillJobCatalog.lineages();
        for (SkillLineageDTO dto : list) {
            dto.setName(resolveCatalogName(dto.getNameKey()));
        }
        return list;
    }

    public List<SkillJobLineDTO> jobLines(String lineage) {
        RequireUtil.requireNotEmpty(lineage, "lineage");
        List<SkillJobLineDTO> list = SkillJobCatalog.jobLines(lineage);
        for (SkillJobLineDTO line : list) {
            line.setName(resolveCatalogName(line.getNameKey()));
            if (line.getStages() == null) {
                continue;
            }
            for (SkillJobStageDTO stage : line.getStages()) {
                stage.setName(resolveCatalogName(stage.getNameKey()));
            }
        }
        return list;
    }

    /** 解析服务端 message_*.properties 中的职业/系列名，供前端直接展示。 */
    private String resolveCatalogName(String nameKey) {
        if (nameKey == null || nameKey.isBlank()) {
            return "";
        }
        try {
            String name = I18nUtil.getMessage(nameKey);
            return name == null || name.isBlank() || name.equals(nameKey) ? nameKey : name;
        } catch (Exception e) {
            return nameKey;
        }
    }

    public SkillBookRtnDTO skillBook(Integer jobId) {
        RequireUtil.requireNotNull(jobId, I18nUtil.getExceptionMessage("SkillWzXmlStore.jobId.required"));
        Document doc = wzXmlStore.loadDocument(wzXmlStore.resolveSkillXml(jobId, true));
        Element root = doc.getDocumentElement();
        Element skillRoot = wzXmlStore.childByName(root, "skill");
        List<SkillNodeDTO> skills = new ArrayList<>();
        List<SkillEdgeDTO> edges = new ArrayList<>();
        if (skillRoot == null) {
            return SkillBookRtnDTO.builder().jobId(jobId).skills(skills).edges(edges).build();
        }
        for (Element skillEl : wzXmlStore.elementChildren(skillRoot)) {
            if (!"imgdir".equals(skillEl.getTagName())) {
                continue;
            }
            Integer skillId;
            try {
                skillId = Integer.parseInt(skillEl.getAttribute("name"));
            } catch (NumberFormatException e) {
                continue;
            }
            Map<Integer, Integer> req = wzXmlStore.readReq(skillEl);
            List<SkillLevelDTO> levels = wzXmlStore.readLevels(skillEl);
            Map<String, String> str = wzXmlStore.readStringFields(skillId);
            Integer invisible = wzXmlStore.getIntAttr(skillEl, "invisible");
            skills.add(SkillNodeDTO.builder()
                    .skillId(skillId)
                    .jobId(jobId)
                    .name(str.getOrDefault("name", String.valueOf(skillId)))
                    .maxLevel(levels.size())
                    .invisible(invisible != null && invisible != 0)
                    .masterLevel(wzXmlStore.getIntAttr(skillEl, "masterLevel"))
                    .iconUrl(resolveIconUrl(skillId))
                    .req(req)
                    .build());
            for (Map.Entry<Integer, Integer> e : req.entrySet()) {
                int from = e.getKey();
                boolean external = from / 10000 != jobId;
                edges.add(SkillEdgeDTO.builder()
                        .fromSkillId(from)
                        .toSkillId(skillId)
                        .reqLevel(e.getValue())
                        .external(external)
                        .build());
            }
        }
        return SkillBookRtnDTO.builder().jobId(jobId).skills(skills).edges(edges).build();
    }

    public SkillDetailRtnDTO skillDetail(Integer skillId) {
        RequireUtil.requireNotNull(skillId, I18nUtil.getExceptionMessage("SkillWzXmlStore.skillId.required"));
        int jobId = skillId / 10000;
        Document doc = wzXmlStore.loadDocument(wzXmlStore.resolveSkillXml(jobId, true));
        Element skillEl = wzXmlStore.findSkillElement(doc, skillId);
        if (skillEl == null) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("SkillWzXmlStore.skill.notFound", String.valueOf(skillId)));
        }
        Map<String, String> str = wzXmlStore.readStringFields(skillId);
        Map<String, String> hs = new LinkedHashMap<>();
        for (Map.Entry<String, String> e : str.entrySet()) {
            if (e.getKey() != null && e.getKey().startsWith("h")) {
                hs.put(e.getKey(), e.getValue());
            }
        }
        Integer invisible = wzXmlStore.getIntAttr(skillEl, "invisible");
        return SkillDetailRtnDTO.builder()
                .skillId(skillId)
                .jobId(jobId)
                .name(str.get("name"))
                .desc(str.get("desc"))
                .invisible(invisible != null && invisible != 0)
                .masterLevel(wzXmlStore.getIntAttr(skillEl, "masterLevel"))
                .iconUrl(resolveIconUrl(skillId))
                .req(wzXmlStore.readReq(skillEl))
                .levels(wzXmlStore.readLevels(skillEl))
                .hs(hs)
                .editorEnabled(isEditorEnabled())
                .build();
    }

    public void writeSkill(SkillWriteReqDTO req) {
        requireEditorEnabled();
        wzXmlStore.writeSkill(req);
    }

    public void writeString(SkillStringWriteReqDTO req) {
        requireEditorEnabled();
        wzXmlStore.writeString(req);
    }

    public SkillReloadRtnDTO reloadSkills() {
        requireEditorEnabled();
        SkillFactory.loadAllSkills();
        int count = SkillFactory.getLoadedSkillCount();
        log.info(I18nUtil.getLogMessage("ClientSkillService.reload.info"), count);
        return SkillReloadRtnDTO.builder()
                .skillCount(count)
                .message(I18nUtil.getMessage("ClientSkillService.reload.ok"))
                .build();
    }

    public SkillEnsureIconsRtnDTO ensureIcons(SkillEnsureIconsReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        List<Integer> ids = req.getSkillIds() == null ? List.of() : req.getSkillIds();
        boolean force = Boolean.TRUE.equals(req.getForce());
        int cached = 0;
        int failed = 0;
        Map<Integer, String> urls = new LinkedHashMap<>();
        for (Integer id : ids) {
            if (id == null || id <= 0) {
                failed++;
                continue;
            }
            Optional<byte[]> bytes = assetService.ensureIconBytes("skill", id, force);
            if (bytes.isPresent() || SharedIconFiles.pngExists("skill", id)) {
                cached++;
                urls.put(id, SharedIconFiles.webUrl("skill", id));
            } else {
                failed++;
                urls.put(id, "");
            }
        }
        return SkillEnsureIconsRtnDTO.builder()
                .requested(ids.size())
                .cached(cached)
                .failed(failed)
                .urls(urls)
                .build();
    }

    public SkillEditorStatusRtnDTO editorStatus() {
        Optional<Path> data = ClientDataPath.resolve();
        Optional<Path> en = skillClientSyncService.resolveEnPathPublic(data.orElse(null));
        Optional<Path> patcher = skillClientSyncService.findPatcherPublic();
        return SkillEditorStatusRtnDTO.builder()
                .editorEnabled(isEditorEnabled())
                .allowClientWrite(skillClientSyncService.isWriteAllowedPublic())
                .clientDataConfigured(data.isPresent())
                .clientDataPath(data.map(Path::toString).orElse(""))
                .clientEnConfigured(en.isPresent())
                .clientEnPath(en.map(Path::toString).orElse(""))
                .patcherAvailable(patcher.isPresent())
                .patcherPath(patcher.map(Path::toString).orElse(""))
                .configEditorWrite(CONFIG_EDITOR_WRITE)
                .configClientWrite(CONFIG_CLIENT_WRITE)
                .build();
    }

    public boolean isEditorEnabled() {
        if (skillEditorProperties.isEnabled()) {
            return true;
        }
        try {
            return GameConfig.getServerBoolean(CONFIG_EDITOR_WRITE);
        } catch (Exception e) {
            return false;
        }
    }

    public void requireEditorEnabled() {
        if (!isEditorEnabled()) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ClientSkillService.editorDisabled"));
        }
    }

    private String resolveIconUrl(int skillId) {
        if (SharedIconFiles.pngExists("skill", skillId)) {
            return SharedIconFiles.webUrl("skill", skillId);
        }
        return assetService.resolve("skill", skillId).getUrl();
    }
}
