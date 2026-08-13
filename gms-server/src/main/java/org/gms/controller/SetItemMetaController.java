package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SetItemPreviewDTO;
import org.gms.model.dto.SetItemPreviewRequest;
import org.gms.model.dto.SetItemWzImportRequest;
import org.gms.model.dto.SubmitBody;
import org.gms.server.setitem.SetBonusColor;
import org.gms.service.SetItemService;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/setItem")
public class SetItemMetaController {
    private final SetItemService setItemService;

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "套装详情（WZ+DB 合并）")
    @GetMapping("/" + ApiConstant.LATEST + "/detail/{setId}")
    public ResultBody<Object> detail(@PathVariable int setId) {
        return ResultBody.success(setItemService.getDetail(setId));
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "WZ 套装预览列表")
    @GetMapping("/" + ApiConstant.LATEST + "/wz/list")
    public ResultBody<Object> wzList() {
        return ResultBody.success(setItemService.listWzSets());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "从 WZ 导入套装")
    @PostMapping("/" + ApiConstant.LATEST + "/wz/import")
    public ResultBody<Object> wzImport(@RequestBody SubmitBody<SetItemWzImportRequest> request) {
        return ResultBody.success(setItemService.importFromWz(request.getData()));
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "合并套装列表（WZ+DB）")
    @GetMapping("/" + ApiConstant.LATEST + "/merged/list")
    public ResultBody<Object> mergedList() {
        return ResultBody.success(setItemService.listMerged());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "战斗/套装属性字段字典")
    @GetMapping("/" + ApiConstant.LATEST + "/meta/statFields")
    public ResultBody<Object> statFields() {
        return ResultBody.success(setItemService.statFieldMeta());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "套装说明颜色枚举")
    @GetMapping("/" + ApiConstant.LATEST + "/meta/colors")
    public ResultBody<Object> colors() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (SetBonusColor c : SetBonusColor.values()) {
            Map<String, String> item = new LinkedHashMap<>();
            item.put("code", c.getCode());
            item.put("label", c.getLabel());
            result.put(c.name(), item);
        }
        return ResultBody.success(result);
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "套装加成预览")
    @PostMapping("/" + ApiConstant.LATEST + "/preview")
    public ResultBody<SetItemPreviewDTO> preview(@RequestBody SubmitBody<SetItemPreviewRequest> request) {
        return ResultBody.success(setItemService.preview(request.getData()));
    }
}
