package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.BossConfigDO;
import org.gms.model.dto.BossConfigReqDTO;
import org.gms.model.dto.BossConfigRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.BossConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * BOSS配置控制器
 * 提供BOSS属性倍率配置相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/bossConfig")
public class BossConfigController {

    /** BOSS配置服务 */
    private final BossConfigService bossConfigService;

    @Tag(name = "/bossConfig/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取BOSS配置列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getBossConfigList")
    public ResultBody<Page<BossConfigRtnDTO>> getBossConfigList(@RequestBody SubmitBody<BossConfigReqDTO> request) {
        return ResultBody.success(request, bossConfigService.getBossConfigList(request.getData()));
    }

    @Tag(name = "/bossConfig/" + ApiConstant.LATEST)
    @Operation(summary = "新增BOSS配置")
    @PostMapping("/" + ApiConstant.LATEST + "/addBossConfig")
    public ResultBody<Object> addBossConfig(@RequestBody SubmitBody<BossConfigDO> request) {
        bossConfigService.addBossConfig(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/bossConfig/" + ApiConstant.LATEST)
    @Operation(summary = "修改BOSS配置")
    @PostMapping("/" + ApiConstant.LATEST + "/updateBossConfig")
    public ResultBody<Object> updateBossConfig(@RequestBody SubmitBody<BossConfigDO> request) {
        bossConfigService.updateBossConfig(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/bossConfig/" + ApiConstant.LATEST)
    @Operation(summary = "删除BOSS配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteBossConfig/{id}")
    public ResultBody<Object> deleteBossConfig(@PathVariable("id") Long id) {
        bossConfigService.deleteBossConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/bossConfig/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除BOSS配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteBossConfigList")
    public ResultBody<Object> deleteBossConfigList(@RequestBody SubmitBody<List<Long>> request) {
        bossConfigService.deleteBossConfigList(request.getData());
        return ResultBody.success(null);
    }
}