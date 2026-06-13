package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.DailyBossSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.DailyBossService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 每日Boss控制器 —— 提供Boss配置的 REST API
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dailyBoss")
public class DailyBossController {

    private final DailyBossService service;

    @Tag(name = "/dailyBoss/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有每日Boss配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<DailyBossSaveDTO>> getConfigList() {
        return ResultBody.success(service.getConfigList());
    }

    @Tag(name = "/dailyBoss/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个每日Boss配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<DailyBossSaveDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(service.getConfigById(id));
    }

    @Tag(name = "/dailyBoss/" + ApiConstant.LATEST)
    @Operation(summary = "保存每日Boss配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<DailyBossSaveDTO> saveConfig(@RequestBody SubmitBody<DailyBossSaveDTO> request) {
        return ResultBody.success(request, service.saveConfig(request.getData()));
    }

    @Tag(name = "/dailyBoss/" + ApiConstant.LATEST)
    @Operation(summary = "删除每日Boss配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        service.deleteConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/dailyBoss/" + ApiConstant.LATEST)
    @Operation(summary = "获取每日Boss环式系统游戏参数")
    @GetMapping("/" + ApiConstant.LATEST + "/getGameParams")
    public ResultBody<Map<String, Object>> getGameParams() {
        return ResultBody.success(service.getGameParams());
    }
}
