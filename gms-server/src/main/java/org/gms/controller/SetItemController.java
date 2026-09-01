package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.exception.BizException;
import org.gms.exception.BizExceptionEnum;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SetItemDTO;
import org.gms.model.dto.SetItemDetailDTO;
import org.gms.model.dto.SetItemPreviewDTO;
import org.gms.model.dto.SetItemPreviewRequest;
import org.gms.model.dto.SetItemWzImportRequest;
import org.gms.model.dto.SubmitBody;
import org.gms.service.SetItemService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/setItem")
public class SetItemController {
    private final SetItemService setItemService;

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "获取套装配置列表（仅 DB）")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<SetItemDTO>> list() {
        return ResultBody.success(setItemService.listAll());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "获取合并后的套装列表（WZ + DB）")
    @GetMapping("/" + ApiConstant.LATEST + "/merged/list")
    public ResultBody<List<SetItemDetailDTO>> mergedList() {
        return ResultBody.success(setItemService.listMerged());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "获取套装详情")
    @GetMapping("/" + ApiConstant.LATEST + "/detail/{setId}")
    public ResultBody<SetItemDetailDTO> detail(@PathVariable("setId") int setId) {
        SetItemDetailDTO detail = setItemService.getDetail(setId);
        if (detail == null) {
            throw new BizException(BizExceptionEnum.NOT_FOUND);
        }
        return ResultBody.success(detail);
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "获取 WZ 套装列表")
    @GetMapping("/" + ApiConstant.LATEST + "/wz/list")
    public ResultBody<List<SetItemDetailDTO>> wzList() {
        return ResultBody.success(setItemService.listWzSets());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "从 WZ 导入套装到 DB")
    @PostMapping("/" + ApiConstant.LATEST + "/wz/import")
    public ResultBody<Map<String, Object>> wzImport(@RequestBody SubmitBody<SetItemWzImportRequest> request) {
        return ResultBody.success(request, setItemService.importFromWz(request.getData()));
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "套装属性字段元数据")
    @GetMapping("/" + ApiConstant.LATEST + "/meta/statFields")
    public ResultBody<Map<String, Object>> statFields() {
        return ResultBody.success(setItemService.statFieldMeta());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "套装说明颜色元数据")
    @GetMapping("/" + ApiConstant.LATEST + "/meta/colors")
    public ResultBody<Map<String, Map<String, String>>> colors() {
        return ResultBody.success(setItemService.colorMeta());
    }

    @Tag(name = "/setItem/" + ApiConstant.LATEST)
    @Operation(summary = "预览套装加成说明")
    @PostMapping("/" + ApiConstant.LATEST + "/preview")
    public ResultBody<SetItemPreviewDTO> preview(@RequestBody SubmitBody<SetItemPreviewRequest> request) {
        return ResultBody.success(request, setItemService.preview(request.getData()));
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
