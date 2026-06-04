package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.gms.server.events.gm.MonsterInvasionManager;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.life.MonsterInformationProvider;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 怪物攻城控制器
 * 提供怪物攻城活动的配置和控制相关的REST API接口
 */
@RestController
@AllArgsConstructor
@RequestMapping("/monsterInvasion")
public class MonsterInvasionController {

    // ==================== 静态数据 ====================

    private static final List<Map<String, Object>> PRESETS = List.of(
            Map.ofEntries(
                    Map.entry("name", "简单"),
                    Map.entry("desc", "蜗牛/绿水灵x30 → 花蘑菇x15"),
                    Map.entry("waves", List.of(
                            Map.of("delay", 0, "mobs", List.of(Map.of("mobId", 100100, "count", 15), Map.of("mobId", 100101, "count", 10), Map.of("mobId", 210100, "count", 5))),
                            Map.of("delay", 90, "mobs", List.of(Map.of("mobId", 100120, "count", 15)))
                    )),
                    Map.entry("expRate", 2.0),
                    Map.entry("expDur", 30),
                    Map.entry("dropRate", 1.0),
                    Map.entry("dropDur", 0),
                    Map.entry("mesoRate", 1.0),
                    Map.entry("mesoDur", 0),
                    Map.entry("cash", 0),
                    Map.entry("meso", 50000),
                    Map.entry("itemId", 0),
                    Map.entry("itemCount", 0)
            ),
            Map.ofEntries(
                    Map.entry("name", "普通"),
                    Map.entry("desc", "花蘑菇x20 → 丝带猪x15 + 蝙蝠x10"),
                    Map.entry("waves", List.of(
                            Map.of("delay", 0, "mobs", List.of(Map.of("mobId", 100120, "count", 20))),
                            Map.of("delay", 60, "mobs", List.of(Map.of("mobId", 1210100, "count", 15), Map.of("mobId", 2130100, "count", 10)))
                    )),
                    Map.entry("expRate", 2.0),
                    Map.entry("expDur", 60),
                    Map.entry("dropRate", 2.0),
                    Map.entry("dropDur", 60),
                    Map.entry("mesoRate", 1.0),
                    Map.entry("mesoDur", 0),
                    Map.entry("cash", 500),
                    Map.entry("meso", 200000),
                    Map.entry("itemId", 0),
                    Map.entry("itemCount", 0)
            ),
            Map.ofEntries(
                    Map.entry("name", "困难"),
                    Map.entry("desc", "绿蘑菇x20 → 野猪x15 + 冰之眼x10 → 僵尸蘑菇x10 + 火野猪x5"),
                    Map.entry("waves", List.of(
                            Map.of("delay", 0, "mobs", List.of(Map.of("mobId", 1110100, "count", 20))),
                            Map.of("delay", 60, "mobs", List.of(Map.of("mobId", 2230100, "count", 15), Map.of("mobId", 2220100, "count", 10))),
                            Map.of("delay", 120, "mobs", List.of(Map.of("mobId", 2230101, "count", 10), Map.of("mobId", 2300100, "count", 5)))
                    )),
                    Map.entry("expRate", 3.0),
                    Map.entry("expDur", 120),
                    Map.entry("dropRate", 3.0),
                    Map.entry("dropDur", 120),
                    Map.entry("mesoRate", 2.0),
                    Map.entry("mesoDur", 120),
                    Map.entry("cash", 1000),
                    Map.entry("meso", 500000),
                    Map.entry("itemId", 0),
                    Map.entry("itemCount", 0)
            ),
            Map.ofEntries(
                    Map.entry("name", "地狱"),
                    Map.entry("desc", "火野猪x20 → 僵尸蘑菇王x10 + 冰之眼x15 → 全怪物x30"),
                    Map.entry("waves", List.of(
                            Map.of("delay", 0, "mobs", List.of(Map.of("mobId", 2300100, "count", 20))),
                            Map.of("delay", 60, "mobs", List.of(Map.of("mobId", 2230101, "count", 10), Map.of("mobId", 2220100, "count", 15))),
                            Map.of("delay", 120, "mobs", List.of(Map.of("mobId", 1110100, "count", 10), Map.of("mobId", 2230100, "count", 10), Map.of("mobId", 1210100, "count", 10)))
                    )),
                    Map.entry("expRate", 5.0),
                    Map.entry("expDur", 180),
                    Map.entry("dropRate", 5.0),
                    Map.entry("dropDur", 180),
                    Map.entry("mesoRate", 3.0),
                    Map.entry("mesoDur", 180),
                    Map.entry("cash", 3000),
                    Map.entry("meso", 1000000),
                    Map.entry("itemId", 0),
                    Map.entry("itemCount", 0)
            )
    );

