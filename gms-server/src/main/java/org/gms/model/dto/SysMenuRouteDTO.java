package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 对齐前端 Arco 侧栏路由结构（path/name/meta/children）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysMenuRouteDTO {
    private String path;
    private String name;
    private SysMenuRouteMetaDTO meta;
    /** 叶子为 null；有子节点时再填充，避免前端把空数组根菜单滤掉 */
    private List<SysMenuRouteDTO> children;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SysMenuRouteMetaDTO {
        private String locale;
        private Boolean requiresAuth;
        private String icon;
        private Integer order;
        private List<String> roles;
        private Boolean hideInMenu;
    }
}
