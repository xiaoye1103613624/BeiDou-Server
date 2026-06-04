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
 * 【业务服务】NpcService：玩家NPC服务类，负责玩家创建的NPC管理。
 * 
 * <p>提供玩家NPC的查询、创建等功能，支持玩家自定义NPC（如商店NPC、任务NPC等）。</p>
 */
@Service
@AllArgsConstructor
public class NpcService {
    /** 玩家NPC基础数据访问接口 */
    private final PlayernpcsMapper playernpcsMapper;
    /** 玩家NPC装备数据访问接口 */
    private final PlayernpcsEquipMapper playernpcsEquipMapper;
    /** 玩家NPC场地数据访问接口 */
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
     * 根据条件查询玩家NPC基础信息列表。
     * 
     * @param condition 查询条件
     * @return 玩家NPC基础信息列表
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
     * 根据条件查询玩家NPC对象列表。
     * 
     * <p>查询NPC基础信息并关联其装备信息，转换为PlayerNPC对象。</p>
     * 
     * @param condition 查询条件
     * @return 玩家NPC对象列表
     */
    public List<PlayerNPC> getPlayerNPC(PlayernpcsDO condition) {
        List<PlayernpcsDO> playerNpcsDOList = getPlayerNpcDOs(condition);
        if (playerNpcsDOList.isEmpty()) {
            return new ArrayList<>();
        }
        return playerNpcsDOList.stream().map(playernpcsDO -> {
            // 查询NPC的装备信息
            List<PlayernpcsEquipDO> playerNpcEquips = getPlayerNpcEquipDOs(
                    PlayernpcsEquipDO.builder().npcid(playernpcsDO.getId()).build());
            return new PlayerNPC(playernpcsDO, playerNpcEquips);
        }).collect(Collectors.toList());
    }

    /**
     * 创建新的玩家NPC。
     * 
     * <p>插入NPC基础信息和装备信息，返回创建的PlayerNPC对象。</p>
     * 
     * @param playerNpcDO NPC基础信息
     * @param playerNpcEquipDOS NPC装备列表
     * @return 创建的PlayerNPC对象，创建失败返回null
     */
    @Transactional(rollbackFor = Exception.class)
    public PlayerNPC createPlayerNPC(PlayernpcsDO playerNpcDO, List<PlayernpcsEquipDO> playerNpcEquipDOS) {
        // 设置ID为null以生成新记录
        playerNpcDO.setId(null);
        playernpcsMapper.insertSelective(playerNpcDO);
        
        // 设置装备的NPC关联ID并批量插入
        playerNpcEquipDOS.forEach(playerNpcEquipDO -> playerNpcEquipDO.setNpcid(playerNpcDO.getId()));
        playernpcsEquipMapper.insertBatch(playerNpcEquipDOS);
        
        // 查询创建的NPC并返回
        List<PlayerNPC> playerNPC = getPlayerNPC(PlayernpcsDO.builder().id(playerNpcDO.getId()).build());
        return playerNPC.isEmpty() ? null : playerNPC.getFirst();
    }
}