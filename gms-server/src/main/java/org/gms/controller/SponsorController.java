package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.SponsorConfigDO;
import org.gms.dao.entity.SponsorRewardDO;
import org.gms.dao.entity.SponsorSkillOptionDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SkillInfoDTO;
import org.gms.model.dto.SubmitBody;
import org.gms.service.SponsorService;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 赞助档位 Web 后台管理。配置直接读写数据库，游戏侧 listConfigs/claim 实时读库，无需重启。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/sponsor")
public class SponsorController {

    private final SponsorService sponsorService;

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "获取全部赞助档位（含停用）")
    @GetMapping("/" + ApiConstant.LATEST + "/getConfigList")
    public ResultBody<List<SponsorConfigDO>> getConfigList() {
        return ResultBody.success(sponsorService.listAllConfigsAdmin());
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "保存赞助档位（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveConfig")
    public ResultBody<SponsorConfigDO> saveConfig(@RequestBody SubmitBody<SponsorConfigDO> request) {
        RequireUtil.requireNotNull(request.getData(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        return ResultBody.success(request, sponsorService.saveConfig(request.getData()));
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "切换赞助档位启用状态")
    @PostMapping("/" + ApiConstant.LATEST + "/toggleEnabled/{id}")
    public ResultBody<Object> toggleEnabled(@PathVariable("id") Integer id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        sponsorService.toggleConfigEnabled(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "删除赞助档位（同时删除奖励与领取记录）")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteConfig/{id}")
    public ResultBody<Object> deleteConfig(@PathVariable("id") Integer id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        sponsorService.deleteConfig(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "获取档位奖励列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getRewards/{configId}")
    public ResultBody<List<SponsorRewardDO>> getRewards(@PathVariable("configId") Integer configId) {
        RequireUtil.requireNotNull(configId, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "configId"));
        return ResultBody.success(sponsorService.listRewardsAdmin(configId));
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "保存档位奖励（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveReward")
    public ResultBody<SponsorRewardDO> saveReward(@RequestBody SubmitBody<SponsorRewardDO> request) {
        RequireUtil.requireNotNull(request.getData(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        return ResultBody.success(request, sponsorService.saveReward(request.getData()));
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "删除档位奖励")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteReward/{id}")
    public ResultBody<Object> deleteReward(@PathVariable("id") Integer id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        sponsorService.deleteReward(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "获取技能组可选技能列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getSkillOptions/{rewardId}")
    public ResultBody<List<SponsorSkillOptionDO>> getSkillOptions(@PathVariable("rewardId") Integer rewardId) {
        RequireUtil.requireNotNull(rewardId, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "rewardId"));
        return ResultBody.success(sponsorService.listSkillOptionsAdmin(rewardId));
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "保存技能组选项（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveSkillOption")
    public ResultBody<SponsorSkillOptionDO> saveSkillOption(@RequestBody SubmitBody<SponsorSkillOptionDO> request) {
        RequireUtil.requireNotNull(request.getData(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "data"));
        return ResultBody.success(request, sponsorService.saveSkillOption(request.getData()));
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "删除技能组选项")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteSkillOption/{id}")
    public ResultBody<Object> deleteSkillOption(@PathVariable("id") Integer id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        sponsorService.deleteSkillOption(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/sponsor/" + ApiConstant.LATEST)
    @Operation(summary = "查询技能名称与最大等级")
    @GetMapping("/" + ApiConstant.LATEST + "/getSkillInfo/{skillId}")
    public ResultBody<SkillInfoDTO> getSkillInfo(@PathVariable("skillId") Integer skillId) {
        RequireUtil.requireNotNull(skillId, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "skillId"));
        return ResultBody.success(sponsorService.getSkillInfo(skillId));
    }
}
