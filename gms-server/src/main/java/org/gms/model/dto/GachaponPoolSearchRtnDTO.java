package org.gms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gms.dao.entity.GachaponRewardPoolDO;

/**
 * 转蛋池搜索结果返回参数
 * 继承转蛋池DO，额外添加真实概率字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GachaponPoolSearchRtnDTO extends GachaponRewardPoolDO {
    /** 真实概率（计算后） */
    private Integer realProb;
}