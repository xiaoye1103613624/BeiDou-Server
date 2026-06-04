package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.service.FileTreeService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 【控制器】FileController（class），包 {@code org.gms.controller}。
 *
 * 处理服务器文件树的管理，包括脚本等文件的读取、写入（在线编辑）
 * 以及目录树的浏览与导航。
 *
 * @author 萧曵
 */
@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileController {
    /** 文件树服务 */
    private final FileTreeService fileTreeService;

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "读取文件")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/read")
    public ResultBody<String> treeRead(@RequestBody SubmitBody<FileReadDTO> request) {
        return ResultBody.success(request, fileTreeService.readFile(request.getData().getCurrentKey(), request.getData().getTitle()));
    }

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "写入文件")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/write")
    public ResultBody<String> treeWrite(@RequestBody SubmitBody<FileWriteDTO> request) {
        fileTreeService.writeFile(request.getData().getCurrentKey(), request.getData().getTitle(), request.getData().getContent());
        return ResultBody.success(request,"写入成功");
    }

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "读取文件树")
    @PostMapping("/" + ApiConstant.LATEST + "/tree")
    public ResultBody<List<FileTreeNodeDTO>> tree(@RequestBody SubmitBody<FileTreeDTO> request) {
        return ResultBody.success(request, fileTreeService.tree(request.getData().getCurrentKey()));
    }

}