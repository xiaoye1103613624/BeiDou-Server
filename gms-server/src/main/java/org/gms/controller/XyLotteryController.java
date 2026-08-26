package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.dao.entity.XyLotteryItemDO;
import org.gms.dao.entity.XyLotteryMachineDO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.XyLotteryService;
import org.gms.util.RequireUtil;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/lottery")
public class XyLotteryController {
    private final XyLotteryService lotteryService;

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "抽奖机列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getMachines")
    public ResultBody<List<XyLotteryMachineDO>> getMachines() {
        return ResultBody.success(lotteryService.listMachines());
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "保存抽奖机")
    @PostMapping("/" + ApiConstant.LATEST + "/saveMachine")
    public ResultBody<XyLotteryMachineDO> saveMachine(@RequestBody SubmitBody<XyLotteryMachineDO> request) {
        return ResultBody.success(request, lotteryService.saveMachine(request.getData()));
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "删除抽奖机及奖品")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteMachine/{npcId}")
    public ResultBody<Object> deleteMachine(@PathVariable("npcId") Integer npcId) {
        lotteryService.deleteMachine(npcId);
        return ResultBody.success(null);
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "奖品列表（按类型+排序）")
    @GetMapping("/" + ApiConstant.LATEST + "/getItems/{npcId}")
    public ResultBody<List<XyLotteryItemDO>> getItems(@PathVariable("npcId") Integer npcId) {
        return ResultBody.success(lotteryService.listItems(npcId));
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "保存奖品")
    @PostMapping("/" + ApiConstant.LATEST + "/saveItem")
    public ResultBody<XyLotteryItemDO> saveItem(@RequestBody SubmitBody<XyLotteryItemDO> request) {
        return ResultBody.success(request, lotteryService.saveItem(request.getData()));
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "删除奖品")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteItem/{id}")
    public ResultBody<Object> deleteItem(@PathVariable("id") Long id) {
        lotteryService.deleteItem(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "按物品反查 NPC")
    @GetMapping("/" + ApiConstant.LATEST + "/findNpcsByItem/{itemId}")
    public ResultBody<List<Map<String, Object>>> findNpcsByItem(@PathVariable("itemId") Integer itemId) {
        return ResultBody.success(lotteryService.findNpcsByItem(itemId));
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "热重载全部")
    @PostMapping("/" + ApiConstant.LATEST + "/reloadAll")
    public ResultBody<Object> reloadAll() {
        lotteryService.reloadAll();
        return ResultBody.success(null);
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "热重载指定 NPC")
    @PostMapping("/" + ApiConstant.LATEST + "/reloadNpc/{npcId}")
    public ResultBody<Object> reloadNpc(@PathVariable("npcId") Integer npcId) {
        lotteryService.reloadNpc(npcId);
        return ResultBody.success(null);
    }

    private static final String DEFAULT_POOL_SCRIPT = "9310022_123.js";
    private static final Path ABSOLUTE_SCRIPT_DIR =
            Path.of("F:\\MXD_dev\\BeiDou-Server\\gms-server\\scripts-zh-CN\\npc");

    /**
     * 解析奖池脚本路径。支持 script 文件名（默认 9310022_123.js）或绝对/相对 path。
     */
    private Path resolvePoolScript(Map<String, Object> data) {
        String pathStr = data.get("path") == null ? null : String.valueOf(data.get("path"));
        if (pathStr == null || pathStr.isBlank()) {
            String script = data.get("script") == null
                    ? DEFAULT_POOL_SCRIPT
                    : String.valueOf(data.get("script")).trim();
            if (!script.endsWith(".js")) {
                script = script + ".js";
            }
            if (!script.contains("/") && !script.contains("\\")) {
                pathStr = "scripts-zh-CN/npc/" + script;
            } else {
                pathStr = script;
            }
        }
        Path path = Path.of(pathStr);
        if (path.isAbsolute()) {
            return path;
        }
        Path cwd = Path.of("").toAbsolutePath();
        Path candidate = cwd.resolve(pathStr);
        if (!candidate.toFile().isFile()) {
            candidate = cwd.resolve("gms-server").resolve(pathStr);
        }
        if (!candidate.toFile().isFile()) {
            String name = Path.of(pathStr).getFileName().toString();
            candidate = ABSOLUTE_SCRIPT_DIR.resolve(name);
        }
        return candidate;
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "从奖池脚本导入（默认 9310022_123.js）")
    @PostMapping("/" + ApiConstant.LATEST + "/importPool")
    public ResultBody<Map<String, Object>> importPool(@RequestBody SubmitBody<Map<String, Object>> request) throws Exception {
        Map<String, Object> data = request.getData() == null ? new HashMap<>() : request.getData();
        int npcId = data.get("npcId") == null ? 9310022 : Integer.parseInt(String.valueOf(data.get("npcId")));
        boolean replace = data.get("replace") == null || Boolean.parseBoolean(String.valueOf(data.get("replace")));
        Path path = resolvePoolScript(data);
        RequireUtil.requireNotNull(npcId, "npcId");
        return ResultBody.success(request, lotteryService.importFromScript(npcId, path, replace));
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "从 9310022_123.js 导入金猪奖池（替换）")
    @PostMapping("/" + ApiConstant.LATEST + "/import123")
    public ResultBody<Map<String, Object>> import123(@RequestBody SubmitBody<Map<String, Object>> request) throws Exception {
        Map<String, Object> data = request.getData() == null ? new HashMap<>() : new HashMap<>(request.getData());
        data.putIfAbsent("script", DEFAULT_POOL_SCRIPT);
        data.putIfAbsent("npcId", 9310022);
        data.putIfAbsent("replace", true);
        request.setData(data);
        return importPool(request);
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "兼容旧接口：从 9310022_303.js 导入")
    @PostMapping("/" + ApiConstant.LATEST + "/import303")
    public ResultBody<Map<String, Object>> import303(@RequestBody SubmitBody<Map<String, Object>> request) throws Exception {
        Map<String, Object> data = request.getData() == null ? new HashMap<>() : new HashMap<>(request.getData());
        data.putIfAbsent("script", "9310022_303.js");
        request.setData(data);
        return importPool(request);
    }

    @Tag(name = "/lottery/" + ApiConstant.LATEST)
    @Operation(summary = "探测物品类型")
    @GetMapping("/" + ApiConstant.LATEST + "/detectItemType/{itemId}")
    public ResultBody<Map<String, Object>> detectItemType(@PathVariable("itemId") Integer itemId) {
        Map<String, Object> m = new HashMap<>();
        m.put("itemId", itemId);
        m.put("itemType", XyLotteryService.detectItemType(itemId));
        return ResultBody.success(m);
    }
}
