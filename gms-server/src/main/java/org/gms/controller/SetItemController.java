package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SetItemDTO;
import org.gms.model.dto.SubmitBody;
import org.gms.service.SetItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/setItem")
public class SetItemController {
    private final SetItemService setItemService;

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "获取套装配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<SetItemDTO>> list() {
        return ResultBody.success(setItemService.listAll());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "保存套装配置")
    @PostMapping("/" + ApiConstant.LATEST + "/save")
    public ResultBody<Object> save(@RequestBody SubmitBody<SetItemDTO> request) {
        setItemService.save(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "删除套装配置")
    @PostMapping("/" + ApiConstant.LATEST + "/delete")
    public ResultBody<Object> delete(@RequestBody SubmitBody<Long> request) {
        setItemService.delete(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "保存并热重载套装")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        setItemService.reload();
        return ResultBody.success();
    }
}
