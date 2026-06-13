package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.service.GuildService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 公会管理控制器
 */
@RestController
@AllArgsConstructor
@RequestMapping("/guild")
public class GuildController {

    private final GuildService guildService;

    @Tag(name = "/guild/" + ApiConstant.LATEST)
    @Operation(summary = "获取所有公会列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<Map<String, Object>>> list() {
        return ResultBody.success(guildService.getAllGuilds());
    }

    @Tag(name = "/guild/" + ApiConstant.LATEST)
    @Operation(summary = "获取公会成员列表")
    @GetMapping("/" + ApiConstant.LATEST + "/members/{guildId}")
    public ResultBody<List<Map<String, Object>>> members(@PathVariable Long guildId) {
        return ResultBody.success(guildService.getGuildMembers(guildId));
    }

    @Tag(name = "/guild/" + ApiConstant.LATEST)
    @Operation(summary = "删除公会")
    @DeleteMapping("/" + ApiConstant.LATEST + "/delete/{guildId}")
    public ResultBody<Object> delete(@PathVariable Long guildId) {
        guildService.deleteGuild(guildId);
        return ResultBody.success(null);
    }
}
