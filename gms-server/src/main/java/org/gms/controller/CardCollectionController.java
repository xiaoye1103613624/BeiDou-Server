package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.CardCollectionConfigDO;
import org.gms.model.dto.BasePageDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.CardCollectionService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 卡片收集控制器
 * 提供卡片收集配置相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/cardCollection")
public class CardCollectionController {

    /** 卡片收集服务 */
    private final CardCollectionService cardCollectionService;

    @Tag(name = "/cardCollection/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取卡片收集配置列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<Page<CardCollectionConfigDO>> getConfigList(@RequestBody SubmitBody<BasePageDTO> request) {
        return ResultBody.success(request, cardCollectionService.getConfigList(request.getData()));
    }

    @Tag(name = "/cardCollection/" + ApiConstant.LATEST)
    @Operation(summary = "新增卡片收集配置")
    @PutMapping("/" + ApiConstant.LATEST + "/addConfig")
    public ResultBody<CardCollectionConfigDO> addConfig(@RequestBody SubmitBody<CardCollectionConfigDO> request) {
        request.getData().setId(null);
        return ResultBody.success(request, cardCollectionService.addConfig(request.getData()));
    }

    @Tag(name = "/cardCollection/" + ApiConstant.LATEST)
    @Operation(summary = "更新卡片收集配置")
    @PostMapping("/" + ApiConstant.LATEST + "/updateConfig")
    public ResultBody<Object> updateConfig(@RequestBody SubmitBody<CardCollectionConfigDO> request) {
        RequireUtil.requireNotNull(request.getData().getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        cardCollectionService.updateConfig(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/cardCollection/" + ApiConstant.LATEST)
    @Operation(summary = "删除卡片收集配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        cardCollectionService.deleteConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/cardCollection/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除卡片收集配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteConfigList")
    public ResultBody<Object> deleteConfigList(@RequestBody SubmitBody<List<Long>> request) {
        cardCollectionService.deleteConfigList(request.getData());
        return ResultBody.success(request, null);
    }
}