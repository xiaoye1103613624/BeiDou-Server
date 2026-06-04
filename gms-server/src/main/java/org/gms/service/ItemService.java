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
 * 【业务服务】ItemService：物品服务类，提供物品信息查询相关业务逻辑。
 * 
 * <p>封装物品信息的查询操作，主要用于获取装备的基础属性信息。</p>
 */
@Service
@Slf4j
public class ItemService {

    /**
     * 根据物品ID获取装备信息。
     * 
     * <p>验证流程：
     * <ol>
     *   <li>通过 {@link ItemInformationProvider} 查询物品名称，验证物品是否存在</li>
     *   <li>通过 {@link ItemConstants#getInventoryType(int)} 验证物品是否为装备类型</li>
     *   <li>获取装备的完整属性信息</li>
     * </ol></p>
     * 
     * @param itemId 物品ID
     * @return 装备对象，包含完整的装备属性
     * @throws BizException 当物品不存在或非装备类型时抛出异常
     */
    public Equip getEquipmentInfoByItemId(Integer itemId) {
        ItemInformationProvider ii = ItemInformationProvider.getInstance();

        // 检查物品是否存在
        String itemName = ii.getName(itemId);
        if (itemName == null) {
            throw new BizException(I18nUtil.getExceptionMessage("EQUIP_NOT_FOUND"));
        }

        // 验证物品类型是否为装备
        if (!ItemConstants.getInventoryType(itemId).equals(InventoryType.EQUIP)) {
            throw new BizException(I18nUtil.getExceptionMessage("ONLY_SUPPORT_GIVE_EQUIP"));
        }

        // 获取装备对象并返回
        return (Equip) ItemInformationProvider.getInstance().getEquipById(itemId);
    }
}