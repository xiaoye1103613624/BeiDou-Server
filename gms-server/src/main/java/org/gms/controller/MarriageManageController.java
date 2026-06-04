package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.BasePageDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.MarriageManageService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 婚姻管理控制器
 * 提供婚姻管理相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/marriageManage")
public class MarriageManageController {

    /** 婚姻管理服务 */
    private final MarriageManageService marriageManageService;

    @Tag(name = "/marriageManage/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取婚姻列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getMarriageList")
    public ResultBody<Page<Map<String, Object>>> getMarriageList(@RequestBody SubmitBody<BasePageDTO> request) {
        return ResultBody.success(request, marriageManageService.getMarriageList(request.getData()));
    }

    @Tag(name = "/marriageManage/" + ApiConstant.LATEST)
    @Operation(summary = "解除婚姻")
    @DeleteMapping("/" + ApiConstant.LATEST + "/dissolveMarriage/{marriageId}")
    public ResultBody<Object> dissolveMarriage(@PathVariable Long marriageId) {
        marriageManageService.dissolveMarriage(marriageId);
        return ResultBody.success(null);
    }
}