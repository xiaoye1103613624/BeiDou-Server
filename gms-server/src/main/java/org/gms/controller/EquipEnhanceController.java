package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.EquipEnhanceSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.EquipEnhanceService;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 装备强化控制器
 * 提供装备强化配置相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/equipEnhance")
public class EquipEnhanceController {

    /** 装备强化服务 */
    private final EquipEnhanceService equipEnhanceService;

    @Tag(name = "/equipEnhance/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有装备强化配置（含等级和消耗）")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<EquipEnhanceSaveDTO>> getConfigList() {
        return ResultBody.success(equipEnhanceService.getConfigList());
    }

    @Tag(name = "/equipEnhance/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个装备强化配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<EquipEnhanceSaveDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(equipEnhanceService.getConfigById(id));
    }

    @Tag(name = "/equipEnhance/" + ApiConstant.LATEST)
    @Operation(summary = "保存装备强化配置（新增或更新，含等级和消耗）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<EquipEnhanceSaveDTO> saveConfig(@RequestBody SubmitBody<EquipEnhanceSaveDTO> request) {
        return ResultBody.success(request, equipEnhanceService.saveConfig(request.getData()));
    }

    @Tag(name = "/equipEnhance/" + ApiConstant.LATEST)
    @Operation(summary = "删除装备强化配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        equipEnhanceService.deleteConfig(id);
        return ResultBody.success(null);
    }
}