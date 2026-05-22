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
 * 物品服务
 * <p>提供物品信息查询相关业务逻辑</p>
 */
@Service
@Slf4j
public class ItemService {

    /**
     * 根据物品ID获取装备信息
     * <p>验证物品是否存在且为装备类型</p>
     *
     * @param itemId 物品ID
     * @return 装备对象
     * @throws BizException 物品不存在或非装备类型时抛出
     */
    public Equip getEquipmentInfoByItemId(Integer itemId) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        // 检查物品名是否存在
        String itemName = ii.getName(itemId);
        if (itemName == null) {
            throw new BizException(I18nUtil.getExceptionMessage("EQUIP_NOT_FOUND"));
        }

        // 验证是否为装备类型
        if (!ItemConstants.getInventoryType(itemId).equals(InventoryType.EQUIP)) {
            throw new BizException(I18nUtil.getExceptionMessage("ONLY_SUPPORT_GIVE_EQUIP"));
        }

        return (Equip) ItemInformationProvider.getInstance().getEquipById(itemId);
    }
}
