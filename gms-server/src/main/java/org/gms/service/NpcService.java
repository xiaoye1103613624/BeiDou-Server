package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import org.gms.dao.entity.PlayernpcsDO;
import org.gms.dao.entity.PlayernpcsEquipDO;
import org.gms.dao.entity.PlayernpcsFieldDO;
import org.gms.dao.mapper.PlayernpcsEquipMapper;
import org.gms.dao.mapper.PlayernpcsFieldMapper;
import org.gms.dao.mapper.PlayernpcsMapper;
import org.gms.server.life.PlayerNPC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * NPC服务类，管理玩家NPC的查询、创建和装备关联。
 */
@Service
@AllArgsConstructor
public class NpcService {

    /**
     * 玩家NPC数据访问对象
     */
    private final PlayernpcsMapper playernpcsMapper;

    /**
     * 玩家NPC装备数据访问对象
     */
    private final PlayernpcsEquipMapper playernpcsEquipMapper;

    /**
     * 玩家NPC场地数据访问对象
     */
    private final PlayernpcsFieldMapper playernpcsFieldMapper;

    /**
     * 根据条件查询玩家NPC场地列表。
     *
     * @param condition 查询条件
     * @return 玩家NPC场地列表
     */
    public List<PlayernpcsFieldDO> getPlayerNpcFields(PlayernpcsFieldDO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create(condition);
        return playernpcsFieldMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 根据条件查询玩家NPC列表。
     *
     * @param condition 查询条件
     * @return 玩家NPC列表
     */
    public List<PlayernpcsDO> getPlayerNpcDOs(PlayernpcsDO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create(condition);
        return playernpcsMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 根据条件查询玩家NPC装备列表。
     *
     * @param condition 查询条件
     * @return 玩家NPC装备列表
     */
    public List<PlayernpcsEquipDO> getPlayerNpcEquipDOs(PlayernpcsEquipDO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create(condition);
        return playernpcsEquipMapper.selectListByQuery(queryWrapper);
    }

    /**
     * 根据条件获取PlayerNPC对象列表，组装NPC及其装备信息。
     *
     * @param condition 查询条件
     * @return PlayerNPC列表
     */
    public List<PlayerNPC> getPlayerNPC(PlayernpcsDO condition) {
        List<PlayernpcsDO> playerNpcsDOList = getPlayerNpcDOs(condition);
        if (playerNpcsDOList.isEmpty()) {
            return new ArrayList<>();
        }
        // 为每个NPC查询其装备列表并组装为PlayerNPC对象
        return playerNpcsDOList.stream().map(playernpcsDO -> {
            List<PlayernpcsEquipDO> playerNpcEquips = getPlayerNpcEquipDOs(PlayernpcsEquipDO.builder().npcid(playernpcsDO.getId()).build());
            return new PlayerNPC(playernpcsDO, playerNpcEquips);
        }).collect(Collectors.toList());
    }

    /**
     * 创建玩家NPC，同时保存NPC基本信息及其装备信息。
     *
     * @param playerNpcDO       NPC基本信息
     * @param playerNpcEquipDOS NPC装备列表
     * @return 创建后的PlayerNPC对象，如果创建失败返回null
     */
    @Transactional(rollbackFor = Exception.class)
    public PlayerNPC createPlayerNPC(PlayernpcsDO playerNpcDO, List<PlayernpcsEquipDO> playerNpcEquipDOS) {
        // 先插入NPC基本信息获取自增ID
        playerNpcDO.setId(null);
        playernpcsMapper.insertSelective(playerNpcDO);
        // 设置装备的NPC关联ID并批量插入
        playerNpcEquipDOS.forEach(playerNpcEquipDO -> playerNpcEquipDO.setNpcid(playerNpcDO.getId()));
        playernpcsEquipMapper.insertBatch(playerNpcEquipDOS);
        // 重新查询组装完整NPC对象返回
        List<PlayerNPC> playerNPC = getPlayerNPC(PlayernpcsDO.builder().id(playerNpcDO.getId()).build());
        return playerNPC.isEmpty() ? null : playerNPC.getFirst();
    }
}