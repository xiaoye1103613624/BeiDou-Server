package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.BasePageDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.FamilyManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 师徒家族管理控制器
 * 提供师徒家族管理相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/familyManage")
public class FamilyManageController {

    /** 师徒家族管理服务 */
    private final FamilyManageService familyManageService;

    @Tag(name = "/familyManage/" + ApiConstant.LATEST)
    @Operation(summary = "分页获取师徒家族列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getFamilyList")
    public ResultBody<Page<Map<String, Object>>> getFamilyList(@RequestBody SubmitBody<BasePageDTO> request) {
        return ResultBody.success(request, familyManageService.getFamilyList(request.getData()));
    }

    @Tag(name = "/familyManage/" + ApiConstant.LATEST)
    @Operation(summary = "获取家族成员列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getFamilyMembers/{familyId}")
    public ResultBody<List<Map<String, Object>>> getFamilyMembers(@PathVariable Integer familyId) {
        return ResultBody.success(familyManageService.getFamilyMembers(familyId));
    }

    @Tag(name = "/familyManage/" + ApiConstant.LATEST)
    @Operation(summary = "移除家族成员")
    @DeleteMapping("/" + ApiConstant.LATEST + "/removeFamilyMember/{cid}")
    public ResultBody<Object> removeFamilyMember(@PathVariable Integer cid) {
        familyManageService.removeFamilyMember(cid);
        return ResultBody.success(null);
    }
}