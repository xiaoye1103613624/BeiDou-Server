package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.IndependentDropConfigDO;
import org.gms.model.dto.IndependentDropSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.IndependentDropService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 独立掉落配置控制器 —— 提供怪物独立掉落开关的 REST API
 */
@RestController
@AllArgsConstructor
@RequestMapping("/independentDrop")
public class IndependentDropController {

    private final IndependentDropService service;

    @Tag(name = "/independentDrop/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有独立掉落怪物配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<IndependentDropConfigDO>> getConfigList() {
        return ResultBody.success(service.getAllConfigs());
    }

    @Tag(name = "/independentDrop/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个独立掉落怪物配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<IndependentDropConfigDO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(service.getConfigById(id));
    }

    @Tag(name = "/independentDrop/" + ApiConstant.LATEST)
    @Operation(summary = "保存独立掉落怪物配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<IndependentDropConfigDO> saveConfig(@RequestBody SubmitBody<IndependentDropSaveDTO> request) {
        return ResultBody.success(request, service.saveConfig(request.getData()));
    }

    @Tag(name = "/independentDrop/" + ApiConstant.LATEST)
    @Operation(summary = "删除独立掉落怪物配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        service.deleteConfig(id);
        return ResultBody.success(null);
    }
}
