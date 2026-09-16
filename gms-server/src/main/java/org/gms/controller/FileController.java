package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.FileMutateDTO;
import org.gms.model.dto.FileReadDTO;
import org.gms.model.dto.FileTreeDTO;
import org.gms.model.dto.FileTreeNodeDTO;
import org.gms.model.dto.FileWriteDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.FileTreeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/file")
public class FileController {
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
        return ResultBody.success(request, "ok");
    }

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "读取文件树")
    @PostMapping("/" + ApiConstant.LATEST + "/tree")
    public ResultBody<List<FileTreeNodeDTO>> tree(@RequestBody SubmitBody<FileTreeDTO> request) {
        return ResultBody.success(request, fileTreeService.tree(request.getData().getCurrentKey()));
    }

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "新建文件或文件夹")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/create")
    public ResultBody<FileTreeNodeDTO> treeCreate(@RequestBody SubmitBody<FileMutateDTO> request) {
        FileMutateDTO data = request.getData();
        boolean directory = Boolean.TRUE.equals(data.getDirectory());
        return ResultBody.success(request, fileTreeService.create(data.getCurrentKey(), data.getName(), directory));
    }

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "重命名文件或文件夹")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/rename")
    public ResultBody<Object> treeRename(@RequestBody SubmitBody<FileMutateDTO> request) {
        FileMutateDTO data = request.getData();
        fileTreeService.rename(data.getCurrentKey(), data.getName());
        return ResultBody.success(request);
    }

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "复制文件或文件夹")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/copy")
    public ResultBody<Object> treeCopy(@RequestBody SubmitBody<FileMutateDTO> request) {
        FileMutateDTO data = request.getData();
        fileTreeService.copy(data.getCurrentKey(), data.getTargetParentKey(), data.getName());
        return ResultBody.success(request);
    }

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "移动文件或文件夹")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/move")
    public ResultBody<Object> treeMove(@RequestBody SubmitBody<FileMutateDTO> request) {
        FileMutateDTO data = request.getData();
        fileTreeService.move(data.getCurrentKey(), data.getTargetParentKey());
        return ResultBody.success(request);
    }

    @Tag(name = "/file/" + ApiConstant.LATEST)
    @Operation(summary = "删除文件或文件夹")
    @PostMapping("/" + ApiConstant.LATEST + "/tree/delete")
    public ResultBody<Object> treeDelete(@RequestBody SubmitBody<FileMutateDTO> request) {
        fileTreeService.delete(request.getData().getCurrentKey());
        return ResultBody.success(request);
    }
}
