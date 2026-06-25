package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ForgeRecipeDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.ForgeRecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 打造配方管理控制器 —— 配方的增删改查，变更后自动刷新 ForgeRecipeManager 缓存
 */
@RestController
@AllArgsConstructor
@RequestMapping("/forgeRecipe")
public class ForgeRecipeController {

    private final ForgeRecipeService forgeRecipeService;

    @Tag(name = "/forgeRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有打造配方列表(含已禁用)")
    @GetMapping("/" + ApiConstant.LATEST + "/getRecipeList")
    public ResultBody<List<ForgeRecipeDTO>> getRecipeList() {
        return ResultBody.success(forgeRecipeService.listAll());
    }

    @Tag(name = "/forgeRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个打造配方详情")
    @GetMapping("/" + ApiConstant.LATEST + "/getRecipe/{id}")
    public ResultBody<ForgeRecipeDTO> getRecipe(@PathVariable("id") Long id) {
        return ResultBody.success(forgeRecipeService.getRecipeDetail(id));
    }

    @Tag(name = "/forgeRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "保存打造配方（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveRecipe")
    public ResultBody<ForgeRecipeDTO> saveRecipe(@RequestBody SubmitBody<ForgeRecipeDTO> request) {
        return ResultBody.success(request, forgeRecipeService.saveRecipe(request.getData()));
    }

    @Tag(name = "/forgeRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "切换打造配方启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Long id) {
        forgeRecipeService.toggleEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/forgeRecipe/" + ApiConstant.LATEST)
    @Operation(summary = "删除打造配方")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteRecipe/{id}")
    public ResultBody<Object> deleteRecipe(@PathVariable("id") Long id) {
        forgeRecipeService.deleteRecipe(id);
        return ResultBody.success(null);
    }
}
