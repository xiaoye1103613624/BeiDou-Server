package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.ClientPathService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 全局客户端 Data 路径（单源配置）。窗口商城等业务通过此 API 读写路径，勿再内嵌副本。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/clientPath")
public class ClientPathController {
    private final ClientPathService clientPathService;

    @Tag(name = "/clientPath/" + ApiConstant.LATEST)
    @Operation(summary = "客户端 Data 路径信息（JVM -D 优先，否则 game_config）")
    @GetMapping("/" + ApiConstant.LATEST)
    public ResultBody<Map<String, Object>> get() {
        return ResultBody.success(clientPathService.getInfo());
    }

    @Tag(name = "/clientPath/" + ApiConstant.LATEST)
    @Operation(summary = "设置客户端 Data 绝对路径（空字符串=清空并跳过客户端校验）")
    @PostMapping("/" + ApiConstant.LATEST)
    public ResultBody<Map<String, Object>> set(@RequestBody SubmitBody<String> request) {
        return ResultBody.success(clientPathService.setPath(request.getData()));
    }

    @Tag(name = "/clientPath/" + ApiConstant.LATEST)
    @Operation(summary = "校验某路径是否像客户端 Data 根目录")
    @PostMapping("/" + ApiConstant.LATEST + "/validate")
    public ResultBody<Map<String, Object>> validate(@RequestBody SubmitBody<String> request) {
        return ResultBody.success(clientPathService.validate(request.getData()));
    }

    @Tag(name = "/clientPath/" + ApiConstant.LATEST)
    @Operation(summary = "列出目录下的子文件夹（用于选择客户端路径）")
    @PostMapping("/" + ApiConstant.LATEST + "/listDirectories")
    public ResultBody<List<Map<String, Object>>> listDirectories(@RequestBody SubmitBody<String> request) {
        return ResultBody.success(clientPathService.listDirectories(request.getData()));
    }
}
