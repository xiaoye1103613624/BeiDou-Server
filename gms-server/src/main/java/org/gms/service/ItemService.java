package org.gms.service;


import lombok.extern.slf4j.Slf4j;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.constants.inventory.ItemConstants;
import org.gms.exception.BizException;
import org.gms.server.ItemInformationProvider;
import org.gms.util.I18nUtil;
import org.springframework.stereotype.Service;

/**
 * 物品服务类
 * 提供物品相关的查询和验证功能
 */
@Service
@Slf4j
public class ItemService {

    /**
     * 根据物品ID获取装备信息
     * 验证物品是否存在且类型为装备，然后返回装备对象
     *
     * @param itemId 物品ID
     * @return 装备对象
     * @throws BizException 如果物品不存在或类型不是装备
     */
    public Equip getEquipmentInfoByItemId(Integer itemId) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        String itemName = ii.getName(itemId);

        if (itemName == null) {
            throw new BizException(I18nUtil.getExceptionMessage("EQUIP_NOT_FOUND"));
        }

        if (!ItemConstants.getInventoryType(itemId).equals(InventoryType.EQUIP)) {
            throw new BizException(I18nUtil.getExceptionMessage("ONLY_SUPPORT_GIVE_EQUIP"));
        }

        return (Equip) ItemInformationProvider.getInstance().getEquipById(itemId);
    }
}