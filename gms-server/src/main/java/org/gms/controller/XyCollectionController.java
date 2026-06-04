package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.XyCollectionSaveDTO;
import org.gms.service.XyCollectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/xyCollection")
public class XyCollectionController {

    /** XY收集服务 */
    private final XyCollectionService xyCollectionService;

    @Tag(name = "/xyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有XY收集配置（含阶段和物品）")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<XyCollectionSaveDTO>> getConfigList() {
        return ResultBody.success(xyCollectionService.getConfigList());
    }

    @Tag(name = "/xyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个XY收集配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<XyCollectionSaveDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(xyCollectionService.getConfigById(id));
    }

    @Tag(name = "/xyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "保存XY收集配置（新增或更新，含阶段和物品）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<XyCollectionSaveDTO> saveConfig(@RequestBody SubmitBody<XyCollectionSaveDTO> request) {
        return ResultBody.success(request, xyCollectionService.saveConfig(request.getData()));
    }

    @Tag(name = "/xyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "删除XY收集配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        xyCollectionService.deleteConfig(id);
        return ResultBody.success(null);
    }
}