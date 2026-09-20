package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SkillBookReqDTO;
import org.gms.model.dto.SkillBookRtnDTO;
import org.gms.model.dto.SkillDetailReqDTO;
import org.gms.model.dto.SkillDetailRtnDTO;
import org.gms.model.dto.SkillEditorStatusRtnDTO;
import org.gms.model.dto.SkillEffectPreviewReqDTO;
import org.gms.model.dto.SkillEffectPreviewRtnDTO;
import org.gms.model.dto.SkillEnsureIconsReqDTO;
import org.gms.model.dto.SkillEnsureIconsRtnDTO;
import org.gms.model.dto.SkillJobLineDTO;
import org.gms.model.dto.SkillLineageDTO;
import org.gms.model.dto.SkillPatchReqDTO;
import org.gms.model.dto.SkillPatchRtnDTO;
import org.gms.model.dto.SkillReloadRtnDTO;
import org.gms.model.dto.SkillStringWriteReqDTO;
import org.gms.model.dto.SkillWriteReqDTO;
import org.gms.model.dto.SubmitBody;
import org.gms.service.skill.ClientSkillService;
import org.gms.service.skill.SkillClientSyncService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 技能资源 REST（浏览 / 开发态写 / 重载 / 客户端同步）。
 * <p>
 * 管理端 UI 必须挂在「客户端」菜单（menu.client / client.ts）。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/clientSkill")
public class ClientSkillController {
    private final ClientSkillService clientSkillService;
    private final SkillClientSyncService skillClientSyncService;

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "职业系列列表")
    @PostMapping("/" + ApiConstant.LATEST + "/lineages")
    public ResultBody<List<SkillLineageDTO>> lineages() {
        return ResultBody.success(clientSkillService.lineages());
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "某系列下的职业线")
    @PostMapping("/" + ApiConstant.LATEST + "/jobLines")
    public ResultBody<List<SkillJobLineDTO>> jobLines(@RequestBody SubmitBody<Map<String, Object>> request) {
        Map<String, Object> data = request.getData() == null ? Map.of() : request.getData();
        Object lineage = data.get("lineage");
        RequireUtil.requireNotNull(lineage, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "lineage"));
        return ResultBody.success(request, clientSkillService.jobLines(String.valueOf(lineage)));
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "某职业技能书（节点+前置边）")
    @PostMapping("/" + ApiConstant.LATEST + "/skillBook")
    public ResultBody<SkillBookRtnDTO> skillBook(@RequestBody SubmitBody<SkillBookReqDTO> request) {
        return ResultBody.success(request, clientSkillService.skillBook(request.getData().getJobId()));
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "技能详情")
    @PostMapping("/" + ApiConstant.LATEST + "/skillDetail")
    public ResultBody<SkillDetailRtnDTO> skillDetail(@RequestBody SubmitBody<SkillDetailReqDTO> request) {
        return ResultBody.success(request, clientSkillService.skillDetail(request.getData().getSkillId()));
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "补齐技能图标到 game-assets")
    @PostMapping("/" + ApiConstant.LATEST + "/ensureIcons")
    public ResultBody<SkillEnsureIconsRtnDTO> ensureIcons(@RequestBody SubmitBody<SkillEnsureIconsReqDTO> request) {
        return ResultBody.success(request, clientSkillService.ensureIcons(request.getData()));
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "技能 effect 多帧预览（WZ 元数据 + 客户端像素）")
    @PostMapping("/" + ApiConstant.LATEST + "/effectPreview")
    public ResultBody<SkillEffectPreviewRtnDTO> effectPreview(
            @RequestBody SubmitBody<SkillEffectPreviewReqDTO> request) {
        return ResultBody.success(request, clientSkillService.effectPreview(request.getData()));
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "编辑器/客户端同步状态")
    @PostMapping("/" + ApiConstant.LATEST + "/editorStatus")
    public ResultBody<SkillEditorStatusRtnDTO> editorStatus() {
        return ResultBody.success(clientSkillService.editorStatus());
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "写 Skill.wz 技能节点（双写；需开发开关）")
    @PostMapping("/" + ApiConstant.LATEST + "/skill/write")
    public ResultBody<Object> writeSkill(@RequestBody SubmitBody<SkillWriteReqDTO> request) {
        clientSkillService.writeSkill(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "写 String.wz Skill 文案（双写；需开发开关）")
    @PostMapping("/" + ApiConstant.LATEST + "/string/write")
    public ResultBody<Object> writeString(@RequestBody SubmitBody<SkillStringWriteReqDTO> request) {
        clientSkillService.writeString(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "热重载技能定义（需开发开关）")
    @PostMapping("/" + ApiConstant.LATEST + "/reloadSkills")
    public ResultBody<SkillReloadRtnDTO> reloadSkills() {
        return ResultBody.success(clientSkillService.reloadSkills());
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "客户端同步 dry-run（导出清单，不改 img）")
    @PostMapping("/" + ApiConstant.LATEST + "/patch/dryRun")
    public ResultBody<SkillPatchRtnDTO> patchDryRun(@RequestBody SubmitBody<SkillPatchReqDTO> request) {
        SkillPatchReqDTO data = request.getData() == null ? new SkillPatchReqDTO() : request.getData();
        data.setDryRun(true);
        return ResultBody.success(request, skillClientSyncService.sync(data));
    }

    @Tag(name = "/clientSkill/" + ApiConstant.LATEST)
    @Operation(summary = "客户端同步 apply（需 allow_skill_client_write）")
    @PostMapping("/" + ApiConstant.LATEST + "/patch/apply")
    public ResultBody<SkillPatchRtnDTO> patchApply(@RequestBody SubmitBody<SkillPatchReqDTO> request) {
        SkillPatchReqDTO data = request.getData() == null ? new SkillPatchReqDTO() : request.getData();
        data.setDryRun(false);
        return ResultBody.success(request, skillClientSyncService.sync(data));
    }
}
