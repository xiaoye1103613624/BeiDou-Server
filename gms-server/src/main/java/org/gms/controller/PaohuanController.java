package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.PaohuanSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.PaohuanService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 跑环控制器 —— 提供跑环管理的 REST API 接口
 * <p>
 * 包括跑环物品池和里程碑奖励的增删改查、游戏参数查询等功能。
 * </p>
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/paohuan")
public class PaohuanController {

    private final PaohuanService paohuanService;

    // ==================== 物品池配置 ====================

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "获取跑环物品池配置列表（含里程碑奖励）")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<PaohuanSaveDTO>> getConfigList() {
        return ResultBody.success(paohuanService.getConfigList());
    }

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个跑环物品池配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<PaohuanSaveDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(paohuanService.getConfigById(id));
    }

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "保存跑环物品池配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<PaohuanSaveDTO> saveConfig(@RequestBody SubmitBody<PaohuanSaveDTO> request) {
        return ResultBody.success(request, paohuanService.saveConfig(request.getData()));
    }

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "删除跑环物品池配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        paohuanService.deleteConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除跑环物品池配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteConfigBatch")
    public ResultBody<Object> deleteConfigBatch(@RequestBody SubmitBody<List<Long>> request) {
        paohuanService.deleteConfigBatch(request.getData());
        return ResultBody.success(null);
    }

    // ==================== 每环随机奖励 ====================

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "获取每环随机奖励池列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getRingRewardList")
    public ResultBody<List<PaohuanSaveDTO.RingRewardDTO>> getRingRewardList() {
        return ResultBody.success(paohuanService.getRingRewardList());
    }

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "保存单条每环随机奖励")
    @PostMapping("/" + ApiConstant.LATEST + "/saveRingReward")
    public ResultBody<PaohuanSaveDTO.RingRewardDTO> saveRingReward(@RequestBody SubmitBody<PaohuanSaveDTO.RingRewardDTO> request) {
        return ResultBody.success(request, paohuanService.saveRingReward(request.getData()));
    }

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "删除每环随机奖励")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteRingReward/{id}")
    public ResultBody<Object> deleteRingReward(@PathVariable("id") Long id) {
        paohuanService.deleteRingReward(id);
        return ResultBody.success(null);
    }

    // ==================== 里程碑奖励 ====================

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有里程碑奖励列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getRewardList")
    public ResultBody<List<PaohuanSaveDTO.RewardDTO>> getRewardList() {
        return ResultBody.success(paohuanService.getRewardList());
    }

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "保存单条里程碑奖励")
    @PostMapping("/" + ApiConstant.LATEST + "/saveReward")
    public ResultBody<PaohuanSaveDTO.RewardDTO> saveReward(@RequestBody SubmitBody<PaohuanSaveDTO.RewardDTO> request) {
        return ResultBody.success(request, paohuanService.saveReward(request.getData()));
    }

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "删除里程碑奖励")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteReward/{id}")
    public ResultBody<Object> deleteReward(@PathVariable("id") Long id) {
        paohuanService.deleteReward(id);
        return ResultBody.success(null);
    }

    // ==================== 游戏参数 ====================

    @Tag(name = "/paohuan/" + ApiConstant.LATEST)
    @Operation(summary = "获取跑环游戏参数")
    @GetMapping("/" + ApiConstant.LATEST + "/getGameParams")
    public ResultBody<Object> getGameParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("dailyLimit", paohuanService.getDailyLimit());
        params.put("expPerRing", paohuanService.getExpPerRing());
        params.put("mesoPerRing", paohuanService.getMesoPerRing());
        return ResultBody.success(params);
    }
}
