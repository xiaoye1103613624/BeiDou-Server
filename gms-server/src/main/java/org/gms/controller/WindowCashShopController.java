package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.XyCashShopCategoryDO;
import org.gms.dao.entity.XyCashShopItemDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.model.dto.WindowCashShopClientSyncReqDTO;
import org.gms.model.dto.WindowCashShopClientSyncRtnDTO;
import org.gms.model.dto.WindowCashShopIconSyncReqDTO;
import org.gms.model.dto.WindowCashShopIconSyncRtnDTO;
import org.gms.server.cashshop.CashShopAssetCheck;
import org.gms.server.cashshop.CashShopClickType;
import org.gms.service.WindowCashShopService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 窗口现金商城管理（与经典 Commodity 商城 {@link CashShopController} 分离）。
 */
@RestController
@AllArgsConstructor
@RequestMapping("/windowCashShop")
public class WindowCashShopController {
    private final WindowCashShopService windowCashShopService;

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "客户端 Data 路径信息（可配置，不写死）")
    @GetMapping("/" + ApiConstant.LATEST + "/clientDataPath")
    public ResultBody<Map<String, Object>> clientDataPath() {
        return ResultBody.success(windowCashShopService.getClientDataPathInfo());
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "设置客户端 Data 绝对路径（空字符串=清空并跳过客户端校验）")
    @PostMapping("/" + ApiConstant.LATEST + "/clientDataPath")
    public ResultBody<Map<String, Object>> setClientDataPath(@RequestBody SubmitBody<String> request) {
        return ResultBody.success(windowCashShopService.setClientDataPath(request.getData()));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "校验某路径是否像客户端 Data 根目录")
    @PostMapping("/" + ApiConstant.LATEST + "/clientDataPath/validate")
    public ResultBody<Map<String, Object>> validateClientDataPath(@RequestBody SubmitBody<String> request) {
        return ResultBody.success(windowCashShopService.validateClientDataPath(request.getData()));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "列出目录下的子文件夹（用于选择客户端路径）")
    @PostMapping("/" + ApiConstant.LATEST + "/listDirectories")
    public ResultBody<List<Map<String, Object>>> listDirectories(@RequestBody SubmitBody<String> request) {
        return ResultBody.success(windowCashShopService.listDirectories(request.getData()));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "分类点击类型枚举（含预留）")
    @GetMapping("/" + ApiConstant.LATEST + "/clickTypes")
    public ResultBody<List<String>> clickTypes() {
        return ResultBody.success(Arrays.stream(CashShopClickType.values()).map(Enum::name).toList());
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "分类列表")
    @GetMapping("/" + ApiConstant.LATEST + "/categories")
    public ResultBody<List<XyCashShopCategoryDO>> categories() {
        return ResultBody.success(windowCashShopService.listCategories());
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "保存分类")
    @PostMapping("/" + ApiConstant.LATEST + "/category/save")
    public ResultBody<XyCashShopCategoryDO> saveCategory(@RequestBody SubmitBody<XyCashShopCategoryDO> request) {
        return ResultBody.success(windowCashShopService.saveCategory(request.getData()));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "删除分类")
    @PostMapping("/" + ApiConstant.LATEST + "/category/delete")
    public ResultBody<Object> deleteCategory(@RequestBody SubmitBody<Integer> request) {
        windowCashShopService.deleteCategory(request.getData());
        return ResultBody.success();
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "商品列表（扁平）")
    @GetMapping("/" + ApiConstant.LATEST + "/items")
    public ResultBody<List<XyCashShopItemDO>> items() {
        return ResultBody.success(windowCashShopService.listItems());
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "商品列表（按分类归组，管理端展示用）")
    @GetMapping("/" + ApiConstant.LATEST + "/itemsGrouped")
    public ResultBody<List<Map<String, Object>>> itemsGrouped() {
        return ResultBody.success(windowCashShopService.listItemsGrouped());
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "保存商品（默认校验服务端存在；requireClient=true 时还校验已配置的客户端 Data）")
    @PostMapping("/" + ApiConstant.LATEST + "/item/save")
    public ResultBody<XyCashShopItemDO> saveItem(
            @RequestBody SubmitBody<XyCashShopItemDO> request,
            @RequestParam(value = "requireClient", defaultValue = "false") boolean requireClient) {
        return ResultBody.success(windowCashShopService.saveItem(request.getData(), requireClient));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "校验物品在服务端/客户端资源是否存在")
    @GetMapping("/" + ApiConstant.LATEST + "/item/checkAsset/{itemId}")
    public ResultBody<CashShopAssetCheck.Result> checkAsset(@PathVariable int itemId) {
        return ResultBody.success(windowCashShopService.checkAsset(itemId));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "将商品挂到分类（热卖可重复挂）")
    @PostMapping("/" + ApiConstant.LATEST + "/link")
    public ResultBody<Object> link(@RequestBody SubmitBody<Map<String, Integer>> request) {
        final Map<String, Integer> d = request.getData();
        windowCashShopService.linkItem(d.get("categoryId"), d.get("itemId"), d.get("sort"), d.get("enabled"));
        return ResultBody.success();
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "从分类移除商品关联")
    @PostMapping("/" + ApiConstant.LATEST + "/unlink")
    public ResultBody<Object> unlink(@RequestBody SubmitBody<Map<String, Integer>> request) {
        final Map<String, Integer> d = request.getData();
        windowCashShopService.unlinkItem(d.get("categoryId"), d.get("itemId"));
        return ResultBody.success();
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "全量热重载（DB 优先，否则 TSV）")
    @PostMapping("/" + ApiConstant.LATEST + "/reload")
    public ResultBody<Map<String, Object>> reload() {
        return ResultBody.success(windowCashShopService.reloadAll());
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "按分类热重载（仅替换对应 legacy 桶）")
    @PostMapping("/" + ApiConstant.LATEST + "/reloadCategory")
    public ResultBody<Map<String, Object>> reloadCategory(@RequestBody SubmitBody<Integer> request) {
        return ResultBody.success(windowCashShopService.reloadCategory(request.getData()));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "从 catalog.tsv 导入 DB（引导用）")
    @PostMapping("/" + ApiConstant.LATEST + "/importTsv")
    public ResultBody<Map<String, Object>> importTsv(
            @RequestParam(value = "onlyIfEmpty", defaultValue = "true") boolean onlyIfEmpty) {
        return ResultBody.success(windowCashShopService.importFromTsv(onlyIfEmpty));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "按 ID 范围浏览服务端存在的物品（WZ/ItemInfo）")
    @PostMapping("/" + ApiConstant.LATEST + "/browseItems")
    public ResultBody<List<Map<String, Object>>> browseItems(@RequestBody SubmitBody<Map<String, Object>> request) {
        final Map<String, Object> d = request.getData() == null ? Map.of() : request.getData();
        final Integer minId = d.get("minId") == null ? null : ((Number) d.get("minId")).intValue();
        final Integer maxId = d.get("maxId") == null ? null : ((Number) d.get("maxId")).intValue();
        final String keyword = d.get("keyword") == null ? null : String.valueOf(d.get("keyword"));
        return ResultBody.success(windowCashShopService.browseItems(minId, maxId, keyword));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "批量导入物品到分类")
    @PostMapping("/" + ApiConstant.LATEST + "/importItems")
    @SuppressWarnings("unchecked")
    public ResultBody<Map<String, Object>> importItems(@RequestBody SubmitBody<Map<String, Object>> request) {
        final Map<String, Object> d = request.getData();
        final int categoryId = ((Number) d.get("categoryId")).intValue();
        final List<Number> raw = (List<Number>) d.get("itemIds");
        final List<Integer> itemIds = raw == null ? List.of()
                : raw.stream().map(Number::intValue).toList();
        final Integer price = d.get("price") == null ? null : ((Number) d.get("price")).intValue();
        final boolean requireClient = Boolean.TRUE.equals(d.get("requireClient"));
        return ResultBody.success(windowCashShopService.importItems(categoryId, itemIds, price, requireClient));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "按序重排分类 sort")
    @PostMapping("/" + ApiConstant.LATEST + "/reorderCategories")
    public ResultBody<Object> reorderCategories(@RequestBody SubmitBody<List<Integer>> request) {
        windowCashShopService.reorderCategories(request.getData());
        return ResultBody.success();
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "从 WZ 刷新全部商品中文名（覆盖 DB 英文/TSV 名）")
    @PostMapping("/" + ApiConstant.LATEST + "/refreshNamesFromWz")
    public ResultBody<Map<String, Object>> refreshNamesFromWz() {
        return ResultBody.success(windowCashShopService.refreshNamesFromWz());
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "种子：热卖榜 / 动漫皮肤 / 伤害皮肤入口；动漫自动灌入 1008900-1009999")
    @PostMapping("/" + ApiConstant.LATEST + "/seedDefaults")
    public ResultBody<Map<String, Object>> seedDefaults() {
        return ResultBody.success(windowCashShopService.seedDefaults());
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "同步商品图标：fillEmpty 仅填空 icon_url；force 覆盖。可写 item-icons/{id}.png")
    @PostMapping("/" + ApiConstant.LATEST + "/syncIcons")
    public ResultBody<WindowCashShopIconSyncRtnDTO> syncIcons(
            @RequestBody SubmitBody<WindowCashShopIconSyncReqDTO> request) {
        return ResultBody.success(windowCashShopService.syncIcons(request.getData()));
    }

    @Tag(name = "/windowCashShop/" + ApiConstant.LATEST)
    @Operation(summary = "从已配置 ClientDataPath 扫描并 upsert 分类/商品（幂等）")
    @PostMapping("/" + ApiConstant.LATEST + "/syncFromClientData")
    public ResultBody<WindowCashShopClientSyncRtnDTO> syncFromClientData(
            @RequestBody(required = false) SubmitBody<WindowCashShopClientSyncReqDTO> request) {
        final WindowCashShopClientSyncReqDTO data = request == null ? null : request.getData();
        return ResultBody.success(windowCashShopService.syncFromClientData(data));
    }
}
