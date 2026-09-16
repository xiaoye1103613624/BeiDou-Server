package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignAccountRoleDTO {
    private Integer accountId;
    /** 是否允许登录后台；null 表示不改 */
    private Integer webadmin;
    /** 角色编码；webadmin=0 时清空角色 */
    private String roleCode;
}
