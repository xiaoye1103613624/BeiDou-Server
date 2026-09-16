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
public class SysRoleBindMenusDTO {
    private Long roleId;
    @Builder.Default
    private List<Long> menuIds = new ArrayList<>();
}
