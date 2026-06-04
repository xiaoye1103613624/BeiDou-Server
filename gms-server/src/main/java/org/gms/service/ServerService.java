package org.gms.service;

import org.gms.model.dto.ChannelListRtnDTO;
import org.gms.model.dto.WorldListRtnDTO;
import org.gms.net.server.Server;
import org.gms.net.server.channel.Channel;
import org.gms.net.server.world.World;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【业务服务】ServerService：服务器信息服务类，提供游戏服务器运行时数据查询。
 * 
 * <p>负责查询游戏大区（World）和频道（Channel）的运行状态信息，包括各服务器的倍率设置等。</p>
 */
@Service
public class ServerService {

    /**
     * 获取所有游戏大区列表。
     * 
     * @return 大区列表，包含各服务器的倍率信息（经验、掉落、金币、BOSS掉落、任务、旅行、钓鱼）
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
     * 获取指定大区下的所有频道列表。
     * 
     * @param worldId 大区ID
     * @return 频道列表，包含频道ID和所属大区ID
     */
    public List<ChannelListRtnDTO> channelList(int worldId) {
        List<Channel> channels = Server.getInstance().getWorld(worldId).getChannels();
        return channels.stream()
                .map(c -> ChannelListRtnDTO.builder().id(c.getId()).worldId(c.getWorld()).build())
                .toList();
    }
}