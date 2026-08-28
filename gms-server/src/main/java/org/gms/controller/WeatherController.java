package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.WeatherApplyDTO;
import org.gms.model.dto.WeatherStatusDTO;
import org.gms.service.WeatherAdminService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherAdminService weatherAdminService;

    @Tag(name = "/weather/" + ApiConstant.LATEST)
    @Operation(summary = "查询当前世界天气/时段状态")
    @GetMapping("/" + ApiConstant.LATEST + "/status")
    public ResultBody<WeatherStatusDTO> status() {
        return ResultBody.success(weatherAdminService.status());
    }

    @Tag(name = "/weather/" + ApiConstant.LATEST)
    @Operation(summary = "切换世界天气/时段（组合或互斥，语义同 !weather）")
    @PostMapping("/" + ApiConstant.LATEST + "/apply")
    public ResultBody<WeatherStatusDTO> apply(@RequestBody SubmitBody<WeatherApplyDTO> request) {
        return ResultBody.success(request, weatherAdminService.apply(request.getData()));
    }
}
