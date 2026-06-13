package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.SponsorLogDO;
import org.gms.dao.entity.SponsorRecordDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SponsorConfigDTO;
import org.gms.model.dto.SubmitBody;
import org.gms.service.SponsorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 赞助系统控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sponsor")
public class SponsorController {

    private final SponsorService sponsorService;

    // ==================== 配置管理 ====================

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "获取赞助配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/configs")
    public ResultBody<List<SponsorConfigDTO>> listConfigs() {
        return ResultBody.success(sponsorService.listConfigs());
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "保存赞助配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<SponsorConfigDTO> saveConfig(@RequestBody SubmitBody<SponsorConfigDTO> request) {
        return ResultBody.success(request, sponsorService.saveConfig(request.getData()));
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "删除赞助配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        sponsorService.deleteConfig(id);
        return ResultBody.success(null);
    }

    // ==================== 赞助记录 ====================

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "查询赞助记录列表")
    @GetMapping("/" + ApiConstant.LATEST + "/records")
    public ResultBody<List<SponsorRecordDO>> listRecords(
            @RequestParam(name = "playerName", required = false) String playerName) {
        return ResultBody.success(sponsorService.listRecords(playerName));
    }

    // ==================== 赞助日志 ====================

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "查询赞助日志")
    @GetMapping("/" + ApiConstant.LATEST + "/logs")
    public ResultBody<List<SponsorLogDO>> listLogs(
            @RequestParam(name = "playerName", required = false) String playerName,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime) {
        return ResultBody.success(sponsorService.listLogs(playerName, type, startTime, endTime));
    }
}
