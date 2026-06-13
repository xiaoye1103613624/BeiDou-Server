package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库控制器 —— 提供仓库管理的 REST API 接口
 * <p>
 * 包括仓库物品白名单配置的增删改查、仓库物品的存取操作、
 * 账号/角色列表查询等功能。
 * </p>
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;

    // ==================== 配置管理 ====================

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "获取仓库物品白名单配置列表（支持筛选）")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<WarehouseConfigDTO>> getConfigList(
            @RequestParam(name = "itemId", required = false) Integer itemId,
            @RequestParam(name = "inventoryType", required = false) Integer inventoryType,
            @RequestParam(name = "enabled", required = false) Integer enabled) {
        return ResultBody.success(warehouseService.getConfigList(itemId, inventoryType, enabled));
    }

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个仓库物品白名单配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<WarehouseConfigDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(warehouseService.getConfigById(id));
    }

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "保存仓库物品白名单配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<WarehouseConfigDTO> saveConfig(@RequestBody SubmitBody<WarehouseConfigDTO> request) {
        return ResultBody.success(request, warehouseService.saveConfig(request.getData()));
    }

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "删除仓库物品白名单配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        warehouseService.deleteConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除仓库物品白名单配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteConfigBatch")
    public ResultBody<Object> deleteConfigBatch(@RequestBody SubmitBody<List<Long>> request) {
        warehouseService.deleteConfigBatch(request.getData());
        return ResultBody.success(null);
    }

    // ==================== 物品存取 ====================

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "查询仓库物品列表（支持按角色ID自动解析账号ID）")
    @GetMapping("/" + ApiConstant.LATEST + "/getWarehouseItems")
    public ResultBody<List<WarehouseItemDTO>> getWarehouseItems(
            @RequestParam(name = "accountId", required = false) Integer accountId,
            @RequestParam(name = "characterId", required = false) Integer characterId,
            @RequestParam(name = "inventoryType", required = false) Integer inventoryType) {
        return ResultBody.success(warehouseService.getWarehouseItems(accountId, characterId, inventoryType));
    }

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "存入物品到仓库")
    @PostMapping("/" + ApiConstant.LATEST + "/deposit")
    public ResultBody<Boolean> deposit(@RequestBody SubmitBody<WarehouseOperateDTO> request) {
        WarehouseOperateDTO dto = request.getData();
        boolean result = warehouseService.depositItem(
                dto.getAccountId(), dto.getCharacterId(),
                dto.getItemId(), dto.getInventoryType(), dto.getQuantity());
        return ResultBody.success(request, result);
    }

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "从仓库取出物品")
    @PostMapping("/" + ApiConstant.LATEST + "/withdraw")
    public ResultBody<Integer> withdraw(@RequestBody SubmitBody<WarehouseOperateDTO> request) {
        WarehouseOperateDTO dto = request.getData();
        int withdrawn = warehouseService.withdrawItem(
                dto.getAccountId(), dto.getCharacterId(),
                dto.getItemId(), dto.getInventoryType(), dto.getQuantity());
        return ResultBody.success(request, withdrawn);
    }

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "删除仓库物品记录")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteWarehouseItem/{id}")
    public ResultBody<Object> deleteWarehouseItem(@PathVariable("id") Long id) {
        warehouseService.deleteWarehouseItem(id);
        return ResultBody.success(null);
    }

    // ==================== 查询辅助 ====================

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "获取有仓库数据的账号ID列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getAccountList")
    public ResultBody<List<Integer>> getAccountList() {
        return ResultBody.success(warehouseService.getAccountList());
    }

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "获取某账号下有仓库数据的角色ID列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getCharacterList")
    public ResultBody<List<Integer>> getCharacterList(@RequestParam(name = "accountId") Integer accountId) {
        return ResultBody.success(warehouseService.getCharacterList(accountId));
    }

    // ==================== 游戏参数 ====================

    @Tag(name = "/warehouse/" + ApiConstant.LATEST)
    @Operation(summary = "获取仓库游戏参数")
    @GetMapping("/" + ApiConstant.LATEST + "/getGameParams")
    public ResultBody<Object> getGameParams() {
        java.util.Map<String, Object> params = new java.util.HashMap<>();
        params.put("accountShared", warehouseService.isAccountShared());
        params.put("maxStack", warehouseService.getMaxStack());
        return ResultBody.success(params);
    }
}
