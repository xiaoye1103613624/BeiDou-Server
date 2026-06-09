package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 背包类型返回参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InventoryTypeRtnDTO {
    /** 背包类型编号 */
    private Byte inventoryType;
    /** 背包类型名称 */
    private String name;
}