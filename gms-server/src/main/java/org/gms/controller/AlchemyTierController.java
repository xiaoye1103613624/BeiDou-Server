package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.config.AlchemyTierManager;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.AlchemyTierDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.AlchemyTierService;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 副职业品级配置管理控制器（炼金/炼药/锻造共用）—— 品级(等级/升级经验值)的增删改查，
 * 按副职业类型(type)隔离，变更后自动刷新缓存
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alchemyTier")
public class AlchemyTierController {

    private final AlchemyTierService alchemyTierService;

    @Tag(name = "/alchemyTier/" + ApiConstant.LATEST)
    @Operation(summary = "获取某副职业品级列表(含已禁用)")
    @GetMapping("/" + ApiConstant.LATEST + "/getTierList")
    public ResultBody<List<AlchemyTierDTO>> getTierList(
            @RequestParam(value = "type", required = false, defaultValue = "1") int type) {
        List<AlchemyTierDTO> result = new ArrayList<>();
        for (var d : alchemyTierService.listAllTiers(normalizeType(type))) {
            result.add(AlchemyTierDTO.builder()
                    .id(d.getId())
                    .type(d.getType())
                    .name(d.getName())
                    .expStart(d.getExpStart())
                    .isMax(d.getIsMax())
                    .sortOrder(d.getSortOrder())
                    .enabled(d.getEnabled())
                    .build());
        }
        return ResultBody.success(result);
    }

    @Tag(name = "/alchemyTier/" + ApiConstant.LATEST)
    @Operation(summary = "保存副职业品级（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveTier")
    public ResultBody<AlchemyTierDTO> saveTier(@RequestBody SubmitBody<AlchemyTierDTO> request) {
        return ResultBody.success(request, alchemyTierService.saveTier(request.getData()));
    }

    @Operation(summary = "切换副职业品级启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Long id) {
        alchemyTierService.toggleEnabled(id);
        return ResultBody.success(null);
    }

    @Operation(summary = "删除副职业品级")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteTier/{id}")
    public ResultBody<Object> deleteTier(@PathVariable("id") Long id) {
        alchemyTierService.deleteTier(id);
        return ResultBody.success(null);
    }

    private int normalizeType(int type) {
        return (type == AlchemyTierManager.TYPE_ALCHEMIST || type == AlchemyTierManager.TYPE_FORGE)
                ? type : AlchemyTierManager.TYPE_ALCHEMY;
    }
}