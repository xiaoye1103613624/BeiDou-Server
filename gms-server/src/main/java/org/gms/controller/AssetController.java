package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.AssetEnsureReqDTO;
import org.gms.model.dto.AssetEnsureRtnDTO;
import org.gms.model.dto.AssetInfoRtnDTO;
import org.gms.model.dto.IconCacheReqDTO;
import org.gms.model.dto.IconCacheRtnDTO;
import org.gms.model.dto.IconResolveRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.AssetService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Unified game-asset resolve/ensure API. Prefer this over feature-specific icon sync.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/asset")
public class AssetController {
    private final AssetService assetService;

    @Tag(name = "/asset/" + ApiConstant.LATEST)
    @Operation(summary = "资源中心说明：落盘目录、Provider、客户端路径")
    @GetMapping("/" + ApiConstant.LATEST + "/info")
    public ResultBody<AssetInfoRtnDTO> info() {
        return ResultBody.success(assetService.info());
    }

    @Tag(name = "/asset/" + ApiConstant.LATEST)
    @Operation(summary = "解析图标 URL：本地 game-assets 优先")
    @GetMapping("/" + ApiConstant.LATEST + "/resolve/{category}/{id}")
    public ResultBody<IconResolveRtnDTO> resolve(
            @PathVariable("category") String category,
            @PathVariable("id") Integer id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        return ResultBody.success(assetService.resolve(category, id));
    }

    @Tag(name = "/asset/" + ApiConstant.LATEST)
    @Operation(summary = "解析图标 URL（query）")
    @GetMapping("/" + ApiConstant.LATEST + "/resolve")
    public ResultBody<IconResolveRtnDTO> resolveQuery(
            @RequestParam("category") String category,
            @RequestParam("id") Integer id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        return ResultBody.success(assetService.resolve(category, id));
    }

    @Tag(name = "/asset/" + ApiConstant.LATEST)
    @Operation(summary = "批量确保图标落入 game-assets（补齐缺失或强制刷新）")
    @PostMapping("/" + ApiConstant.LATEST + "/ensure")
    public ResultBody<AssetEnsureRtnDTO> ensure(@RequestBody SubmitBody<AssetEnsureReqDTO> request) {
        AssetEnsureReqDTO body = request.getData();
        RequireUtil.requireNotNull(body, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        return ResultBody.success(request, assetService.ensure(body));
    }

    @Tag(name = "/asset/" + ApiConstant.LATEST)
    @Operation(summary = "仅补齐缺失图标（force=false 的 ensure 别名）")
    @PostMapping("/" + ApiConstant.LATEST + "/ensureMissing")
    public ResultBody<AssetEnsureRtnDTO> ensureMissing(@RequestBody SubmitBody<AssetEnsureReqDTO> request) {
        AssetEnsureReqDTO body = request.getData() == null ? new AssetEnsureReqDTO() : request.getData();
        body.setForce(false);
        return ResultBody.success(request, assetService.ensure(body));
    }

    @Tag(name = "/asset/" + ApiConstant.LATEST)
    @Operation(summary = "兼容旧 IconCache 批量写法（内部转 ensure）")
    @PostMapping("/" + ApiConstant.LATEST + "/cache")
    public ResultBody<Object> cache(@RequestBody SubmitBody<IconCacheReqDTO> request) {
        IconCacheReqDTO body = request.getData();
        RequireUtil.requireNotNull(body, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        boolean force = Boolean.TRUE.equals(body.getForce());
        if (body.getItems() != null && !body.getItems().isEmpty()) {
            List<IconCacheRtnDTO.IconRef> refs = new ArrayList<>();
            for (IconCacheReqDTO.IconRef item : body.getItems()) {
                if (item == null || item.getId() == null) {
                    continue;
                }
                refs.add(IconCacheRtnDTO.IconRef.builder()
                        .category(item.getCategory())
                        .id(item.getId())
                        .build());
            }
            return ResultBody.success(request, assetService.cacheBatch(refs, force));
        }
        RequireUtil.requireNotNull(body.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        String category = body.getCategory() == null ? "item" : body.getCategory();
        IconCacheRtnDTO one = assetService.cacheOne(category, body.getId(), force);
        return ResultBody.success(request, Collections.singletonList(one));
    }
}
