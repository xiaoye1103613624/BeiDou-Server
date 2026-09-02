package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ActivityActionDTO;
import org.gms.model.dto.ActivityClaimDTO;
import org.gms.model.dto.ActivityRewardTierDTO;
import org.gms.model.dto.ActivityScheduleDTO;
import org.gms.model.dto.ActivitySettleDTO;
import org.gms.model.dto.ActivityStatusDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.activity.ActivityAdminService;
import org.gms.service.activity.ActivityRewardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activity")
public class ActivityController {

    private final ActivityAdminService activityAdminService;
    private final ActivityRewardService activityRewardService;

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "活动列表与实时状态")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<ActivityStatusDTO>> list() {
        return ResultBody.success(activityAdminService.listStatus());
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "启用/禁用活动")
    @PostMapping("/" + ApiConstant.LATEST + "/setEnabled")
    public ResultBody<ActivityStatusDTO> setEnabled(@RequestBody SubmitBody<ActivityActionDTO> request) {
        return ResultBody.success(request, activityAdminService.setEnabled(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "开放报名（可带计划开始时间）")
    @PostMapping("/" + ApiConstant.LATEST + "/openRegistration")
    public ResultBody<ActivityStatusDTO> openRegistration(@RequestBody SubmitBody<ActivityActionDTO> request) {
        return ResultBody.success(request, activityAdminService.openRegistration(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "关闭报名")
    @PostMapping("/" + ApiConstant.LATEST + "/closeRegistration")
    public ResultBody<ActivityStatusDTO> closeRegistration(@RequestBody SubmitBody<ActivityActionDTO> request) {
        return ResultBody.success(request, activityAdminService.closeRegistration(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "开始活动")
    @PostMapping("/" + ApiConstant.LATEST + "/start")
    public ResultBody<ActivityStatusDTO> start(@RequestBody SubmitBody<ActivityActionDTO> request) {
        return ResultBody.success(request, activityAdminService.startActivity(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "停止活动")
    @PostMapping("/" + ApiConstant.LATEST + "/stop")
    public ResultBody<ActivityStatusDTO> stop(@RequestBody SubmitBody<ActivityActionDTO> request) {
        return ResultBody.success(request, activityAdminService.stopActivity(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "停止并清场")
    @PostMapping("/" + ApiConstant.LATEST + "/stopAndClear")
    public ResultBody<ActivityStatusDTO> stopAndClear(@RequestBody SubmitBody<ActivityActionDTO> request) {
        return ResultBody.success(request, activityAdminService.stopAndClear(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "全部传出")
    @PostMapping("/" + ApiConstant.LATEST + "/warpAllOut")
    public ResultBody<ActivityStatusDTO> warpAllOut(@RequestBody SubmitBody<ActivityActionDTO> request) {
        return ResultBody.success(request, activityAdminService.warpAllOut(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "排期列表")
    @GetMapping("/" + ApiConstant.LATEST + "/schedules")
    public ResultBody<List<ActivityScheduleDTO>> schedules() {
        return ResultBody.success(activityAdminService.listSchedules());
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "保存排期")
    @PostMapping("/" + ApiConstant.LATEST + "/saveSchedule")
    public ResultBody<ActivityScheduleDTO> saveSchedule(@RequestBody SubmitBody<ActivityScheduleDTO> request) {
        return ResultBody.success(request, activityAdminService.saveSchedule(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "删除排期")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteSchedule")
    public ResultBody<Object> deleteSchedule(@RequestBody SubmitBody<ActivityScheduleDTO> request) {
        activityAdminService.deleteSchedule(request.getData().getId());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "删除排期（query）")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteScheduleById")
    public ResultBody<Object> deleteScheduleById(@RequestParam Long id) {
        activityAdminService.deleteSchedule(id);
        return ResultBody.success();
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "奖励档位列表")
    @GetMapping("/" + ApiConstant.LATEST + "/rewardTiers")
    public ResultBody<List<ActivityRewardTierDTO>> rewardTiers(
            @RequestParam(required = false) String activityCode) {
        return ResultBody.success(activityRewardService.listTiers(activityCode));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "保存奖励档位")
    @PostMapping("/" + ApiConstant.LATEST + "/saveRewardTier")
    public ResultBody<ActivityRewardTierDTO> saveRewardTier(
            @RequestBody SubmitBody<ActivityRewardTierDTO> request) {
        return ResultBody.success(request, activityRewardService.saveTier(request.getData()));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "删除奖励档位")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteRewardTier")
    public ResultBody<Object> deleteRewardTier(@RequestBody SubmitBody<ActivityRewardTierDTO> request) {
        activityRewardService.deleteTier(request.getData().getId());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "结算并发奖（可手工成绩）")
    @PostMapping("/" + ApiConstant.LATEST + "/settle")
    public ResultBody<Integer> settle(@RequestBody SubmitBody<ActivitySettleDTO> request) {
        return ResultBody.success(request, activityRewardService.settle(request.getData(), null));
    }

    @Tag(name = "/activity/" + ApiConstant.LATEST)
    @Operation(summary = "场次领取单列表")
    @GetMapping("/" + ApiConstant.LATEST + "/sessionClaims")
    public ResultBody<List<ActivityClaimDTO>> sessionClaims(@RequestParam Long sessionId) {
        return ResultBody.success(activityRewardService.listSessionClaims(sessionId));
    }
}
