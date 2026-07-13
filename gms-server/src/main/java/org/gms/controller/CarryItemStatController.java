package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.CarryItemStatDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.CarryItemStatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/carryItemStat")
public class CarryItemStatController {
    private final CarryItemStatService carryItemStatService;

    @Tag(name = "/carryItemStat/" + ApiConstant.LATEST)
    @Operation(summary = "携带物属性列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<CarryItemStatDO>> list() {
        return ResultBody.success(carryItemStatService.listAll());
    }

    @Tag(name = "/carryItemStat/" + ApiConstant.LATEST)
    @Operation(summary = "保存携带物属性")
    @PostMapping("/" + ApiConstant.LATEST + "/save")
    public ResultBody<Object> save(@RequestBody SubmitBody<CarryItemStatDO> request) {
        carryItemStatService.save(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/carryItemStat/" + ApiConstant.LATEST)
    @Operation(summary = "删除携带物属性")
    @PostMapping("/" + ApiConstant.LATEST + "/delete")
    public ResultBody<Object> delete(@RequestBody SubmitBody<Long> request) {
        carryItemStatService.delete(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/carryItemStat/" + ApiConstant.LATEST)
    @Operation(summary = "热重载携带物属性")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        carryItemStatService.reload();
        return ResultBody.success();
    }
}
