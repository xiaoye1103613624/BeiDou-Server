package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.GameIconDO;
import org.gms.model.dto.GameIconSyncReqDTO;
import org.gms.model.dto.GameIconSyncRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.GameIconService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Locale;
import java.util.Optional;

/**
 * 共用图标：懒加载 GET + 批量同步 POST。
 * 路径 {@code /icon/v1/{type}/{id}} 无需鉴权，供后台 &lt;img&gt; 使用。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/icon")
public class IconController {
    private final GameIconService gameIconService;

    @Tag(name = "/icon/" + ApiConstant.LATEST)
    @Operation(summary = "读取图标（缺失时自动拉取并缓存）")
    @GetMapping("/" + ApiConstant.LATEST + "/{type}/{id}")
    public ResponseEntity<byte[]> getIcon(@PathVariable("type") String type,
                                          @PathVariable("id") Integer id,
                                          @RequestParam(value = "force", required = false) Boolean force) {
        if (id == null || id <= 0) {
            return ResponseEntity.notFound().build();
        }
        Optional<byte[]> bytes = gameIconService.resolveIconBytes(type, id, Boolean.TRUE.equals(force));
        if (bytes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .contentType(MediaType.IMAGE_PNG)
                .body(bytes.get());
    }

    @Tag(name = "/icon/" + ApiConstant.LATEST)
    @Operation(summary = "批量同步图标到 xy_icon_cache（需登录）")
    @PostMapping("/" + ApiConstant.LATEST + "/sync")
    public ResultBody<GameIconSyncRtnDTO> sync(@RequestBody SubmitBody<GameIconSyncReqDTO> request) {
        GameIconSyncReqDTO data = request.getData() == null ? new GameIconSyncReqDTO() : request.getData();
        return ResultBody.success(request, gameIconService.sync(data));
    }

    @Tag(name = "/icon/" + ApiConstant.LATEST)
    @Operation(summary = "查询是否已缓存（元数据，不含二进制）")
    @GetMapping("/" + ApiConstant.LATEST + "/meta/{type}/{id}")
    public ResultBody<Object> meta(@PathVariable("type") String type,
                                   @PathVariable("id") Integer id) {
        Optional<GameIconDO> row = gameIconService.findIcon(type, id);
        if (row.isEmpty()) {
            return ResultBody.success(java.util.Map.of(
                    "cached", false,
                    "type", type == null ? "" : type.toLowerCase(Locale.ROOT),
                    "id", id));
        }
        GameIconDO icon = row.get();
        return ResultBody.success(java.util.Map.of(
                "cached", true,
                "type", icon.getCategory(),
                "id", icon.getObjectId(),
                "version", icon.getVersion() == null ? 0 : icon.getVersion(),
                "region", icon.getRegion() == null ? "" : icon.getRegion(),
                "source", icon.getSource() == null ? "" : icon.getSource(),
                "url", gameIconService.sharedIconUrl(icon.getCategory(), icon.getObjectId())));
    }
}
