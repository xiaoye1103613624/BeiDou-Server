package org.gms.controller;


import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.CommandInfoDO;
import org.gms.model.dto.*;
import org.gms.service.CommandService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/command")
public class CommandController {
    private final CommandService commandService;

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "查询命令库所有指令与状态")
    @PostMapping("/" + ApiConstant.LATEST + "/getCommandListFromDB")
    public ResultBody<Page<CommandReqDTO>> getCommandListFromDB(@RequestBody SubmitBody<CommandReqDTO> submitBody) {
        return ResultBody.success(commandService.getCommandListFromDB(submitBody.getData()));
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "更新命令库所有指令与状态")
    @PostMapping("/" + ApiConstant.LATEST + "/updateCommand")
    public ResultBody<CommandInfoDO> updateCommand(@RequestBody SubmitBody<CommandReqDTO> submitBody) {
        return ResultBody.success(commandService.updateCommand(submitBody.getData()));
    }

    //重载事件
    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "复用GM命令代码进行重载事件")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadEventsByGMCommand")
    public ResultBody reloadEventsByGMCommand() {
        commandService.reloadEventsByGMCommand();
        return ResultBody.success();
    }
    //重装传送点
    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "复用GM命令代码进行重装传送点")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadPortalsByGMCommand")
    public ResultBody reloadPortalsByGMCommand() {
        commandService.reloadPortalsByGMCommand();
        return ResultBody.success();
    }

    //重装地图
    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "复用GM命令代码进行重装地图")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadMapsByGMCommand")
    public ResultBody reloadMapsByGMCommand() {
        commandService.reloadMapsByGMCommand();
        return ResultBody.success();
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "热重载地图脚本缓存")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadMapScriptsByGMCommand")
    public ResultBody reloadMapScriptsByGMCommand() {
        commandService.reloadMapScriptsByGMCommand();
        return ResultBody.success();
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "热重载任务脚本缓存")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadQuestScriptsByGMCommand")
    public ResultBody reloadQuestScriptsByGMCommand() {
        commandService.reloadQuestScriptsByGMCommand();
        return ResultBody.success();
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "热重载NPC与物品脚本缓存")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadNpcScriptsByGMCommand")
    public ResultBody reloadNpcScriptsByGMCommand() {
        commandService.reloadNpcScriptsByGMCommand();
        return ResultBody.success();
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "热重载反应堆脚本与掉落缓存")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadReactorScriptsByGMCommand")
    public ResultBody reloadReactorScriptsByGMCommand() {
        commandService.reloadReactorScriptsByGMCommand();
        return ResultBody.success();
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "热重载全部脚本（事件/传送点/地图脚本/任务/NPC·物品/反应堆）")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadAllScriptsByGMCommand")
    public ResultBody reloadAllScriptsByGMCommand() {
        commandService.reloadAllScriptsByGMCommand();
        return ResultBody.success();
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "按路径列表热重载脚本缓存（不含 WZ）")
    @PostMapping("/" + ApiConstant.LATEST + "/reloadScriptsByPaths")
    public ResultBody<ReloadScriptsByPathsResultDTO> reloadScriptsByPaths(
            @RequestBody SubmitBody<ReloadScriptsByPathsDTO> submitBody) {
        return ResultBody.success(submitBody, commandService.reloadScriptsByPaths(submitBody.getData()));
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "复用GM命令代码重载商店")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadShopsByGMCommand")
    public ResultBody reloadShopsByGMCommand() {
        commandService.reloadShopsByGMCommand();
        return ResultBody.success();
    }

    @Tag(name = "/command/" + ApiConstant.LATEST)
    @Operation(summary = "复用GM命令代码重载怪物掉落")
    @GetMapping("/" + ApiConstant.LATEST + "/reloadDropsByGMCommand")
    public ResultBody reloadDropsByGMCommand() {
        commandService.reloadDropsByGMCommand();
        return ResultBody.success();
    }


}