package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.MysqlMonitorRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.ServerMonitorRtnDTO;
import org.gms.service.MonitorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作台只读监控接口：服务器资源与 MySQL/连接池。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/monitor")
public class MonitorController {

    private final MonitorService monitorService;

    @Tag(name = "/monitor/" + ApiConstant.LATEST)
    @Operation(summary = "查询服务器监控信息")
    @GetMapping("/" + ApiConstant.LATEST + "/serverInfo")
    public ResultBody<ServerMonitorRtnDTO> serverInfo() {
        return ResultBody.success(monitorService.getServerInfo());
    }

    @Tag(name = "/monitor/" + ApiConstant.LATEST)
    @Operation(summary = "查询 MySQL 监控信息")
    @GetMapping("/" + ApiConstant.LATEST + "/mysqlInfo")
    public ResultBody<MysqlMonitorRtnDTO> mysqlInfo() {
        return ResultBody.success(monitorService.getMysqlInfo());
    }
}
