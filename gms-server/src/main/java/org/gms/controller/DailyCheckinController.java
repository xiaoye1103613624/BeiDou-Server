package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.DailyCheckinRewardDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.DailyCheckinService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 每日签到奖励 Web 管理。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dailyCheckin")
public class DailyCheckinController {

    private final DailyCheckinService dailyCheckinService;

    @Tag(name = "/dailyCheckin/" + ApiConstant.LATEST)
    @Operation(summary = "获取签到奖励列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<DailyCheckinRewardDO>> list() {
        return ResultBody.success(dailyCheckinService.listAll());
    }

    @Tag(name = "/dailyCheckin/" + ApiConstant.LATEST)
    @Operation(summary = "保存单日签到奖励")
    @PostMapping("/" + ApiConstant.LATEST + "/save")
    public ResultBody<Object> save(@RequestBody SubmitBody<DailyCheckinRewardDO> request) {
        dailyCheckinService.save(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/dailyCheckin/" + ApiConstant.LATEST)
    @Operation(summary = "批量保存签到奖励")
    @PostMapping("/" + ApiConstant.LATEST + "/saveAll")
    public ResultBody<Object> saveAll(@RequestBody SubmitBody<List<DailyCheckinRewardDO>> request) {
        dailyCheckinService.saveAll(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/dailyCheckin/" + ApiConstant.LATEST)
    @Operation(summary = "热重载签到奖励")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        dailyCheckinService.reload();
        return ResultBody.success();
    }
}
