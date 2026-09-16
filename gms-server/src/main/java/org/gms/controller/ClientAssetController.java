package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.WindowCashShopClientSyncReqDTO;
import org.gms.model.dto.WindowCashShopClientSyncRtnDTO;
import org.gms.model.dto.WindowCashShopIconSyncReqDTO;
import org.gms.model.dto.WindowCashShopIconSyncRtnDTO;
import org.gms.server.cashshop.CashShopAssetCheck;
import org.gms.service.WindowCashShopService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 客户端资源相关 API（读客户端 Data 同步/校验）。
 * 图标补齐请走 {@link AssetController} / 游戏资源中心；本类 syncIcons 仅兼容旧调用。
 * 具体逻辑委托 {@link WindowCashShopService}，路径配置见 {@link ClientPathController}。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/clientAsset")
public class ClientAssetController {
    private final WindowCashShopService windowCashShopService;

    @Tag(name = "/clientAsset/" + ApiConstant.LATEST)
    @Operation(summary = "从已配置 ClientDataPath 扫描并 upsert 新商城分类/商品（幂等）")
    @PostMapping("/" + ApiConstant.LATEST + "/syncFromClientData")
    public ResultBody<WindowCashShopClientSyncRtnDTO> syncFromClientData(
            @RequestBody(required = false) SubmitBody<WindowCashShopClientSyncReqDTO> request) {
        final WindowCashShopClientSyncReqDTO data = request == null ? null : request.getData();
        return ResultBody.success(windowCashShopService.syncFromClientData(data));
    }

    @Tag(name = "/clientAsset/" + ApiConstant.LATEST)
    @Operation(summary = "【已收敛】同步图标 → POST /asset/v1/ensure")
    @PostMapping("/" + ApiConstant.LATEST + "/syncIcons")
    public ResultBody<WindowCashShopIconSyncRtnDTO> syncIcons(
            @RequestBody SubmitBody<WindowCashShopIconSyncReqDTO> request) {
        return ResultBody.success(windowCashShopService.syncIcons(request.getData()));
    }

    @Tag(name = "/clientAsset/" + ApiConstant.LATEST)
    @Operation(summary = "校验物品在服务端/客户端 Data 是否存在")
    @GetMapping("/" + ApiConstant.LATEST + "/checkAsset/{itemId}")
    public ResultBody<CashShopAssetCheck.Result> checkAsset(@PathVariable("itemId") int itemId) {
        return ResultBody.success(windowCashShopService.checkAsset(itemId));
    }
}
