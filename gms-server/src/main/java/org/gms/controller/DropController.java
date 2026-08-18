package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.GameIconDO;
import org.gms.model.dto.*;
import org.gms.service.DropService;
import org.gms.service.GameIconService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/drop")
public class DropController {
    private final DropService dropService;
    private final GameIconService gameIconService;

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "按怪物分组分页列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getMobGroupList")
    public ResultBody<Page<MobDropGroupDTO>> getMobGroupList(@RequestBody SubmitBody<DropSearchReqDTO> request) {
        return ResultBody.success(request, dropService.getMobGroupList(request.getData()));
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取掉落列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getDropList")
    public ResultBody<Page<DropSearchRtnDTO>> getDropList(@RequestBody SubmitBody<DropSearchReqDTO> request) {
        return ResultBody.success(request, dropService.getDropList(request.getData(), false));
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取全局掉落列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getGlobalDropList")
    public ResultBody<Page<DropSearchRtnDTO>> getGlobalDropList(@RequestBody SubmitBody<DropSearchReqDTO> request) {
        return ResultBody.success(request, dropService.getDropList(request.getData(), true));
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "新增掉落，返回新增id")
    @PutMapping("/" + ApiConstant.LATEST + "/addDropData")
    public ResultBody<Long> addDropData(@RequestBody SubmitBody<DropSearchRtnDTO> request) {
        request.getData().setId(null);
        return ResultBody.success(request, dropService.modifyDropData(request.getData(), false, false));
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "新增全局掉落，返回新增id")
    @PutMapping("/" + ApiConstant.LATEST + "/addGlobalDropData")
    public ResultBody<Long> addGlobalDropData(@RequestBody SubmitBody<DropSearchRtnDTO> request) {
        request.getData().setId(null);
        return ResultBody.success(request, dropService.modifyDropData(request.getData(), true, false));
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id更新掉落信息")
    @PostMapping("/" + ApiConstant.LATEST + "/updateDropData")
    public ResultBody<Object> updateDropData(@RequestBody SubmitBody<DropSearchRtnDTO> request) {
        RequireUtil.requireNotNull(request.getData().getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        dropService.modifyDropData(request.getData(), false, false);
        return ResultBody.success(request, null);
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id更新全局掉落信息")
    @PostMapping("/" + ApiConstant.LATEST + "/updateGlobalDropData")
    public ResultBody<Object> updateGlobalDropData(@RequestBody SubmitBody<DropSearchRtnDTO> request) {
        RequireUtil.requireNotNull(request.getData().getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        dropService.modifyDropData(request.getData(), true, false);
        return ResultBody.success(request, null);
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id删除掉落信息")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteDropData/{id}")
    public ResultBody<Object> deleteDropData(@PathVariable("id") Long id) {
        dropService.modifyDropData(DropSearchRtnDTO.builder().id(id).build(), false, true);
        return ResultBody.success(null);
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "根据id删除全局掉落信息")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteGlobalDropData/{id}")
    public ResultBody<Object> deleteGlobalDropData(@PathVariable("id") Long id) {
        dropService.modifyDropData(DropSearchRtnDTO.builder().id(id).build(), true, true);
        return ResultBody.success(null);
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "初始化/批量同步图标（小册子版本，默认227）")
    @PostMapping("/" + ApiConstant.LATEST + "/syncIcons")
    public ResultBody<GameIconSyncRtnDTO> syncIcons(@RequestBody SubmitBody<GameIconSyncReqDTO> request) {
        GameIconSyncReqDTO data = request.getData() == null ? new GameIconSyncReqDTO() : request.getData();
        return ResultBody.success(request, gameIconService.sync(data));
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "切换全局掉落启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleGlobalDropEnabled/{id}")
    public ResultBody<Object> toggleGlobalDropEnabled(@PathVariable("id") Long id) {
        dropService.toggleGlobalDropEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/drop/" + ApiConstant.LATEST)
    @Operation(summary = "读取已持久化图标（供 <img> 使用，无需鉴权）")
    @GetMapping("/" + ApiConstant.LATEST + "/icon/{category}/{objectId}")
    public ResponseEntity<byte[]> getIcon(@PathVariable("category") String category,
                                          @PathVariable("objectId") Integer objectId) {
        return gameIconService.findIcon(category, objectId)
                .map(this::toIconResponse)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<byte[]> toIconResponse(GameIconDO icon) {
        MediaType mediaType = MediaType.IMAGE_PNG;
        if (icon.getContentType() != null && !icon.getContentType().isBlank()) {
            try {
                mediaType = MediaType.parseMediaType(icon.getContentType());
            } catch (Exception ignored) {
                mediaType = MediaType.IMAGE_PNG;
            }
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .contentType(mediaType)
                .body(icon.getIconData());
    }
}
