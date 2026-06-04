package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.AllianceDO;
import org.gms.model.dto.BasePageDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.AllianceManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 联盟管理控制器
 * 提供联盟管理相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/allianceManage")
public class AllianceManageController {

    /** 联盟管理服务 */
    private final AllianceManageService allianceManageService;

    @Tag(name = "/allianceManage/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取联盟列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getAllianceList")
    public ResultBody<?> getAllianceList(@RequestBody SubmitBody<BasePageDTO> request) {
        return ResultBody.success(request, allianceManageService.getAllianceList(request.getData()));
    }

    @Tag(name = "/allianceManage/" + ApiConstant.LATEST)
    @Operation(summary = "获取联盟详情（含公会列表）")
    @GetMapping("/" + ApiConstant.LATEST + "/getAllianceDetail/{allianceId}")
    public ResultBody<Map<String, Object>> getAllianceDetail(@PathVariable Long allianceId) {
        return ResultBody.success(allianceManageService.getAllianceDetail(allianceId));
    }

    @Tag(name = "/allianceManage/" + ApiConstant.LATEST)
    @Operation(summary = "修改联盟信息")
    @PostMapping("/" + ApiConstant.LATEST + "/updateAlliance")
    public ResultBody<Object> updateAlliance(@RequestBody SubmitBody<AllianceDO> request) {
        allianceManageService.updateAlliance(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/allianceManage/" + ApiConstant.LATEST)
    @Operation(summary = "解散联盟")
    @DeleteMapping("/" + ApiConstant.LATEST + "/disbandAlliance/{allianceId}")
    public ResultBody<Object> disbandAlliance(@PathVariable Long allianceId) {
        allianceManageService.disbandAlliance(allianceId);
        return ResultBody.success(null);
    }
}