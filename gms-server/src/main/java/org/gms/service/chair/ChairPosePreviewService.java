package org.gms.service.chair;

import lombok.RequiredArgsConstructor;
import org.gms.model.dto.ChairPoseEffectDTO;
import org.gms.model.dto.ChairPosePreviewReqDTO;
import org.gms.model.dto.ChairPosePreviewRtnDTO;
import org.gms.model.dto.IconResolveRtnDTO;
import org.gms.model.dto.TamingMobPoseFrameDTO;
import org.gms.model.dto.TamingMobPosePreviewReqDTO;
import org.gms.model.dto.TamingMobPosePreviewRtnDTO;
import org.gms.server.cashshop.ClientDataPath;
import org.gms.server.icon.SharedIconFiles;
import org.gms.service.AssetService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * 娃娃预览：优先本地/客户端 PNG，否则图标 + 十字线坐标，再否则 NONE。
 */
@Service
@RequiredArgsConstructor
public class ChairPosePreviewService {
    private final ChairWzXmlStore chairWzXmlStore;
    private final TamingMobPoseWzXmlStore tamingMobPoseWzXmlStore;
    private final AssetService assetService;

    public ChairPosePreviewRtnDTO previewChair(ChairPosePreviewReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getItemId(),
                I18nUtil.getExceptionMessage("ClientChairPoseService.itemId.required"));
        String layer = StringUtils.hasText(req.getLayer()) ? req.getLayer().trim() : "effect";

        Path install = chairWzXmlStore.resolveInstallXml(true);
        Document doc = chairWzXmlStore.loadInstallDocumentCached(true);
        Element chairEl = chairWzXmlStore.findChairElement(doc, req.getItemId());
        if (chairEl == null) {
            return ChairPosePreviewRtnDTO.builder()
                    .mode("NONE")
                    .message(I18nUtil.getMessage("ChairPosePreview.mode.none"))
                    .build();
        }
        ChairPoseEffectDTO effect = chairWzXmlStore.readEffectLayer(chairEl, layer);
        if (effect == null && !"effect".equals(layer)) {
            effect = chairWzXmlStore.readEffectLayer(chairEl, "effect");
        }
        Integer ox = effect == null ? null : effect.getOriginX();
        Integer oy = effect == null ? null : effect.getOriginY();
        Integer cw = effect == null ? null : effect.getCanvasWidth();
        Integer ch = effect == null ? null : effect.getCanvasHeight();

