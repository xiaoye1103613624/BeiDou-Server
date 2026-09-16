package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.ClientFileService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 客户端 Data 文件浏览 / 建目录 / 删除（路径根来自 {@link org.gms.service.ClientPathService}）。
 * 若将来做管理端 UI，必须挂在「客户端」菜单（menu.client / client.ts），勿混入 game/system。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/clientFile")
public class ClientFileController {
    private final ClientFileService clientFileService;

    @Tag(name = "/clientFile/" + ApiConstant.LATEST)
    @Operation(summary = "列出客户端 Data 下相对目录的条目")
    @PostMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<Map<String, Object>> list(@RequestBody SubmitBody<Map<String, Object>> request) {
        final Map<String, Object> body = request.getData() == null ? Map.of() : request.getData();
        final String relativePath = body.get("relativePath") == null ? "" : String.valueOf(body.get("relativePath"));
        return ResultBody.success(clientFileService.list(relativePath));
    }

    @Tag(name = "/clientFile/" + ApiConstant.LATEST)
    @Operation(summary = "在相对目录下新建空文件夹")
    @PostMapping("/" + ApiConstant.LATEST + "/mkdir")
    public ResultBody<Map<String, Object>> mkdir(@RequestBody SubmitBody<Map<String, Object>> request) {
        final Map<String, Object> body = request.getData() == null ? Map.of() : request.getData();
        final String parent = body.get("relativePath") == null ? "" : String.valueOf(body.get("relativePath"));
        final String name = body.get("name") == null ? "" : String.valueOf(body.get("name"));
        return ResultBody.success(clientFileService.mkdir(parent, name));
    }

    @Tag(name = "/clientFile/" + ApiConstant.LATEST)
    @Operation(summary = "删除客户端 Data 下文件或目录（confirm=true；非空/顶层关键目录需 recursive）")
    @PostMapping("/" + ApiConstant.LATEST + "/delete")
    public ResultBody<Map<String, Object>> delete(@RequestBody SubmitBody<Map<String, Object>> request) {
        final Map<String, Object> body = request.getData() == null ? Map.of() : request.getData();
        final String relativePath = body.get("relativePath") == null ? "" : String.valueOf(body.get("relativePath"));
        final boolean recursive = Boolean.TRUE.equals(body.get("recursive"));
        final boolean confirm = Boolean.TRUE.equals(body.get("confirm"));
        return ResultBody.success(clientFileService.delete(relativePath, recursive, confirm));
    }
}
