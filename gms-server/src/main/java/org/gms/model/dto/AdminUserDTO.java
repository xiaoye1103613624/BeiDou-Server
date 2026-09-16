package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserDTO {
    private Integer id;
    private String name;
    private Integer webadmin;
    private String nick;
    private String roleCode;
    private String roleName;
}
