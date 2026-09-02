package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.AlchemyRecipeDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.AlchemyRecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 炼金配方管理控制器 —— 配方的增删改查，变更后自动刷新 AlchemyRecipeManager 缓存
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alchemyRecipe")
public class AlchemyRecipeController {

    private final AlchemyRecipeService alchemyRecipeService;

    @Tag(name = "/alchemyRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有炼金配方列表(含已禁用)")
    @GetMapping("/" + ApiConstant.LATEST + "/getRecipeList")
    public ResultBody<List<AlchemyRecipeDTO>> getRecipeList() {
        return ResultBody.success(alchemyRecipeService.listAll());
    }

    @Tag(name = "/alchemyRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个炼金配方详情")
    @GetMapping("/" + ApiConstant.LATEST + "/getRecipe/{id}")
    public ResultBody<AlchemyRecipeDTO> getRecipe(@PathVariable("id") Long id) {
        return ResultBody.success(alchemyRecipeService.getRecipeDetail(id));
    }

    @Tag(name = "/alchemyRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "保存炼金配方（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveRecipe")
    public ResultBody<AlchemyRecipeDTO> saveRecipe(@RequestBody SubmitBody<AlchemyRecipeDTO> request) {
        return ResultBody.success(request, alchemyRecipeService.saveRecipe(request.getData()));
    }

    @Tag(name = "/alchemyRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "切换炼金配方启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Long id) {
        alchemyRecipeService.toggleEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/alchemyRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "删除炼金配方")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteRecipe/{id}")
    public ResultBody<Object> deleteRecipe(@PathVariable("id") Long id) {
        alchemyRecipeService.deleteRecipe(id);
        return ResultBody.success(null);
    }
}
