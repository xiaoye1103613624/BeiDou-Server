package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.CombatPowerRankItemDTO;
import org.gms.model.dto.EquipScoreRankItemDTO;
import org.gms.model.dto.RankingFilterOptionDTO;
import org.gms.model.dto.RankingQueryReqDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.RankingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ranking")
public class RankingController {
    private final RankingService rankingService;

    @Tag(name = "/ranking/" + ApiConstant.LATEST)
    @Operation(summary = "战力排行榜（总榜 / 职业 niche 榜，默认前20）")
    @PostMapping("/" + ApiConstant.LATEST + "/combatPower")
    public ResultBody<List<CombatPowerRankItemDTO>> combatPower(@RequestBody SubmitBody<RankingQueryReqDTO> body) {
        return ResultBody.success(rankingService.listCombatPower(body.getData()));
    }

    @Tag(name = "/ranking/" + ApiConstant.LATEST)
    @Operation(summary = "装备评分排行榜（总榜 / 部位榜，默认前20）")
    @PostMapping("/" + ApiConstant.LATEST + "/equipScore")
    public ResultBody<List<EquipScoreRankItemDTO>> equipScore(@RequestBody SubmitBody<RankingQueryReqDTO> body) {
        return ResultBody.success(rankingService.listEquipScore(body.getData()));
    }

    @Tag(name = "/ranking/" + ApiConstant.LATEST)
    @Operation(summary = "战力榜职业筛选项")
    @GetMapping("/" + ApiConstant.LATEST + "/jobNicheOptions")
    public ResultBody<List<RankingFilterOptionDTO>> jobNicheOptions() {
        return ResultBody.success(rankingService.listJobNicheOptions());
    }

    @Tag(name = "/ranking/" + ApiConstant.LATEST)
    @Operation(summary = "装备榜部位筛选项")
    @GetMapping("/" + ApiConstant.LATEST + "/slotCategoryOptions")
    public ResultBody<List<RankingFilterOptionDTO>> slotCategoryOptions() {
        return ResultBody.success(rankingService.listSlotCategoryOptions());
    }

    @Tag(name = "/ranking/" + ApiConstant.LATEST)
    @Operation(summary = "立即刷新战力与装备排行榜缓存")
    @PostMapping("/" + ApiConstant.LATEST + "/refresh")
    public ResultBody<Object> refresh() {
        rankingService.refreshAll();
        return ResultBody.success();
    }
}
