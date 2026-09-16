package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountInfoDTO {
    private Integer id;
    private String name;
    private Integer webadmin;
    private String nick;
    private String email;
    private Integer language;
    /** 主角色编码，兼容前端单 role 字段 */
    private String role;
    @Builder.Default
    private List<String> roles = new ArrayList<>();
}
