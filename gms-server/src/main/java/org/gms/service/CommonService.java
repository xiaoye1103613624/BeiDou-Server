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
 * 通用服务类
 * 提供装备查询、在线人数统计、资料查询等通用功能
 */
@Service
@Slf4j
public class CommonService {

    /**
     * 物品服务，用于获取装备信息
     */
    @Autowired
    private ItemService itemService;

    /**
     * 根据物品ID获取装备信息
     * 获取装备的基础属性，包括力量、敏捷、智力、幸运、HP、MP、攻击力、魔法攻击力等
     *
     * @param submitData 装备信息请求，包含物品ID
     * @return 装备信息响应对象
     */
    public EquipmentInfoRtnDTO getEquipmentInfoByItemId(EquipmentInfoReqDTO submitData) {
        if (submitData.getId() == null) {
            throw new BizException(I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL"));
        }
        Equip equip = itemService.getEquipmentInfoByItemId(submitData.getId());
        EquipmentInfoRtnDTO rtn = new EquipmentInfoRtnDTO();
        // 映射装备基础属性到返回DTO
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
     * 根据世界ID获取当前世界在线玩家数量
     *
     * @param worldId 世界ID（大区ID）
     * @return 在线玩家数量，如果worldId为null返回0
     */
    public Integer getOnlinePlayerCountByWorldId(Integer worldId) {
        if (worldId == null) {
            return 0;
        }
        return Server.getInstance().getWorld(worldId).getPlayerStorage().getSize();
    }

    /**
     * 获取指定世界列表的在线玩家总数
     * 对多个世界的在线人数进行求和
     *
     * @param worldIdList 世界ID列表
     * @return 在线玩家总数
     */
    public Integer getAllWorldsOnlinePlayersCount(List<Integer> worldIdList) {
        if (worldIdList == null) {
            worldIdList = new ArrayList<>();
        }
        return worldIdList.stream().map(this::getOnlinePlayerCountByWorldId).mapToInt(Integer::intValue).sum();
    }

    /**
     * 查询游戏资料信息
     * 根据过滤条件和类型查询相关的游戏资料
     *
     * @param condition 查询条件，包含过滤词和类型列表
     * @return 资料查询结果列表
     */
    public List<InformationResult> getInformation(InformationSearch condition) {
        RequireUtil.requireNotEmpty(condition.getFilter(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "filter"));
        if (RequireUtil.isEmpty(condition.getTypes())) {
            // 未指定类型时默认查询所有类型
            condition.setTypes(Stream.of(InformationType.values()).map(InformationType::getType).collect(Collectors.toList()));
        }
        return CommonInformation.getInstance().getStringInformation(condition);
    }

}