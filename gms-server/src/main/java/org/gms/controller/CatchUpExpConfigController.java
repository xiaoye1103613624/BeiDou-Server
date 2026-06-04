package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.CatchUpExpConfigDO;
import org.gms.model.dto.CatchUpExpConfigReqDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.CatchUpExpConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 追赶经验配置控制器
 * 提供追赶经验倍率配置相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/catchUpExpConfig")
public class CatchUpExpConfigController {

    /** 追赶经验配置服务 */
    private final CatchUpExpConfigService catchUpExpConfigService;

    @Tag(name = "/catchUpExpConfig/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取追赶经验配置列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<Page<CatchUpExpConfigDO>> getConfigList(@RequestBody SubmitBody<CatchUpExpConfigReqDTO> request) {
        return ResultBody.success(request, catchUpExpConfigService.getConfigList(request.getData()));
    }

    @Tag(name = "/catchUpExpConfig/" + ApiConstant.LATEST)
    @Operation(summary = "新增追赶经验配置")
    @PostMapping("/" + ApiConstant.LATEST + "/addConfig")
    public ResultBody<Object> addConfig(@RequestBody SubmitBody<CatchUpExpConfigDO> request) {
        catchUpExpConfigService.addConfig(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/catchUpExpConfig/" + ApiConstant.LATEST)
    @Operation(summary = "修改追赶经验配置")
    @PostMapping("/" + ApiConstant.LATEST + "/updateConfig")
    public ResultBody<Object> updateConfig(@RequestBody SubmitBody<CatchUpExpConfigDO> request) {
        catchUpExpConfigService.updateConfig(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/catchUpExpConfig/" + ApiConstant.LATEST)
    @Operation(summary = "删除追赶经验配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        catchUpExpConfigService.deleteConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/catchUpExpConfig/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除追赶经验配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteConfigList")
    public ResultBody<Object> deleteConfigList(@RequestBody SubmitBody<List<Long>> request) {
        catchUpExpConfigService.deleteConfigList(request.getData());
        return ResultBody.success(null);
    }
}