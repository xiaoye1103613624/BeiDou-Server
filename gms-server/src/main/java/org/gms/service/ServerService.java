package org.gms.service;

import org.gms.model.dto.ChannelListRtnDTO;
import org.gms.model.dto.WorldListRtnDTO;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 服务器服务类
 * 提供世界列表和频道列表的查询功能
 */
@Service
public class ServerService {

    /**
     * 获取所有世界列表
     * 返回每个世界的配置信息，包括各种倍率设置
     *
     * @return 世界列表，包含世界ID和各项倍率信息
     */
    public List<WorldListRtnDTO> worldList() {
        List<World> worlds = Server.getInstance().getWorlds();
        return worlds.stream()
                .map(w -> WorldListRtnDTO.builder()
                        .id(w.getId())
                        .expRate(w.getExpRate())
                        .dropRate(w.getDropRate())
                        .mesoRate(w.getMesoRate())
                        .bossDropRate(w.getBossDropRate())
                        .questRate(w.getQuestRate())
                        .travelRate(w.getTravelRate())
                        .fishingRate(w.getFishingRate())
                        .build())
                .toList();
    }

    /**
     * 获取指定世界的频道列表
     *
     * @param worldId 世界ID
     * @return 频道列表，包含频道ID和所属世界ID
     */
    public List<ChannelListRtnDTO> channelList(int worldId) {
        List<Channel> channels = Server.getInstance().getWorld(worldId).getChannels();
        return channels.stream()
                .map(c -> ChannelListRtnDTO.builder().id(c.getId()).worldId(c.getWorld()).build())
                .toList();
    }
}