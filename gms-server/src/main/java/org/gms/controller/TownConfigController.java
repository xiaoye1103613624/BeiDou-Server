package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.TownConfigDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.TownConfigReqDTO;
import org.gms.service.TownConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【控制器】TownConfigController（class），包 {@code org.gms.controller}。
 * 
 * <p>处理城镇开放状态配置的REST API接口，包括城镇配置的分页查询、新增、修改、删除及批量删除操作。</p>
 */
@RestController
@AllArgsConstructor
@RequestMapping("/townConfig")
public class TownConfigController {

    /** 城镇配置服务，提供配置的增删改查业务逻辑 */
    private final TownConfigService townConfigService;

    /**
     * 分页获取城镇配置列表。
     * 
     * @param request 包含分页条件和筛选条件的请求体
     * @return 分页后的城镇配置列表
     */
    @Tag(name = "/townConfig/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取城镇配置列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getTownConfigList")
    public ResultBody<Page<TownConfigDO>> getTownConfigList(@RequestBody SubmitBody<TownConfigReqDTO> request) {
        return ResultBody.success(request, townConfigService.getTownConfigList(request.getData()));
    }

    /**
     * 新增城镇配置。
     * 
     * @param request 包含城镇配置数据的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/townConfig/" + ApiConstant.LATEST)
    @Operation(summary = "新增城镇配置")
    @PostMapping("/" + ApiConstant.LATEST + "/addTownConfig")
    public ResultBody<Object> addTownConfig(@RequestBody SubmitBody<TownConfigDO> request) {
        townConfigService.addTownConfig(request.getData());
        return ResultBody.success(request, null);
    }

    /**
     * 修改城镇配置。
     * 
     * @param request 包含城镇配置数据的请求体（须包含ID）
     * @return 操作成功结果
     */
    @Tag(name = "/townConfig/" + ApiConstant.LATEST)
    @Operation(summary = "修改城镇配置")
    @PostMapping("/" + ApiConstant.LATEST + "/updateTownConfig")
    public ResultBody<Object> updateTownConfig(@RequestBody SubmitBody<TownConfigDO> request) {
        townConfigService.updateTownConfig(request.getData());
        return ResultBody.success(request, null);
    }

    /**
     * 删除单个城镇配置。
     * 
     * @param id 配置ID
     * @return 操作成功结果
     */
    @Tag(name = "/townConfig/" + ApiConstant.LATEST)
    @Operation(summary = "删除城镇配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteTownConfig/{id}")
    public ResultBody<Object> deleteTownConfig(@PathVariable("id") Long id) {
        townConfigService.deleteTownConfig(id);
        return ResultBody.success(null);
    }

    /**
     * 批量删除城镇配置。
     * 
     * @param request 包含配置ID列表的请求体
     * @return 操作成功结果
     */
    @Tag(name = "/townConfig/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除城镇配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteTownConfigList")
    public ResultBody<Object> deleteTownConfigList(@RequestBody SubmitBody<List<Long>> request) {
        townConfigService.deleteTownConfigList(request.getData());
        return ResultBody.success(null);
    }
}