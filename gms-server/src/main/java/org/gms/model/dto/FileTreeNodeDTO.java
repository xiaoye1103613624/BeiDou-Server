package org.gms.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * 文件树节点DTO
 * 用于前端展示文件目录树结构
 */
@Data
public class FileTreeNodeDTO {
    /** 节点标题（文件名） */
    private String title;
    /** 节点键（文件路径） */
    private String key;
    /** 子节点列表，目录非空、文件为null */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FileTreeNodeDTO> children;
    /** 是否为叶子节点（文件） */
    @JsonProperty("isLeaf")
    private boolean leaf;

    /**
     * 构造函数，从File对象创建节点
     *
     * @param file 文件对象
     * @param key  节点键（文件路径）
     */
    public FileTreeNodeDTO(File file, String key) {
        this.title = file.getName();
        this.key = key;
        this.children = file.isDirectory() ? Collections.emptyList() : null;
        this.leaf = !file.isDirectory();
    }
}