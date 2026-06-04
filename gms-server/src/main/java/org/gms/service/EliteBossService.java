package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.entity.EliteBossConfigDO;
import org.gms.dao.mapper.EliteBossConfigMapper;
import org.gms.model.dto.EliteBossConfigReqDTO;
import org.gms.model.dto.EliteBossSpawnReqDTO;
import org.gms.model.dto.EliteBossStatusRtnDTO;
import org.gms.model.dto.EliteBossStatusRtnDTO.ChannelBossStatus;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.life.MonsterInformationProvider;
import org.gms.server.maps.MapManager;
import org.gms.server.maps.MapleMap;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 【业务服务】EliteBossService：精英BOSS（野外BOSS）配置管理与实时状态查询服务。
 * 
 * <p>提供精英BOSS的配置管理、实时状态查询、手动召唤和清除功能。
 * 支持按BOSS ID、BOSS名称、启用状态筛选，返回各频道的实时存活状态。</p>
 *
 * @author 萧曵
 */
@Slf4j
@Service
@AllArgsConstructor
public class EliteBossService {

    /** 精英BOSS配置数据访问接口 */
    private final EliteBossConfigMapper eliteBossConfigMapper;

    /**
     * 分页查询精英BOSS配置列表（含实时存活状态）。
     * 
     * <p>支持按BOSS ID、BOSS名称、启用状态筛选，同时查询各频道的BOSS存活状态。</p>
     * 
     * @param data 查询条件（包含BOSS ID、BOSS名称、启用状态、分页信息）
     * @return 分页后的精英BOSS列表（包含实时状态）
     */
    public Page<EliteBossStatusRtnDTO> getEliteBossList(EliteBossConfigReqDTO data) {
        QueryWrapper queryWrapper = QueryWrapper.create(new EliteBossConfigDO());
        
        // 按BOSS ID筛选
        if (data.getBossId() != null) {
            queryWrapper.and(EliteBossConfigDO::getBossId).eq(data.getBossId());
        }
        // 按BOSS名称模糊搜索
        if (data.getBossName() != null && !data.getBossName().isEmpty()) {
            queryWrapper.and(EliteBossConfigDO::getBossName).like(data.getBossName());
        }
        // 按启用状态筛选
        if (data.getEnabled() != null) {
            queryWrapper.and(EliteBossConfigDO::getEnabled).eq(data.getEnabled());
        }
        
        queryWrapper.orderBy(EliteBossConfigDO::getBossId, true);

        int pageNo = data.getPageNo() != null ? data.getPageNo() : 1;
        int pageSize = data.getPageSize() != null ? data.getPageSize() : 20;
        if (data.isNotPage()) {
            pageSize = 200;
        }
        Page<EliteBossConfigDO> paginate = eliteBossConfigMapper.paginate(pageNo, pageSize, queryWrapper);

        // 获取服务器世界列表
        List<World> worlds = Server.getInstance().getWorlds();
        // 构建包含实时状态的返回DTO
        List<EliteBossStatusRtnDTO> records = paginate.getRecords().stream()
                .map(config -> buildStatusDTO(config, worlds))
                .toList();

        return new Page<>(records, paginate.getPageNumber(), paginate.getPageSize(), paginate.getTotalRow());
    }

    /**
     * 构建含实时存活状态的返回DTO。
     * 
     * <p>从WZ文件获取BOSS的等级、最大HP、经验值等信息，并查询各频道的存活状态。</p>
     * 
     * @param config BOSS配置
     * @param worlds 服务器世界列表
     * @return 包含实时状态的DTO
     */
    private EliteBossStatusRtnDTO buildStatusDTO(EliteBossConfigDO config, List<World> worlds) {
        // 从WZ获取怪物信息
        Monster mob = LifeFactory.getMonster(config.getBossId());

        int bossLevel = 0;
        long bossMaxHp = 0;
        long bossExp = 0;
        String mapName = "";
        
        if (mob != null && !"MISSINGNO".equals(mob.getName())) {
            bossLevel = mob.getLevel();
            bossMaxHp = mob.getMaxHp();
            bossExp = mob.getExp();
        }

        // 查询各频道存活状态
        List<ChannelBossStatus> channelStatuses = new ArrayList<>();
        int aliveCount = 0;
        int totalChannelCount = 0;

        for (World world : worlds) {
            for (Channel channel : world.getChannels()) {
                totalChannelCount++;
                int count = countBossInChannel(channel, config.getMapId(), config.getBossId());
                if (count > 0) aliveCount++;
                channelStatuses.add(ChannelBossStatus.builder()
                        .worldId(world.getId())
                        .channelId(channel.getId())
                        .alive(count > 0)
                        .count(count)
                        .build());
            }
        }

        // 获取地图名称
        try {
            if (!worlds.isEmpty() && !worlds.get(0).getChannels().isEmpty()) {
                Channel ch = worlds.get(0).getChannels().get(0);
                MapManager mapFactory = ch.getMapFactory();
                if (mapFactory != null) {
                    MapleMap map = mapFactory.getMap(config.getMapId());
                    if (map != null) {
                        mapName = map.getMapName();
                    }
                }
            }
        } catch (Exception e) {
            log.debug("获取地图名称失败: {}", e.getMessage());
        }

        return EliteBossStatusRtnDTO.builder()
                .id(config.getId())
                .mapId(config.getMapId())
                .mapName(mapName)
                .bossId(config.getBossId())
                .bossName(config.getBossName())
                .companionBossId(config.getCompanionBossId())
                .bossLevel(bossLevel)
                .bossMaxHp(bossMaxHp)
                .bossExp(bossExp)
                .bossTime(config.getBossTime())
                .scriptName(config.getScriptName())
                .enabled(config.getEnabled())
                .channelStatuses(channelStatuses)
                .aliveCount(aliveCount)
                .totalChannelCount(totalChannelCount)
                .build();
    }

