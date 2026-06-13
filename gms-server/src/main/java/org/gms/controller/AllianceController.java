package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.service.AllianceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 联盟管理控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/alliance")
public class AllianceController {

    private final AllianceService allianceService;

    @Tag(name = "/alliance/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有联盟列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<Map<String, Object>>> list() {
        return ResultBody.success(allianceService.getAllAlliances());
    }

    @Tag(name = "/alliance/" + ApiConstant.LATEST)
    @Operation(summary = "删除联盟")
    @DeleteMapping("/" + ApiConstant.LATEST + "/delete/{id}")
    public ResultBody<Object> delete(@PathVariable Long id) {
        allianceService.deleteAlliance(id);
        return ResultBody.success(null);
    }
}
