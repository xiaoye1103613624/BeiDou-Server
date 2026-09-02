package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 右边栏脚本选择树节点（相对 BeiDouSpecial，不含 .js）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SidebarScriptTreeNodeDTO {

    /** 展示名（文件名或目录名） */
    private String title;

    /** 选中值：脚本相对路径；目录节点用 dir: 前缀避免与脚本冲突 */
    private String key;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SidebarScriptTreeNodeDTO> children;

    @JsonProperty("isLeaf")
    private boolean leaf;

    /** 目录不可选，仅叶子脚本可选 */
    private boolean disabled;
}