    /**
     * 查询指定频道的指定地图上BOSS的存活数量。
     * 
     * @param channel 频道
     * @param mapId 地图ID
     * @param bossId BOSS怪物ID
     * @return BOSS存活数量
     */
    private int countBossInChannel(Channel channel, int mapId, int bossId) {
        try {
            MapleMap map = channel.getMapFactory().getMap(mapId);
            if (map == null) return 0;
            return map.countMonster(bossId);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 新增精英BOSS配置。
     * 
     * <p>默认bossTime为180秒，enabled为1（启用）。</p>
     * 
     * @param data 新增配置数据
     * @return 新增后的配置ID
     */
    public Long addEliteBossConfig(EliteBossConfigReqDTO data) {
        EliteBossConfigDO entity = EliteBossConfigDO.builder()
                .mapId(data.getMapId())
                .bossId(data.getBossId())
                .bossName(data.getBossName())
                .bossTime(data.getBossTime() != null ? data.getBossTime() : 180)
                .scriptName(data.getScriptName())
                .enabled(data.getEnabled() != null ? data.getEnabled() : 1)
                .build();
        eliteBossConfigMapper.insert(entity);
        return entity.getId();
    }

    /**
     * 更新精英BOSS配置。
     * 
     * @param data 更新配置数据
     */
    public void updateEliteBossConfig(EliteBossConfigReqDTO data) {
        EliteBossConfigDO entity = EliteBossConfigDO.builder()
                .id(data.getId())
                .mapId(data.getMapId())
                .bossId(data.getBossId())
                .bossName(data.getBossName())
                .bossTime(data.getBossTime())
                .scriptName(data.getScriptName())
                .enabled(data.getEnabled())
                .build();
        eliteBossConfigMapper.update(entity);
    }

    /**
     * 删除精英BOSS配置。
     * 
     * @param id 配置ID
     */
    public void deleteEliteBossConfig(Long id) {
        eliteBossConfigMapper.deleteById(id);
    }

    /**
     * 在指定大区/频道召唤精英BOSS。
     * 
     * <p>支持同时召唤伴生BOSS（默认启用）。召唤数量限制为1~100。</p>
     * 
     * @param data 召唤请求（包含配置ID、大区列表、频道列表、召唤数量）
     * @return 召唤结果描述
     */
    public String spawnEliteBoss(EliteBossSpawnReqDTO data) {
        EliteBossConfigDO config = eliteBossConfigMapper.selectOneById(data.getConfigId());
        if (config == null) {
            throw new IllegalArgumentException("精英BOSS配置不存在: " + data.getConfigId());
        }

        int count = data.getCount() != null ? data.getCount() : 1;
        if (count < 1 || count > 100) {
            throw new IllegalArgumentException("召唤数量必须在1~100之间");
        }

        StringBuilder result = new StringBuilder();
        List<World> worlds = Server.getInstance().getWorlds();

        for (World world : worlds) {
            if (!matches(data.getWorldIds(), world.getId())) continue;
            for (Channel channel : world.getChannels()) {
                if (!matches(data.getChannelIds(), channel.getId())) continue;

                try {
                    MapleMap map = channel.getMapFactory().getMap(config.getMapId());
                    if (map == null) {
                        result.append("大区").append(world.getId()).append(" 频道").append(channel.getId())
                                .append(": 地图 ").append(config.getMapId()).append(" 不存在; ");
                        continue;
                    }
                    
                    // 召唤主BOSS
                    for (int i = 0; i < count; i++) {
                        Monster monster = LifeFactory.getMonster(config.getBossId());
                        if (monster == null || "MISSINGNO".equals(monster.getName())) {
                            result.append("大区").append(world.getId()).append(" 频道").append(channel.getId())
                                    .append(": 怪物数据无效; ");
                            break;
                        }
                        monster.getStats().setRemoveAfter(0);
                        monster.getStats().setSelfDestruction(null);
                        Point pos = new Point(0, 0);
                        map.spawnMonsterOnGroundBelow(monster, pos);
                    }
                    result.append("大区").append(world.getId()).append(" 频道").append(channel.getId())
                            .append(": 已召唤 ").append(count).append(" 只; ");

                    // 伴生BOSS同时召唤（默认true，仅当配置了伴生BOSSID时生效）
                    boolean spawnCompanion = data.getSpawnCompanion() == null || data.getSpawnCompanion();
                    if (spawnCompanion && config.getCompanionBossId() != null) {
                        for (int i = 0; i < count; i++) {
                            Monster companion = LifeFactory.getMonster(config.getCompanionBossId());
                            if (companion != null && !"MISSINGNO".equals(companion.getName())) {
                                companion.getStats().setRemoveAfter(0);
                                companion.getStats().setSelfDestruction(null);
                                map.spawnMonsterOnGroundBelow(companion, new Point(0, 0));
                            }
                        }
                        result.append("伴生").append(config.getCompanionBossId()).append(": ").append(count).append("只; ");
                    }
                } catch (Exception e) {
                    log.error("召唤BOSS失败: world={}, channel={}", world.getId(), channel.getId(), e);
                    result.append("大区").append(world.getId()).append(" 频道").append(channel.getId())
                            .append(": 召唤失败(").append(e.getMessage()).append("); ");
                }
            }
        }

        return result.toString();
    }

    /**
     * 清除指定大区/频道的精英BOSS。
     * 
     * <p>同时清除主BOSS和伴生BOSS。</p>
     * 
     * @param data 清除请求（包含配置ID、大区列表、频道列表）
     * @return 清除结果描述
     */
    public String killEliteBoss(EliteBossSpawnReqDTO data) {
        EliteBossConfigDO config = eliteBossConfigMapper.selectOneById(data.getConfigId());
        if (config == null) {
            throw new IllegalArgumentException("精英BOSS配置不存在: " + data.getConfigId());
        }

        StringBuilder result = new StringBuilder();
        List<World> worlds = Server.getInstance().getWorlds();

        for (World world : worlds) {
            if (!matches(data.getWorldIds(), world.getId())) continue;
            for (Channel channel : world.getChannels()) {
                if (!matches(data.getChannelIds(), channel.getId())) continue;

                try {
                    MapleMap map = channel.getMapFactory().getMap(config.getMapId());
                    if (map == null) continue;

                    // 清除主BOSS的所有实例
                    Monster monster;
                    while ((monster = map.getMonsterById(config.getBossId())) != null) {
                        map.killMonster(monster, null, false);
                    }
                    // 同时清除伴生BOSS
                    if (config.getCompanionBossId() != null) {
                        while ((monster = map.getMonsterById(config.getCompanionBossId())) != null) {
                            map.killMonster(monster, null, false);
                        }
                    }
                    result.append("大区").append(world.getId()).append(" 频道").append(channel.getId())
                            .append(": 已清除; ");
                } catch (Exception e) {
                    log.error("清除BOSS失败: world={}, channel={}", world.getId(), channel.getId(), e);
                    result.append("大区").append(world.getId()).append(" 频道").append(channel.getId())
                            .append(": 清除失败(").append(e.getMessage()).append("); ");
                }
            }
        }

        if (result.isEmpty()) {
            return "未找到匹配的频道，BOSS可能已被清除。";
        }
        return result.toString();
    }

    /**
     * 检查ID列表是否表示"全部"（null、空、或包含-1）。
     * 
     * @param ids ID列表
     * @return 是否表示全部
     */
    private boolean isAll(List<Integer> ids) {
        return ids == null || ids.isEmpty() || ids.contains(-1);
    }

    /**
     * 检查给定值是否匹配ID列表（全部匹配 或 列表中包含该值）。
     * 
     * @param ids ID列表
     * @param value 待检查的值
     * @return 是否匹配
     */
    private boolean matches(List<Integer> ids, Integer value) {
        return isAll(ids) || ids.contains(value);
    }
}