package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.service.ScrollDecomposeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 卷轴分解/兑换控制器 —— 提供卷轴分解和兑换配置的 REST API 接口
 * <p>
 * 包括卷轴分解白名单配置的增删改查，以及卷轴兑换价格配置的增删改查。
 * </p>
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/scrollDecompose")
public class ScrollDecomposeController {

    private final ScrollDecomposeService scrollDecomposeService;

    // ==================== 分解配置管理 ====================

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "获取卷轴分解配置列表（支持筛选）")
    @GetMapping("/" + ApiConstant.LATEST + "/getDecomposeConfigList")
    public ResultBody<List<ScrollDecomposeConfigDTO>> getDecomposeConfigList(
            @RequestParam(name = "scrollId", required = false) Integer scrollId,
            @RequestParam(name = "enabled", required = false) Integer enabled) {
        return ResultBody.success(scrollDecomposeService.getDecomposeConfigList(scrollId, enabled));
    }

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个卷轴分解配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getDecomposeConfig/{id}")
    public ResultBody<ScrollDecomposeConfigDTO> getDecomposeConfig(@PathVariable("id") Long id) {
        return ResultBody.success(scrollDecomposeService.getDecomposeConfigById(id));
    }

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "保存卷轴分解配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveDecomposeConfig")
    public ResultBody<ScrollDecomposeConfigDTO> saveDecomposeConfig(
            @RequestBody SubmitBody<ScrollDecomposeConfigDTO> request) {
        return ResultBody.success(request, scrollDecomposeService.saveDecomposeConfig(request.getData()));
    }

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "删除卷轴分解配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteDecomposeConfig/{id}")
    public ResultBody<Object> deleteDecomposeConfig(@PathVariable("id") Long id) {
        scrollDecomposeService.deleteDecomposeConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除卷轴分解配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteDecomposeConfigBatch")
    public ResultBody<Object> deleteDecomposeConfigBatch(@RequestBody SubmitBody<List<Long>> request) {
        scrollDecomposeService.deleteDecomposeConfigBatch(request.getData());
        return ResultBody.success(null);
    }

    // ==================== 兑换配置管理 ====================

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "获取卷轴兑换配置列表（支持筛选）")
    @GetMapping("/" + ApiConstant.LATEST + "/getExchangeConfigList")
    public ResultBody<List<ScrollExchangeConfigDTO>> getExchangeConfigList(
            @RequestParam(name = "scrollId", required = false) Integer scrollId,
            @RequestParam(name = "enabled", required = false) Integer enabled) {
        return ResultBody.success(scrollDecomposeService.getExchangeConfigList(scrollId, enabled));
    }

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个卷轴兑换配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getExchangeConfig/{id}")
    public ResultBody<ScrollExchangeConfigDTO> getExchangeConfig(@PathVariable("id") Long id) {
        return ResultBody.success(scrollDecomposeService.getExchangeConfigById(id));
    }

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "保存卷轴兑换配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveExchangeConfig")
    public ResultBody<ScrollExchangeConfigDTO> saveExchangeConfig(
            @RequestBody SubmitBody<ScrollExchangeConfigDTO> request) {
        return ResultBody.success(request, scrollDecomposeService.saveExchangeConfig(request.getData()));
    }

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "删除卷轴兑换配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteExchangeConfig/{id}")
    public ResultBody<Object> deleteExchangeConfig(@PathVariable("id") Long id) {
        scrollDecomposeService.deleteExchangeConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/scrollDecompose/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除卷轴兑换配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteExchangeConfigBatch")
    public ResultBody<Object> deleteExchangeConfigBatch(@RequestBody SubmitBody<List<Long>> request) {
        scrollDecomposeService.deleteExchangeConfigBatch(request.getData());
        return ResultBody.success(null);
    }
}
