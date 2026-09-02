package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.SidebarToolConfigDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SidebarScriptTreeNodeDTO;
import org.gms.model.dto.SubmitBody;
import org.gms.service.SidebarToolService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 游戏右边栏 ServerTool Web 管理。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/sidebarTool")
public class SidebarToolController {

    private final SidebarToolService sidebarToolService;

    @Tag(name = "/sidebarTool/" + ApiConstant.LATEST)
    @Operation(summary = "获取右边栏工具列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<SidebarToolConfigDO>> list() {
        return ResultBody.success(sidebarToolService.listAll());
    }

    @Tag(name = "/sidebarTool/" + ApiConstant.LATEST)
    @Operation(summary = "脚本目录树（BeiDouSpecial，供 TreeSelect）")
    @GetMapping("/" + ApiConstant.LATEST + "/scriptTree")
    public ResultBody<List<SidebarScriptTreeNodeDTO>> scriptTree() {
        return ResultBody.success(sidebarToolService.listScriptTree());
    }

    @Tag(name = "/sidebarTool/" + ApiConstant.LATEST)
    @Operation(summary = "保存单项右边栏工具")
    @PostMapping("/" + ApiConstant.LATEST + "/save")
    public ResultBody<Object> save(@RequestBody SubmitBody<SidebarToolConfigDO> request) {
        sidebarToolService.save(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/sidebarTool/" + ApiConstant.LATEST)
    @Operation(summary = "批量保存右边栏工具")
    @PostMapping("/" + ApiConstant.LATEST + "/saveAll")
    public ResultBody<Object> saveAll(@RequestBody SubmitBody<List<SidebarToolConfigDO>> request) {
        sidebarToolService.saveAll(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/sidebarTool/" + ApiConstant.LATEST)
    @Operation(summary = "热重载并同步在线客户端")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        sidebarToolService.reload();
        return ResultBody.success();
    }
}
