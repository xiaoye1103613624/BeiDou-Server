package org.gms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gms.dao.entity.GachaponRewardPoolDO;

/**
 * 百宝箱奖池查询返回DTO
 * <p>继承奖池基础信息，附加计算后的真实中奖概率</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GachaponPoolSearchRtnDTO extends GachaponRewardPoolDO {
    /** 真实中奖概率（万分比） */
    private Integer realProb;
}
