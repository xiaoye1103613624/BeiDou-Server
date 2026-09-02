package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.config.PetGrowthManager;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.PetGrowthPreviewDTO;
import org.gms.model.dto.PetGrowthStageDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.PetGrowthService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 宠物成长进阶配置（Web 后台）
 */
@RestController
@AllArgsConstructor
@RequestMapping("/petGrowth")
public class PetGrowthController {

    private final PetGrowthService petGrowthService;

    @Tag(name = "/petGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有宠物成长阶段配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getStageList")
    public ResultBody<List<PetGrowthStageDTO>> getStageList() {
        return ResultBody.success(petGrowthService.listAll());
    }

    @Tag(name = "/petGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "按进阶链预览（含服务端WZ存在性校验）")
    @GetMapping("/" + ApiConstant.LATEST + "/preview")
    public ResultBody<List<PetGrowthPreviewDTO>> preview() {
        return ResultBody.success(petGrowthService.previewChains());
    }

    @Tag(name = "/petGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个阶段详情")
    @GetMapping("/" + ApiConstant.LATEST + "/getStage/{id}")
    public ResultBody<PetGrowthStageDTO> getStage(@PathVariable("id") Long id) {
        return ResultBody.success(petGrowthService.getDetail(id));
    }

    @Tag(name = "/petGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "保存阶段配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveStage")
    public ResultBody<PetGrowthStageDTO> saveStage(@RequestBody SubmitBody<PetGrowthStageDTO> request) {
        return ResultBody.success(request, petGrowthService.saveStage(request.getData()));
    }

    @Tag(name = "/petGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "切换启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Long id) {
        petGrowthService.toggleEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/petGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "删除阶段配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteStage/{id}")
    public ResultBody<Object> deleteStage(@PathVariable("id") Long id) {
        petGrowthService.deleteStage(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/petGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "热重载成长配置缓存")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        PetGrowthManager.reload();
        return ResultBody.success(null);
    }
}
