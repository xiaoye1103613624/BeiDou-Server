package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.EquipDamageBonusConfigDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.EquipDamageBonusConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 装备伤害加成配置控制器 —— 按单件装备配置普通伤害%/Boss伤害%/固定段伤
 */
@RestController
@AllArgsConstructor
@RequestMapping("/equipDamageBonus")
public class EquipDamageBonusController {

    private final EquipDamageBonusConfigService equipDamageBonusConfigService;

    @Tag(name = "/equipDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有装备伤害加成配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<EquipDamageBonusConfigDTO>> getConfigList() {
        return ResultBody.success(equipDamageBonusConfigService.getConfigList());
    }

    @Tag(name = "/equipDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个装备伤害加成配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<EquipDamageBonusConfigDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(equipDamageBonusConfigService.getConfig(id));
    }

    @Tag(name = "/equipDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "保存装备伤害加成配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<EquipDamageBonusConfigDTO> saveConfig(@RequestBody SubmitBody<EquipDamageBonusConfigDTO> request) {
        return ResultBody.success(request, equipDamageBonusConfigService.saveConfig(request.getData()));
    }

    @Tag(name = "/equipDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "切换装备伤害加成配置启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Long id) {
        equipDamageBonusConfigService.toggleEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/equipDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "删除装备伤害加成配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        equipDamageBonusConfigService.deleteConfig(id);
        return ResultBody.success(null);
    }
}
