package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.DailyActiveTaskDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.DailyActiveService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 每日活跃任务管理控制器 —— 任务阈值/奖励的增删改查
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dailyActiveTask")
public class DailyActiveTaskController {

    private final DailyActiveService dailyActiveService;

    @Tag(name = "/dailyActiveTask/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有每日活跃任务列表(含已禁用)")
    @GetMapping("/" + ApiConstant.LATEST + "/getTaskList")
    public ResultBody<List<DailyActiveTaskDTO>> getTaskList() {
        return ResultBody.success(dailyActiveService.listAllTasks());
    }

    @Tag(name = "/dailyActiveTask/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个每日活跃任务详情")
    @GetMapping("/" + ApiConstant.LATEST + "/getTask/{id}")
    public ResultBody<DailyActiveTaskDTO> getTask(@PathVariable("id") Long id) {
        return ResultBody.success(dailyActiveService.getTaskDetail(id));
    }

    @Tag(name = "/dailyActiveTask/" + ApiConstant.LATEST)
    @Operation(summary = "保存每日活跃任务（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveTask")
    public ResultBody<DailyActiveTaskDTO> saveTask(@RequestBody SubmitBody<DailyActiveTaskDTO> request) {
        return ResultBody.success(request, dailyActiveService.saveTask(request.getData()));
    }

    @Tag(name = "/dailyActiveTask/" + ApiConstant.LATEST)
    @Operation(summary = "切换每日活跃任务启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Long id) {
        dailyActiveService.toggleEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/dailyActiveTask/" + ApiConstant.LATEST)
    @Operation(summary = "删除每日活跃任务")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteTask/{id}")
    public ResultBody<Object> deleteTask(@PathVariable("id") Long id) {
        dailyActiveService.deleteTask(id);
        return ResultBody.success(null);
    }
}
