package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.IconCacheReqDTO;
import org.gms.model.dto.IconCacheRtnDTO;
import org.gms.model.dto.IconResolveRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.server.icon.SharedIconFiles;
import org.gms.service.GameIconService;
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

@RestController
@AllArgsConstructor
@RequestMapping("/icon")
public class IconController {
    private final GameIconService gameIconService;

    @Tag(name = "/icon/" + ApiConstant.LATEST)
    @Operation(summary = "解析图标 URL：本地 game-assets 优先，否则返回 maplestory.io CDN")
    @GetMapping("/" + ApiConstant.LATEST + "/resolve/{category}/{id}")
    public ResultBody<IconResolveRtnDTO> resolve(
            @PathVariable("category") String category,
            @PathVariable("id") Integer id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        return ResultBody.success(gameIconService.resolve(category, id));
    }

    @Tag(name = "/icon/" + ApiConstant.LATEST)
    @Operation(summary = "解析图标 URL（query 形式）")
    @GetMapping("/" + ApiConstant.LATEST + "/resolve")
    public ResultBody<IconResolveRtnDTO> resolveQuery(
            @RequestParam("category") String category,
            @RequestParam("id") Integer id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        return ResultBody.success(gameIconService.resolve(category, id));
    }

    @Tag(name = "/icon/" + ApiConstant.LATEST)
    @Operation(summary = "将远程图标缓存到本地 static/game-assets/{type}/{id}.png")
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
            return ResultBody.success(request, gameIconService.cacheBatch(refs, force));
        }

        RequireUtil.requireNotNull(body.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        String category = body.getCategory() == null ? "item" : body.getCategory();
        IconCacheRtnDTO one = gameIconService.cacheOne(category, body.getId(), force);
        return ResultBody.success(request, Collections.singletonList(one));
    }

    @Tag(name = "/icon/" + ApiConstant.LATEST)
    @Operation(summary = "图标缓存根目录说明")
    @GetMapping("/" + ApiConstant.LATEST + "/info")
    public ResultBody<Object> info() {
        return ResultBody.success(Collections.singletonMap("root", SharedIconFiles.describeRoot()));
    }
}
