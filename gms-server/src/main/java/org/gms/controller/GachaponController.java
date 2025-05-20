package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.GachaponRewardDO;
import org.gms.dao.entity.GachaponRewardPoolDO;
import org.gms.model.dto.GachaponPoolSearchReqDTO;
import org.gms.model.dto.GachaponPoolSearchRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.GachaponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 百宝接口
 */
@RestController
@RequestMapping("/gachapon")
public class GachaponController {
    @Autowired
    private GachaponService gachaponService;

    /**
     * 获取奖池列表接口
     * 该接口用于获取奖池的分页列表数据。
     *
     * @param request 请求体，包含分页查询条件GachaponPoolSearchReqDTO
     * @return ResultBody<Page<GachaponPoolSearchRtnDTO>> 包含奖池分页列表的响应体
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "获取奖池列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getPools")
    public ResultBody<Page<GachaponPoolSearchRtnDTO>> getPools(
            @RequestBody SubmitBody<GachaponPoolSearchReqDTO> request) {
        return ResultBody.success(request, gachaponService.getPools(request.getData()));
    }

    /**
     * 创建或更新奖池
     * 该接口用于创建或更新一个奖池，接收包含奖池信息的请求体，并调用服务层进行更新操作。
     *
     * @param request 包含奖池信息的请求体，类型为SubmitBody<GachaponRewardPoolDO>
     * @return 返回操作结果的ResponseBody，成功时返回成功信息
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "创建或更新奖池")
    @PostMapping("/" + ApiConstant.LATEST + "/updatePool")
    public ResultBody<Object> updatePool(@RequestBody SubmitBody<GachaponRewardPoolDO> request) {
        gachaponService.updatePool(request.getData());
        return ResultBody.success();
    }

    /**
     * 删除奖池接口
     * 该接口用于删除指定的奖池，通过请求体中的奖池ID进行删除操作。
     *
     * @param request 请求体，包含需要删除的奖池信息，具体为SubmitBody<GachaponRewardPoolDO>类型，其中GachaponRewardPoolDO包含奖池ID
     * @return 返回操作结果，成功则返回ResultBody.success()
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "删除奖池")
    @PostMapping("/" + ApiConstant.LATEST + "/deletePool")
    public ResultBody<Object> deletePool(@RequestBody SubmitBody<GachaponRewardPoolDO> request) {
        gachaponService.deletePool(request.getData().getId());
        return ResultBody.success();
    }

    /**
     * 获取奖品列表接口
     * 该接口用于获取指定抽奖奖池的奖品列表
     *
     * @param request 请求体，包含GachaponRewardPoolDO对象，其中包含了奖池的ID
     * @return ResultBody<List<GachaponRewardDO>> 包含奖品列表的响应体
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "获取奖品列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getRewards")
    public ResultBody<List<GachaponRewardDO>> getRewards(@RequestBody SubmitBody<GachaponRewardPoolDO> request) {
        return ResultBody.success(gachaponService.getRewards(request.getData().getId()));
    }

    /**
     * 创建或更新奖品
     * 该接口用于创建或更新一个抽奖奖品，通过传入的奖品数据对象进行更新或创建操作。
     *
     * @param request 包含要创建或更新的奖品数据的请求体对象
     * @return 返回操作结果，成功则返回成功信息
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "创建或更新奖品")
    @PostMapping("/" + ApiConstant.LATEST + "/updateReward")
    public ResultBody<Object> updateReward(@RequestBody SubmitBody<GachaponRewardDO> request) {
        gachaponService.updateReward(request.getData());
        return ResultBody.success();
    }

    /**
     * 删除奖品接口
     * 该接口用于删除指定的奖品，通过请求体中的奖品ID进行删除操作。
     *
     * @param request 请求体，包含要删除的奖品信息，具体为SubmitBody<GachaponRewardDO>类型，其中GachaponRewardDO包含奖品的ID
     * @return 返回操作结果，成功则返回ResultBody<Object>类型的成功响应
     */
    @Tag(name = "/gachapon/" + ApiConstant.LATEST)
    @Operation(summary = "删除奖品")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteReward")
    public ResultBody<Object> deleteReward(@RequestBody SubmitBody<GachaponRewardDO> request) {
        gachaponService.deleteReward(request.getData().getId());
        return ResultBody.success();
    }
}