    private static final List<Map<String, Object>> TOWNS = List.of(
            Map.of("name", "射手村", "mapId", 100000000),
            Map.of("name", "魔法密林", "mapId", 101000000),
            Map.of("name", "勇士部落", "mapId", 102000000),
            Map.of("name", "废弃都市", "mapId", 103000000),
            Map.of("name", "港口", "mapId", 104000000),
            Map.of("name", "诺特勒斯", "mapId", 120000000),
            Map.of("name", "天空之城", "mapId", 200000000),
            Map.of("name", "冰封雪域", "mapId", 211000000),
            Map.of("name", "玩具城", "mapId", 220000000),
            Map.of("name", "水下世界", "mapId", 230000000),
            Map.of("name", "神木村", "mapId", 240000000),
            Map.of("name", "武陵", "mapId", 250000000),
            Map.of("name", "百草村", "mapId", 251000000),
            Map.of("name", "阿里安特", "mapId", 260000000),
            Map.of("name", "玛加提亚", "mapId", 261000000),
            Map.of("name", "圣地", "mapId", 130000000),
            Map.of("name", "里恩", "mapId", 140000000),
            Map.of("name", "埃德尔斯坦", "mapId", 310000000)
    );

    // ==================== DTO ====================

    @Data
    @NoArgsConstructor
    public static class InvasionStartDTO {
        private int worldId;
        private int channelId;
        private int mapId;
        private int durationSeconds;
        private List<WaveDTO> waves;
        private float expRate = 1.0f;
        private int expDurationMin;
        private float dropRate = 1.0f;
        private int dropDurationMin;
        private float mesoRate = 1.0f;
        private int mesoDurationMin;
        private int cashReward;
        private int mesoReward;
        private int rewardItemId;
        private int rewardItemCount;
    }

    @Data
    @NoArgsConstructor
    public static class WaveDTO {
        private int delaySeconds;
        private List<MobEntryDTO> mobs;
    }

    @Data
    @NoArgsConstructor
    public static class MobEntryDTO {
        private int mobId;
        private int count;
    }

    @Data
    @NoArgsConstructor
    public static class MobSearchDTO {
        private String keyword;
    }

    @Data
    @NoArgsConstructor
    public static class CancelDTO {
        private int worldId;
    }

    // ==================== 接口 ====================

    @Tag(name = "/monsterInvasion/" + ApiConstant.LATEST)
    @Operation(summary = "获取预设难度列表")
    @GetMapping("/" + ApiConstant.LATEST + "/presets")
    public ResultBody<List<Map<String, Object>>> getPresets() {
        return ResultBody.success(PRESETS);
    }

    @Tag(name = "/monsterInvasion/" + ApiConstant.LATEST)
    @Operation(summary = "获取城镇列表")
    @GetMapping("/" + ApiConstant.LATEST + "/towns")
    public ResultBody<List<Map<String, Object>>> getTowns() {
        return ResultBody.success(TOWNS);
    }

    @Tag(name = "/monsterInvasion/" + ApiConstant.LATEST)
    @Operation(summary = "获取线路及在线人数")
    @GetMapping("/" + ApiConstant.LATEST + "/channels/{worldId}")
    public ResultBody<List<Map<String, Object>>> getChannels(@PathVariable("worldId") int worldId) {
        List<Map<String, Object>> result = new ArrayList<>();
        World world = Server.getInstance().getWorld(worldId);
        if (world != null) {
            for (Channel ch : world.getChannels()) {
                result.add(Map.of(
                        "id", ch.getId(),
                        "online", ch.getPlayerStorage().getSize()
                ));
            }
        }
        return ResultBody.success(result);
    }

