package org.gms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.gms.dao.entity.GachaponRewardPoolDO;

/**
 * 【类型】GachaponPoolSearchRtnDTO（class），包 `org.gms.model.dto`。
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GachaponPoolSearchRtnDTO extends GachaponRewardPoolDO {
    private Integer realProb;
}
