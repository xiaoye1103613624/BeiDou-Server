package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysRoleDTO {
    private Long id;
    private String code;
    private String name;
    private String remark;
    private Integer enabled;
}
