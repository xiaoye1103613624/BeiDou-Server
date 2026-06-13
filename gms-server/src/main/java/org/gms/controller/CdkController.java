package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.CdkLogDO;
import org.gms.model.dto.*;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.ResultBody;
import org.gms.service.CdkService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CDK兑换码管理接口
 * <p>
 * 提供CDK配置的CRUD、批量生成、兑换执行和日志查询功能。
 * </p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/cdk")
public class CdkController {

    private final CdkService cdkService;

    // ==================== CDK配置 CRUD ====================

    @Tag(name = "/cdk/" + ApiConstant.LATEST)
    @Operation(summary = "获取CDK配置列表（含道具奖励）")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<CdkConfigDTO>> list(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "type", required = false) Integer type,
            @RequestParam(name = "enabled", required = false) Integer enabled) {
        return ResultBody.success(cdkService.listCdkConfigs(keyword, type, enabled));
    }

    @Tag(name = "/cdk/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个CDK配置详情（含道具名称）")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<CdkConfigDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(cdkService.getCdkConfigById(id));
    }

    @Tag(name = "/cdk/" + ApiConstant.LATEST)
    @Operation(summary = "保存CDK配置（新增或更新，级联保存道具列表）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<CdkConfigDTO> saveConfig(@RequestBody SubmitBody<CdkConfigDTO> request) {
        return ResultBody.success(request, cdkService.saveCdkConfig(request.getData()));
    }

    @Tag(name = "/cdk/" + ApiConstant.LATEST)
    @Operation(summary = "删除CDK配置（道具级联删除，日志保留）")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        cdkService.deleteCdkConfig(id);
        return ResultBody.success(null);
    }

    // ==================== 批量生成 ====================

    @Tag(name = "/cdk/" + ApiConstant.LATEST)
    @Operation(summary = "批量生成CDK兑换码")
    @PostMapping("/" + ApiConstant.LATEST + "/batchGenerate")
    public ResultBody<CdkBatchGenRtnDTO> batchGenerate(@RequestBody SubmitBody<CdkBatchGenReqDTO> request) {
        return ResultBody.success(request, cdkService.batchGenerate(request.getData()));
    }

    // ==================== 兑换执行 ====================

    @Tag(name = "/cdk/" + ApiConstant.LATEST)
    @Operation(summary = "兑换CDK（NPC脚本和测试调用）")
    @PostMapping("/" + ApiConstant.LATEST + "/redeem")
    public ResultBody<CdkRedeemRtnDTO> redeem(@RequestBody SubmitBody<CdkRedeemReqDTO> request,
                                               HttpServletRequest httpRequest) {
        CdkRedeemReqDTO req = request.getData();
        String ip = httpRequest.getRemoteAddr();
        // 从请求中获取玩家名（如果未传playerId则仅靠code+ip记录）
        String playerName = req.getPlayerId() != null ? "player#" + req.getPlayerId() : null;
        return ResultBody.success(request, cdkService.redeem(
                req.getCode(), playerName, ip));
    }

    // ==================== 兑换日志 ====================

    @Tag(name = "/cdk/" + ApiConstant.LATEST)
    @Operation(summary = "查询兑换日志（反滥用审计）")
    @GetMapping("/" + ApiConstant.LATEST + "/logs")
    public ResultBody<List<CdkLogDO>> queryLogs(
            @RequestParam(name = "playerName", required = false) String playerName,
            @RequestParam(name = "code", required = false) String code,
            @RequestParam(name = "ip", required = false) String ip,
            @RequestParam(name = "result", required = false) Integer result,
            @RequestParam(name = "startTime", required = false) String startTime,
            @RequestParam(name = "endTime", required = false) String endTime) {
        return ResultBody.success(cdkService.queryLogs(playerName, code, ip, result, startTime, endTime));
    }
}
