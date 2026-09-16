package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.QuestChainRtnDTO;
import org.gms.model.dto.QuestDetailRtnDTO;
import org.gms.model.dto.QuestForceReqDTO;
import org.gms.model.dto.QuestListRtnDTO;
import org.gms.model.dto.QuestProgressReqDTO;
import org.gms.model.dto.QuestProgressRtnDTO;
import org.gms.model.dto.QuestScriptReqDTO;
import org.gms.model.dto.QuestSearchReqDTO;
import org.gms.model.dto.QuestSyncClientReqDTO;
import org.gms.model.dto.QuestSyncClientRtnDTO;
import org.gms.model.dto.QuestWriteReqDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.QuestAdminService;
import org.gms.service.quest.QuestClientSyncService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 任务管理 REST：WZ 定义 CRUD、完成态、强制态、脚本、客户端同步。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/quest")
public class QuestController {
    private final QuestAdminService questAdminService;
    private final QuestClientSyncService questClientSyncService;

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "分页搜索任务定义")
    @PostMapping("/" + ApiConstant.LATEST + "/getQuestList")
    public ResultBody<Page<QuestListRtnDTO>> getQuestList(@RequestBody SubmitBody<QuestSearchReqDTO> request) {
        return ResultBody.success(request, questAdminService.getQuestList(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "任务详情")
    @PostMapping("/" + ApiConstant.LATEST + "/getQuestDetail")
    public ResultBody<QuestDetailRtnDTO> getQuestDetail(@RequestBody SubmitBody<Integer> request) {
        RequireUtil.requireNotNull(request.getData(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        return ResultBody.success(request, questAdminService.getQuestDetail(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "任务完整链路图（nextQuest + 前置双向展开）")
    @PostMapping("/" + ApiConstant.LATEST + "/getQuestChain")
    public ResultBody<QuestChainRtnDTO> getQuestChain(@RequestBody SubmitBody<Integer> request) {
        RequireUtil.requireNotNull(request.getData(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        return ResultBody.success(request, questAdminService.getQuestChain(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "新建任务（写 WZ）")
    @PutMapping("/" + ApiConstant.LATEST + "/addQuest")
    public ResultBody<Integer> addQuest(@RequestBody SubmitBody<QuestWriteReqDTO> request) {
        return ResultBody.success(request, questAdminService.addQuest(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "更新任务（写 WZ）")
    @PostMapping("/" + ApiConstant.LATEST + "/updateQuest")
    public ResultBody<Object> updateQuest(@RequestBody SubmitBody<QuestWriteReqDTO> request) {
        questAdminService.updateQuest(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "删除任务定义（不清理进度表）")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteQuest/{questId}")
    public ResultBody<Object> deleteQuest(@PathVariable("questId") Integer questId) {
        questAdminService.deleteQuest(questId);
        return ResultBody.success(null);
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "热重载任务定义与脚本")
    @PostMapping("/" + ApiConstant.LATEST + "/publish")
    public ResultBody<Map<String, Object>> publish() {
        return ResultBody.success(questAdminService.publish());
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "按任务查询角色完成态")
    @PostMapping("/" + ApiConstant.LATEST + "/getCompletions")
    public ResultBody<Page<QuestProgressRtnDTO>> getCompletions(@RequestBody SubmitBody<QuestProgressReqDTO> request) {
        return ResultBody.success(request, questAdminService.getCompletions(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "按角色查询任务完成态")
    @PostMapping("/" + ApiConstant.LATEST + "/getCharacterQuests")
    public ResultBody<Page<QuestProgressRtnDTO>> getCharacterQuests(
            @RequestBody SubmitBody<QuestProgressReqDTO> request) {
        return ResultBody.success(request, questAdminService.getCharacterQuests(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "强制接取")
    @PostMapping("/" + ApiConstant.LATEST + "/forceStart")
    public ResultBody<Map<String, Object>> forceStart(@RequestBody SubmitBody<QuestForceReqDTO> request) {
        return ResultBody.success(request, questAdminService.forceStart(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "强制完成")
    @PostMapping("/" + ApiConstant.LATEST + "/forceComplete")
    public ResultBody<Map<String, Object>> forceComplete(@RequestBody SubmitBody<QuestForceReqDTO> request) {
        return ResultBody.success(request, questAdminService.forceComplete(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "重置任务进度")
    @PostMapping("/" + ApiConstant.LATEST + "/reset")
    public ResultBody<Map<String, Object>> reset(@RequestBody SubmitBody<QuestForceReqDTO> request) {
        return ResultBody.success(request, questAdminService.reset(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "读取任务脚本")
    @PostMapping("/" + ApiConstant.LATEST + "/script/read")
    public ResultBody<Map<String, Object>> readScript(@RequestBody SubmitBody<QuestScriptReqDTO> request) {
        return ResultBody.success(request, questAdminService.readScript(request.getData()));
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "写入任务脚本")
    @PostMapping("/" + ApiConstant.LATEST + "/script/write")
    public ResultBody<Object> writeScript(@RequestBody SubmitBody<QuestScriptReqDTO> request) {
        questAdminService.writeScript(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "客户端同步状态（路径/开关/patcher）")
    @GetMapping("/" + ApiConstant.LATEST + "/syncClient/status")
    public ResultBody<Map<String, Object>> syncClientStatus() {
        return ResultBody.success(questClientSyncService.status());
    }

    @Tag(name = "/quest/" + ApiConstant.LATEST)
    @Operation(summary = "同步任务到客户端（dryRun 或实写；生产默认关写）")
    @PostMapping("/" + ApiConstant.LATEST + "/syncClient")
    public ResultBody<QuestSyncClientRtnDTO> syncClient(@RequestBody SubmitBody<QuestSyncClientReqDTO> request) {
        return ResultBody.success(request, questClientSyncService.sync(request.getData()));
    }
}
