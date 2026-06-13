package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.DailyDungeonSaveDTO;
import org.gms.model.dto.DailyDungeonSaveDTO.DailyRewardDTO;
import org.gms.model.dto.DailyDungeonSaveDTO.VipConfigDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.DailyDungeonService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 每日副本控制器 —— 提供副本配置、每日奖励、VIP配置的 REST API
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dailyDungeon")
public class DailyDungeonController {

    private final DailyDungeonService service;

    // ==================== 副本配置 ====================

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有每日副本配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<DailyDungeonSaveDTO>> getConfigList() {
        return ResultBody.success(service.getConfigList());
    }

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个每日副本配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<DailyDungeonSaveDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(service.getConfigById(id));
    }

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "保存每日副本配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<DailyDungeonSaveDTO> saveConfig(@RequestBody SubmitBody<DailyDungeonSaveDTO> request) {
        return ResultBody.success(request, service.saveConfig(request.getData()));
    }

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "删除每日副本配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        service.deleteConfig(id);
        return ResultBody.success(null);
    }

    // ==================== 每日完成奖励 ====================

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "获取每日完成奖励列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getDailyRewardList")
    public ResultBody<List<DailyRewardDTO>> getDailyRewardList() {
        return ResultBody.success(service.getDailyRewardList());
    }

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "保存每日完成奖励")
    @PostMapping("/" + ApiConstant.LATEST + "/saveDailyReward")
    public ResultBody<DailyRewardDTO> saveDailyReward(@RequestBody SubmitBody<DailyRewardDTO> request) {
        return ResultBody.success(request, service.saveDailyReward(request.getData()));
    }

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "删除每日完成奖励")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteDailyReward/{id}")
    public ResultBody<Object> deleteDailyReward(@PathVariable("id") Long id) {
        service.deleteDailyReward(id);
        return ResultBody.success(null);
    }

    // ==================== VIP物品配置 ====================

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "获取VIP物品配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getVipConfigList")
    public ResultBody<List<VipConfigDTO>> getVipConfigList() {
        return ResultBody.success(service.getVipConfigList());
    }

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "保存VIP物品配置")
    @PostMapping("/" + ApiConstant.LATEST + "/saveVipConfig")
    public ResultBody<VipConfigDTO> saveVipConfig(@RequestBody SubmitBody<VipConfigDTO> request) {
        return ResultBody.success(request, service.saveVipConfig(request.getData()));
    }

    @Tag(name = "/dailyDungeon/" + ApiConstant.LATEST)
    @Operation(summary = "删除VIP物品配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteVipConfig/{id}")
    public ResultBody<Object> deleteVipConfig(@PathVariable("id") Long id) {
        service.deleteVipConfig(id);
        return ResultBody.success(null);
    }
}
