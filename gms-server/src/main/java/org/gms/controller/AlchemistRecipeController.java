package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.AlchemistRecipeDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.AlchemistRecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 炼药师配方管理控制器 —— 配方的增删改查，变更后自动刷新 AlchemistRecipeManager 缓存
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alchemistRecipe")
public class AlchemistRecipeController {

    private final AlchemistRecipeService alchemistRecipeService;

    @Tag(name = "/alchemistRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有炼药配方列表(含已禁用)")
    @GetMapping("/" + ApiConstant.LATEST + "/getRecipeList")
    public ResultBody<List<AlchemistRecipeDTO>> getRecipeList() {
        return ResultBody.success(alchemistRecipeService.listAll());
    }

    @Tag(name = "/alchemistRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个炼药配方详情")
    @GetMapping("/" + ApiConstant.LATEST + "/getRecipe/{id}")
    public ResultBody<AlchemistRecipeDTO> getRecipe(@PathVariable("id") Long id) {
        return ResultBody.success(alchemistRecipeService.getRecipeDetail(id));
    }

    @Tag(name = "/alchemistRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "保存炼药配方（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveRecipe")
    public ResultBody<AlchemistRecipeDTO> saveRecipe(@RequestBody SubmitBody<AlchemistRecipeDTO> request) {
        return ResultBody.success(request, alchemistRecipeService.saveRecipe(request.getData()));
    }

    @Tag(name = "/alchemistRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "切换炼药配方启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Long id) {
        alchemistRecipeService.toggleEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/alchemistRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "删除炼药配方")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteRecipe/{id}")
    public ResultBody<Object> deleteRecipe(@PathVariable("id") Long id) {
        alchemistRecipeService.deleteRecipe(id);
        return ResultBody.success(null);
    }
}