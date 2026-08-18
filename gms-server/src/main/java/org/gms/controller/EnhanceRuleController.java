package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.EquipEnhanceRuleDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.EnhanceRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/enhanceRule")
public class EnhanceRuleController {
    private final EnhanceRuleService enhanceRuleService;

    @Tag(name = "/enhanceRule/" + ApiConstant.LATEST)
    @Operation(summary = "强化规则列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<EquipEnhanceRuleDO>> list() {
        return ResultBody.success(enhanceRuleService.listAll());
    }

    @Tag(name = "/enhanceRule/" + ApiConstant.LATEST)
    @Operation(summary = "保存强化规则")
    @PostMapping("/" + ApiConstant.LATEST + "/save")
    public ResultBody<Object> save(@RequestBody SubmitBody<EquipEnhanceRuleDO> request) {
        enhanceRuleService.save(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/enhanceRule/" + ApiConstant.LATEST)
    @Operation(summary = "删除强化规则")
    @PostMapping("/" + ApiConstant.LATEST + "/delete")
    public ResultBody<Object> delete(@RequestBody SubmitBody<Long> request) {
        enhanceRuleService.delete(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/enhanceRule/" + ApiConstant.LATEST)
    @Operation(summary = "热重载强化规则")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        enhanceRuleService.reload();
        return ResultBody.success();
    }
}
