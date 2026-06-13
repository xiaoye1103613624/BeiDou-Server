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
import org.gms.server.ItemInformationProvider;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    /**
     * 获取物品详细信息
     * 从ItemInformationProvider中查询物品的完整信息，包括基础属性、价格、装备加成、穿戴要求等
     *
     * @param submitData 物品详情请求，包含物品ID
     * @return 物品详情返回对象
     */
    public ItemDetailRtnDTO getItemDetail(ItemDetailReqDTO submitData) {
        Integer itemId = submitData.getItemId();
        if (itemId == null) {
            throw new BizException(I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL"));
        }
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        String name = ii.getName(itemId);
        if (name == null) {
            throw new BizException(I18nUtil.getExceptionMessage("EQUIP_NOT_FOUND"));
        }

        ItemDetailRtnDTO rtn = ItemDetailRtnDTO.builder()
                .itemId(itemId)
                .name(name)
                .type(submitData.getType())
                .build();

        // 获取通用属性
        rtn.setUnitPrice(ii.getUnitPrice(itemId));
        rtn.setWholePrice(ii.getWholePrice(itemId));
        rtn.setSlotMax((int) ii.getSlotMax(itemId));
        rtn.setCashItem(ii.isCash(itemId));

        // 获取限制标记
        rtn.setQuestItem(ii.isQuestItem(itemId));
        rtn.setUntradeable(ii.isUntradeableRestricted(itemId));
        rtn.setAccountRestricted(ii.isAccountRestricted(itemId));
        rtn.setDropRestricted(ii.isDropRestricted(itemId));

        // 获取装备属性（仅装备类型才有意义的值）
        Map<String, Integer> equipStats = ii.getEquipStats(itemId);
        if (equipStats != null) {
            // 装备加成属性（inc开头的键）
            rtn.setStr(getShortFromStats(equipStats, "STR"));
            rtn.setDex(getShortFromStats(equipStats, "DEX"));
            rtn.setIntVal(getShortFromStats(equipStats, "INT"));
            rtn.setLuk(getShortFromStats(equipStats, "LUK"));
            rtn.setHp(getShortFromStats(equipStats, "MHP"));
            rtn.setMp(getShortFromStats(equipStats, "MMP"));
            rtn.setPAtk(getShortFromStats(equipStats, "PAD"));
            rtn.setMAtk(getShortFromStats(equipStats, "MAD"));
            rtn.setPDef(getShortFromStats(equipStats, "PDD"));
            rtn.setMDef(getShortFromStats(equipStats, "MDD"));
            rtn.setAcc(getShortFromStats(equipStats, "ACC"));
            rtn.setAvoid(getShortFromStats(equipStats, "EVA"));
            rtn.setSpeed(getShortFromStats(equipStats, "Speed"));
            rtn.setJump(getShortFromStats(equipStats, "Jump"));
            // 升级次数
            rtn.setUpgradeSlots(equipStats.get("tuc"));
            // 穿戴要求
            rtn.setReqLevel(equipStats.get("reqLevel"));
            rtn.setReqStr(equipStats.get("reqSTR"));
            rtn.setReqDex(equipStats.get("reqDEX"));
            rtn.setReqInt(equipStats.get("reqINT"));
            rtn.setReqLuk(equipStats.get("reqLUK"));
            rtn.setReqJob(equipStats.get("reqJob"));
            // 现金装备标记
            rtn.setEquipCash(equipStats.get("cash") != null && equipStats.get("cash") == 1);
            // 是否可升级
            rtn.setUpgradeable(ii.isUpgradeable(itemId));
        }

        return rtn;
    }

    /**
     * 从装备属性Map中安全获取Short值
     *
     * @param stats 装备属性Map
     * @param key   属性键名
     * @return 属性值（不存在则返回0）
     */
    private Short getShortFromStats(Map<String, Integer> stats, String key) {
        Integer val = stats.get(key);
        return val != null ? val.shortValue() : 0;
    }

}