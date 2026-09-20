package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ChairPoseDetailReqDTO;
import org.gms.model.dto.ChairPoseDetailRtnDTO;
import org.gms.model.dto.ChairPoseEditorStatusRtnDTO;
import org.gms.model.dto.ChairPoseListReqDTO;
import org.gms.model.dto.ChairPoseListRtnDTO;
import org.gms.model.dto.ChairPosePatchReqDTO;
import org.gms.model.dto.ChairPosePatchRtnDTO;
import org.gms.model.dto.ChairPosePreviewReqDTO;
import org.gms.model.dto.ChairPosePreviewRtnDTO;
import org.gms.model.dto.ChairPoseWriteReqDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.TamingMobPoseDetailReqDTO;
import org.gms.model.dto.TamingMobPoseDetailRtnDTO;
import org.gms.model.dto.TamingMobPoseListReqDTO;
import org.gms.model.dto.TamingMobPoseListRtnDTO;
import org.gms.model.dto.TamingMobPosePreviewReqDTO;
import org.gms.model.dto.TamingMobPosePreviewRtnDTO;
import org.gms.model.dto.TamingMobPoseWriteReqDTO;
import org.gms.service.chair.ChairPoseClientSyncService;
import org.gms.service.chair.ClientChairPoseService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 座椅/骑宠姿态 REST（浏览 / 开发态写 / 娃娃预览 / 客户端同步）。
 * <p>
 * 管理端 UI 挂在「客户端」菜单（menu.client / client.ts）。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/clientChairPose")
public class ClientChairPoseController {
    private final ClientChairPoseService clientChairPoseService;
    private final ChairPoseClientSyncService chairPoseClientSyncService;

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "编辑器与客户端同步状态")
    @PostMapping("/" + ApiConstant.LATEST + "/status")
    public ResultBody<ChairPoseEditorStatusRtnDTO> status() {
        return ResultBody.success(clientChairPoseService.status());
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "椅子列表")
    @PostMapping("/" + ApiConstant.LATEST + "/chairs/list")
    public ResultBody<ChairPoseListRtnDTO> chairsList(@RequestBody SubmitBody<ChairPoseListReqDTO> request) {
        return ResultBody.success(request, clientChairPoseService.listChairs(request.getData()));
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "椅子详情")
    @PostMapping("/" + ApiConstant.LATEST + "/chairs/detail")
    public ResultBody<ChairPoseDetailRtnDTO> chairsDetail(@RequestBody SubmitBody<ChairPoseDetailReqDTO> request) {
        return ResultBody.success(request, clientChairPoseService.chairDetail(request.getData()));
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "写椅子姿态（服务端 WZ）")
    @PostMapping("/" + ApiConstant.LATEST + "/chairs/write")
    public ResultBody<Map<String, Object>> chairsWrite(@RequestBody SubmitBody<ChairPoseWriteReqDTO> request) {
        List<String> warnings = clientChairPoseService.writeChair(request.getData());
        Map<String, Object> body = new HashMap<>();
        body.put("message", clientChairPoseService.writeOkMessage());
        body.put("warnings", warnings);
        return ResultBody.success(request, body);
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "椅子娃娃预览")
    @PostMapping("/" + ApiConstant.LATEST + "/chairs/preview")
    public ResultBody<ChairPosePreviewRtnDTO> chairsPreview(@RequestBody SubmitBody<ChairPosePreviewReqDTO> request) {
        return ResultBody.success(request, clientChairPoseService.previewChair(request.getData()));
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "骑宠列表")
    @PostMapping("/" + ApiConstant.LATEST + "/tamingMobs/list")
    public ResultBody<TamingMobPoseListRtnDTO> tamingMobsList(
            @RequestBody SubmitBody<TamingMobPoseListReqDTO> request) {
        return ResultBody.success(request, clientChairPoseService.listTamingMobs(request.getData()));
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "骑宠详情")
    @PostMapping("/" + ApiConstant.LATEST + "/tamingMobs/detail")
    public ResultBody<TamingMobPoseDetailRtnDTO> tamingMobsDetail(
            @RequestBody SubmitBody<TamingMobPoseDetailReqDTO> request) {
        return ResultBody.success(request, clientChairPoseService.tamingMobDetail(request.getData()));
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "写骑宠姿态（服务端 WZ）")
    @PostMapping("/" + ApiConstant.LATEST + "/tamingMobs/write")
    public ResultBody<Map<String, Object>> tamingMobsWrite(
            @RequestBody SubmitBody<TamingMobPoseWriteReqDTO> request) {
        List<String> warnings = clientChairPoseService.writeTamingMob(request.getData());
        Map<String, Object> body = new HashMap<>();
        body.put("message", clientChairPoseService.writeOkMessage());
        body.put("warnings", warnings);
        return ResultBody.success(request, body);
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "骑宠预览")
    @PostMapping("/" + ApiConstant.LATEST + "/tamingMobs/preview")
    public ResultBody<TamingMobPosePreviewRtnDTO> tamingMobsPreview(
            @RequestBody SubmitBody<TamingMobPosePreviewReqDTO> request) {
        return ResultBody.success(request, clientChairPoseService.previewTamingMob(request.getData()));
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "客户端同步 dry-run")
    @PostMapping("/" + ApiConstant.LATEST + "/patch/dryRun")
    public ResultBody<ChairPosePatchRtnDTO> patchDryRun(@RequestBody SubmitBody<ChairPosePatchReqDTO> request) {
        ChairPosePatchReqDTO data = request.getData() == null ? new ChairPosePatchReqDTO() : request.getData();
        data.setDryRun(true);
        return ResultBody.success(request, chairPoseClientSyncService.sync(data));
    }

    @Tag(name = "/clientChairPose/" + ApiConstant.LATEST)
    @Operation(summary = "客户端同步 apply")
    @PostMapping("/" + ApiConstant.LATEST + "/patch/apply")
    public ResultBody<ChairPosePatchRtnDTO> patchApply(@RequestBody SubmitBody<ChairPosePatchReqDTO> request) {
        ChairPosePatchReqDTO data = request.getData() == null ? new ChairPosePatchReqDTO() : request.getData();
        data.setDryRun(false);
        return ResultBody.success(request, chairPoseClientSyncService.sync(data));
    }
}
