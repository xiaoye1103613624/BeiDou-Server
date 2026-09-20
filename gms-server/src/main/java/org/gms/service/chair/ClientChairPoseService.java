package org.gms.service.chair;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.ChairEditorProperties;
import org.gms.config.GameConfig;
import org.gms.exception.BizException;
import org.gms.model.dto.ChairPoseDetailReqDTO;
import org.gms.model.dto.ChairPoseDetailRtnDTO;
import org.gms.model.dto.ChairPoseEditorStatusRtnDTO;
import org.gms.model.dto.ChairPoseEffectDTO;
import org.gms.model.dto.ChairPoseItemDTO;
import org.gms.model.dto.ChairPoseListReqDTO;
import org.gms.model.dto.ChairPoseListRtnDTO;
import org.gms.model.dto.ChairPosePreviewReqDTO;
import org.gms.model.dto.ChairPosePreviewRtnDTO;
import org.gms.model.dto.ChairPoseWriteReqDTO;
import org.gms.model.dto.IconResolveRtnDTO;
import org.gms.model.dto.TamingMobPoseDetailReqDTO;
import org.gms.model.dto.TamingMobPoseDetailRtnDTO;
import org.gms.model.dto.TamingMobPoseFrameDTO;
import org.gms.model.dto.TamingMobPoseItemDTO;
import org.gms.model.dto.TamingMobPoseListReqDTO;
import org.gms.model.dto.TamingMobPoseListRtnDTO;
import org.gms.model.dto.TamingMobPosePreviewReqDTO;
import org.gms.model.dto.TamingMobPosePreviewRtnDTO;
import org.gms.model.dto.TamingMobPoseWriteReqDTO;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.server.icon.SharedIconFiles;
import org.gms.service.AssetService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * 座椅/骑宠姿态管理：列表/详情/写服务端 + 状态聚合。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClientChairPoseService {
    public static final String CONFIG_EDITOR_WRITE = "allow_chair_editor_write";
    public static final String CONFIG_CLIENT_WRITE = ChairPoseClientSyncService.CONFIG_ALLOW_WRITE;

    private final ChairEditorProperties chairEditorProperties;
    private final ChairWzXmlStore chairWzXmlStore;
    private final TamingMobPoseWzXmlStore tamingMobPoseWzXmlStore;
    private final ChairPosePreviewService chairPosePreviewService;
    private final ChairPoseClientSyncService chairPoseClientSyncService;
    private final AssetService assetService;

    public boolean isEditorEnabled() {
        if (chairEditorProperties.isEnabled()) {
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
                    I18nUtil.getExceptionMessage("ClientChairPoseService.editorDisabled"));
        }
    }

    public ChairPoseEditorStatusRtnDTO status() {
        Optional<Path> data = ClientDataPath.resolve();
        Optional<Path> en = chairPoseClientSyncService.resolveEnPathPublic(data.orElse(null));
        Optional<Path> patcher = chairPoseClientSyncService.findPatcherPublic();
        return ChairPoseEditorStatusRtnDTO.builder()
                .editorEnabled(isEditorEnabled())
                .allowClientWrite(chairPoseClientSyncService.isWriteAllowedPublic())
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

    public ChairPoseListRtnDTO listChairs(ChairPoseListReqDTO req) {
        ChairPoseListReqDTO safe = req == null ? new ChairPoseListReqDTO() : req;
        int page = safe.getPage() == null || safe.getPage() < 1 ? 1 : safe.getPage();
        int pageSize = safe.getPageSize() == null || safe.getPageSize() < 1 ? 50 : Math.min(safe.getPageSize(), 200);
        String keyword = safe.getKeyword() == null ? "" : safe.getKeyword().trim().toLowerCase(Locale.ROOT);
        boolean effect2Only = Boolean.TRUE.equals(safe.getEffect2Only());

        // Install / Ins 均走 mtime 缓存，避免每次搜索重新 parse
        Path install = chairWzXmlStore.resolveInstallXml(true);
        Document doc = chairWzXmlStore.loadInstallDocumentCached(true);
        Map<String, String> nameIndex = chairWzXmlStore.loadInsNameIndex();
        List<ChairPoseItemDTO> matched = new ArrayList<>();
        for (Element el : chairWzXmlStore.listChairElements(doc)) {
            Integer itemId = ChairWzXmlStore.parseChairItemId(el.getAttribute("name"));
            if (itemId == null) {
                continue;
            }
            boolean hasEffect = chairWzXmlStore.childByName(el, "effect") != null;
            boolean hasEffect2 = chairWzXmlStore.childByName(el, "effect2") != null;
            if (effect2Only && !hasEffect2) {
                continue;
            }
            String name = nameIndex.getOrDefault(String.valueOf(itemId), "");
            if (StringUtils.hasText(keyword)) {
                String idStr = String.valueOf(itemId);
                if (!idStr.contains(keyword) && !name.toLowerCase(Locale.ROOT).contains(keyword)) {
                    continue;
                }
            }
            matched.add(ChairPoseItemDTO.builder()
                    .itemId(itemId)
                    .name(name)
                    .hasEffect(hasEffect)
                    .hasEffect2(hasEffect2)
                    .iconUrl(listIconUrl(itemId))
                    .sourceFile(install.toString())
                    .build());
        }
        int total = matched.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        return ChairPoseListRtnDTO.builder()
                .total((long) total)
                .items(matched.subList(from, to))
                .build();
    }

    public ChairPoseDetailRtnDTO chairDetail(ChairPoseDetailReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getItemId(),
                I18nUtil.getExceptionMessage("ClientChairPoseService.itemId.required"));
        Path install = chairWzXmlStore.resolveInstallXml(true);
        Document doc = chairWzXmlStore.loadInstallDocumentCached(true);
        Element chairEl = chairWzXmlStore.findChairElement(doc, req.getItemId());
        if (chairEl == null) {
            throw BizException.illegalArgument(
                    I18nUtil.getExceptionMessage("ChairWzXmlStore.chair.notFound", req.getItemId()));
        }
        Map<String, String> str = chairWzXmlStore.readInsString(req.getItemId());
        List<ChairPoseEffectDTO> effects = chairWzXmlStore.readEffects(chairEl);
        return ChairPoseDetailRtnDTO.builder()
                .itemId(req.getItemId())
                .name(str.getOrDefault("name", ""))
                .desc(str.getOrDefault("desc", ""))
                .iconUrl(resolveItemIconUrl(req.getItemId()))
                .sourceFile(install.toString())
                .editorEnabled(isEditorEnabled())
                .effects(effects)
                .build();
    }

    public List<String> writeChair(ChairPoseWriteReqDTO req) {
        requireEditorEnabled();
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        List<String> warnings = chairWzXmlStore.writeChair(req);
        chairWzXmlStore.invalidateInstallCache();
        log.info(I18nUtil.getLogMessage("ClientChairPoseService.write.info"), req.getItemId(), null);
        return warnings;
    }

    public ChairPosePreviewRtnDTO previewChair(ChairPosePreviewReqDTO req) {
        return chairPosePreviewService.previewChair(req);
    }

    public TamingMobPoseListRtnDTO listTamingMobs(TamingMobPoseListReqDTO req) {
        TamingMobPoseListReqDTO safe = req == null ? new TamingMobPoseListReqDTO() : req;
        int page = safe.getPage() == null || safe.getPage() < 1 ? 1 : safe.getPage();
        int pageSize = safe.getPageSize() == null || safe.getPageSize() < 1 ? 50 : Math.min(safe.getPageSize(), 200);
        String keyword = safe.getKeyword() == null ? "" : safe.getKeyword().trim().toLowerCase(Locale.ROOT);

        // 骑宠 ID 列表 + Eqp 名索引均缓存；列表行不再逐个 parse XML（否则千级目录会卡死）
        List<Integer> ids = tamingMobPoseWzXmlStore.listAllMobIdsCached();
        Map<String, String> nameIndex = tamingMobPoseWzXmlStore.loadTamingNameIndex();
        List<Integer> matchedIds = new ArrayList<>();
        Map<Integer, String> matchedNames = new LinkedHashMap<>();
        for (Integer mobId : ids) {
            String name = nameIndex.getOrDefault(String.valueOf(mobId), "");
            if (StringUtils.hasText(keyword)) {
                String idStr = String.valueOf(mobId);
                if (!idStr.contains(keyword) && !name.toLowerCase(Locale.ROOT).contains(keyword)) {
                    continue;
                }
            }
            matchedIds.add(mobId);
            matchedNames.put(mobId, name);
        }
        int total = matchedIds.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);
        List<TamingMobPoseItemDTO> pageItems = new ArrayList<>();
        for (Integer mobId : matchedIds.subList(from, to)) {
            Path xml = tamingMobPoseWzXmlStore.resolveTamingXml(mobId, true);
            pageItems.add(TamingMobPoseItemDTO.builder()
                    .mobId(mobId)
                    .name(matchedNames.getOrDefault(mobId, ""))
                    // hasNavel / defaultAction 改到详情接口再算，列表只做 ID+名称过滤
                    .hasNavel(null)
                    .defaultAction(null)
                    .iconUrl(listIconUrl(mobId))
                    .sourceFile(xml.toString())
                    .build());
        }
        return TamingMobPoseListRtnDTO.builder()
                .total((long) total)
                .items(pageItems)
                .build();
    }

    public TamingMobPoseDetailRtnDTO tamingMobDetail(TamingMobPoseDetailReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getMobId(),
                I18nUtil.getExceptionMessage("ClientChairPoseService.mobId.required"));
        Path xml = tamingMobPoseWzXmlStore.resolveTamingXml(req.getMobId(), true);
        Document doc = tamingMobPoseWzXmlStore.loadDocument(xml);
        String defaultAction = tamingMobPoseWzXmlStore.resolveDefaultAction(doc);
        List<String> actions = tamingMobPoseWzXmlStore.listActions(doc);
        List<TamingMobPoseFrameDTO> frames = tamingMobPoseWzXmlStore.readDefaultFrames(doc);
        Map<String, String> str = tamingMobPoseWzXmlStore.readEqpString(req.getMobId());
        return TamingMobPoseDetailRtnDTO.builder()
                .mobId(req.getMobId())
                .name(str.getOrDefault("name", ""))
                .desc(str.getOrDefault("desc", ""))
                .iconUrl(resolveItemIconUrl(req.getMobId()))
                .sourceFile(xml.toString())
                .defaultAction(defaultAction)
                .editorEnabled(isEditorEnabled())
                .actions(actions)
                .frames(frames)
                .build();
    }

    public List<String> writeTamingMob(TamingMobPoseWriteReqDTO req) {
        requireEditorEnabled();
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        List<String> warnings = tamingMobPoseWzXmlStore.writePose(req);
        log.info(I18nUtil.getLogMessage("ClientChairPoseService.write.info"), null, req.getMobId());
        return warnings;
    }

    public TamingMobPosePreviewRtnDTO previewTamingMob(TamingMobPosePreviewReqDTO req) {
        return chairPosePreviewService.previewTamingMob(req);
    }

    public String writeOkMessage() {
        return I18nUtil.getMessage("ClientChairPoseService.write.ok");
    }

    /**
     * 列表图标：给稳定本地路径，由前端 ItemIcon 缺失时回退 CDN。
     * 禁止在列表里对每个 ID 调 assetService.resolve（会放大延迟）。
     */
    private String listIconUrl(int objectId) {
        return SharedIconFiles.webUrl("item", objectId);
    }

    private String resolveItemIconUrl(int objectId) {
        if (SharedIconFiles.pngExists("item", objectId)) {
            return SharedIconFiles.webUrl("item", objectId);
        }
        IconResolveRtnDTO resolved = assetService.resolve("item", objectId);
        return resolved == null || resolved.getUrl() == null ? "" : resolved.getUrl();
    }
}
