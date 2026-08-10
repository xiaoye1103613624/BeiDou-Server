package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.OpLogDO;
import org.gms.dao.entity.OpLogTypeDO;
import org.gms.log.OpLogType;
import org.gms.model.dto.OpLogSearchDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.OpLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 操作日志系统（Web 后台）。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/opLog")
public class OpLogController {

    private final OpLogService opLogService;

    @Tag(name = "/opLog/" + ApiConstant.LATEST)
    @Operation(summary = "分页查询操作日志")
    @PostMapping("/" + ApiConstant.LATEST + "/page")
    public ResultBody<Page<OpLogDO>> page(@RequestBody SubmitBody<OpLogSearchDTO> request) {
        return ResultBody.success(request, opLogService.pageLogs(request.getData()));
    }

    @Tag(name = "/opLog/" + ApiConstant.LATEST)
    @Operation(summary = "获取操作类型样式绑定列表")
    @GetMapping("/" + ApiConstant.LATEST + "/typeList")
    public ResultBody<List<OpLogTypeDO>> typeList() {
        return ResultBody.success(opLogService.listTypes());
    }

    @Tag(name = "/opLog/" + ApiConstant.LATEST)
    @Operation(summary = "获取聊天样式预设")
    @GetMapping("/" + ApiConstant.LATEST + "/chatStyles")
    public ResultBody<Map<Integer, String>> chatStyles() {
        return ResultBody.success(OpLogType.chatStylePresets());
    }

    @Tag(name = "/opLog/" + ApiConstant.LATEST)
    @Operation(summary = "创建或更新操作类型样式绑定")
    @PostMapping("/" + ApiConstant.LATEST + "/saveType")
    public ResultBody<Object> saveType(@RequestBody SubmitBody<OpLogTypeDO> request) {
        opLogService.saveType(request.getData());
        return ResultBody.success(null);
    }

    @Tag(name = "/opLog/" + ApiConstant.LATEST)
    @Operation(summary = "删除操作类型样式绑定")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteType/{id}")
    public ResultBody<Object> deleteType(@PathVariable("id") Integer id) {
        opLogService.deleteType(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/opLog/" + ApiConstant.LATEST)
    @Operation(summary = "热重载类型绑定缓存")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        opLogService.reloadTypes();
        return ResultBody.success(null);
    }
}