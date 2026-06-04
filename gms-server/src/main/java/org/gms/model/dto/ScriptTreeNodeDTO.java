package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 脚本文件树节点DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScriptTreeNodeDTO {
    /** 文件/目录名称 */
    private String title;
    /** 相对路径（从scripts-zh-CN根开始，如 "npc/1002000.js"） */
    private String key;
    /** 子节点，文件为null，目录为列表 */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<ScriptTreeNodeDTO> children;
    /** 是否是叶子节点（文件） */
    @JsonProperty("isLeaf")
    private boolean leaf;
    /** 节点类型："file" 或 "directory" */
    private String type;
}
