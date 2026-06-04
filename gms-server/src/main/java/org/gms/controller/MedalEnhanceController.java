package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.MedalEnhanceSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.MedalEnhanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/medalEnhance")
public class MedalEnhanceController {

    /** 勋章强化服务 */
    private final MedalEnhanceService medalEnhanceService;

    @Tag(name = "/medalEnhance/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有勋章强化配置（含等级和消耗）")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<MedalEnhanceSaveDTO>> getConfigList() {
        return ResultBody.success(medalEnhanceService.getConfigList());
    }

    @Tag(name = "/medalEnhance/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个勋章强化配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<MedalEnhanceSaveDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(medalEnhanceService.getConfigById(id));
    }

    @Tag(name = "/medalEnhance/" + ApiConstant.LATEST)
    @Operation(summary = "保存勋章强化配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<MedalEnhanceSaveDTO> saveConfig(@RequestBody SubmitBody<MedalEnhanceSaveDTO> request) {
        return ResultBody.success(request, medalEnhanceService.saveConfig(request.getData()));
    }

    @Tag(name = "/medalEnhance/" + ApiConstant.LATEST)
    @Operation(summary = "删除勋章强化配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        medalEnhanceService.deleteConfig(id);
        return ResultBody.success(null);
    }
}