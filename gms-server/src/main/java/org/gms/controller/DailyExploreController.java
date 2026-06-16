package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.DailyExploreSaveDTO;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.DailyExploreService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 每日探索控制器 —— 提供地图池、随机奖励、完成奖励的 REST API 接口
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/dailyExplore")
public class DailyExploreController {

    private final DailyExploreService dailyExploreService;

    // ==================== 地图池配置 ====================

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "获取每日探索地图池列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getMapList")
    public ResultBody<List<DailyExploreSaveDTO>> getMapList() {
        return ResultBody.success(dailyExploreService.getMapList());
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "获取单个地图池配置")
    @GetMapping("/" + ApiConstant.LATEST + "/getMap/{id}")
    public ResultBody<DailyExploreSaveDTO> getMap(@PathVariable("id") Long id) {
        return ResultBody.success(dailyExploreService.getMapById(id));
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "保存地图池配置（新增或更新）")
    @PostMapping("/" + ApiConstant.LATEST + "/saveMap")
    public ResultBody<DailyExploreSaveDTO> saveMap(@RequestBody SubmitBody<DailyExploreSaveDTO> request) {
        return ResultBody.success(request, dailyExploreService.saveMap(request.getData()));
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "删除地图池配置")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteMap/{id}")
    public ResultBody<Object> deleteMap(@PathVariable("id") Long id) {
        dailyExploreService.deleteMap(id);
        return ResultBody.success(null);
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "批量删除地图池配置")
    @PostMapping("/" + ApiConstant.LATEST + "/deleteMapBatch")
    public ResultBody<Object> deleteMapBatch(@RequestBody SubmitBody<List<Long>> request) {
        dailyExploreService.deleteMapBatch(request.getData());
        return ResultBody.success(null);
    }

    // ==================== 每轮随机奖励 ====================

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "获取每轮随机奖励列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getRewardList")
    public ResultBody<List<DailyExploreSaveDTO.RewardDTO>> getRewardList() {
        return ResultBody.success(dailyExploreService.getRewardList());
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "保存每轮随机奖励")
    @PostMapping("/" + ApiConstant.LATEST + "/saveReward")
    public ResultBody<DailyExploreSaveDTO.RewardDTO> saveReward(
            @RequestBody SubmitBody<DailyExploreSaveDTO.RewardDTO> request) {
        return ResultBody.success(request, dailyExploreService.saveReward(request.getData()));
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "删除每轮随机奖励")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteReward/{id}")
    public ResultBody<Object> deleteReward(@PathVariable("id") Long id) {
        dailyExploreService.deleteReward(id);
        return ResultBody.success(null);
    }

    // ==================== 完成奖励 ====================

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "获取完成奖励列表")
    @GetMapping("/" + ApiConstant.LATEST + "/getFinalRewardList")
    public ResultBody<List<DailyExploreSaveDTO.FinalRewardDTO>> getFinalRewardList() {
        return ResultBody.success(dailyExploreService.getFinalRewardList());
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "保存完成奖励")
    @PostMapping("/" + ApiConstant.LATEST + "/saveFinalReward")
    public ResultBody<DailyExploreSaveDTO.FinalRewardDTO> saveFinalReward(
            @RequestBody SubmitBody<DailyExploreSaveDTO.FinalRewardDTO> request) {
        return ResultBody.success(request, dailyExploreService.saveFinalReward(request.getData()));
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "删除完成奖励")
    @DeleteMapping("/" + ApiConstant.LATEST + "/deleteFinalReward/{id}")
    public ResultBody<Object> deleteFinalReward(@PathVariable("id") Long id) {
        dailyExploreService.deleteFinalReward(id);
        return ResultBody.success(null);
    }

    // ==================== 地图图片爬取 ====================

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "爬取单条地图的渲染图片")
    @PostMapping("/" + ApiConstant.LATEST + "/fetchMapImage/{id}")
    public ResultBody<DailyExploreSaveDTO> fetchMapImage(@PathVariable("id") Long id) {
        DailyExploreSaveDTO result = dailyExploreService.fetchMapImage(id);
        return ResultBody.success(result);
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "批量爬取所有地图的渲染图片（仅处理未缓存的地图）")
    @PostMapping("/" + ApiConstant.LATEST + "/fetchAllMapImages")
    public ResultBody<java.util.Map<String, Integer>> fetchAllMapImages() {
        return ResultBody.success(dailyExploreService.fetchAllMapImages());
    }

    // ==================== 地图图片下载 ====================

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "下载单张地图图片（PNG格式）")
    @GetMapping("/" + ApiConstant.LATEST + "/downloadMapImage/{id}")
    public void downloadMapImage(@PathVariable("id") Long id, HttpServletResponse response) throws IOException {
        byte[] imageBytes = dailyExploreService.getMapImageBytes(id);
        if (imageBytes == null || imageBytes.length == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"message\":\"该地图无缓存图片\"}");
            return;
        }
        String mapId = dailyExploreService.getMapIdForDownload(id);
        String fileName = mapId + ".png"; // 如 "100000000.png"，纯ASCII无需URL编码
        response.setContentType(MediaType.IMAGE_PNG_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"");
        response.setContentLength(imageBytes.length);
        response.getOutputStream().write(imageBytes);
        response.getOutputStream().flush();
    }

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "批量下载所有已缓存地图图片（ZIP打包，文件名=地图ID.png）")
    @GetMapping("/" + ApiConstant.LATEST + "/downloadAllMapImages")
    public void downloadAllMapImages(HttpServletResponse response) throws IOException {
        byte[] zipBytes = dailyExploreService.exportAllMapImagesToZip();
        if (zipBytes == null || zipBytes.length == 0) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"message\":\"没有已缓存的地图图片\"}");
            return;
        }
        String fileName = "dailyExplore_maps.zip";
        response.setContentType("application/zip");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + fileName + "\"");
        response.setContentLength(zipBytes.length);
        response.getOutputStream().write(zipBytes);
        response.getOutputStream().flush();
    }

    // ==================== 游戏参数 ====================

    @Tag(name = "/dailyExplore/" + ApiConstant.LATEST)
    @Operation(summary = "获取每日探索游戏参数")
    @GetMapping("/" + ApiConstant.LATEST + "/getGameParams")
    public ResultBody<Object> getGameParams() {
        Map<String, Object> params = new HashMap<>();
        params.put("dailyLimit", dailyExploreService.getDailyLimit());
        return ResultBody.success(params);
    }
}
