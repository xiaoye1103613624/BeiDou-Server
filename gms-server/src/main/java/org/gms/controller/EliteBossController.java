package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.*;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.gms.service.EliteBossService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 【控制器】EliteBossController（class），包 {@code org.gms.controller}。
 *
 * 精英BOSS（野外BOSS）配置与实时状态管理，包括列表查询、新增/修改/删除配置、
 * 指定频道召唤/清除BOSS等操作。
 *
 * @author 萧曵
 */
@RestController
@AllArgsConstructor
@RequestMapping("/eliteBoss")
public class EliteBossController {

    /** 精英BOSS服务 */
    private final EliteBossService eliteBossService;

    @Tag(name = "/eliteBoss/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取精英BOSS列表（含实时存活状态）")
    @PostMapping("/" + ApiConstant.LATEST + "/getEliteBossList")
    public ResultBody<Page<EliteBossStatusRtnDTO>> getEliteBossList(@RequestBody SubmitBody<EliteBossConfigReqDTO> request) {
        return ResultBody.success(request, eliteBossService.getEliteBossList(request.getData()));
    }

    @Tag(name = "/eliteBoss/" + ApiConstant.LATEST)
    @Operation(summary = "新增精英BOSS配置")
    @PutMapping("/" + ApiConstant.LATEST + "/addEliteBossConfig")
    public ResultBody<Long> addEliteBossConfig(@RequestBody SubmitBody<EliteBossConfigReqDTO> request) {
        request.getData().setId(null);
        return ResultBody.success(request, eliteBossService.addEliteBossConfig(request.getData()));
    }

    @Tag(name = "/eliteBoss/" + ApiConstant.LATEST)
    @Operation(summary = "更新精英BOSS配置")
    @PostMapping("/" + ApiConstant.LATEST + "/updateEliteBossConfig")
    public ResultBody<Object> updateEliteBossConfig(@RequestBody SubmitBody<EliteBossConfigReqDTO> request) {
        RequireUtil.requireNotNull(request.getData().getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        eliteBossService.updateEliteBossConfig(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/eliteBoss/" + ApiConstant.LATEST)
    @Operation(summary = "删除精英BOSS配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteEliteBossConfig/{id}")
    public ResultBody<Object> deleteEliteBossConfig(@PathVariable("id") Long id) {
        eliteBossService.deleteEliteBossConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/eliteBoss/" + ApiConstant.LATEST)
    @Operation(summary = "召唤精英BOSS（指定大区/频道/数量）")
    @PostMapping("/" + ApiConstant.LATEST + "/spawnEliteBoss")
    public ResultBody<String> spawnEliteBoss(@RequestBody SubmitBody<EliteBossSpawnReqDTO> request) {
        return ResultBody.success(request, eliteBossService.spawnEliteBoss(request.getData()));
    }

    @Tag(name = "/eliteBoss/" + ApiConstant.LATEST)
    @Operation(summary = "清除精英BOSS（指定大区/频道）")
    @PostMapping("/" + ApiConstant.LATEST + "/killEliteBoss")
    public ResultBody<String> killEliteBoss(@RequestBody SubmitBody<EliteBossSpawnReqDTO> request) {
        return ResultBody.success(request, eliteBossService.killEliteBoss(request.getData()));
    }

    @Tag(name = "/eliteBoss/" + ApiConstant.LATEST)
    @Operation(summary = "获取大区/频道列表（用于召唤选择）")
    @GetMapping("/" + ApiConstant.LATEST + "/worldChannels")
    public ResultBody<List<Map<String, Object>>> getWorldChannels() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (World world : Server.getInstance().getWorlds()) {
            List<Map<String, Object>> channels = new ArrayList<>();
            for (Channel ch : world.getChannels()) {
                channels.add(Map.of("id", ch.getId(), "online", ch.getPlayerStorage().getSize()));
            }
            result.add(Map.of("worldId", world.getId(), "channels", channels));
        }
        return ResultBody.success(result);
    }
}