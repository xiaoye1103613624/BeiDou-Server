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
public class SysMenuDTO {
    private Long id;
    private Long parentId;
    private String name;
    private String path;
    private String localeKey;
    private String icon;
    private Integer sortOrder;
    private Integer menuType;
    private String roles;
    private Integer requiresAuth;
    private Integer hideInMenu;
    private Integer enabled;
    private String remark;
    @Builder.Default
    private List<SysMenuDTO> children = new ArrayList<>();
}
