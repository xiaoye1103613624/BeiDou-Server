package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.EquipGrowthDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.EquipGrowthService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/equipGrowth")
public class EquipGrowthController {
    private final EquipGrowthService equipGrowthService;

    @Tag(name = "/equipGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "成长配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<EquipGrowthDTO>> list() {
        return ResultBody.success(equipGrowthService.listAll());
    }

    @Tag(name = "/equipGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "成长配置详情")
    @GetMapping("/" + ApiConstant.LATEST + "/detail/{itemId}")
    public ResultBody<EquipGrowthDTO> detail(@PathVariable int itemId) {
        return ResultBody.success(equipGrowthService.get(itemId));
    }

    @Tag(name = "/equipGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "保存成长配置")
    @PostMapping("/" + ApiConstant.LATEST + "/save")
    public ResultBody<Object> save(@RequestBody SubmitBody<EquipGrowthDTO> request) {
        equipGrowthService.save(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/equipGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "删除成长配置")
    @PostMapping("/" + ApiConstant.LATEST + "/delete")
    public ResultBody<Object> delete(@RequestBody SubmitBody<Long> request) {
        equipGrowthService.delete(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/equipGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "热重载成长缓存")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        equipGrowthService.reload();
        return ResultBody.success();
    }

    @Tag(name = "/equipGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "从服务端 Character.wz 导入成长数据（空 itemIds=扫全部可成长装，与套装 WZ 导入一致）")
    @PostMapping("/" + ApiConstant.LATEST + "/init")
    public ResultBody<Map<String, Object>> init(@RequestBody(required = false) Map<String, Object> body) {
        String mode = body != null && body.get("mode") != null ? String.valueOf(body.get("mode")) : "NEW_ONLY";
        @SuppressWarnings("unchecked")
        List<Integer> itemIds = body != null && body.get("itemIds") instanceof List<?>
                ? ((List<?>) body.get("itemIds")).stream()
                .map(o -> Integer.valueOf(String.valueOf(o)))
                .toList()
                : List.of();
        if (itemIds.isEmpty() && body != null && body.get("itemId") != null) {
            itemIds = List.of(Integer.valueOf(String.valueOf(body.get("itemId"))));
        }
        EquipGrowthService.MapResult r = equipGrowthService.initFromWz(mode, itemIds);
        Map<String, Object> out = new HashMap<>();
        out.put("imported", r.imported());
        out.put("skipped", r.skipped());
        return ResultBody.success(out);
    }

    @Tag(name = "/equipGrowth/" + ApiConstant.LATEST)
    @Operation(summary = "预览成长 tip 文案")
    @GetMapping("/" + ApiConstant.LATEST + "/preview/{itemId}")
    public ResultBody<Map<String, String>> preview(@PathVariable int itemId) {
        Map<String, String> out = new HashMap<>();
        out.put("text", equipGrowthService.previewTip(itemId));
        return ResultBody.success(out);
    }
}
