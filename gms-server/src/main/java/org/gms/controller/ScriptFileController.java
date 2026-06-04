package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.service.ScriptFileService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 【控制器】ScriptFileController（class），包 {@code org.gms.controller}。
 *
 * 脚本文件管理，支持文件树展示、读写、创建/删除/重命名。
 * 当配置外部覆盖目录时，写入操作均作用于覆盖目录。
 *
 * @author 萧曵
 */
@RestController
@AllArgsConstructor
@RequestMapping("/scriptFile")
public class ScriptFileController {

    /** 脚本文件服务 */
    private final ScriptFileService scriptFileService;

    @Tag(name = "/scriptFile/" + ApiConstant.LATEST)
    @Operation(summary = "获取脚本文件树")
    @PostMapping("/" + ApiConstant.LATEST + "/tree")
    public ResultBody<List<ScriptTreeNodeDTO>> tree(@RequestBody SubmitBody<ScriptFileReadDTO> request) {
        return ResultBody.success(request, scriptFileService.tree(request.getData().getPath()));
    }

    @Tag(name = "/scriptFile/" + ApiConstant.LATEST)
    @Operation(summary = "读取脚本文件内容")
    @PostMapping("/" + ApiConstant.LATEST + "/read")
    public ResultBody<String> read(@RequestBody SubmitBody<ScriptFileReadDTO> request) {
        return ResultBody.success(request, scriptFileService.readFile(request.getData().getPath()));
    }

    @Tag(name = "/scriptFile/" + ApiConstant.LATEST)
    @Operation(summary = "写入脚本文件内容")
    @PostMapping("/" + ApiConstant.LATEST + "/write")
    public ResultBody<String> write(@RequestBody SubmitBody<ScriptFileWriteDTO> request) {
        scriptFileService.writeFile(request.getData().getPath(), request.getData().getContent());
        return ResultBody.success(request, "success");
    }

    @Tag(name = "/scriptFile/" + ApiConstant.LATEST)
    @Operation(summary = "创建脚本文件或目录")
    @PostMapping("/" + ApiConstant.LATEST + "/create")
    public ResultBody<String> create(@RequestBody SubmitBody<ScriptFileCreateDTO> request) {
        scriptFileService.createFile(request.getData().getPath(), request.getData().isDirectory());
        return ResultBody.success(request, "success");
    }

    @Tag(name = "/scriptFile/" + ApiConstant.LATEST)
    @Operation(summary = "删除脚本文件或目录")
    @PostMapping("/" + ApiConstant.LATEST + "/delete")
    public ResultBody<String> delete(@RequestBody SubmitBody<ScriptFileDeleteDTO> request) {
        scriptFileService.deleteFile(request.getData().getPath());
        return ResultBody.success(request, "success");
    }

    @Tag(name = "/scriptFile/" + ApiConstant.LATEST)
    @Operation(summary = "重命名脚本文件或目录")
    @PostMapping("/" + ApiConstant.LATEST + "/rename")
    public ResultBody<String> rename(@RequestBody SubmitBody<ScriptFileRenameDTO> request) {
        scriptFileService.renameFile(request.getData().getOldPath(), request.getData().getNewPath());
        return ResultBody.success(request, "success");
    }

    @Tag(name = "/scriptFile/" + ApiConstant.LATEST)
    @Operation(summary = "查询脚本覆盖目录状态")
    @GetMapping("/" + ApiConstant.LATEST + "/overrideStatus")
    public ResultBody<Map<String, Object>> overrideStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("active", scriptFileService.isOverrideActive());
        result.put("path", scriptFileService.getOverridePath());
        return ResultBody.success(result);
    }
}