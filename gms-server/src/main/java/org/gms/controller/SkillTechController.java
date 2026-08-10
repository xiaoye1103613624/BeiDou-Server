package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.SkillTechDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.SkillTechService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/skillTech")
public class SkillTechController {
    private final SkillTechService skillTechService;

    @Tag(name = "/skillTech/" + ApiConstant.LATEST)
    @Operation(summary = "技改配置列表")
    @GetMapping("/" + ApiConstant.LATEST + "/list")
    public ResultBody<List<SkillTechDO>> list() {
        return ResultBody.success(skillTechService.listAll());
    }

    @Tag(name = "/skillTech/" + ApiConstant.LATEST)
    @Operation(summary = "预览技能当前加载状态")
    @GetMapping("/" + ApiConstant.LATEST + "/preview")
    public ResultBody<Map<String, Object>> preview(@RequestParam int skillId) {
        return ResultBody.success(skillTechService.previewSkill(skillId));
    }

    @Tag(name = "/skillTech/" + ApiConstant.LATEST)
    @Operation(summary = "保存技改配置并热重载")
    @PostMapping("/" + ApiConstant.LATEST + "/save")
    public ResultBody<Object> save(@RequestBody SubmitBody<SkillTechDO> request) {
        skillTechService.save(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/skillTech/" + ApiConstant.LATEST)
    @Operation(summary = "删除技改配置")
    @PostMapping("/" + ApiConstant.LATEST + "/delete")
    public ResultBody<Object> delete(@RequestBody SubmitBody<Long> request) {
        skillTechService.delete(request.getData());
        return ResultBody.success(request, null);
    }

    @Tag(name = "/skillTech/" + ApiConstant.LATEST)
    @Operation(summary = "热重载技改（WZ + DB）")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Object> reload() {
        skillTechService.reload();
        return ResultBody.success();
    }

    @Tag(name = "/skillTech/" + ApiConstant.LATEST)
    @Operation(summary = "同步客户端 Skill（写服务端 XML + 尝试 orange-wz）")
    @PostMapping("/" + ApiConstant.LATEST + "/syncClient")
    public ResultBody<Map<String, Object>> syncClient(@RequestBody SubmitBody<Integer> request) {
        return ResultBody.success(skillTechService.syncClient(request.getData()));
    }
}
