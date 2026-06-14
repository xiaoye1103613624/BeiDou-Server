package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.MentorConfigDTO;
import org.gms.model.dto.MentorGraduationRewardDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.MentorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 师徒系统控制器 —— 提供配置和毕业奖励的 REST API
 */
@RestController
@AllArgsConstructor
@RequestMapping("/mentor")
public class MentorController {

    private final MentorService service;

    // ==================== 系统配置 CRUD ====================

    @Tag(name = "/mentor/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有师徒系统配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<MentorConfigDTO>> getConfigList() {
        return ResultBody.success(service.getAllConfigs());
    }

    @Tag(name = "/mentor/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个师徒系统配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<MentorConfigDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(service.getConfigById(id));
    }

    @Tag(name = "/mentor/" + ApiConstant.LATEST)
    @Operation(summary = "保存师徒系统配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<MentorConfigDTO> saveConfig(@RequestBody SubmitBody<MentorConfigDTO> request) {
        return ResultBody.success(request, service.saveConfig(request.getData()));
    }

    @Tag(name = "/mentor/" + ApiConstant.LATEST)
    @Operation(summary = "删除师徒系统配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        service.deleteConfig(id);
        return ResultBody.success(null);
    }

    // ==================== 毕业奖励 CRUD ====================

    @Tag(name = "/mentor/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有出师奖励配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getRewardList")
    public ResultBody<List<MentorGraduationRewardDTO>> getRewardList() {
        return ResultBody.success(service.getAllGraduationRewards());
    }

    @Tag(name = "/mentor/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个出师奖励配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getReward/{id}")
    public ResultBody<MentorGraduationRewardDTO> getReward(@PathVariable("id") Long id) {
        return ResultBody.success(service.getGraduationRewardById(id));
    }

    @Tag(name = "/mentor/" + ApiConstant.LATEST)
    @Operation(summary = "保存出师奖励配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveReward")
    public ResultBody<MentorGraduationRewardDTO> saveReward(@RequestBody SubmitBody<MentorGraduationRewardDTO> request) {
        return ResultBody.success(request, service.saveGraduationReward(request.getData()));
    }

    @Tag(name = "/mentor/" + ApiConstant.LATEST)
    @Operation(summary = "删除出师奖励配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteReward/{id}")
    public ResultBody<Object> deleteReward(@PathVariable("id") Long id) {
        service.deleteGraduationReward(id);
        return ResultBody.success(null);
    }
}
