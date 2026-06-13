package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.ToyCollectionDTO;
import org.gms.model.dto.ToyCollectionDTO.CategoryDTO;
import org.gms.model.dto.ToyCollectionDTO.ItemDTO;
import org.gms.model.dto.ToyCollectionDTO.ProgressDTO;
import org.gms.service.ToyCollectionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 玩具收集控制器 —— 提供玩具收集配置的 REST API 接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/toyCollection")
public class ToyCollectionController {

    private final ToyCollectionService toyCollectionService;

    // ==================== 分类接口 ====================

    @Tag(name = "/toyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有分类列表（含物品列表）")
    @GetMapping("/" + ApiConstant.LATEST + "/getCategoryList")
    public ResultBody<List<CategoryDTO>> getCategoryList() {
        return ResultBody.success(toyCollectionService.getCategoryList());
    }

    @Tag(name = "/toyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个分类配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getCategory/{id}")
    public ResultBody<CategoryDTO> getCategory(@PathVariable("id") Long id) {
        return ResultBody.success(toyCollectionService.getCategoryById(id));
    }

    @Tag(name = "/toyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "保存分类配置（新增或更新，含物品列表）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveCategory")
    public ResultBody<CategoryDTO> saveCategory(@RequestBody SubmitBody<CategoryDTO> request) {
        return ResultBody.success(request, toyCollectionService.saveCategory(request.getData()));
    }

    @Tag(name = "/toyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "删除分类配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteCategory/{id}")
    public ResultBody<Object> deleteCategory(@PathVariable("id") Long id) {
        toyCollectionService.deleteCategory(id);
        return ResultBody.success(null);
    }

    // ==================== 物品接口 ====================

    @Tag(name = "/toyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "获取指定分类的物品列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getItemList")
    public ResultBody<List<ItemDTO>> getItemList(@RequestParam Long categoryId) {
        return ResultBody.success(toyCollectionService.getItemList(categoryId));
    }

    @Tag(name = "/toyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "保存单个物品配置")
    @PostMapping("/" + ApiConstant.LATEST + "/saveItem")
    public ResultBody<ItemDTO> saveItem(@RequestBody SubmitBody<ItemDTO> request) {
        return ResultBody.success(request, toyCollectionService.saveItem(request.getData()));
    }

    @Tag(name = "/toyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "删除物品配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteItem/{id}")
    public ResultBody<Object> deleteItem(@PathVariable("id") Long id) {
        toyCollectionService.deleteItem(id);
        return ResultBody.success(null);
    }

    // ==================== 进度接口 ====================

    @Tag(name = "/toyCollection/" + ApiConstant.LATEST)
    @Operation(summary = "获取角色收集进度")
    @GetMapping("/" + ApiConstant.LATEST + "/getProgress")
    public ResultBody<List<ProgressDTO>> getProgress(
            @RequestParam Integer characterId,
            @RequestParam(required = false) Long categoryId) {
        return ResultBody.success(toyCollectionService.getProgress(characterId, categoryId));
    }
}
