package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.SysMenuDTO;
import org.gms.model.dto.SysMenuReorderDTO;
import org.gms.model.dto.SysMenuRouteDTO;
import org.gms.service.SysMenuService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sysMenu")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Tag(name = "/sysMenu/" + ApiConstant.LATEST)
    @Operation(summary = "菜单树（管理用，可含禁用）")
    @GetMapping("/" + ApiConstant.LATEST + "/tree")
    public ResultBody<List<SysMenuDTO>> tree(
            @RequestParam(name = "includeDisabled", required = false, defaultValue = "true") boolean includeDisabled) {
        return ResultBody.success(sysMenuService.listTree(includeDisabled));
    }

    @Tag(name = "/sysMenu/" + ApiConstant.LATEST)
    @Operation(summary = "侧栏菜单路由树（仅启用且未隐藏）")
    @GetMapping("/" + ApiConstant.LATEST + "/sidebar")
    public ResultBody<List<SysMenuRouteDTO>> sidebar() {
        return ResultBody.success(sysMenuService.listSidebarRoutes());
    }

    @Tag(name = "/sysMenu/" + ApiConstant.LATEST)
    @Operation(summary = "新增或更新菜单")
    @PostMapping("/" + ApiConstant.LATEST + "/save")
    public ResultBody<SysMenuDTO> save(@RequestBody SubmitBody<SysMenuDTO> request) {
        return ResultBody.success(request, sysMenuService.save(request.getData()));
    }

    @Tag(name = "/sysMenu/" + ApiConstant.LATEST)
    @Operation(summary = "删除菜单（含子节点）")
    @PostMapping("/" + ApiConstant.LATEST + "/delete")
    public ResultBody<Object> delete(@RequestBody SubmitBody<Long> request) {
        sysMenuService.delete(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/sysMenu/" + ApiConstant.LATEST)
    @Operation(summary = "批量调整父级与排序")
    @PostMapping("/" + ApiConstant.LATEST + "/reorder")
    public ResultBody<Object> reorder(@RequestBody SubmitBody<SysMenuReorderDTO> request) {
        sysMenuService.reorder(request.getData());
        return ResultBody.success(request, null);
    }
}