    @Tag(name = "/monsterInvasion/" + ApiConstant.LATEST)
    @Operation(summary = "搜索怪物（ID或名称）")
    @PostMapping("/" + ApiConstant.LATEST + "/searchMobs")
    public ResultBody<List<Map<String, Object>>> searchMobs(@RequestBody SubmitBody<MobSearchDTO> request) {
        String keyword = request.getData().getKeyword().trim();
        List<Map<String, Object>> results = new ArrayList<>();

        // 尝试按ID精确匹配
        try {
            int id = Integer.parseInt(keyword);
            Monster mob = LifeFactory.getMonster(id);
            if (mob != null && !"MISSINGNO".equals(mob.getName())) {
                results.add(Map.of("mobId", id, "name", mob.getName()));
            }
        } catch (NumberFormatException ignored) {
        }

        // 按名称模糊匹配
        var nameResults = MonsterInformationProvider.getMobsIDsFromName(keyword);
        for (var pair : nameResults) {
            results.add(Map.of("mobId", pair.getLeft(), "name", pair.getRight()));
        }

        return ResultBody.success(request, results);
    }

    @Tag(name = "/monsterInvasion/" + ApiConstant.LATEST)
    @Operation(summary = "启动怪物攻城")
    @PostMapping("/" + ApiConstant.LATEST + "/start")
    public ResultBody<Map<String, Object>> startInvasion(@RequestBody SubmitBody<InvasionStartDTO> request) {
        InvasionStartDTO dto = request.getData();
        MonsterInvasionManager mgr = MonsterInvasionManager.getInstance();

        MonsterInvasionManager.InvasionConfig cfg = mgr.createConfig();
        cfg.setWorldId(dto.getWorldId());
        cfg.setChannelId(dto.getChannelId());
        cfg.setMapId(dto.getMapId());
        cfg.setDurationSeconds(dto.getDurationSeconds());

        if (dto.getWaves() != null) {
            for (WaveDTO w : dto.getWaves()) {
                MonsterInvasionManager.WaveConfig wc = mgr.createWaveConfig();
                wc.setDelaySeconds(w.getDelaySeconds());
                if (w.getMobs() != null) {
                    for (MobEntryDTO m : w.getMobs()) {
                        wc.addMob(m.getMobId(), m.getCount());
                    }
                }
                cfg.addWave(wc);
            }
        }

        cfg.setExpRate(dto.getExpRate());
        cfg.setExpDurationMinutes(dto.getExpDurationMin());
        cfg.setDropRate(dto.getDropRate());
        cfg.setDropDurationMinutes(dto.getDropDurationMin());
        cfg.setMesoRate(dto.getMesoRate());
        cfg.setMesoDurationMinutes(dto.getMesoDurationMin());
        cfg.setCashReward(dto.getCashReward());
        cfg.setMesoReward(dto.getMesoReward());
        cfg.setRewardItemId(dto.getRewardItemId());
        cfg.setRewardItemCount(dto.getRewardItemCount());

        boolean ok = mgr.startInvasion(cfg);
        return ResultBody.success(request, Map.of("success", ok, "message", ok ? "攻城已启动" : "该世界已有进行中的攻城"));
    }

    @Tag(name = "/monsterInvasion/" + ApiConstant.LATEST)
    @Operation(summary = "取消怪物攻城")
    @PostMapping("/" + ApiConstant.LATEST + "/cancel")
    public ResultBody<Map<String, Object>> cancelInvasion(@RequestBody SubmitBody<CancelDTO> request) {
        int worldId = request.getData().getWorldId();
        MonsterInvasionManager.getInstance().cancelInvasion(worldId);
        return ResultBody.success(request, Map.of("success", true, "message", "攻城已取消"));
    }

    @Tag(name = "/monsterInvasion/" + ApiConstant.LATEST)
    @Operation(summary = "获取攻城状态")
    @GetMapping("/" + ApiConstant.LATEST + "/status/{worldId}")
    public ResultBody<Map<String, Object>> getStatus(@PathVariable("worldId") int worldId) {
        Map<String, Object> status = MonsterInvasionManager.getInstance().getStatus(worldId);
        return ResultBody.success(status);
    }
}