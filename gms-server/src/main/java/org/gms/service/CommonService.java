package org.gms.service;


import lombok.extern.slf4j.Slf4j;
import org.gms.client.inventory.Equip;
import org.gms.constants.api.InformationType;
import org.gms.exception.BizException;
import org.gms.model.dto.*;
import org.gms.model.pojo.InformationSearch;
import org.gms.model.pojo.InformationResult;
import org.gms.net.server.Server;
import org.gms.server.CommonInformation;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 【业务服务】CommonService：通用服务类，提供游戏数据查询的通用接口。
 * 
 * <p>封装装备信息查询、在线玩家统计、游戏资料搜索等通用业务逻辑。</p>
 */
@Service
@Slf4j
public class CommonService {

    /** 物品服务，用于获取装备基础信息 */
    @Autowired
    private ItemService itemService;

    /**
     * 根据物品ID获取装备基础属性信息。
     * 
     * <p>从物品服务获取装备实体，转换为DTO返回装备的各项属性（力量、敏捷、智力、幸运、HP、MP、
     * 攻击力、魔法攻击力、防御力、魔法防御力、命中、回避、手数、速度、跳跃、升级槽位、过期时间）。</p>
     * 
     * @param submitData 包含物品ID的请求DTO
     * @return 装备属性信息DTO
     * @throws BizException 当物品ID为空时抛出参数不能为空异常
     */
    public EquipmentInfoRtnDTO getEquipmentInfoByItemId(EquipmentInfoReqDTO submitData) {
        if (submitData.getId() == null) {
            throw new BizException(I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL"));
        }
        Equip equip = itemService.getEquipmentInfoByItemId(submitData.getId());
        EquipmentInfoRtnDTO rtn = new EquipmentInfoRtnDTO();
        rtn.setStr(equip.getStr());
        rtn.setDex(equip.getDex());
        rtn.set_int(equip.getInt());
        rtn.setLuk(equip.getLuk());
        rtn.setHp(equip.getHp());
        rtn.setMp(equip.getMp());
        rtn.setPAtk(equip.getWatk());
        rtn.setMAtk(equip.getMatk());
        rtn.setPDef(equip.getWdef());
        rtn.setMDef(equip.getMdef());
        rtn.setAcc(equip.getAcc());
        rtn.setAvoid(equip.getAvoid());
        rtn.setHands(equip.getHands());
        rtn.setSpeed(equip.getSpeed());
        rtn.setJump(equip.getJump());
        rtn.setUpgradeSlot(equip.getUpgradeSlots());
        rtn.setExpire(equip.getExpiration());
        return rtn;
    }

    /**
     * 根据大区ID获取当前世界在线玩家数量。
     * 
     * @param worldId 大区ID
     * @return 在线玩家数量，若worldId为null则返回0
     */
    public Integer getOnlinePlayerCountByWorldId(Integer worldId) {
        if (worldId == null) {
            return 0;
        }
        //如果传参未序列化可能导致数据丢失，做兜底处理
        return Server.getInstance().getWorld(worldId).getPlayerStorage().getSize();
    }

    /**
     * 查询指定多个大区的在线玩家总数。
     * 
     * @param worldIdList 大区ID列表，若为空则返回0
     * @return 所有指定大区的在线玩家总数
     */
    public Integer getAllWorldsOnlinePlayersCount(List<Integer> worldIdList) {
        //空值防护：若worldIdList为null则初始化为空列表
        if (worldIdList == null) worldIdList = new ArrayList<>();
        
        //流式计算：遍历每个大区ID，累加在线玩家数量
        return worldIdList.stream()
                .map(this::getOnlinePlayerCountByWorldId)
                .mapToInt(Integer::intValue)
                .sum();
    }

    /**
     * 根据关键字搜索游戏内资料（物品、怪物、地图等）。
     * 
     * <p>支持按ID或名称搜索，可指定搜索类型。若未指定类型，则搜索所有类型。</p>
     * 
     * @param condition 搜索条件，包含关键字(filter)和类型列表(types)
     * @return 搜索结果列表
     * @throws BizException 当搜索关键字为空时抛出异常
     */
    public List<InformationResult> getInformation(InformationSearch condition) {
        //参数校验：搜索关键字不能为空
        RequireUtil.requireNotEmpty(condition.getFilter(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "filter"));
        
        //默认值处理：若未指定搜索类型，则搜索所有类型
        if (RequireUtil.isEmpty(condition.getTypes())) {
            condition.setTypes(Stream.of(InformationType.values())
                    .map(InformationType::getType)
                    .collect(Collectors.toList()));
        }
        
        //委托给CommonInformation单例进行搜索
        return CommonInformation.getInstance().getStringInformation(condition);
    }

}