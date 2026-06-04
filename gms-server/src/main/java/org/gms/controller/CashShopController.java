package org.gms.controller;

import com.mybatisflex.core.paginate.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.ModifiedCashItemDO;
import org.gms.model.dto.CashShopBatchOnSaleReqDTO;
import org.gms.model.dto.CashShopSearchRtnDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.pojo.CashCategory;
import org.gms.service.CashShopService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 【控制器】CashShopController（class），包 {@code org.gms.controller}。
 *
 * 处理点券商城（Cash Shop）的商品管理，包括商城分类查询、
 * 按分类分页查询商品列表、商品详情查询、商品上架/下架及批量上下架操作。
 *
 * @author 萧曵
 */
@RestController
@RequestMapping("/cashShop")
@AllArgsConstructor
public class CashShopController {
    /** 点券商城服务 */
    private final CashShopService cashShopService;

    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "获取商城全部分类")
    @GetMapping("/" + ApiConstant.LATEST + "/getAllCategoryList")
    public ResultBody<List<CashCategory>> getAllCategoryList() {
        return ResultBody.success(cashShopService.getAllCategoryList());
    }

    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "分页分类查询商品列表")
    @PostMapping("/" + ApiConstant.LATEST + "/getCommodityByCategory")
    public ResultBody<Page<CashShopSearchRtnDTO>> getCommodityByCategory(@RequestBody SubmitBody<CashCategory> request) {
        return ResultBody.success(cashShopService.getCommodityByCategory(request.getData()));
    }

    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "根据sn查询商品明细")
    @GetMapping("/" + ApiConstant.LATEST + "/getCommodityBySn/{sn}")
    public ResultBody<CashShopSearchRtnDTO> getCommodityBySn(@PathVariable("sn") Integer sn) {
        return ResultBody.success(cashShopService.getCommodityBySn(sn));
    }

    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "上架商品")
    @PostMapping("/" + ApiConstant.LATEST + "/onSale")
    public ResultBody<Object> onSale(@RequestBody SubmitBody<ModifiedCashItemDO> request) {
        request.getData().setOnSale(1);
        cashShopService.changeOnSale(request.getData());
        return ResultBody.success();
    }

    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "下架商品")
    @PostMapping("/" + ApiConstant.LATEST + "/offSale")
    public ResultBody<Object> offSale(@RequestBody SubmitBody<ModifiedCashItemDO> request) {
        request.getData().setOnSale(0);
        cashShopService.changeOnSale(request.getData());
        return ResultBody.success();
    }

    @Tag(name = "/cashShop/" + ApiConstant.LATEST)
    @Operation(summary = "批量上架商品")
    @PostMapping("/" + ApiConstant.LATEST + "/batchOnSale")
    public ResultBody<Object> batchOnSale(@RequestBody SubmitBody<CashShopBatchOnSaleReqDTO> request) {
        cashShopService.batchChangeOnSale(request.getData());
        return ResultBody.success();
    }
}