        Optional<String> clientPng = findClientOrLocalPng(req.getItemId());
        String iconUrl = resolveItemIconUrl(req.getItemId());
        if (clientPng.isPresent()) {
            return ChairPosePreviewRtnDTO.builder()
                    .mode("CLIENT_PNG")
                    .imageUrl(clientPng.get())
                    .iconUrl(iconUrl)
                    .originX(ox)
                    .originY(oy)
                    .canvasWidth(cw)
                    .canvasHeight(ch)
                    .message(I18nUtil.getMessage("ChairPosePreview.mode.clientPng"))
                    .build();
        }
        if (StringUtils.hasText(iconUrl)) {
            return ChairPosePreviewRtnDTO.builder()
                    .mode("ICON_FALLBACK")
                    .imageUrl(iconUrl)
                    .iconUrl(iconUrl)
                    .originX(ox)
                    .originY(oy)
                    .canvasWidth(cw)
                    .canvasHeight(ch)
                    .message(I18nUtil.getMessage("ChairPosePreview.mode.iconFallback"))
                    .build();
        }
        return ChairPosePreviewRtnDTO.builder()
                .mode("NONE")
                .originX(ox)
                .originY(oy)
                .canvasWidth(cw)
                .canvasHeight(ch)
                .message(I18nUtil.getMessage("ChairPosePreview.mode.none"))
                .build();
    }

    public TamingMobPosePreviewRtnDTO previewTamingMob(TamingMobPosePreviewReqDTO req) {
        RequireUtil.requireNotNull(req, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        RequireUtil.requireNotNull(req.getMobId(),
                I18nUtil.getExceptionMessage("ClientChairPoseService.mobId.required"));

        Path xml = tamingMobPoseWzXmlStore.resolveTamingXml(req.getMobId(), true);
        Document doc = tamingMobPoseWzXmlStore.loadDocument(xml);
        String action = StringUtils.hasText(req.getAction())
                ? req.getAction().trim()
                : tamingMobPoseWzXmlStore.resolveDefaultAction(doc);
        int frameIndex = req.getFrameIndex() == null ? 0 : req.getFrameIndex();
        List<TamingMobPoseFrameDTO> frames = tamingMobPoseWzXmlStore.readFrames(doc, action);
        TamingMobPoseFrameDTO frame = null;
        for (TamingMobPoseFrameDTO f : frames) {
            if (f.getFrameIndex() != null && f.getFrameIndex() == frameIndex) {
                frame = f;
                break;
            }
        }
        if (frame == null && !frames.isEmpty()) {
            frame = frames.get(0);
            frameIndex = frame.getFrameIndex() == null ? 0 : frame.getFrameIndex();
        }

        Integer nx = frame == null ? null : frame.getNavelX();
        Integer ny = frame == null ? null : frame.getNavelY();
        Integer ox = frame == null ? null : frame.getOriginX();
        Integer oy = frame == null ? null : frame.getOriginY();
        Integer cw = frame == null ? null : frame.getCanvasWidth();
        Integer ch = frame == null ? null : frame.getCanvasHeight();

        Optional<String> clientPng = findClientOrLocalPng(req.getMobId());
        String iconUrl = resolveItemIconUrl(req.getMobId());
        if (clientPng.isPresent()) {
            return TamingMobPosePreviewRtnDTO.builder()
                    .mode("CLIENT_PNG")
                    .imageUrl(clientPng.get())
                    .iconUrl(iconUrl)
                    .action(action)
                    .frameIndex(frameIndex)
                    .navelX(nx)
                    .navelY(ny)
                    .originX(ox)
                    .originY(oy)
                    .canvasWidth(cw)
                    .canvasHeight(ch)
                    .message(I18nUtil.getMessage("ChairPosePreview.mode.clientPng"))
                    .build();
        }
        if (StringUtils.hasText(iconUrl)) {
            return TamingMobPosePreviewRtnDTO.builder()
                    .mode("ICON_FALLBACK")
                    .imageUrl(iconUrl)
                    .iconUrl(iconUrl)
                    .action(action)
                    .frameIndex(frameIndex)
                    .navelX(nx)
                    .navelY(ny)
                    .originX(ox)
                    .originY(oy)
                    .canvasWidth(cw)
                    .canvasHeight(ch)
                    .message(I18nUtil.getMessage("ChairPosePreview.mode.iconFallback"))
                    .build();
        }
        return TamingMobPosePreviewRtnDTO.builder()
                .mode("NONE")
                .action(action)
                .frameIndex(frameIndex)
                .navelX(nx)
                .navelY(ny)
                .originX(ox)
                .originY(oy)
                .canvasWidth(cw)
                .canvasHeight(ch)
                .message(I18nUtil.getMessage("ChairPosePreview.mode.none"))
                .build();
    }

    private Optional<String> findClientOrLocalPng(int objectId) {
        if (SharedIconFiles.pngExists("item", objectId)) {
            return Optional.of(SharedIconFiles.webUrl("item", objectId));
        }
        // 尝试 ensure 一次（会旁路客户端 web_png / item-icons）
        assetService.ensureItemIconBytes(objectId, false);
        if (SharedIconFiles.pngExists("item", objectId)) {
            return Optional.of(SharedIconFiles.webUrl("item", objectId));
        }
        Optional<Path> data = ClientDataPath.resolve();
        if (data.isPresent()) {
            Path root = data.get();
            String[] relative = {
                    "web_png/" + objectId + ".png",
                    "item-icons/" + objectId + ".png"
            };
            for (String rel : relative) {
                Path p = root.resolve(rel);
                if (Files.isRegularFile(p)) {
                    // 仅当已拷入 game-assets 才有稳定 web URL；否则仍走 icon resolve
                    break;
                }
            }
            if (root.getParent() != null) {
                Path sibling = root.getParent().resolve("web_png").resolve(objectId + ".png");
                if (Files.isRegularFile(sibling) && SharedIconFiles.pngExists("item", objectId)) {
                    return Optional.of(SharedIconFiles.webUrl("item", objectId));
                }
            }
        }
        return Optional.empty();
    }

    private String resolveItemIconUrl(int objectId) {
        if (SharedIconFiles.pngExists("item", objectId)) {
            return SharedIconFiles.webUrl("item", objectId);
        }
        IconResolveRtnDTO resolved = assetService.resolve("item", objectId);
        return resolved == null || resolved.getUrl() == null ? "" : resolved.getUrl();
    }
}
