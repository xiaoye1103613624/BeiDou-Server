package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.AdminUserDTO;
import org.gms.model.dto.AssignAccountRoleDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.SysRoleBindMenusDTO;
import org.gms.model.dto.SysRoleDTO;
import org.gms.service.SysRoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sysRole")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @Tag(name = "/sysRole/" + ApiConstant.LATEST)
    @Operation(summary = "角色列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultBody<List<SysRoleDTO>> list() {
        return ResultBody.success(sysRoleService.listRoles());
    }

    @Tag(name = "/sysRole/" + ApiConstant.LATEST)
    @Operation(summary = "角色已绑定菜单 ID")
    @GetMapping("/" + ApiConstant.LATEST + "/menus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultBody<List<Long>> menus(@RequestParam("roleId") Long roleId) {
        return ResultBody.success(sysRoleService.listMenuIdsByRoleId(roleId));
    }

    @Tag(name = "/sysRole/" + ApiConstant.LATEST)
    @Operation(summary = "绑定角色菜单")
    @PostMapping("/" + ApiConstant.LATEST + "/bindMenus")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultBody<Object> bindMenus(@RequestBody SubmitBody<SysRoleBindMenusDTO> request) {
        sysRoleService.bindMenus(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/sysRole/" + ApiConstant.LATEST)
    @Operation(summary = "后台可登录用户列表")
    @GetMapping("/" + ApiConstant.LATEST + "/adminUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultBody<List<AdminUserDTO>> adminUsers() {
        return ResultBody.success(sysRoleService.listAdminUsers());
    }

    @Tag(name = "/sysRole/" + ApiConstant.LATEST)
    @Operation(summary = "为游戏账号分配后台角色")
    @PostMapping("/" + ApiConstant.LATEST + "/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResultBody<Object> assign(@RequestBody SubmitBody<AssignAccountRoleDTO> request) {
        sysRoleService.assignAccountRole(request.getData());
        return ResultBody.success(request, null);
    }
}
