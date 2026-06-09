package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 世界列表返回参数
 * 包含各世界的倍率配置信息
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldListRtnDTO {
    /** 世界ID */
    private Integer id;
    /** 经验倍率 */
    private Float expRate;
    /** 掉落倍率 */
    private Float dropRate;
    /** 金币倍率 */
    private Float mesoRate;
    /** Boss掉落倍率 */
    private Float bossDropRate;
    /** 任务倍率 */
    private Float questRate;
    /** 旅行倍率 */
    private Float travelRate;
    /** 钓鱼倍率 */
    private Float fishingRate;
}