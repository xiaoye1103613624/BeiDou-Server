package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.LevelRewardSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.LevelRewardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 等级奖励控制器 —— 提供等级奖励配置的 REST API 接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/levelReward")
public class LevelRewardController {

    private final LevelRewardService levelRewardService;

    @Tag(name = "/levelReward/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有等级奖励配置（含道具列表）")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<LevelRewardSaveDTO>> getConfigList() {
        return ResultBody.success(levelRewardService.getConfigList());
    }

    @Tag(name = "/levelReward/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个等级奖励配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<LevelRewardSaveDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(levelRewardService.getConfigById(id));
    }

    @Tag(name = "/levelReward/" + ApiConstant.LATEST)
    @Operation(summary = "保存等级奖励配置（新增或更新，含道具列表）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<LevelRewardSaveDTO> saveConfig(@RequestBody SubmitBody<LevelRewardSaveDTO> request) {
        return ResultBody.success(request, levelRewardService.saveConfig(request.getData()));
    }

    @Tag(name = "/levelReward/" + ApiConstant.LATEST)
    @Operation(summary = "删除等级奖励配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        levelRewardService.deleteConfig(id);
        return ResultBody.success(null);
    }
}
