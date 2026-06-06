package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.EquipAdvanceSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.EquipAdvanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 装备进阶控制器
 * 提供装备进阶路线配置相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/equipAdvance")
public class EquipAdvanceController {

    private final EquipAdvanceService equipAdvanceService;

    @Tag(name = "/equipAdvance/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有装备进阶路线（含阶段和消耗）")
    @GetMapping("/" + ApiConstant.LATEST + "/getRouteList")
    public ResultBody<List<EquipAdvanceSaveDTO>> getRouteList() {
        return ResultBody.success(equipAdvanceService.getRouteList());
    }

    @Tag(name = "/equipAdvance/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个装备进阶路线")
    @GetMapping("/" + ApiConstant.LATEST + "/getRoute/{id}")
    public ResultBody<EquipAdvanceSaveDTO> getRoute(@PathVariable("id") Long id) {
        return ResultBody.success(equipAdvanceService.getRouteById(id));
    }

    @Tag(name = "/equipAdvance/" + ApiConstant.LATEST)
    @Operation(summary = "保存装备进阶路线（新增或更新，含阶段和消耗）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveRoute")
    public ResultBody<EquipAdvanceSaveDTO> saveRoute(@RequestBody SubmitBody<EquipAdvanceSaveDTO> request) {
        return ResultBody.success(request, equipAdvanceService.saveRoute(request.getData()));
    }

    @Tag(name = "/equipAdvance/" + ApiConstant.LATEST)
    @Operation(summary = "删除装备进阶路线")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteRoute/{id}")
    public ResultBody<Object> deleteRoute(@PathVariable("id") Long id) {
        equipAdvanceService.deleteRoute(id);
        return ResultBody.success(null);
    }
}
