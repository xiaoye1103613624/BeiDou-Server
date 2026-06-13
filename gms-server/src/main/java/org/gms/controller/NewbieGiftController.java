package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.NewbieGiftSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.NewbieGiftService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 新手礼包控制器 —— 提供礼包配置的 REST API 接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/newbieGift")
public class NewbieGiftController {

    private final NewbieGiftService newbieGiftService;

    @Tag(name = "/newbieGift/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有新手礼包配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<NewbieGiftSaveDTO>> getConfigList() {
        return ResultBody.success(newbieGiftService.getConfigList());
    }

    @Tag(name = "/newbieGift/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个新手礼包配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfig/{id}")
    public ResultBody<NewbieGiftSaveDTO> getConfig(@PathVariable("id") Long id) {
        return ResultBody.success(newbieGiftService.getConfigById(id));
    }

    @Tag(name = "/newbieGift/" + ApiConstant.LATEST)
    @Operation(summary = "保存新手礼包配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<NewbieGiftSaveDTO> saveConfig(@RequestBody SubmitBody<NewbieGiftSaveDTO> request) {
        return ResultBody.success(request, newbieGiftService.saveConfig(request.getData()));
    }

    @Tag(name = "/newbieGift/" + ApiConstant.LATEST)
    @Operation(summary = "删除新手礼包配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Long id) {
        newbieGiftService.deleteConfig(id);
        return ResultBody.success(null);
    }
}
