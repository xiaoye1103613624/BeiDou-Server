package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 文件树节点DTO
 * <p>用于表示文件系统树结构中的单个节点，支持嵌套子节点</p>
 */
@Data
public class FileTreeNodeDTO {
    /** 节点显示名称 */
    private String title;
    /** 节点唯一标识键 */
    private String key;
    /** 子节点列表，文件类型时为null */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FileTreeNodeDTO> children;
    /** 是否为叶子节点（文件） */
    @JsonProperty("isLeaf")
    private boolean leaf;

    public FileTreeNodeDTO(File file, String key) {
        this.title = file.getName();
        this.key = key;
        this.children = file.isDirectory() ? Collections.emptyList() : null;
        this.leaf = !file.isDirectory();
    }
}
