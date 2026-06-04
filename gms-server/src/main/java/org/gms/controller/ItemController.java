package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ItemSearchResult;
import org.gms.model.dto.ResultBody;
import org.gms.server.ItemInformationProvider;
import org.gms.util.Pair;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 物品控制器
 * 提供物品搜索相关的REST API接口
 */
@RestController
@RequestMapping("/item")
public class ItemController {

    @Tag(name = "/item/" + ApiConstant.LATEST)
    @Operation(summary = "搜索物品（支持名称模糊匹配和ID精确匹配）")
    @GetMapping("/" + ApiConstant.LATEST + "/search")
    public ResultBody<List<ItemSearchResult>> search(@RequestParam String keyword,
                                                      @RequestParam(defaultValue = "20") int limit) {
        List<ItemSearchResult> results = new ArrayList<>();

        // 先尝试按ID精确匹配
        try {
            int itemId = Integer.parseInt(keyword);
            String name = ItemInformationProvider.getInstance().getName(itemId);
            if (name != null && !name.isEmpty()) {
                results.add(ItemSearchResult.builder().itemId(itemId).itemName(name).build());
            }
        } catch (NumberFormatException ignored) {
        }

        // 按名称模糊搜索
        if (keyword.length() >= 1) {
            ArrayList<Pair<Integer, String>> nameMatches = ItemInformationProvider.getItemsIDsFromName(keyword);
            for (Pair<Integer, String> pair : nameMatches) {
                if (results.size() >= limit) break;
                // 避免重复（如果ID已经精确匹配到了）
                boolean duplicate = results.stream().anyMatch(r -> r.getItemId().equals(pair.getLeft()));
                if (!duplicate) {
                    results.add(ItemSearchResult.builder().itemId(pair.getLeft()).itemName(pair.getRight()).build());
                }
            }
        }

        return ResultBody.success(results);
    }
}