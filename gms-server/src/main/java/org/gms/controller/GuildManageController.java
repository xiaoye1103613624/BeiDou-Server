package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.GuildsDO;
import org.gms.model.dto.*;
import org.gms.service.GuildManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公会管理控制器
 * 提供公会管理相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/guildManage")
public class GuildManageController {

    /** 公会管理服务 */
    private final GuildManageService guildManageService;

    @Tag(name = "/guildManage/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取公会列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getGuildList")
    public ResultBody<Page<GuildListRtnDTO>> getGuildList(@RequestBody SubmitBody<GuildManageReqDTO> request) {
        return ResultBody.success(request, guildManageService.getGuildList(request.getData()));
    }

    @Tag(name = "/guildManage/" + ApiConstant.LATEST)
    @Operation(summary = "获取公会成员列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getGuildMembers/{guildId}")
    public ResultBody<List<GuildMemberRtnDTO>> getGuildMembers(@PathVariable Long guildId) {
        return ResultBody.success(guildManageService.getGuildMembers(guildId));
    }

    @Tag(name = "/guildManage/" + ApiConstant.LATEST)
    @Operation(summary = "修改公会信息")
    @PostMapping("/" + ApiConstant.LATEST + "/updateGuild")
    public ResultBody<Object> updateGuild(@RequestBody SubmitBody<GuildsDO> request) {
        guildManageService.updateGuild(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/guildManage/" + ApiConstant.LATEST)
    @Operation(summary = "解散公会")
    @DeleteMapping("/" + ApiConstant.LATEST + "/disbandGuild/{guildId}")
    public ResultBody<Object> disbandGuild(@PathVariable Long guildId) {
        guildManageService.disbandGuild(guildId);
        return ResultBody.success(null);
    }
}