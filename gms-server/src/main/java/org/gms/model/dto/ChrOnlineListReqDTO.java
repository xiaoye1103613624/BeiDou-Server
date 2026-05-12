package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 【类型】ChrOnlineListReqDTO（class），包 `org.gms.model.dto`。
 */
@Getter
@Setter
public class ChrOnlineListReqDTO extends BasePageDTO {
    private Integer id;
    private String name;
    private Integer map;
    private int world;
}
