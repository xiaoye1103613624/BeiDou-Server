package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SetDamageBonusConfigDTO;
import org.gms.model.dto.SubmitBody;
import org.gms.service.SetDamageBonusConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 套装伤害加成配置控制器 —— 按"套装ID+穿戴件数档位"配置普通伤害%/Boss伤害%
 */
@RestController
@AllArgsConstructor
@RequestMapping("/setDamageBonus")
public class SetDamageBonusController {

    private final SetDamageBonusConfigService setDamageBonusConfigService;

    @Tag(name = "/setDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有套装伤害加成配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<SetDamageBonusConfigDTO>> getConfigList() {
        return ResultBody.success(setDamageBonusConfigService.getConfigList());
    }

    @Tag(name = "/setDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个套装伤害加成配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<SetDamageBonusConfigDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(setDamageBonusConfigService.getConfig(id));
    }

    @Tag(name = "/setDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "保存套装伤害加成配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<SetDamageBonusConfigDTO> saveConfig(@RequestBody SubmitBody<SetDamageBonusConfigDTO> request) {
        return ResultBody.success(request, setDamageBonusConfigService.saveConfig(request.getData()));
    }

    @Tag(name = "/setDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "切换套装伤害加成配置启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Long id) {
        setDamageBonusConfigService.toggleEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/setDamageBonus/" + ApiConstant.LATEST)
    @Operation(summary = "删除套装伤害加成配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        setDamageBonusConfigService.deleteConfig(id);
        return ResultBody.success(null);
    }
}
